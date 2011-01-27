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

package org.isatools.isacreator.formatmappingutility.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.formatmappingutility.tablebrowser.IncomingFileBrowser;
import org.isatools.isacreator.settings.OntologySettings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AssaySelectionUtil
 *
 * @author Eamonn Maguire
 * @date Oct 2, 2009
 */


public class AssaySelectionUI extends JPanel {

    static final ImageIcon REMOVE = new ImageIcon(OntologySettings.class.getResource("/images/settings/remove.png"));
    static final ImageIcon REMOVE_OVER = new ImageIcon(OntologySettings.class.getResource("/images/settings/remove_over.png"));
    static final ImageIcon CONFIRM_DELETION = new ImageIcon(OntologySettings.class.getResource("/images/settings/confirm.png"));
    static final ImageIcon CONFIRM_DELETION_OVER = new ImageIcon(OntologySettings.class.getResource("/images/settings/confirm_over.png"));
    static final ImageIcon CANCEL_DELETION = new ImageIcon(OntologySettings.class.getResource("/images/settings/cancel.png"));
    static final ImageIcon CANCEL_DELETION_OVER = new ImageIcon(OntologySettings.class.getResource("/images/settings/cancel_over.png"));
    static final ImageIcon SELECT = new ImageIcon(IncomingFileBrowser.class.getResource("/images/formatmapper/select_button.png"));
    static final ImageIcon SELECT_OVER = new ImageIcon(IncomingFileBrowser.class.getResource("/images/formatmapper/select_button_over.png"));


    public final static String NO_TECHNOLOGY_TEXT = "no technology required";

    private ExtendedJList assayMeasurementList;
    private ExtendedJList assayTechnologyList;
    private ExtendedJList selectedAssaysList;

    private JLabel removeAssay;
    private JLabel selectAssay;

    private Map<String, List<String>> measToAllowedTechnologies;

    public AssaySelectionUI(Map<String, java.util.List<String>> measToAllowedTechnologies) {
        this.measToAllowedTechnologies = measToAllowedTechnologies;

        setLayout(new BorderLayout());
        setSize(new Dimension(400, 400));
    }

    public void createGUI() {
        // need to create a gui with two panels on the left hand side for selection of the
        // measurement and technology and one panel on the right hand side showing which ontologies
        // have been selected!
        add(createAssaySelectionUtil(), BorderLayout.WEST);
        add(createSelectAssaysDisplay(), BorderLayout.CENTER);
    }

