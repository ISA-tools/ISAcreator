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

package org.isatools.isacreator.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * TableFieldObject contains information from the FieldData interface.
 * Both should be edited in Tandem during modification as one will depend on the other
 *
 * @author Eamonn Maguire
 */
public class TableFieldObject implements Serializable {
    private int colNo;
    private String fieldName;
    private String description;
    private String defaultVal;
    private DataTypes datatype;

    // some general validation constraints!
    private boolean acceptsFileLocations = false;
    private boolean acceptsMultipleValues;
    private boolean isInputFormatted;
    private boolean required;
    private String wizardTemplate = "";

    // contains inputformat + checks on size if required.
    private String inputFormat = null;
    // defines the ontologies and branches to search under!
    private Map<String, RecommendedOntology> recommmendedOntologySource;
    // defines the validation/numeric contraints on fields of datatype integer or double!
    private NumericValidation<? extends Number> numberValidation;
    // list of values to be displayed as options for selection for the user!
    private String[] fieldList = null;

    /**
     * @param fieldName             - Name of Field
     * @param description           - Description of field. Will be used when displaying tool tips/help
     * @param datatype              - The type of data being entered e.g. String
     * @param defaultVal            - The default value for the field
     * @param required              - Is the field required?
     * @param acceptsMultipleValues - Does the field accept multiple values separated by comma's (,)
     * @param acceptsFileLocations  - Does the field Accept a file Location
     */
    public TableFieldObject(String fieldName, String description,
                            DataTypes datatype, String defaultVal, boolean required,
                            boolean acceptsMultipleValues, boolean acceptsFileLocations) {

        this(-1, fieldName, description, datatype, defaultVal, required, acceptsMultipleValues, acceptsFileLocations);
    }


    /**
     * @param colNo                 - Column No for field
     * @param fieldName             - Name of Field
     * @param description           - Description of field. Will be used when displaying tool tips/help
     * @param datatype              - The type of data being entered e.g. String
     * @param defaultVal            - The default value for the field
     * @param required              - Is the field required?
     * @param acceptsMultipleValues - Does the field accept multiple values separated by comma's (,)
     * @param acceptsFileLocations  - Does the field Accept a file Location
     */
    public TableFieldObject(int colNo, String fieldName, String description,
                            DataTypes datatype, String defaultVal, boolean required,
                            boolean acceptsMultipleValues, boolean acceptsFileLocations) {
        this.colNo = colNo;
        this.fieldName = fieldName;
        this.description = description;
        this.datatype = datatype;
        this.defaultVal = defaultVal;
        this.required = required;
        this.acceptsMultipleValues = acceptsMultipleValues;
        this.acceptsFileLocations = acceptsFileLocations;

        recommmendedOntologySource = new HashMap<String, RecommendedOntology>();
    }


    public int getColNo() {
        return colNo;
    }

    public DataTypes getDatatype() {
        return datatype;
    }

    public String getDefaultVal() {
        return defaultVal == null ? "" : defaultVal;
    }

    public String getDescription() {
        return description;
    }

    public String[] getFieldList() {
        return fieldList;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void addRecommendedOntologySource(RecommendedOntology ro) {
        recommmendedOntologySource.put(ro.getOntology().getOntologyDisplayLabel(), ro);
    }

    public Map<String, RecommendedOntology> getRecommmendedOntologySource() {
        return recommmendedOntologySource;
    }

    public boolean isAcceptsFileLocations() {
        return acceptsFileLocations;
    }

    public boolean isAcceptsMultipleValues() {
        return acceptsMultipleValues;
    }

    public boolean isInputFormatted() {
        return isInputFormatted;
    }

    public boolean isRequired() {
        return required;
    }

    public void setFieldList(String[] fieldList) {
        this.fieldList = fieldList;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public void setInputFormatted(boolean inputFormatted) {
        isInputFormatted = inputFormatted;
    }

    public void setRecommmendedOntologySource(Map<String, RecommendedOntology> recommmendedOntologySource) {
        this.recommmendedOntologySource = recommmendedOntologySource;
    }

    public String getWizardTemplate() {
        return wizardTemplate;
    }

    public void setWizardTemplate(String wizardTemplate) {
        this.wizardTemplate = wizardTemplate;
    }

    public NumericValidation<? extends Number> getNumberValidation() {
        return numberValidation;
    }

    public void setNumberValidation(NumericValidation<? extends Number> numberValidation) {
        this.numberValidation = numberValidation;
    }
}