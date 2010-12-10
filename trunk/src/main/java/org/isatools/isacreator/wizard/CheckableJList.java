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

package org.isatools.isacreator.wizard;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.HashSet;
import java.util.Iterator;

public class CheckableJList extends JList implements ListSelectionListener {

    HashSet<Integer> selectionCache = new HashSet<Integer>();
    int toggleIndex = -1;
    boolean toggleWasSelected;

    public CheckableJList() {
        super();
        setCellRenderer(new CheckBoxListCellRenderer());
        addListSelectionListener(this);
    }

    public CheckableJList(DefaultListModel dlm) {
        super(dlm);

        setCellRenderer(new CheckBoxListCellRenderer());
        addListSelectionListener(this);

        //set all items to be selected initially for usability purposes!
        if (dlm.getSize() > 1) {
            setSelectionInterval(0, dlm.getSize() - 1);
        }

    }

    public void valueChanged(ListSelectionEvent lse) {
        if (!lse.getValueIsAdjusting()) {
            removeListSelectionListener(this);

            // remember everything selected as a result of this action
            HashSet<Integer> newSelections = new HashSet<Integer>();
            int size = getModel().getSize();

            for (int i = 0; i < size; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    newSelections.add(i);
                }
            }

            // turn on everything that was previously selected
            Iterator<Integer> it;

            for (Integer aSelectionCache : selectionCache) {
                getSelectionModel()
                        .addSelectionInterval(aSelectionCache, aSelectionCache);
            }

            // add or remove the delta
            it = newSelections.iterator();

            while (it.hasNext()) {
                Integer nextInt = it.next();

                if (selectionCache.contains(nextInt)) {
                    getSelectionModel().removeSelectionInterval(nextInt, nextInt);
                } else {
                    getSelectionModel().addSelectionInterval(nextInt, nextInt);
                }
            }

            // save selections for next time
            selectionCache.clear();

            for (int i = 0; i < size; i++) {
                if (getSelectionModel().isSelectedIndex(i)) {
                    selectionCache.add(i);
                }
            }

            addListSelectionListener(this);
        }
    }

    class CheckBoxListCellRenderer extends JComponent
            implements ListCellRenderer {
        DefaultListCellRenderer defaultComp;
        JCheckBox checkbox;

        public CheckBoxListCellRenderer() {
            setLayout(new BorderLayout());

            checkbox = new JCheckBox();
            UIHelper.renderComponent(checkbox, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            defaultComp = new DefaultListCellRenderer();
            UIHelper.renderComponent(defaultComp, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            add(checkbox, BorderLayout.WEST);
            add(defaultComp, BorderLayout.CENTER);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            defaultComp.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            checkbox.setSelected(isSelected);

            Component[] comps = getComponents();

            for (Component comp : comps) {
                comp.setForeground(UIHelper.DARK_GREEN_COLOR);
                comp.setBackground(UIHelper.BG_COLOR);
            }

            return this;
        }
    }
}
