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
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.exportisa.exportadaptors.ISASectionExportAdaptor;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
                UIHelper.GREEN_ROUNDED_BORDER, "investigation description",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        // create box to contain all the fields
        Box fields = Box.createVerticalBox();
        // add a spacer to the layout
        fields.add(Box.createVerticalStrut(5));

        if (investigation.getReferenceObject() == null) {
            TableReferenceObject tro =
                    ConfigurationManager.selectTROForUserSelection(MappingObject.INVESTIGATION);
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

        Set<String> ontologyFields = investigation.getReferenceObject().getOntologyTerms(InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION);
        Set<String> fieldsToIgnore = investigation.getReferenceObject().getFieldsToIgnore();

        for (String contactField : investigation.getReferenceObject().getFieldsForSection(InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION)) {

            if (!investigation.getReferenceObject().getFieldDefinition(contactField).isHidden()) {
                SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, investigation, contactField);
                if (generatedField != null) {
                    contactFields.add(generatedField);
                }
            }
        }

        int numColsToAdd = (investigation.getContacts().size() == 0) ? 4 : investigation.getContacts().size();

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

            if (!investigation.getReferenceObject().getFieldDefinition(publicationField).isHidden()) {
                SubFormField generatedField = generateSubFormField(fieldsToIgnore, ontologyFields, investigation, publicationField);

                if (generatedField != null) {
                    publicationFields.add(generatedField);
                }
            }
        }

        int numColsToAdd = (investigation.getPublications().size() == 0) ? 1
                : investigation.getPublications()
                .size();

        publicationsSubForm = new PublicationSubForm(InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION.toString(),
                FieldTypes.PUBLICATION, publicationFields, numColsToAdd, 300, 125, this);
        publicationsSubForm.createGUI();

        return publicationsSubForm;
    }

    public String toString() {
        update();
        StringBuilder output = new StringBuilder();
        output.append(ISASectionExportAdaptor.exportISASectionAsString(investigation, InvestigationFileSection.INVESTIGATION_SECTION, aliasesToRealNames));

        populateEmptySections();
        output.append(getISASectionAsString(InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION.toString(), getPublications()));
        output.append(getISASectionAsString(InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION.toString(), getContacts()));

        return output.toString();
    }

    private void populateEmptySections() {
        if(getInvestigation().getPublications().size() == 0) {
            getInvestigation().addPublication(new InvestigationPublication());
        }

        if(getInvestigation().getContacts().size() == 0) {
            getInvestigation().addContact(new InvestigationContact());
        }
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
            if (aliasesToRealNames.containsKey(fieldName)) {
                tmpFieldName = aliasesToRealNames.get(fieldName);
            }
            if (fieldDefinitions.get(fieldName) instanceof JTextComponent) {
                investigation.getFieldValues().put(tmpFieldName, ((JTextComponent) fieldDefinitions.get(fieldName)).getText());
            } else if (fieldDefinitions.get(fieldName) instanceof JComboBox) {
                investigation.getFieldValues().put(tmpFieldName, ((JComboBox) fieldDefinitions.get(fieldName)).getSelectedItem().toString());
            }
        }
        publicationsSubForm.update();
        contactsSubform.update();
    }

    public void removeReferences() {

        publicationsSubForm.cleanupReferences();
        publicationsSubForm = null;

        contactsSubform.cleanupReferences();
        contactsSubform = null;

        getDataEntryEnvironment().removeAll();
        setDataEntryEnvironment(null);

        for (String s : investigation.getStudies().keySet()) {
            Study tmpStudy = investigation.getStudies().get(s);
            ApplicationManager.getUserInterfaceForISASection(tmpStudy).removeAll();
        }
        ApplicationManager.getUserInterfaceForISASection(investigation).removeAll();

        investigation = null;
    }
}
