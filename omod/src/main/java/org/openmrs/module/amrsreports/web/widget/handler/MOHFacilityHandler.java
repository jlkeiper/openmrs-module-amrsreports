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
package org.openmrs.module.amrsreports.web.widget.handler;

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.MOHFacility;
import org.openmrs.module.amrsreports.service.MOHFacilityService;
import org.openmrs.module.htmlwidgets.web.WidgetConfig;
import org.openmrs.module.htmlwidgets.web.handler.CodedHandler;
import org.openmrs.module.htmlwidgets.web.html.CodedWidget;
import org.openmrs.module.htmlwidgets.web.html.Option;

/**
 * FieldGenHandler for Calculations
 */
@Handler(supports = {MOHFacility.class}, order = 50)
public class MOHFacilityHandler extends CodedHandler {

	/**
	 * @see CodedHandler#populateOptions(WidgetConfig, CodedWidget)
	 */
	@Override
	public void populateOptions(WidgetConfig widgetConfig, CodedWidget codedWidget) {
		for (MOHFacility f : Context.getService(MOHFacilityService.class).getAllFacilities())
			codedWidget.addOption(new Option(f.getUuid(), f.toString(), null, f), widgetConfig);
	}

	/**
	 * @see org.openmrs.module.htmlwidgets.web.handler.WidgetHandler#parse(String, Class<?>)
	 */
	@Override
	public Object parse(String s, Class<?> aClass) {
		return Context.getService(MOHFacilityService.class).getFacilityByUuid(s);
	}
}
