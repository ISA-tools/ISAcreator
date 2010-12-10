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


package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Provides the GUI to add rows to the Spreadsheet.
 *
 * @author Eamonn Maguire
 */
public class AddMultipleRowsGUI extends JDialog {

    @InjectedResource
    private ImageIcon panelHeader, addRowButton, addRowButtonOver, closeButton, closeButtonOver;

    private JTextField numRowsTxt;
    private Spreadsheet st;

    public AddMultipleRowsGUI(Spreadsheet st) {
        this.st = st;
        ResourceInjector.get("spreadsheet-package.style").inject(this);
    }

    private void addRows() {
        try {
            int i = Integer.parseInt(numRowsTxt.getText());

            if ((i < Spreadsheet.MAX_ROWS) && ((i + st.getTable().getRowCount()) <= Spreadsheet.MAX_ROWS)) {
                st.addRows(i, false);
                st.getParentFrame().hideSheet();
            } else {
                numRowsTxt.setText("Max rows = " + Spreadsheet.MAX_ROWS);
            }
        } catch (NumberFormatException nfe) {
            numRowsTxt.setText("Invalid number");
        }
    }

    public void createGUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                instantiateFrame();
            }
        });
    }

    /**
     * Creates the JFrame for the class.
     */
    private void instantiateFrame() {
        //setPreferredSize(new Dimension(250, 200));
        setBackground(UIHelper.BG_COLOR);

        // add panel returned from instantiatePanel() method
        JPanel headerCont = new JPanel(new GridLayout(1, 1));
        headerCont.setBackground(UIHelper.BG_COLOR);

        headerCont.add(new JLabel(panelHeader,
                JLabel.RIGHT));
        add(headerCont, BorderLayout.NORTH);

        add(instantiatePanel());

        pack();
    }

    /**
     * Creates the JPanel containing data entry fields and the the button
     * for the user to click on.
     *
     * @return - Created JPanel containing all fields.
     */
    public JPanel instantiatePanel() {
        JLabel numRowsLab = new JLabel("How many rows? ");
        numRowsLab.setForeground(UIHelper.DARK_GREEN_COLOR);
        numRowsLab.setFont(UIHelper.VER_12_BOLD);

        numRowsTxt = new JTextField(4);

        Action addRowsAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                addRows();
            }
        };

        numRowsTxt.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ADDROWS");
        numRowsTxt.getActionMap().put("ADDROWS", addRowsAction);

        final JLabel addRows = new JLabel(addRowButton,
                JLabel.RIGHT);
        addRows.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                addRows();
            }

            public void mouseEntered(MouseEvent event) {
                addRows.setIcon(addRowButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                addRows.setIcon(addRowButton);
            }
        });

        final JLabel close = new JLabel(closeButton,
                JLabel.LEFT);
        close.addMouseListener(new MouseAdapter() {


            public void mousePressed(MouseEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        st.getParentFrame().hideSheet();
                    }
                });
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeButton);
            }
        });

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        JPanel fieldCont = new JPanel(new GridLayout(1, 2));
        fieldCont.setBackground(UIHelper.BG_COLOR);
        fieldCont.add(numRowsLab);
        fieldCont.add(numRowsTxt);
        container.add(fieldCont, BorderLayout.CENTER);

        JPanel buttonCont = new JPanel(new BorderLayout());
        buttonCont.setBackground(UIHelper.BG_COLOR);
        buttonCont.add(close, BorderLayout.WEST);
        buttonCont.add(addRows, BorderLayout.EAST);

        container.add(buttonCont, BorderLayout.SOUTH);

        return container;
    }
}
