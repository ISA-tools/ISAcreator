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

package org.isatools.isacreator.mgrast.ui;

import org.isatools.isacreator.archiveoutput.ArchiveOutputWindow;
import org.isatools.isacreator.common.SelectOutputDirectoryDialog;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.common.dialog.WarningDialog;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.mgrast.conceptmapper.MGRastConceptMapper;
import org.isatools.isacreator.mgrast.io.FileExporter;
import org.isatools.isacreator.mgrast.model.ConfidenceLevel;
import org.isatools.isacreator.mgrast.model.FieldMapping;
import org.isatools.isacreator.mgrast.model.SampleExternalIds;
import org.isatools.isacreator.mgrast.utils.APIHook;
import org.isatools.isacreator.model.Publication;
import org.isatools.isacreator.model.Study;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * MGRastUI
 *
 * @author eamonnmaguire
 * @date Sep 24, 2010
 */


public class MGRastUI extends JDialog {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("exporters-package.style").load(
                ArchiveOutputWindow.class.getResource("/dependency-injections/exporters-package.properties"));
        ResourceInjector.get("common-package.style").load(
                ArchiveOutputWindow.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("longtexteditor-package.style").load(
                ArchiveOutputWindow.class.getResource("/dependency-injections/longtexteditor-package.properties"));

    }


    private ConfirmationDialog confirmChoice;

    public static final int MAPPING_INFO = 0;
    public static final int SAMPLE_INFO = 1;
    public static final int HELP = 2;

    @InjectedResource
    private ImageIcon mgRastHeader, mgRastExportSampleSection, weKnowIcon, weKnowIconOver, weDoNotKnowIcon,
            weDoNotKnowIconOver, helpIcon, helpIconOver, closeWindowIcon, closeWindowIconOver, exportIcon,
            exportIconOver, buttonPanelSeparator, loadingSamples, mappingConcepts, noProjectNameWarning, exportHeader;

    private JPanel swappableContainer;

    private JLabel mappingInfoButton, sampleInfoButton, helpButton;

    private int selectedSection = HELP;

    private HelpPane helpPane;
    private ExtraMetaDataPane projectMetadata;
    private SampleInfoPane sampleInfoPane;
    private TermMappingUI termMappingPane;
    private ISAcreator isacreatorEnvironment;
    private APIHook mgRastAPIHook;

    private FileExporter fileUtils;
    private Properties mappingHistory;

    private Map<String, FieldMapping> fieldMappings;
    private List<SampleExternalIds> externalIds;

