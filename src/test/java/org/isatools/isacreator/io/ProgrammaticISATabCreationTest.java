package org.isatools.isacreator.io;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.model.*;
import org.junit.Ignore;
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

    @Ignore
    public void createISATabProgrammatically() {

        ISAcreator isAcreator = new ISAcreator(Mode.NORMAL_MODE, null, "/Users/eamonnmaguire/git/isarepo/ISAcreator/Configurations/isaconfig-default_v2011-02-18/");

        Investigation investigation = new Investigation("gis-investigation", "GIS investigation test");

        investigation.addContact(new InvestigationContact("maguire", "eamonn", "J", "eamonnmag@gmail.com", "", "", "", "Oxford University", ""));
        investigation.addPublication(new InvestigationPublication("64654", "doi", "E. Maguire", "", ""));
        investigation.setFileReference("Data/i_investigation.txt");

        Study study = new Study("gis-1");

        investigation.addStudy(study);

        Assay testAssay = new Assay("assay_1.txt", "transcription profiling", "dna microarray", "");
        // todo assay completely tied to spreadsheet. This needs to be changed.
        // todo we need a way of utilising differ
        study.addAssay(testAssay);

        OutputISAFiles fileOutput = new OutputISAFiles(isAcreator);
        fileOutput.saveISAFiles(true, investigation);
    }
}
