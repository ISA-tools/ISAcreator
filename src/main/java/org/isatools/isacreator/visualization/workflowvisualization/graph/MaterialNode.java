package org.isatools.isacreator.visualization.workflowvisualization.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 */
public class MaterialNode extends Node {

    private List<Node> materialProperties;

    public MaterialNode(int index, String name) {
        super(index, name);

        materialProperties = new ArrayList<Node>();
    }

    public void addMaterialProperty(Node property) {
        materialProperties.add(property);
    }

    public List<Node> getMaterialProperties() {
        return materialProperties;
    }
}
