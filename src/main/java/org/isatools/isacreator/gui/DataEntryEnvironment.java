/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
<<<<<<< HEAD
 �The contents of this file are subject to the CPAL version 1.0 (the �License�);
=======
 The contents of this file are subject to the CPAL version 1.0 (the License);
>>>>>>> 9eb576c237c7bdc3ffbf61636aa76f3cae3d7fb9
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

<<<<<<< HEAD
 Software distributed under the License is distributed on an �AS IS� basis,
=======
 Software distributed under the License is distributed on an AS IS basis,
>>>>>>> 9eb576c237c7bdc3ffbf61636aa76f3cae3d7fb9
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
import org.isatools.isacreator.api.utils.SpreadsheetUtils;
import org.isatools.isacreator.api.utils.StudyUtils;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.help.Controller;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.utils.datastructures.CollectionUtils;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * org.isatools.isacreator.gui.DataEntryEnvironment class contains all the fields required for entry of experimental information
 * into the system for the production of the ISATAB file.
 *
 * @author Eamonn Maguire
 */
public class DataEntryEnvironment extends AbstractDataEntryEnvironment implements
        TreeSelectionListener {

    @InjectedResource
    private ImageIcon loading, visualizationIcon, visualizationIconOver, addStudyIcon, addStudyIconOver, removeStudyIcon,
            removeStudyIconOver, removeStudyIconInactive, navigationPanelHeader, informationPanelHeader,
            warning_reducedFunctionality, removeStudyDialogImage, investigationHelp, studyHelp;

    private DefaultMutableTreeNode overviewTreeRoot;
    private DefaultTreeModel overviewTreeModel;
    private Investigation investigation;
    private JLabel statusInfo, visualization, removeStudyButton, addStudyButton;
    private JTree overviewTree;

    private JPanel navigationPanel;

    private Controller newSubmission;
    private DefaultMutableTreeNode lastAddedNode = null;
    private DefaultMutableTreeNode selectedNode;

    public DataEntryEnvironment() {
        super();

        ResourceInjector.get("gui-package.style").inject(this);

        newSubmission = new Controller();
        newSubmission.createGUI();
        newSubmission.addPropertyChangeListener("addNewStudy", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                addStudyToTree();
            }
        });
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
    public Assay addAssay(String measurementEndpoint, String techType,
                          String assayPlatform, String assayName) {
        // get node
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();
        TableReferenceObject tro = ConfigurationManager.selectTROForUserSelection(measurementEndpoint,
                techType);

        if (tro != null) {
            if ((selectedNode != null) && selectedNode.getAllowsChildren() &&
                    !checkForDuplicateName(assayName, MappingObject.ASSAY_TYPE)) {
                if (assayName != null) {
                    Study s = (Study) selectedNode.getUserObject();

                    DefaultMutableTreeNode entryPoint = locateStudySampleNode(selectedNode);

                    Assay newAssay = new Assay(assayName, measurementEndpoint,
                            techType, assayPlatform, tro);
                    DefaultMutableTreeNode newField = new DefaultMutableTreeNode(newAssay);
                    newField.setAllowsChildren(false);

                    overviewTreeModel.insertNodeInto(newField, entryPoint == null ? selectedNode : entryPoint,
                            entryPoint == null ? selectedNode.getChildCount() : entryPoint.getChildCount());

                    ((Study) selectedNode.getUserObject()).addAssay(newAssay);
                    ApplicationManager.assignDataEntryToISASection(newAssay, ApplicationManager.getUserInterfaceForAssay(newAssay,
                            (StudyDataEntry) ApplicationManager.getUserInterfaceForISASection(s)));

                    investigation.addToAssays(newAssay.getAssayReference(),
                            s.getStudyId());

                    overviewTree.expandPath(new TreePath(entryPoint == null ? selectedNode.getPath() : entryPoint.getPath()));

                    return newAssay;
                }
            } else {
                JOptionPane optionPane = new JOptionPane(
                        "Problem occurred when attempting to add an Assay... " +
                                "\n Please ensure assay names for a study are unique, \n and that you have entered text in the assay name field!",
                        JOptionPane.OK_OPTION);
                optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        getParentFrame().hideSheet();
                    }
                });
                getParentFrame().showJDialogAsSheet(optionPane.createDialog(ApplicationManager.getCurrentApplicationInstance(),
                        "Duplicate Assay Name Detected"));
            }
        } else {
            JOptionPane optionPane = new JOptionPane(
                    "An assay definition with the features you have selected doesn't exist... " +
                            "\n Please ensure that the assay definition you have entered is correct!",
                    JOptionPane.OK_OPTION);
            optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    getParentFrame().hideSheet();
                }
            });
            getParentFrame()
                    .showJDialogAsSheet(optionPane.createDialog(ApplicationManager.getCurrentApplicationInstance(),
                            "Assay definition does not exist"));
        }

        return null;
    }

    public void selectAssayInTree(Assay assay) {

        if (currentPage instanceof StudyDataEntry) {
            System.out.println("Current page is instance of StudyDataEntry...");
            StudyUtils.studySampleFileModified(((StudyDataEntry) currentPage).getStudy(), true);
        }


        setCurrentPage(ApplicationManager.getUserInterfaceForISASection(assay));
        DefaultMutableTreeNode node = locateNodeWithName((DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent(), assay.getAssayReference());
        overviewTree.setSelectionPath(new TreePath(node.getPath()));
    }

    private DefaultMutableTreeNode locateStudySampleNode(DefaultMutableTreeNode studyNode) {

        Enumeration enumeration = studyNode.children();

        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode candidateNode = (DefaultMutableTreeNode) enumeration.nextElement();

            if (candidateNode.getUserObject() instanceof Assay) {
                Assay assay = (Assay) candidateNode.getUserObject();

                if (assay.getTechnologyType().equals("") && assay.getMeasurementEndpoint().equals("")) {
                    return candidateNode;
                }
            }
        }

        return null;
    }

    private DefaultMutableTreeNode locateNodeWithName(DefaultMutableTreeNode node, String nodeName) {

        Enumeration enumeration = node.children();
        while (enumeration.hasMoreElements()) {
            DefaultMutableTreeNode candidateNode = (DefaultMutableTreeNode) enumeration.nextElement();

            if (candidateNode.isLeaf()) {
                if (candidateNode.toString().equals(nodeName)) {
                    return candidateNode;
                }
            } else {
                return locateNodeWithName(candidateNode, nodeName);
            }

        }
        return null;
    }

    public void setInvestigation(Investigation investigation) {
        this.investigation = investigation;
    }

    public void setOverviewIconAsBusy(boolean busy) {
        if (busy) {
            visualization.setIcon(loading);
        } else {
            visualization.setIcon(visualizationIcon);
        }
    }

    public boolean addStudy(String studyName) {
        TableReferenceObject tro = ConfigurationManager.selectTROForUserSelection(MappingObject.STUDY_SAMPLE);

        if (tro != null) {
            Study newStudy = new Study(studyName);
            StudyDataEntry ui = new StudyDataEntry(this, newStudy);

            ApplicationManager.assignDataEntryToISASection(newStudy, ui);

            Assay studySampleRec = new Assay("s_" + studyName + ".txt",
                    tro);

            ApplicationManager.assignDataEntryToISASection(studySampleRec, new AssaySpreadsheet(ui, tro));

            newStudy.setSampleFileName(studySampleRec.getAssayReference());
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
                getParentFrame().showJDialogAsSheet(studyEntry);
            }
        });

    }

    /**
     * build up a tree and the associated gui from an Investigation object
     *
     * @param investigation - Investigation to use to build the tree.
     * @return - DefaultMutableTreeNode. The root node with all nodes contained within it.
     */
    private DefaultMutableTreeNode buildTreeFromInvestigation(Investigation investigation) {
        boolean alwaysShowInvestigation = Boolean.valueOf(ISAcreatorProperties.getProperty("alwaysShowInvestigation"));
        System.out.println("Should I always show the investigation? " + alwaysShowInvestigation);
        if (investigation.getStudies().size() > 1 || alwaysShowInvestigation) {

            ApplicationManager.getUserInterfaceForISASection(investigation).update();
            overviewTreeRoot = new DefaultMutableTreeNode(investigation);

            Study lastAddedStudy = null;

            for (Study s : investigation.getStudies().values()) {
                ApplicationManager.getUserInterfaceForISASection(s).update();
                DefaultMutableTreeNode studyNode = createStudyNode(investigation, s);
                overviewTreeRoot.add(studyNode);
                lastAddedStudy = s;
                lastAddedNode = studyNode;
            }

            if (lastAddedStudy != null) {
                setCurrentPage(ApplicationManager.getUserInterfaceForISASection(lastAddedStudy));
            }

            return overviewTreeRoot;
        } else if (investigation.getStudies().size() > 0) {
            // there's only one node in any case, but it's easier to iterate around the value than to try and call it explicitly!
            for (Study s : investigation.getStudies().values()) {
                overviewTreeRoot = createStudyNode(investigation, s);
                setCurrentPage(ApplicationManager.getUserInterfaceForISASection(s));
                lastAddedNode = overviewTreeRoot;
            }

            return overviewTreeRoot;
        } else {
            overviewTreeRoot = new DefaultMutableTreeNode("No studies added yet...");
            setCurrentPage(newSubmission);

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

    public static boolean isValidName(String text)
    {
        Pattern pattern = Pattern.compile(
                "# Match a valid Windows filename (unspecified file system).          \n" +
                        "^                                # Anchor to start of string.        \n" +
                        "(?!                              # Assert filename is not: CON, PRN, \n" +
                        "  (?:                            # AUX, NUL, COM1, COM2, COM3, COM4, \n" +
                        "    CON|PRN|AUX|NUL|             # COM5, COM6, COM7, COM8, COM9,     \n" +
                        "    COM[1-9]|LPT[1-9]            # LPT1, LPT2, LPT3, LPT4, LPT5,     \n" +
                        "  )                              # LPT6, LPT7, LPT8, and LPT9...     \n" +
                        "  (?:\\.[^.]*)?                  # followed by optional extension    \n" +
                        "  $                              # and end of string                 \n" +
                        ")                                # End negative lookahead assertion. \n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F]*     # Zero or more valid filename chars.\n" +
                        "[^<>:\"/\\\\|?*\\x00-\\x1F\\ .]  # Last char is not a space or dot.  \n" +
                        "$                                # Anchor to end of string.            ",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.COMMENTS);
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public void createGUI() {
        setSize(ApplicationManager.getCurrentApplicationInstance().getSize());
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        this.investigation = new Investigation("Investigation", "");
        setBorder(BorderFactory.createEmptyBorder());
        setupWestPanel(investigation);
        setCurrentPage(newSubmission);
        setVisible(true);
    }

    // need to create data entry panel which has already been formed by the import layer
    // take investigation and iterate through it, adding all nodes including investigation, study, and assay nodes

    public void createGUIFromInvestigation(Investigation newInvestigation) {
        // investigation should have all the studies, assays, etc. in place, ready to be added to the panel
        setSize(ApplicationManager.getCurrentApplicationInstance().getSize());
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        this.investigation = newInvestigation;
        setupWestPanel(investigation);

        // change view pane to initially show the view for whatever node is currently the top node in the tree.
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) overviewTreeModel.getRoot();

        Object nodeInfo = rootNode.getUserObject();

        if (nodeInfo instanceof Investigation) {
            setCurrentPage(ApplicationManager.getUserInterfaceForISASection((Investigation) nodeInfo));
        } else if (nodeInfo instanceof Study) {
            setCurrentPage(ApplicationManager.getUserInterfaceForISASection((Study) nodeInfo));
        }
        setVisible(true);
    }


    private JPanel createNavPanel(Investigation investigation) {
        navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(UIHelper.DARK_GREEN_COLOR);
        navigationPanel.setBorder(null);

        JLabel navPanelHeader = new JLabel(navigationPanelHeader,
                JLabel.LEFT);
        navPanelHeader.setBackground(UIHelper.DARK_GREEN_COLOR);
        navigationPanel.add(navPanelHeader, BorderLayout.NORTH);


        if (ApplicationManager.getUserInterfaceForISASection(investigation) == null) {
            ApplicationManager.assignDataEntryToISASection(investigation,
                    new InvestigationDataEntry(this.investigation, DataEntryEnvironment.this));
        }


        overviewTreeModel = new DefaultTreeModel(buildTreeFromInvestigation(this.investigation));

        overviewTree = new JTree(overviewTreeModel);
        overviewTree.setAutoscrolls(true);
        overviewTree.setShowsRootHandles(false);
        overviewTree.setCellRenderer(new ISAOverviewTreeRenderer());
        overviewTree.addTreeSelectionListener(this);

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

        navigationPanel.add(treeScroll, BorderLayout.CENTER);

        Box buttonBox = Box.createHorizontalBox();
        buttonBox.setOpaque(true);
        buttonBox.setBackground(UIHelper.BG_COLOR);

        addStudyButton = new JLabel(addStudyIcon);
        addStudyButton.setToolTipText("<html>Add a new Study</html>");
        addStudyButton.setOpaque(false);

        addStudyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addStudyButton.setIcon(addStudyIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addStudyButton.setIcon(addStudyIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                addStudyButton.setIcon(addStudyIcon);
                closeEditors();
                addStudyToTree();
            }
        });

        removeStudyButton = new JLabel(removeStudyIconInactive);
        removeStudyButton.setToolTipText("<html>Remove the selected study</html>");
        removeStudyButton.setOpaque(false);

        removeStudyButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (removeStudyButton.getIcon() != removeStudyIconInactive) {
                    removeStudyButton.setIcon(removeStudyIconOver);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (removeStudyButton.getIcon() != removeStudyIconInactive) {
                    removeStudyButton.setIcon(removeStudyIcon);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeEditors();
                if (removeStudyButton.getIcon() != removeStudyIconInactive) {
                    removeStudyButton.setIcon(removeStudyIcon);
                    removeStudy();
                }
            }
        });

        visualization = new JLabel(visualizationIcon);
        visualization.setToolTipText("<html>Visualize the submission</html>");
        visualization.setOpaque(false);
        visualization.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                closeEditors();

                ExperimentVisualization expViz = new ExperimentVisualization(DataEntryEnvironment.this.investigation);
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
                        optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                                getParentFrame().hideSheet();
                            }
                        });
                        getParentFrame().showJDialogAsSheet(optionPane.createDialog(ApplicationManager.getCurrentApplicationInstance(), "Reduced Functionality"));
                    }
                });

                setCurrentPage(expViz);
            }

            public void mouseEntered(MouseEvent event) {
                visualization.setIcon(visualizationIconOver);
            }

            public void mouseExited(MouseEvent event) {
                visualization.setIcon(visualizationIcon);
            }
        });

        buttonBox.add(Box.createHorizontalStrut(125));
        buttonBox.add(addStudyButton);
        buttonBox.add(removeStudyButton);
        buttonBox.add(visualization);

        navigationPanel.add(buttonBox, BorderLayout.SOUTH);

        overviewTree.setSelectionRow(0);

        return navigationPanel;
    }

    private DefaultMutableTreeNode createStudyNode(Investigation inv, Study study) {
        if (ApplicationManager.getUserInterfaceForISASection(study) == null) {
            ApplicationManager.assignDataEntryToISASection(study, new StudyDataEntry(DataEntryEnvironment.this, study));
        }

        DefaultMutableTreeNode studyNode = new DefaultMutableTreeNode(study);
        DefaultMutableTreeNode studySampleNode = new DefaultMutableTreeNode(study.getStudySample());

        for (Assay a : study.getAssays().values()) {
            inv.addToAssays(a.getAssayReference(), study.getStudyId());
            studySampleNode.add(new DefaultMutableTreeNode(a));
        }

        studyNode.add(studySampleNode);

        return studyNode;
    }


    public Investigation getInvestigation() {
        return investigation;
    }

    public List<OntologySourceRefObject> getOntologySources() {
        return OntologyManager.getOntologiesUsed();
    }

    public ISAcreator getParentFrame() {
        return ApplicationManager.getCurrentApplicationInstance();
    }

    private void navigateToPath(DefaultMutableTreeNode nodeToGo) {
        TreeNode[] treeNodes = overviewTreeModel.getPathToRoot(nodeToGo);
        TreePath pathToFollow = new TreePath(treeNodes);
        overviewTree.scrollPathToVisible(pathToFollow);
        overviewTree.setSelectionPath(pathToFollow);
    }


    /**
     * Removes a node from tree
     *
     * @param elementName - Name of node to be removed
     */
    public void removeFromTree(String elementName) {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();

        String altElementName = "a_" + elementName + ".txt";

        Enumeration enumerate = selectedNode.breadthFirstEnumeration();

        while (enumerate.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumerate.nextElement();

            if (node.toString().equals(elementName) || node.toString().equals(altElementName)) {

                overviewTreeModel.removeNodeFromParent(node);

                break;
            }
        }


        overviewTree.repaint();
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
                        removeStudyFromTree();
                        getParentFrame().hideSheet();
                    } else {
                        getParentFrame().hideSheet();
                    }
                }
            }
        });
        getParentFrame().showJDialogAsSheet(confirmStudyRemoval.createDialog(ApplicationManager.getCurrentApplicationInstance(), "Confirm study removal"));
    }

    private void removeStudyFromTree() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();

        if ((selectedNode != null) && selectedNode.getUserObject() instanceof Study) {
            Study toRemove = (Study) selectedNode.getUserObject();
            investigation.getStudies().remove(toRemove.getStudyId());
            overviewTreeModel.setRoot(buildTreeFromInvestigation(investigation));
            navigateToPath((DefaultMutableTreeNode) overviewTreeModel.getRoot());
        }
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
     * @param investigation - The investigation to be added. Required to instantiate upper level of investigation.
     */
    private void setupWestPanel(Investigation investigation) {
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
        westPanel.add(createNavPanel(investigation), BorderLayout.CENTER);

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

    public DefaultMutableTreeNode getSelectedNodeInOverviewTree() {
        return selectedNode;
    }

    public void valueChanged(TreeSelectionEvent event) {
        if (overviewTree != null) {
            selectedNode = (DefaultMutableTreeNode) overviewTree.getLastSelectedPathComponent();
        }

        if (selectedNode == null) {
            return;
        }

        final Component curDataEntry = currentPage;

        Object nodeInfo = selectedNode.getUserObject();

        removeStudyButton.setIcon(removeStudyIconInactive);

        // close all cell editors that may be in view.
        closeEditors();

        // if we are coming away from the Study form or study sample file, we want to set a flag noting that the info
        // may have changed.
        if (currentPage instanceof StudyDataEntry) {
            StudyUtils.studySampleFileModified(((StudyDataEntry) currentPage).getStudy().getStudyId());
        }

        if (nodeInfo instanceof Investigation) {
            setCurrentPage(ApplicationManager.getUserInterfaceForISASection((Investigation) nodeInfo));
            setStatusPaneInfo(investigationHelp);
        } else if (nodeInfo instanceof Study) {
            setCurrentPage(ApplicationManager.getUserInterfaceForISASection((Study) nodeInfo));
            setStatusPaneInfo(studyHelp);
            removeStudyButton.setIcon(removeStudyIcon);
            // expand underlying nodes
            overviewTree.expandPath(new TreePath(selectedNode.getNextNode().getPath()));
        } else if (nodeInfo instanceof Assay) {
            Assay assay = (Assay) nodeInfo;

            if (currentPage instanceof AssaySpreadsheet) {
                Spreadsheet spreadsheet = ((AssaySpreadsheet) currentPage).getSpreadsheet();
                if (spreadsheet.getSpreadsheetTitle().contains("Sample Definition")) {
                    StudyUtils.studySampleFileModified(getParentStudy(selectedNode), true);
                }
            }
            setCurrentPage(ApplicationManager.getUserInterfaceForISASection(assay));

            setStatusPaneInfo("");
        } else {
            setStatusPaneInfo("");
            setCurrentPage(newSubmission);
        }

        ApplicationManager.setScreenInView(nodeInfo);

        if (curDataEntry instanceof StudyDataEntry) {

            final StudyDataEntry curStudyEntry = (StudyDataEntry) curDataEntry;

            ((StudyDataEntry) curDataEntry).updateFactorsAndProtocols();

            Map<String, String[]> termsToBeReplaced = curStudyEntry.getStudy().getTermsToBeReplaced();

            if (needToSubstitute(termsToBeReplaced)) {
                Map<String, String[]> termsToBeReplacedWith = curStudyEntry.getStudy().getTermsToReplaceWith();

                final TermSubstitutionGUI termSubstitutionGUI = new TermSubstitutionGUI(termsToBeReplaced, termsToBeReplacedWith);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        termSubstitutionGUI.createGUI();
                        getParentFrame().setGlassPanelContents(termSubstitutionGUI);
                    }
                });

                termSubstitutionGUI.addPropertyChangeListener("substitutionComplete", new PropertyChangeListener() {
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
                        getParentFrame().hideGlassPane();
                    }
                });
            }
        }
    }

    private Study getParentStudy(DefaultMutableTreeNode node) {
        if (((DefaultMutableTreeNode) node.getParent()).getUserObject() instanceof Study) {
            return (Study) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
        } else {
            return getParentStudy((DefaultMutableTreeNode) node.getParent());
        }
    }

    private boolean needToSubstitute(Map<String, String[]> terms) {

        for (String category : terms.keySet()) {
            String[] toCheck = terms.get(category);
            if (toCheck != null && toCheck.length > 0) {
                return !CollectionUtils.isNullRecord(toCheck);
            }
        }
        return false;
    }

    public void closeEditors() {
        if (currentPage instanceof AssaySpreadsheet) {
            Spreadsheet spreadsheet = ((AssaySpreadsheet) currentPage).getSpreadsheet();
            SpreadsheetUtils.stopCellEditingInTable(spreadsheet.getTable());
        }
    }

    public void removeReferences() {
        System.out.println("REMOVING REFERENCES");
        System.out.println(investigation);

        removeAll();

        if (investigation != null) {
            System.out.println("Removing everything from memory");
            ApplicationManager.clearUserInterfaceAssignments();
            investigation = null;
            currentPage = null;
            newSubmission = null;
            overviewTree.getParent().removeAll();
            overviewTree.removeTreeSelectionListener(this);
            overviewTree.setUI(null);
            navigationPanel.removeAll();
            navigationPanel = null;
            overviewTree = null;
            overviewTreeRoot = null;
            overviewTreeModel.setRoot(null);
            overviewTreeModel = null;
        }
    }

}
