package org.isatools.isacreator.configuration.io;

import org.junit.Test;

import java.util.logging.Logger;

import static org.junit.Assert.assertTrue;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 04/07/2011
 *         Time: 15:54
 */
public class ConfigXMLTest {

    private static Logger log = Logger.getLogger(ConfigXMLTest.class.getName());

    @Test
    public void testLoadConfiguration() {
        log.info("______Testing testLoadConfiguration()________");
        ConfigXMLParser parser = new ConfigXMLParser("Configurations/isaconfig-default_v2011-02-18/");

        parser.loadConfiguration();

        log.info("We loaded " + parser.getTables().size() + " configurations");

        assertTrue("The parser has loaded no files", parser.getMappings().size() > 0);

    }
}
