package com.explodingpixels.macwidgets.plaf;

import com.explodingpixels.widgets.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: Nov 19, 2010
 *         Time: 10:54:38 AM
 */
public class ArtworkUtils {
    private ArtworkUtils() {
        // utility class - no constructor needed.
    }

    public static ImageSet getImageSet(URL imageLocation) {
        Image image = new ImageIcon(imageLocation).getImage();

        // ensure that the given image is divisible by three along the horizontal axis.
        checkImageDivisibleByThree(image);

        int subImageWidth = image.getWidth(null) / 3;
        int imageHeight = image.getHeight(null);
        Image inactiveImage = ImageUtils.getSubImage(image, 0, 0, subImageWidth, imageHeight);
        Image activeImage = ImageUtils.getSubImage(image, subImageWidth, 0, subImageWidth, imageHeight);
        Image pressedImage = ImageUtils.getSubImage(image, subImageWidth * 2, 0, subImageWidth, imageHeight);

        return new ImageSet(inactiveImage, activeImage, pressedImage);
    }

    private static void checkImageDivisibleByThree(Image image) {
        if (image.getWidth(null) % 3 != 0) {
            throw new IllegalArgumentException(
                    "The given image should contain three sub-images all of the same size.");
        }
    }

    public static class ImageSet {

        private final ImageIcon fInactiveImage;
        private final ImageIcon fActiveImage;
        private final ImageIcon fPressedImage;

        private ImageSet(Image inactiveImage, Image activeImage, Image pressedImage) {
            fInactiveImage = new ImageIcon(inactiveImage);
            fActiveImage = new ImageIcon(activeImage);
            fPressedImage = new ImageIcon(pressedImage);
        }

        public ImageIcon getInactiveImage() {
            return fInactiveImage;
        }

        public ImageIcon getActiveImage() {
            return fActiveImage;
        }

        public ImageIcon getPressedImage() {
            return fPressedImage;
        }
    }
}
