package org.isatools.isacreator.gui.modeselection;

import com.sun.awt.AWTUtilities;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.main.AutoActivator;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.plugins.PluginTracker;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 11/04/2011
 *         Time: 15:02
 */
public class ModeSelector extends JFrame implements BundleActivator {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("gui-package.style").load(
                ModeSelector.class.getResource("/dependency-injections/gui-package.properties"));
    }


    private JLabel lightMode;
    private JLabel normalMode;

    private Box optionContainer;
    private JPanel loadingContainer;

    @InjectedResource
    private ImageIcon lightIcon, lightIconOver, normalIcon, normalIconOver, loadingIcon;

    public ModeSelector() {
        ResourceInjector.get("gui-package.style").inject(this);
    }

    private void createGUI(final BundleContext context) {

        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setUndecorated(true);

        AWTUtilities.setWindowOpaque(this, false);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        int x = (dim.width - 590) / 2;
        int y = (dim.height - 394) / 2;

        this.setLocation(x, y);

        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        optionContainer = Box.createHorizontalBox();
        optionContainer.setOpaque(false);

        lightMode = new JLabel(lightIcon);

        lightMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                loadISAcreator(Mode.LIGHT_MODE, context);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lightMode.setIcon(lightIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                lightMode.setIcon(lightIcon);
            }
        });

        normalMode = new JLabel(normalIcon);

        normalMode.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                loadISAcreator(Mode.NORMAL_MODE, context);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                normalMode.setIcon(normalIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                normalMode.setIcon(normalIcon);
            }
        });

        optionContainer.add(lightMode);
        optionContainer.add(normalMode);

        container.add(optionContainer);

        loadingContainer = new JPanel(new GridLayout(1, 1));
        loadingContainer.setOpaque(false);
        loadingContainer.setVisible(false);
        // create and add loading icon
        JLabel loading = new JLabel(loadingIcon);


        loadingContainer.add(loading);

        container.add(loadingContainer);

        add(container, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    private void loadISAcreator(final Mode mode, final BundleContext context) {

        optionContainer.setVisible(false);
        loadingContainer.setVisible(true);

        Thread loadISATask = new Thread(new Runnable() {
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ISAcreator main = new ISAcreator(mode, context);
                        main.createGUI();

                        dispose();
                    }
                });
            }
        });

        loadISATask.start();
    }

    /**
     * Displays the applications window and starts service tracking;
     * everything is done on the Swing event thread to avoid synchronization
     * and repainting issues.
     *
     * @param context The context of the bundle.
     */
    public void start(final BundleContext context) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createGUI(context);
            }
        });
    }

    /**
     * Stops service tracking and disposes of the application window.
     *
     * @param context The context of the bundle.
     */
    public void stop(BundleContext context) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ModeSelector.this.dispose();
            }
        });
    }

    public static void main(String[] args) throws Exception {

        ModeSelector isacreatorModeSelection = new ModeSelector();
        // Create a temporary bundle cache directory and
        // make sure to clean it up on exit.
        final File cachedir = File.createTempFile("isacreator.servicebase", null);
        System.out.println(cachedir.getAbsolutePath());
        cachedir.delete();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                cachedir.delete();
            }
        });

        Map<String, Object> configMap = new HashMap<String, Object>();
        configMap.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                "org.isatools.isacreator.plugins.host.service, org.isatools.isacreator.model, org.isatools.isacreator.gui, org.isatools.isacreator.common, " +
                        "org.isatools.errorreporter.ui, org.apache.log4j, org.apache.log4j.spi, org.isatools.errorreporter.html, org.isatools.errorreporter.model, " +
                        "org.isatools.isacreator.effects, org.isatools.isacreator.spreadsheet, org.isatools.isacreator.apiutils, " +
                        "org.isatools.isacreator.configuration, org.isatools.errorreporter.ui.borders, com.sun.awt, org.isatools.errorreporter.ui.utils, " +
                        "org.isatools.isacreator.settings, uk.ac.ebi.utils.collections, org.jdesktop.fuse, org.isatools.isacreator.gui.menu, " +
                        "org.isatools.isatab.isaconfigurator, com.explodingpixels.macwidgets");

        File pluginDirectory = new File("Plugins");

        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdir();
        } else {
            File[] plugins = pluginDirectory.listFiles();
            StringBuilder toLoad = new StringBuilder();
            for (File plugin : plugins) {
                if (plugin.getName().contains(".jar")) {
                    toLoad.append("file:").append(plugin.getAbsolutePath()).append(" ");
                }
            }
            configMap.put(AutoActivator.AUTO_START_PROP + ".1",
                    toLoad.toString());
        }

        configMap.put(FelixConstants.LOG_LEVEL_PROP, "4");
        configMap.put(Constants.FRAMEWORK_STORAGE, cachedir.getAbsolutePath());

        // Create list to hold custom framework activators.
        List<BundleActivator> list = new ArrayList<BundleActivator>();
        // Add activator to process auto-start/install properties.
        list.add(new AutoActivator(configMap));
        // Add our own activator.
        list.add(isacreatorModeSelection);
        // Add our custom framework activators to the configuration map.
        configMap.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

        try {
            // Now create an instance of the framework.
            Felix felix = new Felix(configMap);
            felix.start();
        } catch (Exception ex) {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(-1);
        }
    }
}
