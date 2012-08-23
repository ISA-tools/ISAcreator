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

package org.isatools.isacreator.qrcode.utils;

import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.formatmappingutility.ui.MappingChoice;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.utils.StringProcessing;

import java.util.*;

/**
 * QRAPIHook
 *
 * @author eamonnmaguire
 * @date Oct 26, 2010
 */


public class QRAPIHook {
    private static final String SAMPLE_NAME = "sample name";

    private ISAcreator isacreatorEnvironment;
    private String studyId;

    private String[][] subData;
    private String[] columnNames;

    private Set<String> sampleNames;

    private int sampleNameIndex = -1;

    public QRAPIHook(ISAcreator isacreatorEnvironment, String studyId) {
        this.isacreatorEnvironment = isacreatorEnvironment;
        this.studyId = studyId;
    }

    public void generateDataFromEnvironment() {
        Study study = getInvestigation().getStudies().get(studyId);

        AssaySpreadsheet studySampleInterface = (AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(study.getStudySample());

        subData = SpreadsheetUtils.getSpreadsheetDataSubset(studySampleInterface.getSpreadsheet(), 4);

        Map<Integer, String> columnIndexToName = SpreadsheetUtils.getColumns(
                studySampleInterface.getSpreadsheet(), new HashSet<String>());

        columnNames = columnIndexToName.values().toArray(new String[columnIndexToName.values().size()]);

        sampleNameIndex = getSampleNameIndex(columnIndexToName);
        if (sampleNameIndex != -1) {
            sampleNames = SpreadsheetUtils.getDataInColumn(studySampleInterface.getSpreadsheet(), 2);
        } else {
            sampleNames = new HashSet<String>();
        }
    }

    private int getSampleNameIndex(Map<Integer, String> colIndexToName) {
        for (int colIndex : colIndexToName.keySet()) {
            if (colIndexToName.get(colIndex).equalsIgnoreCase(SAMPLE_NAME)) {
                return colIndex;
            }
        }

        return -1;
    }

    /**
     * Returns each sample mapped to it's encoded contents for production of the QR codes
     *
     * @param qrCodeBuilderInfo - List of MappingChoices completed by the user
     * @return SampleName -> QR Code contents.
     */
    public Map<String, String> generateEncodeInfo(List<MappingChoice> qrCodeBuilderInfo) {
        Map<String, String> sampleNameToEncoding = new HashMap<String, String>();

        StringBuilder substitution = new StringBuilder();

        for (MappingChoice mc : qrCodeBuilderInfo) {

            substitution.append(mc.getValueEntered());
        }

        Study study = getInvestigation().getStudies().get(studyId);
        String[][] data = SpreadsheetUtils.getSpreadsheetDataSubset(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(study.getStudySample())).getSpreadsheet());

        for (String[] aData : data) {
            sampleNameToEncoding.put(aData[sampleNameIndex - 1],
                    StringProcessing.processSubstitutionString(substitution.toString(), aData));
        }

        return sampleNameToEncoding;
    }


    private Investigation getInvestigation() {
        return isacreatorEnvironment.getDataEntryEnvironment().getInvestigation();
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public Set<String> getSampleNames() {
        return sampleNames;
    }

    public String[][] getSubData() {
        return subData;
    }
}
