package org.isatools.isacreator.gs.gui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 05/11/2012
 * Time: 12:43
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSSaveAsDialog extends JDialog {

    private JLabel status;
    private ISAcreatorMenu menu = null;
    private GSFileChooser fileChooser = null;

    @InjectedResource
    private ImageIcon dialogHeader, closeButton, closeButtonOver,
            saveSubmission, saveSubmissionOver;

    public GSSaveAsDialog(ISAcreatorMenu m) {
        menu = m;
        ResourceInjector.get("gui-package.style").inject(this);
        fileChooser = new GSFileChooser(menu);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        instantiatePanel();
        pack();
    }

    private void instantiatePanel() {
        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        topPanel.setBackground(UIHelper.BG_COLOR);

        JLabel saveAsLabel = new JLabel(dialogHeader,
                JLabel.RIGHT);
        saveAsLabel.setBackground(UIHelper.BG_COLOR);

        topPanel.add(saveAsLabel);

        add(topPanel, BorderLayout.NORTH);

        // setup center panel to contain data entry facility for user.
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setBackground(UIHelper.BG_COLOR);

        JPanel fileNamePanel = new JPanel(new GridLayout(1, 2));
        fileNamePanel.setOpaque(false);

        JLabel fileNameLabel = new JLabel("directory name");
        UIHelper.renderComponent(fileNameLabel, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        final JTextField fileNameTxt = new JTextField(
                "Please enter a directory name...");
        fileNameTxt.setBackground(UIHelper.BG_COLOR);
        UIHelper.renderComponent(fileNameTxt, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        fileNamePanel.add(fileNameLabel);
        fileNamePanel.add(fileNameTxt);

        centerPanel.add(fileNamePanel);

        fileChooser.createDialog();
        centerPanel.add(fileChooser);

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setBackground(UIHelper.BG_COLOR);

        status = new JLabel();
        UIHelper.renderComponent(status, UIHelper.VER_12_BOLD, UIHelper.RED_COLOR, false);

        statusPanel.add(status);

        centerPanel.add(statusPanel);

        add(centerPanel, BorderLayout.CENTER);

        // setup south panel with buttons and so forth :o)
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel close = new JLabel(closeButton,
                JLabel.LEFT);
        close.setOpaque(false);
        close.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("windowClosed", "", "none");
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeButton);
            }
        });

        Action saveAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                save(fileNameTxt.getText().trim());
            }
        };

        fileNameTxt.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SAVE");
        fileNameTxt.getActionMap().put("SAVE", saveAction);

        final JLabel save = new JLabel(saveSubmission,
                JLabel.RIGHT);
        save.setOpaque(false);
        save.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                save(fileNameTxt.getText().trim());
            }

            public void mouseEntered(MouseEvent event) {
                save.setIcon(saveSubmissionOver);
            }

            public void mouseExited(MouseEvent event) {
                save.setIcon(saveSubmission);
            }
        });

        southPanel.add(close, BorderLayout.WEST);
        southPanel.add(save, BorderLayout.EAST);

        add(southPanel, BorderLayout.SOUTH);
    }

    public void save(String fileName) {

    }

}
