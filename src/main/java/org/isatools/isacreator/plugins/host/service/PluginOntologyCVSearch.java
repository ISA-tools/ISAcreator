package org.isatools.isacreator.plugins.host.service;

import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;

import java.util.List;
import java.util.Map;

/**
 * This plugin will allow any implementing classes to act as a new search function in the Ontology Lookup component.
 */
public interface PluginOntologyCVSearch {

    void registerSearch();

    void deregisterSearch();

    Map<OntologySourceRefObject, List<OntologyTerm>> searchRepository(String term);

}
