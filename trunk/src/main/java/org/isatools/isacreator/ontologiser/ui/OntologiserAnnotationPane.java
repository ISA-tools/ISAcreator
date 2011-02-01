package org.isatools.isacreator.ontologiser.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.ontologiser.model.OntologisedResult;
import org.isatools.isacreator.ontologiser.model.SuggestedAnnotationListItem;
import org.isatools.isacreator.ontologiser.ui.listrenderer.OntologyAssignedListRenderer;
import org.isatools.isacreator.ontologiser.ui.listrenderer.ScoringConfidenceListRenderer;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologyselectiontool.ViewTermDefinitionUI;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This pane will contain the CORE GUI components for the Ontologiser function. Including 3 sections containing:
 * 1) The list of unannotated terms found in the spreadsheet(s);
 * 2) The list of suggested terms for each unannotated term; and
 * 3) The definition pane containg definitions
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 28/01/2011
 *         Time: 18:59
 */
public class OntologiserAnnotationPane extends JPanel {

    private Map<String, Map<String, AnnotatorResult>> searchMatches;

    private Map<OntologisedResult, List<SuggestedAnnotationListItem>> annotations;


    @InjectedResource
    private ImageIcon optionsIcon, clearAnnotationsIcon, clearAnnotationsIconOver,
            useSuggestedIcon, useSuggestedIconOver, confidenceKey;

    private ExtendedJList freeTextList, suggestedTermsList;

    private ViewTermDefinitionUI definitionUI;

    private JLabel useSuggestedButton, clearAnnotationsButton;

    private OntologisedResult currentlySelectedOntologyTerm;

    public OntologiserAnnotationPane(Map<String, Map<String, AnnotatorResult>> searchMatches) {
        ResourceInjector.get("ontologiser-generator-package.style").inject(this);
        this.searchMatches = searchMatches;
        this.annotations = new HashMap<OntologisedResult, List<SuggestedAnnotationListItem>>();
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        definitionUI = new ViewTermDefinitionUI();

        createOptionsPanel();
        createListPanels();
    }

    private void createOptionsPanel() {
        Box optionsPanel = Box.createHorizontalBox();

        useSuggestedButton = new JLabel(useSuggestedIcon);
        useSuggestedButton.setHorizontalAlignment(SwingConstants.LEFT);
        useSuggestedButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                useSuggestedButton.setIcon(useSuggestedIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                useSuggestedButton.setIcon(useSuggestedIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                useSuggestedButton.setIcon(useSuggestedIcon);
                autoAnnotate();
            }
        });

