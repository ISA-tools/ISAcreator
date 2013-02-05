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

package org.isatools.isacreator.model;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.StudySubData;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;


/**
 * Assay object
 *
 * @author Eamonn Maguire
 */
public class Assay extends ISASection implements StudySubData {

    public static final String ASSAY_REFERENCE = "Study Assay File Name";
    public static final String MEASUREMENT_ENDPOINT = "Study Assay Measurement Type";
    public static final String MEASUREMENT_ENDPOINT_TERM_ACCESSION = "Study Assay Measurement Type Term Accession Number";
    public static final String MEASUREMENT_ENDPOINT_TERM_SOURCE_REF = "Study Assay Measurement Type Term Source REF";
    public static final String TECHNOLOGY_TYPE = "Study Assay Technology Type";
    public static final String TECHNOLOGY_TYPE_TERM_ACCESSION = "Study Assay Technology Type Term Accession Number";
    public static final String TECHNOLOGY_TYPE_TERM_SOURCE_REF = "Study Assay Technology Type Term Source REF";
    public static final String ASSAY_PLATFORM = "Study Assay Technology Platform";

    private TableReferenceObject tableReferenceObject = null;

    public Assay() {
        this("", null);
    }

    /**
     * The Assay object represents the Spreadsheet/Matrix defining the processes involved
     * to get data files from a Sample
     *
     * @param assayReference       - A Reference to be given to the Assay
     * @param tableReferenceObject - the Table Reference Object which contains all the properties of a an Assay, in particular defining which Columns are required, Which are Ontology terms, etc. These files are provided via the ISAConfiguration Tool.
     */
    public Assay(String assayReference, TableReferenceObject tableReferenceObject) {
        this(assayReference, "", "", "", tableReferenceObject);
    }

    /**
     * The Assay object represents the Spreadsheet/Matrix defining the processes involved
     * to get data files from a Sample
     *
     * @param assayReference      - A Reference to be given to the Assay
     * @param measurementEndpoint - the measurement being made in this Assay (e.g. transcription profiling)
     * @param technologyType      - the technology being used to make the measurement in this Assay (e.g. DNA microarray)
     * @param assayPlatform       - the technology platform used (e.g. Agilent, Affymetrix, Bruker, etc.)
     */
    public Assay(String assayReference, String measurementEndpoint,
                 String technologyType, String assayPlatform) {

        this(assayReference, measurementEndpoint, technologyType, assayPlatform,
                new TableReferenceObject(ConfigurationManager.selectTROForUserSelection(
                        measurementEndpoint, technologyType).getTableFields()));
    }


    /**
     * The Assay object represents the Spreadsheet/Matrix defining the processes involved
     * to get data files from a Sample
     *
     * @param assayReference       - A Reference to be given to the Assay
     * @param measurementEndpoint  - the measurement being made in this Assay (e.g. transcription profiling)
     * @param technologyType       - the technology being used to make the measurement in this Assay (e.g. DNA microarray)
     * @param assayPlatform        - the technology platform used (e.g. Agilent, Affymetrix, Bruker, etc.)
     * @param tableReferenceObject - the Table Reference Object which contains all the properties of a an Assay, in particular defining which Columns are required, Which are Ontology terms, etc. These files are provided via the ISAConfiguration Tool.
     */
    public Assay(String assayReference, String measurementEndpoint,
                 String technologyType, String assayPlatform, TableReferenceObject tableReferenceObject) {
        super();

        this.tableReferenceObject = tableReferenceObject;

        MappingObject mappingObject = ConfigurationManager.getMappingObjectForMeasurementAndTechnology(measurementEndpoint, technologyType);

        fieldValues.put(ASSAY_REFERENCE, assayReference);
        fieldValues.put(MEASUREMENT_ENDPOINT, measurementEndpoint);
        fieldValues.put(MEASUREMENT_ENDPOINT_TERM_ACCESSION, mappingObject!=null? mappingObject.getMeasurementAccession() : "");
        fieldValues.put(MEASUREMENT_ENDPOINT_TERM_SOURCE_REF, mappingObject!=null? mappingObject.getMeasurementSource() : "");
        fieldValues.put(TECHNOLOGY_TYPE, technologyType);
        fieldValues.put(TECHNOLOGY_TYPE_TERM_ACCESSION, mappingObject!=null? mappingObject.getTechnologyAccession(): "");
        fieldValues.put(TECHNOLOGY_TYPE_TERM_SOURCE_REF, mappingObject!=null? mappingObject.getTechnologySource(): "" );
        fieldValues.put(ASSAY_PLATFORM, assayPlatform);
    }


    public String getAssayPlatform() {
        return getValue(ASSAY_PLATFORM);
    }

    public String getAssayReference() {
        return getValue(ASSAY_REFERENCE);
    }

    public void setAssayReference(String newReference) {
        fieldValues.put(ASSAY_REFERENCE, newReference);
    }

    public String getIdentifier() {
        return getAssayReference();
    }

    public String getMeasurementEndpoint() {
        return getValue(MEASUREMENT_ENDPOINT);
    }

    public String getTechnologyType() {
        return getValue(TECHNOLOGY_TYPE);
    }

    public TableReferenceObject getTableReferenceObject() {
        return tableReferenceObject;
    }

    /**
     * Returns a 2D Array representation of the Data contained inside this Assay.
     *
     * @return Object[][] containing all the data contained in this Assay. Top Most row (Object[0] will contain all the headers for the Assay.)
     */
    public Object[][] getAssayDataMatrix() {
        return tableReferenceObject.getDataAsArray();
    }

    public void setTableReferenceObject(TableReferenceObject tro) {
        this.tableReferenceObject = tro;
    }

    /**
     * Retrieves the value of "Study Assay File Name"
     * @return
     */
    public String toString() {
        return getValue(ASSAY_REFERENCE);
    }

}
