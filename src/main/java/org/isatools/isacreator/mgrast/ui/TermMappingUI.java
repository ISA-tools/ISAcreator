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

package org.isatools.isacreator.mgrast.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.mgrast.model.ConfidenceLevel;
import org.isatools.isacreator.mgrast.model.FieldMapping;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Set;

/**
 * TermMappingUI
 *
 * @author eamonnmaguire
 * @date Sep 24, 2010
 */


public class TermMappingUI extends JPanel {

    @InjectedResource
    private ImageIcon confidenceKey, attentionIcon, confirmIcon, confirmIconOver, rejectIcon, rejectIconOver, mappedToIcon;

    private ExtendedJList isatabFieldsList;
    private ExtendedJList mgrastFieldsList;

    private FieldMapping currentlySelectedField;

    private JPanel questionPanel;

    private Set<String> mgRastConcepts;
    private Map<String, FieldMapping> fieldMappings;


    public TermMappingUI(Map<String, FieldMapping> fieldMappings, Set<String> mgRastConcepts) {
        ResourceInjector.get("exporters-package.style").inject(this);
        this.fieldMappings = fieldMappings;
        this.mgRastConcepts = mgRastConcepts;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        createISAtabFieldListPane();
        createMGRastConceptsListPane();

        updateMGRastListContents();
        updateISAListContents();
    }

