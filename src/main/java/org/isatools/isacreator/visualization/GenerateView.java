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

package org.isatools.isacreator.visualization;

import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.visualization.tree.TreeView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class GenerateView {

    public File generateView(Investigation inv) {
        // construct XML file from investigation
        File treeFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "view.xml");
        PrintStream ps = null;

        try {
            ps = new PrintStream(new FileOutputStream(treeFile));

            if (inv != null) {
                ps.println("<tree>");
                ps.println(getDeclaration());
                ps.println(getInvestigationDetails());

                for (Study s : inv.getStudies().values()) {
                    ps.println(processStudy(s));
                }

                ps.println("</branch>\n</tree>");
            }
            return treeFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return null;
    }

    public File generateView(Study s) {
        // construct XML file from investigation

        File treeFile = new File(System.getProperty("java.io.tmpdir") + File.separator + "view.xml");
        PrintStream ps = null;

        try {
            ps = new PrintStream(new FileOutputStream(treeFile));

            if (s != null) {
                ps.println("<tree>");
                ps.println(getDeclaration());


                ps.println(processStudy(s));


                ps.println("</tree>");
            }
            return treeFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return null;
    }

    private String getDeclaration() {
        return "<declarations>\n" +
                "   <attributeDecl name=\"type\" type=\"String\"/>\n" +
                "   <attributeDecl name=\"" + TreeView.NAME_STRING + "\" type=\"String\"/>\n" +
                " </declarations>";
    }

    private String getInvestigationDetails() {
        return "<branch>\n" +
                "<attribute name = \"type\" value = \"Investigation\"/>" +
                "<attribute name = \"" + TreeView.NAME_STRING + "\" value = \"Investigation" + "\"/>\n";
    }

    private String processStudy(Study s) {
        String studyInfo = "<branch>" +
                "<attribute name = \"type\" value = \"Study\"/>" +
                "<attribute name=\"" + TreeView.NAME_STRING + "\" value= \"" + s.getStudyId() + "\"/>\n";

        for (Assay a : s.getAssays().values()) {

            StringBuilder nameSt = new StringBuilder(a.getAssayReference() + " (" + a.getMeasurementEndpoint());
            if (!a.getTechnologyType().equals("")) {
                nameSt.append(" using ").append(a.getTechnologyType());
            }
            nameSt.append(")");

            studyInfo += ("<leaf>\n" +
                    "<attribute name = \"type\" value = \"Assay\"/>" +
                    "<attribute name=\"" + TreeView.NAME_STRING + "\" value= \"" + nameSt + "\"/>" +
                    "\n</leaf>");
        }

        studyInfo += "</branch>";

        return studyInfo;
    }
}
