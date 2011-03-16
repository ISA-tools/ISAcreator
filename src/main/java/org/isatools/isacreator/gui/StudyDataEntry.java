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

package org.isatools.isacreator.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofiltercombo.AutoFilterComboCellEditor;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.investigationfileproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.*;
import java.util.List;


/**
 * StudyDataEntry class
 *
 * @author Eamonn Maguire
 */
public class StudyDataEntry extends DataEntryForm {

    @InjectedResource
    private ImageIcon panelHeader;

    private JTextArea studyDescription;
    private JTextField dateOfStudyPublicRelease;
    private JTextField dateOfStudySubmission;
    private JTextField studyIdentifier;
    private JTextField studyTitle;
    private Study study;
    private SubForm studyDesignSubform;
    private SubForm studyPublicationsSubForm;
    private SubForm assaySubForm;
    private SubForm contactSubForm;
    private SubForm factorSubForm;
    private SubForm protocolSubForm;


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

        instantiatePane();
        createFields();
        finalisePane();
    }

    /**
     * Create the overall input for the study form
     */
    public void createFields() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UIHelper.BG_COLOR);

        JLabel header = new JLabel(panelHeader, JLabel.RIGHT);
        northPanel.add(header, BorderLayout.NORTH);

        if (study.getReferenceObject() == null) {
            TableReferenceObject tro = getISAcreatorEnvironment().selectTROForUserSelection(MappingObject.INVESTIGATION);

            DataEntryReferenceObject referenceObject = new DataEntryReferenceObject();
            referenceObject.setFieldDefinition(tro.getTableFields().getFields());

            study.setReferenceObject(referenceObject);
        }

        container.add(northPanel, BorderLayout.NORTH);
        container.add(createStudyDesc(), BorderLayout.CENTER);

        JPanel subforms = new JPanel();
        subforms.setLayout(new BoxLayout(subforms, BoxLayout.PAGE_AXIS));

        Box subPanel = Box.createVerticalBox();
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyDesignSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyPublicationSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyFactorsSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyAssaysSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyProtocolsSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        subPanel.add(createStudyContactsSubForm());
        subPanel.add(Box.createVerticalStrut(20));

        container.add(subPanel, BorderLayout.SOUTH);

        JScrollPane containerScroller = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        containerScroller.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(containerScroller);

        add(containerScroller);
    }

    /**
     * Create the Assay definition subform.
     *
     * @return - JPanel containing the subform required for definition of the Assay.
     */
    private JPanel createStudyAssaysSubForm() {
        List<SubFormField> assayFields = new ArrayList<SubFormField>();

        List<MappingObject> assayToTypeMapping = getDataEntryEnvironment().getParentFrame()
                .getMappings();

        Set<String> measurementEndPointSet = new HashSet<String>();
        Set<String> techTypeSet = new HashSet<String>();

        for (MappingObject mo : assayToTypeMapping) {
            if (!mo.getMeasurementEndpointType().equalsIgnoreCase("[sample]")) {
                measurementEndPointSet.add(mo.getMeasurementEndpointType());
                if (!mo.getTechnologyType().trim().equals("")) {
                    techTypeSet.add(mo.getTechnologyType());
                }
            }
        }

        List<String> tempMeasurements = new ArrayList<String>();
        List<String> tempTechnologies = new ArrayList<String>();

        // add everything for the sets to a list.
        tempMeasurements.addAll(measurementEndPointSet);
        tempTechnologies.addAll(techTypeSet);

        tempTechnologies.add(0, AutoFilterComboCellEditor.BLANK_VALUE);

        // sort lists
        Collections.sort(tempMeasurements);
        Collections.sort(tempTechnologies);

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_ASSAYS);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();

        for (String assayField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_ASSAYS)) {

            if (!fieldsToIgnore.contains(assayField)) {

                FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(assayField);

                int fieldType = SubFormField.STRING;

                if (assayField.equals(Assay.MEASUREMENT_ENDPOINT)) {
                    assayFields.add(new SubFormField(assayField, SubFormField.COMBOLIST, tempMeasurements.toArray(new String[tempMeasurements.size()])));
                } else if (assayField.equals(Assay.TECHNOLOGY_TYPE)) {
                    assayFields.add(new SubFormField(assayField, SubFormField.COMBOLIST, tempTechnologies.toArray(new String[tempTechnologies.size()])));
                } else if (ontologyFields.contains(assayField)) {

                    fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    assayFields.add(new SubFormField(assayField, fieldType));
                } else {
                    if (fieldDescriptor != null) {
                        fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                                fieldDescriptor.isAcceptsMultipleValues());
                    }
                    assayFields.add(new SubFormField(assayField, fieldType));
                }
            }
        }

        int numColsToAdd = (study.getAssays().size() == 0) ? 1
                : (study.getAssays()
                .size() + 1);
        assaySubForm = new AssaySubForm(InvestigationFileSection.STUDY_ASSAYS.toString(), FieldTypes.ASSAY, assayFields,
                numColsToAdd, 300, 110, this);
        assaySubForm.createGUI();

        return assaySubForm;
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

        //todo this will have to also take into consideration a config.xml which describes the investigation.
        // todo the desired fields will be the union of both sets of fields.

        Set<String> ontologyFields = study.getReferenceObject().getOntologyTerms(InvestigationFileSection.STUDY_CONTACTS);
        Set<String> fieldsToIgnore = study.getReferenceObject().getFieldsToIgnore();

        for (String contactField : study.getReferenceObject().getFieldsForSection(InvestigationFileSection.STUDY_CONTACTS)) {

            FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(contactField);

            if (!fieldsToIgnore.contains(contactField)) {

                int fieldType = SubFormField.STRING;

                if (ontologyFields.contains(contactField)) {

                    fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    contactFields.add(new SubFormField(contactField, fieldType));
                } else {

                    if (fieldDescriptor != null) {
                        fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                                fieldDescriptor.isAcceptsMultipleValues());
                    }

                    contactFields.add(new SubFormField(contactField, fieldType));
                }
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
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), "study description",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        Box verticalContainer = Box.createVerticalBox();

        addFieldsToPanel(verticalContainer, InvestigationFileSection.STUDY_SECTION, study.getFieldValues(), study.getReferenceObject());

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
            FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(factorField);

            if (!fieldsToIgnore.contains(factorField)) {

                int fieldType = SubFormField.STRING;

                if (ontologyFields.contains(factorField)) {

                    fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    factorFields.add(new SubFormField(factorField, fieldType));
                } else {

                    if (fieldDescriptor != null) {
                        fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                                fieldDescriptor.isAcceptsMultipleValues());
                    }

                    factorFields.add(new SubFormField(factorField, fieldType));
                }
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
            FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(protocolField);

            if (!fieldsToIgnore.contains(protocolField)) {

                int fieldType = SubFormField.STRING;

                if (ontologyFields.contains(protocolField)) {

                    fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    protocolFields.add(new SubFormField(protocolField, fieldType));
                } else {

                    if (fieldDescriptor != null) {
                        fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                                fieldDescriptor.isAcceptsMultipleValues());
                    }

                    protocolFields.add(new SubFormField(protocolField, fieldType));
                }
            }
        }

        int numColsToAdd = (study.getProtocols().size() == 0) ? 1
                : study.getProtocols()
                .size();

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

            FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(publicationField);

            if (!fieldsToIgnore.contains(publicationField)) {

                int fieldType = SubFormField.STRING;

                if (ontologyFields.contains(publicationField)) {

                    fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    publicationFields.add(new SubFormField(publicationField, fieldType));
                } else {

                    if (fieldDescriptor != null) {
                        fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                                fieldDescriptor.isAcceptsMultipleValues());
                    }

                    publicationFields.add(new SubFormField(publicationField, fieldType));
                }
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

            FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(studyDesignField);

            if (!fieldsToIgnore.contains(studyDesignField)) {

                int fieldType = SubFormField.STRING;

                if (ontologyFields.contains(studyDesignField)) {

                    fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    studyDesignFields.add(new SubFormField(studyDesignField, fieldType));
                } else {

                    if (fieldDescriptor != null) {
                        fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                                fieldDescriptor.isAcceptsMultipleValues());
                    }

                    studyDesignFields.add(new SubFormField(studyDesignField, fieldType));
                }
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
        // remove assay from the study
        getStudy().removeAssay(assayRef);
        // remove assay from investigation assay list.
        getDataEntryEnvironment().getInvestigation().getAssays().remove(assayRef);
    }

    /**
     * Output the Study definition in tabular format for output to the ISA-TAB files.
     */
    public String toString() {
        update();

        StringBuffer output = new StringBuffer();
        output.append(InvestigationFileSection.STUDY_SECTION).append("\n");

        for (String fieldName : study.getFieldValues().keySet()) {
            output.append(fieldName).append("\t\"").append(study.getFieldValues().get(fieldName)).append("\"\n");
        }

        output.append(studyDesignSubform.toString());
        output.append(studyPublicationsSubForm.toString());
        output.append(factorSubForm.toString());
        output.append(assaySubForm.toString());
        output.append(protocolSubForm.toString());
        output.append(contactSubForm.toString());

        return output.toString();
    }

    /**
     * update method to save all changes in view to the model (Study object)
     */
    public void update() {

        for (String fieldName : fieldDefinitions.keySet()) {
            study.getFieldValues().put(fieldName, fieldDefinitions.get(fieldName).getText());
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
        protocolSubForm.reformItems();
    }

    public void reformFactors() {
        factorSubForm.reformItems();
    }

    public void removeReferences() {

        setDataEntryEnvironment(null);

        studyDesignSubform.setDataEntryEnvironment(null);
        studyDesignSubform.getRowEditor().removeAllListeners();
        studyDesignSubform.setParent(null);
        studyDesignSubform = null;

        studyPublicationsSubForm.setDataEntryEnvironment(null);
        studyPublicationsSubForm.getRowEditor().removeAllListeners();
        studyPublicationsSubForm.setParent(null);
        studyPublicationsSubForm = null;

        factorSubForm.setDataEntryEnvironment(null);
        factorSubForm.getRowEditor().removeAllListeners();
        factorSubForm.setParent(null);
        factorSubForm = null;

        contactSubForm.setDataEntryEnvironment(null);
        contactSubForm.getRowEditor().removeAllListeners();
        contactSubForm.setParent(null);
        contactSubForm = null;

        protocolSubForm.setDataEntryEnvironment(null);
        protocolSubForm.getRowEditor().removeAllListeners();
        protocolSubForm.setParent(null);
        protocolSubForm = null;

        assaySubForm.setDataEntryEnvironment(null);
        assaySubForm.getRowEditor().removeAllListeners();
        assaySubForm.setParent(null);
        assaySubForm = null;

        study.getStudySample().getSpreadsheetUI().setDataEntryEnvironment(null);

        for (String a : study.getAssays().keySet()) {
            Assay tmpAssay = study.getAssays().get(a);
            tmpAssay.getSpreadsheetUI().setDataEntryEnvironment(null);
            tmpAssay.getSpreadsheetUI().getTable().getTable().setCellEditor(null);
            tmpAssay.getSpreadsheetUI().setTable(null);
            tmpAssay.setSpreadsheet(null);
        }


        setDataEntryEnvironment(null);

        study.setAssays(null);

        study.getUserInterface().removeAll();
        study.setUI(null);
        study.setStudySamples(null);
        study = null;
        removeAll();
    }
}
