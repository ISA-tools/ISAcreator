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


package org.isatools.isacreator.common;

import org.isatools.isacreator.filechooser.FileChooserUI;
import org.isatools.isacreator.longtexteditor.TextEditor;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * TextEditUtility
 *
 * @author eamonnmaguire
 * @date May 7, 2010
 */


public class TextEditUtility extends JLabel implements MouseListener {

    @InjectedResource
    private ImageIcon editIcon, editIconOver;

    private JTextComponent editingField;

    private TextEditor textEditor;

    public TextEditUtility(JTextComponent toEdit) {
        this.editingField = toEdit;

        instantiateTextEditor();
        ResourceInjector.get("common-package.style").inject(this);

        setVerticalAlignment(JLabel.TOP);
        setAlignmentY(JLabel.TOP_ALIGNMENT);

        setIcon(editIcon);
        setToolTipText("<html><b>Edit text using the text edit utility</b></html>");
        addMouseListener(TextEditUtility.this);

    }

    private void instantiateTextEditor() {
        textEditor = new TextEditor();
        textEditor.createGUI();

        textEditor.addPropertyChangeListener("enteredText",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        editingField.setText(textEditor.getEnteredText());
                        textEditor.setVisible(false);

                    }
                });
        textEditor.addPropertyChangeListener("reverting",
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        textEditor.setVisible(false);

                    }
                });
    }


    public void mouseClicked(MouseEvent mouseEvent) {

    }

    public void mousePressed(MouseEvent mouseEvent) {


        setIcon(editIcon);

        Point p = editingField.getLocationOnScreen();

        int proposedX = p.x + getWidth();
        int proposedY = p.y + getHeight();

        Rectangle desktopBounds = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getMaximumWindowBounds();

        if ((proposedX + FileChooserUI.WIDTH) > desktopBounds.width) {
            int difference = (proposedX + FileChooserUI.WIDTH) -
                    desktopBounds.width;
            proposedX = proposedX - difference;
        }

        if ((proposedY + FileChooserUI.HEIGHT) > desktopBounds.height) {
            int difference = (proposedY + FileChooserUI.HEIGHT) -
                    desktopBounds.height;
            proposedY = proposedY - difference;
        }

        textEditor.addNotify();
        textEditor.setLocation(proposedX, proposedY);
        textEditor.setText(editingField.getText());
        textEditor.makeVisible();
        textEditor.requestFocusInWindow();

    }

    public void mouseReleased(MouseEvent mouseEvent) {

    }

    public void mouseEntered(MouseEvent mouseEvent) {
        setIcon(editIconOver);
    }

    public void mouseExited(MouseEvent mouseEvent) {
        setIcon(editIcon);
    }
}
