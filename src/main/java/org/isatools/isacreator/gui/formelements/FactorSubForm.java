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

import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.model.Factor;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FactorSubForm
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class FactorSubForm extends HistoricalSelectionEnabledSubForm implements ListSelectionListener {


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

            for (int record = 1; record < factors.size() + 1; record++) {

                if (factors.size() > record - 1) {
                    Map<String, String> fieldList = factors.get(record - 1).getFieldValues();

                    if (record >= dtm.getColumnCount()) {
                        addColumn();
                    }

                    int factorFieldIndex = 0;

                    for (SubFormField field : fields) {
                        String value = fieldList.get(field.getFieldName());
                        dtm.setValueAt(value, factorFieldIndex, record);
                        factorFieldIndex++;
                    }
                }
            }

            dtm.fireTableStructureChanged();
            updateTables();
        }

    }

    protected void removeItem(int itemToRemove) {
        if (parent != null && parent.getStudy() != null) {

            Map<String, String> record = getRecord(itemToRemove);

            Factor tmpFactor = new Factor();
            tmpFactor.addToFields(record);
            parent.getStudy().removeFactor(tmpFactor.getFactorName());

        }
        removeColumn(itemToRemove);
    }

    public void updateItems() {
        int cols = dtm.getColumnCount();

        List<Factor> newFactors = new ArrayList<Factor>();

        for (int recordNumber = 1; recordNumber < cols; recordNumber++) {

            Map<String, String> record = getRecord(recordNumber);

            record.put(Factor.FACTOR_TYPE, record.get(Factor.FACTOR_NAME));

            Factor factor = new Factor();
            factor.addToFields(record);

            if (!factor.getFactorName().equals("")) {
                newFactors.add(factor);
            }
        }

        if (parent.getStudy() != null) {
            parent.getStudy().setFactors(newFactors);
        }
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

            Map<String, String> record = getRecord(columnSelected);

            if (!isNullRecord(record)) {
                Factor tmpFactor = new Factor();
                tmpFactor.addToFields(record);
            }
        }
    }

//    @Override
//    public String toString() {
//        update();
//
//        System.out.println("Outputting using new factor output code...");
//
//        StringBuilder output = new StringBuilder();
//        output.append(InvestigationFileSection.STUDY_FACTORS).append("\n");
//
//        Map<String, List<String>> valuesToOutput = new HashMap<String, List<String>>();
//
//        int cols = dtm.getColumnCount();
//
//        for (int recordNumber = 1; recordNumber < cols; recordNumber++) {
//
//            Map<String, String> record = getRecord(recordNumber);
//
//            Map<Integer, Map<String, String>> ontologyTerms = IOUtils.getOntologyTerms(record.keySet());
//
//            // now, do ontology processing
//            for (int fieldHashCode : ontologyTerms.keySet()) {
//
//                Map<String, String> ontologyField = ontologyTerms.get(fieldHashCode);
//
//                Map<String, String> processedOntologyField = IOUtils.processOntologyField(ontologyField, record);
//                record.put(ontologyField.get(IOUtils.TERM), processedOntologyField.get(ontologyField.get(IOUtils.TERM)));
//                record.put(ontologyField.get(IOUtils.ACCESSION), processedOntologyField.get(ontologyField.get(IOUtils.ACCESSION)));
//                record.put(ontologyField.get(IOUtils.SOURCE_REF), processedOntologyField.get(ontologyField.get(IOUtils.SOURCE_REF)));
//            }
//
//            for (String fieldName : record.keySet()) {
//                if (!valuesToOutput.containsKey(fieldName)) {
//                    valuesToOutput.put(fieldName, new ArrayList<String>());
//                }
//
//                valuesToOutput.get(fieldName).add(StringProcessing.cleanUpString(record.get(fieldName)));
//            }
//        }
//
//        // now output the values.
//        for (String fieldName : valuesToOutput.keySet()) {
//            output.append(fieldName).append("\t");
//
//            for (int outputCount = 0; outputCount < valuesToOutput.get(fieldName).size(); outputCount++) {
//
//                output.append(valuesToOutput.get(fieldName).get(outputCount));
//                if (outputCount != valuesToOutput.get(fieldName).size() - 1) {
//                    output.append("\t");
//                }
//
//            }
//            output.append("\n");
//        }
//
//        return output.toString();
//    }
}
