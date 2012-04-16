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

import org.apache.log4j.Logger;
import org.isatools.isacreator.configuration.DataTypes;

import java.util.Date;

public class SpreadsheetCell {

    private static final Logger log = Logger.getLogger(SpreadsheetCell.class.getName());

    public static final int TEXT = 0;
    public static final int NUMBER = 1;

    private Object value;

    public SpreadsheetCell(Object datum) {
        value = datum;
    }

    public boolean isEmpty() {
        return value.equals("");
    }

    public int getType() {

        if (value instanceof Number) {
            return SpreadsheetCell.NUMBER;
        }

        return SpreadsheetCell.TEXT;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        try {
            return value == null ? "" : value.toString();
        } catch (Exception e) {
            return "";
        }

    }

    void setData(Object datum) {
        value = datum;
    }


    void setValue(Object datum) {
        value = datum;
    }

    /**
     * This is a method similar to compareTo except that it is not
     * consistent with the equals method. It is used for sorting.
     * If two cells are blank or errors
     * then they are "equal" by this compare method. Numbers, Dates and
     * Strings are compared the usual way.
     *
     * @param x cell to compare this to
     * @return -1 if this < x, 0 if this = x, 1 if this > x
     */
    int compare(SpreadsheetCell x, DataTypes type, boolean ascending) {
        //if this is blank which is lowest value

        if (this.value == null) {
            //x is not blank so this is less than
            if (x.value != null) {
                if (ascending) {
                    return 1;
                } else {
                    return -1;
                }
            } else { //otherwise equal

                return 0;
            }
        } else {

            if (x.value == null) {
                if (ascending) {
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        if (this.value.equals("")) {
            //x is not blank so this is less than
            if (!x.value.equals("")) {
                if (ascending) {
                    return 1;
                } else {
                    return -1;
                }
            } else { //otherwise equal

                return 0;
            }
        } else {

            if (x.value.equals("")) {
                if (ascending) {
                    return -1;
                } else {
                    return 1;
                }
            } else { //both have values
                log.info("Data types class: " + type.getClass());
                if (type == DataTypes.STRING) {
                    // even though the class may be a string, it is highly likely that the value contained
                    // is a numeric one due to generalities! to counteract this, we try to do a cast!

                    try {
                        if (value.getClass() == Float.class) {
                            log.info("Comparing on float values.");
                            return ((Float) value).compareTo((Float) x.value);
                        } else {
                            log.info("Comparing on String values.");
                            return ((String) value).compareToIgnoreCase((String) x.value);
                        }
                    } catch (ClassCastException cce) {
                        return String.valueOf(value).compareTo(String.valueOf(x.value));
                    }
                } else if (type == DataTypes.DOUBLE) {
                    try {
                        return Double.valueOf(value.toString()).compareTo(Double.valueOf(x.value.toString()));
                    } catch (NumberFormatException e) {
                        return String.valueOf(value).compareTo(String.valueOf(x.value));
                    }
                } else if (type == DataTypes.INTEGER) {
                    try {
                        return Integer.valueOf(value.toString()).compareTo(Integer.valueOf(x.value.toString()));
                    } catch (NumberFormatException e) {
                        return String.valueOf(value).compareTo(String.valueOf(x.value));
                    }
                } else if (type == DataTypes.DATE) {
                    try {
                        return ((Date) value).compareTo((Date) x.value);
                    } catch (Exception e) {
                        return ((String) value).compareTo((String) x.value);
                    }


                } else {
                    log.info("Performing default String comparison.");
                    String v1AsString = String.valueOf(value);
                    String v2AsString = String.valueOf(x.value);
                    return v1AsString.compareToIgnoreCase(v2AsString);
                }
            }
        }

    }
}

 