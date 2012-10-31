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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.formatmappingutility.ui.MappingBuilderUI;
import org.isatools.isacreator.formatmappingutility.ui.MappingChoice;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * QRCodeBuilderPane - provides the interface to allow users to build up their qr code for each sample row by adding column
 * data in a process much the same as that encountered in the mapping tool.
 *
 * @author eamonnmaguire
 * @date Oct 14, 2010
 */


public class QRCodeBuilderPane extends JPanel {

    @InjectedResource
    private ImageIcon help;

    private String[] columnNames;
    private String[][] spreadsheetDataSnapshot;

    private MappingBuilderUI qrCodeBuilder;

    public QRCodeBuilderPane(String[] columnNames, String[][] spreadsheetDataSnapshot) {
        this.columnNames = columnNames;
        this.spreadsheetDataSnapshot = spreadsheetDataSnapshot;

        ResourceInjector.get("qrcode-generator-package.style").inject(this);
    }

    public void createGUI() {

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        qrCodeBuilder = new MappingBuilderUI(columnNames, null, MappingBuilderUI.VERTICAL_LAYOUT, spreadsheetDataSnapshot);

        container.add(qrCodeBuilder, BorderLayout.NORTH);

        JScrollPane mappingBuilderScroller = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mappingBuilderScroller.setBorder(new EmptyBorder(1, 1, 1, 1));
        mappingBuilderScroller.setPreferredSize(new Dimension(430, 300));
        mappingBuilderScroller.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(mappingBuilderScroller);

        add(mappingBuilderScroller, BorderLayout.CENTER);

        add(UIHelper.wrapComponentInPanel(new JLabel(help)), BorderLayout.WEST);
    }

    public List<MappingChoice> getMappings() {
        return qrCodeBuilder.getMappings();
    }
}
