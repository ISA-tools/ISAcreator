package org.isatools.isacreator.ontologiser.logic;

import org.isatools.isacreator.ontologyselectiontool.OntologyObject;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 11:26
 */
public interface SearchClient {

    public Map<String, List<OntologyObject>> searchForTerms(Set<String> terms);
}
