package org.openmrs.module.amrsreport.calculation;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.calculation.BaseCalculation;
import org.openmrs.calculation.patient.PatientCalculation;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResult;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;

import java.util.Collection;
import java.util.Map;

/**
 * Demo Calculation for use with CalculationDemoRule
 */
public class DemoCalculation extends BaseCalculation implements PatientCalculation {

	@Override
	public CalculationResultMap evaluate(Collection<Integer> patientIds, Map<String, Object> parameters, PatientCalculationContext context) {
		CalculationResultMap results = new CalculationResultMap();
		for (Integer patientId : patientIds) {
			Patient patient = Context.getPatientService().getPatient(patientId);
			results.put(patientId, new SimpleResult(String.format("%s (%d)", patient.getPersonName(), patient.getAge()), this, context));
		}
		return results;
	}
}
