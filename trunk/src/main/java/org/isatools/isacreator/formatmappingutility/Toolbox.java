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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class Toolbox extends JPanel {

    // toolbox will have 5 buttons in it's entirety for addition of characteristics, factors, protocols, parameters and comments.
    public static final ImageIcon ADD_CHAR = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addchar.png"));
    public static final ImageIcon ADD_CHAR_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addchar_over.png"));
    public static final ImageIcon ADD_FACTOR = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addfactor.png"));
    public static final ImageIcon ADD_FACTOR_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addfactor_over.png"));
    public static final ImageIcon ADD_PROTOCOL = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addprotocol.png"));
    public static final ImageIcon ADD_PROTOCOL_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addprotocol_over.png"));
    public static final ImageIcon ADD_PARAMETER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addparameter.png"));
    public static final ImageIcon ADD_PARAMETER_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addparameter_over.png"));
    public static final ImageIcon ADD_COMMENT = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addcomment.png"));
    public static final ImageIcon ADD_COMMENT_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addcomment_over.png"));
    public static final ImageIcon ADD_SAMPLE = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addsample.png"));
    public static final ImageIcon ADD_SAMPLE_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addsample_over.png"));
    public static final ImageIcon ADD_MATERIAL = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addMaterial.png"));
    public static final ImageIcon ADD_MATERIAL_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/addMaterial_over.png"));
    public static final ImageIcon ADD = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/add.png"));
    public static final ImageIcon ADD_OVER = new ImageIcon(Toolbox.class.getResource("/images/formatmapper/add_over.png"));

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
        setLayout(new BorderLayout());
        changingContentsContainer = new JPanel();
        changingContentsContainer.setLayout(new BoxLayout(changingContentsContainer, BoxLayout.LINE_AXIS));
        setContents();
        add(changingContentsContainer, BorderLayout.CENTER);

        createQualifierEntryPanel();
        // now create the panel allowing for specifying qualifiers for characteristics, factors, comments, and parameters.

    }

    public void setContents() {
        changingContentsContainer.add(createLabel(ADD_CHAR, ADD_CHAR_OVER, true, Toolbox.CHARACTERISTIC));
        changingContentsContainer.add(createLabel(ADD_FACTOR, ADD_FACTOR_OVER, true, Toolbox.FACTOR));
        changingContentsContainer.add(createLabel(ADD_PROTOCOL, ADD_PROTOCOL_OVER, false, Toolbox.PROTOCOL));
        changingContentsContainer.add(createLabel(ADD_PARAMETER, ADD_PARAMETER_OVER, true, Toolbox.PARAMETER));
        changingContentsContainer.add(createLabel(ADD_COMMENT, ADD_COMMENT_OVER, true, Toolbox.COMMENT));
        changingContentsContainer.add(createLabel(ADD_SAMPLE, ADD_SAMPLE_OVER, false, Toolbox.SAMPLE));
        changingContentsContainer.add(createLabel(ADD_MATERIAL, ADD_MATERIAL_OVER, false, Toolbox.MATERIAL));
    }


    private JLabel createLabel(final ImageIcon icon, final ImageIcon rollover, final boolean showQualifierEntry, final int labelType) {
        final JLabel label = new JLabel(icon);
        label.setVerticalAlignment(JLabel.TOP);
        label.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                label.setIcon(rollover);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                label.setIcon(icon);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                label.setIcon(icon);

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

            public void mouseReleased(MouseEvent mouseEvent) {

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

        final JTextField qualifier = new JTextField("", 12);
        UIHelper.renderComponent(qualifier, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);


        qualifierCont.add(qualifier);


        final JLabel addButton = new JLabel(ADD);
        addButton.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                addButton.setIcon(ADD_OVER);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addButton.setIcon(ADD);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addButton.setIcon(ADD);

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

            public void mouseReleased(MouseEvent mouseEvent) {

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
