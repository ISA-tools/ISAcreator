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

package org.isatools.isacreator.formatmappingutility;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.formatmappingutility.io.ISAFieldMapping;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Eamonn Maguire
 * @date Jun 12, 2009
 */


public class NormalFieldEntry extends MappingInformation {
    private String fieldName;
    private String[] columnsToBeMappedTo;
    private ISAFieldMapping preExistingMapping;
    private MappingBuilderUI formatBuider;
    private JCheckBox mapTo;

    public NormalFieldEntry(String fieldName, String[] columnsToBeMappedTo) {
        this(fieldName, columnsToBeMappedTo, null);
    }

    public NormalFieldEntry(String fieldName, String[] columnsToBeMappedTo, ISAFieldMapping preExistingMapping) {
        this.fieldName = fieldName;
        this.columnsToBeMappedTo = columnsToBeMappedTo;
        this.preExistingMapping = preExistingMapping;
        createGUI();
    }

    void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));

        if (fieldName != null) {
            final JPanel fieldNameInfoPanel = new JPanel(new GridLayout(1, 1));
            JPanel fieldNameInfo = new JPanel();
            fieldNameInfo.setLayout(new BoxLayout(fieldNameInfo, BoxLayout.LINE_AXIS));

            JPanel mapToCont = new JPanel(new GridLayout(1, 1));
            mapTo = new JCheckBox("map to field from incoming file?", false);
            mapTo.setHorizontalAlignment(JCheckBox.LEFT);

            if (fieldName.equals("Sample Name") || fieldName.equals("Source Name")) {
                mapTo.setSelected(true);
                mapTo.setVisible(false);
            } else {
                mapTo.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        fieldNameInfoPanel.setVisible(mapTo.isSelected());
                        formatBuider.setVisible(mapTo.isSelected());
                        firePropertyChange("changeInWhetherToMap", "", "new");
                        fieldNameInfoPanel.revalidate();
                    }
                });

                UIHelper.renderComponent(mapTo, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

                mapToCont.add(mapTo);
                northPanel.add(mapToCont);
            }


            fieldNameInfo.add(UIHelper.createLabel("define mapping for ", UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, JLabel.LEFT));
            fieldNameInfo.add(UIHelper.createLabel(fieldName, UIHelper.VER_12_BOLD, UIHelper.LIGHT_GREEN_COLOR, JLabel.LEFT));

            fieldNameInfoPanel.add(fieldNameInfo);
            fieldNameInfoPanel.setVisible(mapTo.isSelected());

            northPanel.add(fieldNameInfoPanel);
        }

        if (preExistingMapping != null) {
            mapTo.setSelected(true);
        }

        formatBuider = new MappingBuilderUI(columnsToBeMappedTo,
                preExistingMapping != null ? preExistingMapping.getField() : null);

        formatBuider.setVisible(mapTo.isSelected());
        northPanel.add(formatBuider);
        add(northPanel, BorderLayout.NORTH);
    }

    public MappingBuilderUI getFormatBuider() {
        return formatBuider;
    }

    public boolean isMappedTo() {
        return mapTo.isSelected();
    }

    public void disableEnableComponents(boolean disableEnable) {
        for (MappingChoice mc : formatBuider.getMappings()) {
            mc.disableEnableComponents(disableEnable);
        }
    }

    public ISAFieldMapping createISAFieldMapping() {
        if (isMappedTo()) {
            ISAFieldMapping mapping = new ISAFieldMapping();
            mapping.setField(getFormatBuider().getISAFieldsForMapping());
            return mapping;
        }
        return null;
    }
}
