package org.isatools.isacreator.io.importisa;

import org.apache.commons.collections15.OrderedMap;
import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.FileType;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import uk.ac.ebi.utils.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created Created by the ISA team
 *
 *
 * Abstract class for importing ISATab files
 *
 * Date: 11/07/2012
 * Time: 16:08
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public abstract class ISAtabImporter {


    private static final Logger log = Logger.getLogger(ISAtabImporter.class);

    protected Investigation investigation;
    protected List<ISAFileErrorReport> errors;
    private  List<ErrorMessage> messages;
    protected StructureToInvestigationMapper mapper;


    public ISAtabImporter(){
        errors = new ArrayList<ISAFileErrorReport>();
        messages = new ArrayList<ErrorMessage>();
    }

    /**
     * Imports ISATab files
     *
     * @param parentDir
     * @return indicates if import was successful or not
     */
    public abstract boolean importFile(String parentDir);

    /**
     * Retrieves errors of the import process
     *
     * @return list of ISAFileErrorReports
     */
    public List<ISAFileErrorReport> getMessages() {
        return errors;
    }

    /**
     * Retrieves the investigation object
     *
     * @return investigation
     */
    public Investigation getInvestigation() {
        return investigation;
    }

    protected boolean commonImportFile(String parentDir){


            File investigationFile = new File(parentDir);

            //parentDirectoryPath should not be a class fields as before, as it is relevant for the method importFile only
            String parentDirectoryPath = parentDir;

            if (!investigationFile.isDirectory()) {
                investigationFile = investigationFile.getParentFile();
                parentDirectoryPath = investigationFile.getAbsolutePath();
            }

            log.info("Parent directory is -> " + parentDir);

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
                    messages.add(new ErrorMessage(ErrorLevel.ERROR, "Investigation file does not exist in this folder. Please create an investigation file and name it " +
                            "\"i_<investigation identifier>.txt\""));

                    ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), FileType.INVESTIGATION, messages);
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

                        mapper = new StructureToInvestigationMapper();

                        Pair<Boolean, Investigation> mappingResult = mapper.createInvestigationFromDataStructure(investigationFileImport.snd);

                        messages.addAll(mapper.getMessages());

                        if (!mappingResult.fst) {
                            ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), FileType.INVESTIGATION, messages);
                            errors.add(investigationErrorReport);
                            return false;
                        }


                        investigation = mappingResult.snd;
                        investigation.setFileReference(investigationFile.getPath());

                        if (investigation.getReferenceObject() != null) {
                            TableReferenceObject tro = ConfigurationManager.selectTROForUserSelection(MappingObject.INVESTIGATION);

                            DataEntryReferenceObject referenceObject = investigation.getReferenceObject();

                            referenceObject.setFieldDefinition(tro.getTableFields().getFields());

                            for (Study study : investigation.getStudies().values()) {
                                study.getReferenceObject().setFieldDefinition(tro.getTableFields().getFields());
                            }
                        }

                        if (!processInvestigation(parentDirectoryPath)) {
                            ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), FileType.INVESTIGATION, messages);
                            errors.add(investigationErrorReport);

                            return false;
                        }


                        String lastConfigurationUsed = ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION);

                        if (lastConfigurationUsed.contains(File.separator)) {
                            lastConfigurationUsed = lastConfigurationUsed.substring(lastConfigurationUsed.lastIndexOf(File.separator) + 1);
                        }

                        if (!investigation.getLastConfigurationUsed().equals("") && !lastConfigurationUsed.equals("")) {
                            if (!lastConfigurationUsed.equals(investigation.getLastConfigurationUsed())) {
                                messages.add(new ErrorMessage(ErrorLevel.WARNING, "The last configuration used to load this ISAtab file was " + investigation.getLastConfigurationUsed() + ". The currently loaded configuration is " + lastConfigurationUsed + ". You can continue to load, but " +
                                        "the settings from " + investigation.getLastConfigurationUsed() + " may be important."));

                                ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), FileType.INVESTIGATION, messages);
                                errors.add(investigationErrorReport);
                            }
                        } else {
                            //if (isacreator!=null){
                            //    investigation.setLastConfigurationUsed(isacreator.getLoadedConfiguration());
                            //}
                        }

                    } else {
                        messages.addAll(investigationFileImporter.getMessages());

                        ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), FileType.INVESTIGATION, messages);
                        errors.add(investigationErrorReport);

                        return false;
                    }
                } catch (IOException e) {

                    messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));

                    ISAFileErrorReport investigationErrorReport = new ISAFileErrorReport(investigationFile.getName(), FileType.INVESTIGATION, messages);
                    errors.add(investigationErrorReport);

                    return false;

                }
            }

            return true;


    }


    protected boolean processInvestigation(String parentDirectoryPath) {

        SpreadsheetImport spreadsheetImporter = new SpreadsheetImport();

        boolean errorsFound = false;

        for (String studyIdentifier : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyIdentifier);


            System.out.println("Processing " + studyIdentifier);

            // here we process the study sample file
            TableReferenceObject studySampleReference = ConfigurationManager.selectTROForUserSelection(
                    MappingObject.STUDY_SAMPLE);

            if (studySampleReference != null) {

                List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

                try {
                    TableReferenceObject builtReference = spreadsheetImporter.loadInTables(parentDirectoryPath + File.separator +
                            study.getStudySampleFileIdentifier(), studySampleReference);

                    if (builtReference != null) {
                        study.setStudySamples(new Assay(study.getStudySampleFileIdentifier(), builtReference));
                    }
                } catch (MalformedInvestigationException mie) {
                    messages.add(new ErrorMessage(ErrorLevel.ERROR, mie.getMessage()));

                } catch (Exception e) {

                    messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));

                } finally {
                    if (messages.size() > 0) {
                        ISAFileErrorReport studySampleReport = new ISAFileErrorReport(study.getStudySampleFileIdentifier(), FileType.STUDY_SAMPLE, messages);
                        errors.add(studySampleReport);
                        errorsFound = true;
                    }
                }
            }

            // here we process the assay files

            List<Assay> noReferenceobjectFound = new ArrayList<Assay>();

            for (String assayReference : study.getAssays().keySet()) {

                List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

                Assay assay = study.getAssays().get(assayReference);

                TableReferenceObject assayTableReferenceObject = ConfigurationManager.selectTROForUserSelection(
                        assay.getMeasurementEndpoint(), assay.getTechnologyType());

                if (assayTableReferenceObject != null) {
                    try {

                        TableReferenceObject builtReference = spreadsheetImporter.loadInTables(parentDirectoryPath + File.separator +
                                assay.getAssayReference(), assayTableReferenceObject);
                        if (builtReference != null) {
                            assay.setTableReferenceObject(builtReference);
                        }
                    } catch (IOException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));

                    } catch (MalformedInvestigationException e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    } catch (Exception e) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, e.getMessage()));
                    } finally {
                        if (messages.size() > 0) {
                            ISAFileErrorReport studySampleReport = new ISAFileErrorReport(assay.getAssayReference(), inferISAFileType(assay), messages);
                            errors.add(studySampleReport);
                            errorsFound = true;
                        }
                    }
                } else {
                    messages.add(new ErrorMessage(ErrorLevel.WARNING, "Assay with measurement " + assay.getMeasurementEndpoint() + " & technology " + assay.getTechnologyType() +
                            " is not recognised. Please ensure you are using the correct configuration!"));
                    log.info("Assay with measurement " + assay.getMeasurementEndpoint() + " & technology " + assay.getTechnologyType() +
                            " is not recognised. Please ensure you are using the correct configuration!");
                    noReferenceobjectFound.add(assay);

                    ISAFileErrorReport studySampleReport = new ISAFileErrorReport(assay.getAssayReference(),
                            assay.getTechnologyType(), assay.getMeasurementEndpoint(), inferISAFileType(assay), messages);

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

    protected FileType inferISAFileType(Assay assay) {

        String assayDescription = assay.getTechnologyType().toLowerCase() + " " + assay.getMeasurementEndpoint().toLowerCase();

        if (assayDescription.contains(FileType.MICROARRAY.getType())) {
            return FileType.MICROARRAY;
        } else if (assayDescription.contains(FileType.MASS_SPECTROMETRY.getType())) {
            return FileType.MASS_SPECTROMETRY;
        } else if (assayDescription.contains(FileType.NMR.getType())) {
            return FileType.NMR;
        } else if (assayDescription.contains(FileType.FLOW_CYTOMETRY.getType())) {
            return FileType.FLOW_CYTOMETRY;
        } else if (assayDescription.contains(FileType.GEL_ELECTROPHORESIS.getType())) {
            return FileType.GEL_ELECTROPHORESIS;
        } else if (assayDescription.contains(FileType.CLINICAL_CHEMISTRY.getType())) {
            return FileType.CLINICAL_CHEMISTRY;
        } else if (assayDescription.contains(FileType.HEMATOLOGY.getType())) {
            return FileType.HEMATOLOGY;
        } else if (assayDescription.contains(FileType.HISTOLOGY.getType())) {
            return FileType.HISTOLOGY;
        } else {
            return FileType.STUDY_SAMPLE;
        }
    }

}


