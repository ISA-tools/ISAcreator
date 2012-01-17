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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class Toolbox extends JPanel {

    // toolbox will have 5 buttons in it's entirety for addition of characteristics, factors, protocols, parameters and comments.

    @InjectedResource
    private ImageIcon addField, addFieldOver, removeField, removeFieldOver, add, addOver;

    public static final int CHARACTERISTIC = 0;
    public static final int FACTOR = 1;
    public static final int PROTOCOL = 2;
    public static final int PARAMETER = 3;
    public static final int COMMENT = 4;
    public static final int SAMPLE = 5;
    public static final int MATERIAL = 6;

    private JPanel changingContentsContainer;
    private JPanel southPanel;
    private JPanel qualifierEntryPanel;
    private JPanel fillerPanel;
    private int currentlySelectedForQualifierEntry = -1;

    public Toolbox() {
        ResourceInjector.get("formatmappingutility-package.style").inject(this);

        setLayout(new BorderLayout());
        changingContentsContainer = new JPanel();
        changingContentsContainer.setLayout(new BoxLayout(changingContentsContainer, BoxLayout.PAGE_AXIS));
        setContents();
        add(changingContentsContainer, BorderLayout.CENTER);

        createQualifierEntryPanel();
    }

    public void setContents() {
        changingContentsContainer.add(createLabel("Sample", false, Toolbox.SAMPLE));
        changingContentsContainer.add(createLabel("Material", false, Toolbox.MATERIAL));
        changingContentsContainer.add(createLabel("Characteristic", true, Toolbox.CHARACTERISTIC));
        changingContentsContainer.add(createLabel("Factor Value", true, Toolbox.FACTOR));
        changingContentsContainer.add(createLabel("Protocol", false, Toolbox.PROTOCOL));
        changingContentsContainer.add(createLabel("Parameter Value", true, Toolbox.PARAMETER));
        changingContentsContainer.add(createLabel("Comment", true, Toolbox.COMMENT));
    }


    private JLabel createLabel(String name, final boolean showQualifierEntry, final int labelType) {
        final JLabel label = new JLabel("add a " + name, addField, JLabel.LEFT);
        UIHelper.renderComponent(label, UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR, false);
        label.setVerticalAlignment(JLabel.TOP);
        label.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                label.setIcon(addFieldOver);
                label.setFont(UIHelper.VER_10_BOLD);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                label.setIcon(addField);
                label.setFont(UIHelper.VER_10_PLAIN);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                label.setIcon(addField);
                label.setFont(UIHelper.VER_10_PLAIN);
                southPanel.removeAll();
                if (showQualifierEntry) {
                    qualifierEntryPanel.setVisible(showQualifierEntry);
                    southPanel.add(qualifierEntryPanel);
                    currentlySelectedForQualifierEntry = labelType;
                    // property change event should be thrown from within the qualifier entry panel add button!
                } else {
                    fillerPanel.setVisible(!showQualifierEntry);
                    southPanel.add(fillerPanel);
                    // now fire an property change to inform listening component that a node should be added to the
                    // tree in the position directly after the currently selected node item in the tree.
                    String toAdd;
                    currentlySelectedForQualifierEntry = -1;
                    if (labelType == Toolbox.PROTOCOL) {
                        toAdd = "Protocol REF";
                    } else if (labelType == Toolbox.SAMPLE) {
                        toAdd = "Sample Name";
                    } else {
                        toAdd = "Material Type";
                    }
                    firePropertyChange("nodeAdded", "", toAdd);
                }
                revalidate();
                repaint();
                // do stuff

            }

        });
        return label;
    }

    private void createQualifierEntryPanel() {
        qualifierEntryPanel = new JPanel();
        qualifierEntryPanel.setLayout(new BoxLayout(qualifierEntryPanel, BoxLayout.PAGE_AXIS));
        qualifierEntryPanel.setPreferredSize(new Dimension(170, 40));

        JPanel qualifierCont = new JPanel();
        qualifierCont.setLayout(new BoxLayout(qualifierCont, BoxLayout.LINE_AXIS));

        JPanel qualifierLabelCont = new JPanel(new GridLayout(1, 1));

        JLabel qualifierLabel = UIHelper.createLabel(" enter a qualifier", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);

        qualifierLabelCont.add(qualifierLabel);

        final JTextField qualifier = new RoundedJTextField(12);
        UIHelper.renderComponent(qualifier, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);


        qualifierCont.add(qualifier);


        final JLabel addButton = new JLabel(add);
        addButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                addButton.setIcon(addOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addButton.setIcon(add);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addButton.setIcon(add);

                String qualifierText;
                if (!(qualifierText = qualifier.getText().trim()).equals("")) {

                    String toAdd;

                    if (currentlySelectedForQualifierEntry == Toolbox.CHARACTERISTIC) {
                        toAdd = "Characteristics[" + qualifierText + "]";
                    } else if (currentlySelectedForQualifierEntry == Toolbox.FACTOR) {
                        toAdd = "Factor Value[" + qualifierText + "]";
                    } else if (currentlySelectedForQualifierEntry == Toolbox.PARAMETER) {
                        toAdd = "Parameter Value[" + qualifierText + "]";
                    } else {
                        toAdd = "Comment[" + qualifierText + "]";
                    }

                    firePropertyChange("nodeAdded", "", toAdd);
                }
            }

        });

        qualifierCont.add(addButton);

        qualifierEntryPanel.add(qualifierLabelCont);

        qualifierEntryPanel.add(qualifierCont);

        qualifierEntryPanel.add(Box.createGlue());
        qualifierEntryPanel.setVisible(false);

        fillerPanel = new JPanel();
        fillerPanel.setLayout(new BoxLayout(fillerPanel, BoxLayout.PAGE_AXIS));

        fillerPanel.add(Box.createVerticalStrut(40));

        southPanel = new JPanel();

        southPanel.add(fillerPanel);

        // add stuff to it.
        add(southPanel, BorderLayout.SOUTH);

    }

}
