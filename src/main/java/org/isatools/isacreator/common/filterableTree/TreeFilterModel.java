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

package org.isatools.isacreator.common.filterableTree;

import org.apache.commons.collections15.set.ListOrderedSet;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import java.util.*;

/**
 * TreeFilterModel
 *
 * @author eamonnmaguire
 * @date Oct 20, 2010
 */


public abstract class TreeFilterModel<T, V> extends DefaultTreeModel {
    Map<T, Set<V>> filterItems;
    Map<T, Set<V>> items;
    private FilterableJTree targetJTree;

    public TreeFilterModel(TreeNode treeNode, FilterableJTree targetJTree) {
        super(treeNode);
        this.targetJTree = targetJTree;
        items = new HashMap<T, Set<V>>();
        filterItems = new HashMap<T, Set<V>>();
    }

    /**
     * Add an element to the Map of items, and then refilter the list in case the new item is being shown when it
     * shouldn't be.
     *
     * @param key  - where the item should be added
     * @param item - the item to be added
     */
    public void addElement(T key, V item) {
        if (!items.containsKey(key)) {
            items.put(key, new ListOrderedSet<V>());
        }
        items.get(key).add(item);
        refilter();
    }

    public void removeElement(T key) {
        items.remove(key);
        refilter();
    }

    public void setContents(Map<T, Set<V>> contents) {
        clearItems();
        items.putAll(contents);
        refilter();
        rebuildTree();
    }

    public Map<T, Set<V>> getFilterItems() {
        return filterItems;
    }

    public Map<T, Set<V>> getItems() {
        return items;
    }

    /**
     * Clear the items in the list
     */
    public void clearItems() {
        items.clear();
    }

    /**
     * Get the size of the filterItems list.
     *
     * @return Integer - the size of the list.
     */
    public int getSize() {
        return filterItems.size();
    }

    public Object getElementAt(int i) {
        return null;
    }

    /**
     * Refilter method clears the previously filtered items and then using the value typed into
     * the FilterField JTextField, items which contain the value typed into the field are added to
     * the filterItems list of terms.
     */
    public void refilter() {
        filterItems.clear();

        String term = targetJTree.getFilterField().getText().toLowerCase();

        for (T key : items.keySet()) {

            for (V value : items.get(key)) {
                if (value.toString().toLowerCase().contains(term)) {
                    if (!filterItems.containsKey(key)) {
                        filterItems.put(key, new ListOrderedSet<V>());
                    }
                    filterItems.get(key).add(value);
                }
            }
            targetJTree.clearSelection();
        }
        rebuildTree();

    }

    /**
     * Slight performance enhancement is instead of performing a complete refilter everytime, perform
     * a filter on the filtered items, whose size is inevitable going to be less, but at worse equal to the
     * the already filtered list.
     */
    public void refilterOnFilteredList() {
        String term = targetJTree.getFilterField().getText().toLowerCase();
        Map<T, List<V>> toRemove = new HashMap<T, List<V>>();

        for (T key : filterItems.keySet()) {
            for (V value : items.get(key)) {
                if (!value.toString().toLowerCase().contains(term)) {
                    if (!toRemove.containsKey(key)) {
                        toRemove.put(key, new ArrayList<V>());
                    }
                    toRemove.get(key).add(value);
                }
            }

            // items are removed after. otherwise there is concurrent access on the ArrayList, which is
            // not Thread safe.
            for (T toRemoveKey : toRemove.keySet()) {
                for (V value : toRemove.get(toRemoveKey)) {
                    filterItems.get(toRemoveKey).remove(value);
                }
            }
            targetJTree.clearSelection();
        }
        rebuildTree();
    }

    private void rebuildTree() {
        Set<String> expandedPaths = targetJTree.getExpandedTreePaths();

        DefaultMutableTreeNode root = createRootNode();
        for (T key : filterItems.keySet()) {
            if (filterItems.get(key).size() > 0) {
                DefaultMutableTreeNode nodeForKey = new DefaultMutableTreeNode(key);
                for (V value : filterItems.get(key)) {
                    nodeForKey.add(new DefaultMutableTreeNode(value));
                }
                root.add(nodeForKey);
            }
        }
        setRoot(root);
        targetJTree.showExpandedKeys(expandedPaths);
    }

    public abstract DefaultMutableTreeNode createRootNode();

    public int countValues() {
        int termCount = 0;

        for (T key : filterItems.keySet()) {
            termCount += filterItems.get(key).size();
        }
        return termCount;
    }

    public int countKeys() {
        int ontologyCount = 0;

        for (T key : filterItems.keySet()) {
            if (filterItems.get(key).size() > 0) {
                ontologyCount++;
            }
        }
        return ontologyCount;
    }

}
