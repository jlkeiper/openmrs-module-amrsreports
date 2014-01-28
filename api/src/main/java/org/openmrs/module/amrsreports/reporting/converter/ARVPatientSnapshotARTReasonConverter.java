package org.openmrs.module.amrsreports.reporting.converter;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.amrsreports.snapshot.ARVPatientSnapshot;
import org.openmrs.module.amrsreports.util.MOHReportUtil;
import org.openmrs.module.reporting.data.converter.DataConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Converter for Patient Snapshots
 */
public class ARVPatientSnapshotARTReasonConverter implements DataConverter {

    /**
     * @see DataConverter#convert(Object)
     *
     * @should return null if snapshot is null
     * @should return null if no reason or ti exists
     * @should return formatted reason and ti
     */

	@Override
	public Object convert(Object original) {
		ARVPatientSnapshot s = (ARVPatientSnapshot) original;

		if (s == null)
			return "";

		if (!s.hasProperty("reason") && !s.hasProperty("ti"))
			return "";

		// do not report a date if there is no reason
		List<String> results = new ArrayList<String>();

        //get reason flag from snapshot
        String reason = (String)s.get("reason");
        String ti = (String)s.get("ti");

        /*test for TI and report back*/
        if(StringUtils.isNotBlank(ti)){
            results.add("TI");

            if(StringUtils.isNotBlank(reason) && StringUtils.equals(reason,ARVPatientSnapshot.REASON_CLINICAL)){
                results.add("CLINICAL");
            }
            else if(StringUtils.isNotBlank(reason) && StringUtils.equals(reason, "CD4")){
                results.add("CD4");
            }
            else if (StringUtils.isNotBlank(reason) && StringUtils.equals(reason,ARVPatientSnapshot.REASON_CLINICAL_CD4) ){
                results.add("CLINICAL + CD4");
            }

            return MOHReportUtil.joinAsSingleCell(results);
        }

        if(StringUtils.equals(reason,ARVPatientSnapshot.REASON_CLINICAL)){
            results.add("CLINICAL");

            if (s.hasProperty("extras"))
                results.addAll((List<String>) s.get("extras"));

        }
        /*this entirely requires the use of CD4 count or CD4 percent as eligibility criteria
         *
          * */
        else if(StringUtils.equals(reason, "CD4")){
            results.add("CD4");

            if (s.hasProperty("extras")){
                List<String> extras = (List<String>) s.get("extras");
                String cd4count = extras.get(1);
                results.add(cd4count);
            }

        }
        else if(StringUtils.equals(reason,ARVPatientSnapshot.REASON_CLINICAL_CD4)){

            if (s.hasProperty("extras")){
                List<String> extras = (List<String>) s.get("extras");
                String whoStage = extras.get(0);
                String cd4count = extras.get(1);
                results.add(whoStage);
                results.add(cd4count);
            }

        }

		return MOHReportUtil.joinAsSingleCell(results);
	}

	@Override
	public Class<?> getInputDataType() {
		return ARVPatientSnapshot.class;
	}

	@Override
	public Class<?> getDataType() {
		return String.class;
	}
}