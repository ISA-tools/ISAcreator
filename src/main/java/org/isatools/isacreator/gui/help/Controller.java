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

package org.isatools.isacreator.gui.help;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         Date: 11/05/2011
 *         Time: 18:54
 */
public class Controller extends JPanel {

    private static final int ADD_STUDY_MODE = 0;
    private static final int ISATAB_ANATOMY_MODE = 1;
    private static final int ISACREATOR_ANATOMY_MODE = 2;

    private int mode = ADD_STUDY_MODE;

    @InjectedResource
    private ImageIcon addStudyTabIcon, addStudyTabIconSelected, isatabAnatomyTabIcon,
            isatabAnatomyTabIconOver, isacreatorAnatomyTabIcon, isacreatorAnatomyTabIconOver;

    private JLabel addStudyTab, isatabAnatomyTab, isacreatorAnatomyTab;


    private JPanel swappableContainer;

    private AddStudyPage addStudyPage;
    private AnatomyPage anatomyPage;


    /**
     * Will contain buttons to allow user to switch between different views. Should be a BorderLayout. North will contain buttons,
     * Center panel will contain the view.
     */

    public Controller() {
        ResourceInjector.get("gui-package.style").inject(this);
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));

        anatomyPage = new AnatomyPage();
        addStudyPage = new AddStudyPage();
        addStudyPage.addPropertyChangeListener("addStudy", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                firePropertyChange("addNewStudy", false, true);
            }
        });

        swappableContainer = new JPanel();
        swapContainers(addStudyPage);

        createTabPanel();

        add(swappableContainer, BorderLayout.CENTER);

    }

    private void createTabPanel() {
        JPanel container = new JPanel(new GridLayout(1, 3));
        container.setSize(350, 40);
        container.setBackground(UIHelper.BG_COLOR);

        addStudyTab = new JLabel(addStudyTabIconSelected);
        addStudyTab.setHorizontalAlignment(SwingConstants.RIGHT);

        addStudyTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addStudyTab.setIcon(mode == ADD_STUDY_MODE
                        ? addStudyTabIconSelected : addStudyTabIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addStudyTab.setIcon(addStudyTabIconSelected);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                mode = ADD_STUDY_MODE;
                addStudyTab.setIcon(addStudyTabIconSelected);
                swapContainers(addStudyPage);
            }
        });

        isatabAnatomyTab = new JLabel(isatabAnatomyTabIcon);
        isatabAnatomyTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                isatabAnatomyTab.setIcon(mode == ISATAB_ANATOMY_MODE
                        ? isatabAnatomyTabIconOver : isatabAnatomyTabIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                isatabAnatomyTab.setIcon(isatabAnatomyTabIconOver);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                mode = ISATAB_ANATOMY_MODE;
                isatabAnatomyTab.setIcon(isatabAnatomyTabIconOver);
                anatomyPage.setView(AnatomyPage.ISATAB);
                swapContainers(anatomyPage);
            }
        });

        isacreatorAnatomyTab = new JLabel(isacreatorAnatomyTabIcon);
        isacreatorAnatomyTab.setHorizontalAlignment(SwingConstants.LEFT);

        isacreatorAnatomyTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                isacreatorAnatomyTab.setIcon(mode == ISACREATOR_ANATOMY_MODE
                        ? isacreatorAnatomyTabIconOver : isacreatorAnatomyTabIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                isacreatorAnatomyTab.setIcon(isacreatorAnatomyTabIconOver);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                mode = ISACREATOR_ANATOMY_MODE;
                isacreatorAnatomyTab.setIcon(isacreatorAnatomyTabIconOver);
                anatomyPage.setView(AnatomyPage.ISACREATOR);
                swapContainers(anatomyPage);
            }
        });

        container.add(addStudyTab);
        container.add(isatabAnatomyTab);
        container.add(isacreatorAnatomyTab);

        add(container, BorderLayout.NORTH);
    }

    private void resetButtons() {
        isacreatorAnatomyTab.setIcon(isacreatorAnatomyTabIcon);
        isatabAnatomyTab.setIcon(isatabAnatomyTabIcon);
        addStudyTab.setIcon(addStudyTabIcon);
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }
}
