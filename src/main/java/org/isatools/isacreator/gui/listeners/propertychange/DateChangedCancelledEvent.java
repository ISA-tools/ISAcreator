package org.isatools.isacreator.gui.listeners.propertychange;

import org.isatools.isacreator.calendar.CalendarGUI;
import org.isatools.isacreator.common.DropDownComponent;

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
public class DateChangedCancelledEvent implements PropertyChangeListener {

        private CalendarGUI calendar;
        private DropDownComponent dropDownComponent;

        public DateChangedCancelledEvent(CalendarGUI calendar, DropDownComponent dropDownComponent) {
            this.calendar = calendar;
            this.dropDownComponent = dropDownComponent;

        }

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            dropDownComponent.hidePopup(calendar);
        }
    }
