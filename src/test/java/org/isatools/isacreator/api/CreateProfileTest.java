package org.isatools.isacreator.api;

import org.isatools.isacreator.io.UserProfile;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 15:10
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class CreateProfileTest {

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void emptyPasswordTest(){
        char[] pass = new char[0];
        boolean empty = CreateProfile.emptyPassword(pass);
        assert(empty==true);
    }

    @Test
    public void nonEmptyPasswordTest(){
        char[] pass = new char[2];
        pass[0]='a'; pass[1]='b';
        boolean empty = CreateProfile.emptyPassword(pass);
        assert(empty==false);
    }

    @Test
    public void createSimpleProfileTest(){
        UserProfile up = CreateProfile.createProfile("myusername");
        assert(up!=null);
    }

}
