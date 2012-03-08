package org.isatools.isacreator.visualization.workflowvisualization.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2012
 *         Time: 14:37
 */
public class TreeMLGraphOutput implements GraphOutput {

    private PrintStream printStream = null;

    private String lastOutput = "";

    public void renderGraph(Graph graph, File fileName) {

        try {
            printStream = new PrintStream(new FileOutputStream(fileName));
            printStream.println("<tree>");
            printStream.println(getDeclaration());
            doRendering(graph);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getDeclaration() {
        return "<declarations>\n" +
                "   <attributeDecl name=\"type\" type=\"String\"/>\n" +
                "   <attributeDecl name=\"name\" type=\"String\"/>\n" +
                " </declarations>";
    }

    private void doRendering(Graph graph) {
        renderNode(0, graph.findInstancesOfNode(NodeType.PROCESS_NODE));
        closeTree();
    }

    private void renderNode(int count, List<Node> nodes) {

        boolean inputAdded = false;
        if (count < nodes.size()) {

            Node n = nodes.get(count);
            if (n instanceof ProcessNode) {
                ProcessNode node = (ProcessNode) n;
                if (node.getInputNode() != null) {
                    for (Node inputNode : node.getInputNode()) {
                        if (!inputNode.getName().equals(lastOutput)) {
                            inputAdded = true;
                            addTo(inputNode.getName(), "Material");
                        }
                    }
                }

                addTo(node.getName(), "Protocol");

                if (node.getOutputNode() != null) {
                    for (Node outputNode : node.getOutputNode()) {
                        addTo(outputNode.getName(), "Material");
                        lastOutput = outputNode.getName();
                    }
                }
                renderNode(count + 1, nodes);
                closeBranch();
            }
            closeBranch();
        }
        if (inputAdded) closeBranch();

    }

    public void addTo(String value, String type) {
        String classificationInfo = "<branch>" +
                "<attribute name = \"type\" value = \"" + type + "\"/>" +
                "<attribute name=\"name\" value= \"" + value + "\"/>\n";
        printStream.println(classificationInfo);
    }

    public void closeBranch() {
        printStream.println("</branch>");
    }

    public void closeTree() {
        printStream.println("</tree>");
        if (printStream != null) {
            printStream.close();
        }
    }

}
