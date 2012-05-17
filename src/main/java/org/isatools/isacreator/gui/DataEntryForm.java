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
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.gui.formelements.SubFormField;
import org.isatools.isacreator.gui.listeners.propertychange.DateChangedCancelledEvent;
import org.isatools.isacreator.gui.listeners.propertychange.DateChangedEvent;
import org.isatools.isacreator.gui.listeners.propertychange.OntologySelectedEvent;
import org.isatools.isacreator.gui.listeners.propertychange.OntologySelectionCancelledEvent;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
import org.isatools.isacreator.utils.StringProcessing;

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

    // this will house the translation between Comment aliases e.g. Publication Journal [c] to Comment[Publication Journal]
    protected Map<String, String> aliasesToRealNames;
    protected Map<String, String> realNamesToAliases;


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

        calendar.addPropertyChangeListener("selectedDate",
                new DateChangedEvent(calendar, dropdown, field));
        calendar.addPropertyChangeListener("noneSelected",
                new DateChangedCancelledEvent(calendar, dropdown));

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

    public JComponent createOntologyDropDown(String fieldName, JTextComponent field,
                                             boolean allowsMultiple, boolean forceOntology, Map<String, RecommendedOntology> recommendedOntologySource) {


        OntologySelectionTool ontologySelectionTool = new OntologySelectionTool(allowsMultiple, forceOntology, recommendedOntologySource);
        ontologySelectionTool.createGUI();

        DropDownComponent dropdown = new DropDownComponent(fieldName, field, ontologySelectionTool, DropDownComponent.ONTOLOGY);

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

    protected void generateAliases(Set<String> fieldValues) {

        if (aliasesToRealNames == null) {
            aliasesToRealNames = new HashMap<String, String>();
            realNamesToAliases = new HashMap<String, String>();
        }


        for (String fieldName : fieldValues) {

            if (fieldName.toLowerCase().startsWith("comment")) {
                String alias = StringProcessing.extractQualifierFromField(fieldName) + " [c]";

                System.out.println("Alias for " + fieldName + " is " + alias);
                aliasesToRealNames.put(alias, fieldName);
                realNamesToAliases.put(fieldName, alias);
            }
        }
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
                    String tmpFieldName = fieldName;

                    if (realNamesToAliases.containsKey(fieldName)) {
                        tmpFieldName = realNamesToAliases.get(fieldName);
                    }

                    JPanel fieldPanel = createFieldPanel(1, 2);
                    JLabel fieldLabel = createLabel(tmpFieldName);

                    JTextComponent textComponent;

                    if (fieldDescriptor.getDatatype() == DataTypes.STRING || fieldDescriptor.getDatatype() == DataTypes.ONTOLOGY_TERM || fieldDescriptor.getDatatype() == DataTypes.DATE) {
                        textComponent = new RoundedJTextField(10);
                    } else if (fieldDescriptor.getDatatype() == DataTypes.LONG_STRING) {
                        textComponent = new JTextArea();

                        textComponent.setSelectionColor(UIHelper.LIGHT_GREEN_COLOR);
                        textComponent.setSelectedTextColor(UIHelper.BG_COLOR);

                        ((JTextArea) textComponent).setWrapStyleWord(true);
                        ((JTextArea) textComponent).setLineWrap(true);
                        textComponent.setBackground(UIHelper.BG_COLOR);
                        textComponent.setBorder(UIHelper.GREEN_ROUNDED_BORDER);
                    } else {
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
                            fieldPanel.add(createOntologyDropDown(fieldName, textComponent, true, false, fieldDescriptor.getRecommmendedOntologySource()));
                        } else if (fieldDescriptor.getDatatype() == DataTypes.DATE) {
                            fieldPanel.add(createDateDropDown(textComponent));
                        } else {
                            fieldPanel.add(textComponent);
                        }
                    }

                    fieldDefinitions.put(tmpFieldName, textComponent);

                    containerToAddTo.add(fieldPanel);
                    containerToAddTo.add(Box.createVerticalStrut(5));
                }
            }
        }
    }


    protected SubFormField generateSubFormField(Set<String> fieldsToIgnore, Set<String> ontologyFields, Study study, String fieldName) {
        FieldObject fieldDescriptor = study.getReferenceObject().getFieldDefinition(fieldName);
        return createField(fieldsToIgnore, ontologyFields, fieldDescriptor, fieldName);
    }

    protected SubFormField generateSubFormField(Set<String> fieldsToIgnore, Set<String> ontologyFields, Investigation investigation, String fieldName) {
        FieldObject fieldDescriptor = investigation.getReferenceObject().getFieldDefinition(fieldName);
        return createField(fieldsToIgnore, ontologyFields, fieldDescriptor, fieldName);

    }

    private SubFormField createField(Set<String> fieldsToIgnore, Set<String> ontologyFields, FieldObject fieldDescriptor, String fieldName) {
        if (!fieldsToIgnore.contains(fieldName)) {

            int fieldType = SubFormField.STRING;

            if (ontologyFields.contains(fieldName)) {
                fieldType = SubFormField.SINGLE_ONTOLOGY_SELECT;

                if (fieldDescriptor != null) {
                    if (fieldDescriptor.isAcceptsMultipleValues()) {
                        fieldType = SubFormField.MULTIPLE_ONTOLOGY_SELECT;
                    }
                }
                return new SubFormField(fieldName, fieldType, fieldDescriptor.getRecommmendedOntologySource());

            } else {

                if (fieldDescriptor != null) {
                    fieldType = translateDataTypeToSubFormFieldType(fieldDescriptor.getDatatype(),
                            fieldDescriptor.isAcceptsMultipleValues());

                    if (fieldType == SubFormField.STRING) {
                        if (fieldDescriptor.isAcceptsFileLocations()) {
                            fieldType = SubFormField.FILE;
                        }
                    }

                    if (fieldType == SubFormField.SINGLE_ONTOLOGY_SELECT || fieldType == SubFormField.MULTIPLE_ONTOLOGY_SELECT) {
                        return new SubFormField(fieldName, fieldType, fieldDescriptor.getRecommmendedOntologySource());
                    }
                }

                if (fieldType == SubFormField.COMBOLIST) {
                    return new SubFormField(fieldName, fieldType, fieldDescriptor.getFieldList());
                } else {
                    return new SubFormField(fieldName, fieldType);
                }

            }
        }

        return null;
    }


}
