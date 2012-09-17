package org.isatools.isacreator.launch;

import org.isatools.isacreator.gui.ISAcreator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.swing.*;

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

    public void start(final BundleContext bundleContext) throws Exception {

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                main = new ISAcreator(ISAcreatorApplication.mode, bundleContext, ISAcreatorApplication.configDir);
                main.createGUI(ISAcreatorApplication.configDir, ISAcreatorApplication.username, ISAcreatorApplication.isatabDir);
            }
        });

        loadISATask.start();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                main.dispose();
            }
        });
    }
}
