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

package org.isatools.isacreator.filechooser;

import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * File Image class creates an image for a file object showing a standard file, plus the extension so that the users
 * can see what type a file is without even having to look at the extension.
 *
 * @author Eamonn Maguire
 */
public class FileImage extends JPanel {
    private static final Logger log = Logger.getLogger(FileImage.class.getName());

    public static final String FILE_IMG_DIR = System.getProperty("java.io.tmpdir") + "/images/filechooser/filechooser_icons";

    private static final int WIDTH = 30;
    private static final int HEIGHT = 20;
    private String extension;

    /**
     * File Image class accepts a String called extension as it's only parameter.
     * The extension string is used for painting to the image and to uniquely identify the image
     * for use in the program.
     *
     * @param extension - e.g. PNG to be drawn onto the icon.
     */
    public FileImage(String extension) {
        this.extension = extension;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }

    /**
     * Method should be called to create the File image
     *
     * @throws IOException - If image has not been written, an IOException will be thrown. This exception should be dealt
     *                     with by the calling instance.
     */
    public void createImage() throws IOException {
        saveImage();
    }

    /**
     * Paint the Component, which involves painting the file icon and it's associated text.
     *
     * @param g - Graphics Object
     */
    public void paint(Graphics g) {
        Graphics2D big = (Graphics2D) g;

        int startXPos = 1;
        int startYPos = 1;

        big.setFont(UIHelper.VER_9_BOLD);
        // ANTIALIAS TEXT TO MAKE IT LOOK SMOOTHER!
        big.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        big.setColor(UIHelper.LIGHT_GREEN_COLOR);
        big.fillRect(startXPos, startYPos, 12, 15);

        // draw 4 lines so it looks cool :o)
        big.setColor(new Color(209, 209, 209));

        int yPos = startYPos + 2;
        int noReps = 4;

        for (int i = 0; i < noReps; i++) {
            int xStart = (int) ((Math.random() * 1000) % 7);
            xStart = (xStart < 3) ? (xStart += 2) : xStart;

            int yStart = (int) ((Math.random() * 1000) % 14);
            yStart = (yStart < 10) ? (yStart += 4) : yStart;

            big.drawLine(xStart, yPos, yStart, yPos);
            yPos += 4;
        }

        big.setColor(UIHelper.DARK_GREEN_COLOR);
        big.drawString(extension, startXPos + 1, 16);
    }

    /**
     * Save the image to a pre-defined location
     * todo fall back on a generic default image if writing fails
     */
    private void saveImage() throws IOException {
        int w = WIDTH;
        int h = HEIGHT;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(UIHelper.BG_COLOR);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        paint(g2);
        g2.dispose();

        String ext = "png";

        File f = new File(FILE_IMG_DIR);

        if (!f.exists()) {
            f.mkdirs();
        }


        ImageIO.write(img, ext,
                new File(FILE_IMG_DIR + "/" + extension + "icon." +
                        ext));

    }
}
