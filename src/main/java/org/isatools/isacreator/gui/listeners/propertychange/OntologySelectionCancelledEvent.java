package org.isatools.isacreator.gui.listeners.propertychange;

import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/03/2011
 *         Time: 12:41
 */
public class OntologySelectionCancelledEvent implements PropertyChangeListener {
        private OntologySelectionTool ontologySelectionTool;
        private DropDownComponent dropDownComponent;

        public OntologySelectionCancelledEvent(OntologySelectionTool ontologySelectionTool, DropDownComponent dropDownComponent) {
            this.ontologySelectionTool = ontologySelectionTool;
            this.dropDownComponent = dropDownComponent;
        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            dropDownComponent.hidePopup(ontologySelectionTool);
        }
    }
