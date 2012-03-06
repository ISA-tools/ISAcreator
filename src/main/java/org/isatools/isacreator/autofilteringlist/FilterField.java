package org.isatools.isacreator.autofilteringlist;

import org.isatools.isacreator.ontologybrowsingutils.TreeObserver;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;

/**
 * The FilterFields which implements the DocumentListener class. Calls updates on the JList as and
 * when modifications occur in the textfield as a result of user insertion, deletion, or update.
 */
public class FilterField extends JTextField implements DocumentListener, FilterSubject {
    private List<FilterObserver> observers;

    public FilterField() {
        super(20);
        observers = new ArrayList<FilterObserver>();
        getDocument().addDocumentListener(this);
    }

    public void changedUpdate(DocumentEvent event) {
        notifyObservers(FilterSubject.UPDATE);
    }

    public void insertUpdate(DocumentEvent event) {
        notifyObservers(FilterSubject.UPDATE);
    }

    public void removeUpdate(DocumentEvent event) {
        notifyObservers(FilterSubject.REMOVE);
    }

    public void registerObserver(FilterObserver observer) {
        observers.add(observer);
    }

    public void unregisterObserver(FilterObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(String observation) {
        for (FilterObserver observer : observers) {
            observer.notifyOfSelection(observation);
        }
    }
}
