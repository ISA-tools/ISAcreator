package org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;
import org.isatools.isacreator.ontologymanager.BioPortal4Client;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.omg.CORBA.OBJECT_NOT_EXIST;

import javax.json.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BioPortalQueryEndpoint {

    public static final String API_KEY = "fd88ee35-6995-475d-b15a-85f1b9dd7a42";

    public Map<OntologySourceRefObject, List<OntologyTerm>> getSearchResults(String term, String ontologyIds, String subtree) {

        Map<OntologySourceRefObject, List<OntologyTerm>> result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

        String content = querySearchEndpoint(term, ontologyIds, subtree);

        StringReader reader = new StringReader(content);

        JsonReader rdr = Json.createReader(reader);

        JsonObject obj = rdr.readObject();
        JsonArray results = obj.getJsonArray("collection");
        for (JsonObject resultItem : results.getValuesAs(JsonObject.class)) {
            System.out.println(resultItem.getString("prefLabel"));
            System.out.println(resultItem.getString("@id"));
            JsonArray definitions = resultItem.getJsonArray("definition");
            if (definitions != null) {
                System.out.println("\t" + definitions.get(0));
            }

//            JsonArray links = resultItem.getJS("links");
//            if (links != null) {
//                for(JsonValue link : links)
//                System.out.println("\t" + definitions.get(0));
//            }
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
            method.addParameter("require_definition", "true");


            try {
                setHostConfiguration(client);
            } catch (Exception e) {
                System.err.println("Problem encountered setting host configuration for search");
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

    public Map<OntologySourceRefObject, List<OntologyTerm>> getAllOntologies() {

        Map<OntologySourceRefObject, List<OntologyTerm>> result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

        String content = queryOntologyEndpoint();

        StringReader reader = new StringReader(content);

        JsonReader rdr = Json.createReader(reader);

        JsonStructure generalStructure = rdr.read();
        if(generalStructure instanceof JsonArray) {
            // process array
            JsonArray array = (JsonArray) generalStructure;
            System.out.println("There are " + array.size()  + " items...");
            for (JsonObject resultItem : array.getValuesAs(JsonObject.class)) {
                System.out.println(resultItem.getString("acronym"));
                System.out.println(resultItem.getString("name"));
                System.out.println(resultItem.getString("@id"));
            }
        } else {
           // process object
            JsonObject obj = (JsonObject) generalStructure;
            System.out.println(obj.getString("acronym"));
            System.out.println(obj.getString("name"));
            System.out.println(obj.getString("@id"));
        }

//        JsonArray results = obj.getJsonArray("");
//        System.out.println(results.size() + " results");

        return result;
    }

    public String queryOntologyEndpoint() {
        return queryOntologyEndpoint(null);
    }

    public String queryOntologyEndpoint(String ontology) {
        try {
            HttpClient client = new HttpClient();

            GetMethod method = new GetMethod(BioPortal4Client.REST_URL + "ontologies" + (ontology != null ? "/"+ontology :"") + "?apikey="+API_KEY);

            try {
                setHostConfiguration(client);
            } catch (Exception e) {
                System.err.println("Problem encountered setting host configuration for ontology search");
            }


            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {

                System.out.println(method.getURI().toString());
//                for(NameValuePair param : method.getParams().getp) {
//                    System.out.println(param);
//                }
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
