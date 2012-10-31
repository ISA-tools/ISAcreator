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

package org.isatools.isacreator.ftpupload;

import java.io.FileInputStream;
import java.io.OutputStream;

public class FTPupload {
    protected FTPClient ftpClient;
    public final String localfile;
    public final String targetfile;

    public FTPupload(String host, String username, String password,
                     String localfile, String targetfile) {
        ftpClient = new FTPClient(host, username, password);
        this.localfile = localfile;
        this.targetfile = targetfile;
        doit();
    }

    public FTPupload(String host, String username, String password, String file) {
        ftpClient = new FTPClient(host, username, password);
        localfile = file;
        targetfile = file;
        doit();
    }

    protected void doit() {
        try {
            OutputStream os = ftpClient.openUploadStream(targetfile);
            FileInputStream is = new FileInputStream(localfile);
            byte[] buf = new byte[16384];
            int c;

            while (true) {
                //System.out.print(".");
                c = is.read(buf);

                if (c <= 0) {
                    break;
                }

                //System.out.print("[");
                os.write(buf, 0, c);

                //System.out.print("]");
            }

            os.close();
            is.close();
            ftpClient.close(); // section 3.2.5 of RFC1738
        } catch (Exception E) {
            System.err.println(E.getMessage());
            E.printStackTrace();
        }
    }
}
