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
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * TitlePanel
 *
 * @author Eamonn Maguire
 * @date Feb 3, 2010
 */


public abstract class TitlePanel extends JComponent {

    public static final String CLOSE_EVENT = "close";

    private JButton iconifyButton, maximizeButton, closeButton;

    private int preferredHeight = 26;

    private Image backgroundGradient = new ImageIcon(getClass().getResource("/images/titlepanel/background-active.png")).getImage();
    private Image minimize = new ImageIcon(getClass().getResource("/images/titlepanel/title-minimize.png")).getImage();
    private Image minimizeInactive = new ImageIcon(getClass().getResource("/images/titlepanel/title-minimize-inactive.png")).getImage();
    private Image minimizeOver = new ImageIcon(getClass().getResource("/images/titlepanel/title-minimize-over.png")).getImage();
    private Image minimizePressed = new ImageIcon(getClass().getResource("/images/titlepanel/title-minimize-pressed.png")).getImage();
    private Image maximize = new ImageIcon(getClass().getResource("/images/titlepanel/title-maximize.png")).getImage();
    private Image maximizeInactive = new ImageIcon(getClass().getResource("/images/titlepanel/title-maximize-inactive.png")).getImage();
    private Image maximizeOver = new ImageIcon(getClass().getResource("/images/titlepanel/title-maximize-over.png")).getImage();
    private Image maximizePressed = new ImageIcon(getClass().getResource("/images/titlepanel/title-maximize-pressed.png")).getImage();
    private Image close = new ImageIcon(getClass().getResource("/images/titlepanel/title-close.png")).getImage();
    private Image closeInactive = new ImageIcon(getClass().getResource("/images/titlepanel/title-close-inactive.png")).getImage();
    private Image closeOver = new ImageIcon(getClass().getResource("/images/titlepanel/title-close-over.png")).getImage();
    private Image closePressed = new ImageIcon(getClass().getResource("/images/titlepanel/title-close-pressed.png")).getImage();


    public TitlePanel() {
        ResourceInjector.get("gui-package.style").inject(this);
        setLayout(new GridBagLayout());
        createButtons();
        setBackground(UIHelper.GREY_COLOR);
    }

    public void installListeners() {
        MouseInputHandler handler = new MouseInputHandler();
        Window window = SwingUtilities.getWindowAncestor(this);
        window.addMouseListener(handler);
        window.addMouseMotionListener(handler);
        window.addWindowListener(new WindowHandler());
    }

    private void createButtons() {
        add(Box.createHorizontalGlue(),
                new GridBagConstraints(0, 0,
                        1, 1, 1.0, 1.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));

        add(iconifyButton = createButton(new IconifyAction(),
                minimize, minimizePressed, minimizeOver),
                new GridBagConstraints(1, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));
        iconifyButton.setToolTipText("<html>Minimise <b>ISAcreator</b></html>");

        add(maximizeButton = createButton(new ResizeAction(),
                maximize, maximizePressed, maximizeOver),
                new GridBagConstraints(2, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));

        maximizeButton.setToolTipText("<html>Toggle full/default screen size for <b>ISAcreator</b></html>");

        add(closeButton = createButton(new CloseAction(),
                close, closePressed, closeOver),
                new GridBagConstraints(3, 0,
                        1, 1,
                        0.0, 1.0,
                        GridBagConstraints.NORTHEAST,
                        GridBagConstraints.NONE,
                        new Insets(1, 0, 0, 2),
                        0, 0));

        closeButton.setToolTipText("<html>Close <b>ISAcreator</b></html>");
    }

    private static JButton createButton(final AbstractAction action,
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

    private void iconify() {
        Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
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
        g2.drawImage(backgroundGradient,
                clip.x, 0, clip.width, preferredHeight, null);

        g2.setColor(UIHelper.BG_COLOR);
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        g2.setColor(UIHelper.BG_COLOR);
        g2.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);

        drawGrip(g2, active);
        drawTitle(g2, active);
    }

    protected abstract void drawGrip(Graphics2D g2d, boolean active);

    protected abstract void drawTitle(Graphics2D g2d, boolean active);

    protected abstract void resize();

    private class IconifyAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            iconify();
        }

    }

    private class ResizeAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            resize();
        }
    }

    private class CloseAction extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            TitlePanel.this.firePropertyChange(CLOSE_EVENT, false, true);
        }
    }

    private class MouseInputHandler extends MouseInputAdapter {
        private boolean isMovingWindow;
        private int dragOffsetX;
        private int dragOffsetY;

        private static final int BORDER_DRAG_THICKNESS = 5;

        public void mousePressed(MouseEvent ev) {
            Point dragWindowOffset = ev.getPoint();
            Window w = (Window) ev.getSource();
            if (w != null) {
                w.toFront();
            }
            Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                    w, dragWindowOffset, TitlePanel.this);

            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame) {
                f = (Frame) w;
            } else if (w instanceof Dialog) {
                d = (Dialog) w;
            }

            int frameState = (f != null) ? f.getExtendedState() : 0;

            if (TitlePanel.this.contains(convertedDragWindowOffset)) {
                if ((f != null && ((frameState & Frame.MAXIMIZED_BOTH) == 0)
                        || (d != null))
                        && dragWindowOffset.y >= BORDER_DRAG_THICKNESS
                        && dragWindowOffset.x >= BORDER_DRAG_THICKNESS
                        && dragWindowOffset.x < w.getWidth()
                        - BORDER_DRAG_THICKNESS) {
                    isMovingWindow = true;
                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                }
            } else if (f != null && f.isResizable()
                    && ((frameState & Frame.MAXIMIZED_BOTH) == 0)
                    || (d != null && d.isResizable())) {
                dragOffsetX = dragWindowOffset.x;
                dragOffsetY = dragWindowOffset.y;
            }
        }

        public void mouseReleased(MouseEvent ev) {
            isMovingWindow = false;
        }

        public void mouseDragged(MouseEvent ev) {
            Window w = (Window) ev.getSource();

            if (isMovingWindow) {
                Point windowPt = MouseInfo.getPointerInfo().getLocation();
                windowPt.x = windowPt.x - dragOffsetX;
                windowPt.y = windowPt.y - dragOffsetY;
                w.setLocation(windowPt);
            }
        }
    }

    private class WindowHandler extends WindowAdapter {
        @Override
        public void windowActivated(WindowEvent ev) {
            iconifyButton.setIcon(new ImageIcon(minimize));
            maximizeButton.setIcon(new ImageIcon(maximize));
            getRootPane().repaint();
        }

        @Override
        public void windowDeactivated(WindowEvent ev) {
            iconifyButton.setIcon(new ImageIcon(minimizeInactive));
            maximizeButton.setIcon(new ImageIcon(maximizeInactive));
            getRootPane().repaint();
        }
    }
}