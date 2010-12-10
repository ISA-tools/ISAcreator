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

package org.isatools.isacreator.formatmappingutility;

import org.isatools.isacreator.common.Utils;

import java.io.*;
import java.util.List;
import java.util.Map;


/**
 * GenerateMappingView creates the XML required to display the graph showing which mappings have been performed
 * from ISAtab elements to incoming file data columns.
 */

public class GenerateMappingView {
    private String fileName;
    private Map<MappingField, List<String>> mappings;

    public GenerateMappingView(String fileName, Map<MappingField, List<String>> mappings) {
        this.fileName = fileName;
        this.mappings = mappings;
    }

    public String generateView() {
        // construct XML file from investigation
        Utils.createDir("Data");

        File f = new File("Data" + File.separator + "mapping_view.xml");
        PrintStream ps = null;

        try {
            ps = new PrintStream(new FileOutputStream(f));

            if (mappings != null) {
                ps.println("<tree>");
                ps.println(getDeclaration());
                ps.println(getMappingDetails());

                for (MappingField s : mappings.keySet()) {
                    ps.println(processMapping(s));
                }

                ps.println("</branch>\n</tree>");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return f.getAbsolutePath();
    }

    private String getDeclaration() {
        return "<declarations>\n" +
                "   <attributeDecl name=\"type\" type=\"String\"/>\n" +
                "   <attributeDecl name=\"name\" type=\"String\"/>\n" +
                " </declarations>";
    }

    private String getMappingDetails() {
        String tmpFileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
        return "<branch>\n" +
                "<attribute name = \"type\" value = \"File Name\"/>" +
                "<attribute name = \"name\" value = \"" + tmpFileName + "\"/>\n";
    }

    private String processMapping(MappingField key) {
        String mappingInfo = "<branch>" +
                "<attribute name = \"type\" value = \"Study\"/>" +
                "<attribute name=\"name\" value= \"" + key.getFieldName() + "\"/>\n";

        for (String mapping : mappings.get(key)) {

            String processedMapping;
            try {
                processedMapping = new String(mapping.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                processedMapping = mapping;
            }

            mappingInfo += ("<leaf>\n" +
                    "<attribute name = \"type\" value = \"Assay\"/>" +
                    "<attribute name=\"name\" value= \"" + processedMapping + "\"/>" +
                    "\n</leaf>");
        }
        mappingInfo += "</branch>";

        return mappingInfo;
    }
}
