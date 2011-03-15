package org.isatools.isacreator.gui.listeners.propertychange;

import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;

import javax.swing.text.JTextComponent;
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
public class OntologySelectedEvent implements PropertyChangeListener {
    private OntologySelectionTool ontologySelectionTool;
    private DropDownComponent dropDownComponent;
    private JTextComponent field;

    public OntologySelectedEvent(OntologySelectionTool ontologySelectionTool, DropDownComponent dropDownComponent, JTextComponent field) {
        this.ontologySelectionTool = ontologySelectionTool;
        this.dropDownComponent = dropDownComponent;
        this.field = field;
    }


    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        dropDownComponent.hidePopup(ontologySelectionTool);
        field.setText(propertyChangeEvent.getNewValue().toString());
    }
}
