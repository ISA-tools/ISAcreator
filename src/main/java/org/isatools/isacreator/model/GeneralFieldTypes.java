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
    CHARACTERISTIC("Characteristics"), FACTOR_VALUE("Factor Value"), COMMENT("Comment"), PROTOCOL_REF("Protocol REF"),
    PARAMETER_VALUE("Parameter Value"), UNIT("Unit"), SAMPLE_NAME("Sample Name"), SOURCE_NAME("Source Name"),
    TERM_SOURCE_REF("Term Source REF"), TERM_SOURCE_ACCESSION("Term Source Accession");

    public String name;

    GeneralFieldTypes(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
