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
package org.isatools.isacreator.effects.components;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 14/02/2011
 *         Time: 11:09
 */
public class RoundedJTextField extends JTextField {

    private Color backgroundColor;


    public RoundedJTextField(int columns) {
        this(columns, UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR);
    }

    public RoundedJTextField(int columns, Color backgroundColor) {
        super(columns);
        this.backgroundColor = backgroundColor;

        setOpaque(false);
        setSelectedTextColor(UIHelper.BG_COLOR);
        setCaretColor(UIHelper.DARK_GREEN_COLOR);
        setSelectionColor(UIHelper.LIGHT_GREEN_COLOR);

        setBorder(new EmptyBorder(3, 5, 3, 5));

    }

    public void setWarningMode() {
        backgroundColor = UIHelper.TRANSPARENT_RED_COLOR;
        setForeground(UIHelper.RED_COLOR);
        setSelectionColor(UIHelper.RED_COLOR);
        setCaretColor(UIHelper.BG_COLOR);
        setSelectedTextColor(UIHelper.BG_COLOR);
    }

    public void unsetWarningMode() {
        backgroundColor = UIHelper.TRANSPARENT_LIGHT_GREEN_COLOR;
        setForeground(UIHelper.DARK_GREEN_COLOR);
        setSelectedTextColor(UIHelper.BG_COLOR);
        setCaretColor(UIHelper.DARK_GREEN_COLOR);
        setSelectionColor(UIHelper.LIGHT_GREEN_COLOR);
    }


    @Override
    protected void paintComponent(Graphics graphics) {
        int width = getWidth() - getInsets().left - getInsets().right;
        int height = getHeight() - getInsets().top - getInsets().bottom;

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(backgroundColor);

        g2d.fillRoundRect(getInsets().left, getInsets().top, width, height, 8, 8);

        super.paintComponent(graphics);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
