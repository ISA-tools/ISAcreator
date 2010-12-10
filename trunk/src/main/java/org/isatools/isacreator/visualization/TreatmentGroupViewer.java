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

import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.Utils;
import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.Layout;
import prefuse.action.layout.graph.ForceDirectedLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.Schema;
import prefuse.data.Tuple;
import prefuse.data.expression.AbstractPredicate;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.ShapeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.util.GraphicsLib;
import prefuse.util.PrefuseLib;
import prefuse.util.display.DisplayLib;
import prefuse.util.force.DragForce;
import prefuse.util.force.NBodyForce;
import prefuse.util.force.SpringForce;
import prefuse.visual.AggregateItem;
import prefuse.visual.DecoratorItem;
import prefuse.visual.NodeItem;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import profusians.util.force.ForceSimulatorRemovableForces;
import profusians.zonemanager.ZoneManager;
import profusians.zonemanager.action.ZoneGuardAction;
import profusians.zonemanager.zone.Zone;

import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.List;

public class TreatmentGroupViewer extends Display {
    private static final Logger log = Logger.getLogger(TreatmentGroupViewer.class.getName());

    public static final int RENDER_TEXT = 0;
    public static final int RENDER_CIRCLE = 1;

    public static final int NODE_PERFORMANCE_THRESHOLD = 200;

    public static final String GRAPH = "graph";
    public static final String NODES = "graph.nodes";
    public static final String EDGES = "graph.edges";

    private static final Schema DECORATOR_SCHEMA = PrefuseLib
            .getVisualItemSchema();

    public static final String ZONEAGGR_DECORATORS = "zoneAggrDeco";
    DefaultRendererFactory drf = new DefaultRendererFactory();
    LabelRenderer textRenderer;
    ShapeRenderer circleRenderer;
    ZoneManager zoneManager;
    ActionList colors;
    ColorAction nStroke;
    ColorAction nFill;

    private Timer supervisor;

    private Map<String, List<Object>> groups;
    private int totalNodes;

