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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.AbstractDataEntryEnvironment;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.io.UserProfile;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Class should gather all of the assays defined in the Study definition form and then produce a list of the assay
 * specific questions for each of the assays.
 * When information on each of the assays is collected, we then check the number of treatment groups calculated from the
 * study factors, and the number entered by the user. if they don't match, we need to see why, so we ask the user to select
 * which of the treatment conditions they actually used in their experiment.
 *
 * @author Eamonn Maguire
 */
public class AddAssayPane extends JPanel {

    private static final Logger log = Logger.getLogger(AddAssayPane.class.getName());

    private static final String ARRAY_DESIGN_FILE = System.getProperty("java.io.tmpdir") + File.separator + "AEArrayDesigns.txt";

    private Collection<Assay> assaysToDefine;
    private List<CreationAlgorithm> algorithmsToRun;
    private Study study;
    private List<TempFactors> factorsToAdd;
    private Map<Integer, TreatmentReplicate> treatmentGroups;
    private DataEntryEnvironment dep;
    private ListOrderedMap<String, GeneratedSampleDetails> sampleNameValues;
    private AbstractDataEntryEnvironment dew;
    private UserProfile up;
    private MouseListener[] listeners;

    public AddAssayPane(AbstractDataEntryEnvironment dew, Study study, List<TempFactors> factorsToAdd,
                        Map<Integer, TreatmentReplicate> treatmentGroups, DataEntryEnvironment dep, UserProfile up) {
        this.dew = dew;
        this.up = up;
        this.study = study;
        this.factorsToAdd = factorsToAdd;
        this.treatmentGroups = treatmentGroups;
        this.dep = dep;
        assaysToDefine = study.getAssays().values();

        algorithmsToRun = new ArrayList<CreationAlgorithm>();
        sampleNameValues = new ListOrderedMap<String, GeneratedSampleDetails>();
        listeners = new MouseListener[2];

        setPreferredSize(new Dimension(600, 500));
        setLayout(new BorderLayout());
        setOpaque(false);
    }

    public void createGUI() {
        // create center panel containing content
        add(createCenterPanel(), BorderLayout.CENTER);
        // create bottom panel containing buttons
        createSouthPanel();

        setVisible(true);
    }

    public JComponent createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalStrut(15));

        String sourceNameFormat = dep.getParentFrame().selectTROForUserSelection(MappingObject.STUDY_SAMPLE).getColumnFormatByName("source name");

        String[] arrayDesigns = retrieveArrayDesigns();

        for (Assay a : assaysToDefine) {
            if (a.getTechnologyType().equalsIgnoreCase("dna microarray")) {
                MicroarrayCreationAlgorithm maAlg = new MicroarrayCreationAlgorithm(study, a, factorsToAdd, treatmentGroups,
                        new TableReferenceObject(dep.getParentFrame().selectTROForUserSelection(
                                a.getMeasurementEndpoint(),
                                a.getTechnologyType()).getTableFields()), up.getInstitution(), sourceNameFormat, arrayDesigns);
                algorithmsToRun.add(maAlg);
                centerPanel.add(maAlg);
            } else {
                GeneralCreationAlgorithm gca = new GeneralCreationAlgorithm(study, a, factorsToAdd, treatmentGroups,
                        new TableReferenceObject(dep.getParentFrame().selectTROForUserSelection(a.getMeasurementEndpoint(),
                                a.getTechnologyType()).getTableFields()), up.getInstitution(), sourceNameFormat);
                algorithmsToRun.add(gca);
                centerPanel.add(gca);
            }
        }

        JScrollPane assayScroller = new JScrollPane(centerPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        assayScroller.setBackground(UIHelper.BG_COLOR);
        assayScroller.setBorder(null);
        assayScroller.getViewport().setOpaque(false);
        assayScroller.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);

        IAppWidgetFactory.makeIAppScrollPane(assayScroller);


        return dew.getGeneralLayout(new ImageIcon(
                getClass().getResource("/images/wizard/defineassay.png")), new ImageIcon(getClass()
                .getResource("/images/wizard/BC_4.png")), "Please provide some information about these assays...", assayScroller, getHeight());
    }


    public void createSouthPanel() {
        AbstractDataEntryEnvironment.backButton.setIcon(AbstractDataEntryEnvironment.back);
        listeners[0] = new MouseAdapter() {
            // todo add timer here to ensure that the algorithm isn't run twice!
            public void mousePressed(MouseEvent event) {
                firePropertyChange("canceledAssayCreation", "cancelling", "assaydef");
            }

            public void mouseEntered(MouseEvent event) {
                AbstractDataEntryEnvironment.backButton.setIcon(AbstractDataEntryEnvironment.backOver);
            }

            public void mouseExited(MouseEvent event) {
                AbstractDataEntryEnvironment.backButton.setIcon(AbstractDataEntryEnvironment.back);
            }
        };

        dew.assignListenerToLabel(AbstractDataEntryEnvironment.backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                // todo add timer here to ensure that the algorithm isn't run twice!
                for (CreationAlgorithm ca : algorithmsToRun) {
                    // possible way to create a study sample file is to take the set of all study sample information then adding
                    // it to the row data for a study sample
                    ca.runAlgorithm();
                    // since we're adding the data to a map, we will not get any repeated data! :o)
                    if (ca.getSampleData() != null) {
                        sampleNameValues.putAll(ca.getSampleData());
                    }
                }

                firePropertyChange("finishedAssayCreation", "notfinished", "finished");
                // after which all assays should be populated with data :o)
            }

            public void mouseEntered(MouseEvent event) {
                AbstractDataEntryEnvironment.nextButton.setIcon(AbstractDataEntryEnvironment.nextOver);
            }

            public void mouseExited(MouseEvent event) {
                AbstractDataEntryEnvironment.nextButton.setIcon(AbstractDataEntryEnvironment.next);
            }
        };

        dew.assignListenerToLabel(AbstractDataEntryEnvironment.nextButton, listeners[1]);
    }

    public ListOrderedMap<String, GeneratedSampleDetails> getSampleNameValues() {
        return sampleNameValues;
    }

    private String[] retrieveArrayDesigns() {
        String[] arrayDesignList = new String[]{"no designs available"};
        StringBuffer data = new StringBuffer();

        File arrayDesignsFile = new File(ARRAY_DESIGN_FILE);

        if (arrayDesignsFile.exists()) {

            try {
                Scanner sc = new Scanner(arrayDesignsFile);
                Pattern p = Pattern.compile("[a-zA-Z]+-[a-zA-Z]+-[0-9]+");
                while (sc.hasNext()) {
                    String nextLine = sc.nextLine();

                    String[] nextLineArray = nextLine.split("\t");

                    String candidateEntry = nextLineArray[1].trim();
                    String candidateDescription = null;
                    if (nextLineArray.length > 2) {
                        candidateDescription = nextLine.split("\t")[2].trim();
                    }

                    Matcher m = p.matcher(candidateEntry);
                    if (m.matches()) {
                        data.append(candidateEntry);

                        if (candidateDescription != null) {
                            data.append(" - ").append(candidateDescription);
                        }
                        data.append(":");
                    }
                }

                if (!(data.length() == 0)) {
                    String dataStr = data.toString();
                    dataStr = dataStr.substring(0, data.length() - 1);
                    arrayDesignList = dataStr.split(":");
                }

            } catch (FileNotFoundException e) {
                log.error("File not found: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                log.error("Precautionary catch thrown if file contents are incorrect!!");
            }
        }
        return arrayDesignList;
    }

    public MouseListener[] getListeners() {
        return listeners;
    }
}
