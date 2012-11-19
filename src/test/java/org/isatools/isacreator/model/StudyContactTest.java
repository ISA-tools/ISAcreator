package org.isatools.isacreator.model;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 30/10/2012
 * Time: 12:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class StudyContactTest {

    Contact contact1;
    Contact contact2;

    @Before
    public void setUp() {

    }

    @Test
    public void equality1Test(){
        contact1 = new StudyContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        assert(contact1.equals(contact2));
    }

    @Test
    public void equality2Test(){
        contact1 = new StudyContact("Castrillo","Juan", "", "j.castrillo@gmail.com", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "j.castrillo@gmail.com", "", "", "", "", "");
        assert(contact1.equals(contact2));
    }

    @Test
    public void equality3Test(){
        contact1 = new StudyContact("Castrillo","Juan", "", "", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        assert(!contact1.equals(contact2));
    }

    @Test
    public void equality4Test(){
        contact1 = new StudyContact("","", "", "j.castrillo@gmail.com", "", "", "", "", "");
        contact2 = new StudyContact("","", "", "j.castrillo@gmail.com", "", "", "", "", "");
        assert(contact1.equals(contact2));
    }
}
