package org.isatools.isacreator.gui.menu;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.components.RoundedJPasswordField;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 15/10/2012
 * Time: 16:21
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public abstract class UserCreationMenu extends MenuUIComponent  {

    @InjectedResource
    public ImageIcon backButtonSml, backButtonSmlOver;

    protected JLabel status, back;
    protected JTextField emailVal;
    protected JPasswordField passwordVal;
    protected JPasswordField confirmPasswordVal;
    protected JTextField usernameVal;

    public UserCreationMenu(ISAcreatorMenu menu) {
        super(menu);
    }


    protected JPanel createEmailPanel(Action createProfileAction) {
        // email
        JPanel emailCont = createPanel();
        JLabel emailLabel = createLabel("email *");
        emailCont.add(emailLabel);

        emailVal = createTextField();
        emailCont.add(emailVal);
        assignKeyActionToComponent(createProfileAction, emailVal);
        return emailCont;
    }

    protected JPanel createConfirmPasswordPanel(Action createProfileAction) {
        //confirm password
        JPanel confirmPasswordCont = createPanel();
        JLabel confirmPasswordLabel = createLabel("confirm *");
        confirmPasswordCont.add(confirmPasswordLabel);

        confirmPasswordVal = new RoundedJPasswordField(10);
        UIHelper.renderComponent(confirmPasswordVal, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        confirmPasswordCont.add(confirmPasswordVal);
        assignKeyActionToComponent(createProfileAction, confirmPasswordVal);
        return confirmPasswordCont;
    }

    protected JPanel createPasswordPanel(Action createProfileAction) {
        //password
        JPanel passwordCont = createPanel();
        JLabel passwordLabel = createLabel("password *");
        passwordCont.add(passwordLabel);

        passwordVal = new RoundedJPasswordField(10);
        UIHelper.renderComponent(passwordVal, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        passwordCont.add(passwordVal);
        assignKeyActionToComponent(createProfileAction, passwordVal);
        return passwordCont;
    }

    protected JPanel createUsernamePanel(Action createProfileAction) {
        // username
        JPanel userNameCont = createPanel();
        JLabel usernameLabel = createLabel("username *");
        userNameCont.add(usernameLabel);

        usernameVal = createTextField();
        userNameCont.add(usernameVal);
        assignKeyActionToComponent(createProfileAction, usernameVal);
        return userNameCont;
    }

    protected void createBackJLabel() {
        back = new JLabel(backButtonSml,
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
    }

    protected void assignKeyActionToComponent(Action action, JComponent field) {
        field.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "CREATE_PROFILE");
        field.getActionMap().put("CREATE_PROFILE", action);
    }
}
