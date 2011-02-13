package com.explodingpixels.swingx;


import com.explodingpixels.painter.Painter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Nov 19, 2010
 *         Time: 11:03:14 AM
 */
public class EPPanel extends JPanel {
    private Painter<Component> fBackgroundPainter;

    public EPPanel() {
        super();
        init();
    }

    private void init() {
        setOpaque(false);
    }

    public void setBackgroundPainter(Painter<Component> painter) {
        fBackgroundPainter = painter;
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (fBackgroundPainter != null) {
            Graphics2D graphics2D = (Graphics2D) g.create();
            fBackgroundPainter.paint(graphics2D, this, getWidth(), getHeight());
            graphics2D.dispose();
        }

        // TODO see if we can get rid of this call to super.paintComponent.
        super.paintComponent(g);
    }
}
