package org.isatools.isacreator.gs;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;


/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 08/10/2012
 * Time: 23:10
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSActivator implements BundleActivator {

    private ISAcreator main = null;

    public void start(final BundleContext bundleContext) throws Exception {

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                main = new ISAcreator(ISAcreatorCLArgs.mode(), bundleContext, ISAcreatorCLArgs.configDir());
                if (ISAcreatorCLArgs.mode()== Mode.GS){
                    main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), ISAcreatorCLArgs.isatabDir(),new GSSingleSignOnManager(), "org.isatools.isacreator.gs.gui.GSAuthenticationMenu");
                }
            }
        });

        loadISATask.start();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
