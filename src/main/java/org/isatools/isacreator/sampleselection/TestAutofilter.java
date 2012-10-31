package org.isatools.isacreator.sampleselection;
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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.spreadsheet.CustomTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class TestAutofilter extends JFrame {

    public TestAutofilter() {

    }

    public void createGUI() {
        setTitle("Test");
        setSize(new Dimension(400, 400));

        addTestTable();

        pack();
        setVisible(true);
    }

    private void addTestTable() {
        String[][] data = new String[50][3];

        DefaultTableModel model = new DefaultTableModel(data, new String[]{"Source Name", "Protocol REF", "Sample Name"});

        CustomTable table = new CustomTable(model);
//
//        List<SampleInformation> sampleInformationList = new ArrayList<SampleInformation>();
//        sampleInformationList.add(new SampleInformation(1, "sample 1", Collections.singletonMap("organism", "homo sapiens"), new HashMap<String, Integer>()));
//        sampleInformationList.add(new SampleInformation(2, "sample 2", Collections.singletonMap("organism", "homo sapiens"), new HashMap<String, Integer>()));
//        sampleInformationList.add(new SampleInformation(3, "sample 3", Collections.singletonMap("organism", "homo sapiens"), new HashMap<String, Integer>()));
//        sampleInformationList.add(new SampleInformation(4, "sample 4", Collections.singletonMap("organism", "homo sapiens"), new HashMap<String, Integer>()));
//        sampleInformationList.add(new SampleInformation(5, "sample 5", Collections.singletonMap("organism", "homo sapiens"), new HashMap<String, Integer>()));
//        sampleInformationList.add(new SampleInformation(6, "sample 6", Collections.singletonMap("organism", "homo sapiens"), new HashMap<String, Integer>()));
//
//        table.getColumnModel().getColumn(2).setCellEditor(new MockSampleSelectorCellEditor(sampleInformationList));


        JScrollPane scroller = new JScrollPane(table,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        add(scroller, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        new TestAutofilter().createGUI();
    }
}
