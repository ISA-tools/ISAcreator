/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * DraggablePaneMouseInputHandler
 *
 * @author eamonnmaguire
 * @date Oct 13, 2010
 */


public class DraggablePaneMouseInputHandler extends MouseInputAdapter {
    private boolean isMovingWindow;
    private int dragOffsetX;
    private int dragOffsetY;

    private static final int BORDER_DRAG_THICKNESS = 5;
    private Component container;

    public DraggablePaneMouseInputHandler(Component container) {
        this.container = container;
    }

    public void mousePressed(MouseEvent ev) {
        Point dragWindowOffset = ev.getPoint();
        Window w = (Window) ev.getSource();
        if (w != null) {
            w.toFront();
        }
        Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                w, dragWindowOffset, container);

        Frame f = null;
        Dialog d = null;

        if (w instanceof Frame) {
            f = (Frame) w;
        } else if (w instanceof Dialog) {
            d = (Dialog) w;
        }

        int frameState = (f != null) ? f.getExtendedState() : 0;

        if (container.contains(convertedDragWindowOffset)) {
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
