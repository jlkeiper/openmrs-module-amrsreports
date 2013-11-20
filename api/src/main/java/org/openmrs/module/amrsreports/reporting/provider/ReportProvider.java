package org.openmrs.module.amrsreports.reporting.provider;

import org.openmrs.module.amrsreports.reporting.cohort.definition.AMRSReportsCohortDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Defines what it means to be a report provider
 */
public abstract class ReportProvider {

	protected String name;
	protected Boolean visible = false;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public abstract ReportDefinition getReportDefinition();

	public abstract AMRSReportsCohortDefinition getCohortDefinition();

	public abstract String getRepeatingSections();

	public abstract String getTemplateFilename();
}
