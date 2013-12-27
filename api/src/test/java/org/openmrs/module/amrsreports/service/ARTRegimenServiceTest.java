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

package org.openmrs.module.amrsreports.service;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.model.ARTRegimen;
import org.openmrs.test.BaseModuleContextSensitiveTest;

import java.util.List;

public class ARTRegimenServiceTest extends BaseModuleContextSensitiveTest {

	/**
	 * @verifies return all ART Regimens regardless of retired property
	 * @see ARTRegimenService#getAllARTRegimens()
	 */
	@Test
	public void getAllARTRegimens_shouldReturnAllARTRegimensRegardlessOfRetiredProperty() throws Exception {
		ARTRegimenService ars = Context.getService(ARTRegimenService.class);
		List<ARTRegimen> actual = ars.getAllARTRegimens();
		Assert.assertNotNull(actual);
	}
}
