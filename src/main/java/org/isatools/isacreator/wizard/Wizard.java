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

package org.isatools.isacreator.wizard;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofiltercombo.AutoFilterComboCellEditor;
import org.isatools.isacreator.common.HistoryComponent;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedFormattedTextField;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.*;
import org.isatools.isacreator.gui.formelements.AssaySubForm;
import org.isatools.isacreator.gui.formelements.FactorSubForm;
import org.isatools.isacreator.gui.formelements.FieldTypes;
import org.isatools.isacreator.gui.formelements.SubFormField;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.utils.WorkingScreen;
import org.isatools.isacreator.visualization.ExperimentVisualization;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

/**
 * DataCreationWizardGUI provides the User interface for users to answer questions about their experiment so that
 * we are able to automatically generate as much data as possible for them
 *
 * @author Eamonn Maguire
 */
public class Wizard extends AbstractDataEntryEnvironment {

    private static FinaliseStudyCreationListener finaliseStudy;

    @InjectedResource
    private ImageIcon logo, defineStudyInfo, selectTreatmentHeader, defineStudyHeader, defineInvestigationHeader, overviewHeader,
            completedHeader, completedInfo, breadcrumb1, breadcrumb2, breadcrumb3, breadcrumb5, breadcrumb6;

    // define subset of study fields to get some information about a study before creating it
    private JTextField studyId, studyTitle;
    private JTextArea studyDescription;

    // define fields to describe an investigation
    private JTextField invTitle, invSubmission, invPubReleaseDate, organism;
    private JTextArea invDescription;
    private JFormattedTextField numTreatmentGroups;
    private JFormattedTextField numSamplesPerGroup;
    private ISAcreatorMenu mainMenu;
    private Investigation investigationDefinition;
    private int numberStudiesToDefine = -1;
    private AssaySubForm assayDefinitionSubForm;
    private FactorSubForm factorSubForm;
    private DataEntryEnvironment dep;
    private JPanel investigationDefinitionPanel;
    private JPanel fillerPanel;
    private JLabel status;
    private Study studyBeingEdited = null;
    private Map<Integer, String> studyTreatmentGroups = null;
    private Set<String> tmpVals;
    private List<PropertyType> factorsToAdd;
    private AddAssayPane addAssayUI;
    private Stack<HistoryComponent> previousPage;
    private Component initialPane;
    private UserProfile currentUser;

    private WorkingScreen workingProgressScreen;
    private Component lastPage;


    public Wizard(final ISAcreatorMenu mainMenu) {
        super();
        ResourceInjector.get("wizard-package.style").inject(this);

        currentUser = mainMenu.getMain().getCurrentUser();
        this.mainMenu = mainMenu;

        dep = new DataEntryEnvironment();
        setDataEntryEnvironment(dep);
        this.mainMenu.getMain().setCurDataEntryPanel(dep);

    }

    public void createGUI() {
        tmpVals = new HashSet<String>();
        status = new JLabel();
        previousPage = new Stack<HistoryComponent>();

        workingProgressScreen = new WorkingScreen();
        workingProgressScreen.createGUI();

        mainMenu.getMain().hideGlassPane();

        createWestPanel(logo, defineStudyInfo);

        createSouthPanel();
        setOpaque(false);

        finaliseStudy = new FinaliseStudyCreationListener();
        initialPane = createInvestigationDefinitionPanel();
    }

    public void changeView() {
        setCurrentPage(initialPane);
    }

    private boolean assayDefinitionRequired() {
        return studyBeingEdited.getAssays().size() > 0;
    }

    private String checkForIncorrectAssayDefinition(String[][] data) {
        for (String[] aData : data) {
            if ((aData[0] != null) && !aData[0].trim().equals("") && (aData[1] != null)) {
                String techType = aData[1].equals("--Blank--") ? "" : aData[1];
                TableReferenceObject tro = dep.getParentFrame().selectTROForUserSelection(aData[0],
                        techType);
                if (tro == null) {
                    String techTypeText = " and technology type <i>" + aData[1] + "</i> does not exist!</html>";
                    if (aData[1].trim().equals("")) {
                        techTypeText = " and a technology does not exist!</html>";
                    }
                    return "<html>The assay with measurement type <i>" + aData[0] + "</i>" + techTypeText;
                }
            }
        }

        return null;
    }

    private String checkForDuplicateAssays(String[][] data) {
        // if there are no duplicates names within the assay definition for the current study, then proceed
        if (!checkForDuplicates(data, 2)) {
            Set<String> assays = new HashSet<String>();

            // making assumption that the assays currently in the list have already been checked!
            for (Study s : investigationDefinition.getStudies().values()) {
                for (Assay a : s.getAssays().values()) {
                    assays.add(a.getAssayReference());
                }
            }

            //tmpVals list contains the column data checked in the checkForDuplicates method
            //@see checkForDuplicates(String[][] data, int col)
            for (String currentStudyAssay : tmpVals) {
                String modifiedAssayName = currentStudyAssay;

                if (!modifiedAssayName.startsWith("a_")) {
                    modifiedAssayName = "a_" + modifiedAssayName;

                    if (!modifiedAssayName.endsWith(".txt")) {
                        modifiedAssayName += ".txt";
                    }
                }

                if (assays.contains(modifiedAssayName)) {
                    return "<html>assay with name <i>" + currentStudyAssay +
                            "</i> already exists in another study.</html>";
                }
            }
        } else {
            return "<html>duplicate assay names detected in this study definition!</html>";
        }

        return null;
    }

