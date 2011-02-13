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

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class SmallLoader extends JComponent {

    /**
     * Contains the bars composing the circular shape.
     */
    protected Area[] ticker = null;

    /**
     * The animation thread is responsible for fade in/out and rotation.
     */
    protected Thread animation = null;

    /**
     * Notifies whether the animation is running or not.
     */
    protected boolean started = false;

    /**
     * Amount of bars composing the circular shape.
     */
    protected int barsCount;

    /**
     * Amount of frames per seconde. Lowers this to save CPU.
     */
    protected float fps;

    protected String text;

    /**
     * Rendering hints to set anti aliasing.
     */
    protected RenderingHints hints = null;

    public SmallLoader(int barsCount,
                       float fps, int width, int height, String text) {
        setPreferredSize(new Dimension(width, height + 20));
        this.fps = (fps > 0.0f) ? fps : 15.0f;
        this.barsCount = (barsCount > 0) ? barsCount : 6;

        this.text = text;

        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        this.hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        setOpaque(false);
    }

    /**
     * Builds a bar.
     *
     * @return Area
     */
    private Area buildPrimitive() {
        Rectangle2D.Double body = new Rectangle2D.Double(4, 0, 5, 4);

        return new Area(body);
    }

    /**
     * Builds the circular shape and returns the result as an array of
     * <code>Area</code>. Each <code>Area</code> is one of the bars
     * composing the shape.
     *
     * @return Array of areas
     */
    private Area[] buildTicker() {
        Area[] ticker = new Area[barsCount];
        Point2D.Double center = new Point2D.Double((double) getWidth() / 2,
                (double) getHeight() / 2);
        double fixedAngle = (2.0 * Math.PI) / ((double) barsCount);

        for (double i = 0.0; i < (double) barsCount; i++) {
            Area primitive = buildPrimitive();
            AffineTransform toCenter = AffineTransform.getTranslateInstance(center.getX(),
                    center.getY());
            AffineTransform toBorder = AffineTransform.getTranslateInstance(2.0,
                    -2.0);
            AffineTransform toCircle = AffineTransform.getRotateInstance(-i * fixedAngle,
                    center.getX(), center.getY());

            AffineTransform toWheel = new AffineTransform();
            toWheel.concatenate(toCenter);
            toWheel.concatenate(toBorder);

            primitive.transform(toWheel);
            primitive.transform(toCircle);

            ticker[(int) i] = primitive;
        }

        return ticker;
    }


    public void paintComponent(Graphics g) {
        if (started) {

            double maxY = 0.0;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(hints);

            for (int i = 0; i < ticker.length; i++) {
                int channel = 224 - (128 / (i + 1));
                g2.setColor(new Color(channel, channel, channel, 100));
                g2.fill(ticker[i]);


                try {
                    Rectangle2D bounds = ticker[i].getBounds2D();

                    if (bounds.getMaxY() > maxY) {
                        maxY = bounds.getMaxY();
                    }
                } catch (ArrayIndexOutOfBoundsException ae) {
                    //
                }
            }

            g2.setColor(UIHelper.GREY_COLOR);
            g2.setFont(UIHelper.VER_10_BOLD);

            FontMetrics fm = g2.getFontMetrics(g2.getFont());
            int stringSize = fm.stringWidth(text);

            g2.drawString(text, getWidth() / 2 - stringSize / 2, getHeight() - 15);

        }
    }

    public void start() {
        setVisible(true);
        ticker = buildTicker();
        if (animation != null) {
            animation.interrupt();
            animation = null;
        }
        animation = new Thread(new Animator());
        animation.start();

    }

    /**
     * Stops the waiting animation by stopping the rotation
     * of the circular shape and then by fading out the veil.
     * This methods sets the panel invisible at the end.
     */
    public void stop() {
        if (animation != null) {
            animation.interrupt();
            animation = null;
            setVisible(false);
        }
    }

    /**
     * Animation thread.
     */
    private class Animator implements Runnable {

        public void run() {
            Point2D.Double center = new Point2D.Double((double) getWidth() / 2,
                    (double) getHeight() / 2);
            double fixedIncrement = (2.0 * Math.PI) / ((double) barsCount);
            AffineTransform toCircle = AffineTransform.getRotateInstance(fixedIncrement,
                    center.getX(), center.getY());

            started = true;


            while (!Thread.interrupted()) {

                for (Area aTicker : ticker) {
                    aTicker.transform(toCircle);
                }
                repaint();

                try {
                    Thread.sleep((int) (1000 / fps));
                } catch (InterruptedException ie) {
                    break;
                }

                Thread.yield();
            }

        }
    }

    public static void main(String[] args) {
        final JFrame testframe = new JFrame("test");
        testframe.setPreferredSize(new Dimension(100, 100));

        final SmallLoader lic = new SmallLoader(10, 10.0f, 30, 30, "hello");
        testframe.add(lic);


        JButton start = new JButton("start");
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lic.start();
                testframe.validate();
            }
        });

        testframe.add(start, BorderLayout.NORTH);

        JButton stop = new JButton("stop");
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lic.stop();

            }
        });

        testframe.add(stop, BorderLayout.SOUTH);


        testframe.pack();
        testframe.setVisible(true);

    }
}
