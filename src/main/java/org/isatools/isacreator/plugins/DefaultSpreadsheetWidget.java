package org.isatools.isacreator.plugins;

import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;
import org.isatools.isacreator.plugins.registries.SpreadsheetPluginRegistry;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/09/2011
 *         Time: 15:00
 */
public class DefaultSpreadsheetWidget implements PluginSpreadsheetWidget {

    private BundleContext context;
    private boolean disposed = false;
    private ServiceReference sRef;
    private PluginSpreadsheetWidget menuService;

    public DefaultSpreadsheetWidget(BundleContext context, ServiceReference sRef) {
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
                menuService = (PluginSpreadsheetWidget) context.getService(sRef);
                SpreadsheetPluginRegistry.deregisterPlugin(menuService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    public void registerCellEditor() {
        if (context != null && !disposed) {
            try {
                menuService = (PluginSpreadsheetWidget) context.getService(sRef);
                SpreadsheetPluginRegistry.registerPlugin(menuService);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    public Set<String> targetColumns() {
        if (context != null && !disposed) {
            try {
                menuService = (PluginSpreadsheetWidget) context.getService(sRef);
                return menuService.targetColumns();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new HashSet<String>();
    }
}
