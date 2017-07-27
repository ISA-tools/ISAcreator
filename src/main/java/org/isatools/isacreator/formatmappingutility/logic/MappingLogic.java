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

package org.isatools.isacreator.formatmappingutility.logic;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVParser;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreator.assayselection.AssaySelection;
import org.isatools.isacreator.assayselection.AssaySelectionUI;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.formatmappingutility.loader.FileLoader;
import org.isatools.isacreator.formatmappingutility.ui.*;
import org.isatools.isacreator.formatmappingutility.utils.TableReferenceObjectWrapper;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.utils.GeneralUtils;
import org.isatools.isacreator.utils.StringProcessing;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * MappingLogic
 *
 * @author Eamonn Maguire
 * @date Jun 12, 2009
 */


public class MappingLogic {
    private static final Logger log = Logger.getLogger(MappingEntryGUI.class.getName());

    private List<MappingField> isatabFields;
    private List<MappingInformation> mappingInformation;
    private List<MappedElement> mappings;
    private Set<Integer> uniqueRowHashes;
    private TableReferenceObject referenceTRO;

    private static List<Factor> factorsToAdd;
    private static List<Protocol> protocolsToAdd;

    private String fileName;
    private int readerToUse;
    private boolean mapToBlankFields;
    private Map<MappingField, List<String>> visMapping;
    private Set<String> fieldsUsingUnits;

    // should take in a List of ISATAB columns to be mapped to and a list of JLayeredPanes to get the mappings from.

    public MappingLogic(List<MappedElement> mappings, TableReferenceObject referenceTRO, int readerToUse, boolean mapToBlankFields) {
        this.mappings = mappings;
        this.referenceTRO = referenceTRO;
        this.readerToUse = readerToUse;
        this.mapToBlankFields = mapToBlankFields;
        isatabFields = new ArrayList<MappingField>();
        mappingInformation = new ArrayList<MappingInformation>();
        uniqueRowHashes = new HashSet<Integer>();
        fieldsUsingUnits = new HashSet<String>();
        factorsToAdd = new ArrayList<Factor>();
        protocolsToAdd = new ArrayList<Protocol>();
    }

    private void processMappings() {
        for (MappedElement mn : mappings) {
            MappingInformation mi = mn.getDisplay();
            System.out.println("Map to blank fields - " + mapToBlankFields);
            if (mi.isMappedTo() || mapToBlankFields) {
                isatabFields.add(new MappingField(mn.getFieldName()));
                mappingInformation.add(mi);
            }
        }
    }


