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

package org.isatools.isacreator.qrcode.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.filterableTree.FilterableJTree;
import org.isatools.isacreator.common.filterableTree.TreeFilterModel;
import org.isatools.isacreator.qrcode.logic.QRCode;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.Set;

/**
 * QRCodeViewerPane
 *
 * @author eamonnmaguire
 * @date Oct 20, 2010
 */


public class QRCodeViewerPane extends JPanel implements MouseListener {

    // will display a list with the generated codes.

    @InjectedResource
    private ImageIcon leftFilter, rightFilter;


    private FilterableJTree<String, QRCode> qrCodeTree;
    private QRCodeDetailedView detailedView;
    private String studyId;
    private Map<String, QRCode> sampleNameToQRCodes;


    public QRCodeViewerPane(String studyId, Map<String, QRCode> sampleNameToQRCodes) {
        this.studyId = studyId;

        this.sampleNameToQRCodes = sampleNameToQRCodes;

        ResourceInjector.get("qrcode-generator-package.style").inject(this);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
    }

    public void createGUI() {
        detailedView = new QRCodeDetailedView();
        detailedView.setPreferredSize(new Dimension(350, 280));

        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BoxLayout(treePanel, BoxLayout.LINE_AXIS));
        treePanel.setBackground(UIHelper.BG_COLOR);

        treePanel.add(createTreePanel());

        add(treePanel, BorderLayout.WEST);
        add(detailedView, BorderLayout.CENTER);

        String firstSample = "";
        int count = 0;
        for (String sampleName : sampleNameToQRCodes.keySet()) {
            if (count == 0) {
                firstSample = sampleName;
                count++;
            }
            qrCodeTree.addItem(studyId + " codes", sampleNameToQRCodes.get(sampleName));
        }

        qrCodeTree.expandRow(0);
        qrCodeTree.expandRow(1);
        qrCodeTree.setSelectionRow(2);
        detailedView.setQRCode(sampleNameToQRCodes.get(firstSample));
    }

    /**
     * Will create a tree containing all of the generated QR codes. Tree is filterable.
     *
     * @return JPanel containing the tree panel
     */
    private JPanel createTreePanel() {
        JPanel treePanel = new JPanel();
        treePanel.setLayout(new BorderLayout());
        treePanel.setBackground(UIHelper.BG_COLOR);

        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Samples");
        qrCodeTree = new FilterableJTree<String, QRCode>();

        TreeFilterModel treeModel = new FilterableQRCodeViewTreeModel<String, Set<QRCode>>(top, qrCodeTree);

        qrCodeTree.setModel(treeModel);
        qrCodeTree.setCellRenderer(new QRCodeTreeRenderer());
        qrCodeTree.setShowsRootHandles(false);
        qrCodeTree.addMouseListener(this);
        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        qrCodeTree.setUI(ui);
        qrCodeTree.setRowHeight(30);

        JScrollPane treeScroll = new JScrollPane(qrCodeTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setBorder(new EmptyBorder(1, 1, 1, 1));
        treeScroll.setPreferredSize(new Dimension(200, 275));
        treeScroll.getViewport().setBackground(UIHelper.BG_COLOR);

        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        treePanel.add(treeScroll, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel();
        filterPanel.setBackground(UIHelper.BG_COLOR);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.LINE_AXIS));

        UIHelper.renderComponent(qrCodeTree.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        ((JTextField) qrCodeTree.getFilterField()).setBorder(new EmptyBorder(1, 1, 1, 1));

        filterPanel.add(new JLabel(leftFilter));
        filterPanel.add(qrCodeTree.getFilterField());
        filterPanel.add(new JLabel(rightFilter));
        filterPanel.add(new ClearFieldUtility(qrCodeTree.getFilterField()));

        treePanel.add(filterPanel, BorderLayout.SOUTH);
        treePanel.setBorder(new EmptyBorder(1, 1, 1, 7));
        return treePanel;
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
        TreePath selPath = qrCodeTree.getPathForLocation(event.getX(), event.getY());
        if (selPath != null) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            if (node.isLeaf()) {
                if (node.getUserObject() instanceof QRCode) {
                    QRCode selectedCode = (QRCode) node.getUserObject();
                    detailedView.setQRCode(selectedCode);
                    detailedView.repaint();
                }
            }
        }
    }


}
