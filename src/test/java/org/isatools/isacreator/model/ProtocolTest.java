package org.isatools.isacreator.model;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 02/11/2012
 * Time: 12:11
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ProtocolTest {

    @Before
    public void setUp() {

    }

    @Test
    public void test(){
         Protocol protocol = new Protocol("Extraction",
                 "Extraction",
                 "",
                 "",
                 "Extraction description",
                 "",
                 "",
                 "Post Extraction;Derivatization;",
                 ";",
                 "",
                 "",
                 "",
                 "",
                 ""
                 );

        String[] parameterNames = protocol.getProtocolParameterNames();
        for(String parameterName:parameterNames){
            System.out.println(parameterName);
        }

        assert(protocol.getProtocolParameterNames().length==2);
    }
}
