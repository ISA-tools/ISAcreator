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

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;


/**
 * AnimatingSheet
 * The component to be slid in and out.
 * Majority of code minus a few small changes from Marinacci, J. & Adamson, C.
 * Swing Hacks, O'Reilly 2005.
 *
 * @author Marinacci, J, Adamson, C.
 */
public class AnimatingSheet extends JComponent {
    BufferedImage offscreenImage;
    Dimension animatingSize = new Dimension(0, 1);
    JComponent source;

    public AnimatingSheet() {
        super();
        setOpaque(true);
    }

    public Dimension getMaximumSize() {
        return animatingSize;
    }

    public Dimension getMinimumSize() {
        return animatingSize;
    }

    public Dimension getPreferredSize() {
        return animatingSize;
    }

    private void makeOffscreenImage(JComponent source) {
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();
        source.revalidate();

        int width = source.getWidth();

        int height = source.getHeight();
        width = (width == 0) ? 1 : width;
        height = (height == 0) ? 1 : height;

        offscreenImage = gfxConfig.createCompatibleImage(width, height);

        Graphics2D offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();

        offscreenGraphics.setColor(source.getBackground());
        offscreenGraphics.fillRect(0, 0, source.getWidth(), height);
        try {
            source.paint(offscreenGraphics);
        } catch (Exception e) {
            System.out.println("problem occurred when making off-screen graphic.");
        }
    }

    public void paint(Graphics g) {
        // get the bottom-most n pixels of source and
        // paint them into g, where n is height
        try {
            BufferedImage fragment = offscreenImage.getSubimage(0,
                    offscreenImage.getHeight() - animatingSize.height,
                    source.getWidth(), animatingSize.height);

            g.drawImage(fragment, 0, 0, this);
        } catch (RasterFormatException rfe) {
            // ignore, since this error is down to contents of pane changing due to addition of extra components etc.
        }
    }

    public void setAnimatingHeight(int height) {
        animatingSize.height = height;
        setSize(animatingSize);
    }

    public void setSource(JComponent source) {
        this.source = source;
        animatingSize.width = source.getWidth();

        makeOffscreenImage(source);
    }

    public void update(Graphics g) {
        paint(g);
    }
}
