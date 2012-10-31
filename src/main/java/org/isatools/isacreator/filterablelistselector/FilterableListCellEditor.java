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

package org.isatools.isacreator.filterablelistselector;

import org.isatools.isacreator.filechooser.FileChooserUI;
import org.isatools.isacreator.model.Study;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;

public class FilterableListCellEditor extends JTextField implements TableCellEditor {

    protected transient ArrayList<CellEditorListener> listeners;
    private FilterableListSelector listChooser;
    private JTable curTable;
    protected String originalValue;
    private int curCol = -1;
    private int curRow = -1;
    private Study study = null;


    public FilterableListCellEditor(String[] items) {
        super();
        listChooser = new FilterableListSelector(items);
        setupList();
    }

    public FilterableListCellEditor(Study study) {
        super();

        this.study = study;
        listChooser = new FilterableListSelector(study.getProtocolNames());
        setupList();
    }

    private void setupList() {
        listChooser.createGUI();
        // add two property change listeners. these listen for when a file is selected, and when
        // the FileChooser frame is closed with no file selection made.
        listChooser.addPropertyChangeListener("itemSelected",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        setCellContents(evt.getNewValue().toString());
                        listChooser.setVisible(false);
                        stopCellEditing();
                    }
                });
        listChooser.addPropertyChangeListener("noItemSelected",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        listChooser.setVisible(false);
                        cancelCellEditing();
                    }
                });

        setBorder(null);
        listeners = new ArrayList<CellEditorListener>();
    }


    /**
     * Add a CellEditorListener to the list of CelleEditorListeners available
     *
     * @param cel - A CellEditorListener to add
     */
    public void addCellEditorListener(CellEditorListener cel) {
        listeners.add(cel);
    }

    /**
     * CancelCellEditing method is required by the TableCellEditor interface.
     * Cancels editing of the cell and hides the FileChooser frame.
     */
    public void cancelCellEditing() {
        fireEditingCanceled();
    }

    /**
     * FireEditingCanceled method sets text back to its original value, and cancels editing
     * by referring to the list of CellEditorListener objects and calling the editingStopped method with
     * a ChangeEvent defined using the FileSelectCellEditor as a reference.
     */
    protected void fireEditingCanceled() {
        setText(originalValue);

        ChangeEvent ce = new ChangeEvent(this);
        int listenerSize = listeners.size() - 1;

        for (int i = listenerSize; i >= 0; i--) {
            (listeners.get(i)).editingCanceled(ce);
        }
    }

    /**
     * Stops editing on a cell. Called when the user wants to commit the change rather than reverting to the
     * previous value entered.
     */
    protected void fireEditingStopped() {
        ChangeEvent ce = new ChangeEvent(this);

        int listenerSize = listeners.size() - 1;

        for (int i = listenerSize; i >= 0; i--) {
            (listeners.get(i)).editingCanceled(ce);
        }
    }

    /**
     * Return the value in the editor
     *
     * @return An object representing the value in the editor.
     */
    public Object getCellEditorValue() {
        return getText();
    }

    /**
     * Gets the component used to edit a cell - in this case, the FileSelectCellEditor.
     *
     * @param table      - JTable being edited.
     * @param value      - /
     * @param isSelected - is the cell currently selected
     * @param row        - the row identifier for the cell
     * @param column     - the column identifier for the cell
     * @return The editing component. A DateCellEditor.
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);

        curRow = row;
        curCol = column;
        curTable = table;

        if (study != null) {
            listChooser.updateContents(study.getProtocolNames());
        }

        if (table.getValueAt(row, column) != null) {
            originalValue = table.getValueAt(row, column).toString();
        } else {
            originalValue = "";
        }

        Point p = table.getLocationOnScreen();
        Rectangle r = table.getCellRect(row, column, true);
        int proposedX = r.x + p.x + getWidth();
        int proposedY = r.y + p.y + getHeight();

        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        if ((proposedX + FileChooserUI.WIDTH) > desktopBounds.width) {
            int difference = (proposedX + FileChooserUI.WIDTH) -
                    desktopBounds.width;
            proposedX = proposedX - difference;
        }

        if ((proposedY + FileChooserUI.HEIGHT) > desktopBounds.height) {
            int difference = (proposedY + FileChooserUI.HEIGHT) -
                    desktopBounds.height;
            proposedY = proposedY - difference;
        }

        listChooser.addNotify();
        listChooser.setLocation(proposedX, proposedY);
        listChooser.setVisible(true);

        return this;
    }

    /**
     * Always returns true since the date field should always be editable.
     *
     * @param eo EventObject
     * @return true, always.
     */
    public boolean isCellEditable(EventObject eo) {
        return true;
    }

    /**
     * Removes a CellEditorListener from the list of listeners.
     *
     * @param cel - CellEditorListener to remove
     */
    public void removeCellEditorListener(CellEditorListener cel) {
        listeners.remove(cel);
    }

    private void setCellContents(String newContents) {
        if ((curRow != -1) && (curCol != -1) && (curTable != null)) {
            curTable.setValueAt(newContents, curRow, curCol);
        }
    }

    /**
     * Always returns true. Method indicates whether a user should be able to to select a cell.
     *
     * @param eo - EventObject.
     * @return true
     */
    public boolean shouldSelectCell(EventObject eo) {
        return true;
    }

    /**
     * Stop Editing of a cell component and set the cell value to be the file selected in the FileChooser.
     *
     * @return Boolean
     */
    public boolean stopCellEditing() {
        fireEditingStopped();
        return true;
    }

}
