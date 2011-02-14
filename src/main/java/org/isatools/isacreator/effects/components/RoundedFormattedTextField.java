package org.isatools.isacreator.effects.components;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 14/02/2011
 *         Time: 12:09
 */
public class RoundedFormattedTextField extends JFormattedTextField {

    private Color backgroundColor;


    public RoundedFormattedTextField(NumberFormat format) {
        this(format, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
    }

    public RoundedFormattedTextField(NumberFormat format, Color backgroundColor) {
        super(format);
        this.backgroundColor = backgroundColor;

        setOpaque(false);

        setBorder(new EmptyBorder(3, 5, 3, 5));

    }

    @Override
    protected void paintComponent(Graphics graphics) {
        int width = getWidth() - getInsets().left - getInsets().right;
        int height = getHeight() - getInsets().top - getInsets().bottom;

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(backgroundColor);

        g2d.fillRoundRect(getInsets().left, getInsets().top, width, height, 8, 8);

        super.paintComponent(graphics);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
