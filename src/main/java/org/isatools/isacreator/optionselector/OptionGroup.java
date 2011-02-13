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

package org.isatools.isacreator.optionselector;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * OptionGroup
 *
 * @author eamonnmaguire
 * @date Jun 1, 2010
 */


public class OptionGroup<T> extends JPanel implements MouseListener {

    public static final int HORIZONTAL_ALIGNMENT = 0;
    public static final int VERTICAL_ALIGNMENT = 1;

    private Map<T, OptionItem> availableOptions;
    private boolean singleSelection;
    private int alignment;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("optionselector-package.style").load(
                OptionGroup.class.getResource("/dependency-injections/optionselector-package.properties"));
    }

    /**
     * OptionGroup contains a number of items which you can either select one or many of depending on the single selection parameter
     *
     * @param alignment       - e.g. OptionGroup.HORIZONTAL_ALIGNMENT to align the options in a horizontal arrangement.
     * @param singleSelection - whether or not this group should allow singular selection (true) or multiple option selection (false)
     */
    public OptionGroup(int alignment, boolean singleSelection) {
        this(alignment, singleSelection, 0);
    }

    public OptionGroup(int alignment, boolean singleSelection, int leftPadding) {
        this.singleSelection = singleSelection;
        this.alignment = alignment;

        availableOptions = new ListOrderedMap<T, OptionItem>();

        setLayout(new BoxLayout(this, alignment == HORIZONTAL_ALIGNMENT ? BoxLayout.LINE_AXIS : BoxLayout.PAGE_AXIS));
        add(Box.createHorizontalStrut(leftPadding));
        setOpaque(false);
    }

    public void addOptionItem(T item) {
        addOptionItem(item, false);
    }


    public void addOptionItem(T item, boolean setSelected) {
        addOptionItem(item, setSelected, null, null);
    }

    public void addOptionItem(T item, boolean setSelected, ImageIcon selectedImage, ImageIcon unselectedImage) {
        OptionItem<T> option = new OptionItem<T>(setSelected, item, selectedImage, unselectedImage);
        option.addMouseListener(this);
        if (!availableOptions.containsKey(item)) {
            availableOptions.put(item, option);
            add(option);
            add(alignment == OptionGroup.HORIZONTAL_ALIGNMENT ? Box.createHorizontalStrut(5)
                    : Box.createVerticalStrut(5));
        }
    }

    /**
     * When singleSelection is enabled, this will return the selected option. If singleSelection is not enabled and multiple
     * items can be selected, this method will return the first selected item.
     *
     * @return String - either the selected item (if singleSelection == true) or the first selected item (if singleSelection == false)
     */
    public T getSelectedItem() {
        for (T option : availableOptions.keySet()) {
            if (availableOptions.get(option).getSelectedIcon()) {
                return option;
            }
        }
        return null;
    }

    /**
     * Returns all selected items in a Set of Strings
     *
     * @return Set<T> containing all of the selected items
     */
    public Set<T> getSelectedItems() {
        Set<T> selectedItems = new HashSet<T>();
        for (T option : availableOptions.keySet()) {
            if (availableOptions.get(option).getSelectedIcon()) {
                selectedItems.add(option);
            }
        }
        return selectedItems;
    }

    public void setSelectedItem(T item) {
        clearSelections();
        OptionItem<T> toSelect = availableOptions.get(item);
        System.out.println("setting item to be selected");
        toSelect.setSelectedIcon(toSelect.getSelectedIcon());
        revalidate();
        repaint();

    }


    // mouse listener is used to determine items clicked and so forth

    public void mouseClicked(MouseEvent mouseEvent) {
    }

    public void mouseEntered(MouseEvent mouseEvent) {
    }

    public void mouseExited(MouseEvent mouseEvent) {
    }

    public void mousePressed(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof OptionItem) {
            OptionItem<T> item = (OptionItem<T>) mouseEvent.getSource();
            if (singleSelection) {
                if (!item.getSelectedIcon()) {
                    clearSelections();
                    item.setSelectedIcon(!item.getSelectedIcon());
                    fireItemSelectionEventToListeners(item);
                }
            } else {
                if (checkForSelectedItems(item)) {
                    item.setSelectedIcon(!item.getSelectedIcon());
                    fireItemSelectionEventToListeners(item);
                }
            }

        }
    }

    public void mouseReleased(MouseEvent mouseEvent) {
    }

    private void fireItemSelectionEventToListeners(OptionItem<T> selectedItem) {
        firePropertyChange("optionSelectionChange", "", selectedItem);
    }

    // add method to wipe clear all selections

    private void clearSelections() {
        for (T option : availableOptions.keySet()) {
            if (availableOptions.get(option).getSelectedIcon()) {
                availableOptions.get(option).setSelectedIcon(false);
            }
        }
    }

    // add method to check if there are items selected...used to ensure that there is always at least one item selected

    private boolean checkForSelectedItems(OptionItem<T> currentItem) {
        for (T option : availableOptions.keySet()) {
            if (availableOptions.get(option).getSelectedIcon() && availableOptions.get(option) != currentItem) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        JFrame testFrame = new JFrame("Option group test");

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        final JLabel test = UIHelper.createLabel("", UIHelper.VER_10_BOLD, UIHelper.LIGHT_GREEN_COLOR);

        final OptionGroup<String> optionGroup = new OptionGroup<String>(OptionGroup.HORIZONTAL_ALIGNMENT, true);
        optionGroup.addOptionItem("fast speed and low compression", true, new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_fast.png")), new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_fast_unchecked.png")));
        optionGroup.addOptionItem("medium speed and medium compression", false, new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_medium.png")), new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_medium_unchecked.png")));
        optionGroup.addOptionItem("slow speed and high compression", false, new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_slow.png")), new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_slow_unchecked.png")));

        optionGroup.addPropertyChangeListener("optionSelectionChange", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                test.setText(optionGroup.getSelectedItem());
            }
        });

        container.add(new JLabel(new ImageIcon(OptionGroup.class.getResource("/images/gui/output_archive_choose_compression.png")), JLabel.LEFT), BorderLayout.NORTH);
        container.add(optionGroup);

        container.add(test, BorderLayout.SOUTH);

        testFrame.add(container);
        testFrame.pack();
        testFrame.setVisible(true);
    }
}
