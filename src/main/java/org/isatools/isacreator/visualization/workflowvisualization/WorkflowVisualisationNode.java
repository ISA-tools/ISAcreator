package org.isatools.isacreator.visualization.workflowvisualization;

/**
 * Holds information about the node in focus for rendering in the NodeDetail view.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/06/2012
 *         Time: 07:38
 */
public class WorkflowVisualisationNode {

    private String imageFile, type, value, taxonomyHierarchy;

    public WorkflowVisualisationNode(String imageFile, String type, String value, String taxonomyHierarchy) {
        this.imageFile = imageFile;
        this.type = type;
        this.value = value;
        this.taxonomyHierarchy = taxonomyHierarchy;
    }

    public String getImageFile() {
        return imageFile;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String getTaxonomyHierarchy() {
        return taxonomyHierarchy;
    }
}
