package org.openmrs.module.amrsreport.reports;

import org.openmrs.PatientIdentifierType;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.context.Context;
import org.openmrs.module.amrsreport.cache.MohCacheUtils;
import org.openmrs.module.amrsreport.reports.converter.MOHPersonNameConverter;
import org.openmrs.module.amrsreport.reports.converter.MOHSerialNumberConverter;
import org.openmrs.module.amrsreport.rule.MohEvaluableNameConstants;
import org.openmrs.module.amrsreport.rule.util.MohRuleUtils;
import org.openmrs.module.reporting.data.converter.AgeConverter;
import org.openmrs.module.reporting.data.converter.BirthdateConverter;
import org.openmrs.module.reporting.data.converter.DateConverter;
import org.openmrs.module.reporting.data.converter.ListConverter;
import org.openmrs.module.reporting.data.converter.ObjectFormatter;
import org.openmrs.module.reporting.data.converter.PropertyConverter;
import org.openmrs.module.reporting.data.converter.StringConverter;
import org.openmrs.module.reporting.data.patient.definition.LogicDataDefinition;
import org.openmrs.module.reporting.data.patient.definition.PatientIdentifierDataDefinition;
import org.openmrs.module.reporting.data.person.definition.AgeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.BirthdateDataDefinition;
import org.openmrs.module.reporting.data.person.definition.GenderDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PersonAttributeDataDefinition;
import org.openmrs.module.reporting.data.person.definition.PreferredNameDataDefinition;
import org.openmrs.module.reporting.dataset.definition.PatientDataSetDefinition;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;

/**
 * Renders the MOH 361A Pre-ART Register
 */
public class MOH361AReport {

	public static ReportDefinition getReportDefinition() {

		ReportDefinition report = new PeriodIndicatorReportDefinition();
		report.setName("MOH 361A Report");

		// set up the columns
		PatientDataSetDefinition dsd = new PatientDataSetDefinition();
		dsd.setName("MOH 361A Data Set Definition");

		// a. serial number
		PatientIdentifierType pit = MohCacheUtils.getPatientIdentifierType(
				Context.getAdministrationService().getGlobalProperty("cccgenerator.CCC"));
		dsd.addColumn("Serial Number", new PatientIdentifierDataDefinition("CCC", pit), null, new MOHSerialNumberConverter());

		// b. date chronic HIV+ care started
//		LogicDataDefinition columnB = new LogicDataDefinition();
//		columnB.setLogicQuery("\"MOH Confirmed HIV Positive Date\"");
//		dsd.addColumn("Date Chronic HIV Care Started", columnB, null);

		// c. Unique Patient Number
		dsd.addColumn("Unique Patient Number", new PatientIdentifierDataDefinition("CCC", pit), null);

		// d. Patient's Name
		dsd.addColumn("Name", new PreferredNameDataDefinition(), null, new MOHPersonNameConverter());

		// e1. Date of Birth
		dsd.addColumn("Date of Birth", new BirthdateDataDefinition(), null, new BirthdateConverter(MohRuleUtils.DATE_FORMAT));

		// e2. Age at Enrollment
//		dsd.addColumn("Age at Enrollment", new AgeDataDefinition(), null, new AgeConverter());

		// f. Sex
		dsd.addColumn("Sex", new GenderDataDefinition(), null);

//		// g. Entry point: From where?
//		// TODO add a StringConverter here
//		PersonAttributeType pat = Context.getPersonService().getPersonAttributeTypeByName(MohEvaluableNameConstants.POINT_OF_HIV_TESTING);
//		dsd.addColumn("Entry Point", new PersonAttributeDataDefinition("entryPoint", pat), null);
//
//		// h. Confirmed HIV+ Date
//		dsd.addColumn("Confirmed HIV+ Date", columnB, null);
//
		// i. PEP Start / Stop Date
		LogicDataDefinition columnI = new LogicDataDefinition();
		columnI.setLogicQuery("\"MOH PEP Start Stop Date\"");
		dsd.addColumn("PEP Start / Stop Date", columnI, null);
//
//		// j. Reasons for PEP use:
//		LogicDataDefinition columnJ = new LogicDataDefinition();
//		columnJ.setLogicQuery("\"MOH Reasons For PEP\"");
//		dsd.addColumn("Reasons for PEP Use", columnJ, null);
//
//		// k. CTX startdate and stopdate:
////		LogicDataDefinition columnK = new LogicDataDefinition();
////		columnK.setLogicQuery("\"MOH CTX Start-Stop Date\"");
////		dsd.addColumn("CTX Start / Stop Date", columnK, null);
//
//		// l. Fluconazole startdate and stopdate
//		LogicDataDefinition columnL = new LogicDataDefinition();
//		columnL.setLogicQuery("\"MOH Fluconazole Start-Stop Date\"");
//		dsd.addColumn("Fluconazole Start / Stop Date", columnL, null);
//
//		// m. TB treatment startdate and stopdate
////		LogicDataDefinition columnM = new LogicDataDefinition();
////		columnM.setLogicQuery("\"MOH TB Start-Stop Date\"");
////		dsd.addColumn("TB Treatment Start / Stop Date", columnM, null);
//
//		// n. Pregnancy Yes?, Due date, PMTCT refer
//		LogicDataDefinition columnN = new LogicDataDefinition();
//		columnN.setLogicQuery("\"MOH Pregnancy PMTC Referral\"");
//		dsd.addColumn("Pregnancy EDD and Referral", columnN, null);
//
//		// o. LTFU / TO / Dead and date when the event occurred
//		LogicDataDefinition columnO = new LogicDataDefinition();
//		columnO.setLogicQuery("\"MOH LTFU-TO-DEAD\"");
//		dsd.addColumn("LTFU / TO / DEAD", columnO, null);
//
//		// p. WHO clinical Stage and date
//		LogicDataDefinition columnP = new LogicDataDefinition();
//		columnP.setLogicQuery("\"MOH WHO Stage\"");
//		dsd.addColumn("WHO Clinical Stage", columnP, null);
//
//		// q. Date medically eligible for ART
//		LogicDataDefinition columnQ = new LogicDataDefinition();
//		columnQ.setLogicQuery("\"MOH Date and Reason Medically Eligible For ART\"");
//		dsd.addColumn("Date Medically Eligible for ART", columnQ, null);
//
//		// r. Reason Medically Eligible for ART
//		LogicDataDefinition columnR = new LogicDataDefinition();
//		columnR.setLogicQuery("\"MOH Date and Reason Medically Eligible For ART\"");
//		dsd.addColumn("Reason Medically Eligible for ART", columnR, null);
//
//		// s. Date ART started (Transfer to ART register)
//		LogicDataDefinition columnS = new LogicDataDefinition();
//		columnS.setLogicQuery("\"MOH Date ART Started\"");
//		dsd.addColumn("Date ART Started", columnS, null);

		report.addDataSetDefinition(dsd, null);

		return report;
	}

}
