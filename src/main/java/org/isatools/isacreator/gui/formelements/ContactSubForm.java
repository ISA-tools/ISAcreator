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
import java.util.Map;

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
        for (int record = 1; record < contacts.size() + 1; record++) {

            Map<String, String> fieldList = contacts.get(record - 1).getFieldValues();

            int contactFieldIndex = 0;
            for (SubFormField field : fields) {
                String value = fieldList.get(field.getFieldName());
                dtm.setValueAt(value, contactFieldIndex, record);
                contactFieldIndex++;
            }
        }
    }

    protected void removeItem(int itemToRemove) {

        if (parent != null) {

            Map<String, String> record = getRecord(itemToRemove);

            if (parent instanceof StudyDataEntry) {
                Contact tmpContact = new StudyContact();
                tmpContact.addToFields(record);
                parent.getStudy().removeContact(tmpContact.getFirstName(), tmpContact.getLastName(), tmpContact.getEmail());
            } else {
                Contact tmpContact = new InvestigationContact();
                tmpContact.addToFields(record);
                parent.getInvestigation().removeContact(tmpContact.getFirstName(), tmpContact.getLastName().toLowerCase(), tmpContact.getEmail());
            }
        }
        removeColumn(itemToRemove);
    }

    public void updateItems() {

        int cols = dtm.getColumnCount();

        final List<Contact> newContacts = new ArrayList<Contact>();

        for (int recordNumber = 1; recordNumber < cols; recordNumber++) {

            Map<String, String> record = getRecord(recordNumber);

            // todo for each record, create a Map of key to values for each record.

            if (parent instanceof StudyDataEntry) {
                Contact contact = new StudyContact();
                contact.addToFields(record);

                if (!contact.getFirstName().equals(""))
                    newContacts.add(contact);
            } else {

                Contact contact = new InvestigationContact();
                contact.addToFields(record);

                if (!contact.getFirstName().equals(""))
                    newContacts.add(contact);
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
