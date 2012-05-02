package org.isatools.isacreator.plugins.registries;

import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;
import org.isatools.isacreator.ontologymanager.common.OntologyTerm;
import org.isatools.isacreator.plugins.host.service.PluginOntologyCVSearch;
import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 02/10/2011
 *         Time: 22:23
 */
public class OntologySearchPluginRegistry {

    private static List<PluginOntologyCVSearch> ontologyCVSearchPlugins = new ArrayList<PluginOntologyCVSearch>();

    public static void registerPlugin(PluginOntologyCVSearch plugin) {
        System.out.println("Registering ontology search plugin");
        ontologyCVSearchPlugins.add(plugin);
    }

    public static void deregisterPlugin(PluginOntologyCVSearch plugin) {
        System.out.println("Deregistering ontology search plugin");
        if (ontologyCVSearchPlugins.contains(plugin)) {
            ontologyCVSearchPlugins.remove(plugin);
        }
    }

    public static Map<OntologySourceRefObject, List<OntologyTerm>> compositeSearch(String term) {
        Map<OntologySourceRefObject, List<OntologyTerm>> result = new HashMap<OntologySourceRefObject, List<OntologyTerm>>();

        for (PluginOntologyCVSearch searchResource : ontologyCVSearchPlugins) {
            System.out.println("Executing CV/Ontology search on plugin resource");
            result.putAll(searchResource.searchRepository(term));
        }

        return result;
    }

    public static List<PluginOntologyCVSearch> getOntologyCVSearchPlugins() {
        return ontologyCVSearchPlugins;
    }

    public static boolean searchResourcesAvailable() {
        return ontologyCVSearchPlugins.size() > 0;
    }

}
