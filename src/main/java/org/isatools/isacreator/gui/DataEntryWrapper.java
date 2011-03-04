/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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

package org.isatools.isacreator.gui;

import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;

/**
 * Utility method used to create the type of GUI which can switch central panels.
 */


public abstract class DataEntryWrapper extends DataEntryForm {

    protected Component currentPage = null;
    public static JLabel backButton;
    public static JLabel nextButton;

    private JLabel infoLabel;

    public static ImageIcon back = new ImageIcon(DataEntryWrapper.class.getResource("/images/common/back.png"));
    public static ImageIcon backOver = new ImageIcon(DataEntryWrapper.class.getResource("/images/common/back_over.png"));
    public static ImageIcon next = new ImageIcon(DataEntryWrapper.class.getResource("/images/common/next.png"));
    public static ImageIcon nextOver = new ImageIcon(DataEntryWrapper.class.getResource("/images/common/next_over.png"));
    public static ImageIcon wizard = new ImageIcon(DataEntryWrapper.class.getResource("/images/wizard/exitwizard.png"));
    public static ImageIcon wizardOver = new ImageIcon(DataEntryWrapper.class.getResource("/images/wizard/exitwizard_over.png"));

    protected DataEntryWrapper() {
        setLayout(new BorderLayout());
    }

    /**
     * Changes JLayeredPane being shown in the center panel
     *
     * @param newPage - JLayeredPane to change to
     */
    public void setCurrentPage(final Component newPage) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (currentPage == null) {
                    currentPage = newPage;
                } else {
                    // remove current component from view
                    remove(currentPage);
                    currentPage = newPage;
                }
                // add new page to view, and put in Center of Pane
                add(currentPage, BorderLayout.CENTER);

                repaint();
                validate();

            }
        });
    }

    /**
     * creates the west panel with a logo at the top andn and info image on the side to provide the user with some
     * more information about a particular topic.
     *
     * @param logo - image to put at the top left hand corner of the screen
     * @param info - image to put in the center left position of the screen to give the user a little more information.
     */
    protected void createWestPanel(final ImageIcon logo, final ImageIcon info) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel westPanel = new JPanel();
                westPanel.setLayout(new BoxLayout(westPanel, BoxLayout.PAGE_AXIS));
                westPanel.add(new JLabel(logo, SwingConstants.CENTER));
                westPanel.add(Box.createVerticalStrut(10));
                if (info != null) {
                    infoLabel = new JLabel(info, SwingConstants.CENTER);
                    westPanel.add(infoLabel);
                }
                westPanel.add(Box.createGlue());
                add(westPanel, BorderLayout.WEST);
            }
        });
    }

    protected void changeInfoMessage(final ImageIcon newInfo) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (infoLabel == null) {
                    infoLabel = new JLabel();
                }
                infoLabel.setIcon(newInfo);
            }
        });
    }

    protected void createSouthPanel() {
        createSouthPanel(true);
    }

    protected void createSouthPanel(boolean showNext) {
        JPanel buttonPanel = new JPanel(new BorderLayout());

        backButton = new JLabel(back);
        buttonPanel.add(backButton, BorderLayout.WEST);

        if (showNext) {
            nextButton = new JLabel(next);
            buttonPanel.add(nextButton, BorderLayout.EAST);
        }

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void assignListenerToLabel(JLabel label, MouseListener listener) {
        if (label.getMouseListeners().length > 0) {
            label.removeMouseListener(label.getMouseListeners()[0]);
        }
        label.addMouseListener(listener);
    }

    public JLayeredPane getGeneralLayout(ImageIcon topImage, ImageIcon breadCrumb,
                                         String topText, JComponent centralPanel, int height) {

        int panelHeight = (int) (height * 0.95);
        JLayeredPane generalPanel = new JLayeredPane();
        generalPanel.setLayout(new BorderLayout());
        generalPanel.setOpaque(false);
        generalPanel.setPreferredSize(new Dimension(600, panelHeight));

        // create top panel to contain curves and title information
        JPanel topPanel = new JPanel(new GridLayout(3, 1));
        topPanel.add(new JLabel(topImage));


        JLabel infoLab = new JLabel(topText, JLabel.CENTER);
        UIHelper.renderComponent(infoLab, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        topPanel.add(infoLab);

        JLabel bc_label = new JLabel(breadCrumb, JLabel.CENTER);
        topPanel.add(bc_label);

        // add top image and top text beside one another
        generalPanel.add(topPanel, BorderLayout.NORTH);

        // add central panel

        int modifiedPanelHeight = panelHeight - 40;

        if (centralPanel.getHeight() < modifiedPanelHeight) {
            int difference = modifiedPanelHeight - centralPanel.getHeight();

            JPanel centralPanelMod = new JPanel();
            centralPanelMod.setLayout(new BoxLayout(centralPanelMod, BoxLayout.PAGE_AXIS));
            centralPanelMod.add(Box.createVerticalStrut(difference / 3));
            centralPanelMod.add(centralPanel);
            centralPanelMod.add(Box.createVerticalStrut((difference / 3) * 2));
            centralPanelMod.add(Box.createHorizontalGlue());
            generalPanel.add(centralPanelMod);

        } else {
            generalPanel.add(centralPanel, BorderLayout.CENTER);
        }

        return generalPanel;
    }


}
