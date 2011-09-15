package org.isatools.isacreator.plugins;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import javax.swing.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/09/2011
 *         Time: 16:57
 */
public class PluginTracker extends ServiceTracker {

    private final static int ADDED = 1, REMOVED = 2, MODIFIED = 3;

    private ISAcreator isacreatorEnvironment;

    public PluginTracker(BundleContext context, ISAcreator isacreatorEnvironment) {
        super(context, PluginMenu.class.getName(), null);
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
        PluginMenu menu = new DefaultPluginMenu(context, ref);
        processPlugin(ADDED, ref, menu);
        return menu;
    }

    /**
     * to inform the application object about the modified service.
     *
     * @param ref The service reference of the modified service.
     * @param svc The service object of the modified service.
     */
    @Override
    public void modifiedService(ServiceReference ref, Object svc) {
        PluginMenu plugin = (PluginMenu) svc;
        processPlugin(MODIFIED, ref, plugin);
    }

    /**
     * to inform the application object about the removed service.
     *
     * @param ref The service reference of the removed service.
     * @param svc The service object of the removed service.
     */
    @Override
    public void removedService(final ServiceReference ref, final Object svc) {
        final DefaultPluginMenu dMenu = (DefaultPluginMenu) svc;
        processPlugin(REMOVED, ref, dMenu);
        dMenu.dispose();
    }

    /**
     * Actually performs the processing of the service notification. Invokes
     * the appropriate callback method on the application object depending on
     * the action type of the notification.
     *
     * @param action The type of action associated with the notification.
     * @param ref    The service reference of the corresponding service.
     */
    private void processPlugin(final int action, final ServiceReference ref, final PluginMenu menu) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                switch (action) {
                    case MODIFIED:
                        menu.removeMenu(isacreatorEnvironment.getPluginMenu());

                        // Purposely let this fall through to the 'add' case to
                        // reload the service.

                    case ADDED:
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
