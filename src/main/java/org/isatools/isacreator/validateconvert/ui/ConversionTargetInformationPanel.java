package org.isatools.isacreator.validateconvert.ui;
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

import org.isatools.errorreporter.ui.borders.RoundedBorder;
import org.isatools.errorreporter.ui.utils.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConversionTargetInformationPanel extends JPanel {

    private Color hoverColor = new Color(249, 249, 249);

    @InjectedResource
    private ImageIcon unselected, selected;

    private JLabel selectionIndicator;
    private ConversionTarget conversionTarget;


    public ConversionTargetInformationPanel(ConversionTarget conversionTarget) {

        ResourceInjector.get("validateconvert-package.style").inject(this);

        this.conversionTarget = conversionTarget;

        createGUI();
    }

    public ConversionTarget getConversionTarget() {
        return conversionTarget;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 5));

        add(createTopSection(), BorderLayout.NORTH);
        add(createAssayInfoSection(), BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                setBackground(conversionTarget.isSelected() ? hoverColor : UIHelper.BG_COLOR);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                setBackground(hoverColor);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                firePropertyChange("conversionTargetSelected", false, true);
                conversionTarget.setSelected(!conversionTarget.isSelected());
                updateSelection();
            }
        });

    }

    private Container createTopSection() {
        Box topSection = Box.createHorizontalBox();

        selectionIndicator = new JLabel();
        updateSelection();

        topSection.add(selectionIndicator);
        topSection.add(Box.createHorizontalStrut(5));
        topSection.add(UIHelper.createLabel(conversionTarget.getTarget().getType(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));

        return topSection;
    }

    public void clearSelection() {
        conversionTarget.setSelected(false);
        updateSelection();
    }

    private Container createAssayInfoSection() {
        Box infoPane = Box.createVerticalBox();
        infoPane.setPreferredSize(new Dimension(130, 35));
        infoPane.add(Box.createVerticalStrut(5));
        infoPane.add(UIHelper.createLabel("<html>conversion possible on "
                + conversionTarget.getNumValidAssays() + " assay type" + (conversionTarget.getNumValidAssays() > 1 ? "s" : "") + "</html>",
                UIHelper.VER_8_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));

        infoPane.setBorder(new EmptyBorder(2, 1, 2, 1));
        return infoPane;
    }

    public void updateSelection() {
        selectionIndicator.setIcon(conversionTarget.isSelected() ? selected : unselected);
    }
}
