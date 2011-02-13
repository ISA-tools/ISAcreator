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

package org.isatools.isacreator.formatmappingutility.loader;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author Eamonn Maguire
 * @date Apr 21, 2009
 */


public class CSVFileLoader implements FileLoader {
    private static final Logger log = Logger.getLogger(CSVFileLoader.class.getName());
    private char delimiter;
    private int readerUsed;


    public CSVFileLoader() {
        this(FileLoader.COMMA_DELIM);
    }

    public CSVFileLoader(char delimiter) {
        this.delimiter = delimiter;
    }

    public Map<String, String[]> getColumnNames(File f) {
        return processFile(f);
    }

    public char getDelimiter() {
        return delimiter;
    }

    /**
     * File should be checked to determine whether or not it is a directory. if not, then just load the single file
     *
     * @param f - Either a file or pointer to a directory!
     * @return Map<String, String[]> where key is the file name and the String[] is the list of column names
     */
    public Map<String, String[]> processFile(File f) {
        Map<String, String[]> result = new ListOrderedMap<String, String[]>();

        try {
            File[] files;
            if (f.isDirectory()) {
                files = f.listFiles();
            } else {
                files = new File[]{f};
            }

            for (File file : files) {

                if (!file.isHidden()) {
                    String[] firstLine;

                    CSVReader reader = new CSVReader(new FileReader(file), delimiter);

                    if (delimiter == FileLoader.COMMA_DELIM) {

                        readerUsed = FileLoader.CSV_READER_CSV;
                    } else {
                        readerUsed = FileLoader.CSV_READER_TXT;
                    }

                    firstLine = reader.readNext();

                    // clean up
                    for (int i = 0; i < firstLine.length; i++) {
                        firstLine[i] = firstLine[i].trim();
                    }

                    result.put(file.getPath(), firstLine);
                }
            }

        } catch (FileNotFoundException e) {
            log.error("file not found: " + e.getMessage());
        } catch (IOException e) {
            log.error("io exception occurred " + e.getMessage());
        }
        return result;

    }

    public int getReaderUsed() {
        return readerUsed;
    }
}
