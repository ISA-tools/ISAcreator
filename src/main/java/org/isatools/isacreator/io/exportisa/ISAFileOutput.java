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

package org.isatools.isacreator.io.exportisa;

import org.apache.commons.lang.StringUtils;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.io.exportisa.exportadaptors.ISASectionExportAdaptor;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 01/07/2012
 *         Time: 14:06
 */
public abstract class ISAFileOutput {

    public abstract void saveISAFiles(boolean removeEmptyColumns, Investigation investigation);

    public String getOntologiesUsedOutput() {
        String[] headerTerms = new String[]{
                "Term Source Name", "Term Source File", "Term Source Version",
                "Term Source Description"
        };
        String toReturn = "ONTOLOGY SOURCE REFERENCE\n";

        Set<OntologySourceRefObject> ontologiesUsed = OntologyManager.getOntologiesUsed();

        for (int i = 0; i < headerTerms.length; i++) {
            StringBuffer line = new StringBuffer(headerTerms[i] + "\t");
            String val = "";
            for (OntologySourceRefObject anOntologyUsed : ontologiesUsed) {

                if (headerTerms[i].equals("Term Source Name")) {
                    val = anOntologyUsed.getSourceName();
                } else if (headerTerms[i].equals("Term Source File")) {
                    val = anOntologyUsed.getSourceFile();
                } else if (headerTerms[i].equals("Term Source Version")) {
                    val = anOntologyUsed.getSourceVersion();
                } else if (headerTerms[i].equals("Term Source Description")) {
                    val = anOntologyUsed.getSourceDescription();
                }

                addToLine(line, StringUtils.isEmpty(val) ? " " : val);
            }

            // add new line to everything line but the last line
            if (i != (headerTerms.length - 1)) {
                line.append("\n");
            }

            toReturn += line;
        }

        return toReturn;
    }

    protected void printStudy(PrintStream investigationFilePrintStream, Study study) {

        DataEntryForm def = ApplicationManager.getUserInterfaceForISASection(study);
        if (def!=null)
            def.update();

        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study, InvestigationFileSection.STUDY_SECTION));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getStudyDesigns(), InvestigationFileSection.STUDY_DESIGN_SECTION));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getPublications(), InvestigationFileSection.STUDY_PUBLICATIONS));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getFactors(), InvestigationFileSection.STUDY_FACTORS));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(getAssaysAsList(study), InvestigationFileSection.STUDY_ASSAYS));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getProtocols(), InvestigationFileSection.STUDY_PROTOCOLS));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getContacts(), InvestigationFileSection.STUDY_CONTACTS));
    }

    protected void printInvestigation(Investigation investigation, PrintStream investigationFilePrintStream) {

        DataEntryForm def = ApplicationManager.getUserInterfaceForISASection(investigation);
        if (def!=null){
            def.update();
        }

        // print section defining the Ontologies Used
        investigationFilePrintStream.println(getOntologiesUsedOutput());

        // print the Investigation section.
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(investigation, InvestigationFileSection.INVESTIGATION_SECTION));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(investigation.getPublications(), InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION));
        investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(investigation.getContacts(), InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION));
    }



    protected List<Assay> getAssaysAsList(Study study) {
        List<Assay> assays = new ArrayList<Assay>();
        assays.addAll(study.getAssays().values());
        return assays;
    }

    protected void outputFile(File fileToSave, StringBuilder content) throws FileNotFoundException {
        PrintStream printStream = new PrintStream(fileToSave);
        printStream.print(content);
        printStream.close();
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
}
