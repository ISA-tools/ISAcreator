package org.isatools.isacreator.orcid.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.isatools.isacreator.orcid.OrcidClient;
import org.isatools.isacreator.orcid.model.OrcidAuthor;
import org.isatools.isacreator.orcid.xmlhandlers.OrcidSearchResultHandler;
import org.orcid.ns.orcid.OrcidMessageDocument;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 13:37
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidClientImpl implements OrcidClient {

    public static final String QUERY_URL = "http://pub.orcid.org/search/orcid-bio/";

    public static final String ACCEPT = "Accept-Encoding";
    public static final String ORCID_XML = "application/orcid+xml";
    public static final String CONTENT_TYPE = "Content-Type";

    private HttpClient client = null;
    private GetMethod getMethod = null;
    private OrcidSearchResultHandler handler = null;


    public OrcidClientImpl() {

        client = new HttpClient();
        getMethod = new GetMethod(QUERY_URL);
        handler = new OrcidSearchResultHandler();

    }

    public OrcidAuthor getAuthorInfo(String orcidID) {

        try {

            getMethod.setQueryString("q=" + (orcidID.contains("-") ? "orcid" : "text") + ":" + orcidID);

            System.out.println("query string=" + getMethod.getQueryString());
            System.out.println("URI=" + getMethod.getURI());


            getMethod.addRequestHeader(CONTENT_TYPE, ORCID_XML);
            //getMethod.addRequestHeader(ACCEPT, ORCID_XML);

            int statusCode = client.executeMethod(getMethod);

            if (statusCode != -1) {
                String contents = getMethod.getResponseBodyAsString();

                System.out.println("status text=" + getMethod.getStatusText());
                System.out.println("contents=" + contents);

                getMethod.releaseConnection();
                return processAuthorInfo(contents);
            } else {
                System.out.println("status code is -1");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }//catch(HttpException ex){

        //}
        return null;
    }

    public OrcidAuthor[] getOrcidProfiles(String searchString) {

        try {
            getMethod.setQueryString("q=text:" + URLEncoder.encode(searchString, "UTF-8") + "");

            System.out.println("query string=" + getMethod.getQueryString());
            System.out.println("URI=" + getMethod.getURI());


            getMethod.addRequestHeader(CONTENT_TYPE, ORCID_XML);

            int statusCode = client.executeMethod(getMethod);

            if (statusCode != -1) {
                String contents = getMethod.getResponseBodyAsString();

                System.out.println("status text=" + getMethod.getStatusText());
                System.out.println("contents=" + contents);

                getMethod.releaseConnection();
                return processOrcidProfles(contents);
            } else {
                System.out.println("status code is -1");
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }//catch(HttpException ex){

        //}
        return null;


    }

    private OrcidAuthor[] processOrcidProfles(String contents) {
        OrcidMessageDocument orcidMessageDocument = handler.getOrcidMessageDocument(contents);
        return handler.getOrcidAuthors(orcidMessageDocument);
    }

    private OrcidAuthor processAuthorInfo(String contents) {
        System.out.println("contents=" + contents);
        OrcidMessageDocument orcidMessageDocument = handler.getOrcidMessageDocument(contents);
        return handler.getSingleOrcidAuthor(orcidMessageDocument);
    }


    public static void main(String[] args) {
        OrcidClientImpl client = new OrcidClientImpl();
        //client.getAuthorInfo("0000-0003-3499-8262");
        // client.getOrcidProfiles("English");
        //client.getOrcidProfiles("gonzalez-beltran");
        client.getOrcidProfiles("0000-0003-3499-8262");
    }

}
