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

package org.isatools.isacreator.formatmappingutility;

import jxl.read.biff.BiffException;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isacreator.common.HistoryComponent;
import org.isatools.isacreator.common.MappingObject;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.formatmappingutility.io.ISAFieldMapping;
import org.isatools.isacreator.formatmappingutility.io.MappingXMLCreator;
import org.isatools.isacreator.formatmappingutility.io.MappingXMLLoader;
import org.isatools.isacreator.formatmappingutility.io.SavedMappings;
import org.isatools.isacreator.formatmappingutility.loader.FileLoader;
import org.isatools.isacreator.formatmappingutility.loader.Loader;
import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.DataEntryWrapper;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.io.CustomizableFileFilter;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.spreadsheet.TableReferenceObject;
import org.isatools.isacreator.utils.WorkingScreen;
import org.isatools.isacreator.visualization.TreeView;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * @author Eamonn Maguire
 * @date Apr 20, 2009
 */

public class MappingUtilView extends DataEntryWrapper {
    private static final Logger log = Logger.getLogger(MappingUtilView.class.getName());

    @InjectedResource
    private ImageIcon logo, mappingInfo, selectFilesHeader, selectAssaysUsedHeader, performMappingHeader,
            mappingVisualisationHelp, mappingOverviewHeader, saveMappingHelp, saveMappingsHeader, saveMappingsButtonIcon,
            saveMappingsButtonIconOver, breadcrumb1, breadcrumb2, breadcrumb3, breadcrumb4, breadcrumb5, breadcrumb6, breadcrumb7;

    private JLabel status;
    private Stack<HistoryComponent> previousPage;
    private Investigation inv;
    private DataEntryEnvironment dep;
    private ISAcreatorMenu menuPanels;
    private String fileBeingMapped;
    private JFileChooser jfc;

    private ErrorDisplay errorPanel;

    private WorkingScreen workingProgressScreen;
    private Component lastPage;

    private String[] fileColumns;
    private int reader;

    // list of the assays to be defined
    private List<AssaySelection> assaysToBeDefined;
    // map of from the name of the table to the ASO containing information about the Measurement and Technology
    private Map<String, AssaySelection> tableNameToASO;
    // map of from the name of the table to the TableReferenceObject required for definition of the Spreadsheet
    private Map<String, TableReferenceObject> definitions = new HashMap<String, TableReferenceObject>();
    // provides the fields that should have a fixed mapping (e.g. after the study sample definition, the Study Sample
    // field should have the same definition throughout!)
    private Map<String, MappedElement> fixedMappings = new HashMap<String, MappedElement>();

    // the Map provides a way of storing the different mappings which have occurred for each of the fields (unique) within
    // the ISAtab format. It is known that this is not the only way of saving the mappings, but it is the most flexible (can)
    // be used for multiple types of Assay, as opposed to just one. A more restrictive, but better solution would be to
    // save the mappings according to an assay type, and doing the mappings based on field name & column order.
    private Map<String, ISAFieldMapping> mappingsToSave = new HashMap<String, ISAFieldMapping>();

    // seems an adequate assumption to make that the ISA fields, even when duplicated in name amongst the
    // study sample file and multiple assays should map to the same fields coming from the incoming file.
    // The SavedMappings object contains the mappings loaded (if applicable) which essentially say that field
    // x in the isatab format maps to columns 1, 2 & 3 from the incoming file.
    private SavedMappings preExistingMapping = null;

    public MappingUtilView(final ISAcreatorMenu menuPanels) {
        super();
        this.menuPanels = menuPanels;

        ResourceInjector.get("formatmappingutility-package.style").inject(this);

        jfc = new JFileChooser("Choose file or directory to load...");
        jfc.setDialogTitle("Choose file or directory to load...");
        jfc.setApproveButtonText("Select for mapping");

    }

