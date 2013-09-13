package org.isatools.isacreator.ontologymanager.utils;

import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.settings.ISAcreatorProperties;

import java.util.Map;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 13/09/2013
 * Time: 11:54
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class OntologyTermUtils {

    /**
     * Given an OntologyTerm object, it creates a string such as:
     *
     *   Characteristics[dose,efo:EFO_0000428,EFO]
     *
     * if not purl is defined in the ontology term, or such as
     *
     *   Characteristics[dose,http://www.ebi.ac.uk/efo/EFO_0000428,EFO]
     *
     *
     * @param ontologyTerm
     * @return
     */
   public static String ontologyTermToString(OntologyTerm ontologyTerm){
       if (ontologyTerm.getOntologyPurl()!=null && ISAcreatorProperties.getProperty("ontologyTermURI").equals("true")){
           return ontologyTerm.getOntologyTermName() +"," + ontologyTerm.getOntologyPurl() + "," + ontologyTerm.getOntologySource();
       }
     return ontologyTerm.getOntologyTermName() + "," +  ontologyTerm.getOntologyTermAccession() +"," + ontologyTerm.getOntologySource();
   }

   public static OntologyTerm stringToOntologyTerm(String header){
       OntologyTerm ontologyTerm = null;

       String prevVal = header.substring(header.indexOf('[') + 1, header.indexOf("]"));

       String source = "";
       String term = "";
       String accession = "";


       //moved this code from the TableReferenceObject and we are keeping it for backward compatibility, so that
       //we can still read in column headers with the form
       // <ontology source>-<term label>-<term accession>
       //but now we will save them back only following the patter by the method above (ontologyTermToString).

       if (prevVal.contains("-")) {
           String[] parts = prevVal.split("-");
           source = parts[0];
           term = parts[1];
           accession = parts[2];

           ontologyTerm = new OntologyTerm(term, accession, null, OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source));

       } else if (prevVal.contains(":")) {

           //this should be the currently accepted string
           //such as "Characteristics[dose,efo:EFO_0000428,EFO]" or "Characteristics[dose,http://www.ebi.ac.uk/efo/EFO_0000428,EFO]"
           if (prevVal.contains(",")){
               String[] parts = prevVal.split(",");
               term = parts[0];
               accession = parts[1];
               source = parts[2];

               if (accession.contains("http://"))
                   ontologyTerm = new OntologyTerm(term, null, accession, OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source));
               else
                   ontologyTerm = new OntologyTerm(term, accession, null, OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source));

           }else if (prevVal.startsWith("http://")) {
               // we have a PURL. So we'll use this directly
               if (prevVal.contains("(")) {
                   String[] termAndSource = prevVal.split("\\(");
                   term = termAndSource[0];
                   accession = termAndSource[1].replace(")", "");

                   ontologyTerm = new OntologyTerm(term, accession, null, OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source));
               }
           } else {
               String[] parts = prevVal.split(":");
               if (parts[0].contains("(")) {
                   String[] termAndSource = parts[0].split("\\(");
                   term = termAndSource[0];
                   source = termAndSource[1];
               }
               accession = parts[1].replace(")", "");

               ontologyTerm = new OntologyTerm(term, accession, null, OntologyManager.getOntologySourceReferenceObjectByAbbreviation(source));
           }


       }




     return ontologyTerm;
   }


    /***
     *
      * @param header
     * @return
     */
   public static String headerToString(String header){

       String ontologyUniqueId = header.substring(header.indexOf('[') + 1, header.indexOf("]"));
       String headerName = header.substring(0,header.indexOf('['));

       //convert the ontologyUniqueId to the ontology term string

       Map<String, OntologyTerm> ontologySelectionHistory = OntologyManager.getOntologySelectionHistory();

       if (ontologySelectionHistory!=null){

           OntologyTerm ontologyTerm = ontologySelectionHistory.get(ontologyUniqueId);

           if (ontologyTerm!=null){
              return headerName + "[" +ontologyTermToString(ontologyTerm) +"]";
           }
       }

       return null;
   }

    /**
     * When importing an ISA-TAB file, takes the full annotated header (e.g. Characteristics[organism,http://purl.obolibrary.org/obo/OBI_0100026,OBI])
     * and retrieves the header with the ontology term unique ID
     *
     * @param fullAnnotatedHeader
     * @return
     */
    public static String fullAnnotatedHeaderToUniqueId(String fullAnnotatedHeader){
        OntologyTerm ontologyTerm = stringToOntologyTerm(fullAnnotatedHeader);
        String headerName = fullAnnotatedHeader.substring(0,fullAnnotatedHeader.indexOf('['));

        if (ontologyTerm != null)
           return headerName +"["+ ontologyTerm.getUniqueId() + "]";

        return null;
    }


}
