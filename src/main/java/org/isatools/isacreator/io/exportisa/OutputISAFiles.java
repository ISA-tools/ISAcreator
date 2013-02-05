package org.isatools.isacreator.io.exportisa;

import org.isatools.isacreator.api.utils.SpreadsheetUtils;
import org.isatools.isacreator.io.exportisa.exportadaptors.ISASectionExportAdaptor;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;
import org.isatools.isacreator.model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         Date: 01/07/2012
 *         Time: 14:13
 */
public class OutputISAFiles extends ISAFileOutput {

    @Override
    public void saveISAFiles(boolean removeEmptyColumns, Investigation investigation) {
        File file = new File(investigation.getReference());

        PrintStream investigationFilePrintStream;
        try {
            investigationFilePrintStream = new PrintStream(file);

            printInvestigation(investigation, investigationFilePrintStream);


            File fileToSave;

            for (Study study : investigation.getStudies().values()) {
                printStudy(investigationFilePrintStream, study);

                fileToSave = new File(file.getParentFile().getPath() +
                        File.separator + study.getStudySampleFileIdentifier());

                // output study sample content
                StringBuilder studySampleContent = SpreadsheetUtils.outputAssayAsString(study.getStudySample());
                outputFile(fileToSave, studySampleContent);

                for (String assayRef : study.getAssays().keySet()) {
                    Assay assay = study.getAssays().get(assayRef);

                    fileToSave = new File(file.getParentFile().getPath() +
                            File.separator + assay.getAssayReference());

                    // output study sample content
                    StringBuilder assayContent = SpreadsheetUtils.outputAssayAsString(assay);
                    outputFile(fileToSave, assayContent);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
