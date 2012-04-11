package org.isatools.isacreator.autofilterfield;

import org.isatools.isacreator.autofilteringlist.FilterField;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
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

public abstract class DefaultAutoFilterCellEditor<T> extends FilterField
        implements TableCellEditor, DocumentListener, FocusListener, KeyListener {

    protected transient List<CellEditorListener> listeners;

    protected String originalValue;
    protected AutoCompleteUI<T> selector;

    protected JTable currentTable;
    protected int currentRow;
    protected int currentColumn;

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
     * Gets the component used to edit a cell - in this case, the OntologyCellEditor.
     *
     * @param table      - JTable being edited.
     * @param value      - /
     * @param isSelected - is the cell currently selected
     * @param row        - the row identifier for the cell
     * @param column     - the column identifier for the cell
     * @return The editing component. A OntologyCellEditor.
     */
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        currentTable = table;
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);

        if (table.getValueAt(row, column) != null) {
            originalValue = table.getValueAt(row, column).toString();
        } else {
            originalValue = "";
        }

        setText(originalValue);

        currentRow = row;
        currentColumn = column;

        return this;
    }

    protected Point calculateDisplayLocation(JTable table, int row, int column) {

        Point p = table.getLocationOnScreen();
        Rectangle r = table.getCellRect(row, column, true);
        int proposedX = r.x + p.x;
        int proposedY = r.y + p.y + getHeight();

        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        if ((proposedY + OntologySelectionTool.HEIGHT) > desktopBounds.height) {
            int difference = (proposedY + AutoCompleteUI.HEIGHT) -
                    desktopBounds.height;
            proposedY = proposedY - difference;
        }

        return new Point(proposedX, proposedY);

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
            if (selector.isShowing()) {

                if (getText().length() > 0) {
                    showSelector();
                } else {
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
            System.out.println(e.getMessage());
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
