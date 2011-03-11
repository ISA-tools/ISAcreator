package org.isatools.isacreator.io.importisa;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.collections15.OrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.io.exceptions.MalformedInvestigationException;
import org.isatools.isacreator.io.importisa.InvestigationFileProperties.InvestigationFileSection;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 09/03/2011
 *         Time: 14:27
 */
public class ISAtabImporter {
    private static final Logger log = Logger.getLogger(ISAtabImporter.class.getName());

    private Investigation investigation;

    private DataEntryEnvironment dataEntryEnvironment;
    private Set<String> messages;

    private String parentDirectoryPath;
    private Boolean constructWithGUIs;


    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param configDir - the directory containing the configuration files you wish to use.
     */
    public ISAtabImporter(String configDir) {
        this(new ISAcreator(configDir), false);
    }

    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param applicationContainer - a reference to the Main entry point of the Application
     */
    public ISAtabImporter(ISAcreator applicationContainer) {
        this(applicationContainer, true);
    }

    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param applicationContainer - a reference to the Main entry point of the Application
     * @param constructWithGUIs    - whether or not to construct the Study/Assay Objects with User Interfaces. There would be no point creating GUIs for those accessing features through the API for example.
     */
    public ISAtabImporter(ISAcreator applicationContainer, Boolean constructWithGUIs) {
        this.constructWithGUIs = constructWithGUIs;
        messages = new HashSet<String>();

        this.dataEntryEnvironment = new DataEntryEnvironment(applicationContainer);
        applicationContainer.setCurDataEntryPanel(dataEntryEnvironment);
    }

    public Investigation getInvestigation() {
        return investigation;
    }


    /**
     * Import an ISATAB file set!
     *
     * @param parentDir - Directory containing the ISATAB files. Should include a file of type
     * @return boolean if successful or not!
     */
    public boolean importFile(String parentDir) {
        File investigationFile = new File(parentDir);

        this.parentDirectoryPath = parentDir;
        System.out.println("Parent directory is -> " + parentDir);

        boolean investigationFileFound = false;

        if (investigationFile.exists()) {
            File[] isaDirectorFiles = investigationFile.listFiles();

            for (File isaFile : isaDirectorFiles) {
                if (isaFile.getName().toLowerCase().startsWith("i_")) {
                    investigationFileFound = true;
                    investigationFile = isaFile;
                    break;
                }
            }

            if (!investigationFileFound) {
                messages.add("Investigation file does not exist in this folder. Please create an investigation file and name it " +
                        "\"i_<investigation identifier>.txt\"");
                return false;
            }

            try {
                InvestigationImport investigationFileImporter = new InvestigationImport();
                Pair<Boolean, OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>>> investigationFileImport = investigationFileImporter.importInvestigationFile(investigationFile);

                if (investigationFileImport.fst) {
                    log.info("Import of Investigation in " + investigationFile.getPath() + " was successful...");
                    log.info("Proceeding to map to Investigation...");

                    StructureToInvestigationMapper mapper = new StructureToInvestigationMapper();
                    investigation = mapper.createInvestigationFromDataStructure(investigationFileImport.snd);
                    investigation.setReference(investigationFile.getPath());

                    processInvestigation();
                    if (constructWithGUIs) {
                        attachGUIsToInvestigation();
                        dataEntryEnvironment.createGUIFromSource(investigation);
                    }
                } else {
                    messages.addAll(investigationFileImporter.getMessages());
                    return false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
        }

        return true;
    }

    private void processInvestigation() {
        // todo take a look and this back reference. Should be a way to provide only one reference.


        SpreadsheetImport spreadsheetImporter = new SpreadsheetImport();

        for (String studyIdentifier : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyIdentifier);

            // here we process the study sample file
            TableReferenceObject studySampleReference = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(
                    MappingObject.STUDY_SAMPLE);

            if (studySampleReference != null) {

                try {
                    TableReferenceObject builtReference = spreadsheetImporter.loadInTables(parentDirectoryPath +
                            study.getStudySampleFileIdentifier(), studySampleReference);

                    if (builtReference != null) {
                        study.setStudySamples(new Assay(study.getStudySampleFileIdentifier(), builtReference));
                    }
                } catch (MalformedInvestigationException mie) {
                    messages.add(mie.getMessage());
                } catch (Exception e) {
                    messages.add(e.getMessage());
                    e.printStackTrace();
                }
            }

            // here we process the assay files
            for (Assay assay : study.getAssays().values()) {
                TableReferenceObject assayReference = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(assay.getMeasurementEndpoint(),
                        assay.getTechnologyType());

                if (assayReference != null && !(assay.getAssayReference() == null && assay.getAssayReference().equals(""))) {
                    try {
                        TableReferenceObject builtReference = spreadsheetImporter.loadInTables(parentDirectoryPath +
                                assay.getAssayReference(), assayReference);

                        if (builtReference != null) {
                            assay.setTableReferenceObject(builtReference);
                        } else {
                            messages.add("Assay with measurement " + assay.getMeasurementEndpoint() + " & technology " + assay.getTechnologyType() +
                                    " is not recognised. Please ensure you are using the correct configuration!");
                        }
                    } catch (IOException e) {
                        messages.add(e.getMessage());
                    } catch (MalformedInvestigationException e) {
                        messages.add(e.getMessage());
                    }
                }
            }
        }
    }

    private void attachGUIsToInvestigation() {
        investigation.setUserInterface(new InvestigationDataEntry(investigation, dataEntryEnvironment));

        for (String studyIdentifier : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyIdentifier);

            study.setUI(new StudyDataEntry(dataEntryEnvironment, study));

            study.getStudySample().setUserInterface(study.getUserInterface());

            for (String assay : study.getAssays().keySet()) {
                study.getAssays().get(assay).setUserInterface(study.getUserInterface());
            }
        }
    }


}
