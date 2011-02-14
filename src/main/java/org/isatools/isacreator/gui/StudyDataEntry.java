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
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.model.*;
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
     * @param dep   - DataEntryEnvironment
     * @param study - Associated Study Object.
     */
    public StudyDataEntry(DataEntryEnvironment dep, Study study) {
        super(dep);

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

        // todo for next release integrate use of Comment section in Study and Investigation definition.
//		subPanel.add(createStudyCommentsSubForm());
//		subPanel.add(Box.createVerticalStrut(20));

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

        List<MappingObject> assayToTypeMapping = getDEP().getParentFrame()
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

        assayFields.add(new SubFormField("Assay Measurement Type",
                SubFormField.COMBOLIST, tempMeasurements.toArray(new String[tempMeasurements.size()])));
        assayFields.add(new SubFormField("Assay Technology Type", SubFormField.COMBOLIST,
                tempTechnologies.toArray(new String[tempTechnologies.size()])));
        assayFields.add(new SubFormField("Assay Technology Platform", SubFormField.STRING));
        assayFields.add(new SubFormField("Assay File Name", SubFormField.STRING));

        int numColsToAdd = (study.getAssays().size() == 0) ? 1
                : (study.getAssays()
                .size() + 1);
        assaySubForm = new AssaySubForm("study assays", FieldTypes.ASSAY, assayFields,
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

        contactFields.add(new SubFormField("Person Last Name",
                SubFormField.STRING));
        contactFields.add(new SubFormField("Person First Name",
                SubFormField.STRING));
        contactFields.add(new SubFormField("Person Mid Initials",
                SubFormField.STRING));
        contactFields.add(new SubFormField("Person Email", SubFormField.STRING));
        contactFields.add(new SubFormField("Person Phone", SubFormField.STRING));
        contactFields.add(new SubFormField("Person Fax", SubFormField.STRING));
        contactFields.add(new SubFormField("Person Address", SubFormField.LONG_STRING));
        contactFields.add(new SubFormField("Person Affiliation",
                SubFormField.STRING));
        contactFields.add(new SubFormField("Person Roles",
                SubFormField.MULTIPLE_ONTOLOGY_SELECT));

        int numColsToAdd = (study.getContacts().size() == 0) ? 4
                : study.getContacts()
                .size();

        contactSubForm = new ContactSubForm("study contacts", FieldTypes.CONTACT,
                contactFields, numColsToAdd, 300, 190, this);
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


        // Create study identifier fields
        JPanel studyIDCont = createFieldPanel(1, 2);
        studyIDCont.add(createLabel("study identifier"));
        studyIdentifier = new RoundedJTextField(10);
        studyIdentifier.setText(study.getStudyId());
        studyIdentifier.setToolTipText(
                "<html><b>Study ID</b><p>An identifier for the study</p></html>");
        UIHelper.renderComponent(studyIdentifier, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        studyIDCont.add(studyIdentifier);


        verticalContainer.add(studyIDCont);
        verticalContainer.add(Box.createVerticalStrut(5));

        // create study title fields
        JPanel studyTitleCont = createFieldPanel(1, 2);
        studyTitleCont.add(createLabel("study title"));
        studyTitle = new RoundedJTextField(10);
        studyTitle.setText(study.getStudyTitle());
        studyTitle.setToolTipText(
                "<html><b>Study Title</b><p>The title for this study</p></html>");
        studyTitleCont.add(createTextEditEnabledField(studyTitle));

        UIHelper.renderComponent(studyTitle, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        verticalContainer.add(studyTitleCont);
        verticalContainer.add(Box.createVerticalStrut(5));

        // create study description fields
        JPanel studyDescCont = createFieldPanel(1, 2);
        studyDescCont.add(createLabel("study description"));
        studyDescription = new JTextArea(study.getStudyDesc(), 3, 5);
        studyDescription.setLineWrap(true);
        studyDescription.setWrapStyleWord(true);
        studyDescription.setBackground(UIHelper.BG_COLOR);
        studyDescription.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8));
        studyDescription.setToolTipText(
                "<html><b>Study Description</b><p>A detailed description of the Study.</p></html>");

        UIHelper.renderComponent(studyDescription, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JScrollPane descScroller = new JScrollPane(studyDescription,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descScroller.setPreferredSize(new Dimension(100, 75));
        descScroller.setBorder(UIHelper.STD_ETCHED_BORDER);
        descScroller.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(descScroller);

        studyDescCont.add(UIHelper.createTextEditEnableJTextArea(descScroller, studyDescription));

        verticalContainer.add(studyDescCont);
        verticalContainer.add(Box.createVerticalStrut(5));

        // create date of study submission fields
        JPanel dateOfStudySubmissionCont = createFieldPanel(1, 2);
        dateOfStudySubmissionCont.add(createLabel("study submission date"));
        dateOfStudySubmission = new RoundedJTextField(10);
        dateOfStudySubmission.setText(study.getDateOfSubmission());
        dateOfStudySubmission.setToolTipText(
                "<html><b>Date of Study Submission</b><p>When the study will be submitted</p><p><b>Please use the calendar tool to enter the date!</b></p></html>");
        dateOfStudySubmissionCont.add(createDateDropDown(dateOfStudySubmission));

        UIHelper.renderComponent(dateOfStudySubmission, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        verticalContainer.add(dateOfStudySubmissionCont);
        verticalContainer.add(Box.createVerticalStrut(5));

        // create study  public release date fields
        JPanel publicStudyReleaseDateCont = createFieldPanel(1, 2);
        publicStudyReleaseDateCont.add(createLabel("study public release date"));
        dateOfStudyPublicRelease = new RoundedJTextField(10);
        dateOfStudyPublicRelease.setText(study.getPublicReleaseDate());
        dateOfStudyPublicRelease.setToolTipText(
                "<html><b>Date of Study Public Release</b><p>When will the study be released for the public domain</p><p><b>Please use the calendar tool to enter the date!</b></p></html>");

        UIHelper.renderComponent(dateOfStudyPublicRelease, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        publicStudyReleaseDateCont.add(createDateDropDown(
                dateOfStudyPublicRelease));

        verticalContainer.add(publicStudyReleaseDateCont);
        verticalContainer.add(Box.createVerticalStrut(5));

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

        factorFields.add(new SubFormField("Factor Name", SubFormField.STRING));
        factorFields.add(new SubFormField("Factor Type",
                SubFormField.SINGLE_ONTOLOGY_SELECT));

        int numColsToAdd = (study.getFactors().size() == 0) ? 1
                : study.getFactors()
                .size();

        factorSubForm = new FactorSubForm("study factors", FieldTypes.FACTOR, factorFields,
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

        protocolFields.add(new SubFormField("Protocol Name", SubFormField.STRING));
        protocolFields.add(new SubFormField("Protocol Type",
                SubFormField.SINGLE_ONTOLOGY_SELECT));
        protocolFields.add(new SubFormField("Protocol Description",
                SubFormField.LONG_STRING));
        protocolFields.add(new SubFormField("Protocol URI", SubFormField.STRING));
        protocolFields.add(new SubFormField("Protocol Version",
                SubFormField.STRING));
        protocolFields.add(new SubFormField("Protocol Parameters Name",
                SubFormField.MULTIPLE_ONTOLOGY_SELECT));
        protocolFields.add(new SubFormField("Protocol Components Name",
                SubFormField.STRING));
        protocolFields.add(new SubFormField("Protocol Components Type", SubFormField.MULTIPLE_ONTOLOGY_SELECT));

        int numColsToAdd = (study.getProtocols().size() == 0) ? 4
                : study.getProtocols()
                .size();

        protocolSubForm = new ProtocolSubForm("study protocols", FieldTypes.PROTOCOL,
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


        publicationFields.add(new SubFormField("PubMed ID", SubFormField.STRING));
        publicationFields.add(new SubFormField("Publication DOI", SubFormField.STRING));
        publicationFields.add(new SubFormField("Publication Author list", SubFormField.LONG_STRING));
        publicationFields.add(new SubFormField("Publication Title", SubFormField.LONG_STRING));
        publicationFields.add(new SubFormField("Publication Status", SubFormField.SINGLE_ONTOLOGY_SELECT));


        int numColsToAdd = (study.getPublications().size() == 0) ? 4
                : study.getPublications()
                .size();

        studyPublicationsSubForm = new PublicationSubForm("study publications", FieldTypes.PUBLICATION,
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
        studyDesignFields.add(new SubFormField("Design Type", SubFormField.SINGLE_ONTOLOGY_SELECT, "OBI"));
        int numColsToAdd = (study.getStudyDesigns().size() == 0) ? 4
                : study.getStudyDesigns().size();

        studyDesignSubform = new StudyDesignSubForm("study design descriptors", FieldTypes.DESIGN,
                studyDesignFields, numColsToAdd, 300, 60, this);
        studyDesignSubform.createGUI();
        return studyDesignSubform;
    }

//	/**
//	 * Create the comments subform    a
//	 *
//	 * @return JPanel containing the Publication definition subform
//	 */
//	private JPanel createStudyCommentsSubForm() {
//		List<SubFormField> studyCommentsFields = new ArrayList<SubFormField>();
//		studyCommentsFields.add(new SubFormField("Comment Type", SubFormField.STRING));
//		studyCommentsFields.add(new SubFormField("Comment", SubFormField.STRING, "OBI"));
//
//		int numColsToAdd = 0;
//
//		studyCommentsSubForm = new CommentSubForm("study comments", FieldTypes.COMMENT,
//				studyCommentsFields, numColsToAdd, 300, 60, this);
//		studyCommentsSubForm.createGUI();
//		return studyCommentsSubForm;
//	}

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
        getDEP().removeFromTree(assayRef);
        // remove assay from the study
        getStudy().removeAssay(assayRef);
        // remove assay from investigation assay list.
        getDep().getInvestigation().getAssays().remove(assayRef);
    }

    /**
     * Output the Study definition in tabular format for output to the ISA-TAB files.
     */
    public String toString() {
        String data = "";
        data += "STUDY\n";
        data += ("Study Identifier\t\"" + studyIdentifier.getText() + "\"\n");
        data += ("Study Title\t\"" + studyTitle.getText() + "\"\n");
        data += ("Study Submission Date\t\"" +
                dateOfStudySubmission.getText() + "\"\n");
        data += ("Study Public Release Date\t\"" +
                dateOfStudyPublicRelease.getText() + "\"\n");
        data += ("Study Description\t\"" + studyDescription.getText() + "\"\n");

        data += ("Study File Name\t\"" + study.getStudySampleFileIdentifier() + "\"\n");

        data += studyDesignSubform.toString();
        data += studyPublicationsSubForm.toString();
        data += factorSubForm.toString();
        data += assaySubForm.toString();
        data += protocolSubForm.toString();
        data += contactSubForm.toString();

        return data;
    }

    /**
     * update method to save all changes in view to the model (Study object)
     */
    public void update() {
        study.setStudyId(studyIdentifier.getText());
        study.setStudyDesc(studyDescription.getText());
        study.setStudyTitle(studyTitle.getText());
        study.setDateOfSubmission(dateOfStudySubmission.getText());
        study.setPublicReleaseDate(dateOfStudyPublicRelease.getText());

        // update subform contents
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
}
