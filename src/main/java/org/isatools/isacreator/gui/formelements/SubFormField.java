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

package org.isatools.isacreator.gui.formelements;

import org.isatools.isacreator.configuration.RecommendedOntology;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * SubFormField provides a description of a field which is to be displayed in a SubForm.
 * This allows for fields to be specified with Ontology lookups attached to them, Date Selection,
 * Autofiltering comboboxes, etc.
 *
 * @author Eamonn Maguire
 */
public class SubFormField implements Serializable {
    public static final int STRING = 0;
    public static final int SINGLE_ONTOLOGY_SELECT = 1;
    public static final int MULTIPLE_ONTOLOGY_SELECT = 2;
    public static final int COMBOLIST = 3;
    public static final int DATE = 4;
    public static final int LONG_STRING = 5;
    public static final int FACTOR_LEVEL_UNITS = 6;
    public static final int FILE = 7;
    public static final int POPUP_LIST = 8;

    private String fieldName;
    private Map<String, RecommendedOntology> recommendedOntologyType = null;
    private String[] listValues;
    private int dataType;
    private boolean required;


    public SubFormField(String fieldName, int dataType) {
        this.fieldName = fieldName;
        this.dataType = dataType;
    }


    public SubFormField(String fieldName, int dataType, String[] listValues) {
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.listValues = listValues;
    }

    public SubFormField(String fieldName, int dataType,
                        Map<String, RecommendedOntology> recommendedOntologyType) {
        this.fieldName = fieldName;
        this.dataType = dataType;
        this.recommendedOntologyType = recommendedOntologyType;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public int getDataType() {
        return dataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String[] getListValues() {
        return listValues;
    }

    public Map<String, RecommendedOntology> getRecommendedOntologyType() {
        return recommendedOntologyType;
    }

    public String toString() {
        return fieldName;
    }
}
