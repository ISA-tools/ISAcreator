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

package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class SpreadsheetCellRenderer extends DefaultTableCellRenderer {

    private Set<Integer> requiredIndices;

    /**
     * Creates a SpreadsheetCellRenderer.
     */
    public SpreadsheetCellRenderer() {
        this(new HashSet<Integer>());
    }

    public SpreadsheetCellRenderer(Set<Integer> requiredIndices) {
        super();
        this.requiredIndices = requiredIndices;
    }

    public Set<Integer> getRequiredIndices() {
        return requiredIndices;
    }

    public void addRequiredIndex(int index) {
        requiredIndices.add(index);
    }

    public void setRequiredIndices(Set<Integer> requiredIndices) {
        this.requiredIndices = requiredIndices;
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
        /* the following is the similar to DefaultTableCellRenderer */
        if (isSelected) {
            super.setForeground(UIHelper.BG_COLOR);
            super.setBackground(new Color(0, 104, 56, 175));
        } else {
            super.setForeground(UIHelper.GREY_COLOR);
            super.setBackground(row % 2 == 0 ? new Color(141, 198, 63, 40) : UIHelper.BG_COLOR);
        }

        if (requiredIndices.contains(column)) {
            super.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
        }

        setFont(UIHelper.VER_11_PLAIN);

        if (hasFocus) {
            setBorder(new DragBorder());
            if (table.isCellEditable(row, column)) {
                super.setForeground(UIHelper.BG_COLOR);
                super.setBackground(UIHelper.DARK_GREEN_COLOR);
            }
        } else {
            setBorder(noFocusBorder);
        }

        setValue(value, isSelected, hasFocus, row, column);

        //DefaulTableCellRenderer code
        // begin optimization to avoid painting background
        Color back = getBackground();
        boolean colorMatch = (back != null) && (back.equals(table.getBackground())) && table.isOpaque();
        setOpaque(!colorMatch);

        return this;
    }


    /**
     * Sets the string for the cell being rendered to <code>value</code>.
     *
     * @param value      the string value for this cell; if value is null
     *                   it sets the text value to an empty string
     * @param hasFocus   whether cell has focus or not
     * @param isSelected whether cell is selected
     * @param row        cell row
     * @param column     cell column
     */
    protected void setValue(Object value, boolean hasFocus, boolean isSelected, int row, int column) {
        if (value instanceof SpreadsheetCell) {

            SpreadsheetCell temp = (SpreadsheetCell) value;
            Object data = temp.getValue();

            if (data instanceof Number) {
                //numbers are right justified
                setHorizontalAlignment(JTextField.RIGHT);
            } else {
                //everything else is left justified
                setHorizontalAlignment(JTextField.LEFT);
            }

            //value to display in table
            setText((data == null) ? "" : data.toString());
        } else {
            //not cell object so render with toString of that object
            setText((value == null) ? "" : value.toString());
        }
    }
}
 
