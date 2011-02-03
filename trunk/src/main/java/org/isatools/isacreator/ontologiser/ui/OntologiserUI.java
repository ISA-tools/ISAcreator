package org.isatools.isacreator.ontologiser.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.gui.ISAcreator;
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 11:13
 */
public class OntologiserUI extends JFrame {

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

    private JPanel swappableContainer;

    private JLabel termTaggerButton;
    // functions
    private JLabel visualiseButton;
    private JLabel suggestButton;
    private JLabel clearAllButton;

    private JLabel helpButton;

    private int selectedSection = HELP;

    @InjectedResource
    private ImageIcon termTaggerLogo, termTaggerIcon, termTaggerIconOver, visualiseInactiveIcon, visualiseIcon, visualiseIconOver,
            suggestInactiveIcon, suggestIcon, suggestIconOver, clearAllInactiveIcon, clearAllIcon, clearAllIconOver, helpIcon, helpIconOver, closeWindowIcon,
            closeWindowIconOver, doneIcon, doneIconOver, buttonPanelFiller, working;

    private OntologiserAnnotationPane annotationPane;
    private OntologyHelpPane helpPane;
    private ConfirmationDialog confirmChoice;
    private ISAcreator isacreatorEnvironment;
    private String currentAssay;

    public OntologiserUI(ISAcreator isacreatorEnvironment, String currentAssay) {
        this.isacreatorEnvironment = isacreatorEnvironment;
        this.currentAssay = currentAssay;
    }

    public void createGUI() {
        ResourceInjector.get("ontologiser-generator-package.style").inject(this);

        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(650, 500));
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));


        add(createTopPanel(), BorderLayout.NORTH);

        swappableContainer = new JPanel();
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(650, 350));
        helpPane = new OntologyHelpPane();
        helpPane.createGUI();

        swappableContainer.add(helpPane);

        add(swappableContainer, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
        setVisible(true);

    }

    private Container createTopPanel() {
        Box topPanel = Box.createHorizontalBox();

        termTaggerButton = new JLabel(termTaggerIcon);
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

                selectedSection = TERM_TAGGER_VIEW;


                Thread performer = new Thread(new Runnable() {
                    public void run() {
                        if (annotationPane == null) {
                            // todo call API module to get all unannotated Ontology entries from the spreadsheet

                            annotationPane = new OntologiserAnnotationPane(getTestData());

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
                                visualiseButton.setIcon(visualiseIcon);
                                suggestButton.setIcon(suggestIcon);
                                clearAllButton.setIcon(clearAllIcon);
                            }
                        });
                    }

                });

                swapContainers(UIHelper.wrapComponentInPanel(new JLabel(working)));
                performer.start();
            }
        });

        visualiseButton = new JLabel(visualiseInactiveIcon);
        visualiseButton.setHorizontalAlignment(SwingConstants.LEFT);
        visualiseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                visualiseButton.setIcon((selectedSection == TERM_TAGGER_VIEW || selectedSection == VISUALISATION) ? visualiseIconOver : visualiseInactiveIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                visualiseButton.setIcon((selectedSection == TERM_TAGGER_VIEW || selectedSection == VISUALISATION) ? visualiseIcon : visualiseInactiveIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (visualiseButton.getIcon() != visualiseInactiveIcon) {

                    visualiseButton.setIcon(visualiseIcon);
                    selectedSection = VISUALISATION;
                    // todo show visualisation...
                }
            }
        });


        suggestButton = new JLabel(suggestInactiveIcon);
        suggestButton.setHorizontalAlignment(SwingConstants.LEFT);
        suggestButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                suggestButton.setIcon(selectedSection == TERM_TAGGER_VIEW ? suggestIconOver : suggestInactiveIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                suggestButton.setIcon(selectedSection == TERM_TAGGER_VIEW ? suggestIcon : suggestInactiveIcon);
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
                clearAllButton.setIcon(selectedSection == TERM_TAGGER_VIEW ? clearAllIconOver : clearAllInactiveIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                clearAllButton.setIcon(selectedSection == TERM_TAGGER_VIEW ? clearAllIcon : clearAllInactiveIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (clearAllButton.getIcon() != clearAllInactiveIcon) {
                    clearAllButton.setIcon(clearAllIcon);
                    annotationPane.clearAnnotation();
                }
            }
        });

        helpButton = new JLabel(helpIconOver);

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
                        confirmChoice.showDialog(OntologiserUI.this);
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
                        // todo do String replace in current Spreadsheet or all spreadsheets (give option)
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

    public static void main(String[] args) {
        OntologiserUI ui = new OntologiserUI(null, "assay");
        ui.createGUI();
    }

    public Map<String, Map<String, AnnotatorResult>> getTestData() {

        AnnotatorSearchClient sc = new AnnotatorSearchClient();

        Set<String> testTerms = new HashSet<String>();
        testTerms.add("CY3");
        testTerms.add("DOSE");
        testTerms.add("ASSAY");

        return sc.searchForTerms(testTerms);
    }


}
