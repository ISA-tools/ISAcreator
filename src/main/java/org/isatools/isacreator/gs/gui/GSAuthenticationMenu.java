package org.isatools.isacreator.gs.gui;

import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.FileType;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.api.ImportConfiguration;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gs.GSLocalFilesManager;
import org.isatools.isacreator.gui.menu.ErrorMenu;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.MenuUIComponent;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
    private JLabel register, login, exit, ssoLabel;
    //checkbox for single sign on
    private JCheckBox sso;

    private Authentication authentication = null;

    @InjectedResource
    public ImageIcon pleaseLogin, loginButton, loginButtonOver, registerIcon, registerOverIcon,
            exitButtonSml, exitButtonSmlOver, genomespacelogo, ssoIcon, ssoOverIcon;

    public GSAuthenticationMenu(ISAcreatorMenu menu, Authentication authManager) {
        super(menu);

        authentication = authManager;

        status = new JLabel();
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(400, 300));
        setLayout(new BorderLayout());
        setOpaque(false);
        setClassFields();
    }

    public GSAuthenticationMenu(ISAcreatorMenu menu, Authentication authManager, String defaultUsername) {
        this(menu, authManager);
        username.setText(defaultUsername);
    }

    private void setClassFields() {
        username = new RoundedJTextField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
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

        username.setOpaque(false);

        UIHelper.renderComponent(username, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        userNameCont.add(username);
        userNameCont.setOpaque(false);
        username.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                status.setText("");
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

        password.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                status.setText("");
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

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);

        login = new JLabel(loginButton,
                JLabel.RIGHT);
        login.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                login.setIcon(GSAuthenticationMenu.this.loginButton);
                confirmExitPanel.setVisible(false);
                status.setText("");
                menu.showProgressPanel("Logging in to GenomeSpace...");
                login();
            }

            public void mouseEntered(MouseEvent event) {
                login.setIcon(loginButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                login.setIcon(loginButton);
            }
        });


        login.addFocusListener(new FocusAdapter() {

            public void focusGained(FocusEvent e) {
                status.setText("");
            }
        }
        );

        Action loginAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                menu.showProgressPanel("Logging in to GenomeSpace...");
                login();
            }
        };

        password.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "LOGIN");
        password.getActionMap().put("LOGIN", loginAction);
        username.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "LOGIN");
        username.getActionMap().put("LOGIN", loginAction);

        final JLabel registerButton = new JLabel(registerIcon);
        UIHelper.renderComponent(registerButton, UIHelper.VER_9_BOLD, UIHelper.DARK_GREEN_COLOR, false);


        /*
        //code to use the API to register when ready
        //register
        register = new JLabel(registerIcon,
                JLabel.LEFT);
        register.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                register.setIcon(registerIcon);
                clearFields();
                confirmExitPanel.setVisible(false);
                //TODO change this for registration menu
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
        */

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                registerButton.setIcon(registerIcon);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                registerButton.setIcon(registerOverIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                registerButton.setIcon(registerIcon);
                try {
                    Desktop.getDesktop().browse(new URI("http://www.genomespace.org/register"));
                } catch (IOException e1) {
                    log.error("IOException - " + e1.getMessage());
                } catch (URISyntaxException e1) {
                    log.error("URISyntaxException - " + e1.getMessage());
                }
            }
        });

        buttonContainer.add(registerButton, BorderLayout.WEST);
        buttonContainer.add(login, BorderLayout.EAST);


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
        JPanel exitContainer = new JPanel(new GridLayout(1, 1));
        exitContainer.setOpaque(false);
        exitContainer.add(exit);

        //south panel
        JPanel southPanel = new JPanel(new GridLayout(4, 1));
        southPanel.setOpaque(false);
        southPanel.add(status);
        southPanel.add(buttonContainer);
        southPanel.add(exitContainer);
        southPanel.add(confirmExitPanel);

        northPanel.add(southPanel, BorderLayout.SOUTH);
        northPanel.setOpaque(false);

        add(northPanel, BorderLayout.CENTER);
    }


    private void login() {
        Thread performer = new Thread(new Runnable() {
            public void run() {
                // success, so load

                String passwordString = new String(password.getPassword());

                if (!username.getText().equals("") && !passwordString.equals("") && authentication.login(username.getText(), password.getPassword())) {
                    menu.stopProgressIndicator();

                    //logged in
                    clearFields();
                    if (ISAcreatorCLArgs.configDir() == null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                menu.resetViewAfterProgress();
                                menu.changeView(menu.getImportConfigurationGUI());
                            }
                        });

                    } else {
                        //load configuration and go to main menu
                        ImportConfiguration importConfiguration = new ImportConfiguration(ISAcreatorCLArgs.configDir());
                        boolean successful = importConfiguration.loadConfiguration();

                        if (successful) {

                            if (ISAcreatorCLArgs.isatabDir() != null) {

                                java.util.List<ErrorMessage> errors = GSLocalFilesManager.downloadFiles(menu.getAuthentication());

                                if (!errors.isEmpty()) {
                                    //Problem downloading the files

                                    //load menu to show errors when loading files
                                    System.out.println("Number of errors: " + errors.size());
                                    System.out.println("Showing first one: " + errors.get(0).getMessage());


                                    //status.setText(errors.get(0).getMessage());


                                    ISAFileErrorReport error = new ISAFileErrorReport("", FileType.INVESTIGATION, errors);
                                    List<ISAFileErrorReport> list = new ArrayList<ISAFileErrorReport>();
                                    list.add(error);


                                    ErrorMenu errorMenu = new ErrorMenu(menu, list, false, menu.getMainMenuGUI());
                                    errorMenu.createGUI();

                                } else {
                                    SwingUtilities.invokeLater(new Runnable() {
                                        public void run() {
                                            menu.resetViewAfterProgress();
                                            menu.loadFiles(ISAcreatorCLArgs.isatabDir());
                                        }
                                    });
                                }
                            } else {
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        menu.resetViewAfterProgress();
                                        menu.changeView(menu.getMainMenuGUI());
                                    }
                                });
                            }
                        } else {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    menu.resetViewAfterProgress();
                                    status.setText(
                                            "<html><b>Error: </b> Unable to connect to GenomeSpace. </html>");
                                }
                            });
                            //TODO display problem!!!
                        }
                    }

                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            menu.stopProgressIndicator();
                            menu.resetViewAfterProgress();
                            status.setText(
                                    "<html><b>Error: </b> Username or password incorrect! </html>");
                        }
                    });

                }
            }
        }
        );
        performer.start();
    }

    public void clearFields() {
        status.setText("");
        password.setText("");
        username.setText("");
    }

}
