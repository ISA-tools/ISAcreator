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


package org.isatools.isacreator.spreadsheet;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps file names to actual files
 *
 * @author Eamonn Maguire
 * @date Jun 17, 2009
 */


public class FileLocationMapperUtil {

    /**
     * Given a bunch of file names, this method will iterate through the given directory and pull out the absolute paths
     * for files which it has found in the directory!
     *
     * @param relativePaths - list of files which need to have their links resolved
     * @param dirToSearch   - directory to search in to retrieve proper links
     * @return - Map containing the filename as it was mapped to the absolute name.
     */
    public Map<String, String> findProperFileLocations(String[] relativePaths, File dirToSearch) {
        Map<String, String> contents = indexDirectory(dirToSearch);

        Map<String, String> result = new HashMap<String, String>();

        for (String relPath : relativePaths) {
            // if the contents keyset (which is the filename) contains the
            if (contents.keySet().contains(strippedPath(relPath))) {
                // put the Full path as the value for the relPath link.
                result.put(relPath, contents.get(strippedPath(relPath)));
            }

        }

        return result;
    }

    /**
     * Strips a String containing Directory hierarchies to contain only the name...e.g. /Users/eamonnmaguire/hello.txt to
     * hello.txt
     *
     * @param toModify - String to be modified
     * @return Modified String
     */
    private String strippedPath(String toModify) {

        if (toModify.contains("/") || toModify.contains("\\")) {
            String separatorToUse = toModify.contains("\\") ? "\\" : "/";
            toModify = toModify.substring(toModify.lastIndexOf(separatorToUse) + 1);
            return toModify;
        }

        return toModify;
    }

    private Map<String, String> indexDirectory(File dirToSearch) {
        Map<String, String> dirContents = new HashMap<String, String>();
        if (dirToSearch.isDirectory()) {
            File[] files = dirToSearch.listFiles();

            for (File f : files) {
                if (f.isDirectory()) {
                    // just keep calling this method whenever a directory is found so that we can recursively delve
                    // into a Directory and ALL of its contents!
                    dirContents.putAll(indexDirectory(f));
                } else {
                    dirContents.put(f.getName(), f.getPath());
                }
            }
        }

        return dirContents;
    }
}
