package org.isatools.isacreator.plugins;

import org.isatools.isacreator.configuration.RecommendedOntology;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.plugins.host.service.PluginOntologyCVSearch;
import org.isatools.isacreator.plugins.registries.OntologySearchPluginRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.*;

/**
 * Created by the ISA team
 */
public class DefaultOntologySearchWidget implements PluginOntologyCVSearch {

    private BundleContext context;
    private boolean disposed = false;
    private ServiceReference sRef;
    private PluginOntologyCVSearch ontologySearchService;

    public DefaultOntologySearchWidget(BundleContext context, ServiceReference sRef) {
        this.context = context;
        this.sRef = sRef;
    }

    public void dispose() {
        if (disposed) {
            context.ungetService(sRef);
            context = null;
            sRef = null;
            ontologySearchService = null;
        }
    }

    public void deregisterSearch() {

        if (context != null && !disposed) {
            try {
                ontologySearchService = (PluginOntologyCVSearch) context.getService(sRef);
                OntologySearchPluginRegistry.deregisterPlugin(ontologySearchService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void registerSearch() {
        if (context != null && !disposed) {
            try {
                ontologySearchService = (PluginOntologyCVSearch) context.getService(sRef);
                OntologySearchPluginRegistry.registerPlugin(ontologySearchService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> searchRepository(String term) {
        if (context != null && !disposed) {
            try {
                ontologySearchService = (PluginOntologyCVSearch) context.getService(sRef);
                return ontologySearchService.searchRepository(term);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return new HashMap<OntologySourceRefObject, List<OntologyTerm>>();
    }

    public boolean hasPreferredResourceForCurrentField(Map<String, RecommendedOntology> recommendedOntologies) {
        if (context != null && !disposed) {
            try {
                ontologySearchService = (PluginOntologyCVSearch) context.getService(sRef);
                return ontologySearchService.hasPreferredResourceForCurrentField(recommendedOntologies);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    public Map<OntologySourceRefObject, List<OntologyTerm>> searchRepository(String term, Map<String, RecommendedOntology> recommendedOntologies, boolean searchAll) {
        if (context != null && !disposed) {
            try {
                ontologySearchService = (PluginOntologyCVSearch) context.getService(sRef);
                return ontologySearchService.searchRepository(term, recommendedOntologies, searchAll);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new HashMap<OntologySourceRefObject, List<OntologyTerm>>();
    }

    public Set<String> getAvailableResourceAbbreviations() {
        if (context != null && !disposed) {
            try {
                ontologySearchService = (PluginOntologyCVSearch) context.getService(sRef);
                return ontologySearchService.getAvailableResourceAbbreviations();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new HashSet<String>();
    }
}
