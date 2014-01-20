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

package org.isatools.isacreator.qrcode.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.qrcode.logic.QRCode;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.awt.font.FontRenderContext;

/**
 * QRCodeTreeRenderer
 *
 * @author eamonnmaguire
 * @date Oct 20, 2010
 */


public class QRCodeTreeRenderer extends JComponent implements TreeCellRenderer {
    @InjectedResource
    private ImageIcon selectedIcon, notSelectedIcon, rootClosed, rootExpanded;

    private JLabel leftSide;
    private JLabel text;


    public QRCodeTreeRenderer() {

        ResourceInjector.get("qrcode-generator-package.style").inject(this);
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        leftSide = new JLabel(notSelectedIcon);
        leftSide.setVerticalAlignment(SwingConstants.CENTER);
        leftSide.setPreferredSize(new Dimension(25, 25));

        JPanel qrCodeContainer = new JPanel();
        qrCodeContainer.setBackground(UIHelper.BG_COLOR);
        qrCodeContainer.setLayout(new BoxLayout(qrCodeContainer, BoxLayout.PAGE_AXIS));

        qrCodeContainer.add(Box.createVerticalStrut(3));
        qrCodeContainer.add(leftSide);
        qrCodeContainer.add(Box.createVerticalStrut(3));

        add(qrCodeContainer, BorderLayout.WEST);

        text = UIHelper.createLabel("", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);
        add(text, BorderLayout.CENTER);

    }

    /**
     * Sets all list values to have a white background and green foreground if not selected, and
     * a green background and white foreground if selected.
     *
     * @param tree     - List to render
     * @param val      - value of list item being rendered.
     * @param index    - list index for value to be rendered.
     * @param selected - is the value selected?
     * @param hasFocus - has the cell got focus?
     * @return - The CustomListCellRendered Component.
     */
    public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected,
                                                  boolean expanded, boolean leaf, int index, boolean hasFocus) {

        if (leaf) {
            // leaf nodes...
            if (selected) {
                leftSide.setIcon(selectedIcon);
            } else {
                leftSide.setIcon(notSelectedIcon);
            }

            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) val;
            tn.getUserObject();

            if (tn.getUserObject() instanceof QRCode) {
                QRCode code = (QRCode) tn.getUserObject();
                leftSide.setIcon(new ImageIcon(code.getQrCode(25, 25)));
            }

        } else {
            if (expanded) {
                leftSide.setIcon(rootExpanded);
            } else {
                leftSide.setIcon(rootClosed);
            }
        }


        text.setText(val.toString());
        text.setPreferredSize(new Dimension(calculateWidth(val.toString()), 20));

        if (selected) {
            text.setForeground(UIHelper.LIGHT_GREEN_COLOR);
            text.setFont(UIHelper.VER_12_BOLD);
        } else {
            text.setForeground(UIHelper.DARK_GREEN_COLOR);
            text.setFont(UIHelper.VER_12_PLAIN);
        }

        return this;
    }

    private int calculateWidth(String string) {

        Font font = UIHelper.VER_12_BOLD;
        FontRenderContext frc = new FontRenderContext(null, false, false);
        return (int) font.getStringBounds(string, frc).getWidth();
    }
}
