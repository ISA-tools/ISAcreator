/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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

package org.isatools.isacreator.formatmappingutility.io;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlException;
import org.isatools.isacreator.formatmappingutility.logic.MappingTypes;
import org.isatools.isacreator.formatmappingutility.ui.MappingField;
import org.isatools.isacreator.mapping.schema.*;

import java.io.File;
import java.io.IOException;

public class MappingXMLLoader {
    private static final Logger log = Logger.getLogger(MappingXMLLoader.class.getName());
    private File savedMappingsFile;

    public MappingXMLLoader(String savedMappingsFile) {
        this.savedMappingsFile = new File(savedMappingsFile);
    }

    public SavedMappings loadMappings() throws XmlException, IOException {

        IsaFieldMappingsDocument isaFieldMappings;

        if (savedMappingsFile.exists()) {

            isaFieldMappings = IsaFieldMappingsDocument.Factory.parse(savedMappingsFile);
            return processISAFields(isaFieldMappings);

        } else {
            log.info("file not found...");
        }

        return null;
    }

    /**
     * Utility method to add fields to an ISAFieldMapping object
     *
     * @param fieldsToMapTo  - Array of MappingField objects to be mapped to
     * @param type           - type of mapping, e.g. date, unit, field or provider from ISAFieldMapping public static integers.
     * @param currentMapping - current mapping being dealt with.
     */
    private void processFieldMappings(MappedField[] fieldsToMapTo, int type, ISAFieldMapping currentMapping) {
        for (MappedField mf : fieldsToMapTo) {
            currentMapping.addToISAField(new ISAField(MappingTypes.resolveTypeFromString(mf.getType().toString()), mf.getValue()), type);
        }
    }

    private SavedMappings processISAFields(IsaFieldMappingsDocument isaDocument) {
        SavedMappings mappings = new SavedMappings();

        IsaFieldMappings isaFields = isaDocument.getIsaFieldMappings();

        for (IsaField field : isaFields.getIsaFieldArray()) {

            MappingField isaFieldBeingMappedTo = new MappingField(field.getColumnName());

            ISAFieldMapping currentMapping = new ISAFieldMapping();
            for (FieldMapping fieldMapping : field.getFieldMappingArray()) {
                processFieldMappings(fieldMapping.getMappedFieldArray(),
                        ISAFieldMapping.FIELD,
                        currentMapping);
            }

            if (field.sizeOfDateMappingArray() > 0) {
                for (DateMapping dateMapping : field.getDateMappingArray()) {
                    processFieldMappings(dateMapping.getMappedFieldArray(),
                            ISAFieldMapping.DATE,
                            currentMapping);
                }
            }

            if (field.sizeOfProviderMappingArray() > 0) {
                for (ProviderMapping providerMapping : field.getProviderMappingArray()) {
                    processFieldMappings(providerMapping.getMappedFieldArray(),
                            ISAFieldMapping.PERFORMER,
                            currentMapping);
                }
            }

            if (field.sizeOfUnitMappingArray() > 0) {
                for (UnitMapping unitMapping : field.getUnitMappingArray()) {
                    processFieldMappings(unitMapping.getMappedFieldArray(),
                            ISAFieldMapping.UNIT,
                            currentMapping);
                }
            }

            mappings.addMapping(isaFieldBeingMappedTo, currentMapping);
        }

        return mappings;
    }
}
