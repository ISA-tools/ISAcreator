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

package org.isatools.isacreator.mgrast.conceptmapper;


import org.isatools.isacreator.mgrast.model.ConfidenceLevel;
import org.isatools.isacreator.mgrast.model.FieldMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MGRastConceptMapper
 *
 * @author eamonnmaguire
 * @date Sep 22, 2010
 */


public class MGRastConceptMapper {

    private String mappingFileLocation = "Data" + File.separator + "metadata_list.xls";
    public static final String TEST_FILE = "Data" + File.separator + "air_test.txt";

    private Map<String, Set<String>> conceptsToMGRastTerm;

    private Set<String> allMgRastConcepts;

    private Pattern toFind;

    public MGRastConceptMapper() {
        createIndex();
    }

    private void createIndex() {
        MGRastConceptIndexer indexCreator = new MGRastConceptIndexer();

        File mgRastMappingFile = new File(mappingFileLocation);

        if (mgRastMappingFile.exists()) {
            // continue
            indexCreator.createIndex(mgRastMappingFile);
            conceptsToMGRastTerm = indexCreator.getConceptsToMGRastTerm();
            allMgRastConcepts = indexCreator.getAllConcepts();
        } else {
            System.err.println("No mapping file found at : " + mgRastMappingFile.getAbsolutePath());
        }
    }

    public FieldMapping getMGRastTermFromGSCTerm(String isaTerm, String checkListType) {

        String lcISATerm = isaTerm.toLowerCase();

        Set<String> tuplesForGSCTerm = null;

        for (String value : conceptsToMGRastTerm.keySet()) {
            // Since the mgrast ter

            // if the word length is too short we perform a direct match on the term. Since checking to see if
            // ph is contained in something like phenotype will not give us the correct result...
            if (value.length() > 4) {
                // Simplest case.
                if (lcISATerm.contains(value)) {
                    String term = interrogatePossibilitiesForCorrectTerm(
                            conceptsToMGRastTerm.get(value), checkListType);


                    return new FieldMapping(isaTerm, term, term.equals("") ? ConfidenceLevel.ZERO_PERCENT : ConfidenceLevel.SEVENTY_FIVE_PERCENT);
                }
                // replace or insert '_' | " " where applicable since some terms are represented
                // inconsistently. e.g.alkyl_diether could be matched by alkyl diether
                else if (replaceSpacesWithUnderscores(lcISATerm).contains(value) || replaceUnderscoresWithSpaces(lcISATerm).contains(value)) {

                    String term = interrogatePossibilitiesForCorrectTerm(
                            conceptsToMGRastTerm.get(value), checkListType);

                    return new FieldMapping(isaTerm, term, term.equals("") ? ConfidenceLevel.ZERO_PERCENT : ConfidenceLevel.SEVENTY_FIVE_PERCENT);
                }
                // else, when all else has failed, if the value has more than one word, we build k-tuples of the strings
                // to check for presence of values
                else {
                    if (tuplesForGSCTerm == null) {
                        String tmpISATerm = cleanupString(isaTerm.toLowerCase());
                        tuplesForGSCTerm = buildKTuples(tmpISATerm);
                    }

                    for (String tuple : tuplesForGSCTerm) {
                        if (value.contains(tuple)) {

                            String term = interrogatePossibilitiesForCorrectTerm(
                                    conceptsToMGRastTerm.get(value), checkListType);

                            return new FieldMapping(isaTerm, term, term.equals("") ? ConfidenceLevel.ZERO_PERCENT : ConfidenceLevel.FIFTY_PERCENT);
                        }
                    }
                }

            } else {
                String tmpISATerm = cleanupString(isaTerm.toLowerCase());

                if (value.equalsIgnoreCase(tmpISATerm)) {

                    String term = interrogatePossibilitiesForCorrectTerm(
                            conceptsToMGRastTerm.get(value), checkListType);

                    return new FieldMapping(isaTerm, term, term.equals("") ? ConfidenceLevel.ZERO_PERCENT : ConfidenceLevel.SEVENTY_FIVE_PERCENT);
                }
            }
        }

        return new FieldMapping(isaTerm, "", ConfidenceLevel.ZERO_PERCENT);

    }

