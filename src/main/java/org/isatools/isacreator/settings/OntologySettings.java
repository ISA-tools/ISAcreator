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

package org.isatools.isacreator.settings;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ColumnFilterRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.io.CustomizableFileFilter;
import org.isatools.isacreator.io.OntologyLibrary;
import org.isatools.isacreator.io.UserProfileManager;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OntologySettings extends SettingsScreen {

    private ISAcreatorMenu menu;
    private ExtendedJList historyList;

    private JLabel loadedOntologyStats;
    private JLabel ontologyTermInformation;


    private JFileChooser jfc;

    private Map<String, OntologyTerm> userOntologyHistory;

    public OntologySettings(ISAcreatorMenu menu) {
        this.menu = menu;

        jfc = new JFileChooser();
//		jfc.setFileFilter(new CustomizableFileFilter("ontlib"));

        setLayout(new BorderLayout());
        setOpaque(false);
        add(createOntologyConfigPanel(), BorderLayout.NORTH);
        setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "configure ontologies", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

    }

    private JPanel createOntologyConfigPanel() {
        JPanel configContainer = new JPanel(new GridLayout(1, 2));
        configContainer.setOpaque(false);

        // container will contain a list showing all of the currently loaded ontologies within the Loaded
        // user profile on the left hand side with buttons below the list to export or import
        // ontology lists from an .ontlib file. On the right hand side pane, there will be text describing what to do
        // and functionality to add ontologies to the list via the ontology lookup tool!

        configContainer.add(createLoadedOntologiesPanel());
        configContainer.add(createOntologyAdditionPanel());
        return configContainer;
    }

    /**
     * JPanel will contain functionality to add ontologies to the list of available ontologies already available to the user
     * in their recent history.
     *
     * @return JPanel
     */
    private JPanel createOntologyAdditionPanel() {
        JPanel ontologyPanel = new JPanel();
        ontologyPanel.setLayout(new BoxLayout(ontologyPanel, BoxLayout.PAGE_AXIS));
        ontologyPanel.setOpaque(false);

        ontologyPanel.add(Box.createVerticalStrut(25));
        JLabel info = UIHelper.createLabel("<html>" +
                "Using this ontology panel you can view the ontologies you currently have available to you in your " +
                "<strong>User Profile</strong>. From here you can also modify this list of ontologies by <strong>" +
                "removing</strong> them, or you can <strong>export</strong> the list of ontologies for <strong>import</strong> in " +
                "another <strong>ISAcreator</strong> installation so that you and your collaborators are annotating your " +
                "data using a common vocabulary!" +
                "<p/>" +
                "<p><i>This feature will develop in the coming months to allow the creation of 'views' of a mixture of ontologies " +
                "within <strong>ISAcreator</strong></i></p>" +
                "</html>", UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR);
        info.setPreferredSize(new Dimension(250, 100));

        ontologyPanel.add(info);

        return ontologyPanel;
    }

    private JPanel createLoadedOntologiesPanel() {
        JPanel loadedOntologiesContainer = new JPanel();
        loadedOntologiesContainer.setLayout(new BoxLayout(loadedOntologiesContainer, BoxLayout.PAGE_AXIS));
        loadedOntologiesContainer.setOpaque(false);

        userOntologyHistory = UserProfileManager.getCurrentUser().getUserHistory();

        historyList = new ExtendedJList(new ColumnFilterRenderer());

        updateListContents();

        historyList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                String historyTerm = propertyChangeEvent.getNewValue().toString();
                if (historyTerm != null) {
                    updateOntologyTermInfo(userOntologyHistory.get(historyTerm));
                    ontologyTermInformation.setVisible(true);
                    removeTerm.setVisible(true);
                } else {
                    ontologyTermInformation.setVisible(false);
                    removeTerm.setVisible(false);
                }
            }
        });

        JScrollPane historyScroll = new JScrollPane(historyList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyScroll.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(historyScroll);

        UIHelper.renderComponent(historyList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);

        // add stats info and ontology term info
        loadedOntologyStats = new JLabel();
        loadedOntologyStats.setPreferredSize(new Dimension(200, 25));

        updateLoadedOntologyStats();

        loadedOntologiesContainer.add(UIHelper.wrapComponentInPanel(loadedOntologyStats));

        // add list and filter field to UI
        loadedOntologiesContainer.add(historyList.getFilterField());
        loadedOntologiesContainer.add(Box.createVerticalStrut(5));
        // add the actual list containing the elements!
        loadedOntologiesContainer.add(historyScroll);
        loadedOntologiesContainer.add(Box.createVerticalStrut(5));
        // add controls for list!
        loadedOntologiesContainer.add(createControlPanel());
        loadedOntologiesContainer.add(Box.createVerticalStrut(5));

        ontologyTermInformation = UIHelper.createLabel("", UIHelper.VER_10_PLAIN);
        ontologyTermInformation.setPreferredSize(new Dimension(200, 60));
        ontologyTermInformation.setVisible(false);

        loadedOntologiesContainer.add(Box.createVerticalStrut(20));
        loadedOntologiesContainer.add(UIHelper.wrapComponentInPanel(ontologyTermInformation));

        return loadedOntologiesContainer;
    }


    private void updateListContents() {
        historyList.getItems().clear();
        for (OntologyTerm h : userOntologyHistory.values()) {
            historyList.addItem(h.getShortForm());
        }
    }

    private void updateLoadedOntologyStats() {
        String labelContent = "<html> " +
                "<head>" + getCSS() + "</head>" +
                "<body>" +
                "<span class=\"title\">" + userOntologyHistory.values().size() + "</span>" +
                "<span class=\"info_text\"> ontologies present from </span>" +
                "<span class=\"title\">" + calculateUniqueOntologySources() + "</span> " +
                "<span class=\"info_text\"> sources!</span>" +
                "</body>" +
                "</html>";

        loadedOntologyStats.setText(labelContent);
    }


    private void updateOntologyTermInfo(OntologyTerm ot) {
        String labelContent = "<html> " +
                "<head>" + getCSS() + "</head>" +
                "<body>" +
                "<span class=\"title\">select term information</span>" + "<br/>" +
                "<span class=\"info\">term name: </span>" +
                "<span class=\"info_text\">" + ot.getOntologyTermName() + "</span>" + "<br/>" +
                "<span class=\"info\">source ref: </span> " +
                "<span class=\"info_text\">" + ot.getOntologySource() + "</span><br/>" +
                "<span class=\"info\">accession no: </div>" +
                "<span class=\"info_text\">" + ot.getOntologyTermAccession() + "</span>" +
                "</body>" +
                "</html>";

        ontologyTermInformation.setText(labelContent);
    }


    private int calculateUniqueOntologySources() {
        Set<String> sources = new HashSet<String>();
        for (String uniqueId : userOntologyHistory.keySet()) {
            // unique id is in format of Source:Accession
            if (uniqueId.contains(":")) {
                sources.add(uniqueId.substring(0, uniqueId.indexOf(":")));
            }
        }

        return sources.size();
    }


    public boolean updateSettings() {
        OntologyManager.setOntologySelectionHistory(userOntologyHistory);
        UserProfileManager.saveUserProfiles();
        return true;
    }

    protected void performImportLogic() {

        jfc.setDialogTitle("Select ontlib file");
        jfc.setApproveButtonText("Import Library");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        jfc.setFileFilter(new CustomizableFileFilter("ontlib"));
        jfc.showOpenDialog(this);

        if (jfc.getSelectedFile() != null) {

            File file = jfc.getSelectedFile();

            JOptionPane optionPane = null;

            try {
                OntologyLibrary ol = UserProfileManager.loadOntologyLibrary(
                        file);

                userOntologyHistory.putAll(ol.getOntologies());
                UserProfileManager.getCurrentUser().setUsedOntologySources(ol.getOntologySources());

                updateLoadedOntologyStats();
                ontologyTermInformation.setVisible(false);
                updateListContents();

                optionPane = new JOptionPane("<html>Ontology Library successfully loaded and added to your current ontologies!</html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(new ImageIcon(getClass().getResource("/images/settings/info_bubble_message.png")));

            } catch (IOException e) {
                optionPane = new JOptionPane("<html>A problem occurred when saving!<p>" + e.getMessage() + "</p></html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(new ImageIcon(getClass().getResource("/images/spreadsheet/warningIcon.png")));
            } catch (ClassNotFoundException e) {
                optionPane = new JOptionPane("<html>The imported object <strong>was not a valid</strong> Ontology Library</html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(new ImageIcon(getClass().getResource("/images/spreadsheet/warningIcon.png")));
            } finally {

                if (optionPane != null) {
                    UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);

                    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName()
                                    .equals(JOptionPane.VALUE_PROPERTY)) {
                                ApplicationManager.getCurrentApplicationInstance().hideSheet();
                            }
                        }
                    });

                    ApplicationManager.getCurrentApplicationInstance()
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
                UserProfileManager.saveOntologyLibrary(new OntologyLibrary(
                        userOntologyHistory,
                        UserProfileManager.getCurrentUser().getUsedOntologySources()),
                        exportDir);
                optionPane = new JOptionPane("<html>Ontology library saved in " + exportDir.getPath() + "</html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(new ImageIcon(getClass().getResource("/images/settings/info_bubble_message.png")));

            } catch (IOException e) {
                optionPane = new JOptionPane("<html>A problem occurred when saving!<p>" + e.getMessage() + "</p></html>",
                        JOptionPane.OK_OPTION);

                optionPane.setIcon(new ImageIcon(getClass().getResource("/images/spreadsheet/warningIcon.png")));
            } finally {

                if (optionPane != null) {
                    UIHelper.applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);

                    optionPane.addPropertyChangeListener(new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            if (event.getPropertyName()
                                    .equals(JOptionPane.VALUE_PROPERTY)) {
                                ApplicationManager.getCurrentApplicationInstance().hideSheet();
                            }
                        }
                    });

                    ApplicationManager.getCurrentApplicationInstance()
                            .showJDialogAsSheet(optionPane.createDialog(menu,
                                    "information"));
                }

            }

        }
    }

    protected void performDeletionLogic() {
        userOntologyHistory.remove(historyList.getSelectedTerm());
        updateLoadedOntologyStats();
        ontologyTermInformation.setVisible(false);
        updateListContents();
    }


}