        clearAnnotationsButton = new JLabel(clearAnnotationsIcon);
        clearAnnotationsButton.setHorizontalAlignment(SwingConstants.LEFT);
        clearAnnotationsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                clearAnnotationsButton.setIcon(clearAnnotationsIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                clearAnnotationsButton.setIcon(clearAnnotationsIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                clearAnnotationsButton.setIcon(clearAnnotationsIcon);
                clearAnnotation();
            }
        });

        optionsPanel.add(new JLabel(optionsIcon));
        optionsPanel.add(useSuggestedButton);
        optionsPanel.add(clearAnnotationsButton);

        optionsPanel.setAlignmentX(SwingConstants.LEFT);

        add(optionsPanel, BorderLayout.NORTH);
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
//                        checkAndDisplayAppropriateQuestion(currentlySelectedField.getMgRastTermMappedTo());
                    } else {
                        suggestedTermsList.setSelectedValue(currentlySelectedOntologyTerm.getAssignedOntology(), true);
                    }
                }
            }
        });

        listPanel.add(createListPanel(freeTextList, "Freetext Terms"));

        suggestedTermsList = new ExtendedJList(new ScoringConfidenceListRenderer(), false);

        suggestedTermsList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                SuggestedAnnotationListItem selectedItem = (SuggestedAnnotationListItem) suggestedTermsList.getSelectedValue();

                setAnnotation(selectedItem, currentlySelectedOntologyTerm);
                // should clear other selections to ensure that other suggested terms are not mapping to the ontology result too
            }
        });

        JPanel suggestedTermListContainer = createListPanel(suggestedTermsList, "Suggested terms");
        suggestedTermListContainer.add(new JLabel(confidenceKey), BorderLayout.SOUTH);

        listPanel.add(suggestedTermListContainer);

        definitionUI.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), "Definition",
                TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        listPanel.add(definitionUI);

        add(listPanel, BorderLayout.CENTER);
    }

    private JPanel createListPanel(ExtendedJList list, String listTitle) {

        JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setPreferredSize(new Dimension(200, 300));
        listContainer.setBackground(UIHelper.BG_COLOR);

        JScrollPane scrollPane = new JScrollPane(list,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(new EmptyBorder(1, 1, 1, 1));

        IAppWidgetFactory.makeIAppScrollPane(scrollPane);

        UIHelper.renderComponent(list.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        Box fieldContainer = Box.createHorizontalBox();
        fieldContainer.add(list.getFilterField());
        fieldContainer.add(new ClearFieldUtility(list.getFilterField()));

        listContainer.add(fieldContainer, BorderLayout.NORTH);
        listContainer.add(scrollPane, BorderLayout.CENTER);

        listContainer.setBorder(new TitledBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6), listTitle,
                TitledBorder.DEFAULT_POSITION, TitledBorder.DEFAULT_JUSTIFICATION, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR));

        return listContainer;
    }


    private void initiateFreeTextListContents() {
        for (String freeTextValue : searchMatches.keySet()) {


            OntologisedResult ontologisedResult = new OntologisedResult(freeTextValue);

            freeTextList.addItem(ontologisedResult);

            annotations.put(ontologisedResult, new ArrayList<SuggestedAnnotationListItem>());

            for (String ontologyId : searchMatches.get(ontologisedResult.getFreeTextTerm()).keySet()) {
                SuggestedAnnotationListItem annotatorResult = new SuggestedAnnotationListItem(searchMatches.get(ontologisedResult.getFreeTextTerm()).get(ontologyId));
                annotations.get(ontologisedResult).add(annotatorResult);
            }
        }
    }


    private void updateOntologySuggestionsForFreetextTerm() {
        suggestedTermsList.getItems().clear();

        if (freeTextList.getSelectedIndex() != -1) {
            OntologisedResult ontologyResult = (OntologisedResult) freeTextList.getSelectedValue();

            for (SuggestedAnnotationListItem listItem : annotations.get(ontologyResult)) {
                suggestedTermsList.addItem(listItem);
            }
        }
    }

    private void clearAnnotation() {
        for (OntologisedResult ontologisedResult : annotations.keySet()) {

            if (annotations.containsKey(ontologisedResult)) {
                for (SuggestedAnnotationListItem listItem : annotations.get(ontologisedResult)) {
                    listItem.setMappedTo(null);
                }
            }

            ontologisedResult.setAssignedOntology(null);
        }

        repaint();
    }

    private void clearAnnotation(OntologisedResult ontologisedResult) {
        if (annotations.get(ontologisedResult) != null) {
            for (SuggestedAnnotationListItem listItem : annotations.get(ontologisedResult)) {
                listItem.setMappedTo(null);
            }
        }
    }

    private void autoAnnotate() {
        for (OntologisedResult ontologisedResult : annotations.keySet()) {
            System.out.println("Ontologised Result: " + ontologisedResult.getFreeTextTerm());
            int maxScore = Integer.MIN_VALUE;
            SuggestedAnnotationListItem suggestedAnnotation = null;

            for (SuggestedAnnotationListItem listItem : annotations.get(ontologisedResult)) {
                System.out.println("\tPossible annotation: " + listItem.getAnnotatorResult().toString() + " - > score = " + listItem.getAnnotatorResult().getScore());
                if (listItem.getAnnotatorResult().getScore() > maxScore) {
                    maxScore = listItem.getAnnotatorResult().getScore();
                    suggestedAnnotation = listItem;
                }
            }

            setAnnotation(suggestedAnnotation, ontologisedResult);
        }

        repaint();

    }

    private void setAnnotation(SuggestedAnnotationListItem selectedAnnnotationItem, OntologisedResult ontologisedResult) {
        clearAnnotation(ontologisedResult);

        ontologisedResult.setAssignedOntology(selectedAnnnotationItem.getAnnotatorResult());
        selectedAnnnotationItem.setMappedTo(ontologisedResult);
    }
}
