/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
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
package org.isatools.isacreator.ontologybrowsingutils;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologymanager.bioportal.model.OntologyPortal;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.ontologymanager.utils.OntologyUtils;
import org.isatools.isacreator.utils.StringProcessing;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/05/2011
 *         Time: 15:46
 */
public class WSOntologyTreeCreator implements OntologyTreeCreator, TreeSelectionListener, TreeModelListener, TreeExpansionListener, TreeSubject {

    private List<TreeObserver> observers;

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private Map<String, RecommendedOntology> ontologies;

    private Container browser;
    private static OntologyService bioportalClient;
    private static OntologyService olsClient;
    private JTree tree;

    public WSOntologyTreeCreator(Container browser, JTree tree) {
        this.browser = browser;
        this.tree = tree;
        observers = new ArrayList<TreeObserver>();

        bioportalClient = new BioPortalClient();
        olsClient = new OLSClient();
    }

    public DefaultMutableTreeNode createTree(Map<String, RecommendedOntology> ontologies) throws FileNotFoundException {
        this.ontologies = ontologies;

        rootNode = new DefaultMutableTreeNode(ontologies.size() + " recommended ontologies");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(this);

        tree.setModel(treeModel);
        tree.addTreeExpansionListener(this);
        tree.addTreeSelectionListener(this);


        initiateOntologyVisualization();
        return rootNode;
    }

    private void initiateOntologyVisualization() {
        browser.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // instead of visualising just one ontology, we need to visualise all that have been selected to browse on, if they have branches
        // specified...or maybe should allow to browse the whole ontology as well.

        for (String ontology : ontologies.keySet()) {
            System.out.println("Adding " + ontology + " to tree");

            RecommendedOntology recommendedOntology = ontologies.get(ontology);

            System.out.println("initialising ontology visualisation for " + recommendedOntology.getOntology().getOntologyDisplayLabel());

            System.out.println("Ontology version is: " + recommendedOntology.getOntology().getOntologyVersion());
            // if ontology has no branch specified, query the whole ontology.
            Map<String, OntologyTerm> rootTerms;

            String nodeLabel = recommendedOntology.getOntology().getOntologyDisplayLabel();

            OntologyService service = getCorrectOntologyService(recommendedOntology.getOntology());


            if (recommendedOntology.getBranchToSearchUnder() != null) {

                nodeLabel += " under " + recommendedOntology.getBranchToSearchUnder().getBranchName();

                String branchIdentifier = recommendedOntology.getBranchToSearchUnder().getBranchIdentifier();

                if (StringProcessing.isURL(branchIdentifier)) {
                    branchIdentifier = OntologyUtils.getModifiedBranchIdentifier(branchIdentifier, "#");
                }

                System.out.println("Going to search for " + branchIdentifier + " in " + recommendedOntology.getOntology().getOntologyDisplayLabel());

                rootTerms = service.getTermChildren(branchIdentifier, getCorrectQueryString(service, recommendedOntology.getOntology()));

            } else {
                rootTerms = service.getOntologyRoots(
                        getCorrectQueryString(service, recommendedOntology.getOntology()));

            }

            DefaultMutableTreeNode ontologyNode = new DefaultMutableTreeNode(nodeLabel);

            rootNode.add(ontologyNode);

            boolean addedTerms = false;
            // update the tree
            for (String termId : rootTerms.keySet()) {
                if (termId != null && !termId.equalsIgnoreCase("")) {
                    addedTerms = true;
                    addTermToTree(ontologyNode, rootTerms.get(termId), recommendedOntology.getOntology());
                }
            }

            if (recommendedOntology.getOntology().getOntologyAbbreviation().equalsIgnoreCase("newt")) {
                addInformationToTree(ontologyNode, "NEWT cannot be loaded from the Ontology lookup service");
            }

            if (!addedTerms) {
                if (service instanceof BioPortalClient) {
                    addInformationToTree(ontologyNode, "Problem loading " + recommendedOntology.getOntology().getOntologyAbbreviation() + " (version "
                            + recommendedOntology.getOntology().getOntologyVersion() + ") from BioPortal!");
                } else {
                    addInformationToTree(ontologyNode, "Problem loading " + recommendedOntology.getOntology().getOntologyAbbreviation() + " from OLS");
                }
            }
        }

        updateTree();
        browser.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private OntologyService getCorrectOntologyService(Ontology ontology) {
        return OntologyUtils.getSourceOntologyPortal(ontology) == OntologyPortal.OLS ? olsClient : bioportalClient;
    }

    private String getCorrectQueryString(OntologyService service, Ontology ontology) {
        return service instanceof BioPortalClient ? ontology.getOntologyVersion() : ontology.getOntologyAbbreviation();
    }

    public void updateTree() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        TreePath path = new TreePath(root.getPath());
        tree.collapsePath(path);
        tree.expandPath(path);
    }

