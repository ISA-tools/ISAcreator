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

package org.isatools.isacreator.effects;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * CustomListCellRenderer
 *
 * @author Eamonn Maguire
 * @date Feb 9, 2010
 */


public class SingleSelectionListCellRenderer extends JComponent implements ListCellRenderer {

    public static final ImageIcon SELECTED_IMAGE = new ImageIcon(SingleSelectionListCellRenderer.class.getResource("/images/ontologyselectiontool/item_selected.png"));
    public static final ImageIcon UNSELECTED_IMAGE = new ImageIcon(SingleSelectionListCellRenderer.class.getResource("/images/ontologyselectiontool/item_unselected.png"));
    public static final Color SELECTED_COLOR = UIHelper.LIGHT_GREEN_COLOR;
    public static final Color UNSELECTED_COLOR = UIHelper.BG_COLOR;

    private DefaultListCellRenderer listCellRenderer;

    public SingleSelectionListCellRenderer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new SpecialListImage(), BorderLayout.WEST);

        listCellRenderer = new DefaultListCellRenderer();
        add(listCellRenderer, BorderLayout.CENTER);

        setBorder(new EmptyBorder(2, 2, 2, 2));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {
        listCellRenderer.getListCellRendererComponent(list, value, index,
                selected, cellGotFocus);
        listCellRenderer.setBorder(null);

        //image.checkIsIdEntered(selected);
        Component[] components = getComponents();

        for (Component c : components) {
            ((JComponent) c).setToolTipText(value.toString());
            if (c instanceof SpecialListImage) {
                ((SpecialListImage) c).setSelected(selected);
            } else {
                if (selected) {
                    c.setBackground(SELECTED_COLOR);
                    c.setFont(UIHelper.VER_10_BOLD);
                    c.setForeground(UIHelper.BG_COLOR);
                } else {
                    c.setBackground(UNSELECTED_COLOR);
                    c.setFont(UIHelper.VER_10_BOLD);
                    c.setForeground(UIHelper.DARK_GREEN_COLOR);
                }
            }
        }

        return this;
    }

    class SpecialListImage extends JPanel {
        // this will contain the general panel layout for the list item and modifier elements to allow for changing of images
        // when rendering an item as being selected and so forth.
        private JLabel itemSelectedIndicator;

        SpecialListImage() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            itemSelectedIndicator = new JLabel(UNSELECTED_IMAGE);

            add(itemSelectedIndicator);
            add(Box.createHorizontalStrut(2));
        }

        public void setSelected(boolean selected) {
            if (selected) {
                itemSelectedIndicator.setIcon(SELECTED_IMAGE);
            } else {
                itemSelectedIndicator.setIcon(UNSELECTED_IMAGE);
            }
        }
    }
}
