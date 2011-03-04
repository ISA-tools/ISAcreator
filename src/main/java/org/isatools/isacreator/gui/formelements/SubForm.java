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

package org.isatools.isacreator.gui.formelements;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.log4j.Logger;
import org.isatools.isacreator.autofiltercombo.AutoFilterCombo;
import org.isatools.isacreator.autofiltercombo.AutoFilterComboCellEditor;
import org.isatools.isacreator.calendar.DateCellEditor;
import org.isatools.isacreator.common.ExcelAdaptor;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.factorlevelentry.FactorLevelEntryCellEditor;
import org.isatools.isacreator.filechooser.FileSelectCellEditor;
import org.isatools.isacreator.gui.*;
import org.isatools.isacreator.longtexteditor.TextCellEditor;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.ontologyselectiontool.OntologySourceManager;
import org.isatools.isacreator.utils.GeneralUtils;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * SubForm
 *
 * @author Eamonn Maguire
 * @date Jan 11, 2010
 */


public abstract class SubForm extends JPanel implements ListSelectionListener, FocusListener {
    private static final Logger log = Logger.getLogger(SubForm.class.getName());

    protected static SubFormLockedCellRenderer lockedTableHeaderRenderer = new SubFormLockedCellRenderer();
    protected static SubFormHeaderRenderer scrollTableHeaderRenderer = new SubFormHeaderRenderer();

    private static final int numFrozenColumns = 1;

    @InjectedResource
    protected ImageIcon ontologyLookupHelp, textEditHelp, confirmRemoveColumn, addRecordIcon, addRecordIconOver,
            removeIcon, removeIconOver, selectFromHistoryIcon, selectFromHistoryIconOver, searchIcon, searchIconOver;

    private static final SubFormCellRenderer DEFAULT_LOCKED_TABLE_RENDERER = new SubFormCellRenderer(UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, new Color(141, 198, 63, 40));
    private static final SubFormCellRenderer DEFAULT_SCROLL_TABLE_RENDERER = new SubFormCellRenderer(UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, null);
    protected String title;
    protected DefaultTableModel dtm;
    protected ExtendedJTable lockedTable;
    protected ExtendedJTable scrollTable;

    private JScrollPane frozenTable;

    protected List<SubFormField> fields;

    protected RowEditor rowEditor = new RowEditor();
    protected FieldTypes fieldType;
    protected DataEntryForm parent;
    protected DataEntryEnvironment dataEntryEnvironment;

    private boolean createBorder = true;

    protected Map<String, OntologyObject> userHistory;
    protected Set<Integer> uneditableRecords = new HashSet<Integer>();


    protected int initialNoFields;
    protected int width;
    protected int height;

    // we only ever create one textcelleditor.
    protected static TextCellEditor longTextEditor = new TextCellEditor();

    // we create a static instance because the one component can be used across multiple SubForm elements in this way
    protected static EmptyBorder em = new EmptyBorder(0, 0, 0, 0);
    protected JLabel removeRecord;

    protected JPanel options;
    private boolean showRemoveOption = true;

    public SubForm(String title, FieldTypes fieldType, List<SubFormField> fields, DataEntryEnvironment dataEntryEnvironment) {
        this(title, fieldType, fields, dataEntryEnvironment, true);
    }

    public SubForm(String title, FieldTypes fieldType, List<SubFormField> fields, DataEntryEnvironment dataEntryEnvironment, boolean createBorder) {
        this.title = title;
        this.fieldType = fieldType;
        this.fields = fields;
        this.dataEntryEnvironment = dataEntryEnvironment;

        this.createBorder = createBorder;
    }

    public SubForm(String title, FieldTypes fieldType,
                   List<SubFormField> fields, int initialNoFields, int width,
                   int height, DataEntryForm parent) {
        this(title, fieldType, fields, initialNoFields, width, height, parent, true);
    }

