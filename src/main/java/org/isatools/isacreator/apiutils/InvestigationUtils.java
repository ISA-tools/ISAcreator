package org.isatools.isacreator.apiutils;

import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/02/2011
 *         Time: 16:21
 */
public class InvestigationUtils {


    /**
     * Method return all the unannotated freetext found in the Investigation
     *
     * @param investigation @see Investigation
     * @return Map from Assay object (general container for spreadsheet) to a Map from the Column Name to it's unannotated values
     */
    public static Map<Assay, Map<String, Set<String>>> getFreeTextInInvestigationSpreadsheets(Investigation investigation) {

        Map<Assay, Map<String, Set<String>>> result = new HashMap<Assay, Map<String, Set<String>>>();

        Map<String, Study> studies = investigation.getStudies();

        for (String studyId : studies.keySet()) {
            Study study = studies.get(studyId);

            result.put(study.getStudySample(),
                    SpreadsheetUtils.getFreetextInSpreadsheet(study.getStudySample().getSpreadsheetUI().getSpreadsheet()));

            for (Assay assay : study.getAssays().values()) {
                result.put(assay, SpreadsheetUtils.getFreetextInSpreadsheet(assay.getSpreadsheetUI().getSpreadsheet()));
            }
        }

        return result;
    }
}
