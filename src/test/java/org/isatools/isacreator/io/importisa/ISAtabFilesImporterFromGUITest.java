package org.isatools.isacreator.io.importisa;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.io.importisa.ISAtabFilesImporterFromGUI;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.io.CommonTestIO;
import org.isatools.isacreator.launch.ISAcreatorGUIProperties;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by ISA team
 *
 * Date: 11/07/2012
 * Time: 17:39
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporterFromGUITest implements CommonTestIO {

    private String baseDir = null, isatabParentDir = null;
    private ISAcreator isacreator = null;
    private ISAtabFilesImporterFromGUI importer = null;

    @Before
    public void setUp() {
        ISAcreatorGUIProperties.setProperties();
        baseDir = System.getProperty("project.basedir");
        if ( baseDir == null )
        {
            try{
                baseDir = new File( "." ).getCanonicalPath();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        String configDir = baseDir + DEFAULT_CONFIG_DIR;

        isacreator = new ISAcreator(Mode.NORMAL_MODE, null, configDir);
        importer = new ISAtabFilesImporterFromGUI(isacreator);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void importFileTest(){
        isatabParentDir = baseDir + "/src/test/resources/test-data/BII-I-1";
        importer.importFile(isatabParentDir);
    }

}
