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

package org.isatools.isacreator.visualization;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

/**
 * interface where Assay Information appears such as the technology, measurement and so forth as well as a utility to
 * view the treatment groups which appear within the Assay file.
 *
 * @author Eamonn Maguire
 * @date Feb 23, 2009
 */


public class AssayInfoPanel extends JPanel implements MouseListener {

    private static final String VIEW_INFO_TEXT = "view information  ";
    private static final String VIEW_VIZ_TEXT = "view treatment groups ";

    private static final String VIEW_NODE_NAMES = "view sample names  ";
    private static final String HIDE_NODE_NAMES = "hide sample names ";


    private JPanel assayInformation = new JPanel();
    private JPanel sampleGroupVisualization = new JPanel();
    private AssayAnalysis assayAnalysis;
    private JLabel viewSwitch;
    private JLabel nodeTypeSwitch;
    private Map<String, List<Object>> treatmentGroups;
    private JPanel swappableContainer = new JPanel();
    private TreatmentGroupViewer tgv = null;
    private int width;
    private int height;

    public AssayInfoPanel(AssayAnalysis assayAnalysis, int width, int height) {

        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));
        this.assayAnalysis = assayAnalysis;
        treatmentGroups = assayAnalysis.getTreatmentGroups();

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);
        assayInformation.setBackground(UIHelper.BG_COLOR);
        sampleGroupVisualization.setBackground(UIHelper.BG_COLOR);
        swappableContainer.setBackground(UIHelper.BG_COLOR);

        createGUI();

    }

    public void createGUI() {
        JPanel headerPanel = new JPanel(new GridLayout(1, 3));
        headerPanel.setBackground(UIHelper.BG_COLOR);

        viewSwitch = UIHelper.createLabel(VIEW_VIZ_TEXT, UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);
        viewSwitch.setHorizontalAlignment(JLabel.RIGHT);
        viewSwitch.addMouseListener(this);

        nodeTypeSwitch = UIHelper.createLabel(VIEW_NODE_NAMES, UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);
        nodeTypeSwitch.setHorizontalAlignment(JLabel.RIGHT);
        nodeTypeSwitch.addMouseListener(this);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.setOpaque(false);

        buttonPanel.add(viewSwitch);
        buttonPanel.add(nodeTypeSwitch);

        nodeTypeSwitch.setVisible(false);

        headerPanel.add(new JLabel(new ImageIcon(getClass().getResource("/images/visualization/assayinfo.png")), JLabel.LEFT));
        headerPanel.add(UIHelper.createLabel(assayAnalysis.getA().getAssayReference(), UIHelper.VER_14_BOLD, UIHelper.LIGHT_GREEN_COLOR, JLabel.CENTER));

        if (treatmentGroups.size() > 0) {
            headerPanel.add(buttonPanel);
        } else {
            // fill up allocation 3 slots in any case, otherwise view will be unbalanced!
            viewSwitch.setText("");
            headerPanel.add(viewSwitch);
        }

        add(headerPanel, BorderLayout.NORTH);

        swappableContainer.add(prepareAssayInformation());

        add(swappableContainer, BorderLayout.CENTER);
    }

    private Map<String, String> getAssayDetails() {
        Map<String, String> assayDetails = new ListOrderedMap<String, String>();

        // the assay ref is not pure since some details about the assay are held in this String (the measurement and technology type)

        assayDetails.put("Assay Reference", assayAnalysis.getA().getAssayReference());
        assayDetails.put("Measurement/Endpoint", assayAnalysis.getA().getMeasurementEndpoint());
        assayDetails.put("Technology type", assayAnalysis.getA().getTechnologyType());
        assayDetails.put("# Treatment groups", String.valueOf(treatmentGroups.size()));

        return assayDetails;
    }

    private JPanel prepareAssayInformation() {

        assayInformation.removeAll();

        JEditorPane currentlyShowingInfo = new JEditorPane();
        currentlyShowingInfo.setContentType("text/html");
        currentlyShowingInfo.setEditable(false);
        currentlyShowingInfo.setBackground(UIHelper.BG_COLOR);
        currentlyShowingInfo.setPreferredSize(new Dimension(width - 10, height - 30));

        Map<String, String> data = getAssayDetails();

        String labelContent = "<html>" + "<head>" +
                "<style type=\"text/css\">" + "<!--" + ".bodyFont {" +
                "   font-family: Verdana;" + "   font-size: 9px;" +
                "   color: #006838;" + "}" + "-->" + "</style>" + "</head>" +
                "<body class=\"bodyFont\">";

        for (Object key : data.keySet()) {
            labelContent += ("<p><b>" + ((String) key).trim() + ": </b>");
            labelContent += (data.get(key) + "</font></p>");
        }

        labelContent += "</body></html>";

        currentlyShowingInfo.setText(labelContent);

        assayInformation.add(currentlyShowingInfo);

        return assayInformation;
    }

    private JPanel prepareAssayTreatments() {

        if (treatmentGroups.size() > 0) {
            tgv = new TreatmentGroupViewer(treatmentGroups, assayAnalysis.getNumberOfAssays(), width - 10, height - 30);
        }
        sampleGroupVisualization.removeAll();
        sampleGroupVisualization.add(tgv);
        return sampleGroupVisualization;
    }

    public void mouseClicked(MouseEvent event) {

    }

    public void mousePressed(MouseEvent event) {
        if (event.getSource() == viewSwitch) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    swappableContainer.removeAll();
                    if (viewSwitch.getText().equals(VIEW_INFO_TEXT)) {
                        swappableContainer.add(prepareAssayInformation());
                        viewSwitch.setText(VIEW_VIZ_TEXT);
                        nodeTypeSwitch.setVisible(false);
                    } else {
                        swappableContainer.add(prepareAssayTreatments());
                        viewSwitch.setText(VIEW_INFO_TEXT);
                        nodeTypeSwitch.setVisible(true);
                    }
                    swappableContainer.revalidate();

                }
            });
        } else if (event.getSource() == nodeTypeSwitch) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (tgv != null) {
                        if (nodeTypeSwitch.getText().equals(VIEW_NODE_NAMES)) {
                            tgv.setRenderer(TreatmentGroupViewer.RENDER_TEXT);
                            nodeTypeSwitch.setText(HIDE_NODE_NAMES);
                        } else {

                            tgv.setRenderer(TreatmentGroupViewer.RENDER_CIRCLE);
                            nodeTypeSwitch.setText(VIEW_NODE_NAMES);
                        }
                    }
                }
            });
        }
    }

    public void mouseReleased(MouseEvent event) {

    }

    public void mouseEntered(MouseEvent event) {
        if (event.getSource() instanceof JLabel) {
            JLabel lab = (JLabel) event.getSource();
            lab.setForeground(UIHelper.LIGHT_GREEN_COLOR);
        }
    }

    public void mouseExited(MouseEvent event) {
        if (event.getSource() instanceof JLabel) {
            JLabel lab = (JLabel) event.getSource();
            lab.setForeground(UIHelper.DARK_GREEN_COLOR);
        }
    }
}
