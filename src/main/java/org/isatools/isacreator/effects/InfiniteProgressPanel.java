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

package org.isatools.isacreator.effects;


/**
 * 18:12 17/02/2005
 * Romain Guy <romain.guy@jext.org>
 * Subject to the BSD license.
 */

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.*;


/**
 * An infinite progress panel displays a rotating figure and
 * a message to notice the user of a long, duration unknown
 * task. The shape and the text are drawn upon a white veil
 * which alpha level (or shield value) lets the underlying
 * component shine through. This panel is meant to be used
 * asa <i>glass pane</i> in the window performing the long
 * operation.
 * <br /><br />
 * On the contrary to regular glass panes, you don't need to
 * set it visible or not by yourself. Once you've started the
 * animation all the mouse events are intercepted by this
 * panel, preventing them from being forwared to the
 * underlying components.
 * <br /><br />
 * The panel can be controlled by the <code>start()</code>,
 * <code>stop()</code> and <code>interrupt()</code> methods.
 * <br /><br />
 * Example:
 * <br /><br />
 * <pre>InfiniteProgressPanel pane = new InfiniteProgressPanel();
 * frame.setGlassPane(pane);
 * pane.start()</pre>
 * <br /><br />
 * Several properties can be configured at creation time. The
 * message and its font can be changed at runtime. Changing the
 * font can be done using <code>setFont()</code> and
 * <code>setForeground()</code>.
 *
 * @author Romain Guy
 * @version 1.0
 */
public class InfiniteProgressPanel extends JComponent implements MouseListener {
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
     * Alpha level of the veil, used for fade in/out.
     */
    protected int alphaLevel = 0;

    /**
     * Duration of the veil's fade in/out.
     */
    protected int rampDelay = 200;

    /**
     * Opacity of the veil.
     */
    protected float shield = 0.85f;

    /**
     * Message displayed below the circular shape.
     */
    protected String text = "";

    /**
     * Amount of bars composing the circular shape.
     */
    protected int barsCount = 10;

    /**
     * Amount of frames per second. Lowers this to save CPU.
     */
    protected float fps = 1.0f;

