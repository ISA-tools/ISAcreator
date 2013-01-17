package org.isatools.isacreator.gs.gui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.managers.ApplicationManager;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 09/01/2013
 *         Time: 13:35
 */
public class GSSavingWindow extends JWindow {

    public static final ImageIcon SAVING_IMAGE = new ImageIcon(GSSavingWindow.class.getResource("/images/gs/saving_to_gs.gif"));

    private JLabel additionalInformation;

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setSize(new Dimension(250, 100));
        ((JComponent)getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));

        add(new JLabel(SAVING_IMAGE), BorderLayout.CENTER);

        additionalInformation = UIHelper.createLabel("Uploading", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, JLabel.CENTER);

        add(additionalInformation, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(ApplicationManager.getCurrentApplicationInstance());
        setVisible(true);
    }

    public void fireUpdateToStatus(String message) {
        additionalInformation.setText(message);
    }

    public static void main(String[] args) {
        new GSSavingWindow().createGUI();
    }
}