    public TableReferenceObject doMapping(String fileName, int readerToUse) {
        this.fileName = fileName;
        this.readerToUse = readerToUse;

        // should create a new List<String> containing all fields including units and parameter etc. to be added, and in
        // doing so also create a second List<String> which will provide a way of doing string substitutions on formatted strings such as:
        // <<1>>-<<5>>and<<7>> where the values inside the <<?>> are the column numbers corresponding to a column in the incoming file
        // where the data should be taken from.
        processMappings();

        List<MappingField> fullTable = new ArrayList<MappingField>();
        List<String> substitutions = new ArrayList<String>();

        // we need a data structure to contain mapping information for display in the visualisation graph to make it easy
        // for users to see what the isatab fields have been mapped to.
        visMapping = new ListOrderedMap<MappingField, List<String>>();

        for (int field = 0; field < isatabFields.size(); field++) {
            MappingInformation mi = mappingInformation.get(field);

            MappingField mappingField = isatabFields.get(field);

            fullTable.add(mappingField);
            visMapping.put(mappingField, new ArrayList<String>());

            if (mi instanceof NormalFieldEntry) {
                NormalFieldEntry normalFieldEntry = (NormalFieldEntry) mi;

                if (normalFieldEntry.isMappedTo()) {
                    substitutions.add(normalFieldEntry.getFormatBuider().toString());
                    visMapping.get(mappingField).addAll(normalFieldEntry.getFormatBuider().getVisualizationText());
                } else {
                    substitutions.add("");
                }

            } else if (mi instanceof GeneralAttributeEntry) {
                GeneralAttributeEntry generalFieldEntry = (GeneralAttributeEntry) mi;

                if (generalFieldEntry.isMappedTo()) {
                    substitutions.add(generalFieldEntry.getNormalFieldEntry().getFormatBuider().toString());
                    visMapping.get(mappingField).addAll(generalFieldEntry.getNormalFieldEntry().getFormatBuider().getVisualizationText());
                } else {
                    substitutions.add("");
                }

                if (generalFieldEntry.getUnitPanel().useField()) {
                    fieldsUsingUnits.add(generalFieldEntry.getFieldName());
                    fullTable.add(new MappingField(GeneralFieldTypes.UNIT.name));
                    substitutions.add(generalFieldEntry.getUnitPanel().getFieldBuilder().toString());
                    visMapping.get(mappingField).addAll(generalFieldEntry.getUnitPanel().getFieldBuilder().getVisualizationText());
                }
            } else if (mi instanceof ProtocolFieldEntry) {
                ProtocolFieldEntry protocolFieldFieldEntry = (ProtocolFieldEntry) mi;

                if (protocolFieldFieldEntry.isMappedTo()) {
                    substitutions.add(protocolFieldFieldEntry.getNormalFieldEntry().getFormatBuider().toString());
                    visMapping.get(mappingField).addAll(protocolFieldFieldEntry.getNormalFieldEntry().getFormatBuider().getVisualizationText());
                } else {
                    substitutions.add("");
                }

                if (protocolFieldFieldEntry.getPerformerPanel().useField()) {
                    fullTable.add(new MappingField("Performer"));
                    substitutions.add(protocolFieldFieldEntry.getPerformerPanel().getFieldBuilder().toString());
                    visMapping.get(mappingField).addAll(protocolFieldFieldEntry.getPerformerPanel().getFieldBuilder().getVisualizationText());
                }

                if (protocolFieldFieldEntry.getDatePanel().useField()) {
                    fullTable.add(new MappingField("Date"));
                    substitutions.add(protocolFieldFieldEntry.getDatePanel().getFieldBuilder().toString());
                    visMapping.get(mappingField).addAll(protocolFieldFieldEntry.getDatePanel().getFieldBuilder().getVisualizationText());
                }
            }
        }

        // now, with each mapping unit column we need to construct a field or fields (if a unit is used/required!) and we also need to
        // point each Field to where it needs to take its data from!

        TableReferenceObject tro = new TableReferenceObject(referenceTRO.getTableFields());
        return manufactureReferenceObject(fullTable, substitutions, tro);
    }

    public static Investigation createInvestigation(Map<String, TableReferenceObject> mappings, Map<String, AssaySelection> assayInfo, DataEntryEnvironment dep) {
        Investigation inv = new Investigation("", "");

        Study study = new Study("Mapped Study", "A study mapped from an incoming file", "", "", "", "s_study_sample.txt");

        System.out.println("Creating investigation, and adding all found factors");
        addAllFactorsToInvestigationFile(mappings);
        study.setFactors(factorsToAdd);
        addProtocols(mappings);

        study.setProtocols(protocolsToAdd);


        StudyDataEntry sde = new StudyDataEntry(dep, study);
        ApplicationManager.assignDataEntryToISASection(study, sde);

        Assay studySample = new Assay("s_study_sample.txt", mappings.get(MappingObject.STUDY_SAMPLE));

        ApplicationManager.assignDataEntryToISASection(studySample, ApplicationManager.getUserInterfaceForAssay(studySample, sde));

        study.setStudySamples(studySample);

        for (String assayName : mappings.keySet()) {
            if (!assayName.equals(MappingObject.STUDY_SAMPLE)) {
                AssaySelection aso = assayInfo.get(assayName);
                String measurement = aso.getMeasurement();
                String technology = aso.getTechnology().equals(AssaySelectionUI.NO_TECHNOLOGY_TEXT) ? "" : aso.getTechnology();
                String platform = aso.getPlatform();

                Assay mappedAssay = new Assay("a_" + assayName.replaceAll("\\s+", "") + ".txt",
                        measurement, technology, platform, mappings.get(assayName));

                ApplicationManager.assignDataEntryToISASection(mappedAssay, ApplicationManager.getUserInterfaceForAssay(mappedAssay, sde));
                study.addAssay(mappedAssay);
                inv.addToAssays(mappedAssay.getAssayReference(), study.getStudyId());
            }
        }


        sde.updateAssayPanel();

        inv.addStudy(study);

        return inv;
    }

