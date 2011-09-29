package org.isatools.isacreator.plugins;

import org.isatools.isacreator.plugins.host.service.Plugin;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.swing.*;

/**
 * A default menu item
 * Implementation help and sample provided from http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html
 * and http://karussell.wordpress.com/2009/09/16/plugable-swing-a-hello-world-osgi-example/
 */
public class DefaultPluginMenu implements Plugin, PluginMenu {

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
