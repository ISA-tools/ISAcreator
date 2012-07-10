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

package org.isatools.isacreator.io.exportisa;

import org.apache.log4j.Logger;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.IncorrectColumnOrderGUI;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Outputs the ISAtab files corresponding to their respective objects in the ISAcreator model.
 */
public class OutputISAFilesFromGUI extends ISAFileOutput {
    private static final Logger log = Logger.getLogger(OutputISAFilesFromGUI.class.getName());

    private List<Spreadsheet> errorSheets;
    private List<Spreadsheet> allSheets;

    private boolean shouldShowIncorrectOrderGUI;
    private ISAcreator main;

    public OutputISAFilesFromGUI(ISAcreator main) {
        this.main = main;
    }


    public void saveISAFiles(boolean removeEmptyColumns, Investigation investigation) {

        try {
            // keep a list of saved files for reference!
            errorSheets = new ArrayList<Spreadsheet>();
            allSheets = new ArrayList<Spreadsheet>();
            shouldShowIncorrectOrderGUI = false;

            IncorrectColumnOrderGUI icGUI = main.getIncorrectGUI();

            if (icGUI != null && icGUI.isShowing()) {
                icGUI.dispose();
            }

            File file = new File(investigation.getReference());

            PrintStream investigationFilePrintStream = new PrintStream(file);

            // print section defining the Ontologies Used
            investigationFilePrintStream.println(getOntologiesUsedOutput());
            // print the Investigation section.
            investigationFilePrintStream.println(ApplicationManager.getUserInterfaceForISASection(investigation).toString());

            File fileToSave;

            for (Study study : investigation.getStudies().values()) {
                investigationFilePrintStream.println(ApplicationManager.getUserInterfaceForISASection(study).toString());

                fileToSave = new File(file.getParentFile().getPath() +
                        File.separator + study.getStudySampleFileIdentifier());

                Spreadsheet tmpSheet = ((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(study.getStudySample())).getSpreadsheet();
                if (!tmpSheet.getSpreadsheetFunctions().exportTable(fileToSave, "\t", removeEmptyColumns)) {
                    errorSheets.add(tmpSheet);
                    shouldShowIncorrectOrderGUI = true;
                }
                allSheets.add(tmpSheet);

                Assay assay;
                for (String assayRef : study.getAssays().keySet()) {
                    assay = study.getAssays().get(assayRef);

                    fileToSave = new File(file.getParentFile().getPath() +
                            File.separator + assay.getAssayReference());

                    tmpSheet = ((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(assay)).getSpreadsheet();
                    if (!tmpSheet.getSpreadsheetFunctions().exportTable(fileToSave, "\t", removeEmptyColumns)) {
                        errorSheets.add(tmpSheet);
                        shouldShowIncorrectOrderGUI = true;
                    }

                    allSheets.add(tmpSheet);
                }
            }
        } catch (FileNotFoundException e) {
            log.error("File not found exception caught");
        }
    }


    public List<Spreadsheet> getAllSheets() {
        return allSheets;
    }

    public List<Spreadsheet> getErrorSheets() {
        return errorSheets;
    }

    public boolean isShouldShowIncorrectOrderGUI() {
        return shouldShowIncorrectOrderGUI;
    }
}
