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

/**
 *
 * @author Eamonn Maguire
 * @date Jun 10, 2009
 */

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Provides a panel which expands from a small icon with animation.
 */
public class ExpandingPanel extends JPanel {

    public static final int NW = 0;
    public static final int NE = 1;
    public static final int SE = 2;
    public static final int SW = 3;

    private ToggleButton toggle;
    private boolean expanded;
    private JPanel optionsBox;
    private JComponent options;
    private JComponent content;
    private JLabel headerIm;


    public ExpandingPanel(JComponent content, final JComponent options) {
        super(new BorderLayout());
        add(content);
        this.content = content;
        this.options = options;


        optionsBox = new JPanel(new BorderLayout());
        optionsBox.setOpaque(false);
        optionsBox.add(options);

    }

    public void addNotify() {
        super.addNotify();
        JPanel toggleContainer = new JPanel();
        toggleContainer.setLayout(new BoxLayout(toggleContainer, BoxLayout.LINE_AXIS));

        if (headerIm == null) {

            headerIm = UIHelper.createLabel("add extra elements", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, JLabel.LEFT);
            headerIm.setVerticalAlignment(JLabel.TOP);
            toggleContainer.add(headerIm);
            toggleContainer.add(Box.createHorizontalStrut(40));

        }


        if (toggle == null) {

            toggle = new ToggleButton();
            toggle.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setExpanded(!expanded);
                }
            });
            toggle.setSize(toggle.getPreferredSize());

            toggle.setVerticalAlignment(JButton.CENTER);
            toggle.setToolTipText("<html>to <strong>show or hide</strong> the options panel, click here</html>");
            toggleContainer.add(toggle);
        }

        content.add(toggleContainer);
    }

    private Insets targetInsets(boolean expanded) {
        if (expanded) {
            return new Insets(0, 0, 0, 0);
        }
        Dimension full = options.getPreferredSize();
        full.width = getWidth();
        full.height = Math.min(getHeight(), optionsBox.getHeight());
        return new Insets(full.height, full.width, 0, 0);
    }

    private Dimension targetSize(boolean expanded) {
        if (expanded) {
            int h = Math.min(getHeight(), options.getPreferredSize().height);
            return new Dimension(getWidth(), h);
        }
        return new Dimension(0, 0);
    }

    public void setExpanded(final boolean exp) {
        if ((expanded && !exp) || (!expanded && exp)) {
            toggle.setEnabled(false);
            if (exp) {
                optionsBox.setBorder(new EmptyBorder(targetInsets(false)));
                optionsBox.setPreferredSize(toggle.getSize());
                add(optionsBox, BorderLayout.PAGE_END);
                revalidate();
            }
            final int INTERVAL = 50;
            final int FRACTION = 2;
            Timer timer = new Timer(INTERVAL, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Insets insets = optionsBox.getInsets();
                    Insets delta = targetInsets(exp);
                    Dimension targetSize = targetSize(exp);
                    int dx = (delta.left - insets.left) / FRACTION;
                    if (dx != 0) {
                        insets.left += dx;
                        optionsBox.setBorder(new EmptyBorder(insets));
                        optionsBox.revalidate();
                        repaint();
                    }
                    int dy = (targetSize.height - optionsBox.getHeight()) / FRACTION;
                    if (dy != 0) {
                        Dimension size = optionsBox.getSize();
                        size.height += dy;
                        optionsBox.setPreferredSize(size);
                        optionsBox.revalidate();
                        revalidate();
                        repaint();
                    }
                    if (Math.abs(dx) <= 1 && Math.abs(dy) <= 1) {
                        ((Timer) e.getSource()).stop();
                        toggle.setEnabled(true);
                        if (!exp) {
                            remove(optionsBox);
                        }
                        revalidate();
                        repaint();
                    }
                }
            });
            timer.setRepeats(true);
            timer.start();
            expanded = exp;
            firePropertyChange("expanded", !exp, exp);
        }
    }

    private static class ArrowIcon implements Icon {
        private static int SIZE = 16;
        private int dir;

        public ArrowIcon(int direction) {
            this.dir = direction;
        }

        public int getIconHeight() {
            return SIZE;
        }

        public int getIconWidth() {
            return SIZE;
        }

        public void paintIcon(Component c, Graphics graphics, int x, int y) {
            int w = getIconWidth();
            int h = getIconWidth();

            graphics.setColor(UIHelper.GREY_COLOR);
            graphics.drawRoundRect(0, 0, w - 1, h - 1, 2, 2);

            int m = 2;
            w -= m * 2;
            h -= m * 2;
            Graphics2D g = (Graphics2D) graphics.create(x + m, y + m, w, h);

            g.setColor(UIHelper.LIGHT_GREEN_COLOR);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            int sw = 2;
            g.setStroke(new BasicStroke(sw));
            switch (dir) {
                case SE:
                    break;
                case SW:
                    g.translate(w, 0);
                    g.scale(-1, 1);
                    break;
                case NW:
                    g.translate(w, h);
                    g.scale(-1, -1);
                    break;
                case NE:
                    g.translate(0, h);
                    g.scale(1, -1);
                    break;
            }
            g.drawLine(0, 0, w - 1 - sw / 2, h - 1 - sw / 2);
            g.drawLine(w - 1 - sw / 2, h / 3, w - 1 - sw / 2, h - 1 - sw / 2);
            g.drawLine(w / 3, h - 1 - sw / 2, w - 1 - sw / 2, h - 1 - sw / 2);

            g.dispose();
        }
    }

    private static final Icon open = new ArrowIcon(NW);
    private static final Icon close = new ArrowIcon(SE);

    private class ToggleButton extends JButton {
        public ToggleButton() {
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    setIcon(expanded ? close : open);
                }
            });
            setIcon(expanded ? close : open);
            setOpaque(true);
            setBackground(ExpandingPanel.this.getBackground());
        }

        public Dimension getPreferredSize() {
            return new Dimension(open.getIconWidth(), open.getIconHeight());
        }

        public void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            if (!isEnabled()) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
            }
            if (getModel().isPressed()) {
                g.setColor(Color.gray.brighter());
            } else {
                g.setColor(getBackground());
            }
            g.fillRect(0, 0, getWidth(), getHeight());
            getIcon().paintIcon(this, g, 0, 0);
            g.dispose();
        }
    }
}