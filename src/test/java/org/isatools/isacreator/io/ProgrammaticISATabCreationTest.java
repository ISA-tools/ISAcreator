package org.isatools.isacreator.io;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.io.exportisa.ISAFileOutput;
import org.isatools.isacreator.io.exportisa.OutputISAFiles;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.*;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import org.isatools.isacreator.utils.PropertyFileIO;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 30/06/2012
 *         Time: 12:04
 */
public class ProgrammaticISATabCreationTest implements CommonTestIO {

    @Test
    public void createISATabProgrammatically1Test() {

    	String baseDir = System.getProperty("basedir");

        if ( baseDir == null )
        {
            try{
                baseDir = new File( "." ).getCanonicalPath();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        ConfigurationManager.loadConfigurations(baseDir + DEFAULT_CONFIG_DIR);
        Investigation investigation = new Investigation("gis-investigation", "GIS investigation test");

        investigation.addContact(new InvestigationContact("maguire", "eamonn", "J", "eamonnmag@gmail.com", "", "", "", "Oxford University", ""));
        investigation.addPublication(new InvestigationPublication("64654", "doi", "E. Maguire", "", ""));
        investigation.addPublication(new InvestigationPublication("634654", "doi", "P Rocca-Serra", "Some paper", ""));
        investigation.setFileReference("ProgramData/i_investigation.txt");

        Study study = new Study("gis-1");
        Assay studySample = new Assay("s_samples.txt", ConfigurationManager.selectTROForUserSelection(MappingObject.STUDY_SAMPLE));
        study.setStudySamples(studySample);

        studySample.getTableReferenceObject().addRowData(studySample.getTableReferenceObject().getHeaders().toArray(
                new String[]{"Source Name", "Characteristics[organism]", "Protocol REF", "Sample Name"}),
                new String[]{"source1", "homo sapiens", "sampling", "sample1"});

        investigation.addStudy(study);

        Assay testAssay = new Assay("assay_1.txt", "transcription profiling", "DNA microarray", "");

        study.addAssay(testAssay);

        ISAFileOutput fileOutput = new OutputISAFiles();
        fileOutput.saveISAFiles(true, investigation);
    }

    @Test
    public void createISATabProgrammatically2Test() {

        String baseDir = System.getProperty("basedir");

        if ( baseDir == null )
        {
            try{
                baseDir = new File( "." ).getCanonicalPath();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        ISAcreatorProperties.setProperties(PropertyFileIO.DEFAULT_CONFIGS_SETTINGS_PROPERTIES);
        ConfigurationManager.loadConfigurations(baseDir + DEFAULT_CONFIG_DIR);
        Investigation investigation = new Investigation("gis-investigation", "GIS investigation test");

        investigation.addContact(new InvestigationContact("maguire", "eamonn", "J", "eamonnmag@gmail.com", "", "", "", "Oxford University", ""));
        investigation.addPublication(new InvestigationPublication("64654", "doi", "E. Maguire", "", ""));
        investigation.addPublication(new InvestigationPublication("634654", "doi", "P Rocca-Serra", "Some paper", ""));
        investigation.setFileReference("ProgramData/i_investigation.txt");

        Study study = new Study("gis-1");
        Assay studySample = new Assay("s_samples.txt", ConfigurationManager.selectTROForUserSelection(MappingObject.STUDY_SAMPLE));
        study.setStudySamples(studySample);

        TableReferenceObject studySampleTableReferenceObject =  studySample.getTableReferenceObject();

        studySampleTableReferenceObject.addRowData(studySampleTableReferenceObject.getHeaders().toArray(
                        new String[]{"Source Name", "Characteristics[organism]", "Protocol REF", "Sample Name"}),
                new String[]{"source1", "homo sapiens", "sampling", "sample1"});

        OntologyManager.addToOntologyTerms("homo sapiens", new OntologyTerm("homo sapiens", "http://purl.obolibrary.org/obo/NCBITaxon_9606", "http://purl.obolibrary.org/obo/NCBITaxon_9606", new OntologySourceRefObject("NCBITaxon")));

        investigation.addStudy(study);

        Assay testAssay = new Assay("assay_1.txt", "transcription profiling", "DNA microarray", "");

        study.addAssay(testAssay);

        ISAFileOutput fileOutput = new OutputISAFiles();
        fileOutput.saveISAFiles(true, investigation);
    }
}