    /**
     * Method to preload the next level of the ontology so that we know which nodes are leaves and which are
     * nodes at any given level.
     *
     * @param termAccession - accession of term to load children for
     * @param parentTerm    - the parent node of the term being searched for.
     */
    private void preloadNextOntologyLevel(String termAccession, DefaultMutableTreeNode parentTerm) {
        browser.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Ontology ontology = extractOntologyFromNode(parentTerm);

        OntologyService service = getCorrectOntologyService(ontology);

        Map<String, OntologyTerm> termChildren = service.getTermChildren(termAccession, getCorrectQueryString(service, ontology));

        // add the level of non visible nodes
        for (String accession : termChildren.keySet()) {
            addTermToTree(parentTerm, termChildren.get(accession), ontology);
        }

        browser.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private Ontology extractOntologyFromNode(DefaultMutableTreeNode node) {
        if (node.getUserObject() instanceof OntologyTreeItem) {
            OntologyTreeItem termNode = (OntologyTreeItem) node.getUserObject();
            return termNode.getOntology();
        }

        return null;
    }

    private void addInformationToTree(DefaultMutableTreeNode parent, String information) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(information);

        if (parent == null) {
            parent = rootNode;
        }
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
    }

    private void addTermToTree(DefaultMutableTreeNode parent, OntologyTerm ontologyTerm, Ontology ontology) {
        if (ontologyTerm != null) {

            DefaultMutableTreeNode childNode =
                    new DefaultMutableTreeNode(new OntologyTreeItem(
                            new OntologyBranch((OntologyUtils.getSourceOntologyPortal(ontology) == OntologyPortal.OLS)
                                    ? ontologyTerm.getOntologySource() + ":" + ontologyTerm.getOntologySourceAccession()
                                    : ontologyTerm.getOntologySourceAccession(), ontologyTerm.getOntologyTermName()), ontology));

            if (parent == null) {
                parent = rootNode;
            }
            treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
        }
    }

    public void treeExpanded(TreeExpansionEvent treeExpansionEvent) {
        // get selected node
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeExpansionEvent.getPath().getLastPathComponent();

        if (node == null) {
            return;
        }

        // add next level of nodes if not already added
        final Enumeration<DefaultMutableTreeNode> treeNodeEnumeration = node.children();

        while (treeNodeEnumeration.hasMoreElements()) {
            DefaultMutableTreeNode treeNode = treeNodeEnumeration.nextElement();

            if (treeNode.getUserObject() instanceof OntologyTreeItem) {
                if (treeNode.getChildCount() == 0) {
                    OntologyTreeItem currentTerm = (OntologyTreeItem) treeNode.getUserObject();
                    preloadNextOntologyLevel(currentTerm.getBranch().getBranchIdentifier(), treeNode);
                }
            }
        }
    }

