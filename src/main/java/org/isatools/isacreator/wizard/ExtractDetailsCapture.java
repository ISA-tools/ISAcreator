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

package org.isatools.isacreator.wizard;

import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.listeners.propertychange.OntologySelectedEvent;
import org.isatools.isacreator.gui.listeners.propertychange.OntologySelectionCancelledEvent;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Collections;
import java.util.Map;

public class ExtractDetailsCapture extends JPanel {

    private String initialVal;
    private DataEntryEnvironment dep;

    private JTextField extract;
    private JCheckBox poolingPerformed;

    private DropDownComponent dropdown;

    public ExtractDetailsCapture(String initialVal, DataEntryEnvironment dep) {
        this.initialVal = initialVal;
        this.dep = dep;
        setBackground(UIHelper.BG_COLOR);
        instantiatePanel();
    }

    private void instantiatePanel() {
        JPanel container = new JPanel(new GridLayout(1, 3));
        container.setBackground(UIHelper.BG_COLOR);

        extract = new RoundedJTextField(8);
        extract.setText(initialVal);
        UIHelper.renderComponent(extract, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        extract.setToolTipText("<html><b>Sample Name</b><p>Part of organism used for analysis, e.g. Liver, Kidney, Blood</p></html>");

        poolingPerformed = new JCheckBox("pooled?");
        UIHelper.renderComponent(poolingPerformed, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        poolingPerformed.setToolTipText("<html><b>Pooling event?</b><p>Are there no replicates performed with this sample?</p></html>");

        container.add(extract);
        container.add(createOntologyDropDown(extract, false, Collections.singletonMap("Tissue ontology",
                new RecommendedOntology(new Ontology("", "", "BTO", "Brenda Tissue Ontology")))));
        container.add(poolingPerformed);

        add(container);
    }

    public String getExtractName() {
        return extract.getText();
    }

    public boolean getPoolingPerformed() {
        return poolingPerformed.isSelected();
    }

    private JComponent createOntologyDropDown(JTextComponent field,
                                              boolean allowsMultiple, Map<String, RecommendedOntology> recommendedOntologySource) {

        System.out.println("DataEntryEnvironment parent frame is null? " + (dep == null));

        OntologySelectionTool ontologySelectionTool = new OntologySelectionTool(dep.getParentFrame(),
                allowsMultiple, recommendedOntologySource);
        ontologySelectionTool.createGUI();

        DropDownComponent dropdown = new DropDownComponent(field, ontologySelectionTool, DropDownComponent.ONTOLOGY);

        ontologySelectionTool.addPropertyChangeListener("selectedOntology", new OntologySelectedEvent(ontologySelectionTool, dropdown, field));

        ontologySelectionTool.addPropertyChangeListener("noSelectedOntology", new OntologySelectionCancelledEvent(ontologySelectionTool, dropdown));

        return dropdown;
    }
}
