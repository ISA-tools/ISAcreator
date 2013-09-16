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

package org.isatools.isacreator.spreadsheet.model;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.configuration.TableConfiguration;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.model.GeneralFieldTypes;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.ontologymanager.utils.OntologyTermUtils;
import org.isatools.isacreator.spreadsheet.StringValidation;
import org.isatools.isacreator.spreadsheet.ValidationObject;

import java.io.Serializable;
import java.util.*;

/**
 * Provides a reference to the table to distinguish between fields,
 * field types, validation rules, whether or not they are required fields,
 * and whether or not they are editable/locked.
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class TableReferenceObject implements Serializable {
    public static final String ROW_NO_TEXT = "Row No.";

    private String tableName;
    private TableConfiguration tableConfig;

    private ReferenceData referenceData = null;
    private Map<Integer, ListOrderedSet<Integer>> columnDependencies = new HashMap<Integer, ListOrderedSet<Integer>>();

    private OrderedMap<String, FieldObject> fieldLookup = new ListOrderedMap<String, FieldObject>();
    private Map<Integer, FieldObject> fieldIndexLookup = new HashMap<Integer, FieldObject>();

    private Map<Integer, FieldObject> preprocessedTableFields;
    private Vector<String> preDefinedHeaders;

    private Map<String, OntologyTerm> referencedOntologyTerms;
    private Map<String, FieldObject> missingFields;

    public TableReferenceObject(TableConfiguration tableConfig) {
        this.tableConfig = tableConfig;
        this.tableName = tableConfig.getTableName();

        missingFields = new HashMap<String, FieldObject>();
        referencedOntologyTerms = new HashMap<String, OntologyTerm>();

        referenceData = new ReferenceData();

        for (FieldObject fo : tableConfig.getFields()) {
            fieldLookup.put(fo.getFieldName(), fo);
            fieldIndexLookup.put(fo.getColNo(), fo);
        }
    }

    public List<Protocol> constructProtocolObjects() {
        // we want to go through the array, building up lists of protocols and their parameters and units taking note
        // of not only the protocols parameters and units in existence, but recording dependencies based on this information also!
        List<Protocol> protocols = new ArrayList<Protocol>();
        String[] columns = getTableColumns();

        StringBuffer protocolParameters = new StringBuffer();
        int count = 0;
        int prevParameterIndex = -1;
        int currentProtocolIndex = -1;
        for (String column : columns) {
            // build up string representation of protocol definitions, then process and add them to a protocol object
            // for addition into the protocols to add List!
            if (column.equals(GeneralFieldTypes.PROTOCOL_REF.name)) {

                if (!protocolParameters.toString().equals("")) {
                    // construct a new protocol by adding what is in found.
                    addProtocol(protocols, protocolParameters.toString(), currentProtocolIndex);
                    protocolParameters = new StringBuffer();
                }

                currentProtocolIndex = count;
                protocolParameters.append(column).append(",");

            } else if (column.contains("Parameter")) {
                protocolParameters.append(column).append(",");
                prevParameterIndex = count;
            } else if (column.equals(GeneralFieldTypes.UNIT.name)) {
                if (!protocolParameters.toString().equals("")) {
                    protocolParameters.append(column).append(";");
                    if (prevParameterIndex != -1) {
                        prevParameterIndex = -1;
                    }
                }
            } else {
                // end of protocol definition
                // construct a new protocol by adding what is in found :o)
                if (!protocolParameters.toString().equals("")) {
                    addProtocol(protocols, protocolParameters.toString(), currentProtocolIndex);
                    currentProtocolIndex = -1;
                    protocolParameters = new StringBuffer();
                }
            }
            count++;
        }

        // if a parameter was added at the end, it will be detected here!
        if (!protocolParameters.toString().equals("")) {
            addProtocol(protocols, protocolParameters.toString(), currentProtocolIndex);
        }

        return protocols;
    }

    public List<Factor> constructFactorObjects() {
        // we want to go through the array, building up lists of protocols and their parameters and units taking note
        // of not only the protocols parameters and units in existence, but recording dependencies based on this information
        // also!
        List<Factor> factors = new ArrayList<Factor>();
        Vector<String> columns = getHeaders();
        for (String c : columns) {
            // build up string representation of protocol definitions, then process and add them to a protocol object
            // for addition into the protocols to add List!
            if (c.contains(GeneralFieldTypes.FACTOR_VALUE.name)) {
                // construct a new protocol by adding what is in found.
                String factor = c.substring(c.indexOf("[") + 1, c.lastIndexOf("]"));
                factors.add(new Factor(factor, factor));
            }
        }

        return factors;
    }

    public List<FieldObject> getRecordedFactors() {
        List<FieldObject> factors = new ArrayList<FieldObject>();

        for (String columnName : fieldLookup.keySet()) {
            if (columnName.contains(GeneralFieldTypes.FACTOR_VALUE.name)) {
                factors.add(fieldLookup.get(columnName));
            }
        }

        return factors;
    }


    private void addProtocol(List<Protocol> protocolList, String listOfColumns, int protocolIndex) {
        // process list of columns to determine the protocol and respective parameters.
        if (protocolIndex > 0) {
            FieldObject protocolObject = tableConfig.getFields().get(protocolIndex);

            StringBuilder parameters = new StringBuilder();
            Set<String> parameterSet = extractValue(listOfColumns, ",", "Parameter");
            int count = 0;
            for (String param : parameterSet) {
                if (count == parameterSet.size() - 1) {
                    parameters.append(param);
                } else {
                    parameters.append(param).append(";");
                }
            }

            Protocol p = new Protocol(protocolObject.getDefaultVal(), protocolObject.getDefaultVal(), protocolObject.getDescription(), "", "", parameters.toString(), "", "");
            protocolList.add(p);
        }
    }

    private Set<String> extractValue(String listOfColumns, String separator, String lookFor) {
        String[] parts = listOfColumns.split(separator);

        Set<String> parameters = new HashSet<String>();
        for (String p : parts) {
            if (p.contains(lookFor)) {
                if (p.contains("[")) {
                    String parameter = p.substring(p.indexOf("[") + 1, p.lastIndexOf("]"));
                    parameters.add(parameter);
                }
            }
        }

        return parameters;
    }

    private String[] getTableColumns() {
        List<String> finalFields = new ArrayList<String>();
        List<FieldObject> fields = tableConfig.getFields();

        // ensure the correctness of the field order!
        sortFieldsByColumnNumber(fields);

        for (FieldObject fo : fields) {
            if (!fo.isHidden()) {
                finalFields.add(fo.getFieldName());
            } else {
                System.out.println("Not adding " + fo.getFieldName());
            }
        }

        return finalFields.toArray(new String[finalFields.size()]);
    }

    public Map<Integer, String[]> getTableStructure() {
        return tableConfig.getTableStructure();
    }

    public Map<String, FieldObject> getMissingFields() {
        if (missingFields != null) {
            getMultipleInstanceFields();
            return Collections.synchronizedMap(missingFields);
        } else {
            return null;
        }
    }

    public void getMultipleInstanceFields() {
        Map<String, FieldObject> fieldsThatMayBeAddedMultipleTimes = new HashMap<String, FieldObject>();
        for (String field : fieldLookup.keySet()) {
            if (acceptsMultipleValues(field)) {
                missingFields.put(field, fieldLookup.get(field));
            }
        }
    }


    public void setMissingFields(Map<String, FieldObject> missingFields) {
        this.missingFields = missingFields;
    }

    public TableReferenceObject(String tableName) {
        this.tableName = tableName;
        referencedOntologyTerms = new HashMap<String, OntologyTerm>();
    }

    public boolean acceptsFileLocations(String colName) {

        FieldObject fieldObject = fieldLookup.get(colName);

        return fieldObject != null && !colName.equals(ROW_NO_TEXT) &&
                fieldLookup.get(colName).isAcceptsFileLocations();
    }


    public boolean acceptsMultipleValues(String colName) {
        FieldObject tfo = fieldLookup.get(colName);
        return tfo != null && tfo.isAcceptsMultipleValues();
    }

    public boolean forceOntology(String colName) {
        FieldObject tfo = fieldLookup.get(colName);
        return tfo != null && tfo.isForceOntologySelection();
    }

    public void addField(FieldObject fo) {
        fieldLookup.put(fo.getFieldName(), fo);
        fieldIndexLookup.put(fo.getColNo(), fo);
    }

    /**
     *
     * Adds the row data.
     *
     * @param headers free text or full annotated headers (e.g. Characteristics[<label>, <URI or term accession>, <ontology abbreviation>)
     * @param rowData
     */
    public void addRowData(String[] headers, String[] rowData) {
        if (referenceData == null) {
            referenceData = new ReferenceData();
        }

        List<String> rowDataModified = new ArrayList<String>();
        int prevValLoc = -1;

        for (int i = 0; i < headers.length; i++) {
            String s;
            if (i < rowData.length) {
                s = rowData[i];
            } else {
                s = "";
            }

            //processing header annotation
            if (headers[i].contains(":")) {

                String header = headers[i];

                OntologyTerm ontologyTerm = OntologyTermUtils.stringToOntologyTerm(header);

                String prevVal = ontologyTerm.getUniqueId(); //the uniqueID is source + ":" + term

                if (!prevVal.equals("") && !referencedOntologyTerms.containsKey(prevVal)) {
                    referencedOntologyTerms.put(prevVal, ontologyTerm);
                }

            }

            //process rows
            if (headers[i].toLowerCase().contains("source ref")) {
                if (!s.equals("")) {
                    String prevVal = rowDataModified.get(prevValLoc);
                    rowDataModified.set(prevValLoc, s + ":" + prevVal);
                }
            } else if (headers[i].toLowerCase().trim().contains("term accession number")) {
                if (!s.equals("")) {
                    String prevVal = rowDataModified.get(prevValLoc);

                    if (prevVal.contains(":")) {
                        String[] parts = prevVal.split(":");

                        if (parts.length > 1) {
                            String source = parts[0];
                            String term = parts[1];
                            String accession = s.trim();

                            if (!referencedOntologyTerms.containsKey(prevVal)) {
                                referencedOntologyTerms.put(prevVal,
                                        new OntologyTerm(term, accession, null, OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source)));
                            }
                        }
                    }
                }
            } else {
                rowDataModified.add(s);
                prevValLoc = rowDataModified.size() - 1;
            }
        }

        referenceData.addData(rowDataModified);
    }

    public DataTypes getClassType(String colName) {
        if ((colName == null) || colName.equals("")) {
            return null;
        } else {
            FieldObject dataType = fieldLookup.get(colName);

            if (dataType != null) {
                return dataType.getDatatype();
            }

            return DataTypes.STRING;
        }
    }

    public Map<Integer, ListOrderedSet<Integer>> getColumnDependencies() {
        return columnDependencies;
    }

    public boolean getColumnEditable(String colName) {
        return !colName.equals(ROW_NO_TEXT);
    }

    // replace with col name. must be unique in any case! And even if it isn't, the datatypes will be the same :o)

    public DataTypes getColumnType(String colName) {
        if (colName.equals(ROW_NO_TEXT)) {
            return DataTypes.INTEGER;
        } else {
            if (fieldLookup.get(colName) != null) {
                return fieldLookup.get(colName).getDatatype();
            }
        }

        return DataTypes.STRING;
    }


    public Object[][] getDataAsArray() {
        List<List<String>> spreadsheetData = referenceData.getData();

        Object[][] ssContents = new Object[spreadsheetData.size() + 1][];

        ssContents[0] = getHeaders().subList(1, getHeaders().size()).toArray(new String[getHeaders().size() - 1]);

        int count = 1;
        for (List<String> rowContent : spreadsheetData) {
            ssContents[count] = rowContent.toArray(new Object[rowContent.size()]);
            count++;
        }

        return ssContents;
    }

    public String getDefaultValue(String colName) {
        return fieldLookup.get(colName).getDefaultVal();
    }

    public String getDefaultValue(int colNumber) {
        if (tableConfig != null) {
            if (preprocessedTableFields == null) {
                createProcessedTableFields();
            }
            FieldObject field = preprocessedTableFields.get(colNumber);
            if (field == null) {
                return "";
            }
            return field.getDefaultVal() == null ? "" : field.getDefaultVal();
        }
        return "";
    }

    public Map<String, OntologyTerm> getReferencedOntologyTerms() {
        return referencedOntologyTerms;
    }

    public FieldObject getFieldByName(String name) {
        return fieldLookup.get(name);
    }

    public Vector<String> getHeaders() {
        if (preDefinedHeaders != null) {
            return getPreDefinedHeaders();
        } else {
            return getStdHeaders();
        }
    }

    /**
     * Method initiates a Map containing actual fields, minus the Structural field elements (called Characteristics & Factors) used
     * for the wizard with column indexes directly relating to the position in the spreadsheet (index starts at 1 since first column
     * is the row number in the spreadsheet)
     */
    private void createProcessedTableFields() {
        preprocessedTableFields = new HashMap<Integer, FieldObject>();

        int count = 1;
        for (FieldObject fo : tableConfig.getFields()) {
            if (!fo.getFieldName().equals("Characteristics") && !fo.getFieldName().equalsIgnoreCase("Factors")) {
                preprocessedTableFields.put(count, fo);
                count++;
            }
        }
    }

    public String[] getListItems(String colName) {

        FieldObject field = fieldLookup.get(colName);
        String[] listItems = new String[]{"no items yet!"};

        if (field != null) {

            String[] tmpItems = fieldLookup.get(colName).getFieldList();

            return tmpItems == null ? listItems : tmpItems;
        } else {
            return listItems;
        }
    }

    public void setFieldListItems(String colName, String[] listItems) {
        FieldObject fo = fieldLookup.get(colName);
        if (fo != null) {
            fo.setFieldList(listItems);
        }
    }

    public String getColumnFormatByName(String name) {
        for (Integer i : getTableStructure().keySet()) {
            String[] val = getTableStructure().get(i);
            if (val[0].equalsIgnoreCase(name)) {
                return val[1];
            }
        }
        return "";
    }

    public Vector<String> getPreDefinedHeaders() {
        return preDefinedHeaders;
    }

    public Map<String, RecommendedOntology> getRecommendedSource(String colName) {
        FieldObject tfo = fieldLookup.get(colName);
        return tfo.getRecommmendedOntologySource();
    }

    public FieldObject getNextUnitField(String colName) {
        // go through fields, find the one in the parameter, and then try and find the next unit for that field.
        FieldObject field = fieldLookup.get(colName);

        if (field != null) {
            int proposedColNumber = field.getColNo() + 1;

            if (tableConfig == null) {
                // for those tables built from their layout rather than directly from the tablerefenceobject
                if (fieldIndexLookup.containsKey(proposedColNumber)) {
                    if (fieldIndexLookup.get(proposedColNumber).getFieldName().equalsIgnoreCase(GeneralFieldTypes.UNIT.name)) {
                        return fieldIndexLookup.get(proposedColNumber);
                    }
                }
            } else {
                if (tableConfig.getFields().size() > proposedColNumber) {
                    FieldObject candidateUnitField = tableConfig.getFields().get(proposedColNumber);

                    if (candidateUnitField.getFieldName().equalsIgnoreCase(GeneralFieldTypes.UNIT.name)) {
                        return candidateUnitField;
                    }
                }
            }
        }
        return null;
    }

    public Vector<String> getStdHeaders() {

        List<FieldObject> fields = new ArrayList<FieldObject>();
        Vector<String> headers = new Vector<String>();
        headers.add(ROW_NO_TEXT);

        if (getTableFields() == null) {
            for (String key : fieldLookup.keySet()) {
                fields.add(fieldLookup.get(key));
            }
        } else {
            fields = getTableFields().getFields();
        }

        sortFieldsByColumnNumber(fields);

        for (FieldObject sortedField : fields) {
            if (!sortedField.isHidden()) {
                headers.add(sortedField.getFieldName());
            }
        }

        return headers;
    }

    public void sortFieldsByColumnNumber(List<FieldObject> fields) {
        Collections.sort(fields,
                new Comparator<FieldObject>() {
                    public int compare(FieldObject field1, FieldObject field2) {
                        if (field1.getColNo() < field2.getColNo()) {
                            return -1;
                        }
                        if (field1.getColNo() > field2.getColNo()) {
                            return 1;
                        }
                        return 0;
                    }
                }
        );
    }

    public TableConfiguration getTableFields() {
        return tableConfig;
    }

    public String getTableName() {
        return tableName;
    }


    public ValidationObject getValidationConstraints(String colName) {
        DataTypes classType;

        FieldObject fo = fieldLookup.get(colName);

        if (fo != null && fo.getDatatype() != null) {
            classType = fo.getDatatype();

            if (classType == DataTypes.STRING) {
                if (fo.getInputFormat() == null) {
                    return null;
                } else {
                    return new StringValidation(fo.getInputFormat(),
                            fo.getDescription());
                }
            }
        }

        return null;
    }

    public void setReferenceData(ReferenceData referenceData) {
        this.referenceData = referenceData;
    }

    public ReferenceData getReferenceData() {
        return referenceData;
    }

    public boolean isRequired(String colName) {
        return colName.equals(ROW_NO_TEXT) || fieldLookup.get(colName).isRequired();
    }

    public boolean isRequired(int columnIndex) {
        return fieldIndexLookup.containsKey(columnIndex) && fieldIndexLookup.get(columnIndex).isRequired();
    }

    public void setPreDefinedHeaders(Vector<String> preDefinedHeaders) {
        this.preDefinedHeaders = preDefinedHeaders;
    }

    public boolean usesValidation(String colName) {
        return fieldLookup.get(colName).getNumberValidation() != null;
    }

    public OrderedMap<String, FieldObject> getFieldLookup() {
        return fieldLookup;
    }

    public Map<Integer, FieldObject> getFieldIndexLookup() {
        return fieldIndexLookup;
    }
}