package org.isatools.isacreator.ontologybrowsingutils;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/05/2011
 *         Time: 17:02
 */
public class OntologyTreeItem {

    private OntologyBranch branch;
    private Ontology ontology;

    public OntologyTreeItem(OntologyBranch branch, Ontology ontology) {
        this.branch = branch;
        this.ontology = ontology;
    }

    public OntologyBranch getBranch() {
        return branch;
    }

    public Ontology getOntology() {
        return ontology;
    }

    public String toString(){
        return branch.toString();
    }
}
