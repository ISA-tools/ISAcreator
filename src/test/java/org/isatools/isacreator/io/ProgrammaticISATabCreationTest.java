package org.isatools.isacreator.io;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.io.exportisa.ISAFileOutput;
import org.isatools.isacreator.io.exportisa.OutputISAFiles;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.model.*;
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
public class ProgrammaticISATabCreationTest {

    @Test
    public void createISATabProgrammatically() {

    	String baseDir = System.getProperty("basedir");

        if ( baseDir == null )
        {
            try{
                baseDir = new File( "." ).getCanonicalPath();
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        ConfigurationManager.loadConfigurations(baseDir + "/src/main/resources/Configurations/isaconfig-default_v2014-01-16/");
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
}
