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

package org.isatools.isacreator.publicationlocator;

import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.model.InvestigationPublication;
import org.isatools.isacreator.model.Publication;
import org.isatools.isacreator.model.StudyPublication;
//import uk.ac.ebi.cdb.client.*;

import javax.xml.ws.WebServiceRef;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CiteExploreClient {


//    @WebServiceRef(wsdlLocation = "http://www.ebi.ac.uk/europepmc/webservices/soap?wsdl")
//    private WSCitationImplService service;
//
//    public CiteExploreClient() {
//        service = new WSCitationImplService();
//    }
//
//    public Map<String, Publication> getPublication(SearchOption searchOption, String query, DataEntryForm parent) throws NoPublicationFoundException {
//        Map<String, Publication> publications = new HashMap<String, Publication>();
//        try {
//
//            List<CiteExploreResult> resultBeanCollection = searchForPublication(searchOption, query);
//
//            for (CiteExploreResult resultBean : resultBeanCollection) {
//
//                Publication pub;
//                if (parent instanceof StudyDataEntry) {
//                    pub = new StudyPublication(resultBean.getId(), resultBean.getDoi(), resultBean.getAuthors(), resultBean.getTitle(), "Published");
//                } else {
//                    pub = new InvestigationPublication(resultBean.getId(), resultBean.getDoi(), resultBean.getAuthors(), resultBean.getTitle(), "Published");
//                }
//                pub.setAbstractText(resultBean.getAbstractText());
//                publications.put(resultBean.getId(), pub);
//            }
//        } catch (QueryException_Exception qex) {
//            System.out.printf("Caught QueryException_Exception: %s\n", qex.getFaultInfo().getMessage());
//        }
//
//        return publications;
//    }
//
//    public List<CiteExploreResult> searchForPublication(SearchOption searchOption, String query) throws QueryException_Exception, NoPublicationFoundException {
//
//        String fullQueryString = searchOption.getQueryString(query);
//
//        return performQuery(searchOption, fullQueryString);
//    }
//
//    public List<CiteExploreResult> performQuery(SearchOption searchOption, String fullQueryString) throws QueryException_Exception, NoPublicationFoundException {
//        WSCitationImpl port = service.getWSCitationImplPort();
//
//        ResponseWrapper responseWrapper = port.searchPublications(fullQueryString,
//                "core",
//                "0",
//                "0",
//                "",
//                "false",
//                "isatools@googlgroups.com");
//        ResultList resultList = responseWrapper.getResultList();
//        if (resultList.getResult().size() > 0) {
//            return createResultList(resultList);
//        } else {
//            throw new NoPublicationFoundException(searchOption, fullQueryString);
//        }
//    }
//
//    private List<CiteExploreResult> createResultList(ResultList searchResults) {
//        List<CiteExploreResult> resultSet = new ArrayList<CiteExploreResult>();
//
//        List<Result> resultBeans = searchResults.getResult();
//
//        for (Result result : resultBeans) {
//            if (result.getTitle()==null)
//                continue;
//
//            CiteExploreResult citexploreRecord = new CiteExploreResult(result.getId(), result.getDoi(), result.getAuthorString(),
//                    result.getTitle().replaceAll("\\[|\\]", ""), result.getAbstractText(), result.getAffiliation());
//
//            resultSet.add(citexploreRecord);
//            if (result.getGrantsList() != null) {
//                String grants = "";
//                int grantCount = 0;
//                for (GrantInfo grantInfo : result.getGrantsList().getGrant()) {
//                    grants += (grantInfo.getAcronym() == null ? "" : grantInfo.getAcronym() + ", ") + grantInfo.getAgency() + " (" + grantInfo.getGrantId() + ")";
//                    grants += grantCount < result.getGrantsList().getGrant().size() - 1 ? ", " : "";
//                }
//                citexploreRecord.setGrants(grants);
//            }
//        }
//
//        return resultSet;
//    }
}
