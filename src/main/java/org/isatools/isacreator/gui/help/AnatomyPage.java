package org.isatools.isacreator.gui.help;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 12/05/2011
 *         Time: 14:40
 */
public class AnatomyPage extends JPanel {

    public static final int ISATAB = 0;
    public static final int ISACREATOR= 1;

    private JLabel imageContainer;

    @InjectedResource
    private ImageIcon isatabAnatomy, isacreatorAnatomy;

    public AnatomyPage() {
        ResourceInjector.get("gui-package.style").inject(this);
        setLayout(new GridLayout(1,1));
        createGUI();
    }

    private void createGUI() {
        imageContainer = new JLabel();
        add(imageContainer);
    }

    public void setView(int toShow) {
        switch(toShow) {
            case ISATAB:
                imageContainer.setIcon(isatabAnatomy);
                break;
            case ISACREATOR:
                imageContainer.setIcon(isacreatorAnatomy);
                break;
            default:
                imageContainer.setIcon(isatabAnatomy);
        }

    }


}
