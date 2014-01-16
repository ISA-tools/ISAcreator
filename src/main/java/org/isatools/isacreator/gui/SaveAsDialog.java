/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 <<<<<<< HEAD
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
 =======
 The contents of this file are subject to the CPAL version 1.0 (the License);
 >>>>>>> 9eb576c237c7bdc3ffbf61636aa76f3cae3d7fb9
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 <<<<<<< HEAD
 Software distributed under the License is distributed on an �AS IS� basis,
 =======
 Software distributed under the License is distributed on an AS IS basis,
 >>>>>>> 9eb576c237c7bdc3ffbf61636aa76f3cae3d7fb9
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

package org.isatools.isacreator.gui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;


/**
 * Presents opportunity for user to give a new name for a submission. Will create a new folder in Submissions directory.
 *
 * @author Eamonn Maguire
 * @date Sep 24, 2008
 */
public class SaveAsDialog extends JDialog {

    private JLabel status;
    private RoundedJTextField fileName;

    @InjectedResource
    private ImageIcon dialogHeader;

    public SaveAsDialog() {
        ResourceInjector.get("gui-package.style").inject(this);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        instantiatePanel();

        pack();
    }

    private void instantiatePanel() {
        createAndAddNorthPanel();
        createAndAddCenterPanel();
        createAndAddSouthPanel();


    }

    private void createAndAddNorthPanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        topPanel.setBackground(UIHelper.BG_COLOR);

        JLabel saveAsLab = new JLabel(dialogHeader);
        saveAsLab.setBackground(UIHelper.BG_COLOR);

        topPanel.add(saveAsLab);

        add(topPanel, BorderLayout.NORTH);
    }

    private void createAndAddCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setBackground(UIHelper.BG_COLOR);
        centerPanel.setBorder(new EmptyBorder(5, 20, 5, 20));

        JPanel fileNamePanel = new JPanel(new BorderLayout());
        fileNamePanel.setOpaque(false);

        final String defaultText = " Please enter a directory name...";

        fileName = new RoundedJTextField(20, new Color(241,242,241));
        fileName.setText(defaultText);
        fileName.setBackground(UIHelper.BG_COLOR);
        UIHelper.renderComponent(fileName, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        fileName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if(fileName.getText().equals(defaultText)) {
                    fileName.setText("");
                }
            }
        });

        fileNamePanel.add(fileName, BorderLayout.CENTER);

        centerPanel.add(fileNamePanel);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(UIHelper.BG_COLOR);
        status = new JLabel("<html></html>");
        status.setPreferredSize(new Dimension(350, 30));
        UIHelper.renderComponent(status, UIHelper.VER_12_BOLD, UIHelper.RED_COLOR, false);
        statusPanel.add(status, BorderLayout.CENTER);

        centerPanel.add(statusPanel);

        Action saveAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save(fileName.getText().trim());
            }
        };

        fileName.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SAVE");
        fileName.getActionMap().put("SAVE", saveAction);

        add(centerPanel, BorderLayout.CENTER);
    }

    private void createAndAddSouthPanel() {
        // setup south panel with buttons and so forth :o)
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(UIHelper.EMPTY_BORDER);
        southPanel.setBackground(UIHelper.BG_COLOR);

        JButton close = new FlatButton(ButtonType.RED, "Cancel");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                firePropertyChange("windowClosed", "", "none");
            }
        });


        JButton save = new FlatButton(ButtonType.GREEN, "Save");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                save(fileName.getText().trim());
            }
        });

        southPanel.add(close, BorderLayout.WEST);
        southPanel.add(save, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    public void save(String fileName) {
        if (!fileName.equals("") && !fileName.contains("Please enter a directory name...")) {
            if (fileName.contains("/")) {
                status.setText("<html><b>Invalid file name<: / are not allowed in directory names</b></html>");
            } else {
                File f = new File(ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator +
                        fileName.replace("/", "_"));

                if (!f.exists()) {
                    firePropertyChange("save", "", fileName);
                } else {
                    status.setText(
                            "<html><b>directory name already exists in \"" + ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY + "\" folder</b></html>");
                }
            }
        } else {
            status.setText("<html><b>Invalid directory name</b></html>");
        }
    }
}
