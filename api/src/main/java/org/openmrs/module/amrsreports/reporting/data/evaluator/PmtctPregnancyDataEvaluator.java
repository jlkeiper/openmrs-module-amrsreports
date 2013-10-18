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

import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreports.reporting.data.PmtctPregnancyDataDefinition;
import org.openmrs.module.amrsreports.service.MohCoreService;
import org.openmrs.module.reporting.common.ListMap;
import org.openmrs.module.reporting.data.person.EvaluatedPersonData;
import org.openmrs.module.reporting.data.person.definition.PersonDataDefinition;
import org.openmrs.module.reporting.data.person.evaluator.PersonDataEvaluator;
import org.openmrs.module.reporting.evaluation.EvaluationContext;
import org.openmrs.module.reporting.evaluation.EvaluationException;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 */
@Handler(supports = PmtctPregnancyDataDefinition.class, order = 50)
public class PmtctPregnancyDataEvaluator implements PersonDataEvaluator {

	@Override
	public EvaluatedPersonData evaluate(final PersonDataDefinition definition, final EvaluationContext context) throws EvaluationException {
		EvaluatedPersonData data = new EvaluatedPersonData(definition, context);

		if (context.getBaseCohort().isEmpty())
			return data;

		String sql = "select" +
				"	ap.person_id," +
				"	ap.episode," +
				"	ap.due_date" +
				" from (" +
				"	select person_id, episode, max(pregnancy_id) as p_id" +
				"	from amrsreports_pregnancy ap" +
				"   where" +
				"     person_id in (:personIds)" +
				"     and pregnancy_date < :reportDate" +
				"	group by person_id, episode " +
				"	having episode > 0" +
				"	order by person_id asc" +
				" ) ordered" +
				"	left join amrsreports_pregnancy ap" +
				"		on ap.pregnancy_id = ordered.p_id";

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("personIds", context.getBaseCohort());
		m.put("reportDate", context.getEvaluationDate());

		ListMap<Integer, Date> dateMap = makeDateMapFromSQL(sql, m);

		for (Integer memberId : context.getBaseCohort().getMemberIds()) {

			Set<Date> dueDates = safeFind(dateMap, memberId);
			data.addData(memberId, dueDates);
		}

		return data;
	}

	private Set<Date> safeFind(final ListMap<Integer, Date> map, final Integer key) {
		Set<Date> dateSet = new LinkedHashSet<Date>();
		if (map.containsKey(key))
			dateSet.addAll(map.get(key));
		return dateSet;
	}

	/**
	 * replaces reportDate and personIds with data from private variables before generating a date map
	 */
	private ListMap<Integer, Date> makeDateMapFromSQL(String sql, Map<String, Object> substitutions) {
		List<Object> data = Context.getService(MohCoreService.class).executeSqlQuery(sql, substitutions);
		return makeDateMap(data);
	}

	/**
	 * generates a map of integers to dates, assuming this is the kind of response expected from the SQL
	 */
	private ListMap<Integer, Date> makeDateMap(List<Object> data) {
		ListMap<Integer, Date> dateListMap = new ListMap<Integer, Date>();
		for (Object o : data) {
			Object[] parts = (Object[]) o;
			// data comes in as [id, episode, date] ... don't need episode
			if (parts.length == 3) {
				Integer pId = (Integer) parts[0];
				Date date = (Date) parts[2];
				dateListMap.putInList(pId, date);
			}
		}

		return dateListMap;
	}

}
