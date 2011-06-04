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
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.effects.AniSheetableJFrame;
import org.isatools.isacreator.filechooser.FileSelectCellEditor;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.Factor;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;
import org.isatools.isacreator.ontologyselectiontool.OntologySourceManager;
import org.isatools.isacreator.spreadsheet.transposedview.SpreadsheetConverter;
import org.isatools.isacreator.spreadsheet.transposedview.TransposedSpreadsheetModel;
import org.isatools.isacreator.spreadsheet.transposedview.TransposedSpreadsheetView;
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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

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
    protected static DateCellEditor dateEditor;

    public static final int SWITCH_ABSOLUTE = 0;
    public static final int SWITCH_RELATIVE = 1;
    protected static final int DEFAULT_STATE = 2;
    protected static final int DELETING_COLUMN = 3;
    protected static final int DELETING_ROW = 4;
    protected static final int INITIAL_ROWS = 50;


    static {
        dateEditor = new DateCellEditor();
        fileSelectEditor = new FileSelectCellEditor();
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("spreadsheet-package.style").load(
                Spreadsheet.class.getResource("/dependency-injections/spreadsheet-package.properties"));
    }

    @InjectedResource
    protected ImageIcon addRowButton, addRowButtonOver, deleteRowButton, deleteRowButtonOver, deleteColumnButton, deleteColumnButtonOver,
            multipleSortButton, multipleSortButtonOver, copyColDownButton, copyColDownButtonOver, copyRowDownButton,
            copyRowDownButtonOver, addProtocolButton, addProtocolButtonOver, addFactorButton, addFactorButtonOver,
            addCharacteristicButton, addCharacteristicButtonOver, addParameterButton, addParameterButtonOver, undoButton,
            undoButtonOver, redoButton, redoButtonOver, requiredColumnWarningIcon, confirmRemoveColumnIcon,
            confirmRemoveRowIcon, selectOneColumnWarningIcon, copyColumnDownWarningIcon, copyRowDownWarningIcon, transposeIcon, transposeIconOver;

    //map provides a way of tracking where unit fields belong in the table, so even columns are moved around by the user,
    // they are moved by the user, the software still knows where they belong when it comes to outputting the ISATAB files!!
    protected Map<TableColumn, List<TableColumn>> columnDependencies;
    private JLabel addCharacteristic, addFactor, addParameter, addProtocol, addRow, copyColDown, copyRowDown, deleteColumn,
            deleteRow, multipleSort, undo, redo, transpose;

    protected JOptionPane optionPane;
    private CustomTable table;


    private TableGroupInfo tableGroupInformation;
    protected SpreadsheetColumnRenderer renderer = new SpreadsheetColumnRenderer();
    protected SpreadsheetModel spreadsheetModel;
    private StudyDataEntry studyDataEntryEnvironment;
    private AssaySpreadsheet assayDataEntryEnvironment;
    private TableReferenceObject tableReferenceObject;
    protected Vector<String> columns;
    protected Vector<Object> rows;
    protected int[] rowsToDelete;
    protected int curColDelete = -1;
    protected int currentState = DEFAULT_STATE;
    protected int previouslyAddedCharacteristicPosition = -1;
    protected int startCol = -1;
    protected int startRow = -1;
    private Map<String, String> absRelFileMappings;
    protected Set<String> hiddenColumns;
    private String spreadsheetTitle;
    protected boolean highlightActive = false;
    private TableConsistencyChecker tableConsistencyChecker;

    private SpreadsheetPopupMenus spreadsheetPopups;

    protected SpreadsheetFunctions spreadsheetFunctions;

    // Objects required for the undo function to work.
    protected Clipboard system = Toolkit.getDefaultToolkit().getSystemClipboard();
    protected SpreadsheetHistory spreadsheetHistory = new SpreadsheetHistory();

    protected UndoManager undoManager = new UndoManager() {
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
     * @param tableReferenceObject      - Reference Object to build the table with.
     * @param studyDataEntryEnvironment - StudyDataEntry. Used to retrieve factors and protocols which have been entered.
     * @param spreadsheetTitle          - name to display on the spreadsheet...
     * @param assayDataEntryEnvironment - The assay data entry object :o)
     */
    public Spreadsheet(final TableReferenceObject tableReferenceObject, StudyDataEntry studyDataEntryEnvironment, String spreadsheetTitle, AssaySpreadsheet assayDataEntryEnvironment) {
        ResourceInjector.get("spreadsheet-package.style").inject(this);

        this.studyDataEntryEnvironment = studyDataEntryEnvironment;
        this.assayDataEntryEnvironment = assayDataEntryEnvironment;

        this.spreadsheetTitle = spreadsheetTitle;
        this.tableReferenceObject = tableReferenceObject;

        spreadsheetPopups = new SpreadsheetPopupMenus(this);
        spreadsheetFunctions = new SpreadsheetFunctions(this);

        columnDependencies = new HashMap<TableColumn, List<TableColumn>>();
        Collections.synchronizedMap(columnDependencies);
        hiddenColumns = new HashSet<String>();

        setLayout(new BorderLayout());

        instantiateSpreadsheet();
    }

    public void instantiateSpreadsheet() {
        // create a spreadsheet model which overrides two methods that allow the reference model for the spreadsheet to
        // control which columns can be deleted, and which cannot.
        spreadsheetModel = new SpreadsheetModel(tableReferenceObject) {
            //@overrides
            public Class getColumnClass(int colNo) {
                String colName = getColumnName(colNo);

                Class columnClass = tableReferenceObject.getColumnType(colName).getMapping();

                if (columnClass == DataTypes.DATE.getMapping()) {
                    columnClass = DataTypes.STRING.getMapping();
                }

                return columnClass;
            }

            //overrides
            public boolean isCellEditable(int row, int col) {
                String colName = getColumnName(col);
                //consult reference model to ascertain whether or not the column is editable
                return tableReferenceObject.getColumnEditable(colName);
            }

            public void setValueAt(Object value, int row, int col) {
                super.setValueAt(value, row, col);
            }
        };

        spreadsheetHistory.setTableModel(spreadsheetModel);
        spreadsheetModel.setHistory(spreadsheetHistory);


        rows = new Vector<Object>();

        if (tableReferenceObject.getPreDefinedHeaders() != null) {
            columns = tableReferenceObject.getPreDefinedHeaders();
        } else {
            columns = tableReferenceObject.getHeaders();
        }

        spreadsheetModel.setDataVector(rows, columns);

        // setup the JTable
        setupTable();

        spreadsheetModel.setTable(table);

        if (tableReferenceObject.getData() != null) {
            populateTable(tableReferenceObject.getData());
            rebuildDependencies(tableReferenceObject.getColumnDependencies());
        } else {
            // populate table with some empty fields.
            spreadsheetFunctions.addRows(INITIAL_ROWS, true);

            List<Protocol> protocols = tableReferenceObject.constructProtocolObjects();
            if (protocols.size() > 0) {
                for (Protocol p : protocols) {
                    studyDataEntryEnvironment.getStudy().addProtocol(p);
                }
                studyDataEntryEnvironment.reformProtocols();
            }

            List<Factor> factors = tableReferenceObject.constructFactorObjects();

            if (factors.size() > 0) {
                for (Factor f : factors) {
                    studyDataEntryEnvironment.getStudy().addFactor(f);
                }
                studyDataEntryEnvironment.reformFactors();
            }
        }


        if (tableReferenceObject.getDefinedOntologies().size() > 0) {
            for (OntologyObject oo : tableReferenceObject.getDefinedOntologies().values()) {
                OntologySourceManager.getUserOntologyHistory().put(oo.getUniqueId(), oo);
            }
        }


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
        addUndoableEditListener(undoManager);
    }

    public SpreadsheetFunctions getSpreadsheetFunctions() {
        return spreadsheetFunctions;
    }

    public String getAssignedUnitForColumn(int columnIndex, int rowNo) {

        int[] convertedColumnIndex = Utils.convertSelectedColumnsToModelIndices(table, new int[]{columnIndex});

        Set<Integer> dependentColumns = tableReferenceObject.getColumnDependencies().get(convertedColumnIndex[0]);

        String value = "";
        if (dependentColumns != null) {
            for (int column : dependentColumns) {
                value += getTableModel().getValueAt(rowNo, column);
            }
        }


        return value;
    }

    public String getSpreadsheetTitle() {
        return spreadsheetTitle;
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
            if (tableReferenceObject.isRequired(tc.getHeaderValue().toString())) {
                for (int row = 0; row < spreadsheetModel.getRowCount(); row++) {


                    Object cellObj = table.getValueAt(row, columnViewIndex);

                    String value = (cellObj == null) ? "" : cellObj.toString().trim();

                    if (value.equals("")) {
                        // a required value has not been filled! therefore report the index of the row and column as well as the calling]
                        // location and message!
                        archiveOutputErrors.add(new ArchiveOutputError("Data missing for " + tc.getHeaderValue().toString() + " at record " + row, assayDataEntryEnvironment, tc.getHeaderValue().toString(), row, columnViewIndex));
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

            if (tableReferenceObject.acceptsFileLocations(tc.getHeaderValue().toString())) {
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
                        spreadsheetFunctions.deleteRow(table.getSelectedRow());
                    } else {
                        spreadsheetFunctions.deleteRow(table.getSelectedRows());
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
                    spreadsheetFunctions.deleteColumn(table.getSelectedColumn());
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
                                studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().hideSheet();
                                if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                    spreadsheetFunctions.copyColumnDownwards(row, col);
                                }
                            }
                        }
                    });
                    studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame()
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
                            studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().hideSheet();
                            if (lastOptionAnswer == JOptionPane.YES_OPTION) {
                                spreadsheetFunctions.copyRowDownwards(row);
                            }
                        }
                    }
                });
                studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame()
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
                if (addProtocol.isEnabled()) {
                    FieldObject fo = new FieldObject(table.getColumnCount(),
                            "Protocol REF", "Protocol used for experiment", DataTypes.LIST, "",
                            false, false, false);

                    fo.setFieldList(studyDataEntryEnvironment.getProtocolNames());

                    spreadsheetFunctions.addFieldToReferenceObject(fo);

                    spreadsheetFunctions.addColumnAfterPosition("Protocol REF", null, -1);
                }
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
                if (addFactor.isEnabled()) {
                    showAddColumnsGUI(AddColumnGUI.ADD_FACTOR_COLUMN);
                }
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
                if (addCharacteristic.isEnabled()) {
                    showAddColumnsGUI(AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
                }
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
                if (addParameter.isEnabled()) {
                    showAddColumnsGUI(AddColumnGUI.ADD_PARAMETER_COLUMN);
                }
            }
        });

        undo = new JLabel(undoButton);
        undo.setToolTipText("<html><b>undo previous action<b></html>");
        undo.setEnabled(undoManager.canUndo());
        undo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                undo.setIcon(undoButton);
                undoManager.undo();

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
        redo.setEnabled(undoManager.canRedo());
        redo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                redo.setIcon(redoButton);
                undoManager.redo();

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

        JLabel lab = UIHelper.createLabel(spreadsheetTitle, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, JLabel.RIGHT);
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
     * Reorders the columns defined in the table to ensure that parameters occur the protocol ref they were added with and
     *
     * @param fileName - the name of the file being checked!
     * @return whether or not the table is consistent
     */
    protected boolean checkTableColumnOrderBad(String fileName) {
        tableConsistencyChecker = new TableConsistencyChecker();
        // return true if ok, false if not
        return tableConsistencyChecker.runInspection(fileName, table, columnDependencies);
    }

    public TableConsistencyChecker getTableConsistencyChecker() {
        return tableConsistencyChecker;
    }


    /**
     * Return the current DataEntryEnvironment
     *
     * @return DataEntryPanel
     */
    public DataEntryEnvironment getDataEntryEnv() {
        return studyDataEntryEnvironment.getDataEntryEnvironment();
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
    public Set<String> getOntologiesDefinedInTable() {
        Enumeration<TableColumn> columns = table.getColumnModel().getColumns();

        HashSet<String> ontologySources = new HashSet<String>();

        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();

            if (tableReferenceObject.getClassType(tc.getHeaderValue().toString().trim())
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
        return studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame();
    }

    /**
     * Get the StudyDataEntry object the table is part of.
     *
     * @return the StudyDataEntry object for the current Spreadsheet
     */
    public StudyDataEntry getStudyDataEntryEnvironment
    () {
        return studyDataEntryEnvironment;
    }

    /**
     * Return this JTable
     *
     * @return the JTable component
     */
    public CustomTable getTable() {
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
        return tableReferenceObject;
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
                spreadsheetPopups.popupMenu(table, event.getX() + 10, event.getY() + 10, columnName);
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
            spreadsheetPopups.dragCellPopupMenu(table, event.getX(), event.getY());
        }
    }

    /**
     * Populate the table given a list of values which are to be entered.
     *
     * @param data - data to be entered.
     */
    public void populateTable
    (List<List<String>> data) {
        spreadsheetFunctions.addRows(data.size(), false);

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


    protected void highlight(String toGroupBy, boolean exactMatch, boolean returnSampleNames) {
        if (tableGroupInformation != null && tableGroupInformation.isShowing()) {
            tableGroupInformation.dispose();
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        Map<String, List<Object>> groups = getDataGroupsByColumn(toGroupBy, exactMatch, returnSampleNames);

        Map<String, Color> groupColors = new ListOrderedMap<String, Color>();

        for (String s : groups.keySet()) {
            groupColors.put(s, UIHelper.createColorFromString(s, true));
        }
        // then pass the groups and the colours to the TableGroupInfo class to display the gui
        // showing group distribution!
        final Map<Integer, Color> rowColors = paintRows(groups, groupColors);

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        tableGroupInformation = new TableGroupInfo(groups, groupColors, table.getRowCount());
        tableGroupInformation.setLocation(getWidth() / 2 - tableGroupInformation.getWidth(), getHeight() / 2 - tableGroupInformation.getHeight());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                try {
                    table.setDefaultRenderer(Class.forName("java.lang.Object"), new CustomRowRenderer(rowColors, UIHelper.VER_11_PLAIN));
                    table.repaint();
                    tableGroupInformation.createGUI();
                    highlightActive = true;
                } catch (ClassNotFoundException e) {
                    //
                }
            }
        });
    }


    protected Map<String, Set<String>> getColumnGroups() {

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
        if (tableGroupInformation != null && tableGroupInformation.isShowing()) {
            tableGroupInformation.dispose();
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
            studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().hideSheet();

            if ((currentState == DELETING_COLUMN) &&
                    (lastOptionAnswer == JOptionPane.YES_OPTION)) {

                spreadsheetFunctions.removeColumn();
                curColDelete = -1;
                currentState = DEFAULT_STATE;
            }

            if ((currentState == DELETING_ROW) &&
                    (lastOptionAnswer == JOptionPane.YES_OPTION)) {
                spreadsheetFunctions.removeRows();
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
        log.info("Rebuilding dependencies for: " + spreadsheetTitle);
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


    /**
     * Searches UserHistory for a unique ontology id (source:term pair)
     *
     * @param uniqueId- the ID being searched for in the previous user history.
     * @return - OntologyObject matching the unique id if found, null otherwise.
     */
    private OntologyObject searchUserHistory(String uniqueId) {
        return OntologySourceManager.getUserOntologyHistory().get(uniqueId);
    }

    /**
     * Add a listener to be notified when the selected range changes
     *
     * @param spreadsheetSelectionListener The listener to add
     */
    public void addSelectionListener(SpreadsheetSelectionListener spreadsheetSelectionListener) {
        listenerList.add(SpreadsheetSelectionListener.class, spreadsheetSelectionListener);
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
                                    .setToolTipText(tableReferenceObject.getFieldByName(
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

        for (int i = 0; i < tableReferenceObject.getHeaders().size(); i++) {
            if (!model.getColumn(i).getHeaderValue().toString().equals(TableReferenceObject.ROW_NO_TEXT)) {
                model.getColumn(i).setHeaderRenderer(renderer);
                model.getColumn(i)
                        .setPreferredWidth(spreadsheetFunctions.calcColWidths(
                                model.getColumn(i).getHeaderValue().toString()));
                // add appropriate cell editor for cell.
                spreadsheetFunctions.addCellEditor(model.getColumn(i));
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
    protected void showAddColumnsGUI(final int toShow) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                AddColumnGUI goingToDisplay;

                switch (toShow) {
                    case AddColumnGUI.ADD_FACTOR_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_FACTOR_COLUMN);
                        break;

                    case AddColumnGUI.ADD_CHARACTERISTIC_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_CHARACTERISTIC_COLUMN);
                        break;

                    case AddColumnGUI.ADD_PARAMETER_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_PARAMETER_COLUMN);

                        break;

                    case AddColumnGUI.ADD_COMMENT_COLUMN:
                        goingToDisplay = new AddColumnGUI(Spreadsheet.this,
                                AddColumnGUI.ADD_COMMENT_COLUMN);

                        break;

                    default:
                        goingToDisplay = null;
                }

                if (goingToDisplay != null) {
                    goingToDisplay.createGUI();
                    // do this to ensure that the gui is fully created before displaying it.
                    studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().showJDialogAsSheet(goingToDisplay);
                }
            }
        });
    }

    /**
     * Displays an error message when a user tries to delete more than one column at a time.
     */
    protected void showColumnErrorMessage() {
        if (!(table.getSelectedColumns().length > 1)) {
            spreadsheetFunctions.deleteColumn(table.getSelectedColumn());
        } else {

            optionPane = new JOptionPane("<html>Multiple column select detected!<p>Please select only one column!</p></html>", JOptionPane.OK_OPTION);
            optionPane.setIcon(selectOneColumnWarningIcon);
            UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);
            optionPane.addPropertyChangeListener(this);
            studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame()
                    .showJDialogAsSheet(optionPane.createDialog(this, "Delete Column"));
        }
    }

    /**
     * Displays the MultipleSortGUI
     */
    protected void showMultipleColumnSortGUI
    () {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MultipleSortGUI msGUI = new MultipleSortGUI(Spreadsheet.this);
                msGUI.createGUI();
                msGUI.updateAllCombos();

                studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().showJDialogAsSheet(msGUI);
            }
        });
    }


    /**
     * Displays the Transposed Spreadsheet UI
     */
    protected void showTransposeSpreadsheetGUI() {
        // todo migrate this to occur in the view so that a loading pane is shown whilst the spreadsheet is loaded.

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SpreadsheetConverter converter = new SpreadsheetConverter(Spreadsheet.this);
                TransposedSpreadsheetModel transposedSpreadsheetModel = converter.doConversion();
                TransposedSpreadsheetView transposedSpreadsheetView = new TransposedSpreadsheetView(transposedSpreadsheetModel, (int) (studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().getWidth() * 0.80), (int) (studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().getHeight() * 0.70));
                transposedSpreadsheetView.createGUI();
                studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().showJDialogAsSheet(transposedSpreadsheetView);
                studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().maskOutMouseEvents();
            }
        });
    }

    /**
     * Displays the AddMultipleRowsGUI
     */
    protected void showMultipleRowsGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                AddMultipleRowsGUI amrGUI = new AddMultipleRowsGUI(Spreadsheet.this);
                amrGUI.createGUI();

                studyDataEntryEnvironment.getDataEntryEnvironment().getParentFrame().showJDialogAsSheet(amrGUI);
            }
        });
    }


    public void columnAdded(TableColumnModelEvent event) {


        undoManager.discardAllEdits();
    }

    public void columnMarginChanged(ChangeEvent event) {

    }

    public void columnMoved(TableColumnModelEvent event) {
        if (event.getFromIndex() != event.getToIndex()) {
            undoManager.discardAllEdits();
        }
    }

    public void columnRemoved(TableColumnModelEvent event) {
        undoManager.discardAllEdits();
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
                studyDataEntryEnvironment.getDataEntryEnvironment().setStatusPaneInfo("<html>" +
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
                spreadsheetPopups.popupMenu(header, e.getX(), e.getY(), columnName);
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