package org.isatools.isacreator.gs.gui;

import org.apache.log4j.Logger;
import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.MenuUIComponent;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 08/10/2012
 * Time: 17:26
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSAuthenticationMenu extends MenuUIComponent {

    private static final Logger log = Logger.getLogger(GSAuthenticationMenu.class);

    //GUI related fields
    private JLabel status;
    private JPasswordField password;
    private JTextField username;
    private JLabel iconLabel;
    private JLabel register, login, exit;

    private Authentication authentication = null;

    @InjectedResource
    public ImageIcon pleaseLogin, loginButton, loginButtonOver, registerIcon, registerOverIcon,
             exitButtonSml, exitButtonSmlOver, genomespacelogo;

    public GSAuthenticationMenu(ISAcreatorMenu menu, Authentication authManager) {
        super(menu);

        authentication = authManager;

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

        //username
        JPanel userNameCont = new JPanel(new GridLayout(1, 2));
        JLabel usernameLabel = new JLabel("GS username ");
        usernameLabel.setFont(UIHelper.VER_12_BOLD);
        usernameLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        userNameCont.add(usernameLabel);

        username = new RoundedJTextField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        username.setOpaque(false);

        UIHelper.renderComponent(username, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        userNameCont.add(username);
        userNameCont.setOpaque(false);
        username.addFocusListener( new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                status.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                //do nothing
            }
            }
        );



        //password
        JPanel passwordCont = new JPanel(new GridLayout(1, 2));
        JLabel passwordLabel = new JLabel("GS password ");
        passwordLabel.setFont(UIHelper.VER_12_BOLD);
        passwordLabel.setForeground(UIHelper.DARK_GREEN_COLOR);
        passwordCont.add(passwordLabel);
        password = new RoundedJPasswordField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        UIHelper.renderComponent(password, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        password.addFocusListener( new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                status.setText("");
            }
            @Override
            public void focusLost(FocusEvent e) {
                //do nothing
            }
        }
        );

        passwordCont.add(password);
        passwordCont.setOpaque(false);

        fields.add(userNameCont);
        fields.add(Box.createVerticalStrut(10));
        fields.add(passwordCont);

        //north panel
        JPanel northPanel = new JPanel();
        northPanel.add(new JLabel(
                pleaseLogin,
                JLabel.RIGHT), BorderLayout.NORTH);
        northPanel.add(fields, BorderLayout.CENTER);

        //south panel
        JPanel southPanel = new JPanel(new GridLayout(4, 1));
        southPanel.setOpaque(false);

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
        buttonContainer.setOpaque(false);

        //register
        register = new JLabel(registerIcon,
                JLabel.LEFT);
        register.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                register.setIcon(registerIcon);
                clearFields();
                confirmExitPanel.setVisible(false);
                menu.changeView(menu.getCreateProfileGUI());
            }

            public void mouseEntered(MouseEvent event) {
                register.setIcon(registerOverIcon);
            }

            public void mouseExited(MouseEvent event) {
                register.setIcon(registerIcon);
            }
        });
        buttonContainer.add(register);

        //login
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


        //TODO add GS logo
        //gs logo
        //iconLabel = new JLabel();
        //iconLabel.setIcon(genomespacelogo);


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

        //exit
        JPanel exitCont = new JPanel(new GridLayout(1, 1));
        exitCont.setOpaque(false);
        exitCont.add(exit);

        southPanel.add(exitCont);
        southPanel.add(confirmExitPanel);

        northPanel.add(southPanel, BorderLayout.SOUTH);
        northPanel.setOpaque(false);

        add(northPanel, BorderLayout.CENTER);
    }


    private void login(){
        String passwordString = new String (password.getPassword());
        if (!username.getText().equals("") && passwordString!=null && !passwordString.equals("") && authentication.login(username.getText(), password.getPassword())){
            clearFields();
            menu.changeView(menu.getImportConfigurationGUI());
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
