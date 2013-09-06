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

package org.isatools.isacreator.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.api.utils.SpreadsheetUtils;
import org.isatools.isacreator.api.utils.StudyUtils;
import org.isatools.isacreator.assayselection.AssaySelection;
import org.isatools.isacreator.assayselection.AssaySelectionDialog;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.WeakPropertyChangeListener;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.gui.formelements.assay.AssayInformationPanel;
import org.isatools.isacreator.gui.formelements.assay.AssayInformationWriter;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.utils.StringProcessing;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * StudyDataEntry class
 *
 * @author Eamonn Maguire
 */
public class StudyDataEntry extends DataEntryForm {

    @InjectedResource
    private ImageIcon panelHeader, addRecordIcon, addRecordIconOver;

    private JLabel addRecord;

    private Study study;

    private JPanel assayContainer;
    private AssaySelectionDialog assaySelectionUI;

    private SubForm studyDesignSubform;
    private SubForm studyPublicationsSubForm;
    private SubForm contactSubForm;
    private SubForm factorSubForm;
    private SubForm protocolSubForm;

    private RemoveAssayListener removeAssayListener = new RemoveAssayListener();
    private ViewAssayListener viewAssayListener = new ViewAssayListener();
    private AddAssayListener addAssayListener = new AddAssayListener();
    private JPanel fieldContainer;


    /**
     * StudyDataEntry constructor
     *
     * @param dataEntryEnvironment - DataEntryEnvironment
     * @param study                - Associated Study Object.
     */
    public StudyDataEntry(DataEntryEnvironment dataEntryEnvironment, Study study) {
        super(dataEntryEnvironment);
        ResourceInjector.get("gui-package.style").inject(this);
        this.study = study;
        createGUI();
    }

    public void createGUI() {
        Map<String, List<String>> measToAllowedTechnologies =
                ConfigurationManager.getAllowedTechnologiesPerEndpoint();

        assaySelectionUI = new AssaySelectionDialog(measToAllowedTechnologies);
        generateAliases(study.getFieldValues().keySet());
        instantiatePane();
        createFields();
        finalisePane();
    }

    /**
     * Create the overall input for the study form
     */
    public void createFields() {
        fieldContainer = new JPanel(new BorderLayout());
        fieldContainer.setBackground(UIHelper.BG_COLOR);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UIHelper.BG_COLOR);

        JLabel header = new JLabel(panelHeader, JLabel.RIGHT);
        northPanel.add(header, BorderLayout.NORTH);

        if (study.getReferenceObject() == null) {
            TableReferenceObject tro = ConfigurationManager.selectTROForUserSelection(MappingObject.INVESTIGATION);

            DataEntryReferenceObject referenceObject = new DataEntryReferenceObject();
            referenceObject.setFieldDefinition(tro.getTableFields().getFields());

            study.setReferenceObject(referenceObject);
        }

        fieldContainer.add(northPanel, BorderLayout.NORTH);
        fieldContainer.add(createStudyDesc(), BorderLayout.CENTER);

