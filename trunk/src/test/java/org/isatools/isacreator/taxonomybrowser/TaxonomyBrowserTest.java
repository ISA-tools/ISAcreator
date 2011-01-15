package org.isatools.isacreator.taxonomybrowser;

import gov.nih.nlm.ncbi.www.soap.eutils.TaxonType;
import org.junit.Test;

import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 14/01/2011
 *         Time: 15:36
 */
public class TaxonomyBrowserTest {


    @Test
    public void testTaxonomyBrowser() {
        TaxonomyBrowser tb = new TaxonomyBrowser();

        List<TaxonType> taxa = tb.getTaxonomyInformation("9685,522328");

        for (TaxonType taxon : taxa) {
            System.out.println(taxon.getScientificName() + ": " +
                    taxon.getDivision() + " (" + taxon.getRank() + ")");
        }
    }
}
