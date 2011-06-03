package org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers;

import bioontology.bioportal.annotator.schema.AnnotationBeanDocument;
import bioontology.bioportal.annotator.schema.ConceptDocument;
import bioontology.bioportal.annotator.schema.OntologyUsedBeanDocument;
import bioontology.bioportal.annotator.schema.SuccessDocument;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalOntology;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 18:39
 */
public class BioPortalAnnotatorResultHandler {

    public SuccessDocument getDocument(String fileLocation) {
        SuccessDocument resultDocument = null;
        try {
            resultDocument = SuccessDocument.Factory.parse(new File(fileLocation));
        } catch (org.apache.xmlbeans.XmlException e) {
            System.err.println("XML Exception encountered");
        } catch (java.io.IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
        }

        return resultDocument;
    }

    public Map<String, Map<String, AnnotatorResult>> getSearchResults(String fileLocation) {
        SuccessDocument resultDocument = getDocument(fileLocation);

        Map<String, Ontology> ontologies = getOntologyInformation(resultDocument);

        for (String ontology : ontologies.keySet()) {
            System.out.println(ontology);
        }

        // map from search term to a map of full id to the ontology term.
        Map<String, Map<String, AnnotatorResult>> result = new HashMap<String, Map<String, AnnotatorResult>>();
        // for each token, we wan to find the matches and add them to the list

        String originalTextToAnnotate = extractOriginalSearchText(resultDocument);

        if (resultDocument != null) {
            if (resultDocument.getSuccess().getData().getAnnotatorResultBean().getAnnotations() != null) {
                AnnotationBeanDocument.AnnotationBean[] searchResults = resultDocument.getSuccess().getData().getAnnotatorResultBean().getAnnotations()
                        .getAnnotationBeanArray();

                for (AnnotationBeanDocument.AnnotationBean annotationBean : searchResults) {
                    AnnotatorResult annotatorResult = extractAnnotatorResult(annotationBean, ontologies);

                    if (annotatorResult != null) {

                        System.out.println("Original test to annotate: " + originalTextToAnnotate);

                        String originalTerm = originalTextToAnnotate.substring(annotatorResult.getStartIndex() - 1, annotatorResult.getEndIndex());

                        if (!result.containsKey(originalTerm)) {
                            result.put(originalTerm, new HashMap<String, AnnotatorResult>());
                        }

                        String ontologyId = annotatorResult.getOntologySource().getOntologyID();

                        if (!result.get(originalTerm).containsKey(ontologyId)) {
                            result.get(originalTerm).put(ontologyId, annotatorResult);
                        }
                    }
                }
            }
        }

        return result;
    }

    private String extractOriginalSearchText(SuccessDocument resultDocument) {

        return resultDocument.getSuccess().getData().getAnnotatorResultBean().getParameters().getTextToAnnotate();
    }

    private AnnotatorResult extractAnnotatorResult(AnnotationBeanDocument.AnnotationBean annotation, Map<String, Ontology> ontologies) {
        ConceptDocument.Concept concept = annotation.getConcept();

        if (ontologies.containsKey(concept.getLocalOntologyId().toString())) {
            BioPortalOntology ontologyTerm = new BioPortalOntology();

            ontologyTerm.setOntologySourceAccession(concept.getLocalConceptId());
            ontologyTerm.setOntologyPurl(concept.getFullId());
            ontologyTerm.setOntologyTermName(concept.getPreferredName());
            ontologyTerm.setOntologyVersionId(concept.getLocalOntologyId().toString());

            Ontology ontologySource = ontologies.get(concept.getLocalOntologyId().toString());

            return new AnnotatorResult(ontologyTerm, ontologySource, new Integer(annotation.getScore().toString()),
                    new Integer(annotation.getContext().getFrom().toString()), new Integer(annotation.getContext().getTo().toString()));
        }

        return null;
    }

    private Map<String, Ontology> getOntologyInformation(SuccessDocument document) {
        Map<String, Ontology> ontologies = new HashMap<String, Ontology>();

        if (document != null) {
            for (OntologyUsedBeanDocument.OntologyUsedBean ontology : document.getSuccess().getData().getAnnotatorResultBean().getOntologies().getOntologyUsedBeanArray()) {
                Ontology newOntology = new Ontology();
                newOntology.setOntologyDisplayLabel(ontology.getName());
                newOntology.setOntologyID(ontology.getVirtualOntologyId().toString());
                newOntology.setOntologyVersion(ontology.getLocalOntologyId().toString());


                for (AcceptedOntologies accceptedOntology : AcceptedOntologies.values()) {
                    if (accceptedOntology.toString().equals(newOntology.getOntologyID())) {
                        ontologies.put(ontology.getLocalOntologyId().toString(), newOntology);
                        break;
                    }
                }

            }
        }

        return ontologies;
    }


}
