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

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import java.util.StringTokenizer;


/**
 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables.
 * The clipboard data format used by the adapter is compatible with
 * the clipboard format used by Excel. This provides for clipboard
 * interoperability between enabled JTables and Excel.
 *
 * @author http://www.javaworld.com/javaworld/javatips/jw-javatip77.html, last accessed 10-08-2008, with some
 *         modifications made to code to improve/add functionality.
 */
public class ExcelAdaptor implements ActionListener {
    private Clipboard system;
    private JTable table;
    private boolean ignoreFirstCol;

    /**
     * The Excel Adapter is constructed with a
     * JTable on which it enables Copy-Paste and acts
     * as a Clipboard listener.
     *
     * @param table          - Table to use fo copy pasting between Excel and program
     * @param ignoreFirstCol - whether or not to ignore the first column entry.
     */
    public ExcelAdaptor(JTable table, boolean ignoreFirstCol) {
        this.table = table;
        this.ignoreFirstCol = ignoreFirstCol;

        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C,
                ActionEvent.CTRL_MASK, false);

        // Identifying the copy KeyStroke user can modify this
        // to copy on some other Key combination.
        KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V,
                ActionEvent.CTRL_MASK, false);
        // Identifying the Paste KeyStroke user can modify this
        //to copy on some other Key combination.
        table.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
        table.registerKeyboardAction(this, "Paste", paste,
                JComponent.WHEN_FOCUSED);
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public Clipboard getSystem() {
        return system;
    }

    /**
     * This method is activated on the Keystrokes we are listening to
     * in this implementation. Here it listens for Copy and Paste ActionCommands.
     * Selections comprising non-adjacent cells result in invalid selection and
     * then copy action cannot be performed.
     * Paste is done by aligning the upper left corner of the selection with the
     * 1st element in the current selection of the JTable.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().compareTo("Copy") == 0) {
            copy();
        }

        if (e.getActionCommand().compareTo("Paste") == 0) {
            paste();
        }
    }

    public void cancelCurrentEdit() {
        int editRow = table.getEditingRow();
        int editCol = table.getEditingColumn();
        if (editRow != -1) {
            table.getCellEditor(editRow, editCol).cancelCellEditing();
        }
    }

    public void copy() {
        StringBuffer sbf = new StringBuffer();

        // Check to ensure we have selected only a contiguous block of
        // cells
        int numcols = table.getSelectedColumnCount();
        int numrows = table.getSelectedRowCount();
        int[] rowsselected = table.getSelectedRows();
        int[] colsselected = table.getSelectedColumns();

        if (!((((numrows - 1) == (rowsselected[rowsselected.length - 1] -
                rowsselected[0])) && (numrows == rowsselected.length)) &&
                (((numcols - 1) == (colsselected[colsselected.length - 1] -
                        colsselected[0])) && (numcols == colsselected.length)))) {
            JOptionPane.showMessageDialog(null, "Invalid Copy Selection",
                    "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);

            return;
        }

        for (int i = 0; i < numrows; i++) {
            for (int j = 0; j < numcols; j++) {
                sbf.append(table.getValueAt(rowsselected[i], colsselected[j]));

                if (j < (numcols - 1)) {
                    sbf.append("\t");
                }
            }

            sbf.append("\r");
        }

        StringSelection stsel = new StringSelection(sbf.toString());
        system = Toolkit.getDefaultToolkit().getSystemClipboard();
        system.setContents(stsel, stsel);
    }

    /**
     * Public Accessor methods for the Table on which this adapter acts.
     *
     * @return the Table
     */
    public JTable getJTable() {
        return table;
    }

    public void paste() {
        int startRow = (table.getSelectedRows())[0];
        int startCol = (table.getSelectedColumns())[0];

        cancelCurrentEdit();

        if (ignoreFirstCol && startCol == 0) {
            if (table.getColumnCount() > 1) {
                startCol = 1;
            } else {
                // NO POINT TRYING TO PASTE INTO A TABLE WITH NO COLUMNS!
                return;
            }
        }

        try {
            String trstring = (String) (system.getContents(this)
                    .getTransferData(DataFlavor.stringFlavor));

            Scanner st1 = new Scanner(trstring);

            int rowCount = table.getRowCount();

            for (int i = 0; st1.hasNext() && (i < rowCount); i++) {
                String rowstring = st1.nextLine();
                StringTokenizer st2 = new StringTokenizer(rowstring, "\t");
                for (int j = 0; st2.hasMoreTokens(); j++) {
                    String value = st2.nextToken();

                    if (((startRow + i) < table.getRowCount()) &&
                            ((startCol + j) < table.getColumnCount())) {
                        table.setValueAt(value, startRow + i, startCol + j);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Problem occurred whilst tryin to paste!",
                    "Problem when pasting", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setJTable(JTable table) {
        this.table = table;
    }
}