    /**
     * Checks a 2D array to see if there are any values in a column which are the same
     *
     * @param data - the 2D String array to be checked
     * @param col  - the column to be checked
     * @return true if a duplicate exists, false otherwise
     */
    private boolean checkForDuplicates(String[][] data, int col) {
        tmpVals = new HashSet<String>();

        for (String[] aData : data) {
            if ((aData[col] != null) && !aData[col].trim().equals("")) {
                if (!tmpVals.contains(aData[col])) {
                    tmpVals.add(aData[col]);
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Create the panel which contains the fields required to gather information about the STUDY GROUPS
     *
     * @return JPanel containing all the required fields
     */
    private JPanel createAssayFactorsAndGroupsPanel() {
        final JPanel studyFactorGroupsPanel = new JPanel();
        studyFactorGroupsPanel.setLayout(new BoxLayout(studyFactorGroupsPanel,
                BoxLayout.PAGE_AXIS));
        studyFactorGroupsPanel.setBackground(UIHelper.BG_COLOR);

        // create organism list panel
        JPanel organismListPanel = createFieldPanel(1, 3);

        JLabel organismLab = UIHelper.createLabel("Organism used *");


        organism = new RoundedJTextField(10);
        UIHelper.renderComponent(organism, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        organism.setPreferredSize(new Dimension(60, 21));
        organism.setToolTipText(
                "<html><b>Organism</b><p>Please select the organism the study is based on using the ontology selection tool!</p></html>");

        organismListPanel.add(organismLab);
        organismListPanel.add(organism);

        Map<String, RecommendedOntology> recommendedOntologyMap = new HashMap<String, RecommendedOntology>();

        recommendedOntologyMap.put("NCBI Taxonomy",
                new RecommendedOntology(new Ontology("1132", "38802", "NCBITaxon", "NCBI organismal classification")));

        recommendedOntologyMap.put("NEWT",
                new RecommendedOntology(new Ontology("", "Jun 2011", "NEWT", "NEWT UniProt Taxonomy Database")));

        organismListPanel.add(createOntologyDropDown("Organism used *", organism, false, false, recommendedOntologyMap));

        // create number of treatment groups field
        JPanel numTreatmentGroupsPanel = createFieldPanel(1, 2);
        numTreatmentGroupsPanel.setBackground(UIHelper.BG_COLOR);

        JLabel numTreatmentGroupslabel = UIHelper.createLabel(
                "How many study/treatment groups? *");

        numTreatmentGroups = new RoundedFormattedTextField(NumberFormat.getInstance());
        UIHelper.renderComponent(numTreatmentGroups, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        numTreatmentGroups.setToolTipText(
                "<html><b>Number of study/treatment groups</b> <p>Please specify the number of study/treatment groups defined in this study.</p></html>");

        numTreatmentGroupsPanel.add(numTreatmentGroupslabel);
        numTreatmentGroupsPanel.add(numTreatmentGroups);

        JPanel numSubjectsPanel = createFieldPanel(1, 2);
        numSubjectsPanel.setBackground(UIHelper.BG_COLOR);

        JLabel numSubjectsLabel = UIHelper.createLabel(
                "How many subjects per study/treatment group? *");
        numSubjectsLabel.setBackground(UIHelper.BG_COLOR);

        numSamplesPerGroup = new RoundedFormattedTextField(NumberFormat.getInstance());
        UIHelper.renderComponent(numSamplesPerGroup, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        numSamplesPerGroup.setToolTipText(
                "<html><b>Number of samples per study group</b><p>Please specify the number of samples per study/treatment group.</p></html>");

        numSubjectsPanel.add(numSubjectsLabel);
        numSubjectsPanel.add(numSamplesPerGroup);


        // create study factors subform
        studyFactorGroupsPanel.add(organismListPanel);
        studyFactorGroupsPanel.add(Box.createVerticalStrut(5));
        studyFactorGroupsPanel.add(numTreatmentGroupsPanel);
        studyFactorGroupsPanel.add(Box.createVerticalStrut(5));
        studyFactorGroupsPanel.add(numSubjectsPanel);
        studyFactorGroupsPanel.add(Box.createVerticalStrut(5));
        studyFactorGroupsPanel.add(createStudyFactorsSubForm());
        studyFactorGroupsPanel.add(Box.createVerticalStrut(5));

        return studyFactorGroupsPanel;
    }

    private void createData(HistoryComponent hc) {
        if (processFactorSubform() && processAssaysSubform()) {
            Collection<String> groups = studyTreatmentGroups.values();

            if (studyId.getText().trim().equals("")) {
                status.setText("<html>please enter a study id</html>");
                studyId.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                studyId.requestFocus();
                setCurrentPage(lastPage);
                return;
            } else {
                studyId.setBackground(UIHelper.BG_COLOR);
            }

            if (studyTitle.getText().trim().equals("")) {
                status.setText("<html>please enter a study title</html>");
                studyTitle.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                studyTitle.requestFocus();
                setCurrentPage(lastPage);
                return;
            } else {
                studyTitle.setBackground(UIHelper.BG_COLOR);
            }

            if (studyDescription.getText().trim().equals("")) {
                status.setText("<html>please enter a study description</html>");
                studyDescription.requestFocus();
                setCurrentPage(lastPage);
                return;
            }

            if (organism.getText() == null || organism.getText().trim().equals("")) {

                status.setText("<html>please enter a valid organism</html>");
                organism.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                organism.requestFocus();
                setCurrentPage(lastPage);
                return;
            } else {
                organism.setBackground(UIHelper.BG_COLOR);
            }

            if (studyBeingEdited.getAssays().size() == 0) {
                status.setText("<html>no <b>assays</b> have been defined!</html>");
                setCurrentPage(lastPage);
                return;
            }

            if (!numTreatmentGroups.getText().trim().equals("")) {
                if ((Integer.valueOf(numTreatmentGroups.getText()) > 0)) {
                    if (Integer.valueOf(numTreatmentGroups.getText()) > groups.size()) {
                        status.setText(
                                "<html>the factors and respective levels could not create the number of " +
                                        "factors that you have have specified in the no. treatment groups field.</html>");
                        numTreatmentGroups.requestFocus();
                        numTreatmentGroups.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                        setCurrentPage(lastPage);
                        return;
                    } else {
                        numTreatmentGroups.setBackground(UIHelper.BG_COLOR);
                    }
                } else {
                    status.setText(
                            "<html>invalid value for the number of treatment groups. value must be numeric and greater than 0!</html>");
                    numTreatmentGroups.requestFocus();
                    numTreatmentGroups.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                    setCurrentPage(lastPage);
                    return;
                }
            } else {
                status.setText(
                        "<html>please enter a value for the number of treatment groups. value must be numeric and greater than 0!</html>");
                numTreatmentGroups.requestFocus();
                numTreatmentGroups.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                setCurrentPage(lastPage);
                return;
            }

            if (!numSamplesPerGroup.getText().trim().equals("")) {
                if ((Integer.valueOf(numSamplesPerGroup.getText()) > 0)) {
                    numSamplesPerGroup.setBackground(UIHelper.BG_COLOR);

                } else {
                    status.setText(
                            "<html>invalid value for the number of samples per group. value must be numeric and greater than 0!</html>");
                    numSamplesPerGroup.requestFocus();
                    numSamplesPerGroup.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                    setCurrentPage(lastPage);

                    return;
                }
            } else {
                status.setText(
                        "<html>please enter a value for the number of samples per group. value must be numeric and greater than 0!</html>");
                numSamplesPerGroup.requestFocus();
                numSamplesPerGroup.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                setCurrentPage(lastPage);
                return;
            }

            final TreatmentGroupSelector selectTreatmentGroups = new TreatmentGroupSelector(this, Integer.valueOf(
                    numTreatmentGroups.getText()), groups, numSamplesPerGroup.getText());

            final JLayeredPane finalPane = getGeneralLayout(selectTreatmentHeader, breadcrumb3, "", selectTreatmentGroups, selectTreatmentGroups.getHeight());

            selectTreatmentGroups.addPropertyChangeListener("treatmentGroupsSelected",
                    new PropertyChangeListener() {
                        public void propertyChange(
                                PropertyChangeEvent event) {


                            Map<Integer, TreatmentReplicate> refinedGroups;
                            if (event.getNewValue() != null) {

                                refinedGroups = (Map<Integer, TreatmentReplicate>) event.getNewValue();
                            } else {

                                refinedGroups = new HashMap<Integer, TreatmentReplicate>();
                            }

                            previousPage.push(new HistoryComponent(finalPane, selectTreatmentGroups.getListeners()));

                            showAddAssayPane(refinedGroups);
                        }
                    });

            selectTreatmentGroups.addPropertyChangeListener("treatmentGroupsNotSelected",
                    new PropertyChangeListener() {
                        public void propertyChange(
                                PropertyChangeEvent event) {
                            showPreviousPage(previousPage.pop());
                        }
                    });

            status.setText("");

            previousPage.push(hc);
            setCurrentPage(finalPane);
        } else {
            setCurrentPage(lastPage);
        }
    }

    /**
     * Create panel to define a study and its assays.
     *
     * @param studyRef - The study to be defined
     * @return JPanel containing the study definition.
     */
    public JLayeredPane createDefineStudyPanel(String studyRef) {

        JPanel finalPanel = new JPanel();

        finalPanel.setLayout(new BoxLayout(finalPanel,
                BoxLayout.PAGE_AXIS));
        finalPanel.setOpaque(false);
        finalPanel.setSize(new Dimension(600, 600));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setOpaque(false);

        container.add(createStudyDetailsPanel());

        container.add(createAssayFactorsAndGroupsPanel());

        container.add(createStudyAssaysSubForm());

        JScrollPane containerScroller = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        containerScroller.setPreferredSize(new Dimension(600, 550));
        containerScroller.setBorder(null);
        containerScroller.setOpaque(false);
        containerScroller.getViewport().setOpaque(false);

        IAppWidgetFactory.makeIAppScrollPane(containerScroller);

        finalPanel.add(containerScroller);

        status.setFont(UIHelper.VER_12_BOLD);
        status.setForeground(UIHelper.RED_COLOR);

        backButton.setOpaque(false);

        final int numberOfStudies = investigationDefinition.getStudies().size();

        final MouseListener[] listeners = new MouseListener[2];

        if (numberOfStudies > 0) {
            backButton.setIcon(wizard);
        }

        JPanel statusPanel = createFieldPanel(1, 1);
        statusPanel.add(status);

        finalPanel.add(statusPanel);

        final JLayeredPane finalPane = getGeneralLayout(defineStudyHeader, breadcrumb2, studyRef, finalPanel, getHeight());

        listeners[0] = new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                if (!previousPage.isEmpty()) {
                    // remove the previously added study from the investigation
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (investigationDefinition.getStudies().size() > 0) {

                                mainMenu.getMain().setCurrentPage(mainMenu);
                                mainMenu.getMain().setGlassPanelContents(mainMenu.getCreateISAMenuGUI());
                                mainMenu.startAnimation();

                            } else {
                                showPreviousPage(previousPage.pop());
                            }
                        }
                    });

                }
            }

            public void mouseEntered(MouseEvent event) {
                if (numberOfStudies > 0) {
                    backButton.setIcon(wizardOver);
                } else {
                    backButton.setIcon(backOver);
                }
            }

            public void mouseExited(MouseEvent event) {
                if (numberOfStudies > 0) {
                    backButton.setIcon(wizard);
                } else {
                    backButton.setIcon(back);
                }
            }
        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                Thread performStudyCreation = new Thread(new Runnable() {
                    public void run() {
                        studyBeingEdited = new Study(studyId.getText(),
                                studyTitle.getText(), "", "",
                                studyDescription.getText(),
                                "s_" + studyId.getText() + ".txt");

                        try {
                            boolean showExitWizard = numberOfStudies > 0;
                            lastPage = currentPage;
                            createData(new HistoryComponent(finalPane, listeners, showExitWizard));
                            numTreatmentGroups.setBackground(UIHelper.BG_COLOR);
                        } catch (NumberFormatException nfe) {
                            nfe.printStackTrace();
                            status.setText(
                                    "<html>" + nfe.toString() + "</html>");
                            numTreatmentGroups.requestFocus();
                            numTreatmentGroups.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                        }
                    }
                });

                setCurrentPage(workingProgressScreen);
                performStudyCreation.start();


            }

            public void mouseEntered(MouseEvent event) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent event) {
                nextButton.setIcon(next);
            }
        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;
    }

    private void showPreviousPage(HistoryComponent hc) {
        assignListenerToLabel(backButton, hc.getListeners()[0]);
        assignListenerToLabel(nextButton, hc.getListeners()[1]);
        setCurrentPage(hc.getDisplayComponent());
        if (hc.isForceExitOnBack()) {
            backButton.setIcon(wizard);
            backButton.revalidate();
        } else {
            backButton.setIcon(back);
            backButton.revalidate();
        }
    }

    /**
     * method creates panel to capture the number of studies, and if required, a definition of the investigation
     *
     * @return JPanel containing fields needed for definition of the investigation
     */
    public JLayeredPane createInvestigationDefinitionPanel() {

        JPanel investigationDefPane = new JPanel();
        investigationDefPane.setLayout(new BoxLayout(investigationDefPane, BoxLayout.PAGE_AXIS));
        investigationDefPane.setSize(new Dimension(400, 400));
        investigationDefPane.setOpaque(false);

        // define investigation panel
        investigationDefinitionPanel = new JPanel();
        investigationDefPane.setSize(new Dimension(300, 150));
        investigationDefinitionPanel.setLayout(new BoxLayout(
                investigationDefinitionPanel, BoxLayout.PAGE_AXIS));
        investigationDefinitionPanel.setOpaque(false);
        investigationDefinitionPanel.setVisible(false);
        investigationDefinitionPanel.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8),
                "investigation details", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.DARK_GREEN_COLOR));

        // create field asking users how many studies they are carrying out
        JPanel howManyStudiesPanel = new JPanel(new FlowLayout());

        final JLabel howManyStudiesLab = UIHelper.createLabel(
                "<html><p>how many studies?</p></html>");
        howManyStudiesLab.setSize(new Dimension(200, 25));

        final JFormattedTextField howManyStudiesVal = new RoundedFormattedTextField(NumberFormat.getNumberInstance());
        howManyStudiesVal.setPreferredSize(new Dimension(250, 25));
        howManyStudiesVal.setOpaque(false);

        UIHelper.renderComponent(howManyStudiesVal, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        final FormattedTextFieldVerifier formatChecker = new FormattedTextFieldVerifier();
        howManyStudiesVal.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent event) {
                if (formatChecker.verify(howManyStudiesVal)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showHideInvestigationDefPanel(Integer.valueOf(
                                    howManyStudiesVal.getText()));
                        }
                    });
                }
            }

            public void removeUpdate(DocumentEvent event) {
                if (formatChecker.verify(howManyStudiesVal)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showHideInvestigationDefPanel(Integer.valueOf(
                                    howManyStudiesVal.getText()));
                        }
                    });
                }
            }

            public void changedUpdate(DocumentEvent event) {
                if (formatChecker.verify(howManyStudiesVal)) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            showHideInvestigationDefPanel(Integer.valueOf(
                                    howManyStudiesVal.getText()));
                        }
                    });
                }
            }
        });

        howManyStudiesPanel.add(howManyStudiesLab);
        howManyStudiesPanel.add(howManyStudiesVal);

        investigationDefPane.add(howManyStudiesPanel);
        investigationDefPane.add(Box.createVerticalStrut(10));

        // define investigation title field
        JPanel invTitleContainer = createFieldPanel(1, 2);

        JLabel invTitleLab = UIHelper.createLabel("investigation title: *");
        invTitleLab.setHorizontalAlignment(SwingConstants.LEFT);
        invTitleLab.setVerticalAlignment(SwingConstants.TOP);
        invTitleLab.setPreferredSize(new Dimension(175, 25));

        invTitle = new RoundedJTextField(20);
        invTitle.setSize(new Dimension(250, 25));
        UIHelper.renderComponent(invTitle, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        invTitleContainer.add(invTitleLab);
        invTitleContainer.add(createTextEditEnabledField(invTitle));

        investigationDefinitionPanel.add(invTitleContainer);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));

        // define investigation description field
        JPanel invDescPanel = createFieldPanel(1, 2);

        JLabel invDescLab = UIHelper.createLabel("investigation description: *");
        invDescLab.setPreferredSize(new Dimension(175, 25));
        invDescLab.setVerticalAlignment(SwingConstants.TOP);

        invDescription = new JTextArea();

        UIHelper.renderComponent(invDescription, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        invDescription.setLineWrap(true);
        invDescription.setWrapStyleWord(true);
        invDescription.setBackground(UIHelper.BG_COLOR);
        invDescription.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8));

        JScrollPane invDescScroll = new JScrollPane(invDescription,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        invDescScroll.setBorder(UIHelper.STD_ETCHED_BORDER);
        invDescScroll.setSize(new Dimension(250, 50));
        invDescScroll.getViewport().setBackground(UIHelper.BG_COLOR);

        invDescPanel.add(invDescLab);
        invDescPanel.add(UIHelper.createTextEditEnableJTextArea(invDescScroll, invDescription));

        IAppWidgetFactory.makeIAppScrollPane(invDescScroll);

        investigationDefinitionPanel.add(invDescPanel);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));

        // define investigation date of submission
        JPanel invSubmissionPanel = createFieldPanel(1, 2);

        JLabel invSubmissionLab = UIHelper.createLabel(
                "date of investigation submission:  ");
        invSubmissionLab.setSize(new Dimension(150, 25));

        invSubmission = new RoundedJTextField(17);
        UIHelper.renderComponent(invSubmission, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        invSubmission.setSize(new Dimension(250, 25));

        invSubmissionPanel.add(invSubmissionLab);
        invSubmissionPanel.add(createDateDropDown(invSubmission));

        investigationDefinitionPanel.add(invSubmissionPanel);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));

        // define investigation public release date
        JPanel invPubReleasePanel = createFieldPanel(1, 2);

        JLabel invPubReleaseLab = UIHelper.createLabel(
                "investigation public release date:  ");
        invPubReleaseLab.setSize(new Dimension(175, 25));

        invPubReleaseDate = new RoundedJTextField(17);
        UIHelper.renderComponent(invPubReleaseDate, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        invPubReleaseDate.setOpaque(false);
        invPubReleaseDate.setSize(new Dimension(250, 25));

        invPubReleasePanel.add(invPubReleaseLab);

        JComponent dropDownPR = createDateDropDown(invPubReleaseDate);
        dropDownPR.setSize(new Dimension(250, 25));

        invPubReleasePanel.add(dropDownPR);

        investigationDefinitionPanel.add(invPubReleasePanel);
        investigationDefinitionPanel.add(Box.createVerticalStrut(5));
        JPanel statusPanel = createFieldPanel(1, 1);

        final JLabel status = new JLabel();
        status.setHorizontalAlignment(JLabel.CENTER);
        status.setForeground(UIHelper.RED_COLOR);

        statusPanel.add(status);

        investigationDefinitionPanel.add(Box.createVerticalStrut(10));
        investigationDefinitionPanel.add(statusPanel);

        investigationDefPane.add(investigationDefinitionPanel);

        fillerPanel = new JPanel();
        fillerPanel.setLayout(new BoxLayout(fillerPanel, BoxLayout.PAGE_AXIS));
        fillerPanel.setOpaque(false);

        fillerPanel.add(Box.createVerticalStrut(182));

        fillerPanel.setVisible(true);

        investigationDefPane.add(fillerPanel);

        investigationDefPane.add(Box.createVerticalStrut(225));

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                // go back to the create isatab menu
                mainMenu.changeView(mainMenu.getCreateISAMenuGUI());
                mainMenu.getMain().setCurrentPage(mainMenu);
                mainMenu.startAnimation();
            }

            public void mouseEntered(MouseEvent event) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent event) {
                backButton.setIcon(back);
            }
        };

        final JLayeredPane finalPane = getGeneralLayout(defineInvestigationHeader, breadcrumb1, "", investigationDefPane, getHeight());

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (!howManyStudiesVal.getText().equals("") && formatChecker.verify(howManyStudiesVal)) {
                    if (Integer.valueOf(howManyStudiesVal.getText()) > 1) {
                        // check to ensure that required investigation fields have been entered.
                        if (!invTitle.getText().trim().equals("")) {
                            invTitle.setBackground(UIHelper.BG_COLOR);
                            if (!invDescription.getText().trim()
                                    .equals("")) {
                                // create new investigation and change view to first study to be defined.


                                Thread performLoad = new Thread(new Runnable() {
                                    public void run() {
                                        investigationDefinition = new Investigation("",
                                                invTitle.getText(),
                                                invDescription.getText(),
                                                invSubmission.getText(),
                                                invPubReleaseDate.getText());

                                        investigationDefinition.setLastConfigurationUsed(mainMenu.getMain().getLoadedConfiguration());
                                        investigationDefinition.setConfigurationCreateWith(mainMenu.getMain().getLoadedConfiguration());

                                        numberStudiesToDefine = Integer.valueOf(howManyStudiesVal.getText());

                                        dep.setInvestigation(investigationDefinition);

                                        previousPage.push(new HistoryComponent(finalPane, listeners));
                                        nextButton.setIcon(next);

                                        setCurrentPage(createDefineStudyPanel("study 1 of " + howManyStudiesVal.getText()));
                                    }
                                });

                                setCurrentPage(workingProgressScreen);
                                performLoad.start();
                            } else {
                                status.setText(
                                        "<html><p>the <b>investigation description</b> is missing. this is a required field!</p></html>");
                                invDescription.requestFocus();
                                revalidate();
                            }

                        } else {
                            status.setText(
                                    "<html><p>the <b>investigation title</b> is missing. this is a required field!</p></html>");
                            invTitle.requestFocus();
                            invTitle.setBackground(UIHelper.TRANSPARENT_RED_COLOR);
                            revalidate();
                        }
                    } else {
                        if (Integer.valueOf(howManyStudiesVal.getText()) > 0) {
                            // create new investigation and change view to first study to be defined.
                            Thread performLoad = new Thread(new Runnable() {
                                public void run() {
                                    investigationDefinition = new Investigation("Inv_001",
                                            "Investigation", "", "", "");
                                    dep.setInvestigation(investigationDefinition);
                                    previousPage.push(new HistoryComponent(finalPane, listeners));

                                    status.setText("");
                                    System.out.println("going to next page");
                                    setCurrentPage(createDefineStudyPanel(
                                            "study definition"));
                                }
                            });
                            setCurrentPage(workingProgressScreen);
                            performLoad.start();
                        }
                    }
                    numberStudiesToDefine = Integer.valueOf(howManyStudiesVal.getText());
                }
            }

            public void mouseEntered(MouseEvent event) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent event) {
                nextButton.setIcon(next);
            }
        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;
    }


    private JPanel createStudyAssaysSubForm() {
        List<SubFormField> assayFields = new ArrayList<SubFormField>();

        assayFields.add(new SubFormField("measurement type *",
                SubFormField.COMBOLIST, mainMenu.getMain().getMeasurementEndpoints()));
        assayFields.add(new SubFormField("technology type *",
                SubFormField.COMBOLIST, mainMenu.getMain().getTechnologyTypes()));
        assayFields.add(new SubFormField("assay platform", SubFormField.STRING));
        assayFields.add(new SubFormField("assay file name *",
                SubFormField.STRING));

        assayDefinitionSubForm = new AssaySubForm("assays", FieldTypes.ASSAY, assayFields, 3,
                400, 110, getDataEntryEnvironment());

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                assayDefinitionSubForm.createGUI();
            }
        });

        return assayDefinitionSubForm;
    }

    /**
     * Create the details panel for the study to capture essential information such as the study id, title and description.
     *
     * @return JPanel containing the sutdy fields
     */
    private JPanel createStudyDetailsPanel() {
        JPanel studyDetailsPanel = new JPanel();
        studyDetailsPanel.setLayout(new BoxLayout(studyDetailsPanel,
                BoxLayout.PAGE_AXIS));
        studyDetailsPanel.setOpaque(false);

        // create panel for the study id field
        JPanel studyIDPanel = createFieldPanel(1, 2);
        studyIDPanel.setBackground(UIHelper.DARK_GREEN_COLOR);

        JLabel studyIDLab = UIHelper.createLabel("study id: *");

        studyId = new RoundedJTextField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        UIHelper.renderComponent(studyId, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        studyId.setToolTipText(
                "<html><b>Study Id<b><p>A short identifier for the study</p></html>");

        studyIDPanel.add(studyIDLab);
        studyIDPanel.add(studyId);

        // create panel for the study title field
        JPanel studyTitlePanel = createFieldPanel(1, 2);

        JLabel studyTitleLab = UIHelper.createLabel("study title: *");

        studyTitle = new RoundedJTextField(10, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
        UIHelper.renderComponent(studyTitle, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        studyTitle.setToolTipText(
                "<html><b>Study Title</b><p>Please give a title to the study. <p>This should be a concise description of the experiment in no more than one line </p> <p>of text.</p></html>");

        studyTitlePanel.add(studyTitleLab);
        studyTitlePanel.add(createTextEditEnabledField(studyTitle));

        // create study description field panel
        JPanel studyDescriptionPanel = createFieldPanel(1, 2);

        JLabel studyDescLab = UIHelper.createLabel("study description: *");
        studyDescLab.setVerticalAlignment(JLabel.TOP);

        studyDescription = new JTextArea(5, 5);

        UIHelper.renderComponent(studyDescription, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        studyDescription.setLineWrap(true);
        studyDescription.setWrapStyleWord(true);
        studyDescription.setBackground(UIHelper.BG_COLOR);
        studyDescription.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8));
        studyDescription.setToolTipText(
                "<html><b>Study Description</b><p>Please enter a description of the study which you've carried out.</p></html>");

        JScrollPane studyDescScroll = new JScrollPane(studyDescription,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        studyDescScroll.getViewport().setBackground(UIHelper.BG_COLOR);
        studyDescScroll.setPreferredSize(new Dimension(150, 70));

        studyDescScroll.setBorder(UIHelper.STD_ETCHED_BORDER);

        IAppWidgetFactory.makeIAppScrollPane(studyDescScroll);

        studyDescriptionPanel.add(studyDescLab);
        studyDescriptionPanel.add(UIHelper.createTextEditEnableJTextArea(studyDescScroll, studyDescription));

        studyDetailsPanel.add(studyIDPanel);
        studyDetailsPanel.add(Box.createVerticalStrut(5));

        studyDetailsPanel.add(studyTitlePanel);
        studyDetailsPanel.add(Box.createVerticalStrut(5));

        studyDetailsPanel.add(studyDescriptionPanel);
        studyDetailsPanel.add(Box.createVerticalStrut(5));

        return studyDetailsPanel;
    }

    /**
     * Create a subform to define the factors used in the experiment
     *
     * @return JPanel containing the SubForm
     */
    private JPanel createStudyFactorsSubForm() {
        ArrayList<SubFormField> factorFields = new ArrayList<SubFormField>();

        factorFields.add(new SubFormField("factor name *", SubFormField.SINGLE_ONTOLOGY_SELECT));
        factorFields.add(new SubFormField("factor levels (; separated) *",
                SubFormField.FACTOR_LEVEL_UNITS));
        factorFields.add(new SubFormField("factor level unit (; separated)",
                SubFormField.MULTIPLE_ONTOLOGY_SELECT));

        factorSubForm = new FactorSubForm("define study groups using factors and levels", FieldTypes.FACTOR,
                factorFields, 3, 400, 105, mainMenu.getCurrentDEP());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                factorSubForm.createGUI();
            }
        });

        return factorSubForm;
    }

    private void finaliseStudyEntry(final boolean fromAAP) {

        Thread finaliseStudy = new Thread(new Runnable() {
            public void run() {

                Assay studySample = new Assay("s_" + studyId.getText() + ".txt",
                        null);

                if (fromAAP) {
                    StudySampleCreationAlgorithm ssca = new StudySampleCreationAlgorithm(studyBeingEdited,
                            studySample, factorsToAdd, addAssayUI.getSampleNameValues(),
                            organism.getText(),
                            dep.getParentFrame().selectTROForUserSelection(MappingObject.STUDY_SAMPLE));
                    ssca.runAlgorithm();
                } else {
                    studySample.setTableReferenceObject(dep.getParentFrame().selectTROForUserSelection(MappingObject.STUDY_SAMPLE));
                }

                studyBeingEdited.setStudySamples(studySample);

//                ((AssaySpreadsheet)ApplicationManager.getUserInterfaceForISASection(studyBeingEdited.getStudySample()))
                // todo need to generate the view better.
                StudyDataEntry studyDataEntryUI = (StudyDataEntry) ApplicationManager.getUserInterfaceForISASection(studyBeingEdited);

                ApplicationManager.assignDataEntryToISASection(studySample, ApplicationManager.getUserInterfaceForAssay(studySample, studyDataEntryUI));

                for (Assay assay : studyBeingEdited.getAssays().values()) {
                    investigationDefinition.addToAssays(assay.getAssayReference(),
                            studyBeingEdited.getStudyId());

                    if (assay.getTableReferenceObject() == null) {
                        TableReferenceObject temp = dep.getParentFrame().selectTROForUserSelection(assay.getMeasurementEndpoint(),
                                assay.getTechnologyType());

                        if (temp != null) {
                            TableReferenceObject assayRef = new TableReferenceObject(temp.getTableFields());
                            assay.setTableReferenceObject(assayRef);
                        } else {
                            status.setText(
                                    "the selected combination of endpoint and technology type does not exist!");
                            status.setFont(UIHelper.VER_12_BOLD);

                            return;
                        }
                    }
                    ApplicationManager.assignDataEntryToISASection(assay,
                            ApplicationManager.getUserInterfaceForAssay(assay, studyDataEntryUI));
                }

                ((StudyDataEntry) ApplicationManager.getUserInterfaceForISASection(studyBeingEdited)).reformProtocols();
                investigationDefinition.addStudy(studyBeingEdited);

                //if more studies to define, then define them, else show created thing!
                previousPage.push(new HistoryComponent(addAssayUI, addAssayUI.getListeners()));
                setCurrentPage(visualizeCurrentStudy());
            }
        });

        setCurrentPage(workingProgressScreen);
        finaliseStudy.start();

    }

    private JLayeredPane visualizeCurrentStudy() {

        ExperimentVisualization expViz = new ExperimentVisualization(studyBeingEdited);
        expViz.setSize(600, 600);
        expViz.createGUI();

        final JLayeredPane finalPane = getGeneralLayout(overviewHeader,
                breadcrumb5, "graphical representation of study", expViz, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                // go back to define assay page.
                showPreviousPage(previousPage.pop());
                // remove the assay from the investigation again to avoid duplicates!

                for (Assay a : studyBeingEdited.getAssays().values()) {
                    investigationDefinition.getAssays().remove(a.getAssayReference());
                }

                investigationDefinition.getStudies().remove(studyBeingEdited.getStudyId());
            }

        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                // go back to define assay page.
                if (investigationDefinition.getStudies().size() < numberStudiesToDefine) {
                    status.setText("");
                    setCurrentPage(createDefineStudyPanel("study " +
                            ((investigationDefinition.getStudies().size() + 1) + " of " + numberStudiesToDefine)));

                } else {
                    previousPage.push(new HistoryComponent(finalPane, listeners));
                    setCurrentPage(showDonePage());
                }
            }


        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;

    }

    private JLayeredPane showDonePage() {
        setCurrentPage(workingProgressScreen);

        JPanel container = new JPanel(new BorderLayout());
        container.setSize(500, 400);
        JLabel completedIm = new JLabel(completedInfo);
        completedIm.setVerticalAlignment(JLabel.TOP);
        container.add(completedIm, BorderLayout.CENTER);

        final JLayeredPane finalPane = getGeneralLayout(completedHeader, breadcrumb6, "", container, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        backButton.setIcon(wizard);
        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(wizardOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(wizard);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mainMenu.getMain().setCurrentPage(new ISAcreatorMenu(mainMenu.getMain(), ISAcreatorMenu.SHOW_CREATE_ISA));
                        mainMenu.startAnimation();
                    }
                });
            }
        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                // go back to define assay page.
                mainMenu.getMain().setCurDataEntryPanel(dep);

                ApplicationManager.assignDataEntryToISASection(investigationDefinition, new InvestigationDataEntry(
                        investigationDefinition, dep));

                dep.createGUIFromInvestigation(investigationDefinition);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        mainMenu.getMain().setCurrentPage(dep);
                    }
                });
            }

        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPane;

    }


    private boolean processAssaysSubform() {
        // get assays which were defined
        String[][] assaysInStudy = assayDefinitionSubForm.getDataAsArray();

        String definedCheck = checkForIncorrectAssayDefinition(assaysInStudy);

        if (definedCheck != null) {
            status.setText(definedCheck);
            return false;
        }

        String duplCheckResult = checkForDuplicateAssays(assaysInStudy);


        if (duplCheckResult == null) {
            for (int col = 0; col < assaysInStudy.length; col++) {
                if (((assaysInStudy[col][0] == null) ||
                        assaysInStudy[col][0].trim().equals("")) &&
                        ((assaysInStudy[col][3] == null) ||
                                assaysInStudy[col][3].trim().equals(""))) {
                    //skip blank elements
                } else if ((assaysInStudy[col][0] != null) &&
                        !assaysInStudy[col][0].trim().equals("")) {
                    if ((assaysInStudy[col][3] != null) &&
                            !assaysInStudy[col][3].trim().equals("")) {
                        String modifiedAssayName = assaysInStudy[col][3];

                        if (!modifiedAssayName.startsWith("a_")) {
                            modifiedAssayName = "a_" + modifiedAssayName;
                        }

                        if (!modifiedAssayName.endsWith(".txt")) {
                            modifiedAssayName += ".txt";
                        }

                        String technologyType = (assaysInStudy[col][1] == null || assaysInStudy[col][1].equals(AutoFilterComboCellEditor.BLANK_VALUE)) ? "" : assaysInStudy[col][1];

                        Assay newAssay = new Assay(modifiedAssayName,
                                assaysInStudy[col][0],
                                technologyType, assaysInStudy[col][2]);
                        // add assay to study
                        studyBeingEdited.addAssay(newAssay);
                    } else {
                        status.setText(
                                "<html><p><b>assay file name</b> field must not be left blank in assay definition " +
                                        (col + 1) + "</p></html>");
                        revalidate();

                        return false;
                    }

                } else {
                    status.setText(
                            "<html><p><b>measurement/endpoints name</b> field must not be left blank in assay definition " +
                                    (col + 1) + "</p></html>");
                    revalidate();

                    return false;
                }
            }

            return true;
        } else {
            status.setText(duplCheckResult);

            return false;
        }
    }

    private boolean processFactorSubform() {
        factorsToAdd = new ArrayList<PropertyType>();

        String[][] factorInfo = factorSubForm.getDataAsArray();

        if (!checkForDuplicates(factorInfo, 0)) {
            for (String[] aFactorInfo : factorInfo) {
                String factorName = aFactorInfo[0];
                String factorType = aFactorInfo[0];
                String factorValues = aFactorInfo[1];
                String factorValueUnits = aFactorInfo[2];

                //check to ensure that mandatory fields have been entered...
                if (((factorName != null) && !factorName.trim().equals(""))
                        &&
                        ((factorType != null) && !factorType.trim().equals(""))
                        &&
                        ((factorValues != null) &&
                                !factorValues.trim().equals(""))) {
                    String[] studyFactorLevels = factorValues.split(";");

                    List<PropertyValue> factorLevels = new ArrayList<PropertyValue>();

                    if ((factorValueUnits != null) &&
                            !factorValueUnits.trim().equals("")) {
                        // we have units for the factors
                        String[] studyFactorLevelUnits = factorValueUnits.split(
                                ";");

                        if (studyFactorLevels.length == studyFactorLevelUnits.length) {
                            // equal number of levels and units
                            factorLevels = new ArrayList<PropertyValue>();

                            for (int i = 0; i < studyFactorLevels.length;
                                 i++) {
                                if (!studyFactorLevels[i].trim().equals("")) {
                                    factorLevels.add(new PropertyValue(
                                            studyFactorLevels[i],
                                            studyFactorLevelUnits[i]));
                                }
                            }
                        } else {
                            status.setText(
                                    "<html><p>the <b>factor levels and associated units</b> fields defined in </p><p>factor " +
                                            factorName +
                                            " have an uneven number of terms.</p></html>");
                            revalidate();

                            return false;
                        }
                    } else {
                        for (String s : studyFactorLevels) {
                            if (!s.trim().equals("")) {
                                factorLevels.add(new PropertyValue(s, ""));
                            }
                        }
                    }

                    factorsToAdd.add(new PropertyType(factorName, factorType,
                            factorLevels));
                } else {
                    // check to see if all items are empty, if so, this is not an error.
                    if (factorName != null && factorType != null && factorValues != null) {
                        status.setText(
                                "<html><p><b>missing factor information</b> factor name, type & values must not be empty</p></html>");
                        revalidate();
                        return false;
                    }
                }
            }

            // add factors to study
            for (PropertyType wsf : factorsToAdd) {
                if (!wsf.getPropertyName().trim().equals("")) {
                    studyBeingEdited.addFactor(new Factor(wsf.getPropertyName(),
                            wsf.getPropertyType()));
                }
            }

            studyTreatmentGroups = Utils.getGroupFactors(factorsToAdd);

            return true;
        } else {
            status.setText(
                    "<html><p><b>duplicate factor names</b> factors names must be unique</p></html>");
            revalidate();

            return false;
        }
    }

    private void showAddAssayPane(final Map<Integer, TreatmentReplicate> treatmentGroups) {

        Thread createStudyThread = new Thread(new Runnable() {
            public void run() {
                if (assayDefinitionRequired()) {
                    ApplicationManager.assignDataEntryToISASection(studyBeingEdited, new StudyDataEntry(dep, studyBeingEdited));

                    addAssayUI = new AddAssayPane(Wizard.this, studyBeingEdited, factorsToAdd, treatmentGroups, currentUser);
                    addAssayUI.createGUI();
                    addAssayUI.addPropertyChangeListener("finishedAssayCreation", finaliseStudy);
                    addAssayUI.addPropertyChangeListener("canceledAssayCreation", new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            showPreviousPage(previousPage.pop());
                        }
                    });
                    setCurrentPage(addAssayUI);
                } else {
                    finaliseStudyEntry(false);
                }
            }
        });

        createStudyThread.start();
        setCurrentPage(workingProgressScreen);

    }

    public void showHideInvestigationDefPanel(int value) {
        if (value > 1) {
            fillerPanel.setVisible(false);
            investigationDefinitionPanel.setVisible(true);
        } else {
            fillerPanel.setVisible(true);
            investigationDefinitionPanel.setVisible(false);
        }
    }

    class FinaliseStudyCreationListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent event) {
            System.out.println("finalising study creation");
            finaliseStudyEntry(true);
        }
    }

    public static class FormattedTextFieldVerifier extends InputVerifier {
        public boolean shouldYieldFocus(JComponent input) {
            return verify(input);
        }

        public boolean verify(JComponent input) {
            if (input instanceof JFormattedTextField) {
                JFormattedTextField ftf = (JFormattedTextField) input;
                JFormattedTextField.AbstractFormatter formatter = ftf.getFormatter();

                if (formatter != null) {
                    String text = ftf.getText();
                    try {
                        formatter.stringToValue(text);
                        return true;
                    } catch (ParseException pe) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
