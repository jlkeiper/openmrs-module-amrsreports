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

package org.openmrs.module.amrsreports.reporting.report.processor;

import org.openmrs.api.context.Context;
import org.openmrs.module.reporting.report.Report;
import org.openmrs.module.reporting.report.processor.ReportProcessor;
import org.openmrs.module.reporting.report.service.ReportService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class SaveReportProcessor implements ReportProcessor {

	@Override
	public List<String> getConfigurationPropertyNames() {
		return new ArrayList<String>();
	}

	@Override
	public void process(Report report, Properties configuration) {
		Context.getService(ReportService.class).saveReportRequest(report.getRequest());
	}
}
