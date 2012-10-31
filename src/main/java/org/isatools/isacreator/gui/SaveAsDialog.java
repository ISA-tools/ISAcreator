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
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;


/**
 * Presents opportunity for user to give a new name for a submission. Will create a new folder in Submissions directory.
 *
 * @author Eamonn Maguire
 * @date Sep 24, 2008
 */
public class SaveAsDialog extends JDialog {

    private JLabel status;

    @InjectedResource
    private ImageIcon dialogHeader, closeButton, closeButtonOver,
            saveSubmission, saveSubmissionOver;

    public SaveAsDialog() {
        ResourceInjector.get("gui-package.style").inject(this);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);

        instantiatePanel();
        pack();
    }

    private void instantiatePanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        topPanel.setBackground(UIHelper.BG_COLOR);

        JLabel saveAsLab = new JLabel(dialogHeader,
                JLabel.RIGHT);
        saveAsLab.setBackground(UIHelper.BG_COLOR);

        topPanel.add(saveAsLab);

        add(topPanel, BorderLayout.NORTH);

        // setup center panel to contain data entry facility for user.
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setBackground(UIHelper.BG_COLOR);

        JPanel fileNamePanel = new JPanel(new GridLayout(1, 2));
        fileNamePanel.setOpaque(false);

        JLabel fileNameLab = new JLabel("directory name");
        UIHelper.renderComponent(fileNameLab, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        final JTextField fileNameTxt = new JTextField(
                "Please enter a directory name...");
        fileNameTxt.setBackground(UIHelper.BG_COLOR);
        UIHelper.renderComponent(fileNameTxt, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        fileNamePanel.add(fileNameLab);
        fileNamePanel.add(fileNameTxt);

        centerPanel.add(fileNamePanel);

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setBackground(UIHelper.BG_COLOR);

        status = new JLabel();
        UIHelper.renderComponent(status, UIHelper.VER_12_BOLD, UIHelper.RED_COLOR, false);

        statusPanel.add(status);

        centerPanel.add(statusPanel);

        add(centerPanel, BorderLayout.CENTER);

        // setup south panel with buttons and so forth :o)
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel close = new JLabel(closeButton,
                JLabel.LEFT);
        close.setOpaque(false);
        close.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("windowClosed", "", "none");
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeButton);
            }
        });

        Action saveAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save(fileNameTxt.getText().trim());
            }
        };

        fileNameTxt.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SAVE");
        fileNameTxt.getActionMap().put("SAVE", saveAction);

        final JLabel save = new JLabel(saveSubmission,
                JLabel.RIGHT);
        save.setOpaque(false);
        save.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                save(fileNameTxt.getText().trim());
            }

            public void mouseEntered(MouseEvent event) {
                save.setIcon(saveSubmissionOver);
            }

            public void mouseExited(MouseEvent event) {
                save.setIcon(saveSubmission);
            }
        });

        southPanel.add(close, BorderLayout.WEST);
        southPanel.add(save, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    public void save(String fileName) {
        if (!fileName.equals("") && !fileName.contains("Please enter a directory name...") ) {
            if (fileName.contains("/")) {
                status.setText("<html><b>Invalid file name<: / are not allowed in directory names</b></html>");
            }
            else {
            File f = new File(ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator +
                    fileName.replace("/","_"));

            if (!f.exists()) {
                firePropertyChange("save", "", fileName);
            } else {
                status.setText(
                        "<html><b>directory name already exists in \"" + ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY + "\" folder</b></html>");
            }
        }
    }else {
            status.setText("<html><b>Invalid directory name</b></html>");
        }
    }
}
