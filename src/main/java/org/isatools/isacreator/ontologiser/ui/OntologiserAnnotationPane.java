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

package org.isatools.isacreator.ontologiser.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.ontologiser.logic.ScoreAnalysisUtility;
import org.isatools.isacreator.ontologiser.model.OntologisedResult;
import org.isatools.isacreator.ontologiser.model.SuggestedAnnotation;
import org.isatools.isacreator.ontologiser.ui.listrenderer.OntologyAssignedListRenderer;
import org.isatools.isacreator.ontologiser.ui.listrenderer.ScoringConfidenceListRenderer;
import org.isatools.isacreator.ontologymanager.BioPortalClient;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologyselectiontool.ViewTermDefinitionUI;
import org.isatools.isacreator.utils.datastructures.ISAPair;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

/**
 * This pane contains the CORE GUI components for the Ontologiser function. Including 3 sections:
 * 1) The list of unannotated terms found in the spreadsheet(s);
 * 2) The list of suggested terms for each unannotated term; and
 * 3) The definition pane containg definitions
 *
 */
public class OntologiserAnnotationPane extends JPanel {

    private Map<String, Map<String, AnnotatorResult>> searchMatches;

    private Map<OntologisedResult, List<SuggestedAnnotation>> annotations;


    @InjectedResource
    private ImageIcon confidenceKey, useTerm, useTermOver, doNotUseTerm, doNotUseTermOver, leftFieldIcon, rightFieldIcon;

    private ExtendedJList freeTextList, suggestedTermsList;

    private ViewTermDefinitionUI definitionUI;

    private JLabel useSuggestedButton, clearAnnotationsButton, useAnnotationButton, doNotUseAnnotationButton;

    private OntologisedResult currentlySelectedOntologyTerm;

    private static OntologyService ontologyService;

    static {
        ontologyService = new BioPortalClient();
    }

    public OntologiserAnnotationPane(Map<String, Map<String, AnnotatorResult>> searchMatches) {
        ResourceInjector.get("ontologiser-generator-package.style").inject(this);
        this.searchMatches = searchMatches;
        this.annotations = new HashMap<OntologisedResult, List<SuggestedAnnotation>>();
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        definitionUI = new ViewTermDefinitionUI();

        createListPanels();

        updateOntologySuggestionsForFreetextTerm();
    }