    public MGRastUI(ISAcreator isacreatorEnvironment) {
        this.isacreatorEnvironment = isacreatorEnvironment;
        ResourceInjector.get("exporters-package.style").inject(this);
        projectMetadata = new ExtraMetaDataPane();
        fileUtils = new FileExporter();
    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(650, 560));
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.GREY_COLOR, UIHelper.GREY_COLOR));


        add(createTopPanel(), BorderLayout.NORTH);

        swappableContainer = new JPanel();
        swappableContainer.setPreferredSize(new Dimension(650, 340));
        helpPane = new HelpPane();
        helpPane.createGUI();

        swappableContainer.add(helpPane);

        add(swappableContainer, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    private Container createSouthPanel() {
        Box southPanel = Box.createHorizontalBox();
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel closeButton = new JLabel(closeWindowIcon);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                confirmChoice = new ConfirmationDialog();

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.NO, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                    }
                });

                confirmChoice.addPropertyChangeListener(ConfirmationDialog.YES, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        confirmChoice.hideDialog();
                        confirmChoice.dispose();
                        closeWindow();
                    }
                });

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        confirmChoice.createGUI();
                        confirmChoice.showDialog(isacreatorEnvironment);
                    }
                });


            }
        });

        final JLabel export = new JLabel(exportIcon);
        export.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                export.setIcon(exportIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                export.setIcon(exportIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                final SelectOutputDirectoryDialog outputDir = new SelectOutputDirectoryDialog(exportHeader);


                outputDir.addPropertyChangeListener(SelectOutputDirectoryDialog.CANCEL, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        outputDir.hideDialog();
                        outputDir.dispose();
                    }
                });

                outputDir.addPropertyChangeListener(SelectOutputDirectoryDialog.CONTINUE, new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                        outputDir.hideDialog();
                        outputDir.dispose();

                        // precautionary measure in case the user has not selected sample ids tab.
                        if (externalIds == null) {
                            constructSampleIds();
                        }

                        System.out.println("dir selected = " + propertyChangeEvent.getNewValue().toString());

                        fileUtils.outputFiles(new File(propertyChangeEvent.getNewValue().toString()),
                                fieldMappings, externalIds, projectMetadata, mgRastAPIHook);

                        // todo should we store confidence levels as well?
                        for (String isaField : fieldMappings.keySet()) {
                            FieldMapping fm = fieldMappings.get(isaField);
                            if (fm.getConfidenceLevel() != ConfidenceLevel.ZERO_PERCENT) {
                                mappingHistory.setProperty(isaField, fm.getMgRastTermMappedTo());
                            }
                        }

                        fileUtils.saveMappingHistory(mappingHistory);

                        // if successful, close window!
                        closeWindow();
                    }
                });

                if (projectMetadata.areAllFieldsValid()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            outputDir.createGUI();
                            outputDir.showDialog(MGRastUI.this);
                        }
                    });

                } else {
                    final WarningDialog warningDialog = new WarningDialog(noProjectNameWarning);


                    warningDialog.addPropertyChangeListener(WarningDialog.OK, new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                            warningDialog.hideDialog();
                            warningDialog.dispose();
                        }
                    });


                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            warningDialog.createGUI();
                            warningDialog.showDialog(isacreatorEnvironment);
                        }
                    });

                }
            }
        });

        southPanel.add(closeButton);
        southPanel.add(new JLabel(buttonPanelSeparator));
        southPanel.add(export);

        return southPanel;
    }

    public void loadAndMapData(String studyId) {
        mgRastAPIHook = new APIHook(isacreatorEnvironment, studyId);
        setProjectMetadataFromStudyInformation(isacreatorEnvironment.getDataEntryEnvironment()
                .getInvestigation().getStudies().get(studyId));
    }

    private void setProjectMetadataFromStudyInformation(Study study) {
        projectMetadata.setContacts(study.getContacts());

        String pubmedIds = "";
        if (study.getPublications().size() > 0) {
            int count = 0;
            for (Publication pub : study.getPublications()) {
                if (pub.getPubmedId() != null) {
                    if (!pub.getPubmedId().isEmpty()) {
                        pubmedIds += pub.getPubmedId();
                        if (count != study.getPublications().size() - 1) {
                            pubmedIds += ", ";
                        }
                    }
                }
                count++;
            }
        }

        projectMetadata.setPubmedId(pubmedIds);
        projectMetadata.setProjectName(study.getStudyTitle());
        projectMetadata.setProjectDescription(study.getStudyDesc());
        projectMetadata.setInternalProjectId(study.getStudyId());
    }

    private void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isacreatorEnvironment.hideSheet();
            }
        });
    }

    private Container createTopPanel() {

        Box topContainer = Box.createVerticalBox();

        JLabel mgRastExporterHeader = new JLabel(mgRastHeader);
        mgRastExporterHeader.setHorizontalAlignment(SwingConstants.LEFT);

        topContainer.add(UIHelper.wrapComponentInPanel(mgRastExporterHeader));


        topContainer.add(projectMetadata);

        Box topPanel = Box.createHorizontalBox();

        JLabel mgRastLogo = new JLabel(mgRastExportSampleSection);
        mgRastLogo.setHorizontalAlignment(SwingConstants.LEFT);

        mappingInfoButton = new JLabel(weKnowIcon);
        mappingInfoButton.setHorizontalAlignment(SwingConstants.LEFT);

        mappingInfoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                mappingInfoButton.setIcon(weKnowIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

                mappingInfoButton.setIcon(selectedSection == MAPPING_INFO ? weKnowIconOver : weKnowIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                mappingInfoButton.setIcon(weKnowIconOver);
                selectedSection = MAPPING_INFO;


                if (termMappingPane == null) {

                    Thread performer = new Thread(new Runnable() {
                        public void run() {
                            try {

                                Set<String> isatabFields = mgRastAPIHook.getISAtabFields();

                                fieldMappings = new HashMap<String, FieldMapping>();

                                MGRastConceptMapper mapper = new MGRastConceptMapper();

                                // check properties file to see if mapping exists here.
                                mappingHistory = fileUtils.loadMappingHistory();

                                for (String isaField : isatabFields) {
                                    FieldMapping fm;

                                    boolean mappingHistoryTermAvailable = mappingHistory.containsKey(isaField);

                                    if (!mappingHistoryTermAvailable) {
                                        fm = mapper.getMGRastTermFromGSCTerm(isaField, "");
                                    } else {
                                        fm = new FieldMapping(isaField, mappingHistory.getProperty(isaField), ConfidenceLevel.ONE_HUNDRED_PERCENT);
                                    }
                                    fieldMappings.put(isaField, fm);
                                }

                                termMappingPane = new TermMappingUI(fieldMappings, mapper.getAllMgRastConcepts());
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        termMappingPane.createGUI();
                                        swapContainers(termMappingPane);
                                    }
                                });
                            } catch (Exception e) {
                                System.err.println("Problem occured whilst loading samples: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });


                    swapContainers(UIHelper.wrapComponentInPanel(new JLabel(mappingConcepts)));

                    performer.start();


                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            swapContainers(termMappingPane);
                        }
                    });
                }
            }
        });

        sampleInfoButton = new JLabel(weDoNotKnowIcon);
        sampleInfoButton.setHorizontalAlignment(SwingConstants.LEFT);

        sampleInfoButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                sampleInfoButton.setIcon(weDoNotKnowIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

                sampleInfoButton.setIcon(selectedSection == SAMPLE_INFO ? weDoNotKnowIconOver : weDoNotKnowIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                sampleInfoButton.setIcon(weDoNotKnowIconOver);
                selectedSection = SAMPLE_INFO;

                if (sampleInfoPane == null) {

                    Thread performer = new Thread(new Runnable() {
                        public void run() {
                            try {

                                constructSampleIds();

                                sampleInfoPane = new SampleInfoPane(externalIds);
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        sampleInfoPane.createGUI();
                                        swapContainers(sampleInfoPane);
                                    }
                                });
                            } catch (Exception e) {
                                System.err.println("Problem occured whilst loading samples: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }
                    });


                    swapContainers(UIHelper.wrapComponentInPanel(new JLabel(loadingSamples)));

                    performer.start();


                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            swapContainers(sampleInfoPane);
                        }
                    });
                }
            }
        });

        helpButton = new JLabel(helpIconOver);
        helpButton.setHorizontalAlignment(SwingConstants.LEFT);

        helpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                helpButton.setIcon(helpIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                helpButton.setIcon(selectedSection == HELP ? helpIconOver : helpIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                selectedSection = HELP;
                helpButton.setIcon(helpIconOver);

                if (helpPane == null) {
                    helpPane = new HelpPane();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            helpPane.createGUI();
                        }
                    });
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        swapContainers(helpPane);
                    }
                });
            }
        });

        topPanel.add(mgRastLogo);
        topPanel.add(mappingInfoButton);
        topPanel.add(sampleInfoButton);
        topPanel.add(helpButton);

        topContainer.add(topPanel);

        return topContainer;
    }

    private void constructSampleIds() {
        Set<String> sampleNames = mgRastAPIHook.getSampleNames();

        externalIds = new ArrayList<SampleExternalIds>();

        for (String sampleName : sampleNames) {
            externalIds.add(new SampleExternalIds(sampleName));
        }
    }

    private void resetButtons() {
        mappingInfoButton.setIcon(weKnowIcon);
        sampleInfoButton.setIcon(weDoNotKnowIcon);
        helpButton.setIcon(helpIcon);
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }

    public static void main(String[] args) {
        final MGRastUI mgRastUI = new MGRastUI(null);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                mgRastUI.createGUI();
            }
        });
    }
}
