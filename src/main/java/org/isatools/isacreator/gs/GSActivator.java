package org.isatools.isacreator.gs;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import org.isatools.isacreator.api.Authentication;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.launch.ISAcreatorCLArgs;

import java.io.File;

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
                //main = new ISAcreator(ISAcreatorCLArgs.mode(), bundleContext, ISAcreatorCLArgs.configDir());
                main = new ISAcreator(ISAcreatorCLArgs.mode(), bundleContext);

                //if username and password were given as parameters, log in user into GS
                if (ISAcreatorCLArgs.username()!=null && ISAcreatorCLArgs.password()!=null){

                     gsAuthentication = new GSIdentityManager();
                     loggedIn = gsAuthentication.login(ISAcreatorCLArgs.username(), ISAcreatorCLArgs.password().toCharArray());
                     if (!loggedIn){
                        System.out.println("Login to GenomeSpace failed for user "+ISAcreatorCLArgs.username());
                        System.exit(0);
                     }


                     main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), ISAcreatorCLArgs.isatabDir());

                } else if (ISAcreatorCLArgs.username()!=null){
                    //if username identified, check if token exists
                    gsAuthentication =  new GSSingleSignOnManager();
                    loggedIn = gsAuthentication.login();


                }else {
                      //both username and password are null, check if auth token has been saved locally
                      gsAuthentication =  new GSSingleSignOnManager();
                      loggedIn = gsAuthentication.login();

                      if (loggedIn){
                         if (ISAcreatorCLArgs.isatabDir()!=null || ISAcreatorCLArgs.isatabFiles()!=null){
                          //isatabDir not null or isatabFiles not null
                          String localTmpDirectory = createTmpDirectory();
                          if (ISAcreatorCLArgs.isatabDir()!=null){
                              if (ISAcreatorCLArgs.isatabFiles()!=null){
                                  System.err.println("Either a directory containing the ISA-Tab dataset or the set of ISA-Tab files should be passed as parameters, but not both.");
                                  System.exit(-1);
                              }
                              ISAcreatorCLArgs.isatabDir(localTmpDirectory);
                              if (ISAcreatorCLArgs.isatabFiles()!=null){
                                  GSDataManager gsDataManager = ((GSSingleSignOnManager)gsAuthentication).getGSDataManager();
                                  for(String filePath: ISAcreatorCLArgs.isatabFiles()){
                                      gsDataManager.downloadFile(filePath, localTmpDirectory);
                                  }
                              }

                            }
                        }
                          main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), ISAcreatorCLArgs.isatabDir());

                      } else { //not logged in

                          main.createGUI(ISAcreatorCLArgs.configDir(), ISAcreatorCLArgs.username(), ISAcreatorCLArgs.isatabDir(), gsAuthentication, "org.isatools.isacreator.gs.gui.GSAuthenticationMenu");
                      }

                }//else
            }//run

        });

        loadISATask.start();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private String createTmpDirectory(){
        String localTmpDirectory = System.getProperty("java.io.tmpdir")+ "isatab-" + System.currentTimeMillis() + File.separator;
        boolean success = new File(localTmpDirectory).mkdir();
        if (success) {
            System.out.println("Directory: "+ localTmpDirectory + " created");
            return localTmpDirectory;
        }else{
            System.out.println("Could not create "+localTmpDirectory);
            System.exit(-1);
        }
        return null;
    }
}
