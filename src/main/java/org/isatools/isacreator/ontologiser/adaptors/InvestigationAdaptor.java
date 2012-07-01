package org.isatools.isacreator.ontologiser.adaptors;

import org.isatools.isacreator.apiutils.InvestigationUtils;
import org.isatools.isacreator.apiutils.SpreadsheetUtils;
import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.ontologiser.model.OntologisedResult;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.ontologymanager.utils.OntologyUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/02/2011
 *         Time: 22:43
 */
public class InvestigationAdaptor implements ContentAdaptor {

    private Investigation investigation;

    // by creating and maintaining this Map, we are able to locate which Spreadsheets
    // contain which terms, making string substitution much quicker.

    private Map<Assay, Set<String>> assayToTerms;

    public InvestigationAdaptor(Investigation investigation) {
        this.investigation = investigation;
        assayToTerms = new HashMap<Assay, Set<String>>();
    }

    public void replaceTerms(Set<OntologisedResult> annotations) {

        Map<String, OntologyTerm> mappingsForReplacement = new HashMap<String, OntologyTerm>();

        // for each annotation, if it has an ontology selected, use that and replace the values in the spreadsheet.

        for (OntologisedResult annotation : annotations) {

            if (annotation.getAssignedOntology() != null) {

                Ontology sourceOntology = annotation.getAssignedOntology().getOntologySource();

                OntologySourceRefObject ontologySourceRefObject = OntologyUtils.convertOntologyToOntologySourceReferenceObject(sourceOntology);

                // adding ontology source in case it has not already been added
                OntologyManager.addToUsedOntologySources("annotator", ontologySourceRefObject);

                OntologyTerm ontology = annotation.getAssignedOntology().getOntologyTerm();

                // add the term to the ontology history.
                OntologyTerm ontologyObject = new OntologyTerm(ontology.getOntologyTermName(), ontology.getOntologySourceAccession(), ontologySourceRefObject);

                mappingsForReplacement.put(annotation.getFreeTextTerm(), ontologyObject);

                OntologyManager.addToUserHistory(ontologyObject);
            }
        }

        // now replace the terms in each of the Spreadsheets available within ISAcreator
        for (String studyAccession : investigation.getStudies().keySet()) {
            Study study = investigation.getStudies().get(studyAccession);

            System.out.println("Replacing terms in " + studyAccession);
            SpreadsheetUtils.replaceFreeTextWithOntologyTerms(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(study.getStudySample())).getSpreadsheet(), mappingsForReplacement);

            for (Assay assay : study.getAssays().values()) {
                System.out.println("Replacing terms in " + assay.getAssayReference());
                SpreadsheetUtils.replaceFreeTextWithOntologyTerms(((AssaySpreadsheet) ApplicationManager.getUserInterfaceForISASection(assay)).getSpreadsheet(), mappingsForReplacement);
            }
        }
    }

    public Set<String> getTerms() {
        Map<Assay, Map<String, Set<String>>> result = InvestigationUtils.getFreeTextInInvestigationSpreadsheets(investigation);

        return createFlattenedSet(result);
    }

    private Set<String> createFlattenedSet(Map<Assay, Map<String, Set<String>>> toFlatten) {

        Set<String> flattenedSet = new HashSet<String>();

        for (Assay assay : toFlatten.keySet()) {
            Set<String> assayTerms = new HashSet<String>();
            for (String columnName : toFlatten.get(assay).keySet()) {
                assayTerms.addAll(toFlatten.get(assay).get(columnName));

            }
            flattenedSet.addAll(assayTerms);
            assayToTerms.put(assay, assayTerms);
        }

        return flattenedSet;
    }
}
