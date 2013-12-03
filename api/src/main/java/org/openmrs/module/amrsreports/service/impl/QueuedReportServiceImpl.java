package org.openmrs.module.amrsreports.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.AmrsReportsConstants;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.db.QueuedReportDAO;
import org.openmrs.module.amrsreports.event.ReportCompletedEvent;
import org.openmrs.module.amrsreports.reporting.provider.MOH361AReportProvider_0_1;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.reporting.report.renderer.AMRSReportsExcelRenderer;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.amrsreports.service.UserFacilityService;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.cohort.definition.CohortDefinition;
import org.openmrs.module.reporting.cohort.definition.service.CohortDefinitionService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportRequest;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.renderer.RenderingMode;
import org.openmrs.module.reporting.report.service.ReportService;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Implementation of {@link QueuedReportService}
 */
public class QueuedReportServiceImpl implements QueuedReportService {

	private QueuedReportDAO dao;
	private final Log log = LogFactory.getLog(this.getClass());

	@Autowired
	private org.springframework.context.ApplicationContext applicationContext;

	public void setDao(QueuedReportDAO dao) {
		this.dao = dao;
	}

	@Override
	public QueuedReport getNextQueuedReport() {
		return dao.getNextQueuedReport(new Date());
	}

	@Override
	public void processQueuedReport(QueuedReport queuedReport) throws EvaluationException, IOException {
		// validate
		if (queuedReport.getReportName() == null)
			throw new APIException("The queued report must reference a report provider by name.");

		if (queuedReport.getFacility() == null)
			throw new APIException("The queued report must reference a facility.");

		// generate filename prefix for queued report artifacts
		queuedReport.setFilenamePrefix(generateFilenamePrefix(queuedReport));

		// build a report request
		ReportRequest rr = buildReportRequest(queuedReport);

		// save the request
		rr = Context.getService(ReportService.class).saveReportRequest(rr);

		// mark original QueuedReport as submitted and save status
		queuedReport.setStatus(QueuedReport.STATUS_RUNNING);
		queuedReport.setReportRequestUUID(rr.getUuid());
		Context.getService(QueuedReportService.class).saveQueuedReport(queuedReport);

		// run the report
		Report r = Context.getService(ReportService.class).runReport(rr);

		// deal with errors
		if (OpenmrsUtil.nullSafeEquals(r.getRequest().getStatus(), ReportRequest.Status.FAILED)) {
			throw new APIException("Could not process report request.");
		}

		// update queued report
		queuedReport.setStatus(QueuedReport.STATUS_COMPLETE);
		Context.getService(QueuedReportService.class).saveQueuedReport(queuedReport);

		// update schedule if needed
		if (queuedReport.getRepeatInterval() != null && queuedReport.getRepeatInterval() > 0) {

			//create a new QueuedReport borrowing some values from the run report
			QueuedReport newQueuedReport = new QueuedReport();
			newQueuedReport.setFacility(queuedReport.getFacility());
			newQueuedReport.setReportName(queuedReport.getReportName());

			//compute date for next schedule
			Calendar newScheduleDate = Calendar.getInstance();
			newScheduleDate.setTime(queuedReport.getDateScheduled());
			newScheduleDate.add(Calendar.SECOND, newScheduleDate.get(Calendar.SECOND) + queuedReport.getRepeatInterval());
			Date nextSchedule = newScheduleDate.getTime();

			//set date for next schedule
			newQueuedReport.setDateScheduled(nextSchedule);
			newQueuedReport.setEvaluationDate(nextSchedule);

			newQueuedReport.setStatus(QueuedReport.STATUS_NEW);
			newQueuedReport.setRepeatInterval(queuedReport.getRepeatInterval());

			Context.getService(QueuedReportService.class).saveQueuedReport(newQueuedReport);
		}

		applicationContext.publishEvent(new ReportCompletedEvent(rr));
	}

	private String generateFilenamePrefix(QueuedReport queuedReport) {
		// find the directory to put the file in
		AdministrationService as = Context.getAdministrationService();
		String folderName = as.getGlobalProperty("amrsreports.file_dir");

		// format dates and times for use in filename
		String formattedStartTime = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
				.format(new Date());
		String formattedEvaluationDate = new SimpleDateFormat("yyyy-MM-dd")
				.format(queuedReport.getEvaluationDate());

		// generate the name
		return folderName
				+ File.separator
				+ queuedReport.getReportName().replaceAll(" ", "-")
				+ "_"
				+ queuedReport.getFacility().getCode()
				+ "_"
				+ queuedReport.getFacility().getName().replaceAll(" ", "-")
				+ "_as-of_"
				+ formattedEvaluationDate
				+ "_run-on_"
				+ formattedStartTime;
	}

