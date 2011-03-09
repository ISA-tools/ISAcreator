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
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.InvestigationContact;
import org.isatools.isacreator.model.StudyContact;

import java.util.ArrayList;
import java.util.List;

/**
 * ContactSubForm
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class ContactSubForm extends HistoricalSelectionEnabledSubForm {

    public ContactSubForm(String title, FieldTypes fieldType, List<SubFormField> fields, DataEntryEnvironment dep) {
        super(title, fieldType, fields, dep);
    }

    public ContactSubForm(String title, FieldTypes fieldType, List<SubFormField> fields, int initialNoFields, int width, int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public void reformPreviousContent() {
        reformItems();
    }

    public void reformItems() {
        List<Contact> contacts = parent.getContacts();

        // start off at one since the table has the first row as the field names. consequently, the no of iterations has to be increased,
        // so we add 1 to the contact size
        for (int i = 1; i < (contacts.size() + 1); i++) {
            String[] contactInfo = {
                    contacts.get(i - 1).getLastName(),
                    contacts.get(i - 1).getFirstName(),
                    contacts.get(i - 1).getMidInitial(),
                    contacts.get(i - 1).getEmail(),
                    contacts.get(i - 1).getPhone(), contacts.get(i - 1).getFax(),
                    contacts.get(i - 1).getAddress(),
                    contacts.get(i - 1).getAffiliation(),
                    contacts.get(i - 1).getRole()
            };

            for (int j = 0; j < contactInfo.length; j++) {
                dtm.setValueAt(contactInfo[j], j, i);
            }
        }
    }

    protected void removeItem(int itemToRemove) {
        if (parent != null) {
            String forename = (scrollTable.getModel().getValueAt(1, itemToRemove) != null)
                    ? scrollTable.getModel().getValueAt(1, itemToRemove).toString()
                    : "";
            String surname = (scrollTable.getModel().getValueAt(0, itemToRemove) != null)
                    ? scrollTable.getModel().getValueAt(0, itemToRemove).toString()
                    : "";
            String email = (scrollTable.getModel().getValueAt(3, itemToRemove) != null)
                    ? scrollTable.getModel().getValueAt(3, itemToRemove).toString()
                    : "";

            if (parent instanceof StudyDataEntry) {
                parent.getStudy().removeContact(forename, surname, email);
            } else {
                parent.getInvestigation().removeContact(forename, surname, email);
            }
        }
        removeColumn(itemToRemove);
    }

    public void updateItems() {
        int cols = dtm.getColumnCount();
        int rows = dtm.getRowCount();
        final List<Contact> newContacts = new ArrayList<Contact>();

        for (int i = 1; i < cols; i++) {
            String[] contactVars = new String[9];

            for (int j = 0; j < rows; j++) {
                if (dtm.getValueAt(j, i) != null) {
                    contactVars[j] = dtm.getValueAt(j, i).toString();
                } else {
                    contactVars[j] = "";
                }
            }

            if (!contactVars[0].equals("")) {
                if (parent instanceof StudyDataEntry) {
                    newContacts.add(new StudyContact(contactVars[0], contactVars[1],
                            contactVars[2], contactVars[3], contactVars[4],
                            contactVars[5], contactVars[6], contactVars[7],
                            contactVars[8]));
                } else {
                    newContacts.add(new InvestigationContact(contactVars[0], contactVars[1],
                            contactVars[2], contactVars[3], contactVars[4],
                            contactVars[5], contactVars[6], contactVars[7],
                            contactVars[8]));
                }

            }
        }
        if (parent instanceof StudyDataEntry) {
            parent.getStudy().setContacts(newContacts);
        } else if (parent instanceof InvestigationDataEntry) {
            parent.getInvestigation().setContacts(newContacts);
        }
    }

    public void update() {
        updateItems();
    }
}
