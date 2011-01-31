package org.isatools.isacreator.ontologiser.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.ontologiser.model.OntologisedResult;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

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

    @InjectedResource
    private ImageIcon ontologyAssigned, noOntologyAssigned;

    private DefaultListCellRenderer listCellRenderer;

    public OntologyAssignedListRenderer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        ResourceInjector.get("ontologiser-package.style").inject(this);

        add(new OntologyAssignedCellImage(), BorderLayout.WEST);

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
            if (c instanceof OntologyAssignedCellImage) {
                ((OntologyAssignedCellImage) c).checkIsIdEntered(result);
            } else {
                if (selected) {
                    c.setForeground(UIHelper.GREY_COLOR);
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

    class OntologyAssignedCellImage extends JPanel {
        // this will contain the general panel layout for the list item and modifier elements to allow for changing of images
        // when rendering an item as being selected and so forth.
        private JLabel itemSelectedIndicator;

        OntologyAssignedCellImage() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            itemSelectedIndicator = new JLabel(noOntologyAssigned);

            add(itemSelectedIndicator);
            add(Box.createHorizontalStrut(2));
        }

        public void checkIsIdEntered(OntologisedResult ontologisedResult) {

            boolean valueEntered = ontologisedResult.getAssignedOntology() != null;

            if (valueEntered) {
                itemSelectedIndicator.setIcon(ontologyAssigned);
            } else {
                itemSelectedIndicator.setIcon(noOntologyAssigned);
            }
        }
    }
}
