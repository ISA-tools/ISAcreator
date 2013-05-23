package org.isatools.isacreator.launch;

import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.apache.log4j.Logger;
import org.isatools.isacreator.gs.GSActivator;
import org.isatools.isacreator.gui.modeselection.Mode;
import org.isatools.isacreator.gui.modeselection.ModeSelector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.launch.Framework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;


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

    private static final Logger log = Logger.getLogger(ISAcreatorApplication.class);

    private static Framework m_framework = null;

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

            ISAcreatorCLArgs.parseArgs(args);

            //If files come from GS
            if (ISAcreatorCLArgs.mode()==Mode.GS){
                activatorClass = new GSActivator();
            } else {//mode==GS
                activatorClass = new ISAcreatorActivator();
            }
         } //arguments are not null

        log.debug("isatabDir="+ISAcreatorCLArgs.isatabDir());
        System.out.println("\nLaunching ISAcreator Application...");

        try
        {
            Map<String, Object> configMap = ConfigurationUtil.createConfiguration();

            //setting the dependency-injections, so that plugins can use them
            ISAcreatorGUIProperties.setProperties();

            // Create list to hold custom framework activators.
            List<BundleActivator> list = new ArrayList<BundleActivator>();
            // Add activator to process auto-start/install properties.
            list.add(activatorClass);
            // Add our custom framework activators to the configuration map.
            configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

            Felix felix = new Felix(configMap);
            felix.init();
            AutoProcessor.process(configMap, felix.getBundleContext());

            System.out.println("Starting up OSGi framework....");
            felix.start();
            felix.waitForStop(0);
            System.out.println("....Framework started successfully!");
            System.exit(0);

        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }


    /**
     * Util method for creating an embedded Framework. Tries to create a {@link org.osgi.framework.launch.FrameworkFactory}
     * which is then be used to create the framework.
     *
     * @param config the configuration to create the framework with
     * @return a Framework with the given configuration
     */
    private static Framework createFramework(Map<String, String> config)
    {
        ServiceLoader<org.osgi.framework.launch.FrameworkFactory> factoryLoader =
                ServiceLoader.load(org.osgi.framework.launch.FrameworkFactory.class);
        for(org.osgi.framework.launch.FrameworkFactory factory : factoryLoader){
            return factory.newFramework(config);
        }
        throw new IllegalStateException("Unable to load FrameworkFactory service.");
    }


    /**
     * Installs and starts all bundles used by the application. Therefore the host bundle will be started. The locations
     * of extensions for the host bundle can be passed in as parameters.
     *
 //    * @param bundleLocations the locations where extension for the host bundle are located. Must not be {@code null}!
     * @throws org.osgi.framework.BundleException if something went wrong while installing or starting the bundles.
     */
//    private static void installAndStartBundles(String... bundleLocations) throws BundleException
//    {
//        BundleContext bundleContext = m_framework.getBundleContext();
//        Activator hostActivator = new Activator();
//        hostActivator.start(bundleContext);
//        for (String location : bundleLocations)
//        {
//            Bundle addition = bundleContext.installBundle(location);
//            addition.start();
//        }
//    }


    private static String[] parseFilenames(String arg){
       return arg.split(",");
    }


}