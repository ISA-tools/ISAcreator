package org.isatools.isacreator.ontologiser.logic.impl;

import org.isatools.isacreator.ontologiser.logic.SearchClient;
import org.isatools.isacreator.ontologymanager.utils.DownloadUtils;
import org.isatools.isacreator.ontologyselectiontool.OntologyObject;

import java.io.File;
import java.util.HashSet;
import java.util.List;
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
public class TerminizerSearchClient implements SearchClient {

    public static final String BASE_QUERY_URL = "http://terminizer.org/terminizerBackEnd/service?sourceText=";

    public Map<String, List<OntologyObject>> searchForTerms(Set<String> terms) {
        sendSearchRequest(terms);
        return null;
    }

    public File sendSearchRequest(Set<String> terms) {
        String downloadLocation = DownloadUtils.DOWNLOAD_FILE_LOC + "terminizer-result" + DownloadUtils.XML_EXT;
        String searchString = BASE_QUERY_URL + flattenSetToString(terms);
        System.out.println("Search string is :" + searchString);
        DownloadUtils.downloadFile(searchString, downloadLocation);
        return null;
    }

    private String flattenSetToString(Set<String> terms) {
        StringBuilder buffer = new StringBuilder();
        for (String term : terms) {
            buffer.append(term);
            buffer.append(",");
        }

        // return Substring to remove last comma
        return buffer.substring(0, buffer.length() - 1);
    }

    public static void main(String[] args) {
        SearchClient sc = new TerminizerSearchClient();

        Set<String> testTerms = new HashSet<String>();
        testTerms.add("\"Homo sapien\"");
        testTerms.add("Dose");
        testTerms.add("Assay");

        sc.searchForTerms(testTerms);
    }
}
