package org.isatools.isacreator.api.utils;

/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.sampleselection.SampleInformation;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import java.util.Date;
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

    private static Map<String, Map<String, SampleInformation>> studySampleInformation
            = new HashMap<String, Map<String, SampleInformation>>();

    private static Map<String, Boolean> studySampleFileModifiedFlag = new HashMap<String, Boolean>();
    private static Map<String, Boolean> shouldRunUpdate = new HashMap<String, Boolean>();


    /**
     * Creates a data structure with the sample name as the primary key and all other key/value pairs forming the rest of
     * the data.
     *
     * @param study - Study to be queried
     * @return - Map<String, Map<String, String>> -> Sample Name - other Key, Value pairs describing the sample
     */
    public static Map<String, SampleInformation> getStudySampleInformation(final Study study) {

        final Spreadsheet studySampleSpreadsheet = ((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(
                study.getStudySample())).getSpreadsheet();

        if (studySampleInformation.get(study.getStudyId()) == null || studySampleFileModifiedFlag.get(study.getStudyId())) {

            Thread sampleHarvestingThread = new Thread(new Runnable() {
                public void run() {
                    System.out.println(new Date(System.currentTimeMillis()).toString());
                    System.out.println("Getting study sample information");
                    studySampleInformation.put(study.getStudyId(), SpreadsheetUtils.getGroupInformation("Sample Name", studySampleSpreadsheet));
                    shouldRunUpdate.put(study.getStudyId(), true);
                    clearModifiedFlag(study.getStudyId());
                }
            });

            sampleHarvestingThread.start();

        } else {
            shouldRunUpdate.put(study.getStudyId(), false);
        }

        return studySampleInformation.get(study.getStudyId());
    }

    public static boolean shouldRunUpdate(String studyId) {
        if (shouldRunUpdate.containsKey(studyId)) {
            return shouldRunUpdate.get(studyId);
        }

        return true;
    }

    public static void studySampleFileModified(String studyId) {
        studySampleFileModifiedFlag.put(studyId, true);
    }

    public static void studySampleFileModified(Study study, boolean startHarvestingStudySampleNames) {

        studySampleFileModifiedFlag.put(study.getStudyId(), true);

        if (startHarvestingStudySampleNames) {
            getStudySampleInformation(study);
        }
    }

    public static boolean isModified(String studyId) {
        return studySampleFileModifiedFlag.containsKey(studyId) ? false : studySampleFileModifiedFlag.get(studyId);
    }


    public static void clearModifiedFlag(String studyId) {
        studySampleFileModifiedFlag.put(studyId, false);
    }

    public static String generateAssayReference(Study study, String measurement, String technology) {

        StringBuilder candidateAssayName = new StringBuilder("a_");
        candidateAssayName.append(study.getStudyId().toLowerCase()).append("_");
        candidateAssayName.append(measurement);

        if (!technology.equals("")) {
            candidateAssayName.append("_").append(technology);
        }

        return generateUniqueAssayReference(study, candidateAssayName.toString(), 0);
    }

    private static String generateUniqueAssayReference(Study study, String assayReference, int cycleCount) {

        String candidateRef = cycleCount == 0 ? assayReference + ".txt" : assayReference + "-" + cycleCount + ".txt";

        if (study.getAssays().containsKey(candidateRef)) {
            // generate the id using a recursive method
            return generateUniqueAssayReference(study, assayReference, cycleCount + 1);
        }

        candidateRef = candidateRef.replaceAll("\\s+","_");
        return candidateRef;
    }

}
