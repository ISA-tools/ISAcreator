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

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 09/03/2011
 *         Time: 11:54
 */
public class InvestigationPublication extends Publication {

    public static final String PUBMED_ID = "Investigation PubMed ID";
    public static final String PUBLICATION_DOI = "Investigation Publication DOI";
    public static final String PUBLICATION_AUTHOR_LIST = "Investigation Publication Author list";
    public static final String PUBLICATION_TITLE = "Investigation Publication Title";
    public static final String PUBLICATION_STATUS = "Investigation Publication Status";
    public static final String PUBLICATION_STATUS_TERM_ACC = "Investigation Publication Status Term Accession Number";
    public static final String PUBLICATION_STATUS_SOURCE_REF = "Investigation Publication Status Term Source REF";

    public InvestigationPublication() {
        super();
    }

    /**
     * Publication Object
     *
     * @param pubmedId              - PubMedID uniquely identifying the Publication (if available!)
     * @param publicationDOI        - Publication Database Object Identifier
     * @param publicationAuthorList - List of authors who contributed to the paper.
     * @param publicationTitle      - Title given to the Publication
     * @param publicationStatus     - Status of the Publication (e.g. Submitted)
     */
    public InvestigationPublication(String pubmedId, String publicationDOI, String publicationAuthorList, String publicationTitle, String publicationStatus) {
        this(pubmedId, publicationDOI, publicationAuthorList, publicationTitle, publicationStatus, "", "");
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
    public InvestigationPublication(String pubmedId, String publicationDOI, String publicationAuthorList, String publicationTitle, String publicationStatus, String publicationStatusTermAccession, String publicationStatusSourceRef) {
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
        return getValue(PUBMED_ID);
    }

    public String getPublicationDOI() {
        return getValue(PUBLICATION_DOI);
    }

    public String getPublicationAuthorList() {
        return getValue(PUBLICATION_AUTHOR_LIST);
    }

    public String getPublicationTitle() {
        return getValue(PUBLICATION_TITLE);
    }

    public String getPublicationStatus() {
        return getValue(PUBLICATION_STATUS);
    }

    public String getPublicationTermAcc() {
        return getValue(PUBLICATION_STATUS_TERM_ACC);
    }

    public String getPublicationSourceRef() {
        return getValue(PUBLICATION_STATUS_SOURCE_REF);
    }

    public void setPublicationTermAcc(String publicationTermAcc) {
        fieldValues.put(PUBLICATION_STATUS_TERM_ACC, publicationTermAcc);
    }

    public void setPublicationSourceRef(String publicationSourceRef) {
        fieldValues.put(PUBLICATION_STATUS_SOURCE_REF, publicationSourceRef);
    }

    public void setPublicationStatus(String publicationStatus) {
        fieldValues.put(PUBLICATION_STATUS, publicationStatus);
    }

}
