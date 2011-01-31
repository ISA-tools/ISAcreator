package org.isatools.isacreator.ontologiser.model;

import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 31/01/2011
 *         Time: 16:30
 */
public class OntologisedResult {

    private String freeTextTerm;
    private AnnotatorResult assignedOntology = null;

    public OntologisedResult(String freeTextTerm) {
        this.freeTextTerm = freeTextTerm;
    }

    public String getFreeTextTerm() {
        return freeTextTerm;
    }

    public AnnotatorResult getAssignedOntology() {
        return assignedOntology;
    }

    public void setAssignedOntology(AnnotatorResult assignedOntology) {
        this.assignedOntology = assignedOntology;
    }
}
