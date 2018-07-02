package org.isatools.isacreator.publicationlocator;

import org.junit.Test;
//import uk.ac.ebi.cdb.client.QueryException_Exception;

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

    @Test
    public void testCiteXploreTitle() {
        runTest(SearchOption.TITLE, "obi");
    }

    @Test
    public void testCiteXploreFull() {
        runTest(SearchOption.FULL_TEXT, "ontology for biomedical investigations");
    }

    private void runTest(SearchOption searchOption, String query) {
        CiteExploreClient publicationSearcher = new CiteExploreClient();

//        try {
//            List<CiteExploreResult> result = publicationSearcher.searchForPublication(searchOption, query);
//            printResultBeans(result);
//        } catch (QueryException_Exception qex) {
//            System.out.printf("Caught QueryException_Exception: %s\n", qex.getFaultInfo().getMessage());
//        } catch (NoPublicationFoundException e) {
//            System.out.println("No publication found");
//        }
    }

    private void printResultBeans(Collection<CiteExploreResult> resultBeans) {
        for (CiteExploreResult resultBean : resultBeans) {

            System.out.println("getTitle() = " + resultBean.getTitle());
            System.out.println("resultBean.getId() = " + resultBean.getId());
            System.out.println("resultBean = " + resultBean.getAuthors());
            System.out.println("getAbstractText() = " + resultBean.getAbstractText());
            System.out.println("getAffiliation() = " + resultBean.getAffiliation());
            System.out.println("grants = " + resultBean.getGrants());
            System.out.println();
        }
    }
}
