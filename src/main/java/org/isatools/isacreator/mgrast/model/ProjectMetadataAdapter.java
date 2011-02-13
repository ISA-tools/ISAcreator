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

package org.isatools.isacreator.mgrast.model;

import org.isatools.isacreator.mgrast.ui.ExtraMetaDataPane;
import org.isatools.isacreator.model.Contact;

/**
 * ProjectMetadataAdapter
 * Attaches on to an ExtraMetaDataPane UI component to extract it's information and prepare it for output.
 *
 * @author eamonnmaguire
 * @date Oct 1, 2010
 */


public class ProjectMetadataAdapter {
    private static final String ADMIN_CONTACT_PREFIX = "administrative-contact_PI_";
    private static final String TECHNICAL_CONTACT_PREFIX = "technical-contact_";


    private ExtraMetaDataPane metaDataPane;

    public ProjectMetadataAdapter(ExtraMetaDataPane metaDataPane) {

        this.metaDataPane = metaDataPane;
    }

    public StringBuilder getDataFromMetaDataPane() {
        StringBuilder output = new StringBuilder();

        output.append("project-description_internal_project_ID").append("\t").append(metaDataPane.getInternalProjectId()).append("\n");
        output.append("project-description_project_name").append("\t").append(metaDataPane.getProjectName()).append("\n");
        output.append("project-description_project_description").append("\t\"").append(metaDataPane.getProjectDescription()).append("\"").append("\n");
        output.append("external-ids_pubmed_id").append("\t").append(metaDataPane.getPubmedId()).append("\n");
        output.append("external-ids_project_id").append("\t").append(metaDataPane.getNCBIProjectId()).append("\n");
        output.append("external-ids_greengenes_study_id").append("\t").append(metaDataPane.getGreenegenesStudyId()).append("\n");

        buildContactSection(output, metaDataPane.getAdminContact(), ADMIN_CONTACT_PREFIX);
        buildContactSection(output, metaDataPane.getTechnicalContact(), TECHNICAL_CONTACT_PREFIX);

        return output;
    }

    private void buildContactSection(StringBuilder output, Contact contact, String sectionPrefix) {
        output.append(sectionPrefix).append("firstname").append("\t").append(contact.getFirstName()).append("\n");
        output.append(sectionPrefix).append("lastname").append("\t").append(contact.getLastName()).append("\n");
        output.append(sectionPrefix).append("email").append("\t").append(contact.getEmail()).append("\n");
        output.append(sectionPrefix).append("organization").append("\t").append(contact.getAffiliation()).append("\n");
        output.append(sectionPrefix).append("organization_address").append("\t").append(contact.getAddress()).append("\n");
        output.append(sectionPrefix).append("url").append("\t").append("").append("\n");

    }
}
