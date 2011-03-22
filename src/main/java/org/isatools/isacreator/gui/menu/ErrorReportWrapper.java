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

package org.isatools.isacreator.gui.menu;

import org.isatools.errorreporter.ui.ErrorReporterView;
import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 22/03/2011
 *         Time: 11:12
 */
public class ErrorReportWrapper extends JPanel {

    public static final String BACK_BUTTON_CLICKED_EVENT = "back_button_clicked";
    public static final String CONTINUE_BUTTON_CLICKED_EVENT = "continue_button_clicked";

    private Color stdButtonTextColor = UIHelper.GREY_COLOR;
    private Color overButtonTextColor = UIHelper.RED_COLOR;

    private ErrorReporterView view;
    private boolean showContinue;

    private JLabel backButton, continueButton;

    @InjectedResource
    private ImageIcon backIcon, backIconOver, continueIcon, continueIconOver;

    public ErrorReportWrapper(ErrorReporterView view, boolean showContinue) {
        ResourceInjector.get("gui-package.style").inject(this);
        this.view = view;
        this.showContinue = showContinue;
    }

    public void createGUI() {

        setLayout(new BorderLayout());
        createPane();
        setOpaque(false);
    }

    private void createPane() {

        add(view, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setOpaque(false);

        final JLabel backButton = new JLabel("back", backIcon, SwingConstants.LEFT);
        UIHelper.renderComponent(backButton, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
        backButton.setHorizontalAlignment(JLabel.LEFT);

        backButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                firePropertyChange(BACK_BUTTON_CLICKED_EVENT, false, true);
                backButton.setIcon(backIcon);
                backButton.setForeground(stdButtonTextColor);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                backButton.setIcon(backIconOver);
                backButton.setForeground(overButtonTextColor);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                backButton.setIcon(backIcon);
                backButton.setForeground(stdButtonTextColor);
            }
        });

        buttonPanel.add(backButton, BorderLayout.WEST);

        if (showContinue) {
            final JLabel continueButton = new JLabel("continue to load regardless", continueIcon, SwingConstants.RIGHT);
            UIHelper.renderComponent(continueButton, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
            continueButton.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
            continueButton.setHorizontalAlignment(JLabel.RIGHT);

            continueButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    firePropertyChange(CONTINUE_BUTTON_CLICKED_EVENT, false, true);
                    continueButton.setIcon(continueIcon);
                    continueButton.setForeground(stdButtonTextColor);
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    continueButton.setIcon(continueIconOver);
                    continueButton.setForeground(overButtonTextColor);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    continueButton.setIcon(continueIcon);
                    continueButton.setForeground(stdButtonTextColor);
                }
            });

            buttonPanel.add(continueButton, BorderLayout.EAST);
        }

        add(UIHelper.wrapComponentInPanel(buttonPanel), BorderLayout.SOUTH);

    }
}
