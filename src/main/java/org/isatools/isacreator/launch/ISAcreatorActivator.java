package org.isatools.isacreator.launch;

import org.apache.log4j.Logger;
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

    private static final Logger log = Logger.getLogger(ISAcreatorActivator.class);

    private ISAcreator main = null;

    public void start(final BundleContext bundleContext) throws Exception {

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                //main = new ISAcreator(ISAcreatorCLArgs.mode(), bundleContext, ISAcreatorCLArgs.configDir());
                main = new ISAcreator(ISAcreatorCLArgs.mode(), bundleContext);
                if (ISAcreatorCLArgs.noArguments()){
                    main.createGUI();
                }else{
                    main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), null, ISAcreatorCLArgs.isatabDir(), null, null, null, false);
                }
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
