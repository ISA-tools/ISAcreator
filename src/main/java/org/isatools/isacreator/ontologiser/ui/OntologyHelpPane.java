package org.isatools.isacreator.ontologiser.ui;

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
 *         Date: 28/01/2011
 *         Time: 14:17
 */
public class OntologyHelpPane extends JPanel {


    @InjectedResource
    private ImageIcon helpImage;

    public OntologyHelpPane() {
        ResourceInjector.get("ontologiser-generator-package.style").inject(this);
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
    }

    public void createGUI() {

        JLabel helpContainer = new JLabel(helpImage);
        add(UIHelper.wrapComponentInPanel(helpContainer));
    }
}
