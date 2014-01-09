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

package org.openmrs.module.amrsreports.db.hibernate;

import org.hibernate.SessionFactory;
import org.openmrs.module.amrsreports.db.ARTRegimenDAO;
import org.openmrs.module.amrsreports.model.ARTRegimen;

import java.util.List;

public class HibernateARTRegimenDAO implements ARTRegimenDAO {

	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public List<ARTRegimen> getAllARTRegimens() {
		return (List<ARTRegimen>) sessionFactory.getCurrentSession()
				.createCriteria(ARTRegimen.class)
				.list();
	}

	@Override
	public ARTRegimen getARTRegimen(Integer artRegimenId) {
		return (ARTRegimen) sessionFactory.getCurrentSession()
				.get(ARTRegimen.class, artRegimenId);
	}

	@Override
	public ARTRegimen saveARTRegimen(ARTRegimen regimen) {
		sessionFactory.getCurrentSession().saveOrUpdate(regimen);
		return regimen;
	}

	@Override
	public void purgeARTRegimen(ARTRegimen regimen) {
		sessionFactory.getCurrentSession().delete(regimen);
	}
}
