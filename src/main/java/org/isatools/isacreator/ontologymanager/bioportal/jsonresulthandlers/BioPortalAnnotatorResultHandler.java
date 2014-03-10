package org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class BioPortalAnnotatorResultHandler {

    BioPortalSearchResultHandler searchHandler;

    public BioPortalAnnotatorResultHandler() {
        this(new BioPortalSearchResultHandler());
    }

    public BioPortalAnnotatorResultHandler(BioPortalSearchResultHandler searchHandler) {
        this.searchHandler = searchHandler;
    }

    public Map<String, Map<String, AnnotatorResult>> getSearchResults(String queryContents, String originalText, Set<String> originalTerms) {

        // map from search term to a map of full id to the ontology term.
        Map<String, Map<String, AnnotatorResult>> result = new HashMap<String, Map<String, AnnotatorResult>>();
        // for each token, we wan to find the matches and add them to the list

        JSONArray obj = (JSONArray) JSONValue.parse(queryContents);

        for (Object annotationItem : obj) {

            AnnotatorResult annotatorResult = extractAnnotatorResult((JSONObject)annotationItem);

            if (annotatorResult != null) {

                String originalTerm = originalText.substring(annotatorResult.getStartIndex() - 1, annotatorResult.getEndIndex());
                if (originalTerms.contains(originalTerm)) {

                    if (!result.containsKey(originalTerm)) {
                        result.put(originalTerm, new HashMap<String, AnnotatorResult>());
                    }

                    String ontologySource = annotatorResult.getOntologySource().getOntologyAbbreviation();

                    if (!result.get(originalTerm).containsKey(ontologySource)) {
                        result.get(originalTerm).put(ontologySource, annotatorResult);
                    }
                }
            }
        }

        return result;
    }

    private AnnotatorResult extractAnnotatorResult(JSONObject resultItem) {
        JSONObject annotatedClass = (JSONObject)resultItem.get("annotatedClass");
        JSONObject links = (JSONObject) annotatedClass.get("links");

        String ontologyId = links.get("ontology").toString();

        OntologySourceRefObject sourceRefObject = OntologyManager.getOntologySourceReferenceObjectByAbbreviation(ontologyId);
        OntologyTerm ontologyTerm = new OntologyTerm(
                annotatedClass.get("prefLabel").toString(), annotatedClass.get("@id").toString(), annotatedClass.get("@id").toString(), sourceRefObject);

        int from = -1, to = -1;

        for (Object annotation : (JSONArray)resultItem.get("annotations")) {
            JSONObject annotationObject = (JSONObject) annotation;
            from = Integer.valueOf(annotationObject.get("from").toString());
            to = Integer.valueOf(annotationObject.get("to").toString());
        }

        if (from != -1 && to != -1) {
            return new AnnotatorResult(ontologyTerm, AcceptedOntologies.getAcceptedOntologies().get(ontologyId), 1,
                    from, to);
        }

        return null;
    }
}
