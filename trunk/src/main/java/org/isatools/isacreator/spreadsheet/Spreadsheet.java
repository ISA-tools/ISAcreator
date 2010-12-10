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

package org.isatools.isacreator.spreadsheet;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;
import org.isatools.isacreator.archiveoutput.ArchiveOutputError;
import org.isatools.isacreator.calendar.DateCellEditor;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.TableFieldObject;
import org.isatools.isacreator.effects.AniSheetableJFrame;
import org.isatools.isacreator.filechooser.FileSelectCellEditor;
import org.isatools.isacreator.filterablelistselector.FilterableListCellEditor;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.spreadsheet.transposedview.SpreadsheetConverter;
import org.isatools.isacreator.spreadsheet.transposedview.TransposedSpreadsheetModel;
import org.isatools.isacreator.spreadsheet.transposedview.TransposedSpreadsheetView;
import org.isatools.isacreator.utils.GeneralUtils;
import org.isatools.isacreator.utils.TableConsistencyChecker;
import org.isatools.isacreator.visualization.TableGroupInfo;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Spreadsheet class.
 * Provides the functionality of a spreadsheet including the JTable, Listeners, Addition of Cell Editors, and so forth. Spreadsheet
 * is created automatically from Table Reference Objects created by the ISAcreator configuration tool!
 * <p/>
 * // todo clean up and separate functionality into separate classes.
 *
 * @author Eamonn Maguire
 */
