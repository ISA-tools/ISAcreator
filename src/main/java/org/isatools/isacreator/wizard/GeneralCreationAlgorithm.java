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

package org.isatools.isacreator.wizard;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.formatmappingutility.utils.TableReferenceObjectWrapper;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.GeneralFieldTypes;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Class needs to provide a general implementation of the algorithm for ALL technologies. Will involve capturing the
 * Extract names (e.g. Liver, Heart, and so forth) and creating sample data based on this only.
 */

public class GeneralCreationAlgorithm extends CreationAlgorithm {

    @InjectedResource
    private ImageIcon addRecordIcon, removeIcon;

    private Assay assay;
    private Map<Integer, TreatmentReplicate> treatmentGroups;
    private JCheckBox labelUsed;
    private TableReferenceObject buildingModel;
    private String sourceNameFormat;
    private ExtractDetailsCapture extract;
    private LabelCapture labelCapture;
    private List<ExtractDetailsCapture> extractDetails;
    private Map<String, GeneratedSampleDetails> sampleInfo;
    private String institution;


    public GeneralCreationAlgorithm(Study study, Assay assay, List<PropertyType> factorsToAdd,
                                    Map<Integer, TreatmentReplicate> treatmentGroups,
                                    TableReferenceObject buildingModel, String institution, String sourceNameFormat) {
        super(buildingModel, study, factorsToAdd);
        this.assay = assay;
        this.treatmentGroups = treatmentGroups;

        this.buildingModel = buildingModel;
        this.sourceNameFormat = sourceNameFormat;

        ResourceInjector.get("wizard-package.style").inject(this);

        extractDetails = new ArrayList<ExtractDetailsCapture>();
        sampleInfo = new ListOrderedMap<String, GeneratedSampleDetails>();
        this.institution = institution;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setBackground(UIHelper.BG_COLOR);
                add(instantiatePanel());
                setVisible(true);
            }
        });
    }

    public Map<String, GeneratedSampleDetails> getSampleData() {
        return sampleInfo;
    }

    public void performAssayCentricTask() {

        // run the algorithm using all gathered information!
        int startReplicate = 1;
        // numReplicates = poolingPerformed.isSelected() ? 1 : numReplicates;

        String row = "";

        Vector<String> headersForReferenceObject = new Vector<String>();
        List<String> headersAsArray = new ArrayList<String>();
        headersForReferenceObject.add(TableReferenceObject.ROW_NO_TEXT);

        for (String header : headers) {
            if (!header.equals("characteristics")) {
                headersForReferenceObject.add(header);
                headersAsArray.add(header);
            }
        }

        buildingModel.setPreDefinedHeaders(headersForReferenceObject);

        int maxSamples = calculateMaxRepliates(treatmentGroups);

        for (int groups = 0; groups < treatmentGroups.size(); groups++) {
            for (ExtractDetailsCapture extractField : extractDetails) {

                for (int replicates = startReplicate; replicates <= treatmentGroups.get(groups).getNumReplicates();
                     replicates++) {

                    for (int columnIndex : colsToUse) {
                        if (!tableStructure.get(columnIndex)[0].toLowerCase().equals("characteristics")) {
                            String nextDataToAdd = tableStructure.get(columnIndex)[1]; // try and insert a template item

                            if (nextDataToAdd.trim().equals("")) {
                                // then we are dealing with a protocol, factor, or characteristic
                                if (tableStructure.get(columnIndex)[0].toLowerCase()
                                        .equals("factors")) {

                                    row += treatmentGroups.get(groups).getTreatmentGroup();
                                } else if (tableStructure.get(columnIndex)[0].toLowerCase()
                                        .equals("label")) {

                                    String val = labelUsed.isSelected() ? labelCapture.getLabelName() : "";

                                    row += val + "\t";
                                } else if (tableStructure.get(columnIndex)[0].equals(GeneralFieldTypes.PROTOCOL_REF.name)) {
                                    // we have a protocol. Do two things: set value to it's default value and add it to the study protocols
                                    row += buildingModel.getDefaultValue(columnIndex + 1) + "\t";
                                } else {
                                    // just empty row data
                                    row += (nextDataToAdd + "\t");
                                }
                            } else {
                                row += (nextDataToAdd + "\t");
                            }
                        }
                    }

                    String extractName = extractField.getPoolingPerformed() ? extractField.getExtractName() + ".Pooled" : extractField.getExtractName();

                    String shortExtractName = extractName;
                    if (extractName.contains(":")) {
                        // remove ontology source from the extract name
                        shortExtractName = extractName.substring(extractName.indexOf(":") + 1);
                    }

                    addRow(row, headersAsArray, groups, replicates, extractName, shortExtractName, maxSamples);

                    // reset variables for next iteration
                    row = "";
                }
            }
        }

        TableReferenceObjectWrapper troAdapter = new TableReferenceObjectWrapper(buildingModel);
        troAdapter.setConstructProtocolsWithDefaultValues(true);
        List<Protocol> protocols = troAdapter.findProtocols();

        study.getProtocols().addAll(protocols);

        // add extract statement for reference sample addition.
        assay.setTableReferenceObject(buildingModel);
    }

    private void addRow(String row, List<String> headersAsArray, int groups, int replicates, String extractName, String shortExtractName, int totalSamples) {

        String groupNo = padNumericString(treatmentGroups.size(), groups + 1);
        String subjectNo = padNumericString(totalSamples, replicates);


        row = replaceStringModelValues(row, institution, groupNo,
                subjectNo, shortExtractName, 1, "");


        String sampleName = replaceStringModelValues(buildingModel.getColumnFormatByName("sample name"),
                institution, groupNo, subjectNo, shortExtractName, 1, "");

        String sourceName = replaceStringModelValues(sourceNameFormat,
                institution, groupNo, subjectNo, shortExtractName, 1, "");

        sampleInfo.put(sampleName, new GeneratedSampleDetails(extractName, sourceName, treatmentGroups.get(groups).getTreatmentGroup()));

        buildingModel.addRowData(headersAsArray.toArray(new String[headersAsArray.size()]),
                row.split("\t"));
    }

    public JPanel instantiatePanel() {
        final JPanel generalQuestionCont = new JPanel();
        generalQuestionCont.setLayout(new BoxLayout(generalQuestionCont,
                BoxLayout.PAGE_AXIS));
        generalQuestionCont.setBackground(UIHelper.BG_COLOR);


        JLabel info = new JLabel("<html><b>" + assay.getMeasurementEndpoint() +
                "</b> using <b>" + assay.getTechnologyType() + "</b></html>",
                JLabel.LEFT);
        UIHelper.renderComponent(info, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, false);

        if (assay.getTechnologyType().equals("")) {
            info.setText("<html><b>" + assay.getMeasurementEndpoint() +
                    "</html>");
        }

        info.setPreferredSize(new Dimension(300, 40));

        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.setBackground(UIHelper.BG_COLOR);

        infoPanel.add(info);

        generalQuestionCont.add(infoPanel);

        JPanel labelPanel = new JPanel(new GridLayout(1, 2));
        labelPanel.setBackground(UIHelper.BG_COLOR);

        labelCapture = new LabelCapture("Label", study.getUserInterface().getDataEntryEnvironment());
        labelCapture.setVisible(false);

        labelUsed = new JCheckBox("Label used?", false);
        UIHelper.renderComponent(labelUsed, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, UIHelper.BG_COLOR);

        labelUsed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                labelCapture.setVisible(labelUsed.isSelected());

            }
        });


        labelPanel.add(labelUsed);
        labelPanel.add(labelCapture);

        generalQuestionCont.add(labelPanel);

        final JPanel extractPanel = new JPanel(new GridLayout(2, 2));
        extractPanel.setBackground(UIHelper.BG_COLOR);

        extractDetails.clear();

        JLabel extractsUsedLab = UIHelper.createLabel("Sample(s) used *");
        extractsUsedLab.setHorizontalAlignment(JLabel.LEFT);
        extractsUsedLab.setVerticalAlignment(JLabel.TOP);

        final JPanel extractNameContainer = new JPanel();
        extractNameContainer.setLayout(new BoxLayout(extractNameContainer,
                BoxLayout.PAGE_AXIS));
        extractNameContainer.setBackground(UIHelper.BG_COLOR);

        extract = new ExtractDetailsCapture("Sample " + (extractDetails.size() + 1), study.getUserInterface().getDataEntryEnvironment());

        extractDetails.add(extract);
        extractNameContainer.add(extract);

        JLabel addButton = new JLabel("add sample",
                addRecordIcon,
                JLabel.RIGHT);
        UIHelper.renderComponent(addButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        addButton.setVerticalAlignment(JLabel.TOP);
        addButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                extract = new ExtractDetailsCapture("Sample " + (extractDetails.size() + 1), study.getUserInterface().getDataEntryEnvironment());
                extractDetails.add(extract);
                extractNameContainer.add(extract);

                extractNameContainer.revalidate();
                generalQuestionCont.revalidate();
            }

        });

        addButton.setToolTipText(
                "<html><b>add new sample</b><p>add another sample (e.g. Liver, Heart, Urine, Blood)</p></html>");

        JLabel removeButton = new JLabel("remove sample",
                removeIcon,
                JLabel.RIGHT);
        removeButton.setVerticalAlignment(JLabel.TOP);
        UIHelper.renderComponent(removeButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        removeButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (extractDetails.size() > 1) {
                    extract = extractDetails.get(extractDetails.size() -
                            1);
                    extractDetails.remove(extract);
                    extractNameContainer.remove(extract);
                    generalQuestionCont.revalidate();
                }
            }

        });
        removeButton.setToolTipText(
                "<html><b>remove previously added sample</b><p>remove the sample field last added</p></html>");

        extractPanel.add(extractsUsedLab);
        extractPanel.add(extractNameContainer);

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
        buttonContainer.setBackground(UIHelper.BG_COLOR);

        buttonContainer.add(addButton);
        buttonContainer.add(removeButton);

        extractPanel.add(new JLabel());
        extractPanel.add(buttonContainer);

        generalQuestionCont.add(extractPanel);

        generalQuestionCont.add(Box.createVerticalStrut(5));
        generalQuestionCont.add(Box.createHorizontalGlue());

        generalQuestionCont.setBorder(new TitledBorder(new RoundedBorder(UIHelper.DARK_GREEN_COLOR, 9), assay.getAssayReference(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        return generalQuestionCont;
    }


}
