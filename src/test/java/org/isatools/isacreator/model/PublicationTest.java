package org.isatools.isacreator.model;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 31/10/2012
 * Time: 11:13
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class PublicationTest {

    Publication pub1 = null;
    Publication pub2 = null;

    @Before
    public void setUp() {

    }

    @Test
    public void equality1Test(){
        pub1 = new InvestigationPublication("17439666", "", "authors", "title", "", "", "");
        pub2 = new StudyPublication("17439666", "","","title", "", "","");
        assert(pub1.equals(pub2));
        assert(pub1.hashCode()==pub2.hashCode());
    }

    @Test
    public void equality2Test(){
        pub1 = new InvestigationPublication("17439666", "doi:10.1186/jbiol54", "authors", "title", "", "", "");
        pub2 = new StudyPublication("17439666", "","","", "", "","");
        assert(!pub1.equals(pub2));
        assert(pub1.hashCode()!=pub2.hashCode());
    }

    @Test
    public void equality3Test(){
        pub1 = new InvestigationPublication("", "doi:10.1186/jbiol54", "authors", "title", "", "", "");
        pub2 = new StudyPublication("17439666", "doi:10.1186/jbiol54","","title", "", "","");
        assert(pub1.equals(pub2));
        assert(pub1.hashCode()==pub2.hashCode());
    }

    @Test
    public void equality4Test(){
        pub1 = new InvestigationPublication("", "doi:10.1186/jbiol54", "authors", "title", "", "", "");
        pub2 = new StudyPublication("17439666", "","","", "", "","");
        assert(!pub1.equals(pub2));
        assert(pub1.hashCode()!=pub2.hashCode());
    }

}
