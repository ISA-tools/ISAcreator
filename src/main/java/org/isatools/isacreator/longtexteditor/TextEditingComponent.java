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

package org.isatools.isacreator.longtexteditor;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.IOException;

/**
 * TextEditingComponent provides a component with numerous features to allow users to enter large amounts of text.
 * Provides undo/redo, copy/paste and shows word and character counts.
 *
 * @author Eamonn Maguire
 * @date Jan 12, 2010
 */


public class TextEditingComponent extends JPanel implements DocumentListener {
    @InjectedResource
    private ImageIcon undoIcon, undoIconOver, redoIcon, redoIconOver,
            pasteIcon, pasteIconOver, copyIcon, copyIconOver;

    private Clipboard system;
    private UndoManager undoManager;
    private JTextArea textEntryArea;
    private JLabel redo;
    private JLabel undo;
    private JLabel copy;
    private JLabel paste;
    private JLabel textStats;

    public TextEditingComponent() {
        setLayout(new GridLayout(1, 1));

        ResourceInjector.get("longtexteditor-package.style").inject(this);

        system = Toolkit.getDefaultToolkit().getSystemClipboard();
        undoManager = new UndoManager();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI();
            }
        });
    }


    private void createGUI() {

        textEntryArea = new JTextArea();
        textEntryArea.setLineWrap(true);
        textEntryArea.setWrapStyleWord(true);
        textEntryArea.getDocument().addDocumentListener(this);
        textEntryArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent event) {
                undoManager.addEdit(event.getEdit());
                updateButtons();
            }
        });

        // attach a listener to the text entry area so that when the user right clicks in the component they will be
        // presented with a menu.
        textEntryArea.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isRightMouseButton(event)) {
                    createTextEditorMenu(textEntryArea, event.getX(), event.getY());
                }
            }
        });

        UIHelper.renderComponent(textEntryArea, UIHelper.VER_12_PLAIN, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);

        JScrollPane textScroller = new JScrollPane(textEntryArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textScroller.setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 4));
        textScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        textScroller.setPreferredSize(new Dimension(400, 180));

        IAppWidgetFactory.makeIAppScrollPane(textScroller);

        add(textScroller);
    }

    public JPanel createTextStatsPane() {
        JPanel textStatsContainer = new JPanel(new GridLayout(1, 1));
        textStatsContainer.setBackground(UIHelper.BG_COLOR);
        textStats = UIHelper.createLabel("", UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR);
        textStats.setHorizontalAlignment(JLabel.CENTER);
        textStats.setPreferredSize(new Dimension(300, 15));
        textStatsContainer.add(textStats);

        return textStatsContainer;
    }


    public JPanel createToolBar() {
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.PAGE_AXIS));
        toolbar.setBackground(UIHelper.BG_COLOR);

        undo = new JLabel(undoIcon);
        undo.setVerticalAlignment(SwingConstants.TOP);
        undo.setOpaque(false);
        undo.setEnabled(false);
        undo.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                undo.setIcon(undoIcon);
                undo();
            }

            public void mouseEntered(MouseEvent event) {
                undo.setIcon(undoIconOver);
            }

            public void mouseExited(MouseEvent event) {
                undo.setIcon(undoIcon);
            }
        });

        toolbar.add(undo);

        redo = new JLabel(redoIcon);
        redo.setVerticalAlignment(SwingConstants.TOP);
        redo.setOpaque(false);
        redo.setEnabled(false);
        redo.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                redo.setIcon(redoIcon);
                redo();
            }

            public void mouseEntered(MouseEvent event) {
                redo.setIcon(redoIconOver);
            }

            public void mouseExited(MouseEvent event) {
                redo.setIcon(redoIcon);
            }
        });

        toolbar.add(redo);

        copy = new JLabel(copyIcon);
        copy.setVerticalAlignment(SwingConstants.TOP);
        copy.setOpaque(false);
        copy.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                copy.setIcon(copyIcon);
                copy();
            }

            public void mouseEntered(MouseEvent event) {
                copy.setIcon(copyIconOver);
            }

            public void mouseExited(MouseEvent event) {
                copy.setIcon(copyIcon);
            }
        });

        toolbar.add(copy);

        paste = new JLabel(pasteIcon);
        paste.setVerticalAlignment(SwingConstants.TOP);
        paste.setOpaque(false);
        paste.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                paste.setIcon(pasteIcon);
                paste();
            }

            public void mouseEntered(MouseEvent event) {
                paste.setIcon(pasteIconOver);
            }

            public void mouseExited(MouseEvent event) {
                paste.setIcon(pasteIcon);
            }
        });

        toolbar.add(paste);

        return toolbar;
    }

    public void createTextEditorMenu(JComponent parentComp, int xPos, int yPos) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(UIHelper.GREY_COLOR);
        popup.setForeground(UIHelper.LIGHT_GREEN_COLOR);
        popup.setLightWeightPopupEnabled(false);

        JMenuItem undo = new JMenuItem("undo");
        undo.setEnabled(undoManager.canUndo());


        undo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                undo();
            }
        });

        undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                KeyEvent.CTRL_MASK));


        popup.add(undo);

        JMenuItem redo = new JMenuItem("redo");
        redo.setEnabled(undoManager.canRedo());

        redo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                redo();
            }
        });

        redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,
                KeyEvent.CTRL_MASK));


        popup.add(redo);
        popup.add(new JSeparator());

        JMenuItem copy = new JMenuItem("copy");

        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                copy();
            }
        });

        popup.add(copy);

        JMenuItem paste = new JMenuItem("paste");

        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                paste();
            }
        });

        popup.add(paste);

        popup.show(parentComp, xPos, yPos);
    }

    private void paste() {
        try {
            String clipboardContents = (String) (system.getContents(this)
                    .getTransferData(DataFlavor.stringFlavor));
            textEntryArea.append(clipboardContents);
        } catch (UnsupportedFlavorException e) {
            // ignore and carry on.
        } catch (IOException e) {
            // ignore.
        }
    }

    private void copy() {
        StringSelection stsel = new StringSelection(textEntryArea.getSelectedText());
        system.setContents(stsel, stsel);
    }

    private void undo() {
        try {
            undoManager.undo();
            updateButtons();
        } catch (CannotUndoException cue) {
            // cannot redo
        }
    }

    private void redo() {
        try {
            undoManager.redo();
            updateButtons();
        } catch (CannotRedoException cre) {
            // cannot redo
        }
    }

    public void updateButtons() {
        if (undo != null) {
            undo.setEnabled(undoManager.canUndo());
            redo.setEnabled(undoManager.canRedo());
        }
    }

    public String getEnteredText() {
        return textEntryArea.getText();
    }

    public void insertUpdate(DocumentEvent event) {
        textStats.setText(calculateTextProperties());
    }

    public void removeUpdate(DocumentEvent event) {
        if (textEntryArea.getText().length() == 0) {
            textStats.setText("");
        } else {
            textStats.setText(calculateTextProperties());
        }
    }

    public void changedUpdate(DocumentEvent event) {
        textStats.setText(calculateTextProperties());
    }

    public void setText(String text) {
        if (text != null) {
            textEntryArea.setText(text);
        } else {
            textEntryArea.setText("");
        }
    }

    private String calculateTextProperties() {
        // performs a split on the text to determine word count (by space presence, new line presence
        // or carriage return presence.) and the character count.
        return "<html><b>Word count: </b>" + textEntryArea.getText().split("\\s+|\\n+\\\r+").length +
                " <b># Characters: </b>" + textEntryArea.getText().length() + "</html>";
    }


    public UndoManager getUndoManager() {
        return undoManager;
    }

    public JTextArea getTextEntryArea() {
        return textEntryArea;
    }
}
