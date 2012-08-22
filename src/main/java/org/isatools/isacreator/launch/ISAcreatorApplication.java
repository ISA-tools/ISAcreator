package org.isatools.isacreator.launch;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoActivator;
import org.isatools.isacreator.gui.modeselection.ModeSelector;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.osgi.framework.BundleActivator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 20/08/2012
 * Time: 10:52
 *
 * ISAcreatorApplication class: it provides a static {@code main()} method so that the bundle can be
 * run as a stand-alone host application.
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorApplication  {

    private static Felix felixFramework = null;

    public static Mode mode =  null;
    public static String configDir = null;
    public static String username = null;
    //this probably needs to be a set of files...
    public static String isatabDir = null;


    /**
     * Enables the bundle to run as a stand-alone application. When this
     * static {@code main()} method is invoked, the application creates
     * its own embedded OSGi framework instance
     *
     * @param args
     **/
    public static void main(String[] args)
    {
        BundleActivator activatorClass = null;
        //if there are no parameters, use the ModeSelector activator
        if (args==null || args.length==0){
           activatorClass = new ModeSelector();
        }else{
           int i = 0;
           String arg = null, option = null;
           while (i < args.length && args[i].startsWith("--")) {
                option = args[i++];
                arg = args[i++];

                if (option.equals("--mode"))
                   mode = arg.equals("NORMAL_MODE")? Mode.NORMAL_MODE: Mode.LIGHT_MODE;
                else if (option.equals("--configDir"))
                    configDir = arg;
                else if (option.equals("--username"))
                   username = arg;
                else if (option.equals("--isatabDir"))
                   isatabDir = arg;


               System.out.println("option="+option);
               System.out.println("arg="+arg);
           }
           activatorClass = new ISAcreatorActivator();

        }

        System.out.println("\nLaunching ISAcreator Application...");

        try
        {
            Map<String, Object> configMap = ConfigurationUtil.createConfiguration();

            // Create list to hold custom framework activators.
            List<BundleActivator> list = new ArrayList<BundleActivator>();
            // Add activator to process auto-start/install properties.
            list.add(new AutoActivator(configMap));
            // Add our own activator.
            list.add(activatorClass);
            // Add our custom framework activators to the configuration map.
            configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

            System.out.println("Starting up OSGi framework....");
            felixFramework = new Felix(configMap);
            //felixFramework.init(); //is this needed?
            felixFramework.start();
            //felixFramework.waitForStop(0); //is this needed?
            System.out.println("....Framework started successfully!");
            //System.exit(0);

        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }




}
