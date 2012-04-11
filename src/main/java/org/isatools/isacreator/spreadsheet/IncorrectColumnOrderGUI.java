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
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Shows were columns are incorrectly positioned.
 *
 * @author Eamonn Maguire
 * @date Jun 9, 2009
 */

public class IncorrectColumnOrderGUI extends JFrame {

    @InjectedResource
    private Image titleIcon, titleIconInactive;

    @InjectedResource
    private ImageIcon incorrectInfo, errorReportNode, errorReportNodeOpen, errorReportNodeClosed;

    private Map<String, List<String>> report;

    public IncorrectColumnOrderGUI(Map<String, List<String>> report) throws HeadlessException {
        this.report = report;

        ResourceInjector.get("spreadsheet-package.style").inject(this);

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(400, 325));
        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setAlwaysOnTop(true);
    }

    public void createGUI() {

        add(createContainerPanel(), BorderLayout.CENTER);

        HUDTitleBar titlePanel = new HUDTitleBar(
                titleIcon,
                titleIconInactive
        );
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(UIHelper.GREY_ROUNDED_BORDER);

        pack();
        setVisible(true);
    }

    private JPanel createContainerPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);

        // add label detailing what is wrong
        JPanel infoLabCont = new JPanel(new GridLayout(1, 1));
        JLabel infoLab = UIHelper.createLabel("<html>we detect where dependent columns have been moved into positions which will cause the ISATAB to be invalid...." +
                "<p><font color=\"#BE1E2D\">unfortunately, you have the following problems in your file(s)</font></p></html>", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        infoLab.setPreferredSize(new Dimension(370, 60));
        infoLabCont.add(infoLab);
        container.add(infoLabCont, BorderLayout.NORTH);

        // add image showing what is wrong.
        container.add(new JLabel(incorrectInfo), BorderLayout.WEST);

        // add container detailing problems in center.
        JTree report = new JTree(reformTree());
        report.setCellRenderer(new ErrorReportTreeRenderer());
        report.setAutoscrolls(true);
        report.setOpaque(false);

        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        report.setUI(ui);

        JScrollPane reportScroller = new JScrollPane(report, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        reportScroller.setOpaque(false);
        reportScroller.getViewport().setOpaque(false);
        reportScroller.setBorder(new TitledBorder(new RoundedBorder(UIHelper.RED_COLOR, 7), "problem(s) found", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.BELOW_TOP, UIHelper.VER_11_BOLD, UIHelper.RED_COLOR));

        IAppWidgetFactory.makeIAppScrollPane(reportScroller);

        container.add(reportScroller, BorderLayout.CENTER);

        // add container explaining more in the south panel.
        JPanel moreInfoLabCont = new JPanel(new GridLayout(1, 1));
        JLabel moreInfoLab = UIHelper.createLabel("<html>invalid columns are highlighted in <strong><font color=\"#BE1E2D\">RED</font></strong>" +
                "<p></p><p><i><font color=\"#BE1E2D\">not fixing these issues may result in being " +
                "unable to reopen the isatab file without intervention " +
                "from another spreadsheet package!</font></i></p></html>", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        moreInfoLab.setPreferredSize(new Dimension(370, 75));
        moreInfoLabCont.add(moreInfoLab);
        container.add(moreInfoLabCont, BorderLayout.SOUTH);

        return container;
    }

    private DefaultMutableTreeNode reformTree() {
        DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode("problems");

        for (String fileName : report.keySet()) {
            DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(fileName);

            // now add problems to the list
            for (String problem : report.get(fileName)) {
                fileNode.add(new DefaultMutableTreeNode(problem));
            }
            dmtn.add(fileNode);
        }

        return dmtn;

    }

    class ErrorReportTreeRenderer implements TreeCellRenderer {

        private JPanel contents;
        private JLabel icon;
        private JLabel text;


        public ErrorReportTreeRenderer() {
            contents = new JPanel(new BorderLayout());
            contents.setOpaque(false);
            icon = new JLabel();
            text = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.GREY_COLOR);
            contents.add(icon, BorderLayout.WEST);
            contents.add(text, BorderLayout.CENTER);

        }

        /**
         * Sets all list values to have a white background and green foreground if not selected, and
         * a green background and white foregroud if selected.
         *
         * @param tree     - List to render
         * @param val      - value of list item being rendered.
         * @param index    - list index for value to be renderered.
         * @param selected - is the value selected?
         * @param hasFocus - has the cell got focus?
         * @return - The CustomListCellRendered Component.
         */
        public Component getTreeCellRendererComponent(JTree tree, Object val, boolean selected, boolean expanded, boolean leaf, int index, boolean hasFocus) {

            if (leaf) {
                icon.setIcon(errorReportNode);
            } else if (expanded) {
                icon.setIcon(errorReportNodeOpen);
            } else {
                icon.setIcon(errorReportNodeClosed);
            }

            text.setText(val.toString());
            text.revalidate();

            // change text colour depending on selection
            if (selected) {
                text.setForeground(UIHelper.RED_COLOR);
            } else {
                text.setForeground(UIHelper.GREY_COLOR);
            }

            return contents;
        }
    }
}