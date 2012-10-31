package org.isatools.isacreator.io.importisa;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.log4j.Logger;

import org.isatools.isacreator.model.*;

/**
 * 
 * Test class for ISAtabFilesImporter
 * 
 * It assumes that package.sh was run before running this test, as this script downloads the configuration files into ISAcreator/Configurations (Note: wget must be installed for this to work).
 * 
 * Created by ISA Team
 *
 * Date: 04/07/2012
 * Time: 05:54
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporterTest {

    private static Logger log = Logger.getLogger(ISAtabFilesImporterTest.class);


    private ISAtabFilesImporter importer = null;
    private String configDir = null;
    private String isatabParentDir = null;

    @Before
    public void setUp() {
    	String baseDir = System.getProperty("basedir");
    	configDir = baseDir + "/Configurations/isaconfig-default_v2011-02-18/";
    	log.debug("configDir=" + configDir);
        importer = new ISAtabFilesImporter(configDir);
        isatabParentDir = baseDir + "/src/test/resources/test-data/BII-I-1";
        log.debug("isatabParentDir=" + isatabParentDir);
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void importFileTest(){
        importer.importFile(isatabParentDir);
        Investigation inv = importer.getInvestigation();

        assert(inv!=null);

        //if import worked ok, there should not be error messages
        assert(importer.getMessages().size()==0);
    }


}
