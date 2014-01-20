package org.isatools.isacreator.orcid.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 24/05/2013
 * Time: 10:49
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidContactSelectionCancelledEvent implements PropertyChangeListener {

    private OrcidLookupUI orcidLookupUI = null;

    public OrcidContactSelectionCancelledEvent(OrcidLookupUI orcidLookupUI){
        this.orcidLookupUI = orcidLookupUI;
    }

    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        orcidLookupUI.setVisible(false);
    }
}
