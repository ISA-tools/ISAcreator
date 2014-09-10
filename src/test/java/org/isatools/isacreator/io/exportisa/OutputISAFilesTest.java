package org.isatools.isacreator.io.exportisa;

import org.apache.log4j.Logger;
import org.isatools.isacreator.io.CommonTestIO;
import org.isatools.isacreator.io.importisa.ISAtabFilesImporter;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.utils.PropertyFileIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 14/11/2012
 * Time: 23:03
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OutputISAFilesTest implements CommonTestIO {

    private String configDir = null;
    private static Logger log = Logger.getLogger(OutputISAFilesTest.class);


    private ISAtabFilesImporter importer = null;
    private OutputISAFiles exporter = null;
    private String isatabParentDir = null;

    @Before
    public void setUp() {
        String baseDir = System.getProperty("basedir");
        ISAcreatorProperties.setProperties(PropertyFileIO.DEFAULT_CONFIGS_SETTINGS_PROPERTIES);
        if ( baseDir == null )
        {
            try{
                baseDir = new File( "." ).getCanonicalPath();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        configDir = baseDir + DEFAULT_CONFIG_DIR;

        importer = new ISAtabFilesImporter(configDir);
        exporter = new OutputISAFiles();
        isatabParentDir = baseDir + "/src/test/resources/test-data/BII-I-1";
        log.debug("isatabParentDir=" + isatabParentDir);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void saveISAFilesTest(){
        importer.importFile(isatabParentDir);
        System.out.println("isatabParentDir="+isatabParentDir);

        Investigation inv = importer.getInvestigation();

        System.out.println("inv="+inv);

        assert(inv!=null);

        //if import worked ok, there should not be error messages
        System.out.println(importer.getMessagesAsString());

        assert(importer.getMessages().size()==0);

        System.out.println("investigation reference"+inv.getReference());

        exporter.saveISAFiles(false, inv);
    }
}
