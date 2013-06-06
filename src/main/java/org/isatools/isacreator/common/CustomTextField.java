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

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * CustomTextField
 *
 * @author eamonnmaguire
 * @date Sep 24, 2010
 */


public class CustomTextField extends JPanel {

    private String fieldName;

    private JTextField textField;
    private JLabel validityIndicator;

    private boolean required;
    private Font font;
    private Color textColor;
    private Dimension fieldSize;

    private boolean splitLines = true;

    @InjectedResource
    private ImageIcon correctIcon, incorrectIcon;

    public CustomTextField(String fieldName, boolean required) {
        this(fieldName, required, UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, new Dimension(90, 20));
    }

    public CustomTextField(String fieldName, boolean required, Dimension fieldSize) {
        this(fieldName, required, UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR, fieldSize);
    }

    public CustomTextField(String fieldName, boolean required, Font font, Color textColor, Dimension fieldSize) {
        this(fieldName, required, font, textColor, fieldSize, true);
    }

    public CustomTextField(String fieldName, boolean required, Font font, Color textColor, Dimension fieldSize, boolean splitLines) {
        this.fieldName = fieldName;
        this.required = required;
        this.font = font;
        this.textColor = textColor;
        this.fieldSize = fieldSize;
        this.splitLines = splitLines;

        ResourceInjector.get("common-package.style").inject(this);


        createGUI();

    }

    private void createGUI() {
        Box container;

        if (splitLines) {
            container = Box.createVerticalBox();
        } else {
            container = Box.createHorizontalBox();
        }

        JLabel field = UIHelper.createLabel(fieldName, font, textColor, SwingConstants.LEFT);

        container.add(UIHelper.wrapComponentInPanel(field));

        Box textFieldContainer = Box.createHorizontalBox();

        textField = new JTextField();
        UIHelper.renderComponent(textField, font, textColor, false);

        textField.setPreferredSize(fieldSize);
        textField.setSize(fieldSize);

        textFieldContainer.add(textField);

        if (required) {
            System.err.println("adding validity indicator to pane");
            validityIndicator = new JLabel(incorrectIcon);
            validityIndicator.setHorizontalAlignment(SwingConstants.RIGHT);
            textFieldContainer.add(validityIndicator);

            textField.getDocument().addDocumentListener(new DocumentListener() {

                public void insertUpdate(DocumentEvent event) {

                    setValidityIndicator();
                }

                public void removeUpdate(DocumentEvent event) {
                    setValidityIndicator();
                }

                public void changedUpdate(DocumentEvent event) {
                    setValidityIndicator();
                }

                private void setValidityIndicator() {
                    if (required) {
                        validityIndicator.setIcon(textField.getText().trim().equals("") ? incorrectIcon : correctIcon);
                    }
                }
            });

        } else {
            textFieldContainer.add(Box.createHorizontalStrut(18));
        }

        container.add(textFieldContainer);

        add(container);
    }

    public boolean isValidInput() {
        if (required) {
            return validityIndicator.getIcon() == correctIcon;
        } else {
            return !required;
        }
    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String value) {
        textField.setText(value);
    }

    public JTextField getTextField() {
        return textField;
    }

}
