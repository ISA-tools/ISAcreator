/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */
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