	@Override
	public QueuedReport saveQueuedReport(QueuedReport queuedReport) {
		if (queuedReport == null)
			return queuedReport;

		if (queuedReport.getStatus() == null)
			queuedReport.setStatus(QueuedReport.STATUS_NEW);

		return dao.saveQueuedReport(queuedReport);
	}

	@Override
	public void purgeQueuedReport(QueuedReport queuedReport) {
		dao.purgeQueuedReport(queuedReport);
	}

	@Override
	public List<QueuedReport> getAllQueuedReports() {
		return dao.getAllQueuedReports();
	}

	@Override
	public List<QueuedReport> getQueuedReportsWithStatus(String status) {
		UserFacilityService userFacilityService = Context.getService(UserFacilityService.class);
		List<MOHFacility> allowedFacilities = userFacilityService.getAllowedFacilitiesForUser(Context.getAuthenticatedUser());

		return dao.getQueuedReportsByFacilities(allowedFacilities, status);

	}

	@Override
	public QueuedReport getQueuedReport(Integer reportId) {
		return dao.getQueuedReport(reportId);
	}

	@Override
	public List<QueuedReport> getQueuedReportsByFacilities(List<MOHFacility> facilities, String status) {
		return dao.getQueuedReportsByFacilities(facilities, status);
	}

	@Override
	public void updateCohortDefinition(ReportProvider p) {
		CohortDefinition cd = p.getCohortDefinition();
		CohortDefinition existing = Context.getService(CohortDefinitionService.class).getDefinitionByUuid(cd.getUuid());
		if (existing != null) {
			Context.getService(CohortDefinitionService.class).purgeDefinition(existing);
			Context.flushSession();
		}
		Context.getService(CohortDefinitionService.class).saveDefinition(cd);
	}

	@Override
	public void updateReportDefinition(ReportProvider p) {
		ReportDefinition rd = p.getReportDefinition();
		ReportDefinition existing = Context.getService(ReportDefinitionService.class).getDefinitionByUuid(rd.getUuid());
		if (existing != null) {
			Context.getService(ReportDefinitionService.class).purgeDefinition(existing);
			Context.flushSession();
		}
		Context.getService(ReportDefinitionService.class).saveDefinition(rd);
	}

	@Override
	public void updateReportDesign(ReportProvider p) {
		ReportDesign rd = p.getReportDesign();
		ReportDesign existing = Context.getService(ReportService.class).getReportDesignByUuid(rd.getUuid());
		if (existing != null) {
			Context.getService(ReportService.class).purgeReportDesign(existing);
			Context.flushSession();
		}
		Context.getService(ReportService.class).saveReportDesign(rd);
	}

	private ReportRequest buildReportRequest(QueuedReport queuedReport) throws EvaluationException {

		if (queuedReport == null) {
			throw new EvaluationException("Could not evaluate a null queued report.");
		}

		// get the provider
		ReportProvider p = ReportProviderRegistrar.getInstance().getReportProviderByName(queuedReport.getReportName());
		if (p == null) {
			throw new EvaluationException("No report provider by the name of " + queuedReport.getReportName());
		}

		// get the facility
		MOHFacility f = queuedReport.getFacility();
		if (f == null) {
			throw new EvaluationException("No facility found on the queued report.");
		}

		// build the request
		ReportRequest r = new ReportRequest();

		Mapped<CohortDefinition> mc = new Mapped<CohortDefinition>();
		mc.setParameterizable(p.getCohortDefinition());
		mc.addParameterMapping("facility", f);
		r.setBaseCohort(mc);

		Mapped<ReportDefinition> mp = new Mapped<ReportDefinition>();
		mp.setParameterizable(p.getReportDefinition());
		mp.addParameterMapping("facility", f);
		r.setReportDefinition(mp);

		RenderingMode rm = new RenderingMode(new AMRSReportsExcelRenderer(), "Excel", null, 1);
		rm.setArgument(p.getReportDesign().getUuid());
		r.setRenderingMode(rm);

		r.setEvaluationDate(queuedReport.getEvaluationDate());
		r.setPriority(ReportRequest.Priority.NORMAL);
		r.setProcessAutomatically(true);

		return r;
	}

}