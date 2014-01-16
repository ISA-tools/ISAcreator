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

package org.isatools.isacreator.gui;

import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.managers.ApplicationManager;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AddNodeDialog provides simple interface for which to add a Study to the investigation.
 */
public class AddStudyDialog extends JDialog {

    @InjectedResource
    private ImageIcon addStudyHeader;

    private DataEntryEnvironment dataEntryEnvironment;
    private JButton add, close;
    private JLabel status;
    private JTextField name;
    private String type;

    public AddStudyDialog(DataEntryEnvironment dataEntryEnvironment, String type) {
        this.type = type;
        this.dataEntryEnvironment = dataEntryEnvironment;

        ResourceInjector.get("gui-package.style").inject(this);
    }

    public void createGUI() {
        instantiateFrame();
    }

    private void hideMe() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ApplicationManager.getCurrentApplicationInstance().hideSheet();
                dispose();
            }
        });
    }


    private void instantiateFrame() {
        setBackground(UIHelper.BG_COLOR);
        add(instantiatePanel());
        pack();
    }

    private JPanel instantiatePanel() {

        add(UIHelper.wrapComponentInPanel(new JLabel(addStudyHeader)), BorderLayout.NORTH);

        name = new RoundedJTextField(20, new Color(241, 241, 241));
        name.setText(type + " id");
        name.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent focusEvent) {
                if (name.getText().equals(type + " id")) {
                    name.setText("");
                }
            }
        });
        UIHelper.renderComponent(name, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        Action addStudyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addStudy();
            }
        };

        name.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ADDSTUDY");
        name.getActionMap().put("ADDSTUDY", addStudyAction);

        JPanel buttonCont = createButtonPanel();

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(UIHelper.BG_COLOR);
        status = new JLabel("<html></html>");
        status.setPreferredSize(new Dimension(400, 30));
        UIHelper.renderComponent(status, UIHelper.VER_12_BOLD, UIHelper.RED_COLOR, false);
        statusPanel.add(status, BorderLayout.CENTER);

        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(5, 5, 5, 5));
        container.setBackground(UIHelper.BG_COLOR);

        JPanel nameContainer = new JPanel(new GridLayout(1, 1));
        nameContainer.setOpaque(false);

        nameContainer.setBorder(new EmptyBorder(10, 20, 10, 20));
        nameContainer.add(name);

        container.add(nameContainer, BorderLayout.NORTH);
        container.add(statusPanel, BorderLayout.CENTER);
        container.add(buttonCont, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createButtonPanel() {
        JPanel buttonCont = new JPanel(new BorderLayout());
        buttonCont.setBackground(UIHelper.BG_COLOR);
        buttonCont.setBorder(new EmptyBorder(4,4,4,4));

        add = new FlatButton(ButtonType.GREEN, "Add Study");
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                addStudy();
            }
        });



        close = new FlatButton(ButtonType.RED, "Cancel");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                status.setText("");
                hideMe();
            }
        });

        buttonCont.add(close, BorderLayout.WEST);
        buttonCont.add(add, BorderLayout.EAST);

        return buttonCont;
    }

    //todo: probably should remove close coupling with DataEntryEnvironment.
    private void addStudy() {
        if (!isValidName(name.getText())) {
            status.setText("<html><b>Invalid file name: can not start with COM#|LPT#</b></br> or contain \\<\\>\\/</html>");
        } else if (!dataEntryEnvironment.checkForDuplicateName(name.getText(), "Study")) {

            if (dataEntryEnvironment.addStudy(name.getText())) {
                status.setText("");
                hideMe();
            } else {
                status.setText("<html><b>An unexpected error occurred when adding a study. Please report this to the ISA Team!</b></html>");
            }
        } else {
            status.setText("<html><b>Study with this identifier already exists.</b></html>");
        }
    }

    private boolean isValidName(String text) {
        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n" +
                        "^                                # Anchor to start of string.        \n" +
                        "(?!                              # Assert filename is not: CON, PRN, \n" +
                        "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                        "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                        "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                        "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                        "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                        "  $                              # and end of string                 \n" +
                        ")                                # End negative lookahead assertion. \n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                        "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
