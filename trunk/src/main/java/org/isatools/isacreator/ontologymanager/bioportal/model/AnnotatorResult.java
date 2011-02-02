package org.isatools.isacreator.ontologymanager.bioportal.model;

import org.isatools.isacreator.configuration.Ontology;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 27/01/2011
 *         Time: 15:24
 */
public class AnnotatorResult {

    private BioPortalOntology ontologyTerm;
    private Ontology OntologySource;

    private int score;
    private int startIndex;
    private int endIndex;

    private ScoringConfidence confidenceLevel = ScoringConfidence.MEDIUM;

    public AnnotatorResult(BioPortalOntology ontologyTerm, Ontology ontologySource, int score, int startIndex, int endIndex) {
        this.ontologyTerm = ontologyTerm;
        OntologySource = ontologySource;
        this.score = score;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public BioPortalOntology getOntologyTerm() {
        return ontologyTerm;
    }

    public Ontology getOntologySource() {
        return OntologySource;
    }

    public int getScore() {
        return score;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    // TODO Properly calculate the score confidence
    public ScoringConfidence getScoringConfidenceLevel() {
        return confidenceLevel;
    }

    public void setScoringConfidence(ScoringConfidence confidence) {
        this.confidenceLevel = confidence;
    }

    @Override
    public String toString() {
        return ontologyTerm.getOntologyTermName() + " (" + ontologyTerm.getOntologySourceAccession() + ")";
    }
}
