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


package org.isatools.isacreator.autofiltercombo;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * Provides a Cell Editor so that a Combobox can exist as an CellEditor in a JTable.
 *
 * @author Eamonn Maguire
 */
public class AutoFilterComboCellEditor extends AbstractCellEditor
        implements TableCellEditor {

    public static final String BLANK_VALUE = "--Blank--";

    private JComboBox comboBox;

    public AutoFilterComboCellEditor(JComboBox comboBox) {
        Handler handler = new Handler();
        this.comboBox = comboBox;
        UIHelper.setJComboBoxAsHeavyweight(comboBox);
        this.comboBox.putClientProperty("JComboBox.isTableCellEditor",
                Boolean.TRUE);
        JComponent editorComponent = (JComponent) comboBox.getEditor().getEditorComponent();
        editorComponent.addKeyListener(handler);

        comboBox.addPropertyChangeListener(handler);
        removeComboBorder();
    }


    public Object getCellEditorValue() {
        return comboBox.getSelectedItem();
    }

    // Implementing TableCellEditor

    public Component getTableCellEditorComponent(javax.swing.JTable table,
                                                 Object value, boolean isSelected, int row, int column) {

        comboBox.setSelectedItem(value);
        return comboBox;
    }


    /**
     * Removes border on combobox since cell already has one
     */
    private void removeComboBorder() {
        ((JComponent) comboBox.getEditor().getEditorComponent()).setBorder(null);
    }

    /**
     * Stops editing of the cell.
     *
     * @return true.
     */
    public boolean stopCellEditing() {
        if (comboBox.isEditable()) {
            // tell combobox that editing of cell has stopped
            comboBox.actionPerformed(new ActionEvent(this, 0, ""));
        }

        fireEditingStopped();

        return true;
    }

    /**
     * Update the contents of the ComboBox with newData
     *
     * @param newData - new Contents for the ComboBox.
     */
    public void updateComboBoxContents(String[] newData) {
        comboBox.removeAllItems();

        for (String s : newData) {
            comboBox.addItem(s);
        }
    }

    class Handler extends KeyAdapter implements PropertyChangeListener {
        public void keyPressed(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            if (keyCode == KeyEvent.VK_ENTER) {
                stopCellEditing();
            }
        }

        public void propertyChange(PropertyChangeEvent e) {
            if (e.getPropertyName().equals("editor")) {
                ComboBoxEditor editor = comboBox.getEditor();
                if (editor != null && editor.getEditorComponent() != null) {
                    JComponent editorComponent = (JComponent) comboBox.getEditor().getEditorComponent();
                    editorComponent.addKeyListener(this);
                    editorComponent.setBorder(null);
                }
            }
        }
    }
}