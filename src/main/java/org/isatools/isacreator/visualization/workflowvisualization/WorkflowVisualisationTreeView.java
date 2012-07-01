package org.isatools.isacreator.visualization.workflowvisualization;

import org.isatools.isacreator.visualization.TreeView;
import prefuse.Constants;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.data.Tree;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2012
 *         Time: 23:30
 */
public class WorkflowVisualisationTreeView extends TreeView {
    public WorkflowVisualisationTreeView(Tree t, Dimension size) {
        super(t, size);
    }

    public WorkflowVisualisationTreeView(Tree t, Dimension size, String label, int orientation) {
        super(t, size, label, orientation);
    }

    @Override
    public void initialiseTreeView(Tree t, Dimension size, String label, int orientation) {
        this.m_orientation = orientation;

        m_vis.add(tree, t);

        m_nodeRenderer = new LabelRenderer(null, "image");

        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);
        m_edgeRenderer.setDefaultLineWidth(4);

        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        m_vis.setRendererFactory(rf);

        int[] colorPalette = new int[]{ColorLib.rgb(51, 51, 51), ColorLib.rgb(51, 51, 51), ColorLib.rgb(51, 51, 51)};

        DataColorAction dataColorAction = new DataColorAction("tree.nodes", "type", Constants.NOMINAL, VisualItem.TEXTCOLOR, colorPalette);

        // colors
        ItemAction nodeColor = new WorkflowNodeColorAction(treeNodes);

        m_vis.putAction("textColor", dataColorAction);

        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgba(51, 51, 51, 100));

        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        m_vis.putAction("fullPaint", fullPaint);

        // create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
                m_orientation, 80, 70, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(size.width / 2, 15));
        m_vis.putAction("treeLayout", treeLayout);

        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree,
                m_orientation);
        m_vis.putAction("subLayout", subLayout);

        createAndAddFilter(dataColorAction, nodeColor, edgeColor, treeLayout, subLayout);
        // animated transition
        createAnimation();
        // ensure size is reasonable!
        finaliseVisualizationSteps(size);
    }

    class WorkflowNodeColorAction extends ColorAction {
        public WorkflowNodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        public int getColor(VisualItem item) {
            if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                return ColorLib.rgba(153, 153, 153, 100);
            } else {
                return ColorLib.rgba(255, 255, 255, 0);
            }
        }
    }
}
