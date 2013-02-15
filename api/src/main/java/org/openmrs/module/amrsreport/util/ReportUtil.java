package org.openmrs.module.amrsreport.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Cohort;
import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.dataset.DataSet;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;
import org.openmrs.module.reporting.report.ReportData;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Helper utility for running reports and not overloading the system
 */
public class ReportUtil {

	private static final int MAX_RECORDS = 1000;
	private static final Log log = LogFactory.getLog(ReportUtil.class);

	public static ReportData evaluate(ReportDefinition reportDefinition, EvaluationContext evaluationContext) throws EvaluationException {
		ReportDefinitionService reportDefinitionService = Context.getService(ReportDefinitionService.class);

		Map<String, DataSet> dataSets = new LinkedHashMap<String, DataSet>();
		Cohort cohort = evaluationContext.getBaseCohort();
		Set<Integer> memberIds = cohort.getMemberIds();
		Integer[] members = memberIds.toArray(new Integer[]{});

		while (members.length > 0) {
			log.warn("length is still: " + members.length);

			int size = Math.min(MAX_RECORDS, members.length);

			Integer[] subset = Arrays.copyOfRange(members, 0, size);

			if (size == members.length) {
				members = new Integer[]{};
			} else {
				members = Arrays.copyOfRange(members, size, members.length);
			}

			Cohort subCohort = new Cohort(Arrays.asList(subset));
			evaluationContext.setBaseCohort(subCohort);
			ReportData tempReportData = reportDefinitionService.evaluate(reportDefinition, evaluationContext);
			dataSets.putAll(tempReportData.getDataSets());
		}

		ReportData reportData = new ReportData();
		reportData.setDefinition(reportDefinition);
		reportData.setContext(evaluationContext);
		reportData.setDataSets(dataSets);
		return reportData;
	}
}
