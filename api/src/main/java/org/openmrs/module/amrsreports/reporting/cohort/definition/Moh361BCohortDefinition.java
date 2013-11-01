package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * MOH 361B Register cohort definition
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.MOH361BCohortDefinition")
public class Moh361BCohortDefinition extends BaseCohortDefinition {

	public static final String COHORT_UUID = "MOH361A-01Cohort0000000000000000000000";

	@ConfigurationProperty
	private MOHFacility facility;

	public Moh361BCohortDefinition() {
		super();
		init();
	}

	public Moh361BCohortDefinition(MOHFacility facility) {
		super();
		this.facility = facility;
		init();
	}

	private void init() {
		// initialize metadata
		this.setName("MOH 361A Cohort");
		this.setDescription("Cohort for use with the MOH 361A Register");

		// ensure the UUID is always the same
		this.setUuid(COHORT_UUID);

		// set up parameters
		this.addParameter(new Parameter("facility", "Facility", MOHFacility.class));
	}

	public MOHFacility getFacility() {
		return facility;
	}

	public void setFacility(MOHFacility facility) {
		this.facility = facility;
	}
}
