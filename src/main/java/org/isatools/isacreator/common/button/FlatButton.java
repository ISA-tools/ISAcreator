package org.isatools.isacreator.common.button;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created with IntelliJ IDEA.
 * User: eamonnmaguire
 * Date: 07/09/2013
 * Time: 14:00
 * To change this template use File | Settings | File Templates.
 */
public class FlatButton extends JButton implements MouseListener {

    private ButtonType type;
    private Color fontColor;
    private Font font;

    public FlatButton(ButtonType type, String text) {
        this(type, text, Color.white);

    }

    public FlatButton(ButtonType type, String text, Color fontColor) {
        this(type, text, fontColor, UIHelper.VER_12_BOLD);
    }

    public FlatButton(ButtonType type, String text, Color fontColor, Font font) {
        super(text);
        this.type = type;
        this.fontColor = fontColor;
        this.font = font;
        setForeground(Color.WHITE);
        addMouseListener(this);
    }

    @Override
    public int getHeight() {
        return super.getHeight()-4;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics.create();
        if (!isEnabled()) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .25f));
        }
        if (getModel().isPressed() || getModel().isRollover()) {
            g.setColor(type.getHoverColor());
        } else {
            g.setColor(type.getDefaultColor());
        }


        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(fontColor);
        g.setFont(font);
        FontMetrics fontMetrics = g.getFontMetrics(font);

        int width = fontMetrics.stringWidth(getText());
        int adjustment = (getWidth() - width) / 2;
        g.drawString(getText(), adjustment, getHeight() - 9);
    }

    @Override
    protected void paintBorder(Graphics graphics) {
        // do nothing. No border thank you.
    }

    public static void main(String[] args) {

        JFrame testFrame = new JFrame("Test FlatButton");

        testFrame.setSize(new Dimension(400, 400));

        testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        testFrame.setLayout(new BorderLayout());

        JPanel container = new JPanel();

        container.add(new FlatButton(ButtonType.RED, "Hi Lauren!"));
        container.add(new FlatButton(ButtonType.BLUE, "Hi Annapaola!"));
        container.add(new FlatButton(ButtonType.ORANGE, "Hi Paul!"));


        testFrame.add(container);
        testFrame.setLocationRelativeTo(null);
        testFrame.pack();
        testFrame.setVisible(true);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        //
    }

    public void mousePressed(MouseEvent mouseEvent) {
        // To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void mouseExited(MouseEvent mouseEvent) {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}


