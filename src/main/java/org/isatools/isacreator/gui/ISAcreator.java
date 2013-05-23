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
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics
 project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isatools.isacreator.gui;

import org.apache.log4j.Logger;

import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.archiveoutput.ArchiveOutputWindow;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.TitlePanel;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.gui.io.exportisa.OutputISAFilesFromGUI;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.io.UserProfileManager;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.ontologiser.adaptors.InvestigationAdaptor;
import org.isatools.isacreator.ontologiser.ui.OntologiserUI;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.plugins.MenuPluginTracker;
import org.isatools.isacreator.plugins.OntologyPluginTracker;
import org.isatools.isacreator.plugins.SpreadsheetPluginTracker;
import org.isatools.isacreator.qrcode.ui.QRCodeGeneratorUI;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.settings.SettingsUtil;
import org.isatools.isacreator.spreadsheet.IncorrectColumnOrderGUI;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.utils.IncorrectColumnPositioning;
import org.isatools.isacreator.utils.PropertyFileIO;
import org.isatools.isacreator.validateconvert.ui.OperatingMode;
import org.isatools.isacreator.validateconvert.ui.ValidateUI;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import org.osgi.framework.BundleContext;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.isacreator.gs.GSSaveAction;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * ISAcreator is a container for the whole ISAcreator application.
 * Each section of the application is a JLayeredPane which is swapped in and out
 * of the JFrame depending on what the USER wants to do.
 * For example, the data entry pane will contain everything required for the user to enter
 * information pertaining to their experiment. Moreover, the wizard pane will contain all
 * the information required to create as much of the data as possible through the user answering
 * questions on their experiment.
 *
 * @author Eamonn Maguire
 */
public class ISAcreator extends AnimatableJFrame implements WindowFocusListener {

    private static Logger log = Logger.getLogger(ISAcreator.class);

    public static final String CONFIG_DIR = "/Configurations/isaconfig-default_v2013-02-13/";

    public static String DEFAULT_ISATAB_SAVE_DIRECTORY = "isatab files";
    public static String DEFAULT_CONFIGURATIONS_DIRECTORY = "Configurations";
    public static String DEFAULT_USER_PROFILE_DIRECTORY = "";

    public static final int APP_WIDTH = 999;
    public static final int APP_HEIGHT = 700;

    @InjectedResource
    private Image isacreatorIcon;
    @InjectedResource
    private ImageIcon saveIcon, saveMenuIcon, saveLogoutIcon, saveExitIcon, validateIcon, convertIcon,
            logoutIcon, menuIcon, exitIcon, exportArchiveIcon, addStudyIcon, removeStudyIcon, fullScreenIcon,
            defaultScreenIcon, aboutIcon, helpIcon, supportIcon, feedbackIcon, confirmLogout, confirmMenu, confirmExit;

    private AboutPanel aboutPanel;
    private Properties programSettings;

    private DataEntryEnvironment curDataEntryEnvironment;
    private GridBagConstraints gridbagConstraints;
    private JLayeredPane currentPage = null;
    private JPanel glass;

    private ISAcreatorMenu isacreatorMenu = null;

    private JMenuBar menuBar;
    private JMenu pluginMenu = new JMenu("plugins");;

    private Map<String, JMenu> menusRequiringStudyIds;

    private OutputISAFilesFromGUI outputISATABFromGUI;
    private IncorrectColumnOrderGUI incorrectGUI;

    private Mode mode;

    private MenuPluginTracker menuPluginTracker;
    private SpreadsheetPluginTracker spreadsheetPluginTracker;
    private OntologyPluginTracker ontologyPluginTracker;
    private JMenuItem useShortNames;


    public ISAcreator(Mode mode, BundleContext context) {
        this(mode, context, null);
    }

    /**
     * Could this constructor be removed?
     *
     * @param mode
     * @param context
     * @param configDir
     */
    public ISAcreator(Mode mode, BundleContext context, String configDir) {

        ResourceInjector.get("gui-package.style").inject(this);

        this.mode = mode;

        if (context != null) {
            menuPluginTracker = new MenuPluginTracker(context, this);
            spreadsheetPluginTracker = new SpreadsheetPluginTracker(context);
            ontologyPluginTracker = new OntologyPluginTracker(context);
            menuPluginTracker.open();
            spreadsheetPluginTracker.open();
            ontologyPluginTracker.open();
        }

        outputISATABFromGUI = new OutputISAFilesFromGUI(this);

        menusRequiringStudyIds = new HashMap<String, JMenu>();

        gridbagConstraints = new GridBagConstraints();
        gridbagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagConstraints.weightx = 0.0;
        gridbagConstraints.gridwidth = 2;
        gridbagConstraints.gridheight = 2;
        gridbagConstraints.gridx = 1;
        gridbagConstraints.gridy = 3;

        // this is a way of loading the Configurations through the API.
        ConfigurationManager.loadConfigurations(configDir);

        ApplicationManager.setCurrentApplicationInstance(this);
    }

