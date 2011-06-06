package org.isatools.isacreator.ontologybrowsingutils;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OLSClient;
import org.isatools.isacreator.ontologymanager.OntologyQueryAdapter;
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
public class WSOntologyTreeCreator implements OntologyTreeCreator, TreeSelectionListener, TreeModelListener, TreeExpansionListener {

    private List<TreeObserver> observers;

    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private Map<String, RecommendedOntology> ontologies;

    private Container browser;
    private OntologyService bioportalClient;
    private OntologyService olsClient;
    private JTree tree;

    public WSOntologyTreeCreator(Container browser, JTree tree) {
        this.browser = browser;
        this.tree = tree;
        observers = new ArrayList<TreeObserver>();

        this.bioportalClient = new BioPortalClient();
        this.olsClient = new OLSClient();
    }

    public DefaultMutableTreeNode createTree(Map<String, RecommendedOntology> ontologies) throws FileNotFoundException {
        this.ontologies = ontologies;

        rootNode = new DefaultMutableTreeNode(ontologies.size() + " recommended ontologies");
        treeModel = new DefaultTreeModel(rootNode);
        treeModel.addTreeModelListener(this);

        tree.setModel(treeModel);
        tree.addTreeExpansionListener(this);
        tree.addTreeSelectionListener(this);

//        if (ontology.getOntologyAbbreviation().equals("NEWT")) {
//            rootNode.add(new DefaultMutableTreeNode("NEWT is not browseable"));
//        } else {
        initiateOntologyVisualization();
//        }
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


            // update the tree
            for (String termId : rootTerms.keySet()) {
                addTermToTree(ontologyNode, termId, rootTerms.get(termId), recommendedOntology.getOntology());
            }

            // not root terms found
            if (rootTerms.size() == 0) {
                if (recommendedOntology.getOntology().getOntologyAbbreviation().equalsIgnoreCase("newt")) {
                    addTermToTree(ontologyNode, "NEWT cannot be loaded from the Ontology lookup service", new OntologyTerm(), recommendedOntology.getOntology());
                } else {
                    addTermToTree(ontologyNode, "Problem loading ontology from the ontology resource!", new OntologyTerm(), recommendedOntology.getOntology());
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
            addTermToTree(parentTerm, accession, termChildren.get(accession), ontology);
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

    private void addTermToTree(String termId, OntologyTerm termName, Ontology ontology) {
        addTermToTree(null, termId, termName, ontology);
    }


    private void addTermToTree(DefaultMutableTreeNode parent, String termId, OntologyTerm termName, Ontology ontology) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(new OntologyTreeItem(new OntologyBranch(termName.getOntologySourceAccession(), termName.getOntologyTermName()), ontology));

        if (parent == null) {
            parent = rootNode;
        }
        treeModel.insertNodeInto(childNode, parent, parent.getChildCount());
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

    public static void main(String[] args) {
        JFrame ontologyTest = new JFrame("Ontology browse test");

        ontologyTest.setBackground(UIHelper.BG_COLOR);
        ontologyTest.setSize(500, 300);

        JPanel container = new JPanel();
        container.setBackground(UIHelper.BG_COLOR);

        JTree tree = new JTree();

        JScrollPane scroller = new JScrollPane(tree);

        WSOntologyTreeCreator wsOntologyTreeCreator = new WSOntologyTreeCreator(container, tree);

        container.add(scroller);

        ontologyTest.add(container, BorderLayout.CENTER);

        ontologyTest.pack();
        ontologyTest.setVisible(true);


        try {

            Map<String, RecommendedOntology> ontologies = new HashMap<String, RecommendedOntology>();

            ontologies.put("EFO", new RecommendedOntology(new Ontology("1136", "45659", "EFO", "Experimental Factor Ontology"), new OntologyBranch("span:ProcessualEntity", "process")));
            ontologies.put("CHEBI", new RecommendedOntology(new Ontology("1007", "45734", "ChEBI", "Chemicals of Biological Interest")));
            ontologies.put("FBsp", new RecommendedOntology(new Ontology("", "Jun 2010", "FBsp", "Fly Taxonomy"), new OntologyBranch("FBsp:10000001", "taxon")));

            wsOntologyTreeCreator.createTree(ontologies);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