    private static void addProtocols(Map<String, TableReferenceObject> mappings) {
        for (TableReferenceObject tableReferenceObject : mappings.values()) {
            protocolsToAdd.addAll(new TableReferenceObjectWrapper(tableReferenceObject).findProtocols());
        }
    }

    public Map<MappingField, List<String>> getVisMapping() {
        return visMapping;
    }

    private static void addAllFactorsToInvestigationFile(Map<String, TableReferenceObject> mappings) {
        Set<String> addedFactors = new HashSet<String>();
        for (TableReferenceObject tro : mappings.values()) {
            for (String header : tro.getHeaders()) {
                if (header.contains(GeneralFieldTypes.FACTOR_VALUE.name)) {
                    if (!addedFactors.contains(header)) {
                        addedFactors.add(header);
                        String tmpFactor = header.substring(header.indexOf("[") + 1, header.lastIndexOf("]"));
                        factorsToAdd.add(new Factor(tmpFactor, tmpFactor));
                    }
                }
            }
        }
    }

    private TableReferenceObject manufactureReferenceObject(List<MappingField> isatab, List<String> substitutions, TableReferenceObject referenceTRO) {
        TableReferenceObject final_tro = new TableReferenceObject(referenceTRO.getTableName() + "_1");

        Vector<String> headers = new Vector<String>();
        headers.add("Row No.");

        List<String> filteredSubstitutions = new ArrayList<String>();

        FieldObject fo;

        int count = 0;
        for (int fieldNumber = 0; fieldNumber < isatab.size(); fieldNumber++) {

            MappingField field = isatab.get(fieldNumber);

            fo = referenceTRO.getFieldByName(field.getFieldName());
            if (fo != null) {
                final_tro.addField(fo);
                headers.add(field.getFieldName());
                filteredSubstitutions.add(substitutions.get(fieldNumber));
                count++;
            } else {
                if (field.getFieldName().contains(GeneralFieldTypes.PROTOCOL_REF.name)) {
                    String protocolType = field.getFieldName().substring(field.getFieldName().indexOf("(") + 1, field.getFieldName().indexOf(")")).trim();
                    fo = new FieldObject(count, GeneralFieldTypes.PROTOCOL_REF.name, "", DataTypes.LIST, protocolType, false, false, false);
                    final_tro.addField(fo);
                    headers.add(GeneralFieldTypes.PROTOCOL_REF.name);
                    filteredSubstitutions.add(substitutions.get(fieldNumber));
                    count++;
                } else if (field.getFieldName().contains("Characteristics") || field.getFieldName().contains("Factor Value") ||
                        field.getFieldName().contains("Parameter") || field.getFieldName().contains("Unit")) {
                    DataTypes fieldDataType = fieldsUsingUnits.contains(field.getFieldName()) ? DataTypes.STRING : DataTypes.ONTOLOGY_TERM;
                    fo = new FieldObject(count, field.getFieldName(), field.getFieldName(), fieldDataType, "", false, false, false);
                    final_tro.addField(fo);
                    headers.add(field.getFieldName());
                    filteredSubstitutions.add(substitutions.get(fieldNumber));
                    count++;
                } else if (field.getFieldName().contains("Date")) {
                    fo = new FieldObject(count, field.getFieldName(), field.getFieldName(), DataTypes.DATE, "", false, false, false);
                    final_tro.addField(fo);
                    headers.add(field.getFieldName());
                    filteredSubstitutions.add(substitutions.get(fieldNumber));
                    count++;
                } else if (field.getFieldName().contains("Comment") || field.getFieldName().contains("Performer")) {
                    fo = new FieldObject(count, field.getFieldName(), field.getFieldName(), DataTypes.STRING, "", false, false, false);
                    final_tro.addField(fo);
                    headers.add(field.getFieldName());
                    filteredSubstitutions.add(substitutions.get(fieldNumber));
                    count++;
                }
            }
        }

        final_tro.setMissingFields(GeneralUtils.findMissingFields(headers.toArray(new String[headers.size()]), referenceTRO));
        final_tro.setPreDefinedHeaders(headers);

        createDataForTRO(headers.toArray(new String[headers.size()]), filteredSubstitutions, final_tro);

        return final_tro;
    }

