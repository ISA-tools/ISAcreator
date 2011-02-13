package org.isatools.isacreator.ontologiser.ui.listrenderer;

import org.isatools.isacreator.ontologiser.model.OntologiserListItems;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 01/02/2011
 *         Time: 11:25
 */
public class CheckedCellImage extends JPanel {
    // this will contain the general panel layout for the list item and modifier elements to allow for changing of images
    // when rendering an item as being selected and so forth.
    private JLabel itemSelectedIndicator;

    @InjectedResource
    private ImageIcon ontologyAssigned, noOntologyAssigned;

    public CheckedCellImage() {
        ResourceInjector.get("ontologiser-generator-package.style").inject(this);

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        itemSelectedIndicator = new JLabel(noOntologyAssigned);

        add(itemSelectedIndicator);
        add(Box.createHorizontalStrut(2));
    }

    public void checkIsIdEntered(OntologiserListItems ontologisedResult) {


        if (ontologisedResult.displayAsChecked()) {
            itemSelectedIndicator.setIcon(ontologyAssigned);
        } else {
            itemSelectedIndicator.setIcon(noOntologyAssigned);
        }
    }
}
