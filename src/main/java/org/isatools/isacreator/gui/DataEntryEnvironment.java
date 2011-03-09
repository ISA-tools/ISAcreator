/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an �AS IS� basis,
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

package org.isatools.isacreator.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.ontologyselectiontool.ResultCache;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isacreator.visualization.ExperimentVisualization;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;


/**
 * org.isatools.isacreator.gui.DataEntryEnvironment class contains all the fields required for entry of experimental information
 * into the system for the production of the ISATAB file.
 *
 * @author Eamonn Maguire
 */
public class DataEntryEnvironment extends DataEntryWrapper implements
        TreeSelectionListener, PropertyChangeListener {

    @InjectedResource
    private ImageIcon loading, overviewButton, overviewButtonOver, navigationPanelHeader, informationPanelHeader,
            warning_reducedFunctionality, removeStudyDialogImage, investigationHelp, studyHelp, emptySubmission;


    private DefaultMutableTreeNode overviewTreeRoot;
    private DefaultTreeModel overviewTreeModel;
    private Investigation investigation;
    private JLabel statusInfo;
    private JLabel overviewMenuIcon;

    private JTree overviewTree;
    private ISAcreator mGUI;
    private EmptySubmissionFill emptySubmissionFiller;
    private DefaultMutableTreeNode lastAddedNode = null;

    public DataEntryEnvironment(ISAcreator mGUI) {
        super();

        ResourceInjector.get("gui-package.style").inject(this);

        this.mGUI = mGUI;
        emptySubmissionFiller = new EmptySubmissionFill();
    }

    /**
     * Adds a new field to the JTree showing  an overview of the data.
     *
     * @param measurementEndpoint - measurement endpoint type for the assay being added
     * @param techType            - technology type for the assay being added
     * @param assayPlatform       - platform used
     * @param assayName           - Name of the node to be entered.
     * @return true if added, false otherwise
     */
    public boolean addAssay(String measurementEndpoint, String techType,
                            String assayPlatform, String assayName) {
        // get node
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();
        TableReferenceObject tro = mGUI.selectTROForUserSelection(measurementEndpoint,
                techType);

        if (tro != null) {
            if ((selectedNode != null) && selectedNode.getAllowsChildren() &&
                    !checkForDuplicateName(assayName, MappingObject.ASSAY_TYPE)) {
                if (assayName != null) {
                    Study s = (Study) selectedNode.getUserObject();

                    Assay newAssay = new Assay(assayName, measurementEndpoint,
                            techType, assayPlatform, s.getUserInterface(), tro);
                    DefaultMutableTreeNode newField = new DefaultMutableTreeNode(newAssay);
                    newField.setAllowsChildren(false);
                    overviewTreeModel.insertNodeInto(newField, selectedNode,
                            selectedNode.getChildCount());

                    ((Study) selectedNode.getUserObject()).addAssay(newAssay);
                    investigation.addToAssays(newAssay.getAssayReference(),
                            s.getStudyId());

                    return true;
                }
            } else {
                JOptionPane optionPane = new JOptionPane(
                        "Problem occurred when attempting to add an Assay... " +
                                "\n Please ensure assay names for a study are unique, \n and that you have entered text in the assay name field!",
                        JOptionPane.OK_OPTION);
                optionPane.addPropertyChangeListener(this);
                getParentFrame()
                        .showJDialogAsSheet(optionPane.createDialog(this,
                                "Duplicate Assay Name Detected"));
            }
        } else {
            JOptionPane optionPane = new JOptionPane(
                    "An assay definition with the features you have selected doesn't exist... " +
                            "\n Please ensure that the assay definition you have entered is correct!",
                    JOptionPane.OK_OPTION);
            optionPane.addPropertyChangeListener(this);
            getParentFrame()
                    .showJDialogAsSheet(optionPane.createDialog(this,
                            "Assay definition does not exist"));
        }

        return false;
    }

    public void setInvestigation(Investigation investigation) {
        this.investigation = investigation;
    }

    public void setOverviewIconAsBusy(boolean busy) {
        if (busy) {
            overviewMenuIcon.setIcon(loading);
        } else {
            overviewMenuIcon.setIcon(overviewButton);
        }
    }

    public boolean addStudy(String studyName) {
        TableReferenceObject tro = mGUI.selectTROForUserSelection(MappingObject.STUDY_SAMPLE);

        if (tro != null) {
            Study newStudy = new Study(studyName);
            StudyDataEntry ui = new StudyDataEntry(this, newStudy);
            newStudy.setUI(ui);

            Assay studySampleRec = new Assay("s_" + studyName + ".txt",
                    tro);
            studySampleRec.setUserInterface(ui);

            newStudy.setStudySamples(studySampleRec);

            investigation.addStudy(newStudy);

            overviewTreeModel.setRoot(buildTreeFromInvestigation(investigation));
            navigateToPath(lastAddedNode);

            return true;
        }

        return false;
    }

    /**
     * Show the addStudy dialog to the user.
     */
    public void addStudyToTree() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AddStudyDialog studyEntry = new AddStudyDialog(DataEntryEnvironment.this, "Study");
                studyEntry.createGUI();
                mGUI.showJDialogAsSheet(studyEntry);
            }
        });

    }

    /**
     * build up a tree and the associated gui from an Investigation object
     *
     * @param inv - Investigation to use to build the tree.
     * @return - DefaultMutableTreeNode. The root node with all nodes contained within it.
     */
    private DefaultMutableTreeNode buildTreeFromInvestigation(Investigation inv) {
        if (inv.getStudies().size() > 1) {
            inv.getUserInterface().update();
            overviewTreeRoot = new DefaultMutableTreeNode(inv);

            Study lastAddedStudy = null;

            for (Study s : inv.getStudies().values()) {
                s.getUserInterface().update();
                DefaultMutableTreeNode studyNode = createStudyNode(inv, s);
                overviewTreeRoot.add(studyNode);
                lastAddedStudy = s;
                lastAddedNode = studyNode;
            }

            if (lastAddedStudy != null) {
                setCurrentPage(lastAddedStudy.getUserInterface());
            }

            return overviewTreeRoot;
        } else if (inv.getStudies().size() > 0) {
            // there's only one node in any case, but it's easier to iterate around the value than to try and call it explicitly!
            for (Study s : inv.getStudies().values()) {
                overviewTreeRoot = createStudyNode(inv, s);
                setCurrentPage(s.getUserInterface());
                lastAddedNode = overviewTreeRoot;
            }

            return overviewTreeRoot;
        } else {
            overviewTreeRoot = new DefaultMutableTreeNode("No studies added yet...");
            setCurrentPage(emptySubmissionFiller);

            return overviewTreeRoot;
        }
    }

    /**
     * Check for duplicate field names before entering field into table structure file
     *
     * @param nodeName - The name of the node to be adde
     * @param type     - Either org.isatools.isacreator.gui.Study or org.isatools.isacreator.gui.Assay
     * @return true if item exists, and false otherwise
     */
    public boolean checkForDuplicateName(String nodeName, String type) {
        if (overviewTreeModel.getRoot() != null) {

            Enumeration enumerate = ((DefaultMutableTreeNode) overviewTreeModel.getRoot()).breadthFirstEnumeration();

            while (enumerate.hasMoreElements()) {

                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumerate.nextElement();

                if (type.equals("Study")) {
                    if (node.getAllowsChildren() && node.toString().equals(nodeName)) {
                        return true;
                    }
                } else {
                    if (node.toString().equals(nodeName)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    public void createGUI() {
        setSize(mGUI.getSize());
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        this.investigation = new Investigation("Investigation", "");
        setBorder(BorderFactory.createEmptyBorder());
        setupWestPanel(investigation);
        setCurrentPage(emptySubmissionFiller);
        setVisible(true);
    }

    // need to create data entry panel which has already been formed by the import layer
    // take investigation and iterate through it, adding all nodes including investigation, study, and assay nodes

    public void createGUIFromSource(Investigation inv) {
        // investigation should have all the studies, assays, etc. in place, ready to be added to the panel
        setSize(mGUI.getSize());
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        // change this!
        this.investigation = inv;
        setupWestPanel(investigation);

        // change view pane to initially show the view for whatever node is currently the top node in the tree.
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) overviewTreeModel.getRoot();

        Object nodeInfo = rootNode.getUserObject();

        if (nodeInfo instanceof Investigation) {
            setCurrentPage(((Investigation) nodeInfo).getUserInterface());
        } else if (nodeInfo instanceof Study) {
            setCurrentPage(((Study) nodeInfo).getUserInterface());
        }

        setVisible(true);
    }


    private JPanel createNavPanel(Investigation inv) {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBackground(UIHelper.DARK_GREEN_COLOR);
        navPanel.setBorder(null);

        JLabel navPanelHeader = new JLabel(navigationPanelHeader,
                JLabel.LEFT);
        navPanelHeader.setBackground(UIHelper.DARK_GREEN_COLOR);
        navPanel.add(navPanelHeader, BorderLayout.NORTH);

        if (inv.getUserInterface() == null) {
            investigation.setUserInterface(new InvestigationDataEntry(investigation, this));
        }

        overviewTreeModel = new DefaultTreeModel(buildTreeFromInvestigation(investigation));

        overviewTree = new JTree(overviewTreeModel);
        overviewTree.setAutoscrolls(true);
        overviewTree.setShowsRootHandles(false);
        overviewTree.setCellRenderer(new ISAOverviewTreeRenderer());
        overviewTree.addTreeSelectionListener(this);
        overviewTree.setSelectionRow(0);

        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        overviewTree.setUI(ui);

        JScrollPane treeScroll = new JScrollPane(overviewTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        treeScroll.setPreferredSize(new Dimension(210, 400));
        treeScroll.setBorder(new EtchedBorder(1, UIHelper.BG_COLOR, UIHelper.BG_COLOR));

        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        navPanel.add(treeScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(UIHelper.BG_COLOR);


        //buttonPanel.add(Box.createHorizontalStrut(5));
        overviewMenuIcon = new JLabel(overviewButton);
        overviewMenuIcon.setOpaque(false);
        overviewMenuIcon.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                ExperimentVisualization expViz = new ExperimentVisualization(investigation);
                expViz.createGUI();

                expViz.addPropertyChangeListener("reducedFunctionality", new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

                        JOptionPane optionPane = new JOptionPane("<html><b>Reduced functionality enforced</b>" +
                                "<p>Due to the large number of assays and the restrictions of the visualization used in ISAcreator,</p>" +
                                "<p><b>reduced</b> the <b>functionality</b> in this visualization to improve performance and avoid any memory problems!</p>" +
                                "<p>The next release of <b>ISAcreator</b> will contain new ways to visualize data which don't impact on the tools performance!</html>",
                                JOptionPane.OK_OPTION);
                        UIHelper.renderComponent(optionPane, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
                        optionPane.setIcon(warning_reducedFunctionality);
                        optionPane.addPropertyChangeListener(this);
                        getParentFrame()
                                .showJDialogAsSheet(optionPane.createDialog(DataEntryEnvironment.this,
                                        "Reduced Functionality"));
                    }
                });

                setCurrentPage(expViz);
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
                overviewMenuIcon.setIcon(overviewButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                overviewMenuIcon.setIcon(overviewButton);
            }
        });

        buttonPanel.add(overviewMenuIcon, BorderLayout.EAST);

        navPanel.add(buttonPanel, BorderLayout.SOUTH);

        return navPanel;
    }

    private DefaultMutableTreeNode createStudyNode(Investigation inv, Study s) {
        if (s.getUserInterface() == null) {

            s.setUI(new StudyDataEntry(this, s));
        }

        DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(s);
        studyNode.add(new DefaultMutableTreeNode(s.getStudySample()));

        for (Assay a : s.getAssays().values()) {
            inv.addToAssays(a.getAssayReference(), s.getStudyId());
            studyNode.add(new DefaultMutableTreeNode(a));
        }

        return studyNode;
    }


    public Investigation getInvestigation() {
        return investigation;
    }

    public List<OntologySourceRefObject> getOntologySources() {
        return mGUI.getOntologiesUsed();
    }

    public ISAcreator getParentFrame() {
        return mGUI;
    }

    public ResultCache<String, Map<String, String>> getResultCache() {
        return mGUI.getResultCache();
    }


    public Map<String, OntologyObject> getUserHistory() {
        return mGUI.getUserHistory();
    }

    private void navigateToPath(DefaultMutableTreeNode nodeToGo) {
        TreeNode[] treeNodes = overviewTreeModel.getPathToRoot(nodeToGo);
        TreePath pathToFollow = new TreePath(treeNodes);
        overviewTree.scrollPathToVisible(pathToFollow);
        overviewTree.setSelectionPath(pathToFollow);
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
            getParentFrame().hideSheet();
        }
    }

    /**
     * Removes a node from tree
     *
     * @param elementName - Name of node to be removed
     */
    public void removeFromTree(String elementName) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();

        Enumeration enumerate = selectedNode.breadthFirstEnumeration();

        while (enumerate.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumerate.nextElement();

            if (node.toString().equals(elementName)) {
                overviewTreeModel.removeNodeFromParent(node);

                break;
            }
        }
    }

    /**
     * Remove a study from the tree, and the investigation.
     */
    public void removeStudy() {
        JOptionPane confirmStudyRemoval = new JOptionPane(
                "<html><b>Remove study?</b>" +
                        "<p>Are you sure you want to remove this study? You will NOT be able</p>" +
                        "<p>to undo this operation!!</html>", JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.YES_NO_OPTION);

        confirmStudyRemoval.setIcon(removeStudyDialogImage);
        UIHelper.renderComponent(confirmStudyRemoval, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
        UIHelper.applyOptionPaneBackground(confirmStudyRemoval, UIHelper.BG_COLOR);

        confirmStudyRemoval.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                if (event.getPropertyName()
                        .equals(JOptionPane.VALUE_PROPERTY)) {
                    int answer = Integer.valueOf(event.getNewValue()
                            .toString());

                    if (answer == JOptionPane.OK_OPTION) {
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();

                        if ((selectedNode != null) &&
                                selectedNode.getUserObject() instanceof Study) {

                            Study toRemove = (Study) selectedNode.getUserObject();

                            investigation.getStudies()
                                    .remove(toRemove.getStudyId());

                            overviewTreeModel.setRoot(buildTreeFromInvestigation(
                                    investigation));
                            navigateToPath((DefaultMutableTreeNode) overviewTreeModel.getRoot());
                        }

                        getParentFrame().hideSheet();
                    } else {
                        getParentFrame().hideSheet();
                    }
                }
            }
        });
        getParentFrame()
                .showJDialogAsSheet(confirmStudyRemoval.createDialog(this,
                        "Confirm study removal"));
    }


    public void setStatusPaneInfo(String info) {
        if (info != null && statusInfo != null) {
            statusInfo.setIcon(null);
            statusInfo.setText(info);
            statusInfo.setHorizontalAlignment(JLabel.LEFT);
        }
    }

    public void setStatusPaneInfo(Icon icon) {
        if (icon != null && statusInfo != null) {
            statusInfo.setIcon(icon);
            statusInfo.setHorizontalAlignment(JLabel.CENTER);
            statusInfo.setText("");
        }
    }

    /**
     * Setup the navigation panel
     *
     * @param inv - The investigation to be added. Required to instantiate upper level of investigation.
     */
    private void setupWestPanel(Investigation inv) {
        JPanel westPanel = new JPanel(new BorderLayout());
        westPanel.setBackground(UIHelper.BG_COLOR);
        westPanel.setBorder(BorderFactory.createEmptyBorder());

        // setup status pane
        JPanel statusPane = new JPanel(new BorderLayout());
        statusPane.setPreferredSize(new Dimension(200, 200));
        statusPane.setBackground(UIHelper.DARK_GREEN_COLOR);
        statusPane.setBorder(BorderFactory.createEmptyBorder());

        JLabel infoPanelHeader = new JLabel(informationPanelHeader,
                JLabel.LEFT);
        infoPanelHeader.setBackground(UIHelper.DARK_GREEN_COLOR);

        statusPane.add(infoPanelHeader, BorderLayout.NORTH);

        statusInfo = new JLabel();
        statusInfo.setVerticalAlignment(JLabel.TOP);
        UIHelper.renderComponent(statusInfo, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);
        statusInfo.setPreferredSize(new Dimension(175, 160));

        // setup tree
        westPanel.add(createNavPanel(inv), BorderLayout.CENTER);

        JScrollPane scroller = new JScrollPane(statusInfo,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroller.setPreferredSize(new Dimension(180, 170));
        scroller.setBackground(UIHelper.BG_COLOR);
        scroller.getViewport().setBackground(UIHelper.BG_COLOR);
        scroller.setBorder(new EmptyBorder(1, 1, 1, 1));

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        statusPane.add(scroller);

        westPanel.add(statusPane, BorderLayout.SOUTH);

        add(westPanel, BorderLayout.WEST);
    }

    public void valueChanged(TreeSelectionEvent event) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();

        if (selectedNode == null) {
            return;
        }

        final Component curDataEntry = currentPage;

        Object nodeInfo = selectedNode.getUserObject();

        if (nodeInfo instanceof Investigation) {
            setCurrentPage(((Investigation) nodeInfo).getUserInterface());
            setStatusPaneInfo(investigationHelp);
        } else if (nodeInfo instanceof Study) {
            setCurrentPage(((Study) nodeInfo).getUserInterface());
            setStatusPaneInfo(studyHelp);
        } else if (nodeInfo instanceof Assay) {
            setCurrentPage(((Assay) nodeInfo).getSpreadsheetUI());
            setStatusPaneInfo("");
        } else {
            setStatusPaneInfo("");
            setCurrentPage(emptySubmissionFiller);
        }

        if (curDataEntry instanceof StudyDataEntry) {

            final StudyDataEntry curStudyEntry = (StudyDataEntry) curDataEntry;

            ((StudyDataEntry) curDataEntry).updateFactorsAndProtocols();

            Map<String, String[]> termsToBeReplaced = curStudyEntry.getStudy().getTermsToBeReplaced();

            if (needToSubstitute(termsToBeReplaced)) {
                Map<String, String[]> termsToBeReplacedWith = curStudyEntry.getStudy().getTermsToReplaceWith();

                final TermSubstitutionGUI tsgui = new TermSubstitutionGUI(termsToBeReplaced, termsToBeReplacedWith);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        tsgui.createGUI();
                        mGUI.setGlassPanelContents(tsgui);
                    }
                });

                tsgui.addPropertyChangeListener("substitutionComplete", new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        Map<String, Map<String, String>> result = (Map<String, Map<String, String>>) event.getNewValue();

                        for (String key : result.keySet()) {

                            Map<String, String> replacements = result.get(key);
                            for (String replace : replacements.keySet()) {
                                String replaceWithWhat = replacements.get(replace);
                                if (!replaceWithWhat.equals(TermSubstitutionGUI.DO_NOT_REPLACE_TEXT)) {
                                    if (key.equals(Study.FACTOR_IDENT)) {

                                        curStudyEntry.getStudy().replaceFactors(replace, replaceWithWhat);
                                    } else if (key.equals(Study.PROTOCOL_IDENT)) {

                                        curStudyEntry.getStudy().replaceProtocols(replace, replaceWithWhat);

                                    }
                                } else {
                                    if (key.equals(Study.FACTOR_IDENT)) {
                                        curStudyEntry.getStudy().removeFactor(replace);
                                    } else {
                                        //replace the protocols with a blank one :o)
                                        curStudyEntry.getStudy().replaceProtocols(replace, "");

                                    }
                                }
                            }
                        }
                        curStudyEntry.getStudy().clearTermReplacementHistory();
                        mGUI.hideGlassPane();
                    }
                });
            }
        }


    }

    private boolean needToSubstitute(Map<String, String[]> terms) {
        for (String category : terms.keySet()) {
            String[] toCheck = terms.get(category);
            if (toCheck != null && toCheck.length > 0) {
                return true;
            }
        }

        return false;
    }

    class EmptySubmissionFill extends JLayeredPane {
        public EmptySubmissionFill() {
            setLayout(new BorderLayout());
            setBackground(UIHelper.BG_COLOR);
            setBorder(BorderFactory.createLineBorder(UIHelper.LIGHT_GREEN_COLOR));

            JPanel labelContainer = new JPanel(new GridLayout(1, 1));
            labelContainer.setBackground(UIHelper.BG_COLOR);

            JLabel image = new JLabel(
                    emptySubmission);
            image.setOpaque(false);

            labelContainer.add(image);

            add(labelContainer, BorderLayout.CENTER);
            setVisible(true);
        }

        public String toString() {
            return "Empty Submission";
        }
    }

    public void removeReferences() {
        System.out.println("REMOVING REFERENCES");
        System.out.println(investigation);

        if (investigation != null) {
            investigation.getUserInterface().removeReferences();
            investigation = null;
        }


    }

}
