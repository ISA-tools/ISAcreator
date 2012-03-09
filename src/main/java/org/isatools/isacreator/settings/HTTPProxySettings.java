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

package org.isatools.isacreator.settings;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedFormattedTextField;
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.isatools.isacreator.effects.components.RoundedJTextField;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Properties;

/**
 * @author Eamonn Maguire
 * @date Aug 28, 2009
 */


public class HTTPProxySettings extends SettingsScreen {

    private JPanel proxyDetails;
    private JPanel proxyAuthentication;

    private JPanel loginPanel;
    private JPanel passwordPanel;

    private JPanel hostNamePanel;
    private JPanel portNumberPanel;

    // general fields
    private JCheckBox useProxy;
    private JTextField hostName;
    private JTextField portNumber;

    // authentication fields
    private JCheckBox useProxyAuthentication;
    private JTextField login;
    private JPasswordField password;

    public HTTPProxySettings(Properties settings) {

        this.settings = settings;
        setLayout(new BorderLayout());
        setOpaque(false);
        add(createProxyConfigPanel(), BorderLayout.NORTH);
        setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "configure http proxy", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

        updatePanelIsActive(useProxyAuthentication.isSelected() && useProxy.isSelected(), loginPanel, passwordPanel);
        useProxyAuthentication.setEnabled(useProxy.isSelected());
        updatePanelIsActive(useProxy.isSelected(),
                hostNamePanel, portNumberPanel);
    }

    // to contain section

    private JPanel createProxyConfigPanel() {

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setOpaque(false);

        useProxy = new JCheckBox("use http proxy", Boolean.valueOf(settings.getProperty("httpProxy.usedLast")));
        useProxy.setHorizontalAlignment(SwingConstants.LEFT);
        UIHelper.renderComponent(useProxy, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, false);

        useProxy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                useProxyAuthentication.setEnabled(useProxy.isSelected());
                updatePanelIsActive(useProxy.isSelected(),
                        hostNamePanel, portNumberPanel);

                updatePanelIsActive(useProxyAuthentication.isEnabled() && useProxyAuthentication.isSelected(), loginPanel, passwordPanel);
            }
        });

        container.add(UIHelper.wrapComponentInPanel(useProxy));

        proxyDetails = new JPanel();
        proxyDetails.setLayout(new BoxLayout(proxyDetails, BoxLayout.PAGE_AXIS));

        // add host name and port number
        hostNamePanel = new JPanel(new GridLayout(1, 3));
        hostNamePanel.setOpaque(false);
        hostNamePanel.add(Box.createHorizontalStrut(10));
        hostNamePanel.add(UIHelper.createLabel("host name", UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR));
        hostName = new RoundedJTextField(10);
        hostName.setText(settings.getProperty("httpProxy.hostname"));
        UIHelper.renderComponent(hostName, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        hostNamePanel.add(hostName);

        proxyDetails.add(hostNamePanel);

        portNumberPanel = new JPanel(new GridLayout(1, 3));
        portNumberPanel.setOpaque(false);
        portNumberPanel.add(Box.createHorizontalStrut(10));
        portNumberPanel.add(UIHelper.createLabel("port number", UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR));
        portNumber = new RoundedJTextField(10);
        portNumber.setText(settings.getProperty("httpProxy.portNumber"));
        UIHelper.renderComponent(portNumber, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, false);
        portNumberPanel.add(portNumber);

        proxyDetails.add(portNumberPanel);

        useProxyAuthentication = new JCheckBox("proxy authentication");
        useProxyAuthentication.setHorizontalAlignment(SwingConstants.LEFT);
        UIHelper.renderComponent(useProxyAuthentication, UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR, false);

        useProxyAuthentication.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                updatePanelIsActive(useProxyAuthentication.isSelected(), loginPanel, passwordPanel);
            }
        });

        useProxyAuthentication.setSelected(Boolean.valueOf(settings.getProperty("httpProxy.useAuth")));

        proxyDetails.add(UIHelper.wrapComponentInPanel(useProxyAuthentication));

        container.add(proxyDetails);


        // now add authentication part!
        proxyAuthentication = new JPanel();
        proxyAuthentication.setLayout(new BoxLayout(proxyAuthentication, BoxLayout.PAGE_AXIS));
        proxyAuthentication.setOpaque(false);

        loginPanel = new JPanel(new GridLayout(1, 3));
        loginPanel.setOpaque(false);
        loginPanel.add(Box.createHorizontalStrut(10));
        loginPanel.add(UIHelper.createLabel("login", UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR));
        login = new RoundedJTextField(10);
        login.setText(settings.getProperty("httpProxy.login"));
        UIHelper.renderComponent(login, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        loginPanel.add(login);

        proxyAuthentication.add(loginPanel);

        passwordPanel = new JPanel(new GridLayout(1, 3));
        passwordPanel.setOpaque(false);
        passwordPanel.add(Box.createHorizontalStrut(10));
        passwordPanel.add(UIHelper.createLabel("password", UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR));
        password = new RoundedJPasswordField(10);
        password.setText(settings.getProperty("httpProxy.password"));
        UIHelper.renderComponent(password, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        passwordPanel.add(password);

        proxyAuthentication.add(passwordPanel);
        container.add(proxyAuthentication);


        return container;
    }

    private void updatePanelIsActive(boolean active, Container... panels) {
        for (Container p : panels) {
            disableEnableJPanelContents(p, active);
        }
        revalidate();
        repaint();
    }

    private void disableEnableJPanelContents(Container panel, boolean isEnabled) {
        for (Component c : panel.getComponents()) {
            c.setEnabled(isEnabled);
        }

    }

    public boolean updateSettings() {
        // set new properties...
        try {
            settings.setProperty("httpProxy.usedLast", String.valueOf(useProxy.isSelected()));
            settings.setProperty("httpProxy.hostname", hostName.getText());
            settings.setProperty("httpProxy.portNumber", portNumber.getText());
            settings.setProperty("httpProxy.useAuth", String.valueOf(useProxyAuthentication.isSelected()));
            settings.setProperty("httpProxy.login", login.getText());
            settings.setProperty("httpProxy.password", new String(password.getPassword()));
            return true;
        } catch (Exception e) {
            log.error("Problem occurred when trying to update HTPP proxy settings.");
            return false;
        }
    }

    protected void performImportLogic() {

    }

    protected void performExportLogic() {

    }

    protected void performDeletionLogic() {

    }


}
