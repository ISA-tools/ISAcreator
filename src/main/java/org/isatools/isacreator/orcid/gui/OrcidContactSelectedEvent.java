package org.isatools.isacreator.orcid.gui;

import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.orcid.model.OrcidAuthor;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 24/05/2013
 * Time: 10:45
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidContactSelectedEvent implements PropertyChangeListener {

    private OrcidLookupUI orcidLookupUI;
    private DropDownComponent dropDownComponent;
    private JTextField orcid, firstname, lastname, email;

    public OrcidContactSelectedEvent(OrcidLookupUI ontologySelectionTool,  JTextField orcid, JTextField fn, JTextField ln, JTextField e) {
        this.orcidLookupUI = ontologySelectionTool;
        //this.dropDownComponent = dropDownComponent;
        this.orcid = orcid;
        firstname = fn;
        lastname = ln;
        email = e;
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        //dropDownComponent.hidePopup(orcidLookupUI);
        orcidLookupUI.setVisible(false);
        OrcidAuthor contact = (OrcidAuthor) propertyChangeEvent.getNewValue();
        System.out.println("property change new value - contact="+contact);
        firstname.setText(contact.getGivenNames());
        lastname.setText(contact.getFamilyName());
        email.setText(contact.getEmail());
        orcid.setText(contact.getOrcid());
    }

}
