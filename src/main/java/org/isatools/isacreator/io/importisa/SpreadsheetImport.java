package org.isatools.isacreator.io.importisa;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.utils.GeneralUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 * Utility class to deal with import of Spreadsheet files into the system.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class SpreadsheetImport {


    private Set<String> messages;

    /**
     * Create tablemodel for item!
     *
     * @param fileName        - Name of file to load
     * @param defaultTableRef - TableReferenceObject to be used to load in file
     * @return TableReferenceObject describing the table
     * @throws java.io.IOException when file does not exist or if the CSVReader cannot read the next line
     * @throws org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException
     *                             - when a problem is found when reading in file.
     */
    public TableReferenceObject loadInTables(String fileName,
                                             TableReferenceObject defaultTableRef) throws IOException, MalformedInvestigationException {
        messages = new HashSet<String>();

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
            throw new FileNotFoundException("<p>The file " + fileName + " was not found. Please ensure that the file exists within " +
                    "the folder and that the name referred to in the investigation file is correct!</p>");
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
        System.out.println("Reforming table definition...");
        // way of storing previously read characteristic, factor, or parameter to determine what type it is
        String previousCharFactParam = null;
        int expectedNextUnitLocation = -1;
        int count = 0;
        int positionInheaders = 0;
        int parentColPos;

        for (String columnHeader : headers) {
            positionInheaders++;

            String fieldAsLowercase = columnHeader.toLowerCase();

            System.out.println("Column header is " + columnHeader);

            if (expectedNextUnitLocation == positionInheaders) {
                System.out.println("Expected a unit here, got a " + columnHeader);
                if (fieldAsLowercase.contains("unit")) {
                    // add two fields...one accepting string values and the unit, also accepting string values :o)

                    FieldObject newFo = startReference.getFieldByName(previousCharFactParam);
                    if (newFo == null) {
                        newFo = new FieldObject(count,
                                previousCharFactParam, "", DataTypes.STRING, "", false, false, false);
                    }

                    tro.addField(newFo);

                    if (tro.getColumnDependencies().get(count) == null) {
                        tro.getColumnDependencies()
                                .put(count, new ListOrderedSet<Integer>());
                    }

                    parentColPos = count;

                    count++;

                    // get the unit for this factor.
                    newFo = startReference.getNextUnitField(previousCharFactParam);

                    if (newFo == null) {
                        newFo = new FieldObject(count, columnHeader, "", DataTypes.ONTOLOGY_TERM, "", false, false, false);
                    }
                    tro.addField(newFo);

                    tro.getColumnDependencies().get(parentColPos).add(count);

                    count++;


                    // AND ATTACH UNIT TO FIELD VIA THE MAPPING IN THE TABLE CLASS
                } else {
                    // add a field accepting ontology terms
                    FieldObject newFo = startReference.getFieldByName(previousCharFactParam);

                    if (newFo == null) {
                        newFo = new FieldObject(count,
                                previousCharFactParam, "", DataTypes.ONTOLOGY_TERM, "",
                                false, false, false);
                    }
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

            FieldObject field = startReference.getFieldByName(columnHeader);

            if (field != null) {


                if ((fieldAsLowercase.contains("factor value") ||
                        fieldAsLowercase.contains("characteristics") ||
                        fieldAsLowercase.contains("parameter value")) && !fieldAsLowercase.contains("comment")) {

                    previousCharFactParam = columnHeader;
                    expectedNextUnitLocation = positionInheaders + 1;
                } else {
                    tro.addField(field);
                }

                count++;
            } else {
                if ((fieldAsLowercase.contains("factor value") ||
                        fieldAsLowercase.contains("characteristics") ||
                        fieldAsLowercase.contains("parameter value")) && !fieldAsLowercase.contains("comment")) {

                    previousCharFactParam = columnHeader;
                    expectedNextUnitLocation = positionInheaders + 1;
                }
                if (fieldAsLowercase.equals("performer") ||
                        fieldAsLowercase.contains("comment") ||
                        fieldAsLowercase.equals("provider")) {
                    FieldObject additionalFo = new FieldObject(count, columnHeader,
                            "An additional column", DataTypes.STRING, "", false,
                            false, false);
                    tro.addField(additionalFo);
                    count++;
                }

                if (fieldAsLowercase.contains("material type")) {
                    FieldObject newFo = new FieldObject(count,
                            columnHeader, "", DataTypes.ONTOLOGY_TERM, "",
                            false, false, false);
                    tro.addField(newFo);
                    count++;
                }

                if (fieldAsLowercase.contains("date")) {
                    FieldObject dateFo = new FieldObject(count, columnHeader,
                            "Date field", DataTypes.DATE, "", false, false, false);
                    tro.addField(dateFo);

                    count++;
                }

                if (fieldAsLowercase.contains("protocol ref")) {
                    previousProtocol = count;

                    FieldObject newFo = new FieldObject(count, "Protocol REF",
                            "A reference to a protocol", DataTypes.LIST, "", false,
                            false, false);
                    tro.addField(newFo);
                    count++;
                }
            }
        }

        if (expectedNextUnitLocation != -1) {
            // add last factor/characteristic to the table

            FieldObject newFo = startReference.getFieldByName(previousCharFactParam);
            if (newFo == null) {
                newFo = new FieldObject(count,
                        previousCharFactParam, "", DataTypes.ONTOLOGY_TERM, "", false, false, false);
            }
            tro.addField(newFo);
        }

        tro.setMissingFields(GeneralUtils.findMissingFields(headers, startReference));

        Set<String> invalidHeaders = findInvalidFields(headers, tro);

        if (invalidHeaders.size() > 0) {

            String invalidHeaderNames = "";

            int headerCount = invalidHeaders.size();
            for (String s : invalidHeaders) {
                invalidHeaderNames += s;
                if (headerCount < invalidHeaders.size() - 1) {
                    invalidHeaderNames += ", ";
                }
                headerCount++;
            }

            String colText = invalidHeaders.size() > 1 ? invalidHeaders.size() + "The columns" : "The column ";
            String linkText = invalidHeaders.size() > 1 ? invalidHeaders.size() + " are " : " is ";

            throw new MalformedInvestigationException(colText + invalidHeaderNames + linkText + " not supported in this assay");
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

}