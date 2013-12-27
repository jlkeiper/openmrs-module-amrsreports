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

package org.openmrs.module.amrsreports.model;

import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Drug;

import java.util.ArrayList;
import java.util.List;

public class ARTRegimenDrug extends BaseOpenmrsMetadata {

	private Integer artRegimenDrugId = null;
	private List<Drug> drugs = null;

	@Override
	public Integer getId() {
		return getArtRegimenDrugId();
	}

	@Override
	public void setId(Integer id) {
		this.setArtRegimenDrugId(id);
	}

	public Integer getArtRegimenDrugId() {
		return artRegimenDrugId;
	}

	public void setArtRegimenDrugId(Integer artRegimenDrugId) {
		this.artRegimenDrugId = artRegimenDrugId;
	}

	public List<Drug> getDrugs() {
		if (drugs == null) {
			drugs = new ArrayList<Drug>();
		}
		return drugs;
	}

	public void setDrugs(List<Drug> drugs) {
		this.drugs = drugs;
	}
}
