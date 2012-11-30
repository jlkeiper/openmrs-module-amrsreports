package org.openmrs.module.amrsreport.rule.calculation;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.Calculation;
import org.openmrs.calculation.parameter.ParameterDefinitionSet;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.module.amrsreport.calculation.DemoCalculation;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Example rule demonstrating the use of Calculations to generate evaluations
 */
public class CalculationDemoRule extends MohEvaluableRule {

	private static final DemoCalculation calculation = new DemoCalculation();

	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		Collection<Integer> cohort = Arrays.asList(new Integer[]{ patientId });
		CalculationResultMap results = Context.getService(PatientCalculationService.class).evaluate(cohort, calculation);
		return new Result(results.get(patientId).toString());
	}

	@Override
	protected String getEvaluableToken() {
		return "MOH Calculation Demo Rule";
	}
}
