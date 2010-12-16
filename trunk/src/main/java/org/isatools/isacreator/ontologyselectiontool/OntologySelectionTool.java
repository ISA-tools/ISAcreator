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
import org.isatools.isacreator.effects.*;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.ontologymanager.*;
import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalOntology;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.utils.OntologyURLProcessing;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OntologySelectionTool provides interface to connect to the OLS (OR bioPortal) to retrieve appropriate
 * terms for annotation of their experiment.
 *
 * @author Eamonn Maguire
 */
public class OntologySelectionTool extends JFrame implements MouseListener,
        OntologySelector, WindowListener {

    private static final Logger log = Logger.getLogger(OntologySelectionTool.class.getName());

    @InjectedResource
    private ImageIcon ontologyLookupIcon, recentHistoryIcon, termDefinitionIcon,
            searchButton, searchButtonOver, filterInfo;


    private static char MULTIPLE_TERM_SEPARATOR = ';';

    public static final int WIDTH = 800;
    public static final int HEIGHT = 400;

    private static OntologyService olsClient = null;
    private static OntologyService bioportalClient = null;
    private static InfiniteProgressPanel progressIndicator;

    private Map<String, OntologyObject> history;

    private JRadioButton useRecommendedOntologies;
    private JRadioButton useOntologies;


    private ExtendedJList historyList;
    private JComboBox searchBy;
    private JTextField searchField;
    private JTextField selectedTerm;
    private FilterableJTree ontologySearchResults;
    private Map<String, String> result = null;

    // maps an ontology id e.g. 1123 for OBI to it's version e.g. 40832
    private static Map<String, String> ontologyIdToVersion = new HashMap<String, String>();

    // We'll store recently searched terms in a cache so that multiple searches on the same term in a short period
    // of time do not result in identical queries to the OLS on each occasion. if the user searches all ontologies for
    // "mito", then the map will consist of all:mito -> result map.
    private ResultCache<String, Map<String, String>> searchResultCache;
    private Map<String, RecommendedOntology> recommendedOntologies;
    private boolean multipleTermsAllowed;
    private OntologyConsumer consumer;
    private ViewTermDefinitionUI viewTermDefinition;


    /**
     * OntologySelectionTool constructor.
     *
     * @param consumer              - The parent DataEntryEnvironment
     * @param multipleTermsAllowed  - Whether or not multiple terms are allowed to be selected.
     * @param recommendedOntologies - the recommended ontology source e.g. EFO, UO, NEWT, CHEBI.
     */
    public OntologySelectionTool(OntologyConsumer consumer,
                                 boolean multipleTermsAllowed, Map<String, RecommendedOntology> recommendedOntologies) {
        this.addWindowListener(this);


        this.recommendedOntologies = recommendedOntologies;
        this.history = consumer.getUserHistory();
        this.searchResultCache = consumer.getResultCache();
        this.consumer = consumer;
        this.multipleTermsAllowed = multipleTermsAllowed;

        ResourceInjector.get("ontologyselectiontool-package.style").inject(this);

        progressIndicator = new InfiniteProgressPanel(
                "searching for matching ontologies");
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
        createPanels();
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));
        pack();

    }

    /**
     * Create each of the required JPanels for the GUI, and add them to the appropriate section of
     * the GUI.
     */
    private void createPanels() {

        HUDTitleBar titlePanel = new HUDTitleBar(null, null, true);
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.LINE_AXIS));

        centralPanel.add(createSearchPanel());
        centralPanel.add(Box.createHorizontalStrut(5));
        centralPanel.add(createHistoryPanel());
        centralPanel.add(Box.createHorizontalStrut(5));
        centralPanel.add(createTermDefinitionPanel());

        add(centralPanel, BorderLayout.CENTER);

        add(createSouthPanel(), BorderLayout.SOUTH);
    }


    /**
     * Create the Search panel which will contain the options to use the recommended source and so forth as
     * well as the result tree which shows all the terms found.
     *
     * @return JPanel containing the Search panel components.
     */
    private JPanel createSearchPanel() {
        JPanel container = createStandardBorderPanel(false);
        container.setBackground(UIHelper.BG_COLOR);
        container.setPreferredSize(new Dimension(325, 200));
        container.add(new JLabel(
                ontologyLookupIcon,
                JLabel.LEFT), BorderLayout.NORTH);

        JPanel searchUIContainer = new JPanel();
        searchUIContainer.setLayout(new BorderLayout());
        searchUIContainer.setBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6));
        searchUIContainer.setBackground(UIHelper.BG_COLOR);

        // create source choice
        Box sourceContainer = Box.createHorizontalBox();

        useRecommendedOntologies = new JRadioButton("recommended search", true);
        UIHelper.renderComponent(useRecommendedOntologies, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        useOntologies = new JRadioButton("all ontologies", false);
        UIHelper.renderComponent(useOntologies, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        if ((recommendedOntologies == null) ||
                recommendedOntologies.size() == 0) {
            useRecommendedOntologies.setSelected(false);
            useRecommendedOntologies.setEnabled(false);
            useOntologies.setSelected(true);
        }

        ButtonGroup groupChoices = new ButtonGroup();
        groupChoices.add(useRecommendedOntologies);
        groupChoices.add(useOntologies);

        sourceContainer.add(useRecommendedOntologies);
        sourceContainer.add(Box.createVerticalStrut(10));
        sourceContainer.add(useOntologies);

        sourceContainer.add(Box.createGlue());

        // create field for entering the term
        JPanel searchFieldCont = new JPanel();
        searchFieldCont.setBackground(UIHelper.BG_COLOR);
        searchFieldCont.setLayout(new BoxLayout(searchFieldCont,
                BoxLayout.LINE_AXIS));

        Box horBox = Box.createHorizontalBox();

        String[] searchByValues = new String[]{"term", "accession"};

        searchBy = new JComboBox(searchByValues);
        UIHelper.renderComponent(searchBy, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        horBox.add(searchBy);
        horBox.add(Box.createVerticalStrut(10));

        searchField = new JTextField();
        UIHelper.renderComponent(searchField, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        Action searchAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        };

        searchField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SEARCH");
        searchField.getActionMap().put("SEARCH", searchAction);

        horBox.add(searchField);
        horBox.add(Box.createVerticalStrut(5));

        horBox.add(new ClearFieldUtility(searchField));

        final JLabel searchOntologies = new JLabel(searchButton);
        searchOntologies.setBackground(UIHelper.BG_COLOR);
        searchOntologies.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                searchOntologies.setIcon(searchButton);
                performSearch();
            }

            public void mouseEntered(MouseEvent event) {
                searchOntologies.setIcon(searchButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                searchOntologies.setIcon(searchButton);
            }
        });

        searchOntologies.setToolTipText("<html><b>search</b> for term</html>");

        horBox.add(searchOntologies);

        searchFieldCont.add(horBox, BorderLayout.NORTH);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("result");

        ontologySearchResults = new FilterableJTree();
        TreeFilterModel treeModel = new FilterableOntologyTreeModel<Contact, Set<String>>(top, ontologySearchResults);

        ontologySearchResults.setModel(treeModel);
        ontologySearchResults.setCellRenderer(new CustomTreeRenderer());
        ontologySearchResults.expandRow(0);
        ontologySearchResults.expandRow(1); // expand root and first result node on acquiring result! if there is no result, no exceptions will be thrown!
        ontologySearchResults.setShowsRootHandles(false);

        // remove standard icon used to indicate that node is open.
        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        ontologySearchResults.setUI(ui);
        UIHelper.renderComponent(ontologySearchResults, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        JScrollPane treeScroll = new JScrollPane(ontologySearchResults,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setBorder(new EtchedBorder());
        treeScroll.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        ontologySearchResults.addMouseListener(this);


        JPanel searchFields = new JPanel();
        searchFields.setLayout(new BoxLayout(searchFields, BoxLayout.PAGE_AXIS));
        searchFields.setBackground(UIHelper.BG_COLOR);

        searchFields.add(sourceContainer);
        searchFields.add(searchFieldCont);

        searchUIContainer.add(searchFields, BorderLayout.NORTH);
        searchUIContainer.add(treeScroll);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(UIHelper.BG_COLOR);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));

        UIHelper.renderComponent(ontologySearchResults.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        filterPanel.add(new JLabel(filterInfo));
        filterPanel.add(ontologySearchResults.getFilterField());
        filterPanel.add(new ClearFieldUtility(ontologySearchResults.getFilterField()));

        searchUIContainer.add(filterPanel, BorderLayout.SOUTH);

        container.add(searchUIContainer, BorderLayout.CENTER);

        return container;
    }

    /**
     * create the History panel which consists of a JTextField to use for the filtering operation,
     * and the JList to show the filtered history terms.
     *
     * @return - JPanel containing the required components.
     */
    private JPanel createHistoryPanel() {
        JPanel container = createStandardBorderPanel(false);
        container.setPreferredSize(new Dimension(225, 200));
        container.add(new JLabel(
                recentHistoryIcon,
                JLabel.LEFT), BorderLayout.NORTH);

        JPanel historySelectionList = createStandardBorderPanel(true);
        historySelectionList.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6));
        historyList = new ExtendedJList(new SingleSelectionListCellRenderer());

        try {
            for (OntologyObject h : history.values()) {
                historyList.addItem(h.getUniqueId());
            }
        } catch (ConcurrentModificationException cme) {
            log.info("Concurrent modification of history list encountered. This should never happen!");
        }

        historyList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String historyTerm = propertyChangeEvent.getNewValue().toString();

                if (historyTerm != null) {
                    String source = historyTerm.substring(0,
                            historyTerm.indexOf(":"));

                    String description = OntologySourceManager.getOntologyDescription(source);
                    addSourceToUsedOntologies(source, description);
                    if (multipleTermsAllowed) {
                        addToMultipleTerms(historyTerm);
                    } else {
                        selectedTerm.setText(historyTerm);
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

        historySelectionList.add(historyList.getFilterField(),
                BorderLayout.NORTH);
        historySelectionList.add(historyScroll, BorderLayout.CENTER);

        container.add(historySelectionList);

        return container;
    }

    private JPanel createTermDefinitionPanel() {
        JPanel container = createStandardBorderPanel(false);
        container.setPreferredSize(new Dimension(225, 200));
        container.add(new JLabel(
                termDefinitionIcon,
                JLabel.LEFT), BorderLayout.NORTH);

        JPanel termDefinitionContainer = createStandardBorderPanel(true);
        termDefinitionContainer.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6));

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

        Box termSelectionContainer = Box.createHorizontalBox();

        JLabel selectTermLabel = UIHelper.createLabel("<html><strong>term not found?</strong> just enter freetext: </html>");

        termSelectionContainer.add(UIHelper.wrapComponentInPanel(selectTermLabel));
        termSelectionContainer.add(Box.createVerticalStrut(5));

        selectedTerm = new JTextField();
        UIHelper.renderComponent(selectedTerm, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        Action confirmAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                confirmSelection();
            }
        };

        selectedTerm.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "CONFIRM");
        selectedTerm.getActionMap().put("CONFIRM", confirmAction);

        termSelectionContainer.add(selectedTerm);
        termSelectionContainer.add(Box.createVerticalStrut(5));

        final JLabel ok = new JLabel(Globals.OK_ICON);
        ok.setOpaque(false);
        ok.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                ok.setIcon(Globals.OK_ICON);
                confirmSelection();
            }

            public void mouseEntered(MouseEvent event) {
                ok.setIcon(Globals.OK_OVER_ICON);
            }

            public void mouseExited(MouseEvent event) {
                ok.setIcon(Globals.OK_ICON);
            }
        });


        termSelectionContainer.add(ok);

        southPanel.add(termSelectionContainer, BorderLayout.NORTH);

        FooterPanel footer = new FooterPanel(this);
        southPanel.add(footer, BorderLayout.SOUTH);

        return southPanel;
    }

    private void confirmSelection() {
        firePropertyChange("selectedOntology", "OLD_VALUE",
                selectedTerm.getText());
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

    private void getOntologyVersionsAndDescriptions() {
        try {
            OntologySourceManager.appendOntologyDescriptions(olsClient.getOntologyNames());
            Map<String, String> ontologyVersions = olsClient.getOntologyVersions();
            OntologySourceManager.appendOntologyVersions(ontologyVersions);

        } catch (Exception e) {
            log.error("Failed to connect to ontology service (OLS): " + e.getMessage());
        }
    }

    /**
     * Check to determine if the Ontology source already exists in the previously defined Ontology sources.
     *
     * @param source - Possible source to add.
     * @return Boolean - true if the source already exists, false otherwise.
     */
    private boolean checkOntologySourceRecorded(String source) {
        for (OntologySourceRefObject oRef : consumer.getOntologiesUsed()) {
            if (oRef.getSourceName().equals(source)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check history to determine whether or not the term already exists
     *
     * @param uniqueId the Source:Term combination to uniquely identify an Ontology term.
     * @return Boolean value represented by true if the term exists, and false otherwise.
     */
    private boolean checkHistoryForValue(String uniqueId, String accession) {

        OntologyObject oo = history.get(uniqueId);
        return oo != null && oo.getTermAccession().equals(accession);
    }

    /**
     * Add a value to the selected terms box when multiple term selection is enabled.
     *
     * @param valToEnter - additional term to add to the selection.
     */
    private void addToMultipleTerms(String valToEnter) {
        if (!selectedTerm.getText().contains(valToEnter)) { // if the term is not already in the list of values, then add it
            if (!selectedTerm.getText().equals("")) {
                selectedTerm.setText(selectedTerm.getText() +
                        MULTIPLE_TERM_SEPARATOR + valToEnter);
            } else {
                selectedTerm.setText(valToEnter);
            }
        }

    }

    /**
     * Add a term which has been selected from the list of results from the OLS to the history
     * panel, and to the selected terms text field.
     *
     * @param source - Source of term to be added.
     * @param term   - the term to be added.
     */
    private void addTerm(String source, String term) {
        String accession = extractAccession(term); // extract the accession from the term.
        String url = "";
        term = term.replaceAll(getAccessionPattern(), ""); // remove the accession placeholder in the term String

        if (source.startsWith("http://")) {
            String[] parts = source.substring(source.lastIndexOf("/") + 1)
                    .split("_");
            url = source.substring(0, source.lastIndexOf("/"));

            if (parts.length == 2) {
                source = parts[0];
                accession = parts[1];
            }
        }

        String valToEnter = source + ":" + term;

        if (multipleTermsAllowed) {
            addToMultipleTerms(valToEnter);
        } else {
            selectedTerm.setText(valToEnter);
        }

        // add ontology source to the OntologySources list if it doesn't already exist
        if (!source.trim().equals("")) {
            addSourceToUsedOntologies(source, url);
        }

        if (!checkHistoryForValue(valToEnter, accession)) {
            OntologyObject historyObject = new OntologyObject(term, accession,
                    source);

            // add the item to the history list
            history.put(historyObject.getUniqueId(), historyObject);
            historyList.addItem(valToEnter);
        }
    }

    private void addSourceToUsedOntologies(String source, String url) {
        if (!checkOntologySourceRecorded(source)) {
            consumer.addToUsedOntologies(new OntologySourceRefObject(source, url, OntologySourceManager.getOntologyVersion(source),
                    OntologySourceManager.getOntologyDescription(source)));
        }
    }

    /**
     * Take accession from the term, and then return the accession. normally, a term from the tool with have the term appended with << accession >>.
     * This accession needs to be extracted.
     *
     * @param termWithAccession - String to extract the accession from.
     * @return String
     */
    private String extractAccession(String termWithAccession) {
        Pattern pattern = Pattern.compile(getAccessionPattern());
        Matcher m = pattern.matcher(termWithAccession);
        String toReturn = "";

        if (m.find()) {
            toReturn = termWithAccession.substring(m.start(), m.end());
            toReturn = toReturn.replaceAll("(<<|>>)", "");
        }

        return toReturn;
    }

    private String getAccessionPattern() {
        return "<<[\\s]*([a-zA-Z]*[_|:]*)*[a-zA-z0-9]*[\\s]*>>";
    }

    private String getRecommendedOntologyCacheIdentifier() {
        StringBuffer identifer = new StringBuffer();

        for (String ontology : recommendedOntologies.keySet()) {
            RecommendedOntology ro = recommendedOntologies.get(ontology);
            String ontologyAbbr = ro.getOntology().getOntologyAbbreviation() == null ? "" : ro.getOntology().getOntologyAbbreviation();
            identifer.append(ontologyAbbr);
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


        final String searchByVal = searchBy.getSelectedItem().toString();
        final String olsVersion = OntologySourceManager.getOntologyVersion(OntologySourceManager.OLS_TEXT);

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

                        if (useOntologies.isSelected()) {
                            searchOn = ":all:";
                        } else {
                            searchOn = ":" + getRecommendedOntologyCacheIdentifier() + ":";
                        }

                        String cacheKeyLookup = searchByVal + ":" + searchOn + ":" +
                                searchField.getText();

                        if (!searchResultCache.containsKey(cacheKeyLookup)) {
                            result = new HashMap<String, String>();


                            if (searchByVal.equals("term")) {

                                if (useOntologies.isSelected()) {
                                    System.out.println("no recommended ontology specified, so searching for " + searchField.getText());

                                    Map<String, String> olsResult = olsClient.getTermsByPartialNameFromSource(searchField.getText(), null, false);

                                    if (olsResult != null) {
                                        result.putAll(olsResult);
                                    }

                                    Map<String, String> bioportalResult = bioportalClient.getTermsByPartialNameFromSource(searchField.getText(), "all", false);

                                    System.out.println("found " + bioportalResult.size() + " terms in bioportal");

                                    for (String accession : bioportalResult.keySet()) {
                                        System.out.println("accession: " + accession + " -> " + bioportalResult.get(accession));
                                    }

                                    if (bioportalResult.size() > 0) {
                                        result.putAll(bioportalResult);
                                    }

                                    System.out.println("almalgamated result is " + result.size() + " terms");

                                    OntologySourceManager.appendOntologyDescriptions(bioportalClient.getOntologyNames());
                                    OntologySourceManager.appendOntologyVersions(bioportalClient.getOntologyVersions());
                                } else {

                                    OntologySourceManager.placeRecommendedOntologyInformationInRecords(recommendedOntologies.values());

                                    List<RecommendedOntology> olsOntologies = filterRecommendedOntologiesForService(recommendedOntologies.values(), "ols");

                                    Map<String, String> olsResult = olsClient.getTermsByPartialNameFromSource(searchField.getText(), olsOntologies);

                                    if (olsResult != null) {
                                        System.out.println("ols result size is: " + olsResult);
                                        result.putAll(olsResult);
                                    }

                                    List<RecommendedOntology> bioportalOntologies = filterRecommendedOntologiesForService(recommendedOntologies.values(), "bioportal");

                                    System.out.println("going to search bioportal for: ");
                                    for (RecommendedOntology ro : bioportalOntologies) {
                                        System.out.println("\t" + ro.getOntology().getOntologyAbbreviation());
                                        if (ro.getBranchToSearchUnder() != null) {
                                            System.out.print(" : " + ro.getBranchToSearchUnder().getBranchName());
                                        }

                                    }

                                    Map<String, String> bioportalResult = bioportalClient.getTermsByPartialNameFromSource(searchField.getText(),
                                            bioportalOntologies);

                                    System.out.println("bioportal result size is : " + bioportalResult.size());

                                    if (bioportalResult != null) {
                                        result.putAll(bioportalResult);
                                    }
                                }

                                // only add to the cache if we got a result!
                                if (result.size() > 0) {
                                    searchResultCache.addToCache(cacheKeyLookup,
                                            result);
                                }

                            } else {
                                result = olsClient.getTermByAccessionId(searchField.getText());
                            }

                        } else {
                            result = searchResultCache.get(cacheKeyLookup);
                        }

                        if (olsVersion.trim().equals("")) {
                            getOntologyVersionsAndDescriptions();
                        }

                        reconstructTree();
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

    /**
     * Filters out the Recommended Ontologies so that they are specific to a service (to speed things up)
     *
     * @param ontologies - RecommendedOntology list to filter
     * @param filter     - 'bioportal' to filter out those ROs for BioPortal at NCBO and 'ols' for OLS at the EBI
     * @return a filtered List of RecommendedOntology
     */
    private List<RecommendedOntology> filterRecommendedOntologiesForService(Collection<RecommendedOntology> ontologies, String filter) {
        List<RecommendedOntology> filteredOntologies = new ArrayList<RecommendedOntology>();
        for (RecommendedOntology ro : ontologies) {
            if (filter.equals("ols")) {
                if (ro.getOntology().getOntologyVersion().length() > 5) {
                    filteredOntologies.add(ro);
                }
            } else {
                if (!ro.getOntology().getOntologyVersion().contains(".")) {
                    filteredOntologies.add(ro);
                }
            }
        }

        return filteredOntologies;
    }

    /**
     * Reconstructs the JTree displaying results when a the search results have returned
     */
    private void reconstructTree() {

        Map<String, Set<String>> resultMap = new HashMap<String, Set<String>>();

        for (Map.Entry<String, String> e : result.entrySet()) {
            String newKeyVal;
            String accession = "";
            String[] keyParts;

            if (e.getKey() != null) {
                if ((e.getKey().contains(":") || e.getKey().contains("_")) && !e.getKey().contains("http:")) {

                    String tempKey = e.getKey();

                    Pattern ontologySourcePattern = Pattern.compile("[a-zA-Z]*:[a-zA-Z]*[_|:]+[a-zA-Z]*");
                    Matcher ontologySourceMatcher = ontologySourcePattern.matcher(tempKey);

                    if (tempKey.contains(AcceptedOntologies.NCI_THESAURUS.getOntologyAbbreviation())) {
                        tempKey = tempKey.substring(0, tempKey.indexOf(":"));

                    } else if (ontologySourceMatcher.find()) {
                        tempKey = tempKey.replaceFirst("[a-zA-Z]*:", "");
                    }

                    String separator = (tempKey.contains(":")) ? ":" : "_";
                    keyParts = tempKey.split(separator, 2);
                    newKeyVal = keyParts[0];
                    accession = e.getKey();
                    // 	accession = keyParts[1];
                } else if (e.getKey().contains("http:")) {

                    // Some ontology accessions are URLs

                    int lastIndex = (e.getKey().lastIndexOf("/") > e.getKey()
                            .lastIndexOf("#"))
                            ? e.getKey().lastIndexOf("/") : e.getKey().lastIndexOf("#");

                    String keyAndAccession = e.getKey().substring(lastIndex + 1);
                    String[] keyAccVals = keyAndAccession.split((keyAndAccession.contains("_")) ? "_" : "/?");

                    if (keyAccVals.length > 1) {
                        newKeyVal = keyAccVals[0];
                        accession = keyAndAccession;

                        if (newKeyVal.equals("")) {
                            BioPortalOntology ontology = OntologyURLProcessing.extractonOntologyfromHierarchicalURL(e.getKey());
                            newKeyVal = ontology.getOntologySource();
                            accession = ontology.getOntologySourceAccession();
                        }

                    } else {
                        newKeyVal = keyAndAccession;
                    }
                } else {
                    // Only NEWT terms should get this far
                    newKeyVal = "NEWT";
                    accession = e.getKey();
                }


                if (!newKeyVal.equals("")) {
                    if (!resultMap.containsKey(newKeyVal)) {
                        resultMap.put(newKeyVal, new HashSet<String>());
                    }

                    resultMap.get(newKeyVal)
                            .add(e.getValue() + "<< " + accession + " >>");
                }
            }
        }

        // sort list in alphabetical order!
        List<String> keyList = new ArrayList<String>();

        for (String key : resultMap.keySet()) {
            keyList.add(key);
        }

        Collections.sort(keyList);
        // change filterable tree to work generically.
        Map<String, Set<String>> sortedAndProcessedResults = new HashMap<String, Set<String>>();

        for (String key : keyList) {
            String ontologyDesc = OntologySourceManager.getOntologyDescription(key);

            String title = key;

            if (ontologyDesc != null && !ontologyDesc.equals("")) {
                title += " - " + ontologyDesc;
            }
            Set<String> terms = resultMap.get(key);

            sortedAndProcessedResults.put(title, terms);
        }

        ontologySearchResults.setItems(sortedAndProcessedResults);
    }

    /**
     * Update the history list.
     */
    public void updatehistory() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if ((historyList != null) && (history != null)) {

                    String[] newHistory = new String[history.size()];

                    int count = 0;
                    for (OntologyObject oo : history.values()) {
                        newHistory[count] = oo.getUniqueId();
                        count++;
                    }

                    historyList.updateContents(newHistory);
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

    public void makeVisible() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
                searchField.requestFocusInWindow();
                repaint();
            }
        });

    }

    private OntologyBranch createOntologyBranchFromSelectedTerm(TreeNode selectedNode) {
        if (selectedNode != null) {
            String nodeString = selectedNode.toString();

            String termName = nodeString.substring(0, nodeString.indexOf("<"));
            String termAccession = nodeString.substring(nodeString.indexOf("<"));
            termAccession = termAccession.replaceAll("<<|>>", "").trim();

            return new OntologyBranch(termAccession, termName);
        }

        return null;
    }


    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {

        final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) ontologySearchResults.getLastSelectedPathComponent();
        if (selectedNode != null) {

            if (selectedNode.isLeaf()) {


                String source = selectedNode.getParent().toString();

                if (source.contains("-")) {
                    source = source.substring(0,
                            selectedNode.getParent().toString().indexOf("-")).trim();
                }

                String term = selectedNode.toString();

                if (event.getClickCount() == 2) {
                    addTerm(source, term);
                }

                OntologyBranch ontologyTerm = createOntologyBranchFromSelectedTerm(selectedNode);

                if (ontologyTerm != null) {
                    if (source.contains("OBI")) {
                        String ontologyVersion = "";

                        if (ontologyIdToVersion.containsKey(AcceptedOntologies.OBI.toString())) {
                            ontologyVersion = ontologyIdToVersion.get(AcceptedOntologies.OBI.toString());
                        } else {
                            String idToVersion = ((BioPortalClient) bioportalClient).getLatestOntologyVersion(AcceptedOntologies.OBI.toString());
                            if (idToVersion != null) {
                                ontologyVersion = idToVersion;
                                ontologyIdToVersion.put(AcceptedOntologies.OBI.toString(), ontologyVersion);
                            }
                        }

                        viewTermDefinition.setContent(ontologyTerm, ontologyVersion, bioportalClient == null ? new BioPortalClient() : bioportalClient);
                    } else {
                        viewTermDefinition.setContent(ontologyTerm, source, olsClient);
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

        firePropertyChange("noSelectedOntology", "canceled", "");
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                historyList.getFilterField().setText("");
                historyList.clearSelection();
                setVisible(false);
            }
        });
    }

}
