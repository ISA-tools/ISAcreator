/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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


package org.isatools.isacreator.archiveoutput;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.CustomTreeRenderer;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.spreadsheet.CustomTable;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ViewErrorPane extends JPanel implements TreeSelectionListener {

    private JTree errors;
    private ISAcreator main;
    private JLabel bottomRightCorner;
    private boolean poppedOut = false;

    @InjectedResource
    private ImageIcon locateButton, locateButtonOver, problemsIdentifiedImage, warning_TopRight,
            warning_BottomLeft, warning_BottomRight, popout, popoutOver;

    public ViewErrorPane(ISAcreator main) {
        this.main = main;

        ResourceInjector.get("archiveoutput-package.style").inject(this);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());

        JLabel problemsIdentifiedLabel = new JLabel(problemsIdentifiedImage,
                JLabel.LEFT);
        problemsIdentifiedLabel.setOpaque(false);

        JLabel warningIcon = new JLabel(warning_TopRight,
                JLabel.RIGHT);
        warningIcon.setOpaque(false);

        JPanel headerCont = new JPanel(new GridLayout(1, 2));
        headerCont.setBackground(UIHelper.BG_COLOR);
        headerCont.add(problemsIdentifiedLabel);
        headerCont.add(warningIcon);

        add(headerCont, BorderLayout.NORTH);

        DefaultMutableTreeNode treeInfo = new DefaultMutableTreeNode("nothing to report");
        errors = new JTree(treeInfo);
        errors.addTreeSelectionListener(this);
        errors.setAutoscrolls(true);

        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        errors.setUI(ui);
        errors.setCellRenderer(new CustomTreeRenderer(false, 240, 20));

        JScrollPane listScroller = new JScrollPane(errors,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setPreferredSize(new Dimension(250, 150));
        listScroller.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        add(listScroller, BorderLayout.CENTER);

        bottomRightCorner = new JLabel(popout, JLabel.RIGHT);
        bottomRightCorner.setOpaque(false);
        bottomRightCorner.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent event) {
                bottomRightCorner.setIcon(popoutOver);
            }

            public void mouseExited(MouseEvent event) {
                bottomRightCorner.setIcon(popout);
            }

            public void mousePressed(MouseEvent event) {
                bottomRightCorner.setIcon(popout);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ViewErrorUI enw = new ViewErrorUI(getMe());
                        enw.setLocation(((main.getX() + main.getWidth()) / 2) - (enw.getWidth() / 2), ((main.getY() + main.getHeight()) / 2) - (enw.getHeight() / 2));
                        main.hideGlassPane();
                    }
                });

            }
        });

        JLabel cornerImageLeft = new JLabel(warning_BottomLeft,
                JLabel.LEFT);
        cornerImageLeft.setOpaque(false);

        JLabel cornerImageRight = new JLabel(warning_BottomRight,
                JLabel.LEFT);
        cornerImageRight.setOpaque(false);

        JPanel bottomCont = new JPanel(new GridLayout(1, 2));
        bottomCont.setBackground(UIHelper.BG_COLOR);
        bottomCont.add(cornerImageLeft);

        bottomCont.add(bottomRightCorner);

        add(bottomCont, BorderLayout.SOUTH);

        setVisible(false);

    }

    private ViewErrorPane getMe() {
        return this;
    }

    public void refreshErrorView(DefaultMutableTreeNode newRoot) {
        errors.setModel(new DefaultTreeModel(newRoot));
        errors.revalidate();
    }

    public void removePopoutIcon() {
        bottomRightCorner.setIcon(warning_BottomRight);
        bottomRightCorner.removeMouseListener(bottomRightCorner.getMouseListeners()[0]);
    }

    public void setPoppedOut(boolean poppedOut) {
        this.poppedOut = poppedOut;
    }

    public void allowLocator(boolean allow) {
        if (allow) {
            bottomRightCorner.setIcon(locateButton);
            bottomRightCorner.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent event) {
                    bottomRightCorner.setIcon(locateButtonOver);
                }

                public void mouseExited(MouseEvent event) {
                    bottomRightCorner.setIcon(locateButton);
                }

                public void mousePressed(MouseEvent event) {
                    bottomRightCorner.setIcon(locateButton);
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) errors.getLastSelectedPathComponent();

                    if (selectedNode.isLeaf()) {
                        ArchiveOutputError el = (ArchiveOutputError) selectedNode.getUserObject();

                        if (el.getCol() != -1 && el.getRow() != -1) {


                            AssaySpreadsheet sheet = (AssaySpreadsheet) el.getSpreadsheet();
                            JTable table = sheet.getTable().getTable();

                            main.getDataEntryEnvironment().setCurrentPage(sheet);
                            sheet.scrollRectToVisible(sheet.getTable().getTable().getCellRect(el.getRow(), el.getCol(), false));
                            ((CustomTable) table).scrollToCellLocation(el.getRow(), el.getCol());
                            // set cell as selected!
                            table.setColumnSelectionInterval(el.getCol(), el.getCol());
                            table.setRowSelectionInterval(el.getRow(), el.getRow());
                        }
                    }
                }

            });
        } else {
            bottomRightCorner.setIcon(warning_BottomRight);
            if (bottomRightCorner.getMouseListeners().length > 0) {
                bottomRightCorner.removeMouseListener(bottomRightCorner.getMouseListeners()[0]);
            }
        }
    }

    public void valueChanged(TreeSelectionEvent event) {

        if (poppedOut) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) errors.getLastSelectedPathComponent();
            if (selectedNode != null) {
                bottomRightCorner.setEnabled(selectedNode.isLeaf());
                if (selectedNode.isLeaf()) {
                    if (selectedNode.getUserObject() instanceof ArchiveOutputError) {
                        ArchiveOutputError el = (ArchiveOutputError) selectedNode.getUserObject();

                        if (el.getCol() != -1 && el.getRow() != -1) {
                            allowLocator(true);
                        } else {
                            allowLocator(false);
                        }
                    }
                } else {
                    allowLocator(false);
                }
            }
        }
    }
}
