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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.utils.GeneralUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Map;

public class CustomRowRenderer extends DefaultTableCellRenderer {
    private Map<Integer, Color> rowColors;

    private Font font;
    private boolean fieldNameColumn;

    /**
     * Creates a FactorGroupCellRenderer.
     *
     * @param rowColors - colours rows should be painted in.
     */
    public CustomRowRenderer(Map<Integer, Color> rowColors, Font font) {
        this(rowColors, font, false);

    }

    public CustomRowRenderer(Map<Integer, Color> rowColors, Font font, boolean isFieldNameColumn) {
        super();
        this.rowColors = rowColors;
        this.font = font;
        fieldNameColumn = isFieldNameColumn;
    }

    /**
     * Returns the default table cell renderer.
     *
     * @param table      the <code>JTable</code>
     * @param value      the value to assign to the cell at
     *                   <code>[row, column]</code>
     * @param isSelected true if cell is selected
     * @param hasFocus   true if cell has focus
     * @param row        the row of the cell to render
     * @param column     the column of the cell to render
     * @return the default table cell renderer
     */
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            super.setForeground(UIHelper.BG_COLOR);
            super.setBackground(new Color(0, 104, 56, 175));
        } else {
            super.setForeground(table.getForeground());
            super.setBackground(rowColors.get(row));
        }

        setFont(font);

        if (hasFocus) {
            setBorder(new DragBorder());
            if (table.isCellEditable(row, column)) {
                super.setForeground(UIHelper.BG_COLOR);
                super.setBackground(UIHelper.DARK_GREEN_COLOR);
            }
        } else {
            setBorder(noFocusBorder);
        }

        /* this method has been changed for formula feature */
        setValue(value == null ? "" : value.toString());

        if (fieldNameColumn) {
            boolean shortNames = Boolean.parseBoolean(ISAcreatorProperties.getProperty("useShortNames"));
            if (shortNames && value != null) {
                String shortHeader = GeneralUtils.getShortString(value.toString());
                setValue(shortHeader);
            }
        }

        //DefaulTableCellRenderer code
        // begin optimization to avoid painting background
        Color back = getBackground();
        boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
        setOpaque(!colorMatch);

        // end optimization to aviod painting background
        return this;
    }

}
