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

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * FilterableJTree provides filtering functionality on a JTree
 *
 * @author Eamonn Maguire
 * @date Mar 2, 2010
 */


public class FilterableJTree<T, V> extends JTree {
    private FilterField filterField;

    public FilterableJTree() {
        super();
        filterField = new FilterField();
        filterField.addPropertyChangeListener("filterEvent", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                fireUpdateToListeners();
            }
        });
    }

    private void fireUpdateToListeners() {
        firePropertyChange("update", "", "none");
    }

    public void addItem(T key, V value) {
        ((TreeFilterModel<T, V>) getModel()).addElement(key, value);
    }

    public void setItems(Map<T, Set<V>> values) {
        ((TreeFilterModel<T, V>) getModel()).setContents(values);
    }

    public void clearItems() {
        ((TreeFilterModel) getModel()).clearItems();
    }

    public FilterField getFilterField() {
        return filterField;
    }

    public void setFilterField(FilterField filterField) {
        this.filterField = filterField;
    }

    public Set<String> getExpandedTreePaths() {
        Set<String> expandedPaths = new HashSet<String>();

        TreeNode rootNode = (TreeNode) getModel().getRoot();

        Enumeration children = rootNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            TreePath path = new TreePath(childNode.getPath());

            if (isExpanded(path)) {
                expandedPaths.add(childNode.toString());
            }
        }

        return expandedPaths;
    }

    public void showExpandedKeys(Set<String> expandedKeys) {
        TreeNode rootNode = (TreeNode) getModel().getRoot();
        Enumeration children = rootNode.children();
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) children.nextElement();
            if (expandedKeys.contains(childNode.toString())) {
                expandPath(new TreePath(childNode.getPath()));
            }
        }
    }


    /**
     * The FilterFields which implements the DocumentListener class. Calls updates on the JList as and
     * when modifications occur in the textfield as a result of user insertion, deletion, or update.
     */
    class FilterField extends JTextField implements DocumentListener {
        public FilterField() {
            super();
            getDocument().addDocumentListener(this);
        }

        public void changedUpdate(DocumentEvent event) {
            ((TreeFilterModel) getModel()).refilter();
        }

        public void insertUpdate(DocumentEvent event) {
            ((TreeFilterModel) getModel()).refilterOnFilteredList();
        }

        public void removeUpdate(DocumentEvent event) {
            ((TreeFilterModel) getModel()).refilter();
        }
    }

}
