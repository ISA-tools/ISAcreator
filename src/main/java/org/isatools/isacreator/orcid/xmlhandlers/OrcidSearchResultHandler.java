package org.isatools.isacreator.orcid.xmlhandlers;

import org.isatools.isacreator.orcid.model.OrcidAuthor;
import org.orcid.ns.orcid.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 23/05/2013
 * Time: 21:21
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OrcidSearchResultHandler {

    public OrcidMessageDocument getOrcidMessageDocument(String xmlAsString) {
        OrcidMessageDocument resultDocument = null;
        try {

            InputStream stream = new ByteArrayInputStream(xmlAsString.getBytes("UTF-8"));
            resultDocument = OrcidMessageDocument.Factory.parse(stream);
        } catch (org.apache.xmlbeans.XmlException e) {
            System.err.println("XML Exception encountered");
            e.printStackTrace();
        } catch (java.io.IOException e) {
            System.err.println("IO Exception: " + e.getMessage());
            e.printStackTrace();
        }

        return resultDocument;
    }

    public OrcidAuthor[] getOrcidAuthors(OrcidMessageDocument messageDocument) {

        OrcidMessageDocument.OrcidMessage orcidMessage = messageDocument.getOrcidMessage();
        OrcidSearchResultsDocument.OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();

        if (searchResults == null)
            return null;

        OrcidSearchResultDocument.OrcidSearchResult[] results = searchResults.getOrcidSearchResultArray();

        OrcidAuthor[] authors = new OrcidAuthor[results.length];

        int i = 0;
        for (OrcidSearchResultDocument.OrcidSearchResult result : results) {
            OrcidProfileDocument.OrcidProfile profile = result.getOrcidProfile();
            if (profile != null) {
                authors[i] = getOrcidAuthor(profile);
                i++;
            }
        }
        return authors;
    }

    public OrcidAuthor getSingleOrcidAuthor(OrcidMessageDocument messageDocument) {

        OrcidAuthor orcidAuthor = null;
        OrcidMessageDocument.OrcidMessage orcidMessage = messageDocument.getOrcidMessage();
        OrcidSearchResultsDocument.OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();

        if (searchResults == null)
            return null;

        OrcidSearchResultDocument.OrcidSearchResult[] results = searchResults.getOrcidSearchResultArray();


        if (results.length == 1) {
            OrcidProfileDocument.OrcidProfile profile = results[0].getOrcidProfile();
            orcidAuthor = getOrcidAuthor(profile);
        }

        System.out.println(orcidAuthor);
        return orcidAuthor;
    }


    private OrcidAuthor getOrcidAuthor(OrcidProfileDocument.OrcidProfile profile) {
        OrcidAuthor orcidAuthor = new OrcidAuthor();

        orcidAuthor.setOrcid(profile.getOrcidIdentifier().getPath());

        OrcidBioDocument.OrcidBio orcidBio = profile.getOrcidBio();
        PersonalDetailsDocument.PersonalDetails personalDetails = orcidBio.getPersonalDetails();

        orcidAuthor.setGivenNames(personalDetails.getGivenNames());
        if (personalDetails.getFamilyName() != null) {
            orcidAuthor.setFamilyName(personalDetails.getFamilyName());
        }


        ContactDetailsDocument.ContactDetails contactDetails = orcidBio.getContactDetails();
        if (contactDetails != null) {
            Email[] emails = contactDetails.getEmailArray();
            if (emails.length > 0)
                orcidAuthor.setEmail(emails[0].getStringValue());
        }

        return orcidAuthor;
    }

}