    /**
     * Will read in the file parsed by the loader add add each row as it comes!
     *
     * @param headers       - Column names as an Array of Strings
     * @param substitutions - List of substitutions as <<1>>-<<4>><<8>> and so on
     * @param tro           - @see TableReferenceObject
     */
    private void createDataForTRO(String[] headers, List<String> substitutions, TableReferenceObject tro) {
        try {
            String[] subsAsArray = substitutions.toArray(new String[substitutions.size()]);

            int rowOffSet;
            try {
                rowOffSet = Integer.parseInt(ISAcreatorProperties.getProperty("isacreator.rowOffset")) - 1;
            } catch (NumberFormatException nfe) {
                rowOffSet = 0;
            }

            if (readerToUse == FileLoader.CSV_READER_CSV || readerToUse == FileLoader.CSV_READER_TXT) {

                char delimiter = (readerToUse == FileLoader.CSV_READER_CSV) ? FileLoader.COMMA_DELIM : FileLoader.TAB_DELIM;
                CSVReader fileReader = new CSVReader(new FileReader(fileName), delimiter,
                        CSVParser.DEFAULT_QUOTE_CHARACTER, '|');
                // read first line to discard it!
                String[] nextLine;

                int count = 0;
                while ((nextLine = fileReader.readNext()) != null) {

                    // we don't want the column names as well!
                    if (count != 0 && count > rowOffSet) {
                        // we decrement count since we're skipping 0.
                        addDataToTRO(headers, subsAsArray, nextLine, tro);
                    }
                    count++;
                }

            } else if (readerToUse == FileLoader.SHEET_READER) {
                // read the file using the Sheet reader from jxl library
                Workbook w;
                try {
                    File f = new File(fileName);
                    if (!f.isHidden()) {
                        w = Workbook.getWorkbook(f);
                        // Get the first sheet
                        for (Sheet s : w.getSheets()) {

                            if (s.getRows() > 1) {
                                for (int row = rowOffSet + 1; row < s.getRows(); row++) {
                                    String[] nextLine = new String[s.getColumns()];

                                    for (int col = 0; col < s.getColumns(); col++) {
                                        nextLine[col] = s.getCell(col, row).getContents();
                                    }
                                    addDataToTRO(headers, subsAsArray, nextLine, tro);
                                }
                            }
                            break;
                        }
                    }
                } catch (BiffException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else {
                log.info(" no reader available for use! ");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void addDataToTRO(String[] headers, String[] substitutions, String[] nextLine, TableReferenceObject tro) {
        String[] lineToAdd = new String[headers.length];

        int count = 0;

        // start off at 1 to ignore the Row no. header
        for (int column = 1; column < headers.length; column++) {
            lineToAdd[count] = StringProcessing.processSubstitutionString(substitutions[column - 1], nextLine);
            count++;
        }

        // code to only add the line if it is unique! no point in having exact duplicates!
        int lineHash = createHashFromArray(lineToAdd);

        if (!uniqueRowHashes.contains(lineHash)) {
            uniqueRowHashes.add(lineHash);
            tro.addRowData(headers, lineToAdd);
        }
    }


    private int createHashFromArray(String[] array) {
        StringBuilder buffer = new StringBuilder();
        for (String a : array) {
            buffer.append(a);
        }
        return buffer.toString().hashCode();


    }

}
