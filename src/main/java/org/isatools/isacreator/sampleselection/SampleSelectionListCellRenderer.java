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

package org.isatools.isacreator.sampleselection;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;


/**
 * CustomListCellRenderer is used to render a JList so that it appears more pleasing to the eye
 *
 * @author Eamonn Maguire
 */
public class SampleSelectionListCellRenderer extends JComponent
        implements ListCellRenderer {

    @InjectedResource
    private ImageIcon selectedIcon;

    private Color selectedBackgroundColor = new Color(141, 198, 63, 10);

    JLabel selectedIconContainer;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");
        ResourceInjector.get("sample-selection-package.style").load(
                SampleSelectionListCellRenderer.class.getResource("/dependency-injections/autofilterfield-package.properties"));
    }


    /**
     * CustomListCellRenderer Constructor
     */
    public SampleSelectionListCellRenderer() {

        ResourceInjector.get("sample-selection-package.style").inject(this);

        setLayout(new BorderLayout());

        selectedIconContainer = new JLabel(selectedIcon);
        add(selectedIconContainer, BorderLayout.EAST);

        setPreferredSize(new Dimension(205, 30));

        add(new SampleInformationPane(), BorderLayout.CENTER);
        setBorder(null);
    }

    /**
     * Sets all list values to have a white background and green foreground.
     *
     * @param jList        - List to render
     * @param val          - value of list item being rendered.
     * @param index        - list index for value to be renderered.
     * @param selected     - is the value selected?
     * @param cellGotFocus - has the cell got focus?
     * @return - The CustomListCellRendered Component.
     */
    public Component getListCellRendererComponent(JList jList, Object val,
                                                  int index, boolean selected, boolean cellGotFocus) {

        //image.checkIsIdEntered(selected);
        Component[] components = getComponents();

        for (Component c : components) {

            c.setBackground(selected ? selectedBackgroundColor : Color.WHITE);

            if (c instanceof SampleInformationPane) {
                SampleInformationPane sampleInformationPane = (SampleInformationPane) c;

                if (val instanceof SampleInformation) {

                    SampleInformation sampleInformation = (SampleInformation) val;
                    sampleInformationPane.setSampleName(sampleInformation.getSampleName());
                    sampleInformationPane.setAdditionalInfo(sampleInformation.getAdditionalInformation());

                } else {
                    sampleInformationPane.setSampleName(val.toString());
                }

                sampleInformationPane.setSelected(selected);

            }

            if (selected) {
                c.setFont(UIHelper.VER_11_BOLD);
            } else {
                c.setFont(UIHelper.VER_11_PLAIN);
            }
        }

        return this;
    }

    class SampleInformationPane extends JPanel {

        private Color lessTransparentSampleName = new Color(141, 198, 63, 70);
        private Color lessTransparentAdditionInfo = new Color(0, 104, 56, 70);

        private JLabel sampleName;
        private JLabel additionalInfo;

        SampleInformationPane() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setOpaque(false);

            sampleName = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.LIGHT_GREEN_COLOR);
            additionalInfo = UIHelper.createLabel("", UIHelper.VER_8_PLAIN, UIHelper.DARK_GREEN_COLOR);
            additionalInfo.setSize(new Dimension(160, 10));

            add(sampleName);
            add(additionalInfo);
        }

        public void setSampleName(String sampleNameText) {
            sampleName.setText(sampleNameText);
        }

        public void setAdditionalInfo(String additionalInfoText) {
            additionalInfo.setText(additionalInfoText);
        }

        public void setSelected(boolean selected) {
            sampleName.setForeground(selected ? UIHelper.LIGHT_GREEN_COLOR : lessTransparentSampleName);
            additionalInfo.setForeground(selected ? UIHelper.DARK_GREEN_COLOR : lessTransparentAdditionInfo);

            selectedIconContainer.setIcon(selected ? selectedIcon : null);
        }
    }
}
