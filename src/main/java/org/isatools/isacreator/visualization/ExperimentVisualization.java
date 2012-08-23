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

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.visualization.tree.TreeView;
import prefuse.controls.ControlAdapter;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class ExperimentVisualization extends JLayeredPane {
    private JPanel detailContainer = new JPanel();
    private Investigation investigation;
    private boolean fullGUI;
    private Study study;
    private Map<String, AssayAnalysis> assayAnalyses;

    private static JLabel improvementsLab;

    static {
        improvementsLab = new JLabel(new ImageIcon(ExperimentVisualization.class.getResource("/images/visualization/improvements.png")));
    }

    public ExperimentVisualization(final Investigation investigation) {
        this(investigation, true);
    }

    public ExperimentVisualization(final Investigation investigation, boolean fullGUI) {
        this.investigation = investigation;
        this.fullGUI = fullGUI;
        assayAnalyses = new HashMap<String, AssayAnalysis>();
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);


        try {
            Thread populate = new Thread(new Runnable() {
                public void run() {
                    ApplicationManager.getUserInterfaceForISASection(investigation)
                            .getDataEntryEnvironment().setOverviewIconAsBusy(true);
                    populateAssayInformationInBackground();
                }
            });
            populate.start();
        } finally {

            ApplicationManager.getUserInterfaceForISASection(investigation)
                    .getDataEntryEnvironment().setOverviewIconAsBusy(false);
        }
    }

    public ExperimentVisualization(final Study study) {
        this.study = study;
        assayAnalyses = new HashMap<String, AssayAnalysis>();
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        Thread populate = new Thread(new Runnable() {
            public void run() {
                populateAssayInformationInBackground();
            }
        });
        populate.start();
    }

    private void populateAssayInformationInBackground() {

        Collection<String> toIterateAround = (investigation == null) ? study.getAssays().keySet() : investigation.getAssays().keySet();

        for (String aRef : toIterateAround) {
            Assay assay = getAssay(aRef);
            AssayAnalysis aa = new AssayAnalysis(assay);
            assayAnalyses.put(aRef, aa);
        }
    }


    public void createGUI() {


        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JPanel containerPanel = new JPanel(new BorderLayout());
                containerPanel.setBackground(UIHelper.BG_COLOR);

                File file;
                GenerateView gv = new GenerateView();
                if (investigation == null) {
                    file = gv.generateView(study);
                } else {
                    file = gv.generateView(investigation);
                }


                if (fullGUI && investigation != null) {
                    containerPanel.add(createNorthPanel(), BorderLayout.NORTH);
                    containerPanel.setBorder(new EtchedBorder(UIHelper.DARK_GREEN_COLOR,
                            UIHelper.DARK_GREEN_COLOR));
                }

                containerPanel.add(createSouthPanel(), BorderLayout.SOUTH);
                containerPanel.add(createTreeView(file),
                        BorderLayout.CENTER);

                add(containerPanel, BorderLayout.CENTER);
            }
        });

    }

    /**
     * Creates north panel to contain title "experiment visualization"
     *
     * @return JPanel containing north panel
     */
    private JPanel createNorthPanel() {
        JPanel northPanel = new JPanel(new GridLayout(1, 2));
        northPanel.setBackground(UIHelper.BG_COLOR);

        JLabel help = new JLabel(new ImageIcon(getClass().getResource("/images/visualization/help.png")), JLabel.LEFT);
        help.setOpaque(false);

        northPanel.add(help);

        JLabel image = new JLabel(new ImageIcon(getClass()
                .getResource("/images/visualization/viz_header.png")),
                JLabel.RIGHT);
        image.setVerticalAlignment(JLabel.TOP);
        image.setOpaque(false);

        northPanel.add(image);

        return northPanel;
    }

    private JPanel createSouthPanel() {
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.LINE_AXIS));
        southPanel.setPreferredSize(new Dimension(getWidth(), 300));
        southPanel.setBackground(UIHelper.BG_COLOR);

        DetailsPanel details = new DetailsPanel(southPanel.getWidth(), 300);
        detailContainer.add(details);

        southPanel.add(detailContainer);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                detailContainer.add(improvementsLab);
                detailContainer.getParent().validate();
            }
        });


        return southPanel;
    }

    public JPanel createTreeView(File datafile) {

        Color background = UIHelper.BG_COLOR;
        Color foreground = Color.BLACK;

        Tree t = null;

        System.out.println("Reading " + datafile.getAbsolutePath());

        System.out.println("Does data file exist? " + datafile.exists());
        try {
            t = (Tree) new TreeMLReader().readGraph(datafile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // create a new treemap
        final TreeView tview = new TreeView(t,
                new Dimension(getWidth(), 500));

        tview.setBackground(background);
        tview.setForeground(foreground);

        // create a search panel for the tree map
        tview.addControlListener(new ControlAdapter() {

            public void itemClicked(VisualItem item, MouseEvent e) {
                if (item.canGetString(TreeView.NAME_STRING) && item.canGetString("type")) {
                    final String name = item.getString(TreeView.NAME_STRING);
                    final String type = item.getString("type");

                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            detailContainer.removeAll();

                            if (type.toLowerCase().contains("investigation")) {
                                detailContainer.add(new InvestigationInfoPanel(investigation, getWidth(), 300));
                            }

                            if (type.toLowerCase().contains("study")) {
                                Study s;
                                if (study != null) {
                                    s = study;
                                } else {
                                    s = investigation.getStudies().get(name);
                                }
                                detailContainer.add(new StudyInfoPanel(s, getWidth(), 300));
                            }

                            if (type.toLowerCase().contains("assay")) {

                                AssayAnalysis aa = assayAnalyses.get(name.substring(0, name.indexOf("(")).trim());
                                if (aa == null) {
                                    aa = new AssayAnalysis(getAssay(name));
                                    assayAnalyses.put(name, aa);
                                }

                                AssayInfoPanel aip = new AssayInfoPanel(aa, getWidth(), 300);
                                detailContainer.add(aip);
                            }

                            detailContainer.getParent().validate();
                        }
                    });

                }
            }
        });

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);
        panel.setForeground(foreground);
        panel.add(tview, BorderLayout.CENTER);

        return panel;
    }

    private Assay getAssay(String assayRef) {
        // the assay ref is not pure since some details about the assay are held in this String (the measurement and technology type)
        if (assayRef.contains("(")) {
            assayRef = assayRef.substring(0, assayRef.indexOf("(")).trim();
        } else {
            assayRef = assayRef.trim();
        }

        if (investigation == null) {
            return study.getAssays()
                    .get(assayRef);
        } else {
            String assayStudy = investigation.getAssays().get(assayRef);
            return investigation.getStudies().get(assayStudy).getAssays()
                    .get(assayRef);
        }
    }


    class DetailsPanel extends JPanel {
        JLabel header;
        JEditorPane info;

        public DetailsPanel(int width, int height) {
            setLayout(new BorderLayout());
            setBackground(UIHelper.BG_COLOR);
            setMinimumSize(new Dimension((int) (width * 0.80), (int) (height * 0.90)));
            header = new JLabel();
            header.setHorizontalAlignment(JLabel.LEFT);
            header.setVerticalAlignment(JLabel.TOP);

            info = new JEditorPane();

            info.setContentType("text/html");
            info.setEditable(false);
            info.setBackground(UIHelper.BG_COLOR);

            JScrollPane infoScroller = new JScrollPane(info, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            infoScroller.getViewport().setBackground(UIHelper.BG_COLOR);
            infoScroller.setBorder(null);

            IAppWidgetFactory.makeIAppScrollPane(infoScroller);

            add(header, BorderLayout.NORTH);
            add(infoScroller, BorderLayout.WEST);

        }

        public void setHeader(Icon icon) {
            header.removeAll();
            header.setIcon(icon);
        }

        public void setInfo(String infoText) {
            info.removeAll();
            info.setText(infoText);
        }


    }
}
