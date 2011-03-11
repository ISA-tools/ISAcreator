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

package org.isatools.isacreator.configuration.io;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.configuration.*;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isacreator.utils.StringProcessing;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class uses the JaxB classes to load XML output from ISAcreator Config into TableConfiguration objects which
 * contain TableFieldObjects to describe the elements in a Table.
 *
 * @author Eamonn Maguire
 * @date Sep 21, 2009
 */


public class ConfigXMLParser {
    private String configDir;
    private List<MappingObject> mappings;
    private List<TableReferenceObject> tables;
    private String problemLog = "";
    private boolean problemsEncountered = false;

    public ConfigXMLParser(String configDir) {
        this.configDir = configDir;
        mappings = new ArrayList<MappingObject>();
        tables = new ArrayList<TableReferenceObject>();
    }


    public void loadConfiguration() {
        List<IsaTabConfigFileType> definitions;
        try {
            definitions = getTableDefinitions();
        } catch (Exception e) {
            problemLog += "<p> problem encountered loading configurations. please make sure the directory contains valid configuration files.";
            problemsEncountered = true;
            return;
        }

        for (IsaTabConfigFileType isa : definitions) {
            for (IsaTabConfigurationType doc : isa.getIsatabConfigurationArray()) {
                try {
                    processTable(doc);
                } catch (Exception e) {
                    problemLog += "<p> problem processing : " + doc.getTableName();
                    problemsEncountered = true;
                }
            }
        }

    }

    public List<TableReferenceObject> getTables() {
        return tables;
    }

    public List<MappingObject> getMappings() {
        return mappings;
    }

    private List<IsaTabConfigFileType> getTableDefinitions() {
        File dir = new File(configDir);
        File[] configFiles = dir.listFiles();

        List<IsaTabConfigFileType> configurations = new ArrayList<IsaTabConfigFileType>();
        for (File tableConfig : configFiles) {
            if (!tableConfig.getName().startsWith(".")) {
                IsatabConfigFileDocument newISAConfig = null;
                try {
                    newISAConfig = IsatabConfigFileDocument.Factory.parse(tableConfig);
                    IsaTabConfigFileType isa = newISAConfig.getIsatabConfigFile();
                    configurations.add(isa);
                } catch (XmlException e) {
                    problemsEncountered = true;
                    problemLog = "<p>Please ensure you have provided a directory containing only valid configuration xml produced by ISAcreator Configurator!</p>";

                } catch (IOException e) {
                    problemsEncountered = true;
                    problemLog = "<p>Problem encountered when reading files. Please ensure you have specified a valid directory!</p>";
                }
            }
        }

        return configurations;
    }

