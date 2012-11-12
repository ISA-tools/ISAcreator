package org.isatools.isacreator.io.importisa;

import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.spreadsheet.model.ReferenceData;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.log4j.Logger;

import org.isatools.isacreator.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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

        if ( baseDir == null )
        {
            try{
            baseDir = new File( "." ).getCanonicalPath();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

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
        //assert(importer.getMessages().size()==0);

        List<ISAFileErrorReport> errors = importer.getMessages();
        for(ISAFileErrorReport error: errors){
            System.out.println(error.getProblemSummary());
            List<ErrorMessage> messages = error.getMessages();
            for(ErrorMessage message: messages){
                System.out.println(message.getMessage());
            }
        }

        System.out.println("ontologies used="+OntologyManager.getOntologiesUsed());
        System.out.println("ontology description="+OntologyManager.getOntologyDescription("OBI"));
        System.out.println("ontology selection history=" + OntologyManager.getOntologySelectionHistory());
        System.out.println("ontology selection history size=" + OntologyManager.getOntologySelectionHistory().keySet().size());
        System.out.println("ontology term=" + OntologyManager.getOntologyTerm("OBI:metabolite profiling"));

        Map<String, Study> studyMap = inv.getStudies();
        for(String studyId: studyMap.keySet()){
            Study study = studyMap.get(studyId);
            TableReferenceObject tableReferenceObject = study.getStudySample().getTableReferenceObject();
            FieldObject fieldObject = tableReferenceObject.getFieldByName("Characteristics[organism]");

            System.out.println("recommended ontology source for fieldobject=" +fieldObject.getRecommmendedOntologySource());
            System.out.println("fieldobject name=" +fieldObject.getFieldName());

            ReferenceData data = tableReferenceObject.getReferenceData();

            for(List<String> row : data.getData()) {
                System.out.println("row="+row);
                for(String column : row) {
                    System.out.println("column="+column);
                    if(OntologyManager.getOntologySelectionHistory().containsKey(column)) {
                        System.out.println("source accession="+OntologyManager.getOntologySelectionHistory().get(column).getOntologySourceAccession());
                        System.out.println("purl="+OntologyManager.getOntologySelectionHistory().get(column).getOntologyPurl());
                    }
                }
                System.out.println();
            }
            System.out.println();

        }

    }


}
