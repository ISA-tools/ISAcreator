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

package org.isatools.isacreator.spreadsheet.transposedview;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.gui.formelements.FieldTypes;
import org.isatools.isacreator.gui.formelements.SubFormCellRenderer;
import org.isatools.isacreator.spreadsheet.CustomRowRenderer;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * TransposedSpreadsheetView
 * This class will reuse the SubForm view to create a transposed view of a Spreadsheet so that users may enter
 * data in the way more amenable to them. This is of particular use when users are filling in spreadsheets with
 * many columns and few rows of data.
 *
 * @author Eamonn Maguire
 * @date Sep 9, 2010
 */


public class TransposedSpreadsheetView extends JDialog {

    @InjectedResource
    private ImageIcon logo, toolboxIcon, highlightOnIcon, highlightOnOver, highlightOffIcon, highlightOffOver, goToRecord, goToRecordOver, go, goOver;

    private TransposedSpreadsheetModel transposedSpreadsheetModel;
    private TransposedSubForm transposedSpreadsheetSubform;
    private ConfirmationDialog confirmChoice;
    private int width;
    private int height;
    private JLabel information;
    private boolean isHighlighted = false;


    public TransposedSpreadsheetView(TransposedSpreadsheetModel transposedSpreadsheetModel, int width, int height) {
        this.transposedSpreadsheetModel = transposedSpreadsheetModel;
        this.width = width;
        this.height = height;
        ResourceInjector.get("spreadsheet-package.style").inject(this);
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        add(createTopPanel(), BorderLayout.NORTH);
        add(createTransposedView());
        add(createSouthPanel(), BorderLayout.SOUTH);
        updateInformation();
        pack();
    }

