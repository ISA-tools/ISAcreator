package org.isatools.isacreator.plugins.host.service;

import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This plugin will allow any implementing classes to act as a new search function in the Ontology Lookup component.
 */
public interface PluginOntologyCVSearch {

    void registerSearch();

    void deregisterSearch();

    /**
     * Will search using the plugin for terms across all resources defined within the plugin
     *
     * @param term - term to search for.
     * @return Map<OntologySourceRefObject, List<OntologyTerm>> Result of the query
     */
    Map<OntologySourceRefObject, List<OntologyTerm>> searchRepository(String term);

    /**
     * Will search using the plugin for terms across all resources defined within the plugin
     *
     * @param term                  - term to search for.
     * @param recommendedOntologies - recommended ontologies as defined in the ISA configuration XML.
     * @param searchAll             - search in all ontologies, or search in just those defined in the recommended ontologies or found in the field definition in the
     *                              resource-description.xml
     * @return Map<OntologySourceRefObject, List<OntologyTerm>> Result of the query
     */
    Map<OntologySourceRefObject, List<OntologyTerm>> searchRepository(String term, Map<String, RecommendedOntology> recommendedOntologies, boolean searchAll);

    boolean hasPreferredResourceForCurrentField(Map<String, RecommendedOntology> recommendedOntologies);

    Set<String> getAvailableResourceAbbreviations();
}
