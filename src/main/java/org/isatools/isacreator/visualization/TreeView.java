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

package org.isatools.isacreator.visualization;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.*;
import prefuse.data.Tree;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;


public class TreeView extends Display {
    public static final String TREE_CHI = System.getProperty("java.io.tmpdir") + File.separator + "view.xml";
    private static final String tree = "tree";
    private static final String treeNodes = "tree.nodes";
    private static final String treeEdges = "tree.edges";
    private LabelRenderer m_nodeRenderer;
    private EdgeRenderer m_edgeRenderer;
    private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

    public TreeView(Tree t, String label, Dimension size) {
        super(new Visualization());

        m_vis.add(tree, t);

        m_nodeRenderer = new LabelRenderer(label);
        m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
        m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
        m_nodeRenderer.setRoundedCorner(8, 8);
        m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

        DefaultRendererFactory rf = new DefaultRendererFactory(m_nodeRenderer);
        rf.add(new InGroupPredicate(treeEdges), m_edgeRenderer);
        m_vis.setRendererFactory(rf);

        int[] colorPalette = new int[]{
                ColorLib.rgb(51, 51, 51), ColorLib.rgb(51, 51, 51), ColorLib.rgb(141, 198, 63)
        };
        DataColorAction dca = new DataColorAction("tree.nodes", "type", Constants.NOMINAL, VisualItem.TEXTCOLOR, colorPalette);

        // colors
        ItemAction nodeColor = new NodeColorAction(treeNodes);

        m_vis.putAction("textColor", dca);

        ItemAction edgeColor = new ColorAction(treeEdges,
                VisualItem.STROKECOLOR, ColorLib.rgba(153, 153, 153, 100));

        // quick repaint
        ActionList repaint = new ActionList();
        repaint.add(nodeColor);
        repaint.add(new RepaintAction());
        m_vis.putAction("repaint", repaint);

        // full paint
        ActionList fullPaint = new ActionList();
        fullPaint.add(nodeColor);
        m_vis.putAction("fullPaint", fullPaint);

        // animate paint change
        ActionList animatePaint = new ActionList(400);
        animatePaint.add(new ColorAnimator(treeNodes));
        animatePaint.add(new RepaintAction());
        m_vis.putAction("animatePaint", animatePaint);

        // create the tree layout action
        NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(tree,
                m_orientation, 50, 0, 8);
        treeLayout.setLayoutAnchor(new Point2D.Double(25, 75));
        m_vis.putAction("treeLayout", treeLayout);

        CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(tree,
                m_orientation);
        m_vis.putAction("subLayout", subLayout);

        // create the filtering and layout
        ActionList filter = new ActionList();
        filter.add(new FisheyeTreeFilter(tree, 2));
        filter.add(new FontAction(treeNodes, FontLib.getFont("Verdana", 16)));
        filter.add(treeLayout);
        filter.add(subLayout);
        filter.add(dca);
        filter.add(nodeColor);
        filter.add(edgeColor);
        m_vis.putAction("filter", filter);

        // animated transition
        ActionList animate = new ActionList(1000);
        animate.setPacingFunction(new SlowInSlowOutPacer());
        animate.add(new QualityControlAnimator());
        animate.add(new VisibilityAnimator(tree));
        animate.add(new LocationAnimator(treeNodes));
        animate.add(new ColorAnimator(treeNodes));
        animate.add(new RepaintAction());
        m_vis.putAction("animate", animate);
        m_vis.alwaysRunAfter("filter", "animate");

        // ------------------------------------------------

        // initialize the display

        // ensure size is reasonable!
        size.width = (size.width < 100) ? 200 : size.width;
        size.height = (size.height < 100) ? 200 : size.height;

        setSize(size);
        setItemSorter(new TreeDepthItemSorter());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());
        addControlListener(new PanControl());
        addControlListener(new FocusControl(1, "filter"));

        // ------------------------------------------------

        // filter graph and perform layout
        setOrientation(m_orientation);
        m_vis.run("filter");
    }

    public int getOrientation() {
        return m_orientation;
    }

    // ------------------------------------------------------------------------
    public void setOrientation(int orientation) {
        NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) m_vis.getAction(
                "treeLayout");
        CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) m_vis.getAction(
                "subLayout");

        switch (orientation) {
            case Constants.ORIENT_LEFT_RIGHT:
                m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);

                break;

            case Constants.ORIENT_RIGHT_LEFT:
                m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
                m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
                m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
                m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);

                break;

            case Constants.ORIENT_TOP_BOTTOM:
                m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
                m_edgeRenderer.setVerticalAlignment2(Constants.TOP);

                break;

            case Constants.ORIENT_BOTTOM_TOP:
                m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
                m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
                m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
                m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);

                break;

            default:
                throw new IllegalArgumentException(
                        "Unrecognized orientation value: " + orientation);
        }

        m_orientation = orientation;
        rtl.setOrientation(orientation);
        stl.setOrientation(orientation);
    }


    public static class NodeColorAction extends ColorAction {
        public NodeColorAction(String group) {
            super(group, VisualItem.FILLCOLOR);
        }

        public int getColor(VisualItem item) {
            if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS)) {
                return ColorLib.rgba(153, 153, 153, 100);
            } else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS)) {
                return ColorLib.rgba(153, 153, 153, 100);
            } else if (item.getDOI() > -1) {
                return ColorLib.rgb(164, 193, 193);
            } else {
                return ColorLib.rgba(255, 255, 255, 0);
            }
        }
    } // end of inner class TreeMapColorAction

} // end of class TreeMap
