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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.managers.ApplicationManager;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
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

    @InjectedResource
    private ImageIcon closeWindowIcon, closeWindowIconOver, doneIcon, doneIconOver, buttonPanelFiller;

    private AssaySelectionUI assaySelectionUI;
    private Map<String, List<String>> measurementsToTechnologies;

    public AssaySelectionDialog(Map<String, List<String>> measurementsToTechnologies) {

        ResourceInjector.get("assayselection-package.style").inject(this);

        this.measurementsToTechnologies = measurementsToTechnologies;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 510));
    }

    public void createGUI() {

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        assaySelectionUI = new AssaySelectionUI(measurementsToTechnologies);

        assaySelectionUI.createGUI();

        add(Box.createVerticalStrut(10), BorderLayout.NORTH);

        add(assaySelectionUI);

        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    public void clearSelectedAssays() {
        assaySelectionUI.clearSelectedAssays();
    }

    private Container createSouthPanel() {
        Box southPanel = Box.createHorizontalBox();

        final JLabel closeButton = new JLabel(closeWindowIcon);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIcon);
                closeWindow();
            }
        });

        final JLabel addAssay = new JLabel(doneIcon);
        addAssay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addAssay.setIcon(doneIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addAssay.setIcon(doneIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                firePropertyChange("assaysChosen", false, true);
                closeWindow();
            }
        });

        southPanel.add(closeButton);
        southPanel.add(new JLabel(buttonPanelFiller));
        southPanel.add(addAssay);

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
        return assaySelectionUI.getAssaysToDefine();
    }

}
