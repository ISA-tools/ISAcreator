package org.isatools.isacreator.validate.ui;

import org.apache.log4j.spi.LoggingEvent;
import org.isatools.errorreporter.model.ISAFileType;
import org.isatools.isacreator.assayselection.AssayType;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/08/2011
 *         Time: 15:50
 */
public class ValidationUtils {

    public static String extractFileInformation(LoggingEvent loggingEvent) {

        String ndc = loggingEvent.getNDC();

        if (ndc != null) {
            System.out.println(ndc);

            return ndc.substring(ndc.indexOf("file:")).replaceAll("file:|]", "").trim();
        }

        return null;

    }

    public static ISAFileType resolveFileTypeFromFileName(String fileName, Investigation currentInvestigation) {

        for (String studyId : currentInvestigation.getStudies().keySet()) {
            Assay assay;
            if ((assay = currentInvestigation.getStudies().get(studyId).getAssays().get(fileName)) != null) {
                if (assay.getTechnologyType().contains(AssayType.MICROARRAY.getType())) {
                    return ISAFileType.MICROARRAY;
                } else if (assay.getTechnologyType().contains(AssayType.FLOW_CYTOMETRY.getType())) {
                    return ISAFileType.FLOW_CYT;
                } else if (assay.getTechnologyType().contains(AssayType.MASS_SPECTROMETRY.getType()) ||
                        assay.getTechnologyType().contains(AssayType.NMR.getType())) {
                    return ISAFileType.MASS_SPECTROMETRY;
                } else if (assay.getTechnologyType().contains(AssayType.SEQUENCING.getType())) {
                    return ISAFileType.SEQUENCING;
                } else if (assay.getTechnologyType().contains(AssayType.GEL_ELECTROPHORESIS.getType())) {
                    return ISAFileType.GEL_ELECTROPHORESIS;
                } else if (assay.getTechnologyType().contains(AssayType.HEMATOLOGY.getType())
                        || assay.getTechnologyType().contains(AssayType.HISTOLOGY.getType())
                        || assay.getTechnologyType().contains(AssayType.CLINICAL_CHEMISTRY.getType())) {
                    return ISAFileType.STUDY_SAMPLE;
                } else {
                    return ISAFileType.INVESTIGATION;
                }
            }
        }

        return ISAFileType.INVESTIGATION;
    }
}
