package org.isatools.isacreator.plugins;

import org.isatools.isacreator.plugins.host.service.Plugin;
import org.isatools.isacreator.plugins.host.service.PluginOntologyCVSearch;
import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;
import org.isatools.isacreator.plugins.registries.OntologySearchPluginRegistry;
import org.isatools.isacreator.plugins.registries.SpreadsheetPluginRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by the ISA team
 */
public class DefaultOntologySearchWidget implements Plugin, PluginOntologyCVSearch {

    private BundleContext context;
    private boolean disposed = false;
    private ServiceReference sRef;
    private PluginOntologyCVSearch menuService;

    public DefaultOntologySearchWidget(BundleContext context, ServiceReference sRef) {
        this.context = context;
        this.sRef = sRef;
    }

    public void dispose() {
        if (disposed) {
            context.ungetService(sRef);
            context = null;
            sRef = null;
            menuService = null;
        }
    }

    public void deregisterCellEditor() {

        if (context != null && !disposed) {
            try {
                menuService = (PluginOntologyCVSearch) context.getService(sRef);
                OntologySearchPluginRegistry.deregisterPlugin(menuService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void registerCellEditor() {
        if (context != null && !disposed) {
            try {
                menuService = (PluginOntologyCVSearch) context.getService(sRef);
                OntologySearchPluginRegistry.registerPlugin(menuService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
