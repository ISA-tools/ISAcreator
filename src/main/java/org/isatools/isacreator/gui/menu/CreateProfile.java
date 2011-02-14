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
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.isatools.isacreator.io.UserProfile;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CreateProfileGUI provides interface to allow users to construct a new profile
 * so that their settings such as previously selected ontology terms, factors,
 * protocols, and contacts can be retrieved.
 *
 * @author Eamonn Maguire
 */


public class CreateProfile extends MenuUIComponent {
    @InjectedResource
    private ImageIcon createProfileButton, createProfileButtonOver,
            backButtonSml, backButtonSmlOver;


    private JLabel status;
    private JTextField emailVal;
    private JTextField firstnameVal;
    private JTextField institutionVal;
    private JPasswordField passwordVal;
    private JPasswordField confirmPasswordVal;
    private JTextField surnameVal;
    private JTextField usernameVal;

    public CreateProfile(ISAcreatorMenu menu) {
        super(menu);
        status = new JLabel("                                      ");
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(350, 400));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        Box fields = Box.createVerticalBox();
        fields.add(Box.createVerticalStrut(10));
        fields.setOpaque(false);

        Action createProfileAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                createProfile();
            }
        };

        // username
        JPanel userNameCont = createPanel();
        JLabel usernameLabel = createLabel("username *");
        userNameCont.add(usernameLabel);

        usernameVal = createTextField();
        userNameCont.add(usernameVal);
        assignKeyActionToComponent(createProfileAction, usernameVal);

        //password
        JPanel passwordCont = createPanel();
        JLabel passwordLabel = createLabel("password *");
        passwordCont.add(passwordLabel);

        passwordVal = new RoundedJPasswordField(10);
        UIHelper.renderComponent(passwordVal, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        passwordCont.add(passwordVal);
        assignKeyActionToComponent(createProfileAction, passwordVal);

        //password
        JPanel confirmPasswordCont = createPanel();
        JLabel confirmPasswordLabel = createLabel("confirm *");
        confirmPasswordCont.add(confirmPasswordLabel);

        confirmPasswordVal = new RoundedJPasswordField(10);
        UIHelper.renderComponent(confirmPasswordVal, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        confirmPasswordCont.add(confirmPasswordVal);
        assignKeyActionToComponent(createProfileAction, confirmPasswordVal);

        //forename
        JPanel firstNameCont = createPanel();
        JLabel firstNameLabel = createLabel("forename *");
        firstNameCont.add(firstNameLabel);

        firstnameVal = createTextField();
        firstNameCont.add(firstnameVal);
        assignKeyActionToComponent(createProfileAction, firstnameVal);

        //surname
        JPanel surnameCont = createPanel();
        JLabel surnameLabel = createLabel("surname *");
        surnameCont.add(surnameLabel);

        surnameVal = createTextField();
        surnameCont.add(surnameVal);
        assignKeyActionToComponent(createProfileAction, surnameVal);

        // institution
        JPanel institutionCont = createPanel();
        JLabel institutionLabel = createLabel("institution *");
        institutionCont.add(institutionLabel);

        institutionVal = createTextField();
        institutionCont.add(institutionVal);
        assignKeyActionToComponent(createProfileAction, institutionVal);

        // email
        JPanel emailCont = createPanel();
        JLabel emailLabel = createLabel("email *");
        emailCont.add(emailLabel);

        emailVal = createTextField();
        emailCont.add(emailVal);
        assignKeyActionToComponent(createProfileAction, emailVal);

        fields.add(userNameCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(passwordCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(confirmPasswordCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(firstNameCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(surnameCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(institutionCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(emailCont);

        JLabel info = new JLabel(
                "<html><b>* </b> indicates required field </html>", SwingConstants.RIGHT);
        info.setForeground(UIHelper.DARK_GREEN_COLOR);
        fields.add(UIHelper.wrapComponentInPanel(info));

        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(
                new ImageIcon(getClass()
                        .getResource("/images/gui/createNewProfile.png")),
                JLabel.RIGHT), BorderLayout.NORTH);
        northPanel.add(fields, BorderLayout.CENTER);

        JPanel buttonContainer = new JPanel(new BorderLayout());

        final JLabel back = new JLabel(backButtonSml,
                JLabel.LEFT);
        back.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                back.setIcon(backButtonSml);
                menu.changeView(menu.getAuthenticationGUI());
            }

            public void mouseEntered(MouseEvent event) {
                back.setIcon(backButtonSmlOver);
            }

            public void mouseExited(MouseEvent event) {
                back.setIcon(backButtonSml);
            }
        });

        buttonContainer.add(back, BorderLayout.WEST);


        final JLabel createProfile = new JLabel(this.createProfileButton,
                JLabel.RIGHT);
        createProfile.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                createProfile.setIcon(createProfileButton);
                createProfile();
            }

            public void mouseEntered(MouseEvent event) {
                createProfile.setIcon(createProfileButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                createProfile.setIcon(createProfileButton);
            }
        });


        buttonContainer.add(createProfile, BorderLayout.EAST);

        JPanel southPanel = new JPanel(new GridLayout(2, 1));
        southPanel.setOpaque(false);

        JPanel statusContainer = new JPanel(new GridLayout(1, 1));
        statusContainer.setOpaque(false);
        statusContainer.setPreferredSize(new Dimension(300, 30));
        statusContainer.add(status);

        southPanel.add(UIHelper.wrapComponentInPanel(statusContainer));
        southPanel.add(buttonContainer);

        northPanel.add(southPanel, BorderLayout.SOUTH);

        northPanel.setOpaque(false);
        add(northPanel, BorderLayout.CENTER);
    }

    private void assignKeyActionToComponent(Action action, JComponent field) {
        field.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "CREATE_PROFILE");
        field.getActionMap().put("CREATE_PROFILE", action);
    }

    private void createProfile() {
        // check password is not empty and that the password and the confirmation match!
        String password = new String(passwordVal.getPassword());
        if (!password.equals("")) {
            String passwordConfirmation = new String(confirmPasswordVal.getPassword());
            if (!password.equals(passwordConfirmation)) {
                status.setText(
                        "<html><b>passwords do not match!</b> the password and confirmation must match!</html>");
                return;
            }
        } else {
            status.setText(
                    "<html><b>password is required!</b></html>");
            return;
        }

        // check the rest of the fields to ensure values have been entered and proceed to creating the
        // profile if everything is ok!
        if (!usernameVal.getText().equals("")) {
            if (!firstnameVal.getText().equals("")) {
                if (!surnameVal.getText().equals("")) {
                    if (!institutionVal.getText().equals("")) {
                        if (!emailVal.getText().equals("")) {
                            Pattern p = Pattern.compile("[.]*@[.]*");
                            Matcher m = p.matcher(emailVal.getText());

                            if (m.find()) {
                                UserProfile newUser = new UserProfile(usernameVal.getText(),
                                        new String(passwordVal.getPassword()).hashCode(),
                                        firstnameVal.getText(),
                                        surnameVal.getText(),
                                        institutionVal.getText(),
                                        emailVal.getText());
                                boolean dupUser = false;

                                for (UserProfile up : menu.getMain().getUserProfiles()) {
                                    if (up.getUsername()
                                            .equals(usernameVal.getText())) {
                                        dupUser = true;
                                        status.setText(
                                                "<html><b>user name taken!</b> this username is already in use</html>");

                                        break;
                                    }
                                }

                                if (!dupUser) {
                                    menu.getMain().getUserProfiles().add(newUser);
                                    menu.getMain().setCurrentUser(newUser);
                                    menu.getMain().setUserHistory(newUser.getUserHistory());
                                    menu.getMain().saveUserProfiles();

                                    menu.changeView(menu.getMainMenuGUI());
                                }
                            } else {
                                status.setText(
                                        "<html><b>email is not valid!</b> please enter a valid email address</html>");
                            }
                        } else {
                            status.setText(
                                    "<html><b>email is required!</b> please enter an email</html>");
                        }
                    } else {
                        status.setText(
                                "<html><b>institution is required!</b> please enter a surname</html>");
                    }
                } else {
                    status.setText(
                            "<html><b>surname is required!</b> please enter a surname</html>");
                }
            } else {
                status.setText(
                        "<html><b>forename is required!</b> please enter a forename</html>");
            }
        } else {
            status.setText(
                    "<html><b>username is required!</b> please enter a username</html>");
        }
    }
}

