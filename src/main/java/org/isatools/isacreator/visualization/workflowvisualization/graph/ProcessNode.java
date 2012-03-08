package org.isatools.isacreator.visualization.workflowvisualization.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/10/2011
 *         Time: 10:13
 */
public class ProcessNode extends Node {

    private List<Node> inputNodes = new ArrayList<Node>();
    private List<Node> outputNodes = new ArrayList<Node>();

    public ProcessNode(int index, String name) {
        super(index, name);
    }

    public ProcessNode(int index, String name, Node inputNode, Node outputNode) {
        super(index, name);
        inputNodes.add(inputNode);
        outputNodes.add(outputNode);
    }

    public void addInputNode(Node inputNode) {
        inputNodes.add(inputNode);
    }

    public void addOutputNode(Node outputNode) {
        outputNodes.add(outputNode);
    }

    public List<Node> getInputNode() {
        return inputNodes;
    }

    public List<Node> getOutputNode() {
        return outputNodes;
    }
}
