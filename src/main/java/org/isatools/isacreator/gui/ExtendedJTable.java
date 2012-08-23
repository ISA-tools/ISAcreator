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

import org.isatools.isacreator.autofilterfield.DefaultAutoFilterCellEditor;
import org.isatools.isacreator.calendar.DateCellEditor;
import org.isatools.isacreator.filechooser.FileSelectCellEditor;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.util.EventObject;

/**
 * ExtendedJTable provides a way to allow a custom cell editor to be used in a JTable across
 * as opposed to down columns.
 *
 * @author Eamonn Maguire
 * @date Jun 2, 2008
 */
public class ExtendedJTable extends JTable implements Serializable {
    private RowEditor rowEditorData;

    public ExtendedJTable(DefaultTableModel dtm, RowEditor rowEditorData) {
        super(dtm);
        this.rowEditorData = rowEditorData;
    }


    public TableCellEditor getCellEditor(int rowNo, int colNo) {
        TableCellEditor cellEditor;

        if (rowEditorData != null) {
            cellEditor = rowEditorData.getCellEditor(rowNo);

            if (cellEditor != null) {
                return cellEditor;
            }
        }

        return super.getCellEditor(rowNo, colNo);
    }


    public boolean editCellAt(int row, int col, EventObject e) {
        final TableCellEditor editor = getCellEditor(row, col);

        if (editor instanceof OntologyCellEditor || editor instanceof FileSelectCellEditor || editor instanceof DateCellEditor) {
            if (e instanceof MouseEvent && ((MouseEvent) e).getClickCount() == 2) {
                super.editCellAt(row, col, e);
            }
        } else {
            super.editCellAt(row, col, e);

            boolean result = super.editCellAt(row, col, e);

            if (editor != null && editor instanceof JTextComponent) {
                if (e == null) {
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


        return false;
    }


    @Override
    public void changeSelection(int row, int column, boolean toggle, boolean extend) {
        super.changeSelection(row, column, toggle, extend);
        TableCellEditor editor = getCellEditor(row, column);
        if (editor instanceof DefaultAutoFilterCellEditor) {
            if (editCellAt(row, column))
                getEditorComponent().requestFocusInWindow();
        }
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

    public RowEditor getRowEditor() {
        return rowEditorData;
    }
}
