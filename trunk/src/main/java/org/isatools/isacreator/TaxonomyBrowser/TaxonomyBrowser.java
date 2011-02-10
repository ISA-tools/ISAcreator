package org.isatools.isacreator.taxonomybrowser;

import gov.nih.nlm.ncbi.www.soap.eutils.EFetchRequest;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchResult;
import gov.nih.nlm.ncbi.www.soap.eutils.EFetchTaxonService;
import gov.nih.nlm.ncbi.www.soap.eutils.TaxonType;

import javax.xml.ws.WebServiceRef;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 01/02/2011
 *         Time: 16:50
 */
public class TaxonomyBrowser {

    @WebServiceRef(wsdlLocation = "http://eutils.ncbi.nlm.nih.gov/soap/v2.0/efetch_taxon.wsdl")
    private EFetchTaxonService service;

    public TaxonomyBrowser() {
        service = new EFetchTaxonService();
    }

    public List<TaxonType> getTaxonomyInformation(String taxaIds) {
        try {
            service = new EFetchTaxonService();
            // call NCBI EFetch utility
            EFetchRequest req = new EFetchRequest();
            req.setId(taxaIds);
            EFetchResult res = service.getEUtilsServiceSoap().runEFetch(req);

            return res.getTaxaSet().getTaxon();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return new ArrayList<TaxonType>();
    }
}
