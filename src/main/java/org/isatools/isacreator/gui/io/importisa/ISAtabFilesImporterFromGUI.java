package org.isatools.isacreator.gui.io.importisa;

import org.isatools.isacreator.gui.DataEntryEnvironment;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.io.importisa.ISAtabImporter;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.List;

/**
 * Created by ISA team
 *
 * Date: 11/07/2012
 * Time: 16:10
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporterFromGUI extends ISAtabImporter {

    private DataEntryEnvironment dataEntryEnvironment;
    private ISAcreator isacreator;

    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * @param isacreator        - a reference to the Main entry point of the Application
     */
    public ISAtabFilesImporterFromGUI(ISAcreator isacreator){
        this.isacreator = isacreator;
        this.dataEntryEnvironment = new DataEntryEnvironment();
        isacreator.setCurDataEntryPanel(dataEntryEnvironment);
    }

    public boolean importFile(String parentDir){
        boolean result = commonImportFile(parentDir);

        //GUI related stuff
        attachGUIsToInvestigation();
        dataEntryEnvironment.createGUIFromInvestigation(investigation);
        assignOntologiesToSession(mapper.getOntologyTermsDefined());

        return result;
    }

    private void attachGUIsToInvestigation() {
        ApplicationManager.assignDataEntryToISASection(investigation, new InvestigationDataEntry(investigation, dataEntryEnvironment));

        for (String studyIdentifier : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyIdentifier);

            ApplicationManager.assignDataEntryToISASection(study, new StudyDataEntry(dataEntryEnvironment, study));
            ApplicationManager.assignDataEntryToISASection(study.getStudySample(), ApplicationManager.getUserInterfaceForAssay(
                    study.getStudySample(), ((StudyDataEntry) ApplicationManager.getUserInterfaceForISASection(study))));

            for (String assay : study.getAssays().keySet()) {
                Assay assayToAdd = study.getAssays().get(assay);
                ApplicationManager.assignDataEntryToISASection(assayToAdd, ApplicationManager.getUserInterfaceForAssay(
                        assayToAdd, ((StudyDataEntry) ApplicationManager.getUserInterfaceForISASection(study))));
            }
        }
    }


    private void assignOntologiesToSession(List<OntologyTerm> ontologiesUsed) {
        for (OntologyTerm oo : ontologiesUsed) {
            if (!oo.getOntologyTermName().trim().equals("")) {
                OntologyManager.addToUserHistory(oo);
            }
        }
    }


}
