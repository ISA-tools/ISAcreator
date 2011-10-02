package org.isatools.isacreator.plugins.registries;

import org.isatools.isacreator.plugins.host.service.PluginOntologyCVSearch;
import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;

import java.util.ArrayList;
import java.util.List;

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

}
