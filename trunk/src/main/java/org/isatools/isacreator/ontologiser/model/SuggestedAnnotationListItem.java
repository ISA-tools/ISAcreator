package org.isatools.isacreator.ontologiser.model;

import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 01/02/2011
 *         Time: 11:22
 */
public class SuggestedAnnotationListItem implements OntologiserListItems {

    private OntologisedResult mappedTo;
    private AnnotatorResult annotatorResult;

    public SuggestedAnnotationListItem(AnnotatorResult annotatorResult) {
        this.annotatorResult = annotatorResult;
    }

    public AnnotatorResult getAnnotatorResult() {
        return annotatorResult;
    }

    public boolean displayAsChecked() {
        return mappedTo != null;
    }

    public void setMappedTo(OntologisedResult mappedTo) {
        this.mappedTo = mappedTo;
    }

    @Override
    public String toString() {
        return annotatorResult.getOntologyTerm().getOntologyTermName() + " (" + annotatorResult.getOntologySource().getOntologyDisplayLabel() + ")";
    }
}
