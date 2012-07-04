package org.isatools.isacreator.visualization.graph;

import org.isatools.isacreator.common.UIHelper;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.filter.GraphDistanceFilter;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/07/2012
 *         Time: 11:40
 */
public class GraphView extends JPanel {

    private static final String graph = "graph";
    private static final String nodes = "graph.nodes";
    private static final String edges = "graph.edges";
    private Display display;

    private static int backgroundColor = ColorLib.rgb(255, 255, 255);

    private Visualization m_vis;

    public GraphView(Graph g, String label, int orientation, Dimension size) {
        // create a new, empty visualization for our data
        initialiseGraphView(g, label, orientation, size);
    }

    private void initialiseGraphView(Graph g, String label, int orientation, Dimension size) {
        setSize(size);
        m_vis = new Visualization();
        m_vis.addFocusGroup("selected");

        LabelRenderer tr = new LabelRenderer(label.equalsIgnoreCase("image") ? null :
                label, label.equalsIgnoreCase("image") ? label : null);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // adds graph to visualization and sets renderer label field
        setGraph(g);

        int hops = 10;
        final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

        ColorAction fill = new ColorAction(nodes, VisualItem.FILLCOLOR, backgroundColor);
        fill.add("ingroup('selected')", ColorLib.rgb(105, 210, 231));

        ActionList draw = new ActionList(500);
        draw.add(filter);
        draw.add(fill);
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));
        draw.add(new RepaintAction());

        ActionList animate = new ActionList();

        NodeLinkTreeLayout layout = new NodeLinkTreeLayout(graph);
        layout.setDepthSpacing(70);
        layout.setBreadthSpacing(70);
        layout.setOrientation(orientation);

        animate.add(new QualityControlAnimator());
        animate.add(new LocationAnimator(nodes));
        animate.add(layout);

        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        // set up a display to show the visualization
        display = new Display(m_vis);
        display.setSize(size);

        // main display controls
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());

        // now we run our action list
        m_vis.run("draw");
        m_vis.run("layout");

        add(display);
    }

    public Display getDisplay() {
        return display;
    }

    public void setGraph(Graph g) {
        VisualGraph vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.TRUE);
        VisualItem f = (VisualItem) vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
    }

    public static void main(String[] args) {
        UILib.setPlatformLookAndFeel();

        // create graphview
        String datafile = "/Users/eamonnmaguire/git/eamonnrepo/GraphMacro/data/graphml-test.xml";
        String label = "value";

        Graph g = null;

        try {
            g = new GraphMLReader().readGraph(datafile);

            final GraphView view = new GraphView(g, label, Constants.ORIENT_TOP_BOTTOM, new Dimension(800, 600));

            view.getDisplay().addControlListener(new ControlAdapter() {

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    // do nothing
                }

                @Override
                public void itemClicked(VisualItem visualItem, MouseEvent mouseEvent) {
                    // do nothing.
                }

                @Override
                public void itemEntered(VisualItem visualItem, MouseEvent mouseEvent) {
                    if (visualItem.canGetString("id")) {
                        System.out.println(visualItem.get("id"));
                        TupleSet focused = view.getDisplay().getVisualization().getFocusGroup("selected");
                        focused.addTuple(visualItem);
                        view.getDisplay().getVisualization().run("draw");
                    }
                }

                @Override
                public void itemExited(VisualItem visualItem, MouseEvent mouseEvent) {
                    if (visualItem.canGetString("id")) {
                        System.out.println(visualItem.get("id"));
                        TupleSet focused = view.getDisplay().getVisualization().getFocusGroup("selected");
                        focused.removeTuple(visualItem);
                        view.getDisplay().getVisualization().run("draw");
                    }
                }
            }
            );

            // launch window
            JFrame frame = new JFrame("Workflow Viewer | Graph");
            frame.setBackground(UIHelper.BG_COLOR);

            frame.setContentPane(view);
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        } catch (
                DataIOException e
                )

        {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
