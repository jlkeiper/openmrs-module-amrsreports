package org.openmrs.module.amrsreports.reporting.cohort.definition;

import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.reporting.cohort.definition.BaseCohortDefinition;
import org.openmrs.module.reporting.common.Localized;
import org.openmrs.module.reporting.definition.configuration.ConfigurationProperty;
import org.openmrs.module.reporting.definition.configuration.ConfigurationPropertyCachingStrategy;
import org.openmrs.module.reporting.evaluation.caching.Caching;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;

/**
 * Parent class for cohort definitions used in AMRS Reports
 */
@Caching(strategy = ConfigurationPropertyCachingStrategy.class)
@Localized("reporting.AMRSReportsCohortDefinition")
public abstract class AMRSReportsCohortDefinition extends BaseCohortDefinition {

	@ConfigurationProperty
	private MOHFacility facility;

	public AMRSReportsCohortDefinition() {
		super();
		init();
	}

	public AMRSReportsCohortDefinition(MOHFacility facility) {
		super();
		this.facility = facility;
		init();
	}

	public MOHFacility getFacility() {
		return facility;
	}

	public void setFacility(MOHFacility facility) {
		this.facility = facility;
	}

	protected void init() {
		// initialize metadata
		this.initProperties();

		// set up parameters
		this.addParameter(new Parameter("facility", "Facility", MOHFacility.class));
	}

	protected abstract void initProperties();
}