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
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.managers.ApplicationManager;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static org.isatools.isacreator.gui.DataEntryEnvironment.isValidName;


/**
 * AddNodeDialog provides simple interface for which to add a Study to the investigation.
 */
public class AddStudyDialog extends JDialog {

    @InjectedResource
    private ImageIcon addStudyHeader, addStudyButton, addStudyButtonOver,
            closeButton, closeButtonOver;

    private DataEntryEnvironment dataEntryEnvironment;
    private JLabel add;
    private JLabel close;
    private JLabel status;
    private JTextField name;
    private String type;

    public AddStudyDialog(DataEntryEnvironment dataEntryEnvironment, String type) {
        this.type = type;
        this.dataEntryEnvironment = dataEntryEnvironment;

        ResourceInjector.get("gui-package.style").inject(this);
    }

    private void addStudy() {
        
        if (!isValidName(name.getText())) {
            status.setText("<html><b>Invalid file name: can not start with COM#|LPT#</b></br> or contain \\<\\>\\/</html>");
            status.setVisible(true);
        }
        else  if (!dataEntryEnvironment.checkForDuplicateName(name.getText(), "Study")) {

             if (dataEntryEnvironment.addStudy(name.getText())) {
                status.setVisible(false);
                hideMe();
                }

            else {
                status.setVisible(true);

             }
        } else {
            status.setVisible(true);
        }
    }

    public void createGUI() {
        add = new JLabel(addStudyButton,
                JLabel.RIGHT);
        add.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                add.setIcon(addStudyButton);
                addStudy();
            }

            public void mouseEntered(MouseEvent event) {
                add.setIcon(addStudyButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                add.setIcon(addStudyButton);
            }
        });

        close = new JLabel(closeButton,
                JLabel.LEFT);
        close.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                status.setVisible(false);
                hideMe();
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeButton);
            }
        });

        instantiateFrame();
    }

    public int getHeight() {
        return 120;
    }

    public int getWidth() {
        return 200;
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
        JPanel headerCont = new JPanel(new GridLayout(1, 1));
        headerCont.setBackground(UIHelper.BG_COLOR);

        // by default, header image is to add study!
        headerCont.add(new JLabel(
                addStudyHeader,
                JLabel.RIGHT));

        add(headerCont, BorderLayout.NORTH);

        JLabel exHeaderLab = UIHelper.createLabel("Please enter " + type + " name: ");
        JPanel topCont = new JPanel(new GridLayout(2, 1));
        topCont.setBackground(UIHelper.BG_COLOR);
        topCont.add(exHeaderLab);

        name = new RoundedJTextField(10);
        name.setText(type);
        UIHelper.renderComponent(name, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);

        Action addStudyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addStudy();
            }
        };

        name.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ADDSTUDY");


        name.getActionMap().put("ADDSTUDY", addStudyAction);


        topCont.add(name);

        JPanel buttonCont = new JPanel(new GridLayout(1, 2));
        buttonCont.setBackground(UIHelper.BG_COLOR);

        buttonCont.add(close);
        buttonCont.add(add);

        status = UIHelper.createLabel("<html>study with that name already exists!</html>", UIHelper.VER_11_BOLD, UIHelper.RED_COLOR);

        status.setBackground(UIHelper.BG_COLOR);
        status.setVisible(false);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);
        container.add(topCont, BorderLayout.NORTH);
        container.add(status, BorderLayout.CENTER);
        container.add(buttonCont, BorderLayout.SOUTH);

        return container;
    }
}
