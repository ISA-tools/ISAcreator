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
import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.api.AuthenticationManager;
import org.isatools.isacreator.api.CreateProfile;
import org.isatools.isacreator.api.ImportConfiguration;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.GenericPanel;
import org.isatools.isacreator.effects.InfiniteImageProgressPanel;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.gs.GSLocalFilesManager;
import org.isatools.isacreator.gs.gui.GSRegistrationMenu;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.ISAcreatorBackground;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;
import org.isatools.isacreator.mergeutil.MergeFilesUI;
import org.isatools.isacreator.settings.SettingsUtil;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * LoginPage
 * Provides all the elements for logging into the system, creating a profile, and navigating
 * around the program.
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorMenu extends JLayeredPane {

    private static final Logger log = Logger.getLogger(ISAcreatorMenu.class);

    // todo refactor menu to make this class much smaller!
    public static final int SHOW_MAIN = 0;
    public static final int SHOW_LOGIN = 1;
    public static final int SHOW_CREATE_ISA = 2;
    public static final int SHOW_IMPORT_CONFIGURATION = 3;
    public static final int SHOW_UNSUPPORTED_JAVA = 4;
    public static final int NONE = 5;

    public static final int SHOW_LOADED_FILES = 6;

    private boolean loggedIn;
    private Authentication authentication = null;
    //TODO create specific super class
    private MenuUIComponent authGUI;
    private CreateISATABMenu createISA;
    private MenuUIComponent createProfileGUI;
    private ImportFilesMenu importISA;
    private MergeFilesUI mergeStudies;
    private SettingsUtil settings;
    private ImportConfigurationMenu importConfigurationMenu;
    private ISAcreator isacreator;
    private MainMenu mainMenu;

    private static InfiniteProgressPanel progressIndicator;
    private static JPanel previousGlassPane = null;

    private Component currentPanel = null;
    private GenericPanel background;


    public ISAcreatorMenu(ISAcreator ISAcreator, String username, Authentication authentication, String authMenuClassName, final int panelToShow, boolean loggedIn) {
        this(ISAcreator, username, null, null,  null, authentication, authMenuClassName, panelToShow, loggedIn);
    }


    public ISAcreatorMenu(ISAcreator ISAcreator, String username, char[] password, String configDir, String isatabDir, Authentication auth, String authMenuClassName, final int panelToShow, boolean li) {
        this.isacreator = ISAcreator;

        setSize(ISAcreator.getSize());
        setLayout(new OverlayLayout(this));
        setBackground(UIHelper.BG_COLOR);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
        authentication = auth;
        loggedIn = li;

        boolean profileCreated = false;
        if (username!=null){

            if (authentication!=null && ISAcreatorCLArgs.mode()!=Mode.GS && !authentication.login(username, password)) {
                CreateProfile.createProfile(username);
                profileCreated = true;
                loggedIn = true;
            }
        }

        if (authMenuClassName==null && authentication==null){
                authentication = new AuthenticationManager();
                authGUI = new AuthenticationMenu(this, authentication, username);
        } else {
             //authGUI requires this class (ISAcreatorMenu) as parameter for the constructor, thus it is created here with the reflection API

           if (authentication!=null && username!=null && password!=null && ISAcreatorCLArgs.mode()!=Mode.GS){
                loggedIn = authentication.login(username, password);
                if (!loggedIn) {
                    System.err.print("Username and/or password are invalid");
                    loggedIn = true;
                }
           }

           //TODO this should not needed anymore
           if (authMenuClassName!=null){
            try{
                Class authMenuUIClass = Class.forName(authMenuClassName);
                log.debug("authMenuUIClass="+authMenuUIClass);
                Constructor constructor = authMenuUIClass.getConstructor(ISAcreatorMenu.class, Authentication.class, String.class);
                authGUI = (MenuUIComponent) constructor.newInstance(this, authentication, username);
            }catch(ClassNotFoundException e){
                e.printStackTrace();
            }catch(NoSuchMethodException e){
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
           }

        }//else

        createISA = new CreateISATABMenu(this);
        //if (isacreator.getMode()==Mode.GS){
        //    createProfileGUI = new GSRegistrationMenu(this);
        //}else{
            createProfileGUI = new CreateProfileMenu(this);
        //}
        importISA = new ImportFilesMenu(this);
        importConfigurationMenu = new ImportConfigurationMenu(this);
        mergeStudies = new MergeFilesUI(this);

        if (isacreator.getMode() == Mode.NORMAL_MODE)
            settings = new SettingsUtil(this, ISAcreator.getProgramSettings());

        mainMenu = new MainMenu(this);

        background = new ISAcreatorBackground();


        boolean importConfigSuccessful = false;
        if (configDir!=null){
            ImportConfiguration importConfiguration = new ImportConfiguration(configDir);
            importConfigSuccessful = importConfiguration.loadConfiguration();
            if (importConfigSuccessful)
                System.out.println("Problem importing the configuration at "+ configDir);
        }

        System.out.println("user " + (profileCreated ? "created" : "authenticated") + (importConfigSuccessful ? ", configuration imported": ", configuration not imported yet"));

//        importISA = new ImportFilesMenu(ISAcreatorMenu.this);
        if (isacreator.getMode()!= Mode.GS && isatabDir!=null){
            loadFiles(isatabDir);
        }

        if (panelToShow==SHOW_LOADED_FILES) {
            if (ISAcreatorCLArgs.mode()== Mode.GS  && !loggedIn){
                GSLocalFilesManager.downloadFiles(getAuthentication());
            }
            loadFiles(ISAcreatorCLArgs.isatabDir());
        }




        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                //TODO only create GUIs if necessary
                if (authGUI!=null)
                    authGUI.createGUI();
                createProfileGUI.createGUI();
                createISA.createGUI();
                importISA.createGUI();
                mainMenu.createGUI();
                importConfigurationMenu.createGUI();


                add(background, JLayeredPane.DEFAULT_LAYER);
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


                    case NONE:
                        break;

                    case SHOW_LOADED_FILES:
                        break;

                    default:  //SHOW_IMPORT_CONFIGURATION
                        isacreator.setGlassPanelContents(importConfigurationMenu);
                }

                UIHelper.applyBackgroundToSubComponents(ISAcreatorMenu.this, UIHelper.BG_COLOR);
            }
        });
    }

    public void loadFiles(String isatabDir) {
        importISA.getSelectedFileAndLoad(new File(isatabDir));
        System.out.println("ISATAB dataset loaded");
    }

    public ISAcreatorMenu(ISAcreator ISAcreator, final int panelToShow) {
        this(ISAcreator, null, null, null, panelToShow, false);
    }

    public Authentication getAuthentication(){
        return authentication;
    }

    public boolean isUserLoggedIn(){
        return loggedIn;
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
        System.out.println("==================== Show progress panel");
        captureCurrentGlassPaneContents();
        System.out.println("previousGlassPane=" + previousGlassPane);
        InfiniteImageProgressPanel imageProgress = new InfiniteImageProgressPanel(image);
        System.out.println("imageProgress" + imageProgress);
        int isacreatorWidth = isacreator.getContentPane().getWidth();
        int isacreatorHeight = isacreator.getContentPane().getHeight();
        imageProgress.setSize(new Dimension(
                isacreatorWidth == 0 ? ISAcreator.APP_WIDTH : isacreatorWidth,
                isacreatorHeight == 0 ? ISAcreator.APP_HEIGHT : isacreatorHeight));

        System.out.println("imageProgress = " + imageProgress);

        isacreator.setGlassPane(imageProgress);
        isacreator.validate();
    }

    protected void showProgressPanel(String text) {
        captureCurrentGlassPaneContents();
        progressIndicator = new InfiniteProgressPanel(text);
        progressIndicator.setSize(new Dimension(
                isacreator.getContentPane().getWidth(),
                isacreator.getContentPane().getHeight()));

        isacreator.setGlassPane(progressIndicator);

        progressIndicator.start();
        isacreator.validate();
    }

    protected void captureCurrentGlassPaneContents() {
        previousGlassPane = (JPanel) isacreator.getGlassPane();
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
        System.out.println("============Hiding glass pane");
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

    public MenuUIComponent getAuthenticationGUI() {
        return authGUI;
    }

    public CreateISATABMenu getCreateISAMenuGUI() {
        return createISA;
    }

    public MenuUIComponent getCreateProfileGUI() {
        return createProfileGUI;
    }

    public ImportConfigurationMenu getImportConfigurationGUI() {
        return importConfigurationMenu;
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
