package org.isatools.isacreator.visualization.workflowvisualization.graph;

import org.isatools.isacreator.visualization.workflowvisualization.SpreadsheetAnalysis;
import org.isatools.isacreator.visualization.workflowvisualization.TranscriptomicSpreadsheetAnalysis;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2012
 *         Time: 12:11
 */
public class GraphTest {
    String[] columnNames;
    String[][] rows;

    @Before
    public void setUp() throws Exception {
        columnNames = new String[]{"Sample Name",
                "Protocol REF", "Extract Name", "Protocol REF",
                "Labeled Extract Name", "Label", "Term Source REF",
                "Term Accession Number", "Protocol REF", "Hybridization Assay Name",
                "Array Design REF", "Scan Name", "Array Data File",
                "Normalization Name", "Derived Array Data File", "Factor Value[limiting nutrient]",
                "Term Source REF", "Term Accession Number", "Factor Value[rate]", "Unit",
                "Term Source REF", "Term Accession Number"};

        rows = new String[2][22];
        rows[0] = new String[]{"C-0.07-aliquot1", "mRNA extraction", "C-0.07-aliquot1", "biotin labeling",
                "C-0.07-aliquot1", "biotin", "CHEBI", "15956", "EukGE-WS4", "HYB:MEXP:3908", "A-AFFY-27", "SCAN:MEXP:3908",
                "E-MEXP-115-raw-data-331217737.txt", "GCRMA normalization", "E-MEXP-115-processed-data-1341986893.txt",
                "carbon", "", "", "0.07", "l/hr", "", ""};
        rows[1] = new String[]{"C-0.07-aliquot2", "mRNA extraction", "C-0.07-aliquot2", "biotin labeling",
                "C-0.07-aliquot2", "biotin", "CHEBI", "15956", "EukGE-WS4", "HYB:MEXP:3908", "A-AFFY-27", "SCAN:MEXP:3908",
                "E-MEXP-115-raw-data-331217737.txt", "GCRMA normalization", "E-MEXP-115-processed-data-1341986893.txt",
                "carbon", "", "", "0.07", "l/hr", "", ""};
    }

    @Test
    public void testGraph() {
        SpreadsheetAnalysis analysis = new TranscriptomicSpreadsheetAnalysis();
        analysis.createGraph(columnNames);
        analysis.getGraph().outputGraph();

        TreeMLGraphOutput graphOutput = new TreeMLGraphOutput();
        graphOutput.renderGraph(analysis.getGraph(), new File("ProgramData/myfile.xml"));

        assertEquals("Number of processes is not what I expected", 3, analysis.getGraph().findInstancesOfNode(NodeType.PROCESS_NODE).size());
        assertEquals("Number of materials is not what I expected", 6, analysis.getGraph().findInstancesOfNode(NodeType.MATERIAL_NODE).size());
        assertEquals("Number of data nodes is not what I expected", 2, analysis.getGraph().findInstancesOfNode(NodeType.DATA_NODE).size());
    }

}