public class Spreadsheet extends JComponent implements
        MouseListener, ListSelectionListener, PropertyChangeListener, TableColumnModelListener {

    private static final Logger log = Logger.getLogger(Spreadsheet.class.getName());

    public static final int MAX_ROWS = 32000;
    public static FileSelectCellEditor fileSelectEditor;
    private static EmptyBorder emtpyBorder = new EmptyBorder(1, 1, 1, 1);
    private static DateCellEditor dateEditor;

    public static final int SWITCH_ABSOLUTE = 0;
    public static final int SWITCH_RELATIVE = 1;
    private static final int DEFAULT_STATE = 2;
    private static final int DELETING_COLUMN = 3;
    private static final int DELETING_ROW = 4;
    private static final int INITIAL_ROWS = 50;


    static {
        dateEditor = new DateCellEditor();
        fileSelectEditor = new FileSelectCellEditor();
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("spreadsheet-package.style").load(
                Spreadsheet.class.getResource("/dependency-injections/spreadsheet-package.properties"));
    }

    @InjectedResource
    private ImageIcon addRowButton, addRowButtonOver, deleteRowButton, deleteRowButtonOver, deleteColumnButton, deleteColumnButtonOver,
            multipleSortButton, multipleSortButtonOver, copyColDownButton, copyColDownButtonOver, copyRowDownButton,
            copyRowDownButtonOver, addProtocolButton, addProtocolButtonOver, addFactorButton, addFactorButtonOver,
            addCharacteristicButton, addCharacteristicButtonOver, addParameterButton, addParameterButtonOver, undoButton,
            undoButtonOver, redoButton, redoButtonOver, requiredColumnWarningIcon, confirmRemoveColumnIcon,
            confirmRemoveRowIcon, selectOneColumnWarningIcon, copyColumnDownWarningIcon, copyRowDownWarningIcon, transposeIcon, transposeIconOver;

    private AddMultipleRowsGUI amrGUI;
    private DataEntryEnvironment dataEntryEnv;

    //map provides a way of tracking where unit fields belong in the table, so even columns are moved around by the user,
    // they are moved by the user, the software still knows where they belong when it comes to outputting the ISATAB files!!
    private Map<TableColumn, List<TableColumn>> columnDependencies;
    private JLabel addCharacteristic, addFactor, addParameter, addProtocol, addRow, copyColDown, copyRowDown, deleteColumn,
            deleteRow, multipleSort, undo, redo, transpose;

    private JOptionPane optionPane;
    private CustomTable table;
    private MultipleSortGUI msGUI;
    private TransposedSpreadsheetView tsv;
    private TableGroupInfo tgi;
    private SpreadsheetColumnRenderer renderer = new SpreadsheetColumnRenderer();
    private SpreadsheetModel spreadsheetModel;
    private StudyDataEntry sde;
    private AssaySpreadsheet ade;
    private TableReferenceObject tro;
    private Vector<String> columns;
    private Vector<Object> rows;
    private int[] rowsToDelete;
    private int curColDelete = -1;
    private int currentState = DEFAULT_STATE;
    private int previouslyAddedCharacteristicPosition = -1;
    private int startCol = -1;
    private int startRow = -1;
    private Map<String, String> absRelFileMappings;
    private Set<String> hiddenColumns;
    private String title;
    private boolean highlightActive = false;
    private TableConsistencyChecker tcc;

    // Objects required for the undo function to work.
    private Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
    private SpreadsheetHistory spreadsheetHistory = new SpreadsheetHistory();

    private UndoManager um = new UndoManager() {
        public void undoableEditHappened(UndoableEditEvent e) {
            super.undoableEditHappened(e);
            undo.setEnabled(canUndo());
            redo.setEnabled(canRedo());
        }

        public void undo() {
            try {
                super.undo();
                undo.setEnabled(canUndo());
                redo.setEnabled(canRedo());
            } catch (Exception e) {
                log.info("Can't undo...");
            }
        }

        public void redo() {
            try {
                super.redo();
                undo.setEnabled(canUndo());
                redo.setEnabled(canRedo());
            } catch (Exception e) {
                log.info("Can't redo...");
            }
        }

        @Override
        public void discardAllEdits() {
            super.discardAllEdits();
            undo.setEnabled(canUndo());
            redo.setEnabled(canRedo());
        }
    };

    /**
     * Spreadsheet Constructor.
     *
     * @param tro   - Reference Object to build the table with.
     * @param sde   - StudyDataEntry. Used to retrieve factors and protocols which have been entered.
     * @param title - name to display on the spreadsheet...
     * @param ade   - The assay data entry object :o)
     */
    public Spreadsheet(final TableReferenceObject tro, StudyDataEntry sde, String title, AssaySpreadsheet ade) {
        this.sde = sde;
        this.ade = ade;
        this.dataEntryEnv = sde.getDEP();
        this.title = title;

        ResourceInjector.get("spreadsheet-package.style").inject(this);

        columnDependencies = new HashMap<TableColumn, List<TableColumn>>();
        Collections.synchronizedMap(columnDependencies);
        hiddenColumns = new HashSet<String>();
        this.tro = tro;

        setLayout(new BorderLayout());

        // create a spreadsheet model which overrides two methods that allow the reference model for the spreadsheet to
        // control which columns can be deleted, and which cannot.
        spreadsheetModel = new SpreadsheetModel(tro) {
            //@overrides
            public Class getColumnClass(int colNo) {
                String colName = getColumnName(colNo);

                Class c = tro.getColumnType(colName).getMapping();

                if (c == DataTypes.DATE.getMapping()) {
                    c = DataTypes.STRING.getMapping();
                }

                return c;
            }

            //overrides
            public boolean isCellEditable(int row, int col) {
                String colName = getColumnName(col);
                //consult reference model to ascertain whether or not the column is editable
                return tro.getColumnEditable(colName);
            }

            public void setValueAt(Object obj, int row, int col) {
                super.setValueAt(obj, row, col);
            }


        };

        spreadsheetHistory.setTableModel(spreadsheetModel);
        spreadsheetModel.setHistory(spreadsheetHistory);


        rows = new Vector<Object>();

        if (tro.getPreDefinedHeaders() != null) {
            columns = tro.getPreDefinedHeaders();
        } else {
            columns = tro.getHeaders();
        }

        spreadsheetModel.setDataVector(rows, columns);

        // setup the JTable
        setupTable();

        spreadsheetModel.setTable(table);

        if (tro.getData() != null) {
            populateTable(tro.getData());
            rebuildDependencies(tro.getColumnDependencies());
        } else {
            // populate table with some empty fields.
            addRows(INITIAL_ROWS, true);

            List<Protocol> protocols = tro.constructProtocolObjects();
            if (protocols.size() > 0) {
                for (Protocol p : protocols) {
                    sde.getStudy().addProtocol(p);
                }
                sde.reformProtocols();
            }

            List<Factor> factors = tro.constructFactorObjects();

            if (factors.size() > 0) {
                for (Factor f : factors) {
                    sde.getStudy().addFactor(f);
                }
                sde.reformFactors();
            }
        }


        if (tro.getDefinedOntologies().size() > 0) {
            for (OntologyObject oo : tro.getDefinedOntologies().values()) {
                dataEntryEnv.getUserHistory().put(oo.getUniqueId(), oo);
            }
        }

        amrGUI = new AddMultipleRowsGUI(this);
        amrGUI.createGUI();
        msGUI = new MultipleSortGUI(this);
        msGUI.createGUI();

        table.setAutoscrolls(true);

        JScrollPane pane = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setBackground(UIHelper.BG_COLOR);
        pane.setAutoscrolls(true);
        pane.getViewport().setBackground(UIHelper.BG_COLOR);
        pane.setBorder(emtpyBorder);

        IAppWidgetFactory.makeIAppScrollPane(pane);

        add(pane, BorderLayout.CENTER);

        createButtonPanel();
        addUndoableEditListener(um);
    }

    public String getAssignedUnitForColumn(int columnIndex, int rowNo) {

        int[] convertedColumnIndex = Utils.convertSelectedColumnsToModelIndices(table, new int[]{columnIndex});

        Set<Integer> dependentColumns = tro.getColumnDependencies().get(convertedColumnIndex[0]);

        String value = "";
        if (dependentColumns != null) {
            for (int column : dependentColumns) {
                value += getTableModel().getValueAt(rowNo, column);
            }
        }


        return value;
    }

    public String getTitle() {
        return title;
    }

    /**
     * Return the Column count.
     *
     * @return number of columns in table
     */
    public int getColumnCount() {
        return table.getColumnCount();
    }


    public String getColValAtRow(String colName, int rowNumber) {
        for (int col = 1; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            if (column.getHeaderValue().toString().equalsIgnoreCase(colName)) {
                // safety precaution to finalise any cells. otherwise their value would be missed!
                if (table.getCellEditor(rowNumber, col) != null) {
                    table.getCellEditor(rowNumber, col).stopCellEditing();
                }
                return table.getValueAt(rowNumber, col).toString();
            }
        }
        return "";
    }

    /**
     * Groups up types of columns such as factors and returns the concatenation of those values on a per row basis.
     *
     * @param group             - e.g. Factor, Characterisitc
     * @param exactMatch        - if the Group should be an exact match to a table value (e.g. Factor could be any factor but Factor Value[Run Time] would be a perfect match.)
     * @param returnSampleNames - return the sample names or return a list of row indexes corresponding to a group. True to return the sample names, false otherwise!
     * @return Map<String, List<Object>> where list will be Strings if return Sample Names or Row indexes
     */
    public Map<String, List<Object>> getDataGroupsByColumn(String group, boolean exactMatch, boolean returnSampleNames) {
        Map<String, List<Object>> groups = new ListOrderedMap<String, List<Object>>();

        boolean allowedUnit = false;
        for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {
            String groupVal = "";
            for (int col = 1; col < table.getColumnCount(); col++) {
                TableColumn column = table.getColumnModel().getColumn(col);

                boolean match = false;

                if (exactMatch) {
                    if (column.getHeaderValue().toString().equalsIgnoreCase(group)) {
                        match = true;
                    } else if (allowedUnit && column.getHeaderValue().toString().equalsIgnoreCase("unit")) {
                        match = true;
                    }
                } else {
                    if (column.getHeaderValue().toString().contains(group)) {
                        match = true;
                    } else if (allowedUnit && column.getHeaderValue().toString().equalsIgnoreCase("unit")) {
                        match = true;
                    }
                }

                if (match) {
                    // safety precaution to finalise any cells. otherwise their value would be missed!
                    if (table.getCellEditor(row, col) != null) {
                        try {
                            table.getCellEditor(row, col).stopCellEditing();
                        } catch (Exception e) {
                            // ignore error...
                        }
                    }
                    groupVal += " " + table.getValueAt(row, col);
                    allowedUnit = true;
                } else {
                    allowedUnit = false;
                }
            }
            if (!groupVal.equals("")) {
                groupVal = groupVal.trim();
                if (!groups.containsKey(groupVal)) {
                    groups.put(groupVal, new ArrayList<Object>());
                }

                if (returnSampleNames) {
                    groups.get(groupVal).add(getColValAtRow("Sample Name", row));
                } else {
                    groups.get(groupVal).add(row);
                }
            }
        }

        return groups;
    }


    /**
     * Recovers cell editor for a field for attachment to a column
     *
     * @param col - Column to attach a custom cell editor to
     */
    @SuppressWarnings({"ConstantConditions"})
    private void addCellEditor(TableColumn col) {
        ValidationObject vo = tro.getValidationConstraints(col.getHeaderValue()
                .toString());
        DataTypes classType = tro.getColumnType(col.getHeaderValue().toString());

        if (vo != null && classType == DataTypes.STRING) {
            StringValidation sv = ((StringValidation) vo);
            col.setCellEditor(new StringEditor(sv));
            return;
        }

        if (col.getHeaderValue().toString().equals("Protocol REF")) {
            col.setCellEditor(new FilterableListCellEditor(sde.getStudy()));
            return;
        }

        if (tro.getClassType(col.getHeaderValue().toString()) == DataTypes.ONTOLOGY_TERM) {
            col.setCellEditor(new OntologyCellEditor(dataEntryEnv.getParentFrame(),
                    tro.acceptsMultipleValues(col.getHeaderValue().toString()),
                    tro.getRecommendedSource(col.getHeaderValue().toString())));
            return;
        }

        if (tro.getClassType(col.getHeaderValue().toString()) == DataTypes.LIST) {
            col.setCellEditor(new FilterableListCellEditor(tro.getListItems(col.getHeaderValue().toString())));
            return;
        }

        if (tro.getClassType(col.getHeaderValue().toString())
                == DataTypes.DATE) {
            col.setCellEditor(dateEditor);

            return;
        }

        if (tro.getClassType(col.getHeaderValue().toString()) == DataTypes.BOOLEAN) {
            col.setCellEditor(new StringEditor(new StringValidation("true|yes|TRUE|YES|NO|FALSE|no|false", "not a valid boolean!"), true));
            return;
        }

        if ((classType == DataTypes.STRING) &&
                tro.acceptsFileLocations(col.getHeaderValue().toString())) {
            col.setCellEditor(fileSelectEditor);
            return;
        }


        if (classType == DataTypes.INTEGER) {
            col.setCellEditor(new StringEditor(new StringValidation("[0-9]+", "Please enter an integer value!"), true));
            return;
        }

        if (classType == DataTypes.DOUBLE) {
            col.setCellEditor(new StringEditor(new StringValidation("[0-9]+[.]{0,1}[0-9]*", "Please enter a double value!"), true));
            return;
        }

        col.setCellEditor(new StringEditor(new StringValidation(".*", "")));
    }

    /**
     * Adds a column to the table with a specified name
     *
     * @param headerLabel - name of column to be added
     */
    public void addColumn(Object headerLabel) {
        SpreadsheetModel model = (SpreadsheetModel) table.getModel();
        TableColumn col = new TableColumn(table.getModel().getColumnCount());
        col.setHeaderValue(headerLabel);
        col.setPreferredWidth(calcColWidths(headerLabel.toString()));

        // add a cell editor (if available to the column)
        addCellEditor(col);

        table.addColumn(col);

        model.addColumn(headerLabel.toString());
        model.fireTableStructureChanged();

        table.getColumnModel().getColumn(table.getColumnCount() - 1)
                .setHeaderRenderer(renderer);

        if (table.getRowCount() > 0) {
            table.setValueAt(tro.getDefaultValue(headerLabel.toString()), 0,
                    table.getColumnCount() - 1);
            copyColumnDownwards(0, table.getColumnCount() - 1);
            tro.getDefaultValue(headerLabel.toString());
        }
    }

    /**
     * Add a column after the currently selected column
     *
     * @param headerLabel             - name of column to add.
     * @param fixedVal                - initial value to populate column with, if any.
     * @param currentlySelectedColumn - place in table to add the column after.
     */
    public void addColumnAfterPosition(Object headerLabel, String fixedVal,
                                       int currentlySelectedColumn) {

        if (currentlySelectedColumn == -1) {
            currentlySelectedColumn = (table.getSelectedColumn() == -1)
                    ? (table.getColumnCount() - 1) : table.getSelectedColumn();
        }

        SpreadsheetModel model = (SpreadsheetModel) table.getModel();

        TableColumn col = new TableColumn(table.getModel().getColumnCount());
        col.setHeaderValue(headerLabel);
        col.setPreferredWidth(calcColWidths(headerLabel.toString()));
        col.setHeaderRenderer(renderer);

        addCellEditor(col);

        model.addToColumns(headerLabel.toString());
        model.addColumn(col);

        table.addColumn(col);

        model.fireTableStructureChanged();
        model.fireTableDataChanged();

        // now move the column into it's correct position
        int stopValue = headerLabel.toString().equals("Unit")
                ? (previouslyAddedCharacteristicPosition + 1)
                : (currentlySelectedColumn + 1);

        for (int i = table.getColumnCount() - 1; i > stopValue; i--) {
            table.getColumnModel().moveColumn(i - 1, i);
        }


        if (headerLabel.toString().equals("Unit")) {
            addColumnToDependencies(table.getColumnModel()
                    .getColumn(previouslyAddedCharacteristicPosition),
                    col);
        } else if (headerLabel.toString().contains("Parameter")) {
            addColumnToDependencies(table.getColumnModel()
                    .getColumn(currentlySelectedColumn),
                    col);
        }


        if (headerLabel.toString().contains("Characteristics") ||
                headerLabel.toString().contains("Factor") ||
                headerLabel.toString().contains("Parameter")) {
            previouslyAddedCharacteristicPosition = stopValue;
        }

        fixedVal = fixedVal == null ? "" : fixedVal;
        if (fixedVal != null && table.getRowCount() > 0) {
            table.setValueAt(fixedVal, 0, stopValue);
            copyColumnDownwards(0, stopValue);
        }

        table.addNotify();
    }

    /**
     * Adds a Dependent column (dependentCol) to a Parent Column (parentCol) list of columns.
     *
     * @param parentCol    - Parent Column
     * @param dependentCol - Column Dependent on Parent e.g. Unit to a factor.
     */
    public void addColumnToDependencies(TableColumn parentCol,
                                        TableColumn dependentCol) {
        if (!columnDependencies.containsKey(parentCol)) {
            columnDependencies.put(parentCol, new ArrayList<TableColumn>());
        }

        if (dependentCol != null) {
            columnDependencies.get(parentCol).add(dependentCol);
        }
    }

    /**
     * Add an Array of columns to the columns vector.
     *
     * @param colName - Array of column names to be added to the spreadsheet.
     */
    public void addColumns(String[] colName) {

        for (String aColName : colName) {
            columns.addElement(aColName);
        }
    }

    /**
     * Add a field to the TableReferenceObject
     *
     * @param fo - Field object to add.
     */
    public void addFieldToReferenceObject(TableFieldObject fo) {
        tro.addField(fo);
    }

    public void addRow() {
        Vector r;
        r = createBlankElement(false);
        rows.addElement(r);
        table.addNotify();
    }


    /**
     * Add rows to the table
     *
     * @param number            - number of rows to add.
     * @param creatingFromEmpty - whether or not the rows have been added at the very start.
     */
    public synchronized void addRows(int number, boolean creatingFromEmpty) {
        for (int i = 0; i < number; i++) {
            // need to create separate references.
            Vector<SpreadsheetCell> r = createBlankElement(creatingFromEmpty);
            rows.addElement(r);
        }

        spreadsheetModel.fireTableStructureChanged();
        table.addNotify();
        spreadsheetModel.updateRowCount();
    }

    private void insertRowInPosition(int selectedRow) {
        // insert row
        addRows(1, false);

        // cut cell range from selected row downwards
        int[] rows = Utils.getArrayOfVals(selectedRow, spreadsheetModel.getRowCount() - 2);
        int[] cols = Utils.getArrayOfVals(1, spreadsheetModel.getColumnCount() - 1);
        SpreadsheetCellRange toMove = new SpreadsheetCellRange(rows, Utils.convertSelectedColumnsToModelIndices(table, cols));

        doCopy(true, toMove);

        paste((selectedRow + 1), 1, false);

        spreadsheetModel.setSelection(new SpreadsheetCellRange(new int[]{selectedRow}, cols));

    }


    /**
     * Harsh and extremely inaccurate way of calculating column width. Will change to use FontMetrics.
     *
     * @param colName - Name of column to check
     * @return an int for the width of the column.
     */
    private int calcColWidths(String colName) {
        return (int) (((colName.length() + 3) * 8) * 1.20);
    }

    /**
     * Change a file name in the spreadsheet to a new one.
     *
     * @param prevFileName Previously used file name
     * @param newFileName  New file name to be used.
     */
    public void changeFileName(String prevFileName, String newFileName) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (tro.acceptsFileLocations(tc.getHeaderValue().toString())) {
                int colIndex = Utils.convertModelIndexToView(table, tc.getModelIndex());
                for (int row = 0; row < table.getRowCount(); row++) {
                    String s = (table.getValueAt(row, colIndex) == null) ? ""
                            : table.getValueAt(row,
                            colIndex).toString();

                    if (s != null && !s.trim().equals("") && s.equals(prevFileName)) {
                        table.setValueAt(newFileName, row, colIndex);
                    }
                }
            }
        }
    }

    /**
     * This method checks through the spreadsheet to determine whether or not all the required fields defined in the configuration
     * have been filled in. If they have not been filled in, an ErrorLocator is logged and returned in a List of ErrorLocator objects!
     *
     * @return returns a List (@see List) of ErrorLocator (@see ErrorLocator) objects
     * @see org.isatools.isacreator.spreadsheet.TableReferenceObject
     * @see org.isatools.isacreator.archiveoutput.ArchiveOutputError
     */
    public List<ArchiveOutputError> checkForCompleteness() {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        List<ArchiveOutputError> archiveOutputErrors = new ArrayList<ArchiveOutputError>();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            int columnViewIndex = Utils.convertModelIndexToView(table, tc.getModelIndex());
            if (tro.isRequired(tc.getHeaderValue().toString())) {
                for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {


                    Object cellObj = table.getValueAt(row, columnViewIndex);

                    String value = (cellObj == null) ? "" : cellObj.toString().trim();

                    if (value.equals("")) {
                        // a required value has not been filled! therefore report the index of the row and column as well as the calling]
                        // location and message!
                        archiveOutputErrors.add(new ArchiveOutputError("Data missing for " + tc.getHeaderValue().toString() + " at record " + row, ade, tc.getHeaderValue().toString(), row, columnViewIndex));
                    }
                }
            }
        }
        return archiveOutputErrors;
    }

    /**
     * Method will replace any absolute file paths to relative ones to match with their new location inside the ISArchive
     *
     * @param toSwitch -> to switch to relative, use Spreadsheet.SWITCH_RELATIVE, to switch back to absolute, use Spreadsheet.SWITCH_ABSOLUTE
     */
    public void changeFilesToRelativeOrAbsolute(int toSwitch) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        if (absRelFileMappings == null) {
            absRelFileMappings = new HashMap<String, String>();
        }

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (tro.acceptsFileLocations(tc.getHeaderValue().toString())) {
                int colIndex = tc.getModelIndex();

                for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {
                    String s = (spreadsheetModel.getValueAt(row, colIndex) == null) ? ""
                            : spreadsheetModel.getValueAt(row,
                            colIndex).toString();

                    if (s != null && !s.trim().equals("")) {
                        switch (toSwitch) {
                            case SWITCH_RELATIVE:

                                if (!s.startsWith("ftp") && !s.startsWith("http")) {
                                    String newFileName = s.substring(s.lastIndexOf(
                                            File.separator) + 1);
                                    absRelFileMappings.put(newFileName, s);
                                    spreadsheetModel.doSetValueAt(newFileName, row, colIndex);
                                }

                                break;

                            case SWITCH_ABSOLUTE:

                                if (!s.startsWith("ftp") && !s.startsWith("ftps")) {
                                    String absFileName = absRelFileMappings.get(s);

                                    if (absFileName != null) {
                                        spreadsheetModel.doSetValueAt(absFileName, row, colIndex);
                                    }
                                }


                                break;
                        }
                    }
                }
            }
        }

        if (toSwitch == SWITCH_ABSOLUTE) {
            absRelFileMappings = null;
        }
    }

    /**
     * Check to see if a column with a given name exists.
     *
     * @param colName name of column to check for.
     * @return true if it exists, false otherwise.
     */
    public boolean checkColumnExists(String colName) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(colName)) {
                return true;
            }
        }

        return false;
    }

    private void clearCells(int startRow, int startCol, int endRow, int endCol) {
        int[] rows = Utils.getArrayOfVals(startRow, endRow);
        int[] columns = Utils.getArrayOfVals(startCol, endCol);

        SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(rows, Utils.convertSelectedColumnsToModelIndices(table, columns));

        spreadsheetHistory.add(affectedRange);

        fill(affectedRange, "");
    }


    private int convertViewRowToModel(int row) {
        return spreadsheetModel.getIndexes()[row];
    }

    private void copyColumnDownwards(int rowId, int colInd) {
        int convColIndex = toModel(colInd);
        String val = spreadsheetModel.getValueAt(rowId, convColIndex).toString();
        fill(new SpreadsheetCellRange(rowId, table.getRowCount(), convColIndex, convColIndex), val);
    }

    public int toModel(int vColIndex) {
        if (vColIndex >= table.getColumnCount()) {
            return -1;
        }
        return table.getColumnModel().getColumn(vColIndex).getModelIndex();
    }

    /**
     * Copy the contents of a row downwards.
     *
     * @param rowId - row to copy from
     */
    private void copyRowDownwards(int rowId) {
        // if there is a row selected
        if (rowId > -1) {
            String rowRepresentationAsString = getRowAsString(rowId);
            StringBuffer totalRepresentation = new StringBuffer("");
            int numRows = spreadsheetModel.getRowCount() - rowId;

            for (int i = 0; i < numRows; i++) {
                totalRepresentation.append(rowRepresentationAsString).append(System.getProperty("line.separator"));
            }

            int[] rows = Utils.getArrayOfVals(rowId, table.getRowCount() - 1);
            int[] cols = Utils.getArrayOfVals(1, table.getColumnCount() - 1);

            SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(rows, Utils.convertSelectedColumnsToModelIndices(table, cols));

            spreadsheetHistory.add(affectedRange);
            spreadsheetModel.extendedFromString(totalRepresentation.toString(), '\t', affectedRange);

        }

    }

    private String getRowAsString(int rowId) {
        StringBuffer rowRepresentation = new StringBuffer("");
        for (int col = 1; col < table.getColumnCount(); col++) {
            if (col != table.getColumnCount() - 1) {
                rowRepresentation.append(table.getValueAt(rowId, col)).append("\t");
            }
        }

        return rowRepresentation.toString();
    }

    /**
     * Fill a range of cells with a given value (or formula)
     *
     * @param range The range to fill
     * @param value The value to fill. Should begin with '=' for a formula.
     */
    public void fill(SpreadsheetCellRange range, String value) {
        CellEditor editor = table.getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }
        spreadsheetHistory.add(range);
        spreadsheetModel.fillRange(range, value);
        spreadsheetModel.fireTableDataChanged();
    }

    /**
     * Creates blank elements (or those including default data) into the rows being added
     * todo check this again and make sure the proper values are being obtained for default values.
     *
     * @param creatingFromEmpty - whether or not the rows have been added at the very start.
     * @return Vector containing elements to be added to the row.
     */
    private Vector<SpreadsheetCell> createBlankElement(boolean creatingFromEmpty) {
        Vector<SpreadsheetCell> columnValues = new Vector<SpreadsheetCell>();
        int totalColumns = spreadsheetModel.getColumnCount();
        int curRowNo = spreadsheetModel.getRowCount();

        // if we don't find a field, we should skip over the index? Or we include a padding factor which can change depending
        // on if a field isn't found in the current position. i.e, starts off at -1, then can change to zero.
        for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {
            String colName = spreadsheetModel.getColumnName(columnIndex);
            // Col 0 = Row Number
            if (colName.equals(TableReferenceObject.ROW_NO_TEXT)) {
                columnValues.addElement(new SpreadsheetCell(String.valueOf(curRowNo + 1)));
            } else {
                // / Else, all other columns are data. We can fairly safely put this data in as a String for now. But maybe
                // we should use the previous code since it covered more cases. If it works after modification, I can
                // put the previous code back in again.
                String value = tro.getDefaultValue(columnIndex);
                columnValues.addElement(new SpreadsheetCell(creatingFromEmpty ? value : ""));
            }
        }
        return columnValues;
    }


    /**
     * Create the Button panel - a panel which contains graphical representations of the options available
     * to the user when interacting with the software.
     */
    private void createButtonPanel() {

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        addRow = new JLabel(addRowButton);
        addRow.setToolTipText("<html><b>add row</b>" +
                "<p>add a new row to the table</p></html>");
        addRow.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                addRow.setIcon(addRowButton);
                showMultipleRowsGUI();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                addRow.setIcon(addRowButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addRow.setIcon(addRowButton);
            }
        });

        deleteRow = new JLabel(deleteRowButton);
        deleteRow.setToolTipText("<html><b>remove row</b>" +
                "<p>remove selected row from table</p></html>");
        deleteRow.setEnabled(false);
        deleteRow.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                deleteRow.setIcon(deleteRowButton);
                if (table.getSelectedRow() != -1) {
                    if (!(table.getSelectedRowCount() > 1)) {
                        deleteRow(table.getSelectedRow());
                    } else {
                        deleteRow(table.getSelectedRows());
                    }

                }
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                deleteRow.setIcon(deleteRowButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                deleteRow.setIcon(deleteRowButton);
            }
        });

        deleteColumn = new JLabel(deleteColumnButton);
        deleteColumn.setToolTipText("<html><b>remove column</b>" +
                "<p>remove selected column from table</p></html>");
        deleteColumn.setEnabled(false);
        deleteColumn.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                deleteColumn.setIcon(deleteColumnButton);
                if (!(table.getSelectedColumns().length > 1)) {
                    deleteColumn(table.getSelectedColumn());
                } else {
                    showColumnErrorMessage();
                }
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                deleteColumn.setIcon(deleteColumnButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                deleteColumn.setIcon(deleteColumnButton);
            }
        });

        multipleSort = new JLabel(multipleSortButton);
        multipleSort.setToolTipText("<html><b>multiple sort</b>" +
                "<p>perform a multiple sort on the table</p></html>");
        multipleSort.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                multipleSort.setIcon(multipleSortButton);
                showMultipleColumnSortGUI();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                multipleSort.setIcon(multipleSortButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                multipleSort.setIcon(multipleSortButton);
            }
        });

        copyColDown = new JLabel(copyColDownButton);
        copyColDown.setToolTipText("<html><b>copy column downwards</b>" +
                "<p>duplicate selected column and copy it from the current</p>" +
                "<p>position down to the final row in the table</p></html>");
        copyColDown.setEnabled(false);
        copyColDown.addMouseListener(new MouseAdapter() {

            public void mouseExited(MouseEvent mouseEvent) {
                copyColDown.setIcon(copyColDownButton);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                copyColDown.setIcon(copyColDownButtonOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                copyColDown.setIcon(copyColDownButton);

                final int row = table.getSelectedRow();
                final int col = table.getSelectedColumn();

                if (row != -1 && col != -1) {
                    JOptionPane copyColDownConfirmationPane = new JOptionPane("<html><b>Confirm Copy of Column...</b><p>Are you sure you wish to copy " +
                            "this column downwards?</p><p>This Action can not be undone!</p></html>", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

                    copyColDownConfirmationPane.setIcon(copyColumnDownWarningIcon);
                    UIHelper.applyOptionPaneBackground(copyColDownConfirmationPane, UIHelper.BG_COLOR);

                    copyColDownConfirmationPane.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                                int lastOptionAnswer = Integer.valueOf(event.getNewValue().toString());
                                dataEntryEnv.getParentFrame().hideSheet();
                                if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                    copyColumnDownwards(row, col);
                                }
                            }
                        }
                    });
                    dataEntryEnv.getParentFrame()
                            .showJDialogAsSheet(copyColDownConfirmationPane.createDialog(Spreadsheet.this, "Copy Column?"));

                }
            }
        });

        copyRowDown = new JLabel(copyRowDownButton);
        copyRowDown.setToolTipText("<html><b>copy row downwards</b>" +
                "<p>duplicate selected row and copy it from the current</p>" +
                "<p>position down to the final row</p></html>");
        copyRowDown.setEnabled(false);
        copyRowDown.addMouseListener(new MouseAdapter() {

            public void mouseExited(MouseEvent mouseEvent) {
                copyRowDown.setIcon(copyRowDownButton);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                copyRowDown.setIcon(copyRowDownButtonOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                copyRowDown.setIcon(copyRowDownButton);

                final int row = table.getSelectedRow();

                JOptionPane copyRowDownConfirmationPane = new JOptionPane("<html><b>Confirm Copy of Row...</b><p>Are you sure you wish to copy " +
                        "this row downwards?</p><p>This Action can not be undone!</p>", JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);

                copyRowDownConfirmationPane.setIcon(copyRowDownWarningIcon);

                UIHelper.applyOptionPaneBackground(copyRowDownConfirmationPane, UIHelper.BG_COLOR);

                copyRowDownConfirmationPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
                            int lastOptionAnswer = Integer.valueOf(event.getNewValue().toString());
                            dataEntryEnv.getParentFrame().hideSheet();
                            if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                copyRowDownwards(row);
                            }
                        }
                    }
                });
                dataEntryEnv.getParentFrame()
                        .showJDialogAsSheet(copyRowDownConfirmationPane.createDialog(Spreadsheet.this, "Copy Row Down?"));
            }
        });

        addProtocol = new JLabel(addProtocolButton);
        addProtocol.setToolTipText("<html><b>add a protocol column</b>" +
                "<p>Add a protocol column to the table</p></html>");
        addProtocol.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                addProtocol.setIcon(addProtocolButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addProtocol.setIcon(addProtocolButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addProtocol.setIcon(addProtocolButton);
                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Protocol REF", "Protocol used for experiment", DataTypes.LIST, "",
                        false, false, false);

                fo.setFieldList(sde.getProtocolNames());

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Protocol REF", null, -1);
            }
        });

        addFactor = new JLabel(addFactorButton);
        addFactor.setToolTipText("<html><b>add a factor column</b>" +
                "<p>Add a factor column to the table</p></html>");
        addFactor.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addFactor.setIcon(addFactorButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addFactor.setIcon(addFactorButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addFactor.setIcon(addFactorButton);
                showAddColumnsGUI(AddColumnGUI.ADD_FACTOR_COLUMN);
            }
        });

        addCharacteristic = new JLabel(addCharacteristicButton);
        addCharacteristic.setToolTipText(
                "<html><b>add a characteristic column</b>" +
                        "<p>Add a characteristic column to the table</p></html>");
        addCharacteristic.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addCharacteristic.setIcon(addCharacteristicButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addCharacteristic.setIcon(addCharacteristicButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addCharacteristic.setIcon(addCharacteristicButton);
                showAddColumnsGUI(AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
            }
        });

        addParameter = new JLabel(addParameterButton);
        addParameter.setToolTipText("<html><b>add a parameter column</b>" +
                "<p>Add a parameter column to the table</p></html>");
        addParameter.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                addParameter.setIcon(addParameterButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                addParameter.setIcon(addParameterButton);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                addParameter.setIcon(addParameterButton);
                showAddColumnsGUI(AddColumnGUI.ADD_PARAMETER_COLUMN);
            }
        });

        undo = new JLabel(undoButton);
        undo.setToolTipText("<html><b>undo previous action<b></html>");
        undo.setEnabled(um.canUndo());
        undo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                undo.setIcon(undoButton);
                um.undo();

                if (highlightActive) {
                    setRowsToDefaultColor();
                }
                table.addNotify();
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                undo.setIcon(undoButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                undo.setIcon(undoButton);
            }
        });

        redo = new JLabel(redoButton);
        redo.setToolTipText("<html><b>redo action<b></html>");
        redo.setEnabled(um.canRedo());
        redo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                redo.setIcon(redoButton);
                um.redo();

                if (highlightActive) {
                    setRowsToDefaultColor();
                }
                table.addNotify();

            }

            public void mouseEntered(MouseEvent mouseEvent) {
                redo.setIcon(redoButtonOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                redo.setIcon(redoButton);
            }
        });

        transpose = new JLabel(transposeIcon);
        transpose.setToolTipText("<html>View a transposed version of this spreadsheet</html>");
        transpose.addMouseListener(new MouseAdapter() {

            public void mouseExited(MouseEvent mouseEvent) {
                transpose.setIcon(transposeIcon);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                transpose.setIcon(transposeIconOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                showTransposeSpreadsheetGUI();
            }
        });


        buttonPanel.add(addRow);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(deleteRow);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(deleteColumn);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(multipleSort);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(copyColDown);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(copyRowDown);
        buttonPanel.add(Box.createHorizontalStrut(5));

        //add factor, protocol, parameter and characteristic here!
        buttonPanel.add(addFactor);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(addCharacteristic);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(addProtocol);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(addParameter);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(transpose);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(undo);
        buttonPanel.add(Box.createHorizontalStrut(5));
        buttonPanel.add(redo);


        addProtocol.setEnabled(false);
        addParameter.setEnabled(false);
        addCharacteristic.setEnabled(false);

        JPanel labelContainer = new JPanel(new GridLayout(1, 1));
        labelContainer.setBackground(UIHelper.BG_COLOR);

        JLabel lab = UIHelper.createLabel(title, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, JLabel.RIGHT);
        lab.setBackground(UIHelper.BG_COLOR);
        lab.setVerticalAlignment(JLabel.CENTER);
        lab.setPreferredSize(new Dimension(200, 30));

        labelContainer.add(lab);

        buttonPanel.add(labelContainer);
        buttonPanel.add(Box.createHorizontalStrut(10));
        //buttonPanel.add(exportAsCSV);
        add(buttonPanel, BorderLayout.NORTH);
    }

    /**
     * Delete column given an index.
     *
     * @param vColIndex - column to remove.
     */
    private void deleteColumn(int vColIndex) {
        curColDelete = vColIndex;
        currentState = DELETING_COLUMN;

        TableColumn col = table.getColumnModel().getColumn(curColDelete);


        if (!col.getHeaderValue().toString().equals(TableReferenceObject.ROW_NO_TEXT)) {
            if (tro.isRequired(col.getHeaderValue().toString()) && !areMultipleOccurences(col.getHeaderValue().toString())) {
                optionPane = new JOptionPane("<html>This column can not be deleted due to it being a required field in this assay!</html>",
                        JOptionPane.OK_OPTION);
                optionPane.setIcon(requiredColumnWarningIcon);
                UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
                optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {
                        if (event.getPropertyName()
                                .equals(JOptionPane.VALUE_PROPERTY)) {
                            dataEntryEnv.getParentFrame().hideSheet();
                        }
                    }
                });
                dataEntryEnv.getParentFrame()
                        .showJDialogAsSheet(optionPane.createDialog(this,
                                "Can not delete"));
            } else {
                optionPane = new JOptionPane("<html>Are you sure you want to delete this column? <p>This Action can not be undone!</p></html>",
                        JOptionPane.INFORMATION_MESSAGE,
                        JOptionPane.YES_NO_OPTION,
                        confirmRemoveColumnIcon);
                UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
                optionPane.addPropertyChangeListener(this);
                dataEntryEnv.getParentFrame()
                        .showJDialogAsSheet(optionPane.createDialog(this,
                                "Confirm Delete Column"));
            }
        }
    }

    private boolean areMultipleOccurences(String colName) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        int count = 0;
        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().equals(colName)) {
                count++;
                if (count > 1) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Delete multiple rows
     *
     * @param index - array of ints containing indexes to remove.
     */
    private void deleteRow(int[] index) {
        rowsToDelete = index;
        currentState = DELETING_ROW;

        optionPane = new JOptionPane("<html>Are you sure you want to delete these rows? <p>This Action can not be undone!</p></html>",
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION,
                confirmRemoveRowIcon);
        optionPane.addPropertyChangeListener(this);
        UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
        dataEntryEnv.getParentFrame()
                .showJDialogAsSheet(optionPane.createDialog(this,
                        "Confirm Delete Rows"));
    }

    /**
     * Delete a row with a given index.
     *
     * @param index - index of row to remove
     */
    private void deleteRow(int index) {
        rowsToDelete = new int[]{index};
        currentState = DELETING_ROW;

        optionPane = new JOptionPane("<html>Are you sure you want to delete this row? <p>This Action can not be undone!</p></html>",
                JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION,
                confirmRemoveRowIcon);
        optionPane.addPropertyChangeListener(this);
        UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
        dataEntryEnv.getParentFrame()
                .showJDialogAsSheet(optionPane.createDialog(this,
                        "Confirm Delete Rows"));
    }

    /**
     * Creates a popup when the user has dragged across cells which allows the user to autofill the columns
     * (based on values dragged from), copy the selection, or clear the fields.
     *
     * @param jc - Parent Component to display popup in.
     * @param x  - x position to display popup in
     * @param y  - y position for where to display popup.
     */
    private void dragCellPopupMenu(JComponent jc, final int x, final int y) {
        final JPopupMenu popup = new JPopupMenu("Utilities");
        popup.setLightWeightPopupEnabled(false);
        popup.setBackground(new Color(0, 104, 56, 50));
        jc.add(popup);

        JMenuItem autoIncrementCells = new JMenuItem("Autofill");
        autoIncrementCells.setForeground(UIHelper.DARK_GREEN_COLOR);
        autoIncrementCells.setBackground(UIHelper.BG_COLOR);

        autoIncrementCells.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (table.getValueAt(startRow, startCol) != null) {
                    int endRow = table.rowAtPoint(new Point(x, y));
                    if (!table.getColumnModel().getColumn(startCol)
                            .getHeaderValue().toString()
                            .contains(TableReferenceObject.ROW_NO_TEXT)) {

                        String startVal = table.getValueAt(startRow, startCol).toString();
                        Pattern p = Pattern.compile("[[0-9]*.]*[0-9]+");
                        Matcher m = p.matcher(startVal);
                        int finalStartIndex = -1;
                        int finalStopIndex = -1;

                        while (m.find()) {
                            finalStartIndex = m.start();
                            finalStopIndex = m.end();
                        }

                        if (finalStartIndex != -1) {
                            String startVal2 = "";
                            int finalStartIndex2 = -1;
                            int finalStopIndex2 = -1;

                            if (table.getValueAt(startRow + 1, startCol) != null) {
                                // we have a 2nd value to determine the increment with
                                startVal2 = table.getValueAt(startRow + 1,
                                        startCol).toString();

                                if (!startVal2.equals("")) {
                                    m = p.matcher(startVal2);

                                    while (m.find()) {
                                        finalStartIndex2 = m.start();
                                        finalStopIndex2 = m.end();
                                    }
                                }
                            }

                            String strippedStartVal = startVal.substring(0,
                                    finalStartIndex);

                            boolean unconventionalFormatting = false;

                            double valToIncrement;
                            try {
                                valToIncrement = Double.valueOf(startVal.substring(
                                        finalStartIndex, finalStopIndex));
                            } catch (NumberFormatException nfe) {
                                // in the event where there is a strange value e.g. 1.254.213, then we need to take the last value as the incrementer!
                                valToIncrement = Double.valueOf(startVal.substring(startVal.lastIndexOf(".") + 1));
                                strippedStartVal = startVal.substring(0, startVal.lastIndexOf(".") + 1);
                                unconventionalFormatting = true;
                            }
                            double difference = 0;

                            if (finalStartIndex2 != -1) {

                                double valToIncrement2;
                                try {
                                    valToIncrement2 = Double.valueOf(startVal2.substring(
                                            finalStartIndex2, finalStopIndex2));
                                } catch (NumberFormatException nfe) {
                                    // in the event where there is a strange value e.g. 1.254.213, then we need to take the last value as the incrementer!
                                    valToIncrement2 = Double.valueOf(startVal2.substring(startVal2.lastIndexOf(".") + 1));
                                }

                                difference = valToIncrement2 -
                                        valToIncrement;
                                valToIncrement = valToIncrement2;
                            } else {
                                valToIncrement = valToIncrement + 1;
                            }

                            String autofillContents = "";

                            for (int row = startRow + 1; row <= endRow; row++) {
                                String strValOfInc = String.valueOf(valToIncrement);
                                strValOfInc = strValOfInc.endsWith(".0") ? strValOfInc.substring(0, strValOfInc.length() - 2) : strValOfInc;

                                autofillContents += strippedStartVal + strValOfInc;
                                if (row != endRow) {
                                    autofillContents += "\n";
                                }

                                if (finalStartIndex2 != -1) {
                                    valToIncrement += difference;
                                    if (valToIncrement < 0 && unconventionalFormatting) {
                                        // positify it! :o) this is the behaviour in excel
                                        valToIncrement *= -1;
                                        difference *= -1;
                                    }
                                    valToIncrement = Utils.formatDoubleValue(startVal, valToIncrement);
                                } else {
                                    valToIncrement++;
                                }
                            }
                            putStringOnClipboard(autofillContents);
                            paste(startRow + 1, startCol, true);
                        } else {
                            // fill as string
                            String autofillContents = "";

                            for (int row = startRow + 1; row <= endRow; row++) {
                                autofillContents += startVal;
                                if (row != endRow) {
                                    autofillContents += "\n";
                                }
                            }

                            putStringOnClipboard(autofillContents);
                            paste(startRow + 1, startCol, true);
                        }
                    }
                }
            }
        });

        JMenuItem mapFilesToDirectory = new JMenuItem("Resolve file names");
        mapFilesToDirectory.setToolTipText("<html>" +
                "<strong>resolve file names</strong>" +
                "<p>you can select a directory and <strong>ISAcreator</strong> will resolve the correct,</p> " +
                "<p>absolute file location (where possible!)</p>" +
                "</html>");

        mapFilesToDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                resolveFileLocations();
            }
        });

        JMenuItem copyData = new JMenuItem("Copy selection");
        copyData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                copy();
            }
        });

        JMenuItem clearData = new JMenuItem("Clear fields");
        clearData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (!table.getColumnModel().getColumn(startCol)
                        .getHeaderValue().toString().contains("Row")) {

                    clearCells(startRow, startCol,
                            table.rowAtPoint(new Point(x, y)),
                            table.columnAtPoint(new Point(x, y)));
                }
            }
        });

        JMenuItem close = new JMenuItem("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                popup.setVisible(false);
            }
        });

        popup.add(autoIncrementCells);
        popup.add(new JSeparator());
        popup.add(mapFilesToDirectory);
        popup.add(new JSeparator());
        popup.add(copyData);
        popup.add(clearData);
        popup.add(new JSeparator());
        popup.add(close);

        popup.show(jc, x, y);
    }

    private void putStringOnClipboard(String toPlace) {
        StringSelection stsel = new StringSelection(toPlace);
        system.setContents(stsel, stsel);
    }

    private boolean checkIsEmpty(TableColumn tc) {
        int colIndex = tc.getModelIndex();
        int viewIndex = Utils.convertModelIndexToView(table, colIndex);
        for (int rowNo = 0; rowNo < table.getRowCount(); rowNo++) {
            if (table.getValueAt(rowNo, viewIndex) != null && !table.getValueAt(rowNo, viewIndex).equals("")) {
                return false;
            }

        }
        return true;
    }

    private void resolveFileLocations() {
        int[] selectedRows = table.getSelectedRows();
        int selectedColumn = table.getSelectedColumn();

        JFileChooser fileLocChooser = new JFileChooser();
        fileLocChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileLocChooser.setDialogTitle("Select directory to search through");
        fileLocChooser.setApproveButtonText("Select directory");

        if (fileLocChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File dir = fileLocChooser.getSelectedFile();

            String[] files = new String[selectedRows.length];

            for (int row = 0; row < selectedRows.length; row++) {
                files[row] = table.getValueAt(selectedRows[row], selectedColumn).toString();
            }

            FileLocationMapperUtil fmu = new FileLocationMapperUtil();
            Map<String, String> result = fmu.findProperFileLocations(files, dir);

            for (int selectedRow : selectedRows) {
                String candidateVal = table.getValueAt(selectedRow, selectedColumn).toString();
                if (result.keySet().contains(candidateVal)) {
                    table.setValueAt(result.get(candidateVal), selectedRow, selectedColumn);
                }
            }
        }

        // otherwise, do nothing
    }


    /**
     * Perfroms a clipboard function of COPY
     */
    public void copy() {
        doCopy(false, null); //sets isCut to false
    }

    /**
     * Perfroms a clipboard function of cutting (COPY + CLEAR)
     */
    public void cut() {
        doCopy(true, null); //sets isCut to true
    }

    /**
     * Performs a clipboard function of pasting
     *
     * @param startRow - Where pasting is to start. If from selected row, supply -1 as a parameter
     * @param startCol - Where pasting is to start. If from selected column, supply -1 as a parameter
     */
    public void paste(int startRow, int startCol, boolean storeInHistory) {
        CellEditor editor = table.getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }

        startRow = (startRow == -1) ? table.getSelectedRow() : startRow;
        startCol = (startCol == -1) ? table.getSelectedColumn() : startCol;

        //checks if anything is selected
        if (startRow != -1) {
            try {
                String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));

                SpreadsheetCellPoint size = SpreadsheetModel.getSize(trstring, '\t');


                int rowSpaceToFill = (size.getRow());

                int endRow = Math.min(table.getRowCount() - 1, (startRow + size.getRow()) - 1);
                int endCol = Math.min(table.getColumnCount() - 1, (startCol + size.getCol()) - 1);

                int colSpaceToFill = (endCol - startCol) + 1;

                int[] colRange = new int[colSpaceToFill];
                int[] rowRange = new int[rowSpaceToFill];
                if (colSpaceToFill > 0) {

                    colRange = new int[colSpaceToFill];
                    int startVal = 0;
                    for (int val = startCol; val <= endCol; val++) {
                        colRange[startVal] = val;
                        startVal++;
                    }
                }

                if (rowSpaceToFill > 0) {
                    rowRange = new int[rowSpaceToFill];
                    int startVal = 0;
                    for (int val = startRow; val <= endRow; val++) {
                        rowRange[startVal] = val;
                        startVal++;
                    }
                }

                if (colRange.length == 0) {
                    colRange = new int[1];
                    colRange[0] = startCol;
                }

                if (rowRange.length == 0) {
                    rowRange = new int[1];
                    rowRange[0] = startRow;
                }

                SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(rowRange, Utils.convertSelectedColumnsToModelIndices(table, colRange));

                // add to history
                if (storeInHistory) {
                    spreadsheetHistory.add(affectedRange);
                }
                spreadsheetModel.extendedFromString(trstring, '\t', affectedRange);
                spreadsheetModel.extendedSetSelection(affectedRange);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Performs a clipboard function of cut/copy
     *
     * @param isCut true for cut, false for copy
     */

    private void doCopy(boolean isCut, SpreadsheetCellRange toUse) {
        CellEditor editor = table.getCellEditor();
        if (editor != null) {
            editor.cancelCellEditing();
        }
        boolean modifiedCellRange = false;
        if (table.getSelectedRowCount() != 0 || toUse != null) {
            if (toUse == null) {

                toUse = new SpreadsheetCellRange(table.getSelectedRows(), Utils.convertSelectedColumnsToModelIndices(table, table.getSelectedColumns()));
                modifiedCellRange = true;
            }

            if (isCut && modifiedCellRange) {
                spreadsheetHistory.add(toUse);
            }

            String str = spreadsheetModel.extendedToString(toUse, '\t');
            log.info("copying \n" + str);
            StringSelection stsel = new StringSelection(str);
            system.setContents(stsel, stsel);

            if (isCut) {
                spreadsheetModel.clearRange(toUse);
            }
        } else {
            log.info("no rows are selected so no copying has taken place.");
        }

    }

    /**
     * Reorders the columns defined in the table to ensure that parameters occur the protocol ref they were added with and
     *
     * @param fileName - the name of the file being checked!
     * @return whether or not the table is consistent
     */
    private boolean checkTableColumnOrderBad(String fileName) {
        tcc = new TableConsistencyChecker();
        // return true if ok, false if not
        return tcc.runInspection(fileName, table, columnDependencies);
    }

    public TableConsistencyChecker getTableConsistencyChecker() {
        return tcc;
    }

    /**
     * Exports current Table into a CSV or TAB file depending on separator chosen.
     *
     * @param f                  File to be written to
     * @param separator          -> "\t" for tab separation or "," for CSV
     * @param removeEmptyColumns - should empty columns be removed from the submission?
     * @throws java.io.FileNotFoundException - never thrown since the file is created if it doesn't exist, and over written if
     *                                       it already exists
     */
    public boolean exportTable(File f, String separator, boolean removeEmptyColumns)
            throws FileNotFoundException {
        PrintStream ps = new PrintStream(f);

        Set<TableColumn> emptyColumns = new HashSet<TableColumn>();

        Map<String, OntologyObject> history = dataEntryEnv.getUserHistory();

        for (int col = 1; col < table.getColumnCount(); col++) {
            TableColumn tc = table.getColumnModel().getColumn(col);
            // only hide columns which are empty and that are not necessarily required!
            if (removeEmptyColumns && checkIsEmpty(tc) && !tro.isRequired(tc.getHeaderValue().toString())) {
                emptyColumns.add(tc);
            } else {
                if (col == 1) {
                    ps.print("\"" + tc.getHeaderValue() + "\"");
                } else {
                    ps.print(separator + "\"" + tc.getHeaderValue() + "\"");
                }

                if (tc.getCellEditor() instanceof OntologyCellEditor ||
                        tc.getHeaderValue().toString().equalsIgnoreCase("unit")) {
                    ps.print(separator + "\"Term Source REF\"");
                    ps.print(separator + "\"Term Accession Number\"");
                }
            }
        }

        ps.print("\n");

        // write out each column
        for (int rows = 0; rows < table.getRowCount(); rows++) {
            String rowInfo = "";

            for (int cols = 1; cols < table.getColumnCount(); cols++) {
                // the value to be output to the field
                TableColumn tc = table.getColumnModel().getColumn(cols);

                if (!emptyColumns.contains(tc)) {
                    String val;

                    // where the term came from if there is an ontology term.
                    String source = "";
                    String toAdd;

                    if (table.getValueAt(rows, cols) != null) {
                        val = table.getValueAt(rows, cols).toString();

                        if (tc.getCellEditor() instanceof OntologyCellEditor ||
                                tc.getHeaderValue().toString()
                                        .equalsIgnoreCase("unit")) {


                            String termAccession = "";

                            if (!GeneralUtils.isValueURL(val)) {
                                OntologyObject oo = history.get(val);

                                if (oo != null) {
                                    termAccession = oo.getTermAccession();
                                }

                                if (val.contains(":")) {
                                    source = val.substring(0, val.indexOf(":"));
                                    val = val.substring(val.indexOf(":") + 1);
                                }
                            }

                            toAdd = "\"" + val + "\"" + separator + "\"" + source + "\"" + separator +
                                    "\"" + termAccession + "\"";
                        } else {
                            toAdd = "\"" + val + "\"";
                        }
                    } else {
                        if (tc.getCellEditor() instanceof OntologyCellEditor ||
                                tc.getHeaderValue().toString()
                                        .equalsIgnoreCase("unit")) {
                            // add triple separated value for term : source : accession triple
                            toAdd = "\"\"" + separator + "\"\"" + separator + "\"\"";
                        } else {
                            toAdd = "\"\"";
                        }
                    }

                    // only add the row separator if we are not adding the last column!
                    if (cols == table.getColumnCount() - 1) {
                        rowInfo += toAdd;
                    } else {
                        rowInfo += (toAdd + separator);
                    }
                }
            }

            if (rowInfo.length() > 0) {
                ps.println(rowInfo);
            }
        }

        ps.close();

        return checkTableColumnOrderBad(f.getName());
    }


    /**
     * Return the current DataEntryEnvironment
     *
     * @return DataEntryPanel
     */
    public DataEntryEnvironment getDataEntryEnv
            () {
        return dataEntryEnv;
    }

    /**
     * Method returns a Set of all the files defined in a spreadsheet. These locations are used to zip up the data files
     * in the ISArchive for submission to the index.
     *
     * @return Set of files defined in the spreadsheet
     */
    public Set<String> getFilesDefinedInTable
            () {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
        Set<String> files = new HashSet<String>();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (tro.acceptsFileLocations(tc.getHeaderValue().toString())) {
                int colIndex = Utils.convertModelIndexToView(table, tc.getModelIndex());

                for (int row = 0; row < table.getRowCount(); row++) {
                    String s = (table.getValueAt(row, colIndex) == null) ? ""
                            : table.getValueAt(row,
                            colIndex).toString();

                    if (s != null && !s.trim().equals("")) {
                        files.add(s);
                    }
                }
            }
        }

        return files;
    }

    /**
     * Return a list of the current column headers
     *
     * @param needColNo - if the column number is needed, a Col: n is prepended to the column name, where n is the column number.
     * @param unique    - if unique is true, then only unique columns are sent back. doens't make sense when needColNo is set to true.
     * @return A vector of strings containing headers - set to vector since the values will be instantly suitable for a ComboBox for example.
     */
    public Vector<String> getHeaders
            (
                    boolean needColNo,
                    boolean unique) {
        Vector<String> headerList = new Vector<String>();

        for (int i = 0; i < spreadsheetModel.getColumnCount(); i++) {
            String h;

            if (!spreadsheetModel.getColumnName(i).equals(TableReferenceObject.ROW_NO_TEXT)) {
                if (needColNo) {
                    h = "Col: " + i + " " + spreadsheetModel.getColumnName(i);
                } else {
                    h = spreadsheetModel.getColumnName(i);
                }

                if (unique) {
                    if (!headerList.contains(h)) {
                        headerList.add(h);
                    }
                } else {
                    headerList.add(h);
                }
            }
        }

        return headerList;
    }

    /**
     * Gets the ontology sources used within the table by searching each column defined to use ontologies and pulling out
     * all Sources used. These sources can then be used in the import section to ensure that all ontologies used throughout
     * the submission have been defined.
     *
     * @return Set<String> containing the Ontologies defined in the Spreadsheet.
     */
    public Set<String> getOntologiesDefinedInTable
            () {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        HashSet<String> ontologySources = new HashSet<String>();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (tro.getClassType(tc.getHeaderValue().toString().trim())
                    == DataTypes.ONTOLOGY_TERM ||
                    tc.getHeaderValue().toString().trim()
                            .equalsIgnoreCase("unit")) {
                int colIndex = Utils.convertModelIndexToView(table, tc.getModelIndex());

                for (int row = 0; row < table.getRowCount(); row++) {
                    String s = (table.getValueAt(row, colIndex) == null) ? ""
                            : table.getValueAt(row,
                            colIndex).toString();

                    if (s.contains(":")) {
                        // an ontology term should be in the field!
                        String[] termParts = s.split(":");

                        if (!termParts[0].trim().equals("") && !ontologySources.contains(termParts[0].trim())) {
                            ontologySources.add(termParts[0].trim());
                        }
                    }
                }
            }
        }

        return ontologySources;
    }

    /**
     * Return the parent frame for the entire ISAcreator GUI.
     *
     * @return MainGUI object.
     */
    public AniSheetableJFrame getParentFrame
            () {
        return dataEntryEnv.getParentFrame();
    }

    /**
     * Get the StudyDataEntry object the table is part of.
     *
     * @return the StudyDataEntry object for the current Spreadsheet
     */
    public StudyDataEntry getSDE
            () {
        return sde;
    }

    /**
     * Return this JTable
     *
     * @return the JTable component
     */
    public JTable getTable
            () {
        return table;
    }

    /**
     * Return the SpreadsheetModel associated with the current table
     *
     * @return SpreadsheetModel for current table.
     */
    public SpreadsheetModel getTableModel
            () {
        return spreadsheetModel;
    }

    /**
     * Return the respective TableReferenceObject for the current table
     *
     * @return - TableReferenceObject defining current table.
     */
    public TableReferenceObject getTableReferenceObject() {
        return tro;
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {

        if (event.getSource() instanceof JLabel) {

        } else {
            if (SwingUtilities.isRightMouseButton(event)) {
                String columnName = table.getColumnModel()
                        .getColumn(table.columnAtPoint(
                                event.getPoint())).getHeaderValue().toString();
                SwingUtilities.convertPointFromScreen(event.getPoint(), table);
                popupMenu(table, event.getX() + 10, event.getY() + 10, columnName);
            }

            if (SwingUtilities.isLeftMouseButton(event)) {
                startRow = table.rowAtPoint(event.getPoint());
                startCol = table.columnAtPoint(event.getPoint());
            }
        }
    }

    public void mouseReleased(MouseEvent event) {

        if (SwingUtilities.isLeftMouseButton(event) && (table.rowAtPoint(event.getPoint()) - startRow) > 1) {
            SwingUtilities.convertPointFromScreen(event.getPoint(), table);
            dragCellPopupMenu(table, event.getX(), event.getY());
        }
    }

    /**
     * Populate the table given a list of values which are to be entered.
     *
     * @param data - data to be entered.
     */
    public void populateTable
            (List<List<String>> data) {
        addRows(data.size(), false);

        int dataSize = data.size();

        for (int row = 0; row < dataSize; row++) {
            List<String> rowData = data.get(row);
            int rowDataSize = rowData.size();

            for (int col = 0; col < rowDataSize; col++) {
                // add one to column to take into account that the first column is the row number
                String rowDataVal = (rowData.get(col) == null) ? ""
                        : rowData.get(col);

                spreadsheetModel.setValueAt(rowDataVal, row, col + 1);
            }
        }

        spreadsheetModel.fireTableDataChanged();
    }

    /**
     * Popup menu supplies the main form of menu in the draw tool
     *
     * @param jc         the component the popup menu is to be added to
     * @param x          horizontal position where the popup position should be
     * @param y          vertical position for the location of the popup menu.
     * @param columnName -> name of column where the popup was called.
     */
    private void popupMenu
            (JComponent
                    jc, final int x,
             final int y, String
                    columnName) {
        final JPopupMenu popup = new JPopupMenu("Utilities");
        popup.setLightWeightPopupEnabled(false);
        popup.setBackground(new Color(0, 104, 56, 50));

        JMenuItem undo = new JMenuItem("Undo");
        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                um.undo();
            }
        });

        undo.setEnabled(um.canUndo());


        JMenuItem redo = new JMenuItem("Redo");
        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                um.redo();
            }
        });

        redo.setEnabled(um.canRedo());


        JMenu addRow = new JMenu("Add Row(s)");

        JMenuItem addRowsAtEndOfTable = new JMenuItem("Add rows to end of table");
        addRowsAtEndOfTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMultipleRowsGUI();
            }
        });

        JMenuItem addRowsBeforeSelectedRow = new JMenuItem("Add row before this row");
        addRowsBeforeSelectedRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertRowInPosition(table.getSelectedRow());
            }
        });

        addRow.add(addRowsBeforeSelectedRow);
        addRow.add(addRowsAtEndOfTable);

        JMenuItem deleteRow = new JMenuItem("Remove Row(s)");
        deleteRow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selectedRows = table.getSelectedRows();
                deleteRow(selectedRows);
            }
        });

        JMenu addColumn = new JMenu("Add Column");


        JMenuItem addSampleName = new JMenuItem("Add Sample Name");
        addSampleName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                popup.setVisible(false);

                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Sample Name", "The name of the sample being used",
                        DataTypes.STRING, "", false, false, false);

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Sample Name", null, -1);
            }
        });

        JMenuItem addMaterialType = new JMenuItem("Add Material Type");
        addMaterialType.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                popup.setVisible(false);


                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Material Type", "The type of material used for analysis in this assay",
                        DataTypes.ONTOLOGY_TERM, "", false, false, false);

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Material Type", null, -1);
            }
        });


        JMenuItem addCharacteristic = new JMenuItem("Add Characteristic");
        addCharacteristic.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                showAddColumnsGUI(AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
            }
        });

        JMenuItem addFactor = new JMenuItem("Add Factor");
        addFactor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                showAddColumnsGUI(AddColumnGUI.ADD_FACTOR_COLUMN);
            }
        });

        JMenuItem addProtocol = new JMenuItem("Add Protocol");
        addProtocol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Protocol REF", "Protocol used for experiment",
                        DataTypes.LIST, "", false, false, false);


                fo.setFieldList(sde.getProtocolNames());

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Protocol REF", null, -1);
            }
        });

        JMenuItem addParameter = new JMenuItem("Add Parameter");
        addParameter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                showAddColumnsGUI(AddColumnGUI.ADD_PARAMETER_COLUMN);
            }
        });

        JMenuItem addComment = new JMenuItem("Add Comment");
        addComment.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
                showAddColumnsGUI(AddColumnGUI.ADD_COMMENT_COLUMN);
            }
        });

        JMenuItem addDate = new JMenuItem("Add Date");
        addDate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Date", "Date field", DataTypes.DATE, "", false,
                        false, false);

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Date", "", -1);
            }
        });

        JMenuItem addPerformer = new JMenuItem("Add Performer");
        addPerformer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Performer",
                        "Performer of this hybridisation/sample preparation",
                        DataTypes.STRING, "", false, false, false);

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Performer", null, -1);
            }
        });

        JMenuItem addProvider = new JMenuItem("Add Provider");
        addProvider.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);

                TableFieldObject fo = new TableFieldObject(table.getColumnCount(),
                        "Provider", "Provider of this data", DataTypes.STRING, "",
                        false, false, false);

                addFieldToReferenceObject(fo);

                addColumnAfterPosition("Provider", null, -1);
            }
        });

        addColumn.add(addSampleName);
        addColumn.add(addMaterialType);

        if (checkColumnExists("Material Type")) {
            addMaterialType.setEnabled(false);
        }

        final String[] toRemove = new String[]{null};
        if (tro.getMissingFields() != null && tro.getMissingFields().size() != 0) {
            for (final String missingField : tro.getMissingFields().keySet()) {
                if (!checkColumnExists(missingField)) {
                    JMenuItem item = new JMenuItem(missingField);
                    item.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ae) {
                            popup.setVisible(false);

                            addFieldToReferenceObject(tro.getMissingFields().get(missingField));

                            addColumnAfterPosition(missingField, "", -1);
                            toRemove[0] = missingField;

                        }
                    });
                    addColumn.add(item);
                }
            }
            if (toRemove[0] != null) {
                tro.getMissingFields().remove(toRemove[0]);
            }
        }

        addColumn.add(new JSeparator());

        addColumn.add(addCharacteristic);
        addColumn.add(addFactor);

        if (columnName.toLowerCase().contains("protocol")) {
            addColumn.add(addParameter);
        }

        if (!columnName.toLowerCase().contains("characteristic") &&
                !columnName.toLowerCase().contains("unit") &&
                !columnName.toLowerCase().contains("factor") &&
                !columnName.toLowerCase().contains("date") &&
                !columnName.toLowerCase().contains("performer") &&
                !columnName.toLowerCase().contains("provider") &&
                !columnName.toLowerCase().contains("comment") &&
                !columnName.toLowerCase().contains("material type")) {
            addColumn.add(addProtocol);
        }

        addColumn.add(new JSeparator());
        addColumn.add(addComment);

        if (columnName.toLowerCase().contains("protocol")) {
            addColumn.add(addDate);
            addColumn.add(addPerformer);
        }

        addColumn.add(addProvider);

        JMenuItem deleteColumn = new JMenuItem("Remove Column");
        deleteColumn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(table.getSelectedColumns().length > 1)) {
                    deleteColumn(table.getSelectedColumn());
                } else {
                    showColumnErrorMessage();
                }
            }
        });


        JMenu unhideColumns = new JMenu("Add previously removed column(s)");
        if (hiddenColumns.size() > 0) {

            for (final String hiddenColumn : hiddenColumns) {
                JMenuItem item = new JMenuItem(hiddenColumn);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        addColumnAfterPosition(hiddenColumn, null, -1);
                        hiddenColumns.remove(hiddenColumn);
                    }
                });
                unhideColumns.add(item);
            }
        }

        JMenuItem copyColumnDown = new JMenuItem("Copy Column Downwards");
        copyColumnDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyColumnDownwards(table.getSelectedRow(),
                        table.getSelectedColumn());
            }
        });

        JMenuItem copyRowDown = new JMenuItem("Copy Row Downwards");
        copyRowDown.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copyRowDownwards(table.getSelectedRow());
            }
        });

        JMenuItem copy = new JMenuItem("Copy");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                copy();
            }
        });

        JMenuItem paste = new JMenuItem("Paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                paste(-1, -1, true);
            }
        });


        JMenuItem cut = new JMenuItem("Cut");
        cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doCopy(true, null);
            }
        });

        JMenuItem multipleSort = new JMenuItem("Perform Multiple Sort");
        multipleSort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showMultipleColumnSortGUI();
            }
        });


        JMenuItem clearField = new JMenuItem("Clear Field");
        clearField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                clearCells(table.getSelectedRow(), table.getSelectedColumn(), table.getSelectedRow(), table.getSelectedColumn());
            }
        });

        JMenu highlightGroups = new JMenu("Highlight groups");
        createMenuItemsForHighlighter(highlightGroups);

        JMenuItem removeHighlight = new JMenuItem("Remove Highlight");
        removeHighlight.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                if (highlightActive) {

                    setRowsToDefaultColor();
                }

            }
        });

        JMenuItem mapFilesToDirectory = new JMenuItem("Resolve file names");
        mapFilesToDirectory.setToolTipText("<html>" +
                "<strong>resolve file names</strong>" +
                "<p>you can select a directory and <strong>ISAcreator</strong> will resolve the correct,</p> " +
                "<p>absolute file location (where possible!)</p>" +
                "</html>");

        mapFilesToDirectory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                resolveFileLocations();
            }
        });

        if (table.getSelectedRows().length == 0) {
            deleteRow.setEnabled(false);
        }

        if (table.getSelectedColumns().length == 0) {
            deleteColumn.setEnabled(false);
        }

        if (table.getRowCount() == 0) {
            multipleSort.setEnabled(false);
        }

        popup.add(undo);
        popup.add(redo);
        popup.add(new JSeparator());
        popup.add(addRow);
        popup.add(deleteRow);

        popup.add(new JSeparator());
        String columnLC = columnName.toLowerCase();
        if (!columnLC.contains("characteristic") &&
                !columnLC.contains("unit") &&
                !columnLC.contains("factor") &&
                !columnLC.contains("date") &&
                !columnLC.contains("performer") &&
                !columnLC.contains("provider") &&
                !columnLC.contains("comment") &&
                !columnLC.contains("material type")) {
            popup.add(addColumn);
        }
        if (hiddenColumns.size() > 0) {
            popup.add(unhideColumns);
        }
        popup.add(deleteColumn);
        popup.add(new JSeparator());
        popup.add(copyColumnDown);
        popup.add(copyRowDown);
        popup.add(new JSeparator());
        popup.add(copy);
        popup.add(paste);
        popup.add(cut);
        popup.add(new JSeparator());
        popup.add(multipleSort);
        popup.add(new JSeparator());
        popup.add(clearField);
        popup.add(new JSeparator());
        popup.add(mapFilesToDirectory);
        popup.add(new JSeparator());
        popup.add(highlightGroups);
        if (highlightActive) {
            popup.add(removeHighlight);
        }

        popup.show(jc, x, y);
    }

    private void highlight(String toGroupBy, boolean exactMatch, boolean returnSampleNames) {
        if (tgi != null && tgi.isShowing()) {
            tgi.dispose();
        }

        Map<String, List<Object>> groups = getDataGroupsByColumn(toGroupBy, exactMatch, returnSampleNames);

        Map<String, Color> groupColors = new ListOrderedMap<String, Color>();

        for (String s : groups.keySet()) {
            groupColors.put(s, UIHelper.createColorFromString(s, true));
        }
        // then pass the groups and the colours to the TableGroupInfo class to display the gui
        // showing group distribution!
        final Map<Integer, Color> rowColors = paintRows(groups, groupColors);

        tgi = new TableGroupInfo(groups, groupColors, table.getRowCount());
        tgi.setLocation(getWidth() / 2 - tgi.getWidth(), getHeight() / 2 - tgi.getHeight());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    table.setDefaultRenderer(Class.forName("java.lang.Object"), new CustomRowRenderer(rowColors, UIHelper.VER_11_PLAIN));
                    table.repaint();
                    tgi.createGUI();
                    highlightActive = true;
                } catch (ClassNotFoundException e) {
                    //
                }
            }
        });
    }

    private void createMenuItemsForHighlighter(JMenu toAddTo) {
        JMenu characteristicsMenu = new JMenu("Characteristics");
        JMenu factors = new JMenu("Factors");

        Map<String, Set<String>> groupInfo = getColumnGroups();
        JMenuItem mi;
        for (String group : groupInfo.keySet()) {
            if (group.equals("Normal")) {
                for (final String column : groupInfo.get(group)) {
                    if (!column.equals(TableReferenceObject.ROW_NO_TEXT)) {
                        mi = new JMenuItem(column);
                        mi.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent actionEvent) {
                                highlight(column, true, false);
                            }
                        });
                        toAddTo.add(mi);
                    }
                }
            } else if (group.equals("Characteristics")) {

                mi = new JMenuItem("All Characteristics");
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        highlight("Characteristics", false, false);
                    }
                });
                characteristicsMenu.add(mi);
                for (final String column : groupInfo.get(group)) {
                    mi = new JMenuItem(column);
                    mi.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            highlight(column, true, false);
                        }
                    });
                    characteristicsMenu.add(mi);
                }

                toAddTo.add(characteristicsMenu);

            } else if (group.equals("Factor")) {

                mi = new JMenuItem("All Factors");
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        highlight("Factor Value", false, false);
                    }
                });
                factors.add(mi);
                for (final String column : groupInfo.get(group)) {
                    mi = new JMenuItem(column);
                    mi.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            highlight(column, true, false);
                        }
                    });
                    factors.add(mi);
                }
                toAddTo.add(factors);
            }
        }

    }

    private Map<String, Set<String>> getColumnGroups() {

        Map<String, Set<String>> groupInfo = new HashMap<String, Set<String>>();

        Enumeration<TableColumn> enumer = table.getColumnModel().getColumns();

        while (enumer.hasMoreElements()) {
            TableColumn tc = enumer.nextElement();

            String columnHeaderValue = tc.getHeaderValue().toString();

            if (columnHeaderValue.contains("Characteristics")) {
                if (!groupInfo.containsKey("Characteristics")) {
                    groupInfo.put("Characteristics", new HashSet<String>());
                }
                groupInfo.get("Characteristics").add(columnHeaderValue);

            } else if (columnHeaderValue.contains("Factor")) {
                if (!groupInfo.containsKey("Factor")) {
                    groupInfo.put("Factor", new HashSet<String>());
                }
                groupInfo.get("Factor").add(columnHeaderValue);
            } else {
                if (!groupInfo.containsKey("Normal")) {
                    groupInfo.put("Normal", new HashSet<String>());
                }
                groupInfo.get("Normal").add(columnHeaderValue);
            }
        }

        return groupInfo;
    }

    private Map<Integer, Color> paintRows(Map<String, List<Object>> groupsAndRows, Map<String, Color> groupColors) {
        Map<Integer, Color> rowColors = new ListOrderedMap<Integer, Color>();
        for (String group : groupsAndRows.keySet()) {
            List<Object> rows = groupsAndRows.get(group);

            for (Object o : rows) {
                Integer i = (Integer) o;
                rowColors.put(i, groupColors.get(group));
            }
        }

        return rowColors;
    }

    public void highlightSpecificColumns(final Map<Integer, Color> columnColors) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    table.setDefaultRenderer(Class.forName("java.lang.Object"), new ColumnGroupCellRenderer(columnColors));
                    table.repaint();
                    highlightActive = true;
                } catch (ClassNotFoundException e) {
                    //
                }
            }
        });
    }


    public void setRowsToDefaultColor() {
        if (tgi != null && tgi.isShowing()) {
            tgi.dispose();
        }
        try {
            table.setDefaultRenderer(Class.forName("java.lang.Object"), new SpreadsheetCellRenderer());
            table.repaint();
            highlightActive = false;
        } catch (ClassNotFoundException e) {
            // ignore this error
        }
    }

    public void propertyChange
            (PropertyChangeEvent
                    event) {
        if (event.getPropertyName().equals(JOptionPane.VALUE_PROPERTY)) {
            int lastOptionAnswer = Integer.valueOf(event.getNewValue().toString());
            dataEntryEnv.getParentFrame().hideSheet();

            if ((currentState == DELETING_COLUMN) &&
                    (lastOptionAnswer == JOptionPane.YES_OPTION)) {

                removeColumn();
                curColDelete = -1;
                currentState = DEFAULT_STATE;
            }

            if ((currentState == DELETING_ROW) &&
                    (lastOptionAnswer == JOptionPane.YES_OPTION)) {
                removeRows();
                rowsToDelete = null;
                currentState = DEFAULT_STATE;
            }

            currentState = DEFAULT_STATE;
            curColDelete = -1;
            rowsToDelete = null;
        }
    }


    /**
     * Rebuild the dependencies based on mappings built up when reading in ISA-TAB files.
     *
     * @param mappings - Mappings of parent column positions to the dependent column positions.
     */
    private void rebuildDependencies(Map<Integer, ListOrderedSet<Integer>> mappings) {
        log.info("Rebuilding dependencies for: " + title);
        log.info("Number of columns is " + table.getColumnCount());
        for (Integer parentColIndex : mappings.keySet()) {
            if (parentColIndex + 1 < table.getColumnCount()) {
                TableColumn parentCol = table.getColumnModel()
                        .getColumn(parentColIndex + 1);

                // create column to column list mapping if it doesn't already exist
                if (columnDependencies.get(parentCol) == null) {
                    columnDependencies.put(parentCol, new ArrayList<TableColumn>());
                }

                // add dependent columns to mappings
                for (Integer dependentCol : mappings.get(parentColIndex)) {
                    if ((dependentCol + 1) < table.getColumnCount()) {
                        columnDependencies.get(parentCol)
                                .add(table.getColumnModel()
                                        .getColumn(dependentCol + 1));
                    }
                }
            }
        }
    }

    private void removeColumn() {
        if (curColDelete != -1) {
            SpreadsheetModel model = (SpreadsheetModel) table.getModel();
            TableColumn col = table.getColumnModel().getColumn(curColDelete);

            hiddenColumns.add(col.getHeaderValue().toString());
            deleteColumn(model, col);

            removeDependentColumns(col);
            removeColumnFromDependencies(col);
        }
    }

    /**
     * Remove a column from the table, delete all the data associated with the column in the model, and keep indices
     * intact by decreasing the index of every column after the one deleted by one to stop fragmentation.
     */
    private void deleteColumn(SpreadsheetModel model, TableColumn col) {

        int columnModelIndex = col.getModelIndex();
        Vector data = model.getDataVector();
        Vector colIds = model.getColumnIdentifiers();
        table.removeColumn(col);
        colIds.removeElementAt(columnModelIndex);

        // remove any data present in the column on deletion
        for (Object aData : data) {
            Vector row = (Vector) aData;
            row.removeElementAt(columnModelIndex);
        }

        model.setDataVector(data, colIds);

        // decrease each column index after deleted column by 1 so that indexes can be kept intact.
        Enumeration enumer = table.getColumnModel().getColumns();

        while (enumer.hasMoreElements()) {
            TableColumn c = (TableColumn) enumer.nextElement();

            if (c.getModelIndex() >= columnModelIndex) {
                c.setModelIndex(c.getModelIndex() - 1);
            }
        }
        // update the model
        model.fireTableStructureChanged();
    }

    /**
     * Remove a column from a table given a column name
     *
     * @param colName - Name of column to be removed.
     */
    public void removeColumnByName
            (String
                    colName) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(colName)) {
                curColDelete = Utils.convertModelIndexToView(table, col.getModelIndex());
                removeColumn();

                removeDependentColumns(col);
            }
        }

        ((SpreadsheetModel) table.getModel()).fireTableStructureChanged();
    }

    /**
     * Removes a column from the list of dependencies
     *
     * @param col - Column to be removed
     */
    public void removeColumnFromDependencies
            (TableColumn
                    col) {
        boolean removingParent = false;
        TableColumn toRemove = null;
        TableColumn parentColumn = null;

        for (TableColumn cp : columnDependencies.keySet()) {
            if (cp == col) {
                toRemove = cp;
                removingParent = true;
                break;
            }

            for (TableColumn cc : columnDependencies.get(cp)) {
                if (cc == col) {
                    toRemove = cc;
                    parentColumn = cp;
                    break;
                }
            }
        }


        if (toRemove != null) {
            if (removingParent) {
                //remove column with it's associated dependencies
                columnDependencies.remove(toRemove);
            } else {
                // remove column from it's parent
                columnDependencies.get(parentColumn).remove(toRemove);
            }
        } else {
            log.info("Dependents on this column not found, so not removing any other columns!");
        }
    }

    /**
     * if a given column has dependent columns, e.g. does a factor column have an associated unit column. if it does,
     * then remove the factor and the unit. this method does that.
     *
     * @param col - column to remove...
     */
    private void removeDependentColumns(TableColumn col) {

        try {
            if (columnDependencies.containsKey(col)) {
                for (TableColumn tc : columnDependencies.get(col)) {
                    curColDelete = Utils.convertModelIndexToView(table, tc.getModelIndex());
                    removeColumn();
                }
                removeColumnFromDependencies(col);
            }
        } catch (ConcurrentModificationException cme) {
            // ignore this error
        }

    }


    /**
     * Remove a row, fire a table data changed event, and then update the row numbers.
     *
     * @param i - row to be removed.
     */
    private void removeRow(int i) {
        //rows.removeElementAt(convertViewRowToModel(i));
        spreadsheetModel.removeRow(convertViewRowToModel(i));
        spreadsheetModel.updateRowCount();
    }

    /**
     * Remove multiple rows from a table
     */
    private void removeRows() {
        if (rowsToDelete != null && rowsToDelete.length > 0) {
            for (final int i : Utils.arrayToList(rowsToDelete)) {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        removeRow(i);
                    }
                });
            }
        }

        table.addNotify();

        currentState = DEFAULT_STATE;
    }

    /**
     * Searches UserHistory for a unique ontology id (source:term pair)
     *
     * @param uniqueId- the ID being searched for in the previous user history.
     * @return - OntologyObject matching the unique id if found, null otherwise.
     */
    private OntologyObject searchUserHistory
            (String
                    uniqueId) {
        return dataEntryEnv.getUserHistory().get(uniqueId);
    }

    /**
     * Add a listener to be notified when the selected range changes
     *
     * @param l The listener to add
     */
    public void addSelectionListener
            (SpreadsheetSelectionListener
                    l) {
        listenerList.add(SpreadsheetSelectionListener.class, l);
    }

    /**
     * Add a listener for undoable events
     *
     * @param l The listener to add
     */
    public void addUndoableEditListener
            (UndoableEditListener
                    l) {
        spreadsheetHistory.addUndoableEditListener(l);
    }

    /**
     * Setup the JTable with its desired characteristics
     */
    private void setupTable() {
        table = new CustomTable(spreadsheetModel);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(UIHelper.LIGHT_GREEN_COLOR);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(true);
        table.setAutoCreateColumnsFromModel(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().addListSelectionListener(this);
        table.getColumnModel().getSelectionModel().addListSelectionListener(this);
        table.getTableHeader().setReorderingAllowed(true);
        table.getColumnModel().addColumnModelListener(this);
        try {
            table.setDefaultRenderer(Class.forName("java.lang.Object"), new SpreadsheetCellRenderer());
        } catch (ClassNotFoundException e) {
            // ignore this error
        }

        table.addMouseListener(this);
        table.getTableHeader().addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent event) {
            }

            public void mouseMoved(MouseEvent event) {
                // display a tooltip when user hovers over a column. tooltip is derived
                // from the description of a field from the TableReferenceObject.
                JTable table = ((JTableHeader) event.getSource()).getTable();
                TableColumnModel colModel = table.getColumnModel();
                int colIndex = colModel.getColumnIndexAtX(event.getX());

                // greater than 1 to account for the row no. being the first col
                if (colIndex >= 1) {
                    TableColumn tc = colModel.getColumn(colIndex);
                    if (tc != null) {
                        try {
                            table.getTableHeader()
                                    .setToolTipText(tro.getFieldByName(
                                            tc.getHeaderValue().toString()).getDescription());
                        } catch (Exception e) {
                            // ignore this error
                        }
                    }
                }
            }
        });

        //table.getColumnModel().addColumnModelListener(this);
        InputMap im = table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        KeyStroke tab = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);

        //  Override the default tab behaviour
        //  Tab to the next editable cell. When no editable cells goto next cell.
        final Action previousTabAction = table.getActionMap().get(im.get(tab));
        Action newTabAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // maintain previous tab action procedure
                previousTabAction.actionPerformed(e);

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

        table.getActionMap().put(im.get(tab), newTabAction);

        TableColumnModel model = table.getColumnModel();

        for (int i = 0; i < tro.getHeaders().size(); i++) {
            if (!model.getColumn(i).getHeaderValue().toString().equals(TableReferenceObject.ROW_NO_TEXT)) {
                model.getColumn(i).setHeaderRenderer(renderer);
                model.getColumn(i)
                        .setPreferredWidth(calcColWidths(
                                model.getColumn(i).getHeaderValue().toString()));
                // add appropriate cell editor for cell.
                addCellEditor(model.getColumn(i));
            } else {
                model.getColumn(i).setHeaderRenderer(new RowNumberCellRenderer());
            }
        }

        JTableHeader header = table.getTableHeader();

        header.setBackground(UIHelper.BG_COLOR);

        header.addMouseListener(new HeaderListener(header, renderer));

        table.addNotify();
    }

    /**
     * Method is used to show the appropriate add column gui. This method can display the add factor gui,
     * add parameter gui, add characteristic gui, or add comment gui depending on the value of toShow.
     *
     * @param toShow can be any one of four static values from the AddColumnGUI class, e.g. ADD_FACTOR_COLUMN, or ADD_COMMENT_COLUMN.
     */
    private void showAddColumnsGUI
            (
                    int toShow) {
        final AddColumnGUI goingToDisplay;

        switch (toShow) {
            case AddColumnGUI.ADD_FACTOR_COLUMN:
                goingToDisplay = new AddColumnGUI(this,
                        AddColumnGUI.ADD_FACTOR_COLUMN);

                break;

            case AddColumnGUI.ADD_CHARACTERISTIC_COLUMN:
                goingToDisplay = new AddColumnGUI(this,
                        AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);

                break;

            case AddColumnGUI.ADD_PARAMETER_COLUMN:
                goingToDisplay = new AddColumnGUI(this,
                        AddColumnGUI.ADD_PARAMETER_COLUMN);

                break;

            case AddColumnGUI.ADD_COMMENT_COLUMN:
                goingToDisplay = new AddColumnGUI(this,
                        AddColumnGUI.ADD_COMMENT_COLUMN);

                break;

            default:
                goingToDisplay = null;
        }

        if (goingToDisplay != null) {
            goingToDisplay.createGUI();
            // do this to ensure that the gui is fully created before displaying it.
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    dataEntryEnv.getParentFrame().showJDialogAsSheet(goingToDisplay);
                }
            });
        }
    }

    /**
     * Displays an error message when a user tries to delete more than one column at a time.
     */
    private void showColumnErrorMessage
            () {
        if (!(table.getSelectedColumns().length > 1)) {
            deleteColumn(table.getSelectedColumn());
        } else {

            optionPane = new JOptionPane("<html>Multiple column select detected!<p>Please select only one column!</p></html>", JOptionPane.OK_OPTION);
            optionPane.setIcon(selectOneColumnWarningIcon);
            UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
            optionPane.addPropertyChangeListener(this);
            dataEntryEnv.getParentFrame()
                    .showJDialogAsSheet(optionPane.createDialog(this, "Delete Column"));
        }
    }

    /**
     * Displays the MultipleSortGUI
     */
    private void showMultipleColumnSortGUI
            () {
        msGUI.updateAllCombos();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dataEntryEnv.getParentFrame().showJDialogAsSheet(msGUI);
            }
        });
    }


    /**
     * Displays the Transposed Spreadsheet UI
     */
    private void showTransposeSpreadsheetGUI() {

        // todo migrate this to occur in the view so that a loading pane is shown whilst the spreadsheet is loaded.
        SpreadsheetConverter converter = new SpreadsheetConverter(Spreadsheet.this);
        final TransposedSpreadsheetModel transposedSpreadsheetModel = converter.doConversion();

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                tsv = new TransposedSpreadsheetView(transposedSpreadsheetModel, (int) (dataEntryEnv.getParentFrame().getWidth() * 0.80), (int) (dataEntryEnv.getParentFrame().getHeight() * 0.70));
                tsv.createGUI();
                dataEntryEnv.getParentFrame().showJDialogAsSheet(tsv);
                dataEntryEnv.getParentFrame().maskOutMouseEvents();
            }
        });
    }

    /**
     * Displays the AddMultipleRowsGUI
     */
    private void showMultipleRowsGUI
            () {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dataEntryEnv.getParentFrame().showJDialogAsSheet(amrGUI);
            }
        });
    }


    /**
     * Substitutes a given value prevTerm with a new value newTerm in a column whose header
     * is colName
     *
     * @param colName  - Column to be searched in.
     * @param prevTerm - the term to replace
     * @param newTerm  - the new term to be used instead.
     */
    public void susbstituteTermsInColumn(String colName, String prevTerm, String newTerm) {

        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(colName)) {
                int colIndex = col.getModelIndex();

                for (int i = 0; i < spreadsheetModel.getRowCount(); i++) {
                    // safety precaution to finalise any cells. otherwise their value would be missed!
                    if (table.getCellEditor(i, colIndex) != null) {
                        table.getCellEditor(i, colIndex).stopCellEditing();
                    }

                    if (spreadsheetModel.getValueAt(i, colIndex) != null && spreadsheetModel.getValueAt(i, colIndex).toString()
                            .equals(prevTerm)) {
                        spreadsheetModel.setValueAt(newTerm, i, colIndex);
                    }
                }
            }
        }
    }


    public void performMultipleSort(int primaryColumn,
                                    int secondaryColumn,
                                    boolean primaryAscending,
                                    boolean secondaryAscending) {
        SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(Utils.getArrayOfVals(0, table.getRowCount() - 1), Utils.convertSelectedColumnsToModelIndices(table, Utils.getArrayOfVals(1, table.getColumnCount() - 1)));
        spreadsheetHistory.add(affectedRange);
        setRowsToDefaultColor();
        spreadsheetModel.sort(affectedRange, table.columnViewIndextoModel(primaryColumn), table.columnViewIndextoModel(secondaryColumn), primaryAscending, secondaryAscending);
    }

    /**
     * Substitutes headers - replaces a column name with another one
     *
     * @param prevHeaderName - Previous column header
     * @param newHeaderName  - New column header
     */
    public void substituteHeaderNames(String prevHeaderName, String newHeaderName) {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        while (columns.hasMoreElements()) {
            TableColumn col = columns.nextElement();

            if (col.getHeaderValue().toString().equals(prevHeaderName)) {
                col.setHeaderValue(newHeaderName);
            }
        }

        table.addNotify();
    }


    public void columnAdded(TableColumnModelEvent event) {
        um.discardAllEdits();
    }

    public void columnMarginChanged(ChangeEvent event) {

    }

    public void columnMoved(TableColumnModelEvent event) {
        if (event.getFromIndex() != event.getToIndex()) {
            um.discardAllEdits();
        }
    }

    public void columnRemoved(TableColumnModelEvent event) {
        um.discardAllEdits();
    }

    public void columnSelectionChanged(ListSelectionEvent event) {

    }

    /**
     * If the users cell selection changes, reflect the changes in the buttons which are used for the addition of columns
     * to the table. for example, the user can only add a parameter column when they're focused on a protocol column.
     *
     * @param event - ListSelectionEvent.
     */
    public void valueChanged(ListSelectionEvent event) {

        int columnSelected = table.getSelectedColumn();
        int rowSelected = table.getSelectedRow();

        if (columnSelected == -1) {
            deleteColumn.setEnabled(false);
            copyColDown.setEnabled(false);
            deleteRow.setEnabled(false);
            copyRowDown.setEnabled(false);
            addCharacteristic.setEnabled(false);
            addParameter.setEnabled(false);
            addProtocol.setEnabled(false);
        } else {
            deleteColumn.setEnabled(true);
            copyColDown.setEnabled(true);
            deleteRow.setEnabled(true);
            copyRowDown.setEnabled(true);
            addCharacteristic.setEnabled(true);
            addProtocol.setEnabled(true);
            addFactor.setEnabled(true);

            String colName = table.getColumnName(columnSelected);

            if (colName.equalsIgnoreCase("protocol ref")) {
                addCharacteristic.setEnabled(false);
                addFactor.setEnabled(false);
                addParameter.setEnabled(true);
            } else {
                addParameter.setEnabled(false);
            }

            if (colName.contains("Characteristic") ||
                    colName.contains("Factor") ||
                    colName.equals("Unit")) {
                addCharacteristic.setEnabled(false);
                addFactor.setEnabled(false);
                addProtocol.setEnabled(false);
            }
        }

        if ((rowSelected != -1) && (columnSelected != -1) && table.getValueAt(rowSelected, columnSelected) != null) {
            String s = table.getValueAt(rowSelected, columnSelected)
                    .toString();

            OntologyObject ooForSelectedTerm = searchUserHistory(s);

            if (ooForSelectedTerm != null) {
                // update status panel in bottom left hand corner of workspace to contain the ontology
                // information. this should possibly be extended to visualize the ontology location within
                // the ontology tree itself.
                dataEntryEnv.setStatusPaneInfo("<html>" +
                        "<b>ontology term information</b>" + "</hr>" +
                        "<p><term name: >" + ooForSelectedTerm.getTerm() +
                        "</p>" + "<p><b>source ref: </b> " +
                        ooForSelectedTerm.getTermSourceRef() + "</p>" +
                        "<p><b>accession no: </b>" +
                        ooForSelectedTerm.getTermAccession() + "</p>" +
                        "</html>");
            }
        }
    }

    /**
     * HeaderListener source partially from http://www.java2s.com/Code/Java/Swing-Components/SortableTableExample.htm, last accessed 09-08-2008
     * Class listens for user interaction with the header. if there's a double click event on a column in the header,
     * this column will be sorted.
     */
    class HeaderListener extends MouseAdapter {
        JTableHeader header;
        SpreadsheetColumnRenderer renderer;

        HeaderListener(JTableHeader header, SpreadsheetColumnRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
        }

        public void mousePressed(MouseEvent e) {

            int col = header.columnAtPoint(e.getPoint());

            if (SwingUtilities.isRightMouseButton(e)) {
                String columnName = table.getColumnModel()
                        .getColumn(table.columnAtPoint(
                                e.getPoint())).getHeaderValue().toString();
                popupMenu(header, e.getX(), e.getY(), columnName);
            } else {
                if (e.getClickCount() == 2) {

                    int sortCol = header.getTable()
                            .convertColumnIndexToModel(col);


                    renderer.setSelectedColumn(col);
                    header.repaint();

                    if (header.getTable().isEditing()) {
                        header.getTable().getCellEditor().stopCellEditing();
                    }

                    boolean isAscent;
                    isAscent = SpreadsheetColumnRenderer.DOWN == renderer.getState(col);

                    // check conversion tool to make sure it's spitting out the right values. -1 IS BEING RETURNED AS A CONVERTED INDEX FOR COL 20 IN GRIFFIN EXAMPLE!!!
                    log.info("starting sort of " + sortCol);
                    SpreadsheetCellRange affectedRange = new SpreadsheetCellRange(Utils.getArrayOfVals(0, table.getRowCount() - 1), Utils.convertSelectedColumnsToModelIndices(table, Utils.getArrayOfVals(1, table.getColumnCount() - 1)));
                    spreadsheetHistory.add(affectedRange);
                    setRowsToDefaultColor();

                    ((SpreadsheetModel) header.getTable().getModel()).sort(affectedRange, sortCol, sortCol,
                            isAscent, false);

                } else {
                    table.setColumnSelectionInterval(col, col);
                    table.setRowSelectionInterval(0, table.getRowCount() - 1);
                }
            }
        }

    }

}