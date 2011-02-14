package org.isatools.isacreator.effects.components;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 14/02/2011
 *         Time: 11:09
 */
public class RoundedJTextField extends JTextField {

    private Color backgroundColor;


    public RoundedJTextField(int columns) {
        this(columns, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
    }

    public RoundedJTextField(int columns, Color backgroundColor) {
        super(columns);
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
