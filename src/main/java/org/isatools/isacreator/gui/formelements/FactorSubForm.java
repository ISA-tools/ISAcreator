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

package org.isatools.isacreator.gui.formelements;

import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.model.Factor;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * FactorSubForm
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class FactorSubForm extends HistoricalSelectionEnabledSubForm implements ListSelectionListener {

    public FactorSubForm(String title, FieldTypes fieldType, List<SubFormField> fields, DataEntryEnvironment dep) {
        super(title, fieldType, fields, dep);
    }

    public FactorSubForm(String title, FieldTypes fieldType, List<SubFormField> fields, int initialNoFields, int width, int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public void reformPreviousContent() {
        if (parent != null) {
            reformItems();
        }
    }

    public void reformItems() {
        if (parent != null && parent.getFactors() != null) {
            List<Factor> factors = parent.getFactors();
            int rowNo = dtm.getRowCount();

            // start off at one since the table has the first row as the field names. consequently, the no of iterations has to be increased,
            // so we add 1 to the factor size
            for (int i = 1; i < (factors.size() + 1); i++) {

                String[] factorInfo = {
                        factors.get(i - 1).getFactorName(),
                        factors.get(i - 1).getFactorType()
                };

                for (int j = 0; j < rowNo; j++) {
                    if (i >= dtm.getColumnCount()) {
                        addColumn();
                        updateTables();
                    }
                    dtm.setValueAt(factorInfo[j], j, i);
                }
            }
        }
    }

    protected void removeItem(int itemToRemove) {
        if (parent != null && parent.getStudy() != null) {
            String factorName = (scrollTable.getModel()
                    .getValueAt(0, itemToRemove) != null)
                    ? scrollTable.getModel().getValueAt(0, itemToRemove).toString()
                    : null;

            if (factorName != null) {
                parent.getStudy().removeFactor(factorName);
            }
        }
        removeColumn(itemToRemove);
    }

    public void updateItems() {
        int cols = dtm.getColumnCount();
        int rows = dtm.getRowCount();
        final List<Factor> newFactors = new ArrayList<Factor>();

        for (int i = 1; i < cols; i++) {
            String[] factorVars = new String[2];

            for (int j = 0; j < rows; j++) {
                if (dtm.getValueAt(j, i) != null &&
                        !dtm.getValueAt(j, i).equals("")) {
                    factorVars[j] = dtm.getValueAt(j, i).toString();
                }
            }

            if ((factorVars[0] != null && !factorVars[0].equals("")) && (factorVars[1] != null && !factorVars[1].equals(""))) {
                newFactors.add(new Factor(factorVars[0], factorVars[1]));
            }
        }

        parent.getStudy().setFactors(newFactors);
    }

    public void update() {
        if (parent != null) {
            updateItems();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        super.valueChanged(event);
        int columnSelected = scrollTable.getSelectedColumn();
        if (columnSelected > -1) {
            if (scrollTable.getValueAt(0, columnSelected) != null) {
                String factorName = scrollTable.getValueAt(0, columnSelected).toString();
                if (scrollTable.getValueAt(1, columnSelected) == null || scrollTable.getValueAt(1, columnSelected).toString().equals("")) {
                    System.out.println("factor type: " + factorName);
                    scrollTable.setValueAt(factorName, 1, columnSelected);
                }
            }
        }
    }

}
