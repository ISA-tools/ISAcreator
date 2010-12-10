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

package org.isatools.isacreator.filterablelistselector;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FilterableListSelector extends JFrame {

    private static final ImageIcon CLOSE = new ImageIcon(FilterableListSelector.class.getResource("/images/filterablelistselector/close.png"));
    private static final ImageIcon CLOSE_OVER = new ImageIcon(FilterableListSelector.class.getResource("/images/filterablelistselector/close_over.png"));

    private String[] listItems;
    private ExtendedJList filterList;

    public FilterableListSelector(String[] listItems) {
        this.listItems = listItems;
    }

    /**
     * Create the GUI.
     */
    public void createGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setLayout(new BorderLayout());
                setPreferredSize(new Dimension(250, 200));
                setUndecorated(true);
                setAlwaysOnTop(true);
                instantiateFrame();
                pack();
            }
        });
    }

    public void updateContents(final String[] newContents) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if ((filterList != null) && (newContents != null)) {
                    filterList.updateContents(newContents);
                }
            }
        });
    }

    public void instantiateFrame() {
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBackground(UIHelper.BG_COLOR);
        selectionPanel.setPreferredSize(new Dimension(250, 200));

        selectionPanel.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6));

        JPanel protocolSelectionList = new JPanel(new BorderLayout());
        protocolSelectionList.setBackground(UIHelper.BG_COLOR);

        filterList = new ExtendedJList(new ColumnFilterRenderer());

        for (String item : listItems) {
            filterList.addItem(item);
        }

        filterList.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {

                if (SwingUtilities.isLeftMouseButton(event) && event.getClickCount() == 2) {
                    String historyTerm = filterList.getSelectedTerm();
                    if (historyTerm != null) {
                        firePropertyChange("itemSelected", "OLD_VALUE", historyTerm);
                    }
                }

            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
            }

            public void mouseExited(MouseEvent event) {
            }
        });

        filterList.setToolTipText("double click on term to add to select it.");

        JScrollPane historyScroll = new JScrollPane(filterList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScroll.setBorder(null);

        IAppWidgetFactory.makeIAppScrollPane(historyScroll);

        UIHelper.renderComponent(filterList.getFilterField(), UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);
        protocolSelectionList.add(filterList.getFilterField(),
                BorderLayout.NORTH);
        protocolSelectionList.add(historyScroll, BorderLayout.CENTER);

        protocolSelectionList.add(UIHelper.createLabel("<html><b>double click</b> on an item to select it.</html>", UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR), BorderLayout.SOUTH);

        selectionPanel.add(protocolSelectionList);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel close = new JLabel(CLOSE, JLabel.RIGHT);

        close.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {

            }

            public void mousePressed(MouseEvent event) {
                firePropertyChange("noItemSelected", "", "none");
            }

            public void mouseReleased(MouseEvent event) {

            }

            public void mouseEntered(MouseEvent event) {
                close.setIcon(CLOSE_OVER);
            }

            public void mouseExited(MouseEvent event) {
                close.setIcon(CLOSE);
            }
        });

        buttonPanel.add(close, BorderLayout.EAST);

        buttonPanel.add(new JLabel(
                new ImageIcon(getClass().getResource("/images/filterablelistselector/select_value.png")),
                SwingConstants.RIGHT), BorderLayout.WEST);

        selectionPanel.add(buttonPanel, BorderLayout.NORTH);
        add(selectionPanel, BorderLayout.CENTER);
    }

}
