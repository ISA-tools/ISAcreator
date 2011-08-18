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
package org.isatools.isacreator.ontologiser.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.ontologiser.adaptors.ContentAdaptor;
import org.isatools.isacreator.ontologiser.logic.impl.AnnotatorSearchClient;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 11:13
 */
public class OntologiserUI extends JDialog {

    public static final int TERM_TAGGER_VIEW = 0;
    public static final int HELP = 1;
    public static final int VISUALISATION = 2;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("ontologiser-generator-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/ontologiser-generator-package.properties"));
        ResourceInjector.get("formatmappingutility-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/formatmappingutility-package.properties"));
        ResourceInjector.get("common-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("ontologyselectiontool-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/ontologyselectiontool-package.properties"));

    }

    private boolean isLoading = false;

    private JPanel swappableContainer;

    private JLabel termTaggerButton;
    // functions
    private JLabel visualiseButton;
    private JLabel suggestButton;
    private JLabel clearAllButton;

    private JLabel helpButton;

    private ContentAdaptor content;

    private int selectedSection = HELP;

    private Map<String, Map<String, AnnotatorResult>> terms;

    @InjectedResource
    private ImageIcon termTaggerLogo, termTaggerIcon, termTaggerIconOver, visualiseInactiveIcon, visualiseIcon, visualiseIconOver,
            suggestInactiveIcon, suggestIcon, suggestIconOver, clearAllInactiveIcon, clearAllIcon, clearAllIconOver, helpIcon, helpIconOver, closeWindowIcon,
            closeWindowIconOver, doneIcon, doneIconOver, buttonPanelFiller, working;

    private OntologiserAnnotationPane annotationPane;
    private OntologyHelpPane helpPane;
    private ConfirmationDialog confirmChoice;
    private ISAcreator isacreatorEnvironment;


    public OntologiserUI(ISAcreator isacreatorEnvironment, ContentAdaptor content) {
        this.isacreatorEnvironment = isacreatorEnvironment;
        this.content = content;
    }

    public void createGUI() {
        ResourceInjector.get("ontologiser-generator-package.style").inject(this);

        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(650, 500));
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        add(createTopPanel(), BorderLayout.NORTH);

        swappableContainer = new JPanel();
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(650, 350));

        tagTerms();

        add(swappableContainer, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    private Container createTopPanel() {
        Box topPanel = Box.createHorizontalBox();

        termTaggerButton = new JLabel(termTaggerIconOver);
        termTaggerButton.setHorizontalAlignment(SwingConstants.LEFT);
        termTaggerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                termTaggerButton.setIcon(termTaggerIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                termTaggerButton.setIcon(selectedSection == TERM_TAGGER_VIEW ? termTaggerIconOver : termTaggerIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                termTaggerButton.setIcon(termTaggerIconOver);
                visualiseButton.setIcon(visualiseInactiveIcon);
                suggestButton.setIcon(suggestInactiveIcon);
                clearAllButton.setIcon(clearAllInactiveIcon);

                tagTerms();
            }
        });

        visualiseButton = new JLabel(visualiseInactiveIcon);
        visualiseButton.setHorizontalAlignment(SwingConstants.LEFT);
        visualiseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }
        });


        suggestButton = new JLabel(suggestInactiveIcon);
        suggestButton.setHorizontalAlignment(SwingConstants.LEFT);
        suggestButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                suggestButton.setIcon((selectedSection == TERM_TAGGER_VIEW && !isLoading) ? suggestIconOver : suggestInactiveIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                suggestButton.setIcon((selectedSection == TERM_TAGGER_VIEW && !isLoading) ? suggestIcon : suggestInactiveIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (suggestButton.getIcon() != suggestInactiveIcon) {

                    suggestButton.setIcon(suggestIcon);
                    annotationPane.autoAnnotate();
                }
            }
        });

        clearAllButton = new JLabel(clearAllInactiveIcon);
        clearAllButton.setHorizontalAlignment(SwingConstants.LEFT);
        clearAllButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                clearAllButton.setIcon((selectedSection == TERM_TAGGER_VIEW && !isLoading) ? clearAllIconOver : clearAllInactiveIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                clearAllButton.setIcon((selectedSection == TERM_TAGGER_VIEW && !isLoading) ? clearAllIcon : clearAllInactiveIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (clearAllButton.getIcon() != clearAllInactiveIcon) {
                    clearAllButton.setIcon(clearAllIcon);
                    annotationPane.clearAnnotation();
                }
            }
        });

        helpButton = new JLabel(helpIcon);

        helpButton.setHorizontalAlignment(SwingConstants.LEFT);
        helpButton.addMouseListener(new

                MouseAdapter() {
                    @Override
                    public void mouseEntered
                            (MouseEvent
                                     mouseEvent) {
                        helpButton.setIcon(helpIconOver);
                    }

                    @Override
                    public void mouseExited
                            (MouseEvent mouseEvent) {
                        helpButton.setIcon(selectedSection == HELP ? helpIconOver : helpIcon);
                    }

                    @Override
                    public void mousePressed
                            (MouseEvent
                                     mouseEvent) {
                        resetButtons();
                        selectedSection = HELP;
                        helpButton.setIcon(helpIconOver);

                        if (helpPane == null) {
                            helpPane = new OntologyHelpPane();
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
                }

        );

        topPanel.add(termTaggerButton);
        topPanel.add(visualiseButton);
        topPanel.add(suggestButton);
        topPanel.add(clearAllButton);
        topPanel.add(helpButton);
        topPanel.add(new JLabel(termTaggerLogo));

        return topPanel;
    }

    private Container createSouthPanel() {
        Box southPanel = Box.createHorizontalBox();

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

        final JLabel export = new JLabel(doneIcon);
        export.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                export.setIcon(doneIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                export.setIcon(doneIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                Thread performer = new Thread(new Runnable() {
                    public void run() {
                        if (annotationPane != null) {
                            content.replaceTerms(annotationPane.getAnnotations());
                        }
                        // todo show summary page stating what has been replaced.
                        closeWindow();
                    }

                });
                swapContainers(UIHelper.wrapComponentInPanel(new JLabel(working)));
                performer.start();
            }
        });

        southPanel.add(closeButton);
        southPanel.add(new JLabel(buttonPanelFiller));
        southPanel.add(export);

        return southPanel;
    }

    private void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isacreatorEnvironment.hideSheet();
            }
        });
    }

    private void resetButtons() {
        termTaggerButton.setIcon(termTaggerIcon);
        helpButton.setIcon(helpIcon);

        visualiseButton.setIcon(visualiseInactiveIcon);
        suggestButton.setIcon(suggestInactiveIcon);
        clearAllButton.setIcon(clearAllInactiveIcon);

    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }

    private void tagTerms() {
        selectedSection = TERM_TAGGER_VIEW;

        Thread performer = new Thread(new Runnable() {
            public void run() {

                if (terms == null) {
                    terms = getTerms();
                }


                boolean haveTerms = terms != null;

                if (haveTerms) {

                    if (annotationPane == null) {

                        annotationPane = new OntologiserAnnotationPane(terms);

                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                annotationPane.createGUI();
                            }
                        });
                    }

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            swapContainers(annotationPane);

                            termTaggerButton.setIcon(termTaggerIconOver);
                            suggestButton.setIcon(suggestIcon);
                            clearAllButton.setIcon(clearAllIcon);
                        }
                    });
                } else {
                    // todo add info pane saying there are no terms to annotate
                    swapContainers(helpPane);
                }

                isLoading = false;
            }

        });
        isLoading = true;
        swapContainers(UIHelper.wrapComponentInPanel(new JLabel(working)));
        performer.start();
    }

    public Map<String, Map<String, AnnotatorResult>> getTerms() {

        AnnotatorSearchClient sc = new AnnotatorSearchClient();

        if (content != null && content.getTerms().size() > 0) {
            return sc.searchForTerms(content.getTerms());
        }
        return null;

    }


}
