package org.isatools.isacreator.validateconvert.ui;
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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.errorreporter.ui.utils.UIHelper;
import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isatab.gui_invokers.AllowedConversions;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by the ISA team
 */
public class ConvertUI extends JPanel {


    private Collection<ConversionTarget> conversionTargets;


    private List<ConversionTargetInformationPanel> conversionTargetInformationPanels;
    private FileSelectionPanel fileSelectionPanel;

    @InjectedResource
    private ImageIcon startConversion, startConversionOver;

    public ConvertUI(Collection<ConversionTarget> conversionTargets) {

        ResourceInjector.get("validateconvert-package.style").inject(this);

        this.conversionTargets = conversionTargets;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        createSelectionPane();
    }

    private void createSelectionPane() {
        JPanel conversionTargetContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conversionTargetContainer.setOpaque(false);

        conversionTargetInformationPanels = new ArrayList<ConversionTargetInformationPanel>();


        for (ConversionTarget conversionTarget : conversionTargets) {
            ConversionTargetInformationPanel conversionTargetInformationPanel = new ConversionTargetInformationPanel(conversionTarget);

            conversionTargetInformationPanel.addPropertyChangeListener("conversionTargetSelected", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    // clear selection on all panels...
                    for (ConversionTargetInformationPanel panel : conversionTargetInformationPanels) {
                        panel.clearSelection();
                    }
                }
            });

            conversionTargetInformationPanels.add(conversionTargetInformationPanel);
            conversionTargetContainer.add(conversionTargetInformationPanel);
        }

        JScrollPane scroller = new JScrollPane(conversionTargetContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);
        scroller.setBorder(new EmptyBorder(1, 1, 1, 1));
        scroller.setPreferredSize(new Dimension(400, 80));

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        container.add(UIHelper.wrapComponentInPanel(
                UIHelper.createLabel("<html><b>1. Choose your conversion target(s):</b></html>",
                        UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT)));

        container.add(Box.createVerticalStrut(5));

        container.add(UIHelper.wrapComponentInPanel(
                UIHelper.createLabel("<html><i>we’ve filtered out conversion targets not applicable to your data. just click on a box to select the format.</i></html>",
                        UIHelper.VER_9_PLAIN, UIHelper.GREY_COLOR, SwingConstants.LEFT)));

        container.add(Box.createVerticalStrut(15));
        container.add(scroller);
        container.add(Box.createVerticalStrut(15));

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose conversion output directory");
        fileChooser.setControlButtonsAreShown(true);
        fileChooser.setApproveButtonText("Put it here");

        fileSelectionPanel = new FileSelectionPanel("2. Choose the output directory",
                fileChooser, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);

        container.add(fileSelectionPanel);

        container.add(Box.createVerticalStrut(10));

        container.add(createButtonPanel());

        add(container, BorderLayout.NORTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonContainer = new JPanel(new BorderLayout());

        final JLabel convertButton = new JLabel(startConversion);
        convertButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                convertButton.setIcon(startConversionOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                convertButton.setIcon(startConversion);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                convertButton.setIcon(startConversion);

                if (fileSelectionPanel.getSelectedFilePath().equals("") &&
                        !fileSelectionPanel.getSelectedFilePath().equals("Please select a directory!") &&
                        !fileSelectionPanel.getSelectedFilePath().equals("Please select a valid location!")) {

                    if (!(new File(fileSelectionPanel.getSelectedFilePath()).exists())) {
                        fileSelectionPanel.setText("Please select a valid location!");
                    } else {
                        fileSelectionPanel.setText("Please select a directory!");
                    }

                    fileSelectionPanel.setWarning(true);
                } else {
                    fileSelectionPanel.setWarning(false);
                    firePropertyChange("startConversion", false, true);
                }
            }
        });

        buttonContainer.add(convertButton, BorderLayout.CENTER);

        return buttonContainer;
    }

    public AllowedConversions getConversionToPerform() {
        for (ConversionTargetInformationPanel panel : conversionTargetInformationPanels) {
            if (panel.getConversionTarget().isSelected()) {
                return panel.getConversionTarget().getTarget();
            }
        }

        return AllowedConversions.ALL;
    }

    public String getOutputLocation() {
        return fileSelectionPanel.getSelectedFilePath();
    }

}
