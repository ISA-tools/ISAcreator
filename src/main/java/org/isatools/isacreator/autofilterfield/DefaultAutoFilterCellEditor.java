package org.isatools.isacreator.autofilterfield;

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

import org.isatools.isacreator.autofilteringlist.FilterField;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public abstract class DefaultAutoFilterCellEditor<T extends Comparable> extends FilterField
        implements TableCellEditor, DocumentListener, FocusListener, KeyListener {

    protected transient List<CellEditorListener> listeners;

    protected String originalValue;
    protected AutoCompleteUI<T> selector;

    protected JTable currentTable;
    protected transient int currentRow;
    protected transient int currentColumn;

    protected Spreadsheet spreadsheet;

    public DefaultAutoFilterCellEditor(Spreadsheet spreadsheet) {
        super();

        this.spreadsheet = spreadsheet;

        listeners = new ArrayList<CellEditorListener>();

        setForeground(UIHelper.DARK_GREEN_COLOR);
        setCaretColor(UIHelper.LIGHT_GREEN_COLOR);
        setSelectedTextColor(UIHelper.BG_COLOR);
        setSelectionColor(UIHelper.LIGHT_GREEN_COLOR);

        setBorder(null);
        addKeyListener(this);
    }

    /**
     * Add a CellEditorListener to the list of CelleEditorListeners available
     *
     * @param cel - A CellEditorListener to add
     */
    public void addCellEditorListener(CellEditorListener cel) {
        listeners.add(cel);
    }

    public Study getStudyFromSpreadsheet() {
        return spreadsheet.getStudyDataEntryEnvironment().getStudy();
    }

    /**
     * CancelCellEditing method is required by the TableCellEditor interface.
     * Stops editing of the cell and hides the OntologySelectionTool.
     */
    public void cancelCellEditing() {
        fireEditingCanceled();
        selector.setVisible(false);
    }

    /**
     * FireEditingCanceled method sets text back to its original value, and cancels editing
     * by referring to the list of CellEditorListener objects and calling the editingStopped method with
     * a ChangeEvent defined using the OntologyCellEditor as a reference.
     */
    protected void fireEditingCanceled() {
        setText(originalValue);

        ChangeEvent ce = new ChangeEvent(this);

        for (int i = listeners.size() - 1; i >= 0; i--) {
            (listeners.get(i)).editingCanceled(ce);
        }
    }

    /**
     * Stops editing on a cell. Called when the user wants to commit the change rather than reverting to the
     * previous value entered.
     */
    protected void fireEditingStopped() {
        ChangeEvent ce = new ChangeEvent(this);

        for (int i = listeners.size() - 1; i >= 0; i--) {
            (listeners.get(i)).editingStopped(ce);
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
     * Gets the component used to edit a cell.
     *
     * @param table      - JTable being edited.
     * @param value      - /
     * @param isSelected - is the cell currently selected
     * @param row        - the row identifier for the cell
     * @param column     - the column identifier for the cell
     * @return The editing component.
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        currentTable = table;
        setCurrentRowAndColumn(row, column);

        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);

        if (table.getValueAt(row, column) != null) {
            originalValue = table.getValueAt(row, column).toString();
        } else {
            originalValue = "";
        }

        setText(originalValue);
        setCaretPositionToEndOfString();
        return this;
    }

    public void setCaretPositionToEndOfString() {
        int position = getText().length();
        select(position, position);
        setCaretPosition(position);
    }

    public void setCurrentRowAndColumn(int row, int column) {
        currentRow = row;
        currentColumn = column;
    }

    protected Point calculateDisplayLocation(JTable table, int row, int column) {
        Point p = table.getLocationOnScreen();
        Rectangle r = table.getCellRect(row, column, false);
        int proposedX = r.x + p.x;
        int proposedY = r.y + p.y + 20;

        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        if ((proposedY + AutoCompleteUI.HEIGHT) > desktopBounds.height) {
            int difference = (proposedY + AutoCompleteUI.HEIGHT) -
                    desktopBounds.height;
            proposedY = proposedY - difference;
        }

        return new Point(proposedX, proposedY);

    }

    /**
     * Always returns true since the field should always be editable.
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
     * Stop Editing of a cell component and set the cell value to be the term(s) selected in the OntologySelectionTool.
     *
     * @return true
     */
    public boolean stopCellEditing() {

        fireEditingStopped();
        if (selector != null) {
            selector.setVisible(false);
        }

        return true;
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        super.changedUpdate(event);
        showSelector();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        super.insertUpdate(event);
        if (event.getType() != DocumentEvent.EventType.REMOVE) {
            showSelector();
        }

    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        super.removeUpdate(event);
        if (selector != null) {

            if (!selector.isShowing()) {
                if (getText().length() > 0) {
                    showSelector();
                }
            }

            if (selector.isShowing()) {
                if (getText().length() == 0) {
                    hideSelector();
                }
            }
        }
    }

    private void showSelector() {
        try {
            if (selector == null) {
                instantiateSelectorIfRequired();
                selector.fadeInWindow();
            } else {
                selector.setLocation(calculateDisplayLocation(currentTable, currentRow, currentColumn));

                if (!selector.isShowing()) {
                    selector.fadeInWindow();
                }
            }
        } catch (Exception e) {
            // do nothing
        }
    }

    public abstract void instantiateSelectorIfRequired();

    protected abstract void updateContent();

    private void hideSelector() {
        if (selector.isShowing()) {
            selector.fadeOutWindow();
        }
    }

    public void focusGained(FocusEvent focusEvent) {

    }

    public void focusLost(FocusEvent focusEvent) {
        cancelCellEditing();
        hideSelector();
    }

    public void keyTyped(KeyEvent keyEvent) {
        processKeyInputEvent(keyEvent);
    }

    public void keyPressed(KeyEvent keyEvent) {
        processKeyInputEvent(keyEvent);
    }

    public void keyReleased(KeyEvent keyEvent) {
    }

    private void processKeyInputEvent(KeyEvent keyEvent) {
        instantiateSelectorIfRequired();

        if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {

            if (!selector.isShowing()) {
                showSelector();
            } else {
                selector.selectNextItem();
            }
        }

        if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            if (!selector.isShowing()) {
                showSelector();
            } else {
                selector.selectPreviousItem();
            }
        }

        if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (selector.isShowing()) {
                hideSelector();
            }
        }

        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            if (selector.getSelectedItem() != null) {
                setText(selector.getSelectedItem().toString());
            } else {
                setText(getText());
            }

            performAdditionalTasks();
        }
    }

    public abstract void performAdditionalTasks();

    public void setSpreadsheet(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
    }

}
