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

import org.isatools.isacreator.formatmappingutility.exceptions.MultipleExtensionsException;
import org.isatools.isacreator.formatmappingutility.exceptions.NoAvailableLoaderException;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Loads Excel files, CSV or TAB delimited files into the ISAcreator mapping utility to determine column headers
 *
 * @author Eamonn Maguire
 * @date Apr 20, 2009
 */

public class Loader {

    private String recordType = "";
    private int readerToUse;

    public String getRecordType() {
        return recordType;
    }

    public Map<String, String[]> loadFile(File f) throws NoAvailableLoaderException, MultipleExtensionsException {

        FileLoader fl = getAppropriateLoader(f);
        if (fl != null) {

            Map<String, String[]> columnnNames = fl.getColumnNames(f);
            readerToUse = fl.getReaderUsed();

            return columnnNames;
        }
        return null;
    }

    public int getReaderToUse() {
        return readerToUse;
    }

    private FileLoader getAppropriateLoader(File f) throws MultipleExtensionsException, NoAvailableLoaderException {
        Set<String> extensions = new HashSet<String>();
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                if (!file.isHidden()) {
                    extensions.add(file.getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase());
                }
            }
        } else {
            extensions.add(f.getName().substring(f.getName().lastIndexOf(".") + 1).toLowerCase());
        }

        if (extensions.size() > 1) {
            throw new MultipleExtensionsException("The files supplied in the directory do not have the same extension: detected " + extensions.size() + " different formats!");
        } else if (extensions.size() == 1) {
            // pop out first item!
            String extension = extensions.toArray(new String[extensions.size()])[0];

            if (extension.equals(FileLoader.CSV_EXT)) {
                recordType = "Comma Separated (CSV) Files";
                return new CSVFileLoader(FileLoader.COMMA_DELIM);
            } else if (extension.equals(FileLoader.XLS_EXT)) {
                recordType = "Excel (XLS) sheets";
                return new ExcelFileLoader();
            } else if (extension.equals(FileLoader.XLSX_EXT)) {
                recordType = "Excel (XLSX) sheets";
                throw new NoAvailableLoaderException(".xslx (MS Excel 2010 format) file is not supported for your file " + f.getName());
            } else if (extension.equals(FileLoader.TXT_EXT)) {
                recordType = "TAB Delimited (TEXT) files";
                return new CSVFileLoader(FileLoader.TAB_DELIM);
            } else {
                throw new NoAvailableLoaderException("No file loader found for " + f.getName());
            }
        }

        return null;
    }

}