    public void createGUI(String configDir, String username, char[] password, final String isatabDir, String[] isatabFiles, Authentication authentication, String authMenuClassName, boolean loggedIn, List<ErrorMessage> errors) {
        performStandardSetup();

        // check that java version is supported!
        if (!checkSystemRequirements()) {
            //this can't happen if this is used from java web start
            isacreatorMenu = new ISAcreatorMenu(ISAcreatorMenu.SHOW_UNSUPPORTED_JAVA);
        }else{
            int panelToShow = ISAcreatorMenu.SHOW_ERROR;

            isacreatorMenu = new ISAcreatorMenu(username, password, configDir, isatabDir, authentication ,authMenuClassName, panelToShow, loggedIn, errors);
        }
        setCurrentPage(isacreatorMenu);
        pack();
        setVisible(true);
    }


    /**
     * Creates GUI bypassing the load of configuration files, user profile creation, and load of ISATAB files according to parameters received.
     *
     * @param configDir
     * @param username
     * @param isatabDir
     */
    public void createGUI(String configDir, String username, char[] password, final String isatabDir, String[] isatabFiles, Authentication authentication, String authMenuClassName, boolean loggedIn) {

        performStandardSetup();
        // check that java version is supported!
        if (!checkSystemRequirements()) {
            //this can't happen if this is used from java web start
            isacreatorMenu = new ISAcreatorMenu(ISAcreatorMenu.SHOW_UNSUPPORTED_JAVA);
        } else {
            //mode, configuration, user, main
            int panelToShow;
            if (!loggedIn && (username==null || password==null) || authMenuClassName!=null){
                panelToShow = ISAcreatorMenu.SHOW_LOGIN;
            }else if (configDir==null){
                panelToShow = ISAcreatorMenu.SHOW_IMPORT_CONFIGURATION;
            } else if (isatabDir==null && isatabFiles==null){
                panelToShow = ISAcreatorMenu.SHOW_MAIN;
            }else{
                panelToShow = ISAcreatorMenu.SHOW_LOADED_FILES;
            }

            isacreatorMenu = new ISAcreatorMenu(username, null, configDir, isatabDir, authentication,authMenuClassName, panelToShow, loggedIn);
        }
        setCurrentPage(isacreatorMenu);
        pack();
        setVisible(true);
    }

    public void createGUI() {
        performStandardSetup();

        // check that java version is supported!
        if (!checkSystemRequirements()) {
            isacreatorMenu = new ISAcreatorMenu(ISAcreatorMenu.SHOW_UNSUPPORTED_JAVA);
        } else {
            isacreatorMenu = new ISAcreatorMenu(ISAcreatorMenu.SHOW_LOGIN);
        }
        setCurrentPage(isacreatorMenu);
        pack();
        setVisible(true);

    }

