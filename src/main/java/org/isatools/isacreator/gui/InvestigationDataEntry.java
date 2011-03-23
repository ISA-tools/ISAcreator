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
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.investigationfileproperties.InvestigationFileSection;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Publication;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isacreator.utils.StringProcessing;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * InvestigationDataEntry
 * Provides the GUI for entering Investigation information into the submission.
 *
 * @author Eamonn Maguire
 */
public class InvestigationDataEntry extends DataEntryForm {
    @InjectedResource
    private ImageIcon panelHeader;

    private Investigation investigation;

    private SubForm publicationsSubForm;
    private SubForm contactsSubform;



    public InvestigationDataEntry(Investigation investigation, DataEntryEnvironment dep) {
        super(dep);

        ResourceInjector.get("gui-package.style").inject(this);

        this.investigation = investigation;
        instantiatePane();
        generateAliases(investigation.getFieldValues().keySet());
        createInvestigationSectionFields();
        finalisePane();
    }

    private void createInvestigationSectionFields() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        JPanel invDescPanel = new JPanel();
        invDescPanel.setLayout(new BoxLayout(invDescPanel, BoxLayout.PAGE_AXIS));
        UIHelper.renderComponent(invDescPanel, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);
        invDescPanel.setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), "investigation description",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        // create box to contain all the fields
        Box fields = Box.createVerticalBox();
        // add a spacer to the layout
        fields.add(Box.createVerticalStrut(5));

        if (investigation.getReferenceObject() == null) {
            TableReferenceObject tro = getISAcreatorEnvironment().selectTROForUserSelection(MappingObject.INVESTIGATION);

            DataEntryReferenceObject referenceObject = new DataEntryReferenceObject();
            referenceObject.setFieldDefinition(tro.getTableFields().getFields());

            investigation.setReferenceObject(referenceObject);
        }

        addFieldsToPanel(invDescPanel, InvestigationFileSection.INVESTIGATION_SECTION, investigation.getFieldValues(), investigation.getReferenceObject());

        fields.add(invDescPanel);

        fields.add(Box.createVerticalStrut(20));

        fields.add(createInvestigationPublicationSubForm());
        fields.add(Box.createVerticalStrut(20));

        fields.add(createInvestigationContactsSubForm());
        fields.add(Box.createVerticalStrut(20));

        fields.add(Box.createGlue());

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBackground(UIHelper.BG_COLOR);
        northPanel.add(fields, BorderLayout.CENTER);

        JLabel header = new JLabel(panelHeader,
                JLabel.RIGHT);
        northPanel.add(header, BorderLayout.NORTH);
        container.add(northPanel, BorderLayout.NORTH);

        JScrollPane containerScroller = new JScrollPane(container,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        containerScroller.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(containerScroller);

        add(containerScroller);
    }

    /**
     * Create the Contacts subform for the definition of contacts in the Study form.
     *
     * @return - a JPanel containing the Contacts subform.
     */
    private JPanel createInvestigationContactsSubForm() {
        List<SubFormField> contactFields = new ArrayList<SubFormField>();

        //todo this will have to also take into consideration a config.xml which describes the investigation.
        // todo the desired fields will be the union of both sets of fields.

        Set<String> ontologyFields = investigation.getReferenceObject().getOntologyTerms(InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION);
        Set<String> fieldsToIgnore = investigation.getReferenceObject().getFieldsToIgnore();

        for (String contactField : investigation.getReferenceObject().getFieldsForSection(InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION)) {

            FieldObject fieldDescriptor = investigation.getReferenceObject().getFieldDefinition(contactField);

            if (!fieldsToIgnore.contains(contactField)) {
                if (ontologyFields.contains(contactField)) {
                    int fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    contactFields.add(new SubFormField(contactField, fieldType));
                } else {

                    contactFields.add(new SubFormField(contactField,
                            translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(), fieldDescriptor.isAcceptsMultipleValues())));
                }
            }
        }

        int numColsToAdd = (investigation.getContacts().size() == 0) ? 4
                : investigation.getContacts()
                .size();

        contactsSubform = new ContactSubForm(InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION.toString(), FieldTypes.CONTACT,
                contactFields, numColsToAdd, 300, 195, this);
        contactsSubform.createGUI();

        return contactsSubform;
    }


    private JPanel createInvestigationPublicationSubForm() {
        List<SubFormField> publicationFields = new ArrayList<SubFormField>();

        Set<String> ontologyFields = investigation.getReferenceObject().getOntologyTerms(InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION);
        Set<String> fieldsToIgnore = investigation.getReferenceObject().getFieldsToIgnore();
        for (String publicationField : investigation.getReferenceObject().getFieldsForSection(InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION)) {

            FieldObject fieldDescriptor = investigation.getReferenceObject().getFieldDefinition(publicationField);

            if (!fieldsToIgnore.contains(publicationField)) {
                if (ontologyFields.contains(publicationField)) {
                    int fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                    if (fieldDescriptor != null)
                        if (fieldDescriptor.isAcceptsMultipleValues())
                            fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;

                    publicationFields.add(new SubFormField(publicationField, fieldType));
                } else {
                    publicationFields.add(new SubFormField(publicationField,
                            translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(), fieldDescriptor.isAcceptsMultipleValues())));
                }
            }
        }

        int numColsToAdd = (investigation.getPublications().size() == 0) ? 1
                : investigation.getPublications()
                .size();

        // todo should calculate the height of the subform based on the number of fields.
        publicationsSubForm = new PublicationSubForm(InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION.toString(),
                FieldTypes.PUBLICATION, publicationFields, numColsToAdd, 300, 125, this);
        publicationsSubForm.createGUI();

        return publicationsSubForm;
    }

    public String toString() {
        update();

        StringBuffer output = new StringBuffer();
        output.append(InvestigationFileSection.INVESTIGATION_SECTION).append("\n");

        boolean displayInvestigationInfo = investigation.getStudies().size() > 1;

        Set<String> ontologyFields = IOUtils.filterFields(investigation.getFieldValues().keySet(), IOUtils.ACCESSION, IOUtils.SOURCE_REF);

        Map<Integer, Map<String, String>> ontologyTerms = IOUtils.getOntologyTerms(investigation.getFieldValues().keySet());
        // now, do ontology processing
        for (String fieldName : ontologyFields) {

            int fieldHashCode = fieldName.substring(0, fieldName.toLowerCase().indexOf("term")).trim().hashCode();


            if (ontologyTerms.containsKey(fieldHashCode)) {

                Map<String, String> ontologyField = ontologyTerms.get(fieldHashCode);

                Map<String, String> processedOntologyField = processOntologyField(ontologyField, investigation.getFieldValues());

                investigation.getFieldValues().put(ontologyField.get(IOUtils.TERM), processedOntologyField.get(processedOntologyField.get(IOUtils.TERM)));
                investigation.getFieldValues().put(ontologyField.get(IOUtils.ACCESSION), processedOntologyField.get(processedOntologyField.get(IOUtils.ACCESSION)));
                investigation.getFieldValues().put(ontologyField.get(IOUtils.SOURCE_REF), processedOntologyField.get(processedOntologyField.get(IOUtils.SOURCE_REF)));
            }
        }

        // now, do output
        for (String fieldName : investigation.getFieldValues().keySet()) {

            String tmpFieldName = fieldName;

            if(aliasesToRealNames.containsKey(fieldName)) {
                tmpFieldName = aliasesToRealNames.get(fieldName);
            }

            output.append(tmpFieldName).append("\t\"").append(displayInvestigationInfo ?
                    StringProcessing.cleanUpString(investigation.getFieldValues().get(tmpFieldName)) : "").append("\"\n");
        }

        output.append(publicationsSubForm.toString());
        output.append(contactsSubform.toString());

        return output.toString();
    }

    public List<Contact> getContacts() {
        return investigation.getContacts();
    }

    @Override
    public List<Publication> getPublications() {
        return investigation.getPublications();
    }

    @Override
    public Investigation getInvestigation() {
        return investigation;
    }

    public void update() {

        for (String fieldName : fieldDefinitions.keySet()) {

            String tmpFieldName = fieldName;

            if(aliasesToRealNames.containsKey(fieldName)) {
                tmpFieldName = aliasesToRealNames.get(fieldName);
            }

            investigation.getFieldValues().put(tmpFieldName, fieldDefinitions.get(fieldName).getText());
        }

        publicationsSubForm.update();
        contactsSubform.update();
    }

    public void removeReferences() {

        publicationsSubForm.setDataEntryEnvironment(null);
        publicationsSubForm.setParent(null);
        publicationsSubForm = null;

        contactsSubform.setDataEntryEnvironment(null);
        contactsSubform.setParent(null);
        contactsSubform = null;

        setDataEntryEnvironment(null);

        for (String s : investigation.getStudies().keySet()) {
            Study tmpStudy = investigation.getStudies().get(s);
            tmpStudy.getUserInterface().removeReferences();
            tmpStudy.setUI(null);
        }

        investigation.getUserInterface().removeAll();
        investigation.setUserInterface(null);

        investigation = null;
    }
}
