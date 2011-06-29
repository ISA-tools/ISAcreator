package org.isatools.isacreator.configuration;

public enum AssayTypes {

    GEL_ELECTROPHORESIS("gel_electrophoresis_assay"), GENERIC("generic_assay"), MASS_SPEC("ms_spec_assay"), TRANSCRIPTOMICS("transcriptomics_assay");
    private String type;

    AssayTypes(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }


    public static String[] asStringArray() {
        String[] values = new String[values().length];
        for(int valueIndex = 0; valueIndex < values().length; valueIndex++) {
            values[valueIndex] = values()[valueIndex].toString();
        }

        return values;
    }
}
