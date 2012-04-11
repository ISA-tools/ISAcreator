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
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.HistoricalSelectionGUI;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.InvestigationPublication;
import org.isatools.isacreator.model.Publication;
import org.isatools.isacreator.model.StudyPublication;
import org.isatools.isacreator.publicationlocator.PublicationLocatorUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * PublicationSubForm
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class PublicationSubForm extends SubForm {

    public PublicationSubForm(String title, FieldTypes fieldType,
                              List<SubFormField> fields, int initialNoFields,
                              int width, int height, DataEntryForm parent) {
        super(title, fieldType, fields, initialNoFields, width, height, parent);
    }

    public void reformPreviousContent() {
        if (dataEntryForm != null) {
            reformItems();
        }
    }

    public void reformItems() {
        List<Publication> publications = dataEntryForm.getPublications();
        for (int record = 1; record < publications.size() + 1; record++) {

            Map<String, String> fieldList = publications.get(record - 1).getFieldValues();

            int publicationFieldIndex = 0;
            for (SubFormField field : fields) {
                String value = fieldList.get(field.getFieldName());
                defaultTableModel.setValueAt(value, publicationFieldIndex, record);
                publicationFieldIndex++;
            }
        }
    }

    protected void removeItem(int itemToRemove) {
        // provide a publication id or a publication title depending on which is available
        if (dataEntryForm != null) {

            Map<String, String> record = getRecord(itemToRemove);

            if (dataEntryForm instanceof StudyDataEntry) {
                Publication tmpPublication = new StudyPublication();
                tmpPublication.addToFields(record);
                dataEntryForm.getStudy().removePublication(tmpPublication.getPubmedId(), tmpPublication.getPublicationTitle());
            } else {
                Publication tmpPublication = new InvestigationPublication();
                tmpPublication.addToFields(record);
                dataEntryForm.getInvestigation().removePublication(tmpPublication.getPubmedId(), tmpPublication.getPublicationTitle());
            }
        }
        removeColumn(itemToRemove);
    }

    public void updateItems() {
        int cols = defaultTableModel.getColumnCount();

        final List<Publication> newPublications = new ArrayList<Publication>();

        for (int recordNumber = 1; recordNumber < cols; recordNumber++) {

            Map<String, String> record = getRecord(recordNumber);

            if (!isNullRecord(record)) {
                if (dataEntryForm instanceof StudyDataEntry) {
                    Publication publication = new StudyPublication();
                    publication.addToFields(record);

                    if (!publication.getPubmedId().equals("") || !publication.getPublicationDOI().equals("") ||
                            !publication.getPublicationTitle().equals("") || !publication.getPublicationAuthorList().equals(""))
                        newPublications.add(publication);
                } else {

                    Publication publication = new InvestigationPublication();
                    publication.addToFields(record);

                    if (!publication.getPubmedId().equals("") || !publication.getPublicationDOI().equals("") ||
                            !publication.getPublicationTitle().equals("") || !publication.getPublicationAuthorList().equals(""))
                        newPublications.add(publication);

                }
            }
        }

        if (dataEntryForm instanceof StudyDataEntry) {
            dataEntryForm.getStudy().setPublications(newPublications);
        } else if (dataEntryForm instanceof InvestigationDataEntry) {
            dataEntryForm.getInvestigation().setPublications(newPublications);
        }
    }

    public void update() {
        if (dataEntryForm != null) {
            updateItems();
        }
    }

    /**
     * Implementing this method allows for the creation of additional menu
     * elements in the options panel of the subform.
     */
    public void createCustomOptions() {
        if (dataEntryForm != null && fieldType == FieldTypes.PUBLICATION) {

            final JLabel selectPublicationLabel = new JLabel(
                    "search for " + fieldType,
                    searchIcon,
                    JLabel.LEFT);

            UIHelper.renderComponent(selectPublicationLabel, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            selectPublicationLabel.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    selectPublicationLabel.setIcon(searchIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    selectPublicationLabel.setIcon(searchIcon);
                }

                public void mousePressed(MouseEvent event) {
                    selectPublicationLabel.setIcon(searchIcon);
                    if (selectPublicationLabel.isEnabled()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                final PublicationLocatorUI publicationLocator = new PublicationLocatorUI(dataEntryForm);
                                publicationLocator.createGUI();
                                publicationLocator.installListeners();
                                publicationLocator.addPropertyChangeListener("selectedPublication",
                                        new PropertyChangeListener() {
                                            public void propertyChange(
                                                    PropertyChangeEvent evt) {

                                                if (evt.getNewValue() instanceof Publication) {
                                                    Publication p = (Publication) evt.getNewValue();
                                                    boolean added;

                                                    if (dataEntryForm instanceof StudyDataEntry) {
                                                        added = dataEntryForm.getStudy().addPublication(p);
                                                    } else {
                                                        //parent is instance of InvestigationDataEntry
                                                        System.out.println("Adding Publication to investigation");
                                                        added = dataEntryForm.getInvestigation().addPublication(p);
                                                    }

                                                    if (added) {
                                                        SwingUtilities.invokeLater(new Runnable() {
                                                            public void run() {
                                                                addColumn();
                                                                updateTables();
                                                                reformItems();
                                                            }
                                                        });

                                                    }

                                                }
                                                selectPublicationLabel.setEnabled(true);
                                            }
                                        });


                                publicationLocator.addPropertyChangeListener("noSelectedPublication",
                                        new PropertyChangeListener() {
                                            public void propertyChange
                                                    (PropertyChangeEvent evt) {
                                                selectPublicationLabel.setEnabled(true);
                                                publicationLocator.setVisible(false);
                                            }
                                        });

                                // set up location on screen
                                int proposedX = (int) selectPublicationLabel.getLocationOnScreen()
                                        .getX();
                                int proposedY = (int) selectPublicationLabel.getLocationOnScreen()
                                        .getY();

                                // get the desktop bounds e.g. 1440*990, 800x600, etc.
                                Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                                        .getMaximumWindowBounds();

                                if ((proposedX + HistoricalSelectionGUI.WIDTH) > desktopBounds.width)

                                {
                                    int difference = (proposedX +
                                            HistoricalSelectionGUI.WIDTH) -
                                            desktopBounds.width;
                                    proposedX = proposedX - difference;
                                }

                                if ((proposedY + HistoricalSelectionGUI.HEIGHT) > desktopBounds.height)

                                {
                                    int difference = (proposedY +
                                            HistoricalSelectionGUI.HEIGHT) -
                                            desktopBounds.height;
                                    proposedY = proposedY - difference;
                                }

                                publicationLocator.setLocation(proposedX, proposedY);
                                publicationLocator.setVisible(true);
                            }
                        });
                    }
                }
            });


            options.add(selectPublicationLabel);
        }
    }

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