    private JPanel createAssaySelectionUtil() {
        JPanel assaySelectionPanel = new JPanel();
        assaySelectionPanel.setLayout(new BoxLayout(assaySelectionPanel, BoxLayout.PAGE_AXIS));
        assaySelectionPanel.setOpaque(false);

        assayMeasurementList = new ExtendedJList(new ColumnFilterRenderer());
        assayTechnologyList = new ExtendedJList(new ColumnFilterRenderer());
        populateMeasurements();
        updateTechnologies(assayMeasurementList.getSelectedTerm());

        assayMeasurementList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String measurement = propertyChangeEvent.getNewValue().toString();
                updateTechnologies(measurement);
                // update technology list
            }
        });

        assayTechnologyList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                // allow to add assay type if it is not already in the list of assays to define.
                // need to add a little button below this list to add the assay
                if (assayTechnologyList.getItems().size() == 1 && assayTechnologyList.getItems().get(0).equals(NO_TECHNOLOGY_TEXT)) {
                    selectAssay.setVisible(true);
                } else if (!assayTechnologyList.isSelectionEmpty()) {
                    selectAssay.setVisible(true);
                } else {
                    selectAssay.setVisible(false);
                }
            }
        });

        JScrollPane measurementScroller = new JScrollPane(assayMeasurementList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        measurementScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(measurementScroller);

        UIHelper.renderComponent(assayMeasurementList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        JScrollPane technologyScroller = new JScrollPane(assayTechnologyList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        technologyScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(technologyScroller);

        UIHelper.renderComponent(assayTechnologyList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        JPanel measurementContainer = new JPanel(new BorderLayout());
        measurementContainer.setOpaque(false);

        measurementContainer.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "select measurement", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

        measurementContainer.add(assayMeasurementList.getFilterField(), BorderLayout.NORTH);
        measurementContainer.add(measurementScroller, BorderLayout.CENTER);

        assaySelectionPanel.add(measurementContainer);

        JPanel technologyContainer = new JPanel(new BorderLayout());
        technologyContainer.setOpaque(false);

        technologyContainer.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "select technology", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

        technologyContainer.add(assayTechnologyList.getFilterField(), BorderLayout.NORTH);
        technologyContainer.add(technologyScroller, BorderLayout.CENTER);

        assaySelectionPanel.add(technologyContainer);

        selectAssay = new JLabel(SELECT);
        selectAssay.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                selectAssay.setIcon(SELECT_OVER);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                selectAssay.setIcon(SELECT);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                selectAssay.setIcon(SELECT);

                String measurement = assayMeasurementList.getSelectedValue().toString();
                String technology = assayTechnologyList.getSelectedValue().toString();

                addToSelectedAssays(new AssaySelection(measurement, technology));
            }

            public void mouseReleased(MouseEvent mouseEvent) {

            }
        });

        JPanel selectAssayButtonCont = new JPanel(new BorderLayout());
        selectAssayButtonCont.setOpaque(false);
        selectAssayButtonCont.add(selectAssay, BorderLayout.EAST);
        assaySelectionPanel.add(selectAssayButtonCont);

        return assaySelectionPanel;
    }

    private JPanel createSelectAssaysDisplay() {

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        JPanel selectedAssayPanel = new JPanel(new BorderLayout());
        selectedAssayPanel.setOpaque(false);

        selectedAssaysList = new ExtendedJList(new ColumnFilterRenderer());

        selectedAssaysList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                removeAssay.setVisible(true);
            }
        });

        JScrollPane measurementScroller = new JScrollPane(selectedAssaysList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        measurementScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(measurementScroller);

        UIHelper.renderComponent(selectedAssaysList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        selectedAssayPanel.add(measurementScroller);

        JPanel removePanel = new JPanel();
        removePanel.setLayout(new BoxLayout(removePanel, BoxLayout.LINE_AXIS));
        removePanel.setOpaque(false);

        final JPanel confirmDeletionContainer = createConfirmRemovalPanel();

        JPanel removeButtonPanel = new JPanel(new BorderLayout());
        removeButtonPanel.setOpaque(false);

        removeAssay = new JLabel(REMOVE);
        removeAssay.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                removeAssay.setIcon(REMOVE_OVER);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                removeAssay.setIcon(REMOVE);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                removeAssay.setIcon(REMOVE);
                confirmDeletionContainer.setVisible(true);
            }

            public void mouseReleased(MouseEvent mouseEvent) {

            }
        });

        removeAssay.setVisible(false);

        removeButtonPanel.add(removeAssay, BorderLayout.WEST);

        removePanel.add(removeButtonPanel);
        removePanel.add(confirmDeletionContainer);

        selectedAssayPanel.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "selected assays", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

        container.add(selectedAssayPanel, BorderLayout.CENTER);
        container.add(removePanel, BorderLayout.SOUTH);

        return container;
    }

    private void populateMeasurements() {

        for (String s : measToAllowedTechnologies.keySet()) {
            if (!s.equalsIgnoreCase("[sample]")) {
                assayMeasurementList.addItem(s);
            }
        }
        if (!measToAllowedTechnologies.isEmpty()) {
            assayMeasurementList.setSelectedIndex(0);
        }
    }

    private void updateTechnologies(String measurement) {
        assayTechnologyList.clearItems();
        if (!measToAllowedTechnologies.get(measurement).isEmpty()) {
            for (String technology : measToAllowedTechnologies.get(measurement)) {
                if (technology.trim().equals("")) {
                    assayTechnologyList.addItem(NO_TECHNOLOGY_TEXT);
                } else {
                    assayTechnologyList.addItem(technology);
                }
            }
        }
        assayTechnologyList.setSelectedIndex(0);
    }

    private JPanel createConfirmRemovalPanel() {
        final JPanel confirmDeletionContainer = new JPanel();
        confirmDeletionContainer.setLayout(new BoxLayout(confirmDeletionContainer, BoxLayout.LINE_AXIS));
        confirmDeletionContainer.setOpaque(false);
        confirmDeletionContainer.setVisible(false);

        final JLabel confirmRemovalButton = new JLabel(CONFIRM_DELETION);

        confirmRemovalButton.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                confirmRemovalButton.setIcon(CONFIRM_DELETION_OVER);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                confirmRemovalButton.setIcon(CONFIRM_DELETION);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                confirmRemovalButton.setIcon(CONFIRM_DELETION);
                confirmDeletionContainer.setVisible(false);
                removeAssay.setVisible(false);
                deleteItem(selectedAssaysList.getSelectedIndex());
            }

            public void mouseReleased(MouseEvent mouseEvent) {

            }
        });

        final JLabel cancelRemovalButton = new JLabel(CANCEL_DELETION);

        cancelRemovalButton.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                cancelRemovalButton.setIcon(CANCEL_DELETION_OVER);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                cancelRemovalButton.setIcon(CANCEL_DELETION);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                cancelRemovalButton.setIcon(CANCEL_DELETION);
                confirmDeletionContainer.setVisible(false);
                removeAssay.setVisible(false);
            }

            public void mouseReleased(MouseEvent mouseEvent) {

            }
        });

        confirmDeletionContainer.add(confirmRemovalButton);
        confirmDeletionContainer.add(cancelRemovalButton);

        return confirmDeletionContainer;
    }

    private void deleteItem(int index) {
        if (index != -1) {
            selectedAssaysList.removeItem(selectedAssaysList.getItems().get(index));
            selectedAssaysList.repaint();
        }
    }

    private void addToSelectedAssays(AssaySelection aso) {

        // check to make sure the object doesn't already exist

        boolean present = false;
        for (Object o : selectedAssaysList.getItems()) {
            if (o.toString().compareTo(aso.toString()) == 0) {
                present = true;
                break;
            }
        }

        if (!present) {
            selectedAssaysList.addItem(aso);
        }
    }

    public List<AssaySelection> getAssaysToDefine() {
        List<AssaySelection> result = new ArrayList<AssaySelection>();
        for (Object o : selectedAssaysList.getItems()) {
            AssaySelection aso = (AssaySelection) o;
            result.add(aso);
        }
        return result;
    }


}
