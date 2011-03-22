/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
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

package org.isatools.isacreator.effects;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Eamonn Maguire
 * @date May 5, 2009
 */


public class HUDTitleBar extends JComponent {
    private JButton closeButton;

    private int preferredHeight = 30;
    private Image backgroundGradient = new ImageIcon(getClass().getResource("/images/visualization/title-background.png")).getImage();
    private Image backgroundGradientInactive = new ImageIcon(getClass().getResource("/images/visualization/title-background-inactive.png")).getImage();
    private Image grip;
    private Image inactiveGrip;
    private Image close = new ImageIcon(getClass().getResource("/images/visualization/title-close.png")).getImage();
    private Image closeInactive = new ImageIcon(getClass().getResource("/images/visualization/title-close-inactive.png")).getImage();
    private Image closeOver = new ImageIcon(getClass().getResource("/images/visualization/title-close-over.png")).getImage();
    private Image closePressed = new ImageIcon(getClass().getResource("/images/visualization/title-close-pressed.png")).getImage();
    private boolean dispose;

    public HUDTitleBar(Image activeImage, Image inactiveImage) {
        this(activeImage, inactiveImage, true);
    }

    public HUDTitleBar(Image activeImage, Image inactiveImage, boolean dispose) {
        this.dispose = dispose;
        setLayout(new GridBagLayout());
        grip = activeImage;
        inactiveGrip = inactiveImage;
        createButtons();
        setBackground(UIHelper.BG_COLOR);
    }

    public void installListeners() {
        MouseInputAdapter handler = new DraggablePaneMouseInputHandler(this);
        Window window = SwingUtilities.getWindowAncestor(this);
        window.addMouseListener(handler);
        window.addMouseMotionListener(handler);

        window.addWindowListener(new WindowHandler());
    }

    private void createButtons() {
        add(Box.createHorizontalGlue(),
                new GridBagConstraints(0, 0,
                        1, 1,
                        1.0, 1.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));

        add(closeButton = createButton(new CloseAction(),
                close, closePressed, closeOver),
                new GridBagConstraints(2, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));
    }

    private JButton createButton(final AbstractAction action,
                                 final Image image,
                                 final Image pressedImage,
                                 final Image overImage) {
        JButton button = new JButton(action);
        button.setIcon(new ImageIcon(image));
        button.setPressedIcon(new ImageIcon(pressedImage));
        button.setRolloverIcon(new ImageIcon(overImage));
        button.setRolloverEnabled(true);
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(image.getWidth(null),
                image.getHeight(null)));
        return button;
    }

    private void close() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (dispose) {
            w.dispatchEvent(new WindowEvent(w,
                    WindowEvent.WINDOW_CLOSING));
            w.dispose();
        } else {
            w.setVisible(false);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    public Dimension getMinimumSize() {
        Dimension size = super.getMinimumSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (!isVisible()) {
            return;
        }

        boolean active = SwingUtilities.getWindowAncestor(this).isActive();

        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);

        Rectangle clip = g2.getClipBounds();

        g2.drawImage(active ? backgroundGradient : backgroundGradientInactive,
                clip.x, 0, clip.width, getHeight(), null);

        g2.drawImage(active ? grip : inactiveGrip, 0, 0, null);
    }

    private class CloseAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            close();
        }
    }


    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent ev) {
            closeButton.setIcon(new ImageIcon(close));
            getRootPane().repaint();
        }

        @Override
        public void windowDeactivated(WindowEvent ev) {
            closeButton.setIcon(new ImageIcon(closeInactive));
            getRootPane().repaint();
        }
    }
}
