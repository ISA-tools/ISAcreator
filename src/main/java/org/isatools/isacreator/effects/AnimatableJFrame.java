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

package org.isatools.isacreator.effects;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.settings.ISAcreatorProperties;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


/**
 * AnimatableJFrame
 * Provides functionality to allow JDialogs to scroll in and out like those in the Safari browser
 * Majority of code minus a few small changes from Marinacci, J. & Adamson, C.
 * Swing Hacks, O'Reilly 2005.
 *
 * @author Marinacci, J, Adamson, C.
 */
public class AnimatableJFrame extends JFrame implements ActionListener, MouseListener {
    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;
    public static final float ANIMATION_DURATION = 200f;
    public static final int ANIMATION_SLEEP = 25;
    private AnimatingSheet animatingSheet;

    private JComponent sheet;
    private JPanel glass;
    private Timer animationTimer;
    private boolean animating;
    private boolean sheetInView = false;
    private int animationDirection;
    private long animationStart;

    public AnimatableJFrame() {
        super();
        setupPane();
    }

    public void actionPerformed(ActionEvent e) {
        if (animating) {
            // calculate height to show
            float animationPercent = (System.currentTimeMillis() -
                    animationStart) / ANIMATION_DURATION;
            animationPercent = Math.min(1.0f, animationPercent);

            int animatingHeight;

            if (animationDirection == INCOMING) {
                animatingHeight = (int) (animationPercent * sheet.getHeight());
            } else {
                animatingHeight = (int) ((1.0f - animationPercent) * sheet.getHeight());
            }

            // clip off that much from sheet and blit it
            // into animatingSheet
            animatingSheet.setAnimatingHeight(animatingHeight);
            animatingSheet.repaint();

            if (animationPercent >= 1.0f) {
                stopAnimation();

                if (animationDirection == INCOMING) {
                    finishShowingSheet();
                    sheetInView = true;
                } else {
                    glass.removeAll();
                    animatingSheet = new AnimatingSheet();
                    glass.setVisible(false);
                    sheetInView = false;
                }
            }
        }
    }

    private void finishShowingSheet() {
        if(getJavaVersion() < 7) {
            glass.removeAll();
        } else {
            glass = new JPanel();
            glass.setOpaque(true);
            glass.setBackground(new Color(255,255,255,100));
            setGlassPane(glass);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.anchor = GridBagConstraints.NORTH;
                glass.add(sheet, gbc);
                gbc.gridy = 1;
                gbc.weighty = Integer.MAX_VALUE;
                glass.add(Box.createGlue(), gbc);
                glass.revalidate();
                glass.repaint();
            }
        });

    }

    public void hideSheet() {
        if (sheetInView) {
            glass.removeMouseListener(this);
            glass.setOpaque(false);
            glass.setVisible(false);
            animationDirection = OUTGOING;
            startAnimation();
        }
    }

    public void instantHideSheet() {
        glass.removeMouseListener(this);
        glass.removeAll();
        glass.setVisible(false);
    }

    private void setupAnimation() {
        glass.removeAll();
        glass.setBackground(new Color(255, 255, 255, 100));
        glass.setOpaque(true);
        glass.setVisible(true);
        animationDirection = INCOMING;
        startAnimation();
    }

    private int getJavaVersion() {
        String version = ISAcreatorProperties.getProperty("java.version");
        String[] versionParts = version.split("\\.");
        return Integer.valueOf(versionParts[1]);
    }

    public void setupPane() {
        glass = (JPanel) getGlassPane();
        glass.setLayout(new GridBagLayout());
        glass.setBackground(UIHelper.BG_COLOR);
        animatingSheet = new AnimatingSheet();
        animatingSheet.setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));
    }

    public void maskOutMouseEvents() {
        glass.addMouseListener(this);
    }

    /**
     * Used to show JDialog windows as sheets
     *
     * @param dialog - Dialog to be shown as a sheet
     */
    public void showJDialogAsSheet(JDialog dialog) {
        sheet = (JComponent) dialog.getContentPane();
        sheet.setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));
        setupAnimation();
    }

    private void startAnimation() {
        glass.repaint();
        // clear glasspane and set up animatingSheet
        animatingSheet.setSource(sheet);
        animatingSheet.revalidate();
        glass.removeAll();
        glass.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        glass.add(animatingSheet, gbc);
        gbc.gridy = 1;
        gbc.weighty = Integer.MAX_VALUE;
        glass.add(Box.createGlue(), gbc);
        glass.setVisible(true);

        // start animation timer
        animationStart = System.currentTimeMillis();

        if (animationTimer == null) {
            animationTimer = new Timer(ANIMATION_SLEEP, this);
        }

        animating = true;
        animationTimer.start();
    }

    private void stopAnimation() {
        animationTimer.stop();
        animating = false;
        System.setProperty("awt.nativeDoubleBuffering", "false");
    }

    public void mouseClicked(MouseEvent mouseEvent) {}

    public void mouseEntered(MouseEvent mouseEvent) {}

    public void mouseExited(MouseEvent mouseEvent) {}

    public void mousePressed(MouseEvent mouseEvent) {}

    public void mouseReleased(MouseEvent mouseEvent) {}
}
