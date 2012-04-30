package org.isatools.isacreator.model;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/01/2012
 *         Time: 16:57
 */
public enum GeneralFieldTypes {
    CHARACTERISTIC("Characteristics", "[CH]"), FACTOR_VALUE("Factor Value", "[FV]"), COMMENT("Comment", "[C]"), PROTOCOL_REF("Protocol REF"),
    PARAMETER_VALUE("Parameter Value", "[PV]"), UNIT("Unit"), SAMPLE_NAME("Sample Name"), SOURCE_NAME("Source Name"),
    TERM_SOURCE_REF("Term Source REF"), TERM_SOURCE_ACCESSION("Term Source Accession");

    public String name;
    public String abbreviation;

    GeneralFieldTypes(String name) {
        this(name, name);
    }

    GeneralFieldTypes(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String toString() {
        return name;
    }
}
