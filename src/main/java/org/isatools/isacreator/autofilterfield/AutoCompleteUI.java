package org.isatools.isacreator.autofilterfield;

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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.sun.awt.AWTUtilities;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.autofilteringlist.FilterField;
import org.isatools.isacreator.autofilteringlist.FilterableListCellRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.GraphicsUtils;
import uk.ac.ebi.utils.collections.AlphaNumComparator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.List;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;

/**
 * The AutoCompleteUI class provides the user interface to allow users to select study samples from an autocompleting list
 * of Study sample ids. It also allows the user to propagate metadata from the study sample file directly into the Assay file.
 */
public class AutoCompleteUI<T extends Comparable> extends JWindow implements ActionListener {

    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;

    public static final float ANIMATION_DURATION = 250f;
    public static final int ANIMATION_SLEEP = 10;
    public static final float DESIRED_OPACITY = .93f;

    private Timer animationTimer;

    private boolean animating;

    private int animationDirection;
    private long animationStart;

    public static final int HEIGHT = 200;
    public static final int WIDTH = 230;

    private FilterField filterField;
    private List<T> filterableContent;
    private ListCellRenderer cellRenderer;

    private ExtendedJList filterList;


    public AutoCompleteUI(FilterField filterField, List<T> filterableContent) {
        this(filterField, filterableContent, new FilterableListCellRenderer());
    }

    public AutoCompleteUI(FilterField filterField, List<T> filterableContent, ListCellRenderer cellRenderer) {
        this.filterField = filterField;
        this.filterableContent = filterableContent;
        this.cellRenderer = cellRenderer;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setAlwaysOnTop(true);


        setBackground(UIHelper.BG_COLOR);

        addList();
        addCloseOption();
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(2, 2, 2, 2));
        pack();
    }

    private void addList() {
        filterList = new ExtendedJList(cellRenderer, filterField, true);
        filterList.setAutoscrolls(true);
        filterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    if (!filterList.isSelectionEmpty()) {
                        filterField.setText(filterList.getSelectedTerm());
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        updateContent(filterableContent);

        JScrollPane listScroller = new JScrollPane(filterList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        add(listScroller, BorderLayout.CENTER);
    }

    private void addCloseOption() {
        final JLabel closeLabel = UIHelper.createLabel("close this window (esc)", UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR);
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        closeLabel.setToolTipText("<html><strong>close</strong></html>");

        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeLabel.setFont(UIHelper.VER_9_PLAIN);
                fadeOutWindow();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeLabel.setFont(UIHelper.VER_9_BOLD);

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeLabel.setFont(UIHelper.VER_9_PLAIN);
            }
        });

        JPanel bottomPanel = UIHelper.wrapComponentInPanel(closeLabel);
        bottomPanel.setBackground(UIHelper.LIGHT_GREEN_COLOR);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void selectNextItem() {

        if (filterList.isSelectionEmpty()) {
            filterList.setSelectedIndex(0);
        } else if (filterList.getSelectedIndex() < filterList.getItems().size()) {
            filterList.setSelectedIndex(filterList.getSelectedIndex() + 1);
            scrollToItem();
        }
    }

    public void selectPreviousItem() {

        if (filterList.isSelectionEmpty()) {
            filterList.setSelectedIndex(0);
        } else if (filterList.getSelectedIndex() > 0) {
            filterList.setSelectedIndex(filterList.getSelectedIndex() - 1);
            scrollToItem();
        }
    }

    public Object getSelectedItem() {
        if (!filterList.isSelectionEmpty()) {
            return filterList.getSelectedValue();
        }

        return null;
    }

    public String getSelectedValue() {
        if (!filterList.isSelectionEmpty()) {
            return filterList.getSelectedTerm();
        }

        return "";
    }

    private void scrollToItem() {
        filterList.scrollRectToVisible(
                filterList.getCellBounds(filterList.getSelectedIndex(), filterList.getSelectedIndex()));
    }


    public void fadeInWindow() {
        if (GraphicsUtils.isWindowTransparencySupported()) {
            animationDirection = INCOMING;
            startAnimation();
        } else {
            setVisible(true);
        }
    }

    public void fadeOutWindow() {
        if (GraphicsUtils.isWindowTransparencySupported()) {
            animationDirection = OUTGOING;
            startAnimation();
        } else {
            setVisible(false);
        }

    }

    private void startAnimation() {

        // start animation timer
        animationStart = System.currentTimeMillis();

        if (animationTimer == null) {
            animationTimer = new Timer(ANIMATION_SLEEP, this);
        }

        animating = true;

        if (!isShowing()) {

            AWTUtilities.setWindowOpacity(this, 0f);
            repaint();
            setVisible(true);
        }

        animationTimer.start();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (animating) {
            // calculate height to show
            float animationPercent = (System.currentTimeMillis() -
                    animationStart) / ANIMATION_DURATION;
            animationPercent = Math.min(DESIRED_OPACITY, animationPercent);

            float opacity;

            if (animationDirection == INCOMING) {
                opacity = animationPercent;
            } else {
                opacity = DESIRED_OPACITY - animationPercent;
            }

            AWTUtilities.setWindowOpacity(this, opacity);
            repaint();

            if (animationPercent >= DESIRED_OPACITY) {
                stopAnimation();

                if (animationDirection == OUTGOING) {
                    setVisible(false);
                }
            }
        }
    }

    private void stopAnimation() {
        animationTimer.stop();
        animating = false;

        repaint();
    }

    public void updateContent(List<T> content) {
        filterableContent = content;

        filterList.clearItems();
        Collections.sort(filterableContent, new AlphaNumComparator<T>());
        for (T item : filterableContent) {
            filterList.addItem(item);
        }
    }
}
