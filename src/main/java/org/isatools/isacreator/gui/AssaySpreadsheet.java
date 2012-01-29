/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
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

package org.isatools.isacreator.gui;

import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.awt.*;

/**
 * AssayDataEntry provides link from Assay to its associate GUI.
 *
 * @author Eamonn Maguire
 * @date Jun 10, 2008
 */
public class AssaySpreadsheet extends DataEntryForm {
    private Spreadsheet table;


    /**
     * AssayDataEntry constructor
     * Associated StudyDataEntry object and TableReferenceObject are defined here.
     *
     * @param studyDataEntry       - The StudyDataEntryObject associated with this assays related study.
     * @param tableReferenceObject - TableReferenceObject to be used to create the Spreadsheet.
     */
    public AssaySpreadsheet(StudyDataEntry studyDataEntry, TableReferenceObject tableReferenceObject) {
        this(studyDataEntry, tableReferenceObject, "[Sample]", "");

    }

    public AssaySpreadsheet(StudyDataEntry studyDataEntry, TableReferenceObject tableReferenceObject, String measurementType, String technologyType) {
        super(studyDataEntry.getDataEntryEnvironment());
        instantiatePane();

        String title = constructTitleString(measurementType, technologyType);

        table = new Spreadsheet(tableReferenceObject, studyDataEntry, title, this);
        add(table, BorderLayout.CENTER);
        finalisePane();
    }

    private String constructTitleString(String measType, String techType) {
        String toReturn = "";
        if (measType.equals("[Sample]")) {
            toReturn = "Sample Definitions";
        } else if (!measType.equals("") && !measType.equals("n/a")) {
            toReturn = "<p align=\"right\">Assay measuring <b>" + measType + "</b>";
            if (!techType.equals("")) {
                toReturn += " using <b>" + techType + "</b></p>";
            }
        }

        toReturn = "<html>" + toReturn + "</html>";

        return toReturn;
    }

    /**
     * Return the Spreadsheet.
     *
     * @return Spreadsheet
     */
    public Spreadsheet getTable() {
        return table;
    }

    public void setTable(Spreadsheet table) {
        this.table = table;
    }
}
