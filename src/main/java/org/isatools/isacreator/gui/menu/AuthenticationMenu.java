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
import org.isatools.isacreator.api.ImportConfiguration;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;
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
 * Date: Mar 3, 2010
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 */
public class AuthenticationMenu extends MenuUIComponent {

    private static final Logger log = Logger.getLogger(AuthenticationMenu.class);

    private JLabel status;
    private JPasswordField password;
    private JTextField username;

    private JLabel createProfile, login, exit;

    private Authentication authentication = null;

    @InjectedResource
    public ImageIcon pleaseLogin, loginButton, loginButtonOver, createProfileButton,
            createProfileButtonOver, exitButtonSml, exitButtonSmlOver;

    /**
     * Constructor
     *
     * @param menu the ISAcreatorMenu to which this authentication menu is associated
     * @param auth an object implementing the Authentication interface
     */
    public AuthenticationMenu(ISAcreatorMenu menu, Authentication auth) {
        super(menu);
        authentication = auth;
        status = new JLabel();
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout());
        setOpaque(false);

        username = new RoundedJTextField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
    }

    public AuthenticationMenu(ISAcreatorMenu menu, Authentication auth, String un) {
        this(menu, auth);
        if (un!=null)
            username.setText(un);
    }

    /**
     * Setting all fields back to empty string
     *
     */
    public void clearFields() {
        status.setText("");
        password.setText("");
        username.setText("");
    }


    public void createGUI() {
        // create username field info
        Box fields = Box.createVerticalBox();
        fields.add(Box.createVerticalStrut(10));
        fields.setOpaque(false);

        JPanel userNameContainer = new JPanel(new GridLayout(1, 2));
        JLabel usernameLabel = new JLabel("username ");
        usernameLabel.setFont(UIHelper.VER_12_BOLD);
        usernameLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        userNameContainer.add(usernameLabel);

        username.setOpaque(false);

        UIHelper.renderComponent(username, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        userNameContainer.add(username);
        userNameContainer.setOpaque(false);

        JPanel passwordContainer = new JPanel(new GridLayout(1, 2));
        JLabel passwordLabel = new JLabel("password ");
        passwordLabel.setFont(UIHelper.VER_12_BOLD);
        passwordLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        passwordContainer.add(passwordLabel);
        password = new RoundedJPasswordField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        UIHelper.renderComponent(password, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        passwordContainer.add(password);
        passwordContainer.setOpaque(false);

        fields.add(userNameContainer);
        fields.add(Box.createVerticalStrut(10));
        fields.add(passwordContainer);

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(
                pleaseLogin,
                JLabel.RIGHT), BorderLayout.NORTH);
        northPanel.add(fields, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new GridLayout(4, 1));
        southPanel.setOpaque(false);

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
        buttonContainer.setOpaque(false);

        createProfile = new JLabel(createProfileButton,
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

        login = new JLabel(loginButton,
                JLabel.RIGHT);
        login.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                login.setIcon(AuthenticationMenu.this.loginButton);
                confirmExitPanel.setVisible(false);
                login();
            }

            public void mouseEntered(MouseEvent event) {
                login.setIcon(loginButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                login.setIcon(AuthenticationMenu.this.loginButton);
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

        exit = new JLabel(exitButtonSml,
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

        JPanel exitContainer = new JPanel(new GridLayout(1, 1));
        exitContainer.setOpaque(false);

        exitContainer.add(exit);

        southPanel.add(exitContainer);

        southPanel.add(confirmExitPanel);

        northPanel.add(southPanel, BorderLayout.SOUTH);
        northPanel.setOpaque(false);

        add(northPanel, BorderLayout.CENTER);
    }


    public void login(){
        if (authentication.login(username.getText(), password.getPassword())){
            clearFields();
            if (ISAcreatorCLArgs.configDir()==null)     {

                menu.changeView(menu.getImportConfigurationGUI());

            }else{ //configDir is not null

                //load configuration and go to main menu
                ImportConfiguration importConfiguration = new ImportConfiguration(ISAcreatorCLArgs.configDir());
                boolean successful = importConfiguration.loadConfiguration();

                if (successful) {

                    if (ISAcreatorCLArgs.isatabDir() != null) {

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    menu.loadFiles(ISAcreatorCLArgs.isatabDir(), false);

                                }
                            });

                    }else{

                        menu.changeView(menu.getMainMenuGUI());

                    }
                }//successful

       }
        }else{
            status.setText(
                    "<html><b>Error: </b> Username or password incorrect! </html>");
        }

    }

}