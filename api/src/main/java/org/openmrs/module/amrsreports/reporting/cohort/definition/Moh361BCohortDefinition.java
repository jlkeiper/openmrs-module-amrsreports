package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;

/**
 * MOH 361B Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.MOH361BCohortDefinition")
public class Moh361BCohortDefinition extends AMRSReportsCohortDefinition {

	public static final String COHORT_UUID = "MOH361B-01Cohort0000000000000000000000";

	@Override
	protected void initProperties() {
		// initialize metadata
		this.setName("MOH 361B Cohort");
		this.setDescription("Cohort for use with the MOH 361B Register");

		// ensure the UUID is always the same
		this.setUuid(COHORT_UUID);
	}

}
