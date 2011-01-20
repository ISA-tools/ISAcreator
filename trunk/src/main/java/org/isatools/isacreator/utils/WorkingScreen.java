package org.isatools.isacreator.utils;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 19/01/2011
 *         Time: 14:16
 */
public class WorkingScreen extends JPanel {

    static {
        ResourceInjector.get("utils-package.style").load(
                WorkingScreen.class.getResource("/dependency-injections/utils-package.properties"));
    }

    @InjectedResource
    private ImageIcon workingScreen;

    public WorkingScreen() {
        ResourceInjector.get("utils-package.style").inject(this);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());
        add(new JPanel().add(new JLabel(workingScreen)), BorderLayout.CENTER);
    }
}
