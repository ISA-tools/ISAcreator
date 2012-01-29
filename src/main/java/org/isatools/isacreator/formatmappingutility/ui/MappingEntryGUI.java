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

package org.isatools.isacreator.formatmappingutility.ui;

import au.com.bytecode.opencsv.CSVReader;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.CustomSplitPaneDivider;
import org.isatools.isacreator.formatmappingutility.exceptions.NoAvailableLoaderException;
import org.isatools.isacreator.formatmappingutility.io.ISAFieldMapping;
import org.isatools.isacreator.formatmappingutility.io.SavedMappings;
import org.isatools.isacreator.formatmappingutility.loader.FileLoader;
import org.isatools.isacreator.formatmappingutility.renderers.MappingSelectionTreeCellRenderer;
import org.isatools.isacreator.formatmappingutility.utils.TableReferenceObjectWrapper;
import org.isatools.isacreator.model.GeneralFieldTypes;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class MappingEntryGUI extends JPanel implements TreeSelectionListener, MouseListener {
    private static final Logger log = Logger.getLogger(MappingEntryGUI.class.getName());

    @InjectedResource
    private ImageIcon mappingHelp;
    @InjectedResource
    private ImageIcon errorIcon;
    @InjectedResource
    private ImageIcon popupOptions;
    @InjectedResource
    private static ImageIcon tableBrowserIcon;

    private TableReferenceObject tableReferenceObject;
    private static String[] columnsToBeMappedTo;
    private String fileName;
    private int readerToUse;
    private SavedMappings savedMappings;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;
    private JTree isatabFieldsTree;
    private JPanel swappableDataEntryContainer;
    private JPanel statusPanel;
    private JLabel statusUI;

    private List<String> addedFields;
    // maintain the mapping from a tree element to it's respective mapping entry screen.
    private List<MappedElement> mappingRef;
    private Map<String, MappedElement> fixedMappings;
    private Set<String> fixedMappingsAdded;

    private MappingInfoTab mappingInfo;
    private Toolbox addNewFieldUI;

    private static String[][] initialData;

    public MappingEntryGUI(TableReferenceObject tableReferenceObject, final String[] columnsToBeMappedTo,
                           final String fileName, int readerToUse, SavedMappings savedMappings, Map<String, MappedElement> fixedMappings) {
        this.tableReferenceObject = tableReferenceObject;
        MappingEntryGUI.columnsToBeMappedTo = columnsToBeMappedTo;
        this.fileName = fileName;
        this.readerToUse = readerToUse;
        this.savedMappings = savedMappings;
        this.fixedMappings = fixedMappings;

        ResourceInjector.get("formatmappingutility-package.style").inject(this);

        mappingRef = new ArrayList<MappedElement>();
        addedFields = new ArrayList<String>();
        fixedMappingsAdded = new HashSet<String>();

    }

    public void performPreliminaryLoading() throws BiffException, IOException, NoAvailableLoaderException {
        initialData = processTable();
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        swappableDataEntryContainer = new JPanel();

        JScrollPane deScroller = new JScrollPane(
                swappableDataEntryContainer,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        deScroller.setBackground(UIHelper.BG_COLOR);
        deScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        deScroller.setBorder(new EmptyBorder(0, 0, 0, 0));
        deScroller.setMinimumSize(new Dimension(400, 300));

        IAppWidgetFactory.makeIAppScrollPane(deScroller);

        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true,
                createTree(), deScroller);

        BasicSplitPaneUI paneUI = new BasicSplitPaneUI() {
            @Override
            public BasicSplitPaneDivider createDefaultDivider() {
                return new CustomSplitPaneDivider(this);
            }
        };

        pane.setUI(paneUI);
        pane.setBackground(UIHelper.BG_COLOR);

        pane.setBorder(new EmptyBorder(1, 1, 10, 1));

        add(pane, BorderLayout.CENTER);

        isatabFieldsTree.setSelectionRow(0);


        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setVisible(false);

        statusUI = UIHelper.createLabel("", UIHelper.VER_11_BOLD, UIHelper.RED_COLOR, JLabel.CENTER);
        statusPanel.add(statusUI);

        add(statusPanel, BorderLayout.SOUTH);
    }

    public JComponent createTree() {

        // take in TableReferenceObject and create initial fields in the order they should appear and add the TreeModel
        // add TreeSelectionListener to the tree to monitor which node is selected for change of the data entry panel being
        // shown in the RHS panel.
        rootNode = new DefaultMutableTreeNode("ISATAB Fields");

        addHeadersToTree();
        treeModel = new DefaultTreeModel(rootNode);
        isatabFieldsTree = new JTree(treeModel);
        isatabFieldsTree.addTreeSelectionListener(this);
        isatabFieldsTree.addMouseListener(this);
        isatabFieldsTree.setCellRenderer(new MappingSelectionTreeCellRenderer());

        JScrollPane treeScroller = new JScrollPane(isatabFieldsTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroller.setBackground(UIHelper.BG_COLOR);
        treeScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        treeScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(treeScroller);

        JPanel treeContainer = new JPanel();
        treeContainer.setLayout(new BoxLayout(treeContainer, BoxLayout.PAGE_AXIS));

        treeContainer.add(treeScroller);
        treeContainer.add(Box.createVerticalStrut(5));

        JPanel westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanel.setPreferredSize(new Dimension(200, 375));

        westPanel.add(treeContainer);
        westPanel.add(createToolbox(), BorderLayout.SOUTH);

        return westPanel;
    }

    private void addHeadersToTree() {
        TableReferenceObjectWrapper wrapperForMapper = new TableReferenceObjectWrapper(tableReferenceObject);
        for (String column : wrapperForMapper.getStdHeaders()) {
            if (!column.equals(GeneralFieldTypes.UNIT.name) && !column.equals(TableReferenceObject.ROW_NO_TEXT)) {
                MappingInformation toUse = chooseDisplay(column);
                MappedElement mn = new MappedElement(column, toUse);
                mappingRef.add(mn);

                rootNode.add(new DefaultMutableTreeNode(mn));
            }
        }
    }

    private Toolbox createToolbox() {
        addNewFieldUI = new Toolbox();

        addNewFieldUI.addPropertyChangeListener("nodeAdded", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

                // now need to reform tree based on the addition of this element!
                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) isatabFieldsTree.getLastSelectedPathComponent();

                if (dmtn != null) {

                    String newFieldName = propertyChangeEvent.getNewValue().toString();

                    if (!isDuplicateField(newFieldName) && dmtn.getUserObject() instanceof MappedElement) {

                        MappedElement mn = (MappedElement) dmtn.getUserObject();

                        if (newFieldName.contains(GeneralFieldTypes.PARAMETER_VALUE.name) && !mn.getFieldName().contains(GeneralFieldTypes.PROTOCOL_REF.name)) {
                            statusUI.setText("you can only add a Parameter Value to a Protocol REF");
                            statusUI.setVisible(true);
                        } else {
                            int count = 0;

                            for (MappedElement node : mappingRef) {
                                if (node == mn) {
                                    break;
                                }
                                count++;
                            }

                            // determine what type of mapping interface to show!
                            MappingInformation toUse = chooseDisplay(newFieldName);

                            mappingRef.add(count + 1, new MappedElement(newFieldName, toUse));
                            addedFields.add(newFieldName);
                            reformTree();
                            statusPanel.setVisible(false);
                        }
                    } else {
                        statusUI.setText("a field with the name " + newFieldName + " already exists!");
                        statusPanel.setVisible(true);
                    }
                } else {
                    statusUI.setText("please select a node in the tree to add the element after!");
                    statusPanel.setVisible(true);
                }
                statusPanel.revalidate();
                statusPanel.repaint();
            }
        });
        return addNewFieldUI;
    }

    public MappedElement getMappingNodeForField(String fieldName) {
        for (MappedElement mn : mappingRef) {
            if (mn.getFieldName().equals(fieldName)) {
                return mn;
            }
        }
        return null;
    }

    private MappingInformation chooseDisplay(String newFieldName) {
        ISAFieldMapping mapping = null;

        if (savedMappings != null) {
            mapping = savedMappings.getISAFieldMappingByISAFieldName(newFieldName);
        }

        if (fixedMappings.containsKey(newFieldName) && !fixedMappingsAdded.contains(newFieldName)) {
            // only add the field once, then perhaps the field should be removed from the fixedMappings map, although perhaps
            // a boolean is more useful, or a map of field to boolean
            MappedElement mi = fixedMappings.get(newFieldName);
            mi.getDisplay().disableEnableComponents(false);
            fixedMappingsAdded.add(newFieldName);
            return mi.getDisplay();

        } else if (newFieldName.contains("Characteristics") ||
                newFieldName.contains(GeneralFieldTypes.FACTOR_VALUE.name) ||
                newFieldName.contains(GeneralFieldTypes.PARAMETER_VALUE.name)) {
            return new GeneralAttributeEntry(newFieldName, columnsToBeMappedTo, mapping);
        } else if (newFieldName.contains(GeneralFieldTypes.PROTOCOL_REF.name)) {
            // current implementation only dealing with general column names rather than their positions.
            return new ProtocolFieldEntry(newFieldName, columnsToBeMappedTo, mapping);

        } else {
            return new NormalFieldEntry(newFieldName, columnsToBeMappedTo, mapping);
        }
    }

    private boolean isDuplicateField(String newFieldName) {
        for (MappedElement mn : mappingRef) {
            if (mn.getFieldName().equals(newFieldName) && !newFieldName.equals(GeneralFieldTypes.SAMPLE_NAME.name) &&
                    !newFieldName.equals("Material Type") &&
                    !newFieldName.equals(GeneralFieldTypes.PROTOCOL_REF.name)) {

                return true;
            }
        }
        return false;
    }

    private void reformTree() {
        rootNode = new DefaultMutableTreeNode("ISATAB Fields");
        for (MappedElement mn : mappingRef) {
            rootNode.add(new DefaultMutableTreeNode(mn));
        }
        treeModel.setRoot(rootNode);
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

        if (isatabFieldsTree.getLastSelectedPathComponent() != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) isatabFieldsTree.getLastSelectedPathComponent();
            swappableDataEntryContainer.removeAll();
            if (selectedNode.isLeaf()) {
                MappedElement mn = (MappedElement) selectedNode.getUserObject();
                swappableDataEntryContainer.add(mn.getDisplay());

                mn.getDisplay().addPropertyChangeListener("changeInWhetherToMap", new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        isatabFieldsTree.revalidate();
                        isatabFieldsTree.repaint();
                    }
                });

                addNewFieldUI.updateFieldOptions(mn.getFieldName());
            } else {
                if (mappingInfo == null) {
                    mappingInfo = new MappingInfoTab();
                }
                // show info node item
                swappableDataEntryContainer.add(mappingInfo);
            }
            swappableDataEntryContainer.revalidate();
            swappableDataEntryContainer.repaint();
        }

    }

    private void showTreePopup(JComponent jc, final int x, final int y) {
        final JPopupMenu popup = new JPopupMenu("Remove");
        popup.setLightWeightPopupEnabled(false);
        jc.add(popup);


        JMenuItem removeField = new JMenuItem("Remove field");
        removeField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {

                TreePath selPath = isatabFieldsTree.getPathForLocation(x, y);

                DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) selPath.getLastPathComponent();

                MappedElement mn = (MappedElement) dmtn.getUserObject();
                if (mn != null) {
                    // check if node is allowed to be removed!
                    if (addedFields.contains(mn.getFieldName())) {
                        mappingRef.remove(mn);
                        reformTree();
                        addedFields.remove(mn.getFieldName());
                        statusPanel.setVisible(false);
                    } else {
                        statusUI.setText("this field cannot be deleted as it is required!");
                        statusPanel.setVisible(true);
                    }
                }
            }
        });


        JPanel popupOptionsTitle = new JPanel(new GridLayout(1, 1));
        popupOptionsTitle.add(new JLabel(popupOptions, JLabel.LEFT));

        popup.add(popupOptionsTitle);
        popup.add(removeField);
        popup.show(jc, x, y);
    }


    public List<MappedElement> getTreeInfo() {
        List<MappedElement> mn = new ArrayList<MappedElement>();

        Enumeration enumerate = ((DefaultMutableTreeNode) treeModel.getRoot()).breadthFirstEnumeration();

        while (enumerate.hasMoreElements()) {

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumerate.nextElement();

            if (node.isLeaf()) {
                mn.add((MappedElement) node.getUserObject());
            }
        }

        return mn;
    }


    public void mouseClicked(MouseEvent mouseEvent) {

    }

    public void mousePressed(MouseEvent mouseEvent) {
        // we're right clicking on the JTree here, so will act on jtree nodes
        int selRow = isatabFieldsTree.getRowForLocation(mouseEvent.getX(), mouseEvent.getY());

        if (selRow != -1 && mouseEvent.getSource() instanceof JTree &&
                SwingUtilities.isRightMouseButton(mouseEvent)) {
            showTreePopup(isatabFieldsTree, mouseEvent.getX(), mouseEvent.getY());
        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    class MappingInfoTab extends JLayeredPane {

        MappingInfoTab() {
            setLayout(new BorderLayout());
            createGUI();
        }

        private void createGUI() {
            JPanel infoLabCont = new JPanel(new GridLayout(1, 1));
            JLabel infoLab = new JLabel(mappingHelp);
            infoLabCont.add(infoLab);
            add(infoLabCont, BorderLayout.CENTER);
        }
    }

    public static String[][] getInitialData() {
        return initialData;
    }


    /**
     * Pulls out first rows (up to 4) of the table to display
     *
     * @return 2D data array
     */
    private String[][] processTable() throws IOException, BiffException, NoAvailableLoaderException {
        List<String[]> data = new ArrayList<String[]>();

        if (readerToUse == FileLoader.CSV_READER_CSV || readerToUse == FileLoader.CSV_READER_TXT) {

            char delimiter = (readerToUse == FileLoader.CSV_READER_CSV) ? FileLoader.COMMA_DELIM : FileLoader.TAB_DELIM;
            CSVReader fileReader = new CSVReader(new FileReader(fileName), delimiter);

            // read first line to discard it!
            fileReader.readNext();
            String[] nextLine;
            int count = 0;

            while ((nextLine = fileReader.readNext()) != null && count < 4) {
                data.add(nextLine);
                count++;
            }

            return convertListToArray(data);

        } else if (readerToUse == FileLoader.SHEET_READER) {
            // read the file using the Sheet reader from jxl library
            Workbook w;

            File f = new File(fileName);
            if (!f.isHidden()) {
                w = Workbook.getWorkbook(f);
                // Get the first sheet
                for (Sheet s : w.getSheets()) {
                    int rowCount = Math.min(5, s.getRows());

                    if (s.getRows() > 1) {
                        for (int row = 1; row < rowCount; row++) {
                            String[] nextLine = new String[s.getColumns()];

                            for (int col = 0; col < s.getColumns(); col++) {
                                nextLine[col] = s.getCell(col, row).getContents();
                            }
                            data.add(nextLine);
                        }
                    }
                    break;
                }
                return convertListToArray(data);
            }
        } else {
            log.info("no reader available for use!");
            throw new NoAvailableLoaderException("no reader available for use for this file");
        }

        return convertListToArray(data);
    }

    private String[][] convertListToArray(List<String[]> list) {
        String[][] converted = new String[list.size()][];

        for (int listItem = 0; listItem < list.size(); listItem++) {
            converted[listItem] = list.get(listItem);
        }
        return converted;
    }


    public Map<String, ISAFieldMapping> createMappingRefs() {
        Map<String, ISAFieldMapping> fields = new HashMap<String, ISAFieldMapping>();
        for (MappedElement mn : mappingRef) {
            // todo accommodate the protocol ref mapping...?)
            ISAFieldMapping mapping = mn.getDisplay().createISAFieldMapping();
            if (mapping != null) {
                fields.put(mn.getFieldName(), mapping);
            }
        }

        return fields;
    }


}
