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

package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.configuration.ValidationTypes;

/**
 * Holds the value(s) required to provide validation on a numeric field.
 *
 * @author Eamonn Maguire
 * @date May 12, 2008
 */
public class NumberValidation<T extends Number> implements ValidationObject {
    private T val1 = null;
    private T val2 = null;
    private String dataType;
    private ValidationTypes validationType;

    /**
     * NumberValidation object.
     *
     * @param val1           - first value of range a value should fall between, or a pivot value which a value should
     *                       be greater than, equal to, less than, etc.
     * @param val2           upper level of a range.
     * @param validationType - e.g. between, greater than or equal to, and so forth.
     * @param dataType       - e.g. double, integer.
     */
    public NumberValidation(T val1, T val2, ValidationTypes validationType,
                            String dataType) {
        this.validationType = validationType;
        this.dataType = dataType;

        if (val1 != null) {
            this.val1 = val1;

            if (val2 != null) {
                this.val2 = val2;
            }
        }
    }

    public String getDataType() {
        return dataType;
    }

    public T getVal1() {
        return val1;
    }

    public T getVal2() {
        return val2;
    }

    public String toString() {
        if (validationType == ValidationTypes.BETWEEN) {
            return "Number should be between " + val1 + " and " + val2;
        }

        if (validationType != null) {
            return "Number should be " + validationType + " " + val1;
        }

        return "";
    }
}
