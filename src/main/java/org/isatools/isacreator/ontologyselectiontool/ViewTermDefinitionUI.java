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

package org.isatools.isacreator.ontologyselectiontool;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.ontologymanager.OntologyService;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * ViewTermDefinitionUI
 *
 * @author Eamonn Maguire
 * @date Mar 1, 2010
 */


public class ViewTermDefinitionUI extends JPanel {

    @InjectedResource
    private ImageIcon placeHolder;

    // the LOADING image is loaded this way since loading using Dependency injection removes the gifs animation.
    private static final ImageIcon LOADING = new ImageIcon(ViewTermDefinitionUI.class.getResource("/images/ontologyselectiontool/loading.gif"));

    public final Dimension PANE_SIZE = new Dimension(200, 270);

    private JPanel swappableContainer;

    private Map<String, String> properties;

    public ViewTermDefinitionUI() {

        ResourceInjector.get("ontologyselectiontool-package.style").inject(this);

        setLayout(new BorderLayout());
        setPreferredSize(PANE_SIZE);
        setBackground(Color.WHITE);


        createGUI();

    }

    public void createGUI() {
        swappableContainer = new JPanel(new BorderLayout());
        swappableContainer.setBackground(UIHelper.BG_COLOR);
        swappableContainer.setPreferredSize(new Dimension(200, 270));
        swappableContainer.add(new JLabel(placeHolder));
        add(swappableContainer, BorderLayout.CENTER);
    }

    private JPanel createOntologyInformationPane(OntologyBranch term) {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(Color.WHITE);
        contentPane.setBorder(new EmptyBorder(2, 2, 2, 2));

        JEditorPane ontologyInfoPane = createOntologyInformationDisplay(term);

        JScrollPane ontologyInfoScroller = new JScrollPane(ontologyInfoPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        ontologyInfoScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        ontologyInfoScroller.setBorder(new EmptyBorder(2, 2, 2, 2));

        IAppWidgetFactory.makeIAppScrollPane(ontologyInfoScroller);

        contentPane.add(ontologyInfoScroller, BorderLayout.CENTER);

        return contentPane;
    }

    private JEditorPane createOntologyInformationDisplay(OntologyBranch term) {

        JEditorPane ontologyInfo = new JEditorPane();
        ontologyInfo.setContentType("text/html");
        ontologyInfo.setEditable(false);
        ontologyInfo.setBackground(UIHelper.BG_COLOR);

        String labelContent = "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 8.5px;" +
                "   color: #006838;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">";


        labelContent += "<b>Term name: </b>" + term.getBranchName() + "</p>";

        // special handling for ChEBI to get the structural image
        if (term.getBranchIdentifier().toLowerCase().contains("chebi")) {

            String chebiTermId = term.getBranchIdentifier().substring(term.getBranchIdentifier().indexOf(":") + 1);
            String chebiImageURL = "http://www.ebi.ac.uk/chebi/displayImage.do?defaultImage=true&imageIndex=0&chebiId=" + chebiTermId;
            labelContent += "<p><b>Chemical structure:</b>" +
                    "<p/>" +
                    "<img src=\"" + chebiImageURL + "\" alt=\"chemical structure for " + term.getBranchName() + "\" width=\"150\" height=\"150\"/>";

        }

        properties = sortMap(properties);

        if (properties != null && properties.size() > 0) {
            for (String propertyType : properties.keySet()) {
                if (propertyType != null) {

                    labelContent += ("<p><b>" + propertyType + ": </b>");
                    labelContent += (properties.get(propertyType) == null
                            ? "no definition available"
                            : properties.get(propertyType)
                            + "</font></p>");
                }
            }
        } else {
            labelContent += "<p>No definition found for this term! This can be due to 2 reasons: " +
                    "1) Unfortunately, not all terms have their definitions supplied; and 2) the " +
                    "ability to view the definitions for a term in this tool is not yet fully supported.</p>";
        }

        labelContent += "</body></html>";

        ontologyInfo.setText(labelContent);

        return ontologyInfo;
    }

    private Map<String, String> sortMap(Map<String, String> toSort) {
        Map<String, String> sortedMap = new ListOrderedMap<String, String>();

        if (toSort != null) {

            Set<String> keys = toSort.keySet();

            java.util.List<String> sortedKeys = new ArrayList<String>();
            for (String key : keys) {
                if (key != null) {
                    sortedKeys.add(key);
                }
            }

            Collections.sort(sortedKeys);

            for (String key : sortedKeys) {
                sortedMap.put(key, toSort.get(key));
            }
        }

        return sortedMap;
    }


    public void setContent(OntologyBranch term, String searchOntology, OntologyService ontologyService) {

        if (properties != null) {
            properties.clear();
        }
        performSearch(term, searchOntology, ontologyService);
    }

    private void performSearch(final OntologyBranch term, final String searchOntology, final OntologyService ontologyService) {
        Thread performer = new Thread(new Runnable() {
            public void run() {
                try {
                    setCurrentPage(new JLabel(LOADING));
                    properties = ontologyService.getTermMetadata(term.getBranchIdentifier(), searchOntology);
                    setCurrentPage(createOntologyInformationPane(term));
                } catch (Exception e) {
                    setCurrentPage(createOntologyInformationPane(term));
                    System.err.println("Failed to connect to ontology client: " + e.getMessage());
                } finally {
                    ViewTermDefinitionUI.this.validate();
                    ViewTermDefinitionUI.this.repaint();
                }
            }
        });

        performer.start();
    }

    /**
     * Changes Container being shown in the swappableContainer panel
     *
     * @param newContainer - Container to change to
     */
    private void setCurrentPage(Container newContainer) {
        swappableContainer.removeAll();
        swappableContainer.add(newContainer);
        swappableContainer.validate();
    }
}
