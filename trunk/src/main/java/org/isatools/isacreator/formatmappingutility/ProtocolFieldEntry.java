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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ProtocolEntryPanel
 *
 * @author Eamonn Maguire
 * @date Oct 6, 2009
 */


public class ProtocolFieldEntry extends MappingInformation {


    private String fieldName;
    private String[] columnsToBeMappedTo;
    private ISAFieldMapping mappings;

    private NormalFieldEntry normalFieldEntry;
    private GenericFieldEntry performerEntry;
    private GenericFieldEntry dateEntry;


    public ProtocolFieldEntry(String fieldName, String[] columnsToBeMappedTo) {
        this(fieldName, columnsToBeMappedTo, null);
    }

    public ProtocolFieldEntry(String fieldName, String[] columnsToBeMappedTo, ISAFieldMapping mappings) {
        this.fieldName = fieldName;
        this.columnsToBeMappedTo = columnsToBeMappedTo;
        this.mappings = mappings;
        createGUI();
    }

    void createGUI() {

        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.PAGE_AXIS));

        normalFieldEntry = new NormalFieldEntry(fieldName, columnsToBeMappedTo, mappings);

        northPanel.add(normalFieldEntry);

        normalFieldEntry.addPropertyChangeListener("changeInWhetherToMap", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                performerEntry.setVisible(normalFieldEntry.isMappedTo());
                dateEntry.setVisible(normalFieldEntry.isMappedTo());
            }
        });

        performerEntry = new GenericFieldEntry("Provider", columnsToBeMappedTo, mappings != null ? mappings.hasProvider() ? mappings.getPerformer() : null : null);
        performerEntry.setVisible(normalFieldEntry.isMappedTo());

        dateEntry = new GenericFieldEntry("Date", columnsToBeMappedTo, mappings != null ? mappings.hasDate() ? mappings.getDate() : null : null);
        dateEntry.setVisible(normalFieldEntry.isMappedTo());

        northPanel.add(UIHelper.wrapComponentInPanel(performerEntry));
        northPanel.add(UIHelper.wrapComponentInPanel(dateEntry));
        add(northPanel, BorderLayout.NORTH);
    }

    public NormalFieldEntry getNormalFieldEntry() {
        return normalFieldEntry;
    }

    public GenericFieldEntry getPerformerPanel() {
        return performerEntry;
    }

    public GenericFieldEntry getDatePanel() {
        return dateEntry;
    }


    public boolean isMappedTo() {
        return normalFieldEntry.isMappedTo();
    }

    public void disableEnableComponents(boolean disableEnable) {
        normalFieldEntry.disableEnableComponents(disableEnable);
        for (MappingChoice mc : performerEntry.getFieldBuilder().getMappings()) {
            mc.disableEnableComponents(disableEnable);
        }
        for (MappingChoice mc : dateEntry.getFieldBuilder().getMappings()) {
            mc.disableEnableComponents(disableEnable);
        }

    }

    public ISAFieldMapping createISAFieldMapping() {
        if (isMappedTo()) {
            ISAFieldMapping mapping = normalFieldEntry.createISAFieldMapping();

            if (performerEntry.useField()) {
                mapping.setPerformer(performerEntry.getFieldBuilder().getISAFieldsForMapping());
            }

            if (dateEntry.useField()) {
                mapping.setPerformer(dateEntry.getFieldBuilder().getISAFieldsForMapping());
            }
            return mapping;
        }
        return null;
    }

}
