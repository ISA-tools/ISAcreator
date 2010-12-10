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

package org.isatools.isacreator.mgrast.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.CustomTextField;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Contact;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * ExtraMetaDataPane contains the entry fields for extra metadata items such as external project ids
 * and so forth.
 *
 * @author Eamonn Maguire
 * @date Sep 24, 2010
 */


public class ExtraMetaDataPane extends JPanel {

    // fields required for:
    //	- project name - internal project id - technical contact - administrative contact - ncbi project id

    private CustomTextField projectName;
    private JTextArea projectDescription;

    private CustomTextField pubmedId;
    private CustomTextField ncbiProjectId;
    private CustomTextField internalProjectId;
    private CustomTextField greengenesStudyId;

    private JComboBox technicalContact;
    private JComboBox administrativeContact;
    private List<Contact> contacts;

    // COMBO box for technical contact
    // COMBO box for administrative contact

    public ExtraMetaDataPane() {

        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        createDataEntryFields();
        layoutItemsOnScreen();
    }

    private void createDataEntryFields() {
        // should be set to the study id.
        projectName = new CustomTextField("project name", true, new Dimension(160, 18));
        pubmedId = new CustomTextField("pubmed id", false, new Dimension(110, 18));
        ncbiProjectId = new CustomTextField("ncbi project id", false, new Dimension(130, 18));
        internalProjectId = new CustomTextField("internal project id", false, new Dimension(110, 18));
        greengenesStudyId = new CustomTextField("greengenes study id", false, new Dimension(130, 18));

        createComboBoxes();

    }

    private Container createTextAreaEntry() {

        // should be set to the study description.
        projectDescription = new JTextArea();
        UIHelper.renderComponent(projectDescription, UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
        projectDescription.setLineWrap(true);
        projectDescription.setWrapStyleWord(true);

        JScrollPane projectDescScroller = new JScrollPane(projectDescription,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        projectDescScroller.setBorder(new LineBorder(UIHelper.GREY_COLOR, 1));
        projectDescScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        projectDescScroller.setPreferredSize(new Dimension(140, 40));

        IAppWidgetFactory.makeIAppScrollPane(projectDescScroller);

        // add text editor button here? I think I will :)

        return projectDescScroller;
    }

    private void createComboBoxes() {

        String[] contactsAsArray = new String[]{"nothing"};

        technicalContact = new JComboBox(contactsAsArray);
        technicalContact.setPreferredSize(new Dimension(140, 20));
        UIHelper.renderComponent(technicalContact, UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
        UIHelper.setJComboBoxAsHeavyweight(technicalContact);

        administrativeContact = new JComboBox(contactsAsArray);
        administrativeContact.setPreferredSize(new Dimension(140, 20));
        UIHelper.renderComponent(administrativeContact, UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
        UIHelper.setJComboBoxAsHeavyweight(administrativeContact);
    }

    private void layoutItemsOnScreen() {

        Box container = Box.createHorizontalBox();

        Box g1 = Box.createVerticalBox();
        g1.add(projectName);
        g1.add(prepareComponentForInsertion("project description", createTextAreaEntry(), 6));

        // add project description here

        container.add(g1);

        Box g2 = Box.createVerticalBox();
        g2.add(internalProjectId);
        g2.add(pubmedId);
        container.add(g2);

        Box g3 = Box.createVerticalBox();
        g3.add(ncbiProjectId);
        g3.add(greengenesStudyId);

        container.add(g3);

        Box g4 = Box.createVerticalBox();
        g4.add(prepareComponentForInsertion("admin contact", administrativeContact, 0));
        g4.add(prepareComponentForInsertion("technical contact", technicalContact, 0));

        container.add(g4);

        add(container, BorderLayout.WEST);
    }

    private Container prepareComponentForInsertion(String componentLabel, Component c, int leftPadding) {
        Box container = Box.createVerticalBox();

        Box labelContainer = Box.createHorizontalBox();

        JLabel field = UIHelper.createLabel(componentLabel, UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, SwingConstants.LEFT);
        labelContainer.add(Box.createHorizontalStrut(leftPadding));
        labelContainer.add(UIHelper.wrapComponentInPanel(field));

        container.add(labelContainer);

        Box componentContainer = Box.createHorizontalBox();

        componentContainer.add(Box.createHorizontalStrut(leftPadding));
        componentContainer.add(c);
        componentContainer.add(Box.createHorizontalStrut(18));

        container.add(componentContainer);

        return container;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;

        technicalContact.removeAllItems();
        administrativeContact.removeAllItems();

        for (Contact c : contacts) {
            technicalContact.addItem(c);
            administrativeContact.addItem(c);
        }
    }

    public boolean areAllFieldsValid() {
        return projectName.isValidInput();
    }

    public void setPubmedId(String projectIdentifier) {
        pubmedId.setText(projectIdentifier);
    }

    public String getPubmedId() {
        return pubmedId.getText();
    }

    public void setProjectName(String projectName) {
        this.projectName.setText(projectName);
    }

    public String getProjectName() {
        return projectName.getText();
    }

    public void setProjectDescription(String projectDesc) {
        projectDescription.setText(projectDesc);
    }

    public String getProjectDescription() {
        return projectDescription.getText();
    }

    public void setInternalProjectId(String internalProjectIdentifier) {
        internalProjectId.setText(internalProjectIdentifier);
    }

    public String getInternalProjectId() {
        return internalProjectId.getText();
    }

    public void setNCBIProjectId(String ncbiProjectIdentifier) {
        ncbiProjectId.setText(ncbiProjectIdentifier);
    }

    public String getNCBIProjectId() {
        return ncbiProjectId.getText();
    }

    public void setGreenegenesStudyId(String greenegenesStudyIdentifier) {
        greengenesStudyId.setText(greenegenesStudyIdentifier);
    }

    public String getGreenegenesStudyId() {
        return greengenesStudyId.getText();
    }

    public Contact getAdminContact() {
        return (Contact) administrativeContact.getSelectedItem();
    }

    public Contact getTechnicalContact() {
        return (Contact) technicalContact.getSelectedItem();
    }

}
