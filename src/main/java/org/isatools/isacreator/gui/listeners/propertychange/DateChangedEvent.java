package org.isatools.isacreator.gui.listeners.propertychange;

import org.isatools.isacreator.calendar.CalendarGUI;
import org.isatools.isacreator.common.DropDownComponent;

import javax.swing.*;
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
public class DateChangedEvent implements PropertyChangeListener {

    private CalendarGUI calendar;
    private DropDownComponent dropDownComponent;
    private JTextField field;

    public DateChangedEvent(CalendarGUI calendar, DropDownComponent dropDownComponent, JTextField field) {
        this.calendar = calendar;
        this.dropDownComponent = dropDownComponent;
        this.field = field;
    }


    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        dropDownComponent.hidePopup(calendar);
        field.setText(propertyChangeEvent.getNewValue().toString());
    }
}
