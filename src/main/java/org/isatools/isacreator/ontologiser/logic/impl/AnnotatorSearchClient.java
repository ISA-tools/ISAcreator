package org.isatools.isacreator.ontologiser.logic.impl;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.bioportal.jsonresulthandlers.BioPortalAnnotatorResultHandler;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import uk.ac.ebi.utils.io.DownloadUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 11:29
 */
public class AnnotatorSearchClient {

    public static final String BASE_QUERY_URL = "http://data.bioontology.org/annotator";

    public Map<String, Map<String, AnnotatorResult>> searchForTerms(Set<String> terms) {
        return searchForTerms(terms, "", true);
    }

    public Map<String, Map<String, AnnotatorResult>> searchForTerms(Set<String> terms, String ontologiesToSearchOn, boolean wholeWordOnly) {
        try {
            String flattenedTerms = flattenSetToString(terms);

            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(BASE_QUERY_URL);

            // Configure the form parameters
            method.addParameter("wholeWordOnly", wholeWordOnly ? " true" : "false");

            method.addParameter("ontologies", ontologiesToSearchOn);
            method.addParameter("text", flattenedTerms);
            method.addParameter("include", "prefLabel");
            method.addParameter("apikey", "fd88ee35-6995-475d-b15a-85f1b9dd7a42");

            try {
                HostConfiguration configuration = new HostConfiguration();
                configuration.setHost("http://data.bioontology.org");
                configuration.setProxy(System.getProperty("http.proxyHost"), Integer.valueOf(System.getProperty("http.proxyPort")));
                client.setHostConfiguration(configuration);
            } catch (Exception e) {
                System.err.println("Problem encountered setting proxy for annotator search");
            }

            int statusCode = client.executeMethod(method);
            if (statusCode != -1) {
                String contents = method.getResponseBodyAsString();

                method.releaseConnection();
                return processContent(contents, flattenedTerms, terms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, Map<String, AnnotatorResult>> processContent(String content, String originalText, Set<String> terms) {
        BioPortalAnnotatorResultHandler handler = new BioPortalAnnotatorResultHandler();
        return handler.getSearchResults(content, originalText, terms);
    }

    private String flattenSetToString(Set<String> terms) {
        StringBuilder buffer = new StringBuilder();
        for (String term : terms) {
            buffer.append(term);
            buffer.append(" ");
        }

        // return Substring to remove last comma
        return buffer.toString();
    }
}
