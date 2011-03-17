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
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.calendar.CalendarGUI;
import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.common.TextEditUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.formelements.SubFormField;
import org.isatools.isacreator.gui.listeners.propertychange.DateChangedCancelledEvent;
import org.isatools.isacreator.gui.listeners.propertychange.DateChangedEvent;
import org.isatools.isacreator.gui.listeners.propertychange.OntologySelectedEvent;
import org.isatools.isacreator.gui.listeners.propertychange.OntologySelectionCancelledEvent;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.io.importisa.investigationfileproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataEntryForm extends JLayeredPane implements Serializable {

    private DataEntryEnvironment dataEntryEnvironment;

    protected OrderedMap<String, JTextComponent> fieldDefinitions;

    public DataEntryForm(DataEntryEnvironment dataEntryEnvironment) {
        this.dataEntryEnvironment = dataEntryEnvironment;
    }

    public DataEntryForm() {
    }

    public void update() {
        // implemented in subclasses
    }

    public JComponent createDateDropDown(JTextComponent field) {

        CalendarGUI calendar = new CalendarGUI();
        calendar.createGUI();

        DropDownComponent dropdown = new DropDownComponent(field, calendar, DropDownComponent.CALENDAR);

        calendar.addPropertyChangeListener("selectedDate", new DateChangedEvent(calendar, dropdown, field));

        calendar.addPropertyChangeListener("noneSelected", new DateChangedCancelledEvent(calendar, dropdown));

        return dropdown;
    }

    /**
     * Create a field panel which has a grid layout, and is opaque
     *
     * @param rows    - Number of rows to be added for the field
     * @param columns - Number of columns to be added
     * @return JPanel with a GridLayout with the rows and columns specified
     */
    public JPanel createFieldPanel(int rows, int columns) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(rows, columns));

        panel.setOpaque(false);

        return panel;
    }

    public void setDataEntryEnvironment(DataEntryEnvironment dataEntryEnvironment) {
        this.dataEntryEnvironment = dataEntryEnvironment;

    }

    /**
     * Method to be overridden by subclasses for creating all fields
     */
    public void createFields() {
    }

    public JLabel createLabel(String labelName) {
        JLabel label = UIHelper.createLabel(labelName, UIHelper.VER_11_BOLD);
        label.setVerticalAlignment(JLabel.TOP);
        label.setHorizontalAlignment(JLabel.LEFT);

        return label;
    }

    public JComponent createOntologyDropDown(JTextComponent field,
                                             boolean allowsMultiple, Map<String, RecommendedOntology> recommendedOntologySource) {

        OntologySelectionTool ontologySelectionTool = new OntologySelectionTool(dataEntryEnvironment.getParentFrame(),
                allowsMultiple, recommendedOntologySource);
        ontologySelectionTool.createGUI();

        DropDownComponent dropdown = new DropDownComponent(field, ontologySelectionTool, DropDownComponent.ONTOLOGY);

        ontologySelectionTool.addPropertyChangeListener("selectedOntology", new OntologySelectedEvent(ontologySelectionTool, dropdown, field));

        ontologySelectionTool.addPropertyChangeListener("noSelectedOntology", new OntologySelectionCancelledEvent(ontologySelectionTool, dropdown));

        return dropdown;
    }

    protected JPanel createTextEditEnabledField(JTextComponent component) {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        container.add(component, BorderLayout.CENTER);

        TextEditUtility textEdit = new TextEditUtility(component);
        textEdit.setVerticalAlignment(SwingConstants.TOP);

        JPanel textEditPanel = new JPanel();
        UIHelper.setLayoutForEditingIcons(textEditPanel, textEdit);

        textEditPanel.setSize(new Dimension(23, 23));

        container.add(textEditPanel, BorderLayout.EAST);

        return container;
    }


    /**
     * Final step in setting up the JLayeredPane
     */
    protected void finalisePane() {
        setVisible(true);
    }

    protected ISAcreator getISAcreatorEnvironment() {
        return dataEntryEnvironment.getParentFrame();
    }

    public DataEntryEnvironment getDataEntryEnvironment() {
        return dataEntryEnvironment;
    }

    /**
     * Generic initialisers for frame
     */
    protected void instantiatePane() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setPreferredSize(new Dimension(600, 600));
        setBorder(BorderFactory.createLineBorder(UIHelper.LIGHT_GREEN_COLOR));
    }

    public Map<String, Assay> getAssays() {
        return null;
    }

    public List<Factor> getFactors() {
        return null;
    }

    public List<Contact> getContacts() {
        return null;
    }

    public List<Protocol> getProtocols() {
        return null;
    }

    public Study getStudy() {
        return null;
    }

    public Investigation getInvestigation() {
        return null;
    }

    public void removeAssay(String ref) {

    }

    public List<Publication> getPublications() {
        return null;
    }

    public List<StudyDesign> getDesigns() {
        return null;
    }


    public int translateDataTypeToSubFormFieldType(DataTypes dataType, boolean acceptsMultipleValues) {
        if (dataType == DataTypes.ONTOLOGY_TERM) {
            return acceptsMultipleValues ? SubFormField.MULTIPLE_ONTOLOGY_SELECT : SubFormField.SINGLE_ONTOLOGY_SELECT;
        }

        if (dataType == DataTypes.DATE) {
            return SubFormField.DATE;
        }

        if (dataType == DataTypes.LONG_STRING) {
            return SubFormField.LONG_STRING;
        }

        if (dataType == DataTypes.LIST) {
            return SubFormField.COMBOLIST;
        }

        return SubFormField.STRING;

    }

    /**
     * Adds the fields describing a section to a Container (e.g. JPanel, Box, etc.)
     *
     * @param containerToAddTo @see JPanel, @see Box, @see  Container
     * @param sectionToAddTo   - @see InvestigationFileSection as a reference for where the fields are being added
     * @param fieldValues      - Map of field name to values.
     * @param referenceObject  - A @see DataEntryReferenceObject which gives information about
     */
    public void addFieldsToPanel(Container containerToAddTo, InvestigationFileSection sectionToAddTo, OrderedMap<String, String> fieldValues, DataEntryReferenceObject referenceObject) {

        if (fieldDefinitions == null) {
            fieldDefinitions = new ListOrderedMap<String, JTextComponent>();
        }

        Set<String> ontologyFields = referenceObject.getOntologyTerms(sectionToAddTo);
        Set<String> fieldsToIgnore = referenceObject.getFieldsToIgnore();

        for (String fieldName : fieldValues.keySet()) {

            if (!fieldsToIgnore.contains(fieldName)) {

                FieldObject fieldDescriptor = referenceObject.getFieldDefinition(fieldName);

                if (!fieldDescriptor.isHidden()) {


                    JPanel fieldPanel = createFieldPanel(1, 2);
                    JLabel fieldLabel = createLabel(fieldName);

                    JTextComponent textComponent;

                    if (fieldDescriptor.getDatatype() == DataTypes.STRING || fieldDescriptor.getDatatype() == DataTypes.ONTOLOGY_TERM || fieldDescriptor.getDatatype() == DataTypes.DATE) {
                        textComponent = new RoundedJTextField(10);
                    } else if (fieldDescriptor.getDatatype() == DataTypes.LONG_STRING) {
                        textComponent = new JTextArea();
                        ((JTextArea) textComponent).setWrapStyleWord(true);
                        ((JTextArea) textComponent).setLineWrap(true);
                        textComponent.setBackground(UIHelper.BG_COLOR);
                        textComponent.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 8));
                    } else {
                        // todo should add in the option to have a combobox...
                        textComponent = new RoundedJTextField(10);
                    }


                    textComponent.setText(fieldValues.get(fieldName).equals("")
                            ? fieldDescriptor.getDefaultVal() : fieldValues.get(fieldName));
                    textComponent.setToolTipText(fieldDescriptor.getDescription());

                    UIHelper.renderComponent(textComponent, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

                    fieldPanel.add(fieldLabel);

                    if (textComponent instanceof JTextArea) {

                        JScrollPane invDescScroll = new JScrollPane(textComponent,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                        invDescScroll.setPreferredSize(new Dimension(200, 75));

                        invDescScroll.getViewport().setBackground(UIHelper.BG_COLOR);

                        IAppWidgetFactory.makeIAppScrollPane(invDescScroll);
                        fieldPanel.add(UIHelper.createTextEditEnableJTextArea(invDescScroll, textComponent));
                    } else {

                        if (fieldDescriptor.getDatatype() == DataTypes.ONTOLOGY_TERM || ontologyFields.contains(fieldName)) {
                            fieldPanel.add(createOntologyDropDown(textComponent, true, fieldDescriptor.getRecommmendedOntologySource()));
                        } else if (fieldDescriptor.getDatatype() == DataTypes.DATE) {
                            fieldPanel.add(createDateDropDown(textComponent));
                        } else {
                            fieldPanel.add(textComponent);
                        }
                    }

                    fieldDefinitions.put(fieldName, textComponent);

                    containerToAddTo.add(fieldPanel);
                    containerToAddTo.add(Box.createVerticalStrut(5));
                }
            }

        }
    }

    /**
     * @param ontologyTerms
     * @param fieldValues
     * @return
     */
    public Map<String, String> processOntologyField(Map<String, String> ontologyTerms, Map<String, String> fieldValues) {


        String term = fieldValues.get(ontologyTerms.get(IOUtils.TERM));

        // At this point, we not have the term, accession and source ref fields. Next, is to set them to their correct values

        String tmpTerm = "";
        String tmpAccession = "";
        String tmpSourceRefs = "";

        if (term.contains(";")) {
            // then we have multiple values
            String[] ontologies = term.split(";");

            int numberAdded = 0;
            for (String ontologyTerm : ontologies) {
                OntologyObject oo = getDataEntryEnvironment().getUserHistory().get(ontologyTerm);

                if (oo.getTerm() != null) {
                    tmpTerm += oo.getTerm();
                    tmpAccession += oo.getTermAccession();
                    tmpSourceRefs += oo.getTermSourceRef();
                } else {
                    if (term.contains(":")) {
                        String[] termAndSource = term.split(":");

                        tmpSourceRefs += termAndSource[0];
                        tmpTerm += termAndSource[1];
                    }
                }


                if (numberAdded < ontologies.length - 1) {
                    tmpTerm += ";";
                    tmpAccession += ";";
                    tmpSourceRefs += ";";
                }
                numberAdded++;
            }

        } else {
            if (term.contains(":")) {
                OntologyObject oo = getDataEntryEnvironment().getUserHistory().get(term);

                if (oo.getTerm() != null) {
                    tmpTerm = oo.getTerm();
                    tmpAccession = oo.getTermAccession();
                    tmpSourceRefs = oo.getTermSourceRef();
                } else {
                    if (term.contains(":")) {
                        String[] termAndSource = term.split(":");

                        tmpSourceRefs = termAndSource[0];
                        tmpTerm = termAndSource[1];
                    } else {
                        tmpTerm = term;
                        tmpAccession = "";
                        tmpSourceRefs = "";
                    }
                }
            } else {
                tmpTerm = term;
                tmpAccession = "";
                tmpSourceRefs = "";
            }
        }

        Map<String, String> result = new HashMap<String, String>();

        result.put(ontologyTerms.get(IOUtils.TERM), tmpTerm);
        result.put(ontologyTerms.get(IOUtils.ACCESSION), tmpAccession);
        result.put(ontologyTerms.get(IOUtils.SOURCE_REF), tmpSourceRefs);
        return result;
    }


}
