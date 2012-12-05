package org.isatools.isacreator.gs.gui;

import org.isatools.isacreator.api.CreateProfile;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gs.GSIdentityManager;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.UserCreationMenu;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 09/10/2012
 * Time: 23:28
 *
 * Menu to allow users to register to GS from ISAcreator
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSRegistrationMenu extends UserCreationMenu {

    @InjectedResource
    public ImageIcon registerIcon, registerOverIcon;

    private JLabel register;


    public GSRegistrationMenu(ISAcreatorMenu menu){
        super(menu);
        status = new JLabel("                                      ");
        status.setForeground(UIHelper.RED_COLOR);
        setPreferredSize(new Dimension(350, 400));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    @Override
    protected void createGUI() {
        Box fields = Box.createVerticalBox();
        fields.add(Box.createVerticalStrut(4));
        fields.setOpaque(false);

        Action registerAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        };

        JPanel userNameCont = createUsernamePanel(registerAction);
        JPanel passwordCont = createPasswordPanel(registerAction);
        JPanel confirmPasswordCont = createConfirmPasswordPanel(registerAction);
        JPanel emailCont = createEmailPanel(registerAction);

        fields.add(userNameCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(passwordCont);
        fields.add(Box.createVerticalStrut(7));
        fields.add(confirmPasswordCont);
        fields.add(Box.createVerticalStrut(7));
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

        createBackJLabel();

        buttonContainer.add(back, BorderLayout.WEST);

        register = new JLabel(this.registerIcon,
                JLabel.RIGHT);
        register.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                register.setIcon(registerIcon);
                register();
            }

            public void mouseEntered(MouseEvent event) {
                register.setIcon(registerOverIcon);
            }

            public void mouseExited(MouseEvent event) {
                register.setIcon(registerIcon);
            }
        });


        buttonContainer.add(register, BorderLayout.EAST);

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


    private void register(){
        // check password is not empty and that the password and the confirmation match!
        if (CreateProfile.emptyPassword(passwordVal.getPassword())) {
            status.setText(
                    "<html><b>password is required!</b></html>");
            return;
        }
        if (!CreateProfile.matchingPasswords(passwordVal.getPassword(),confirmPasswordVal.getPassword())){
            status.setText(
                    "<html><b>passwords do not match!</b> the password and confirmation must match!</html>");
            return;
        }

        // check the rest of the fields to ensure values have been entered and proceed to creating the
        // profile if everything is ok!
        if (!CreateProfile.emptyField(usernameVal.getText())) {

                        if (!CreateProfile.emptyField(emailVal.getText())) {
                            if (CreateProfile.validEmail(emailVal.getText())) {
                                if (CreateProfile.duplicateUser(usernameVal.getText())){
                                    status.setText(
                                            "<html><b>user name taken!</b> this username is already in use</html>");
                                }else{

                                    //TODO only create local profile if GS registration was successful
                                    CreateProfile.createProfile(usernameVal.getText(), passwordVal.getPassword(),null,null,"default-organisation",emailVal.getText());
                                    String result = GSIdentityManager.registerUser(usernameVal.getText(), passwordVal.getPassword().toString(), emailVal.getText());
                                    if (result.equals(""))
                                        menu.changeView(menu.getMainMenuGUI());
                                    else

                                        status.setText(
                                                "<html><b>"+result+"</b> please try again</html>");

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
        }//register method

}
