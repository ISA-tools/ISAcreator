package org.isatools.isacreator.assayselection;

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

import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.List;

/**
 * Dialog container for the add assay interface.
 * This means it can appear from the top of the screen, sliding in.
 */
public class AssaySelectionDialog extends JDialog {

    private static final int PREDEFINED = 0;
    private static final int CUSTOM = 1;

    private AssaySelectionUI assaySelectionUI;
    private CustomAssaySelectionUI customAssaySelectionUI;

    private Map<String, List<String>> measurementsToTechnologies;

    // this will hold either the predefined assay selection panel or the custom assay panel.
    private Container swappableContainer;
    private JLabel predefinedAssayTab;
    private JLabel customAssayTab;

    private int pageInView = PREDEFINED;

    public AssaySelectionDialog(Map<String, List<String>> measurementsToTechnologies) {

        ResourceInjector.get("assayselection-package.style").inject(this);

        this.measurementsToTechnologies = measurementsToTechnologies;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 510));
    }

    public void createGUI() {

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));
        swappableContainer = new JPanel();

        assaySelectionUI = new AssaySelectionUI(measurementsToTechnologies);
        assaySelectionUI.createGUI();

        customAssaySelectionUI = new CustomAssaySelectionUI();
        customAssaySelectionUI.createGUI();

        JPanel tabPanel = new JPanel(new GridLayout(1, 3));
        tabPanel.setOpaque(false);
//        int top, int left, int bottom, int right
        tabPanel.setBorder(new EmptyBorder(15, 5, 20, 0));

        predefinedAssayTab = UIHelper.createLabel("Select from predefined assays", UIHelper.VER_14_BOLD);
        customAssayTab = UIHelper.createLabel("Create a custom assay", UIHelper.VER_14_PLAIN);

        predefinedAssayTab.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                swapContainers(assaySelectionUI);
                customAssayTab.setFont(UIHelper.VER_12_PLAIN);
                predefinedAssayTab.setFont(UIHelper.VER_12_BOLD);
            }
        });

        customAssayTab.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                swapContainers(customAssaySelectionUI);
                customAssayTab.setFont(UIHelper.VER_12_BOLD);
                predefinedAssayTab.setFont(UIHelper.VER_12_PLAIN);
            }
        });

        // check if there is a generic configuration available first.
        if (ConfigurationManager.searchMappingsForMatch("*", "*") != null) {
            tabPanel.add(predefinedAssayTab);
            tabPanel.add(customAssayTab);
        }

        add(tabPanel, BorderLayout.NORTH);

        swapContainers(assaySelectionUI);

        add(swappableContainer);

        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    public void clearSelectedAssays() {
        assaySelectionUI.clearSelectedAssays();
    }

    private Container createSouthPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(UIHelper.EMPTY_BORDER);
        southPanel.setPreferredSize(new Dimension(750, 40));

        JButton closeButton = new FlatButton(ButtonType.RED, "Cancel");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                closeWindow();
            }
        });

        JButton addAssay = new FlatButton(ButtonType.GREEN, "Confirm");
        addAssay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if ((pageInView == CUSTOM && customAssaySelectionUI.valid()) || pageInView == PREDEFINED) {
                    firePropertyChange("assaysChosen", false, true);
                    closeWindow();
                }
            }
        });

        southPanel.add(closeButton, BorderLayout.WEST);
        southPanel.add(addAssay, BorderLayout.EAST);

        return southPanel;
    }

    private void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ApplicationManager.getCurrentApplicationInstance().hideSheet();
            }
        });
    }

    public List<AssaySelection> getSelectedAssays() {
        return pageInView == PREDEFINED ? assaySelectionUI.getAssaysToDefine() : customAssaySelectionUI.getAssaysToDefine();
    }

    private void swapContainers(final JPanel newContainer) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (newContainer != null) {
                    swappableContainer.removeAll();
                    swappableContainer.add(newContainer);
                    swappableContainer.repaint();
                    swappableContainer.validate();

                    if (newContainer instanceof CustomAssaySelectionUI) {
                        pageInView = CUSTOM;
                    } else {
                        pageInView = PREDEFINED;
                    }
                }
            }
        });
    }
}
