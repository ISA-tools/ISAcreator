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

package org.isatools.isacreator.utils;

import java.text.BreakIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OntologyURLProcessing
 *
 * @author eamonnmaguire
 * @date Oct 27, 2010
 */


public class StringProcessing {


    /**
     * This method recursively replaces String substitution text with values found in an Array of Strings.
     * It is currently used when mapping formats into the ISAtab format by the Mapping utility and by the QR
     * Code generator when building up the Strings to be used as content for the QR code.
     *
     * @param substitution - e.g <<0>>-<<2>>
     * @param nextLine     - e.g. an Array of Strings with contents 0:sample1;1:homo sapiens;2:24 years old
     * @return for the example displayed above, we'd get the String sample1-24 years old from the algorithm.
     */
    public static String processSubstitutionString(String substitution, String[] nextLine) {
        // string will be in the format <<1>>-<<14>>. need to extract the numbers!

        if (substitution.contains("<<")) {
            String pattern = "(<<[0-9]+>>){1}";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(substitution);

            if (m.find()) {
                int startIndex = m.start();
                int endIndex = m.end();

                // we get the number in between the << >> so we add 2 to the start index and subtract 2 from the end index
                int colIndex = Integer.valueOf(substitution.substring(startIndex + 2, endIndex - 2));

                String part1 = substitution.substring(0, startIndex);
                String part2 = substitution.substring(endIndex);

                substitution = part1 + nextLine[colIndex] + part2;
                // recursively call this method since direct substitutions inside the first string will change its
                // length and therefore destroy String index locations...
                return processSubstitutionString(substitution, nextLine);
            }
        }
        return substitution;
    }

    /**
     * Cleans up the String to remove quotes and incorrect spaces
     *
     * @param toClean - String to be cleaned
     * @return Clean String
     */
    public static String cleanUpString(String toClean) {
        // replace " with nothing
        if (toClean == null) {
            toClean = "";
            return toClean;
        }

        toClean = toClean.replaceAll("\"", "");

        //replace one or more spaces with just one space
        toClean = toClean.replaceAll("[\\s]+", " ");

        // remove all trailing spaces
        toClean = toClean.trim();

        return toClean;
    }

    /**
     * Extracts the value between [ & ]. Used to extract the qualifier from Factor Values, Comments, etc.
     *
     * @param value - String, e.g. Comment[Publication author affiliation]
     * @return Qualifier in between the square brackets
     */
    public static String extractQualifierFromField(String value) {
        if (value.contains("[") && value.contains("]")) {
            String tmpValue = value.substring(value.indexOf("[") + 1);
            return tmpValue.replaceAll("]", "").trim();
        } else {
            return value;
        }
    }

    /**
     * From a Comment [qualifier] for example, this method will remove any spaces
     * between the Comment and [qualifier] so that everything is consistent in the
     * interface
     * @param fieldName field to check and split
     * @return modified field
     */
    public static String removeSpaceFromQualifiedField(String fieldName) {
        if(fieldName.contains("[")) {
            return fieldName.substring(0,fieldName.indexOf("[")).trim() + fieldName.substring(fieldName.indexOf("["));
        }
        return fieldName;
    }

    public static String convertStringToTitleCase(String toConvert) {
        BreakIterator wordBreaker = BreakIterator.getWordInstance();
        wordBreaker.setText(toConvert);
        int end;

        String word = "";
        for (int start = wordBreaker.first();
             (end = wordBreaker.next()) != BreakIterator.DONE; start = end) {

            word += StringProcessing.wordToTitleCase(toConvert.substring(start, end));

        }

        return word;

    }

    private static String wordToTitleCase(String word) {
        String result = "";
        for (int i = 0; i < word.length(); i++) {
            String next = word.substring(i, i + 1);
            if (i == 0) {
                result += next.toUpperCase();
            } else {
                result += next;
            }
        }
        return result;
    }

    public static boolean isURL(String toCheck) {
        return toCheck.matches("^(https?|ftp|file).*");
    }

    public static void main(String[] args) {
        String test = "http://www.bioassayontology.org/bao#BAO_0000020";

        boolean result = StringProcessing.isURL(test);

        System.out.println(result);
    }
}
