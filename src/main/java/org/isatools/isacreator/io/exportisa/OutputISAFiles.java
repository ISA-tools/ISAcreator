package org.isatools.isacreator.io.exportisa;

import org.isatools.isacreator.api.utils.SpreadsheetUtils;
import org.isatools.isacreator.io.exportisa.exportadaptors.ISASectionExportAdaptor;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;

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
 *         Time: 14:13
 */
public class OutputISAFiles extends ISAFileOutput {

    @Override
    public void saveISAFiles(boolean removeEmptyColumns, Investigation investigation) {
        File file = new File(investigation.getReference());

        generateMissingSections(investigation);

        PrintStream investigationFilePrintStream;
        try {
            investigationFilePrintStream = new PrintStream(file);

            // print section defining the Ontologies Used
            investigationFilePrintStream.println(getOntologiesUsedOutput());
            // print the Investigation section.
            investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(investigation, InvestigationFileSection.INVESTIGATION_SECTION));
            investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(investigation.getPublications(), InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION));
            investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(investigation.getContacts(), InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION));

            File fileToSave;

            for (Study study : investigation.getStudies().values()) {
                investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study, InvestigationFileSection.STUDY_SECTION));
                investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getStudyDesigns(), InvestigationFileSection.STUDY_DESIGN_SECTION));
                investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getFactors(), InvestigationFileSection.STUDY_FACTORS));
                investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(getAssaysAsList(study), InvestigationFileSection.STUDY_ASSAYS));
                investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getPublications(), InvestigationFileSection.STUDY_PUBLICATIONS));
                investigationFilePrintStream.print(ISASectionExportAdaptor.exportISASectionAsString(study.getContacts(), InvestigationFileSection.STUDY_CONTACTS));

                fileToSave = new File(file.getParentFile().getPath() +
                        File.separator + study.getStudySampleFileIdentifier());

                // output study sample content
                StringBuilder studySampleContent = SpreadsheetUtils.outputAssayAsString(study.getStudySample());
                outputFile(fileToSave, studySampleContent);

                for (String assayRef : study.getAssays().keySet()) {
                    Assay assay = study.getAssays().get(assayRef);

                    fileToSave = new File(file.getParentFile().getPath() +
                            File.separator + assay.getAssayReference());

                    // output study sample content
                    StringBuilder assayContent = SpreadsheetUtils.outputAssayAsString(assay);
                    outputFile(fileToSave, assayContent);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Will generate any content which isn't already there.
     *
     * @param investigation - Investigation to generate content for.
     */
    private void generateMissingSections(Investigation investigation) {
        // generate investigation sections if they don't exist.
        if (investigation.getPublications().size() == 0) {
            investigation.addPublication(new InvestigationPublication());
        }
        if (investigation.getContacts().size() == 0) {
            investigation.addContact(new InvestigationContact());
        }

        // for each study generate study sections if they don't exist.
        for (Study study : investigation.getStudies().values()) {
            if (study.getStudyDesigns().size() == 0) {
                study.getStudyDesigns().add(new StudyDesign());
            }
            if (study.getAssays().size() == 0) {
                study.addAssay(new Assay());
            }
            if (study.getFactors().size() == 0) {
                study.addFactor(new Factor());
            }
            if (study.getProtocols().size() == 0) {
                study.addProtocol(new Protocol());
            }
            if (study.getPublications().size() == 0) {
                study.addPublication(new StudyPublication());
            }
            if (study.getContacts().size() == 0) {
                study.addContact(new StudyContact());
            }
        }
    }

    public List<Assay> getAssaysAsList(Study study) {
        List<Assay> assays = new ArrayList<Assay>();
        assays.addAll(study.getAssays().values());
        return assays;
    }

    private void outputFile(File fileToSave, StringBuilder content) throws FileNotFoundException {
        PrintStream printStream = new PrintStream(fileToSave);
        printStream.print(content);
        printStream.close();
    }
}