    public void createGUI() {

        menuPanels.getMain().hideGlassPane();


        createWestPanel(logo, mappingInfo);

        createSouthPanel();

        workingProgressScreen = new WorkingScreen();
        workingProgressScreen.createGUI();

        setupErrorPanel();

        status = new JLabel();
        previousPage = new Stack<HistoryComponent>();
        setOpaque(false);

        // create first pane (select files pane!)
        dep = new DataEntryEnvironment(menuPanels.getMain());
        setDep(dep);
    }

    public void changeView() {
        setCurrentPage(createSelectFilesPanel());
    }

    private TableReferenceObject getTableReferenceObject(String technology, String endpoint) {

        if (endpoint.equalsIgnoreCase("[sample]")) {
            return menuPanels.getMain().selectTROForUserSelection(MappingObject.STUDY_SAMPLE);
        } else {
            technology = technology.equalsIgnoreCase(AssaySelectionUI.NO_TECHNOLOGY_TEXT) ? "" : technology;
            return menuPanels.getMain().selectTROForUserSelection(endpoint, technology);
        }
    }

    /**
     * Create JPanel asking users to select the file or directory containing files to be mapped
     * and an existing mapping file if they have one.
     *
     * @return JPanel containing elements!
     */
    private JLayeredPane createSelectFilesPanel() {
        // create overall panel

        final JPanel selectFilesContainer = new JPanel();
        selectFilesContainer.setSize(new Dimension(400, 100));
        selectFilesContainer.setLayout(new BoxLayout(selectFilesContainer, BoxLayout.PAGE_AXIS));

        // create selector for mapping files
        final FileSelectionPanel fileToMapFSP = new FileSelectionPanel("<html>please select file(s) to be mapped. Please ensure " +
                "that this file has <b>no empty columns</b> and if possible, please remove any special characters e.g. mu.</html>", jfc);
        selectFilesContainer.add(fileToMapFSP);

        JPanel selectMappingPanel = new JPanel();
        selectMappingPanel.setLayout(new BoxLayout(selectMappingPanel, BoxLayout.PAGE_AXIS));
        selectMappingPanel.setOpaque(false);

        // need a jcheckbox to ask users if they wish to use a mapping
        JPanel useMappingContainer = new JPanel(new BorderLayout());
        useMappingContainer.setOpaque(false);

        // need a file selection panel to select the file.
        JFileChooser mappingFileChooser = new JFileChooser();
        mappingFileChooser.setDialogTitle("Choose mapping file (XML)");
        mappingFileChooser.setApproveButtonText("select file");
        mappingFileChooser.setFont(UIHelper.VER_11_PLAIN);
        mappingFileChooser.setFileFilter(new CustomizableFileFilter("xml"));

        final FileSelectionPanel savedMappingsFile = new FileSelectionPanel("<html>please select saved mapping file (xml): </html>", mappingFileChooser);
        savedMappingsFile.setVisible(false);

        final JCheckBox useMapping = new JCheckBox("use a previous mapping?");
        UIHelper.renderComponent(useMapping, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        useMapping.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                savedMappingsFile.setVisible(useMapping.isSelected());
            }
        });

        useMappingContainer.add(useMapping, BorderLayout.WEST);

        selectMappingPanel.add(useMappingContainer);

        selectMappingPanel.add(savedMappingsFile);

        selectFilesContainer.add(selectMappingPanel);


        JPanel statusPanel = new JPanel(new GridLayout(1, 1));
        statusPanel.setPreferredSize(new Dimension(400, 30));
        final JLabel statusLab = UIHelper.createLabel("", UIHelper.VER_11_BOLD, UIHelper.RED_COLOR);
        statusPanel.add(statusLab);

        selectFilesContainer.add(statusPanel);

        final JLayeredPane finalLayout = getGeneralLayout(selectFilesHeader, breadcrumb1, "", selectFilesContainer, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                backButton.setIcon(back);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        menuPanels.getMain().setCurrentPage(menuPanels);
                        menuPanels.getMain().setGlassPanelContents(menuPanels.getCreateISAMenuGUI());
                        menuPanels.startAnimation();
                    }
                });
            }
        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                nextButton.setIcon(next);

                Thread loadFile = new Thread(new Runnable() {
                    public void run() {
                        if (useMapping.isSelected()) {

                            if (!savedMappingsFile.getSelectedFilePath().trim().equals("")) {

                                MappingXMLLoader loader = new MappingXMLLoader(savedMappingsFile.getSelectedFilePath());
                                try {
                                    preExistingMapping = loader.loadMappings();
                                } catch (XmlException e) {
                                    log.error(e.getMessage());
                                    statusLab.setText("<html>problem found in xml for saved mapping: " + e.getMessage() + " </html>");
                                    setCurrentPage(lastPage);
                                    return;
                                } catch (IOException e) {
                                    log.error(e.getMessage());
                                    statusLab.setText("<html>problem found when resolving file for saved mapping: " + e.getMessage() + " </html>");
                                    setCurrentPage(lastPage);
                                    return;
                                }
                            } else {
                                statusLab.setText("<html>please select a file containing previous mappings...</html>");
                                setCurrentPage(lastPage);
                                return;
                            }
                        } else {
                            log.info("Mapping is not selected");
                            statusLab.setText("");
                        }

                        if (fileToMapFSP.notEmpty()) {
                            previousPage.push(new HistoryComponent(finalLayout, listeners));
                            statusLab.setText("");
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    setCurrentPage(createAssayUsedPanel(fileToMapFSP.getSelectedFilePath()));
                                }
                            });
                        } else {
                            statusLab.setText("<html>please <strong>select</strong> a file to map!</html>");
                            setCurrentPage(lastPage);
                        }
                    }
                });

                if (fileToMapFSP.notEmpty() && fileToMapFSP.checkFileExtensionValid("xls", "csv", "txt")) {
                    statusLab.setText("");
                    lastPage = currentPage;
                    setCurrentPage(workingProgressScreen);
                    loadFile.start();
                } else {
                    statusLab.setText("<html>please select a file with the extension <strong>.xls</strong>, <strong>.csv</strong> or <strong>.txt</strong>...</html>");
                }


            }
        };
        assignListenerToLabel(nextButton, listeners[1]);
        return finalLayout;
    }


    /**
     * Create JPanel asking users to select the assay used in each file (iteratively)
     *
     * @param fileToMap - file to be mapped.
     * @return JPanel containing elements allowing confirmation of assay type
     */
    private JLayeredPane createAssayUsedPanel(final String fileToMap) {

        Map<String, List<String>> measToAllowedTechnologies = menuPanels.getMain().getAllowedTechnologiesPerEndpoint();

        final AssaySelectionUI assaySelection = new AssaySelectionUI(measToAllowedTechnologies);
        assaySelection.createGUI();

        final JLayeredPane finalPanel = getGeneralLayout(selectAssaysUsedHeader, breadcrumb2, fileBeingMapped, assaySelection, getHeight());


        // by having an array of MouseListeners, we can pass this to a History object which allows the functionality specific
        // to buttons on certain pages to be transported.
        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                backButton.setIcon(back);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        HistoryComponent hc = previousPage.pop();
                        assignListenerToLabel(backButton, hc.getListeners()[0]);
                        assignListenerToLabel(nextButton, hc.getListeners()[1]);
                        setCurrentPage(hc.getDisplayComponent());

                    }
                });
            }

        };

        assignListenerToLabel(backButton, listeners[0]);


        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
                previousPage.push(new HistoryComponent(finalPanel, listeners));
                assaysToBeDefined = assaySelection.getAssaysToDefine();

                tableNameToASO = new HashMap<String, org.isatools.isacreator.formatmappingutility.AssaySelection>();

                Thread loadFileProcess = new Thread(new Runnable() {
                    public void run() {
                        // attempt to load the supplied fileToMap/directory...
                        Loader l = new Loader();
                        String fileName = null;

                        try {
                            Map<String, String[]> fileColumnMap = l.loadFile(new File(fileToMap));
                            fileColumns = null;
                            for (String file : fileColumnMap.keySet()) {
                                fileColumns = fileColumnMap.get(file);
                                fileBeingMapped = file.substring(file.lastIndexOf(File.separator) + 1);
                                fileName = file;
                                break;
                            }
                            status.setText("");
                            reader = l.getReaderToUse();
                            if (reader == FileLoader.SHEET_READER) {
                                fileName = fileToMap;
                            }
                            // we will always start off with defining the study sample file!
                            // it will be in the create mappings listeners where we
                            // will iterate around the assays to be defined.
                            setCurrentPage(createMappings(-1, getTableReferenceObject("", "[Sample]"), fileColumns, fileName, reader));
                        } catch (NoAvailableLoaderException e) {
                            setCurrentPage(lastPage);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    menuPanels.getMain().setGlassPanelContents(errorPanel);
                                }
                            });

                            log.error("No loader available for file format!");

                        } catch (MultipleExtensionsException e) {
                            setCurrentPage(lastPage);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    menuPanels.getMain().setGlassPanelContents(errorPanel);
                                }
                            });

                            log.error("There are files with different extensions in the selected folder! This is not allowed.");

                        } catch (BiffException e) {
                            setCurrentPage(lastPage);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    menuPanels.getMain().setGlassPanelContents(errorPanel);
                                }
                            });

                            log.error(e.getMessage());
                        } catch (IOException e) {
                            setCurrentPage(lastPage);

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    menuPanels.getMain().setGlassPanelContents(errorPanel);
                                }
                            });

                            log.error(e.getMessage());

                        }
                    }
                });
                lastPage = currentPage;
                setCurrentPage(workingProgressScreen);
                loadFileProcess.start();
            }
        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPanel;
    }

    private String getDescriptiveStringForFile(String fileName, int sequence) {


        StringBuffer toReturn = new StringBuffer();
        toReturn.append(fileName).append(" : mapping to ");
        if (sequence < 0) {
            toReturn.append("study sample file");
        } else {
            org.isatools.isacreator.formatmappingutility.AssaySelection aso = assaysToBeDefined.get(sequence);
            String measurement = aso.getMeasurement();
            String technology = aso.getTechnology();
            toReturn.append(measurement);
            if (!technology.equals(AssaySelectionUI.NO_TECHNOLOGY_TEXT)) {
                toReturn.append(" using ").append(technology);
            }

            toReturn.append(" to ISAtab");
        }


        return toReturn.toString();
    }

    /**
     * Create JPanel allowing users to map a column to an ISATAB column in the sample or assay files.
     *
     * @param sequence            - which sequence we are currently in with respect to defining the mappings. -1 = defining sample file.
     * @param tableReference      - TableReferenceObject to be used for the definition.
     * @param columnsToBeMappedTo - array of strings representing header value to be mapped to.
     * @param fileName            - name of file being mapped
     * @param readerToUse         - type of reader to use, @see FileLoader.CSV_READER_TXT, FileLoader.CSV_READER_CSV or FileLoader.SHEET_READER
     * @return JPanel containing the GUI to allow for mappings!
     */
    private JLayeredPane createMappings(final int sequence, final TableReferenceObject tableReference,
                                        final String[] columnsToBeMappedTo,
                                        final String fileName, final int readerToUse) throws BiffException, IOException, NoAvailableLoaderException {

        fixedMappings = sequence == -1 ? new HashMap<String, MappedElement>() : fixedMappings;

        final MappingEntryGUI mappingTableGUI = new MappingEntryGUI(tableReference, columnsToBeMappedTo, fileName, readerToUse, preExistingMapping, fixedMappings);
        mappingTableGUI.performPreliminaryLoading();
        mappingTableGUI.createGUI();
        mappingTableGUI.expandColumnToolbox();
        mappingTableGUI.setSize(new Dimension((int) (menuPanels.getWidth() * 0.80), (int) (menuPanels.getHeight() * 0.90)));
        mappingTableGUI.setBorder(null);

        ImageIcon crumb = sequence == -1 ? breadcrumb3 : breadcrumb5;

        final JLayeredPane finalPanel = getGeneralLayout(performMappingHeader,
                crumb, getDescriptiveStringForFile(fileBeingMapped, sequence), mappingTableGUI, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                backButton.setIcon(back);
                HistoryComponent hc = previousPage.pop();
                setCurrentPage(hc.getDisplayComponent());
                assignListenerToLabel(backButton, hc.getListeners()[0]);
                assignListenerToLabel(nextButton, hc.getListeners()[1]);
            }
        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                // call mapping function!
                Thread mappingThread = new Thread(new Runnable() {
                    public void run() {

                        final MappingLogic mu = new MappingLogic(mappingTableGUI.getTreeInfo(), tableReference, readerToUse);

                        TableReferenceObject populatedTRO = mu.doMapping(fileName, readerToUse);

                        // whenever we create the mappings now, we now add the mappings in a Map which
                        // point to the populated tros for each of the assays to be defined and the study
                        // sample file to be defined!
                        if (sequence == -1) {
                            fixedMappings.put("Sample Name", mappingTableGUI.getMappingNodeForField("Sample Name"));
                            definitions.put(MappingObject.STUDY_SAMPLE, populatedTRO);
                        } else {
                            tableNameToASO.put(populatedTRO.getTableName(), assaysToBeDefined.get(sequence));
                            definitions.put(populatedTRO.getTableName(), populatedTRO);
                        }

                        // at this point, we want to populate a Map relating field names
                        // with ISAFieldMappings for output at a later stage in XML format.
                        mappingsToSave.putAll(mappingTableGUI.createMappingRefs());

                        nextButton.setIcon(next);
                        previousPage.push(new HistoryComponent(finalPanel, listeners));

                        setCurrentPage(createMappingVisualization(sequence + 1, fileName, mu.getVisMapping()));
                    }
                });

                setCurrentPage(workingProgressScreen);
                mappingThread.start();

            }

        };

        assignListenerToLabel(nextButton, listeners[1]);

        return finalPanel;
    }


    /**
     * Create a visualization of the mapping showing what columns the file had and what isatab fields each of these columns have been mapped to
     *
     * @param sequence - sequence of assay processing.
     * @param filename - name to save the mapping visualization as.
     * @param mapping  - Mappings for each isatab file to the incoming column name(s) and any literals.
     * @return JLayeredPane containing the GUI to display the mapping visualization.
     */
    private JLayeredPane createMappingVisualization(final int sequence, final String filename, Map<MappingField, List<String>> mapping) {
        JPanel visContainer = new JPanel();
        visContainer.setLayout(new BoxLayout(visContainer, BoxLayout.PAGE_AXIS));
        visContainer.setSize(new Dimension((int) (menuPanels.getWidth() * 0.80), (int) (menuPanels.getHeight() * 0.90)));

        Tree t = null;

        GenerateMappingView gmv = new GenerateMappingView(filename, mapping);
        String treeFileName = gmv.generateView();

        try {
            t = (Tree) new TreeMLReader().readGraph(treeFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create a new treemap
        final TreeView tview = new TreeView(t, "name",
                new Dimension((int) (menuPanels.getWidth() * 0.80), (int) (menuPanels.getHeight() * 0.90)));

        tview.setBackground(UIHelper.BG_COLOR);
        tview.setForeground(UIHelper.GREY_COLOR);

        visContainer.add(tview);

        JLabel help_section = new JLabel(mappingVisualisationHelp);
        visContainer.add(help_section);

        ImageIcon crumb = ((sequence - 1) == -1) ? breadcrumb4 : breadcrumb6;

        final JLayeredPane finalPanel = getGeneralLayout(mappingOverviewHeader, crumb,
                "", visContainer, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                backButton.setIcon(back);

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        HistoryComponent hc = previousPage.pop();
                        setCurrentPage(hc.getDisplayComponent());
                        assignListenerToLabel(backButton, hc.getListeners()[0]);
                        assignListenerToLabel(nextButton, hc.getListeners()[1]);
                    }
                });
            }

        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
                Thread performMappingProcess = new Thread(new Runnable() {
                    public void run() {
                        try {
                            if (sequence <= assaysToBeDefined.size() - 1) {
                                org.isatools.isacreator.formatmappingutility.AssaySelection aso = assaysToBeDefined.get(sequence);
                                String nextMeasurement = aso.getMeasurement();
                                String nextTechnology = aso.getTechnology();
                                setCurrentPage(createMappings(sequence,
                                        getTableReferenceObject(nextTechnology, nextMeasurement), fileColumns, filename, reader));
                            } else {
                                setCurrentPage(createSaveMappings(filename));
                            }
                        } catch (BiffException e) {
                            setCurrentPage(lastPage);
                            log.error(e.getMessage());
                        } catch (IOException e) {
                            setCurrentPage(lastPage);
                            log.error(e.getMessage());
                        } catch (NoAvailableLoaderException e) {
                            setCurrentPage(lastPage);
                            log.error(e.getMessage());
                        }
                    }
                });
                lastPage = currentPage;
                setCurrentPage(workingProgressScreen);
                performMappingProcess.start();
            }
        };

        assignListenerToLabel(nextButton, listeners[1]);
        return finalPanel;
    }


    /**
     * Create the JPanel to allow users to save the mappings created to be used in a proceeding mapping activity!
     *
     * @return JLayeredPane containing the gui to allow a user to save the mapping file!
     */
    private JLayeredPane createSaveMappings(final String filename) {
        JPanel saveMappingFilesCont = new JPanel();
        saveMappingFilesCont.setSize(new Dimension(400, 300));
        saveMappingFilesCont.setLayout(new BoxLayout(saveMappingFilesCont, BoxLayout.PAGE_AXIS));

        saveMappingFilesCont.add(UIHelper.wrapComponentInPanel(new JLabel(saveMappingHelp, SwingConstants.CENTER)));
        saveMappingFilesCont.add(Box.createVerticalStrut(10));
        saveMappingFilesCont.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("" +
                "<html>" +
                "Please be aware, the mappings saved are saved on a field by field basis. " +
                "This means that regardless of what assay you selected to do this/these mappings," +
                " common fields amongst all assays automatically inherit the mapping information. This has a number" +
                " of benefits and a number of negative points. " +
                "<br/>" +
                "<p>The <strong>main benefit</strong> being that common field mappings are transportable regardless of assay types.<p>" +
                "<p>The <strong>main problem</strong> is that because fields are saved on a field name basis, duplicate fields like" +
                " <strong>Protocol REF</strong> columns are not saved. This mechanism for saving will be changed in the next release!</p>" +
                "<br/>" +
                "<br/>" +
                "<br/>" +
                "</html>", UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR)));

        // create selector for mapping files
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose where and what to save the file as...");
        chooser.setApproveButtonText("Select file");

        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

        JPanel selectMappingPanel = new JPanel();
        selectMappingPanel.setLayout(new BoxLayout(selectMappingPanel, BoxLayout.PAGE_AXIS));
        selectMappingPanel.setOpaque(false);

        // need a jcheckbox to ask users if they wish to use a mapping
        JPanel useMappingContainer = new JPanel(new BorderLayout());
        useMappingContainer.setOpaque(false);

        final JPanel savedMappingsPanel = new JPanel();
        savedMappingsPanel.setLayout(new BoxLayout(savedMappingsPanel, BoxLayout.PAGE_AXIS));
        savedMappingsPanel.setOpaque(false);
        savedMappingsPanel.setVisible(false);

        final JLabel saveStatusInfo = UIHelper.createLabel("", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);

        final FileSelectionPanel savedMappingsFile = new FileSelectionPanel("<html>select <strong>where</strong> to save file and <strong>it's name</strong>: </html>", chooser, FileSelectionPanel.SAVE);

        final JCheckBox useMapping = new JCheckBox("save mapping?");
        UIHelper.renderComponent(useMapping, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        useMapping.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                savedMappingsPanel.setVisible(useMapping.isSelected());
                saveStatusInfo.setText("");
            }
        });


        useMappingContainer.add(useMapping, BorderLayout.WEST);
        selectMappingPanel.add(useMappingContainer);

        // add button to save mappings
        final JLabel saveMappingsButton = new JLabel(saveMappingsButtonIcon);
        saveMappingsButton.setVerticalAlignment(JLabel.BOTTOM);
        saveMappingsButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                saveMappingsButton.setIcon(saveMappingsButtonIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                saveMappingsButton.setIcon(saveMappingsButtonIcon);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                // save the mapping

                MappingXMLCreator mappingCreator = new MappingXMLCreator();
                try {
                    if (useMapping.isSelected()) {
                        if (!savedMappingsFile.getSelectedFilePath().equals("")) {
                            nextButton.setEnabled(false);
                            backButton.setEnabled(false);
                            mappingCreator.createXMLFile(savedMappingsFile.getSelectedFilePath(), mappingsToSave);
                            saveStatusInfo.setText("mappings saved successfully...");
                        } else {
                            saveStatusInfo.setText("please select a file...");
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    saveStatusInfo.setText("mappings not saved..." + e.getMessage());
                } finally {
                    nextButton.setEnabled(true);
                    backButton.setEnabled(true);
                }
            }

        });


        final JLayeredPane finalPanel = getGeneralLayout(saveMappingsHeader, breadcrumb7,
                "", saveMappingFilesCont, getHeight());

        final MouseListener[] listeners = new MouseListener[2];

        listeners[0] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(back);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                backButton.setIcon(back);
                saveStatusInfo.setText("");
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        HistoryComponent hc = previousPage.pop();
                        setCurrentPage(hc.getDisplayComponent());
                        assignListenerToLabel(backButton, hc.getListeners()[0]);
                        assignListenerToLabel(nextButton, hc.getListeners()[1]);
                    }
                });
            }

        };

        assignListenerToLabel(backButton, listeners[0]);

        listeners[1] = new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                nextButton.setIcon(nextOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                nextButton.setIcon(next);
            }

            public void mousePressed(MouseEvent mouseEvent) {

                nextButton.setIcon(next);
                Thread performMappingLogic = new Thread(new Runnable() {
                    public void run() {
                        inv = MappingLogic.createInvestigation(definitions, tableNameToASO, dep);
                        // now we need to construct the investigation from the defined table reference objects and the
                        inv.setUserInterface(new InvestigationDataEntry(inv, dep));
                        dep.createGUIFromSource(inv);

                        previousPage.push(new HistoryComponent(finalPanel, listeners));
                        menuPanels.getMain().hideGlassPane();
                        menuPanels.getMain().setCurDataEntryPanel(dep);
                        menuPanels.getMain().setCurrentPage(dep);
                        // todo clear object space
                    }
                });

                setCurrentPage(workingProgressScreen);
                performMappingLogic.start();
            }
        };

        JPanel saveMappingsSection = new JPanel();
        saveMappingsSection.setLayout(new BoxLayout(saveMappingsSection, BoxLayout.LINE_AXIS));

        saveMappingsSection.add(savedMappingsFile);
        saveMappingsSection.add(Box.createHorizontalStrut(10));

        JPanel saveButtonContainer = new JPanel(new BorderLayout());
        saveButtonContainer.add(saveMappingsButton, BorderLayout.SOUTH);

        saveMappingsSection.add(UIHelper.wrapComponentInPanel(saveButtonContainer));

        savedMappingsPanel.add(saveMappingsSection);


        selectMappingPanel.add(savedMappingsPanel);

        saveMappingFilesCont.add(selectMappingPanel);


        saveMappingFilesCont.add(UIHelper.wrapComponentInPanel(saveStatusInfo));

        assignListenerToLabel(nextButton, listeners[1]);
        return finalPanel;

    }

    private void setupErrorPanel() {
        errorPanel = new ErrorDisplay();
        errorPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                menuPanels.getMain().getGlassPane().setVisible(false);
            }
        });

    }
}
