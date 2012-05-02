package org.isatools.isacreator.ontologymanager.bioportal.io;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/05/2012
 *         Time: 11:13
 */
public class AcceptedOntology {

    private String ontologyID;
    private String ontologyAbbreviation;

    AcceptedOntology(String ontologyID, String ontologyAbbreviation) {
        this.ontologyID = ontologyID;
        this.ontologyAbbreviation = ontologyAbbreviation;
    }

    @Override
    public String toString() {
        return ontologyID;
    }

    public String getOntologyAbbreviation() {
        return ontologyAbbreviation;
    }

    public String getOntologyID() {
        return ontologyID;
    }

}
