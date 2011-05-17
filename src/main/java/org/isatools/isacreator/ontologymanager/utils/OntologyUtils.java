package org.isatools.isacreator.ontologymanager.utils;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.model.OntologyPortal;

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
        return ontology.getOntologyVersion().length() > 5 ? OntologyPortal.OLS : OntologyPortal.BIOPORTAL;
    }
}
