package org.isatools.isacreator.visualization.workflowvisualization;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.visualization.graph.GraphView;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import prefuse.Constants;
import prefuse.controls.ControlAdapter;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 06/03/2012
 *         Time: 18:09
 */
public class WorkflowVisualization extends AnimatableJFrame {

    // will visualize a biological workflow. Tree will be formed using prefuse. Images will come from the
    // glyphs created for the visweek paper.

    // we need the unique branches of processing (so doesn't include names, just processes enacted)

    // grouped by pertubation, e.g. factor groups.

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("workflow-package.style").load(
                WorkflowVisualization.class.getResource("/dependency-injections/workflow-package.properties"));
    }

    @InjectedResource
    private Image workflowVisIcon, workflowVisIconInactive;
    private File fileName;
    private GraphView view;


    public WorkflowVisualization(File fileName) {
        this.fileName = fileName;
        ResourceInjector.get("workflow-package.style").inject(this);
    }

    private NodeDetail nodeDetailView = null;

    public void createGUI() {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 840));
        setBackground(UIHelper.BG_COLOR);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));
        addTitlePane();
        createCentralPanel();
        addSouthPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addTitlePane() {
        HUDTitleBar titleBar = new HUDTitleBar(workflowVisIcon, workflowVisIconInactive, false);
        add(titleBar, BorderLayout.NORTH);
        titleBar.installListeners();
    }

    private void createCentralPanel() {
        String datafile = fileName.getAbsolutePath();

        final Graph g;
        try {
            g = new GraphMLReader().readGraph(datafile);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    String label = "image";
                    view = new GraphView(g, label, Constants.ORIENT_TOP_BOTTOM, new Dimension(750, 900));
                    view.getDisplay().addControlListener(new ControlAdapter() {

                        @Override
                        public void mousePressed(MouseEvent mouseEvent) {
                            // do nothing
                        }

                        @Override
                        public void itemClicked(VisualItem visualItem, MouseEvent mouseEvent) {
                            if (nodeDetailView == null) {
                                nodeDetailView = new NodeDetail();
                                nodeDetailView.createGUI();
                            }

                            setContentForNodeDetail(visualItem);
                            if (!nodeDetailView.isShowing()) {
                                nodeDetailView.setVisible(true);
                            }
                        }

                        @Override
                        public void itemEntered(VisualItem visualItem, MouseEvent mouseEvent) {
                            if (visualItem.canGetString("id")) {
                                TupleSet focused = view.getDisplay().getVisualization().getFocusGroup("highlighted");
                                focused.addTuple(visualItem);
                                view.getDisplay().getVisualization().run("draw");
                                if (nodeDetailView != null && nodeDetailView.isShowing()) {
                                    setContentForNodeDetail(visualItem);
                                }
                            }

                        }

                        @Override
                        public void itemExited(VisualItem visualItem, MouseEvent mouseEvent) {
                            if (visualItem.canGetString("id")) {
                                TupleSet focused = view.getDisplay().getVisualization().getFocusGroup("highlighted");
                                focused.removeTuple(visualItem);
                                view.getDisplay().getVisualization().run("draw");
                            }
                        }
                    });

                    add(view, BorderLayout.CENTER);
                }
            });

        } catch (DataIOException e) {
            System.err.println("Graph for " + fileName.getName() + " does not exist.");
        }
    }

    private void setContentForNodeDetail(VisualItem visualItem) {
        try {
            nodeDetailView.setContent(new WorkflowVisualisationNode(visualItem.get("image").toString(),
                    visualItem.get("type").toString(), visualItem.get("value").toString(),
                    visualItem.get("taxonomy").toString()));
        } catch (Exception e) {
            // ignore the error.
        }
    }

    private void addSouthPanel() {
        FooterPanel footerPanel = new FooterPanel(this);
        add(footerPanel, BorderLayout.SOUTH);
    }

}
