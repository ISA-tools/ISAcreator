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

package org.isatools.isacreator.formatmappingutility.tablebrowser;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.CustomTableHeaderRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.SingleSelectionListCellRenderer;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Widget which allows for the browsing of an incoming file via a filterable column list and a sub-representation of the
 * file currently being imported.
 *
 * @author Eamonn Maguire
 */

public class IncomingFileBrowser extends JFrame {

    @InjectedResource
    private ImageIcon select, selectOver;

    @InjectedResource
    private Image columnSelectorHeader, columnSelectorHeaderInactive;

    private ColumnObject[] columns;
    private ExtendedJList filterableList;
    private String[] availableColumns;
    private String[][] initialData;

    private ColumnHighlighter ch;
    private SummaryTable st;


    public IncomingFileBrowser(String[] availableColumns, String[][] initialData) {
        this.availableColumns = availableColumns;
        this.initialData = initialData;
        this.columns = getColumnObjects(availableColumns);

        ResourceInjector.get("formatmappingutility-package.style").inject(this);

        setUndecorated(true);
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(600, 350));

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

    public void createGUI() {
        HUDTitleBar titlePanel = new HUDTitleBar(columnSelectorHeader,
                columnSelectorHeaderInactive, true);
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        JPanel filteredListContainer = new JPanel(new BorderLayout());
        filteredListContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "filter column", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR));

        filterableList = new ExtendedJList(new SingleSelectionListCellRenderer());

        ch = new ColumnHighlighter(400, 25);

        for (ColumnObject co : columns) {
            filterableList.addItem(co);
        }

        JScrollPane listScroller = new JScrollPane(filterableList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setPreferredSize(new Dimension(175, 250));
        listScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        listScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        filteredListContainer.add(listScroller);
        filteredListContainer.add(filterableList.getFilterField(), BorderLayout.NORTH);

        filteredListContainer.setPreferredSize(new Dimension(175, 270));

        add(filteredListContainer, BorderLayout.WEST);

        st = new SummaryTable(availableColumns);
        add(st, BorderLayout.CENTER);

        filterableList.addPropertyChangeListener("update", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                List<String> coHighlight = new ArrayList<String>();

                if (filterableList.getItems().size() == filterableList.getFilteredItems().size()) {
                    ch.setColumnsToHighlight(coHighlight);
                } else {
                    for (Object c : filterableList.getFilteredItems()) {
                        coHighlight.add(c.toString());
                    }
                    ch.setColumnsToHighlight(coHighlight);
                }
            }
        });

        filterableList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                ColumnObject column = (ColumnObject) propertyChangeEvent.getNewValue();
                st.scrollToColumnIndex(column.getColumnNumber());
            }
        });
        pack();
    }

    private void fireUpdateToListeners() {
        firePropertyChange("selectedColumn", "", st.getSelectedColumn());
    }

    public void showHideGUI(boolean show) {
        setVisible(show);
    }

    private ColumnObject[] getColumnObjects(String[] availableColumns) {
        ColumnObject[] toReturn = new ColumnObject[availableColumns.length];
        for (int i = 0; i < availableColumns.length; i++) {
            toReturn[i] = new ColumnObject(i, availableColumns[i]);
        }

        return toReturn;
    }

    class ColumnObject {
        private int columnNumber;
        private String columnName;

        public ColumnObject(int columnNumber, String columnName) {

            this.columnNumber = columnNumber;
            this.columnName = columnName;
        }

        public int getColumnNumber() {
            return columnNumber;
        }

        public String getColumnName() {
            return columnName;
        }

        @Override
        public String toString() {
            return columnName;
        }
    }

    /**
     * Provides a visual representation of which columns are being filtered on in the list
     */
    class ColumnHighlighter extends JPanel {

        List<String> columnsToHighlight;
        private int width;
        private int height;

        public ColumnHighlighter(int width, int height) {
            this.width = width;
            this.height = height;
            columnsToHighlight = new ArrayList<String>();
            setPreferredSize(new Dimension(width, height));
        }

        public void setColumnsToHighlight(List<String> columnsToHighlight) {
            this.columnsToHighlight = columnsToHighlight;
            repaint();
            revalidate();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int tileWidth = ((width - 10) / columns.length);
            int x = 1;
            int y = 5;

            for (ColumnObject co : columns) {

                if (columnsToHighlight.contains(co.getColumnName())) {
                    g2d.setColor(UIHelper.LIGHT_GREEN_COLOR);
                    g2d.fillRoundRect(x, y + 2, tileWidth, height - 7, 5, 3);

                } else {
                    g2d.setColor(new Color(151, 151, 151, 50));
                    // reduce size to 80% of highlighted colour
                    g2d.fillRoundRect(x, y + 4, tileWidth, height - 7, 3, 2);
                }
                x += tileWidth + 1;
            }
            graphics.dispose();
        }
    }

    class SummaryTable extends JPanel implements ListSelectionListener {

        private JTable table;
        private DefaultTableModel dtm;
        private String[] headers;
        private JScrollPane tableScroller;

        private JPanel infoPanel;
        private JLabel infoLabel;

        public SummaryTable(String[] headers) {
            this.headers = headers;
            setLayout(new BorderLayout());
            setBackground(UIHelper.BG_COLOR);
            createGUI();
        }

        public String getSelectedColumn() {
            int selectedColumn = table.getSelectedColumn();
            return selectedColumn == -1 ? null : table.getColumnName(selectedColumn);
        }

        private void createGUI() {

            dtm = new DefaultTableModel(initialData, headers) {
                @Override
                public boolean isCellEditable(int i, int i1) {
                    return false;
                }
            };

            table = new JTable(dtm);
            table.setColumnSelectionAllowed(true);
            table.setRowSelectionAllowed(false);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            table.getTableHeader().setReorderingAllowed(false);
            table.getColumnModel().getSelectionModel().addListSelectionListener(this);
            table.setToolTipText("<html><strong>summary of incoming table</strong>" +
                    "<p>here we only show the columns which appear within the incoming file as well as</p>" +
                    "<p>the first 4 rows of data in the table...</p></html>");

            Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn tc = columns.nextElement();
                tc.setHeaderRenderer(new CustomTableHeaderRenderer());
            }

            try {
                table.setDefaultRenderer(Class.forName("java.lang.Object"), new IncomingFileDataCellRenderer());
            } catch (ClassNotFoundException e) {
                // ignore
            }

            tableScroller = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            tableScroller.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "select column directly from table",
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR));
            tableScroller.getViewport().setBackground(UIHelper.BG_COLOR);
            tableScroller.setPreferredSize(new Dimension(325, 130));
            IAppWidgetFactory.makeIAppScrollPane(tableScroller);

            add(ch, BorderLayout.CENTER);
            add(tableScroller, BorderLayout.NORTH);

            JPanel infoLabelCont = new JPanel(new GridLayout(1, 1));
            infoLabel = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR);
            infoLabel.setVerticalAlignment(JLabel.TOP);

            infoLabelCont.add(infoLabel);

            infoPanel = new JPanel();
            infoPanel.setLayout(new BorderLayout());
            infoPanel.add(infoLabelCont, BorderLayout.CENTER);

            final JLabel selectButton = new JLabel(select);
            selectButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent mouseEvent) {
                    selectButton.setIcon(selectOver);
                }

                public void mouseExited(MouseEvent mouseEvent) {
                    selectButton.setIcon(select);
                }

                public void mousePressed(MouseEvent mouseEvent) {
                    selectButton.setIcon(select);
                    fireUpdateToListeners();
                }
            });

            JPanel selectButtonCont = new JPanel();
            selectButtonCont.setLayout(new BorderLayout());
            selectButtonCont.add(selectButton, BorderLayout.EAST);

            infoPanel.add(selectButtonCont, BorderLayout.SOUTH);
            infoPanel.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 7), "selection",
                    TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_11_BOLD,
                    UIHelper.GREY_COLOR));

            infoPanel.setVisible(false);
            add(infoPanel, BorderLayout.SOUTH);
        }

        public void updateLabel() {
            if (table.getSelectedColumn() != -1) {
                infoLabel.setText("<html>" + "<body>" +
                        "you have selected column with name <font color=\"#8DC63F\"><strong>"
                        + table.getColumnName(table.getSelectedColumn()) + "</strong></font>" +
                        "</body>" + "</html>");
                infoPanel.setVisible(true);
            } else {
                infoLabel.setText("");
                infoPanel.setVisible(false);
            }
        }

        public void scrollToColumnLocation(String colName) {
            for (ColumnObject co : columns) {
                if (colName.equals(co.getColumnName())) {
                    int colidx = co.getColumnNumber();
                    scrollToColumnIndex(colidx);
                }
            }
        }

        private void scrollToColumnIndex(int colidx) {
            table.setColumnSelectionInterval(colidx, colidx);

            JViewport scrollPane = tableScroller.getViewport();
            Rectangle rect = table.getCellRect(1, colidx, true);
            Point p = scrollPane.getViewPosition();
            rect.setLocation(rect.x - p.x, rect.y - p.y);
            scrollPane.scrollRectToVisible(rect);
        }

        public void valueChanged(ListSelectionEvent listSelectionEvent) {
            updateLabel();
        }
    }
}
