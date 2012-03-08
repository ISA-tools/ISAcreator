package org.isatools.isacreator.visualization.workflowvisualization.graph;

import java.io.File;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2012
 *         Time: 14:36
 */
public interface GraphOutput {

    public void renderGraph(Graph graph, File fileName);
}