    public void expandTreeToReachTerm(OntologyTerm term) {
        Enumeration treeVisitor = rootNode.breadthFirstEnumeration();

        Ontology ontology = new Ontology("", term.getOntologyVersionId(), term.getOntologySourceInformation().getSourceName(), term.getOntologySourceInformation().getSourceDescription());

        OntologyService service = getCorrectOntologyService(ontology);

        Map<String, OntologyTerm> nodeParentsFromRoot = service.getAllTermParents((OntologyUtils.getSourceOntologyPortal(ontology) == OntologyPortal.OLS)
                ? term.getOntologySource() + ":" + term.getOntologySourceAccession()
                : term.getOntologySourceAccession(), service instanceof OLSClient ? term.getOntologySource() : term.getOntologySourceInformation().getSourceVersion());
        TreePath lastPath = null;

        for (OntologyTerm node : nodeParentsFromRoot.values()) {
            while (treeVisitor.hasMoreElements()) {
                DefaultMutableTreeNode visitingNode = (DefaultMutableTreeNode) treeVisitor.nextElement();
                if (visitingNode.getUserObject() instanceof OntologyTreeItem) {
                    OntologyTreeItem termNode = (OntologyTreeItem) visitingNode.getUserObject();

                    if (termNode.getBranch().getBranchName().toLowerCase().equalsIgnoreCase(node.getOntologyTermName())
                            || termNode.getBranch().getBranchIdentifier().equalsIgnoreCase(node.getOntologySourceAccession())) {
                        TreePath pathToNode = new TreePath(visitingNode.getPath());
                        tree.expandPath(pathToNode);
                        tree.setSelectionPath(pathToNode);
                        lastPath = pathToNode;
                        break;
                    }
                }
            }
        }

        if (lastPath != null) {

            tree.scrollPathToVisible(lastPath);
            tree.repaint();
        }
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        // get selected node

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) treeSelectionEvent.getPath().getLastPathComponent();

        if (node == null) {
            return;
        }

        // get node data object
        if (node.getUserObject() instanceof OntologyTreeItem) {
            OntologyTreeItem ontologyTerm = (OntologyTreeItem) node.getUserObject();

            // tell the observers that an item has been selected.
            notifyObservers();
            // load the children and the meta data, unless the term is the 'no roots defined' dummy term
            if (ontologyTerm != null && !ontologyTerm.getBranch().getBranchIdentifier().equalsIgnoreCase("No Root Terms Defined!")) {

                // load children only for leaf nodes and those that have not been marked as processed
                if (node.isLeaf() && node.getAllowsChildren()) {
                    OntologyService service = getCorrectOntologyService(ontologyTerm.getOntology());
                    // load children. if no children, set allowsChildren to false
                    Map<String, OntologyTerm> termChildren = service.getTermChildren(ontologyTerm.getBranch().getBranchIdentifier(),
                            getCorrectQueryString(service, ontologyTerm.getOntology()));

                    if (termChildren.size() > 0) {
                        node.setAllowsChildren(true);
                    }
                }
                // olsDialog.loadMetaData(nodeInfo.getTermId(), OLSDialog.OLS_DIALOG_BROWSE_ONTOLOGY);
            } else {
                rootNode.removeAllChildren();
                treeModel.reload();
            }
        }

    }

    public void treeNodesChanged(TreeModelEvent treeModelEvent) {
    }

    public void treeNodesInserted(TreeModelEvent treeModelEvent) {
    }

    public void treeNodesRemoved(TreeModelEvent treeModelEvent) {
    }

    public void treeStructureChanged(TreeModelEvent treeModelEvent) {
    }

    public void treeCollapsed(TreeExpansionEvent treeExpansionEvent) {
    }

    public void notifyObservers() {
        for (TreeObserver observer : observers) {
            observer.notifyOfSelection();
        }
    }

    public void registerObserver(TreeObserver observer) {
        if (observer != null) {
            observers.add(observer);
        }
    }

    public void unregisterObserver(TreeObserver observer) {
        if (observer != null) {
            if (observers.contains(observer)) {
                observers.remove(observer);
            }
        }
    }
}
