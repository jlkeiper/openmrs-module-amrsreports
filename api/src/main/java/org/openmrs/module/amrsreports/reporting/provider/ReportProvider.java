package org.openmrs.module.amrsreports.reporting.provider;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.cohort.definition.AMRSReportsCohortDefinition;
import org.openmrs.module.amrsreports.reporting.report.processor.SaveReportProcessor;
import org.openmrs.module.amrsreports.reporting.report.renderer.AMRSReportsExcelRenderer;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.reporting.report.processor.LoggingReportProcessor;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

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

	protected abstract String getReportDesignUUID();

	public ReportDesign getReportDesign() {

		ReportDesign design = new ReportDesign();
		design.setName(this.getName() + " Design");
		design.setRendererType(AMRSReportsExcelRenderer.class);
		design.setUuid(this.getReportDesignUUID());

		// add the definition
		design.setReportDefinition(Context.getService(ReportDefinitionService.class).getDefinitionByUuid(this.getReportDefinition().getUuid()));

		// add repeating sections property
		Properties props = new Properties();
		props.put("repeatingSections", this.getRepeatingSections());
		design.setProperties(props);

		// add the excel template
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("templates/" + this.getTemplateFilename());

		if (is == null)
			throw new APIException("Could not find report template.");

		try {
			resource.setContents(IOUtils.toByteArray(is));
		} catch (IOException ex) {
			throw new APIException("Could not create report design for MOH 361A Register.", ex);
		}

		IOUtils.closeQuietly(is);
		resource.setReportDesign(design);
		design.addResource(resource);

		// add the logging processor
		ReportProcessorConfiguration logger = new ReportProcessorConfiguration(
				"Log AMRS Reports",
				LoggingReportProcessor.class,
				new Properties(),
				true,
				false
		);
		logger.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.AUTOMATIC);
		logger.setUuid(UUID.randomUUID().toString());
		design.addReportProcessor(logger);

		// add the saving processor
		ReportProcessorConfiguration saver = new ReportProcessorConfiguration(
				"Save AMRS Reports",
				SaveReportProcessor.class,
				new Properties(),
				true,
				false
		);
		saver.setProcessorMode(ReportProcessorConfiguration.ProcessorMode.AUTOMATIC);
		saver.setUuid(UUID.randomUUID().toString());
		design.addReportProcessor(saver);

		return design;
	}
}
