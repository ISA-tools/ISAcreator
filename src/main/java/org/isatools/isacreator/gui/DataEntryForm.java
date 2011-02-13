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

import org.isatools.isacreator.calendar.CalendarGUI;
import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.common.TextEditUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class DataEntryForm extends JLayeredPane implements Serializable {
    private static DataEntryEnvironment dep = null;

    public DataEntryForm(DataEntryEnvironment dep) {
        this.dep = dep;
    }

    public DataEntryForm() {
    }

    public void update() {
        // implemented in subclasses
    }

    public static JComponent createDateDropDown(final JTextField field) {
        final CalendarGUI cal = new CalendarGUI();
        cal.createGUI();

        final DropDownComponent dropdown = new DropDownComponent(field, cal, DropDownComponent.CALENDAR);
        cal.addPropertyChangeListener("selectedDate",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(cal);
                        field.setText(evt.getNewValue().toString());
                    }
                });

        cal.addPropertyChangeListener("noneSelected",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(cal);
                    }
                });

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

    public void setDep(DataEntryEnvironment dep) {
        this.dep = dep;
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

    public static JComponent createOntologyDropDown(final JTextField field,
                                                    boolean allowsMultiple, Map<String, RecommendedOntology> recommendedOntologySource) {
        final OntologySelectionTool ost = new OntologySelectionTool(dep.getParentFrame(),
                allowsMultiple, recommendedOntologySource);
        ost.createGUI();

        final DropDownComponent dropdown = new DropDownComponent(field, ost, DropDownComponent.ONTOLOGY);
        ost.addPropertyChangeListener("selectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(ost);
                        field.setText(evt.getNewValue().toString());
                    }
                });

        ost.addPropertyChangeListener("noSelectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(ost);
                    }
                });

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

    public DataEntryEnvironment getDep() {
        return dep;
    }

    protected ISAcreator getMGUI() {
        return dep.getParentFrame();
    }

    public DataEntryEnvironment getDEP() {
        return dep;
    }

    /**
     * Generic initialisers for frame
     */
    protected void instantiatePane() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setPreferredSize(new Dimension(600, 600));
        setBorder(new EtchedBorder(1, UIHelper.DARK_GREEN_COLOR,
                UIHelper.DARK_GREEN_COLOR));
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
}
