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

package org.isatools.isacreator.io.importisa;

import au.com.bytecode.opencsv.CSVReader;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationSection;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationStructureLoader;
import org.isatools.isacreator.utils.StringProcessing;
import org.isatools.isacreator.utils.datastructures.SetUtils;
import uk.ac.ebi.utils.collections.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2011
 *         Time: 11:26
 */
public class InvestigationImport {

    private static final Character TAB_DELIM = '\t';

    private List<ErrorMessage> messages;


    public InvestigationImport() {
        messages = new ArrayList<ErrorMessage>();
    }

    /**
     * imports the Investigation file into a Map data structure.
     *
     * @param investigationFile- File object representing Investigation file to be loaded.
     * @return Map is formatted like so:
     *         (Main section name) e.g. Investigation-1
     *         -> Section name e.g. InvestigationFileSection e.g. InvestigationFileSection.ONTOLOGY_SECTION
     *         -> Label for the ontology section e.g. Term Source Name
     *         -> Values for the given section/label e.g. OBI
     *         EFO
     *         etc
     */
    public Pair<Boolean, OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>>> importInvestigationFile(File investigationFile) throws IOException {

        OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>> importedInvestigationFile = new ListOrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>>();

        List<String[]> investigationFileContents = loadFile(investigationFile);

        String currentMajorSection = "Investigation-1";

        int studyCount = 1;

        importedInvestigationFile.put(currentMajorSection, new ListOrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>());

        InvestigationFileSection currentMinorSection = null;

        for (String[] line : investigationFileContents) {
            InvestigationFileSection tmpSection;

            if ((tmpSection = InvestigationFileSection.convertToInstance(line[0])) != null) {

                currentMinorSection = tmpSection;

                if (currentMinorSection == InvestigationFileSection.STUDY_SECTION) {
                    currentMajorSection = "Study-" + studyCount;
                    studyCount++;

                    importedInvestigationFile.put(currentMajorSection, new ListOrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>());
                }

                importedInvestigationFile.get(currentMajorSection).put(currentMinorSection, new ListOrderedMap<String, List<String>>());

            } else {

                String lineLabel = line[0].trim();

                if (!StringUtils.isEmpty(lineLabel)) {


                    String valueToTitleCase = lineLabel;
                    if (lineLabel.contains("Comment"))
                        valueToTitleCase = StringProcessing.removeSpaceFromQualifiedField(StringProcessing.convertStringToTitleCase(lineLabel));

                    //System.out.println(valueToTitleCase);
                    if (!importedInvestigationFile.get(currentMajorSection).get(currentMinorSection).containsKey(valueToTitleCase)) {
                        importedInvestigationFile.get(currentMajorSection).get(currentMinorSection).put(valueToTitleCase, new ArrayList<String>());
                    }

                    if (line.length > 1) {
                        for (int index = 1; index < line.length; index++) {
                            importedInvestigationFile.get(currentMajorSection).get(currentMinorSection).get(valueToTitleCase).add(line[index]);
                        }
                    }
                }

            }
        }

        return new Pair<Boolean, OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>>>(
                isValidInvestigationSections(importedInvestigationFile), importedInvestigationFile);
    }

    /**
     * Checks to make sure all required Investigation file sections have been entered
     *
     * @param investigationFile - Map containing investigation file structure
     * @return - true if valid, false otherwise.
     */
    private boolean isValidInvestigationSections(OrderedMap<String, OrderedMap<InvestigationFileSection, OrderedMap<String, List<String>>>> investigationFile) {

        InvestigationStructureLoader loader = new InvestigationStructureLoader();
        Map<InvestigationFileSection, InvestigationSection> sections = loader.loadInvestigationStructure();

        MessageFormat fmt = new MessageFormat("The field {0} is missing from the {1} section of the investigation file");

        for (String mainSection : investigationFile.keySet()) {

            // checking major section, e.g. study or investigation
            Set<InvestigationFileSection> majorSectionParts = new HashSet<InvestigationFileSection>();

            for (InvestigationFileSection section : investigationFile.get(mainSection).keySet()) {
                majorSectionParts.add(section);

                Set<String> minorSectionParts = new HashSet<String>();
                // we also want to check if the salient information for each minor section is in place.
                for (String sectionLabelsAndValues : investigationFile.get(mainSection).get(section).keySet()) {
                    minorSectionParts.add(sectionLabelsAndValues.toLowerCase());
                }

                SetUtils<String> setUtils = new SetUtils<String>();

                Set<String> requiredValuesAsLowercase = setUtils.getLowerCaseSetContents(sections.get(section).getRequiredValues());

                Pair<Boolean, Set<String>> equalityResult = setUtils.compareSets(minorSectionParts, requiredValuesAsLowercase, false);
                if (!equalityResult.fst) {
                    for (String sectionValue : equalityResult.snd) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, fmt.format(new Object[]{sectionValue, section})));
                    }
                }
                // check minor section for salient information
            }

            // check major section for salient information

            // the mainsection string is investigation-1 or study-2 - here we strip away from - onwards.
            Set<InvestigationFileSection> requiredSections = loader.getRequiredSections(mainSection.substring(0, mainSection.lastIndexOf("-")));
            SetUtils<InvestigationFileSection> setUtils = new SetUtils<InvestigationFileSection>();
            Pair<Boolean, Set<InvestigationFileSection>> equalityResult = setUtils.compareSets(majorSectionParts, requiredSections, true);

            // if false,
            if (!equalityResult.fst) {
                if (equalityResult.snd != null) {
                    for (InvestigationFileSection section : equalityResult.snd) {
                        messages.add(new ErrorMessage(ErrorLevel.ERROR, fmt.format(new Object[]{section, mainSection.substring(0, mainSection.lastIndexOf("-"))})));
                    }
                } else {
                    messages.add(new ErrorMessage(ErrorLevel.ERROR, "Incorrect number of sections defined for " + mainSection.substring(0, mainSection.lastIndexOf("-"))));
                }
            }
        }

        // if the message size is 0, there are no errors...
        return messages.size() == 0;
    }

    private List<String[]> loadFile(File investigationFile) throws IOException {
        List<String[]> fileContents = new ArrayList<String[]>();
        if (investigationFile.exists()) {
            CSVReader csvReader = new CSVReader(new FileReader(investigationFile), TAB_DELIM);

            String[] line;
            while((line = csvReader.readNext()) != null) {
                if(line.length > 0) {
                    if(!line[0].startsWith("#")) {
                        fileContents.add(line);
                    }
                }
            }

            return fileContents;
        } else {
            throw new FileNotFoundException("The specified file " + investigationFile.getName() + "does not exist in " + investigationFile.getAbsolutePath());
        }
    }

    public List<ErrorMessage> getMessages() {
        return messages;
    }
}
