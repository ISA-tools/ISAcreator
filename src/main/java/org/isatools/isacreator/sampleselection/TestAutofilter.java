package org.isatools.isacreator.sampleselection;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.ontologyselectiontool.OntologyCellEditor;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.EventObject;

public class TestAutofilter extends JFrame {

    public TestAutofilter() {

    }

    public void createGUI() {
        setTitle("Test");
        setSize(new Dimension(400, 400));

        addTestTable();

        pack();
        setVisible(true);
    }

    private void addTestTable() {
        String[][] data = new String[50][3];

        JTable table = new JTable(data, new String[]{"Source Name", "Protocol REF", "Sample Name"}) {

            @Override
            public boolean editCellAt(int row, int column, EventObject eventObject) {
                boolean result = super.editCellAt(row, column, eventObject);
                final Component editor = getEditorComponent();
                if (editor != null && editor instanceof JTextComponent) {
                    if (eventObject == null) {
                        ((JTextComponent) editor).selectAll();
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                ((JTextComponent) editor).selectAll();
                            }
                        });
                    }
                }

                return result;
            }

            @Override
            public void changeSelection(int row, int column, boolean toggle, boolean extend) {
                super.changeSelection(row, column, toggle, extend);
                if (editCellAt(row, column))
                    getEditorComponent().requestFocusInWindow();
            }

            @Override
            public Component prepareEditor
                    (TableCellEditor tableCellEditor, int row, int column) {

                Component c = super.prepareEditor(tableCellEditor, row, column);
                if (c instanceof JTextComponent) {
                    ((JTextField) c).selectAll();
                }
                return c;
            }
        };

        table.getColumnModel().getColumn(1).setCellEditor(new OntologyCellEditor(false, null));

        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        add(scroller, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        new TestAutofilter().createGUI();
    }
}
