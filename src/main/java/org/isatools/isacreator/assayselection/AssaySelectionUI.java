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

package org.isatools.isacreator.assayselection;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.assayselection.platform.Platform;
import org.isatools.isacreator.assayselection.platform.PlatformParser;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.SingleSelectionListCellRenderer;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public final static String NO_TECHNOLOGY_TEXT = "no technology required";

    @InjectedResource
    private ImageIcon removeIcon, removeIconOver, removeIconInactive, selectIcon, selectIconOver, selectIconInactive, leftFieldIcon, rightFieldIcon;

    private ExtendedJList assayMeasurementList;
    private ExtendedJList assayTechnologyList;
    private ExtendedJList assayPlatformList;
    private ExtendedJList selectedAssaysList;

    private RoundedJTextField otherInformationEntry;

    private JLabel removeAssay;
    private JLabel selectAssay;

    private Map<String, List<String>> measToAllowedTechnologies;

    private static Map<AssayType, List<Platform>> platforms;

    static {
        PlatformParser platformParser = new PlatformParser();
        platforms = platformParser.loadPlatformFile();
    }

    public AssaySelectionUI(Map<String, java.util.List<String>> measToAllowedTechnologies) {
        this.measToAllowedTechnologies = measToAllowedTechnologies;

        ResourceInjector.get("assayselection-package.style").inject(this);

    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setSize(new Dimension(400, 500));

        // need to create a gui with two panels on the left hand side for selection of the
        // measurement and technology and one panel on the right hand side showing which ontologies
        // have been selected!
        add(createAssaySelectionUtil(), BorderLayout.CENTER);
        add(createSelectedAssaysDisplay(), BorderLayout.SOUTH);
    }

    private Container createAssaySelectionUtil() {


        final Box assaySelectionPanel = Box.createHorizontalBox();
        assaySelectionPanel.setOpaque(false);

        assayMeasurementList = new ExtendedJList(new SingleSelectionListCellRenderer());
        assayTechnologyList = new ExtendedJList(new TechnologyListCellRenderer());
        assayPlatformList = new ExtendedJList(new SingleSelectionListCellRenderer());

        populateMeasurements();
        updateTechnologies(assayMeasurementList.getSelectedTerm());
        updatePlatforms(assayTechnologyList.getSelectedTerm());

        assayMeasurementList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String measurement = propertyChangeEvent.getNewValue().toString();
                otherInformationEntry.setVisible(false);
                assayPlatformList.setSelectedIndex(-1);

                updateTechnologies(measurement);
            }
        });

        assayTechnologyList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                otherInformationEntry.setVisible(false);
                assayPlatformList.setSelectedIndex(-1);

                updatePlatforms(assayTechnologyList.getSelectedTerm());
                if (assayTechnologyList.getItems().size() == 1 && assayTechnologyList.getItems().get(0).equals(NO_TECHNOLOGY_TEXT)) {
                    selectAssay.setVisible(true);
                } else if (!assayTechnologyList.isSelectionEmpty()) {
                    selectAssay.setVisible(true);
                } else {
                    selectAssay.setVisible(false);
                }

            }
        });

        otherInformationEntry = new RoundedJTextField(20);
        otherInformationEntry.setFont(UIHelper.VER_10_BOLD);
        otherInformationEntry.setForeground(UIHelper.DARK_GREEN_COLOR);
        otherInformationEntry.setVisible(false);

        assayPlatformList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                if (assayPlatformList.getSelectedTerm() != null) {
                    otherInformationEntry.setVisible(assayPlatformList.getSelectedTerm().equals("other"));

                    assaySelectionPanel.repaint();

                    if (assayTechnologyList.getItems().size() == 1 && assayTechnologyList.getItems().get(0).equals(NO_TECHNOLOGY_TEXT)) {
                        selectAssay.setVisible(true);
                    } else if (!assayTechnologyList.isSelectionEmpty()) {
                        selectAssay.setVisible(true);
                    } else {
                        selectAssay.setVisible(false);
                    }
                }
            }
        });

        assaySelectionPanel.add(packAndStyleFilterList(assayMeasurementList, "select measurement", null));

        assaySelectionPanel.add(packAndStyleFilterList(assayTechnologyList, "select technology", null));

        Container assayPlatformListContainer = packAndStyleFilterList(assayPlatformList, "select platform",
                UIHelper.wrapComponentInPanel(otherInformationEntry));

        assaySelectionPanel.add(assayPlatformListContainer);

        selectAssay = new JLabel(selectIcon);
        selectAssay.setHorizontalAlignment(SwingConstants.CENTER);
        selectAssay.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                selectAssay.setIcon(selectIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                selectAssay.setIcon(selectIcon);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                selectAssay.setIcon(selectIcon);

                String measurement = assayMeasurementList.getSelectedValue().toString();
                String technology = assayTechnologyList.getSelectedValue().toString();

                String platform = assayPlatformList.getSelectedValue().toString();

                if (platform.equals("other")) {
                    platform = otherInformationEntry.getText();
                }

                addToSelectedAssays(new AssaySelection(measurement, technology, platform));
            }
        });


        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        container.add(assaySelectionPanel, BorderLayout.CENTER);
        container.add(UIHelper.wrapComponentInPanel(selectAssay), BorderLayout.SOUTH);

        return container;
    }


    private Container createSelectedAssaysDisplay() {

        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        container.setPreferredSize(new Dimension(400, 170));

        JPanel selectedAssayPanel = new JPanel(new BorderLayout());
        selectedAssayPanel.setOpaque(false);

        selectedAssaysList = new ExtendedJList(new TechnologyListCellRenderer());

        selectedAssaysList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                removeAssay.setIcon(removeIcon);
            }
        });

        JScrollPane measurementScroller = new JScrollPane(selectedAssaysList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        measurementScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(measurementScroller);

        UIHelper.renderComponent(selectedAssaysList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        selectedAssayPanel.add(measurementScroller);

        removeAssay = new JLabel(removeIconInactive);
        removeAssay.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                removeAssay.setIcon(removeAssay.getIcon() == removeIconInactive ? removeIconInactive : removeIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                removeAssay.setIcon(removeAssay.getIcon() == removeIconInactive ? removeIconInactive : removeIcon);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                if (removeAssay.getIcon() != removeIconInactive) {
                    removeAssay.setIcon(removeIcon);
                    removeAssay.setIcon(removeIconInactive);
                    deleteItem(selectedAssaysList.getSelectedIndex());
                }
            }
        });

        selectedAssayPanel.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "selected assays", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

        container.add(selectedAssayPanel, BorderLayout.CENTER);
        container.add(UIHelper.wrapComponentInPanel(removeAssay), BorderLayout.SOUTH);

        return container;
    }

    private void populateMeasurements() {

        for (String s : measToAllowedTechnologies.keySet()) {
            if (!s.equalsIgnoreCase("[sample]") && !s.equalsIgnoreCase("[investigation]")) {
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

    private void updatePlatforms(String selectedTerm) {
        assayPlatformList.clearItems();

        AssayType assayType = AssayType.extractRelevantType(selectedTerm);

        if (assayType != null) {
            if (platforms.containsKey(assayType)) {
                for (Platform platform : platforms.get(assayType)) {
                    assayPlatformList.addItem(platform);
                }
            }
        }

        assayPlatformList.addItem("other");
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

    public Container packAndStyleFilterList(ExtendedJList list, String listTitle, Container extraFieldContainer) {

        JScrollPane scroller = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        UIHelper.renderComponent(list.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.LIGHT_GREEN_COLOR, false);


        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(160, 200));
        container.setOpaque(false);

        container.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                listTitle, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.DARK_GREEN_COLOR));

        if (extraFieldContainer != null) {
            container.add(extraFieldContainer, BorderLayout.SOUTH);
        }

        container.add(UIHelper.createStyledFilterField(list.getFilterField(), leftFieldIcon, rightFieldIcon), BorderLayout.NORTH);
        container.add(scroller, BorderLayout.CENTER);

        return container;
    }


}