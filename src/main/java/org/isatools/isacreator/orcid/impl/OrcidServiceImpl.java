package org.isatools.isacreator.orcid.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.isatools.isacreator.orcid.OrcidService;
import org.isatools.isacreator.orcid.model.OrcidAuthor;
import org.isatools.isacreator.orcid.xmlhandlers.OrcidSearchResultHandler;
import org.orcid.ns.orcid.OrcidMessageDocument;

import java.io.IOException;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 13:37
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidServiceImpl implements OrcidService {

    public static final String QUERY_URL = "http://pub.orcid.org/search/orcid-bio/";

    public static final String ACCEPT  = "Accept-Encoding";
    public static final String ORCID_XML = "application/orcid+xml";
    public static final String CONTENT_TYPE = "Content-Type";

    private HttpClient client = null;
    private GetMethod getMethod = null;


    public OrcidServiceImpl(){

        client = new HttpClient();
        getMethod = new GetMethod(QUERY_URL);



    }

    public OrcidAuthor getAuthorInfo(String orcidID) {

            getMethod.setQueryString("q=orcid:" + orcidID );


        try{
            System.out.println("query string=" + getMethod.getQueryString());
            System.out.println("URI="+ getMethod.getURI());


            getMethod.addRequestHeader(CONTENT_TYPE, ORCID_XML);
            //getMethod.addRequestHeader(ACCEPT, ORCID_XML);

            int statusCode = client.executeMethod(getMethod);

            if (statusCode != -1) {
                String contents = getMethod.getResponseBodyAsString();

                System.out.println("status text=" + getMethod.getStatusText());
                System.out.println("contents=" + contents);

                getMethod.releaseConnection();
                return processAuthorInfo(contents);
            }else{
                System.out.println("status code is -1");
            }

        }catch(IOException ex){
            ex.printStackTrace();
        }//catch(HttpException ex){

        //}
        return null;
    }


    private OrcidAuthor processAuthorInfo(String contents){
        System.out.println("contents="+contents);
        OrcidSearchResultHandler handler = new OrcidSearchResultHandler();
        OrcidMessageDocument orcidMessageDocument = handler.getOrcidMessageDocument(contents);
        return handler.getOrcidAuthor(orcidMessageDocument);
    }


    public static void main(String[] args) {
        OrcidServiceImpl service = new OrcidServiceImpl();
        service.getAuthorInfo("0000-0003-3499-8262");
    }

}
