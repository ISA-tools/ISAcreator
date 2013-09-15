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

package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Provides the GUI to enable users to perform a sort operation on multiple table columns.
 * (2 at the minute :o) )
 */
public class MultipleSortGUI extends JDialog implements ActionListener {

    @InjectedResource
    private ImageIcon sortButton, sortButtonOver, closeButton, closeButtonOver;

    private JCheckBox sort2Check = new JCheckBox("Sort on 2 columns?", false);
    private JComboBox sortOpt1;
    private JComboBox sortOpt1IsAscending;
    private JComboBox sortOpt2;
    private JComboBox sortOpt2IsAscending;
    private Spreadsheet st;

    public MultipleSortGUI(Spreadsheet st) {
        this.st = st;

        ResourceInjector.get("spreadsheet-package.style").inject(this);
    }

    /**
     * Action Listener for class.
     *
     * @param source - Source of event e.g. JButton click.
     */
    public void actionPerformed(ActionEvent source) {
        if (source.getSource() == sortOpt1) {
            updateCombo(sortOpt2);
        }

        if (source.getSource() == sort2Check) {
            if (sort2Check.isSelected()) {
                sortOpt2.setEnabled(true);
                sortOpt2IsAscending.setEnabled(true);
                updateCombo(sortOpt2);
            } else {
                sortOpt2.setEnabled(false);
                sortOpt2IsAscending.setEnabled(false);
            }
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
        setBackground(UIHelper.BG_COLOR);

        JPanel headerCont = new JPanel();
        headerCont.setBackground(UIHelper.BG_COLOR);
        headerCont.setSize(new Dimension(300, 25));
        headerCont.setLayout(new BoxLayout(headerCont, BoxLayout.LINE_AXIS));
        headerCont.add(UIHelper.createLabel("Perform Multiple Sort", UIHelper.VER_14_BOLD, UIHelper.DARK_GREEN_COLOR, JLabel.LEFT));
        add(headerCont, BorderLayout.NORTH);
        instantiatePanel();

        pack();
    }

    /**
     * Creates the JPanel containing data entry fields and the the button
     * for the user to click on.
     */
    public void instantiatePanel() {
        sortOpt1IsAscending = new JComboBox((new String[]{
                "Ascending", "Descending"
        }));

        UIHelper.setJComboBoxAsHeavyweight(sortOpt1IsAscending);
        UIHelper.renderComponent(sortOpt1IsAscending, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        sortOpt1IsAscending.setPreferredSize(new Dimension(40, 20));
        sortOpt2IsAscending = new JComboBox((new String[]{
                "Ascending", "Descending"
        }));

        UIHelper.renderComponent(sortOpt2IsAscending, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        UIHelper.setJComboBoxAsHeavyweight(sortOpt2IsAscending);
        sortOpt2IsAscending.setPreferredSize(new Dimension(40, 20));
        sortOpt2IsAscending.setEnabled(false);

        JButton sort = new FlatButton(ButtonType.GREEN, "Apply Sort");
        sort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try {

                    int primaryCol = getAbsoluteColumn(sortOpt1.getSelectedItem().toString());
                    int secondaryCol = primaryCol;
                    if (sort2Check.isSelected()) {
                        secondaryCol = getAbsoluteColumn(sortOpt2.getSelectedItem().toString());
                    }

                    boolean primaryAscending = true;
                    if (!sortOpt1IsAscending.getSelectedItem().toString().equals("Ascending")) {
                        primaryAscending = false;
                    }

                    boolean secondaryAscending = true;
                    if (!sortOpt2IsAscending.getSelectedItem().toString().equals("Ascending")) {
                        secondaryAscending = false;
                    }

                    st.getSpreadsheetFunctions().performMultipleSort(primaryCol, secondaryCol, primaryAscending, secondaryAscending);
                    st.getParentFrame().hideSheet();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null,
                            "Unexpected Problem encountered : " + toString(),
                            "Problem encountered", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });

        JButton close = new FlatButton(ButtonType.RED, "Cancel");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        st.getParentFrame().hideSheet();
                        dispose();
                    }
                });
            }
        });

        // Panel to contain button
        JPanel doSortPanel = new JPanel(new BorderLayout());
        doSortPanel.setBackground(UIHelper.BG_COLOR);
        doSortPanel.add(close, BorderLayout.WEST);
        doSortPanel.add(sort, BorderLayout.EAST);

        JPanel options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.PAGE_AXIS));
        options.setBackground(UIHelper.BG_COLOR);

        sortOpt1 = new JComboBox(st.getHeaders(true, true));
        UIHelper.setJComboBoxAsHeavyweight(sortOpt1);
        UIHelper.renderComponent(sortOpt1, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        sortOpt1.setPreferredSize(new Dimension(100, 20));

        sortOpt2 = new JComboBox();
        UIHelper.setJComboBoxAsHeavyweight(sortOpt2);
        UIHelper.renderComponent(sortOpt2, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        sortOpt2.setEnabled(false);
        sortOpt2.setPreferredSize(new Dimension(100, 20));

        sortOpt1.addActionListener(this);
        sortOpt2.addActionListener(this);
        sort2Check.addActionListener(this);

        sort2Check.setBackground(UIHelper.BG_COLOR);
        sort2Check.setForeground(UIHelper.DARK_GREEN_COLOR);
        sort2Check.setFont(UIHelper.VER_12_BOLD);

        // Selection Panel 1
        JPanel sel1Pan = new JPanel();

        sel1Pan.setLayout(new BoxLayout(sel1Pan, BoxLayout.PAGE_AXIS));
        sel1Pan.setBackground(UIHelper.BG_COLOR);

        JPanel lab1Cont = new JPanel(new GridLayout(1, 1));
        lab1Cont.setBackground(UIHelper.BG_COLOR);

        JLabel firstColLab = new JLabel(
                "Please select the 1st column to sort by");
        firstColLab.setForeground(UIHelper.DARK_GREEN_COLOR);
        firstColLab.setBackground(UIHelper.BG_COLOR);
        firstColLab.setFont(UIHelper.VER_12_BOLD);

        lab1Cont.add(firstColLab);

        sel1Pan.add(lab1Cont);
        sel1Pan.add(Box.createVerticalStrut(5));

        JPanel sel1ComboCont = new JPanel();
        sel1ComboCont.setLayout(new BoxLayout(sel1ComboCont, BoxLayout.LINE_AXIS));
        sel1ComboCont.setBackground(UIHelper.BG_COLOR);
        sel1ComboCont.add(sortOpt1);
        sel1ComboCont.add(Box.createHorizontalStrut(5));
        sel1ComboCont.add(sortOpt1IsAscending);

        sel1ComboCont.add(Box.createHorizontalGlue());

        sel1Pan.add(sel1ComboCont);
        sel1Pan.add(Box.createVerticalGlue());

        // selection panel 2
        JPanel sel2Pan = new JPanel();
        sel2Pan.setLayout(new BoxLayout(sel2Pan, BoxLayout.PAGE_AXIS));
        sel2Pan.setBackground(UIHelper.BG_COLOR);

        JPanel lab2Cont = new JPanel(new GridLayout(2, 1));
        lab2Cont.setBackground(UIHelper.BG_COLOR);

        JLabel secondColLab = new JLabel(
                "Please select the 2nd column to sort by");
        secondColLab.setBackground(UIHelper.BG_COLOR);
        secondColLab.setForeground(UIHelper.DARK_GREEN_COLOR);
        secondColLab.setFont(UIHelper.VER_12_BOLD);

        lab2Cont.add(sort2Check);
        lab2Cont.add(secondColLab);

        sel2Pan.add(lab2Cont);
        sel2Pan.add(Box.createVerticalStrut(5));

        JPanel sel2ComboCont = new JPanel();
        sel2ComboCont.setLayout(new BoxLayout(sel2ComboCont, BoxLayout.LINE_AXIS));
        sel2ComboCont.setBackground(UIHelper.BG_COLOR);
        sel2ComboCont.add(sortOpt2);
        sel2ComboCont.add(Box.createHorizontalStrut(5));
        sel2ComboCont.add(sortOpt2IsAscending);
        sel2ComboCont.add(Box.createHorizontalGlue());

        sel2Pan.add(sel2ComboCont);
        sel2Pan.add(Box.createVerticalGlue());

        options.add(sel1Pan);
        options.add(Box.createVerticalStrut(10));
        options.add(sel2Pan);
        options.add(Box.createVerticalStrut(10));
        options.add(Box.createVerticalGlue());

        // panel to contain everything.
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);
        add(options, BorderLayout.CENTER);
        add(doSortPanel, BorderLayout.SOUTH);
    }

    /**
     * Parses the string detailing the column information into the column index!
     *
     * @param colDescriptor - Format is Col: <<ID>> <<COLUMN NAME>>
     * @return index of column
     */
    private int getAbsoluteColumn(String colDescriptor) {
        String[] splitRes = colDescriptor.trim().split(" ");
        return Integer.valueOf(splitRes[1]);
    }

    /**
     * Update the sort option 2 combo box based on removal of the term selected by the user in combo
     * box 1. doesn't make much sense to sort on the same column twice :o)
     */
    public void updateAllCombos() {
        if (sortOpt2 != null) {
            sortOpt2.removeAllItems();
            updateCombo(sortOpt1);
        }
    }

    /**
     * Update the combo box based on removal of the term selected by the user in combo
     * box 1 (sortOpt1). doesn't make much sense to sort on the same column twice :o)
     *
     * @param combo - JComboBox to be updated
     */
    private void updateCombo(JComboBox combo) {
        combo.removeAllItems();

        for (String s : st.getHeaders(true, true)) {
            if (combo == sortOpt2) {
                if (!s.equals(sortOpt1.getSelectedItem())) {
                    combo.addItem(s);
                }
            } else {
                combo.addItem(s);
            }
        }
    }
}
