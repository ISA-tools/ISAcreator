package org.isatools.isacreator.ontologiser.ui.listrenderer;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.ontologiser.model.OntologisedResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 31/01/2011
 *         Time: 16:35
 */
public class OntologyAssignedListRenderer extends JComponent
        implements ListCellRenderer {

    private DefaultListCellRenderer listCellRenderer;

    public OntologyAssignedListRenderer() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new CheckedCellImage(), BorderLayout.WEST);

        listCellRenderer = new DefaultListCellRenderer();
        add(listCellRenderer, BorderLayout.CENTER);

        setBorder(new EmptyBorder(2, 2, 2, 2));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {
        listCellRenderer.getListCellRendererComponent(list, value, index,
                selected, cellGotFocus);
        listCellRenderer.setBorder(null);

        //image.checkIsIdEntered(selected);
        Component[] components = getComponents();

        OntologisedResult result = (OntologisedResult) value;

        for (Component c : components) {
            ((JComponent) c).setToolTipText(value.toString());
            if (c instanceof CheckedCellImage) {
                ((CheckedCellImage) c).checkIsIdEntered(result);
            } else {
                if (selected) {
                    c.setForeground(UIHelper.DARK_GREEN_COLOR);
                    c.setBackground(UIHelper.BG_COLOR);
                    c.setFont(UIHelper.VER_11_BOLD);
                } else {
                    c.setForeground(UIHelper.LIGHT_GREEN_COLOR);
                    c.setBackground(UIHelper.BG_COLOR);
                    c.setFont(UIHelper.VER_11_BOLD);
                }
            }
        }


        return this;
    }


}
