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

package org.isatools.isacreator.settings;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Contact;

import javax.swing.*;

/**
 * ContactEditor
 *
 * @author Eamonn Maguire
 * @date Sep 28, 2009
 */


public class ContactEditor extends ElementEditor {

    private Contact currentContact;

    private JTextField firstName;
    private JTextField lastName;
    private JTextField midInitial;
    private JTextField email;
    private JTextField phone;
    private JTextField fax;
    private JTextField address;
    private JTextField affiliation;
    private JTextField role;

    public ContactEditor() {
        super(ElementEditor.CONTACTS);
    }

    protected void createGUI() {
        firstName = new JTextField();
        firstName.setEnabled(false);
        add(UIHelper.createFieldComponent("first name", firstName));

        lastName = new JTextField();
        lastName.setEnabled(false);
        add(UIHelper.createFieldComponent("last name", lastName));

        midInitial = new JTextField();
        midInitial.setEnabled(false);
        add(UIHelper.createFieldComponent("mid initials", midInitial));

        email = new JTextField();
        email.setEnabled(false);
        add(UIHelper.createFieldComponent("email", email));

        phone = new JTextField();
        phone.setEnabled(false);
        add(UIHelper.createFieldComponent("phone", phone));

        fax = new JTextField();
        fax.setEnabled(false);
        add(UIHelper.createFieldComponent("fax", fax));

        address = new JTextField();
        address.setEnabled(false);
        add(UIHelper.createFieldComponent("address", address));

        affiliation = new JTextField();
        affiliation.setEnabled(false);
        add(UIHelper.createFieldComponent("affiliation", affiliation));

        role = new JTextField();
        role.setEnabled(false);
        add(UIHelper.createFieldComponent("role", role));
    }


    public void setCurrentContact(Contact c) {
        this.currentContact = c;

        firstName.setText(c.getFirstName());
        lastName.setText(c.getLastName());
        midInitial.setText(c.getMidInitial());
        email.setText(c.getEmail());
        phone.setText(c.getPhone());
        fax.setText(c.getFax());
        address.setText(c.getAddress());
        affiliation.setText(c.getAffiliation());
        role.setText(c.getRole());
    }

    public Contact getGeneratedContact() {
        return currentContact;
    }
}
