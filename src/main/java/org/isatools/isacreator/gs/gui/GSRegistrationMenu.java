package org.isatools.isacreator.gs.gui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.MenuUIComponent;
import org.isatools.isacreator.gui.menu.UserCreationMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

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

    }

    private void register(){

    }
}
