package org.isatools.isacreator.launch;

import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.isatools.isacreator.io.osgi.OSGiDependencyImport;
import org.osgi.framework.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 21/08/2012
 * Time: 11:56
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
final class ConfigurationUtil {

    /**
     * Creates a configuration for the Felix framework.
     *
     * @return a map of string, string with the configuration for the framework
     */
    public static Map<String, Object> createConfiguration()
    {
        final File cachedir = createCacheDir();

        Map<String, Object> configMap = new HashMap<String, Object>();

        String osgiDependencies = OSGiDependencyImport.getDependencies();
        System.out.println("Loaded the following system packages:\n" + osgiDependencies);
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, OSGiDependencyImport.getDependencies());

        //add plugins
        File pluginsDirectory = new File("Plugins");
        if (!pluginsDirectory.exists()) {
            pluginsDirectory.mkdir();
        } else {
            File[] plugins = pluginsDirectory.listFiles();
            StringBuilder toLoad = new StringBuilder();
            for (File plugin : plugins) {
                if (plugin.isDirectory()) {
                    for (File jarFile : plugin.listFiles()) {
                        if (jarFile.getName().contains(".jar")) {
                            toLoad.append("file:").append("\"").append(jarFile.getAbsolutePath()).append("\"").append(" ");
                            System.out.println("Added plugin " + jarFile.getName());
                        }
                    }
                } else {
                    if (plugin.getName().contains(".jar")) {
                        toLoad.append("file:").append("\"").append(plugin.getAbsolutePath()).append("\"").append(" ");
                        System.out.println("Added plugin " + plugin.getName());
                    }
                }
            }
            configMap.put(AutoProcessor.AUTO_START_PROP + ".1",
                    toLoad.toString());

        }

        configMap.put(FelixConstants.LOG_LEVEL_PROP, "4");

        if (cachedir != null)
        {
            configMap.put(Constants.FRAMEWORK_STORAGE, cachedir.getAbsolutePath());
        }

        return configMap;
    }

    /**
     * Tries to create a temporay cache dir. If creation of the cache dir is successful,
     * it will be returned. If creation fails, null will be returned.
     *
     * @return a {@code File} object representing the cache dir
     */
    private static File createCacheDir()
    {
        // Create a temporary bundle cache directory and
        // make sure to clean it up on exit.
        try
        {
            final File cachedir = File.createTempFile("isacreator.servicebase", null);
            System.out.println(cachedir.getAbsolutePath());
            cachedir.delete();


            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    cachedir.delete();
                }
            });
            return cachedir;

        }
        catch (IOException e)
        {
            return null;
        }

    }


}