    private void createListPanels() {
        // create 2 list panels and a definition panel
        Box listPanel = Box.createHorizontalBox();

        freeTextList = new ExtendedJList(new OntologyAssignedListRenderer(), true);
        initiateFreeTextListContents();


        freeTextList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                currentlySelectedOntologyTerm = (OntologisedResult) propertyChangeEvent.getNewValue();
                if (suggestedTermsList != null) {

                    updateOntologySuggestionsForFreetextTerm();

                    if (currentlySelectedOntologyTerm.getAssignedOntology() == null) {
                        suggestedTermsList.clearSelection();
                        useAnnotationButton.setIcon(useTerm);
                        doNotUseAnnotationButton.setIcon(doNotUseTermOver);
                    } else {
                        suggestedTermsList.setSelectedValue(currentlySelectedOntologyTerm.getAssignedOntology(), true);
                        useAnnotationButton.setIcon(useTermOver);
                        doNotUseAnnotationButton.setIcon(doNotUseTerm);
                    }
                }
            }
        });

        listPanel.add(createListPanel(freeTextList, "Freetext Terms in ISAtab"));

        suggestedTermsList = new ExtendedJList(new ScoringConfidenceListRenderer(), false);

        suggestedTermsList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                SuggestedAnnotation selectedItem = (SuggestedAnnotation) suggestedTermsList.getSelectedValue();

                setAnnotation(selectedItem, currentlySelectedOntologyTerm);

                System.out.println("Term purl " + selectedItem.getAnnotatorResult().getOntologyTerm().getOntologyPurl() +
                        " Name = " + selectedItem.getAnnotatorResult().getOntologyTerm().getOntologyTermName());

                definitionUI.setContent(
                        new OntologyBranch(selectedItem.getAnnotatorResult().getOntologyTerm().getOntologySourceAccession(),
                                selectedItem.getAnnotatorResult().getOntologyTerm().getOntologyTermName()),
                        selectedItem.getAnnotatorResult().getOntologySource().getOntologyVersion(), ontologyService);

                repaint();
                // should clear other selections to ensure that other suggested terms are not mapping to the ontology result too
            }
        });


        JPanel suggestedTermListContainer = createListPanel(suggestedTermsList, "Suggested terms");
        suggestedTermListContainer.add(createSuggestionListKeyAndOptions(), BorderLayout.SOUTH);

        listPanel.add(suggestedTermListContainer);

        definitionUI.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), "Definition",
                TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        listPanel.add(definitionUI);

        add(listPanel, BorderLayout.CENTER);
    }

    private JPanel createListPanel(ExtendedJList list, String listTitle) {

        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setPreferredSize(new Dimension(220, 340));
        listContainer.setBackground(UIHelper.BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new EmptyBorder(1, 1, 1, 1));

        IAppWidgetFactory.makeIAppScrollPane(scrollPane);

        UIHelper.renderComponent(list.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        list.getFilterField().setBorder(null);

        Box fieldContainer = Box.createHorizontalBox();
        fieldContainer.add(new JLabel(leftFieldIcon));
        fieldContainer.add(list.getFilterField());
        fieldContainer.add(new ClearFieldUtility(list.getFilterField()));
        fieldContainer.add(new JLabel(rightFieldIcon));

        listContainer.add(fieldContainer, BorderLayout.SOUTH);
        listContainer.add(scrollPane, BorderLayout.CENTER);

        listContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), listTitle,
                TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        return listContainer;
    }

    private Container createSuggestionListKeyAndOptions() {
        Box container = Box.createVerticalBox();
        container.setBackground(UIHelper.BG_COLOR);

        JPanel confirmDeletionContainer = createShowUseOfAnnotationPane();

        container.add(confirmDeletionContainer);

        return container;
    }

    private JPanel createShowUseOfAnnotationPane() {
        final JPanel confirmDeletionContainer = new JPanel();
        confirmDeletionContainer.setLayout(new BoxLayout(confirmDeletionContainer, BoxLayout.LINE_AXIS));
        confirmDeletionContainer.setOpaque(false);
        confirmDeletionContainer.setVisible(true);

        useAnnotationButton = new JLabel(useTerm);

        useAnnotationButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                useAnnotationButton.setIcon(useTermOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                useAnnotationButton.setIcon(currentlySelectedOntologyTerm.getAssignedOntology() == null ? useTerm : useTermOver);
            }

            public void mousePressed(MouseEvent mouseEvent) {

                SuggestedAnnotation selectedItem = (SuggestedAnnotation) suggestedTermsList.getSelectedValue();
                setAnnotation(selectedItem, currentlySelectedOntologyTerm);

                useAnnotationButton.setIcon(useTermOver);
                doNotUseAnnotationButton.setIcon(doNotUseTerm);

            }
        });

        doNotUseAnnotationButton = new JLabel(doNotUseTerm);

        doNotUseAnnotationButton.addMouseListener(new MouseAdapter() {

            public void mouseEntered(MouseEvent mouseEvent) {
                doNotUseAnnotationButton.setIcon(doNotUseTermOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {

                doNotUseAnnotationButton.setIcon((currentlySelectedOntologyTerm  == null
                        || currentlySelectedOntologyTerm.getAssignedOntology() == null) ? doNotUseTermOver : doNotUseTerm);
            }

            public void mousePressed(MouseEvent mouseEvent) {
                clearAnnotation(currentlySelectedOntologyTerm, true);

                doNotUseAnnotationButton.setIcon(doNotUseTermOver);
                useAnnotationButton.setIcon(useTerm);

                repaint();
            }
        });

        confirmDeletionContainer.add(useAnnotationButton);
        confirmDeletionContainer.add(doNotUseAnnotationButton);

        return confirmDeletionContainer;
    }


    private void initiateFreeTextListContents() {
        for (String freeTextValue : searchMatches.keySet()) {

            OntologisedResult ontologisedResult = new OntologisedResult(freeTextValue);

            freeTextList.addItem(ontologisedResult);

            annotations.put(ontologisedResult, new ArrayList<SuggestedAnnotation>());

            for (String ontologyId : searchMatches.get(ontologisedResult.getFreeTextTerm()).keySet()) {
                SuggestedAnnotation annotatorResult = new SuggestedAnnotation(searchMatches.get(ontologisedResult.getFreeTextTerm()).get(ontologyId));
                annotations.get(ontologisedResult).add(annotatorResult);
            }

            ScoreAnalysisUtility.assignConfidenceLevels(annotations.get(ontologisedResult));
        }

        currentlySelectedOntologyTerm = (OntologisedResult) freeTextList.getSelectedValue();

    }


    private void updateOntologySuggestionsForFreetextTerm() {
        suggestedTermsList.getItems().clear();

        if (freeTextList.getSelectedIndex() != -1) {
            OntologisedResult ontologyResult = (OntologisedResult) freeTextList.getSelectedValue();

            for (SuggestedAnnotation listItem : annotations.get(ontologyResult)) {
                suggestedTermsList.addItem(listItem);
            }
        }

    }

    public void clearAnnotation() {
        for (OntologisedResult ontologisedResult : annotations.keySet()) {

            clearAnnotation(ontologisedResult, false);
        }
        refreshDisplayAfterClear();
    }

    private void clearAnnotation(OntologisedResult ontologisedResult, boolean refresh) {
        if (annotations.get(ontologisedResult) != null) {
            for (SuggestedAnnotation listItem : annotations.get(ontologisedResult)) {
                listItem.setMappedTo(null);
            }
        }
        ontologisedResult.setAssignedOntology(null);

        if (refresh) {
            refreshDisplayAfterClear();
        }
    }

    private void refreshDisplayAfterClear() {
        doNotUseAnnotationButton.setIcon(doNotUseTermOver);
        useAnnotationButton.setIcon(useTerm);
        repaint();
    }

    public void autoAnnotate() {
        for (OntologisedResult ontologisedResult : annotations.keySet()) {

            ScoreAnalysisUtility analysisUtility = new ScoreAnalysisUtility();

            ISAPair<Integer, SuggestedAnnotation> maxResult = analysisUtility.getMaxScore(annotations.get(ontologisedResult));

            setAnnotation(maxResult.snd, ontologisedResult);
        }

        repaint();
    }

    private void setAnnotation(SuggestedAnnotation selectedAnnnotationItem, OntologisedResult ontologisedResult) {
        if (selectedAnnnotationItem != null) {
            clearAnnotation(ontologisedResult, false);


            if (ontologisedResult == null) {
                if (freeTextList.getSelectedIndex() == -1) {
                    freeTextList.setSelectedIndex(0);
                }
                ontologisedResult = (OntologisedResult) freeTextList.getSelectedValue();
            }

            ontologisedResult.setAssignedOntology(selectedAnnnotationItem.getAnnotatorResult());
            selectedAnnnotationItem.setMappedTo(ontologisedResult);

            useAnnotationButton.setIcon(useTermOver);
            doNotUseAnnotationButton.setIcon(doNotUseTerm);

            repaint();
        }
    }

    public Set<OntologisedResult> getAnnotations() {
        return annotations.keySet();
    }


}
