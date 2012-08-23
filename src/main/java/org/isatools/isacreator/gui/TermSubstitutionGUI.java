/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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

package org.isatools.isacreator.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TermSubstitutionGUI extends JPanel {
    private Map<String, String[]> termsToBeReplaced;
    private Map<String, String[]> termsToReplaceWith;
    private Map<String, Set<SingleSubstitutionPanel>> categorySubstitutionPanels;


    private JLabel status;
    public static final String DO_NOT_REPLACE_TEXT = "Delete";

    public TermSubstitutionGUI(Map<String, String[]> termsToBeReplaced, Map<String, String[]> termsToReplaceWith) {
        this.termsToBeReplaced = termsToBeReplaced;
        this.termsToReplaceWith = termsToReplaceWith;
        categorySubstitutionPanels = new HashMap<String, Set<SingleSubstitutionPanel>>();

    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 3));
        instantiatePanel();

    }


    private void instantiatePanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        JPanel subHeaderPanel = new JPanel();
        subHeaderPanel.setLayout(new BoxLayout(subHeaderPanel, BoxLayout.PAGE_AXIS));
        subHeaderPanel.setBackground(UIHelper.BG_COLOR);

        JLabel headerLab = new JLabel(
                new ImageIcon(getClass().getResource("/images/gui/substituteExpiredTermsHeader.png")),
                JLabel.RIGHT);

        JPanel headerLabPanel = new JPanel(new GridLayout(1, 1));
        headerLabPanel.setBackground(UIHelper.BG_COLOR);
        headerLabPanel.add(headerLab);

        subHeaderPanel.add(headerLabPanel);

        JPanel infoLabPanel = new JPanel(new GridLayout(1, 1));
        infoLabPanel.setBackground(UIHelper.BG_COLOR);
        JLabel infoLab = new JLabel("<html>You have changed the names of some terms used within the Study sample and assay definitions." +
                " In order to ensure that the terms are consistent, <b>either match the term you replaced with one of the new terms you have added</b>, or" +
                " <b>delete</b> the term to remove it's presence in the sample or assay definitions.</html>");
        infoLab.setOpaque(false);
        infoLab.setPreferredSize(new Dimension(400, 90));
        infoLab.setFont(UIHelper.VER_12_PLAIN);
        infoLab.setForeground(UIHelper.DARK_GREEN_COLOR);

        infoLabPanel.add(infoLab);

        subHeaderPanel.add(infoLabPanel);

        container.add(subHeaderPanel, BorderLayout.NORTH);

        JPanel substitutionArea = new JPanel();
        substitutionArea.setLayout(new BoxLayout(substitutionArea, BoxLayout.PAGE_AXIS));
        substitutionArea.setOpaque(false);


        for (String title : termsToBeReplaced.keySet()) {
            substitutionArea.add(Box.createVerticalStrut(10));
            substitutionArea.add(createLabel(title, UIHelper.VER_14_BOLD, UIHelper.LIGHT_GREEN_COLOR));

            for (String toReplace : termsToBeReplaced.get(title)) {
                final SingleSubstitutionPanel ssp = new SingleSubstitutionPanel(toReplace, termsToReplaceWith.get(title));

                ssp.createGUI();
                if (categorySubstitutionPanels.get(title) == null) {
                    categorySubstitutionPanels.put(title, new HashSet<SingleSubstitutionPanel>());
                }
                categorySubstitutionPanels.get(title).add(ssp);
                substitutionArea.add(ssp);
                substitutionArea.add(Box.createVerticalStrut(5));
            }
        }

        substitutionArea.add(Box.createVerticalGlue());

        JScrollPane subPanelScroller = new JScrollPane(substitutionArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        subPanelScroller.setBorder(null);
        subPanelScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        subPanelScroller.setPreferredSize(new Dimension(390, 190));

        IAppWidgetFactory.makeIAppScrollPane(subPanelScroller);

        container.add(subPanelScroller);

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.setBackground(UIHelper.BG_COLOR);

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setBackground(UIHelper.BG_COLOR);

        status = new JLabel();
        status.setFont(UIHelper.VER_12_PLAIN);
        status.setForeground(UIHelper.RED_COLOR);
        status.setPreferredSize(new Dimension(390, 30));

        statusPanel.add(status);

        southPanel.add(statusPanel);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel okButton = new JLabel(new ImageIcon(getClass().getResource("/images/common/ok.png")), JLabel.RIGHT);
        okButton.setOpaque(false);

        okButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                // check to ensure that all terms have been replaced uniquely.

                // Hashmap containing the category for replacement and the terms selected. as soon as we come across a term added twice, it will be
                // reported to the user...
                Map<String, Set<String>> uniquenessCheck = new HashMap<String, Set<String>>();

                for (String category : categorySubstitutionPanels.keySet()) {
                    if (uniquenessCheck.get(category) == null) {
                        uniquenessCheck.put(category, new HashSet<String>());
                    }

                    for (SingleSubstitutionPanel ssp : categorySubstitutionPanels.get(category)) {
                        if (uniquenessCheck.get(category).contains(ssp.getCurrentlySelectedValue()) && !ssp.getCurrentlySelectedValue().equals(DO_NOT_REPLACE_TEXT)) {
                            status.setText("<html><b>Error occurred</b> <p> <b>" + ssp.getCurrentlySelectedValue() + "</b> selected as more than one replacement in <b>" + category + "</b></p>");
                            revalidate();
                            return;
                        } else {
                            uniquenessCheck.get(category).add(ssp.getCurrentlySelectedValue());
                        }
                    }
                }
                status.setText("");
                firePropertyChange("substitutionComplete", "", getDefinedSubstitutions());
            }

            public void mouseEntered(MouseEvent event) {
                okButton.setIcon(new ImageIcon(getClass().getResource("/images/common/ok_over.png")));
            }

            public void mouseExited(MouseEvent event) {
                okButton.setIcon(new ImageIcon(getClass().getResource("/images/common/ok.png")));
            }
        });

        buttonPanel.add(okButton);

        southPanel.add(buttonPanel);

        container.add(southPanel, BorderLayout.SOUTH);

        add(container);

    }

    public Map<String, Map<String, String>> getDefinedSubstitutions() {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();

        for (String category : categorySubstitutionPanels.keySet()) {
            if (result.get(category) == null) {
                result.put(category, new HashMap<String, String>());
            }

            for (SingleSubstitutionPanel ssp : categorySubstitutionPanels.get(category)) {
                result.get(category).put(ssp.getToReplace(), ssp.getCurrentlySelectedValue());
            }
        }

        return result;
    }

    private JPanel createLabel(String labelContents, Font f, Color c) {
        JPanel labelPanel = new JPanel(new GridLayout(1, 1));
        labelPanel.setOpaque(false);

        JLabel newLab = new JLabel(labelContents, JLabel.LEFT);
        newLab.setOpaque(false);
        newLab.setFont(f);
        newLab.setForeground(c);

        labelPanel.add(newLab);

        return labelPanel;
    }


    class SingleSubstitutionPanel extends JPanel {

        private String toReplace;
        private String[] replacementOptions;

        private JComboBox options;

        public SingleSubstitutionPanel(String toReplace, String[] replacementOptions) {
            this.toReplace = toReplace;

            if (replacementOptions != null) {
                this.replacementOptions = new String[replacementOptions.length + 1];


                this.replacementOptions[0] = DO_NOT_REPLACE_TEXT;
                int count = 1;
                for (String r : replacementOptions) {
                    this.replacementOptions[count] = r;
                    count++;
                }
            } else {
                this.replacementOptions = new String[]{"nothing here!"};
            }

        }

        public void createGUI() {
            setBackground(UIHelper.BG_COLOR);
            setLayout(new GridLayout(1, 2));
            instantiatePanel();
        }

        public void instantiatePanel() {

            add(createLabel(toReplace, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

            options = new JComboBox(replacementOptions);
            options.setPreferredSize(new Dimension(150, 25));
            UIHelper.setJComboBoxAsHeavyweight(options);
            UIHelper.renderComponent(options, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);
            options.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    firePropertyChange("itemSelected", "", options.getSelectedItem());
                }
            });

            add(options);
        }

        public String getCurrentlySelectedValue() {
            return options.getSelectedItem().toString();
        }

        public String getToReplace() {
            return toReplace;
        }
    }
}
