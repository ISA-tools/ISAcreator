package org.isatools.isacreator.visualization.workflowvisualization.graph;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/10/2011
 *         Time: 10:06
 */
public class Node {

    private int index;
    private String name;

    public Node(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

}
