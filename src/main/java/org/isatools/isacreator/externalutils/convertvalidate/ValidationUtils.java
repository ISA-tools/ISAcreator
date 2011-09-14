package org.isatools.isacreator.externalutils.convertvalidate;

import org.apache.log4j.spi.LoggingEvent;
import org.isatools.errorreporter.model.FileType;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import uk.ac.ebi.utils.collections.Pair;

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

            ndc = ndc.substring(ndc.lastIndexOf("file:")).replaceAll("file:|]|\\[sect:ASSAYS", "").trim();


            return ndc;
        }

        return null;

    }

    public static Pair<Assay, FileType> resolveFileTypeFromFileName(String fileName, Investigation currentInvestigation) {

        for (String studyId : currentInvestigation.getStudies().keySet()) {
            Assay assay;
            if ((assay = currentInvestigation.getStudies().get(studyId).getAssays().get(fileName)) != null) {
                if (assay.getTechnologyType().contains(FileType.MICROARRAY.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.MICROARRAY);
                } else if (assay.getTechnologyType().contains(FileType.FLOW_CYTOMETRY.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.FLOW_CYTOMETRY);
                } else if (assay.getTechnologyType().contains(FileType.MASS_SPECTROMETRY.getType()) ||
                        assay.getTechnologyType().contains(FileType.NMR.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.MASS_SPECTROMETRY);
                } else if (assay.getTechnologyType().contains(FileType.SEQUENCING.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.SEQUENCING);
                } else if (assay.getTechnologyType().contains(FileType.GEL_ELECTROPHORESIS.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.GEL_ELECTROPHORESIS);
                } else if (assay.getTechnologyType().contains(FileType.HEMATOLOGY.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.HEMATOLOGY);
                } else if (assay.getTechnologyType().contains(FileType.CLINICAL_CHEMISTRY.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.CLINICAL_CHEMISTRY);
                } else if (assay.getTechnologyType().contains(FileType.HISTOLOGY.getType())) {
                    return new Pair<Assay, FileType>(assay, FileType.HISTOLOGY);
                } else {
                    return new Pair<Assay, FileType>(assay, FileType.INVESTIGATION);
                }
            }
        }

        return new Pair<Assay, FileType>(null, FileType.INVESTIGATION);
    }
}