    /**
     * Rendering hints to set anti aliasing.
     */
    protected RenderingHints hints = null;

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>No message</li>
     * <li>14 bars</li>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     */
    public InfiniteProgressPanel() {
        this("");
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>14 bars</li>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text The message to be displayed. Can be null or empty.
     */
    public InfiniteProgressPanel(String text) {
        this(text, 14);
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>Veil's alpha level is 70%</li>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape
     */
    public InfiniteProgressPanel(String text, int barsCount) {
        this(text, barsCount, 0.70f);
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>15 frames per second</li>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape.
     * @param shield    The alpha level between 0.0 and 1.0 of the colored
     *                  shield (or veil).
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield) {
        this(text, barsCount, shield, 10.0f);
    }

    /**
     * Creates a new progress panel with default values:<br />
     * <ul>
     * <li>Fade in/out last 300 ms</li>
     * </ul>
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape.
     * @param shield    The alpha level between 0.0 and 1.0 of the colored
     *                  shield (or veil).
     * @param fps       The number of frames per second. Lower this value to
     *                  decrease CPU usage.
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield,
                                 float fps) {
        this(text, barsCount, shield, fps, 300);
    }

    /**
     * Creates a new progress panel.
     *
     * @param text      The message to be displayed. Can be null or empty.
     * @param barsCount The amount of bars composing the circular shape.
     * @param shield    The alpha level between 0.0 and 1.0 of the colored
     *                  shield (or veil).
     * @param fps       The number of frames per second. Lower this value to
     *                  decrease CPU usage.
     * @param rampDelay The duration, in milli seconds, of the fade in and
     *                  the fade out of the veil.
     */
    public InfiniteProgressPanel(String text, int barsCount, float shield,
                                 float fps, int rampDelay) {
        this.text = text;
        this.rampDelay = (rampDelay >= 0) ? rampDelay : 0;
        this.shield = (shield >= 0.0f) ? shield : 0.0f;
        this.fps = (fps > 0.0f) ? fps : 15.0f;
        this.barsCount = (barsCount > 0) ? barsCount : 14;

        this.hints = new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        this.hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        this.hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    /**
     * Builds a bar.
     */
    private Area buildPrimitive() {
        Rectangle2D.Double body = new Rectangle2D.Double(6, 0, 30, 12);
        Ellipse2D.Double head = new Ellipse2D.Double(0, 0, 12, 12);
        Ellipse2D.Double tail = new Ellipse2D.Double(30, 0, 12, 12);

        Area tick = new Area(body);
        tick.add(new Area(head));
        tick.add(new Area(tail));

        return tick;
    }

    /**
     * Builds the circular shape and returns the result as an array of
     * <code>Area</code>. Each <code>Area</code> is one of the bars
     * composing the shape.
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
            AffineTransform toBorder = AffineTransform.getTranslateInstance(45.0,
                    -6.0);
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

    /**
     * Returns the current displayed message.
     */
    public String getText() {
        return text;
    }

    /**
     * Interrupts the animation, whatever its state is. You
     * can use it when you need to stop the animation without
     * running the fade out phase.
     * This methods sets the panel invisible at the end.
     */
    public void interrupt() {
        if (animation != null) {
            animation.interrupt();
            animation = null;

            removeMouseListener(this);
            setVisible(false);
        }
    }

    public boolean isStarted() {
        return started;
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void paintComponent(Graphics g) {
        if (started) {
            int width = getWidth();
            int height = getHeight();

            double maxY = 0.0;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(hints);

            g2.setColor(new Color(255, 255, 255, (int) (alphaLevel * shield)));
            g2.fillRect(0, 0, width, height);

            for (int i = 0; i < ticker.length; i++) {
                int channel = 224 - (128 / (i + 1));
                g2.setColor(new Color(channel, channel, channel, alphaLevel));
                g2.fill(ticker[i]);

                Rectangle2D bounds = ticker[i].getBounds2D();

                if (bounds.getMaxY() > maxY) {
                    maxY = bounds.getMaxY();
                }
            }

            if ((text != null) && (text.length() > 0)) {
                FontRenderContext context = g2.getFontRenderContext();
                TextLayout layout = new TextLayout(text, UIHelper.VER_14_BOLD, context);
                Rectangle2D bounds = layout.getBounds();
                g2.setColor(UIHelper.LIGHT_GREY_COLOR);
                g2.setFont(UIHelper.VER_14_BOLD);

                layout.draw(g2, (float) (width - bounds.getWidth()) / 2,
                        (float) (maxY + layout.getLeading() +
                                (2 * layout.getAscent())));
            }
        }

    }

    /**
     * Changes the displayed message at runtime.
     *
     * @param text The message to be displayed. Can be null or empty.
     */
    public void setText(String text) {
        this.text = text;
        repaint();
    }

    /**
     * Starts the waiting animation by fading the veil in, then
     * rotating the shapes. This method handles the visibility
     * of the glass pane.
     */
    public void start() {
        addMouseListener(this);
        setVisible(true);
        ticker = buildTicker();
        animation = new Thread(new Animator(true));
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
            animation = new Thread(new Animator(false));
            animation.start();
        }
    }

    /**
     * Animation thread.
     */
    private class Animator implements Runnable {
        private boolean rampUp = true;

        protected Animator(boolean rampUp) {
            this.rampUp = rampUp;
        }

        public void run() {
            Point2D.Double center = new Point2D.Double((double) getWidth() / 2,
                    (double) getHeight() / 2);
            double fixedIncrement = (2.0 * Math.PI) / ((double) barsCount);
            AffineTransform toCircle = AffineTransform.getRotateInstance(fixedIncrement,
                    center.getX(), center.getY());

            long start = System.currentTimeMillis();

            if (rampDelay == 0) {
                alphaLevel = rampUp ? 255 : 0;
            }

            started = true;

            boolean inRamp = rampUp;

            while (!Thread.interrupted()) {
                if (!inRamp) {
                    for (Area aTicker : ticker) {
                        aTicker.transform(toCircle);
                    }
                }

                repaint();

                if (rampUp) {
                    if (alphaLevel < 255) {
                        alphaLevel = (int) ((255 * (System.currentTimeMillis() -
                                start)) / rampDelay);

                        if (alphaLevel >= 255) {
                            alphaLevel = 255;
                            inRamp = false;
                        }
                    }
                } else if (alphaLevel > 0) {
                    alphaLevel = (int) (255 -
                            ((255 * (System.currentTimeMillis() - start)) / rampDelay));

                    if (alphaLevel <= 0) {
                        alphaLevel = 0;
                        break;
                    }
                }

                try {
                    Thread.sleep(inRamp ? 10 : (int) (1000 / fps));
                } catch (InterruptedException ie) {
                    break;
                }

                Thread.yield();
            }

            if (!rampUp) {
                started = false;
                repaint();

                setVisible(false);
                removeMouseListener(InfiniteProgressPanel.this);
            }
        }
    }
}
