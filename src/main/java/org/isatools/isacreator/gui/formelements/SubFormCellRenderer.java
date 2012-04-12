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

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * SubFormCellRenderer
 *
 * @author Eamonn Maguire
 * @date Jan 11, 2010
 */


public class SubFormCellRenderer extends DefaultTableCellRenderer {
    private Map<Integer, Color> columnColors;

    private Font defaultFont;
    private Color defaultFontColor;
    private Color bgColor;

    /**
     * Creates a SharpCellRenderer.
     */
    public SubFormCellRenderer(Font defaultFont, Color defaultFontColor, Color bgColor) {
        this(defaultFont, defaultFontColor, bgColor, new HashMap<Integer, Color>());
    }

    public SubFormCellRenderer(Font defaultFont, Color defaultFontColor, Color bgColor, Map<Integer, Color> columnColors) {
        this.bgColor = bgColor;
        this.columnColors = columnColors;
        this.defaultFont = defaultFont;
        this.defaultFontColor = defaultFontColor;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        if (columnColors.containsKey(column)) {
            super.setForeground(defaultFontColor);
            super.setBackground(columnColors.get(column));
            super.setFont(new Font(defaultFont.getFontName(), defaultFont.getSize(), Font.BOLD));
        } else if (bgColor != null) {
            super.setBackground(bgColor);
            super.setForeground(defaultFontColor);
        } else {
            if (hasFocus && table.isCellEditable(row, column)) {
                super.setForeground(UIHelper.BG_COLOR);
                super.setBackground(new Color(0, 104, 56, 175));
            } else {

                super.setForeground(defaultFontColor);
                super.setBackground(column % 2 == 1 ? new Color(141, 198, 63, 40) : UIHelper.BG_COLOR);
            }
        }

        setFont(defaultFont);
        setText(value == null ? "" : value.toString());
        return this;
    }
}
