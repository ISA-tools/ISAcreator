package org.isatools.isacreator.validateconvert.ui;

/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
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

import org.apache.log4j.spi.LoggingEvent;
import org.isatools.errorreporter.model.FileType;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import uk.ac.ebi.utils.collections.Pair;


public class ValidationUtils {

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
