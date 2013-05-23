package org.isatools.isacreator.orcid.xmlhandlers;

import org.apache.xmlbeans.XmlOptions;
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

    public OrcidAuthor getOrcidAuthor(OrcidMessageDocument messageDocument){

        OrcidAuthor orcidAuthor = new OrcidAuthor();

        OrcidMessageDocument.OrcidMessage orcidMessage = messageDocument.getOrcidMessage();
        OrcidSearchResultsDocument.OrcidSearchResults searchResults = orcidMessage.getOrcidSearchResults();

        if (searchResults==null)
            return null;

        OrcidSearchResultDocument.OrcidSearchResult[] results = searchResults.getOrcidSearchResultArray();
        XmlOptions opts = new XmlOptions();
        opts.setSaveInner();

        if (results.length == 1){

            OrcidProfileDocument.OrcidProfile profile = results[0].getOrcidProfile();

            orcidAuthor.setOrcid(profile.getOrcid().toString());

            OrcidBioDocument.OrcidBio orcidBio = profile.getOrcidBio();
            PersonalDetailsDocument.PersonalDetails personalDetails = orcidBio.getPersonalDetails();
            AffiliationsDocument.Affiliations affiliations = orcidBio.getAffiliations();


            GivenNamesDocument.GivenNames givenNames = personalDetails.getGivenNames();

            orcidAuthor.setGivenNames(removeFragments(givenNames.xmlText()));
            orcidAuthor.setFamilyName(removeFragments(personalDetails.getFamilyName().xmlText(opts)));

            AffiliationDocument.Affiliation[] affiliationArray = affiliations.getAffiliationArray();
            if (affiliationArray.length>0)
                orcidAuthor.setCurrentPrimaryInstitution(removeFragments(affiliationArray[0].getAffiliationName().xmlText(opts)));

            ContactDetailsDocument.ContactDetails contactDetails = orcidBio.getContactDetails();
            if (contactDetails!=null){
                Email[] emails = contactDetails.getEmailArray();
                if (emails.length>0)
                    orcidAuthor.setEmail(emails[0].getStringValue());
            }

        }

        System.out.println(orcidAuthor);
        return orcidAuthor;
    }


    private String removeFragments(String text){
        return text.substring(14,text.length()-15);

    }
}