    private void processTable(IsaTabConfigurationType isaConf) {
        OntologyEntryType measurementInfo = isaConf.getMeasurement();
        OntologyEntryType technologyInfo = isaConf.getTechnology();
        String tableType = measurementInfo.getTermLabel().equalsIgnoreCase("[sample]") ? MappingObject.STUDY_SAMPLE : MappingObject.ASSAY_TYPE;

        MappingObject mo = new MappingObject(tableType, measurementInfo.getTermLabel(),
                measurementInfo.getSourceAbbreviation(), measurementInfo.getTermAccession(),
                technologyInfo.getTermLabel(), technologyInfo.getSourceAbbreviation(), technologyInfo.getTermAccession(),
                isaConf.getTableName());

        List<FieldObject> fields = new ArrayList<FieldObject>();
        Map<Integer, String[]> tableStructure = new HashMap<Integer, String[]>();

        int colNo = 0;
        int sequenceNumber = 0;
        while (sequenceNumber < getConfigurationFields(isaConf).length) {
            XmlObject obj = getConfigurationFields(isaConf)[sequenceNumber];
            // check what the obj is an instance of, and handle it accordingly!
            if (obj instanceof FieldType) {
                FieldType stdField = (FieldType) obj;
                FieldObject newField = new FieldObject(colNo, stdField.getHeader(), StringProcessing.cleanUpString(stdField.getDescription()), DataTypes.resolveDataType(stdField.getDataType()), stdField.getDefaultValue(),
                        stdField.getIsRequired(), stdField.getIsMultipleValue(),
                        stdField.getIsFileField());

                newField.setWizardTemplate(stdField.getGeneratedValueTemplate());

                if (stdField.getRecommendedOntologies() != null) {
                    processRecommendedOntologies(stdField, newField);
                }

                if (stdField.getValueRange() != null) {
                    ValidationTypes type = ValidationTypes.resolveDataType(stdField.getValueRange().getType().toString());
                    NumericValidation<Double> nv = new NumericValidation<Double>(type, 0.0, 0.0);

                    if (stdField.getValueRange().getMax() != null) {
                        nv.setUpperBounds(Double.valueOf(stdField.getValueRange().getMax()));
                    }

                    if (stdField.getValueRange().getMin() != null) {
                        nv.setUpperBounds(Double.valueOf(stdField.getValueRange().getMin()));
                    }

                    newField.setNumberValidation(nv);
                }

                if (stdField.getValueFormat() != null) {
                    newField.setInputFormat(stdField.getValueFormat());
                }

                if (stdField.getListValues() != null) {
                    String values = stdField.getListValues();

                    if (values.contains(",")) {
                        String[] valueList = values.split(",");
                        newField.setFieldList(valueList);
                    }
                }

                fields.add(newField);

                tableStructure.put(colNo, new String[]{newField.getFieldName(), newField.getWizardTemplate() == null ? "" : newField.getWizardTemplate()});
                colNo++;
            } else if (obj instanceof ProtocolFieldType) {
                ProtocolFieldType protocolField = (ProtocolFieldType) obj;
                FieldObject newField = new FieldObject(colNo, "Protocol REF", "", DataTypes.LIST, protocolField.getProtocolType(),
                        protocolField.getIsRequired(), false, false);
                newField.setWizardTemplate(newField.getWizardTemplate());

                fields.add(newField);

                tableStructure.put(colNo, new String[]{newField.getFieldName(), ""});
                colNo++;
            } else if (obj instanceof UnitFieldType) {
                UnitFieldType unitField = (UnitFieldType) obj;

                FieldObject newField = new FieldObject(colNo, "Unit", StringProcessing.cleanUpString(unitField.getDescription()), DataTypes.ONTOLOGY_TERM, "",
                        unitField.getIsRequired(), false, false);

                if (unitField.getRecommendedOntologies() != null) {
                    processRecommendedOntologies(unitField, newField);
                }

                fields.add(newField);

                tableStructure.put(colNo, new String[]{newField.getFieldName(), ""});
                colNo++;
            } else if (obj instanceof StructuredFieldType) {
                StructuredFieldType structuredField = (StructuredFieldType) obj;
                // we don't add it to the fields, but we do add it to the mapping object...
                tableStructure.put(colNo, new String[]{structuredField.getName(), ""});
                colNo++;
            }

            sequenceNumber++;
        }

        TableConfiguration tc = new TableConfiguration(mo, fields, tableStructure);

        mappings.add(mo);
        tables.add(new TableReferenceObject(tc));
    }

    private void processRecommendedOntologies(FieldType processing, FieldObject tfo) {
        for (OntologyType ot : processing.getRecommendedOntologies().getOntologyArray()) {
            processOntologyType(ot, tfo);
        }
    }

    private void processRecommendedOntologies(UnitFieldType processing, FieldObject tfo) {
        for (OntologyType ot : processing.getRecommendedOntologies().getOntologyArray()) {
            processOntologyType(ot, tfo);
        }
    }

    private void processOntologyType(OntologyType ot, FieldObject tfo) {
        RecommendedOntology ro = new RecommendedOntology(new Ontology(ot.getId(), ot.getVersion(),
                ot.getAbbreviation(), ot.getName()), null);
        // add branch!
        if (ot.getBranchArray() != null && ot.getBranchArray().length > 0) {
            BranchType branch = ot.getBranchArray(0);
            OntologyBranch ob = new OntologyBranch(branch.getId(), branch.getName());
            ro.setBranchToSearchUnder(ob);
        }
        tfo.addRecommendedOntologySource(ro);
    }

    private static XmlObject[] getConfigurationFields(IsaTabConfigurationType cfg) {
        return cfg.selectPath("./*");
    }

    public String getProblemLog() {
        return "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 10px;" +
                "   color: #BF1E2D;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">" +
                "<b>Problem loading configuration files</b>" + problemLog +
                "</body></html>";
    }

    public boolean isProblemsEncountered() {
        return problemsEncountered;
    }
}
