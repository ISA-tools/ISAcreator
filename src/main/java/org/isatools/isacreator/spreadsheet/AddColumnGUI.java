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


package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.autofiltercombo.AutoFilterCombo;
import org.isatools.isacreator.common.DropDownComponent;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Provides the GUI to allow for multiple types of columns to be added
 *
 * @author Eamonn Maguire
 */
public class AddColumnGUI extends JDialog {

    @InjectedResource
    private ImageIcon addCharacteristicButton, addCharacteristicButtonOver, addFactorButton,
            addFactorButtonOver, addParameterButton, addParameterButtonOver, addCommentButton, addCommentButtonOver,
            closeButton, closeButtonOver;

    private final static String UNIT_ONTOLOGY = "UO";

    final static int ADD_FACTOR_COLUMN = 1;
    final static int ADD_CHARACTERISTIC_COLUMN = 2;
    final static int ADD_PARAMETER_COLUMN = 3;
    final static int ADD_COMMENT_COLUMN = 4;

    private AutoFilterCombo optionList;
    private JLabel status;
    private JRadioButton qualitativeOp;
    private JRadioButton quantativeOp;
    private JTextField stdTextField;
    private JTextField unitField;
    private JTextField varSelectOntologyField;
    private Spreadsheet st;
    private int type;
    private DropDownComponent dropdown;
    private static OntologySelectionTool ontologySelectionTool;


    public AddColumnGUI(Spreadsheet st, int type) {
        this.type = type;
        this.st = st;
        ResourceInjector.get("spreadsheet-package.style").inject(this);
    }

