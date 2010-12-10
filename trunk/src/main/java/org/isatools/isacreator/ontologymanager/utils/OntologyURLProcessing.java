package org.isatools.isacreator.ontologymanager.utils;

import org.isatools.isacreator.ontologymanager.bioportal.model.BioPortalOntology;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         Date: Dec 3, 2010
 *         Time: 4:54:24 PM
 */
public class OntologyURLProcessing {

    public static BioPortalOntology extractOntologyFromURL(String url) {
        BioPortalOntology tmpOntology = new BioPortalOntology();

        int lastIndex = (url.lastIndexOf("/") > url
                .lastIndexOf("#"))
                ? url.lastIndexOf("/") : url.lastIndexOf("#");

        String keyAndAccession = url.substring(lastIndex + 1);
        String[] keyAccVals = keyAndAccession.split((keyAndAccession.contains("_")) ? "_" : "/?");

        if (keyAccVals.length > 1) {
            tmpOntology.setOntologySource(keyAccVals[0]);
            tmpOntology.setOntologySourceAccession(keyAndAccession);

            return extractonOntologyfromHierarchicalURL(url);
        } else {
            tmpOntology.setOntologySource(keyAndAccession);
        }

        return tmpOntology;
    }

    /**
     * Extract the source and accession from a URL formed like this: http:/ontology/source/accession
     *
     * @param url -  e.g. http:/ontology/source/accession
     * @return BioPortalOntology to encapsulate the extracted information.
     */
    public static BioPortalOntology extractonOntologyfromHierarchicalURL(String url) {
        BioPortalOntology tmpOntology = new BioPortalOntology();

        String[] tokensSplitOnSlash = url.split("/");

        tmpOntology.setOntologySource(tokensSplitOnSlash[tokensSplitOnSlash.length - 2]);
        tmpOntology.setOntologySourceAccession(tokensSplitOnSlash[tokensSplitOnSlash.length - 1]);

        return tmpOntology;
    }

}
