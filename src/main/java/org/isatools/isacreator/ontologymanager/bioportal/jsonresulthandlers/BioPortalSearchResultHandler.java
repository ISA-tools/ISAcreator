package org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.BioPortal4Client;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioPortalSearchResultHandler {

    public static final String API_KEY = "fd88ee35-6995-475d-b15a-85f1b9dd7a42";
    public static final String PARENTS = "ancestors";
    public static final String CHILDREN = "children";


    public Map<String, List<OntologyTerm>> getSearchResults(String term, String ontologyIds, String subtree) {
        return getSearchResults(term, ontologyIds, subtree, false);
    }

    /**
     * Returns the result of the search operation
     *
     * @param term        - the string being searched for
     * @param ontologyIds - the ontologies the search is being restricted to
     * @param @nullable   subtree - a subtree, if any to be searched under (optional)
     * @return - Map from the id of the ontology to the list of terms found under it.
     */
    public Map<String, List<OntologyTerm>> getSearchResults(String term, String ontologyIds, String subtree, boolean exactMatch) {

        // map from ontology id to the list of terms found for that id.
        Map<String, List<OntologyTerm>> result = new HashMap<String, List<OntologyTerm>>();

        String content = querySearchEndpoint(term, ontologyIds, subtree, exactMatch);

        JSONObject obj = (JSONObject) JSONValue.parse(content);

        JSONArray results = (JSONArray) obj.get("collection");

        if (results == null)
            return result;

        for (Object result1 : results) {

            JSONObject resultItem = (JSONObject) result1;
            String ontologyId = extractOntologyId(resultItem);

            if (!result.containsKey(ontologyId)) {
                result.put(ontologyId, new ArrayList<OntologyTerm>());
            }
            OntologyTerm ontologyTerm = createOntologyTerm(resultItem);
            result.get(ontologyId).add(ontologyTerm);

        }

        return result;
    }

    private String extractOntologyId(JSONObject ontologyItemJsonDictionary) {
        JSONObject links = (JSONObject) ontologyItemJsonDictionary.get("links");
        return links.get("ontology").toString();
    }

    private void extractDefinitionFromOntologyTerm(JSONObject ontologyItemJsonDictionary, OntologyTerm ontologyTerm) {
        JSONArray definitions = (JSONArray) ontologyItemJsonDictionary.get("definition");
        if (definitions != null && definitions.size() > 0) {
            ontologyTerm.addToComments("definition", definitions.get(0).toString());
        }
    }

    private void extractSynonymsFromOntologyTerm(JSONObject ontologyItemJsonDictionary, OntologyTerm ontologyTerm) {
        JSONArray synonyms = (JSONArray) ontologyItemJsonDictionary.get("synonyms");
        if (synonyms != null && synonyms.size() > 0) {
            StringBuilder synonymList = new StringBuilder();
            int count = 0;
            for (Object value : synonyms) {
                synonymList.append(value.toString());
                if (count != synonyms.size() - 1) {
                    synonymList.append(",");
                }
                count++;
            }
            ontologyTerm.addToComments("synonyms", synonymList.toString());
        }
    }


    private String querySearchEndpoint(String term, String ontologyIds, String subtree, boolean exactMatch) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(BioPortal4Client.REST_URL + "search");

            // Configure the form parameters
            method.addParameter("q", term);

            if (StringUtils.trimToNull(subtree) != null) {
                method.addParameter("subtree", subtree);

                if (ontologyIds != null)
                    method.addParameter("ontology", ontologyIds);
            } else if (!ontologyIds.equals("all")) {
                method.addParameter("ontologies", ontologyIds);
            }


            method.addParameter("apikey", API_KEY);
            method.addParameter("pagesize", "100");
            method.addParameter("no_context", "true");

            if (exactMatch) {
                method.addParameter("exact_match", "true");
            }

            try {
                setHostConfiguration(client);
            } catch (Exception e) {
                System.err.println("Problem encountered setting host configuration for search");
            }

            long startTime = System.currentTimeMillis();
            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                System.out.println("It took " + (System.currentTimeMillis() - startTime) + "ms to do that query...");
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

        if (content.contains("Internal server error")) {
            System.err.println("The BioPortal REST service reports Internal server error");
            return result;
        }

        JSONArray obj = (JSONArray) JSONValue.parse(content);

        for (Object resultItem : obj) {
            addOntology(result, (JSONObject) resultItem);
        }

        return result;
    }

    private void addOntology(Map<String, Ontology> result, JSONObject resultItem) {
        JSONObject ontology = (JSONObject) resultItem.get("ontology");
        boolean summaryOnly = Boolean.valueOf(ontology.get("summaryOnly") != null ? ontology.get("summaryOnly").toString() : "false");
        if (!summaryOnly) {
            Ontology newOntology = new Ontology(ontology.get("@id").toString(), "version", ontology.get("acronym").toString(), ontology.get("name").toString());
            if (!newOntology.getOntologyAbbreviation().contains("test") &&
                    !newOntology.getOntologyDisplayLabel().contains("test")) {

                String version = resultItem.get("version") != null ? resultItem.get("version").toString() : "";
                String submissionId = resultItem.get("submissionId") != null ? resultItem.get("submissionId").toString() : "";
                String homepage = resultItem.get("homepage") != null ? resultItem.get("homepage").toString() : "";

                newOntology.setHomePage(homepage);
                newOntology.setOntologyVersion(version);
                newOntology.setSubmissionId(submissionId);

                result.put(resultItem.get("@id").toString(), newOntology);
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
        String proxyPort = System.getProperty("http.proxyPort");
        if (proxyPort == null)
            return;
        configuration.setProxy(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")));
        client.setHostConfiguration(configuration);
    }

    public OntologyTerm getTermMetadata(String termId, String ontologyId) {

        String content = queryTermMetadataEndpoint(termId, ontologyId);
        JSONObject obj = (JSONObject) JSONValue.parse(content);


        // if we have a nice error free page, continue
        if (!obj.containsKey("errors")) {
            OntologySourceRefObject osro = getOntologySourceRefObject(ontologyId);

            OntologyTerm ontologyTerm = new OntologyTerm(
                    obj.get("prefLabel").toString(),
                    obj.get("@id").toString(),
                    obj.get("@id").toString(), osro);
            ontologyTerm.addToComments("Service Provider", OntologyManager.BIO_PORTAL);
            extractDefinitionFromOntologyTerm(obj, ontologyTerm);
            extractSynonymsFromOntologyTerm(obj, ontologyTerm);

            System.out.println(ontologyTerm.getOntologyTermName() + " - " + ontologyTerm.getOntologyTermAccession() + " - " + ontologyTerm.getOntologyTermURI());

            return ontologyTerm;
        } else {
            return null;
        }
    }

    private OntologySourceRefObject getOntologySourceRefObject(String ontologyId) {
        Ontology associatedOntologySource = AcceptedOntologies.getAcceptedOntologies().get(ontologyId);
        return new OntologySourceRefObject(associatedOntologySource.getOntologyAbbreviation(), associatedOntologySource.getOntologyID(), associatedOntologySource.getOntologyVersion(), associatedOntologySource.getOntologyDisplayLabel());
    }

    public String queryTermMetadataEndpoint(String termId, String ontologyURI) {
        try {
            HttpClient client = new HttpClient();
            String url = ontologyURI + "/classes/" + URLEncoder.encode(termId, "UTF-8") + "?apikey=" + API_KEY;

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
            System.err.println("Unable to retrieve term metadata");
        }
        return null;
    }

    public Map<String, OntologyTerm> getOntologyRoots(String ontologyAbbreviation) {

        Map<String, OntologyTerm> roots = new HashMap<String, OntologyTerm>();

        String queryContents = generalQueryEndpoint(BioPortal4Client.REST_URL + "ontologies/" + ontologyAbbreviation + "/classes/roots?apikey=" + API_KEY);
        System.out.println(BioPortal4Client.REST_URL + "ontologies/" + ontologyAbbreviation + "/classes/roots?apikey=" + API_KEY);

        JSONArray ontologyTerms = (JSONArray) JSONValue.parse(queryContents);


        for (Object annotationItem : ontologyTerms) {
            JSONObject annotationObject = (JSONObject) annotationItem;
            OntologySourceRefObject osro = getOntologySourceRefObject(extractOntologyId(annotationObject));

            OntologyTerm ontologyTerm = createOntologyTerm(annotationObject);
            ontologyTerm.setOntologySourceInformation(osro);

            roots.put(ontologyTerm.getOntologyTermAccession(), ontologyTerm);
        }

        return roots;
    }

    private OntologyTerm createOntologyTerm(JSONObject annotationItem) {

        OntologyTerm ontologyTerm = new OntologyTerm(annotationItem.get("prefLabel") !=null? annotationItem.get("prefLabel").toString(): "",
                                                    annotationItem.get("@id") != null ? annotationItem.get("@id").toString(): "",
                                                    annotationItem.get("@id") !=null ? annotationItem.get("@id").toString(): "",
                                                    null);
        ontologyTerm.addToComments("Service Provider", OntologyManager.BIO_PORTAL);
        extractDefinitionFromOntologyTerm(annotationItem, ontologyTerm);
        extractSynonymsFromOntologyTerm(annotationItem, ontologyTerm);
        String ontologyId = extractOntologyId(annotationItem);
        if (ontologyId != null) {
            String ontologyAbbreviation = ontologyId.substring(ontologyId.lastIndexOf('/') + 1);
            OntologySourceRefObject sourceRefObject = OntologyManager.getOntologySourceReferenceObjectByAbbreviation(ontologyAbbreviation);
            if (sourceRefObject != null)
                ontologyTerm.setOntologySourceInformation(sourceRefObject);
        }
        return ontologyTerm;
    }

    /**
     * @param url
     * @return
     */
    private String generalQueryEndpoint(String url) {
        try {
            HttpClient client = new HttpClient();

            GetMethod method = new GetMethod(url);
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

    public Map<String, OntologyTerm> getTermParents(String termAccession, String ontologyAbbreviation) {
        return getTermChildrenOrParents(termAccession, ontologyAbbreviation, PARENTS);
    }

    public Map<String, OntologyTerm> getTermChildren(String termAccession, String ontologyAbbreviation) {
        return getTermChildrenOrParents(termAccession, ontologyAbbreviation, CHILDREN);
    }

    /**
     * Will make a call to get the parents or children of a term, identified by its termAccession in a particular
     * ontology, defined by the ontologyAbbreviation.
     *
     * @param termAccession        - e.g. http://purl.obolibrary.org/obo/OBI_0000785
     * @param ontologyAbbreviation - e.g. EFO
     * @param parentsOrChildren    - 'parents' or 'children' as an input value
     * @return Map from the ontology term id to its OntologyTerm object.
     */
    public Map<String, OntologyTerm> getTermChildrenOrParents(String termAccession, String ontologyAbbreviation, String parentsOrChildren) {
        try {
            Map<String, OntologyTerm> parents = new ListOrderedMap<String, OntologyTerm>();

            String queryContents = generalQueryEndpoint(BioPortal4Client.REST_URL + "ontologies/" + ontologyAbbreviation + "/classes/"
                    + URLEncoder.encode(termAccession, "UTF-8") + "/" + parentsOrChildren + "?apikey=" + API_KEY);


            Object obj = JSONValue.parse(queryContents);

            JSONArray rootArray;

            if (obj instanceof JSONObject) {
                // the children result returns a dictionary, or JsonStructure, so we have to go down one level to get
                // the collection of results.
                rootArray = (JSONArray) ((JSONObject) obj).get("collection");
            } else {
                // the parents result returns an array directly, so we can just use it immediately.
                rootArray = (JSONArray) obj;
            }

            for (Object arrayValue: rootArray) {
                JSONObject annotationItem = (JSONObject) arrayValue;
                OntologySourceRefObject osro = getOntologySourceRefObject(extractOntologyId(annotationItem));

                OntologyTerm ontologyTerm = createOntologyTerm(annotationItem);
                ontologyTerm.setOntologySourceInformation(osro);

                parents.put(ontologyTerm.getOntologyTermAccession(), ontologyTerm);
            }

            return parents;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