    /**
     * Creates panel with function logo and some utilities
     */
    private Container createTopPanel() {
        JPanel topContainer = new JPanel(new BorderLayout());

        Box logoContainer = Box.createHorizontalBox();
        logoContainer.add(new JLabel(logo));

        topContainer.add(logoContainer, BorderLayout.WEST);

        Box toolBox = Box.createHorizontalBox();

        toolBox.add(new JLabel(toolboxIcon));


        final JLabel highlightGroups = new JLabel(highlightOffIcon);
        highlightGroups.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                highlightGroups.setIcon(isHighlighted ? highlightOnOver : highlightOffOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                highlightGroups.setIcon(isHighlighted ? highlightOnIcon : highlightOffIcon);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                if (isHighlighted) {

                    transposedSpreadsheetSubform.changeTableRenderer(
                            transposedSpreadsheetSubform.getLockedTable(), null);
                    transposedSpreadsheetSubform.changeTableRenderer(transposedSpreadsheetSubform.getScrollTable(), null);
                } else {

                    transposedSpreadsheetSubform.changeTableRenderer(transposedSpreadsheetSubform.getLockedTable(),
                            new CustomRowRenderer(transposedSpreadsheetModel.getRowToColour(), UIHelper.VER_11_BOLD, true));

                    transposedSpreadsheetSubform.changeTableRenderer(transposedSpreadsheetSubform.getScrollTable(),
                            new CustomRowRenderer(transposedSpreadsheetModel.getRowToColour(), UIHelper.VER_11_PLAIN));
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        transposedSpreadsheetSubform.validate();
                        transposedSpreadsheetSubform.repaint();
                        highlightGroups.setIcon(isHighlighted ? highlightOnIcon : highlightOffIcon);
                    }
                });
                isHighlighted = !isHighlighted;
            }
        });

        toolBox.add(highlightGroups);

        final JLabel goToRecordButton = new JLabel(goToRecord);
        goToRecordButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                goToRecordButton.setIcon(goToRecordOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                goToRecordButton.setIcon(goToRecord);
            }
        });

        toolBox.add(goToRecordButton);

        Box goToRecordEntryField = Box.createVerticalBox();

        final JTextField field = new JTextField("row #");
        UIHelper.renderComponent(field, UIHelper.VER_10_PLAIN, UIHelper.LIGHT_GREY_COLOR, false);

        Dimension fieldSize = new Dimension(60, 16);
        field.setPreferredSize(fieldSize);
        field.setSize(fieldSize);

        goToRecordEntryField.add(Box.createVerticalStrut(5));
        goToRecordEntryField.add(field);
        goToRecordEntryField.add(Box.createVerticalStrut(5));

        final JLabel goButton = new JLabel(go);
        goButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                goButton.setIcon(goOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                goButton.setIcon(go);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                goToColumn(field);
            }
        });

        Action locateColumn = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                goToColumn(field);
            }
        };

        field.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "LOCATE_COLUMN");
        field.getActionMap().put("LOCATE_COLUMN", locateColumn);

        toolBox.add(goToRecordEntryField);
        toolBox.add(goButton);
        goToRecordEntryField.add(Box.createVerticalStrut(5));

        topContainer.add(toolBox, BorderLayout.EAST);

        information = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.LIGHT_GREY_COLOR);
        information.setHorizontalAlignment(SwingConstants.RIGHT);

        topContainer.add(UIHelper.wrapComponentInPanel(information), BorderLayout.SOUTH);

        return topContainer;
    }

    private void goToColumn(JTextField field) {
        int index;
        try {
            index = Integer.valueOf(field.getText());
            if (index > 0 && index <= transposedSpreadsheetSubform.getScrollTable().getColumnCount()) {
                scrollToColumnLocation(index - 1);
            } else {
                field.setText("invalid");
            }
        } catch (NumberFormatException nfe) {
            field.setText("invalid");
        }
    }

    /**
     * Creates the Factor definition subform
     *
     * @return - JPanel containing the Factor definition subform.
     */
    private Container createTransposedView() {

        Box subformContainer = Box.createVerticalBox();

        transposedSpreadsheetSubform = new TransposedSubForm("spreadsheet data", FieldTypes.ROW, transposedSpreadsheetModel.getFields(),
                transposedSpreadsheetModel.getNumberOfRecords(), width, height, transposedSpreadsheetModel.getData(), null);

        transposedSpreadsheetSubform.createGUI();
        transposedSpreadsheetSubform.addPropertyChangeListener("rowAdded", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                updateInformation();
            }
        });

        subformContainer.add(Box.createVerticalStrut(15));
        subformContainer.add(transposedSpreadsheetSubform);
        subformContainer.add(Box.createVerticalStrut(15));

        return subformContainer;
    }

    public void scrollToColumnLocation(int colIndex) {
        JTable scrollTable = transposedSpreadsheetSubform.getScrollTable();
        scrollTable.setColumnSelectionInterval(colIndex, colIndex);

        JViewport scrollPane = transposedSpreadsheetSubform.getFrozenTable().getViewport();
        Rectangle rect = scrollTable.getCellRect(1, colIndex, true);
        Point p = scrollPane.getViewPosition();
        rect.setLocation(rect.x - p.x, rect.y - p.y);

        scrollPane.scrollRectToVisible(rect);

        Map<Integer, Color> columnToColor = new HashMap<Integer, Color>();
        columnToColor.put(colIndex, new Color(28, 117, 188, 70));

        transposedSpreadsheetSubform.changeTableRenderer(transposedSpreadsheetSubform.getScrollTable(),
                new SubFormCellRenderer(UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, null, columnToColor));

        transposedSpreadsheetSubform.validate();
        transposedSpreadsheetSubform.repaint();
    }

    private Container createSouthPanel() {

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBorder(new LineBorder(Color.white, 4));
        buttonPanel.setOpaque(false);

        JButton closeButton = new FlatButton(ButtonType.RED, "Cancel");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                confirmChoice = new ConfirmationDialog();
                confirmChoice.addPropertyChangeListener(ConfirmationDialog.NO, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                    }
                });

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.YES, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                        closeWindow();
                    }
                });

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        confirmChoice.createGUI();
                        confirmChoice.showDialog(transposedSpreadsheetSubform);
                    }
                });
            }
        });

        JButton toSpreadsheet = new FlatButton(ButtonType.GREEN, "Apply Changes");
        toSpreadsheet.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                confirmChoice = new ConfirmationDialog();

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.NO, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                    }
                });

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.YES, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();

                        SubformConverter converter = new SubformConverter(transposedSpreadsheetSubform);
                        converter.doConversion();

                        int rowDifference;
                        if ((rowDifference = (transposedSpreadsheetSubform.getScrollTable().getColumnCount()
                                - transposedSpreadsheetModel.getSpreadsheet().getTableModel().getRowCount())) > 0) {
                            transposedSpreadsheetModel.getSpreadsheet().getSpreadsheetFunctions().addRows(rowDifference, false);
                        }
                        // start at column 1 since the first column is for the row number
                        transposedSpreadsheetModel.getSpreadsheet().getSpreadsheetFunctions().paste(0, 1, true);

                        closeWindow();
                    }
                });

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        confirmChoice.createGUI();
                        confirmChoice.showDialog(TransposedSpreadsheetView.this);
                    }
                });
            }
        });

        buttonPanel.add(closeButton, BorderLayout.WEST);
        buttonPanel.add(toSpreadsheet, BorderLayout.EAST);

        return buttonPanel;
    }

    private void updateInformation() {
        information.setText("<html><font color=\"#8DC63F\"><b>" + transposedSpreadsheetSubform.getScrollTable().getColumnCount() + "</b></font> records annotated with <font color=\"#8DC63F\"><b>" +
                transposedSpreadsheetModel.getFields().size() + "</b></font> metadata fields</html>");
    }

    private void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                transposedSpreadsheetModel.getSpreadsheet().getParentFrame().hideSheet();
                dispose();
            }
        });
    }

}
