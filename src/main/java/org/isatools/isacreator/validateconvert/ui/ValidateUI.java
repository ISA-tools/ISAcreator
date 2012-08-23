package org.isatools.isacreator.validateconvert.ui;

/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

import com.sun.awt.AWTUtilities;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.isatools.errorreporter.model.*;
import org.isatools.errorreporter.ui.ErrorReporterView;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isatab.gui_invokers.AllowedConversions;
import org.isatools.isatab.gui_invokers.GUIISATABConverter;
import org.isatools.isatab.gui_invokers.GUIISATABValidator;
import org.isatools.isatab.gui_invokers.GUIInvokerResult;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
import org.isatools.tablib.utils.BIIObjectStore;
import org.isatools.tablib.utils.logging.TabLoggingEventWrapper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import uk.ac.ebi.utils.collections.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class ValidateUI extends JFrame {

    @InjectedResource
    private Image validateIcon, validateIconInactive, convertIcon, convertIconInactive;

    @InjectedResource
    private ImageIcon validationSuccess, conversionSuccess, saveISAtab;

    private ISAcreator isacreatorEnvironment;

    private static Logger log = Logger.getLogger(ValidateUI.class.getName());

    protected static ImageIcon validateISAAnimation = new ImageIcon(ValidateUI.class.getResource("/images/validator/validating.gif"));
    protected static ImageIcon convertISAAnimation = new ImageIcon(ValidateUI.class.getResource("/images/validator/converting.gif"));

    public static final float DESIRED_OPACITY = .93f;

    private JPanel swappableContainer;
    private OperatingMode mode;

    public ValidateUI(ISAcreator isacreatorEnvironment, OperatingMode mode) {
        this.mode = mode;

        ResourceInjector.get("validateconvert-package.style").inject(this);

        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    public void createGUI() {

        setTitle(mode == OperatingMode.VALIDATE ? "Validate ISAtab" : "Convert ISAtab");
        setUndecorated(true);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        if (GraphicsUtils.isWindowTransparencySupported()) {
            AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);
        }

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

                log.info("Current ISA-Tab is: " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));

                if (!new File(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB)).exists()) {
                    log.warn("Current ISA Tab file doesn't exist in the file system...");
                    Container saveISAtabContainer = UIHelper.padComponentVerticalBox(70, new JLabel(saveISAtab));
                    swapContainers(saveISAtabContainer);
                } else {
                    log.info("Saving current ISAtab file");
                    ApplicationManager.getCurrentApplicationInstance().saveISATab();
                    log.info("ISAtab file saved");

                    ISAConfigurationSet.setConfigPath(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

                    final GUIISATABValidator isatabValidator = new GUIISATABValidator();

                    GUIInvokerResult result = isatabValidator.validate(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));

                    boolean strictValidationEnabled = Boolean.valueOf(ISAcreatorProperties.getProperty(ISAcreatorProperties.STRICT_VALIDATION));
                    log.info("Strict validation on? " + strictValidationEnabled);

                    final Map<String, List<ErrorMessage>> errorMessages = getErrorMessages(isatabValidator.getLog());

                    boolean shouldShowErrors = strictValidationEnabled && errorMessages.size() > 0;

                    if (result == GUIInvokerResult.SUCCESS && !shouldShowErrors) {

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
                        }
                    } else {
                        log.info("Showing errors and warnings...");
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                displayValidationErrorsAndWarnings(errorMessages);
                            }
                        });
                    }
                }
            }
        });

        performer.start();
    }

    private void displayValidationErrorsAndWarnings(Map<String, List<ErrorMessage>> fileToErrors) {
        List<ISAFileErrorReport> errors = new ArrayList<ISAFileErrorReport>();
        for (String fileName : fileToErrors.keySet()) {

            Pair<Assay, FileType> assayAndType = ValidationUtils.resolveFileTypeFromFileName(fileName,
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
            view.add(UIHelper.createLabel("<html>Validation performed using <i>"
                    + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION) + "</i></html>"),
                    BorderLayout.SOUTH);

            swapContainers(view);
        } else {
            Container successfulValidationContainer = UIHelper.padComponentVerticalBox(70, new JLabel(validationSuccess));
            swapContainers(successfulValidationContainer);
        }
    }

    private Map<String, List<ErrorMessage>> getErrorMessages(List<TabLoggingEventWrapper> logEvents) {
        Map<String, List<ErrorMessage>> fileToErrors = new HashMap<String, List<ErrorMessage>>();

        for (TabLoggingEventWrapper event : logEvents) {
            String fileName = ErrorUtils.extractFileInformation(event.getLogEvent());

            if (fileName != null) {
                if (event.getLogEvent().getLevel().toInt() >= Level.WARN_INT) {
                    if (!fileToErrors.containsKey(fileName)) {
                        fileToErrors.put(fileName, new ArrayList<ErrorMessage>());
                    }
                    fileToErrors.get(fileName).add(new ErrorMessage(event.getLogEvent().getLevel() == Level.WARN ? ErrorLevel.WARNING : ErrorLevel.ERROR, event.getLogEvent().getMessage().toString()));
                }
            }
        }
        return fileToErrors;
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
