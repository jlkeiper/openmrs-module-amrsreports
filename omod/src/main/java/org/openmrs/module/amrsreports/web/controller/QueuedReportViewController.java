package org.openmrs.module.amrsreports.web.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.QueuedReport;
import org.openmrs.module.amrsreports.service.QueuedReportService;
import org.openmrs.web.WebConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller for viewing queued reports ... at least compeleted ones.
 */
@Controller
public class QueuedReportViewController {

	private final String VIEW_FAILURE = "redirect:queuedReport.list";
	private final String VIEW_DETAILS = "redirect:/module/reporting/reports/reportHistoryOpen.form?uuid=";

	@RequestMapping(method = RequestMethod.GET, value = "module/amrsreports/viewReport.form")
	public String processForm(ModelMap map, @RequestParam(required = true, value = "reportId") Integer reportId, HttpServletResponse response, HttpServletRequest request) {

		if (reportId == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "No report specified in request.");
			return VIEW_FAILURE;
		}

		QueuedReport report = Context.getService(QueuedReportService.class).getQueuedReport(reportId);

		if (report == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Could not find queued report #" + report.getId());
			return VIEW_FAILURE;
		}

		if (report.getReportRequestUUID() == null) {
			request.getSession().setAttribute(WebConstants.OPENMRS_ERROR_ATTR, "Could not find request uuid for queued report #" + report.getId());
			return VIEW_FAILURE;
		}


		return VIEW_DETAILS + report.getReportRequestUUID();

//		String folderName = Context.getAdministrationService().getGlobalProperty("amrsreports.file_dir");
//		File fileDir = OpenmrsUtil.getDirectoryInApplicationDataDirectory(folderName);
//
//		///end of interface population after submitting
//		try {
//			File amrsFile = new File(fileDir, report.getCsvFilename());
//			FileInputStream fstream = new FileInputStream(amrsFile);
//			Map<String, Object> csv = MOHReportUtil.renderDataSetFromCSV(fstream);
//			fstream.close();
//
//			map.addAttribute("columnHeaders", csv.get("columnHeaders"));
//			map.addAttribute("records", csv.get("records"));
//			map.addAttribute("report", report);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
