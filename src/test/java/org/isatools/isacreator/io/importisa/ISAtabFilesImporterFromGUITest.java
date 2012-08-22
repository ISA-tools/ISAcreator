package org.isatools.isacreator.io.importisa;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.io.importisa.ISAtabFilesImporterFromGUI;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ISA team
 *
 * Date: 11/07/2012
 * Time: 17:39
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporterFromGUITest {

    private String baseDir = null, isatabParentDir = null;
    private ISAcreator isacreator = null;
    private ISAtabFilesImporterFromGUI importer = null;

    @Before
    public void setUp() {
        baseDir = System.getProperty("basedir");
        isacreator = new ISAcreator(Mode.NORMAL_MODE, null, baseDir + "/Configurations/isaconfig-default_v2011-02-18/");
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
