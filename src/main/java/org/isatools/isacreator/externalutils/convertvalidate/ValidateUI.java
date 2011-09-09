package org.isatools.isacreator.externalutils.convertvalidate;


import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.sun.awt.AWTUtilities;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.isatools.errorreporter.html.ErrorMessageWriter;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.FileType;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.errorreporter.ui.ErrorReporterView;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.menu.ImportFilesMenu;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.utils.datastructures.ISAPair;
import org.isatools.isatab.gui_invokers.AllowedConversions;
import org.isatools.isatab.gui_invokers.GUIISATABConverter;
import org.isatools.isatab.gui_invokers.GUIISATABValidator;
import org.isatools.isatab.gui_invokers.GUIInvokerResult;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
import org.isatools.tablib.utils.BIIObjectStore;
import org.isatools.tablib.utils.logging.TabLoggingEventWrapper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class ValidateUI extends JFrame {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("validator-package.style").load(
                ValidateUI.class.getResource("/dependency-injections/validator-package.properties"));
    }

    @InjectedResource
    private Image validateIcon, validateIconInactive, convertIcon, convertIconInactive;

    @InjectedResource
    private ImageIcon validationSuccess, conversionSuccess;

    private ISAcreator isacreatorEnvironment;

    protected static ImageIcon validateISAAnimation = new ImageIcon(ImportFilesMenu.class.getResource("/images/validator/validating.gif"));
    protected static ImageIcon convertISAAnimation = new ImageIcon(ImportFilesMenu.class.getResource("/images/validator/converting.gif"));

    public static final float DESIRED_OPACITY = .93f;

    private Timer animationTimer;

    private boolean animating;

    private int animationDirection;
    private long animationStart;

    private JPanel swappableContainer;
    private OperatingMode mode;

    public ValidateUI(ISAcreator isacreatorEnvironment) {

        this(isacreatorEnvironment, OperatingMode.VALIDATE);
    }

    public ValidateUI(ISAcreator isacreatorEnvironment, OperatingMode mode) {
        this.mode = mode;

        ResourceInjector.get("validator-package.style").inject(this);

        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    public void createGUI() {

        setTitle(mode == OperatingMode.VALIDATE ? "Validate ISAtab" : "Convert ISAtab");
        setUndecorated(true);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);

        HUDTitleBar titlePanel = new HUDTitleBar(
                mode == OperatingMode.VALIDATE ? validateIcon : convertIcon,
                mode == OperatingMode.VALIDATE ? validateIconInactive : convertIconInactive);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        Container loadingInfo = UIHelper.padComponentVerticalBox(100, new JLabel(validateISAAnimation));

        swappableContainer = new JPanel();
        swappableContainer.add(loadingInfo);
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(750, 450));

        add(swappableContainer, BorderLayout.CENTER);

        FooterPanel footer = new FooterPanel(this);
        add(footer, BorderLayout.SOUTH);

        pack();
    }

    public void validateISAtab() {

        Thread performer = new Thread(new Runnable() {
            public void run() {


                ISAConfigurationSet.setConfigPath(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

                final GUIISATABValidator isatabValidator = new GUIISATABValidator();

                GUIInvokerResult result = isatabValidator.validate(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));

                if (result == GUIInvokerResult.SUCCESS) {
                    Container successfulValidationContainer = UIHelper.padComponentVerticalBox(70, new JLabel(validationSuccess));
                    swapContainers(successfulValidationContainer);
                    if (mode == OperatingMode.CONVERT) {

                        final ConvertUI convertUI = new ConvertUI(constructConversionTargets());
                        convertUI.setPreferredSize(new Dimension(750, 440));
                        convertUI.createGUI();

                        convertUI.addPropertyChangeListener("startConversion", new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

                                swapContainers(UIHelper.padComponentVerticalBox(100, new JLabel(convertISAAnimation)));

                                convertISAtab(isatabValidator.getStore(), convertUI.getConversionToPerform(),
                                        ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB),
                                        convertUI.getOutputLocation());
                            }
                        });

                        swapContainers(convertUI);

                        swapContainers(convertUI);
                    }
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {

                            List<TabLoggingEventWrapper> logEvents = isatabValidator.getLog();

                            List<ISAFileErrorReport> errors = new ArrayList<ISAFileErrorReport>();

                            Map<String, List<ErrorMessage>> fileToErrors = new HashMap<String, List<ErrorMessage>>();

                            for (TabLoggingEventWrapper event : logEvents) {

                                String fileName = ValidationUtils.extractFileInformation(event.getLogEvent());

                                if (fileName != null) {
                                    if (event.getLogEvent().getLevel().toInt() >= Level.WARN_INT) {
                                        if (!fileToErrors.containsKey(fileName)) {
                                            fileToErrors.put(fileName, new ArrayList<ErrorMessage>());
                                        }
                                        fileToErrors.get(fileName).add(new ErrorMessage(event.getLogEvent().getLevel() == Level.WARN ? ErrorLevel.WARNING : ErrorLevel.ERROR, event.getLogEvent().getMessage().toString()));
                                    }
                                }
                            }

                            for (String fileName : fileToErrors.keySet()) {

                                ISAPair<Assay, FileType> assayAndType = ValidationUtils.resolveFileTypeFromFileName(fileName,
                                        isacreatorEnvironment.getDataEntryEnvironment().getInvestigation());

                                errors.add(new ISAFileErrorReport(fileName,
                                        assayAndType.fst != null ? assayAndType.fst.getTechnologyType() : "",
                                        assayAndType.fst != null ? assayAndType.fst.getMeasurementEndpoint() : "",
                                        assayAndType.snd, fileToErrors.get(fileName)));
                            }

                            if (fileToErrors.size() > 0) {
                                ErrorReporterView view = new ErrorReporterView(errors);
                                view.setPreferredSize(new Dimension(750, 440));
                                view.createGUI();

                                swapContainers(view);
                            } else {
                                Container successfulValidationContainer = UIHelper.padComponentVerticalBox(70, new JLabel(validationSuccess));
                                swapContainers(successfulValidationContainer);
                            }
                        }
                    });
                }

            }
        });

        performer.start();
    }

    private void convertISAtab(BIIObjectStore store, AllowedConversions conversion,
                               String isatabLocation, String outputLocation) {


        GUIISATABConverter converter = new GUIISATABConverter();
        GUIInvokerResult result = converter.convert(store, isatabLocation, outputLocation, conversion);

        if (result == GUIInvokerResult.SUCCESS) {

            Box successContainer = Box.createVerticalBox();

            successContainer.add(Box.createVerticalStrut(50));
            successContainer.add(UIHelper.wrapComponentInPanel(new JLabel(conversionSuccess)));

            successContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("<html>" +
                    "<b>Conversion was a success.</b>" +
                    "<p>Files stored in " + outputLocation + "</p>" +
                    "</html>", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR)));

            swapContainers(successContainer);

        } else {

            List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

            for (TabLoggingEventWrapper tlew : converter.getLog()) {
                LoggingEvent le = tlew.getLogEvent();
                if (le.getLevel() == Level.ERROR) {
                    messages.add(new ErrorMessage(ErrorLevel.ERROR, le.getMessage().toString()));
                }
            }

            ConversionErrorUI errorContainer = new ConversionErrorUI();
            errorContainer.constructErrorPane(messages);
            errorContainer.setPreferredSize(new Dimension(750, 440));

            swapContainers(errorContainer);
        }
    }


    private Collection<ConversionTarget> constructConversionTargets() {
        Map<String, ConversionTarget> conversionTargets = new HashMap<String, ConversionTarget>();

        Map<String, Study> studies = isacreatorEnvironment.getDataEntryEnvironment().getInvestigation().getStudies();

        int totalAssays = 0;

        for (Study study : studies.values()) {
            boolean add = false;
            AllowedConversions conversionType = null;
            for (Assay assay : study.getAssays().values()) {
                String technology = assay.getTechnologyType();

                if (technology.contains(FileType.MICROARRAY.getType())) {
                    add = true;
                    conversionType = AllowedConversions.MAGETAB;
                } else if (technology.contains(FileType.MASS_SPECTROMETRY.getType())) {
                    add = true;
                    conversionType = AllowedConversions.PRIDEML;
                } else if (technology.contains(FileType.NMR.getType())) {
                    add = true;
                    conversionType = AllowedConversions.PRIDEML;
                } else if (technology.contains(FileType.SEQUENCING.getType())) {
                    add = true;
                    conversionType = AllowedConversions.SRA;
                }

                if (add) {
                    if (!conversionTargets.containsKey(technology)) {
                        conversionTargets.put(technology, new ConversionTarget(conversionType, 1, false));
                    } else {
                        conversionTargets.get(technology).incrementNumValidAssays();
                    }
                    totalAssays++;
                }

            }
        }

        conversionTargets.put("all", new ConversionTarget(AllowedConversions.ALL, totalAssays, true));

        return conversionTargets.values();
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }

}
