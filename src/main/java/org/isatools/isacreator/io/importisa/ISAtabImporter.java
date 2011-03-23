package org.isatools.isacreator.io.importisa;

import com.sun.tools.javac.util.Pair;
import org.apache.commons.collections15.OrderedMap;
import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.errorreporter.model.ISAFileType;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException;
import org.isatools.isacreator.io.importisa.investigationfileproperties.InvestigationFileSection;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private List<ISAFileErrorReport> errors;

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
        errors = new ArrayList<ISAFileErrorReport>();

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
        log.info("Parent directory is -> " + parentDir);

        boolean investigationFileFound = false;

        Set<String> messages = new HashSet<String>();

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

                ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), ISAFileType.INVESTIGATION, messages);
                errors.add(investigationErrorReport);
                return false;
            }

            try {

                InvestigationImport investigationFileImporter = new InvestigationImport();
                Pair<Boolean, OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>>> investigationFileImport = investigationFileImporter.importInvestigationFile(investigationFile);

                messages.addAll(investigationFileImporter.getMessages());

                if (investigationFileImport.fst) {
                    log.info("Import of Investigation in " + investigationFile.getPath() + " was successful...");
                    log.info("Proceeding to map to Investigation...");

                    StructureToInvestigationMapper mapper = new StructureToInvestigationMapper();

                    Pair<Boolean, Investigation> mappingResult = mapper.createInvestigationFromDataStructure(investigationFileImport.snd);

                    messages.addAll(mapper.getMessages());

                    if (!mappingResult.fst) {
                        ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), ISAFileType.INVESTIGATION, messages);
                        errors.add(investigationErrorReport);
                        return false;
                    }


                    investigation = mappingResult.snd;
                    investigation.setFileReference(investigationFile.getPath());

                    if (investigation.getReferenceObject() != null) {
                        TableReferenceObject tro = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(MappingObject.INVESTIGATION);

                        DataEntryReferenceObject referenceObject = investigation.getReferenceObject();

                        referenceObject.setFieldDefinition(tro.getTableFields().getFields());

                        for (Study study : investigation.getStudies().values()) {
                            study.getReferenceObject().setFieldDefinition(tro.getTableFields().getFields());
                        }
                    }

                    if (!processInvestigation()) {
                        ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), ISAFileType.INVESTIGATION, messages);
                        errors.add(investigationErrorReport);

                        return false;
                    }

                    if (constructWithGUIs) {
                        attachGUIsToInvestigation();
                        dataEntryEnvironment.createGUIFromSource(investigation);
                        assignOntologiesToSession(mapper.getOntologyTermsDefined(), investigation.getOntologiesUsed());
                    }
                } else {
                    messages.addAll(investigationFileImporter.getMessages());

                    ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), ISAFileType.INVESTIGATION, messages);
                    errors.add(investigationErrorReport);

                    return false;
                }
            } catch (IOException e) {

                messages.add(e.getMessage());

                ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), ISAFileType.INVESTIGATION, messages);
                errors.add(investigationErrorReport);

                return false;

            }
        }

        return true;
    }

    private boolean processInvestigation() {

        SpreadsheetImport spreadsheetImporter = new SpreadsheetImport();

        boolean errorsFound = false;

        for (String studyIdentifier : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyIdentifier);


            System.out.println("Processing " + studyIdentifier);

            // here we process the study sample file
            TableReferenceObject studySampleReference = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(
                    MappingObject.STUDY_SAMPLE);

            if (studySampleReference != null) {

                Set<String> messages = new HashSet<String>();

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

                } finally {
                    if (messages.size() > 0) {
                        ISAFileErrorReport studySampleReport = new ISAFileErrorReport(study.getStudySampleFileIdentifier(), ISAFileType.STUDY_SAMPLE, messages);
                        errors.add(studySampleReport);
                        errorsFound = true;
                    }
                }
            }

            // here we process the assay files

            List<Assay> noReferenceobjectFound = new ArrayList<Assay>();

            for (String assayReference : study.getAssays().keySet()) {

                Set<String> messages = new HashSet<String>();

                Assay assay = study.getAssays().get(assayReference);

                TableReferenceObject assayTableReferenceObject = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(assay.getMeasurementEndpoint(),
                        assay.getTechnologyType());

                if (assayTableReferenceObject != null) {
                    try {

                        TableReferenceObject builtReference = spreadsheetImporter.loadInTables(parentDirectoryPath +
                                assay.getAssayReference(), assayTableReferenceObject);

                        if (builtReference != null) {
                            assay.setTableReferenceObject(builtReference);
                        }
                    } catch (IOException e) {
                        messages.add(e.getMessage());
                    } catch (MalformedInvestigationException e) {
                        messages.add(e.getMessage());
                    } catch (Exception e) {
                        messages.add(e.getMessage());
                    } finally {
                        if (messages.size() > 0) {
                            ISAFileErrorReport studySampleReport = new ISAFileErrorReport(assay.getAssayReference(), inferISAFileType(assay), messages);
                            errors.add(studySampleReport);
                            errorsFound = true;
                        }
                    }
                } else {
                    messages.add("Assay with measurement " + assay.getMeasurementEndpoint() + " & technology " + assay.getTechnologyType() +
                            " is not recognised. Please ensure you are using the correct configuration!");
                    log.info("Assay with measurement " + assay.getMeasurementEndpoint() + " & technology " + assay.getTechnologyType() +
                            " is not recognised. Please ensure you are using the correct configuration!");
                    noReferenceobjectFound.add(assay);

                    ISAFileErrorReport studySampleReport = new ISAFileErrorReport(assay.getAssayReference(), inferISAFileType(assay), messages);
                    errors.add(studySampleReport);
                    errorsFound = false;

                }


            }

            for (Assay toRemove : noReferenceobjectFound) {
                log.info("Assay " + toRemove.getAssayReference() + " will not be loaded into ISAcreator because there is no configuration to define it...");
                study.removeAssay(toRemove.getAssayReference());
            }
        }

        return !errorsFound;
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

    private void assignOntologiesToSession(List<OntologyObject> ontologiesUsed, List<OntologySourceRefObject> ontologySourcesAdded) {

        for (OntologyObject oo : ontologiesUsed) {
            if (!oo.getTerm().trim().equals("")) {
                dataEntryEnvironment.getParentFrame().addToUserHistory(oo);
            }
        }

    }

    public List<ISAFileErrorReport> getMessages() {
        return errors;
    }

    private ISAFileType inferISAFileType(Assay assay) {


        String technology = assay.getTechnologyType().toLowerCase();
        if (technology.contains("microarray")) {
            return ISAFileType.MICROARRAY;
        } else if (technology.contains("spectrometry")) {
            return ISAFileType.MASS_SPECTROMETRY;
        } else if (technology.contains("nmr")) {
            return ISAFileType.NMR;
        } else if (technology.contains("flow")) {
            return ISAFileType.FLOW_CYT;
        } else if (technology.contains("electrophoresis")) {
            return ISAFileType.GEL_ELECTROPHORESIS;
        } else if (technology.contains("sequencing")) {
            return ISAFileType.SEQUENCING;
        } else {
            return ISAFileType.STUDY_SAMPLE;
        }
    }
}
