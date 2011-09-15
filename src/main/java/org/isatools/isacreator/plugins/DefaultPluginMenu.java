package org.isatools.isacreator.plugins;

import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.swing.*;

public class DefaultPluginMenu implements PluginMenu {

    private BundleContext context;
    private boolean disposed = false;
    private ServiceReference sRef;
    private PluginMenu menuService;

    public DefaultPluginMenu(BundleContext context, ServiceReference sRef) {
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

    public JMenu removeMenu(JMenu menu) {

        if (context != null && !disposed) {
            try {
                menuService = (PluginMenu) context.getService(sRef);
                return menuService.removeMenu(menu);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return menu;
    }

    public JMenu addMenu(JMenu menu) {
        if (context != null && !disposed) {
            try {
                menuService = (PluginMenu) context.getService(sRef);
                return menuService.addMenu(menu);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return menu;
    }
}
