package org.isatools.isacreator.visualization.workflowvisualisation.graph;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/10/2011
 *         Time: 10:13
 */
public class ProcessNode extends Node {

    private Node inputNode, outputNode;

    public ProcessNode(int index, String name) {
        super(index, name);
    }

    public ProcessNode(int index, String name, Node inputNode, Node outputNode) {
        super(index, name);

        this.inputNode = inputNode;
        this.outputNode = outputNode;
    }

    public void addInputNode(Node inputNode) {
        this.inputNode = inputNode;
    }

    public void addOutputNode(Node outputNode) {
        this.outputNode = outputNode;
    }

    public Node getInputNode() {
        return inputNode;
    }

    public Node getOutputNode() {
        return outputNode;
    }
}