    /**
     * Creates the Panels and the fields in them depending on the type of column to add.
     *
     * @param panelType - type of gui to show. e.g. for the addition of a Factor column, the integer
     *                  ADD_FACTOR_COLUMN would be input for the panelType value.
     */
    private void changePanels(int panelType) {
        JPanel containingPanel = new JPanel();
        containingPanel.setBackground(UIHelper.BG_COLOR);

        JPanel headerCont = new JPanel(new GridLayout(1, 1));
        headerCont.setOpaque(false);

        Box container = Box.createVerticalBox();
        container.setBackground(UIHelper.BG_COLOR);

        if (panelType == ADD_FACTOR_COLUMN) {
            List<Factor> factors = st.getStudyDataEntryEnvironment().getFactors();
            String[] terms = new String[factors.size()];

            for (int i = 0; i < factors.size(); i++) {
                terms[i] = factors.get(i).getFactorName();
            }

            headerCont.add(new JLabel(
                    new ImageIcon(getClass()
                            .getResource("/images/spreadsheet/addFactorHeader.png")),
                    JLabel.RIGHT));
            container.add(headerCont);
            container.add(Box.createVerticalStrut(5));
            container.add(createDropDownField("factor", terms));
            container.add(Box.createVerticalStrut(5));
            container.add(createUnitField());
        }

        if (panelType == ADD_CHARACTERISTIC_COLUMN) {
            headerCont.add(new JLabel(
                    new ImageIcon(getClass()
                            .getResource("/images/spreadsheet/addCharacteristicHeader.png")),
                    JLabel.RIGHT));
            container.add(headerCont);
            container.add(createStdOntologyField("characteristic"));
            container.add(Box.createVerticalStrut(5));
            container.add(createUnitField());
        }

        if (panelType == ADD_PARAMETER_COLUMN) {
            headerCont.add(new JLabel(
                    new ImageIcon(getClass()
                            .getResource("/images/spreadsheet/addParameterHeader.png")),
                    JLabel.RIGHT));
            container.add(headerCont);
            container.add(createStdOntologyField("parameter"));
            container.add(Box.createVerticalStrut(5));
            container.add(createUnitField());
        }

        if (panelType == ADD_COMMENT_COLUMN) {
            headerCont.add(new JLabel(
                    new ImageIcon(getClass()
                            .getResource("/images/spreadsheet/addCommentHeader.png")),
                    JLabel.RIGHT));
            container.add(headerCont);

            JLabel lab = new JLabel("Enter comment qualifier");
            UIHelper.createLabel("Enter comment qualifier", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

            stdTextField = new RoundedJTextField(10);

            JPanel commentFieldcont = new JPanel(new GridLayout(1, 2));
            commentFieldcont.setOpaque(false);

            commentFieldcont.add(lab);
            commentFieldcont.add(stdTextField);

            container.add(commentFieldcont);
        }

        containingPanel.add(container, BorderLayout.NORTH);
        add(containingPanel, BorderLayout.CENTER);
    }

    /**
     * Create a dropdown combo box field given an array of data to show in the list, and a String
     * called typeToAdd which will be used to form the associated label field.
     *
     * @param typeToAdd - e.g. Factor
     * @param data      - e.g. new String[]{"dose","compound"};
     * @return JPanel containing drop down field and its associated label.
     */
    private JPanel createDropDownField(String typeToAdd, String[] data) {
        JPanel olsFieldCont = new JPanel(new GridLayout(1, 2));
        olsFieldCont.setBackground(UIHelper.BG_COLOR);

        optionList = new AutoFilterCombo(data, false);
        UIHelper.setJComboBoxAsHeavyweight(optionList);
        UIHelper.renderComponent(optionList, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        JLabel stdFieldLab = UIHelper.createLabel("select " + typeToAdd, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

        olsFieldCont.add(stdFieldLab);

        olsFieldCont.add(optionList);

        return olsFieldCont;
    }

    /**
     * Create the GUI.
     */
    public void createGUI() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                //setMinimumSize(SIZE);
                setBackground(UIHelper.BG_COLOR);

                instantiatePanel();

            }
        });
    }

    /**
     * Create an DropDownComponent field.
     *
     * @param field                     - JTextField to be associated with the OntologySelectionTool.
     * @param allowsMultiple            - Should the OntologySelectionTool allow multiple terms to be selected.
     * @param recommendedOntologySource - A recommended ontology source.
     * @return DropDownComponent object.
     */
    protected DropDownComponent createOntologyDropDown(final JTextField field,
                                                       boolean allowsMultiple, boolean forceOntology, Map<String, RecommendedOntology> recommendedOntologySource) {
        ontologySelectionTool = new OntologySelectionTool(allowsMultiple, forceOntology, recommendedOntologySource);
        ontologySelectionTool.createGUI();

        dropdown = new DropDownComponent(field, ontologySelectionTool, DropDownComponent.ONTOLOGY);
        ontologySelectionTool.addPropertyChangeListener("selectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(ontologySelectionTool);
                        // we need to get the selected terms, get the first object and modify the output so that it gives us something conforming to
                        // term (source:accession)
                        OntologyTerm term = OntologyManager.getUserOntologyHistory().get(evt.getNewValue().toString());
                        if (term != null) {
                            field.setText(getStringForHeaderFromOntologyTerm(term));
                        } else {
                            field.setText(evt.getNewValue().toString());
                        }
                    }
                });

        ontologySelectionTool.addPropertyChangeListener("noSelectedOntology",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        dropdown.hidePopup(ontologySelectionTool);
                    }
                });

        return dropdown;
    }

    private String getStringForHeaderFromOntologyTerm(OntologyTerm ontologyTerm) {
        // we just need the one term.
        return ontologyTerm.getOntologyTermName() + "(" + (ontologyTerm.getOntologyPurl().isEmpty()
                ? ontologyTerm.getOntologySource() + ":" + ontologyTerm.getOntologySourceAccession()
                : ontologyTerm.getOntologyPurl()) + ")";
    }

    /**
     * Create a field which requires ontology lookup.
     *
     * @param typeToAdd - Type of field to add, just for definition of label. e.g. characteristic, parameter.
     * @return JPanel containing the label and the Ontology field.
     */
    private JPanel createStdOntologyField(String typeToAdd) {
        JPanel olsFieldCont = new JPanel(new GridLayout(1, 2));
        olsFieldCont.setBackground(UIHelper.BG_COLOR);

        varSelectOntologyField = new RoundedJTextField(10);

        JLabel stdFieldLab = UIHelper.createLabel("select " + typeToAdd, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

        olsFieldCont.add(stdFieldLab);

        olsFieldCont.add(createOntologyDropDown(varSelectOntologyField, false, false, null));

        return olsFieldCont;
    }

    /**
     * Create a JPanel which is specifically used to deal with creating the Unit fields.
     * Allows for modular approach to building the GUI for different types of columns.
     *
     * @return - JPanel containing the elements.
     */
    private JPanel createUnitField() {
        JPanel unitContainer = new JPanel();
        unitContainer.setLayout(new BoxLayout(unitContainer, BoxLayout.PAGE_AXIS));
        unitContainer.setOpaque(false);

        JPanel unitFieldCont = new JPanel(new GridLayout(1, 2));
        unitFieldCont.setOpaque(false);

        unitField = new RoundedJTextField(10);

        JLabel stdFieldLab = UIHelper.createLabel("select unit", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

        unitFieldCont.add(stdFieldLab);

        final DropDownComponent ontologyDropDown = createOntologyDropDown(unitField, false,
                false, Collections.singletonMap("Unit", new RecommendedOntology(new Ontology("", "", UNIT_ONTOLOGY, "Unit Ontology"))));
        unitFieldCont.add(ontologyDropDown);

        JPanel determineUnitRequired = new JPanel(new GridLayout(1, 2));
        determineUnitRequired.setOpaque(false);

        qualitativeOp = new JRadioButton("qualitative value", false);
        UIHelper.renderComponent(qualitativeOp, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);
        qualitativeOp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (qualitativeOp.isSelected()) {
                    ontologyDropDown.disableElements();
                }
            }
        });

        quantativeOp = new JRadioButton("quantitative value", true);
        UIHelper.renderComponent(quantativeOp, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);
        quantativeOp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (quantativeOp.isSelected()) {
                    ontologyDropDown.enableElements();
                }
            }
        });

        ButtonGroup groupChoices = new ButtonGroup();
        groupChoices.add(qualitativeOp);
        groupChoices.add(quantativeOp);

        determineUnitRequired.add(qualitativeOp);
        determineUnitRequired.add(quantativeOp);

        unitContainer.add(determineUnitRequired);
        unitContainer.add(Box.createVerticalStrut(10));
        unitContainer.add(unitFieldCont);

        return unitContainer;
    }

    /**
     * Create the Panel containing the required elements depending on the type of column being added.
     */
    private void instantiatePanel() {
        status = new JLabel("<html><b></b></html>");
        status.setForeground(UIHelper.RED_COLOR);

        final ImageIcon typeText;
        final ImageIcon[] typeHoverImage = new ImageIcon[1];

        switch (type) {
            case ADD_CHARACTERISTIC_COLUMN:
                typeText = addCharacteristicButton;
                typeHoverImage[0] = addCharacteristicButtonOver;
                break;

            case ADD_FACTOR_COLUMN:
                typeText = addFactorButton;
                typeHoverImage[0] = addFactorButtonOver;
                break;

            case ADD_PARAMETER_COLUMN:
                typeText = addParameterButton;
                typeHoverImage[0] = addParameterButtonOver;
                break;

            case ADD_COMMENT_COLUMN:
                typeText = addCommentButton;
                typeHoverImage[0] = addCommentButtonOver;
                break;

            default:
                typeText = addCommentButton;
        }

        final JLabel addColumn = new JLabel((typeText), JLabel.RIGHT);

        addColumn.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                String typeAsText;

                switch (type) {
                    case ADD_CHARACTERISTIC_COLUMN:
                        typeAsText = "Characteristics";

                        break;

                    case ADD_FACTOR_COLUMN:
                        typeAsText = "Factor Value";

                        break;

                    case ADD_PARAMETER_COLUMN:
                        typeAsText = "Parameter Value";

                        break;

                    case ADD_COMMENT_COLUMN:
                        typeAsText = "Comment";

                        break;

                    default:
                        typeAsText = "add";
                }

                if (type == ADD_COMMENT_COLUMN && stdTextField != null) {
                    String toAdd = stdTextField.getText();

                    if (toAdd.equals("")) {
                        status.setText("<html><b>" + typeAsText +
                                "qualifier missing!</b></html>");
                        status.setForeground(UIHelper.RED_COLOR);

                        return;
                    }

                    String colName = typeAsText + "[" + toAdd + "]";

                    FieldObject newFieldObject = st.getTableReferenceObject().getFieldByName(colName);
                    if (newFieldObject == null) {
                        newFieldObject = new FieldObject(st.getColumnCount(),
                                colName, typeAsText + " value", DataTypes.STRING,
                                "", false, false, false);
                    }

                    st.getSpreadsheetFunctions().addFieldToReferenceObject(newFieldObject);
                    st.getSpreadsheetFunctions().addColumnAfterPosition(colName, null, newFieldObject.isRequired(), -1);

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            st.getParentFrame().hideSheet();
                        }
                    });
                }

                if ((type == ADD_CHARACTERISTIC_COLUMN) ||
                        ((type == ADD_FACTOR_COLUMN) ||
                                (type == ADD_PARAMETER_COLUMN))) {
                    if ((optionList != null) ||
                            (varSelectOntologyField != null)) {
                        String toAdd = "";

                        if (varSelectOntologyField != null) {
                            toAdd = varSelectOntologyField.getText();
                        } else {
                            if (optionList.getSelectedItem() != null) {
                                toAdd = optionList.getSelectedItem()
                                        .toString();
                            }
                        }

                        if (optionList != null && optionList.getSelectedItem() == null) {
                            status.setText(
                                    "<html><b>No factors available</b></html>");
                            status.setForeground(UIHelper.RED_COLOR);

                            return;
                        }

                        if (toAdd.equals("")) {
                            status.setText("<html><b>" + typeAsText +
                                    " missing!</b></html>");
                            status.setForeground(UIHelper.RED_COLOR);

                            return;
                        }

                        if (quantativeOp.isSelected() && unitField.getText().equals("")) {
                            status.setText(
                                    "<html><b>Unit missing!</b></html>");
                            status.setForeground(UIHelper.RED_COLOR);

                            return;
                        }

                        String colName = typeAsText + "[" + toAdd + "]";

                        if (!st.getSpreadsheetFunctions().checkColumnExists(colName)) {
                            boolean useOntology = qualitativeOp.isSelected();
                            DataTypes type = useOntology ? DataTypes.ONTOLOGY_TERM
                                    : DataTypes.STRING;
                            FieldObject charFo = new FieldObject(st.getColumnCount(),
                                    colName, typeAsText + " value", type,
                                    "", false, false, false);
                            st.getSpreadsheetFunctions().addFieldToReferenceObject(charFo);

                            if (!toAdd.equals("")) {
                                st.getSpreadsheetFunctions().addColumnAfterPosition(colName, null, charFo.isRequired(), -1);
                            }

                            if (quantativeOp.isSelected()) {
                                FieldObject unitFo = new FieldObject(st.getColumnCount(),
                                        "Unit",
                                        "Unit for definition of value",
                                        DataTypes.ONTOLOGY_TERM, "", false, false,
                                        false);
                                st.getSpreadsheetFunctions().addFieldToReferenceObject(unitFo);
                                st.getSpreadsheetFunctions().addColumnAfterPosition("Unit",
                                        unitField.getText(), unitFo.isRequired(), -1);
                            }

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    st.getParentFrame().hideSheet();
                                }
                            });
                        } else {
                            status.setText("<html><b>Duplicate " +
                                    typeAsText + "</b></html>");
                            status.setForeground(UIHelper.RED_COLOR);
                        }
                    } else {
                        status.setText(
                                "<html><b>No factors available</b></html>");
                        status.setForeground(UIHelper.RED_COLOR);
                    }
                }
            }

            public void mouseEntered(MouseEvent event) {
                addColumn.setIcon(typeHoverImage[0]);
            }

            public void mouseExited(MouseEvent event) {
                addColumn.setIcon(typeText);
            }
        });

        final JLabel close = new JLabel(closeButton,
                JLabel.LEFT);
        close.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        st.getParentFrame().hideSheet();
                        ontologySelectionTool = null;
                    }
                });
            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(closeButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(closeButton);
            }
        });

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.setBackground(UIHelper.BG_COLOR);

        JPanel buttonCont = new JPanel(new BorderLayout());
        buttonCont.setBackground(UIHelper.BG_COLOR);

        buttonCont.add(close, BorderLayout.WEST);
        buttonCont.add(addColumn, BorderLayout.EAST);

        // show characteristics addition option first since this is also the first item in the whatToAdd comboBox
        changePanels(type);

        southPanel.add(status);
        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(buttonCont);
        southPanel.add(Box.createGlue());

        add(southPanel, BorderLayout.SOUTH);
        pack();
    }
}
