/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isatools.isacreator.autofilteringlist;

import org.isatools.isacreator.effects.components.RoundedJTextField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * The extended JList provides a way of creating a list which can be filtered using a JTextField
 *
 * @author Majority of code and concepts from Marinacci, J, Adamson, C., Swing Hacks, O'Reilly 2005.
 */
public class ExtendedJList extends JList implements ListSelectionListener, FilterObserver {
    private FilterField filterField;
    private String currentTerm;
    private boolean autoSelectFirstListItemOnFilter;

    public ExtendedJList() {
        this(new FilterableListCellRenderer());
    }

    public ExtendedJList(ListCellRenderer renderer) {
        this(renderer, false);
    }

    public ExtendedJList(ListCellRenderer renderer, boolean autoSelectFirstListItemOnFilter) {
        this(renderer, new FilterField(), autoSelectFirstListItemOnFilter);
    }

    public ExtendedJList(ListCellRenderer renderer, FilterField field, boolean autoSelectFirstListItemOnFilter) {
        super();

        this.autoSelectFirstListItemOnFilter = autoSelectFirstListItemOnFilter;

        setModel(new ListFilterModel());

        setCellRenderer(renderer);
        addListSelectionListener(this);

        filterField = field;
        filterField.registerObserver(this);
    }

    /**
     * Add an item to the JList
     *
     * @param s Item to add
     */
    public void addItem(Object s) {
        ((ListFilterModel) getModel()).addElement(s);
    }

    public void removeItem(Object s) {
        ((ListFilterModel) getModel()).removeElement(s);
    }

    public List<Object> getItems() {
        return ((ListFilterModel) getModel()).getItems();
    }

    public List<Object> getFilteredItems() {
        return ((ListFilterModel) getModel()).getFilterItems();
    }

    public void clearItems() {
        ((ListFilterModel) getModel()).clearItems();
    }

    /**
     * Return the filterField
     *
     * @return JTextField
     */
    public JTextField getFilterField() {
        return filterField;
    }

    /**
     * Return the selected Term
     *
     * @return String
     */
    public String getSelectedTerm() {
        return currentTerm;
    }

    /**
     * Set the JList's ListModel to be a new one
     *
     * @param lm - new ListFilterModel
     */
    public void setModel(ListFilterModel lm) {
        ListModel model = (lm == null) ? new DefaultListModel() : lm;
        super.setModel(model);
    }

    public void updateContents(Object[] newContents) {

        ((ListFilterModel) getModel()).clearItems();

        if (newContents != null) {
            for (Object s : newContents) {
                addItem(s);
            }
        }

        updateUI();
    }

    /**
     * On a ListSelectEvent, it can be assumed that a new term has been selected.
     * Therefore set the currentTerm to be the selected value as a String.
     *
     * @param event - ListSelectionEvent.
     */
    public void valueChanged(ListSelectionEvent event) {
        if (getSelectedValue() != null) {
            currentTerm = getSelectedValue().toString();
            firePropertyChange("itemSelected", "", getSelectedValue());
        }
    }

    /**
     * The ListFilterModel provides the logic to filter the list given the values entered in FilterField
     * textfield.
     */
    class ListFilterModel extends AbstractListModel {
        List<Object> filterItems;
        List<Object> items;

        public ListFilterModel() {
            super();
            items = new ArrayList<Object>();
            filterItems = new ArrayList<Object>();
        }

        /**
         * Add an element to the list of items, and then refilter the list in case the new item is being shown when it
         * shouldn't be.
         *
         * @param s - String to be added
         */
        public void addElement(Object s) {
            items.add(s);
            refilter();
        }

        public void removeElement(Object o) {
            items.remove(o);
            refilter();
        }

        public List<Object> getFilterItems() {
            return filterItems;
        }

        public List<Object> getItems() {
            return items;
        }

        /**
         * Clear the items in the list
         */
        public void clearItems() {
            items.clear();
        }

        /**
         * Get the element at an int i in the list
         *
         * @param i - index of item to fetch.
         * @return String if the item exists, null otherwise.
         */
        public Object getElementAt(int i) {

            if (i < filterItems.size()) {
                return filterItems.get(i);
            }


            return null;
        }

        /**
         * Get the index in the list for a specific item.
         *
         * @param item - item to search for.
         * @return Integer - index of item if it exists, -1 if it doesn't.
         */
        public int getIndexForItem(String item) {
            int itemSize = items.size();

            Object[] itemsAsArray = items.toArray(new Object[items.size()]);

            for (int i = 0; i < itemSize; i++) {
                if (itemsAsArray[i].toString().equalsIgnoreCase(item)) {
                    return i;
                }
            }

            return -1;
        }

        /**
         * Get the size of the filterItems list.
         *
         * @return Integer - the size of the list.
         */
        public int getSize() {
            return filterItems.size();
        }

        /**
         * Refilter method clears the previously filtered items and then using the value typed into
         * the FilterField JTextField, items which contain the value typed into the field are added to
         * the filterItems list of terms.
         */
        public void refilter() {
            filterItems.clear();

            String term = getFilterField().getText().toLowerCase();

            for (Object s : items) {
                if (s.toString().toLowerCase().contains(term)) {
                    filterItems.add(s);
                }

                fireContentsChanged(this, 0, getSize());
                clearSelection();

                selectFirstItem();
            }

        }

        /**
         * Slight performance enhancement is instead of performing a complete refilter everytime, perform
         * a filter on the filtered items, whose size is inevitable going to be less, but at worse equal to the
         * the already filtered list.
         */
        public void refilterOnFilteredList() {
            String term = getFilterField().getText().toLowerCase();
            List<Object> toRemove = new ArrayList<Object>();

            for (Object s : filterItems) {
                if (!s.toString().toLowerCase().contains(term)) {
                    toRemove.add(s);
                }
            }

            // items are removed after. otherwise there is concurrent access on the ArrayList, which is
            // not Thread safe.
            for (Object lo : toRemove) {
                filterItems.remove(lo);
            }

            fireContentsChanged(this, 0, getSize());
            clearSelection();


            selectFirstItem();
        }

        private void selectFirstItem() {
            if (autoSelectFirstListItemOnFilter) {
                if (filterItems.size() > 0) {
                    setSelectedIndex(0);
                } else {
                    firePropertyChange("zeroTerms", 0, -1);
                }
            }
        }
    }

    public void notifyOfSelection(String observation) {
        if (observation.equals(FilterSubject.UPDATE)) {
            // if the size is greater than one, there were already contents, so it's
            // better to refilter than to do a complete refilter on all contents
            if (getFilterField().getText().length() > 1) {
                ((ListFilterModel) getModel()).refilterOnFilteredList();
            } else {
                ((ListFilterModel) getModel()).refilter();
            }
        } else if (observation.equals(FilterSubject.REMOVE)) {
            ((ListFilterModel) getModel()).refilter();
        }
    }

    public void cleanReferences() {
        removeListSelectionListener(this);
        setCellRenderer(null);
        setModel(null);
        filterField.unregisterAllObservers();
        filterField = null;
    }
}
