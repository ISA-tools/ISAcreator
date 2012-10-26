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

package org.isatools.isacreator.gui.menu;

import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.FileType;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.api.ImportConfiguration;
import org.isatools.isacreator.gs.GSLocalFilesManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import java.util.ArrayList;
import java.util.List;

/**
 * ImportFilesMenu provides the interface to allow users to import previously saved ISATAB
 * submissions into the software for editing/viewing.
 *
 * Date: Mar 3, 2010
 *
 * @author eamonnmaguire
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 *
 */
public class ImportConfigurationMenu extends AbstractImportFilesMenu {

    private static Logger log = Logger.getLogger(ImportConfigurationMenu.class);

    @InjectedResource
    private ImageIcon panelHeader, listImage, searchButton, searchButtonOver,
            loadButton, loadButtonOver, exitButtonSml, exitButtonSmlOver, filterLeft, filterRight;

    //private boolean initialLoadingPassed = true;

    public ImportConfigurationMenu(ISAcreatorMenu menu) {
        super(menu);
        setPreferredSize(new Dimension(400, 400));
    }

    public JPanel createAlternativeExitDisplay() {

        final JLabel exit = new JLabel(exitButtonSml, JLabel.CENTER);
        exit.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                exit.setIcon(exitButtonSml);
                confirmExitPanel.setVisible(true);
                confirmExitPanel.getParent().validate();
            }

            public void mouseEntered(MouseEvent event) {
                exit.setIcon(exitButtonSmlOver);
            }

            public void mouseExited(MouseEvent event) {
                exit.setIcon(exitButtonSml);
            }
        });

        JPanel exitCont = new JPanel(new GridLayout(1, 1));
        exitCont.setOpaque(false);
        exitCont.add(exit);

        JPanel exitPanelContainer = new JPanel(new GridLayout(1, 1));
        exitPanelContainer.add(confirmExitPanel);

        add(exitPanelContainer, BorderLayout.SOUTH);

        return exitCont;
    }

    public void getSelectedFileAndLoad() {
        if (previousFileList.getSelectedIndex() != -1) {
            // select file from list
            for (File candidate : previousFiles) {
                if (candidate.getName()
                        .equals(previousFileList.getSelectedValue()
                                .toString())) {

                    menu.showProgressPanel("attempting to load configuration in " +
                            candidate.getName() + "...");

                    loadFile(ISAcreator.DEFAULT_CONFIGURATIONS_DIRECTORY + File.separator +
                            candidate.getName() + File.separator);
                }
            }
        }
    }


    public void loadFile(final String dir) {

        // show infinite panel. if successful, hide glass panel and show either the errors, or the data entry panel containing the submission
        Thread performer = new Thread(new Runnable() {
            public void run() {
                // success, so load
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        ImportConfiguration importConfiguration = new ImportConfiguration(dir);
                        boolean successful = importConfiguration.loadConfiguration();

                        System.out.println("Loaded configuration");
                        menu.stopProgressIndicator();
                        if (!successful) {
                            menu.resetViewAfterProgress();
                            System.out.println("Problem encountered");
                            problemReport.setText(importConfiguration.getProblemLog());
                            problemScroll.setVisible(true);
                            revalidate();
                            repaint();
                        } else {
                            menu.resetViewAfterProgress();
                            menu.hideGlassPane();

                            //TODO remove
                            /*
                            if (!initialLoadingPassed) {
                                log.info("loading hasn't been performed before, so now going to authentication screen");
                                menu.changeView(menu.getAuthenticationGUI());
                            } else {


                                log.info("loading has been performed before, so now going to menu");
                                menu.changeView(menu.getMainMenuGUI());
                            }
                            */

                            //Assuming this menu was only created, if it was necessary to load the configuration files through the GUI (ie. not given as parameter when launching ISAcreator)
                            //After loading configuration files, check if the isatabDir or isatabFiles where given as parameter
                            //If GS mode, download the files and load them
                            //In other modes (NORMAL or LIGHT), just load the files

                            //TODO dependency with GS stuff
                            if (ISAcreatorCLArgs.mode()== Mode.GS && !menu.isUserLoggedIn()){

                               if (ISAcreatorCLArgs.isatabDir()!=null || ISAcreatorCLArgs.isatabFiles()!=null){

                                List<ErrorMessage> errors = GSLocalFilesManager.downloadFiles(menu.getAuthentication());


                                if (!errors.isEmpty()){

                                    ISAFileErrorReport error = new ISAFileErrorReport("", FileType.INVESTIGATION, errors);
                                    java.util.List<ISAFileErrorReport> list = new ArrayList<ISAFileErrorReport>();
                                    list.add(error);

                                    ErrorMenu errorMenu = new ErrorMenu(menu, list, false, ImportConfigurationMenu.this);
                                    errorMenu.createGUI();

                                } else {
                                    menu.loadFiles(ISAcreatorCLArgs.isatabDir());
                                }

                                } else {
                                   //the ISAtab files were not given as parameter, show main menu
                                   menu.changeView(menu.getMainMenuGUI());
                               }

                            } else{
                                //mode is not GS
                                if (ISAcreatorCLArgs.isatabDir()!=null){
                                    menu.loadFiles(ISAcreatorCLArgs.isatabDir());
                                }else {
                                    menu.changeView(menu.getMainMenuGUI());
                                }
                            }

                            }//else

                            problemScroll.setVisible(false);
                            //initialLoadingPassed = true;
                        }
                }
                );
            }
        }
        );
        performer.start();
    }

    public File[] getPreviousFiles() {

        previousFileList.clearItems();

        File f = new File(ISAcreator.DEFAULT_CONFIGURATIONS_DIRECTORY);

        if (!f.exists() || !f.isDirectory()) {
            f.mkdir();
        }

        previousFiles = f.listFiles();

        for (File prevSubmission : previousFiles) {
            if (prevSubmission.isDirectory()) {
                previousFileList.addItem(prevSubmission.getName());
            }
        }

        return previousFiles;
    }

    public String getBorderTitle() {
        return "selected configuration to load";
    }

    public ImageIcon getPanelHeaderImage() {
        return panelHeader;
    }

    public void setListRenderer() {
        previousFileList.setCellRenderer(new ImportFilesListCellRenderer(listImage));
    }

    public ImageIcon getSearchButton() {
        return searchButton;
    }

    public ImageIcon getSearchButtonOver() {
        return searchButtonOver;
    }

    public ImageIcon getLoadButton() {
        return loadButton;
    }

    public ImageIcon getLoadButtonOver() {
        return loadButtonOver;
    }

    @Override
    public ImageIcon getLeftFilterImage() {
        return filterLeft;
    }

    @Override
    public ImageIcon getRightFilterImage() {
        return filterRight;
    }




}
