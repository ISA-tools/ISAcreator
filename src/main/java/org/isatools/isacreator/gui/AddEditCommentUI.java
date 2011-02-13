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

package org.isatools.isacreator.gui;

import org.isatools.isacreator.common.Globals;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.longtexteditor.TextEditingComponent;
import org.isatools.isacreator.model.Comment;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * AddCommentUI
 *
 * @author Eamonn Maguire
 * @date Jan 11, 2010
 */


public class AddEditCommentUI extends JFrame implements WindowListener {
    public static final int EDIT_MODE = 0;
    public static final int ADD_MODE = 1;

    @InjectedResource
    private ImageIcon editCommentLogo, editCommentLogoInactive,
            addCommentLogo, addCommentLogoInactive;

    private int mode;

    private JTextField commentType;
    private TextEditingComponent textEdit;

    public AddEditCommentUI(int mode) {
        this.mode = mode;

        ResourceInjector.get("gui-package.style").inject(this);

        this.textEdit = new TextEditingComponent();
        setBackground(UIHelper.BG_COLOR);
        setLayout(new BorderLayout());
        setResizable(false);
        setUndecorated(true);
        setPreferredSize(new Dimension(400, 300));
        addWindowListener(this);

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        createGUI();

    }

    public void createGUI() {

        ImageIcon activeLogo = mode == EDIT_MODE ? editCommentLogo : editCommentLogoInactive;
        ImageIcon inactiveLogo = mode == ADD_MODE ? addCommentLogo : addCommentLogoInactive;

        HUDTitleBar titlePanel = new HUDTitleBar(
                activeLogo.getImage(), inactiveLogo.getImage());

        JPanel northPanel = new JPanel(new GridLayout(2, 1));
        northPanel.setOpaque(false);

        northPanel.add(titlePanel);
        northPanel.add(createCommentTypeEntry());
        add(northPanel, BorderLayout.NORTH);

        JPanel commentValueContainer = new JPanel(new BorderLayout());
        commentValueContainer.setOpaque(false);

        commentValueContainer.add(UIHelper.createLabel("Comment value:", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR), BorderLayout.NORTH);
        commentValueContainer.add(textEdit, BorderLayout.CENTER);
        commentValueContainer.add(textEdit.createToolBar(), BorderLayout.EAST);
        commentValueContainer.add(textEdit.createTextStatsPane(), BorderLayout.SOUTH);

        add(commentValueContainer, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        final JLabel ok = new JLabel(Globals.OK_ICON, JLabel.RIGHT);
        ok.setOpaque(false);
        ok.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent event) {
            }

            public void mousePressed(MouseEvent event) {
                firePropertyChange("addedEditedComment", "OLD_VALUE", "comment_added");
                setVisible(false);
            }

            public void mouseReleased(MouseEvent event) {
            }

            public void mouseEntered(MouseEvent event) {
                ok.setIcon(Globals.OK_OVER_ICON);
            }

            public void mouseExited(MouseEvent event) {
                ok.setIcon(Globals.OK_ICON);
            }
        });


        southPanel.add(ok, BorderLayout.EAST);

        FooterPanel fp = new FooterPanel(this);
        southPanel.add(fp, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private JPanel createCommentTypeEntry() {
        JPanel commentEntryPanel = new JPanel(new GridLayout(1, 2));
        commentEntryPanel.setOpaque(false);

        commentEntryPanel.add(UIHelper.createLabel("Comment type:", UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));
        commentType = new JTextField();
        UIHelper.renderComponent(commentType, UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        commentEntryPanel.add(commentType);

        return commentEntryPanel;
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
        firePropertyChange("noChangeAdditionOfComment", "NONE_SELECTED", "no_comment_added");
    }

    public static void main(String[] args) {
        new AddEditCommentUI(AddEditCommentUI.EDIT_MODE);
    }

    public Comment getComment() {
        return new Comment(commentType.getText(), textEdit.getEnteredText());
    }


}
