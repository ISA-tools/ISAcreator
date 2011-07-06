package org.isatools.isacreator.ontologiser.logic.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologymanager.bioportal.utils.BioPortalXMLModifier;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.BioPortalAnnotatorResultHandler;
import org.isatools.isacreator.ontologymanager.utils.DownloadUtils;

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

    public static final String BASE_QUERY_URL = "http://rest.bioontology.org/obs/annotator";

    public Map<String, Map<String, AnnotatorResult>> searchForTerms(Set<String> terms) {
        try {
            HttpClient client = new HttpClient();
            PostMethod method = new PostMethod(BASE_QUERY_URL);

            // Configure the form parameters
//            method.addParameter("longestOnly", "true");
            method.addParameter("wholeWordOnly", "true");
            //method.addParameter("stopWords", "choubala");
            //method.addParameter("withDefaultStopWords", "true");
            method.addParameter("scored", "true");
            //method.addParameter("ontologiesToExpand", "38802,13578,40644,40403");
            //method.addParameter("ontologiesToKeepInResult", "40403");
            //method.addParameter("semanticTypes", "T999");
            //method.addParameter("levelMax", "50");
            method.addParameter("isVirtualOntologyId", "true");
            method.addParameter("withSynonyms", "true");
            method.addParameter("textToAnnotate", flattenSetToString(terms));
            method.addParameter("apikey", "fd88ee35-6995-475d-b15a-85f1b9dd7a42");
            //method.addParameter("format", "asText");

            // Execute the POST method
            int statusCode = client.executeMethod(method);

            if (statusCode != -1) {
                String contents = method.getResponseBodyAsString();
                method.releaseConnection();
                return processContent(contents, terms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<String, Map<String, AnnotatorResult>> processContent(String content, Set<String> terms) throws FileNotFoundException {

        File fileWithNameSpace = BioPortalXMLModifier.addNameSpaceToFile(
                createFileForContent(content), "http://bioontology.org/bioportal/annotator#", "<success>");

        BioPortalAnnotatorResultHandler handler = new BioPortalAnnotatorResultHandler();

        System.out.println("File saved in " + fileWithNameSpace.getAbsolutePath());

        return handler.getSearchResults(fileWithNameSpace.getAbsolutePath(), terms);
    }

    private File createFileForContent(String content) throws FileNotFoundException {
        File toSaveAs = new File(DownloadUtils.DOWNLOAD_FILE_LOC + "annotator-result" + DownloadUtils.XML_EXT);
        PrintStream ps = new PrintStream(toSaveAs);
        ps.print(content);
        ps.close();

        return toSaveAs;
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
