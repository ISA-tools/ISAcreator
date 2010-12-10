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
import org.isatools.isacreator.common.CustomTextField;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.mgrast.model.ExternalResources;
import org.isatools.isacreator.mgrast.model.SampleExternalIds;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * SampleInfoPane
 *
 * @author eamonnmaguire
 * @date Sep 24, 2010
 */


public class SampleInfoPane extends JPanel {

    @InjectedResource
    private ImageIcon externalReferencesHeader, externalReferenceInfo, key;

    private ExtendedJList sampleList;
    private List<SampleExternalIds> samples;

    private JLabel sampleReferenceInformation;

    private SampleExternalIds currentSample;

    private CustomTextField goldIdField;

    public SampleInfoPane(List<SampleExternalIds> samples) {
        this.samples = samples;
        ResourceInjector.get("exporters-package.style").inject(this);
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        createAndAddSampleList();
        updateSampleReferenceInfo();
        createAndAddIDPane();
        updateIdFields();
    }


    private void createAndAddSampleList() {

        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(315, 300));
        container.setBackground(UIHelper.BG_COLOR);

        sampleList = new ExtendedJList(new ExternalIdListCellRenderer(), true);

        updateListContents();


        if (sampleList.getItems().size() > 0) {
            try {
                sampleList.setSelectedIndex(0);
                currentSample = samples.get(sampleList.getSelectedIndex());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sampleList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                currentSample = (SampleExternalIds) propertyChangeEvent.getNewValue();
                updateIdFields();
                goldIdField.getTextField().setEnabled(true);
            }
        });

        sampleList.addPropertyChangeListener("zeroTerms", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                currentSample = null;
                goldIdField.setText("no sample selected");
                goldIdField.getTextField().setEnabled(false);
            }
        });

        JScrollPane historyScroll = new JScrollPane(sampleList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        historyScroll.setBorder(new EmptyBorder(1, 1, 1, 1));

        IAppWidgetFactory.makeIAppScrollPane(historyScroll);


        UIHelper.renderComponent(sampleList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        Box fieldContainer = Box.createHorizontalBox();
        fieldContainer.add(sampleList.getFilterField());
        fieldContainer.add(new ClearFieldUtility(sampleList.getFilterField()));

        container.add(fieldContainer, BorderLayout.NORTH);
        container.add(historyScroll, BorderLayout.CENTER);

        Box infoPanel = Box.createVerticalBox();
        infoPanel.setBackground(UIHelper.BG_COLOR);

        JLabel infoKey = new JLabel(key);
        infoKey.setHorizontalAlignment(SwingConstants.LEFT);

        sampleReferenceInformation = UIHelper.createLabel("", UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR, SwingConstants.LEFT);

        infoPanel.add(infoKey);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(sampleReferenceInformation);

        container.add(infoPanel, BorderLayout.SOUTH);
        container.setBorder(new TitledBorder(new RoundedBorder(UIHelper.GREY_COLOR, 3), "Samples",
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD,
                UIHelper.GREY_COLOR));

        add(container, BorderLayout.WEST);
    }

    private void updateIdFields() {
        if (currentSample != null) {
            goldIdField.setText(currentSample.getValueByExternalResource(ExternalResources.GOLD));
        }
    }

    private void createAndAddIDPane() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);
        container.setPreferredSize(new Dimension(325, 300));

        Box idsContainer = Box.createVerticalBox();

        JLabel headerImage = new JLabel(externalReferencesHeader);
        headerImage.setHorizontalAlignment(SwingConstants.RIGHT);

        idsContainer.add(UIHelper.wrapComponentInPanel(headerImage));
        idsContainer.add(Box.createVerticalStrut(10));

        JLabel infoImage = new JLabel(externalReferenceInfo);
        infoImage.setHorizontalAlignment(SwingConstants.LEFT);

        idsContainer.add(UIHelper.wrapComponentInPanel(infoImage));
        idsContainer.add(Box.createVerticalStrut(5));

        goldIdField = new CustomTextField(ExternalResources.GOLD.getUiId(), false,
                UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, new Dimension(150, 20), false);

        goldIdField.getTextField().getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent event) {

                setValidityIndicator();
            }

            public void removeUpdate(DocumentEvent event) {
                setValidityIndicator();
            }

            public void changedUpdate(DocumentEvent event) {
                setValidityIndicator();
            }

            private void setValidityIndicator() {
                if (currentSample != null) {
                    currentSample.getExternalIds().put(ExternalResources.GOLD, goldIdField.getText());
                    updateSampleReferenceInfo();
                    sampleList.validate();
                    sampleList.repaint();
                }
            }
        });

        idsContainer.add(goldIdField);
        container.add(idsContainer, BorderLayout.NORTH);
        add(container, BorderLayout.EAST);
    }

    private void updateListContents() {
        sampleList.getItems().clear();
        for (SampleExternalIds seId : samples) {
            sampleList.addItem(seId);
        }
    }

    private void updateSampleReferenceInfo() {

        int annotatedCount = 0;
        for (SampleExternalIds seId : samples) {
            for (ExternalResources er : seId.getExternalIds().keySet()) {
                if (!seId.getExternalIds().get(er).trim().equals("")) {
                    annotatedCount++;
                }
            }
        }


        StringBuffer information = new StringBuffer();
        information.append("<html><b>").append(samples.size())
                .append("</b> samples of which <b>").append(annotatedCount).append("</b> have ids assigned</html>");

        sampleReferenceInformation.setText(information.toString());
    }
}
