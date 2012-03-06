package org.isatools.isacreator.plugins;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.host.service.PluginMenu;
import org.isatools.isacreator.plugins.host.service.PluginOntologyCVSearch;
import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import javax.swing.*;


/**
 * A default menu item
 * Implementation help and sample provided from http://felix.apache.org/site/apache-felix-maven-bundle-plugin-bnd.html
 * and http://karussell.wordpress.com/2009/09/16/plugable-swing-a-hello-world-osgi-example/
 */

public class SpreadsheetPluginTracker extends ServiceTracker {

    private final static int ADDED = 1, REMOVED = 2, MODIFIED = 3;

    public SpreadsheetPluginTracker(BundleContext context) {
        super(context, PluginSpreadsheetWidget.class.getName(), null);
    }

    /**
     * inform the application object about the added service.
     *
     * @param ref The service reference of the added service.
     * @return The service object to be used by the tracker.
     */
    @Override
    public Object addingService(ServiceReference ref) {
        System.out.println("Adding spreadsheet plugin");

        if (context.getService(ref) instanceof PluginSpreadsheetWidget) {
            PluginSpreadsheetWidget widget = new DefaultSpreadsheetWidget(context, ref);
            processPlugin(ADDED, widget);
            return widget;
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
        System.out.println("Modified spreadsheet plugin");
        if (svc instanceof PluginSpreadsheetWidget) {
            PluginSpreadsheetWidget plugin = (PluginSpreadsheetWidget) svc;
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
        System.out.println("Removed spreadsheet plugin");
        if (svc instanceof PluginSpreadsheetWidget) {
            PluginSpreadsheetWidget widget = new DefaultSpreadsheetWidget(context, ref);
            processPlugin(REMOVED, widget);

        }

    }

    /**
     * Actually performs the processing of the service notification. Invokes
     * the appropriate callback method on the application object depending on
     * the action type of the notification.
     *
     * @param action The type of action associated with the notification.
     */
    private void processPlugin(final int action, final PluginSpreadsheetWidget widget) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                switch (action) {
                    case MODIFIED:
                        widget.registerCellEditor();

                    case ADDED:
                        widget.registerCellEditor();
                        break;

                    case REMOVED:
                        widget.deregisterCellEditor();
                        break;
                }
            }
        });
    }
}
