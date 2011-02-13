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

package org.isatools.isacreator.settings;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.gui.StudySubData;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.io.CustomizableFileFilter;
import org.isatools.isacreator.io.UserProfileIO;
import org.isatools.isacreator.model.Contact;
import org.isatools.isacreator.model.Protocol;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;


public class GeneralViewerEditor<T> extends SettingsScreen {

    @InjectedResource
    private ImageIcon bubbleMessage, warningIcon;

    private List<T> currentValues;

    private String title;
    private ISAcreatorMenu menu;
    private ExtendedJList termList;

    private JLabel loadedTStats;

    private static JFileChooser jfc;
    private ElementEditor editor;

    public GeneralViewerEditor(String title, ISAcreatorMenu menu, List<T> currentValues) {

        ResourceInjector.get("settings-package.style").inject(this);

        this.title = title;
        this.menu = menu;
        this.currentValues = currentValues;

        jfc = new JFileChooser();

        setLayout(new BorderLayout());
        setOpaque(false);
        add(createPanel(), BorderLayout.NORTH);

        setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "configure " + title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));


    }

    private JPanel createPanel() {
        JPanel configContainer = new JPanel(new GridLayout(1, 2));
        configContainer.setOpaque(false);

        if (title.equals(ElementEditor.CONTACTS)) {
            editor = new ContactEditor();
        } else {
            editor = new ProtocolEditor();
        }

        configContainer.add(createTypeListView());
        configContainer.add(editor);
        return configContainer;
    }

    private JPanel createTypeListView() {
        JPanel loadedElementsView = new JPanel();
        loadedElementsView.setLayout(new BoxLayout(loadedElementsView, BoxLayout.PAGE_AXIS));
        loadedElementsView.setOpaque(false);

        termList = new ExtendedJList(new ColumnFilterRenderer());

        updateListContents();

        if (termList.getModel().getSize() > 0) {
            termList.setSelectedIndex(0);
            updateEditor(termList.getSelectedTerm());
        }

        termList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String term = propertyChangeEvent.getNewValue().toString();
                if (term != null) {
                    updateEditor(term);
                    removeTerm.setVisible(true);
                } else {
                    removeTerm.setVisible(false);
                }
            }
        });

        JScrollPane historyScroll = new JScrollPane(termList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScroll.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(historyScroll);

        UIHelper.renderComponent(termList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        // add stats info and ontology term info
        loadedTStats = new JLabel();
        loadedTStats.setPreferredSize(new Dimension(historyScroll.getWidth(), 25));

        updateStats();

        loadedElementsView.add(UIHelper.wrapComponentInPanel(loadedTStats));

        // add list and filter field to UI
        JPanel filterFieldContainer = new JPanel(new FlowLayout());
        filterFieldContainer.add(termList.getFilterField());
        termList.getFilterField().setPreferredSize(new Dimension(200, 25));

        loadedElementsView.add(filterFieldContainer);
        loadedElementsView.add(Box.createVerticalStrut(5));
        // add the actual list containing the elements!
        loadedElementsView.add(historyScroll);
        loadedElementsView.add(Box.createVerticalStrut(5));
        // add controls for list!
        loadedElementsView.add(createControlPanel());
        loadedElementsView.add(Box.createVerticalStrut(5));

        return loadedElementsView;
    }

    private void updateEditor(String item) {
        if (editor instanceof ContactEditor) {
            Contact toModify = (Contact) findItem(item);
            ((ContactEditor) editor).setCurrentContact(toModify);
        } else if (editor instanceof ProtocolEditor) {
            Protocol toModify = (Protocol) findItem(item);
            ((ProtocolEditor) editor).setCurrentProtocol(toModify);
        }
    }

    private void updateStats() {
        String labelContent = "";
        if (currentValues != null) {
            labelContent = "<html> " +
                    "<head>" + getCSS() + "</head>" +
                    "<body>" +
                    "<span class=\"title\">" + currentValues.size() + "</span>" +
                    "<span class=\"info_text\"> " + title + " in your user profile</span>" +
                    "</body>" +
                    "</html>";
        }
        loadedTStats.setText(labelContent);
    }

    private void updateListContents() {
        termList.getItems().clear();
        for (T h : currentValues) {
            if (h instanceof StudySubData) {
                termList.addItem(((StudySubData) h).getIdentifier());
            }
        }
        if (termList.getModel().getSize() > 0) {
            termList.setSelectedIndex(0);
            updateEditor(termList.getSelectedTerm());
        }
    }

    public boolean updateSettings() {
        if (title.equals(ElementEditor.CONTACTS)) {
            menu.getMain().getCurrentUser().setPreviouslyUsedContacts((List<StudySubData>) currentValues);
        } else {
            menu.getMain().getCurrentUser().setPreviouslyUsedProtocols((List<StudySubData>) currentValues);
        }

        menu.getMain().saveUserProfiles();
        return true;
    }

    protected void performImportLogic() {

        jfc.setDialogTitle("Select " + (title.equals(ElementEditor.CONTACTS) ? "contacts library (contlib)" : "protocol library (protlib)"));
        jfc.setApproveButtonText("Import Library");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new CustomizableFileFilter((title.equals(ElementEditor.CONTACTS) ? "contlib" : "protlib")));
        jfc.showOpenDialog(this);

        if (jfc.getSelectedFile() != null) {

            File file = jfc.getSelectedFile();

            JOptionPane optionPane = null;

            try {

                if (title.equals(ElementEditor.CONTACTS)) {
                    currentValues.addAll((Collection<? extends T>) UserProfileIO.loadContactsLibrary(file));
                } else {
                    currentValues.addAll((Collection<? extends T>) UserProfileIO.loadProtocolLibrary(file));
                }

                updateStats();
                updateListContents();

                optionPane = new JOptionPane("<html>Library successfully loaded and added to your current " + title + "!</html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(bubbleMessage);

            } catch (IOException e) {
                optionPane = new JOptionPane("<html>A problem occurred when saving!<p>" + e.getMessage() + "</p></html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(warningIcon);
            } catch (ClassNotFoundException e) {
                optionPane = new JOptionPane("<html>The imported object <strong>was not a valid</strong> " + title + " Library</html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(new ImageIcon(getClass().getResource("/images/spreadsheet/warningIcon.png")));
            } finally {

                if (optionPane != null) {
                    UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);

                    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName()
                                    .equals(JOptionPane.VALUE_PROPERTY)) {
                                menu.getMain().hideSheet();
                            }
                        }
                    });
                    menu.getMain()
                            .showJDialogAsSheet(optionPane.createDialog(menu,
                                    "information"));
                }
            }
        }
    }

    protected void performExportLogic() {
        jfc.setDialogTitle("Select a directory to save library in");
        jfc.setApproveButtonText("Save Library");
        jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jfc.showOpenDialog(this);

        if (jfc.getSelectedFile() != null) {
            File exportDir = jfc.getSelectedFile();
            JOptionPane optionPane = null;
            try {
                if (title.equals(ElementEditor.CONTACTS)) {
                    UserProfileIO.saveContactsLibrary((List<Contact>) currentValues, exportDir);
                } else {
                    UserProfileIO.saveProtocolLibrary((List<Protocol>) currentValues, exportDir);
                }
                optionPane = new JOptionPane("<html>" + title + " library saved in " + exportDir.getPath() + "</html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(bubbleMessage);

            } catch (IOException e) {
                optionPane = new JOptionPane("<html>A problem occurred when saving!<p>" + e.getMessage() + "</p></html>",
                        JOptionPane.OK_OPTION);
                optionPane.setIcon(warningIcon);
            } finally {
                if (optionPane != null) {
                    UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);

                    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName()
                                    .equals(JOptionPane.VALUE_PROPERTY)) {
                                menu.getMain().hideSheet();
                            }
                        }
                    });
                    menu.getMain()
                            .showJDialogAsSheet(optionPane.createDialog(menu,
                                    "information"));
                }

            }

        }
    }

    protected void performDeletionLogic() {
        deleteItem(findItem(termList.getSelectedTerm()));
        updateStats();
        updateListContents();
    }

    private void deleteItem(T toDelete) {
        if (toDelete != null) {
            currentValues.remove(toDelete);
        }
    }

    private T findItem(String identifier) {
        for (T v : currentValues) {
            if (((StudySubData) v).getIdentifier().equals(identifier)) {
                return v;
            }
        }
        return null;
    }

}
