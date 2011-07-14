package org.isatools.isacreator.publicationlocator;

import org.junit.Test;
import uk.ac.ebi.cdb.client.Citation;
import uk.ac.ebi.cdb.client.QueryException_Exception;
import uk.ac.ebi.cdb.client.ResultBean;

import java.util.Collection;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 14/07/2011
 *         Time: 13:45
 */
public class CiteXploreTest {

    @Test
    public void testCiteXplorePubMed() {
        runTest(SearchOption.PUBMED, "19815759");
    }

    @Test
    public void testCiteXploreDOI() {
        runTest(SearchOption.DOI, "10.1093/bioinformatics/btq415");
    }

    private void runTest(SearchOption searchOption, String query) {
        CiteExploreClient publicationSearcher = new CiteExploreClient();

        try {
            List<ResultBean> result = publicationSearcher.searchForPublication(searchOption, query);
            printResultBeans(result);
        } catch (QueryException_Exception qex) {
            System.out.printf("Caught QueryException_Exception: %s\n", qex.getFaultInfo().getMessage());
        } catch (NoPublicationFoundException e) {
            System.out.println("No publication found");
        }
    }

    private void printResultBeans(Collection<ResultBean> resultBeans) {
        for(ResultBean resultBean : resultBeans) {
            Citation citation = resultBean.getCitation();

            System.out.println("citation.getTitle() = " + citation.getTitle());
            System.out.println("citation.getAbstractText() = " + citation.getAbstractText());
            System.out.println("citation.getAffiliation() = " + citation.getAffiliation());
        }
    }
}
