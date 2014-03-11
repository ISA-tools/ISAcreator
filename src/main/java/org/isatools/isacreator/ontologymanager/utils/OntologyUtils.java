package org.isatools.isacreator.ontologymanager.utils;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.OntologyBranch;
import org.isatools.isacreator.ontologymanager.BioPortal4Client;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.bioportal.model.OntologyPortal;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
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
        return getSourceOntologyPortalByVersionAndId(ontology);
    }

    public static OntologyPortal getSourceOntologyPortalByVersionAndId(Ontology ontology) {
        return OntologyPortal.BIOPORTAL;
    }

    public static OntologyPortal getSourceOntologyPortalByVersion(String version) {
        return OntologyPortal.BIOPORTAL;
    }

    private static boolean checkVersion(String version) {
        return version.length() > 5;
    }

    public static String getModifiedBranchIdentifier(String branchIdentifier, String splitOn) {
        if (StringProcessing.isURL(branchIdentifier)) {
            if (branchIdentifier.contains(splitOn)) {
                return branchIdentifier.substring(branchIdentifier.indexOf(splitOn) + 1);
            }
        }
        return branchIdentifier;
    }

    public static OntologySourceRefObject convertOntologyToOntologySourceReferenceObject(Ontology ontology) {

        OntologySourceRefObject converted = new OntologySourceRefObject(
                ontology.getOntologyAbbreviation(), "", ontology.getOntologyVersion(), ontology.getOntologyDisplayLabel());

        converted.setSourceFile(OntologyUtils.getSourceOntologyPortalByVersion(BioPortal4Client.DIRECT_ONTOLOGY_URL + converted.getSourceVersion()).toString());


        return converted;
    }

    public static OntologyTerm convertOntologyBranchToOntologyTerm(OntologyBranch branch, OntologySourceRefObject ontologySource) {
        System.out.println(branch.getBranchIdentifier());
        System.out.println("Source Name = " + ontologySource.getSourceName());
        System.out.println("Source Description = " + ontologySource.getSourceDescription());
        return new OntologyTerm(branch.getBranchName(),branch.getBranchIdentifier(), branch.getBranchIdentifier(), ontologySource);
    }
}
