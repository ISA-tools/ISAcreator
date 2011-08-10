package org.isatools.isacreator.assayselection;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/08/2011
 *         Time: 18:59
 */
public enum AssayType {
    MICROARRAY("microarray"), NMR("nmr"), MASS_SPECTROMETRY("spectrometry"), SEQUENCING("sequencing"),
    GEL_ELECTROPHORESIS("electrophoresis"), FLOW_CYTOMETRY("flow"), HISTOLOGY("histology"),
    HEMATOLOGY("hematology"), CLINICAL_CHEMISTRY("chemistry");

    private String type;

    AssayType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static AssayType extractRelevantType(String type) {
        for (AssayType assayType : values()) {
            if (type.toLowerCase().contains(assayType.getType())) {
                return assayType;
            }
        }

        return null;
    }
}
