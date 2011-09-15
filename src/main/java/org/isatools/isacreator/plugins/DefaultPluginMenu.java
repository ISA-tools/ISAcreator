package org.isatools.isacreator.plugins;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import javax.swing.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/09/2011
 *         Time: 16:58
 */
public class DefaultPluginMenu implements PluginMenu {

    private BundleContext context;
    private boolean disposed = false;
    private ServiceReference sRef;
    private PluginMenu menuService;
    private ISAcreator isacreatorEnvironment;

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

    public void setEnvironment(ISAcreator isacreatorEnvironment) {
        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    public ISAcreator getEnvironment() {
        return isacreatorEnvironment;
    }

    public JMenu removeMenu(JMenu menu) {
        // If this is a proxy shape, instantiate the shape class
        // and use it to draw the shape.
        if (context != null && !disposed) {
            try {
                // Get the shape service.
                menuService = (PluginMenu) context.getService(sRef);

                // Change the menu via the service
                // If everything was successful, then simply return.
                return menuService.removeMenu(menu);
            } catch (Exception ex) {
                // This generally should not happen, but if it does then
                // we can just fall through and paint the default icon.
                ex.printStackTrace();
            }
        }

        // return unchanged menu
        return menu;
    }

    public JMenu addMenu(JMenu menu) {
        // If this is a proxy shape, instantiate the shape class
        // and use it to draw the shape.
        if (context != null && !disposed) {
            try {
                // Get the shape service.
                menuService = (PluginMenu) context.getService(sRef);

                // Change the menu via the service
                // If everything was successful, then simply return.
                return menuService.addMenu(menu);
            } catch (Exception ex) {
                // This generally should not happen, but if it does then
                // we can just fall through and paint the default icon.
                ex.printStackTrace();
            }
        }

        // return unchanged menu
        return menu;
    }
}
