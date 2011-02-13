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

package org.isatools.isacreator.io;

import org.apache.log4j.Logger;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.spreadsheet.IncorrectColumnOrderGUI;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eamonn Maguire
 * @date Jul 10, 2009
 */


public class OutputISAFiles {
    private static final Logger log = Logger.getLogger(OutputISAFiles.class.getName());

    private List<Spreadsheet> errorSheets;
    private List<Spreadsheet> allSheets;

    private boolean shouldShowIncorrectOrderGUI;
    private ISAcreator main;

    public OutputISAFiles(ISAcreator main) {
        this.main = main;
    }


    public void saveISAFiles(boolean removeEmptyColumns, Investigation inv) {

        try {
            // keep a list of saved files for reference!
            errorSheets = new ArrayList<Spreadsheet>();
            allSheets = new ArrayList<Spreadsheet>();
            shouldShowIncorrectOrderGUI = false;

            IncorrectColumnOrderGUI icGUI = main.getIncorrectGUI();

            if (icGUI != null && icGUI.isShowing()) {
                icGUI.dispose();
            }

            File file = new File(inv.getReference());

            PrintStream ps = new PrintStream(file);

            // print section defining the Ontologies Used
            ps.println(getOntologiesUsedOutput());
            // print the Investigation section.
            ps.println(inv.getUserInterface().toString());

            File fileToSave;

            for (Study s : inv.getStudies().values()) {
                ps.println(s.getUserInterface().toString());

                fileToSave = new File(file.getParentFile().getPath() +
                        File.separator + s.getStudySampleFileIdentifier());

                Spreadsheet tmpSheet = s.getStudySample().getSpreadsheetUI().getTable();
                if (!tmpSheet.getSpreadsheetFunctions().exportTable(fileToSave, "\t", removeEmptyColumns)) {
                    errorSheets.add(tmpSheet);
                    shouldShowIncorrectOrderGUI = true;
                }
                allSheets.add(tmpSheet);

                Assay a;
                for (String assayRef : s.getAssays().keySet()) {
                    a = s.getAssays().get(assayRef);

                    fileToSave = new File(file.getParentFile().getPath() +
                            File.separator + a.getAssayReference());

                    tmpSheet = a.getSpreadsheetUI().getTable();
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

    public String getOntologiesUsedOutput() {
        String[] headerTerms = new String[]{
                "Term Source Name", "Term Source File", "Term Source Version",
                "Term Source Description"
        };
        String toReturn = "ONTOLOGY SOURCE REFERENCE\n";

        for (int i = 0; i < headerTerms.length; i++) {
            StringBuffer line = new StringBuffer(headerTerms[i] + "\t");
            String val;
            for (OntologySourceRefObject anOntologiesUsed : main.getOntologiesUsed()) {

                if (headerTerms[i].equals("Term Source Name")) {
                    val = anOntologiesUsed.getSourceName();
                    addToLine(line, val);
                } else if (headerTerms[i].equals("Term Source File")) {
                    val = anOntologiesUsed.getSourceFile();
                    addToLine(line, val);
                } else if (headerTerms[i].equals("Term Source Version")) {
                    val = anOntologiesUsed.getSourceVersion();
                    addToLine(line, val);
                } else if (headerTerms[i].equals("Term Source Description")) {
                    val = anOntologiesUsed.getSourceDescription();
                    addToLine(line, val);
                }
            }

            // add new line to everything line but the last line
            if (i != (headerTerms.length - 1)) {
                line.append("\n");
            }

            toReturn += line;
        }

        return toReturn;
    }

    private void addToLine(StringBuffer line, String toAdd) {
        if (toAdd == null) {
            toAdd = "";
        }

        if (!toAdd.equals("")) {
            toAdd = toAdd.trim();

            line.append("\"").append(toAdd).append("\"\t");
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
