package org.isatools.isacreator.launch;

import org.isatools.isacreator.gui.ISAcreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 21/08/2012
 * Time: 13:44
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorActivator implements BundleActivator {

    private ISAcreator main = null;

    @Override
    public void start(final BundleContext bundleContext) throws Exception {

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        main = new ISAcreator(ISAcreatorApplication.mode, bundleContext);
                        main.createGUI();
                    }
                });
            }
        });

        loadISATask.start();
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                main.dispose();
            }
        });
    }
}
