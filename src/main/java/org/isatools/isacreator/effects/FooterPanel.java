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
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * FooterPanel
 *
 * @author Eamonn Maguire
 * @date Dec 8, 2009
 */


public class FooterPanel extends JComponent {
    private JFrame container;

    // Size of area on screen which will instigate display of the resize cursor
    private static Rectangle mouseTargetArea = new Rectangle(22, 21);
    private Point point = new Point();
    private boolean resizing = false;
    private ImageIcon resizeIcon;

    public FooterPanel(JFrame container) {
        this(container, UIHelper.BG_COLOR, new ImageIcon(FooterPanel.class.getResource("/images/effects/resize_active.png")));
    }

    public FooterPanel(final JFrame container, Color bgColor, ImageIcon resizeIcon) {
        this.container = container;
        setLayout(new GridBagLayout());
        setBackground(bgColor);
        this.resizeIcon = resizeIcon;
        instantiateComponent();
        installListeners();

        getContainer().getContentPane().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                resizing = getContainer().getCursor().equals(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                if (!e.isMetaDown()) {
                    point.x = e.getX();
                    point.y = e.getY();
                }
            }
        });
    }

    private void installListeners() {

        getContainer().getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                if (resizing) {
                    getContainer().setSize(getContainer().getWidth() + e.getX() - point.x,
                            getContainer().getHeight() + e.getY() - point.y);
                    point.x = e.getX();
                    point.y = e.getY();
                } else if (!e.isMetaDown()) {
                    Point p = getContainer().getLocation();
                    getContainer().setLocation(p.x + e.getX() - point.x,
                            p.y + e.getY() - point.y);
                }
            }

            public void mouseMoved(MouseEvent me) {

                Point cursorLocation = MouseInfo.getPointerInfo().getLocation();
                if (checkIfMouseIsInBounds(cursorLocation)) {
                    getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                } else {
                    getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        getContainer().getContentPane().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                getContainer().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }

    public JFrame getContainer() {
        return container;
    }

    /**
     * Method checks whether or not the mouse is in a location to instigate a resize operation,
     * whereby the application will display a resize cursor to tell the user that they can resize.
     *
     * @param mouseLocation - Point object indicating the mouse location on the screen.
     * @return false if not in a valid location, true otherwise.
     */
    private boolean checkIfMouseIsInBounds(Point mouseLocation) {
        try {
            Point containerLocation = getContainer().getLocationOnScreen();
            Dimension containerSize = getContainer().getSize();
            Rectangle bounds = new Rectangle((containerLocation.x + containerSize.width) - mouseTargetArea.width, (containerLocation.y + containerSize.height) - mouseTargetArea.height,
                    mouseTargetArea.width, mouseTargetArea.height);
            return bounds.contains(mouseLocation);
        } catch (Exception e) {
            // an illegal state not supported exception can be thrown here whenever the location on
            // screen cannot be calculated. This appears to only happen on Linux boxes.
            return false;
        }
    }

    private void instantiateComponent() {
        add(Box.createHorizontalGlue(),
                new GridBagConstraints(0, 0,
                        1, 1,
                        1.0, 1.0,
                        GridBagConstraints.EAST,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 0),
                        0, 0));

        JLabel resizeButton = new JLabel(resizeIcon);

        add(resizeButton, new GridBagConstraints(2, 0,
                1, 1,
                0.0, 1.0,
                GridBagConstraints.NORTHEAST,
                GridBagConstraints.NONE,
                new Insets(1, 0, 0, 2),
                0, 0));
    }

    public void cleanupReferences() {
        this.container = null;
        removeAll();
    }
}
