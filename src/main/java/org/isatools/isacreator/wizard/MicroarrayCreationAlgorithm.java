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

import org.apache.axis.utils.StringUtils;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.autofiltercombo.AutoFilterCombo;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
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


public class MicroarrayCreationAlgorithm extends CreationAlgorithm {

    @InjectedResource
    private ImageIcon addRecordIcon, removeIcon;

    private JCheckBox dyeSwapUsed;
    private List<AutoFilterCombo> arrayDesignsUsed;
    private AutoFilterCombo newArrayDesign;
    private String sourceNameFormat;
    private String[] arrayDesigns;
    private Assay assay;
    private Map<Integer, TreatmentReplicate> treatmentGroups;
    private ExtractDetailsCapture extract;
    private List<ExtractDetailsCapture> extractDetails;
    private TableReferenceObject buildingModel;
    private Map<String, GeneratedSampleDetails> sampleInfo;
    private LabelCapture label1Capture;
    private LabelCapture label2Capture;
    private String institution;

    public MicroarrayCreationAlgorithm(Study study,
                                       Assay assay, List<PropertyType> factorsToAdd,
                                       Map<Integer, TreatmentReplicate> treatmentGroups,
                                       TableReferenceObject buildingModel, String institution,
                                       String sourceNameFormat, String[] arrayDesigns) {


        super(buildingModel, study, factorsToAdd);
        this.assay = assay;

        this.sourceNameFormat = sourceNameFormat;
        this.arrayDesigns = arrayDesigns;
        this.factorsToAdd = factorsToAdd;
        this.buildingModel = buildingModel;
        this.arrayDesignsUsed = new ArrayList<AutoFilterCombo>();
        this.treatmentGroups = treatmentGroups;
        this.institution = institution;

        ResourceInjector.get("wizard-package.style").inject(this);

        sampleInfo = new ListOrderedMap<String, GeneratedSampleDetails>();
        extractDetails = new ArrayList<ExtractDetailsCapture>();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setOpaque(false);
                add(instantiatePanel());
                setVisible(true);
            }
        }
        );
    }

    public int getLabelCount() {
        if (dyeSwapUsed.isSelected()) {
            return 2;
        } else {
            return 1;
        }
    }

    public Map<String, GeneratedSampleDetails> getSampleData() {
        return sampleInfo;
    }

    private List<String> getSelectedArrayDesigns() {
        List<String> arrayDesignsAsString = new ArrayList<String>();

        for (AutoFilterCombo afc : arrayDesignsUsed) {
            if (afc != null) {
                arrayDesignsAsString.add(afc.toString() == null ? "" : afc.toString());
            }
        }

        return arrayDesignsAsString;
    }

    private JPanel instantiatePanel() {
        final JPanel microArrayQuestionCont = new JPanel();
        microArrayQuestionCont.setLayout(new BoxLayout(microArrayQuestionCont,
                BoxLayout.PAGE_AXIS));
        microArrayQuestionCont.setOpaque(false);

        StringBuffer text = new StringBuffer("<html><b>" + assay.getMeasurementEndpoint() +
                "</b> using <b>" + assay.getTechnologyType() + "</b>");

        if (!StringUtils.isEmpty(assay.getAssayPlatform())) {
            text.append(" on <b>").append(assay.getAssayPlatform()).append("</b>");
        }

        JLabel info = new JLabel(text.append("</html>").toString(),
                JLabel.LEFT);

        UIHelper.renderComponent(info, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, false);
        info.setPreferredSize(new Dimension(300, 40));

        JPanel infoPanel = new JPanel(new GridLayout(1, 1));
        infoPanel.setOpaque(false);

        infoPanel.add(info);

        microArrayQuestionCont.add(infoPanel);

        // create reference sample used checkbox

        JPanel labelPanel = new JPanel(new GridLayout(2, 2));
        labelPanel.setBackground(UIHelper.BG_COLOR);

        System.out.println("Study user interface is null? " + (study.getUserInterface() == null));
        System.out.println("Study user interface dep is null? " + (study.getUserInterface().getDataEntryEnvironment() == null));

        label1Capture = new LabelCapture("Label (e.g. Cy3)", study.getUserInterface().getDataEntryEnvironment());
        label2Capture = new LabelCapture("Label (e.g. Cy5)", study.getUserInterface().getDataEntryEnvironment());
        label2Capture.setVisible(false);

        // create dye swap check box
        dyeSwapUsed = new JCheckBox("dye-swap performed?", false);
        UIHelper.renderComponent(dyeSwapUsed, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        dyeSwapUsed.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                label2Capture.setVisible(dyeSwapUsed.isSelected());

            }
        });

        labelPanel.add(UIHelper.createLabel("Label(s) used"));
        labelPanel.add(label1Capture);
        labelPanel.add(dyeSwapUsed);
        labelPanel.add(label2Capture);

        microArrayQuestionCont.add(labelPanel);

        final JPanel extractPanel = new JPanel(new GridLayout(2, 2));
        extractPanel.setOpaque(false);

        extractDetails.clear();

        JLabel extractsUsedLab = UIHelper.createLabel("sample(s) used *");
        extractsUsedLab.setHorizontalAlignment(JLabel.LEFT);
        extractsUsedLab.setVerticalAlignment(JLabel.TOP);

        final JPanel extractNameContainer = new JPanel();
        extractNameContainer.setLayout(new BoxLayout(extractNameContainer,
                BoxLayout.PAGE_AXIS));
        extractNameContainer.setOpaque(false);

        extract = new ExtractDetailsCapture("Sample " + (extractDetails.size() + 1), study.getUserInterface().getDataEntryEnvironment());

        extractDetails.add(extract);
        extractNameContainer.add(extract);

        JLabel addExtractButton = new JLabel("add sample",
                addRecordIcon,
                JLabel.RIGHT);
        UIHelper.renderComponent(addExtractButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        addExtractButton.setVerticalAlignment(JLabel.TOP);
        addExtractButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                extract = new ExtractDetailsCapture("Sample " + (extractDetails.size() + 1), study.getUserInterface().getDataEntryEnvironment());
                extractDetails.add(extract);
                extractNameContainer.add(extract);

                extractNameContainer.revalidate();
                microArrayQuestionCont.revalidate();
            }
        });

        addExtractButton.setToolTipText(
                "<html><b>add new sample</b><p>add another sample (e.g. Liver, Heart, Urine, Blood)</p></html>");

        JLabel removeExtractButton = new JLabel("remove sample",
                removeIcon,
                JLabel.RIGHT);
        removeExtractButton.setVerticalAlignment(JLabel.TOP);
        UIHelper.renderComponent(removeExtractButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        removeExtractButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (extractDetails.size() > 1) {
                    extract = extractDetails.get(extractDetails.size() -
                            1);
                    extractDetails.remove(extract);
                    extractNameContainer.remove(extract);
                    microArrayQuestionCont.revalidate();
                }
            }
        });
        removeExtractButton.setToolTipText(
                "<html><b>remove previously added sample</b><p>remove the array design field last added</p></html>");

        extractPanel.add(extractsUsedLab);
        extractPanel.add(extractNameContainer);

        JPanel extractButtonContainer = new JPanel(new GridLayout(1, 2));
        extractButtonContainer.setOpaque(false);

        extractButtonContainer.add(addExtractButton);
        extractButtonContainer.add(removeExtractButton);

        extractPanel.add(new JLabel());
        extractPanel.add(extractButtonContainer);

        microArrayQuestionCont.add(extractPanel);

        // ask for array designs used...
        // create array designs panel
        final JPanel arrayDesignPanel = new JPanel(new GridLayout(2, 2));
        arrayDesignPanel.setOpaque(false);

        arrayDesignsUsed.clear();

        JLabel arrayDesignLab = UIHelper.createLabel("array design(s) used *");
        arrayDesignLab.setVerticalAlignment(JLabel.TOP);
        // the array designs container must adjust to an unknown number of fields. therefore, a JPanel with a BoxLayout
        // will be used since it is flexible!
        final JPanel arrayDesignsContainer = new JPanel();
        arrayDesignsContainer.setLayout(new BoxLayout(arrayDesignsContainer,
                BoxLayout.PAGE_AXIS));
        arrayDesignsContainer.setOpaque(false);

        newArrayDesign = new AutoFilterCombo(arrayDesigns, true);

        UIHelper.renderComponent(newArrayDesign, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        newArrayDesign.setPreferredSize(new Dimension(70, 30));

        arrayDesignsContainer.add(newArrayDesign);
        arrayDesignsUsed.add(newArrayDesign);

        JLabel addButton = new JLabel("add design",
                addRecordIcon,
                JLabel.RIGHT);

        UIHelper.renderComponent(addButton, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
        addButton.setVerticalAlignment(JLabel.TOP);
        addButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                newArrayDesign = new AutoFilterCombo(arrayDesigns, true);
                UIHelper.renderComponent(newArrayDesign, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);
                newArrayDesign.setPreferredSize(new Dimension(70, 30));
                arrayDesignsUsed.add(newArrayDesign);
                arrayDesignsContainer.add(newArrayDesign);
                arrayDesignsContainer.revalidate();
                microArrayQuestionCont.revalidate();
            }

        });
        addButton.setToolTipText(
                "<html><b>add new array design</b><p>add another array design</p></html>");

        JLabel removeButton = new JLabel("remove design",
                removeIcon,
                JLabel.RIGHT);
        removeButton.setVerticalAlignment(JLabel.TOP);
        UIHelper.renderComponent(removeButton, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        removeButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (arrayDesignsUsed.size() > 1) {
                    newArrayDesign = arrayDesignsUsed.get(arrayDesignsUsed.size() -
                            1);
                    arrayDesignsUsed.remove(newArrayDesign);
                    arrayDesignsContainer.remove(newArrayDesign);
                    arrayDesignPanel.revalidate();
                    microArrayQuestionCont.validate();
                }
            }

        });
        removeButton.setToolTipText(
                "<html><b>remove previously added array design</b><p>remove the array design field last added</p></html>");

        arrayDesignPanel.add(arrayDesignLab);
        arrayDesignPanel.add(arrayDesignsContainer);

        JPanel buttonContainer = new JPanel(new GridLayout(1, 2));
        buttonContainer.setOpaque(false);

        buttonContainer.add(addButton);
        buttonContainer.add(removeButton);

        arrayDesignPanel.add(new JLabel());
        arrayDesignPanel.add(buttonContainer);

        microArrayQuestionCont.add(arrayDesignPanel);

        microArrayQuestionCont.add(Box.createVerticalStrut(5));
        microArrayQuestionCont.add(Box.createHorizontalGlue());

        microArrayQuestionCont.setBorder(new TitledBorder(new RoundedBorder(UIHelper.DARK_GREEN_COLOR, 9), assay.getAssayReference(), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        return microArrayQuestionCont;
    }


    @SuppressWarnings({"ConstantConditions"})
    public void performAssayCentricTask() {
        // run the algorithm using all gathered information!

        int startSubject = 1;
        StringBuffer row = new StringBuffer();

        Vector<String> headersForReferenceObject = new Vector<String>();
        String[] headersAsArray = new String[headers.size()];
        headersForReferenceObject.add(TableReferenceObject.ROW_NO_TEXT);

        for (int i = 0; i < headers.size(); i++) {
            headersForReferenceObject.add(headers.get(i));
            headersAsArray[i] = headers.get(i);
        }

        buildingModel.setPreDefinedHeaders(headersForReferenceObject);


        for (String arrayDesign : getSelectedArrayDesigns()) {
            for (int groups = 0; groups < treatmentGroups.size(); groups++) {
                for (ExtractDetailsCapture extractField : extractDetails) {
                    for (int subjects = startSubject; subjects <= treatmentGroups.get(groups).getNumReplicates();
                         subjects++) {
                        for (int labelNum = 0; labelNum < getLabelCount();
                             labelNum++) {
                            for (int i : colsToUse) {
                                String nextDataToAdd = tableStructure.get(i)[1] == null ? "" : tableStructure.get(i)[1]; // try and insert a template item
                                nextDataToAdd = nextDataToAdd.trim();

                                if (nextDataToAdd.equals("")) {
                                    if (tableStructure.get(i)[0].toLowerCase()
                                            .equals("factors")) {
                                        row.append(treatmentGroups.get(groups).getTreatmentGroup());
                                    } else if (tableStructure.get(i)[0].toLowerCase()
                                            .equals("label")) {
                                        if (!dyeSwapUsed.isSelected()) {
                                            row.append(label1Capture.getLabelName()).append("\t");
                                        } else {
                                            if (labelNum == 0) {
                                                row.append(label1Capture.getLabelName()).append("\t");
                                            } else {
                                                row.append(label2Capture.getLabelName()).append("\t");
                                            }
                                        }
                                    } else {
                                        // just empty row data
                                        row.append(nextDataToAdd).append("\t");
                                    }
                                } else {
                                    row.append(nextDataToAdd).append("\t");
                                }
                            }

                            String extractName = extractField.getPoolingPerformed() ? extractField.getExtractName() + ".Pooled" : extractField.getExtractName();

                            String shortExtractName = extractName;
                            if (extractName.contains(":")) {
                                // remove ontology source from the extract name
                                shortExtractName = extractName.substring(extractName.indexOf(":") + 1);
                            }

                            row = new StringBuffer(replaceStringModelValues(row.toString(), institution, groups + 1,
                                    subjects, shortExtractName,
                                    labelNum + 1, arrayDesign));

                            String sampleName = replaceStringModelValues(buildingModel.getColumnFormatByName("sample name"),
                                    institution, groups + 1, subjects, shortExtractName, labelNum + 1, arrayDesign);

                            String sourceName = replaceStringModelValues(sourceNameFormat,
                                    institution, groups + 1, subjects, shortExtractName, labelNum + 1, arrayDesign);

                            sampleInfo.put(sampleName, new GeneratedSampleDetails(
                                    extractName, sourceName, treatmentGroups.get(groups).getTreatmentGroup()));

                            buildingModel.addRowData(headersAsArray, row.toString().split("\t"));

                            // reset variables for nex iteration
                            row = new StringBuffer();
                        }
                    }
                }
            }
        }
        // add extract statement for reference sample addition.
        assay.setTableReferenceObject(buildingModel);
    }
}