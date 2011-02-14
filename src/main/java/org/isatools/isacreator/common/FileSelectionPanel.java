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


package org.isatools.isacreator.common;

import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Represents a panel which contains a label to tell the user what we're expecting,
 * a jtextfield to hold the selected file (uneditable), and a button to invoke the appearance
 * of the file chooser!
 */

public class FileSelectionPanel extends JPanel {

    public static final int OPEN = 0;
    public static final int SAVE = 1;


    @InjectedResource
    private ImageIcon fileSelect, fileSelectOver;

    private JTextField fileToUse;
    private String text;
    private int operationType;
    private JFileChooser fileChooser;
    private Font textFont;
    private Color textColor;


    public FileSelectionPanel(String text, JFileChooser fileChooser) {
        this(text, fileChooser, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, OPEN);
    }

    public FileSelectionPanel(String text, JFileChooser fileChooser, int operationType) {
        this(text, fileChooser, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, operationType);
    }

    public FileSelectionPanel(String text, JFileChooser fileChooser, Font textFont, Color textColor) {
        this(text, fileChooser, textFont, textColor, OPEN);
    }

    public FileSelectionPanel(String text, JFileChooser fileChooser, Font textFont, Color textColor, int operationType) {
        this.text = text;
        this.operationType = operationType;

        if (fileChooser == null) {
            this.fileChooser = new JFileChooser();
        } else {
            this.fileChooser = fileChooser;
        }

        this.textFont = textFont;
        this.textColor = textColor;

        setLayout(new GridLayout(2, 1));
        setSize(new Dimension(400, 60));

        ResourceInjector.get("common-package.style").inject(this);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

    private void createGUI() {

        add(UIHelper.createLabel(text, textFont, textColor));

        // create field for viewing the file location selected (uneditable) and the label representing a file selection action
        JPanel fileSelectionUtil = new JPanel();
        fileSelectionUtil.setLayout(new BoxLayout(fileSelectionUtil, BoxLayout.LINE_AXIS));

        fileToUse = new RoundedJTextField(10);
        fileToUse.setEditable(false);
        UIHelper.renderComponent(fileToUse, textFont, textColor, false);

        fileSelectionUtil.add(fileToUse);

        final JLabel selectFileButton = new JLabel(fileSelect);
        selectFileButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent event) {
                selectFileButton.setIcon(fileSelectOver);
            }

            public void mouseExited(MouseEvent event) {
                selectFileButton.setIcon(fileSelect);
            }

            public void mousePressed(MouseEvent event) {
                selectFileButton.setIcon(fileSelect);

                if (operationType == OPEN) {
                    if (fileChooser.showOpenDialog(getInstance()) == JFileChooser.APPROVE_OPTION
                            && fileChooser.getSelectedFile() != null) {
                        fileToUse.setText(fileChooser.getSelectedFile().getPath());
                    }
                } else {
                    if (fileChooser.showSaveDialog(getInstance()) == JFileChooser.APPROVE_OPTION
                            && fileChooser.getSelectedFile() != null) {
                        fileToUse.setText(processFilePath(fileChooser.getSelectedFile().getPath()));
                    }
                }
            }
        });
        fileSelectionUtil.add(selectFileButton);
        add(fileSelectionUtil);
    }

    public void setText(String text) {
        if (text != null && fileToUse != null) {
            fileToUse.setText(text);
        }
    }

    /**
     * Processes filename to ensure that it has the right extension
     */
    private String processFilePath(String path) {
        if (!path.contains(".")) {
            return path += ".xml";
        }
        return path;
    }

    public String getSelectedFilePath() {
        return fileToUse.getText();
    }

    private JPanel getInstance() {
        return this;
    }

    public boolean notEmpty() {
        return getSelectedFilePath() != null && !getSelectedFilePath().trim().equals("");

    }

    public boolean checkFileExtensionValid(String... validExtensions) {
        if (getSelectedFilePath().contains(".")) {
            String extension = getSelectedFilePath().substring(getSelectedFilePath().lastIndexOf(".") + 1);

            for (String validExtension : validExtensions) {
                if (validExtension.equalsIgnoreCase(extension)) {
                    return true;
                }
            }
        }

        return false;
    }
}
