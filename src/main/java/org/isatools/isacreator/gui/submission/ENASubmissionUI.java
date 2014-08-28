package org.isatools.isacreator.gui.submission;

import com.sun.awt.AWTUtilities;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.io.File;

/**
 * User intereface for ENA submission.
 *
 *
 *
 */
public class ENASubmissionUI extends JFrame {

    @InjectedResource
    private ImageIcon saveISAtab, submitIcon, created_by;

    public static final float DESIRED_OPACITY = .98f;

    private static Logger log = Logger.getLogger(ENASubmissionUI.class.getName());
    private JPanel swappableContainer;

    protected static ImageIcon submitENAAnimation = new ImageIcon(ENASubmissionUI.class.getResource("/images/submission/submitting.gif"));

    public ENASubmissionUI() {
        ResourceInjector.get("submission-package.style").inject(this);
    }

    public void createGUI() {
        setTitle("Submit to ENA");
        setUndecorated(true);


        setBackground(UIHelper.BG_COLOR);

        if (GraphicsUtils.isWindowTransparencySupported()) {
            AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);
        }

        HUDTitleBar titlePanel = new HUDTitleBar(null, null);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        Box container = Box.createVerticalBox();

        container.add(UIHelper.wrapComponentInPanel(new JLabel(submitIcon)));
        container.add(Box.createVerticalStrut(220));

        JPanel created_by_panel = new JPanel();
        created_by_panel.setBackground(new Color(236, 240, 241));
        container.add(UIHelper.wrapComponentInPanel(new JLabel(created_by)));

//        Container submitInfo = UIHelper.padComponentVerticalBox(100, new JLabel(submitENAAnimation));

        swappableContainer = new JPanel();
        swappableContainer.add(container);
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(750, 450));

        add(swappableContainer, BorderLayout.CENTER);

//        FooterPanel footer = new FooterPanel(this);
//        add(footer, BorderLayout.SOUTH);

        pack();

    }

    public void submit(){

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

                    System.out.println("Setting config path before validation to " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

                    ISAConfigurationSet.setConfigPath(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));


                }


            }});

        performer.start();

    }

        private void swapContainers(final Container newContainer) {

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

}