    public SubForm(String title, FieldTypes fieldType,
                   List<SubFormField> fields, int initialNoFields, int width,
                   int height, DataEntryForm parent, boolean createBorder) {
        this.title = title;
        this.fieldType = fieldType;
        this.fields = fields;
        this.initialNoFields = initialNoFields;
        this.width = width;
        this.height = height;
        this.parent = parent;
        this.createBorder = createBorder;

        if (parent instanceof DataEntryEnvironment) {
            this.dataEntryEnvironment = (DataEntryEnvironment) parent;
        } else {
            this.dataEntryEnvironment = parent.getDataEntryEnvironment();
        }
    }

    protected void initialisePanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(UIHelper.BG_COLOR);

        ResourceInjector.get("gui-package.style").inject(true, this);

        if (createBorder) {
            setBorder(new TitledBorder(
                    new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), title,
                    TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.CENTER,
                    UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));
        } else {
            setBorder(new EmptyBorder(2, 2, 2, 2));
        }
    }

    public JScrollPane getFrozenTable(DefaultTableModel model, int width,
                                      int height) {
        // number of initial records equal to the number of columns in the model - the first column which contains field names!
        frozenTable = new JScrollPane();

        IAppWidgetFactory.makeIAppScrollPane(frozenTable);

        lockedTable = new ExtendedJTable(model, rowEditor);
        setTableProperties(lockedTable, true);

        //lockedTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        scrollTable = new ExtendedJTable(model, rowEditor);
        setTableProperties(scrollTable, false);
        scrollTable.getSelectionModel()
                .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        scrollTable.getTableHeader().setResizingAllowed(true);

        try {
            lockedTable.setDefaultRenderer(Class.forName("java.lang.Object"),
                    DEFAULT_LOCKED_TABLE_RENDERER);

            scrollTable.setDefaultRenderer(Class.forName("java.lang.Object"),
                    DEFAULT_SCROLL_TABLE_RENDERER);
        } catch (ClassNotFoundException e) {
            // ignore
        }

        scrollTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        scrollTable.getSelectionModel().addListSelectionListener(this);
        scrollTable.getColumnModel().getSelectionModel().addListSelectionListener(this);
        scrollTable.addFocusListener(this);
        scrollTable.setBackground(UIHelper.BG_COLOR);

        UIHelper.renderComponent(scrollTable.getTableHeader(), UIHelper.VER_12_PLAIN, UIHelper.BG_COLOR, new Color(141, 198, 63, 40));

        new ExcelAdaptor(scrollTable, false);
        frozenTable.setViewportView(scrollTable);
        frozenTable.setBorder(em);

        setupTableTabBehaviour();

        JTableHeader lockedHeader = lockedTable.getTableHeader();
        setHeaderProperties(lockedTable, lockedTableHeaderRenderer);
        lockedHeader.setReorderingAllowed(false);
        lockedHeader.setResizingAllowed(false);
        frozenTable.setCorner(JScrollPane.UPPER_LEFT_CORNER, lockedHeader);


        setHeaderProperties(scrollTable, scrollTableHeaderRenderer);

        JViewport viewport = new JViewport();
        viewport.setBackground(UIHelper.BG_COLOR);
        viewport.setView(lockedTable);
        frozenTable.setRowHeader(viewport);

        updateTables();

        frozenTable.setPreferredSize(new Dimension(width, height));
        frozenTable.getViewport().setBackground(UIHelper.BG_COLOR);
        frozenTable.getHorizontalScrollBar().setBackground(UIHelper.BG_COLOR);


        return frozenTable;

    }

    public JScrollPane getFrozenTable() {
        return frozenTable;
    }

    public ExtendedJTable getScrollTable() {
        return scrollTable;
    }

    public void updateTables() {
        TableColumnModel scrollColumnModel = scrollTable.getColumnModel();

        for (int i = 0; i < numFrozenColumns; i++) {
            scrollColumnModel.removeColumn(scrollColumnModel.getColumn(0));
        }

        TableColumnModel lockedColumnModel = lockedTable.getColumnModel();

        while (lockedTable.getColumnCount() > numFrozenColumns) {
            lockedColumnModel.removeColumn(lockedColumnModel.getColumn(
                    numFrozenColumns));
        }

        for (int i = 0; i < scrollTable.getColumnCount(); i++) {
            scrollColumnModel.getColumn(i).setPreferredWidth(100);
        }

        lockedTable.getColumnModel().getColumn(0).setPreferredWidth(220);
        lockedTable.setPreferredScrollableViewportSize(lockedTable.getPreferredSize());

        setHeaderProperties(scrollTable, scrollTableHeaderRenderer);
        setHeaderProperties(lockedTable, lockedTableHeaderRenderer);
    }


    /**
     * Returns subform as an 2D array of Strings
     *
     * @return String[][] containing representation of data
     */
    public String[][] getDataAsArray() {
        java.util.List<String[]> data = new ArrayList<String[]>();

        String[] val = new String[dtm.getRowCount()];

        for (int col = 1; col < dtm.getColumnCount(); col++) {
            for (int row = 0; row < dtm.getRowCount(); row++) {
                if (dtm.getValueAt(row, col) != null) {
                    val[row] = dtm.getValueAt(row, col).toString();
                }
            }

            data.add(val);
            val = new String[dtm.getRowCount()];
        }

        String[][] finalData = new String[data.size()][dtm.getRowCount()];
        int dataSize = data.size();

        for (int i = 0; i < dataSize; i++) {
            finalData[i] = data.get(i);
        }

        return finalData;
    }


    protected Object[][] getRowData() {
        Object[][] rowData = new Object[fields.size()][1];

        for (int i = 0; i < fields.size(); i++) {
            rowData[i][0] = fields.get(i).getFieldName();

            if (fields.get(i).getDataType() == SubFormField.LONG_STRING) {
                rowEditor.addCellEditorForRow(i, longTextEditor);
            }

            if (fields.get(i).getDataType() == SubFormField.DATE) {
                rowEditor.addCellEditorForRow(i, new DateCellEditor());
            }

            if (fields.get(i).getDataType() == SubFormField.SINGLE_ONTOLOGY_SELECT) {
                rowEditor.addCellEditorForRow(i,
                        new OntologyCellEditor(dataEntryEnvironment.getParentFrame(), false, null));
            }

            if (fields.get(i).getDataType() == SubFormField.MULTIPLE_ONTOLOGY_SELECT) {
                rowEditor.addCellEditorForRow(i,
                        new OntologyCellEditor(dataEntryEnvironment.getParentFrame(), true, null));
            }

            if (fields.get(i).getDataType() == SubFormField.COMBOLIST) {
                rowEditor.addCellEditorForRow(i,
                        new AutoFilterComboCellEditor(new AutoFilterCombo(
                                fields.get(i).getListValues(), false)));
            }

            if (fields.get(i).getDataType() == SubFormField.FILE) {
                rowEditor.addCellEditorForRow(i, new FileSelectCellEditor());
            }

            if (fields.get(i).getDataType() == SubFormField.FACTOR_LEVEL_UNITS) {
                rowEditor.addCellEditorForRow(i, new FactorLevelEntryCellEditor(dataEntryEnvironment.getParentFrame()));
            }

        }

        return rowData;
    }

    public String toString() {
        String data = title.toUpperCase().trim() + "\n";
        String[] toPrint = new String[calculateNoRows()];

        for (int i = 0; i < toPrint.length; i++) {
            toPrint[i] = "";
        }

        Set<Integer> ontologyRows = new HashSet<Integer>();

        Map<String, OntologyObject> history = dataEntryEnvironment.getUserHistory();

        for (int col = 0; col < dtm.getColumnCount(); col++) {
            String val;
            String termAcc;
            String termSource;
            int count = 0;

            for (int row = 0; row < dtm.getRowCount(); row++) {

                val = (dtm.getValueAt(row, col) != null) ? dtm.getValueAt(row, col).toString() : "";

                if (fieldType == FieldTypes.ASSAY) {
                    if (row == 0) {
                        if (val.equals("")) {
                            break;
                        }
                    }

                }

                if (col == 0) {
                    if (parent instanceof StudyDataEntry) {
                        val = "Study " + val;
                    } else if (parent instanceof InvestigationDataEntry) {
                        val = "Investigation " + val;
                    }
                }

                if (scrollTable.getCellEditor(row, 0) instanceof OntologyCellEditor || val.contains("Assay Measurement Type") || val.contains("Assay Technology Type") || ontologyRows.contains(row)) {
                    ontologyRows.add(row);
                    if (col == 0) {
                        termAcc = val + " Term Accession Number";
                        termSource = val + " Term Source REF";
                    } else {
                        termAcc = "";
                        termSource = "";


                        // code to print out assay accessions and sources from table mapping definitions...
                        if (fieldType == FieldTypes.ASSAY) {
                            if (row == 0 || row == 1) {
                                String termSourceAcc = getSourceAndAccessionForMapping(val);
                                if (termSourceAcc != null) {
                                    String[] parts = termSourceAcc.split(":");
                                    if (parts.length > 1) {
                                        termSource = parts[0];
                                        termAcc = parts[1];
                                    } else if (parts.length > 0) {
                                        termSource = parts[0];
                                    }
                                }
                            }

                        } else {
                            // change val to not have the ontology source ref anymore, and add the ref to
                            // the term source!
                            if (val.contains(":") && !GeneralUtils.isValueURL(val)) {
                                OntologyObject oo = history.get(val);
                                termSource = val.substring(0, val.indexOf(":"));
                                val = val.substring(val.indexOf(":") + 1);

                                if (oo != null) {
                                    termAcc = oo.getTermAccession();
                                }

                            }
                        }
                    }

                    //add to array
                    if (col == 0) {
                        toPrint[count] += val;
                    } else {
                        toPrint[count] += ("\t\"" + val + "\"");
                    }

                    count++;

                    if (col == 0) {
                        toPrint[count] += termAcc;
                    } else {
                        toPrint[count] += ("\t\"" + termAcc + "\"");
                    }

                    count++;

                    if (col == 0) {
                        toPrint[count] += termSource;
                    } else {
                        toPrint[count] += ("\t\"" + termSource + "\"");
                    }

                    count++;
                } else {
                    if (col == 0) {

                        toPrint[count] += val;
                    } else {
                        toPrint[count] += ("\t\"" + val + "\"");
                    }

                    count++;
                }
            }

        }

        for (String line : toPrint) {
            data += (line + "\n");
        }

        return data;
    }

    private String getSourceAndAccessionForMapping(String val) {
        for (MappingObject mo : parent.getDataEntryEnvironment().getParentFrame().getMappings()) {
            if (mo.getMeasurementEndpointType().equals(val)) {
                return mo.getMeasurementSource() + ":" + mo.getMeasurementAccession();
            }

            if (mo.getTechnologyType().equals(val)) {
                return mo.getTechnologySource() + ":" + mo.getTechnologyAccession();
            }
        }
        return null;
    }

    private int calculateNoRows() {
        int noOntologyRows = 0;

        for (int i = 0; i < scrollTable.getRowCount(); i++) {
            if (scrollTable.getCellEditor(i, 0) instanceof OntologyCellEditor || dtm.getValueAt(i, 0).toString().contains("Assay Measurement Type") || dtm.getValueAt(i, 0).toString().contains("Assay Technology Type")) {
                noOntologyRows++;
            }
        }

        return scrollTable.getRowCount() + (noOntologyRows * 2);
    }

    public void toggleShowRemoveOption() {
        showRemoveOption = !showRemoveOption;
    }

    protected void setupTableModel(int initialFieldNo) {
        dtm = new DefaultTableModel(getRowData(), getColumnNames(initialFieldNo)) {
            public Object getValueAt(int row, int col) {
                if (col == 0) {
                    return fields.get(row);
                }

                return super.getValueAt(row, col);
            }

            public boolean isCellEditable(int row, int col) {
                return (col != 0) && !uneditableRecords.contains(col);
            }
        };
    }

    private Object[] getColumnNames(int initialFields) {
        // add 1 to account for first column, which is nt used for data entry.
        Object[] colNames = new Object[initialFields + 1];
        colNames[0] = "Field Name";

        for (int i = 1; i < colNames.length; i++) {
            colNames[i] = fieldType;
        }

        return colNames;
    }

    protected void setupTableTabBehaviour() {
        InputMap im = scrollTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);

        //  Override the default tab behaviour
        //  Tab to the next editable cell. When no editable cells goto next cell.
        final Action previousTabAction = scrollTable.getActionMap()
                .get(im.get(tab));
        Action newTabAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                int rowSel = scrollTable.getSelectedRow();
                int colSel = scrollTable.getSelectedColumn();

                if (rowSel == (scrollTable.getRowCount() - 1)) {
                    scrollTable.setRowSelectionInterval(0, 0);

                    if ((colSel + 1) == scrollTable.getColumnCount()) {
                        scrollTable.setColumnSelectionInterval(0, 0);
                    } else {
                        scrollTable.setColumnSelectionInterval(colSel + 1,
                                colSel + 1);
                    }
                } else {
                    rowSel = rowSel + 1;
                    scrollTable.setRowSelectionInterval(rowSel, rowSel);

                    if (colSel > -1) {
                        scrollTable.setColumnSelectionInterval(colSel,
                                colSel);
                    }
                }

                scrollTable.scrollRectToVisible(scrollTable.getCellRect(
                        rowSel, colSel, true));

                JTable table = (JTable) e.getSource();
                int row = table.getSelectedRow();
                int originalRow = row;
                int column = table.getSelectedColumn();
                int originalColumn = column;

                while (!table.isCellEditable(row, column)) {
                    previousTabAction.actionPerformed(e);
                    row = table.getSelectedRow();
                    column = table.getSelectedColumn();

                    //  Back to where we started, get out.
                    if ((row == originalRow) && (column == originalColumn)) {
                        break;
                    }
                }

                if (table.editCellAt(row, column)) {
                    table.getEditorComponent().requestFocusInWindow();
                }
            }
        };
        scrollTable.getActionMap().put(im.get(tab), newTabAction);
    }

    protected void setHeaderProperties(JTable table, TableCellRenderer renderer) {

        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            columns.nextElement().setHeaderRenderer(renderer);
        }
    }


    protected void setTableProperties(JTable table, boolean isFieldName) {
        Font font = (!isFieldName) ? UIHelper.VER_12_PLAIN : UIHelper.VER_12_BOLD;
        table.setFont(font);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setColumnSelectionAllowed(true);
        table.setRowSelectionAllowed(true);
        table.setGridColor(UIHelper.LIGHT_GREEN_COLOR);
        table.setShowGrid(true);
    }


    public void valueChanged(ListSelectionEvent event) {
        int columnSelected = scrollTable.getSelectedColumn();
        int rowSelected = scrollTable.getSelectedRow();

        if ((rowSelected != -1) && (columnSelected != -1)) {


            removeRecord.setText((showRemoveOption ? "Remove " : "Selected ") + fieldType + " " + (columnSelected + 1));
            if (!removeRecord.isVisible()) {
                removeRecord.setVisible(true);
            }

            if (scrollTable.getValueAt(rowSelected, columnSelected) != null) {
                String s = scrollTable.getValueAt(rowSelected, columnSelected)
                        .toString();

                OntologyObject ooForSelectedTerm = searchUserHistory(s);

                if (ooForSelectedTerm != null) {
                    parent.getDataEntryEnvironment().setStatusPaneInfo("<html>" +
                            "<b>ontology term information</b>" +
                            "<p><term name: >" + ooForSelectedTerm.getTerm() +
                            "</p>" + "<p><b>source ref: </b> " +
                            ooForSelectedTerm.getTermSourceRef() + "</p>" +
                            "<p><b>accession no: </b>" +
                            ooForSelectedTerm.getTermAccession() + "</p>" +
                            "</html>");
                }
            } else {
                TableCellEditor tce = scrollTable.getCellEditor(rowSelected, columnSelected);
                Icon icon = null;
                if (tce instanceof OntologyCellEditor) {
                    // insert images to inform user of what to do in this situation :o)
                    icon = ontologyLookupHelp;

                } else if (tce instanceof TextCellEditor) {
                    icon = textEditHelp;
                }
                if (parent != null) {
                    if (icon != null) {
                        parent.getDataEntryEnvironment().setStatusPaneInfo(icon);
                    } else {
                        parent.getDataEntryEnvironment().setStatusPaneInfo("");
                    }
                }
            }
        } else {
            removeRecord.setVisible(false);
        }

    }

    private OntologyObject searchUserHistory(String uniqueId) {
        if (userHistory == null) {
            return null;
        }

        for (OntologyObject oo : userHistory.values()) {

            if (oo.getUniqueId().equals(uniqueId)) {
                return oo;
            }
        }
        return null;
    }


    public void focusGained(FocusEvent event) {
        removeRecord.setVisible(true);
    }

    public void focusLost(FocusEvent event) {
        removeRecord.setVisible(false);
    }

    public boolean addColumn() {
        DefaultTableModel model = (DefaultTableModel) scrollTable.getModel();
        TableColumn col = new TableColumn(scrollTable.getModel().getColumnCount());
        col.setHeaderRenderer(scrollTableHeaderRenderer);
        if (scrollTable.getEditingColumn() != -1) {
            scrollTable.getCellEditor(scrollTable.getEditingRow(), scrollTable.getEditingColumn()).stopCellEditing();
        }

        return doAddColumn(model, col);
    }

    public abstract boolean doAddColumn(DefaultTableModel model, TableColumn col);

    public void createGUI() {
        initialisePanel();
        setupTableModel(initialNoFields);

        add(setupOptionsPanel(), BorderLayout.NORTH);

        add(getFrozenTable(dtm, width, height), BorderLayout.CENTER);
        reformPreviousContent();
    }

    public abstract void reformPreviousContent();

    private void removalConfirmation(final FieldTypes whatIsBeingRemoved) {
        // delete reference to protocol in subform

        // add one to take into account the model and the initial column which contains fields names.
        final int selectedItem = scrollTable.getSelectedColumn() + 1;

        // check to ensure the value isn't 0, if it is, nothing is selected in the table since -1 (value returned by model if
        // no column is selected + 1 = 0!)
        if ((selectedItem != 0) && (parent != null)) {
            String displayText;
            if ((whatIsBeingRemoved == FieldTypes.FACTOR) ||
                    (whatIsBeingRemoved == FieldTypes.PROTOCOL)) {
                displayText = "<html>" + "<b>Confirm deletion of " +
                        fieldType + "</b>" + "<p>Deleting this " + fieldType +
                        " will result in all " + fieldType +
                        "s of this type in subsequent assays</p>" +
                        "<p>being deleted too! Do you wish to continue?</p>" +
                        "</html>";
            } else {
                displayText = "<html>" + "<b>Confirm deletion of " +
                        fieldType + "</b>" + "<p>Deleting this " + fieldType +
                        " will result in it's complete removal from this experiment annotation!</p>" +
                        "<p>Do you wish to continue?</p>" + "</html>";
            }

            JOptionPane optionPane = new JOptionPane(displayText,
                    JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION);
            optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    if (event.getPropertyName()
                            .equals(JOptionPane.VALUE_PROPERTY)) {
                        int lastOptionAnswer = Integer.valueOf(event.getNewValue()
                                .toString());

                        if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                            removeItem(selectedItem);
                            dataEntryEnvironment.getParentFrame().hideSheet();
                        } else {
                            // just hide the sheet and cancel further actions!
                            dataEntryEnvironment.getParentFrame().hideSheet();
                        }
                    }
                }
            });
            optionPane.setIcon(confirmRemoveColumn);
            UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
            dataEntryEnvironment.getParentFrame()
                    .showJDialogAsSheet(optionPane.createDialog(this,
                            "Confirm Delete"));
        } else {
            removeColumn(selectedItem);
        }
    }

    protected abstract void removeItem(int itemToRemove);

    protected void removeColumn(int curColDelete) {
        if ((curColDelete == -1) || (curColDelete == 0)) {
            return;
        }

        if (fieldType == FieldTypes.ASSAY && (parent != null) && !uneditableRecords.contains(curColDelete)) {
            clearColumn(curColDelete);
            return;
        }

        if (dtm.getColumnCount() == 2
                && curColDelete == (dtm.getColumnCount() - 1)) {
            clearColumn(curColDelete);
            return;
        }


        DefaultTableModel model = (DefaultTableModel) scrollTable.getModel();

        // get the column. because 1 was added on previously to take account of the first column, we need to remove
        // it this time since the column indexes are now coming from the table.
        TableColumn col = scrollTable.getColumnModel()
                .getColumn(curColDelete - 1);
        int columnModelIndex = col.getModelIndex();
        Vector data = model.getDataVector();
        Vector<String> colIds = new Vector<String>();

        for (int i = 0; i < model.getColumnCount(); i++) {
            colIds.addElement(model.getColumnName(i));
        }

        scrollTable.removeColumn(col);
        colIds.removeElementAt(columnModelIndex);

        // remove any data present in the column on deletion
        for (Object aData : data) {
            Vector row = (Vector) aData;
            row.removeElementAt(columnModelIndex);
        }

        model.setDataVector(data, colIds);

        // decrease each column index after deleted column by 1 so that indexes can be kept intact.
        Enumeration enumer = scrollTable.getColumnModel().getColumns();

        while (enumer.hasMoreElements()) {
            TableColumn c = (TableColumn) enumer.nextElement();

            if (c.getModelIndex() >= columnModelIndex) {
                c.setModelIndex(c.getModelIndex() - 1);
            }
        }

        if (fieldType == FieldTypes.ASSAY && uneditableRecords.contains(dtm.getColumnCount() - 1)) {
            uneditableRecords.remove(dtm.getColumnCount() - 1);
        }

        // update the model
        model.fireTableStructureChanged();
        updateTables();
    }

    protected void checkForSourcePresence(String source) {
        List<OntologySourceRefObject> definedSources = parent.getDataEntryEnvironment()
                .getOntologySources();
        boolean isPresent = false;

        for (OntologySourceRefObject osro : definedSources) {
            if (osro.getSourceName().equals(source)) {
                isPresent = true;
            }
        }

        // if it doesn't exist, then add the ontology information to the defined sources
        if (!isPresent) {

            OntologySourceRefObject osro = parent.getDataEntryEnvironment().getParentFrame()
                    .getCurrentUser()
                    .getOntologySource(source);

            if (osro == null) {
                osro = new OntologySourceRefObject(source, "", OntologySourceManager.getOntologyVersion(source), OntologySourceManager.getOntologyDescription(source));
            }

            parent.getDataEntryEnvironment().getOntologySources().add(osro);
        }
    }

    private void clearColumn(int colindex) {
        int noRows = dtm.getRowCount();
        for (int i = 0; i < noRows; i++) {
            dtm.setValueAt("", i, colindex);
        }
    }

    private JPanel setupOptionsPanel() {
        options = new JPanel();
        options.setLayout(new BoxLayout(options, BoxLayout.LINE_AXIS));
        options.setOpaque(false);

        String addRecordString = fieldType == FieldTypes.ASSAY && parent != null ? "create assay" : "add a new " + fieldType + " column";

        final JLabel addRecord = new JLabel(addRecordString, addRecordIcon, JLabel.LEFT);


        String toolTipText;

        if (fieldType == FieldTypes.ASSAY) {
            toolTipText = "<html><b>Create a new Assay</b>" +
                    "			 <p>Complete the details for the assay in the fields provided</p>" +
                    "			 <p>and click this button to add the assay to this study...</p>" +
                    "</html>";
        } else {
            toolTipText = "<html><b>Add a new " + fieldType + "</b>" +
                    "			 <p>Click here to add a new column to enter an additional " + fieldType + "</p>" +
                    "</html>";
        }

        addRecord.setToolTipText(toolTipText);
        Font fontToUse = fieldType == FieldTypes.ASSAY && parent != null ? UIHelper.VER_12_BOLD : UIHelper.VER_12_PLAIN;
        UIHelper.renderComponent(addRecord, fontToUse, UIHelper.DARK_GREEN_COLOR, false);

        addRecord.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                addRecord.setIcon(addRecordIcon);
                if (addColumn()) {
                    updateTables();
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addRecord.setIcon(addRecordIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addRecord.setIcon(addRecordIcon);
            }
        });

        options.add(addRecord);
        options.add(Box.createHorizontalStrut(10));

        removeRecord = new JLabel("Remove " + fieldType + "...", removeIcon, JLabel.LEFT);
        removeRecord.setVisible(false);
        UIHelper.renderComponent(removeRecord, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        removeRecord.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (removeRecord.getIcon() != null) {
                    removeRecord.setIcon(removeIcon);
                }
                removalConfirmation(fieldType);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (removeRecord.getIcon() != null) {
                    removeRecord.setIcon(removeIcon);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (removeRecord.getIcon() != null) {
                    removeRecord.setIcon(removeIconOver);
                }
            }
        });

        if (!showRemoveOption) {
            removeRecord.setIcon(null);
            removeRecord.setText("");
        }

        options.add(removeRecord);
        options.add(Box.createHorizontalStrut(10));
        createCustomOptions();

        options.add(Box.createGlue());

        return options;
    }

    public ExtendedJTable getLockedTable() {
        return lockedTable;
    }

    public void changeTableRenderer(JTable table, TableCellRenderer renderer) {
        try {
            TableCellRenderer rendererToUse = renderer;
            if (rendererToUse == null) {
                if (table == scrollTable) {
                    rendererToUse = DEFAULT_SCROLL_TABLE_RENDERER;
                } else {
                    rendererToUse = DEFAULT_LOCKED_TABLE_RENDERER;
                }
            }
            table.setDefaultRenderer(Class.forName("java.lang.Object"),
                    rendererToUse);
        } catch (ClassNotFoundException e) {
            log.error("Problem occurred when changing TableRenderer : " + e.getMessage());
        }
    }

    public void setDataEntryEnvironment(DataEntryEnvironment dataEntryEnvironment) {
        this.dataEntryEnvironment = dataEntryEnvironment;
    }

    public void setParent(DataEntryForm parent) {
        this.parent = parent;
    }

    public RowEditor getRowEditor() {
        return rowEditor;
    }

    public abstract void update();

    public abstract void updateItems();

    public abstract void reformItems();

    /**
     * Implementing this method allows for the creation of additional menu
     * elements in the options panel of the subform.
     */
    public abstract void createCustomOptions();


}
