/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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

package org.isatools.isacreator.spreadsheet;

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.ParseException;

/**
 * Implements a cell editor that uses a formatted text field
 * to edit Integer values.
 */
public class DateEditor extends SpreadsheetCellEditor {
    private static final Logger log = Logger.getLogger(DateEditor.class.getName());
    private JFormattedTextField textField;

    public DateEditor() {
        super(new JFormattedTextField(),
                "Date must be of yyyy-MM-dd e.g. 2008-12-23");

        // grab JFormattedTextField from super class for customisation
        textField = (JFormattedTextField) getComponent();
        textField.setBorder(null);

        MaskFormatter inputMask = null;

        try {
            inputMask = new MaskFormatter("####-##-##");
            inputMask.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            log.error("Parse Exception thrown handling mask formatter");
        }

        //textField.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        if (inputMask != null) {
            inputMask.setAllowsInvalid(true);
            textField.setFormatterFactory(new DefaultFormatterFactory(inputMask));
        }

        // set initial value to lowest possible date.
        //textField.setValue(nv.getVal1());
        textField.setFocusLostBehavior(JFormattedTextField.PERSIST);

        textField.getInputMap()
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "validate");
        textField.getActionMap().put("validate",
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        if (!textField.isEditValid()) { //The text is invalid.
                            textField.postActionEvent();
                        } else {
                            try { //The text is valid,
                                textField.commitEdit(); //so use it.
                                textField.postActionEvent(); //stop editing
                            } catch (java.text.ParseException exc) {
                                // ignore...
                            }
                        }
                    }
                });
    }

    public Object getCellEditorValue() {
        JFormattedTextField textField = (JFormattedTextField) getComponent();
        Object o = textField.getValue();

        if (o instanceof String) {
            return o;
        }

        return null;
    }
}
