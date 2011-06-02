package org.isatools.isacreator.ontologymanager.utils;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.model.OntologyPortal;
import org.isatools.isacreator.ontologymanager.bioportal.xmlresulthandlers.AcceptedOntologies;
import org.isatools.isacreator.utils.StringProcessing;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/05/2011
 *         Time: 17:37
 */
public class OntologyUtils {

    public static OntologyPortal getSourceOntologyPortal(Ontology ontology) {
        return getSourceOntologyPortal(ontology.getOntologyVersion());
    }

    public static OntologyPortal getSourceOntologyPortal(String version) {
        return version.length() > 5 ? OntologyPortal.OLS : OntologyPortal.BIOPORTAL;
    }

     public static String getModifiedBranchIdentifier(String branchIdentifier, String splitOn) {
        if (StringProcessing.isURL(branchIdentifier)) {
            if (branchIdentifier.contains(splitOn)) {
                return branchIdentifier.substring(branchIdentifier.indexOf(splitOn) + 1);
            }
        }
        return branchIdentifier;
    }

    public static OntologyPortal getSourcePortalByAbbreviation(String abbreviation) {
        boolean isBioPortal = false;
        for(AcceptedOntologies bioPortalOntologies : AcceptedOntologies.values()) {
            if(bioPortalOntologies.getOntologyAbbreviation().equalsIgnoreCase(abbreviation)) {
                isBioPortal = true;
                break;
            }
        }

        return isBioPortal ? OntologyPortal.BIOPORTAL : OntologyPortal.OLS;
    }
}
