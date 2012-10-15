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

package org.isatools.isacreator.factorlevelentry;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.CustomSpreadsheetCellRenderer;
import org.isatools.isacreator.common.CustomTableHeaderRenderer;
import org.isatools.isacreator.common.ExcelAdaptor;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;
import org.isatools.isacreator.spreadsheet.SpreadsheetCellPoint;
import org.isatools.isacreator.spreadsheet.SpreadsheetModel;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Vector;

public class FactorLevelEntryGUI extends JFrame {
    private static Logger log = Logger.getLogger(FactorLevelEntryGUI.class);
    private static final String LEVEL_TEXT = "Level";
    private static final String UNIT_TEXT = "Unit";

    private JCheckBox useUnit;
    private FactorLevelTable dataEntryTable;
    private DefaultTableModel model;
    private static TableColumn hiddenColumn = null;
    private static OntologyCellEditor levelOCE;
    private static OntologyCellEditor unitOCE;
    private Vector<String> columns;
    private Vector<Object> rows;

    private JLabel status;
    private ExcelAdaptor ea;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("factorlevelentry-package.style").load(
                FactorLevelEntryGUI.class.getResource("/dependency-injections/factorlevelentry-package.properties"));
    }

    @InjectedResource
    private ImageIcon headerImage, addRowIcon, addRowOverIcon, removeRowIcon, removeRowOverIcon;

    public FactorLevelEntryGUI() {

        ResourceInjector.get("factorlevelentry-package.style").inject(this);

        setTitle("Factor Level Entry");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(UIHelper.BG_COLOR);

        setPreferredSize(new Dimension(300, 350));
        setUndecorated(true);

        unitOCE = new OntologyCellEditor(false, false, null);
        levelOCE = new OntologyCellEditor(false, false, null);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });

        pack();
    }

    public void createGUI() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCentralPanel());
        add(createButtonPanel(), BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));
    }

    public void makeVisible() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
                dataEntryTable.addNotify();
                repaint();
                dataEntryTable.requestFocusInWindow();
            }
        });

    }


    private JPanel createTopPanel() {
        JPanel imagePanel = new JPanel(new GridLayout(1, 1));

        JLabel image = new JLabel(headerImage, JLabel.RIGHT);
        image.setOpaque(false);

        imagePanel.add(image);

        return imagePanel;
    }

    private void setupInitialRows(int num) {
        for (int i = 0; i < num; i++) {
            addRow();
        }
        dataEntryTable.repaint();
    }

    private void createPopup(JComponent parent, int xPos, int yPos) {
        JPopupMenu popup = new JPopupMenu();
        popup.setLightWeightPopupEnabled(false);
        parent.add(popup);

        JMenuItem copy = new JMenuItem("copy");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                ea.copy();
            }
        });

        JMenuItem paste = new JMenuItem("paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                try {
                    SpreadsheetCellPoint pasteFragmentSize = SpreadsheetModel.getSize((String) (ea.getSystem().getContents(this)
                            .getTransferData(DataFlavor.stringFlavor)), '\t');

                    int curRow = dataEntryTable.getSelectedRow();

                    if (curRow + pasteFragmentSize.getRow() > dataEntryTable.getRowCount()) {
                        int numToFill = (curRow + pasteFragmentSize.getRow()) - dataEntryTable.getRowCount();
                        setupInitialRows(numToFill);
                    }
                    ea.paste();
                } catch (UnsupportedFlavorException e) {
                    log.info("ISAcreator didn't like what you just pasted");
                } catch (IOException e) {
                    log.info("ISAcreator couldn't get any input from the System clipboard");
                }

            }
        });

        JMenuItem copyDownwards = new JMenuItem("copy value downwards");
        copyDownwards.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                Object obj = dataEntryTable.getValueAt(dataEntryTable.getSelectedRow(), dataEntryTable.getSelectedColumn());
                String val = obj == null ? "" : obj.toString();

                for (int row = dataEntryTable.getSelectedRow(); row < dataEntryTable.getRowCount(); row++) {
                    dataEntryTable.setValueAt(val, row, dataEntryTable.getSelectedColumn());
                }
            }
        });

        popup.add(copy);
        popup.add(paste);
        popup.add(new JSeparator());
        popup.add(copyDownwards);

        popup.show(parent, xPos, yPos);
    }

    private void setupColumns() {
        columns = new Vector<String>();
        columns.add(LEVEL_TEXT);
        columns.add(UNIT_TEXT);
    }

    private void setupRows() {
        rows = new Vector<Object>();
    }

    private JPanel createCentralPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));

        setupColumns();

        setupRows();

        model = new DefaultTableModel(rows, columns);

        dataEntryTable = new FactorLevelTable(model);
        dataEntryTable.getTableHeader().setReorderingAllowed(false);

        dataEntryTable.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    createPopup(dataEntryTable, event.getX(), event.getY());
                }
            }

        });

        ea = new ExcelAdaptor(dataEntryTable, false);

        try {
            dataEntryTable.setDefaultRenderer(Class.forName("java.lang.Object"), new CustomSpreadsheetCellRenderer());
        } catch (ClassNotFoundException e) {
            log.info("");
        }


        JTableHeader header = dataEntryTable.getTableHeader();
        header.setBackground(UIHelper.BG_COLOR);
        header.setFont(UIHelper.VER_12_BOLD);
        header.setForeground(UIHelper.DARK_GREEN_COLOR);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));

        JScrollPane tableScroller = new JScrollPane(dataEntryTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tableScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
        tableScroller.setBackground(UIHelper.BG_COLOR);
        tableScroller.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(tableScroller);

        tableScroller.setPreferredSize(new Dimension(350, 250));


        container.add(Box.createVerticalStrut(10));
        container.add(createControlPanel());
        container.add(Box.createVerticalStrut(10));
        container.add(tableScroller);

        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setOpaque(false);

        status = UIHelper.createLabel("", UIHelper.VER_11_BOLD, UIHelper.RED_COLOR);

        statusPanel.add(status);

        container.add(statusPanel);

        status.setVisible(false);
        container.add(Box.createGlue());

        showHideColumn(UNIT_TEXT, false);
        showHideColumn(UNIT_TEXT, true);
        updateCellEditors();
        Enumeration<TableColumn> columns = dataEntryTable.getColumnModel().getColumns();
        while (columns.hasMoreElements()) {
            TableColumn tc = columns.nextElement();
            tc.setHeaderRenderer(new CustomTableHeaderRenderer());
        }

        return container;


    }

    private void showHideColumn(String columnName, boolean hide) {
        Enumeration<TableColumn> columns = dataEntryTable.getColumnModel().getColumns();

        if (hide) {
            while (columns.hasMoreElements()) {
                TableColumn tc = columns.nextElement();
                if (tc.getHeaderValue().equals(columnName)) {
                    hiddenColumn = tc;
                    dataEntryTable.removeColumn(hiddenColumn);

                }
            }
        } else {
            if (hiddenColumn != null) {
                dataEntryTable.addColumn(hiddenColumn);
                hiddenColumn.setCellEditor(unitOCE);
            }
        }
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.LINE_AXIS));

        useUnit = new JCheckBox("use unit?", true);
        UIHelper.renderComponent(useUnit, UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        useUnit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                if (!useUnit.isSelected()) {
                    showHideColumn(UNIT_TEXT, true);
                } else {
                    showHideColumn(UNIT_TEXT, false);
                }

                updateCellEditors();
            }
        });

        controlPanel.add(useUnit);

        controlPanel.add(Box.createHorizontalStrut(70));

        final JLabel addrow = new JLabel(addRowIcon, JLabel.RIGHT);
        addrow.setOpaque(false);
        addrow.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                addRow();
            }

            public void mouseEntered(MouseEvent event) {
                addrow.setIcon(addRowOverIcon);
            }

            public void mouseExited(MouseEvent event) {
                addrow.setIcon(addRowIcon);
            }
        });

        final JLabel removerow = new JLabel(removeRowIcon, JLabel.LEFT);
        removerow.setOpaque(false);
        removerow.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (dataEntryTable.getSelectedRow() != -1) {
                    int[] selectedRows;
                    if ((selectedRows = dataEntryTable.getSelectedRows()).length > 1) {
                        // remove from back to front!
                        for (int row = selectedRows.length - 1; row >= 0; row--) {
                            model.removeRow(selectedRows[row]);
                        }
                    } else {
                        model.removeRow(dataEntryTable.getSelectedRow());
                    }
                }
            }

            public void mouseEntered(MouseEvent event) {
                removerow.setIcon(removeRowOverIcon);
            }

            public void mouseExited(MouseEvent event) {
                removerow.setIcon(removeRowIcon);
            }
        });

        controlPanel.add(addrow);
        controlPanel.add(Box.createHorizontalStrut(4));
        controlPanel.add(removerow);
        controlPanel.add(Box.createGlue());

        return controlPanel;
    }

    private String getLevels() {
        StringBuffer levels = new StringBuffer();
        for (int row = 0; row < dataEntryTable.getRowCount(); row++) {
            String rowVal = dataEntryTable.getValueAt(row, 0).toString();
            if (rowVal != null && !rowVal.equals("")) {
                if (row != 0) {
                    levels.append(";").append(rowVal);
                } else {
                    levels.append(rowVal);
                }
            }
        }

        return levels.toString();
    }

    private String getUnits() {
        if (useUnit.isSelected()) {
            StringBuffer units = new StringBuffer();
            for (int row = 0; row < dataEntryTable.getRowCount(); row++) {
                String rowVal = dataEntryTable.getValueAt(row, 1).toString();
                if (rowVal != null && !rowVal.equals("")) {
                    if (row != 0) {
                        units.append(";").append(rowVal);
                    } else {
                        units.append(rowVal);
                    }
                }
            }
            return units.toString();
        }

        return "";
    }

    private boolean checkLevelsAndUnits() {
        if (useUnit.isSelected()) {
            int factorLevels = getLevels().split(";").length;
            int units = getUnits().split(";").length;

            return factorLevels == units;
        } else {
            // don't care otherwise since there are no units!
            return true;
        }
    }

    private void updateCellEditors() {

        if (!useUnit.isSelected()) {
            dataEntryTable.getColumnModel().getColumn(0).setCellEditor(levelOCE);
        } else {
            JTextField defaultEntry = new JTextField();
            defaultEntry.setBorder(null);
            dataEntryTable.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(defaultEntry));
        }
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));

        final JLabel ok = new JLabel(UIHelper.OK_BUTTON, JLabel.RIGHT);
        ok.setOpaque(false);
        ok.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                if (checkLevelsAndUnits()) {
                    firePropertyChange("changedFactorLevels", "OLD_VALUE", new LevelsAndUnits(getLevels(), getUnits()));
                    rows.clear();
                    status.setVisible(false);
                    setVisible(false);
                } else {
                    status.setText("<html><strong>error:</strong> missing units!</html>");
                    status.setVisible(true);
                }
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
                ok.setIcon(UIHelper.OK_BUTTON_OVER);
            }

            public void mouseExited(MouseEvent event) {
                ok.setIcon(UIHelper.OK_BUTTON);
            }
        });

        final JLabel cancel = new JLabel(UIHelper.CLOSE_BUTTON, JLabel.LEFT);
        cancel.setOpaque(false);
        cancel.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                firePropertyChange("noChange", "canceled", "");
                status.setVisible(false);
                rows.clear();
                setVisible(false);
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
                cancel.setIcon(UIHelper.CLOSE_BUTTON_OVER);
            }

            public void mouseExited(MouseEvent event) {
                cancel.setIcon(UIHelper.CLOSE_BUTTON);
            }
        });

        buttonPanel.add(cancel);
        buttonPanel.add(ok);

        return buttonPanel;

    }

    public void setFactorLevels(String levels, String units) {
        String[] levelsAsArray;
        levels = levels.trim();
        units = units.trim();
        if (!levels.equals("")) {
            if (levels.contains(";")) {
                levelsAsArray = levels.split(";");
                setupInitialRows(levelsAsArray.length);
            } else {
                setupInitialRows(1);
            }
            populateTable(levels, units);
        } else {
            setupInitialRows(10);
        }

        dataEntryTable.addNotify();

    }

    private void populateTable(String levels, String units) {
        if (units.equals("")) {
            useUnit.setSelected(false);
            showHideColumn(UNIT_TEXT, true);
            updateCellEditors();
        }

        String[] levelsToInclude = null;
        String[] unitsToInclude = null;

        if (!levels.equals("")) {
            if (levels.contains(";")) {
                // we have more than one level to deal with.
                levelsToInclude = levels.split(";");

                if (units.contains(";")) {
                    unitsToInclude = units.split(";");
                }

            } else {
                levelsToInclude = new String[1];
                levelsToInclude[0] = levels;

                if (!units.equals("")) {
                    unitsToInclude = new String[1];
                    if (units.contains(";")) {
                        unitsToInclude[0] = units.split(";")[0];
                    } else {
                        unitsToInclude[0] = units;
                    }

                }
            }
        }
        if (levelsToInclude != null) {
            for (int i = 0; i < levelsToInclude.length; i++) {
                dataEntryTable.getModel().setValueAt(levelsToInclude[i], i, 0);
            }
        }

        if (unitsToInclude != null) {
            for (int i = 0; i < unitsToInclude.length; i++) {
                if (i < unitsToInclude.length) {
                    dataEntryTable.getModel().setValueAt(unitsToInclude[i], i, 1);
                }
            }
        }
        updateCellEditors();
    }


    private Vector<String> createBlankElement() {
        Vector<String> t = new Vector<String>();
        int j = model.getColumnCount();

        for (int i = 0; i < j; i++) {
            t.add("");
        }

        return t;
    }

    /**
     * Add rows to the table
     */
    synchronized void addRow() {
        Vector<String> r = createBlankElement();
        rows.addElement(r);
        dataEntryTable.addNotify();

    }


    class FactorLevelTable extends JTable implements Serializable {

        public FactorLevelTable(DefaultTableModel dtm) {
            super(dtm);

        }


        public boolean editCellAt(int row, int col, EventObject e) {
            TableCellEditor editor = getCellEditor(row, col);

            if (editor instanceof OntologyCellEditor) {
                if (e instanceof MouseEvent && ((MouseEvent) e).getClickCount() == 2) {
                    super.editCellAt(row, col, e);
                }
            } else {
                super.editCellAt(row, col, e);
            }


            return false;
        }

    }

}
