package org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.BioPortal4Client;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.omg.CORBA.OBJECT_NOT_EXIST;

import javax.json.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioPortalQueryEndpoint {

    public static final String API_KEY = "fd88ee35-6995-475d-b15a-85f1b9dd7a42";

    /**
     * Returns the result of the search operation
     *
     * @param term        - the string being searched for
     * @param ontologyIds - the ontologies the search is being restricted to
     * @param @nullable   subtree - a subtree, if any to be searched under (optional)
     * @return - Map from the id of the ontology to the list of terms found under it.
     */
    public Map<String, List<OntologyTerm>> getSearchResults(String term, String ontologyIds, String subtree) {

        // map from ontology id to the list of terms found for that id.
        Map<String, List<OntologyTerm>> result = new HashMap<String, List<OntologyTerm>>();

        String content = querySearchEndpoint(term, ontologyIds, subtree);

        StringReader reader = new StringReader(content);

        JsonReader rdr = Json.createReader(reader);

        JsonObject obj = rdr.readObject();
        JsonArray results = obj.getJsonArray("collection");
        for (JsonObject resultItem : results.getValuesAs(JsonObject.class)) {

            JsonObject links = resultItem.getJsonObject("links");

            String ontologyId = links.getJsonString("ontology").toString();
            if (!result.containsKey(ontologyId)) {
                result.put(ontologyId, new ArrayList<OntologyTerm>());
            }
            OntologyTerm ontologyTerm = new OntologyTerm(resultItem.getString("prefLabel"), resultItem.getString("@id"), "", null);

            JsonArray definitions = resultItem.getJsonArray("definition");
            if (definitions != null) {
                ontologyTerm.addToComments("definition", definitions.get(0).toString());
            }

            JsonArray synonyms = resultItem.getJsonArray("synonyms");
            if (synonyms != null && synonyms.size() > 0) {
                StringBuilder synonymList = new StringBuilder();
                int count = 0;
                for (JsonValue value : synonyms.getValuesAs(JsonValue.class)) {
                    synonymList.append(value.toString());
                    if (count != synonyms.size() - 1) {
                        synonymList.append(",");
                    }
                    count++;
                }
                ontologyTerm.addToComments("synonyms", synonymList.toString());
            }

            result.get(ontologyId).add(ontologyTerm);

        }

        return result;
    }

    public String querySearchEndpoint(String term, String ontologyIds, String subtree) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(BioPortal4Client.REST_URL + "search");

            // Configure the form parameters
            method.addParameter("q", term);


            if (StringUtils.trimToNull(subtree) != null) {
                method.addParameter("subtree", subtree);
                method.addParameter("ontology", ontologyIds);
            } else {
                if (StringUtils.trimToNull(ontologyIds) != null) {
                    method.addParameter("ontologies", ontologyIds);
                }
            }
            method.addParameter("apikey", API_KEY);
            method.addParameter("pagesize", "500");


            try {
                setHostConfiguration(client);
            } catch (Exception e) {
                System.err.println("Problem encountered setting host configuration for search");
            }


            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                String contents = method.getResponseBodyAsString();
                method.releaseConnection();
                return contents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, Ontology> getAllOntologies() {

        Map<String, Ontology> result = new HashMap<String, Ontology>();

        String content = queryOntologyEndpoint();

        StringReader reader = new StringReader(content);

        JsonReader rdr = Json.createReader(reader);

        JsonStructure generalStructure = rdr.read();
        if (generalStructure instanceof JsonArray) {
            // process array
            JsonArray array = (JsonArray) generalStructure;
            System.out.println("There are " + array.size() + " items...");
            for (JsonObject resultItem : array.getValuesAs(JsonObject.class)) {
                addOntology(result, resultItem);
            }
        } else {
            // process object
            JsonObject obj = (JsonObject) generalStructure;
            addOntology(result, obj);
        }

        return result;
    }

    private void addOntology(Map<String, Ontology> result, JsonObject resultItem) {
        JsonValue summaryOnly = resultItem.get("summaryOnly");
        if (summaryOnly != null) {
            Ontology newOntology = new Ontology(resultItem.getString("@id"), "version", resultItem.getString("acronym"), resultItem.getString("name"));

            if (!newOntology.getOntologyAbbreviation().contains("test") &&
                    !newOntology.getOntologyDisplayLabel().contains("test"))
                result.put(resultItem.getString("@id"), newOntology);
        }
    }

    public String queryOntologyEndpoint() {
        return queryOntologyEndpoint(null);
    }

    public String queryOntologyEndpoint(String ontology) {
        try {
            HttpClient client = new HttpClient();

            GetMethod method = new GetMethod(BioPortal4Client.REST_URL + "ontologies" + (ontology != null ? "/" + ontology : "") + "?apikey=" + API_KEY);

            try {
                setHostConfiguration(client);
            } catch (Exception e) {
                System.err.println("Problem encountered setting host configuration for ontology search");
            }


            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                String contents = method.getResponseBodyAsString();
                method.releaseConnection();
                return contents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setHostConfiguration(HttpClient client) {
        HostConfiguration configuration = new HostConfiguration();
        configuration.setHost("http://data.bioontology.org");
        client.setHostConfiguration(configuration);
    }
}
