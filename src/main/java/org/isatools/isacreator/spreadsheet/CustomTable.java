/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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

import org.isatools.isacreator.autofilterfield.DefaultAutoFilterCellEditor;
import org.isatools.isacreator.calendar.DateCellEditor;
import org.isatools.isacreator.filechooser.FileSelectCellEditor;
import org.isatools.isacreator.filterablelistselector.FilterableListCellEditor;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;
import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.EventObject;

/**
 * Purpose of CustomTable class is to override the editCellAt method of the JTable. This is required
 * to prevent the OntologySelectionTool, FileChooser, or Calendar widgets popping up as soon as a user
 * enters a cell. Instead, the user must double click on a cell.
 *
 * @author Eamonn Maguire
 */
public class CustomTable extends JTable {
    public CustomTable(DefaultTableModel defaultTableModel) {
        super(defaultTableModel);
    }

    public boolean editCellAt(int row, int column, EventObject eventObject) {
        final TableCellEditor editor = getCellEditor(row, column);

        if (editor instanceof OntologyCellEditor ||
                editor instanceof FileSelectCellEditor ||
                editor instanceof DateCellEditor ||
                editor instanceof FilterableListCellEditor ||
                editor instanceof PluginSpreadsheetWidget) {

            if (eventObject instanceof MouseEvent && ((MouseEvent) eventObject).getClickCount() == 2) {
                super.editCellAt(row, column, eventObject);
            }

        } else {

            super.editCellAt(row, column, eventObject);

            boolean result = super.editCellAt(row, column, eventObject);

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
        return false;
    }

    private boolean iskeyOk(KeyEvent event) {
        return event.getKeyCode() != KeyEvent.VK_DOWN && event.getKeyCode() != KeyEvent.VK_UP && event.getKeyCode() != KeyEvent.VK_ENTER;
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

    public int columnViewIndextoModel(int vColIndex) {
        if (vColIndex >= getColumnCount()) {
            return -1;
        }
        return getColumnModel().getColumn(vColIndex).getModelIndex();
    }

    public int columnModelIndextoView(int mColIndex) {
        for (int c = 0; c < getColumnCount(); c++) {
            TableColumn col = getColumnModel().getColumn(c);
            if (col.getModelIndex() == mColIndex) {
                return c;
            }
        }
        return -1;
    }

    public void scrollToCellLocation(int rowIndex, int colIndex) {

        if (!(getParent() instanceof JViewport)) {
            return;
        }

        JViewport scrollPane = (JViewport) getParent();
        Rectangle rect = getCellRect(rowIndex, colIndex, true);
        Point p = scrollPane.getViewPosition();
        rect.setLocation(rect.x - p.x, rect.y - p.y);
        scrollPane.scrollRectToVisible(rect);
    }

}
