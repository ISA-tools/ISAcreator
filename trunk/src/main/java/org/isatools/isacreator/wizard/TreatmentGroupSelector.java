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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.gui.DataEntryWrapper;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;


public class TreatmentGroupSelector extends JPanel {
    private List<TreatmentGroupInformation> treatmentGroupDefinitions;
    private DataEntryWrapper dew;
    private int numTreatmentGroupsSpecified;
    private Collection<String> treatmentGroups;
    private JLabel status;
    private String defaultReplicates;
    private MouseListener[] listeners;

    public TreatmentGroupSelector(DataEntryWrapper dew, int numTreatmentGroupsSpecified,
                                  final Collection<String> treatmentGroups, String defaultReplicates) {
        this.dew = dew;
        this.numTreatmentGroupsSpecified = numTreatmentGroupsSpecified;
        this.treatmentGroups = treatmentGroups;
        this.defaultReplicates = defaultReplicates;
        treatmentGroupDefinitions = new ArrayList<TreatmentGroupInformation>();
        listeners = new MouseListener[2];
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setLayout(new BorderLayout());
                setPreferredSize(new Dimension(500, (int) (getHeight() * 0.80)));
                setOpaque(false);
                instantiatePanel();
                setVisible(true);
            }
        });
    }

    public void instantiatePanel() {
        // create header panel telling user why they are here...
        JPanel headerPanel = new JPanel(new GridLayout(1, 1));
        headerPanel.setOpaque(false);

        String labelText = "<html>";

        if (numTreatmentGroupsSpecified == treatmentGroups.size()) {
            labelText += "if the number of replicates varies depending on the factor group, please modify these replicate numbers here.";
        } else {
            labelText += "since the number of factor groups you have specified (" +
                    numTreatmentGroupsSpecified +
                    ") differs from the number of factor groups " +
                    "calculated from the cross product of factors and their levels (" +
                    treatmentGroups.size() +
                    "), please select the factor groups used in " +
                    "this study and modify the number of replicates per factor group <i>(if applicable)</i>.";
        }

        labelText += "</html>";

        JLabel info = UIHelper.createLabel(labelText, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

        info.setPreferredSize(new Dimension(550, 70));

        headerPanel.add(info);

        add(headerPanel, BorderLayout.NORTH);

        JPanel treatmentsToSelect = new JPanel();
        treatmentsToSelect.setLayout(new BoxLayout(treatmentsToSelect, BoxLayout.PAGE_AXIS));
        treatmentsToSelect.setBackground(UIHelper.BG_COLOR);

        for (String group : treatmentGroups) {
            TreatmentGroupInformation tgi = new TreatmentGroupInformation(group);
            treatmentGroupDefinitions.add(tgi);
            treatmentsToSelect.add(tgi);
        }

        JScrollPane definitionsScroller = new JScrollPane(treatmentsToSelect, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        definitionsScroller.setBackground(UIHelper.BG_COLOR);
        definitionsScroller.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.DARK_GREEN_COLOR, 9),
                "generated factor groups",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.DARK_GREEN_COLOR));

        definitionsScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        definitionsScroller.setPreferredSize(new Dimension(600, 250));

        IAppWidgetFactory.makeIAppScrollPane(definitionsScroller);

        add(definitionsScroller, BorderLayout.CENTER);

        // create south panel to accept and proceed :o)
        JPanel southPanel = new JPanel(new GridLayout(1, 1));
        southPanel.setOpaque(false);

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setBackground(UIHelper.BG_COLOR);

        status = new JLabel("", JLabel.LEFT);
        status.setPreferredSize(new Dimension(400, 40));

        status.setForeground(UIHelper.RED_COLOR);
        status.setFont(UIHelper.VER_12_PLAIN);
        status.setOpaque(false);

        statusPanel.add(status);

        southPanel.add(statusPanel);


        DataEntryWrapper.backButton.setIcon(DataEntryWrapper.back);
        listeners[0] = new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                firePropertyChange("treatmentGroupsNotSelected", "",
                        "treatment");
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
                DataEntryWrapper.backButton.setIcon(DataEntryWrapper.backOver);
            }

            public void mouseExited(MouseEvent event) {
                DataEntryWrapper.backButton.setIcon(DataEntryWrapper.back);
            }
        };

        dew.assignListenerToLabel(DataEntryWrapper.backButton, listeners[0]);

        listeners[1] = new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                String checkValidResult = checkValidTreatments();
                int numberOfCheckedItems = getNumberOfCheckedItems();
                if (checkValidResult == null) {
                    if (numberOfCheckedItems == numTreatmentGroupsSpecified) {
                        Map<Integer, TreatmentReplicate> selectedGroups = new HashMap<Integer, TreatmentReplicate>();

                        int count = 0;

                        for (TreatmentGroupInformation t : treatmentGroupDefinitions) {
                            if (t.getUseGroup()) {
                                selectedGroups.put(count, new TreatmentReplicate(t.getTreatmentGroup(), Integer.valueOf(t.getNumReplicates())));
                                count++;
                            }
                        }

                        status.setText("");

                        firePropertyChange("treatmentGroupsSelected", null,
                                selectedGroups);
                    } else {
                        status.setText(
                                "<html>the number of factor groups selected <b>(" +
                                        numberOfCheckedItems +
                                        ")</b> does not match the number specified <b>(" +
                                        numTreatmentGroupsSpecified + ")</b></html>");
                    }
                } else {
                    status.setText(checkValidResult);
                }
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
                DataEntryWrapper.nextButton.setIcon(DataEntryWrapper.nextOver);
            }

            public void mouseExited(MouseEvent event) {
                DataEntryWrapper.nextButton.setIcon(DataEntryWrapper.next);
            }
        };

        dew.assignListenerToLabel(DataEntryWrapper.nextButton, listeners[1]);

        add(southPanel, BorderLayout.SOUTH);
    }

    private int getNumberOfCheckedItems() {
        int checkedItems = 0;

        for (TreatmentGroupInformation tgi : treatmentGroupDefinitions) {
            if (tgi.getUseGroup()) {
                checkedItems++;
            }
        }

        return checkedItems;
    }

    private String checkValidTreatments() {
        for (TreatmentGroupInformation tgi : treatmentGroupDefinitions) {
            if (!tgi.isReplicateNumValid()) {
                return "<html>The replicate value entered for <b>" + tgi.getTreatmentGroup() + "</b> is invalid! Must be numeric and greater than 0!</html>";
            }
        }
        return null;
    }

    public MouseListener[] getListeners() {
        return listeners;
    }

    class TreatmentGroupInformation extends JPanel {

        private String treatmentGroup;

        private JTextField numReplicates;
        private JCheckBox useGroup;

        public TreatmentGroupInformation(String treatmentGroup) {
            this.treatmentGroup = treatmentGroup;
            setBackground(UIHelper.BG_COLOR);

            instantiatePanel();
        }

        private void instantiatePanel() {

            JPanel container = new JPanel();
            container.setLayout(new GridLayout(1, 2));

            container.setPreferredSize(new Dimension(545, 30));

            container.setBackground(UIHelper.BG_COLOR);

            numReplicates = new JTextField(defaultReplicates);
            numReplicates.setSize(new Dimension(25, 20));

            final JLabel replicatesLab = UIHelper.createLabel("# Replicates", UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR);
            replicatesLab.setHorizontalAlignment(SwingConstants.LEFT);

            UIHelper.renderComponent(numReplicates, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, false);
            numReplicates.setHorizontalAlignment(SwingConstants.LEFT);
            numReplicates.setToolTipText("<html><b>Number of replicates</b><p>The number of replicates performed on this factor group</p></html>");

            useGroup = new JCheckBox("<html><div align=\"left\">" + treatmentGroup.trim() + "</div></html>", true);
//			useGroup.setPreferredSize(new Dimension(400, 25));
            UIHelper.renderComponent(useGroup, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
            useGroup.setToolTipText("<html><b>Use this factor group</b><p>Is this factor group used in the study?</p></html>");
            useGroup.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    numReplicates.setEnabled(useGroup.isSelected());
                    replicatesLab.setEnabled(useGroup.isSelected());

                }
            });
            if (numTreatmentGroupsSpecified == treatmentGroups.size()) {
                JLabel groupLab = UIHelper.createLabel(treatmentGroup.trim(), UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR);
                groupLab.setHorizontalAlignment(SwingConstants.LEFT);
                container.add(groupLab, BorderLayout.WEST);
            } else {
                container.add(useGroup, BorderLayout.WEST);
            }

            JPanel replicatesCont = new JPanel();
            replicatesCont.setLayout(new BoxLayout(replicatesCont, BoxLayout.LINE_AXIS));
            replicatesCont.setOpaque(false);
            replicatesCont.setSize(new Dimension(100, 20));

            replicatesCont.add(replicatesLab);

            replicatesCont.add(numReplicates);

            replicatesCont.add(Box.createHorizontalStrut(150));

            container.add(replicatesCont);


            add(container, BorderLayout.WEST);
        }

        public String getNumReplicates() {
            return numReplicates.getText().trim();
        }

        public boolean getUseGroup() {
            return useGroup.isSelected();
        }

        public String getTreatmentGroup() {
            return treatmentGroup;
        }

        public boolean isReplicateNumValid() {
            if (useGroup.isSelected()) {
                try {
                    int i = Integer.valueOf(numReplicates.getText().trim());
                    return i > 0;
                } catch (NumberFormatException nfe) {
                    return false;
                }
            }
            return true;
        }
    }
}
