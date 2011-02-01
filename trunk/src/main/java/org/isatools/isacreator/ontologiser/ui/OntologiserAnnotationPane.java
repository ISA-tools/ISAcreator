package org.isatools.isacreator.ontologiser.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
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
            useSuggestedIcon, useSuggestedIconOver;

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

        listPanel.add(createListPanel(freeTextList));

        suggestedTermsList = new ExtendedJList(new ScoringConfidenceListRenderer(), false);

        suggestedTermsList.addPropertyChangeListener("itemSelected", new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                SuggestedAnnotationListItem selectedItem = (SuggestedAnnotationListItem) suggestedTermsList.getSelectedValue();

                clearAnnotation(currentlySelectedOntologyTerm);

                currentlySelectedOntologyTerm.setAssignedOntology(selectedItem.getAnnotatorResult());

                System.out.println("\t Adding mapping to : " + currentlySelectedOntologyTerm);
                selectedItem.setMappedTo(currentlySelectedOntologyTerm);

                // should clear other selections to ensure that other suggested terms are not mapping to the ontology result too
            }
        });

        listPanel.add(createListPanel(suggestedTermsList));

        listPanel.add(definitionUI);

        add(listPanel, BorderLayout.CENTER);
    }

    private JPanel createListPanel(ExtendedJList list) {

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

        return listContainer;
    }


    private void initiateFreeTextListContents() {
        for (String freeTextValue : searchMatches.keySet()) {
            freeTextList.addItem(new OntologisedResult(freeTextValue));
        }
    }


    private void updateOntologySuggestionsForFreetextTerm() {
        suggestedTermsList.getItems().clear();

        if (freeTextList.getSelectedIndex() != -1) {
            OntologisedResult ontologyResult = (OntologisedResult) freeTextList.getSelectedValue();


            if (!annotations.containsKey(ontologyResult)) {

                annotations.put(ontologyResult, new ArrayList<SuggestedAnnotationListItem>());

                for (String ontologyId : searchMatches.get(ontologyResult.getFreeTextTerm()).keySet()) {
                    SuggestedAnnotationListItem annotatorResult = new SuggestedAnnotationListItem(searchMatches.get(ontologyResult.getFreeTextTerm()).get(ontologyId));
                    annotations.get(ontologyResult).add(annotatorResult);
                }
            }

            for (SuggestedAnnotationListItem listItem : annotations.get(ontologyResult)) {
                suggestedTermsList.addItem(listItem);
            }
        }
    }

    private void clearAnnotation(OntologisedResult ontologisedResult) {
        if (annotations.get(ontologisedResult) != null) {
            for (SuggestedAnnotationListItem listItem : annotations.get(ontologisedResult)) {
                listItem.setMappedTo(null);
            }
        }
    }
}
