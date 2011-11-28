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

package org.isatools.isacreator.spreadsheet.transposedview;

import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.gui.formelements.SubFormField;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.utils.TableDataStructureCreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SpreadsheetConverter
 * Converts the Spreadsheet into a TransposedSpreadsheet option for use in the TransposedSpreadsheetView
 * <p/>
 * // todo get basic version working with Ontology support...need to add support for the FileChooser and DateChooser.
 *
 * @author Eamonn Maguire
 * @date Sep 9, 2010
 */


public class SpreadsheetConverter {

    @InjectedResource
    private Color characteristicColor, factorColor, protocolColor, parameterColor, commentColor,
            materialTypeColor, sampleNameColor, defaultColor;

    private Spreadsheet sheet;

    private Map<Integer, Color> rowToColour;

    public SpreadsheetConverter(Spreadsheet sheet) {
        ResourceInjector.get("spreadsheet-package.style").inject(this);

        this.sheet = sheet;
        rowToColour = new HashMap<Integer, Color>();
    }

    public TransposedSpreadsheetModel doConversion() {
        TransposedSpreadsheetModel model = new TransposedSpreadsheetModel(sheet);

        TableDataStructureCreator tableModel = new TableDataStructureCreator(sheet);

        model.setData(tableModel.getDataMatrix());

        model.setFields(generateFieldCharacteristics());

        model.setRowToColour(rowToColour);

        return model;
    }

    private List<SubFormField> generateFieldCharacteristics() {
        int columnCount = sheet.getTable().getColumnCount();

        List<SubFormField> fields = new ArrayList<SubFormField>();

        for (int column = 1; column < columnCount; column++) {
            String colName = sheet.getTable().getColumnName(column);

            rowToColour.put(column - 1, getColorForValue(colName));

            DataTypes dt = sheet.getTableReferenceObject().getColumnType(colName);
            boolean acceptsFiles = sheet.getTableReferenceObject().acceptsFileLocations(colName);

            int fieldType = resolveDataTypeForSubform(dt, acceptsFiles);

            if (fieldType == SubFormField.SINGLE_ONTOLOGY_SELECT || fieldType == SubFormField.MULTIPLE_ONTOLOGY_SELECT) {
                Map<String, RecommendedOntology> recommendedOntologyMap = sheet.getTableReferenceObject().getRecommendedSource(colName);
                if (recommendedOntologyMap != null) {
                    fields.add(new SubFormField(colName, fieldType, recommendedOntologyMap));
                } else {
                    fields.add(new SubFormField(colName, fieldType));
                }
            } else {

                String[] list = sheet.getTableReferenceObject().getListItems(colName);
                if (sheet.getStudyDataEntryEnvironment() != null) {
                    if (colName.startsWith("Protocol")) {
                        list = sheet.getStudyDataEntryEnvironment().getProtocolNames();
                    }
                }
                if (list != null) {
                    fields.add(new SubFormField(colName, fieldType, list));
                } else {
                    fields.add(new SubFormField(colName, fieldType));
                }
            }
        }

        return fields;
    }

    private int resolveDataTypeForSubform(DataTypes dt, boolean acceptsFiles) {
        return acceptsFiles ? SubFormField.FILE : dt == DataTypes.ONTOLOGY_TERM
                ? SubFormField.SINGLE_ONTOLOGY_SELECT :
                dt == DataTypes.LIST ? SubFormField.COMBOLIST : dt == DataTypes.DATE
                        ? SubFormField.DATE : SubFormField.STRING;
    }

    private Color getColorForValue(String value) {

        if (value.contains("Characteristics")) {
            return characteristicColor;
        } else if (value.contains("Factor Value")) {
            return factorColor;
        } else if (value.contains("Protocol REF")) {
            return protocolColor;
        } else if (value.contains("Parameter")) {
            return parameterColor;
        } else if (value.contains("Comment")) {
            return commentColor;
        } else if (value.contains("Material Type")) {
            return materialTypeColor;
        } else if (value.contains("Sample Name")) {
            return sampleNameColor;
        } else {
            return defaultColor;
        }
    }
}