    public TreatmentGroupViewer(Map<String, List<Object>> groups, int totalNodes, int width, int height) {
        // initialize display and data
        super(new Visualization());

        this.groups = groups;
        this.totalNodes = totalNodes;
        initDataGroups(totalNodes);
        // todo uncomment after testing coords.
        createZones(groups);
        initZoneManager(groups);

        DECORATOR_SCHEMA.setDefault(VisualItem.TEXTCOLOR, ColorLib.gray(50));
        DECORATOR_SCHEMA.setDefault(VisualItem.FONT, FontLib.getFont("Verdana",
                Font.BOLD, 14));
        m_vis.addDecorators(ZONEAGGR_DECORATORS, ZoneManager.ZONEAGGREGATES,
                new NonEmptyZonePredicate(), DECORATOR_SCHEMA);

        circleRenderer = new ShapeRenderer(15);

        textRenderer = new LabelRenderer("label");

        drf.setDefaultRenderer(circleRenderer);

        drf.add(new InGroupPredicate(ZONEAGGR_DECORATORS),
                new MyDecoratorLabelRenderer());


        zoneManager.addZoneRenderer(drf, ZoneManager.CONVEXHULLZONERENDERER);

        m_vis.setRendererFactory(drf);

        ColorAction nEdges = new ColorAction(EDGES, VisualItem.STROKECOLOR);
        nEdges.setDefaultColor(ColorLib.gray(50));

        // set up the visual operators
        // first set up all the color actions
        nStroke = new ColorAction(NODES, VisualItem.STROKECOLOR);
        nStroke.setDefaultColor(ColorLib.gray(100));
        nStroke.add("_hover", ColorLib.gray(50));

        nFill = new ColorAction(NODES, VisualItem.FILLCOLOR);
        nFill.setDefaultColor(ColorLib.gray(255));
        nFill.add("_hover", ColorLib.gray(200));
        zoneManager.addZoneItemColorMapping(nFill);

        ColorAction nodeText = new ColorAction(NODES, VisualItem.TEXTCOLOR);
        nodeText.setDefaultColor(ColorLib.gray(50));

        ColorAction aFill = zoneManager.getZoneColorAction();

        // bundle the color actions
        colors = new ActionList();
        colors.add(nodeText);
        colors.add(nFill);
        colors.add(nStroke);
        colors.add(aFill);
        colors.add(nEdges);

        ForceDirectedLayout fdlZone = new ForceDirectedLayout(GRAPH,
                zoneManager.getForceSimulator(), false);

        int duration = 1000;

        m_vis.putAction("colorThings", colors);

        ActionList catchThem = new ActionList(duration);
        catchThem.setPacingFunction(new SlowInSlowOutPacer());
        catchThem.add(colors);
        catchThem.add(new ZoneGuardAction(zoneManager));
        catchThem.add(zoneManager
                .getZoneLayout(ZoneManager.CONVEXHULLZONERENDERER));
        catchThem.add(fdlZone);
        catchThem.add(new ColorAnimator(NODES));
        catchThem.add(new LocationAnimator(NODES));
        catchThem.add(new ZoneLabelLayout(ZONEAGGR_DECORATORS));
        catchThem.add(new RepaintAction());

        m_vis.putAction("catchThem", catchThem);

        // action list which catches nodes out of place, but which allows free location of the label fields!
        ActionList lightCatchThem = new ActionList(duration);
        lightCatchThem.setPacingFunction(new SlowInSlowOutPacer());
        lightCatchThem.add(colors);
        lightCatchThem.add(new ZoneGuardAction(zoneManager));
        lightCatchThem.add(zoneManager
                .getZoneLayout(ZoneManager.CONVEXHULLZONERENDERER));
        lightCatchThem.add(new ColorAnimator(NODES));
        lightCatchThem.add(new RepaintAction());

        m_vis.putAction("lightCatchThem", lightCatchThem);

        ActionList keepThem = new ActionList(duration);
        keepThem.add(new ZoneGuardAction(zoneManager));
        keepThem.add(fdlZone);
        keepThem.add(zoneManager
                .getZoneLayout(ZoneManager.CONVEXHULLZONERENDERER));
        keepThem.add(new RepaintAction());

        m_vis.putAction("keepThem", keepThem);

        ActionList lightKeepThem = new ActionList(duration);
        lightKeepThem.add(zoneManager
                .getZoneLayout(ZoneManager.CONVEXHULLZONERENDERER));
        lightKeepThem.add(new RepaintAction());

        m_vis.putAction("lightKeepThem", lightKeepThem);
        m_vis.alwaysRunAfter("catchThem", "keepThem");

        // in order to maintain a good performance, we reduce load on the CPU by performing a smaller action list
        // in lightKeepThem. This stops cpu intensive tasks such as the force simulator from running.
        if (totalNodes > NODE_PERFORMANCE_THRESHOLD) {
            m_vis.alwaysRunAfter("lightCatchThem", "lightKeepThem");
        } else {
            m_vis.alwaysRunAfter("lightCatchThem", "keepThem");
        }


        ActionList zoomToCenter = new ActionList(duration);
        zoomToCenter.add(new ZoomCenterAction());
        m_vis.putAction("zoomToCenter", zoomToCenter);

        setSize(width, height);
        setHighQuality(true);
        addControlListener(new ZoomControl());
        addControlListener(new DragControl());
        addControlListener(new PanControl());
        addControlListener(new ZoomToFitControl());
        addControlListener(new ZoomControl());
        addControlListener(new WheelZoomControl());

        zoneRound();
    }

    public void setRenderer(int option) {
        switch (option) {

            case RENDER_TEXT:
                colors.remove(nFill);
                colors.remove(nStroke);
                drf.setDefaultRenderer(textRenderer);
                m_vis.run("colorThings");
                break;
            case RENDER_CIRCLE:
                colors.add(nFill);
                colors.add(nStroke);
                drf.setDefaultRenderer(circleRenderer);
                m_vis.run("colorThings");
                break;
            default:
                colors.add(nFill);
                colors.add(nStroke);
                drf.setDefaultRenderer(circleRenderer);
                m_vis.run("colorThings");
        }
    }


