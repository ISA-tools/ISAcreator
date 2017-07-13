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

package org.isatools.isacreator.formatmappingutility.loader;


import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreator.settings.ISAcreatorProperties;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eamonn Maguire
 * @date Apr 21, 2009
 */


public class ExcelFileLoader implements FileLoader {

    private Map<String, Sheet> sheetReaders = new HashMap<String, Sheet>();
    private static final Logger log = Logger.getLogger(CSVFileLoader.class.getName());

    public Map<String, String[]> getColumnNames(File f) {
        return processFile(f);
    }


    public Map<String, String[]> processFile(File f) {
        Map<String, String[]> columnHeaders = new ListOrderedMap<String, String[]>();
        Workbook w;
        try {
            if (!f.isHidden()) {
                w = Workbook.getWorkbook(f);
                // Get the first sheet
                for (Sheet s : w.getSheets()) {

                    int rowStart;
                    try {
                        rowStart = Integer.parseInt(ISAcreatorProperties.getProperty("isacreator.rowOffset")) - 1;
                        System.out.println("Row offset is: " + rowStart);
                    } catch (NumberFormatException nfe) {
                        System.out.println(nfe.getMessage());
                        rowStart = 0;
                    }

                    if (s.getRows() > 0) {

                        List<String> headersForSheet = new ArrayList<String>();
                        for (int j = 0; j < s.getColumns(); j++) {

                            String cellContents = s.getCell(j, rowStart).getContents();
                            if (!cellContents.trim().equals("")) {
                                headersForSheet.add(cellContents);
                            }
                        }

                        columnHeaders.put(s.getName(), headersForSheet.toArray(new String[headersForSheet.size()]));
                    }
                }
            }

        } catch (BiffException e) {
            log.error("problem with incoming file. We were unable to process it for this reason: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return columnHeaders;
    }

    public int getReaderUsed() {
        return FileLoader.SHEET_READER;
    }

    public Map<String, Sheet> getFileReaders() {
        return sheetReaders;
    }

}
