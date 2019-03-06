package org.isatools.isacreator.gui.modeselection;
/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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

//import com.sun.awt.AWTUtilities;
import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.gui.ISAcreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class ModeSelector extends JFrame implements BundleActivator {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");


    }

    private JLabel lightMode;
    private JLabel normalMode;
    private JLabel gsMode;
    private Box optionContainer;
    private JPanel loadingContainer;

    @InjectedResource
    private ImageIcon lightIcon, lightIconOver, normalIcon, normalIconOver, loadingIcon, gsIcon, gsIconOver;

    /**
     * Constructor
     */
    public ModeSelector() {
        ResourceInjector.get("gui-package.style").inject(this);
    }

    /**
     * Creates GUI for mode selection
     *
     * @param context
     */
    private void createGUI(final BundleContext context) {
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setUndecorated(true);

//        AWTUtilities.setWindowOpaque(this, false);
        this.setOpacity(0);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (dim.width - 590) / 2;
        int y = (dim.height - 394) / 2;

        this.setLocation(x, y);

        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        optionContainer = Box.createHorizontalBox();
        optionContainer.setOpaque(false);

        lightMode = new JLabel(lightIcon);

        lightMode.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                loadISAcreator(Mode.LIGHT_MODE, context);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                lightMode.setIcon(lightIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                lightMode.setIcon(lightIcon);
            }
        });

        normalMode = new JLabel(normalIcon);

        normalMode.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                loadISAcreator(Mode.NORMAL_MODE, context);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                normalMode.setIcon(normalIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                normalMode.setIcon(normalIcon);
            }
        });


        gsMode = new JLabel(gsIcon);

        gsMode.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                loadISAcreator(Mode.GS, context);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                gsMode.setIcon(gsIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
                gsMode.setIcon(gsIcon);
            }
        });


        optionContainer.add(lightMode);
        optionContainer.add(normalMode);
        optionContainer.add(gsMode);

        container.add(optionContainer);

        loadingContainer = new JPanel(new GridLayout(1, 1));
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

    /**
     * Loads the ISAcreator main GUI.
     *
     * @param mode
     * @param context
     */
    private void loadISAcreator(final Mode mode, final BundleContext context) {

        optionContainer.setVisible(false);
        loadingContainer.setVisible(true);

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ISAcreator main = new ISAcreator(mode, context);
                        main.createGUI();

                        dispose();
                    }
                });
            }
        });

        loadISATask.start();
    }

    /**
     * Displays the applications window and starts service tracking;
     * everything is done on the Swing event thread to avoid synchronization
     * and repainting issues.
     *
     * @param context The context of the bundle.
     */
    public void start(final BundleContext context) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (GraphicsUtils.isWindowTransparencySupported()) {
                    System.out.println("Creating GUI");
                    createGUI(context);
                } else {
                    ISAcreator main = new ISAcreator(Mode.NORMAL_MODE, context);
                    main.createGUI();
                    dispose();
                }

            }
        });
    }

    /**
     * Stops service tracking and disposes of the application window.
     *
     * @param context The context of the bundle.
     */
    public void stop(BundleContext context) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ModeSelector.this.dispose();
            }
        });
    }


}
