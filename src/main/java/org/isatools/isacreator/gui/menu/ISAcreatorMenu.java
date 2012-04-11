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

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.GenericPanel;
import org.isatools.isacreator.effects.InfiniteImageProgressPanel;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.ISAcreatorBackground;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.mergeutil.MergeFilesUI;
import org.isatools.isacreator.settings.SettingsUtil;

import javax.swing.*;
import java.awt.*;


/**
 * LoginPage
 * Provides all the elements for logging into the system, creating a profile, and navigating
 * around the program.
 *
 * @author Eamonn Maguire
 */
public class ISAcreatorMenu extends JLayeredPane {

    // todo refactor menu to make this class much smaller!
    public static final int SHOW_MAIN = 0;
    public static final int SHOW_LOGIN = 1;
    public static final int SHOW_CREATE_ISA = 2;
    public static final int SHOW_IMPORT_CONFIGURATION = 3;
    public static final int SHOW_UNSUPPORTED_JAVA = 4;


    private Authentication authGUI;
    private CreateISATABMenu createISA;
    private CreateProfile createProfileGUI;
    private ImportFilesMenu importISA;
    private MergeFilesUI mergeStudies;
    private SettingsUtil settings;
    private ImportConfiguration importConfiguration;
    private ISAcreator isacreator;
    private MainMenu mainMenu;

    private static InfiniteProgressPanel progressIndicator;
    private static JPanel previousGlassPane = null;

    private Component currentPanel = null;
    private GenericPanel generic;


    public ISAcreatorMenu(ISAcreator ISAcreator, final int panelToShow) {
        this.isacreator = ISAcreator;

        setSize(ISAcreator.getSize());
        setLayout(new OverlayLayout(this));
        setBackground(UIHelper.BG_COLOR);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        authGUI = new Authentication(this);
        createISA = new CreateISATABMenu(this);
        createProfileGUI = new CreateProfile(this);
        importISA = new ImportFilesMenu(this);
        importConfiguration = new ImportConfiguration(this);
//        mergeStudies = new MergeFilesUI(this);

        if (isacreator.getMode() == Mode.NORMAL_MODE)
            settings = new SettingsUtil(this, ISAcreator.getProgramSettings());

        mainMenu = new MainMenu(this);

        generic = new ISAcreatorBackground();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                authGUI.createGUI();
                createProfileGUI.createGUI();
                createISA.createGUI();
                importISA.createGUI();
                mainMenu.createGUI();
                importConfiguration.createGUI();


                add(generic, JLayeredPane.DEFAULT_LAYER);
                startAnimation();

                switch (panelToShow) {
                    case SHOW_MAIN:
                        isacreator.setGlassPanelContents(mainMenu);

                        break;

                    case SHOW_LOGIN:
                        isacreator.setGlassPanelContents(authGUI);

                        break;

                    case SHOW_CREATE_ISA:
                        isacreator.setGlassPanelContents(createISA);

                        break;

                    case SHOW_UNSUPPORTED_JAVA:
                        UnSupportedJava noSupport = new UnSupportedJava(ISAcreatorMenu.this);
                        noSupport.createGUI();
                        isacreator.setGlassPanelContents(noSupport);

                        break;

                    default:
                        isacreator.setGlassPanelContents(importConfiguration);
                }

                UIHelper.applyBackgroundToSubComponents(ISAcreatorMenu.this, UIHelper.BG_COLOR);
            }
        });
    }


    public void startAnimation() {
//        final Timer[] timer = new Timer[1];
//        timer[0] = new Timer(125, new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (isShowing()) {
//                    generic.animate();
//                    generic.repaint();
//                } else {
//                    timer[0].stop();
//                }
//            }
//        });
//        timer[0].start();
    }

    public void changeView(Component panel) {
        if (panel != null) {
            currentPanel = panel;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    isacreator.setGlassPanelContents((JPanel) currentPanel);
                }
            });

        }
    }


    protected void showProgressPanel(ImageIcon image) {
        previousGlassPane = (JPanel) isacreator.getGlassPane();
        InfiniteImageProgressPanel imageProgress = new InfiniteImageProgressPanel(image);
        imageProgress.setSize(new Dimension(
                isacreator.getContentPane().getWidth(),
                isacreator.getContentPane().getHeight()));

        isacreator.setGlassPane(imageProgress);
        isacreator.validate();
    }

    protected void showProgressPanel(String text) {
        previousGlassPane = (JPanel) isacreator.getGlassPane();
        progressIndicator = new InfiniteProgressPanel(text);
        progressIndicator.setSize(new Dimension(
                isacreator.getContentPane().getWidth(),
                isacreator.getContentPane().getHeight()));

        isacreator.setGlassPane(progressIndicator);
        progressIndicator.start();
        isacreator.validate();
    }

    protected void stopProgressIndicator() {
        if (progressIndicator != null) {
            progressIndicator.stop();
        }
    }

    protected void resetViewAfterProgress() {
        isacreator.setGlassPane(previousGlassPane);
    }

    protected void hideGlassPane() {
        isacreator.hideGlassPane();
    }


    public DataEntryEnvironment getCurrentDEP() {
        return isacreator.getDataEntryEnvironment();
    }

    public ISAcreator getMain() {
        return isacreator;
    }

    public void paintComponent(Graphics g) {
        // check current size to ensure that it's larger than the preferred dimension...
        super.paintChildren(g);
    }

    public void showGUI(final int guiType) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                switch (guiType) {
                    case SHOW_MAIN:
                        isacreator.setGlassPanelContents(mainMenu);

                        break;

                    case SHOW_LOGIN:
                        isacreator.setGlassPanelContents(authGUI);

                        break;

                    default:
                        isacreator.setGlassPanelContents(mainMenu);
                }
            }
        });
    }

    public Authentication getAuthenticationGUI() {
        return authGUI;
    }

    public CreateISATABMenu getCreateISAMenuGUI() {
        return createISA;
    }

    public CreateProfile getCreateProfileGUI() {
        return createProfileGUI;
    }

    public ImportConfiguration getImportConfigurationGUI() {
        return importConfiguration;
    }

    public ImportFilesMenu getImportISAGUI() {
        return importISA;
    }

    public MainMenu getMainMenuGUI() {
        return mainMenu;
    }

    public MergeFilesUI getMergeStudiesGUI() {
        if (mergeStudies == null) {
            mergeStudies = new MergeFilesUI(this);
        }
        return mergeStudies;
    }

    public SettingsUtil getSettings() {
        settings.createGUI();
        return settings;
    }
}
