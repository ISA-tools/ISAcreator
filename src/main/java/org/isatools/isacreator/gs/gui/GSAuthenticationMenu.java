package org.isatools.isacreator.gs.gui;

import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gs.GSIdentityManager;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.MenuUIComponent;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 08/10/2012
 * Time: 17:26
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSAuthenticationMenu extends MenuUIComponent {

    private JLabel status;
    private JPasswordField password;
    private JTextField username;

    private JLabel createProfile, login, exit;

    @InjectedResource
    public ImageIcon pleaseLogin, loginButton, loginButtonOver, createProfileButton,
            createProfileButtonOver, exitButtonSml, exitButtonSmlOver;

    public GSAuthenticationMenu(ISAcreatorMenu menu) {
        super(menu);
        status = new JLabel();
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        // create username field info
        Box fields = Box.createVerticalBox();
        fields.add(Box.createVerticalStrut(10));
        fields.setOpaque(false);

        JPanel userNameCont = new JPanel(new GridLayout(1, 2));
        JLabel usernameLabel = new JLabel("username ");
        usernameLabel.setFont(UIHelper.VER_12_BOLD);
        usernameLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        userNameCont.add(usernameLabel);

        username = new RoundedJTextField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        username.setOpaque(false);


        UIHelper.renderComponent(username, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        userNameCont.add(username);
        userNameCont.setOpaque(false);

        JPanel passwordCont = new JPanel(new GridLayout(1, 2));
        JLabel passwordLabel = new JLabel("password ");
        passwordLabel.setFont(UIHelper.VER_12_BOLD);
        passwordLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        passwordCont.add(passwordLabel);
        password = new RoundedJPasswordField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        UIHelper.renderComponent(password, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

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

        /*
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
        */

        //buttonContainer.add(createProfile);

        login = new JLabel(loginButton,
                JLabel.RIGHT);
        login.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                login.setIcon(GSAuthenticationMenu.this.loginButton);
                confirmExitPanel.setVisible(false);
                login();
            }

            public void mouseEntered(MouseEvent event) {
                login.setIcon(loginButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                login.setIcon(GSAuthenticationMenu.this.loginButton);
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

        JPanel exitCont = new JPanel(new GridLayout(1, 1));
        exitCont.setOpaque(false);

        exitCont.add(exit);

        southPanel.add(exitCont);

        southPanel.add(confirmExitPanel);

        northPanel.add(southPanel, BorderLayout.SOUTH);
        northPanel.setOpaque(false);

        add(northPanel, BorderLayout.CENTER);
    }


    public void login(){
        Authentication authentication = new GSIdentityManager();
        if (authentication.login(username.getText(), password.getPassword())){
            clearFields();
            menu.changeView(menu.getMainMenuGUI());
        } else {
            status.setText(
                    "<html><b>Error: </b> Username or password incorrect! </html>");
        }

    }

    public void clearFields() {
        status.setText("");
        password.setText("");
        username.setText("");
    }

}
