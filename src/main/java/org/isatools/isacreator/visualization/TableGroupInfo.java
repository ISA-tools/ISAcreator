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

package org.isatools.isacreator.visualization;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Displays the groups which appear within a table along with a key.
 *
 * @author Eamonn Maguire
 * @date Apr 29, 2009
 */


public class TableGroupInfo extends JFrame {
    private Map<String, List<Object>> groupsAndRows;
    private Map<String, Color> rowColors;
    private int totalRows;

    public TableGroupInfo(Map<String, List<Object>> groupsAndRows, Map<String, Color> rowColors, int totalRows) {
        this.groupsAndRows = groupsAndRows;
        this.rowColors = rowColors;
        this.totalRows = totalRows;

        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setPreferredSize(new Dimension(350, 270));
        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setAlwaysOnTop(true);

    }

    public void createGUI() {
        // create LHS panel for containment of group display and RHS for group information
        JPanel container = new JPanel(new GridLayout(1, 2));
        container.setLayout(new BoxLayout(container, BoxLayout.LINE_AXIS));
//        container.setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 3));

        JPanel groupContainer = new JPanel();
        groupContainer.setLayout(new BoxLayout(groupContainer, BoxLayout.PAGE_AXIS));

        JPanel groupLabCont = new JPanel(new GridLayout(1, 1));
        groupLabCont.add(UIHelper.createLabel("group distribution in table", UIHelper.VER_12_BOLD, new Color(153, 153, 153), JLabel.CENTER));
        groupContainer.add(groupLabCont);

        groupContainer.add(createGroupView(175, 225));

        container.add(groupContainer);

        JPanel keyContainer = new JPanel();
        keyContainer.setLayout(new BoxLayout(keyContainer, BoxLayout.PAGE_AXIS));

        JScrollPane keyScroller = new JScrollPane(null, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        keyScroller.setBackground(UIHelper.BG_COLOR);
        keyScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        keyScroller.setPreferredSize(new Dimension(165, 225));
        keyScroller.setAutoscrolls(true);
        keyScroller.setViewportView(createKey());
        keyScroller.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(keyScroller);

        JPanel keyLabCont = new JPanel(new GridLayout(1, 1));
        keyLabCont.add(UIHelper.createLabel("key", UIHelper.VER_12_BOLD, new Color(153, 153, 153), JLabel.CENTER));
        keyContainer.add(keyLabCont);

        keyContainer.add(keyScroller);
        container.add(keyContainer);

        add(container, BorderLayout.CENTER);

        HUDTitleBar titlePanel = new HUDTitleBar(
                new ImageIcon(getClass().getResource("/images/visualization/title-grip.png")).getImage(),
                new ImageIcon(getClass().getResource("/images/visualization/title-grip-inactive.png")).getImage()
        );
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));

        pack();
        setVisible(true);
    }

    private JPanel createGroupView(int width, int height) {
        Map<Rectangle, Color> toPaint = new HashMap<Rectangle, Color>();
        int x = 5;
        int y = 5;
        for (String s : groupsAndRows.keySet()) {
            int numRows = groupsAndRows.get(s).size();

            double size = (double) numRows / totalRows;

            double rectHeight = height * size;

            toPaint.put(new Rectangle(x, y, width - 5, (int) rectHeight), rowColors.get(s));

            y += rectHeight;

        }

        return new RowSection(toPaint, width, height);
    }

    private JComponent createKey() {

        return new GroupInformation(rowColors);
    }

    class RowSection extends JPanel {
        private Map<Rectangle, Color> toPaint;

        public RowSection(Map<Rectangle, Color> toPaint, int totalWidth, int totalHeight) {
            setPreferredSize(new Dimension(totalWidth, totalHeight));
            this.toPaint = toPaint;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g2d = (Graphics2D) graphics;

            for (Rectangle r : toPaint.keySet()) {
                g2d.setColor(toPaint.get(r));
                g2d.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
            }
        }
    }

    class GroupInformation extends JComponent {
        private Map<String, Color> groupColors;
        private double groupHeight;

        public GroupInformation(Map<String, Color> groupColors) {
            this.groupColors = groupColors;
            groupHeight = 15;
            setPreferredSize(new Dimension(300, 5 + (groupColors.size() * 17)));
        }


        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g2d = (Graphics2D) graphics;

            g2d.setFont(UIHelper.VER_10_BOLD);

            int x = 5;
            int y = 5;

            for (String s : groupColors.keySet()) {
                g2d.setColor(groupColors.get(s));
                g2d.fillRoundRect(x, y, 30, (int) groupHeight, 3, 3);
                g2d.setColor(new Color(153, 153, 153));

                g2d.drawString(s, x + 35, y + 10);


                y += groupHeight + 2;
            }
        }
    }
}





