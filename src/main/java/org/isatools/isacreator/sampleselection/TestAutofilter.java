package org.isatools.isacreator.sampleselection;
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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.EventObject;

public class TestAutofilter extends JFrame {

    public TestAutofilter() {

    }

    public void createGUI() {
        setTitle("Test");
        setSize(new Dimension(400, 400));

        addTestTable();

        pack();
        setVisible(true);
    }

    private void addTestTable() {
        String[][] data = new String[50][3];

        JTable table = new JTable(data, new String[]{"Source Name", "Protocol REF", "Sample Name"}) {

            @Override
            public boolean editCellAt(int row, int column, EventObject eventObject) {
                boolean result = super.editCellAt(row, column, eventObject);
                final Component editor = getEditorComponent();
                if (editor != null && editor instanceof JTextComponent) {
                    if (eventObject == null) {
                        ((JTextComponent) editor).selectAll();
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ((JTextComponent) editor).selectAll();
                            }
                        });
                    }
                }

                return result;
            }

            @Override
            public void changeSelection(int row, int column, boolean toggle, boolean extend) {
                super.changeSelection(row, column, toggle, extend);
                if (editCellAt(row, column))
                    getEditorComponent().requestFocusInWindow();
            }

            @Override
            public Component prepareEditor
                    (TableCellEditor tableCellEditor, int row, int column) {

                Component c = super.prepareEditor(tableCellEditor, row, column);
                if (c instanceof JTextComponent) {
                    ((JTextField) c).selectAll();
                }
                return c;
            }
        };

        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        add(scroller, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        new TestAutofilter().createGUI();
    }
}
