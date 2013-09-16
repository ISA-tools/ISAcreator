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

package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.ontologymanager.utils.OntologyTermUtils;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.utils.GeneralUtils;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * SpreadsheetColumnRenderer - GUI component for the Spreadsheet table.
 */
public class SpreadsheetColumnRenderer extends JPanel implements TableCellRenderer {

    @InjectedResource
    private ImageIcon upArrow, downArrow, normal, endColumnIcon;

    public static final int NONE = 0;
    public static final int DOWN = 1;
    public static final int UP = 2;

    private Map<Integer, Integer> state;
    private Set<Integer> isRequired;

    private JLabel text;
    private JLabel sortIndicator;

    public SpreadsheetColumnRenderer() {
        ResourceInjector.get("spreadsheet-package.style").inject(this);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        state = new Hashtable<Integer, Integer>();
        isRequired = new HashSet<Integer>();

        instantiatePanel();
    }

    private void instantiatePanel() {
        text = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, JLabel.LEFT);
        add(text, BorderLayout.CENTER);
        sortIndicator = new JLabel(normal);
        add(UIHelper.wrapComponentInPanel(sortIndicator), BorderLayout.WEST);
        add(UIHelper.wrapComponentInPanel(new JLabel(endColumnIcon)), BorderLayout.EAST);
    }

    public void setIsRequired(int columnIndex) {
        isRequired.add(columnIndex);
    }

    public int getState(int col) {
        int retValue;
        Integer obj = state.get(col);

        if (obj == null) {
            retValue = NONE;
        } else {
            if (obj == DOWN) {
                retValue = DOWN;
            } else {
                retValue = UP;
            }
        }
        return retValue;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int rowIndex, int vColIndex) {
        Integer obj = state.get(vColIndex);
        if (obj != null) {
            if (obj == DOWN) {
                sortIndicator.setIcon(upArrow);
            } else {
                sortIndicator.setIcon(downArrow);
            }
        } else {
            sortIndicator.setIcon(normal);
        }

        text.setForeground(isRequired.contains(vColIndex) ? UIHelper.RED_COLOR : UIHelper.DARK_GREEN_COLOR);
        text.setFont(isSelected ? UIHelper.VER_11_BOLD : UIHelper.VER_11_PLAIN);

        boolean shortNames = Boolean.parseBoolean(ISAcreatorProperties.getProperty("useShortNames"));

        String valueString = value.toString();

        if (shortNames) {
            String shortHeader = GeneralUtils.getShortString(valueString);
            text.setText(shortHeader);
        } else {
            if (valueString.contains(":")){
                text.setText(OntologyTermUtils.fullAnnotatedHeaderToUniqueId(valueString));
            }else{
                text.setText(valueString);
            }
        }

        return this;
    }


    public void setSelectedColumn(int col) {
        if (col < 0) {
            return;
        }

        Integer value;
        Integer obj = state.get(col);

        if (obj == null) {
            value = DOWN;
        } else {
            if (obj == DOWN) {
                value = UP;
            } else {
                value = DOWN;
            }
        }

        state.clear();
        state.put(col, value);
    }

}