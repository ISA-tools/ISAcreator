package org.isatools.isacreator.apiutils;

import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.sampleselection.SampleInformation;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/02/2011
 *         Time: 16:21
 */
public class StudyUtils {

    private static String currentStudyId;
    private static Map<String, Map<String, SampleInformation>> studySampleInformation
            = new HashMap<String, Map<String, SampleInformation>>();

    // todo add flag to manage changes to the study sample file
    private static Map<String, Boolean> studySampleFileModifiedFlag = new HashMap<String, Boolean>();


    /**
     * Creates a data structure with the sample name as the primary key and all other key/value pairs forming the rest of
     * the data.
     *
     * @param study - Study to be queried
     * @return - Map<String, Map<String, String>> -> Sample Name - other Key, Value pairs describing the sample
     */
    public static Map<String, SampleInformation> getStudySampleInformation(Study study) {
        Spreadsheet studySampleSpreadsheet = study.getStudySample().getSpreadsheetUI().getTable();

        if (studySampleInformation == null || studySampleFileModifiedFlag.get(study.getStudyId())) {
            System.out.println("Getting study sample information");

            studySampleInformation.put(study.getStudyId(), SpreadsheetUtils.getGroupInformation("Sample Name", studySampleSpreadsheet));
            clearModifiedFlag(study.getStudyId());
        }

        return studySampleInformation.get(study.getStudyId());
    }

    public static void studySampleFileModified(String studyId) {
        studySampleFileModifiedFlag.put(studyId, true);
    }

    public static boolean isModified(String studyId) {
        return studySampleFileModifiedFlag.containsKey(studyId) ? false : studySampleFileModifiedFlag.get(studyId);
    }

    public static void clearModifiedFlag(String studyId) {
        studySampleFileModifiedFlag.put(studyId, false);
    }

}
