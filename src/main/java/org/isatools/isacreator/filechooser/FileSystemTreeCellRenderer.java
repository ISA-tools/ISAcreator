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

package org.isatools.isacreator.filechooser;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.io.File;
import java.io.IOException;


/**
 * Provides a custom renderer for the JTree to allow for nodes of different
 * types to be displayed in different ways in the tree
 */
public class FileSystemTreeCellRenderer extends DefaultTreeCellRenderer {

    @InjectedResource
    private ImageIcon openedGroupItem, closedGroupItem, openedDirectory, closedDirectory, unknownFileType;

    private JPanel contents;
    private JLabel icon;
    private JLabel text;

    public FileSystemTreeCellRenderer() {

        ResourceInjector.get("filechooser-package.style").inject(this);

        contents = new JPanel();
        contents.setLayout(new BoxLayout(contents, BoxLayout.LINE_AXIS));
        contents.setOpaque(false);

        icon = new JLabel();
        text = UIHelper.createLabel("", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);
//        text.setMinimumSize(new Dimension(170, 20));

        contents.add(icon);
        contents.add(text);

    }

    public Component getTreeCellRendererComponent(JTree jTree, Object o,
                                                  boolean selected, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        FileBrowserTreeNode node = (FileBrowserTreeNode) o;
        switch (node.getType()) {
            case FileBrowserTreeNode.ITEM_LIST:
                if (expanded) {
                    icon.setIcon(openedGroupItem);
                } else {
                    icon.setIcon(closedGroupItem);
                }

                break;

            case FileBrowserTreeNode.FILE_TYPE:
                icon.setIcon(unknownFileType);
                break;

            case FileBrowserTreeNode.DIRECTORY:
                if (expanded) {
                    icon.setIcon(openedDirectory);
                } else {
                    icon.setIcon(closedDirectory);
                }

                break;
        }

        text.setText(o.toString());

        if (selected) {
            text.setForeground(UIHelper.LIGHT_GREEN_COLOR);
        } else {
            text.setForeground(UIHelper.DARK_GREEN_COLOR);
        }

        return contents;
    }


}