    private void createISAtabFieldListPane() {
        isatabFieldsList = new ExtendedJList(new MappingConfidenceListCellRenderer(), true);

        // todo add property change listener to detect itemselected and zeroItem events.
        isatabFieldsList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                currentlySelectedField = (FieldMapping) propertyChangeEvent.getNewValue();
                if (mgrastFieldsList != null) {

                    if (currentlySelectedField.getMgRastTermMappedTo() == null) {
                        currentlySelectedField.setMgRastTermMappedTo("");
                    }

                    if (currentlySelectedField.getMgRastTermMappedTo().equals("")) {
                        mgrastFieldsList.clearSelection();
                        checkAndDisplayAppropriateQuestion(currentlySelectedField.getMgRastTermMappedTo());
                    } else {
                        mgrastFieldsList.setSelectedValue(currentlySelectedField.getMgRastTermMappedTo(), true);
                    }
                }
            }
        });

        JPanel container = createListPanel(isatabFieldsList);
        container.setBorder(new TitledBorder(new RoundedBorder(UIHelper.GREY_COLOR, 3), "ISAtab fields",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD,
                UIHelper.GREY_COLOR));

        JLabel infoKey = new JLabel(confidenceKey);
        infoKey.setHorizontalAlignment(SwingConstants.LEFT);

        container.add(UIHelper.wrapComponentInPanel(infoKey), BorderLayout.SOUTH);
        add(container, BorderLayout.WEST);
    }

    private void createMGRastConceptsListPane() {
        mgrastFieldsList = new ExtendedJList(new MGRastConceptListCellRenderer(), false);

        questionPanel = new JPanel();
        questionPanel.setPreferredSize(new Dimension(315, 60));
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.LINE_AXIS));

        mgrastFieldsList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                checkAndDisplayAppropriateQuestion(propertyChangeEvent.getNewValue().toString());
            }
        });

        mgrastFieldsList.setAutoscrolls(true);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        JPanel listContainer = createListPanel(mgrastFieldsList);
        listContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.GREY_COLOR, 3), "MG-Rast concepts",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD,
                UIHelper.GREY_COLOR));
        updateMGRastListContents();

        container.add(listContainer);

        container.add(questionPanel, BorderLayout.SOUTH);

        add(container, BorderLayout.EAST);
    }

    private JPanel createListPanel(ExtendedJList list) {


        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setPreferredSize(new Dimension(315, 270));
        listContainer.setBackground(UIHelper.BG_COLOR);

        JScrollPane mgRastConceptScroller = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mgRastConceptScroller.setBorder(new EmptyBorder(1, 1, 1, 1));

        IAppWidgetFactory.makeIAppScrollPane(mgRastConceptScroller);

        UIHelper.renderComponent(list.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        Box fieldContainer = Box.createHorizontalBox();
        fieldContainer.add(list.getFilterField());
        fieldContainer.add(new ClearFieldUtility(list.getFilterField()));

        listContainer.add(fieldContainer, BorderLayout.NORTH);
        listContainer.add(mgRastConceptScroller, BorderLayout.CENTER);

        return listContainer;
    }

    private void updateISAListContents() {
        isatabFieldsList.getItems().clear();

        for (String isaField : fieldMappings.keySet()) {
            isatabFieldsList.addItem(fieldMappings.get(isaField));
        }

        if (isatabFieldsList.getItems().size() > 0) {
            isatabFieldsList.setSelectedIndex(0);
            if (mgrastFieldsList != null) {
                mgrastFieldsList.setSelectedValue(currentlySelectedField.getMgRastTermMappedTo(), true);
                checkAndDisplayAppropriateQuestion(currentlySelectedField.getMgRastTermMappedTo());
            }
        }
    }

    /**
     * show confirm box if confidence level is below 100% or change info if the item selected is different
     * to that already stored.
     *
     * @param newValue - mgRast value entered.
     */
    private void checkAndDisplayAppropriateQuestion(String newValue) {

        if (newValue.equals(currentlySelectedField.getMgRastTermMappedTo()) && !newValue.equals("")) {
            if (currentlySelectedField.getConfidenceLevel() != ConfidenceLevel.ONE_HUNDRED_PERCENT) {
                changeQuestionPanelContents(createConfirmMappingPanel());
            } else {
                changeQuestionPanelContents(createMappedToInfo());
            }
        } else if (newValue == null || newValue.equals("")) {
            changeQuestionPanelContents(createMappedToInfo());
        } else {
            changeQuestionPanelContents(createChangeMappingPanel());
        }
    }

    private Container createConfirmMappingPanel() {
        Box confirmMappingPane = Box.createVerticalBox();
        confirmMappingPane.setBackground(UIHelper.BG_COLOR);

        JLabel question = UIHelper.createLabel("confirm this mapping?", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR);
        question.setIcon(attentionIcon);

        confirmMappingPane.add(UIHelper.wrapComponentInPanel(question));

        confirmMappingPane.add(createMappedToInfo());

        Box mappingInfo = Box.createHorizontalBox();

        final JLabel confirmMapping = new JLabel(confirmIcon);
        confirmMapping.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                confirmMapping.setIcon(confirmIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                confirmMapping.setIcon(confirmIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                confirmMapping.setIcon(confirmIcon);
                currentlySelectedField.setMgRastTermMappedTo(mgrastFieldsList.getSelectedTerm());
                currentlySelectedField.setConfidenceLevel(ConfidenceLevel.ONE_HUNDRED_PERCENT);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        changeQuestionPanelContents(createMappedToInfo());
                        isatabFieldsList.validate();
                        isatabFieldsList.repaint();
                    }
                });
            }
        });

        final JLabel rejectMapping = new JLabel(rejectIcon);
        rejectMapping.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                rejectMapping.setIcon(rejectIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                rejectMapping.setIcon(rejectIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                rejectMapping.setIcon(rejectIcon);
                currentlySelectedField.setMgRastTermMappedTo("");
                currentlySelectedField.setConfidenceLevel(ConfidenceLevel.ZERO_PERCENT);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        changeQuestionPanelContents(createMappedToInfo());
                        isatabFieldsList.validate();
                        isatabFieldsList.repaint();
                    }
                });


            }
        });

        mappingInfo.add(Box.createHorizontalStrut(5));
        mappingInfo.add(confirmMapping);
        mappingInfo.add(rejectMapping);

        confirmMappingPane.add(mappingInfo);

        return confirmMappingPane;
    }

    private Container createMappedToInfo() {
        Box mappingInfo = Box.createHorizontalBox();
        mappingInfo.setBackground(UIHelper.BG_COLOR);

        JLabel isaField = UIHelper.createLabel(currentlySelectedField.getIsatabFieldName(), UIHelper.VER_9_BOLD, UIHelper.LIGHT_GREEN_COLOR);
        isaField.setPreferredSize(new Dimension(120, 20));

        mappingInfo.add(isaField);
        mappingInfo.add(new JLabel(mappedToIcon));

        JLabel mgRastTerm = UIHelper.createLabel(mgrastFieldsList.getSelectedTerm(), UIHelper.VER_9_BOLD, UIHelper.GREY_COLOR);
        mgRastTerm.setPreferredSize(new Dimension(120, 20));
        mappingInfo.add(mgRastTerm);

        return mappingInfo;
    }

    private Container createChangeMappingPanel() {
        Box confirmMappingPane = Box.createVerticalBox();
        confirmMappingPane.setBackground(UIHelper.BG_COLOR);

        JLabel question = UIHelper.createLabel("change to this mapping?", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR);
        question.setIcon(attentionIcon);

        confirmMappingPane.add(UIHelper.wrapComponentInPanel(question));

        confirmMappingPane.add(createMappedToInfo());

        Box mappingInfo = Box.createHorizontalBox();

        final JLabel confirmMapping = new JLabel(confirmIcon);
        confirmMapping.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                confirmMapping.setIcon(confirmIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                confirmMapping.setIcon(confirmIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                confirmMapping.setIcon(confirmIcon);
                currentlySelectedField.setMgRastTermMappedTo(mgrastFieldsList.getSelectedTerm());
                currentlySelectedField.setConfidenceLevel(ConfidenceLevel.ONE_HUNDRED_PERCENT);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        changeQuestionPanelContents(createMappedToInfo());
                        isatabFieldsList.validate();
                        isatabFieldsList.repaint();
                    }
                });
            }
        });

        final JLabel rejectMapping = new JLabel(rejectIcon);
        rejectMapping.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                rejectMapping.setIcon(rejectIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                rejectMapping.setIcon(rejectIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                rejectMapping.setIcon(rejectIcon);
                mgrastFieldsList.setSelectedValue(currentlySelectedField.getMgRastTermMappedTo(), true);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        checkAndDisplayAppropriateQuestion(currentlySelectedField.getMgRastTermMappedTo());
                        isatabFieldsList.validate();
                        isatabFieldsList.repaint();
                    }
                });
            }
        });

        mappingInfo.add(Box.createHorizontalStrut(5));
        mappingInfo.add(confirmMapping);
        mappingInfo.add(rejectMapping);

        confirmMappingPane.add(mappingInfo);

        return confirmMappingPane;
    }

    private void updateMGRastListContents() {
        mgrastFieldsList.getItems().clear();

        for (String mgRastConcept : mgRastConcepts) {
            mgrastFieldsList.addItem(mgRastConcept);
        }
    }

    private void changeQuestionPanelContents(final Container newContainer) {
        if (newContainer != null) {
            questionPanel.removeAll();
            questionPanel.add(newContainer);
            questionPanel.repaint();
            questionPanel.validate();
        }
    }


    // two extended JLists - one containing the headers from ISAcreator - the merging of the study sample table and the
    // assay table of interest. the other containing the mgrast terms.
}
