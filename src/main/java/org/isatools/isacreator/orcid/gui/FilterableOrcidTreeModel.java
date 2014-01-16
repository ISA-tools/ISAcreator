package org.isatools.isacreator.orcid.gui;

import org.isatools.isacreator.common.filterableTree.FilterableJTree;
import org.isatools.isacreator.common.filterableTree.TreeFilterModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 24/05/2013
 * Time: 11:46
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */


    public class FilterableOrcidTreeModel<T, V> extends TreeFilterModel<T, V> {

        public FilterableOrcidTreeModel(TreeNode rootNode, FilterableJTree targetTree) {
            super(rootNode, targetTree);
        }

        public DefaultMutableTreeNode createRootNode() {
            return new DefaultMutableTreeNode(countValues() + " researchers grouped by " + countKeys() + " surnames");
        }


    }


