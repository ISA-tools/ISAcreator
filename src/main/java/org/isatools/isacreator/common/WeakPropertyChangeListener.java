package org.isatools.isacreator.common;

import org.apache.log4j.Logger;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;


public class WeakPropertyChangeListener implements PropertyChangeListener {

    private static Logger logger = Logger.getLogger(WeakPropertyChangeListener.class.getName());

    WeakReference<PropertyChangeListener> listenerRef;

    public WeakPropertyChangeListener(PropertyChangeListener listener) {
        listenerRef = new WeakReference<PropertyChangeListener>(listener);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        PropertyChangeListener listener = listenerRef.get();
        if (listener == null) {
            removeListener(evt.getSource());
        } else {
            listener.propertyChange(evt);
        }
    }

    private void removeListener(Object src) {
        try {
            Method method = src.getClass().getMethod("removePropertyChangeListener"
                    , new Class[]{PropertyChangeListener.class});
            method.invoke(src, new Object[]{this});
        } catch (Exception e) {
            logger.error("Cannot remove listener: " + e);
        }
    }

}