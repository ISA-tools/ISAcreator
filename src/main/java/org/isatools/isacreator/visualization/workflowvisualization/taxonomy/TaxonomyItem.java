package org.isatools.isacreator.visualization.workflowvisualization.taxonomy;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/06/2012
 *         Time: 22:28
 */
public class TaxonomyItem extends JPanel {

    private String name;
    private String fileLocation;

    public TaxonomyItem(String name, String fileLocation) {
        this.name = name;
        this.fileLocation = fileLocation;
        createGUI();
    }

    public String getName() {
        return name;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void createGUI() {
        setBackground(new Color(241, 242, 241));
        setPreferredSize(new Dimension(70, 70));
        setBorder(new EmptyBorder(2, 2, 2, 2));

        setLayout(new BorderLayout());

        add(UIHelper.wrapComponentInPanel(new JLabel(new ImageIcon(fileLocation))), BorderLayout.CENTER);
        add(UIHelper.wrapComponentInPanel(UIHelper.createLabel(name, UIHelper.VER_8_PLAIN, UIHelper.GREY_COLOR)),
                BorderLayout.NORTH);
    }
}
