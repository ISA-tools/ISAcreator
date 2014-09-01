package org.isatools.isacreator.validateconvert.ui;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.isatools.errorreporter.model.*;
import org.isatools.errorreporter.ui.ErrorReporterView;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isatab.gui_invokers.AllowedConversions;
import org.isatools.isatab.gui_invokers.GUIISATABConverter;
import org.isatools.isatab.gui_invokers.GUIInvokerResult;
import org.isatools.tablib.utils.BIIObjectStore;
import org.isatools.tablib.utils.logging.TabLoggingEventWrapper;
import uk.ac.ebi.utils.collections.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * User: eamonnmaguire
 * Date: 01/09/2014
 * Time: 10:54
 * To change this template use File | Settings | File Templates.
 */

public class CommonValidationConversionUI extends JFrame {

    private ImageIcon conversionSuccess = new ImageIcon("/images/validator/conversion_successful.png");
    private ImageIcon validationSuccess = new ImageIcon("/images/validator/validation_successful.png");
    public JPanel swappableContainer;

    protected void swapContainers(final Container newContainer) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (newContainer != null) {
                    swappableContainer.removeAll();
                    swappableContainer.add(newContainer);
                    swappableContainer.repaint();
                    swappableContainer.validate();
                    swappableContainer.updateUI();

                    newContainer.validate();
                    newContainer.repaint();

                    validate();
                    repaint();
                }
            }
        });

    }

    protected Map<String, List<ErrorMessage>> getErrorMessages(List<TabLoggingEventWrapper> logEvents) {
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

    protected void displayValidationErrorsAndWarnings(Map<String, List<ErrorMessage>> fileToErrors) {
        List<ISAFileErrorReport> errors = new ArrayList<ISAFileErrorReport>();
        for (String fileName : fileToErrors.keySet()) {

            Pair<Assay, FileType> assayAndType = ValidationUtils.resolveFileTypeFromFileName(fileName,
                    ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation());

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

    protected GUIInvokerResult convertISAtab(BIIObjectStore store, AllowedConversions conversion,
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

            java.util.List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

            for (TabLoggingEventWrapper tlew : converter.getLog()) {
                LoggingEvent le = tlew.getLogEvent();
                if (le.getLevel() == Level.ERROR) {
                    messages.add(new ErrorMessage(ErrorLevel.ERROR, le.getMessage().toString()));
                }
            }

            ConversionErrorUI errorContainer = new ConversionErrorUI();
            errorContainer.constructErrorPane(messages);
            errorContainer.setPreferredSize(new Dimension(650, 440));

            swapContainers(errorContainer);
        }

        return result;
    }
}
