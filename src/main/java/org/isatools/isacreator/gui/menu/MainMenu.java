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

package org.isatools.isacreator.gui.menu;

import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MainMenuGUI Panel provides the initial navigation options to the user to either:
 * Create a new Study
 * Load an existing ISATAB file
 * Merge ISATab
 * Change Settings
 * Load existing configurations
 * Logout
 * Exit Program
 *
 * @author Eamonn Maguire
 * @date Mar 3, 2010
 */


public class MainMenu extends MenuUIComponent {

    private JLabel newISA, merge, loadPrev, settingsButton, loadAnotherConfiguration, logoutButton;

    @InjectedResource
    public ImageIcon panelHeader, createNew, createNewOver, mergeFiles,
            mergeFilesOver, settings, settingsOver, loadExisting, loadExistingOver,
            loadConfiguration, loadConfigurationOver, logout, logoutOver, exit, exitOver;


    public MainMenu(ISAcreatorMenu menu) {
        super(menu);
        setPreferredSize(new Dimension(400, 400));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        Box topContainer = Box.createVerticalBox();
        topContainer.setOpaque(false);

        JPanel mainMenuPanel = new JPanel(new GridLayout(1, 1));
        mainMenuPanel.setOpaque(false);
        mainMenuPanel.add(new JLabel(panelHeader, JLabel.RIGHT));

        topContainer.add(mainMenuPanel);
        topContainer.add(Box.createVerticalStrut(10));

        topContainer.add(Box.createGlue());

        Box menuItems = Box.createVerticalBox();
        menuItems.setOpaque(false);
        menuItems.add(Box.createVerticalStrut(10));

        newISA = new JLabel(createNew,
                JLabel.LEFT);
        newISA.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                newISA.setIcon(createNew);
                confirmExitPanel.setVisible(false);
                menu.changeView(menu.getCreateISAMenuGUI());
            }

            public void mouseEntered(MouseEvent event) {
                newISA.setIcon(createNewOver);
            }

            public void mouseExited(MouseEvent event) {
                newISA.setIcon(createNew);
            }
        });
        menuItems.add(newISA);
        menuItems.add(Box.createVerticalStrut(10));

        loadPrev = new JLabel(loadExisting,
                JLabel.LEFT);
        loadPrev.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                loadPrev.setIcon(loadExisting);
                confirmExitPanel.setVisible(false);
                menu.getImportISAGUI().getPreviousFiles();
                menu.changeView(menu.getImportISAGUI());
            }

            public void mouseEntered(MouseEvent event) {
                loadPrev.setIcon(loadExistingOver);
            }

            public void mouseExited(MouseEvent event) {
                loadPrev.setIcon(loadExisting);
            }
        });
        menuItems.add(loadPrev);
        menuItems.add(Box.createVerticalStrut(10));

        merge = new JLabel(mergeFiles,
                JLabel.LEFT);
        merge.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                merge.setIcon(mergeFiles);
                confirmExitPanel.setVisible(false);
                menu.getMergeStudiesGUI().createGUI();
                menu.getMergeStudiesGUI().changeView();
                menu.getMain().setCurrentPage(menu.getMergeStudiesGUI());
            }

            public void mouseEntered(MouseEvent event) {
                merge.setIcon(mergeFilesOver);
            }

            public void mouseExited(MouseEvent event) {
                merge.setIcon(mergeFiles);
            }
        });
        menuItems.add(merge);
        menuItems.add(Box.createVerticalStrut(10));

        settingsButton = new JLabel(settings,
                JLabel.LEFT);
        settingsButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                settingsButton.setIcon(settings);
                confirmExitPanel.setVisible(false);
                menu.getSettings().createGUI();
                menu.getSettings().changeView();
                menu.getMain().setCurrentPage(menu.getSettings());
            }

            public void mouseEntered(MouseEvent event) {
                settingsButton.setIcon(settingsOver);
            }

            public void mouseExited(MouseEvent event) {
                settingsButton.setIcon(settings);
            }
        });
        menuItems.add(settingsButton);
        menuItems.add(Box.createVerticalStrut(10));

        loadAnotherConfiguration = new JLabel(loadConfiguration,
                JLabel.LEFT);
        loadAnotherConfiguration.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                loadAnotherConfiguration.setIcon(loadConfiguration);
                confirmExitPanel.setVisible(false);
                menu.getImportConfigurationGUI().getPreviousFiles();
                menu.changeView(menu.getImportConfigurationGUI());
            }

            public void mouseEntered(MouseEvent event) {
                loadAnotherConfiguration.setIcon(loadConfigurationOver);
            }

            public void mouseExited(MouseEvent event) {
                loadAnotherConfiguration.setIcon(loadConfiguration);
            }
        });
        menuItems.add(loadAnotherConfiguration);
        menuItems.add(Box.createVerticalStrut(10));

        logoutButton = new JLabel(logout,
                JLabel.LEFT);
        logoutButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                logoutButton.setIcon(MainMenu.this.logout);
                confirmExitPanel.setVisible(false);
                menu.getMain().setCurrentUser(null);
                menu.changeView(menu.getAuthenticationGUI());
            }

            public void mouseEntered(MouseEvent event) {
                logoutButton.setIcon(logoutOver);
            }

            public void mouseExited(MouseEvent event) {
                logoutButton.setIcon(MainMenu.this.logout);
            }
        });
        menuItems.add(logoutButton);
        menuItems.add(Box.createVerticalStrut(10));

        final JLabel exitProgram = new JLabel(exit,
                JLabel.LEFT);
        exitProgram.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                exitProgram.setIcon(exit);
                confirmExitPanel.setVisible(true);
            }

            public void mouseEntered(MouseEvent event) {
                exitProgram.setIcon(exitOver);
            }

            public void mouseExited(MouseEvent event) {
                exitProgram.setIcon(exit);
            }
        });
        menuItems.add(exitProgram);
        menuItems.add(Box.createVerticalStrut(10));

        JPanel northPanel = new JPanel();
        northPanel.add(topContainer, BorderLayout.NORTH);
        northPanel.add(menuItems, BorderLayout.CENTER);
        northPanel.add(confirmExitPanel, BorderLayout.SOUTH);

        northPanel.setOpaque(false);
        add(northPanel, BorderLayout.CENTER);
    }
}
