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

package org.isatools.isacreator.settings;

import org.apache.log4j.Logger;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Properties;

/**
 * @author Eamonn Maguire
 * @date Aug 29, 2009
 */
public abstract class SettingsScreen extends JPanel {

    protected final static Logger log = Logger.getLogger(SettingsScreen.class.getName());

    protected Properties settings;
    protected Properties propertiesOverride;

    protected FlatButton removeButton;

    protected abstract boolean updateSettings();

    protected abstract void performImportLogic();

    protected abstract void performExportLogic();

    protected abstract void performDeletionLogic();

    protected JPanel createControlPanel() {
        final JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));

        final JPanel confirmDeletionContainer = new JPanel();
        confirmDeletionContainer.setLayout(new BoxLayout(confirmDeletionContainer, BoxLayout.LINE_AXIS));
        confirmDeletionContainer.setPreferredSize(new Dimension(130, 30));
        confirmDeletionContainer.setOpaque(false);
        confirmDeletionContainer.setVisible(false);


        FlatButton confirmButton = new FlatButton(ButtonType.RED, "Confirm");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                confirmDeletionContainer.setVisible(false);
                performDeletionLogic();
            }
        });


        FlatButton cancelButton = new FlatButton(ButtonType.GREY, "Cancel");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                confirmDeletionContainer.setVisible(false);
            }
        });

        confirmDeletionContainer.add(cancelButton);
        confirmDeletionContainer.add(confirmButton);


        // add main buttons!
        JPanel mainButtonContainer = new JPanel();
        mainButtonContainer.setLayout(new BoxLayout(mainButtonContainer, BoxLayout.LINE_AXIS));
        mainButtonContainer.setPreferredSize(new Dimension(130, 30));

        FlatButton exportButton = new FlatButton(ButtonType.GREEN, "Export");
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                performExportLogic();
            }
        });

        FlatButton importButton = new FlatButton(ButtonType.GREEN, "Import");
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                performImportLogic();
            }
        });

        removeButton = new FlatButton(ButtonType.RED, "Remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                confirmDeletionContainer.setVisible(true);
            }
        });


        removeButton.setVisible(false);

        mainButtonContainer.add(exportButton);
        mainButtonContainer.add(Box.createHorizontalStrut(5));
        mainButtonContainer.add(importButton);
        mainButtonContainer.add(Box.createHorizontalStrut(5));
        mainButtonContainer.add(removeButton);

        controlPanel.add(mainButtonContainer);
        controlPanel.add(confirmDeletionContainer);

        return controlPanel;
    }

    protected String getCSS() {

        return "<style type=\"text/css\">" +
                "<!--" + ".title {" +
                "   font-family: Verdana;" + "   font-weight:bold;" + "   font-size: 8px;" +
                "   color: #8DC63F;" +
                "}" +
                ".info {" +
                "   font-family: Verdana;   font-weight:bold;" + "   font-size: 8px;" +
                "   color: #333333;" +
                "}" +
                ".info_text {" +
                "   font-family: Verdana;   font-weight:normal;" + "   font-size: 8px;" +
                "   color: #333333;" +
                "}" +
                "-->" + "</style>";
    }


}
