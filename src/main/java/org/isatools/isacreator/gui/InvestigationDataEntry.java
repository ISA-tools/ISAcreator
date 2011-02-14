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
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Publication;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * InvestigationDataEntry
 * Provides the GUI for entering Investigation information into the submission.
 *
 * @author Eamonn Maguire
 */
public class InvestigationDataEntry extends DataEntryForm {
    @InjectedResource
    private ImageIcon panelHeader;


    private Investigation inv;
    private JTextField invId;
    private JTextField invTitle;
    private JTextArea invDesc;
    private JTextField invSubmissionDate;
    private JTextField pubReleaseDate;
    private SubForm publicationsSubForm;
    private SubForm contactsSubform;


    public InvestigationDataEntry(Investigation inv, DataEntryEnvironment dep) {
        super(dep);

        ResourceInjector.get("gui-package.style").inject(this);

        this.inv = inv;
        instantiatePane();
        createFields();
        finalisePane();
    }

    public void createFields() {
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

        // create publication doi fields
        JPanel invIdPanel = createFieldPanel(1, 2);
        JLabel invIdLabel = createLabel("investigation identifier");

        invId = new RoundedJTextField(10);
        invId.setText(inv.getInvestigationIdentifier());
        invId.setToolTipText(
                "<html><b>investigation identifier</b><p>An identifier for the investigation</p>");

        UIHelper.renderComponent(invId, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        invIdPanel.add(invIdLabel);
        invIdPanel.add(invId);

        invDescPanel.add(invIdPanel);
        invDescPanel.add(Box.createVerticalStrut(5));

        // Create investigation title fields and panel to contain components
        JPanel invTitleCont = createFieldPanel(1, 2);

        invTitle = new RoundedJTextField(10);
        invTitle.setText(inv.getInvestigationTitle());

        invTitle.setToolTipText(
                "<html><b>investigation title</b><p>The title of the investigation.</p></html>");

        UIHelper.renderComponent(invTitle, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        JLabel invTitleLab = createLabel("investigation Title");

        invTitleCont.add(invTitleLab);
        invTitleCont.add(createTextEditEnabledField(invTitle));

        invDescPanel.add(invTitleCont);
        invDescPanel.add(Box.createVerticalStrut(5));

        // Create investigation description fields and panel to contain components
        JPanel invDescCont = createFieldPanel(1, 2);

        JLabel invDescLab = createLabel("investigation description");

        invDesc = new JTextArea(inv.getInvestigationDescription());
        invDesc.setWrapStyleWord(true);
        invDesc.setLineWrap(true);
        invDesc.setBackground(UIHelper.BG_COLOR);
        invDesc.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8));

        invDesc.setToolTipText(
                "<html><b>Investigation Description</b><p>A description of the investigation, it's purpose, and so forth.</p></html>");


        JScrollPane invDescScroll = new JScrollPane(invDesc,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        invDescScroll.setPreferredSize(new Dimension(200, 75));

        invDescScroll.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(invDescScroll);

        UIHelper.renderComponent(invDesc, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        invDescCont.add(invDescLab);
        invDescCont.add(UIHelper.createTextEditEnableJTextArea(invDescScroll, invDesc));

        invDescPanel.add(invDescCont);
        invDescPanel.add(Box.createVerticalStrut(5));

        // Create investigation submission date fields and panel to contain components
        JPanel invSubDatePanel = createFieldPanel(1, 2);
        JLabel invSubDateLabel = createLabel("investigation submission date");

        invSubmissionDate = new RoundedJTextField(10);
        invSubmissionDate.setText(inv.getSubmissionDate());
        invSubmissionDate.setToolTipText(
                "<html><b>Investigation Submission Date</b><p>The date the investigation is to be submitted</p></html>");
        UIHelper.renderComponent(invSubmissionDate, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        invSubDatePanel.add(invSubDateLabel);
        invSubDatePanel.add(createDateDropDown(invSubmissionDate));

        invDescPanel.add(invSubDatePanel);
        invDescPanel.add(Box.createVerticalStrut(5));

        // Create investigation submission date fields and panel to contain components
        JPanel pubRelDatePanel = createFieldPanel(1, 2);
        JLabel pubRelDateLabel = createLabel(
                "investigation public release date");

        pubReleaseDate = new RoundedJTextField(10);
        pubReleaseDate.setText(inv.getPublicReleaseDate());
        pubReleaseDate.setToolTipText(
                "<html><b>Public Release Date</b><p>The date when the investigation is to be publicly released</p></html>");
        UIHelper.renderComponent(pubReleaseDate, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        pubRelDatePanel.add(pubRelDateLabel);
        pubRelDatePanel.add(createDateDropDown(pubReleaseDate));

        invDescPanel.add(pubRelDatePanel);

        invDescPanel.add(Box.createVerticalStrut(5));

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

        int numColsToAdd = (inv.getContacts().size() == 0) ? 4
                : inv.getContacts()
                .size();

        contactsSubform = new ContactSubForm("investigation contacts", FieldTypes.CONTACT,
                contactFields, numColsToAdd, 300, 195, this);
        contactsSubform.createGUI();

        return contactsSubform;
    }


    private JPanel createInvestigationPublicationSubForm() {
        List<SubFormField> publicationFields = new ArrayList<SubFormField>();

        publicationFields.add(new SubFormField("PubMed ID", SubFormField.STRING));
        publicationFields.add(new SubFormField("Publication DOI",
                SubFormField.STRING));
        publicationFields.add(new SubFormField("Publication Author list",
                SubFormField.LONG_STRING));
        publicationFields.add(new SubFormField("Publication Title",
                SubFormField.LONG_STRING));
        publicationFields.add(new SubFormField("Publication Status",
                SubFormField.SINGLE_ONTOLOGY_SELECT));

        int numColsToAdd = (inv.getPublications().size() == 0) ? 4
                : inv.getPublications()
                .size();

        publicationsSubForm = new PublicationSubForm("investigation publications",
                FieldTypes.PUBLICATION, publicationFields, numColsToAdd, 300, 125, this);
        publicationsSubForm.createGUI();

        return publicationsSubForm;
    }

    public String toString() {
        String data = "INVESTIGATION\n";

        boolean displayInvestigationInfo = inv.getStudies().size() > 1;

        String invIdStr = displayInvestigationInfo ? invId.getText() : "";
        data += ("Investigation Identifier\t\"" + invIdStr + "\"\n");
        String invTitleStr = displayInvestigationInfo ? invTitle.getText() : "";
        data += ("Investigation Title\t\"" + invTitleStr + "\"\n");
        String invDescStr = displayInvestigationInfo ? invDesc.getText() : "";
        data += ("Investigation Description\t\"" + invDescStr + "\"\n");
        String invSubmissionDateStr = displayInvestigationInfo ? invSubmissionDate.getText() : "";
        data += ("Investigation Submission Date\t\"" +
                invSubmissionDateStr + "\"\n");
        String invPubReleaseDateStr = displayInvestigationInfo ? pubReleaseDate.getText() : "";
        data += ("Investigation Public Release Date\t\"" +
                invPubReleaseDateStr + "\"\n");

        data += publicationsSubForm.toString();
        data += contactsSubform.toString();


        return data;
    }

    public List<Contact> getContacts() {
        return inv.getContacts();
    }

    @Override
    public List<Publication> getPublications() {
        return inv.getPublications();
    }

    @Override
    public Investigation getInvestigation() {
        return inv;
    }

    public void update() {
        inv.setInvestigationIdentifier(invId.getText());
        inv.setInvestigationTitle(invTitle.getText());
        inv.setInvestigationDescription(invDesc.getText());
        inv.setPublicReleaseDate(pubReleaseDate.getText());
        inv.setSubmissionDate(invSubmissionDate.getText());

        publicationsSubForm.update();
        contactsSubform.update();
    }
}
