/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis,
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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.formatmappingutility.io.ISAField;
import org.isatools.isacreator.formatmappingutility.io.ISAFieldMapping;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class GenericFieldEntry extends JPanel {
    private JCheckBox useField;
    private MappingBuilderUI fieldBuilder;
    private String fieldName;
    private String[] columnsToMapTo;
    private List<ISAField> mappings;


    public GenericFieldEntry(String fieldName, String[] columnsToMapTo) {
        this(fieldName, columnsToMapTo, null);
    }

    public GenericFieldEntry(String fieldName, String[] columnsToMapTo, List<ISAField> mappings) {
        this.fieldName = fieldName;
        this.columnsToMapTo = columnsToMapTo;
        this.mappings = mappings;
        createGUI();
    }

    void createGUI() {
        setBorder(new TitledBorder(new RoundedBorder(UIHelper.GREY_COLOR, 7), fieldName, TitledBorder.RIGHT, TitledBorder.CENTER, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        // create builder for unit
        JPanel fieldInfo = new JPanel();
        fieldInfo.setLayout(new BoxLayout(fieldInfo, BoxLayout.PAGE_AXIS));
        fieldInfo.setOpaque(false);

        JPanel useFieldPanel = new JPanel(new GridLayout(1, 1));



        useField = new JCheckBox("use " + fieldName.toLowerCase() + "?", mappings != null);
        UIHelper.renderComponent(useField, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);

        fieldBuilder = new MappingBuilderUI(columnsToMapTo, mappings);
        fieldBuilder.setVisible(useField.isSelected());

        useField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                fieldBuilder.setVisible(useField.isSelected());
            }
        });

        useFieldPanel.add(useField);
        fieldInfo.add(useFieldPanel);
        fieldInfo.add(fieldBuilder);
        add(fieldInfo);
    }

    public boolean useField() {
        return useField.isSelected();
    }

    public MappingBuilderUI getFieldBuilder() {
        return fieldBuilder;
    }

    public ISAFieldMapping createISAFieldMapping() {
        if (useField()) {
            ISAFieldMapping mapping = new ISAFieldMapping();
            mapping.setField(getFieldBuilder().getISAFieldsForMapping());
            return mapping;
        }
        return null;
    }
}