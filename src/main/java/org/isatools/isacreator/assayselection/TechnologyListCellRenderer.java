package org.isatools.isacreator.assayselection;

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

import org.isatools.errorreporter.model.FileType;
import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class TechnologyListCellRenderer implements ListCellRenderer {

    @InjectedResource
    private ImageIcon microarray, microarrayUnselected, ms, msUnselected, nmr, nmrUnselected, uhts, uhtsUnselected,
            generic, genericUnselected, gelElec, gelElecUnselected, flowCyt, flowCytUnselected;

    private JPanel contents;
    private JLabel icon;
    private JLabel text;

    public TechnologyListCellRenderer() {
        ResourceInjector.get("assayselection-package.style").inject(this);

        contents = new JPanel(new BorderLayout());
        contents.setOpaque(true);

        icon = new JLabel();
        text = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR);

        contents.add(icon, BorderLayout.WEST);
        contents.add(text, BorderLayout.CENTER);
        contents.setBorder(new EmptyBorder(2, 2, 2, 2));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {

        String technology = value.toString().toLowerCase();

        if (technology.contains(FileType.MICROARRAY.getType())) {
            icon.setIcon(selected ? microarray : microarrayUnselected);
        } else if (technology.contains(FileType.MASS_SPECTROMETRY.getType())) {
            icon.setIcon(selected ? ms : msUnselected);
        } else if (technology.contains(FileType.NMR.getType())) {
            icon.setIcon(selected ? nmr : nmrUnselected);
        } else if (technology.contains(FileType.FLOW_CYTOMETRY.getType())) {
            icon.setIcon(selected ? flowCyt : flowCytUnselected);
        } else if (technology.contains(FileType.GEL_ELECTROPHORESIS.getType())) {
            icon.setIcon(selected ? gelElec : gelElecUnselected);
        } else if (technology.contains(FileType.SEQUENCING.getType())) {
            icon.setIcon(selected ? uhts : uhtsUnselected);
        } else {
            icon.setIcon(selected ? generic : genericUnselected);
        }

        text.setText(value.toString());

        text.setFont(selected ? UIHelper.VER_11_BOLD : UIHelper.VER_11_PLAIN);


        return contents;
    }
}
