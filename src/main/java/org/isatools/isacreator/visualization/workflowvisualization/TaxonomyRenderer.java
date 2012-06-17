package org.isatools.isacreator.visualization.workflowvisualization;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/06/2012
 *         Time: 08:02
 */
public class TaxonomyRenderer extends JPanel {

    private Color greenColor = new Color(129, 163, 43);
    private Color greyColor = new Color(209, 211, 212);

    private OrderedMap<String, Integer> taxonomyToRender;

    public void setTaxonomyToRender(String taxonomyToRender) {
        this.taxonomyToRender = processTaxonomyString(taxonomyToRender);
    }

    private void renderTaxonomy(Graphics graphics) {
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);

        double y = 20;

        double diameter = 10;
        int xSpace = 16;
        int ySpace = 40;

        double previousMaxNodeReach = 0;

        if (taxonomyToRender != null) {

            g2d.setFont(UIHelper.VER_8_BOLD);

            double midPoint = calculateMidPoint(xSpace, diameter);

            int count = 0;
            for (String key : taxonomyToRender.keySet()) {

                double distanceFromMidPoint = ((double) taxonomyToRender.get(key) / 2) * ((diameter + xSpace) / 2);
                double x = (midPoint - distanceFromMidPoint);

                int highlightNode = calculateNodeToHighlight(key, taxonomyToRender.get(key));

                double nodeReach = midPoint + distanceFromMidPoint;

                if (previousMaxNodeReach != 0 && previousMaxNodeReach > nodeReach) {
                    // we have a change to make in the starting index.
                    x += previousMaxNodeReach - nodeReach;
                    nodeReach += previousMaxNodeReach - nodeReach;
                }

                previousMaxNodeReach = nodeReach;

                Point startLinePoint = new Point();
                Point endLinePoint = new Point();

                for (int nodeCount = 0; nodeCount < taxonomyToRender.get(key); nodeCount++) {

                    drawLinkingNodeLine(g2d, y, diameter, ySpace, count, x, highlightNode, nodeCount);
                    drawVerticalNodeConnectionLine(g2d, y, diameter, ySpace, count, x);


                    g2d.setStroke(new BasicStroke(4f));
                    g2d.setColor(nodeCount == highlightNode ? greenColor : greyColor);
                    g2d.draw(new Ellipse2D.Double(x, y, diameter, diameter));

                    if (count == taxonomyToRender.size() - 1 && nodeCount == highlightNode) {
                        g2d.fillOval((int) x, (int) y, (int) diameter, (int) diameter);
                    }

                    if (nodeCount == highlightNode) {
                        g2d.setColor(greenColor);
                        g2d.drawString(key, (int) x, (int) (y + (ySpace / 2) + 2));
                    }

                    if (nodeCount == 0) {
                        // first node
                        startLinePoint = new Point((int) (x + (diameter / 2)), (int) (y - (ySpace / 2) + (diameter / 2)));
                    } else if (nodeCount == taxonomyToRender.get(key) - 1) {
                        endLinePoint = new Point((int) (x + (diameter / 2)), (int) (y - (ySpace / 2) + (diameter / 2)));
                    }

                    x += xSpace;
                }
                drawHorizontalLine(g2d, count, startLinePoint, endLinePoint);


                y += ySpace;

                count++;
            }
        }
    }

    private void drawHorizontalLine(Graphics2D g2d, int count, Point startLinePoint, Point endLinePoint) {
        // draw horizontal line
        if (count != 0) {
            g2d.setStroke(new BasicStroke(2f));
            g2d.setColor(greyColor);
            g2d.drawLine(startLinePoint.x, startLinePoint.y, endLinePoint.x, endLinePoint.y);
        }
    }

    private void drawVerticalNodeConnectionLine(Graphics2D g2d, double y, double diameter, int ySpace, int count, double x) {
        // draw line up from lower nodes.
        if (count != 0) {
            g2d.setStroke(new BasicStroke(2f));
            g2d.setColor(greyColor);
            g2d.drawLine((int) (x + (diameter / 2)), (int) (y),
                    (int) (x + (diameter / 2)), (int) (y - (ySpace / 2) + (diameter / 2)));
        }
    }

    private void drawLinkingNodeLine(Graphics2D g2d, double y, double diameter, int ySpace, int count, double x, int highlightNode, int nodeCount) {
        // draw line down from linking node
        if (nodeCount == highlightNode && count != taxonomyToRender.size() - 1) {
            // draw line down from node.
            g2d.setStroke(new BasicStroke(2f));
            g2d.setColor(greyColor);
            g2d.drawLine((int) (x + (diameter / 2)), (int) (y + diameter),
                    (int) (x + (diameter / 2)), (int) (y + (ySpace / 2) + (diameter / 2)));
        }
    }

    private int calculateNodeToHighlight(String field, int totalNodeCount) {
        int hash = UIHelper.hashString(field);
        hash = hash < 0 ? hash * -1 : hash;
        return hash % (totalNodeCount);
    }

    private double calculateMidPoint(double xSpace, double diameter) {
        int maxWidth = getMaxWidth();

        return ((diameter + xSpace) * maxWidth) / 2;
    }

    private int getMaxWidth() {
        int maxNodeLength = Integer.MIN_VALUE;

        for (String key : taxonomyToRender.keySet()) {
            if (taxonomyToRender.get(key) > maxNodeLength) {
                maxNodeLength = taxonomyToRender.get(key);
            }
        }
        return maxNodeLength;
    }

    @Override
    public void paint(Graphics graphics) {
        super.paint(graphics);
        renderTaxonomy(graphics);
    }

    private OrderedMap<String, Integer> processTaxonomyString(String taxonomyAsString) {
        //"protocol(2):in vivo(3):material amplification(5):organism(7)"
        String[] fragments = taxonomyAsString.split(":");
        taxonomyToRender = new ListOrderedMap<String, Integer>();
        for (String fragment : fragments) {
            String classification = fragment.substring(0, fragment.indexOf("("));
            taxonomyToRender.put(classification, Integer.valueOf(fragment.replaceAll(classification, "").replaceAll("\\(|\\)", "")));
        }


        return taxonomyToRender;
    }
}
