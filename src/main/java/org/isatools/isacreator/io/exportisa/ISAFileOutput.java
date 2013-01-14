package org.isatools.isacreator.io.exportisa;

import org.apache.axis.utils.StringUtils;
import org.isatools.isacreator.api.utils.SpreadsheetUtils;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.io.exportisa.exportadaptors.ISASectionExportAdaptor;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

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

        for (int i = 0; i < headerTerms.length; i++) {
            StringBuffer line = new StringBuffer(headerTerms[i] + "\t");
            String val = "";
            for (OntologySourceRefObject anOntologiesUsed : OntologyManager.getOntologiesUsed()) {

                if (headerTerms[i].equals("Term Source Name")) {
                    val = anOntologiesUsed.getSourceName();
                } else if (headerTerms[i].equals("Term Source File")) {
                    val = anOntologiesUsed.getSourceFile();
                } else if (headerTerms[i].equals("Term Source Version")) {
                    val = anOntologiesUsed.getSourceVersion();
                } else if (headerTerms[i].equals("Term Source Description")) {
                    val = anOntologiesUsed.getSourceDescription();
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

    /**
     * Will generate any content which isn't already there.
     *
     * @param investigation - Investigation to generate content for.
     */
    protected void generateMissingSections(Investigation investigation) {
        // generate investigation sections if they don't exist.
        if (investigation.getPublications().size() == 0) {
            investigation.addPublication(new InvestigationPublication());
        }
        if (investigation.getContacts().size() == 0) {
            investigation.addContact(new InvestigationContact());
        }

        // for each study generate study sections if they don't exist.
        for (Study study : investigation.getStudies().values()) {
            System.out.println("Checking study... " + study.getStudyId());
            if (study.getStudyDesigns().size() == 0) {
                study.getStudyDesigns().add(new StudyDesign());
            }
            if (study.getAssays().size() == 0) {
                study.getAssays().put("", new Assay());
            }
            if (study.getFactors().size() == 0) {
                study.getFactors().add(new Factor());
            }
            if (study.getProtocols().size() == 0) {
                study.getProtocols().add(new Protocol());
            }
            if (study.getPublications().size() == 0) {
                System.out.println("adding dumb publication");
                study.getPublications().add(new StudyPublication());
            }
            if (study.getContacts().size() == 0) {
                study.getContacts().add(new StudyContact());
            }
        }
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

        generateMissingSections(investigation);
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