    private void initZones(Map<String, List<Object>> groups) {
        Iterator nodes = m_vis.getVisualGroup(NODES).tuples();
        Set<String> zoneLabels = groups.keySet();
        String[] zoneArray = zoneLabels.toArray(new String[zoneLabels.size()]);

        int zoneCount = 0;

        String curZone = zoneArray[zoneCount];
        int maxNodeCount = groups.get(curZone).size();
        int curNodeCount = 0;
        zoneManager.setAllZonesVisible(true);

        while (nodes.hasNext()) {
            NodeItem aNodeItem = (NodeItem) nodes.next();

            if (curNodeCount >= maxNodeCount) {
                zoneCount++;
                curZone = zoneArray[zoneCount];
                maxNodeCount = groups.get(curZone).size();
                curNodeCount = 0;
            }

            aNodeItem.setString("label", groups.get(curZone).get(curNodeCount).toString());
            zoneManager.addItemToZone(aNodeItem, curZone);
            curNodeCount++;

        }
    }

    public void zoneRound() {

        m_vis.cancel("catchThem");
        m_vis.cancel("keepThem");
        m_vis.cancel("freeThem");

        zoneManager.setAllZonesVisible(true);

        initZones(groups);

        m_vis.run("catchThem");
        m_vis.run("zoomToCenter");

        if (totalNodes < NODE_PERFORMANCE_THRESHOLD) {
            supervisor = new Timer(3000, new NodeSupervisor());
            supervisor.start();
        }

    }


