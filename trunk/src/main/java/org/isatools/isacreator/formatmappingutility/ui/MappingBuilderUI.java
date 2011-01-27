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

import org.isatools.isacreator.formatmappingutility.io.ISAField;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * MappingBuilderUI provides an interface to allow one to build up Mappings to either data
 * columns from an incoming file or literals (plain strings).
 */
public class MappingBuilderUI extends JPanel implements PropertyChangeListener {

    public static final int HORIZONTAL_LAYOUT = 0;
    public static final int VERTICAL_LAYOUT = 1;

    private List<MappingChoice> mappings;
    private String[] columnsToBeMappedTo;

    private int layout = VERTICAL_LAYOUT;
    private String[][] initialData;

    /**
     * Default constructor to supply just the columns to be mapped to. No mappings will be fixed
     * or pre-set.
     *
     * @param columnsToBeMappedTo - The columns given as a choice to be mapped to (from incoming file)
     */
    public MappingBuilderUI(String[] columnsToBeMappedTo) {
        this(columnsToBeMappedTo, null);
    }


    /**
     * Constructor to supply the columns to be mapped to and mappings to be pre-set.
     *
     * @param columnsToBeMappedTo - The columns given as a choice to be mapped to (from incoming file)
     * @param preExistingMappings - pre existing mappings to be loaded so that the mappings are pre-populated
     */
    public MappingBuilderUI(String[] columnsToBeMappedTo, List<ISAField> preExistingMappings) {
        this(columnsToBeMappedTo, preExistingMappings, MappingBuilderUI.VERTICAL_LAYOUT);
    }

    /**
     * Constructor to supply the columns to be mapped to and mappings to be pre-set.
     *
     * @param columnsToBeMappedTo - The columns given as a choice to be mapped to (from incoming file)
     * @param preExistingMappings - pre existing mappings to be loaded so that the mappings are pre-populated
     * @param layout              - either MappingBuilderUI.VERTICAL_LAYOUT or MappingBuilderUI.HORIZONTAL_LAYOUT to indicate how the fields will be added to the page.
     */
    public MappingBuilderUI(String[] columnsToBeMappedTo, List<ISAField> preExistingMappings, int layout) {
        this(columnsToBeMappedTo, preExistingMappings, layout, MappingEntryGUI.getInitialData());
    }

    /**
     * Constructor to supply the columns to be mapped to and mappings to be pre-set.
     *
     * @param columnsToBeMappedTo - The columns given as a choice to be mapped to (from incoming file)
     * @param preExistingMappings - pre existing mappings to be loaded so that the mappings are pre-populated
     * @param layout              - either MappingBuilderUI.VERTICAL_LAYOUT or MappingBuilderUI.HORIZONTAL_LAYOUT to indicate how the fields will be added to the page.
     * @param initialData         -  Initial data as a 2D array containing the information to be displayed in the IncomingFileBrowser utility.
     */
    public MappingBuilderUI(String[] columnsToBeMappedTo, List<ISAField> preExistingMappings, int layout, String[][] initialData) {
        this.columnsToBeMappedTo = columnsToBeMappedTo;

        this.layout = layout;
        this.initialData = initialData;

        mappings = new ArrayList<MappingChoice>();
        if (preExistingMappings != null) {
            for (int i = 0; i < preExistingMappings.size(); i++) {
                ISAField mapping = preExistingMappings.get(i);
                boolean lastInList = (i == preExistingMappings.size() - 1);

                MappingChoice newMapping = new MappingChoice(columnsToBeMappedTo, lastInList, mapping);
                // add new MappingChoice to the maintained list
                mappings.add(newMapping);
                // add MappingChoice as a GUI element.
                add(newMapping);
            }
        }

        createGUI();
    }


    void createGUI() {
        setOpaque(false);

        setLayout(new BoxLayout(this,
                layout == VERTICAL_LAYOUT ? BoxLayout.PAGE_AXIS : BoxLayout.LINE_AXIS));

        if (mappings.size() == 0) {
            addMapping();
        }

    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();

        for (MappingChoice mc : mappings) {
            toReturn.append(mc.getValueEntered());
        }
        return toReturn.toString();
    }

    public List<String> getVisualizationText() {
        List<String> toReturn = new ArrayList<String>();

        for (MappingChoice mc : mappings) {
            toReturn.add(mc.getValueForVisualization());
        }

        return toReturn;
    }

    private void addMapping() {
        MappingChoice newMapping = new MappingChoice(columnsToBeMappedTo, true, null, initialData);
        newMapping.addPropertyChangeListener(this);
        mappings.add(newMapping);
        add(newMapping);
        updateIconInMappingChoices();
        revalidate();
        repaint();
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("addNewMapping")) {
            addMapping();
        } else if (propertyChangeEvent.getPropertyName().equals("removeThisMapping")) {
            Object obj = propertyChangeEvent.getNewValue();
            if (obj instanceof MappingChoice) {
                MappingChoice mappingChoice = (MappingChoice) obj;
                remove(mappingChoice);
                mappings.remove(mappingChoice);
                updateIconInMappingChoices();
                revalidate();
                repaint();
            }
        }

    }

    private void updateIconInMappingChoices() {
        for (int mapping = 0; mapping < mappings.size(); mapping++) {
            if (mapping == mappings.size() - 1) {
                mappings.get(mapping).setLastInListProperty(true);
            } else {
                mappings.get(mapping).setLastInListProperty(false);
            }
        }
    }

    public List<MappingChoice> getMappings() {
        return mappings;
    }

    public List<ISAField> getISAFieldsForMapping() {
        List<ISAField> isaFields = new ArrayList<ISAField>();
        for (MappingChoice mc : mappings) {
            isaFields.add(mc.createISAField());
        }

        return isaFields;
    }


}
