package org.isatools.isacreator.orcid.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.filterableTree.FilterableJTree;
import org.isatools.isacreator.common.filterableTree.TreeFilterModel;
import org.isatools.isacreator.effects.DraggablePaneMouseInputHandler;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.ontologyselectiontool.CustomTreeRenderer;
import org.isatools.isacreator.ontologyselectiontool.FilterableOntologyTreeModel;
import org.isatools.isacreator.orcid.OrcidClient;
import org.isatools.isacreator.orcid.impl.OrcidClientImpl;
import org.isatools.isacreator.orcid.model.OrcidAuthor;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 15:28
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidLookupUI extends JFrame implements WindowListener, MouseListener {

    private static InfiniteProgressPanel progressIndicator;

    private JLabel searchTypeLabel;
    private JTextField searchField;
    private FilterableJTree<String, OrcidAuthor> orcidSearchResultsTree = null;

    private Container searchAndResultsContainer;
    private OrcidSearchResultsPanel resultPane;
    //private JPanel parent;
    private JPanel searchUIContainer;
    private JLabel resultButton;

    private static OrcidClient orcidClient = null;
    private OrcidAuthor currentOrcidContact;

    @InjectedResource
    private ImageIcon orcidText, searchFieldLeft, search, searchOver, close, closeOver, accept, acceptOver,
        resultOver, result,filterInfo, leftFieldIcon, rightFieldIcon;

    public OrcidLookupUI() {
       // this.parent = parent;
        ResourceInjector.get("orcidlookup-package.style").inject(this);
        resultPane = new OrcidSearchResultsPanel();
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(599, 500));
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        progressIndicator = new InfiniteProgressPanel(
                "searching orcid");

        add(createTopPanel(), BorderLayout.NORTH);
        searchUIContainer = new JPanel();
        searchUIContainer.setPreferredSize(new Dimension(499, 270));
        searchUIContainer.add(createSearchAndResultPanel());

        //searchUIContainer.add(createResultsPanel());

        add(searchUIContainer, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    private Container createSearchAndResultPanel() {
        searchAndResultsContainer = Box.createVerticalBox();
        searchAndResultsContainer.setBackground(UIHelper.BG_COLOR);

        Box textContainer = Box.createHorizontalBox();

        searchTypeLabel = new JLabel(orcidText);

        textContainer.add(searchTypeLabel);

        searchField = new JTextField();

        Action searchOrcidContacts = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        };

        searchField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SEARCH_ORCID");
        searchField.getActionMap().put("SEARCH_ORCID", searchOrcidContacts);

        UIHelper.renderComponent(searchField, UIHelper.VER_12_BOLD, UIHelper.LIGHT_GREEN_COLOR, UIHelper.BG_COLOR);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(new EmptyBorder(2, 2, 2, 2));
        searchField.setText("enter id");

        textContainer.add(Box.createHorizontalStrut(5));
        textContainer.add(new JLabel(searchFieldLeft));
        textContainer.add(searchField);
        textContainer.add(Box.createHorizontalStrut(20));

        final JLabel searchButton = new JLabel(search);
        searchButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                searchButton.setIcon(searchOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                searchButton.setIcon(search);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                searchButton.setIcon(search);
                performSearch();
            }
        });

        textContainer.add(searchButton);

        searchAndResultsContainer.add(Box.createVerticalStrut(100));
        searchAndResultsContainer.add(textContainer);
        searchAndResultsContainer.add(Box.createVerticalGlue());

        createResultsPanel();

        return searchAndResultsContainer;
    }

    private Container createResultsPanel(){
        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        createSearchResultsTree(ui);
        JScrollPane treeScroll = new JScrollPane(orcidSearchResultsTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setBorder(new EtchedBorder());
        treeScroll.getViewport().setBackground(UIHelper.BG_COLOR);
        treeScroll.setPreferredSize(new Dimension(400, 170));
        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        orcidSearchResultsTree.addMouseListener(this);

        JPanel searchFields = new JPanel();
        searchFields.setLayout(new BoxLayout(searchFields, BoxLayout.PAGE_AXIS));
        searchFields.setBackground(UIHelper.BG_COLOR);

        //searchFields.add(searchSpan);
        //searchFields.add(searchFieldCont);
        searchFields.add(Box.createVerticalStrut(10));

        searchAndResultsContainer.add(searchFields, BorderLayout.NORTH);
        searchAndResultsContainer.add(treeScroll);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(UIHelper.BG_COLOR);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));

        ((JComponent) orcidSearchResultsTree.getFilterField()).setBorder(null);
        UIHelper.renderComponent(orcidSearchResultsTree.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        filterPanel.add(new JLabel(filterInfo));
        filterPanel.add(new JLabel(leftFieldIcon));
        filterPanel.add(orcidSearchResultsTree.getFilterField());
        filterPanel.add(new ClearFieldUtility(orcidSearchResultsTree.getFilterField()));
        filterPanel.add(new JLabel(rightFieldIcon));
        return filterPanel;
    }

    private void createSearchResultsTree(BasicTreeUI ui) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("result");

        orcidSearchResultsTree = new FilterableJTree<String,OrcidAuthor>();
        TreeFilterModel treeModel = new FilterableOrcidTreeModel<String, OrcidAuthor>(top, orcidSearchResultsTree);

        orcidSearchResultsTree.setModel(treeModel);
        orcidSearchResultsTree.setCellRenderer(new CustomTreeRenderer());
        orcidSearchResultsTree.expandRow(0);
        orcidSearchResultsTree.expandRow(1); // expand root and first result node on acquiring result! if there is no result, no exceptions will be thrown!
        orcidSearchResultsTree.setShowsRootHandles(false);
        orcidSearchResultsTree.setUI(ui);
    }

    private Container createTopPanel(){

        Box topContainer = Box.createVerticalBox();

        Box topPanel = Box.createHorizontalBox();

        resultButton = new JLabel();
        resultButton.setHorizontalAlignment(SwingConstants.LEFT);

        resultButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
//                if (resultButton.getIcon() != resultInactive) {
//                    resultButton.setIcon(resultOver);
//                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
//                if (resultButton.getIcon() != resultInactive) {
//                    //resultButton.setIcon(selectedSection == RESULT ? resultOver : result);
//                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
//                if (resultButton.getIcon() != resultInactive) {
                    resetButtons();
                    //selectedSection = RESULT;
//                    resultButton.setIcon(resultOver);
                    swapContainers(resultPane);
//                }
            }
        });

        topPanel.add(resultButton);
        //topPanel.add(new JLabel(end));

        topContainer.add(topPanel);

        return topContainer;

    }




    private Container createSouthPanel() {

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel closeButton = new JLabel(close);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeButton.setIcon(closeOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeButton.setIcon(close);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                firePropertyChange("noSelectedOrcidAuthor", "noneSelected", "");
                setVisible(false);
            }
        });

        final JLabel searchButton = new JLabel(accept);
        searchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                searchButton.setIcon(acceptOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                searchButton.setIcon(accept);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                firePropertyChange("selectedOrcid", "OLD_VALUE",
                        currentOrcidContact);

                setVisible(false);
            }
        });

        southPanel.add(closeButton, BorderLayout.WEST);
        southPanel.add(searchButton, BorderLayout.EAST);

        return southPanel;
    }

    private void performSearch() {

        Thread performer = new Thread(new Runnable() {
            public void run() {
                //try {
                    System.out.println("starting search");
                    if (!searchField.getText().equals("")) {
                        progressIndicator.setSize(new Dimension(
                                getWidth(),
                                getHeight()));
                        setGlassPane(progressIndicator);
                        progressIndicator.start();
                        OrcidLookupUI.this.validate();

                        OrcidAuthor[] result =  orcidClient.getOrcidProfiles(searchField.getText());


                        Map<String, Set<OrcidAuthor>> map = new HashMap<String, Set<OrcidAuthor>>();

                        for(OrcidAuthor contact: result){
                            Set<OrcidAuthor> set = new HashSet<OrcidAuthor>();
                            for(OrcidAuthor contact1: result){
                                if (contact.getFamilyName().equals(contact1.getFamilyName()))
                                    set.add(contact1);
                            }
                            map.put(contact.getFamilyName(), set);
                        }
                        orcidSearchResultsTree.setItems(map);

                        progressIndicator.stop();


                        /*
                        for (OrcidAuthor contact: result) {
                            currentOrcidContact = contact;
                            // push to SearchResultPane
                            resetButtons();
                            resultPane.showOrcidContact(currentOrcidContact);
                            swapContainers(resultPane);
                            resultButton.setIcon(resultOver);
                            progressIndicator.stop();
                            break;
                        }
                        */

                    }
                //} catch (Exception e) {
                //    e.printStackTrace();
                //} finally {
                //    if (progressIndicator.isStarted()) {
                //        SwingUtilities.invokeLater(new Runnable() {
                          //  public void run() {
                //                progressIndicator.stop();
                //            }
                //        });
                //    }
                //}
            }
        });



        orcidClient = new OrcidClientImpl();

        if (orcidClient != null) {
            performer.start();
        } else {
            resultPane.showError();
        }
    }

    private void resetButtons() {
//        if (resultButton.getIcon() != resultInactive) {
//            resultButton.setIcon(result);
//        }

    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            searchUIContainer.removeAll();
            searchUIContainer.add(newContainer);
            searchUIContainer.repaint();
            searchUIContainer.validate();
        }
    }

    public void windowOpened(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosing(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowClosed(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowIconified(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeiconified(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent windowEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent windowEvent) {
        firePropertyChange("noSelectedOrcid", "canceled", windowEvent.toString());
    }

    public void installListeners() {
        MouseInputAdapter handler = new DraggablePaneMouseInputHandler(this);
        Window window = this;
        window.addMouseListener(handler);
        window.addMouseMotionListener(handler);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof JTree) {
            JTree tree = (JTree) mouseEvent.getSource();

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {

                if (tree == orcidSearchResultsTree) {
                    if (selectedNode.isLeaf()) {

                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                        String surname = (String) parentNode.getUserObject();
                        System.out.println("Selected surname ="+surname);

                        OrcidAuthor orcidAuthor = (OrcidAuthor) selectedNode.getUserObject();

                        System.out.println("Selected author="+orcidAuthor);

                        if (mouseEvent.getClickCount() == 1) {

                            currentOrcidContact = orcidAuthor;
                           //retrieve the author information
                            firePropertyChange("selectedOrcid", "OLD_VALUE",
                                    currentOrcidContact);

                        }

//                        if (OntologyUtils.getSourceOntologyPortalByVersion(ontologySource.getSourceVersion()) == OntologyPortal.BIOPORTAL) {
//                            boolean sourceIsInPlugins = OntologySearchPluginRegistry.isOntologySourceAbbreviationDefinedInPlugins(ontologyTerm.getOntologySource());
//                            viewTermDefinition.setContent(createOntologyBranch(ontologyTerm), ontologySource.getSourceVersion(), sourceIsInPlugins ? null : bioportalClient == null ? new BioPortalClient() : bioportalClient);
//                        } else {
//                            viewTermDefinition.setContent(createOntologyBranch(ontologyTerm), ontologySource.getSourceVersion(), olsClient);
//                        }
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseEntered(MouseEvent mouseEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void mouseExited(MouseEvent mouseEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
