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

package org.isatools.isacreator.settings;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.gui.AbstractDataEntryEnvironment;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.io.UserProfileManager;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.utils.PropertyFileIO;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Properties;

/**
 * @author Eamonn Maguire
 * @date Aug 28, 2009
 */

public class SettingsUtil extends AbstractDataEntryEnvironment {

    public static final String PROPERTIES_FILE = "settings.properties";

    private ISAcreatorMenu menuPanels;
    private JList settingsOptions;
    private JPanel centralPanel;
    private HTTPProxySettings httpProxy;
    private OntologySettings ontology;
    private DataLocationSettings dataLocationSettings;
    private ValidationSettings validationSettings;

    private GeneralViewerEditor<Contact> contactEditor;
    private GeneralViewerEditor<Protocol> protocolEditor;

    private Properties settings;
    private Properties defaultSettings;

    @InjectedResource
    public ImageIcon logo, info;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("settings-package.style").load(
                SettingsUtil.class.getResource("/dependency-injections/settings-package.properties"));
    }


    public SettingsUtil(ISAcreatorMenu menuPanels, Properties settings) {
        super();

        this.settings = settings;
        this.defaultSettings = PropertyFileIO.retrieveDefaultSettings();

        ResourceInjector.get("settings-package.style").inject(this);

        this.menuPanels = menuPanels;
        if (settings == null) {
            settings = new Properties();
        } else {
            PropertyFileIO.setProxy(settings);
        }

        httpProxy = new HTTPProxySettings(settings);

    }

    public void createGUI() {
        setLayout(new BorderLayout());
        menuPanels.getMain().hideGlassPane();

        createSouthPanel(false);

        createWestPanel(logo, info);

        MouseListener backButtonListener = new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {

                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {

                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                backButton.setIcon(back);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        callUpdate();

                        menuPanels.getMain().setCurrentPage(menuPanels);
                        menuPanels.getMain().setGlassPanelContents(menuPanels.getMainMenuGUI());
                        menuPanels.startAnimation();
                    }
                });
            }
        };


        assignListenerToLabel(backButton, backButtonListener);

        setOpaque(false);

    }

    public void changeView() {
        setCurrentPage(createSettingsScreen());
    }

    private JPanel createSettingsScreen() {

        JPanel settingsScreen = new JPanel();
        settingsScreen.setLayout(new BorderLayout());
        settingsScreen.setOpaque(false);

        settingsScreen.add(Box.createVerticalStrut(150), BorderLayout.NORTH);

        // add list for users to change through different settings screens.
        DefaultListModel dlm = new DefaultListModel();
        for (Settings s : Settings.values()) {
            dlm.addElement(s);
        }

        settingsOptions = new JList(dlm);
        settingsOptions.setCellRenderer(new ColumnFilterRenderer(UIHelper.VER_12_BOLD, UIHelper.VER_12_PLAIN));
        settingsOptions.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent listSelectionEvent) {

                if (settingsOptions.getSelectedValue() != null) {
                    if (Settings.resolveSetting(settingsOptions.getSelectedValue().toString()) == Settings.HTTP_PROXY) {
                        // show http proxy screen in the center panel!
                        changeCentralPanel(httpProxy);
                    } else if (Settings.resolveSetting(settingsOptions.getSelectedValue().toString()) == Settings.ONTOLOGY) {

                        if (ontology == null) {
                            ontology = new OntologySettings(menuPanels);
                        }

                        changeCentralPanel(ontology);
                    } else if (Settings.resolveSetting(settingsOptions.getSelectedValue().toString()) == Settings.CONTACTS) {

                        if (contactEditor == null) {
                            contactEditor = new GeneralViewerEditor<Contact>(ElementEditor.CONTACTS,
                                    menuPanels,
                                    UserProfileManager.getCurrentUser().getPreviouslyUsedContacts());
                        }

                        changeCentralPanel(contactEditor);
                    } else if (Settings.resolveSetting(settingsOptions.getSelectedValue().toString()) == Settings.PROTOCOLS) {

                        if (protocolEditor == null) {
                            protocolEditor = new GeneralViewerEditor<Protocol>(ElementEditor.PROTOCOLS,
                                    menuPanels,
                                    UserProfileManager.getCurrentUser().getPreviouslyUsedProtocols());
                        }

                        changeCentralPanel(protocolEditor);
                    } else if (Settings.resolveSetting(settingsOptions.getSelectedValue().toString()) == Settings.DATA_LOCATIONS) {

                        if (dataLocationSettings == null) {
                            dataLocationSettings = new DataLocationSettings(settings, defaultSettings);

                        }

                        changeCentralPanel(dataLocationSettings);
                        dataLocationSettings.updateLocations();
                    } else if (Settings.resolveSetting(settingsOptions.getSelectedValue().toString()) == Settings.VALIDATION) {

                        if (validationSettings == null) {
                            validationSettings = new ValidationSettings(settings, defaultSettings);

                        }
                        changeCentralPanel(validationSettings);
                    }
                }
            }
        });

        JScrollPane listScroller = new JScrollPane(settingsOptions, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setPreferredSize(new Dimension(175, 250));
        listScroller.getViewport().setOpaque(false);
        IAppWidgetFactory.makeIAppScrollPane(listScroller);
        listScroller.setBackground(UIHelper.BG_COLOR);

        JPanel westPanel = new JPanel();
        westPanel.add(listScroller);
        westPanel.setBackground(UIHelper.BG_COLOR);

        westPanel.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "available settings", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

        settingsScreen.add(westPanel, BorderLayout.WEST);

        centralPanel = new JPanel(new BorderLayout());
        settingsScreen.add(centralPanel, BorderLayout.CENTER);

        settingsOptions.setSelectedIndex(0);
        return settingsScreen;
    }

    private void changeCentralPanel(final JPanel newPanel) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                centralPanel.removeAll();
                centralPanel.add(newPanel);
                centralPanel.validate();
                centralPanel.repaint();
            }
        });
    }

    private void callUpdate() {

        httpProxy.updateSettings();

        if (ontology != null) {
            ontology.updateSettings();
        }

        if (contactEditor != null) {
            contactEditor.updateSettings();
        }

        if (protocolEditor != null) {
            protocolEditor.updateSettings();
        }

        if (dataLocationSettings != null) {
            dataLocationSettings.updateSettings();
        }

        if (validationSettings != null) {
            validationSettings.updateSettings();
        }

        PropertyFileIO.updateISAcreatorProperties(settings);
        PropertyFileIO.saveProperties(settings, PROPERTIES_FILE);
        PropertyFileIO.setProxy(settings);
    }


    enum Settings {
        HTTP_PROXY("http proxy"), ONTOLOGY("ontologies"),
        CONTACTS("contacts"), PROTOCOLS("protocols"), DATA_LOCATIONS("program file locations"),
        VALIDATION("validation");
        private String name;

        Settings(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }


        public static Settings resolveSetting(String s) {
            for (Settings setting : values()) {
                if (setting.getName().equals(s)) {
                    return setting;
                }
            }

            return null;
        }
    }


}
