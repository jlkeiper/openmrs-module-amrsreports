package org.openmrs.module.amrsreport.rule.calculation;

import org.openmrs.api.context.Context;
import org.openmrs.calculation.patient.PatientCalculationService;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.logic.LogicContext;
import org.openmrs.logic.result.Result;
import org.openmrs.logic.rule.RuleParameterInfo;
import org.openmrs.module.amrsreport.calculation.DemoCalculation;
import org.openmrs.module.amrsreport.rule.MohEvaluableRule;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Example rule demonstrating the use of Calculations to generate evaluations
 */
public class CalculationDemoRule extends MohEvaluableRule {

	private static final DemoCalculation calculation = new DemoCalculation();

	public static final String TOKEN = "MOH Calculation Demo Rule";

	@Override
	protected Result evaluate(LogicContext context, Integer patientId, Map<String, Object> parameters) {
		Collection<Integer> cohort = Arrays.asList(new Integer[]{patientId});
		CalculationResultMap results = Context.getService(PatientCalculationService.class).evaluate(cohort, calculation);
		return new Result(results.get(patientId).toString());
	}

	@Override
	protected String getEvaluableToken() {
		return TOKEN;
	}

	/**
	 * @see org.openmrs.logic.Rule#getDependencies()
	 */
	@Override
	public String[] getDependencies() {
		return new String[]{};
	}

	/**
	 * Get the definition of each parameter that should be passed to this rule execution
	 *
	 * @return all parameter that applicable for each rule execution
	 */
	@Override
	public Result.Datatype getDefaultDatatype() {
		return Result.Datatype.TEXT;
	}

	public Set<RuleParameterInfo> getParameterList() {
		return null;
	}

	@Override
	public int getTTL() {
		return 0;
	}
}
