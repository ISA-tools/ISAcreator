package org.isatools.isacreator.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 13/06/2012
 *         Time: 23:04
 */
public class CommonMouseAdapter implements MouseListener {
    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof JComponent) {
            ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public void mouseExited(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof JComponent) {
            ((JComponent) mouseEvent.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
