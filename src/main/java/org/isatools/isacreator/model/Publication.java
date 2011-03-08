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

package org.isatools.isacreator.model;

import org.isatools.isacreator.gui.StudySubData;


/**
 * Publication representation in model
 *
 * @author Eamonn Maguire
 */
public class Publication extends ISASection implements StudySubData {

    public static final String PUBMED_ID = "Study PubMed ID";
    public static final String PUBLICATION_DOI = "Study Publication DOI";
    public static final String PUBLICATION_AUTHOR_LIST = "Study Publication Author list";
    public static final String PUBLICATION_TITLE = "Study Publication Title";
    public static final String PUBLICATION_STATUS = "Study Publication Status";
    public static final String PUBLICATION_STATUS_TERM_ACC = "Study Publication Status Term Accession Number";
    public static final String PUBLICATION_STATUS_SOURCE_REF = "Study Publication Status Term Source REF";
    public static final String ABSTRACT_TEXT = "Abstract Text";

    /**
     * Publication Object
     *
     * @param pubmedId              - PubMedID uniquely identifying the Publication (if available!)
     * @param publicationDOI        - Publication Database Object Identifier
     * @param publicationAuthorList - List of authors who contributed to the paper.
     * @param publicationTitle      - Title given to the Publication
     * @param publicationStatus     - Status of the Publication (e.g. Submitted)
     */
    public Publication(String pubmedId, String publicationDOI, String publicationAuthorList, String publicationTitle, String publicationStatus) {
        super();
        fieldValues.put(PUBMED_ID, pubmedId);
        fieldValues.put(PUBLICATION_DOI, publicationDOI);
        fieldValues.put(PUBLICATION_AUTHOR_LIST, publicationAuthorList);
        fieldValues.put(PUBLICATION_TITLE, publicationTitle);
        fieldValues.put(PUBLICATION_STATUS, publicationStatus);
    }

    /**
     * Publication Object
     *
     * @param pubmedId                       - PubMedID uniquely identifying the Publication (if available!)
     * @param publicationDOI                 - Publication Database Object Identifier
     * @param publicationAuthorList          - List of authors who contributed to the paper.
     * @param publicationTitle               - Title given to the Publication
     * @param publicationStatus              - Status of the Publication (e.g. Submitted)
     * @param publicationStatusTermAccession - Accession (e.g. 000123) for the Ontology term used for the publicationStatus field
     * @param publicationStatusSourceRef     - Source REF (e.g. OBI) for the Ontology term used for the publicationStatus field
     */
    public Publication(String pubmedId, String publicationDOI, String publicationAuthorList, String publicationTitle, String publicationStatus, String publicationStatusTermAccession, String publicationStatusSourceRef) {
        super();
        fieldValues.put(PUBMED_ID, pubmedId);
        fieldValues.put(PUBLICATION_DOI, publicationDOI);
        fieldValues.put(PUBLICATION_AUTHOR_LIST, publicationAuthorList);
        fieldValues.put(PUBLICATION_TITLE, publicationTitle);
        fieldValues.put(PUBLICATION_STATUS, publicationStatus);
        fieldValues.put(PUBLICATION_STATUS_TERM_ACC, publicationStatusTermAccession);
        fieldValues.put(PUBLICATION_STATUS_SOURCE_REF, publicationStatusSourceRef);
    }

    public String getPubmedId() {
        return fieldValues.get(PUBMED_ID);
    }

    public String getPublicationDOI() {
        return fieldValues.get(PUBLICATION_DOI);
    }

    public String getPublicationAuthorList() {
        return fieldValues.get(PUBLICATION_AUTHOR_LIST);
    }

    public String getPublicationTitle() {
        return fieldValues.get(PUBLICATION_TITLE);
    }

    public String getPublicationStatus() {
        return fieldValues.get(PUBLICATION_STATUS);
    }

    public String getPublicationTermAcc() {
        return fieldValues.get(PUBLICATION_STATUS_TERM_ACC);
    }

    public String getPublicationSourceRef() {
        return fieldValues.get(PUBLICATION_STATUS_SOURCE_REF);
    }

    public void setPublicationTermAcc(String publicationTermAcc) {
        fieldValues.put(PUBLICATION_STATUS_TERM_ACC, publicationTermAcc);
    }

    public void setPublicationSourceRef(String publicationSourceRef) {
        fieldValues.put(PUBLICATION_STATUS_SOURCE_REF, publicationSourceRef);
    }

    public String getIdentifier() {
        return getPubmedId();
    }

    public void setPublicationStatus(String publicationStatus) {
        fieldValues.put(PUBLICATION_STATUS, publicationStatus);
    }

    public String getAbstractText() {
        return fieldValues.get(ABSTRACT_TEXT) == null ? "" : fieldValues.get(ABSTRACT_TEXT);
    }

    public void setAbstractText(String abstractText) {
        fieldValues.put(ABSTRACT_TEXT, abstractText);
    }

    /**
     * Since there is no way to determine if a publication is valid or not, so we just return if there is information contained in it :o)
     *
     * @return boolean to determine if there is information contained in it or not.
     */
    public boolean hasInformation() {

        String publicationDOI = getPublicationDOI() == null ? "" : getPublicationDOI().trim();
        String pubmedId = getPubmedId() == null ? "" : getPubmedId().trim();
        String publicationTitle = getPublicationTitle() == null ? "" : getPublicationTitle().trim();
        String publicationAuthorList = getPublicationAuthorList() == null ? "" : getPublicationAuthorList().trim();

        return !((pubmedId.equals("") || publicationDOI.equals("")) &&
                publicationAuthorList.equals("") &&
                publicationTitle.equals(""));
    }
}
