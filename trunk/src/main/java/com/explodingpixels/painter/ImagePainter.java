package com.explodingpixels.painter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ImagePainter implements Painter<Component> {

    private final Image fImage;

    public ImagePainter(Image image) {
        fImage = image;
    }

    public ImagePainter(URL url) {
        try {
            fImage = ImageIO.read(url);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem reading image file.");
        }

    }

    public void paint(Graphics2D graphics, Component objectToPaint, int width, int height) {
        graphics.drawImage(fImage, 0, 0, width, height, null);
    }

    public Image getImage() {
        return fImage;
    }
}