    class NodeSupervisor implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (TreatmentGroupViewer.this.isShowing()) {
                m_vis.run("lightCatchThem");
            } else {
                log.info("stopping node supervisor");
                supervisor.stop();
            }
        }
    }

    private void initDataGroups(int totalSamples) {
        Graph g = new Graph();
        g.addColumn("label", String.class);
        for (int i = 0; i < totalSamples; i++) {
            g.addNode();
        }

        // add visual data groups
        m_vis.addGraph(GRAPH, g);
        m_vis.setInteractive(EDGES, null, false);
        m_vis.setValue(NODES, null, VisualItem.SHAPE, Constants.SHAPE_ELLIPSE);
    }

    // change to has colour representation.

    private void initZoneManager(Map<String, List<Object>> groups) {

        ForceSimulatorRemovableForces fsim = getForceSimulator();

        zoneManager = new ZoneManager(m_vis, fsim);

        zoneManager.addZonesFromFile("Data" + File.separator + "zones.xml");

        // setting the colors
        HashMap allZones = zoneManager.getZones();

        for (Object o : allZones.values()) {
            Zone aZone = (Zone) o;
            Color c = UIHelper.createColorFromString(aZone.getName(), false);
            aZone.getColors().setItemColor(ColorLib.rgb(c.getRed(), c.getGreen(), c.getBlue()));
            aZone.getColors().setFillColor(ColorLib.rgba(c.getRed(), c.getGreen(), c.getBlue(), 111));
        }

        initZones(groups);
    }


    private ForceSimulatorRemovableForces getForceSimulator() {

        float gravConstant = -0.2f; // -1.0f;
        float minDistance = 50f; // -1.0f;
        float theta = 0.9f; // 0.9f;
        float drag = 0.01f; // 0.01f;
        float springCoeff = 1E-4f; // 1E-4f;

        ForceSimulatorRemovableForces fsim;

        fsim = new ForceSimulatorRemovableForces();

        fsim.addForce(new NBodyForce(gravConstant, minDistance, theta));
        fsim.addForce(new DragForce(drag));
        fsim.addForce(new SpringForce(springCoeff, SpringForce.DEFAULT_SPRING_LENGTH));

        return fsim;
    }

    /**
     * modify to take into consideration area of circles to be included in the group plus a constant value to take into consideration
     * the fact that circles do not pack precusely :D
     *
     * @param groups - groups to be added.
     */
    public void createZones(final Map<String, List<Object>> groups) {

        Utils.createDir("Data");

        File f = new File("Data" + File.separator + "zones.xml");
        PrintStream ps = null;

        float shapeRadius = 7.5f;

        float previousX = 30f;
        float startY = 30f;
        float previousY = 30f;
        float largestRadii = Float.MIN_VALUE;
        float radius;
        int count = 0;
        try {
            ps = new PrintStream(new FileOutputStream(f));
            ps.println("<zonexml>");

            for (String group : groups.keySet()) {

                // determine size of circle depending on number of elements
                int groupSize = groups.get(group).size();

                radius = groupSize < 10 ? (groupSize * shapeRadius) : (groupSize * shapeRadius) * 0.6f;
                largestRadii = radius > largestRadii ? radius : largestRadii;
                if (count == 2) {
                    previousX = (previousX + largestRadii + 200);
                    previousY = startY;
                    // reset largest radii
                    largestRadii = Float.MIN_VALUE;
                    count = 0;
                }

                ps.println("<zone shape=\"circular\" name=\"" + group + "\" shapedata=\"" + (previousX + "," + previousY + "," + radius) + "\"></zone>");
                previousY = (previousY + radius + 150);
                count++;
            }
            ps.println("</zonexml>");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
    }

    class MyDecoratorLabelRenderer extends LabelRenderer {
        public String getText(VisualItem vi) {
            AggregateItem decoratedItem = (AggregateItem) ((DecoratorItem) vi)
                    .getDecoratedItem();
            int zoneNumber = decoratedItem.getInt("zoneNumber");
            Zone aZone = zoneManager.getZone(zoneNumber);
            return vi.getString("zoneName") + " (" + aZone.getNumberOfItems()
                    + " samples)";
        }
    }

    class NonEmptyZonePredicate extends AbstractPredicate {
        public boolean getBoolean(Tuple t) {
            try {
                int zoneNumber = t.getInt("zoneNumber");
                Zone aZone = zoneManager.getZone(zoneNumber);
                return aZone.getNumberOfItems() > 0;
            } catch (Exception e) {
                return false;
            }
        }
    }


    class ZoneLabelLayout extends Layout {
        public ZoneLabelLayout(String group) {
            super(group);
        }

        public void run(double frac) {

            Iterator iter = m_vis.items(m_group);
            while (iter.hasNext()) {
                DecoratorItem decorator = (DecoratorItem) iter.next();
                AggregateItem decoratedItem = (AggregateItem) decorator
                        .getDecoratedItem();

                int zoneNumber = decoratedItem.getInt("zoneNumber");

                Zone aZone = zoneManager.getZone(zoneNumber);

                if (aZone.getNumberOfItems() > 0) {
                    decorator.setVisible(true);

                    decorator.setFillColor(aZone.getColors().getFillColor());
                    Rectangle2D b = decoratedItem.getBounds();
                    double x = b.getCenterX();
                    double y = b.getCenterY();

                    double height = b.getHeight();

                    double textWidth = decorator.getBounds().getWidth();


                    setX(decorator, null, x - textWidth / 5);


                    if (y < 0) {
                        setY(decorator, null, y - height / 2 - 10);
                    } else {
                        setY(decorator, null, y + height / 2 + 10);
                    }
                } else {
                    decorator.setVisible(false);
                }
            }

        }

    }

    private Display getDisplay() {
        return this;
    }

    public class ZoomCenterAction extends Action {

        ZoomCenterAction() {
            super();
        }

        public void run(double frac) {

            if (m_vis == null) {

                return;
            }
            Rectangle2D bounds = m_vis.getBounds(Visualization.ALL_ITEMS);
            GraphicsLib.expand(bounds, 70 + (int) (1 / getDisplay().getScale()));
            DisplayLib.fitViewToBounds(getDisplay(), bounds, 0);
        }
    }


}


