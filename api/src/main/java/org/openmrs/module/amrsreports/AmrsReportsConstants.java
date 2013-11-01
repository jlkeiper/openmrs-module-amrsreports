package org.openmrs.module.amrsreports;

import org.openmrs.module.amrsreports.reporting.provider.MOH361AReportProvider_0_1;
import org.openmrs.module.amrsreports.reporting.provider.MOH361AReportProvider_0_2;
import org.openmrs.module.amrsreports.reporting.provider.MOH361BReportProvider_0_1;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;

import java.util.Arrays;
import java.util.List;

/**
 * Primary container for all static constants used in this module.  Note: other constant files exist, but will hopefully
 * be migrated into this one eventually or clearly separated by purpose.
 */
public class AmrsReportsConstants {

	// separator used to delineate multiple values within a single cell
	public static final String INTER_CELL_SEPARATOR = "\n";

	public static final char DEFAULT_CSV_DELIMITER = ',';

	public static final String GP_CCC_NUMBER_IDENTIFIER_TYPE = "amrsreports.cccIdentifierType";

	public static final String GP_PRODUCTION_SERVER_URL = "amrsreports.productionServerURL";

	public static final String TRANSFER_IN = "Transfer In";

	public static final String SAVED_COHORT_UUID = "AMRSReportsTemporaryCohort000000000000";

	public static List<ReportProvider> REPORT_PROVIDERS = Arrays.asList(
			new MOH361AReportProvider_0_1(),
			new MOH361AReportProvider_0_2(),
			new MOH361BReportProvider_0_1()
	);
}
