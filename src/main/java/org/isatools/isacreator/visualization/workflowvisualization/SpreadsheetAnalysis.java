package org.isatools.isacreator.visualization.workflowvisualization;

import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.sampleselection.SampleInformation;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.visualization.workflowvisualization.graph.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/10/2011
 *         Time: 11:19
 */
public abstract class SpreadsheetAnalysis {


    protected Graph graph;
    private String[][] spreadsheetContents;

    private Map<String, SampleInformation> groups;
    private Spreadsheet spreadsheet;

    public SpreadsheetAnalysis() {
    }

    public SpreadsheetAnalysis(Spreadsheet spreadsheet) {
        this.spreadsheet = spreadsheet;
        groups = new HashMap<String, SampleInformation>();
    }

    public void runAnalysis() {
        extractGroups();
        createGraph(SpreadsheetUtils.getColumnNamesAsArray(spreadsheet, new HashSet<String>()));
        setSpreadsheetContents(SpreadsheetUtils.getSpreadsheetDataSubset(spreadsheet));
    }

    public void setSpreadsheetContents(String[][] spreadsheetContents) {
        this.spreadsheetContents = spreadsheetContents;
    }

    public abstract void createGraph(String[] columnNames);

    protected void processCharacteristic(int index, Node lastMaterialOrData, String column) {
        Node materialProperty = new MaterialProperty(index, column);
        if (lastMaterialOrData != null && lastMaterialOrData instanceof MaterialNode) {
            ((MaterialNode) graph.getNode(lastMaterialOrData.getIndex())).addMaterialProperty(materialProperty);
        }
    }

    protected ProcessNode processProtocolColumn(int index, Node lastMaterialOrData, String column) {
        ProcessNode lastProcess;
        ProcessNode processNode = new ProcessNode(index, column);
        graph.addNode(processNode);
        if (lastMaterialOrData != null) {
            processNode.addInputNode(
                    new MaterialNode(lastMaterialOrData.getIndex(), lastMaterialOrData.getName()));
        }
        lastProcess = processNode;
        return lastProcess;
    }

    /**
     * @return Returns a Map of the processes present, and a count of how many times that process was used.
     */
    public Map<String, Integer> extractProcesses() {
        return extractNodes(NodeType.PROCESS_NODE);
    }

    /**
     * @return Returns a Map of the data files present, and a count of how many times that data was used.
     */
    public Map<String, Integer> extractData() {
        return extractNodes(NodeType.DATA_NODE);
    }

    public Map<String, Map<String, Integer>> extractMaterials() {
        Map<String, Map<String, Integer>> resultNodes
                = new HashMap<String, Map<String, Integer>>();

        List<Node> node = graph.findInstancesOfNode(NodeType.MATERIAL_NODE);

        for (Node nodeOfInterest : node) {
            // extract the values!
            MaterialNode materialNode = (MaterialNode) nodeOfInterest;

            for (Node property : materialNode.getMaterialProperties()) {

                // go through each row, pull out values and properties
                for (String[] spreadsheetContent : spreadsheetContents) {

                    if (property.getIndex() < spreadsheetContent.length) {
                        String value = spreadsheetContent[property.getIndex()];
                        if (value != null && !value.equals("")) {

                            if (!resultNodes.containsKey(property.getName())) {
                                resultNodes.put(property.getName(), new HashMap<String, Integer>());
                            }

                            if (!resultNodes.get(property.getName()).containsKey(value)) {
                                resultNodes.get(property.getName()).put(value, 1);
                            } else {
                                int newCount = resultNodes.get(property.getName()).get(value) + 1;
                                resultNodes.get(property.getName()).put(value, newCount);
                            }
                        }
                    }
                }
            }
        }
        return resultNodes;
    }


    private Map<String, Integer> extractNodes(NodeType type) {
        Map<String, Integer> resultNodes = new HashMap<String, Integer>();

        List<Node> node = graph.findInstancesOfNode(type);

        for (Node nodeOfInterest : node) {
            // extract the values!
            for (String[] spreadsheetContent : spreadsheetContents) {
                if (nodeOfInterest.getIndex() < spreadsheetContent.length) {
                    String value = spreadsheetContent[nodeOfInterest.getIndex()];
                    if (value != null && !value.equals("")) {
                        if (!resultNodes.containsKey(value)) {
                            resultNodes.put(value, 1);
                        } else {
                            int newCount = resultNodes.get(value) + 1;
                            resultNodes.put(value, newCount);
                        }
                    }
                }
            }
        }
        return resultNodes;
    }


    public Map<String, SampleInformation> extractGroups() {
        return groups = SpreadsheetUtils.getGroupInformation("Factor", spreadsheet);
    }

    public Map<String, SampleInformation> getGroups() {
        return groups;
    }

    public Graph getGraph() {
        return graph;
    }


}
