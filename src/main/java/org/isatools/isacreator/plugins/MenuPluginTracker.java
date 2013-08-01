package org.isatools.isacreator.plugins;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import javax.swing.*;


/**
 * A default menu item
 * Implementation help and sample provided from http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html
 * and http://karussell.wordpress.com/2009/09/16/plugable-swing-a-hello-world-osgi-example/
 */

public class MenuPluginTracker extends ServiceTracker {

    private final static int ADDED = 1, REMOVED = 2, MODIFIED = 3;

    private ISAcreator isacreatorEnvironment;

    public MenuPluginTracker(BundleContext context, ISAcreator isacreatorEnvironment) {
        super(context, PluginMenu.class.getName(), null);
        System.out.println("Registered plugin with menu plugin tracker.");
        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    /**
     * inform the application object about the added service.
     *
     * @param ref The service reference of the added service.
     * @return The service object to be used by the tracker.
     */
    @Override
    public Object addingService(ServiceReference ref) {
        System.out.println("Adding menu plugin");
        if (context.getService(ref) instanceof PluginMenu) {
            PluginMenu menu = new DefaultPluginMenu(context, ref);
            processPlugin(ADDED, menu);
            return menu;
        }

        return null;
    }

    /**
     * to inform the application object about the modified service.
     *
     * @param ref The service reference of the modified service.
     * @param svc The service object of the modified service.
     */
    @Override
    public void modifiedService(ServiceReference ref, Object svc) {
        System.out.println("Modifying menu plugin");
        if (svc instanceof PluginMenu) {
            PluginMenu plugin = (PluginMenu) svc;
            processPlugin(MODIFIED, plugin);
        }
    }

    /**
     * to inform the application object about the removed service.
     *
     * @param ref The service reference of the removed service.
     * @param svc The service object of the removed service.
     */
    @Override
    public void removedService(final ServiceReference ref, final Object svc) {
        System.out.println("Removing menu plugin");
        if (svc instanceof PluginMenu) {
            PluginMenu menu = new DefaultPluginMenu(context, ref);
            processPlugin(REMOVED, menu);
        }
    }

    /**
     * Actually performs the processing of the service notification. Invokes
     * the appropriate callback method on the application object depending on
     * the action type of the notification.
     *
     * @param action The type of action associated with the notification.
     */
    private void processPlugin(final int action, final PluginMenu menu) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                switch (action) {
                    case MODIFIED:
                        menu.removeMenu(isacreatorEnvironment.getPluginMenu());
                    case ADDED:
                        System.out.println(menu);
                        System.out.println(isacreatorEnvironment.getPluginMenu());
                        menu.addMenu(isacreatorEnvironment.getPluginMenu());
                        break;

                    case REMOVED:
                        menu.removeMenu(isacreatorEnvironment.getPluginMenu());
                        break;
                }
            }
        });
    }
}
