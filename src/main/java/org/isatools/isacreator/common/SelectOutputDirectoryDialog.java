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

package org.isatools.isacreator.common;

import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * SelectOutputDirectoryDialog
 *
 * @author eamonnmaguire
 * @date Oct 1, 2010
 */


public class SelectOutputDirectoryDialog extends JFrame {

    public static final String CANCEL = "cancel";
    public static final String CONTINUE = "continue";

    private static final int WIDTH = 300;
    private static final int HEIGHT = 200;

    @InjectedResource
    private ImageIcon panelHeader, cancelIcon, cancelIconOver, continueIcon, continueIconOver;

    public SelectOutputDirectoryDialog() {
        this(null);
    }

    public SelectOutputDirectoryDialog(ImageIcon topImage) {
        ResourceInjector.get("common-package.style").inject(this);

        if(topImage != null) {
            this.panelHeader = topImage;
        }
    }

    public void createGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setLayout(new BorderLayout());
                setPreferredSize(new Dimension(WIDTH, HEIGHT));

                setAlwaysOnTop(true);
                ((JComponent) getContentPane())
                        .setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));
                instantiateFrame();
                pack();
            }
        });
    }

    private void instantiateFrame() {
        Box container = Box.createVerticalBox();

        JLabel confirmation1 = new JLabel(panelHeader);

        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.setApproveButtonText("Output here");
        jfc.setDialogTitle("Select output directory");
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);

        final FileSelectionPanel fileSelector = new FileSelectionPanel("Choose save location", jfc);

        final JLabel cancelButton = new JLabel(cancelIconOver);
        cancelButton.setHorizontalAlignment(SwingConstants.RIGHT);
        cancelButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                cancelButton.setIcon(cancelIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                cancelButton.setIcon(cancelIconOver);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                cancelButton.setIcon(cancelIconOver);
                firePropertyChange(CANCEL, "cancelPressed", "");

            }
        });

        final JLabel continueButton = new JLabel(continueIconOver);
        continueButton.setHorizontalAlignment(SwingConstants.RIGHT);
        continueButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                continueButton.setIcon(continueIcon);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                continueButton.setIcon(continueIconOver);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                continueButton.setIcon(continueIconOver);
                if (fileSelector.getSelectedFilePath().trim().equals("") || fileSelector.getSelectedFilePath().equals("Please select a directory...")) {
                    fileSelector.setText("Please select a directory...");
                } else {
                    firePropertyChange(CONTINUE, "continuePressed", fileSelector.getSelectedFilePath());
                }

            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(cancelButton, BorderLayout.WEST);
        buttonPanel.add(continueButton, BorderLayout.EAST);

        container.add(UIHelper.wrapComponentInPanel(confirmation1));
        container.add(Box.createVerticalStrut(10));
        container.add(fileSelector);
        container.add(Box.createVerticalStrut(20));
        container.add(buttonPanel);

        add(container);
    }

    public void showDialog(Container parent) {

        Point parentLoc = parent.getLocation();
        Dimension parentDim = parent.getSize();
        setLocation(new Point(parentLoc.x + ((parentDim.width / 2) - (WIDTH / 2)),
                parentLoc.y + ((parentDim.height / 2) - (HEIGHT / 2))));

        setUndecorated(true);
        setVisible(true);
    }

    public void hideDialog() {
        setVisible(false);
    }
}
