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
import org.isatools.isacreator.model.Protocol;

import java.util.ArrayList;
import java.util.List;

/**
 * ProtocolSubform
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class ProtocolSubForm extends HistoricalSelectionEnabledSubForm {

    public ProtocolSubForm(String title, FieldTypes fieldType,
                           List<SubFormField> fields, DataEntryEnvironment dep) {
        super(title, fieldType, fields, dep);
    }

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
        int rowNo = dtm.getRowCount();

        for (int i = 1; i < (protocols.size() + 1); i++) {
            String[] protocolInfo = {
                    protocols.get(i - 1).getProtocolName(),
                    protocols.get(i - 1).getProtocolType(),
                    protocols.get(i - 1).getProtocolDescription(),
                    protocols.get(i - 1).getProtocolURL(),
                    protocols.get(i - 1).getProtocolVersion(),
                    protocols.get(i - 1).getProtocolParameterName(),
                    protocols.get(i - 1).getProtocolComponentName(),
                    protocols.get(i - 1).getProtocolComponentType()
            };

            for (int j = 0; j < rowNo; j++) {
                if (i >= dtm.getColumnCount()) {
                    addColumn();
                    updateTables();
                }
                dtm.setValueAt(protocolInfo[j], j, i);
            }
        }
    }

    protected void removeItem(int itemToRemove) {
        String protocolName = (scrollTable.getModel()
                .getValueAt(0, itemToRemove) != null)
                ? scrollTable.getModel().getValueAt(0, itemToRemove).toString()
                : null;

        if (protocolName != null) {
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
        int rows = dtm.getRowCount();
        final List<Protocol> newProtocols = new ArrayList<Protocol>();

        for (int i = 1; i < cols; i++) {
            String[] protocolVars = new String[8];

            for (int j = 0; j < rows; j++) {
                if (dtm.getValueAt(j, i) != null) {
                    protocolVars[j] = dtm.getValueAt(j, i).toString();
                } else {
                    protocolVars[j] = "";
                }
            }

            if (!protocolVars[0].equals("")) {
                newProtocols.add(new Protocol(protocolVars[0], protocolVars[1],
                        protocolVars[2], protocolVars[3], protocolVars[4],
                        protocolVars[5], protocolVars[6], protocolVars[7]));
            }
        }

        parent.getStudy().setProtocols(newProtocols);
    }

}


