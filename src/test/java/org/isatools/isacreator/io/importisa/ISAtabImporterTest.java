package org.isatools.isacreator.io.importisa;

import java.io.File;

import java.net.URL;

import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.io.IOUtils;
import org.isatools.isacreator.model.*;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test class for ISAtabImporter
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
public class ISAtabImporterTest {

    private ISAtabImporter importer = null;
    private String configDir = null;
    private String isatabParentDir = null;

    @Before
    public void setUp() {
    	String baseDir = System.getProperty("basedir");
    	configDir = baseDir + "/Configurations/isaconfig-default_v2011-02-18/";
        isatabParentDir = baseDir + "/src/test/resources/test-data/BII-I-1";
        importer = new ISAtabImporter(configDir);
    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void importFileTest(){
        importer.importFile(isatabParentDir);
        Investigation inv = importer.getInvestigation();
        assert(inv!=null);
        assert(importer.getMessages().size()==0);
    }


}
