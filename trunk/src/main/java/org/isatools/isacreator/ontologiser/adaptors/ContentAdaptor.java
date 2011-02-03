package org.isatools.isacreator.ontologiser.adaptors;

import org.isatools.isacreator.ontologiser.model.OntologisedResult;

import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/02/2011
 *         Time: 22:40
 */
public interface ContentAdaptor {

    /**
     * Mechanism to get the terms to be annotated
     *
     * @return @see Set<String>
     */
    public Set<String> getTerms();

    /**
     * Will be the mechanism to replace the content
     *
     * @param result @see Set<OntologisedResult>
     */
    public void replaceTerms(Set<OntologisedResult> result);
}
