package org.isatools.isacreator.io;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.io.exportisa.ISAFileOutput;
import org.isatools.isacreator.io.exportisa.OutputISAFiles;
import org.isatools.isacreator.model.*;
import org.junit.Test;

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

        ISAcreator isAcreator = new ISAcreator(Mode.NORMAL_MODE, null, "/Users/eamonnmaguire/git/isarepo/ISAcreator/Configurations/isaconfig-default_v2011-02-18/");

        Investigation investigation = new Investigation("gis-investigation", "GIS investigation test");

        investigation.addContact(new InvestigationContact("maguire", "eamonn", "J", "eamonnmag@gmail.com", "", "", "", "Oxford University", ""));
        investigation.addPublication(new InvestigationPublication("64654", "doi", "E. Maguire", "", ""));
        investigation.addPublication(new InvestigationPublication("634654", "doi", "P Rocca-Serra", "Stupid paper", ""));
        investigation.setFileReference("Data/i_investigation.txt");

        Study study = new Study("gis-1");
        Assay studySample = new Assay("s_samples.txt", ApplicationManager.getCurrentApplicationInstance().selectTROForUserSelection(MappingObject.STUDY_SAMPLE));
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
