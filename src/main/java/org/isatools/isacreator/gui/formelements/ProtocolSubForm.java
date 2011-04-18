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
import org.isatools.isacreator.model.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ProtocolSubform
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class ProtocolSubForm extends HistoricalSelectionEnabledSubForm {

    public ProtocolSubForm(String title, FieldTypes fieldType,
                           List<SubFormField> fields, int initialNoFields,
                           int width, int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public void reformPreviousContent() {
        if (parent != null) {
            reformItems();
        }
    }

    public void reformItems() {
        List<Protocol> protocols = parent.getProtocols();

        if(dtm.getColumnCount() <= 2) {
            addColumn();
        }

        for (int record = 1; record <= protocols.size() + 1; record++) {

            if (protocols.size() > record-1) {
                Map<String, String> fieldList = protocols.get(record - 1).getFieldValues();
                if (record >= dtm.getColumnCount()) {
                    addColumn();
                }

                int protocolFieldIndex = 0;

                for (SubFormField field : fields) {
                    String value = fieldList.get(field.getFieldName());
                    dtm.setValueAt(value, protocolFieldIndex, record);
                    protocolFieldIndex++;
                }
            }
        }

        updateTables();

    }

    protected void removeItem(int itemToRemove) {

        Map<String, String> record = getRecord(itemToRemove);

        Protocol tmpProtocol = new Protocol();
        tmpProtocol.addToFields(record);

        String protocolName = tmpProtocol.getProtocolName();

        if (!protocolName.equals("")) {
            parent.getStudy().removeProtocol(protocolName);
            parent.getStudy().replaceProtocols(protocolName, "");
        }

        // remove column
        removeColumn(itemToRemove);
    }

    public void update() {
        if (parent != null) {
            updateItems();
        }
    }

    public void updateItems() {
        int cols = dtm.getColumnCount();

        final List<Protocol> newProtocols = new ArrayList<Protocol>();

        for (int recordNumber = 1; recordNumber < cols; recordNumber++) {

            Map<String, String> record = getRecord(recordNumber);

            if (!isNullRecord(record)) {

                Protocol tmpProtocol = new Protocol();
                tmpProtocol.addToFields(record);

                if (!tmpProtocol.getProtocolName().equals("")) {
                    newProtocols.add(tmpProtocol);
                }
            }
        }

        parent.getStudy().setProtocols(newProtocols);
    }

}


