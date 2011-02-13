package com.explodingpixels.widgets;

import com.explodingpixels.painter.ImagePainter;
import com.explodingpixels.swingx.EPPanel;

import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Nov 19, 2010
 *         Time: 11:15:22 AM
 */
public class ImageBasedJComponent extends EPPanel {

    private final ImagePainter fPainter;

    public ImageBasedJComponent(Image image) {
        fPainter = new ImagePainter(image);
        setBackgroundPainter(fPainter);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(fPainter.getImage().getWidth(null),
                fPainter.getImage().getHeight(null));
    }
}
