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

package org.isatools.isacreator.ontologyselectiontool;

import org.isatools.isacreator.configuration.RecommendedOntology;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Map;

/**
 * OntologyCellEditor provides a way for the OntologySelectionTool be be the editor for a JTable cell.
 *
 * @author Eamonn Maguire
 */
public class OntologyCellEditor extends JTextField implements TableCellEditor {

    protected transient List<CellEditorListener> listeners;
    private boolean allowsMultipleTerms, forceOntologySelection;
    private Map<String, RecommendedOntology> recommendedOntologyMap;
    protected String originalValue;

    private static OntologySelectionTool ontologyTool;

    static {
        ontologyTool = new OntologySelectionTool();
        ontologyTool.createGUI();
    }

    /**
     * OntologyCellEditor constructor.
     *
     * @param allowsMultipleTerms - Whether or not the user is allowed to enter multiple ontology values into a field
     * @param recommendedSource   - A recommended source - can be null, could be something like UO for a unit field for example.
     */
    public OntologyCellEditor(final boolean allowsMultipleTerms, final boolean forceOntologySelection, Map<String, RecommendedOntology> recommendedSource) {
        super();
        this.allowsMultipleTerms = allowsMultipleTerms;
        this.forceOntologySelection = forceOntologySelection;
        this.recommendedOntologyMap = recommendedSource;

        // OntologySelectionTool doesn't directly interact with the CellEditor. Instead, a PropertyChange event is
        // fired, and it is here where we listen to the event and deal with it.
        ontologyTool.addPropertyChangeListener("selectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        setCellValue();
                        stopCellEditing();
                    }
                });

        ontologyTool.addPropertyChangeListener("noSelectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        cancelCellEditing();
                    }
                });

        setBorder(null);
        listeners = new ArrayList<CellEditorListener>();
    }

    private void setCellValue() {
        setText(ontologyTool.getSelectedTerm());
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
     * Stops editing of the cell and hides the OntologySelectionTool.
     */
    public void cancelCellEditing() {
        fireEditingCanceled();
        ontologyTool.setVisible(false);
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
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        table.setRowSelectionInterval(row, row);
        table.setColumnSelectionInterval(column, column);

        if (table.getValueAt(row, column) != null) {
            originalValue = table.getValueAt(row, column).toString();
        } else {
            originalValue = "";
        }

        ontologyTool.setSelectedTerm(originalValue);
        setText(originalValue);

        Point p = table.getLocationOnScreen();
        Rectangle r = table.getCellRect(row, column, true);
        int proposedX = r.x + p.x + getWidth();
        int proposedY = r.y + p.y + getHeight();

        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        if ((proposedX + OntologySelectionTool.WIDTH) > desktopBounds.width) {
            int difference = (proposedX + OntologySelectionTool.WIDTH) -
                    desktopBounds.width;
            proposedX = proposedX - difference;
        }

        if ((proposedY + OntologySelectionTool.HEIGHT) > desktopBounds.height) {
            int difference = (proposedY + OntologySelectionTool.HEIGHT) -
                    desktopBounds.height;
            proposedY = proposedY - difference;
        }

        // Set ontology tool parameters
        ontologyTool.setRecommendedOntologies(recommendedOntologyMap);
        ontologyTool.setForceOntologySelection(forceOntologySelection);
        ontologyTool.setMultipleTermsAllowed(allowsMultipleTerms);

        ontologyTool.updatehistory();
        ontologyTool.addNotify();
        ontologyTool.setLocation(proposedX, proposedY);
        ontologyTool.makeVisible();
        ontologyTool.loadRecommendedOntologiesIfAllowed();
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
        ontologyTool.setVisible(false);
        ontologyTool.updatehistory();
        setCellValue();

        return true;
    }

    public void cleanupReferences() {
        listeners.clear();
        listeners = new ArrayList<CellEditorListener>();
//        removeAll();
    }
}