    private String replaceSpacesWithUnderscores(String toModify) {
        return toModify.replaceAll("\\s+", "_");
    }

    private String replaceUnderscoresWithSpaces(String toModify) {
        return toModify.replaceAll("_", "+");
    }

    private String cleanupString(String toClean) {
        return toClean.replaceAll("(characteristics\\[)|(factor value\\[)|(parameter value\\[)|(comment\\[)|\\]", "");
    }

    /**
     * Method builds all the permutations of a number of words, removing prepositions and definite/indefinite articles
     *
     * @param toPermutate String to split and move words around in
     * @return Set<String> representating all permutations.
     */
    private Set<String> buildKTuples(String toPermutate) {
        // prepare the String by removing all definite/indefinite articles and removing prepositions

        toPermutate = GrammarUtil.removeValuesFromString(toPermutate, DefiniteArticlesAndConjunctions.buildRegexForDetection());
        toPermutate = GrammarUtil.removeValuesFromString(toPermutate, Prepositions.buildRegexForDetection());

        String[] words = toPermutate.split("\\s+|_");

        Set<String> result = new HashSet<String>();

        for (int outerLoop = 0; outerLoop < words.length; outerLoop++) {
            for (int innerLoop = 0; innerLoop < words.length; innerLoop++) {
                // miss doubling the same word
                if (outerLoop != innerLoop) {
                    result.add(words[outerLoop] + " " + words[innerLoop]);
                }
            }
        }

        return result;
    }


    private String interrogatePossibilitiesForCorrectTerm(Set<String> possibleConcepts, String checkListType) {

        if (possibleConcepts != null) {
            if (possibleConcepts.size() == 1) {
                return possibleConcepts.iterator().next();
            } else {
                for (String candidateConcept : possibleConcepts) {

                    if (findChecklist(candidateConcept)) {
                        return candidateConcept;
                    }

                }
            }
        }

        return "";

    }

    private boolean findChecklist(String text) {
        if (toFind == null) {
            toFind = Pattern.compile("(air)|(sample-origin)|(sample-isolation)|(host)|(human)|(microbial)|(miscellaneous)|(plant)|(sediment)|(water)|(soil)");
        }

        Matcher m = toFind.matcher(text);
        return m.find();
    }

    private Set<String> createTestDataFromFile() {
        File testData = new File(TEST_FILE);

        Set<String> testTerms = new HashSet<String>();

        if (testData.exists()) {
            try {
                Scanner fileScanner = new Scanner(testData);

                while (fileScanner.hasNextLine()) {
                    String value = fileScanner.nextLine().trim().toLowerCase();
                    testTerms.add(value);
                }

            } catch (FileNotFoundException e) {
                System.err.println("File not found");
            }


        } else {
            System.err.println("Test data doesn't exist");

        }
        return testTerms;
    }


    public static void main(String[] args) {
        MGRastConceptMapper mapper = new MGRastConceptMapper();

        Set<String> testData = mapper.createTestDataFromFile();

        System.out.println("Testing with " + testData.size() + " terms");

        StringBuffer buffer = new StringBuffer();

        int failCount = 0;
        for (String testItem : testData) {
            FieldMapping mgRastTerm = mapper.getMGRastTermFromGSCTerm(testItem, "air");
            if (mgRastTerm.getConfidenceLevel() == ConfidenceLevel.ZERO_PERCENT) {
                failCount++;
            }
            buffer.append(testItem).append("\t").append(mgRastTerm).append("\n");
        }

        System.out.println("Success matched = " + (testData.size() - failCount) + "/" + testData.size());

        System.out.println();

        System.out.println(buffer.toString());

    }

    public Set<String> getAllMgRastConcepts() {
        return allMgRastConcepts;
    }
}
