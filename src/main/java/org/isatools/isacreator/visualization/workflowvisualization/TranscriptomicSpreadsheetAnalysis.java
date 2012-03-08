package org.isatools.isacreator.visualization.workflowvisualization;

import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.visualization.workflowvisualization.graph.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2012
 *         Time: 13:50
 */
public class TranscriptomicSpreadsheetAnalysis extends SpreadsheetAnalysis {

    public TranscriptomicSpreadsheetAnalysis() {
    }

    public TranscriptomicSpreadsheetAnalysis(Spreadsheet spreadsheet) {
        super(spreadsheet);
    }

    public void createGraph(String[] columnNames) {
        graph = new Graph();

        int index = 0;
        ProcessNode lastProcess = null;
        Node lastMaterialOrData = null;

        for (String column : columnNames) {
            if (column.equalsIgnoreCase("Protocol REF")) {
                lastProcess = processProtocolColumn(index, lastMaterialOrData, column);
            } else if (column.contains("File") && !column.matches("(Comment.*)")) {
                Node dataNode = new DataNode(index, column);
                graph.addNode(dataNode);
                lastMaterialOrData = dataNode;
                if (lastProcess != null) {
                    lastProcess.addOutputNode(dataNode);
                    lastProcess = null;
                }
            } else if (column.matches("(Characteristic.*)")) {
                processCharacteristic(index, lastMaterialOrData, column);
            } else if (!column.matches("(Factor.*)|(Parameter.*)|(Comment.*)|(Unit.*)|(Term.*)|(Material.*)|(Array\\sDesign.*)|(Date.*)|(Provider.*)")) {
                Node materialNode = new MaterialNode(index, column);
                if (lastProcess != null) {
                    if (column.matches("Label")) {
                        lastProcess.addInputNode(materialNode);
                        lastProcess = null;
                    } else {
                        graph.addNode(materialNode);
                        lastMaterialOrData = materialNode;
                        lastProcess.addOutputNode(materialNode);
                        if (!column.matches("Label.*")) lastProcess = null;
                    }
                } else {
                    graph.addNode(materialNode);
                    lastMaterialOrData = materialNode;
                }
            }
            index++;
        }
    }


}
