package org.isatools.isacreator.model;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 30/10/2012
 * Time: 12:41
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ContactTest {

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
        assert(contact1.hashCode()==contact2.hashCode());
    }

    @Test
    public void equality2Test(){
        contact1 = new StudyContact("Castrillo","Juan", "", "j.castrillo@gmail.com", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "j.castrillo@gmail.com", "", "", "", "", "");
        assert(contact1.equals(contact2));
        assert(contact1.hashCode()==contact2.hashCode());
    }

    @Test
    public void equality3Test(){
        contact1 = new StudyContact("Castrillo","Juan", "", "", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        assert(!contact1.equals(contact2));
        assert(contact1.hashCode()!=contact2.hashCode());
    }

    @Test
    public void equality4Test(){
        contact1 = new StudyContact("","", "", "j.castrillo@gmail.com", "", "", "", "", "");
        contact2 = new StudyContact("","", "", "j.castrillo@gmail.com", "", "", "", "", "");
        assert(contact1.equals(contact2));
        assert(contact1.hashCode()==contact2.hashCode());
    }

    @Test
    public void equality5Test(){
        contact1 = new InvestigationContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        assert(contact1.equals(contact2));
        assert(contact1.hashCode()==contact2.hashCode());
    }

    @Test
    public void equality6Test(){
        contact1 = new InvestigationContact("Castrillo","Juan", "", "j.castrillo@gmail.com", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "j.castrillo@gmail.com", "", "", "", "", "");
        assert(contact1.equals(contact2));
        assert(contact1.hashCode()==contact2.hashCode());
    }

    @Test
    public void equality7Test(){
        contact1 = new InvestigationContact("Castrillo","Juan", "", "", "", "", "", "", "");
        contact2 = new StudyContact("Castrillo","Juan", "M", "", "", "", "", "", "");
        assert(!contact1.equals(contact2));
        assert(contact1.hashCode()!=contact2.hashCode());
    }

    @Test
    public void equality8Test(){
        contact1 = new InvestigationContact("","", "", "j.castrillo@gmail.com", "", "", "", "", "");
        contact2 = new StudyContact("","", "", "j.castrillo@gmail.com", "", "", "", "", "");
        assert(contact1.equals(contact2));
        assert(contact1.hashCode()==contact2.hashCode());

        Map<Contact, String> map = new HashMap<Contact, String>();
        map.put(contact1,"Castrillo");
        String result = map.get(contact2);
        assert(result.equals("Castrillo"));

    }

}
