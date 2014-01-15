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
package org.openmrs.module.amrsreports.reporting.data.evaluator;

import org.openmrs.PersonAttributeType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.CohortRestrictedPersonAttributeDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.DateARTStartedDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TbStartStopDataDefinition;
import org.openmrs.module.amrsreports.reporting.data.TbTreatmentStartDateDataDefinition;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.service.PersonDataService;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
@Handler(supports = TbTreatmentStartDateDataDefinition.class, order = 50)
public class TbTreatmentStartDateDataEvaluator extends DrugStartStopDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());

        String sql = "select person_id, " +
                "(CASE" +
                "     WHEN concept_id = 1113 THEN value_datetime" +
                "     WHEN concept_id = 1268 AND value_coded=1256 THEN obs_datetime" +
                "END" +
                ") as start_date" +
                "from obs  " +
                " 	  where" +
                "		person_id in (:personIds)" +
                "		and voided = 0";

		ListMap<Integer, Date> mappedStartDates = makeDatesMapFromSQL(sql, m);


        /*get tb registration number for patients*/
        PersonAttributeType pat = Context.getPersonService().getPersonAttributeType(17);
        CohortRestrictedPersonAttributeDataDefinition patientTBRegistrationDetails = new CohortRestrictedPersonAttributeDataDefinition(pat);

        EvaluatedPersonData tbRegData = Context.getService(PersonDataService.class).evaluate(patientTBRegistrationDetails, context);
        Map<Integer,Object> regDetails = tbRegData.getData();

        List allDetails = new ArrayList();


		for (Integer memberId : context.getBaseCohort().getMemberIds()) {
			Set<Date> startDates = safeFind(mappedStartDates, memberId);
            String tbRegistrationNo = (String)regDetails.get(memberId);

            /*Add findings to the list*/

            allDetails.add(startDates);
            allDetails.add(tbRegistrationNo);
			data.addData(memberId, allDetails);
		}

		return data;
	}

}