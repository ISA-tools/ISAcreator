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

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.TableFieldObject;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isacreator.utils.GeneralUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ImportISAFiles class used to import the ISATAB files into the ISAcreator program.
 * Can also be used as a way of validating the document, since if it doesn't parse, it's not valid!
 *
 * @author Eamonn Maguire
 */
public class ImportISAFiles {
    private static final Logger log = Logger.getLogger(ImportISAFiles.class.getName());

    private static final int READING_FACTORS = 0;
    private static final int READING_ASSAYS = 1;
    private static final int READING_PROTOCOLS = 2;
    private static final int READING_STUDY_CONTACTS = 3;
    private static final int READING_INVESTIGATION_CONTACTS = 4;
    private static final int READING_STUDY_PUBLICATIONS = 5;
    private static final int READING_INVESTIGATION_PUBLICATIONS = 6;
    private static final int READING_DESIGNS = 7;

    private List<Assay> assaysToAdd;
    private List<OntologySourceRefObject> ontologySourcesDefined;
    private List<OntologyObject> ontologyTermsDefined;
    private Assay studySample;
    private DataEntryEnvironment dataEntryEnvironment;
    private Investigation investigation;
    private String parentDir = null;

    // problem log contains errors found when reading document, validation errors etc. to be fed back to the user so that they can correct the document
    private StringBuilder problemLog;
    private Study currentStudy;
    private Map<String, ISAPropertiesSection> allowedSections;
    private Boolean constructWithGUIs;

    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param configDir - the directory containing the configuration files you wish to use.
     */
    public ImportISAFiles(String configDir) {
        this(new ISAcreator(configDir), false);
    }

    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param applicationContainer - a reference to the Main entry point of the Application
     */
    public ImportISAFiles(ISAcreator applicationContainer) {
        this(applicationContainer, true);
    }

    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param applicationContainer - a reference to the Main entry point of the Application
     * @param constructWithGUIs    - whether or not to construct the Study/Assay Objects with User Interfaces. There would be no point creating GUIs for those accessing features through the API for example.
     */
    public ImportISAFiles(ISAcreator applicationContainer, Boolean constructWithGUIs) {
        this.constructWithGUIs = constructWithGUIs;
        this.dataEntryEnvironment = new DataEntryEnvironment(applicationContainer);
        applicationContainer.setCurDataEntryPanel(dataEntryEnvironment);
        assaysToAdd = new ArrayList<Assay>();
        ontologySourcesDefined = new ArrayList<OntologySourceRefObject>();
        ontologyTermsDefined = new ArrayList<OntologyObject>();
        problemLog = new StringBuilder();
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
        File f = new File(parentDir);

        System.out.println("Parent directory is -> " + parentDir);
        boolean found = false;

        if (f.exists()) {
            File[] dirContents = f.listFiles();

            for (File dirFile : dirContents) {
                if (dirFile.getName().toLowerCase().startsWith("i_")) {
                    found = true;
                    f = dirFile;

                    break;
                }
            }

            if (!found) {
                problemLog.append("<p><b>Investigation file does not exist in this folder. Please create an investigation file and name it " +
                        "\"i_<investigation indentifier>.txt\"</b></p>");

                return false;
            }
        }


        try {

            System.out.println("Checking investigation file");

            if (checkInvestigationFileStructure(f)) {
                CSVReader csvReader = new CSVReader(new FileReader(f), '\t');

                String[] nextLine;

                while ((nextLine = csvReader.readNext()) != null) {

                    String valOfInterest = nextLine[0].trim();

                    if (valOfInterest.equals("ONTOLOGY SOURCE REFERENCE")) {
                        log.info("Processing ontology source information");
                        readOntologyTerms(csvReader, allowedSections.get(valOfInterest).getNumRows());
                    }

                    if (valOfInterest.equals("INVESTIGATION")) {
                        log.info("Processing investigation information");
                        readInvestigation(csvReader, allowedSections.get(valOfInterest).getNumRows());
                    }

                    if (valOfInterest.equals("INVESTIGATION PUBLICATIONS")) {
                        processSubData(csvReader, READING_INVESTIGATION_PUBLICATIONS, allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("INVESTIGATION CONTACTS")) {
                        processSubData(csvReader, READING_INVESTIGATION_CONTACTS, allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("STUDY")) {
                        log.info("Processing study information");
                        readStudy(csvReader, parentDir, allowedSections.get(valOfInterest).getNumRows());
                    }

                    if (valOfInterest.equals("STUDY DESIGN DESCRIPTORS")) {
                        processSubData(csvReader, READING_DESIGNS,
                                allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("STUDY PUBLICATIONS")) {
                        processSubData(csvReader, READING_STUDY_PUBLICATIONS, allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("STUDY FACTORS")) {
                        processSubData(csvReader, READING_FACTORS,
                                allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("STUDY ASSAYS")) {
                        log.info("processing assays");
                        processSubData(csvReader, READING_ASSAYS,
                                allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("STUDY PROTOCOLS")) {
                        processSubData(csvReader, READING_PROTOCOLS,
                                allowedSections.get(valOfInterest).getNumRows(), parentDir);
                    }

                    if (valOfInterest.equals("STUDY CONTACTS")) {
                        processSubData(csvReader, READING_STUDY_CONTACTS,
                                allowedSections.get(valOfInterest).getNumRows(), parentDir);

                        // ADD STUDY TO THE INVESTIGATION!
                        if (constructWithGUIs) {
                            addStudyWithGUIs();
                        } else {
                            addStudyWithoutGUIS();
                        }
                    }
                }
            } else {
                problemLog.append("<p><b>Malformed investigation file: </b> The investigation file does not have the correct structure. If the structure is correct, please ensure that the section headers are <b>UPPERCASE</b>.");
                return false;
            }

            if (validateInvestigationFile(investigation)) {
                investigation.setOntologiesUsed(ontologySourcesDefined);
                investigation.setUserInterface(new InvestigationDataEntry(investigation, dataEntryEnvironment));
                investigation.setReference(f.getPath());
                dataEntryEnvironment.createGUIFromSource(investigation);
            } else {
                return false;
            }

            return true;
        } catch (FileNotFoundException e) {
            problemLog.append("<p>").append(e.getMessage()).append("</p>");
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        } catch (IOException e) {
            problemLog.append("<p>").append(e.getMessage()).append("</p>");
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        } catch (MalformedOntologyTermException e) {
            problemLog.append("<p>").append(e.getMessage()).append("</p>");
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        } catch (MalformedInvestigationException e) {
            problemLog.append("<p>").append(e.getMessage()).append("</p>");
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void addStudyWithGUIs() throws MalformedInvestigationException {
        System.out.println("Adding study with GUIs");
        StudyDataEntry sde = new StudyDataEntry(dataEntryEnvironment,
                currentStudy);

        currentStudy.setUI(sde);

        for (Assay a : assaysToAdd) {
            a.setUserInterface(sde);

            for (String s : a.getSpreadsheetUI().getTable()
                    .getOntologiesDefinedInTable()) {
                ontologyTermsDefined.add(new OntologyObject(
                        "", "", s));
            }

            if (!currentStudy.addAssay(a)) {
                problemLog.append("<p><b>Duplicate Assay Found : </b> Assay with name ").append(a.getAssayReference()).append(" already exists in Study ").append(currentStudy.getStudyId());
                throw new MalformedInvestigationException(
                        "Assay with name " + a.getAssayReference() +
                                " already exists in this study!");
            }
        }

        if (studySample != null) {
            studySample.setUserInterface(sde);
            currentStudy.setStudySamples(studySample);
        }

        if (!investigation.addStudy(currentStudy)) {
            problemLog.append("<p><b>A problem occurred when adding </b> Study with name ").append(currentStudy.getStudyId()).append(". Check that it hasn't been added to the Investigation file too many times" +
                    "and ensure that the investigation file is well formed!");
            throw new MalformedInvestigationException(
                    "Study with name " + currentStudy.getStudyId() +
                            " could not be added to the Investigation. Please check the Investigation file!");
        }

        assaysToAdd.clear();
    }

    private void addStudyWithoutGUIS() throws MalformedInvestigationException {
        for (Assay a : assaysToAdd) {

            if (!currentStudy.addAssay(a)) {
                problemLog.append("<p><b>Duplicate Assay Found : </b> Assay with name ").append(a.getAssayReference()).append(" already exists in Study ").append(currentStudy.getStudyId());
                throw new MalformedInvestigationException(
                        "Assay with name " + a.getAssayReference() +
                                " already exists in this study!");
            }
        }

        if (studySample != null) {
            currentStudy.setStudySamples(studySample);
        }

        if (!investigation.addStudy(currentStudy)) {
            problemLog.append("<p><b>Duplicate Study Found : </b> Study with name ").append(currentStudy.getStudyId()).append(" already exists.");
            throw new MalformedInvestigationException(
                    "Study with name " + currentStudy.getStudyId() +
                            "already exists");
        }

        assaysToAdd.clear();
    }


    /**
     * Method checks structure of investigation file to ensure that each section appears in the correct
     * order so that processing is more likely to be successful!
     *
     * @param f - Investigation file to check
     * @return - boolean - true if file is valid, false otherwise!
     * @throws FileNotFoundException - thrown if the file cannot be found
     */
    private boolean checkInvestigationFileStructure(File f)
            throws FileNotFoundException {
        Scanner sc = new Scanner(f);

        System.out.println("Checking " + f.getAbsolutePath());

        List<String> fileStructure = new ArrayList<String>();

        ISAPropertiesLoader ipl = new ISAPropertiesLoader();

        allowedSections = ipl.getSections();

        while (sc.hasNext()) {

            // all headers should be capitalised for each section
            String nextLine = sc.nextLine();

            if (!nextLine.trim().equals("")) {
                String nextVal = nextLine.split("\t")[0];
                nextVal = nextVal.replaceAll("\"", "");
                nextVal = nextVal.trim();
                if (allowedSections.keySet().contains(nextVal)) {
                    fileStructure.add(nextVal);
                }
            }
        }

        String pattern = ipl.getPreferredFileStructure();
        String toMatch = "";


        for (String header : fileStructure) {
            toMatch += (header.trim() + " ");
        }

        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(toMatch);

        boolean matches = m.matches();
        System.out.println("File Structure is valid? " + matches);

        return matches;
    }

    public DataEntryEnvironment getDataEntryPanel() {
        return dataEntryEnvironment;
    }

    public String getParentDir() {
        return parentDir;
    }

    public String getProblemLog() {
        return "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 10px;" +
                "   color: #BF1E2D;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">" +
                "<b>INVALID ISATAB FILE</b>" + problemLog.toString() +
                "</body></html>";
    }

    private String groupElements(String fieldBeingCombined, String term,
                                 String accession, String sourceRef)
            throws MalformedOntologyTermException {
        String toReturn = "";

        term = term == null ? "" : term;
        accession = accession == null ? "" : accession;
        sourceRef = sourceRef == null ? "" : sourceRef;
        // if all elements contain ; then we are dealing with multiple ontology terms
        if (term.contains(";") && sourceRef.contains(";")) {
            String[] splitTerms = term.split(";");

            // small hack to ensure that we have equal numbers of terms!
            if (sourceRef.endsWith(";")) {
                sourceRef += " ";
            }

            String[] splitSourceRefs = sourceRef.split(";");

            String[] splitAccession = null;

            if (accession.split(";").length > 0) {
                splitAccession = accession.split(";");
            }

            if ((splitTerms.length == splitSourceRefs.length)) {
                // we know that everything is properly formed between the split terms and the source refs

                // we know that everything is properly formed between the source refs, accessions, and the terms.
                for (int i = 0; i < splitTerms.length; i++) {
                    if (splitSourceRefs[i].equals("") ||
                            splitSourceRefs[i].equals(" ")) {
                        toReturn += splitTerms[i];
                    } else {
                        toReturn += (splitSourceRefs[i] + ":" + splitTerms[i]);

                        String accToAdd = "";

                        if ((splitAccession != null) &&
                                (i < splitAccession.length)) {
                            accToAdd = splitAccession[i];
                        }

                        ontologyTermsDefined.add(new OntologyObject(
                                splitTerms[i], accToAdd, splitSourceRefs[i]));

                    }

                    if (i != (splitTerms.length - 1)) {
                        toReturn += ";";
                    }
                }
            } else {
                problemLog.append("<p><b>Uneven distribution of terms: </b> Term and Source REF fields for <b>").append(fieldBeingCombined).append("</b> in <b>Study ").append(currentStudy.getStudyId()).append("</b> do not have an equal number of terms separated by <b>;</b></p>");
                throw new MalformedOntologyTermException(
                        "Ontology terms do not match in size for field " +
                                fieldBeingCombined + " in study " +
                                currentStudy.getStudyId());
            }
        } else {
            // can assume that there is only one value in each cell. so group them
            if (sourceRef.equals("")) {
                toReturn = term;
            } else {
                toReturn = sourceRef + ":" + term;


                if (!term.trim().equals("") &&
                        !sourceRef.trim().equals("")) {
                    ontologyTermsDefined.add(new OntologyObject(
                            term, accession, sourceRef));
                }

            }
        }

        return toReturn;
    }

    public List<OntologySourceRefObject> getOntologySourcesDefined() {
        return ontologySourcesDefined;
    }

    public List<OntologyObject> getOntologyTermsDefined() {
        return ontologyTermsDefined;
    }


    /**
     * Create tablemodel for item!
     *
     * @param fileName        - Name of file to load
     * @param defaultTableRef - TableReferenceObject to be used to load in file
     * @return TableReferenceObject describing the table
     * @throws java.io.IOException when file does not exist or if the CSVReader cannot read the next line
     * @throws MalformedInvestigationException
     *                             - when a problem is found when reading in file.
     */
    private TableReferenceObject loadInTables(String fileName,
                                              TableReferenceObject defaultTableRef) throws IOException, MalformedInvestigationException {
        // process headers to build table reference object for table.
        // create new sortable and then read in the data row by row :o)

        File f = new File(fileName);

        if (f.exists()) {
            CSVReader reader = new CSVReader(new FileReader(f), '\t');
            int count = 0;
            String[] nextLine;
            String[] colHeaders = null;
            TableReferenceObject tro = null;

            while ((nextLine = reader.readNext()) != null) {
                if (count == 0) {
                    colHeaders = nextLine;
                    tro = reformTableDefinition(fileName, nextLine,
                            defaultTableRef);

                    Vector<String> preDefinedHeaders = new Vector<String>();
                    preDefinedHeaders.add("Row No.");

                    for (String h : nextLine) {
                        if (!h.toLowerCase().contains("term source ref") &&
                                !h.toLowerCase().contains("term accession number") &&
                                !h.equals("")) {
                            preDefinedHeaders.add(h);
                        }
                    }

                    if (preDefinedHeaders.size() > 0) {
                        tro.setPreDefinedHeaders(preDefinedHeaders);
                    }

                    count++;
                } else {
                    tro.addRowData(colHeaders, nextLine);
                }
            }

            return tro;
        } else {
            throw new FileNotFoundException("<p>The file " + fileName + " was not found. Please ensure that the file exists within the folder and that the name referred to in the investigation file is correct!</p>");
        }
    }

    /**
     * Method pads out an array of Strings to be of a defined length
     * Required when parser starts splits lines unevenly, leading to an
     * unstable structure when writing out the data.
     *
     * @param array          - String array to pad out
     * @param extendToLength - Length to extend array to
     * @return The padded array with empty items filled with blank Strings.
     */
    private String[] padoutArray(String[] array, int extendToLength) {
        String[] paddedArray;

        if (array.length == extendToLength) {
            return array;
        } else {
            paddedArray = new String[extendToLength];

            for (int i = 0; (i < paddedArray.length) && (i < array.length);
                 i++) {
                paddedArray[i] = array[i];
            }

            for (int i = 0; i < paddedArray.length; i++) {
                if (paddedArray[i] == null) {
                    paddedArray[i] = "";
                }
            }
        }

        return paddedArray;
    }

    /**
     * Process all the subform data
     *
     * @param csvReader - the CSVReader instance provided to parse the files
     * @param beingRead - the section of the file currently being read - see static Integer values for current class
     * @param numLines  - number of lines to expect to be read for the main investigation file
     * @param parentDir - The parent directory to get the required files from (e.g. Sample files, and assay)
     * @throws IOException                    - Thrown by CSVReader when IO problem occurs
     * @throws MalformedOntologyTermException - Thrown when Ontology field values which accept multiple values do not have a matching number of sources, accessions, and terms.
     * @throws MalformedInvestigationException
     *                                        - When an error attributed to Investigation file Structure is found.
     */
    private void processSubData(CSVReader csvReader, int beingRead,
                                int numLines, String parentDir)
            throws IOException, MalformedOntologyTermException, MalformedInvestigationException {

        String[][] data = null;
        int count = 0;
        String[] nextLine;

        while ((count < numLines) &&
                ((nextLine = csvReader.readNext()) != null)) {
            if (count == 0) {
                data = new String[numLines][nextLine.length];
            }

            data[count] = padoutArray(nextLine, data[0].length);
            count++;
        }

        // by this point, we have an array representation of the entire data source!
        if (data != null) {
            if (beingRead == READING_FACTORS) {
                List<Factor> factors = new ArrayList<Factor>();

                for (int i = 1; i < data[0].length; i++) {
                    factors.add(new Factor(data[0][i], data[1][i], data[2][i],
                            data[3][i]));
                }

                for (Factor f : factors) {
                    if ((f.getFactorType() != null) &&
                            !f.getFactorType().trim().equals("")) {
                        f.setFactorType(groupElements("Factor type",
                                f.getFactorType(),
                                f.getFactorTypeTermAccession(),
                                f.getFactorTypeTermSource()));
                    } else {
                        f.setFactorType("");
                    }

                    if (!f.getFactorName().trim().equals("")) {
                        currentStudy.getFactors().add(f);
                    }
                }
            }

            if (beingRead == READING_ASSAYS) {
                for (int i = 1; i < data[0].length; i++) {
                    TableReferenceObject referenceObject = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(data[0][i],
                            data[3][i]);
                    if (referenceObject != null && !data[7][i].trim().equals("")) {
                        TableReferenceObject builtReference = loadInTables(parentDir +
                                data[7][i], referenceObject);

                        if (builtReference != null) {
                            assaysToAdd.add(new Assay(data[7][i],
                                    data[0][i], data[3][i], data[6][i],
                                    builtReference));
                        }
                    }
                }
            }

            if (beingRead == READING_STUDY_CONTACTS || beingRead == READING_INVESTIGATION_CONTACTS) {
                List<Contact> contacts = new ArrayList<Contact>();

                for (int i = 1; i < data[0].length; i++) {
                    contacts.add(new Contact(data[0][i], data[1][i],
                            data[2][i], data[3][i], data[4][i], data[5][i],
                            data[6][i], data[7][i], data[8][i], data[9][i],
                            data[10][i]));
                }

                for (Contact c : contacts) {
                    if (!c.getFirstName().trim().equals("") && !c.getLastName().trim().equals("")) {
                        c.setRole(groupElements("Contact Role", c.getRole(),
                                c.getRoleTermAccession(), c.getRoleTermSourceRef()));
                        if (beingRead == READING_STUDY_CONTACTS) {
                            currentStudy.getContacts().add(c);
                        } else {
                            investigation.getContacts().add(c);
                        }
                    }
                }

                // since contacts will be the last thing to read in the investigation section, we should create the investigation gui here :o)

            }

            if (beingRead == READING_STUDY_PUBLICATIONS || beingRead == READING_INVESTIGATION_PUBLICATIONS) {
                List<Publication> publications = new ArrayList<Publication>();

                for (int i = 1; i < data[0].length; i++) {
                    publications.add(new Publication(data[0][i], data[1][i],
                            data[2][i], data[3][i], data[4][i], data[5][i],
                            data[6][i]));
                }

                for (Publication p : publications) {
                    if (p.hasInformation()) {
                        p.setPublicationStatus(groupElements("Publication Status", p.getPublicationStatus(), p.getPublicationTermAcc(), p.getPublicationSourceRef()));

                        if (beingRead == READING_STUDY_PUBLICATIONS) {
                            currentStudy.getPublications().add(p);
                        } else {
                            investigation.getPublications().add(p);
                        }

                    }
                }
            }

            if (beingRead == READING_DESIGNS) {
                List<StudyDesign> designs = new ArrayList<StudyDesign>();

                for (int i = 1; i < data[0].length; i++) {
                    designs.add(new StudyDesign(data[0][i], data[1][i], data[2][i]));
                }

                for (StudyDesign sd : designs) {
                    if (!sd.getStudyDesignType().trim().equals("")) {
                        sd.setStudyDesignType(groupElements("Study Design", sd.getStudyDesignType(), sd.getStudyDesignTermAcc(), sd.getStudyDesignTermSourceRef()));
                        currentStudy.getStudyDesigns().add(sd);
                    }
                }
            }

            if (beingRead == READING_PROTOCOLS) {
                List<Protocol> protocols = new ArrayList<Protocol>();

                for (int i = 1; i < data[0].length; i++) {
                    protocols.add(new Protocol(data[0][i], data[1][i],
                            data[2][i], data[3][i], data[4][i], data[5][i],
                            data[6][i], data[7][i], data[8][i], data[9][i],
                            data[10][i], data[11][i], data[12][i], data[13][i]));
                }

                for (Protocol p : protocols) {
                    // group elements method allows for the Term, Source, And Accession to be process into one value in the
                    // term field, with an OntologyObject being built for each term!
                    p.setProtocolType(groupElements("Protocol Type",
                            p.getProtocolType(),
                            p.getProtocolTypeTermAccession(),
                            p.getProtocolTypeTermSourceRef()));

                    p.setProtocolParameterName(groupElements(
                            "Protocol Parameter Name",
                            p.getProtocolParameterName(),
                            p.getProtocolParameterNameAccession(),
                            p.getProtocolParameterNameSource()));

                    p.setProtocolComponentType(groupElements("Protocol Components Type", p.getProtocolComponentType(), p.getProtocolComponentTypeAccession(), p.getProtocolComponentTypeSource()));


                    if (!p.getProtocolName().trim().equals("")) {
                        currentStudy.getProtocols().add(p);
                    }
                }
            }
        }
    }

    /**
     * method reads in the investigation description and inserts it into the software for editing/viewing
     *
     * @param csvReader - CSVReader object being used to read the file
     * @param numLines  - Number of lines to be read for this Investigation
     * @throws java.io.IOException by CSVReader
     */
    private void readInvestigation(CSVReader csvReader, int numLines)
            throws IOException {
        int count = 0;
        String[] nextLine;
        String[] invData = new String[numLines];

        while ((count < numLines) &&
                ((nextLine = csvReader.readNext()) != null)) {

            invData[count] = nextLine.length > 1 ? nextLine[1] : "";

            count++;
        }
        investigation = new Investigation(invData[0], invData[1], invData[2],
                invData[3], invData[4]);
    }

    /**
     * method reads in the ontology source refs and inserts them back into the software
     *
     * @param csvReader - CSVReader object being used to read the file
     * @param numLines  - Number of lines to read for this section.
     * @throws java.io.IOException by CSVReader
     */
    private void readOntologyTerms(CSVReader csvReader, int numLines)
            throws IOException {
        int count = 0;
        String[][] definitions = null;

        String[] nextLine;

        while ((count < numLines) &&
                ((nextLine = csvReader.readNext()) != null)) {
            if (count == 0) {
                definitions = new String[numLines][nextLine.length];
            }

            definitions[count] = padoutArray(nextLine, definitions[0].length);

            count++;
        }

        if (definitions != null) {
            for (int col = 1; col < definitions[0].length; col++) {
                OntologySourceRefObject osro = new OntologySourceRefObject(definitions[0][col],
                        definitions[1][col], definitions[2][col],
                        definitions[3][col]);


                if (!osro.getSourceName().trim().equals("")) {
                    ontologySourcesDefined.add(osro);
                }
            }
        }

    }

    /**
     * method reads in the study definition and inserts it into the software for editing/viewing
     *
     * @param csvReader - CSVReader object being used to read the file
     * @param parentDir - directorty to load information from
     * @param numLines  - number of Lines to be read
     * @throws java.io.IOException by CSVReader
     * @throws MalformedInvestigationException
     *                             - if an Assay is not recognised.
     */
    private void readStudy(CSVReader csvReader, String parentDir, int numLines)
            throws IOException, MalformedInvestigationException {
        int count = 0;
        String[] nextLine;
        Map<String, String> fieldNameToValMapping = new HashMap<String, String>();
        String[] studyData = new String[numLines];
        String previousFieldName = "";

        while ((count < numLines) &&
                ((nextLine = csvReader.readNext()) != null)) {

            try {

                previousFieldName = nextLine[0].trim();

                fieldNameToValMapping.put(nextLine[0], nextLine[1]);
                // maintain list to keep order of elements!
                studyData[count] = nextLine[0];


                count++;
            } catch (Exception e) {
                problemLog.append("<b>").append(previousFieldName).append("</b> has an error in it's definition");
            }
        }

        List<String> finalVals = new ArrayList<String>();

        for (String s : studyData) {
            if (s != null && !s.contains("Term Accession Number") &&
                    !s.contains("Term Source REF")) {
                finalVals.add(fieldNameToValMapping.get(s));
            }
        }

        currentStudy = new Study(finalVals.get(0), finalVals.get(1),
                finalVals.get(2), finalVals.get(3), finalVals.get(4),
                finalVals.get(5));

        // get study sample and attach it to the study!
        String sampleFile = currentStudy.getStudySampleFileIdentifier();

        TableReferenceObject referenceObject = dataEntryEnvironment.getParentFrame().selectTROForUserSelection(
                MappingObject.STUDY_SAMPLE);

        if (referenceObject != null) {

            try {
                TableReferenceObject builtReference = loadInTables(parentDir +
                        sampleFile, referenceObject);

                if (builtReference != null) {
                    studySample = new Assay(sampleFile, builtReference);
                }
            } catch (MalformedInvestigationException mie) {
                throw mie;
            } catch (Exception e) {
                e.printStackTrace();
                throw new MalformedInvestigationException("Unrecognised assay in " + sampleFile);
            }
        }
    }

    /**
     * Process the table file, piecing together which Units belong to which factors, and which parameters belong to which protocol refs, etc.
     *
     * @param tableName      - Name of table to read
     * @param headers        - The column headers for the table
     * @param startReference - the TableReferenceObject to be used to define standard terms
     * @return the Fully built table reference object for the table!
     * @throws MalformedInvestigationException
     *          -  when a problem is found when reforming the Investigation.
     */
    private TableReferenceObject reformTableDefinition(String tableName,
                                                       String[] headers, TableReferenceObject startReference) throws MalformedInvestigationException {
        TableReferenceObject tro = new TableReferenceObject(tableName);

        // way of storing previously seen protocol to determine where the parameters are which associated with it.
        int previousProtocol = -1;

        // way of storing previously read characteristic, factor, or parameter to determine what type it is
        String previousCharFactParam = null;
        int expectedNextUnitLocation = -1;
        int count = 0;
        int positionInheaders = 0;
        int parentColPos;

        for (String h : headers) {
            positionInheaders++;

            if (expectedNextUnitLocation == positionInheaders) {
                if (h.toLowerCase().contains("unit")) {
                    // add two fields...one accepting string values and the unit, also accepting string values :o)
                    TableFieldObject newFo = new TableFieldObject(count,
                            previousCharFactParam, "", DataTypes.STRING, "", false, false, false);
                    tro.addField(newFo);

                    if (tro.getColumnDependencies().get(count) == null) {
                        tro.getColumnDependencies()
                                .put(count, new ListOrderedSet<Integer>());
                    }

                    parentColPos = count;

                    count++;

                    newFo = new TableFieldObject(count, h, "", DataTypes.ONTOLOGY_TERM, "", false, false, false);
                    tro.addField(newFo);

                    tro.getColumnDependencies().get(parentColPos).add(count);

                    count++;


                    // AND ATTACH UNIT TO FIELD VIA THE MAPPING IN THE TABLE CLASS
                } else {
                    // add a field accepting ontology terms
                    TableFieldObject newFo = new TableFieldObject(count,
                            previousCharFactParam, "", DataTypes.ONTOLOGY_TERM, "",
                            false, false, false);
                    tro.addField(newFo);

                    parentColPos = count;
                    count++;
                }

                // add just added parameter to a list of dependencies to be maintained for each protocol reference (Protocol REF) field
                if (previousCharFactParam != null && previousCharFactParam.toLowerCase().contains("parameter value")) {
                    if (tro.getColumnDependencies().get(previousProtocol) == null) {
                        tro.getColumnDependencies()
                                .put(previousProtocol,
                                        new ListOrderedSet<Integer>());
                    }
                    tro.getColumnDependencies().get(previousProtocol)
                            .add(parentColPos);
                }

                // reset expectedPosition
                expectedNextUnitLocation = -1;
            }

            TableFieldObject field = startReference.getFieldByName(h);

            if (field != null) {
                tro.addField(field);
                count++;
            } else {
                // doesn't exist
                if (h.toLowerCase().contains("factor value") ||
                        h.toLowerCase().contains("characteristics") ||
                        h.toLowerCase().contains("parameter value")) {

                    previousCharFactParam = h;
                    expectedNextUnitLocation = positionInheaders + 1;
                }

                if (h.equalsIgnoreCase("performer") ||
                        h.contains("Comment") ||
                        h.equalsIgnoreCase("provider")) {
                    TableFieldObject additionalFo = new TableFieldObject(count, h,
                            "An additional column", DataTypes.STRING, "", false,
                            false, false);
                    tro.addField(additionalFo);
                    count++;
                }

                if (h.toLowerCase().contains("material type")) {
                    TableFieldObject newFo = new TableFieldObject(count,
                            h, "", DataTypes.ONTOLOGY_TERM, "",
                            false, false, false);
                    tro.addField(newFo);
                    count++;
                }

                if (h.toLowerCase().contains("date")) {
                    TableFieldObject dateFo = new TableFieldObject(count, h,
                            "Date field", DataTypes.DATE, "", false, false, false);
                    tro.addField(dateFo);

                    count++;
                }

                if (h.toLowerCase().contains("protocol ref")) {
                    previousProtocol = count;

                    TableFieldObject newFo = new TableFieldObject(count, "Protocol REF",
                            "A reference to a protocol", DataTypes.LIST, "", false,
                            false, false);
                    tro.addField(newFo);
                    count++;
                }
            }
        }

        if (expectedNextUnitLocation != -1) {
            // add last factor/characteristic to the table
            TableFieldObject newFo = new TableFieldObject(count, previousCharFactParam,
                    "", DataTypes.ONTOLOGY_TERM, "", false, false, false);
            tro.addField(newFo);
        }

        tro.setMissingFields(GeneralUtils.findMissingFields(headers, startReference));

        Set<String> invalidHeaders = findInvalidFields(headers, tro);

        if (invalidHeaders.size() > 0) {

            String invalidHeaderNames = "";

            for (String s : invalidHeaders) {
                invalidHeaderNames += "<li><b>" + s + "</b></li>";
            }

            String colText = invalidHeaders.size() > 1 ? invalidHeaders.size() + " columns do" : "column does";

            throw new MalformedInvestigationException("The following " + colText + " not exist for the assay defined in " + tableName + ":</p><ul>" + invalidHeaderNames + "</ul>");
        }

        return tro;
    }

    private Set<String> findInvalidFields(String[] headers, TableReferenceObject finalTableDefinition) {

        Set<String> headerSet = new HashSet<String>();

        headerSet.addAll(Arrays.asList(headers));

        Set<String> invalidHeaders = new HashSet<String>();

        Set<String> toIgnoreAsSet = new HashSet<String>();
        toIgnoreAsSet.add("Term Source REF");
        toIgnoreAsSet.add("Term Accession Number");

        for (String field : headerSet) {
            if (!toIgnoreAsSet.contains(field) && !field.trim().equals("") && !finalTableDefinition.getHeaders().contains(field)) {
                invalidHeaders.add(field);
            }
        }
        return invalidHeaders;
    }

    public void setParentDir(String parentDir) {
        this.parentDir = parentDir;
    }

    private boolean validateInvestigationFile(Investigation inv) {
        // check for duplicate assay names across all studies.
        Set<String> assayNames = new HashSet<String>();
        Set<String> studyNames = new HashSet<String>();

        for (Study s : inv.getStudies().values()) {
            if (studyNames.contains(s.getStudyId())) {
                problemLog.append("<p><b>Duplicate study names found in investigation! </b>Study with with ID : ").append(s.getStudyId()).append(" already exists!</p>");

                return false;
            } else {
                studyNames.add(s.getStudyId());
            }

            for (Assay a : s.getAssays().values()) {
                if (assayNames.contains(a.getAssayReference())) {
                    problemLog.append("<p><b>Duplicate assay found in investigation! </b>Assay with with name : ").append(a.getAssayReference()).append(" already exists!</p>");

                    return false;
                } else {
                    assayNames.add(a.getAssayReference());
                }
            }
        }

        // check that all ontologies have been defined

        // build up set of ontology sources that have been defined
        Set<String> definedOntologySources = new HashSet<String>();

        for (OntologySourceRefObject osro : ontologySourcesDefined) {
            definedOntologySources.add(osro.getSourceName());
        }

        // now search through added ontology objects to determine which ontologies haven't been defined
        Set<String> missingOntologyObjects = new HashSet<String>();

        for (OntologyObject oo : ontologyTermsDefined) {
            if (!definedOntologySources.contains(oo.getTermSourceRef()) &&
                    !oo.getTermSourceRef().equals("")) {
                System.out.println(oo.getUniqueId());
                if (!GeneralUtils.isValueURL(oo.getUniqueId())) {
                    missingOntologyObjects.add(oo.getTermSourceRef());
                }
            }
        }

        if (missingOntologyObjects.size() > 0) {
            String missing = "";

            for (String m : missingOntologyObjects) {
                missing += (m + " ");
            }

            problemLog.append("<p><b>Some ontology sources are not defined in the ONTOLOGY SOURCE REFERENCE section -> </b> ").append(missing).append(" </p>");
            log.info("Some ontology sources are not defined in the ONTOLOGY SOURCE REFERENCE section -> " + missing);
            return false;
        }

        return true;
    }
}
