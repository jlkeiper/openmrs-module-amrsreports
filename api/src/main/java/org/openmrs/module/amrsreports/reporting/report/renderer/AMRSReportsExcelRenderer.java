/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.amrsreports.reporting.report.renderer;

import org.apache.commons.io.IOUtils;
import org.openmrs.api.APIException;
import org.openmrs.module.amrsreports.reporting.provider.ReportProvider;
import org.openmrs.module.amrsreports.reporting.report.processor.SaveReportProcessor;
import org.openmrs.module.amrsreports.service.ReportProviderRegistrar;
import org.openmrs.module.reporting.report.ReportDesign;
import org.openmrs.module.reporting.report.ReportDesignResource;
import org.openmrs.module.reporting.report.ReportProcessorConfiguration;
import org.openmrs.module.reporting.report.processor.LoggingReportProcessor;
import org.openmrs.module.reporting.report.renderer.ExcelTemplateRenderer;
import org.openmrs.util.OpenmrsClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.openmrs.module.reporting.report.ReportProcessorConfiguration.ProcessorMode;

public class AMRSReportsExcelRenderer extends ExcelTemplateRenderer {

	public ReportDesign getDesign(String argument) {

		ReportProvider rp = ReportProviderRegistrar.getInstance().getReportProviderByName(argument);

		ReportDesign design = new ReportDesign();
		design.setName(rp.getName() + " Design");
		design.setRendererType(ExcelTemplateRenderer.class);

		// add repeating sections property
		Properties props = new Properties();
		props.put("repeatingSections", rp.getRepeatingSections());
		design.setProperties(props);

		// add the excel template
		ReportDesignResource resource = new ReportDesignResource();
		resource.setName("template.xls");
		InputStream is = OpenmrsClassLoader.getInstance().getResourceAsStream("templates/" + rp.getTemplateFilename());

		if (is == null)
			throw new APIException("Could not find report template.");

		try {
			resource.setContents(IOUtils.toByteArray(is));
		} catch (IOException ex) {
			throw new APIException("Could not create report design for MOH 361A Register.", ex);
		}

		IOUtils.closeQuietly(is);
		design.addResource(resource);

		// add the logging processor
		design.addReportProcessor(new ReportProcessorConfiguration(
				"Log AMRS Reports",
				LoggingReportProcessor.class,
				new Properties(),
				true,
				false
		));

		// add the saving processor
		design.addReportProcessor(new ReportProcessorConfiguration(
				"Save AMRS Reports",
				SaveReportProcessor.class,
				new Properties(),
				true,
				false
		));

		// TODO is this where we put rendering to CSV, etc?  DiskReportProcessor?

		return design;
	}

}
