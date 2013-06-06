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

package org.isatools.isacreator.ontologyselectiontool;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.log4j.Logger;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.Globals;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.filterableTree.FilterableJTree;
import org.isatools.isacreator.common.filterableTree.TreeFilterModel;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.effects.SingleSelectionListCellRenderer;
import org.isatools.isacreator.ontologybrowsingutils.OntologyTreeItem;
import org.isatools.isacreator.ontologybrowsingutils.WSOntologyTreeCreator;
import org.isatools.isacreator.ontologymanager.*;
import org.isatools.isacreator.ontologymanager.bioportal.model.OntologyPortal;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.ontologymanager.utils.OntologyUtils;
import org.isatools.isacreator.optionselector.OptionGroup;
import org.isatools.isacreator.plugins.registries.OntologySearchPluginRegistry;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;

/**
 * OntologySelectionTool provides interface to connect to the OLS (OR bioPortal) to retrieve appropriate
 * terms for annotation of their experiment.
 * <p/>
 * todo modify to work based on resetting recommended ontologies etc for new static import in cell editor.
 *
 * @author Eamonn Maguire
 */
public class OntologySelectionTool extends JFrame implements MouseListener, OntologySelector, WindowListener {

    private static final Logger log = Logger.getLogger(OntologySelectionTool.class.getName());

    // We'll store recently searched terms in a cache so that multiple searches on the same term in a short period
    // of time do not result in identical queries to the OLS on each occasion. if the user searches all ontologies for
    // "mito", then the map will consist of all:mito -> result map.
    private static final int SEARCH_MODE = 0;
    private static final int BROWSE_MODE = 1;
    private static final int HISTORY_MODE = 2;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 400;
    public static final String RECOMMENDED_ONTOLOGIES = "Recommended Ontologies";
    public static final String ALL_ONTOLOGIES = "All Ontologies";

    private SingleSelectionListCellRenderer singleSelectionListCellRenderer = new SingleSelectionListCellRenderer();

    @InjectedResource
    private ImageIcon termDefinitionIcon, searchButton, searchButtonOver, filterInfo, browseOntologiesIcon,
            browseOntologiesIconOver, searchOntologiesIcon, searchOntologiesIconOver, leftFieldIcon, rightFieldIcon,
            viewHistoryIcon, viewHistoryIconOver;

    private static OntologyService olsClient = null;
    private static OntologyService bioportalClient = null;

    private InfiniteProgressPanel progressIndicator;
    private ExtendedJList historyList;
    private JTextField searchField, selectedTerm;
    private FilterableJTree<OntologySourceRefObject, OntologyTerm> ontologySearchResultsTree;
    private JTree browseRecommendedOntologyTree;

    private int mode = SEARCH_MODE;

    private Map<OntologySourceRefObject, List<OntologyTerm>> result = null;
    private Map<String, RecommendedOntology> recommendedOntologies;

    private boolean multipleTermsAllowed;

    private ViewTermDefinitionUI viewTermDefinition;
    private WSOntologyTreeCreator wsOntologyTreeCreator;

    private JPanel ontologyViewContainer, searchUIContainer, browseUIContainer, historyUIContainer;
    private JLabel searchOntologiesTab, browseRecommendedOntologiesTab, viewHistoryTab, searchOntologiesButton, confirmOkButton;

    private OptionGroup<String> searchSpan;
    private Set<OntologyTerm> selectedTerms;

    private boolean treeCreated = false;
    private boolean forceOntologySelection;
    private FooterPanel footer;

    /**
     *
     */
    public OntologySelectionTool() {
        this(false, false, new HashMap<String, RecommendedOntology>());
    }

    /**
     * OntologySelectionTool constructor.
     *
     * @param multipleTermsAllowed   - Whether or not multiple terms are allowed to be selected.
     * @param forceOntologySelection - force user to add an ontology term. Effectively renders the text box uneditable.
     * @param recommendedOntologies  - the recommended ontology source e.g. EFO, UO, NEWT, CHEBI.
     */
    public OntologySelectionTool(boolean multipleTermsAllowed, boolean forceOntologySelection, Map<String, RecommendedOntology> recommendedOntologies) {
        ResourceInjector.get("ontologyselectiontool-package.style").inject(this);

        addWindowListener(this);
        this.multipleTermsAllowed = multipleTermsAllowed;
        this.forceOntologySelection = forceOntologySelection;
        this.recommendedOntologies = recommendedOntologies;

        selectedTerms = new HashSet<OntologyTerm>();
        footer = new FooterPanel(this);
    }

