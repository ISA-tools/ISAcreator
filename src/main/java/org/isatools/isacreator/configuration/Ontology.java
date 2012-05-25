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

package org.isatools.isacreator.configuration;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.Serializable;

/**
 * @author Eamonn Maguire
 * @date Jul 17, 2009
 */


public class Ontology implements Serializable {

    private String ontologyID;
    private String ontologyVersion;
    private String ontologyDisplayLabel;
    private String ontologyAbbreviation;
    private boolean isFoundry;
    private OntologyFormats format;
    private DefaultMutableTreeNode view = null;
    private OntologyBranch subsectionToQuery;
    private String contactName;
    private String contactEmail;
    private String homepage;
    private Boolean isView;

    public Ontology() {
    }

    public Ontology(String ontologyID, String ontologyVersion, String ontologyAbbreviation, String ontologyDisplayLabel) {
        this(ontologyID, ontologyVersion, ontologyAbbreviation, ontologyDisplayLabel, false, OntologyFormats.OBO);
    }

    public Ontology(String ontologyID, String ontologyVersion, String ontologyAbbreviation, String ontologyDisplayLabel, boolean foundry, OntologyFormats format) {
        this.ontologyAbbreviation = ontologyAbbreviation;
        this.ontologyDisplayLabel = ontologyDisplayLabel;
        this.ontologyID = ontologyID;
        this.ontologyVersion = ontologyVersion;
        this.isFoundry = foundry;
        this.format = format;
    }

    public DefaultMutableTreeNode getView() {
        return view;
    }

    public void setView(DefaultMutableTreeNode view) {
        this.view = view;
    }

    public String getOntologyAbbreviation() {
        return ontologyAbbreviation == null ? "" : ontologyAbbreviation;
    }

    public String getOntologyDisplayLabel() {
        return ontologyDisplayLabel;
    }

    public String getOntologyID() {
        return ontologyID;
    }

    public String getOntologyVersion() {
        return ontologyVersion == null ? "" : ontologyVersion;
    }

    @Override
    public String toString() {
        return ontologyDisplayLabel;
    }

    public void setOntologyAbbreviation(String ontologyAbbreviation) {
        this.ontologyAbbreviation = ontologyAbbreviation;
    }

    public void setOntologyDisplayLabel(String ontologyDisplayLabel) {
        this.ontologyDisplayLabel = ontologyDisplayLabel;
    }

    public void setOntologyID(String ontologyID) {
        this.ontologyID = ontologyID;
    }

    public void setOntologyVersion(String ontologyVersion) {
        this.ontologyVersion = ontologyVersion;
    }

    public boolean isFoundry() {
        return isFoundry;
    }

    public void setFoundry(boolean foundry) {
        isFoundry = foundry;
    }

    public OntologyFormats getFormat() {
        return format;
    }

    public OntologyBranch getSubsectionToQuery() {
        return subsectionToQuery;
    }

    public void setSubsectionToQuery(OntologyBranch subsectionToQuery) {
        this.subsectionToQuery = subsectionToQuery;
    }

    public void setFormat(OntologyFormats format) {
        this.format = format;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setHomePage(String homepage) {
        this.homepage = homepage;
    }

    public void setIsView(boolean isView) {
        this.isView = isView;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getHomepage() {
        return homepage;
    }
}
