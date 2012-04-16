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


package org.isatools.isacreator.archiveoutput;

import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.optionselector.OptionGroup;
import org.isatools.isacreator.optionselector.OptionItem;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class ArchiveOutputWindow extends JFrame implements WindowListener {
    public static final int WIDTH = 300;
    public static final int HEIGHT = 350;

    @InjectedResource
    private Image headerImage;

    @InjectedResource
    private ImageIcon chooseCompression, fastLowCompressionSelected, fastLowCompressionNotSelected,
            medCompressionSelected, medCompressionNotSelected, slowHighCompressionSelected, slowHighCompressionNotSelected,
            chooseOutputLocation, openDirectory, wait, saveArchive, saveArchiveOver;

    private ImageIcon creatingArchiveAnimation = new ImageIcon(getClass().getResource("/images/gui/create_archive_progress.gif"));

    private JPanel swappableContainer = new JPanel(new BorderLayout());

    private FileSelectionPanel outputFileLocation;
    private JFileChooser fileChooser;
    private JPanel exportingProgressContainer;
    private JLabel selectedCompression;
    private JLabel createArchiveButton;
    private Timer closeWindowTimer;
    private final ArchiveOutputUtil archiveOutputUtil;

    public ArchiveOutputWindow() {
        ResourceInjector.get("archiveoutput-package.style").inject(this);
        setUndecorated(true);
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        closeWindowTimer = new Timer(4000, new CloseEvent());
        archiveOutputUtil = new ArchiveOutputUtil();
    }

    public void createGUI() {
        HUDTitleBar titlePanel = new HUDTitleBar(headerImage, headerImage);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        createProgressScreen();

        JPanel exportOptionContainer = new JPanel();
        exportOptionContainer.setLayout(new BoxLayout(exportOptionContainer, BoxLayout.PAGE_AXIS));

        exportOptionContainer.add(createCompressionRateChoiceUI());
        exportOptionContainer.add(Box.createVerticalStrut(20));
        exportOptionContainer.add(createSelectOutputDirectoryUI());
        exportOptionContainer.add(Box.createVerticalStrut(20));
        exportOptionContainer.add(createCreateArchiveButtonUI());

        swappableContainer.add(exportOptionContainer, BorderLayout.CENTER);
        add(swappableContainer, BorderLayout.CENTER);

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2, true));

        addWindowListener(this);
        pack();
        setVisible(true);
    }

    public void createProgressScreen() {
        exportingProgressContainer = new JPanel();
        exportingProgressContainer.setSize(new Dimension(this.getWidth(), this.getHeight()));
        exportingProgressContainer.setLayout(new BorderLayout());
        exportingProgressContainer.setBackground(UIHelper.BG_COLOR);

        exportingProgressContainer.add(UIHelper.wrapComponentInPanel(
                new JLabel(creatingArchiveAnimation, JLabel.CENTER)), BorderLayout.CENTER);
        exportingProgressContainer.add(UIHelper.wrapComponentInPanel(archiveOutputUtil.getArchiveOutputStatus()), BorderLayout.SOUTH);
    }

    private JPanel createCompressionRateChoiceUI() {
        JPanel compressionOptionContainer = new JPanel(new BorderLayout());

        selectedCompression = UIHelper.createLabel("", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, JLabel.CENTER);

        OptionGroup<String> compressionOptions = new OptionGroup<String>(OptionGroup.HORIZONTAL_ALIGNMENT, true, 90);
        compressionOptions.addOptionItem("<html><strong>fast</strong> speed & <strong>low</strong> compression</html>", true, fastLowCompressionSelected, fastLowCompressionNotSelected, true, false);
        compressionOptions.addOptionItem("<html><strong>medium</strong> speed & <strong>medium</strong> compression</html>", false, medCompressionSelected, medCompressionNotSelected, true, false);
        compressionOptions.addOptionItem("<html><strong>slow</strong> speed & <strong>high</strong> compression</html>", false, slowHighCompressionSelected, slowHighCompressionNotSelected, true, false);
        compressionOptionContainer.add(new JLabel(chooseCompression, JLabel.LEFT), BorderLayout.NORTH);
        compressionOptionContainer.add(compressionOptions, BorderLayout.CENTER);

        compressionOptions.addPropertyChangeListener("optionSelectionChange", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                selectedCompression.setText(((OptionItem<String>) propertyChangeEvent.getNewValue()).getUserObject());
            }
        });

        compressionOptionContainer.add(selectedCompression, BorderLayout.SOUTH);

        selectedCompression.setText(compressionOptions.getSelectedItem());

        return compressionOptionContainer;
    }

    private JPanel createCreateArchiveButtonUI() {
        JPanel waitContainer = new JPanel(new BorderLayout());

        createArchiveButton = new JLabel(saveArchive);
        createArchiveButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

                if (createArchiveButton.isEnabled()) {
                    createArchiveButton.setIcon(saveArchive);
                }
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                if (createArchiveButton.isEnabled()) {
                    createArchiveButton.setIcon(saveArchiveOver);
                }
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                System.out.println("Selected file path..." + outputFileLocation.getSelectedFilePath());
                if (!outputFileLocation.getSelectedFilePath().isEmpty()) {
                    createArchive();
                } else {
                    outputFileLocation.setWarning(true);
                }
            }
        });
        waitContainer.add(UIHelper.wrapComponentInPanel(createArchiveButton), BorderLayout.WEST);

        return waitContainer;
    }

    private void createArchive() {

        System.out.println("Creating archive..");
        swapContainers(exportingProgressContainer);

        attachListenersForArchiveOutputUtil();
        archiveOutputUtil.setOutputLocation(outputFileLocation.getSelectedFilePath());
        archiveOutputUtil.setCompressionLevel(getCompressionLevel());
        final Thread archiveCreation = new Thread(archiveOutputUtil);
        archiveCreation.start();
    }

    private JPanel createSelectOutputDirectoryUI() {

        JPanel container = new JPanel(new BorderLayout());
        setupFileChooser();

        outputFileLocation = new FileSelectionPanel("", fileChooser);

        Box fileLocationContainer = Box.createVerticalBox();
        fileLocationContainer.add(UIHelper.wrapComponentInPanel(new JLabel(chooseOutputLocation, JLabel.LEFT)));
        fileLocationContainer.add(Box.createVerticalStrut(5));
        fileLocationContainer.add(outputFileLocation);

        container.add(fileLocationContainer, BorderLayout.NORTH);

        return container;
    }

    private void attachListenersForArchiveOutputUtil() {
        archiveOutputUtil.addPropertyChangeListener("archiveOutputFailed", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                // if the output fails, tell the user why it failed and point to the error report
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        OutputFailedUI failUI = new OutputFailedUI(archiveOutputUtil.getArchiveOutputStatus().getText());

                        failUI.addPropertyChangeListener("viewErrors", new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                                ViewErrorUI enw = new ViewErrorUI(archiveOutputUtil.getBrowseViewErrorPane());
                                enw.setLocation(getX(), getY());
                                dispose();
                            }
                        });
                        swapContainers(failUI);
                    }
                });
            }
        });

        archiveOutputUtil.addPropertyChangeListener("archiveOutputCompleted", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                // if the output completes, tell the user it has completed and where the files were saved,
                // then automatically close the window after 3 seconds :)

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        OutputSuccessUI successUI = new OutputSuccessUI(archiveOutputUtil.getStatistics().getStatisticsAsHTML());
                        swapContainers(successUI);
                    }
                });
                closeWindowTimer.start();
            }
        });
    }


    private void swapContainers(final JPanel newContainer) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (newContainer != null) {
                    swappableContainer.removeAll();
                    swappableContainer.add(newContainer);
                    swappableContainer.repaint();
                    swappableContainer.validate();
                }
            }
        });
    }

    private void setupFileChooser() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select directory to output ISArchive to...");
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setApproveButtonText("Output to selected directory");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }


    private int getCompressionLevel() {
        String compressionType = selectedCompression.getText();
        if (compressionType.contains("fast")) {
            return ArchiveOutputUtil.LOW_COMPRESSION;
        } else if (compressionType.contains("medium")) {
            return ArchiveOutputUtil.MED_COMPRESSION;
        } else {
            return ArchiveOutputUtil.HIGH_COMPRESSION;
        }
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
        dispose();
    }

    public void windowIconified(WindowEvent windowEvent) {
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }

    class CloseEvent implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            ArchiveOutputWindow.this.dispose();
            if (closeWindowTimer != null) {
                closeWindowTimer.stop();
                closeWindowTimer = null;
            }
        }
    }

}
