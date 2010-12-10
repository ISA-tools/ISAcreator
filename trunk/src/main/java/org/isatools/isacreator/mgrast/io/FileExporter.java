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

package org.isatools.isacreator.mgrast.io;

import org.apache.log4j.Logger;
import org.isatools.isacreator.mgrast.model.*;
import org.isatools.isacreator.mgrast.ui.ExtraMetaDataPane;
import org.isatools.isacreator.mgrast.utils.APIHook;
import org.isatools.isacreator.utils.PropertyFileIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * FileExporter
 *
 * @author eamonnmaguire
 * @date Oct 1, 2010
 */


public class FileExporter {

    private static final Logger log = Logger.getLogger(FileExporter.class.getName());
    public static final String MAPPING_HISTORY_FILE = "mgRastMappingHistory.properties";

    public FileExporter() {

    }

    public void outputFiles(File outputDirectory, Map<String, FieldMapping> fieldMappings, List<SampleExternalIds> sampleExtIds, ExtraMetaDataPane metaData, APIHook apiHook) {

        // try to map out files
        StringBuilder projectMetadata = new ProjectMetadataAdapter(metaData).getDataFromMetaDataPane();

        Map<String, Map<String, List<String>>> sampleToData = apiHook.getDataForSamples(apiHook.getSampleNames());


        // for each sample, we want to output 1 or more files (more in case of replicates...)
        for (SampleExternalIds seId : sampleExtIds) {
            Map<Integer, StringBuilder> samplesToOutput = new HashMap<Integer, StringBuilder>();

            StringBuilder sampleDescription = new StringBuilder();
            sampleDescription.append("project-description_metagenome_name").append("\t").append(seId.getSampleName()).append("\n");

            sampleDescription.append(projectMetadata);

            for (ExternalResources er : seId.getExternalIds().keySet()) {
                sampleDescription.append(er.getMgRastTerm()).append("\t").append(seId.getExternalIds().get(er)).append("\n");
            }

            for (String dataColumn : sampleToData.get(seId.getSampleName()).keySet()) {
                if (fieldMappings.containsKey(dataColumn)) {
                    // get mg_rast term if one exists. if no mapping exists, don't output it.
                    FieldMapping fm = fieldMappings.get(dataColumn);
                    if (fm.getConfidenceLevel() != ConfidenceLevel.ZERO_PERCENT) {
                        List<String> columnValues = sampleToData.get(seId.getSampleName()).get(dataColumn);

                        for (int valueCount = 0; valueCount < columnValues.size(); valueCount++) {
                            if (!samplesToOutput.containsKey(valueCount)) {
                                samplesToOutput.put(valueCount, new StringBuilder());
                            }

                            // todo look here at what happens
                            samplesToOutput.get(valueCount).append(fm.getMgRastTermMappedTo()).append("\t")
                                    .append(sampleToData.get(seId.getSampleName()).get(dataColumn).get(valueCount)).append("\n");
                        }
                    }
                }
            }

            for (int samples : samplesToOutput.keySet()) {
                outputFileToDirectory(outputDirectory, seId.getSampleName(), sampleDescription.append(samplesToOutput.get(samples)), samples);
            }
        }
    }

    private void outputFileToDirectory(File outputDirectory, String sampleName, StringBuilder output, int count) {

        File enclosingDir = new File(outputDirectory.getAbsolutePath() + File.separator + "MGRastExport");

        if (!enclosingDir.exists()) {
            enclosingDir.mkdir();
        }

        File newFile = new File(enclosingDir.getAbsolutePath() + File.separator + sampleName + (count == 0 ? "" : "-" + (count + 1)) + "-mgrast.txt");

        try {
            PrintStream ps = new PrintStream(newFile);

            ps.print(output.toString());

            ps.close();

        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
            log.error(e);
        }
    }

    public Properties loadMappingHistory() {
        Properties mappings = PropertyFileIO.loadSettings(MAPPING_HISTORY_FILE);
        if (mappings == null) {
            mappings = new Properties();
        }

        return mappings;
    }

    public void saveMappingHistory(Properties toSave) {
        PropertyFileIO.saveProperties(toSave, MAPPING_HISTORY_FILE);
    }


}