        Box subPanel = Box.createVerticalBox();
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyAssaySection());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyDesignSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyPublicationSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyFactorsSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyProtocolsSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyContactsSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        fieldContainer.add(subPanel, BorderLayout.SOUTH);

        JScrollPane containerScroller = new JScrollPane(fieldContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        containerScroller.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(containerScroller);
        containerScroller.getVerticalScrollBar().setUnitIncrement(16);

        add(containerScroller);
    }

    /**
     * Create the Assay definition section.
     *
     * @return - JPanel containing the UI elements required for definition of the Assay.
     */
    private Container createStudyAssaySection() {

        assayContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assayContainer.setBackground(UIHelper.BG_COLOR);

        updateAssayPanel();

        JScrollPane assayScroller = new JScrollPane(assayContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(assayScroller);

        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(300, 180));
        container.setBorder(new TitledBorder(
                UIHelper.GREEN_ROUNDED_BORDER, InvestigationFileSection.STUDY_ASSAYS.toString(),
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        addRecord = new JLabel("add new assay(s)", addRecordIcon, JLabel.LEFT);
        UIHelper.renderComponent(addRecord, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        addRecord.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        Map<String, List<String>> measToAllowedTechnologies =
                                ConfigurationManager.getAllowedTechnologiesPerEndpoint();

                        assaySelectionUI = new AssaySelectionDialog(measToAllowedTechnologies);
                        assaySelectionUI.createGUI();

                        ApplicationManager.getCurrentApplicationInstance().showJDialogAsSheet(assaySelectionUI);
                        addRecord.setIcon(addRecordIcon);

                        assaySelectionUI.addPropertyChangeListener("assaysChosen", new WeakPropertyChangeListener(addAssayListener));
                    }
                });
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                addRecord.setIcon(addRecordIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addRecord.setIcon(addRecordIcon);
            }
        });

        container.add(addRecord, BorderLayout.NORTH);

        container.add(assayScroller, BorderLayout.CENTER);

        return container;
    }

    public void updateAssayPanel() {
        assayContainer.removeAll();

        for (Assay assay : study.getAssays().values()) {
            AssayInformationPanel informationPanel = new AssayInformationPanel(assay);
            informationPanel.addPropertyChangeListener("removeAssay", new WeakPropertyChangeListener(removeAssayListener));
            informationPanel.addPropertyChangeListener("viewAssay", new WeakPropertyChangeListener(viewAssayListener));
            assayContainer.add(informationPanel);
        }
    }

    @Override
    public List<StudyDesign> getDesigns() {
        return study.getStudyDesigns();
    }


    /**
     * Create the Contacts subform for the definition of contacts in the Study form.
     *
     * @return - a JPanel containing the Contacts subform.
     */
    private JPanel createStudyContactsSubForm() {
        List<SubFormField> contactFields = new ArrayList<SubFormField>();

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_CONTACTS);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();

        for (String contactField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_CONTACTS)) {
            SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, study, contactField);

            if (generatedField != null) {
                contactFields.add(generatedField);
            }
        }

        int numColsToAdd = (study.getContacts().size() == 0) ? 4
                : study.getContacts()
                .size();

        contactSubForm = new ContactSubForm(InvestigationFileSection.STUDY_CONTACTS.toString(), FieldTypes.CONTACT,
                contactFields, numColsToAdd, 300, 195, this);
        contactSubForm.createGUI();

        return contactSubForm;


    }


    /**
     * Create the majority of fields for data entry in the study definition form
     *
     * @return - JPanel containing the fields required for singular definition in the Study form
     */
    private JPanel createStudyDesc() {
        JPanel studyDesc = new JPanel();
        studyDesc.setLayout(new BoxLayout(studyDesc, BoxLayout.PAGE_AXIS));
        UIHelper.renderComponent(studyDesc, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);
        studyDesc.setBorder(new TitledBorder(
                UIHelper.GREEN_ROUNDED_BORDER, "study description",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        Box verticalContainer = Box.createVerticalBox();

        addFieldsToPanel(verticalContainer, InvestigationFileSection.STUDY_SECTION,
                study.getFieldValues(), study.getReferenceObject());

        studyDesc.add(verticalContainer, BorderLayout.NORTH);

        return studyDesc;
    }

    /**
     * Creates the Factor definition subform
     *
     * @return - JPanel containing the Factor definition subform.
     */
    private JPanel createStudyFactorsSubForm() {
        List<SubFormField> factorFields = new ArrayList<SubFormField>();

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_FACTORS);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();

        for (String factorField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_FACTORS)) {

            SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, study, factorField);

            if (generatedField != null) {
                factorFields.add(generatedField);
            }
        }

        int numColsToAdd = (study.getFactors().size() == 0) ? 1
                : study.getFactors()
                .size();

        factorSubForm = new FactorSubForm(InvestigationFileSection.STUDY_FACTORS.toString(), FieldTypes.FACTOR, factorFields,
                numColsToAdd, 300, 80, this);

        factorSubForm.createGUI();

        return factorSubForm;
    }

    /**
     * Create the Protocol definition subform in the study definition form.
     *
     * @return JPanel containing the Protocol definition subform.
     */
    private JPanel createStudyProtocolsSubForm() {
        List<SubFormField> protocolFields = new ArrayList<SubFormField>();

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_PROTOCOLS);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();

        for (String protocolField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_PROTOCOLS)) {

            SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, study, protocolField);

            if (generatedField != null) {
                protocolFields.add(generatedField);
            }
        }

        int numColsToAdd = study.getProtocols().size() == 0 ? 1 : study.getProtocols().size();

        protocolSubForm = new ProtocolSubForm(InvestigationFileSection.STUDY_PROTOCOLS.toString(), FieldTypes.PROTOCOL,
                protocolFields, numColsToAdd, 300, 180, this);
        protocolSubForm.createGUI();

        return protocolSubForm;
    }

    /**
     * Create the publications subform
     *
     * @return JPanel containing the Publication definition subform
     */
    private JPanel createStudyPublicationSubForm() {
        List<SubFormField> publicationFields = new ArrayList<SubFormField>();

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_PUBLICATIONS);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();
        for (String publicationField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_PUBLICATIONS)) {

            SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, study, publicationField);

            if (generatedField != null) {
                publicationFields.add(generatedField);
            }
        }

        int numColsToAdd = (study.getPublications().size() == 0) ? 1
                : study.getPublications()
                .size();

        studyPublicationsSubForm = new PublicationSubForm(InvestigationFileSection.STUDY_PUBLICATIONS.toString(), FieldTypes.PUBLICATION,
                publicationFields, numColsToAdd, 300, 120, this);
        studyPublicationsSubForm.createGUI();

        return studyPublicationsSubForm;
    }

    /**
     * Create the publications subform
     *
     * @return JPanel containing the Publication definition subform
     */
    private JPanel createStudyDesignSubForm() {

        List<SubFormField> studyDesignFields = new ArrayList<SubFormField>();

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_DESIGN_SECTION);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();

        for (String studyDesignField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_DESIGN_SECTION)) {

            SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, study, studyDesignField);

            if (generatedField != null) {
                studyDesignFields.add(generatedField);
            }
        }

        int numColsToAdd = (study.getStudyDesigns().size() == 0) ? 2
                : study.getStudyDesigns().size();

        studyDesignSubform = new StudyDesignSubForm(InvestigationFileSection.STUDY_DESIGN_SECTION.toString(), FieldTypes.DESIGN,
                studyDesignFields, numColsToAdd, 300, 60, this);
        studyDesignSubform.createGUI();
        return studyDesignSubform;
    }

    public synchronized Map<String, Assay> getAssays() {
        return study.getAssays();
    }

    public synchronized List<Contact> getContacts() {
        return study.getContacts();
    }

    public synchronized List<Factor> getFactors() {
        return study.getFactors();
    }

    public synchronized List<Protocol> getProtocols() {
        return study.getProtocols();
    }

    public synchronized String[] getProtocolNames() {
        return study.getProtocolNames();

    }

    public Study getStudy() {
        return study;
    }

    @Override
    public List<Publication> getPublications() {
        return study.getPublications();
    }

    /**
     * Removing the Assay involves two steps:
     * Removing it from the tree; and
     * Removing it from the Study itself.
     * This method does both.
     *
     * @param assayRef - assay to be removed.
     */
    public void removeAssay(String assayRef) {
        // remove assay from the tree
        getDataEntryEnvironment().removeFromTree(assayRef);
        removeUnusedProtocols(assayRef);
        // remove assay from the study
        getStudy().removeAssay(assayRef);

        // remove assay from investigation assay list.
        getDataEntryEnvironment().getInvestigation().getAssays().remove(assayRef);
    }

    private void removeUnusedProtocols(String assayRef) {
        Assay assay = getStudy().getAssays().get(assayRef);

        Set<String> protocolRefsInAssay = SpreadsheetUtils.findValuesForColumnInSpreadsheet(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(study.getStudySample())).getSpreadsheet(), GeneralFieldTypes.PROTOCOL_REF.name);

        Set<String> fieldFocus = Collections.singleton(GeneralFieldTypes.PROTOCOL_REF.name);

        Set<String> protocolsPresentInOtherAssays = new HashSet<String>();
        if (!protocolRefsInAssay.isEmpty()) {
            // now check remaining assays to see if the protocol is used elsewhere
            for (String otherAssayRef : getStudy().getAssays().keySet()) {
                if (!otherAssayRef.equals(assay.getAssayReference())) {
                    Set<String> foundValues = SpreadsheetUtils.findValueInSheet(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(assay)).getSpreadsheet(), protocolRefsInAssay, fieldFocus);
                    protocolsPresentInOtherAssays.addAll(foundValues);
                }
            }
        }

        // now check if we need to remove any protocols
        if (!protocolsPresentInOtherAssays.containsAll(protocolRefsInAssay)) {
            // we need to remove some protocols
            for (String protocolRef : protocolRefsInAssay) {
                if (!protocolsPresentInOtherAssays.contains(protocolRef)) {
                    // remove this protocol
                    int index = protocolSubForm.getColumnIndexForValue(0, protocolRef);
                    if (index != -1) {
                        protocolSubForm.removeItem(index);
                    }
                }
            }
        }
    }

    /**
     * Output the Study definition in tabular format for output to the ISA-TAB files.
     */
    public String toString() {
        update();

        StringBuilder output = new StringBuilder();
        output.append(InvestigationFileSection.STUDY_SECTION).append("\n");

        Map<Integer, Map<String, String>> ontologyTerms = IOUtils.getOntologyTerms(study.getFieldValues().keySet());

        // now, do ontology processing
        for (int fieldHashCode : ontologyTerms.keySet()) {
            Map<String, String> ontologyField = ontologyTerms.get(fieldHashCode);
            Map<String, String> processedOntologyField = IOUtils.processOntologyField(ontologyField, study.getFieldValues());
            study.getFieldValues().put(ontologyField.get(IOUtils.TERM), processedOntologyField.get(ontologyField.get(IOUtils.TERM)));
            study.getFieldValues().put(ontologyField.get(IOUtils.ACCESSION), processedOntologyField.get(ontologyField.get(IOUtils.ACCESSION)));
            study.getFieldValues().put(ontologyField.get(IOUtils.SOURCE_REF), processedOntologyField.get(ontologyField.get(IOUtils.SOURCE_REF)));
        }

        // now, output the fields
        for (String fieldName : study.getFieldValues().keySet()) {
            output.append(fieldName).append("\t\"").append(StringProcessing.cleanUpString(study.getFieldValues().get(fieldName))).append("\"\n");
        }

        populateEmptySections();

        output.append(getISASectionAsString(InvestigationFileSection.STUDY_DESIGN_SECTION.toString(), getStudy().getStudyDesigns()));
        output.append(getISASectionAsString(InvestigationFileSection.STUDY_PUBLICATIONS.toString(), getStudy().getPublications()));
        output.append(getISASectionAsString(InvestigationFileSection.STUDY_FACTORS.toString(), getStudy().getFactors()));
        output.append(new AssayInformationWriter().printAssays(study.getAssays().values(),
                ConfigurationManager.getMappings()));
        output.append(getISASectionAsString(InvestigationFileSection.STUDY_PROTOCOLS.toString(), getStudy().getProtocols()));
        output.append(getISASectionAsString(InvestigationFileSection.STUDY_CONTACTS.toString(), getStudy().getContacts()));

        return output.toString();
    }

    private void populateEmptySections() {
        if(getStudy().getStudyDesigns().size() == 0) {
            getStudy().getStudyDesigns().add(new StudyDesign());
        }

        if(getStudy().getFactors().size() == 0) {
            getStudy().addFactor(new Factor());
        }

        if(getStudy().getProtocols().size() == 0) {
            getStudy().addProtocol(new Protocol());
        }

        if(getStudy().getPublications().size() == 0) {
            getStudy().addPublication(new StudyPublication());
        }

        if(getStudy().getContacts().size() == 0) {
            getStudy().addContact(new StudyContact());
        }
    }

    /**
     * update method to save all changes in view to the model (Study object)
     */
    public void update() {
        for (String fieldName : fieldDefinitions.keySet()) {
            String tmpFieldName = fieldName;
            if (aliasesToRealNames.containsKey(fieldName)) {
                tmpFieldName = aliasesToRealNames.get(fieldName);
            }

            if (fieldDefinitions.get(fieldName) instanceof JTextComponent) {
                study.getFieldValues().put(tmpFieldName, ((JTextComponent) fieldDefinitions.get(fieldName)).getText());
            } else if (fieldDefinitions.get(fieldName) instanceof JComboBox) {
                study.getFieldValues().put(tmpFieldName, ((JComboBox) fieldDefinitions.get(fieldName)).getSelectedItem().toString());
            }

        }

        studyDesignSubform.update();
        studyPublicationsSubForm.update();
        factorSubForm.update();
        protocolSubForm.update();
        contactSubForm.update();
    }

    public void updateFactorsAndProtocols() {
        factorSubForm.update();
        protocolSubForm.update();
    }

    public void reformProtocols() {
        protocolSubForm.reformPreviousContent();
    }

    public void reformFactors() {
        factorSubForm.reformItems();
    }

    public void removeReferences() {
        setDataEntryEnvironment(null);

        studyDesignSubform.cleanupReferences();
        studyDesignSubform = null;

        studyPublicationsSubForm.cleanupReferences();
        studyPublicationsSubForm = null;

        factorSubForm.cleanupReferences();
        factorSubForm = null;

        contactSubForm.cleanupReferences();
        contactSubForm = null;

        protocolSubForm.cleanupReferences();
        protocolSubForm = null;

        addRecord.getParent().removeAll();
        addRecord.removeMouseListener(addRecord.getMouseListeners()[0]);
        addRecord = null;

        assayContainer.getParent().removeAll();
        assayContainer.removeAll();
        assayContainer = null;

        assaySelectionUI.removePropertyChangeListener("assaysChosen", new WeakPropertyChangeListener(addAssayListener));
        assaySelectionUI.removeAll();
        assaySelectionUI = null;

        addAssayListener = null;
        viewAssayListener = null;
        removeAssayListener = null;


        ApplicationManager.getIsaSectionToDataEntryForm().get(study.getStudySample()).setDataEntryEnvironment(null);

        for (String assayReference : study.getAssays().keySet()) {
            Assay assay = study.getAssays().get(assayReference);
            ApplicationManager.removeISASectionAndDataEntryForm(assay);
        }

        study.getAssays().clear();


        setDataEntryEnvironment(null);
        study.setAssays(null);
        // todo add in previous removals for cleanup.
        study.setStudySamples(null);
        study = null;

        fieldContainer.removeAll();
        ApplicationManager.clearUserInterfaceAssignments();

        removeAll();
    }

    class RemoveAssayListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getNewValue() instanceof AssayInformationPanel) {
                final AssayInformationPanel panel = (AssayInformationPanel) propertyChangeEvent.getNewValue();

                String removalText = "<html>" + "<b>Confirm deletion of assay</b>" + "<p>Deleting this will result " +
                        "in its complete removal from this experiment annotation!</p>" +
                        "<p>Do you wish to continue?</p>" + "</html>";

                JOptionPane optionPane = new JOptionPane(removalText,
                        JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION);
                optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName()
                                .equals(JOptionPane.VALUE_PROPERTY)) {
                            int lastOptionAnswer = Integer.valueOf(event.getNewValue()
                                    .toString());

                            if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                removeAssay(panel.getAssay().getAssayReference());
                                assayContainer.remove(panel);
                                assayContainer.repaint();
                                getDataEntryEnvironment().getParentFrame().hideSheet();
                            } else {
                                // just hide the sheet and cancel further actions!
                                getDataEntryEnvironment().getParentFrame().hideSheet();
                            }
                        }
                    }
                });
                UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
                getDataEntryEnvironment().getParentFrame().showJDialogAsSheet(optionPane.createDialog(StudyDataEntry.this, "Confirm Delete"));
            }

        }
    }

    class ViewAssayListener implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getNewValue() instanceof AssayInformationPanel) {
                final AssayInformationPanel panel = (AssayInformationPanel) propertyChangeEvent.getNewValue();
                getDataEntryEnvironment().selectAssayInTree(panel.getAssay());
            }

        }
    }

    class AddAssayListener implements PropertyChangeListener {

        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            List<AssaySelection> selectedAssays = assaySelectionUI.getSelectedAssays();

            for (AssaySelection assay : selectedAssays) {

                String assayRef = StudyUtils.generateAssayReference(study, assay.getMeasurement(), assay.getTechnology());

                Assay addedAssay = getDataEntryEnvironment().addAssay(assay.getMeasurement(), assay.getTechnology(), assay.getPlatform(), assayRef);

                AssayInformationPanel informationPanel = new AssayInformationPanel(addedAssay);
                informationPanel.addPropertyChangeListener("removeAssay", new WeakPropertyChangeListener(removeAssayListener));
                informationPanel.addPropertyChangeListener("viewAssay", new WeakPropertyChangeListener(viewAssayListener));

                assayContainer.add(informationPanel);
                assayContainer.repaint();
            }
        }
    }

}
