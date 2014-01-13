package org.isatools.isacreator.io.importisa;

import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.Assert.assertTrue;

/**
 * Test class for ISAtabFilesImporter
 * <p/>
 * It assumes that package.sh was run before running this test, as this script downloads the configuration files into ISAcreator/Configurations (Note: wget must be installed for this to work).
 * <p/>
 * Created by ISA Team
 * <p/>
 * Date: 04/07/2012
 * Time: 05:54
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporterTest {

    private String configDir = null;
    private static Logger log = Logger.getLogger(ISAtabFilesImporterTest.class);


    private ISAtabFilesImporter importer = null;
    private String isatabParentDir = null;

    @Before
    public void setUp() {
        String baseDir = System.getProperty("basedir");

        if (baseDir == null) {
            try {
                baseDir = new File(".").getCanonicalPath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        configDir = baseDir + "/Configurations/isaconfig-default_v2013-02-13/";

        log.debug("configDir=" + configDir);
        importer = new ISAtabFilesImporter(configDir);
        isatabParentDir = baseDir + "/src/test/resources/test-data/BII-I-1";
        log.debug("isatabParentDir=" + isatabParentDir);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void importFileTest() {
        importer.importFile(isatabParentDir);
        Investigation inv = importer.getInvestigation();

        assert (inv != null);

        for (ISAFileErrorReport report : importer.getMessages()) {
            System.out.println(report.getFileName());
            for (ErrorMessage message : report.getMessages()) {
                System.out.println(message.getErrorLevel().toString() + " > " + message.getMessage());
            }
        }

        //if import worked ok, there should not be error messages
        assert (importer.getMessages().size() == 0);

        System.out.println("ontologies used=" + OntologyManager.getOntologiesUsed());
        System.out.println("ontology description=" + OntologyManager.getOntologyDescription("OBI"));
        //System.out.println("ontology selection history=" + OntologyManager.getOntologySelectionHistory());
        System.out.println("ontology selection history size=" + OntologyManager.getOntologySelectionHistorySize());
        System.out.println("ontology term=" + OntologyManager.getOntologyTerm("OBI:metabolite profiling"));

        assertTrue("Oh no, I didnt' get the expected number of studies :(", inv.getStudies().size() == 2);

    }


}
