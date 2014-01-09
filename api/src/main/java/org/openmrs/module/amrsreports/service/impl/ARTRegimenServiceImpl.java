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

package org.openmrs.module.amrsreports.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.amrsreports.db.ARTRegimenDAO;
import org.openmrs.module.amrsreports.model.ARTRegimen;
import org.openmrs.module.amrsreports.service.ARTRegimenService;

import java.util.Date;
import java.util.List;

public class ARTRegimenServiceImpl extends BaseOpenmrsService implements ARTRegimenService {

	ARTRegimenDAO dao;
	private final Log log = LogFactory.getLog(getClass());

	public ARTRegimenDAO getDao() {
		return dao;
	}

	public void setDao(ARTRegimenDAO dao) {
		this.dao = dao;
	}

	@Override
	public List<ARTRegimen> getAllARTRegimens() {
		return dao.getAllARTRegimens();
	}

	@Override
	public ARTRegimen getARTRegimen(Integer artRegimenId) {
		return dao.getARTRegimen(artRegimenId);
	}

	@Override
	public ARTRegimen saveARTRegimen(ARTRegimen regimen) {
		return dao.saveARTRegimen(regimen);
	}

	@Override
	public void retireARTRegimen(ARTRegimen regimen, String retireReason) {
		if (retireReason == null || retireReason.length() < 1)
			throw new APIException("A reason is required when retiring a regimen");

		regimen.setRetired(true);
		regimen.setRetiredBy(Context.getAuthenticatedUser());
		regimen.setDateRetired(new Date());
		regimen.setRetireReason(retireReason);

		dao.saveARTRegimen(regimen);
	}

	@Override
	public void unretireARTRegimen(ARTRegimen regimen) {
		regimen.setRetired(false);
		regimen.setRetiredBy(null);
		regimen.setDateRetired(null);
		regimen.setRetireReason(null);

		dao.saveARTRegimen(regimen);
	}

	@Override
	public void purgeARTRegimen(ARTRegimen regimen) {
		dao.purgeARTRegimen(regimen);
	}
}