    /**
     * Create the OntologySelectionTool GUI.
     */
    public void createGUI() {

        setTitle("Ontology Selection Tool");
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setUndecorated(true);
        setAlwaysOnTop(true);

        ontologyViewContainer = new JPanel(new BorderLayout());
        ontologyViewContainer.setPreferredSize(new Dimension(400, 170));

        progressIndicator = new InfiniteProgressPanel("searching for matching terms in ontologies");

        createPanels();
        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));

        pack();
    }

    public void setSearchFieldText(String text) {
        searchField.setText(text);
    }

    public void setMultipleTermsAllowed(boolean multipleTermsAllowed) {
        this.multipleTermsAllowed = multipleTermsAllowed;
    }

    public void setForceOntologySelection(boolean forceOntologySelection) {
        this.forceOntologySelection = forceOntologySelection;
        selectedTerm.setEditable(!forceOntologySelection);
    }

    public void setRecommendedOntologies(final Map<String, RecommendedOntology> recommendedOntologies) {

        System.out.println("Resetting recommended ontologies, it is now: parameter="+recommendedOntologies + " field="+ this.recommendedOntologies);

        if (recommendedOntologies!=null){

            final boolean resetView = !(this.recommendedOntologies == recommendedOntologies);

            this.recommendedOntologies = recommendedOntologies;

            SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                boolean recommendedOntologiesAvailable = checkIfRecommendedOntologiesAreAvailable();
                browseRecommendedOntologiesTab.setVisible(recommendedOntologiesAvailable);

                if (resetView) {
                    resetView();
                    // should also reset the recommended ontologies. Displaying recommended ontologies for another field
                    // if not a great idea.
                    if (recommendedOntologiesAvailable) {
                        ontologySearchResultsTree.setModel(new FilterableOntologyTreeModel<OntologySourceRefObject, List<OntologyTerm>>(new DefaultMutableTreeNode("results"), ontologySearchResultsTree));

                        treeCreated = false;
                    }
                    searchSpan.toggleOptionEnabled(RECOMMENDED_ONTOLOGIES, recommendedOntologiesAvailable);
                    searchSpan.setSelectedItem(recommendedOntologiesAvailable ? RECOMMENDED_ONTOLOGIES : ALL_ONTOLOGIES);
                }
                }
            });

        }
    }

    private void resetView() {

        resetButtons();
        searchOntologiesTab.setIcon(searchOntologiesIconOver);
        mode = SEARCH_MODE;
        swapContainers(searchUIContainer);
    }


    /**
     * Create each of the required JPanels for the GUI, and add them to the appropriate section of
     * the GUI.
     */
    private void createPanels() {
        HUDTitleBar titlePanel = new HUDTitleBar(null, null, true);
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));

        mainPanel.add(createSearchPanel());
        mainPanel.add(Box.createHorizontalStrut(5));
        mainPanel.add(createTermDefinitionPanel());

        add(mainPanel, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);
    }


    /**
     * Create the Search panel which will contain the options to use the recommended source and so forth as
     * well as the result tree which shows all the terms found.
     *
     * @return JPanel containing the Search panel components.
     */
    private JPanel createSearchPanel() {
        JPanel searchPanelContainer = createStandardBorderPanel(false);
        searchPanelContainer.setBackground(UIHelper.BG_COLOR);

        searchOntologiesTab = new JLabel(searchOntologiesIconOver);
        searchOntologiesTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                swapContainers(searchUIContainer);
                searchOntologiesTab.setIcon(searchOntologiesIconOver);
                mode = SEARCH_MODE;
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                searchOntologiesTab.setIcon(searchOntologiesIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                searchOntologiesTab.setIcon(mode == SEARCH_MODE ? searchOntologiesIconOver : searchOntologiesIcon);
            }
        });

        viewHistoryTab = new JLabel(viewHistoryIcon);
        viewHistoryTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                swapContainers(historyUIContainer);
                viewHistoryTab.setIcon(viewHistoryIconOver);
                mode = HISTORY_MODE;
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                viewHistoryTab.setIcon(viewHistoryIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                viewHistoryTab.setIcon(mode == HISTORY_MODE ? viewHistoryIconOver : viewHistoryIcon);
            }
        });

        browseRecommendedOntologiesTab = new JLabel(browseOntologiesIcon);
        browseRecommendedOntologiesTab.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                browseRecommendedOntologiesTab.setIcon(browseOntologiesIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                browseRecommendedOntologiesTab.setIcon(mode == BROWSE_MODE ? browseOntologiesIconOver : browseOntologiesIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                swapContainers(browseUIContainer);
                browseRecommendedOntologiesTab.setIcon(browseOntologiesIconOver);
                mode = BROWSE_MODE;

                loadTree();
            }
        });

        boolean recommendedOntologiesAvailable = checkIfRecommendedOntologiesAreAvailable();

        Box navigationTabContainer = Box.createHorizontalBox();
        navigationTabContainer.add(searchOntologiesTab);

        navigationTabContainer.add(Box.createHorizontalStrut(5));
        navigationTabContainer.add(browseRecommendedOntologiesTab);
        browseRecommendedOntologiesTab.setVisible(checkIfRecommendedOntologiesAreAvailable());

        navigationTabContainer.add(Box.createHorizontalStrut(5));
        navigationTabContainer.add(viewHistoryTab);

        searchPanelContainer.add(navigationTabContainer, BorderLayout.NORTH);

        createBrowseUI();

        // searchUIContainer will be initialised
        createSearchUI(recommendedOntologiesAvailable);
        createHistoryPanel();

        ontologyViewContainer.add(searchUIContainer);

        searchPanelContainer.add(ontologyViewContainer, BorderLayout.CENTER);

        return searchPanelContainer;
    }

    private boolean checkIfRecommendedOntologiesAreAvailable() {
        boolean recommendedOntologiesAvailable = false;
        if ((recommendedOntologies != null) &&
                recommendedOntologies.size() > 0) {
            recommendedOntologiesAvailable = true;
        }
        return recommendedOntologiesAvailable;
    }

    private void createSearchUI(boolean recommendedOntologiesAvailable) {
        searchUIContainer = new JPanel();
        searchUIContainer.setLayout(new BorderLayout());
        searchUIContainer.setBorder(new TitledBorder(UIHelper.GREEN_ROUNDED_BORDER, ""));
        searchUIContainer.setBackground(UIHelper.BG_COLOR);

        // instantiate the searchSpan OptionGroup object
        createOntologySearchSpanOptions(recommendedOntologiesAvailable);

        // create field for entering the term
        Box searchFieldCont = Box.createHorizontalBox();
        searchFieldCont.setBackground(UIHelper.BG_COLOR);

        Box searchFieldContainer = Box.createHorizontalBox();
        searchFieldContainer.add(UIHelper.createLabel("Search for: ", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));
        searchFieldContainer.add(Box.createVerticalStrut(10));
        searchFieldContainer.add(new JLabel(leftFieldIcon));

        searchField = new JTextField();
        searchField.setBorder(null);
        UIHelper.renderComponent(searchField, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        Action searchAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        };

        searchField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SEARCH");
        searchField.getActionMap().put("SEARCH", searchAction);

        searchFieldContainer.add(searchField);
        searchFieldContainer.add(new ClearFieldUtility(searchField));
        searchFieldContainer.add(new JLabel(rightFieldIcon));

        searchOntologiesButton = new JLabel(searchButton);
        searchOntologiesButton.setBackground(UIHelper.BG_COLOR);
        searchOntologiesButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                searchOntologiesButton.setIcon(searchButton);
                performSearch();
            }

            public void mouseEntered(MouseEvent event) {
                searchOntologiesButton.setIcon(searchButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                searchOntologiesButton.setIcon(searchButton);
            }
        });

        searchOntologiesButton.setToolTipText("<html><b>search</b> for term</html>");

        searchFieldContainer.add(searchOntologiesButton);

        searchFieldCont.add(searchFieldContainer, BorderLayout.NORTH);

        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        createSearchResultsTree(ui);

        JScrollPane treeScroll = new JScrollPane(ontologySearchResultsTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setBorder(new EtchedBorder());
        treeScroll.getViewport().setBackground(UIHelper.BG_COLOR);
        treeScroll.setPreferredSize(new Dimension(400, 170));
        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        ontologySearchResultsTree.addMouseListener(this);

        JPanel searchFields = new JPanel();
        searchFields.setLayout(new BoxLayout(searchFields, BoxLayout.PAGE_AXIS));
        searchFields.setBackground(UIHelper.BG_COLOR);

        searchFields.add(searchSpan);
        searchFields.add(searchFieldCont);
        searchFields.add(Box.createVerticalStrut(10));

        searchUIContainer.add(searchFields, BorderLayout.NORTH);
        searchUIContainer.add(treeScroll);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(UIHelper.BG_COLOR);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));

        ((JComponent) ontologySearchResultsTree.getFilterField()).setBorder(null);
        UIHelper.renderComponent(ontologySearchResultsTree.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        filterPanel.add(new JLabel(filterInfo));
        filterPanel.add(new JLabel(leftFieldIcon));
        filterPanel.add(ontologySearchResultsTree.getFilterField());
        filterPanel.add(new ClearFieldUtility(ontologySearchResultsTree.getFilterField()));
        filterPanel.add(new JLabel(rightFieldIcon));

        searchUIContainer.add(filterPanel, BorderLayout.SOUTH);
    }

    private void createSearchResultsTree(BasicTreeUI ui) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("result");

        ontologySearchResultsTree = new FilterableJTree<OntologySourceRefObject, OntologyTerm>();
        TreeFilterModel treeModel = new FilterableOntologyTreeModel<OntologySourceRefObject, List<OntologyTerm>>(top, ontologySearchResultsTree);

        ontologySearchResultsTree.setModel(treeModel);
        ontologySearchResultsTree.setCellRenderer(new CustomTreeRenderer());
        ontologySearchResultsTree.expandRow(0);
        ontologySearchResultsTree.expandRow(1); // expand root and first result node on acquiring result! if there is no result, no exceptions will be thrown!
        ontologySearchResultsTree.setShowsRootHandles(false);
        ontologySearchResultsTree.setUI(ui);
    }

    private void createBrowseUI() {
        browseUIContainer = new JPanel();
        browseUIContainer.setLayout(new BorderLayout());
        browseUIContainer.setBorder(new TitledBorder(UIHelper.GREEN_ROUNDED_BORDER, ""));
        browseUIContainer.setBackground(UIHelper.BG_COLOR);

        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        createBrowseRecommendedOntologyTree(ui);

        JScrollPane treeScroll = new JScrollPane(browseRecommendedOntologyTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setBorder(null);
        treeScroll.getViewport().setBackground(UIHelper.BG_COLOR);
        treeScroll.setPreferredSize(new Dimension(400, 170));
        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        browseRecommendedOntologyTree.addMouseListener(this);
        browseUIContainer.add(treeScroll, BorderLayout.CENTER);
    }

    private void createBrowseRecommendedOntologyTree(BasicTreeUI ui) {
        browseRecommendedOntologyTree = new JTree(new DefaultMutableTreeNode("Nothing to display"));
        wsOntologyTreeCreator = new WSOntologyTreeCreator(this, browseRecommendedOntologyTree);
        browseRecommendedOntologyTree.setCellRenderer(new CustomTreeRenderer());
        browseRecommendedOntologyTree.setShowsRootHandles(false);
        browseRecommendedOntologyTree.setUI(ui);
    }

    private void createOntologySearchSpanOptions(boolean recommendedOntologiesAvailable) {
        searchSpan = new OptionGroup<String>(OptionGroup.HORIZONTAL_ALIGNMENT, true);

        // if we have search resources available via the plugin mechanism, then we should encourage searching on this resource.
        if (recommendedOntologies != null) {
            if (OntologySearchPluginRegistry.areSearchResourcesAvailableForCurrentField(recommendedOntologies) || recommendedOntologies.size() > 0) {
                recommendedOntologiesAvailable = true;
            }
        }

        searchSpan.addOptionItem(RECOMMENDED_ONTOLOGIES, recommendedOntologiesAvailable, recommendedOntologiesAvailable, true);
        searchSpan.addOptionItem(ALL_ONTOLOGIES, !recommendedOntologiesAvailable, true, true);

    }

    /**
     * create the History panel which consists of a JTextField to use for the filtering operation,
     * and the JList to show the filtered history terms.
     *
     * @return - JPanel containing the required components.
     */
    private void createHistoryPanel() {
        historyUIContainer = createStandardBorderPanel(false);
        historyUIContainer.setPreferredSize(new Dimension(400, 200));

        JPanel historySelectionUI = createStandardBorderPanel(true);
        historySelectionUI.setBorder(new TitledBorder(UIHelper.GREEN_ROUNDED_BORDER, ""));
        historyList = new ExtendedJList(singleSelectionListCellRenderer);

        try {
            for (OntologyTerm h : getSortedHistory()) {
                historyList.addItem(h);
            }
        } catch (ConcurrentModificationException cme) {
            log.info("Concurrent modification of history list encountered. This should never happen!");
        }

        historyList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                System.out.println("Item selected");
                if (propertyChangeEvent.getNewValue() instanceof OntologyTerm) {
                    OntologyTerm historyTerm = (OntologyTerm) propertyChangeEvent.getNewValue();
                    if (historyTerm != null) {
                        OntologyPortal portal = OntologyUtils.getSourceOntologyPortalByVersion(historyTerm.getOntologySourceInformation().getSourceVersion());
                        if (portal == OntologyPortal.OLS) {
                            setTermDefinitionView(historyTerm);
                        } else {
                            if (bioportalClient == null) {
                                bioportalClient = new BioPortalClient();
                            }
                            Map<String, String> ontologyVersions = bioportalClient.getOntologyVersions();
                            setTermDefinitionView(historyTerm, ontologyVersions);
                        }
                        addSourceToUsedOntologies(historyTerm.getOntologySourceInformation());
                        if (multipleTermsAllowed) {
                            addToMultipleTerms(historyTerm.getUniqueId());
                        } else {
                            selectedTerm.setText(historyTerm.getUniqueId());
                        }
                    }
                }
            }
        });

        historyList.setToolTipText("click on term to select it...");

        JScrollPane historyScroll = new JScrollPane(historyList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScroll.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(historyScroll);

        UIHelper.renderComponent(historyList.getFilterField(),
                UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        historyList.getFilterField().setBorder(null);

        Box historyFilterContainer = Box.createHorizontalBox();
        historyFilterContainer.add(new JLabel(filterInfo));
        historyFilterContainer.add(new JLabel(leftFieldIcon));
        historyFilterContainer.add(historyList.getFilterField());
        historyFilterContainer.add(new ClearFieldUtility(historyList.getFilterField()));
        historyFilterContainer.add(new JLabel(rightFieldIcon));

        historySelectionUI.add(historyScroll, BorderLayout.CENTER);
        historySelectionUI.add(historyFilterContainer, BorderLayout.SOUTH);
        historyUIContainer.add(historySelectionUI);
    }

    private List<OntologyTerm> getSortedHistory() {
        List<OntologyTerm> ontologyTerms = new ArrayList<OntologyTerm>();
        ontologyTerms.addAll(OntologyManager.getOntologySelectionHistory().values());
        Collections.sort(ontologyTerms);
        return ontologyTerms;
    }

    private void setTermDefinitionView(OntologyTerm ontologyTerm, Map<String, String> ontologyVersions) {
        boolean sourceIsInPlugins = OntologySearchPluginRegistry.isOntologySourceAbbreviationDefinedInPlugins(ontologyTerm.getOntologySource());
        viewTermDefinition.setContent(
                createOntologyBranch(ontologyTerm), ontologyVersions.get(ontologyTerm.getOntologySource()), sourceIsInPlugins ? null : bioportalClient);
    }

    private OntologyBranch createOntologyBranch(OntologyTerm ontologyTerm) {
        OntologyBranch branch = new OntologyBranch(ontologyTerm.getOntologyTermAccession(), ontologyTerm.getOntologyTermName());
        branch.setComments(ontologyTerm.getComments());
        return branch;
    }

    private void setTermDefinitionView(OntologyTerm ontologyTerm) {
        boolean sourceIsInPlugins = OntologySearchPluginRegistry.isOntologySourceAbbreviationDefinedInPlugins(ontologyTerm.getOntologySource());
        viewTermDefinition.setContent(
                new OntologyBranch(ontologyTerm.getOntologyTermAccession(), ontologyTerm.getOntologyTermName()), ontologyTerm.getOntologySource(),
                sourceIsInPlugins ? null : olsClient == null ? new OLSClient() : olsClient);
    }

    private JPanel createTermDefinitionPanel() {
        JPanel container = createStandardBorderPanel(false);
        container.setPreferredSize(new Dimension(225, 200));
        container.add(new JLabel(
                termDefinitionIcon,
                JLabel.LEFT), BorderLayout.NORTH);

        JPanel termDefinitionContainer = createStandardBorderPanel(true);
        termDefinitionContainer.setBorder(new TitledBorder(UIHelper.GREEN_ROUNDED_BORDER, ""));

        viewTermDefinition = new ViewTermDefinitionUI();
        termDefinitionContainer.add(viewTermDefinition);

        container.add(termDefinitionContainer);
        return container;
    }

    /**
     * Create the panel to view the terms that have been selected. Also contains the ok and cancel buttons
     * to allow the user to select the term and close the window, or just close the window respectively.
     *
     * @return - JPanel
     */
    private JPanel createSouthPanel() {
        JPanel southPanel = createStandardBorderPanel(true);
        southPanel.setBorder(new EtchedBorder(UIHelper.BG_COLOR, UIHelper.BG_COLOR));

        JLabel selectTermLabel = UIHelper.createLabel(forceOntologySelection ? "<html>Selected term (must be an ontology term): </html>" : "<html>Selected term. (You can also enter freetext here): </html>");

        Box termSelectionContainer = Box.createHorizontalBox();
        termSelectionContainer.add(UIHelper.wrapComponentInPanel(selectTermLabel));
        termSelectionContainer.add(Box.createVerticalStrut(5));
        termSelectionContainer.add(new JLabel(leftFieldIcon));

        selectedTerm = new JTextField();
        selectedTerm.setEditable(!forceOntologySelection);
        selectedTerm.setBorder(null);
        UIHelper.renderComponent(selectedTerm, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        Action confirmAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                confirmSelection();
            }
        };

        selectedTerm.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "CONFIRM");
        selectedTerm.getActionMap().put("CONFIRM", confirmAction);

        termSelectionContainer.add(selectedTerm);
        termSelectionContainer.add(new ClearFieldUtility(selectedTerm));
        termSelectionContainer.add(new JLabel(rightFieldIcon));
        termSelectionContainer.add(Box.createVerticalStrut(5));

        confirmOkButton = new JLabel(Globals.OK_ICON);
        confirmOkButton.setOpaque(false);
        confirmOkButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                confirmOkButton.setIcon(Globals.OK_ICON);
                confirmSelection();
            }

            public void mouseEntered(MouseEvent event) {
                confirmOkButton.setIcon(Globals.OK_OVER_ICON);
            }

            public void mouseExited(MouseEvent event) {
                confirmOkButton.setIcon(Globals.OK_ICON);
            }
        });


        termSelectionContainer.add(confirmOkButton);

        southPanel.add(termSelectionContainer, BorderLayout.NORTH);

        southPanel.add(footer, BorderLayout.SOUTH);

        return southPanel;
    }

    private void confirmSelection() {

        for (OntologyTerm selectedTerm : selectedTerms) {
            addTermToHistory(selectedTerm);
        }

        firePropertyChange("selectedOntology", "OLD_VALUE", selectedTerm.getText());
        if (historyList.getSelectedIndex() != -1) {
            OntologyManager.getOntologySelectionHistory().put(historyList.getSelectedValue().toString(), (OntologyTerm) historyList.getSelectedValue());
        }
        historyList.getFilterField().setText("");
        historyList.clearSelection();
        setVisible(false);
    }


    /**
     * Creates a bordered panel, or unbordered as the case may be.
     *
     * @param hasBorder - whether the panel has a border or not.
     * @return The bordered or unbordered JPanel.
     */
    private JPanel createStandardBorderPanel(boolean hasBorder) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.BG_COLOR);

        if (hasBorder) {
            panel.setBorder(new EtchedBorder(UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR));
        } else {
            panel.setBorder(null);
        }

        return panel;
    }

    /**
     * Check to determine if the Ontology source already exists in the previously defined Ontology sources.
     *
     * @param source - Possible source to add.
     * @return Boolean - true if the source already exists, false otherwise.
     */
    private boolean checkOntologySourceRecorded(String source) {
        for (OntologySourceRefObject oRef : OntologyManager.getOntologiesUsed()) {
            if (oRef.getSourceName().equals(source)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a value to the selected terms box when multiple term selection is enabled.
     *
     * @param valToEnter - additional term to add to the selection.
     */
    private void addToMultipleTerms(String valToEnter) {
        if (!selectedTerm.getText().contains(valToEnter)) { // if the term is not already in the list of values, then add it
            if (!selectedTerm.getText().equals("")) {
                char multipleTermSeparator = ';';
                selectedTerm.setText(selectedTerm.getText() +
                        multipleTermSeparator + valToEnter);
            } else {
                selectedTerm.setText(valToEnter);
            }
        }
    }

    /**
     * Add a term which has been selected from the list of results from the OLS to the history
     * panel, and to the selected terms text field.
     *
     * @param term - the Ontology Term to be added.
     */
    private void addTerm(OntologyTerm term) {

        if (multipleTermsAllowed) {
            selectedTerms.add(term);
            addToMultipleTerms(term.getUniqueId());
        } else {
            selectedTerms.clear();
            selectedTerms.add(term);
            selectedTerm.setText(term.getUniqueId());
        }

    }

    private void addTermToHistory(OntologyTerm termInformation) {
        // add the item to the history list
        addSourceToUsedOntologies(termInformation.getOntologySourceInformation());
        OntologyManager.addToUserHistory(termInformation);
        historyList.addItem(termInformation);
    }


    private void addSourceToUsedOntologies(OntologySourceRefObject ontologySourceRefObject) {
        if (ontologySourceRefObject != null) {
            if (!checkOntologySourceRecorded(ontologySourceRefObject.getSourceName())) {
                OntologyManager.addToUsedOntologies(ontologySourceRefObject);
            }
        }
    }

    private String getRecommendedOntologyCacheIdentifier() {
        StringBuilder identifer = new StringBuilder();

        if (recommendedOntologies != null) {
            for (String ontology : recommendedOntologies.keySet()) {
                RecommendedOntology ro = recommendedOntologies.get(ontology);
                String ontologyAbbr = ro.getOntology().getOntologyAbbreviation() == null ? "" : ro.getOntology().getOntologyAbbreviation();
                identifer.append(ontologyAbbr);
            }
        }

        return identifer.toString();
    }

    private void performSearch() {

        if (olsClient == null) {
            olsClient = new OLSClient();
        }

        if (bioportalClient == null) {
            bioportalClient = new BioPortalClient();
        }

        Thread performer = new Thread(new Runnable() {
            public void run() {
                if (!searchField.getText().equals("")) {
                    try {
                        log.info("performing search");
                        progressIndicator.setSize(new Dimension(
                                getWidth(),
                                getHeight()));
                        setGlassPane(progressIndicator);
                        progressIndicator.start();
                        OntologySelectionTool.this.validate();

                        String searchOn;

                        boolean searchAllOntologies = searchSpan.getSelectedItem().equals("All Ontologies");

                        if (searchAllOntologies) {
                            searchOn = ":all:";
                        } else {
                            searchOn = ":" + getRecommendedOntologyCacheIdentifier() + ":";
                        }

                        String cacheKeyLookup = "term" + ":" + searchOn + ":" +
                                searchField.getText();

                        if (!OntologyManager.searchResultCache.containsKey(cacheKeyLookup)) {
                            result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

                            if (searchAllOntologies) {
                                searchAllOntologies();
                            } else {
                                searchSpecificOntologies();
                            }

                            // only add to the cache if we got a result!
                            if (result.size() > 0) {
                                OntologyManager.searchResultCache.addToCache(cacheKeyLookup, result);
                            }

                        } else {
                            result = OntologyManager.searchResultCache.get(cacheKeyLookup);
                        }


                        ontologySearchResultsTree.setItems(processResults());
                    } catch (Exception
                            e) {
                        log.error("Failed to connect to ontology service: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        if (progressIndicator.isStarted()) {
                            EventQueue.invokeLater(new Runnable() {
                                public void run() {
                                    progressIndicator.stop();
                                    OntologySelectionTool.this.validate();
                                }
                            });

                        }
                    }
                }
            }
        });
        performer.start();
    }

    private void searchSpecificOntologies() {
        OntologyManager.placeRecommendedOntologyInformationInRecords(recommendedOntologies.values());

        List<RecommendedOntology> olsOntologies = filterRecommendedOntologiesForService(recommendedOntologies.values(), OntologyPortal.OLS);

        if (olsOntologies.size() > 0) {
            Map<OntologySourceRefObject, List<OntologyTerm>> olsResult = olsClient.getTermsByPartialNameFromSource(searchField.getText(), olsOntologies);

            if (olsResult != null) {
                log.info("found terms in " + olsResult.size() + " ols ontologies");
                result.putAll(olsResult);
            }
        } else {
            log.info("Not searching OLS, nothing to search for in recommended ontologies.");
        }

        List<RecommendedOntology> bioportalOntologies = filterRecommendedOntologiesForService(recommendedOntologies.values(), OntologyPortal.BIOPORTAL);

        int totalResourcesSearchedOnByPluginResources = OntologySearchPluginRegistry.howManyOfTheseResourcesAreSearchedOnByPlugins(recommendedOntologies.values());

        if (bioportalOntologies.size() > 0 && totalResourcesSearchedOnByPluginResources != recommendedOntologies.size()) {
            Map<OntologySourceRefObject, List<OntologyTerm>> bioportalResult = bioportalClient.getTermsByPartialNameFromSource(searchField.getText(), bioportalOntologies);

            if (bioportalResult != null) {
                log.info("found terms in " + bioportalResult.size() + " bioportal ontologies");
                result.putAll(bioportalResult);
            }
        } else {
            log.info("Not searching Bioportal, nothing to search for in recommended ontologies.");
        }

        // by default, for now we'll assume that reading from the recommended ontologies will also
        // encompass plugged in ontology resources.
        result.putAll(OntologySearchPluginRegistry.compositeSearch(searchField.getText(), recommendedOntologies));
    }

    private void searchAllOntologies() {
        log.info("no recommended ontology specified, so searching for " + searchField.getText());

        Map<OntologySourceRefObject, List<OntologyTerm>> olsResult = olsClient.getTermsByPartialNameFromSource(searchField.getText(), null, false);

        if (olsResult != null) {
            log.info("found terms in " + olsResult.size() + " ols ontologies");
            result.putAll(olsResult);
        }

        Map<OntologySourceRefObject, List<OntologyTerm>> bioportalResult = bioportalClient.getTermsByPartialNameFromSource(searchField.getText(), "all", false);

        log.info("found terms in " + bioportalResult.size() + " bioportal ontologies");

        if (bioportalResult.size() > 0) {
            result.putAll(bioportalResult);
        }

        Map<OntologySourceRefObject, List<OntologyTerm>> pluginResults = OntologySearchPluginRegistry.compositeSearch(searchField.getText());
        if (pluginResults != null && pluginResults.size() > 0) {
            result.putAll(pluginResults);
        }

        log.info("almalgamated result is comprised of " + result.size() + " ontologies");

        OntologyManager.addOLSOntologyDefinitions(bioportalClient.getOntologyNames(), bioportalClient.getOntologyVersions());
    }

    private Map<OntologySourceRefObject, Set<OntologyTerm>> processResults() {

        SortedMap<OntologySourceRefObject, Set<OntologyTerm>> processedResult = new TreeMap<OntologySourceRefObject, Set<OntologyTerm>>();


        for (OntologySourceRefObject osro : result.keySet()) {

            if (osro != null) {
                if (!processedResult.containsKey(osro)) {
                    processedResult.put(osro, new HashSet<OntologyTerm>());
                }

                for (OntologyTerm term : result.get(osro)) {
                    processedResult.get(osro).add(term);
                }
            }
        }
        return processedResult;
    }


    /**
     * Filters out the Recommended Ontologies so that they are specific to a service (to speed things up)
     *
     * @param ontologies - RecommendedOntology list to filter
     * @param filter     - 'bioportal' to filter out those ROs for BioPortal at NCBO and 'ols' for OLS at the EBI
     * @return a filtered List of RecommendedOntology
     */
    private List<RecommendedOntology> filterRecommendedOntologiesForService(Collection<RecommendedOntology> ontologies, OntologyPortal filter) {
        List<RecommendedOntology> filteredOntologies = new ArrayList<RecommendedOntology>();
        for (RecommendedOntology ro : ontologies) {
            if (OntologyUtils.getSourceOntologyPortal(ro.getOntology()) == filter) {
                filteredOntologies.add(ro);
            }
        }

        return filteredOntologies;
    }

    /**
     * Update the history list.
     */
    public void updatehistory() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if ((historyList != null) && (OntologyManager.getOntologySelectionHistory() != null)) {

                    OntologyTerm[] newHistory = new OntologyTerm[OntologyManager.getOntologySelectionHistory().size()];

                    int count = 0;
                    for (OntologyTerm oo : getSortedHistory()) {
                        newHistory[count] = oo;
                        count++;
                    }

                    historyList.updateContents(newHistory, true);
                }
            }
        });
    }

    /**
     * Sets the selected term field to equals a given term.
     *
     * @param term - new value to be contained in the selectedTerm field.
     */
    public void setSelectedTerm(String term) {
        selectedTerm.setText(term);
    }

    /**
     * Return the term selected
     *
     * @return String.
     */
    public String getSelectedTerm() {
        if (selectedTerm != null) {
            return selectedTerm.getText();
        }
        return "";
    }

    public Set<OntologyTerm> getSelectedTerms() {
        return selectedTerms;
    }

    public void makeVisible() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
                searchField.requestFocusInWindow();
                repaint();
            }
        });

    }

    public void loadRecommendedOntologiesIfAllowed() {
        if (recommendedOntologies != null && recommendedOntologies.size() > 0) {
            resetButtons();
            browseRecommendedOntologiesTab.setIcon(browseOntologiesIconOver);
            swapContainers(browseUIContainer);
            loadTree();
        }
    }

    private void loadTree() {

        Thread loadTreeThread = new Thread(new Runnable() {
            public void run() {
                try {
                    log.info("performing search");
                    progressIndicator.setText("connecting to ontology resources");
                    progressIndicator.setSize(new Dimension(
                            getWidth(),
                            getHeight()));
                    setGlassPane(progressIndicator);
                    progressIndicator.start();

                    wsOntologyTreeCreator.createTree(recommendedOntologies);
                    treeCreated = true;

                } catch (FileNotFoundException e) {
                    log.error(e.getMessage());
                    browseRecommendedOntologyTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Unable to load recommended ontologies")));

                } finally {
                    resetButtons();
                    mode = BROWSE_MODE;
                    browseRecommendedOntologiesTab.setIcon(browseOntologiesIconOver);

                    if (progressIndicator.isStarted()) {
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                progressIndicator.stop();
                                OntologySelectionTool.this.validate();
                            }
                        });

                    }

                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            browseUIContainer.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            repaint();
                        }
                    });

                }
            }
        });
        if (!treeCreated) {
            loadTreeThread.start();
        }
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {

        if (event.getSource() instanceof JTree) {
            JTree tree = (JTree) event.getSource();

            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            if (selectedNode != null) {

                if (tree == ontologySearchResultsTree) {
                    if (selectedNode.isLeaf()) {

                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                        OntologySourceRefObject ontologySource = (OntologySourceRefObject) parentNode.getUserObject();

                        OntologyTerm ontologyTerm = (OntologyTerm) selectedNode.getUserObject();

                        if (event.getClickCount() == 1) {

                            addTerm(ontologyTerm);
                        }

                        if (OntologyUtils.getSourceOntologyPortalByVersion(ontologySource.getSourceVersion()) == OntologyPortal.BIOPORTAL) {
                            boolean sourceIsInPlugins = OntologySearchPluginRegistry.isOntologySourceAbbreviationDefinedInPlugins(ontologyTerm.getOntologySource());
                            viewTermDefinition.setContent(createOntologyBranch(ontologyTerm), ontologySource.getSourceVersion(), sourceIsInPlugins ? null : bioportalClient == null ? new BioPortalClient() : bioportalClient);
                        } else {
                            viewTermDefinition.setContent(createOntologyBranch(ontologyTerm), ontologySource.getSourceVersion(), olsClient);
                        }
                    }
                } else if (tree == browseRecommendedOntologyTree) {

                    if (selectedNode.getUserObject() instanceof OntologyTreeItem) {
                        OntologyTreeItem termNode = (OntologyTreeItem) selectedNode.getUserObject();

                        if (selectedNode.isLeaf()) {
                            if (event.getClickCount() == 1) {
                                OntologySourceRefObject ontologySourceRefObject = OntologyUtils.convertOntologyToOntologySourceReferenceObject(termNode.getOntology());
                                addTerm(OntologyUtils.convertOntologyBranchToOntologyTerm(termNode.getBranch(), ontologySourceRefObject));
                            }
                        }

                        if (OntologyUtils.getSourceOntologyPortal(termNode.getOntology()) == OntologyPortal.BIOPORTAL) {
                            viewTermDefinition.setContent(termNode.getBranch(),
                                    termNode.getOntology().getOntologyVersion(), bioportalClient == null ? new BioPortalClient() : bioportalClient);
                        } else {

                            viewTermDefinition.setContent(termNode.getBranch(),
                                    termNode.getOntology().getOntologyAbbreviation(), olsClient == null ? new OLSClient() : olsClient);
                        }
                    }
                }
            }
        }
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
        firePropertyChange("noSelectedOntology", "canceled", event.toString());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                historyList.getFilterField().setText("");
                historyList.clearSelection();
                setVisible(false);
            }
        });
    }

    private void resetButtons() {
        searchOntologiesTab.setIcon(searchOntologiesIcon);
        browseRecommendedOntologiesTab.setIcon(browseOntologiesIcon);
        viewHistoryTab.setIcon(viewHistoryIcon);
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            ontologyViewContainer.removeAll();
            ontologyViewContainer.add(newContainer);
            ontologyViewContainer.repaint();
            ontologyViewContainer.validate();
        }
    }
}
