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
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * AuthenticationGUI provides the GUI which allows users to log in to the software
 * Alternatively, if they haven't yet got a login, they can create a profile using
 * the create profile button.
 *
 * @author eamonnmaguire
 * @date Mar 3, 2010
 */


public class Authentication extends MenuUIComponent {

    private JLabel status;
    private JPasswordField password;
    private JTextField username;
    @InjectedResource
    public ImageIcon pleaseLogin, loginButton, loginButtonOver, createProfileButton,
            createProfileButtonOver, exitButtonSml, exitButtonSmlOver;

    public Authentication(ISAcreatorMenu menu) {
        super(menu);
        status = new JLabel();
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void clearFields() {
        status.setText("");
        password.setText("");
        username.setText("");
    }

    void createGUI() {
        // create username field info
        Box fields = Box.createVerticalBox();
        fields.add(Box.createVerticalStrut(10));
        fields.setOpaque(false);

        JPanel userNameCont = new JPanel(new GridLayout(1, 2));
        JLabel usernameLabel = new JLabel("username ");
        usernameLabel.setFont(UIHelper.VER_12_BOLD);
        usernameLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        userNameCont.add(usernameLabel);

        username = new JTextField(10);
        UIHelper.renderComponent(username, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        userNameCont.add(username);
        userNameCont.setOpaque(false);

        JPanel passwordCont = new JPanel(new GridLayout(1, 2));
        JLabel passwordLabel = new JLabel("password ");
        passwordLabel.setFont(UIHelper.VER_12_BOLD);
        passwordLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        passwordCont.add(passwordLabel);
        password = new JPasswordField("");
        UIHelper.renderComponent(password, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        passwordCont.add(password);
        passwordCont.setOpaque(false);

        fields.add(userNameCont);
        fields.add(Box.createVerticalStrut(10));
        fields.add(passwordCont);

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(
                pleaseLogin,
                JLabel.RIGHT), BorderLayout.NORTH);
        northPanel.add(fields, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(4, 1));
        southPanel.setOpaque(false);

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
        buttonContainer.setOpaque(false);

        final JLabel createProfile = new JLabel(createProfileButton,
                JLabel.LEFT);
        createProfile.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                createProfile.setIcon(createProfileButton);

                clearFields();

                confirmExitPanel.setVisible(false);

                menu.changeView(menu.getCreateProfileGUI());
            }

            public void mouseEntered(MouseEvent event) {
                createProfile.setIcon(createProfileButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                createProfile.setIcon(createProfileButton);
            }
        });

        buttonContainer.add(createProfile);

        final JLabel login = new JLabel(this.loginButton,
                JLabel.RIGHT);
        login.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                login.setIcon(Authentication.this.loginButton);
                confirmExitPanel.setVisible(false);
                login();
            }

            public void mouseEntered(MouseEvent event) {
                login.setIcon(loginButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                login.setIcon(Authentication.this.loginButton);
            }
        });

        Action loginAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        };

        password.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "LOGIN");
        password.getActionMap().put("LOGIN", loginAction);
        username.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "LOGIN");
        username.getActionMap().put("LOGIN", loginAction);

        buttonContainer.add(login);

        southPanel.add(status);
        southPanel.add(buttonContainer);

        final JLabel exit = new JLabel(exitButtonSml,
                JLabel.CENTER);
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

        southPanel.add(exitCont);

        southPanel.add(confirmExitPanel);

        northPanel.add(southPanel, BorderLayout.SOUTH);
        northPanel.setOpaque(false);

        add(northPanel, BorderLayout.CENTER);
    }

    public void login() {
        boolean found = false;

        for (UserProfile up : menu.getMain().getUserProfiles()) {
            if (up.getUsername().equals(username.getText())) {
                char[] pwd = password.getPassword();
                String pwdAsString = "";

                for (char c : pwd) {
                    pwdAsString += c;
                }

                if (up.getPassword() == pwdAsString.hashCode()) {
                    clearFields();

                    menu.getMain().setCurrentUser(up);
                    menu.getMain().setUserHistory(up.getUserHistory());
                    Spreadsheet.fileSelectEditor.setFtpManager(up.getFtpManager());
                    menu.changeView(menu.getMainMenuGUI());
                    found = true;

                    break;
                }
            }
        }

        if (!found) {
            status.setText(
                    "<html><b>Error: </b> Username or password incorrect! </html>");
        }
    }
}