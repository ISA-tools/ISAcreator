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
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.omg.CORBA.OBJECT_NOT_EXIST;

import javax.json.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioPortalSearchResultHandler {

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

            extractDefinitionFromOntologyTerms(resultItem, ontologyTerm);
            extractSynonymsFromOntologyTerm(resultItem, ontologyTerm);

            result.get(ontologyId).add(ontologyTerm);

        }

        return result;
    }

    private void extractDefinitionFromOntologyTerms(JsonObject resultItem, OntologyTerm ontologyTerm) {
        JsonArray definitions = resultItem.getJsonArray("definition");
        if (definitions != null && definitions.size() > 0) {
            ontologyTerm.addToComments("definition", definitions.get(0).toString());
        }
    }

    private void extractSynonymsFromOntologyTerm(JsonObject resultItem, OntologyTerm ontologyTerm) {
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
//            method.addParameter("no_links", "true");
            method.addParameter("no_context", "true");

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
        JsonArray array = rdr.readArray();

        for (JsonObject resultItem : array.getValuesAs(JsonObject.class)) {
            addOntology(result, resultItem);
        }

        return result;
    }

    private void addOntology(Map<String, Ontology> result, JsonObject resultItem) {
        JsonObject ontology = resultItem.getJsonObject("ontology");
        JsonValue summaryOnly = ontology.get("summaryOnly");
        if (summaryOnly != null) {
            Ontology newOntology = new Ontology(ontology.getString("@id"), "version", ontology.getString("acronym"), ontology.getString("name"));
            if (!newOntology.getOntologyAbbreviation().contains("test") &&
                    !newOntology.getOntologyDisplayLabel().contains("test")) {

                String version = resultItem.get("version").toString();
                JsonNumber submissionId = resultItem.getJsonNumber("submissionId");
                String homepage = resultItem.get("homepage").toString();

                newOntology.setHomePage(homepage);
                newOntology.setOntologyVersion(version);
                newOntology.setSubmissionId(submissionId.toString());

                result.put(resultItem.getString("@id"), newOntology);
            }

        }
    }


    public String queryOntologyEndpoint() {
        try {
            HttpClient client = new HttpClient();

            //http://data.bioontology.org/submissions
            GetMethod method = new GetMethod(BioPortal4Client.REST_URL + "submissions?apikey=" + API_KEY);

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
        configuration.setProxy(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")));
        client.setHostConfiguration(configuration);
    }

    public OntologyTerm getTermMetadata(String termId, String ontologyId) {

        String content = queryTermMetadataEndpoint(termId, ontologyId);
        StringReader reader = new StringReader(content);
        JsonReader rdr = Json.createReader(reader);
        JsonObject obj = rdr.readObject();

        // if we have a nice error free page, continue
        if (!obj.containsKey("errors")) {
            Ontology associatedOntologySource = AcceptedOntologies.getAcceptedOntologies().get(ontologyId);
            OntologySourceRefObject osro = new OntologySourceRefObject(associatedOntologySource.getOntologyAbbreviation(), associatedOntologySource.getOntologyID(), associatedOntologySource.getOntologyVersion(), associatedOntologySource.getOntologyDisplayLabel());

            OntologyTerm ontologyTerm = new OntologyTerm(obj.getString("prefLabel"), obj.getString("@id"), obj.getString("@id"), osro);
            extractDefinitionFromOntologyTerms(obj, ontologyTerm);
            extractSynonymsFromOntologyTerm(obj, ontologyTerm);

            System.out.println(ontologyTerm.getOntologyTermName() + " - " + ontologyTerm.getOntologyTermAccession());

            return ontologyTerm;
        } else {
            return null;
        }
    }

    public String queryTermMetadataEndpoint(String termId, String ontologyId) {
        try {
            HttpClient client = new HttpClient();
            String url = ontologyId + "/classes/" + URLEncoder.encode(termId, "UTF-8") + "?apikey=" + API_KEY;

            GetMethod method = new GetMethod(url);

            System.out.println(method.getURI().toString());
            try {
                setHostConfiguration(client);
            } catch (Exception e) {
                System.err.println("Problem encountered setting host configuration for ontology search");
            }

            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                String contents = method.getResponseBodyAsString();
                System.out.println(contents);
                method.releaseConnection();
                return contents;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
