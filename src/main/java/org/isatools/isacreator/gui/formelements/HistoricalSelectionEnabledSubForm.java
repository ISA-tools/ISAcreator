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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.*;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.model.StudyContact;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * HistoricalSelectionEnabledSubForm
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public abstract class HistoricalSelectionEnabledSubForm extends SubForm {
    public HistoricalSelectionEnabledSubForm(String title, FieldTypes fieldType, List<SubFormField> fields, DataEntryEnvironment dep) {
        super(title, fieldType, fields, dep);
    }

    public HistoricalSelectionEnabledSubForm(String title, FieldTypes fieldType, List<SubFormField> fields, int initialNoFields, int width, int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    /**
     * Implementing this method allows for the creation of additional menu
     * elements in the options panel of the subform.
     */
    public void createCustomOptions() {
        if (dataEntryForm != null) {
            final JLabel selectFromHistory = new JLabel(
                    "select from previous " + fieldType + "s...",
                    selectFromHistoryIcon,
                    JLabel.LEFT);

            UIHelper.renderComponent(selectFromHistory, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            final UserProfile up = dataEntryEnvironment.getParentFrame()
                    .getCurrentUser();

            selectFromHistory.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    selectFromHistory.setIcon(selectFromHistoryIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    selectFromHistory.setIcon(selectFromHistoryIcon);
                }

                public void mousePressed(MouseEvent event) {
                    selectFromHistory.setIcon(selectFromHistoryIcon);
                    if (selectFromHistory.isEnabled()) {
                        updateItems();
                        final HistoricalSelectionGUI selectPrevious = new HistoricalSelectionGUI(up, fieldType);
                        selectFromHistory.setEnabled(false);

                        selectPrevious.addPropertyChangeListener("selectedTerms",
                                new PropertyChangeListener() {
                                    public void propertyChange(
                                            PropertyChangeEvent evt) {
                                        selectFromHistory.setEnabled(true);

                                        switch (fieldType) {
                                            case FACTOR:

                                                for (StudySubData f : selectPrevious.getSelectedTerms()) {
                                                    if (f instanceof Factor) {
                                                        // add factors to study
                                                        Factor factor = (Factor) f;

                                                        if (factor.getFactorType()
                                                                .contains(":")) {
                                                            checkForSourcePresence(factor.getFactorType()
                                                                    .substring(0,
                                                                            factor.getFactorType()
                                                                                    .indexOf(":")));
                                                        }

                                                        if (dataEntryForm.getStudy()
                                                                .addFactor((Factor) f) && !(dataEntryForm.getStudy()
                                                                .getFactors()
                                                                .size() < (defaultTableModel.getColumnCount() -
                                                                1))) {
                                                            addColumn();
                                                            updateTables();
                                                        }
                                                    }
                                                }

                                                selectPrevious.setVisible(false);
                                                selectPrevious.dispose();

                                                reformItems();

                                                break;

                                            case PROTOCOL:

                                                for (StudySubData p : selectPrevious.getSelectedTerms()) {
                                                    if (p instanceof Protocol) {
                                                        // add protocols to study
                                                        Protocol protocol = (Protocol) p;

                                                        if (protocol.getProtocolType()
                                                                .contains(":")) {
                                                            checkForSourcePresence(protocol.getProtocolType()
                                                                    .substring(0,
                                                                            protocol.getProtocolType()
                                                                                    .indexOf(":")));
                                                        }

                                                        if (dataEntryForm.getStudy()
                                                                .addProtocol((Protocol) p) && !(dataEntryForm.getStudy()
                                                                .getProtocols()
                                                                .size() < (defaultTableModel.getColumnCount() -
                                                                1))) {
                                                            addColumn();
                                                            updateTables();
                                                        }
                                                    }
                                                }

                                                selectPrevious.setVisible(false);
                                                selectPrevious.dispose();

                                                reformItems();

                                                break;

                                            case CONTACT:

                                                for (StudySubData c : selectPrevious.getSelectedTerms()) {
                                                    if (c instanceof Contact) {
                                                        // add contacts to study
                                                        List<Contact> contacts;
                                                        boolean added;
                                                        if (dataEntryForm instanceof StudyDataEntry) {
                                                            added = dataEntryForm.getStudy().addContact((StudyContact) c);
                                                            contacts = dataEntryForm.getStudy().getContacts();

                                                        } else {
                                                            //parent is instance of InvestigationDataEntry
                                                            added = dataEntryForm.getInvestigation().addContact((Contact) c);
                                                            contacts = dataEntryForm.getInvestigation().getContacts();
                                                        }

                                                        if (added) {
                                                            Contact contact = (Contact) c;

                                                            if (contact.getRole()
                                                                    .contains(":")) {
                                                                checkForSourcePresence(contact.getRole()
                                                                        .substring(0,
                                                                                contact.getRole()
                                                                                        .indexOf(":")));
                                                            }

                                                            if (!(contacts.size() < (defaultTableModel.getColumnCount() - 1))) {
                                                                addColumn();
                                                                updateTables();
                                                            }
                                                        }
                                                    }
                                                }

                                                selectPrevious.setVisible(false);
                                                selectPrevious.dispose();

                                                reformItems();

                                                break;
                                        }
                                    }
                                });

                        selectPrevious.addPropertyChangeListener("noSelectedTerms",
                                new PropertyChangeListener() {
                                    public void propertyChange(
                                            PropertyChangeEvent evt) {
                                        selectFromHistory.setEnabled(true);

                                        selectPrevious.setVisible(false);
                                        selectPrevious.dispose();
                                    }
                                });

                        // set up location on screen
                        int proposedX = (int) selectFromHistory.getLocationOnScreen()
                                .getX();
                        int proposedY = (int) selectFromHistory.getLocationOnScreen()
                                .getY();

                        // get the desktop bounds e.g. 1440*990, 800x600, etc.
                        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                .getMaximumWindowBounds();

                        if ((proposedX + HistoricalSelectionGUI.WIDTH) > desktopBounds.width) {
                            int difference = (proposedX +
                                    HistoricalSelectionGUI.WIDTH) -
                                    desktopBounds.width;
                            proposedX = proposedX - difference;
                        }

                        if ((proposedY + HistoricalSelectionGUI.HEIGHT) > desktopBounds.height) {
                            int difference = (proposedY +
                                    HistoricalSelectionGUI.HEIGHT) -
                                    desktopBounds.height;
                            proposedY = proposedY - difference;
                        }

                        selectPrevious.createGUI(proposedX, proposedY);
                    }
                }
            });

            options.add(selectFromHistory);
            options.add(Box.createHorizontalStrut(10));
        }
    }

    public abstract void reformItems();

    public abstract void reformPreviousContent();

    public abstract void removeItem(int itemToRemove);

    public abstract void update();

    public abstract void updateItems();

    public boolean doAddColumn(DefaultTableModel model, TableColumn col) {
        try {
            scrollTable.addColumn(col);
            model.addColumn(fieldType);
            model.fireTableStructureChanged();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


}
