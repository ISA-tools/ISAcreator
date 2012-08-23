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

package org.isatools.isacreator.longtexteditor;

import org.isatools.isacreator.common.Globals;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * TextEditor widget to allow users to edit large bits of text in a light, convenient interface.
 *
 * @author Eamonn Maguire
 * @date Jan 29, 2009
 */


public class TextEditor extends JFrame implements WindowListener {

    @InjectedResource
    private Image logo, logoInactive;

    private TextEditingComponent textEditor;

    public TextEditor() {
        this(new Dimension(400, 275));

        ResourceInjector.get("longtexteditor-package.style").inject(this);
    }

    public TextEditor(Dimension size) {
        this.textEditor = new TextEditingComponent();

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        setResizable(false);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setPreferredSize(size);
        addWindowListener(this);
    }

    public void createGUI() {
        HUDTitleBar titlePanel = new HUDTitleBar(
                logo,
                logoInactive);

        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.setOpaque(false);
        northPanel.add(titlePanel);

        // add the components status pane to indicate word count, etc.
        northPanel.add(textEditor.createTextStatsPane());

        add(northPanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(
                new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        add(textEditor.createToolBar(), BorderLayout.WEST);

        JPanel textEditorContainer = new JPanel(new GridLayout(1, 1));
        textEditorContainer.setOpaque(false);
        textEditorContainer.add(textEditor);
        add(textEditorContainer, BorderLayout.CENTER);

        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(UIHelper.BG_COLOR);

        final JLabel ok = new JLabel(Globals.OK_ICON, JLabel.RIGHT);
        ok.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("enteredText", "OLD_VALUE", textEditor.getEnteredText());
                setVisible(false);
            }

            public void mouseEntered(MouseEvent event) {
                ok.setIcon(Globals.OK_OVER_ICON);
            }

            public void mouseExited(MouseEvent event) {
                ok.setIcon(Globals.OK_ICON);
            }
        });

        buttonContainer.add(ok, BorderLayout.EAST);

        FooterPanel footer = new FooterPanel(this);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.PAGE_AXIS));
        southPanel.setOpaque(false);

        southPanel.add(buttonContainer);
        southPanel.add(footer);
        add(southPanel, BorderLayout.SOUTH);
        pack();
    }

    public void makeVisible() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                textEditor.getUndoManager().discardAllEdits();
                textEditor.updateButtons();
                setVisible(true);
                textEditor.getTextEntryArea().requestFocusInWindow();
            }
        });
    }


    public void windowOpened(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
    }

    public void windowClosed(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
        firePropertyChange("enteredText", "", textEditor.getEnteredText());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(false);
            }
        });
    }

    public static void main(String[] args) {
        TextEditor te = new TextEditor();
        te.createGUI();
        te.setVisible(true);
    }

    public String getEnteredText() {
        return textEditor.getEnteredText();
    }

    public void setText(String text) {
        textEditor.setText(text);
    }
}
