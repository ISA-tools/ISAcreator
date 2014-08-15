package org.isatools.isacreator.gui.submission;

import com.sun.awt.AWTUtilities;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gui.ISAcreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

/**
 * User intereface for ENA submission.
 *
 *
 *
 */
public class ENASubmissionUI extends JFrame {

    private ISAcreator isacreatorEnvironment;

    @InjectedResource
    private Image submitIcon, submitIconInactive;

    public static final float DESIRED_OPACITY = .93f;

    private static Logger log = Logger.getLogger(ENASubmissionUI.class.getName());
    private JPanel swappableContainer;
    protected static ImageIcon submitENAAnimation = new ImageIcon(ENASubmissionUI.class.getResource("/images/submission/submitting.gif"));

    public ENASubmissionUI(ISAcreator isacreatorEnvironment) {
        ResourceInjector.get("submission-package.style").inject(this);
        this.isacreatorEnvironment = isacreatorEnvironment;
    }

    public void createGUI() {
        setTitle("Submit to ENA");
        setUndecorated(true);

        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        if (GraphicsUtils.isWindowTransparencySupported()) {
            AWTUtilities.setWindowOpacity(this, DESIRED_OPACITY);
        }

        HUDTitleBar titlePanel = new HUDTitleBar(submitIcon, submitIconInactive);

        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

//        Container loadingInfo = UIHelper.padComponentVerticalBox(100, new JLabel(submitENAAnimation));
//
//        swappableContainer = new JPanel();
//        swappableContainer.add(loadingInfo);
//        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
//        swappableContainer.setPreferredSize(new Dimension(750, 450));
//
//        add(swappableContainer, BorderLayout.CENTER);

        FooterPanel footer = new FooterPanel(this);
        add(footer, BorderLayout.SOUTH);

        pack();

    }

    public void submit(){

    }

}
