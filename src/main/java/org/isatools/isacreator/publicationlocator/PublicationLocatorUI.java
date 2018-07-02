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

package org.isatools.isacreator.publicationlocator;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.DraggablePaneMouseInputHandler;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.model.Publication;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.Map;

/**
 * PublicationLocatorUI
 *
 * @author eamonnmaguire
 * @date Oct 11, 2010
 */


public class PublicationLocatorUI extends JFrame implements WindowListener {

    private static final int PUBMED_SEARCH = 0;
    private static final int DOI_SEARCH = 1;
    private static final int RESULT = 2;

    private static InfiniteProgressPanel progressIndicator;

    private static CiteExploreClient pubExplorer;

    private Publication currentPublication;

    @InjectedResource
    private ImageIcon searchBy, pubmedOption, pubmedOptionOver, doiOption, doiOptionOver, resultInactive, result,
            resultOver, end, search, searchOver, pubmedText, doiText, searchFieldLeft;

    private JPanel swappableContainer;

    private JLabel searchTypeLabel;
    private JTextField searchField;

    private Container searchContainer;
    private SearchResultPane resultPane = new SearchResultPane();

    private JLabel pubmedButton, doiButton, resultButton;
    private JButton export = null;

    private int selectedSection = PUBMED_SEARCH;
    private DataEntryForm parent;

    public PublicationLocatorUI(DataEntryForm parent) {
        this.parent = parent;
        ResourceInjector.get("publicationlocator-package.style").inject(this);

    }

    public void createGUI() {
        setBackground(UIHelper.BG_COLOR);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(499, 400));
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        progressIndicator = new InfiniteProgressPanel(
                "searching citexplore");

        add(createTopPanel(), BorderLayout.NORTH);

        swappableContainer = new JPanel();
        swappableContainer.setPreferredSize(new Dimension(499, 270));
        swappableContainer.add(createSearchPanel());

