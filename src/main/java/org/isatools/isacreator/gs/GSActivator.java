package org.isatools.isacreator.gs;

import org.isatools.errorreporter.model.ErrorMessage;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;

import java.io.File;
import java.util.List;

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

                //this shouldn't happen
                if (ISAcreatorCLArgs.mode()!= Mode.GS){
                    System.exit(-1);
                }

                Authentication gsAuthentication = null;
                boolean loggedIn = false;

                //LOGIN
                main = new ISAcreator(ISAcreatorCLArgs.mode(), bundleContext);
                gsAuthentication =  GSIdentityManager.getInstance();

                //if username and password were given as parameters, log in user into GS
                if (ISAcreatorCLArgs.username()!=null && ISAcreatorCLArgs.password()!=null){

                    gsAuthentication = GSIdentityManager.getInstance();
                    loggedIn = gsAuthentication.login(ISAcreatorCLArgs.username(), ISAcreatorCLArgs.password());
                    if (!loggedIn){
                        System.out.println("Login to GenomeSpace failed for user "+ISAcreatorCLArgs.username());
                        System.exit(0);
                    } else {
                        System.out.println("Logged in to GenomeSpace as user "+ISAcreatorCLArgs.username());

                        System.out.println("Downloading files from GenomeSpace directory: "+ ISAcreatorCLArgs.isatabDir());
                        List<ErrorMessage> errors = GSLocalFilesManager.downloadFiles(gsAuthentication);


                        if (!errors.isEmpty()){
                            System.out.println("The files on "+ISAcreatorCLArgs.isatabDir()+" could not be accessed");
                            for(ErrorMessage errorMessage: errors){
                                System.out.println(errorMessage.getMessage());
                            }
                            // System.exit(-1);
                            main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), ISAcreatorCLArgs.password(), ISAcreatorCLArgs.isatabDir(), ISAcreatorCLArgs.isatabFiles(), gsAuthentication, null, loggedIn, errors);
                        }else{

                            System.out.println("Files downloaded from GenomeSpace downloaded to "+ISAcreatorCLArgs.isatabDir());

                            main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), ISAcreatorCLArgs.password(), ISAcreatorCLArgs.isatabDir(), ISAcreatorCLArgs.isatabFiles(), gsAuthentication, null, loggedIn);
                        }
                    }




                } else if (ISAcreatorCLArgs.username()!=null){
                    //if username identified, check if token exists

                    loggedIn = gsAuthentication.login("");
                    if (loggedIn){
                        GSLocalFilesManager.downloadFiles(gsAuthentication);
                        main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), null, ISAcreatorCLArgs.isatabDir(), null, null, null,true);
                    }else {
                        //not logged in
                        main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), null, ISAcreatorCLArgs.isatabDir(), null, gsAuthentication, "org.isatools.isacreator.gs.gui.GSAuthenticationMenu", false);

                    }

                }else {
                    //both username and password are null, check if auth token has been saved locally
                    gsAuthentication =  GSIdentityManager.getInstance();
                    loggedIn = gsAuthentication.login("");

                    if (loggedIn){
                        main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), null, ISAcreatorCLArgs.isatabDir(), null, gsAuthentication, null, true);
                    }else {
                        //not logged in
                        main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), null, ISAcreatorCLArgs.isatabDir(), null, gsAuthentication, "org.isatools.isacreator.gs.gui.GSAuthenticationMenu", false);

                    }

                }//else
            }//run

        });

        loadISATask.start();
    }


    public void stop(BundleContext bundleContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }


}