    private void performStandardSetup() {
        setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));
        setIconImage(isacreatorIcon);
        setBackground(UIHelper.BG_COLOR);
        setTitle(ISAcreatorProperties.getProperty("appTitleAndVersion"));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setResizable(true);
        setLayout(new BorderLayout());

        addWindowFocusListener(this);
        // load user profiles into the system
        loadProgramSettings();
        UserProfileManager.loadUserProfiles();
        // create the top menu bar

        createTopPanel();

        FooterPanel fp = new FooterPanel(this);
        add(fp, BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));
    }

    private void checkMenuRequired() {
        boolean menuRequired = currentPage instanceof DataEntryEnvironment;
        menuBar.getParent().setVisible(menuRequired);
    }

    private void setupAboutPanel() {
        aboutPanel = new AboutPanel();
        aboutPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                glass.setVisible(false);
            }
        });

    }

    private void loadProgramSettings() {
        programSettings = PropertyFileIO.loadSettings(SettingsUtil.PROPERTIES_FILE);

        if (programSettings == null) {
            programSettings = new Properties();
        } else {
            Object isatabLocation = programSettings.get("isacreator.isatabLocation");


            if (isatabLocation != null) {
                // test to ensure directory exists before replacing working directory with an incorrect one
                File isaLocation = new File(isatabLocation.toString());

                if (isaLocation.exists()) {
                    DEFAULT_ISATAB_SAVE_DIRECTORY = isaLocation.getAbsolutePath();
                }
            }

            Object configurationLocation = programSettings.get("isacreator.configurationLocation");

            if (configurationLocation != null) {
                File configurationLocationFile = new File(configurationLocation.toString());
                if (configurationLocationFile.exists()) {
                    DEFAULT_CONFIGURATIONS_DIRECTORY = configurationLocationFile.getAbsolutePath();
                }
            }

            Object profileLocation = programSettings.get("isacreator.userProfileLocation");

            if (profileLocation != null) {
                if (new File(profileLocation.toString()).exists()) {
                    DEFAULT_USER_PROFILE_DIRECTORY = profileLocation.toString();
                }
            }

            // add all properties into the ISAcreatorProperties file
            for (String propertyName : programSettings.stringPropertyNames()) {
                System.out.println("Property name: " + propertyName + "= " + programSettings.get(propertyName));
                ISAcreatorProperties.setProperty(propertyName, programSettings.get(propertyName).toString());
            }
        }
    }

    private boolean checkSystemRequirements() {
        String version = System.getProperty("java.version");
        ISAcreatorProperties.setProperty("java.version", version);

        String[] versionParts = version.split("\\.");
        int minorVersion = Integer.valueOf(versionParts[1]);

        log.info("System details");
        log.info(System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + (System.getProperty("os.arch").equals("x86_64") ? "64 bit" : "32 bit"));
        log.info("JRE version: " + version);

        return minorVersion >= 6;
    }

    private void createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.PAGE_AXIS));

        TitlePanel titlePane = new ISAcreatorTitlePanel();
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        titlePane.addPropertyChangeListener(ISAcreatorTitlePanel.CLOSE_EVENT, new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                closeISAcreator();
            }
        });

        topPanel.add(titlePane);
        topPanel.add(createMenuBar());
        add(topPanel, BorderLayout.NORTH);
        titlePane.installListeners();
    }

    private Container createMenuBar() {
        // create menu bar
        menuBar = new JMenuBar();
        menuBar.setForeground(UIHelper.DARK_GREEN_COLOR);
        menuBar.setBorder(null);

        MouseAdapter cleanUpDisplayedEditors = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeEditors();
            }
        };

        JMenu fileMenu = new JMenu("file");
        fileMenu.addMouseListener(cleanUpDisplayedEditors);

        JMenuItem save = new JMenuItem(new SaveAction(SaveAction.SAVE_ONLY,
                "save", saveIcon, mode != Mode.GS ? "save ISA files": "save ISA files locally", KeyEvent.VK_S));

        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_MASK));
        fileMenu.add(save);
        JMenuItem saveAs = new JMenuItem(new SaveAction(SaveAction.SAVE_AS,
                "save as", saveIcon, mode != Mode.GS ? "save as a different set of ISA files" : "save as a different set of local ISA files",
                KeyEvent.VK_A));


        fileMenu.add(saveAs);

        if(mode == null) {
            mode = Mode.NORMAL_MODE;
        }

        //GenomeSpace related code
        if (mode.equals(Mode.GS)){

            JMenuItem saveGS = new JMenuItem(new GSSaveAction(GSSaveAction.SAVE_ONLY,
                    "save in GenomeSpace", saveIcon, "save ISA files in GenomeSpace", KeyEvent.VK_I, this, isacreatorMenu));

            fileMenu.add(saveGS);

            JMenuItem saveAsGS = new JMenuItem(new GSSaveAction(GSSaveAction.SAVE_AS,
                    "save in GenomeSpace as", saveIcon, "save as a different set of ISA files in GenomeSpace", KeyEvent.VK_G, this, isacreatorMenu));

            fileMenu.add(saveAsGS);
        }

        fileMenu.add(new JSeparator());

        JMenuItem main = new JMenuItem(new LeaveAction(LeaveAction.MAIN,
                "go to main menu",
                menuIcon,
                "go back to main menu without saving", null));

        fileMenu.add(main);
        fileMenu.add(new JSeparator());

        JMenuItem exportISArchive = new JMenuItem("create ISArchive",
                exportArchiveIcon);
        exportISArchive.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                final ArchiveOutputWindow oaw = new ArchiveOutputWindow();
                oaw.setLocation(((getX() + getWidth()) / 2) - (ArchiveOutputWindow.WIDTH / 2), ((getY() + getHeight()) / 2) - (ArchiveOutputWindow.HEIGHT / 2));

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        oaw.createGUI();
                    }
                });
            }
        });

        fileMenu.add(exportISArchive);


        JMenuItem validateISA = new JMenuItem("Validate ISAtab", validateIcon);
        validateISA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ValidateUI validateUI = new ValidateUI(ISAcreator.this, OperatingMode.VALIDATE);
                validateUI.createGUI();
                validateUI.setLocationRelativeTo(ISAcreator.this);
                validateUI.setAlwaysOnTop(true);
                validateUI.setVisible(true);
                validateUI.validateISAtab();
            }
        });

        fileMenu.add(new JSeparator());

        fileMenu.add(validateISA);

        JMenuItem convertISA = new JMenuItem("Convert ISAtab", convertIcon);

        convertISA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                ValidateUI validateUI = new ValidateUI(ISAcreator.this, OperatingMode.CONVERT);
                validateUI.createGUI();
                validateUI.setLocationRelativeTo(ISAcreator.this);
                validateUI.setAlwaysOnTop(true);
                validateUI.setVisible(true);
                validateUI.validateISAtab();
            }
        });

        fileMenu.add(convertISA);

        menuBar.add(fileMenu);


        // study section

        JMenu studyMenu = new JMenu("study");
        studyMenu.addMouseListener(cleanUpDisplayedEditors);

        JMenuItem addStudy = new JMenuItem("add study",
                addStudyIcon);

        addStudy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDataEntryEnvironment().addStudyToTree();
            }
        });
        studyMenu.add(addStudy);

        JMenuItem removeStudy = new JMenuItem("remove study",
                removeStudyIcon);

        removeStudy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getDataEntryEnvironment().removeStudy();
            }
        });
        studyMenu.add(removeStudy);

        menuBar.add(studyMenu);

        // end study section

        JMenu view = new JMenu("view");
        view.addMouseListener(cleanUpDisplayedEditors);

        JMenuItem fullScreen = new JMenuItem(new ScreenResizeAction(
                ScreenResizeAction.FULL_SCREEN, "full screen",
                fullScreenIcon,
                "Put application in full screen mode.", KeyEvent.VK_F));

        fullScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,
                KeyEvent.ALT_MASK));

        JMenuItem defaultScreen = new JMenuItem(new ScreenResizeAction(
                ScreenResizeAction.DEFAULT_SIZE, "default screen",
                defaultScreenIcon,
                "Put application in the default screen mode.", KeyEvent.VK_D));

        defaultScreen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
                KeyEvent.ALT_MASK));

        view.add(defaultScreen);
        view.add(fullScreen);

        menuBar.add(view);

        JMenu utilities = new JMenu("utilities");
        utilities.addMouseListener(cleanUpDisplayedEditors);

        JMenu sampleTracking = new JMenu("Sample Tracking");

        sampleTracking.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                updateExportList();
            }
        });

        JMenuItem tagInvestigation = new JMenuItem("Autotagging (beta)");
        tagInvestigation.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                OntologiserUI ontologiserUI = new OntologiserUI(ApplicationManager.getCurrentApplicationInstance(), new InvestigationAdaptor(getDataEntryEnvironment().getInvestigation()));
                ontologiserUI.createGUI();

                showJDialogAsSheet(ontologiserUI);
            }
        });

        JMenu qrCodeExport = new JMenu("generate QR codes for Study samples");
        menusRequiringStudyIds.put("qr", qrCodeExport);
        sampleTracking.add(qrCodeExport);


        utilities.add(sampleTracking);
        utilities.add(tagInvestigation);

        menuBar.add(utilities);

        final JMenuItem clearOntologySearchCache = new JMenuItem("Clear Ontology Search Cache");

        clearOntologySearchCache.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                OntologyManager.clearResultCache();
            }
        });


        useShortNames = new JMenuItem(!Boolean.parseBoolean(ISAcreatorProperties.getProperty("useShortNames")) ? "Use abbreviated form of field names?" : "Use full form of field names");
        useShortNames.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                boolean shortNames = Boolean.parseBoolean(ISAcreatorProperties.getProperty("useShortNames"));

                ISAcreatorProperties.setProperty("useShortNames", String.valueOf(!shortNames));
                useShortNames.setText(!shortNames ? "Use full form of field names" : "Use abbreviated form of field names?");
                repaint();
            }
        });

        JMenu options = new JMenu("options");
        options.addMouseListener(cleanUpDisplayedEditors);
        options.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                clearOntologySearchCache.setEnabled(OntologyManager.searchResultCache.size() > 0);
            }
        });

        options.add(clearOntologySearchCache);
        options.add(useShortNames);

        menuBar.add(options);


        pluginMenu.setVisible(false);
        menuBar.add(pluginMenu);

        JMenu help = new JMenu("help");
        help.addMouseListener(cleanUpDisplayedEditors);

        JMenuItem about = new JMenuItem("about",
                aboutIcon);
        about.setForeground(UIHelper.DARK_GREEN_COLOR);
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (aboutPanel == null) {
                    setupAboutPanel();
                }
                setGlassPanelContents(aboutPanel);
            }
        });
        help.add(about);

        JMenuItem manual = new JMenuItem("user manual",
                helpIcon);
        manual.setForeground(UIHelper.DARK_GREEN_COLOR);

        manual.setEnabled(Desktop.isDesktopSupported());


        manual.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://isatab.sourceforge.net/isahelp/ch04.html"));
                } catch (IOException e1) {
                    log.error("IOException - " + e1.getMessage());
                } catch (URISyntaxException e1) {
                    log.error("URISyntaxException - " + e1.getMessage());
                }

            }
        });
        help.add(manual);
        manual.setEnabled(true);

        JMenuItem contact = new JMenuItem("contact support team",
                supportIcon);
        contact.setForeground(UIHelper.DARK_GREEN_COLOR);
        contact.setEnabled(Desktop.isDesktopSupported());
        contact.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://isatab.sourceforge.net/contact.html"));
                } catch (IOException e1) {
                    log.error("IOException - " + e1.getMessage());
                } catch (URISyntaxException e1) {
                    log.error("URISyntaxException - " + e1.getMessage());
                }
            }
        });
        help.add(contact);

        JMenuItem feedback = new JMenuItem("report a bug",
                feedbackIcon);
        feedback.setEnabled(Desktop.isDesktopSupported());
        feedback.setForeground(UIHelper.DARK_GREEN_COLOR);
        feedback.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("http://www.github.com/ISA-tools/ISAcreator/issues"));
                } catch (IOException e1) {
                    log.error("IOException - " + e1.getMessage());
                } catch (URISyntaxException e1) {
                    log.error("URISyntaxException - " + e1.getMessage());
                }
            }
        });
        help.add(feedback);
        menuBar.add(help);

        return UIHelper.wrapComponentInPanel(menuBar);
    }

    private void updateExportList() {

        Investigation i = curDataEntryEnvironment.getInvestigation();

        Set<String> studies = i.getStudies().keySet();

        String id = "";

        for (String s : studies) {
            id += s;
        }
        // we only want to update when something changes!
        String lastExport = ISAcreatorProperties.getProperty("last_export");
        if (lastExport.isEmpty() || !lastExport.equals(id)) {

            ISAcreatorProperties.setProperty("last_export", id);

            for (String menuType : menusRequiringStudyIds.keySet()) {
                Component[] menuComponents = menusRequiringStudyIds.get(menuType).getMenuComponents();
                for (Component c : menuComponents) {
                    if (c instanceof JMenuItem) {
                        menusRequiringStudyIds.get(menuType).remove((JMenuItem) c);
                    }
                }

                System.out.println("Adding study ids for menusRequiringStudyIds.");
                for (final String study_id : studies) {

                    JMenuItem item = new JMenuItem(study_id);

                    if (menuType.equalsIgnoreCase("qr")) {
                        item.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {

                                Thread loader = new Thread(new Runnable() {
                                    public void run() {
                                        SwingUtilities.invokeLater(new Runnable() {
                                            public void run() {
                                                QRCodeGeneratorUI qrCodeUI = new QRCodeGeneratorUI(ApplicationManager.getCurrentApplicationInstance(), study_id);
                                                qrCodeUI.createGUI();
                                                showJDialogAsSheet(qrCodeUI);
                                                maskOutMouseEvents();
                                            }
                                        });
                                    }
                                });
                                loader.start();
                            }
                        });
                    }
                    menusRequiringStudyIds.get(menuType).add(item);
                }
            }
        }


    }

    private void createSubmissionDirectory(String dirPath) {
        File f = new File(dirPath);
        if (!f.exists() && !f.isDirectory()) {
            f.mkdir();
        }
    }

    public DataEntryEnvironment getDataEntryEnvironment() {
        return curDataEntryEnvironment;
    }

    public void hideGlassPane() {
        if (glass != null) {
            glass.setVisible(false);
        }
    }

    private void saveProfilesAndExit() {
        setVisible(false);
        if (UserProfileManager.getCurrentUser() != null) {
            UserProfileManager.updateUserProfiles();
        }
        UserProfileManager.saveUserProfiles();
        dispose();
        System.exit(0);
    }

    private void saveProfilesAndGoToMain() {

        try {

            UserProfileManager.updateUserProfiles();

            UserProfileManager.saveUserProfiles();
            checkMenuRequired();

            ISAcreatorProperties.setProperty(ISAcreatorProperties.CURRENT_ISATAB, "");

            OntologyManager.clearReferencedOntologySources();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Problem occurred when saving user profiles.");
        }

        curDataEntryEnvironment.removeReferences();
        curDataEntryEnvironment = null;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isacreatorMenu.showGUI(ISAcreatorMenu.SHOW_MAIN);
                isacreatorMenu.startAnimation();
                setCurrentPage(isacreatorMenu);
            }
        });
    }




    public void setCurDataEntryPanel(DataEntryEnvironment dataEntryEnvironment) {
        curDataEntryEnvironment = dataEntryEnvironment;
        System.out.println("Data entry panel changed & initialised");
    }

    public void setCurrentPage(JLayeredPane newPage) {

        if (currentPage == null) {
            currentPage = newPage;
        } else {
            System.out.println("removing current page...");
            getContentPane().remove(currentPage);
            currentPage = null;
            currentPage = newPage;
        }

        checkMenuRequired();
        getContentPane().add(currentPage, BorderLayout.CENTER);

        currentPage.setBorder(new EmptyBorder(0, 0, 0, 0));

        repaint();
        validate();
    }

    public void setGlassPanelContents(Container panel) {
        if (glass != null) {
            glass.removeAll();
        }
        glass = (JPanel) getGlassPane();
        glass.setLayout(new GridBagLayout());
        glass.add(panel, gridbagConstraints);
        glass.setBackground(new Color(255, 255, 255, 10));
        glass.setVisible(true);
        glass.revalidate();
        glass.repaint();
    }

    public Properties getProgramSettings() {
        return programSettings;
    }

    public String getLoadedConfiguration() {
        return ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION);
    }


    /***
     * Class implenting the action to be performed when leaving the application.
     *
     */
    class LeaveAction extends AbstractAction implements PropertyChangeListener {
        static final int LOGOUT = 0;
        static final int MAIN = 1;
        static final int EXIT = 2;
        private int type;
        private Timer closeWindowTimer;

        public LeaveAction(int type, String text, ImageIcon icon, String desc,
                           Integer mnemonic) {
            super(text, icon);
            this.type = type;
            putValue(SHORT_DESCRIPTION, desc);

            if (mnemonic != null) {
                putValue(MNEMONIC_KEY, mnemonic);
            }

            closeWindowTimer = new Timer(500, new CloseEvent());
        }

        public void actionPerformed(ActionEvent event) {

            final JOptionPane[] optionPane = new JOptionPane[1];
            final ImageIcon[] icon = new ImageIcon[1];

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String leaveText = "";

                    switch (type) {
                        case LOGOUT:
                            leaveText = "Are you sure you want to logout without saving?";
                            icon[0] = confirmLogout;
                            break;
                        case MAIN:
                            leaveText = "Are you sure you want to go to the main menu without saving?";
                            icon[0] = confirmMenu;
                            break;
                        case EXIT:
                            leaveText = "Are you sure you want to exit ISAcreator?";
                            icon[0] = confirmExit;
                            break;
                    }

                    optionPane[0] = new JOptionPane(leaveText,
                            JOptionPane.INFORMATION_MESSAGE,
                            JOptionPane.YES_NO_OPTION,
                            icon[0]);
                    UIHelper.applyOptionPaneBackground(optionPane[0], UIHelper.BG_COLOR);
                    optionPane[0].addPropertyChangeListener(LeaveAction.this);

                    showJDialogAsSheet(optionPane[0].createDialog(ISAcreator.this, "Confirm"));
                }
            });
        }

        public void propertyChange(PropertyChangeEvent event) {

            if (event.getPropertyName()
                    .equals(JOptionPane.VALUE_PROPERTY)) {
                int lastOptionAnswer = Integer.valueOf(event.getNewValue()
                        .toString());
                hideSheet();
                if (lastOptionAnswer == JOptionPane.YES_OPTION) {

                    ApplicationManager.resetForNextSession();
                    closeWindowTimer.start();
                }
            }
        }

        class CloseEvent implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                doEvent();

                if (closeWindowTimer != null) {
                    closeWindowTimer.stop();
                }
            }

            private void doEvent() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        switch (type) {
                            case MAIN:
                                saveProfilesAndGoToMain();

                                break;


                            case EXIT:
                                saveProfilesAndExit();

                                break;

                            default:
                                //
                        }
                    }
                });
            }
        }

    }

    /***
     * Class implementing saving ISAtab files in the different situations
     *
     */
    class SaveAction extends AbstractAction {
        static final int SAVE_ONLY = 0;
        static final int SAVE_AS = 1;
        static final int SAVE_MAIN = 2;
        static final int SAVE_LOGOUT = 3;
        static final int SAVE_EXIT = 4;
        private int type;
        private Timer closeWindowTimer;

        /**
         * SaveAction constructor.
         *
         * @param type     - type of screen resize to be performed, either full screen or default screen...
         * @param text     - Text to be displayed in component
         * @param icon     - Icon to be displayed in component
         * @param desc     - description of the components purpose
         * @param mnemonic - shortcut key to be used!
         */
        public SaveAction(int type, String text, ImageIcon icon, String desc,
                          Integer mnemonic) {
            super(text, icon);
            this.type = type;
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);

            closeWindowTimer = new Timer(500, new CloseEvent());
        }

        public void actionPerformed(ActionEvent ae) {
            Calendar c = Calendar.getInstance();


            if ((type != SAVE_AS) && curDataEntryEnvironment.getInvestigation().getReference() != null && !curDataEntryEnvironment.getInvestigation().getReference().trim()
                    .equals("")) {

                if (curDataEntryEnvironment.getInvestigation().getReference().trim()
                        .equals("")) {
                    curDataEntryEnvironment.getInvestigation()
                            .setFileReference(DEFAULT_ISATAB_SAVE_DIRECTORY +
                                    File.separator + "SubmissionOn" +
                                    c.get(Calendar.DAY_OF_MONTH) + "-" +
                                    c.get(Calendar.MONTH) + "-" +
                                    c.get(Calendar.HOUR_OF_DAY) + ":" +
                                    c.get(Calendar.MINUTE) + File.separator +
                                    "Investigation");
                    createSubmissionDirectory(DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator +
                            "SubmissionOn" + c.get(Calendar.DAY_OF_MONTH) + "-" +
                            c.get(Calendar.MONTH) + "-" +
                            c.get(Calendar.HOUR_OF_DAY) + ":" +
                            c.get(Calendar.MINUTE));
                }

                saveISATab();

                closeWindowTimer.start();

                if (type != SAVE_ONLY) {
                    OntologyManager.clearReferencedOntologySources();
                }
            } else {
                // need to get a new reference from the user!
                SaveAsDialog sad = new SaveAsDialog();
                sad.addPropertyChangeListener("windowClosed",
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent event) {
                                hideSheet();
                            }
                        });

                sad.addPropertyChangeListener("save",
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent event) {
                                String baseDirectory = DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator +
                                        event.getNewValue().toString();

                                String fileName = baseDirectory + File.separator + "Investigation";
                                createSubmissionDirectory(DEFAULT_ISATAB_SAVE_DIRECTORY +
                                        File.separator +
                                        event.getNewValue().toString());
                                curDataEntryEnvironment.getInvestigation()
                                        .setFileReference(fileName);

                                ISAcreatorProperties.setProperty(ISAcreatorProperties.CURRENT_ISATAB, baseDirectory);

                                saveISATab();
                                UserProfileManager.saveUserProfiles();

                                hideSheet();
                                closeWindowTimer.start();
                            }
                        });
                sad.createGUI();

                showJDialogAsSheet(sad);
            }

        }


        class CloseEvent implements ActionListener {
            public void actionPerformed(ActionEvent event) {
                doEvent();

                if (closeWindowTimer != null) {
                    closeWindowTimer.stop();
                }
            }

            private void doEvent() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        if (outputISATABFromGUI.isShouldShowIncorrectOrderGUI()) {


                            Map<String, List<String>> report = new HashMap<String, List<String>>();
                            // highlight columns in each affected spreadsheet
                            for (Spreadsheet s : outputISATABFromGUI.getErrorSheets()) {
                                // highlight the columns in the spreadsheet
                                report.putAll(s.getTableConsistencyChecker().getErrorReport());
                                Map<Integer, Color> colorcoding = new HashMap<Integer, Color>();
                                for (IncorrectColumnPositioning icor : s.getTableConsistencyChecker().getRecords()) {
                                    colorcoding.putAll(icor.getColumnColors());
                                }
                                s.highlightSpecificColumns(colorcoding);
                            }

                            // show IncorrectColumnOrderGUI
                            incorrectGUI = new IncorrectColumnOrderGUI(report);
                            incorrectGUI.setLocation((getX() + getWidth()) / 2 - (incorrectGUI.getWidth() / 2), (getY() + getHeight()) / 2 - (incorrectGUI.getHeight() / 2));
                            incorrectGUI.createGUI();

                        } else {
                            // IF EVERYTHING IS OK, RESET ROW COLORS!
                            for (Spreadsheet s : outputISATABFromGUI.getAllSheets()) {
                                s.setRowsToDefaultColor();
                            }
                            // proceed to subsequent tasks...
                            switch (type) {
                                case SAVE_MAIN:

                                    saveProfilesAndGoToMain();
                                    break;

                                case SAVE_EXIT:
                                    saveProfilesAndExit();
                                    break;

                                default:
                                    //
                            }
                        }
                    }
                });
            }
        }
    }

    public void saveISATab() {
        outputISATABFromGUI.saveISAFiles(false, getDataEntryEnvironment().getInvestigation());
    }

    class ScreenResizeAction extends AbstractAction {
        static final int FULL_SCREEN = 0;
        static final int DEFAULT_SIZE = 1;
        int type;

        /**
         * ScreenResizeAction constructor.
         *
         * @param type     - type of screen resize to be performed, either full screen or default screen...
         * @param text     - Text to be displayed in component
         * @param icon     - Icon to be displayed in component
         * @param desc     - description of the components purpose
         * @param mnemonic - shortcut key to be used!
         */
        public ScreenResizeAction(int type, String text, ImageIcon icon,
                                  String desc, Integer mnemonic) {
            super(text, icon);
            this.type = type;
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent ae) {
            switch (type) {
                case FULL_SCREEN:

                    if (!glass.isVisible()) {
                        setResizable(true);
                        setSize(Toolkit.getDefaultToolkit().getScreenSize());
                        repaint();
                    }

                    break;

                case DEFAULT_SIZE:

                    if (!glass.isVisible()) {
                        setResizable(true);
                        setSize(ISAcreator.APP_WIDTH, ISAcreator.APP_HEIGHT);
                        repaint();
                    }

                    break;
                default:
                    //go default...
            }
        }
    }

    public IncorrectColumnOrderGUI getIncorrectGUI() {
        return incorrectGUI;
    }

    public Mode getMode() {
        return mode;
    }

    private void closeISAcreator() {
        // checking for this instance should cover the case of the Wizard, Mapper, and main data entry screen
        if (currentPage instanceof AbstractDataEntryEnvironment) {

            JOptionPane optionPane = new JOptionPane("Are you sure you want to exit ISAcreator?",
                    JOptionPane.INFORMATION_MESSAGE,
                    JOptionPane.YES_NO_OPTION,
                    confirmExit);
            UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
            optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    if (propertyChangeEvent.getPropertyName()
                            .equals(JOptionPane.VALUE_PROPERTY)) {
                        int lastOptionAnswer = Integer.valueOf(propertyChangeEvent.getNewValue()
                                .toString());
                        hideSheet();
                        if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                            System.exit(0);
                        }
                    }
                }
            });

            showJDialogAsSheet(optionPane.createDialog(
                    ISAcreator.this, "Confirm"));
            // currently showing edit view, so ask for confirmation before exiting
        } else {
            saveProfilesAndExit();
        }
    }

    public void windowGainedFocus(WindowEvent windowEvent) {
        // do nothing
    }

    public void windowLostFocus(WindowEvent windowEvent) {
        closeEditors();
    }

    private void closeEditors() {
        if (curDataEntryEnvironment != null) {
            curDataEntryEnvironment.closeEditors();
        }
    }

    public JMenu getPluginMenu() {
        return pluginMenu;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ISAcreator main = new ISAcreator(Mode.NORMAL_MODE, null);
                main.createGUI();
            }
        });
    }
}
