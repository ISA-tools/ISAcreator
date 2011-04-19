package org.isatools.isacreator.gui.modeselection;

import com.sun.awt.AWTUtilities;
import org.isatools.isacreator.gui.ISAcreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 11/04/2011
 *         Time: 15:02
 */
public class ModeSelector extends JFrame {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("gui-package.style").load(
                ModeSelector.class.getResource("/dependency-injections/gui-package.properties"));
    }

    private JLabel lightMode;
    private JLabel normalMode;

    private Box optionContainer;
    private JPanel loadingContainer;

    @InjectedResource
    private ImageIcon lightIcon, lightIconOver, normalIcon, normalIconOver, loadingIcon;

    public ModeSelector() {
        ResourceInjector.get("gui-package.style").inject(this);

        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setUndecorated(true);

        AWTUtilities.setWindowOpaque(this, false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (dim.width - 590) / 2;
        int y = (dim.height - 394) / 2;

        this.setLocation(x, y);

        createGUI();
    }

    private void createGUI() {

        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        optionContainer = Box.createHorizontalBox();
        optionContainer.setOpaque(false);

        lightMode = new JLabel(lightIcon);

        lightMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                loadISAcreator(Mode.LIGHT_MODE);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lightMode.setIcon(lightIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lightMode.setIcon(lightIcon);
            }
        });

        normalMode = new JLabel(normalIcon);

        normalMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                loadISAcreator(Mode.NORMAL_MODE);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                normalMode.setIcon(normalIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                normalMode.setIcon(normalIcon);
            }
        });

        optionContainer.add(lightMode);
        optionContainer.add(normalMode);

        container.add(optionContainer);

        loadingContainer = new JPanel(new GridLayout(1,1));
        loadingContainer.setOpaque(false);
        loadingContainer.setVisible(false);
        // create and add loading icon
        JLabel loading = new JLabel(loadingIcon);


        loadingContainer.add(loading);

        container.add(loadingContainer);

        add(container, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    private void loadISAcreator(final Mode mode) {

        optionContainer.setVisible(false);
        loadingContainer.setVisible(true);

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ISAcreator main = new ISAcreator(mode);
                        main.createGUI();
                        dispose();
                    }
                });
            }
        });

        loadISATask.start();
    }


    public static void main(String[] args) {
        new ModeSelector();
    }

}