        add(swappableContainer, BorderLayout.CENTER);
        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    private Container createSouthPanel() {

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(UIHelper.EMPTY_BORDER);
        southPanel.setBackground(UIHelper.BG_COLOR);

        JButton closeButton = new FlatButton(ButtonType.RED, "Cancel");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                firePropertyChange("noSelectedPublication", "noneSelected", "");
                setVisible(false);
            }
        });


        export = new FlatButton(ButtonType.GREEN, "Select Publication");
        export.setEnabled(false);
        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                firePropertyChange("selectedPublication", "OLD_VALUE",
                        currentPublication);
                setVisible(false);
            }
        });

        southPanel.add(closeButton, BorderLayout.WEST);
        southPanel.add(export, BorderLayout.EAST);

        return southPanel;
    }

    private Container createTopPanel() {

        Box topContainer = Box.createVerticalBox();

        Box topPanel = Box.createHorizontalBox();

        JLabel mgRastLogo = new JLabel(searchBy);
        mgRastLogo.setHorizontalAlignment(SwingConstants.LEFT);

        pubmedButton = new JLabel(pubmedOptionOver);
        pubmedButton.setHorizontalAlignment(SwingConstants.LEFT);
        pubmedButton.setVerticalAlignment(SwingConstants.TOP);

        pubmedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                pubmedButton.setIcon(pubmedOptionOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

                pubmedButton.setIcon(selectedSection == PUBMED_SEARCH ? pubmedOptionOver : pubmedOption);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                pubmedButton.setIcon(pubmedOptionOver);
                selectedSection = PUBMED_SEARCH;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        searchTypeLabel.setIcon(pubmedText);
                        swapContainers(searchContainer);
                    }
                });

            }
        });

        doiButton = new JLabel(doiOption);
        doiButton.setHorizontalAlignment(SwingConstants.LEFT);

        doiButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                doiButton.setIcon(doiOptionOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

                doiButton.setIcon(selectedSection == DOI_SEARCH ? doiOptionOver : doiOption);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                resetButtons();
                doiButton.setIcon(doiOptionOver);
                selectedSection = DOI_SEARCH;

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        searchTypeLabel.setIcon(doiText);
                        swapContainers(searchContainer);
                    }
                });
            }
        });

        resultButton = new JLabel(resultInactive);
        resultButton.setHorizontalAlignment(SwingConstants.LEFT);

        resultButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (resultButton.getIcon() != resultInactive) {
                    resultButton.setIcon(resultOver);
                }
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                if (resultButton.getIcon() != resultInactive) {
                    resultButton.setIcon(selectedSection == RESULT ? resultOver : result);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (resultButton.getIcon() != resultInactive) {
                    resetButtons();
                    selectedSection = RESULT;
                    resultButton.setIcon(resultOver);
                    swapContainers(resultPane);
                }
            }
        });

        topPanel.add(mgRastLogo);
        topPanel.add(pubmedButton);
        topPanel.add(doiButton);
        topPanel.add(resultButton);
        topPanel.add(new JLabel(end));

        topContainer.add(topPanel);

        return topContainer;
    }

    private Container createSearchPanel() {
        searchContainer = Box.createVerticalBox();
        searchContainer.setBackground(UIHelper.BG_COLOR);

        Box textContainer = Box.createHorizontalBox();

        searchTypeLabel = new JLabel(pubmedText);

        textContainer.add(searchTypeLabel);

        searchField = new JTextField();

        Action searchPublications = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        };

        searchField.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "SEARCH_PUB");
        searchField.getActionMap().put("SEARCH_PUB", searchPublications);

        UIHelper.renderComponent(searchField, UIHelper.VER_12_BOLD, UIHelper.LIGHT_GREEN_COLOR, UIHelper.BG_COLOR);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(new EmptyBorder(2, 2, 2, 2));
        searchField.setText("enter id");

        textContainer.add(Box.createHorizontalStrut(5));
        textContainer.add(new JLabel(searchFieldLeft));
        textContainer.add(searchField);
        textContainer.add(Box.createHorizontalStrut(20));

        final JLabel searchButton = new JLabel(search);
        searchButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                searchButton.setIcon(searchOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                searchButton.setIcon(search);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                searchButton.setIcon(search);
                performSearch();
            }
        });

        textContainer.add(searchButton);

        searchContainer.add(Box.createVerticalStrut(100));
        searchContainer.add(textContainer);
        searchContainer.add(Box.createVerticalGlue());

        return searchContainer;
    }

    private void performSearch() {

        Thread performer = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("starting search");
                    if (!searchField.getText().equals("")) {
                        progressIndicator.setSize(new Dimension(
                                getWidth(),
                                getHeight()));
                        setGlassPane(progressIndicator);
                        progressIndicator.start();
                        PublicationLocatorUI.this.validate();

                        SearchOption so = selectedSection == PUBMED_SEARCH ? SearchOption.PUBMED : SearchOption.DOI;

//                        Map<String, Publication> result = pubExplorer.getPublication(so, searchField.getText(), parent);

//                        for (String key : result.keySet()) {
//                            currentPublication = result.get(key);
//                            // push to SearchResultPane
//                            resetButtons();
//                            resultPane.showPublication(currentPublication);
//                            selectedSection = RESULT;
//                            swapContainers(resultPane);
//                            resultButton.setIcon(resultOver);
//                            export.setEnabled(true);
//                            break;
//                        }
                        throw new NoPublicationFoundException(so, searchField.getText());
                    }
                } catch (NoPublicationFoundException e) {
                    resetButtons();
                    selectedSection = RESULT;
                    resultPane.showError();
                    swapContainers(resultPane);
                    resultButton.setIcon(resultOver);
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (progressIndicator.isStarted()) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                progressIndicator.stop();
                            }
                        });
                    }
                }
            }
        });


        try {
            pubExplorer = new CiteExploreClient();
        } catch (Exception e) {
            pubExplorer = null;
        }


        if (pubExplorer != null) {
            performer.start();
        } else {
            resultPane.showError();
        }
    }

    private void resetButtons() {
        pubmedButton.setIcon(pubmedOption);
        doiButton.setIcon(doiOption);
        if (resultButton.getIcon() != resultInactive) {
            resultButton.setIcon(result);
        }
    }

    public void installListeners() {
        MouseInputAdapter handler = new DraggablePaneMouseInputHandler(this);
        Window window = this;
        window.addMouseListener(handler);
        window.addMouseMotionListener(handler);
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
        Toolkit.getDefaultToolkit().setDynamicLayout(false);
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        validate();
        repaint();
    }

    public void windowDeactivated(WindowEvent event) {
        firePropertyChange("noSelectedPublication", "noneSelected", "");
    }
}