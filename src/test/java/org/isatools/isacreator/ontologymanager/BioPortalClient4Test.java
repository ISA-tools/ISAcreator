package org.isatools.isacreator.ontologymanager;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.ontologymanager.bioportal.io.AcceptedOntologies;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Created by eamonnmaguire on 17/12/2013.
 */
public class BioPortalClient4Test {

    private static BioPortal4Client client = new BioPortal4Client();
    public static String testOntologyID = "EFO";
    public static String testTermAccession = "efo:EFO_0000428";
    public static String testOntologyVersion = "45781";
    public static String testSearchTerm = "dose";

    public static String obiID = "1123";
    public static String obiVersion = "47893";


    @Test
    public void getTermsByPartialNameFromSource() {
        System.out.println("_____Testing getTermsByPartialNameFromSource()____");

        long startTime = System.currentTimeMillis();
        Map<OntologySourceRefObject, List<OntologyTerm>> result = client.getTermsByPartialNameFromSource(testSearchTerm, "all", false);
        System.out.println("Took " + (System.currentTimeMillis()-startTime) + "ms to do that query.");


        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }

        System.out.println();

        startTime = System.currentTimeMillis();
        result = client.getTermsByPartialNameFromSource(testSearchTerm, "all", false);
        System.out.println("Took " + (System.currentTimeMillis()-startTime) + "ms to do that query.");

        for (OntologySourceRefObject source : result.keySet()) {
            System.out.println(source.getSourceName() + " (" + source.getSourceVersion() + ")");

            for (OntologyTerm term : result.get(source)) {
                System.out.println("\t" + term.getOntologyTermName() + " (" + term.getOntologyTermAccession() + ")");
            }
        }

    }

    private String ontologySources() {
        return "CBO,FB-DV,NMR,MESH,ECG,PSIMOD,PR,MA,TADS,OBI,SNPO,ATMO,BT,BSPO,GEOSPECIES,CHEBI,LHN,MEGO,pseudo,WB-LS,GO,FHHO,PAE,BILA,OGI,EFO,OPB,SEP,MEDLINEPLUS,BTO,MAT,CARO,NEMO,REX,FB-SP,BIRNLEX,OCRE,TAO,HAO,TGMA,IMR,BHO,BRO,DC-CL,HP,FBbi,EHDAA,SBO,ZFA,PTO,LOINC,GRO,PATO,TTO,WB-BT,ZEA,GRO-CPD,PDQ,OPL,PECO,SO,CPTAC,NDFRT,CPRO,PRO-ONT,COSTART,BCGO,ICPC,SOPHARM,SNOMEDCT,EHDA,FIX,SYMP,XAO,OBOREL,EVOC,DERMLEX,IDOMAL,PEO,ATO,MFO,WHO-ART,IEV,EMAP,FB-BT,AAO,HC,BP-METADATA,PSDS,NCBITAXON,HOM,FAO,ABA-AMB,WB-PHENOTYPE,ENVO,GALEN,BFO,LIPRO,MAO,ACGT-MO,CTONT,UO,PHYFIELD,OGDI,SPD,FB-CV,OMIM,SPO,EP,CDAO,VO,MHC,APO,SBRO,PROPREO,MS,PPIO,SITBAC,DDANAT,SAO,OGR,RS,ICD9CM,CLO,DOID,PTRANS,NIFSTD,MP,MO,PW,AMINO-ACID,ECO,FYPO";
    }

    @Test
    public void getAllOntologies() {
        System.out.println("_____Testing getAllOntologies()____");

        Collection<Ontology> ontologies = client.getAllOntologies();

        assertTrue("Oh no! No returned ontologies (empty result)! ", ontologies.size() > 0);

        System.out.println("Found " + ontologies.size() + " ontologies \n");
        for (Ontology ontology : ontologies) {
            System.out.println(ontology.getOntologyID() + " - " + ontology.getOntologyAbbreviation() + " -> " + ontology.getOntologyDisplayLabel()
                    + " -> " + ontology.getOntologyVersion() + " - " + ontology.getHomepage() + " " + ontology.getContactName());
        }
    }
}
