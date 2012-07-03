package org.isatools.isacreator.visualization.graph;

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
import prefuse.action.layout.graph.TreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.Tuple;
import prefuse.data.event.TupleSetListener;
import prefuse.data.io.GraphMLReader;
import prefuse.data.tuple.TupleSet;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.GraphicsLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.display.ItemBoundsListener;
import prefuse.util.ui.UILib;
import prefuse.visual.VisualGraph;
import prefuse.visual.VisualItem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

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

    private static int backgroundColor = ColorLib.rgb(255, 255, 255);

    private Visualization m_vis;

    public GraphView(Graph g, String label, int orientation) {
        setLayout(new BorderLayout());
        // create a new, empty visualization for our data
        m_vis = new Visualization();

        // --------------------------------------------------------------------
        // set up the renderers

        LabelRenderer tr = new LabelRenderer(label.equalsIgnoreCase("image") ? null : label, label.equalsIgnoreCase("image") ? label : null);
        tr.setRoundedCorner(8, 8);
        m_vis.setRendererFactory(new DefaultRendererFactory(tr));

        // --------------------------------------------------------------------
        // register the data with a visualization

        // adds graph to visualization and sets renderer label field
        setGraph(g);

        // fix selected focus nodes
        TupleSet focusGroup = m_vis.getGroup(Visualization.FOCUS_ITEMS);
        focusGroup.addTupleSetListener(new TupleSetListener() {
            public void tupleSetChanged(TupleSet ts, Tuple[] add, Tuple[] rem) {
                for (Tuple aRem : rem) ((VisualItem) aRem).setFixed(false);
                for (Tuple anAdd : add) {
                    ((VisualItem) anAdd).setFixed(false);
                    ((VisualItem) anAdd).setFixed(true);
                }
                if (ts.getTupleCount() == 0) {
                    ts.addTuple(rem[0]);
                    ((VisualItem) rem[0]).setFixed(false);
                }
                m_vis.run("draw");
            }
        });

        int hops = 10;
        final GraphDistanceFilter filter = new GraphDistanceFilter(graph, hops);

        ColorAction fill = new ColorAction(nodes,
                VisualItem.FILLCOLOR, backgroundColor);
        fill.add(VisualItem.HIGHLIGHT, backgroundColor);

        ActionList draw = new ActionList();
        draw.add(filter);
        draw.add(fill);
        draw.add(new ColorAction(nodes, VisualItem.STROKECOLOR, 0));
        draw.add(new ColorAction(nodes, VisualItem.TEXTCOLOR, ColorLib.rgb(0, 0, 0)));
        draw.add(new ColorAction(edges, VisualItem.FILLCOLOR, ColorLib.gray(200)));
        draw.add(new ColorAction(edges, VisualItem.STROKECOLOR, ColorLib.gray(200)));

        ActionList animate = new ActionList(Activity.DEFAULT_STEP_TIME);

        NodeLinkTreeLayout layout = new NodeLinkTreeLayout(graph);
        layout.setDepthSpacing(70);
        layout.setBreadthSpacing(70);
        layout.setOrientation(orientation);

        animate.add(new QualityControlAnimator());
        animate.add(new LocationAnimator(nodes));
        animate.add(layout);
        animate.add(fill);
        animate.add(new RepaintAction());

        // finally, we register our ActionList with the Visualization.
        // we can later execute our Actions by invoking a method on our
        // Visualization, using the name we've chosen below.
        m_vis.putAction("draw", draw);
        m_vis.putAction("layout", animate);

        m_vis.runAfter("draw", "layout");


        // --------------------------------------------------------------------
        // set up a display to show the visualization

        Display display = new Display(m_vis);
        display.setSize(700, 700);
        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // main display controls
        display.addControlListener(new FocusControl());
//        display.addControlListener(new DragControl());
        display.addControlListener(new PanControl());
        display.addControlListener(new ZoomControl());
        display.addControlListener(new WheelZoomControl());
        display.addControlListener(new ZoomToFitControl());
        display.addControlListener(new NeighborHighlightControl());

        display.setForeground(Color.GRAY);
        display.setBackground(Color.WHITE);

        // now we run our action list
        m_vis.run("draw");

        add(display);
    }

    public void setGraph(Graph g) {

        // update graph
//        m_vis.removeGroup(graph);
        VisualGraph vg = m_vis.addGraph(graph, g);
        m_vis.setValue(edges, null, VisualItem.INTERACTIVE, Boolean.TRUE);
        VisualItem f = (VisualItem) vg.getNode(0);
        m_vis.getGroup(Visualization.FOCUS_ITEMS).setTuple(f);
        f.setFixed(true);
    }

    // ------------------------------------------------------------------------
    // Main and demo methods

    public static void main(String[] args) {
        UILib.setPlatformLookAndFeel();

        // create graphview
        String datafile = "/Users/eamonnmaguire/git/eamonnrepo/GraphMacro/data/graphml-test.xml";
        String label = "value";

        JFrame frame = demo(datafile, label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static JFrame demo(String datafile, String label) {
        Graph g = null;

        try {
            g = new GraphMLReader().readGraph(datafile);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return demo(g, label);
    }

    public static JFrame demo(Graph g, String label) {
        final GraphView view = new GraphView(g, label, Constants.ORIENT_TOP_BOTTOM);

        // launch window
        JFrame frame = new JFrame("Workflow Viewer | Graph");
        frame.setContentPane(view);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
                view.m_vis.run("layout");
            }

            public void windowDeactivated(WindowEvent e) {
                view.m_vis.cancel("layout");
            }
        });

        return frame;
    }


    class FitOverviewListener implements ItemBoundsListener {
        private Rectangle2D m_bounds = new Rectangle2D.Double();
        private Rectangle2D m_temp = new Rectangle2D.Double();
        private double m_d = 15;

        public void itemBoundsChanged(Display d) {
            d.getItemBounds(m_temp);
            GraphicsLib.expand(m_temp, 25 / d.getScale());

            double dd = m_d / d.getScale();
            double xd = Math.abs(m_temp.getMinX() - m_bounds.getMinX());
            double yd = Math.abs(m_temp.getMinY() - m_bounds.getMinY());
            double wd = Math.abs(m_temp.getWidth() - m_bounds.getWidth());
            double hd = Math.abs(m_temp.getHeight() - m_bounds.getHeight());
            if (xd > dd || yd > dd || wd > dd || hd > dd) {
                m_bounds.setFrame(m_temp);
                DisplayLib.fitViewToBounds(d, m_bounds, 0);
            }
        }
    }
}
