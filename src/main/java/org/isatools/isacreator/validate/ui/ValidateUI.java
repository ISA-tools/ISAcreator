package org.isatools.isacreator.validate.ui;


import com.sun.awt.AWTUtilities;
import org.apache.log4j.Level;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.errorreporter.model.ISAFileType;
import org.isatools.errorreporter.ui.ErrorReporterView;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.menu.ImportFilesMenu;
import org.isatools.isacreator.ontologiser.ui.OntologiserAnnotationPane;
import org.isatools.isacreator.ontologiser.ui.OntologyHelpPane;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isatab.gui_invokers.GUIISATABValidator;
import org.isatools.isatab.gui_invokers.GUIInvokerResult;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
import org.isatools.tablib.utils.logging.TabLoggingEventWrapper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/08/2011
 *         Time: 13:38
 */
public class ValidateUI extends JFrame implements ActionListener {


    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("validator-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/validator-package.properties"));

    }

    @InjectedResource
    private Image validateIcon, validateIconInactive;
    private ISAcreator isacreatorEnvironment;

    protected static ImageIcon validateISAAnimation = new ImageIcon(ImportFilesMenu.class.getResource("/images/validator/validating.gif"));

    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;

    public static final float ANIMATION_DURATION = 500f;
    public static final int ANIMATION_SLEEP = 10;
    public static final float DESIRED_OPACITY = .93f;

    private Timer animationTimer;

    private boolean animating;

    private int animationDirection;
    private long animationStart;

    private JPanel swappableContainer;

    public ValidateUI(ISAcreator isacreatorEnvironment) {

        ResourceInjector.get("validator-package.style").inject(this);

        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    public void createGUI() {

        setTitle("Validate ISAtab");
        setUndecorated(true);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);

        HUDTitleBar titlePanel = new HUDTitleBar(validateIcon, validateIconInactive);
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

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        System.out.println("Validating against: " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

                        System.out.println("With " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));

                        ISAConfigurationSet.setConfigPath(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

                        final GUIISATABValidator isatabValidator = new GUIISATABValidator();
                        GUIInvokerResult result = isatabValidator.validate(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));

                        if (result == GUIInvokerResult.SUCCESS) {

                            // show success icon
                        } else {

                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {

                                    List<TabLoggingEventWrapper> logEvents = isatabValidator.getLog();

                                    List<ISAFileErrorReport> errors = new ArrayList<ISAFileErrorReport>();

                                    Map<String, Set<String>> fileToErrors = new HashMap<String, Set<String>>();

                                    for (TabLoggingEventWrapper event : logEvents) {

                                        String fileName = ValidationUtils.extractFileInformation(event.getLogEvent());

                                        if (fileName != null) {
                                            if (event.getLogEvent().getLevel().toInt() >= Level.WARN_INT) {
                                                if (!fileToErrors.containsKey(fileName)) {
                                                    fileToErrors.put(fileName, new HashSet<String>());
                                                }
                                                fileToErrors.get(fileName).add(event.getLogEvent().getMessage().toString());
                                            }
                                        }
                                    }


                                    for (String fileName : fileToErrors.keySet()) {

                                        errors.add(new ISAFileErrorReport(fileName,
                                                ValidationUtils.resolveFileTypeFromFileName(fileName,
                                                        isacreatorEnvironment.getDataEntryEnvironment().getInvestigation()),
                                                fileToErrors.get(fileName)));
                                    }

                                    ErrorReporterView view = new ErrorReporterView(errors);
                                    view.setPreferredSize(new Dimension(750, 440));
                                    view.createGUI();

                                    swapContainers(view);
                                }
                            });

                        }
                    }
                });
            }
        });


        performer.start();
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }

    public void fadeInWindow() {
        animationDirection = INCOMING;
        startAnimation();
    }

    private void startAnimation() {

        // start animation timer
        animationStart = System.currentTimeMillis();

        if (animationTimer == null) {
            animationTimer = new Timer(ANIMATION_SLEEP, this);
        }

        animating = true;

        if (!isShowing()) {
            AWTUtilities.setWindowOpacity(this, 0f);
            repaint();
            setVisible(true);
        }

        animationTimer.start();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (animating) {
            // calculate height to show
            float animationPercent = (System.currentTimeMillis() -
                    animationStart) / ANIMATION_DURATION;
            animationPercent = Math.min(DESIRED_OPACITY, animationPercent);

            float opacity;

            if (animationDirection == INCOMING) {
                opacity = animationPercent;
            } else {
                opacity = DESIRED_OPACITY - animationPercent;
            }

            AWTUtilities.setWindowOpacity(this, opacity);
            repaint();

            if (animationPercent >= DESIRED_OPACITY) {
                stopAnimation();

                if (animationDirection == OUTGOING) {
                    setVisible(false);
                }
            }
        }
    }

    private void stopAnimation() {
        animationTimer.stop();
        animating = false;

        repaint();
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ValidateUI validateUI = new ValidateUI(null);
                validateUI.createGUI();
                validateUI.validateISAtab();
                validateUI.fadeInWindow();
            }
        });

    }

}
