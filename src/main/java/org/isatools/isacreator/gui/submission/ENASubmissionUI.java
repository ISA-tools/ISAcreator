package org.isatools.isacreator.gui.submission;

import com.sun.awt.AWTUtilities;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.launch.ISAcreatorGUIProperties;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 * User intereface for ENA submission.
 */
public class ENASubmissionUI extends JFrame {

    @InjectedResource
    private ImageIcon saveISAtab, submitIcon, created_by, new_sub, new_sub_over, update_sub, update_sub_over,
            box_icon, metadata_icon;

    public static final float DESIRED_OPACITY = .98f;

    private static Logger log = Logger.getLogger(ENASubmissionUI.class.getName());
    private JPanel swappableContainer;

    private JLabel newSubmission, updateSubmission;

    private JTextField username, centerName, labName, brokerName;
    private JPasswordField password;

    protected static ImageIcon submitENAAnimation = new ImageIcon(ENASubmissionUI.class.getResource("/images/submission/submitting.gif"));

    public static ENASubmissionUI createENASubmissionUI() {
        return new ENASubmissionUI();
    }

    private ENASubmissionUI() {
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

        swappableContainer = new JPanel();
        swappableContainer.add(createMenu());
        swappableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));
        swappableContainer.setPreferredSize(new Dimension(750, 450));

        add(swappableContainer, BorderLayout.CENTER);

        pack();

    }

    public Container createMenu() {
        Box container = Box.createVerticalBox();

        addHeaderImageToContainer(container);

        newSubmission = new JLabel(new_sub);

        newSubmission.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                newSubmission.setIcon(new_sub_over);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
                newSubmission.setIcon(new_sub);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                newSubmission.setIcon(new_sub);
                swapContainers(createMetadataEntryUI());
            }
        });

        updateSubmission = new JLabel(update_sub);
        updateSubmission.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                updateSubmission.setIcon(update_sub_over);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
                updateSubmission.setIcon(update_sub);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                updateSubmission.setIcon(update_sub);

                submit();
            }
        });

        Box menuContainer = Box.createHorizontalBox();
        menuContainer.add(newSubmission);
        menuContainer.add(updateSubmission);

        container.add(menuContainer);

        JPanel created_by_panel = new JPanel();
        created_by_panel.setBackground(new Color(236, 240, 241));
        container.add(UIHelper.wrapComponentInPanel(new JLabel(created_by)));

        container.add(Box.createVerticalStrut(20));

        return container;
    }


    private Container createMetadataEntryUI() {
        Box metadataEntryContainer = Box.createVerticalBox();


        addHeaderImageToContainer(metadataEntryContainer);

        Box leftAndRightSections = Box.createHorizontalBox();

        Box userLoginSection = createUserLoginSection();
        leftAndRightSections.add(userLoginSection);

        leftAndRightSections.add(Box.createHorizontalStrut(10));
        Box metadataSection = createMetadataSection();
        leftAndRightSections.add(metadataSection);

        metadataEntryContainer.add(leftAndRightSections);

        Box buttonContainer = Box.createHorizontalBox();
        FlatButton backButton = new FlatButton(ButtonType.RED, "Back");
        FlatButton nextButton = new FlatButton(ButtonType.EMERALD, "Next");
        nextButton.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                submit();
            }
        });

        buttonContainer.add(backButton);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(nextButton);


        metadataEntryContainer.add(Box.createVerticalStrut(70));
        metadataEntryContainer.add(buttonContainer);

        return metadataEntryContainer;
    }

    private Box createUserLoginSection() {
        Box userLoginSection = Box.createVerticalBox();

        JLabel enaBoxDetails = new JLabel("ENA Dropbox Credentials", box_icon, JLabel.LEFT);
        enaBoxDetails.setHorizontalAlignment(SwingConstants.LEFT);
        UIHelper.renderComponent(enaBoxDetails, UIHelper.VER_12_BOLD, UIHelper.EMERALD, false);
        userLoginSection.add(UIHelper.wrapComponentInPanel(enaBoxDetails));

        userLoginSection.add(createMetadataFieldContainer(username, "Username"));
        userLoginSection.add(Box.createVerticalStrut(5));
        userLoginSection.add(createMetadataFieldContainer(password, "Password", 1, 10));
        userLoginSection.add(Box.createVerticalStrut(55));

        JLabel info = UIHelper.createLabel("<html>Don’t have an account? <span style=\"color:#4FBA6F\">Create one...</span></html>", UIHelper.VER_9_PLAIN, new Color(127, 140, 141));
        userLoginSection.add(UIHelper.wrapComponentInPanel(info));

        return userLoginSection;
    }

    private Box createMetadataSection() {
        Box metadataSection = Box.createVerticalBox();

        JLabel metadataDetails = new JLabel("Additional Metadata", metadata_icon, JLabel.LEFT);
        UIHelper.renderComponent(metadataDetails, UIHelper.VER_12_BOLD, UIHelper.EMERALD, false);
        metadataSection.add(UIHelper.wrapComponentInPanel(metadataDetails));


        metadataSection.add(createMetadataFieldContainer(centerName, "SRA Centre Name"));
        metadataSection.add(Box.createVerticalStrut(5));
        metadataSection.add(createMetadataFieldContainer(brokerName, "Broker Name", 0, 35));
        metadataSection.add(Box.createVerticalStrut(5));
        metadataSection.add(createMetadataFieldContainer(labName, "SRA Lab Name", 0, 30));
        metadataSection.add(Box.createVerticalStrut(20));

        JLabel info = UIHelper.createLabel("<html><span style=\"color:#4FBA6F\">Read more</span> about ENA Submission Requirements...</html>", UIHelper.VER_9_PLAIN, new Color(127, 140, 141));
        metadataSection.add(UIHelper.wrapComponentInPanel(info));


        return metadataSection;
    }

    private Container createMetadataFieldContainer(JTextField field, String fieldName) {
        return createMetadataFieldContainer(field, fieldName, 0, 10);
    }

    /**
     * @param field     - Field to be created and added
     * @param fieldName - Name to be given to the field
     * @param type      - 0 for JTextField, 1 for JPasswordField
     * @return a container with the field and it's label in a grey box.
     */
    private Container createMetadataFieldContainer(JTextField field, String fieldName, int type, int padding) {


        if (type == 1) {
            field = new JPasswordField();
        } else {
            field = new JTextField(fieldName);
        }

        field.setSize(new Dimension(200, 25));
        field.setOpaque(true);

        UIHelper.renderComponent(field, UIHelper.VER_10_PLAIN, UIHelper.EMERALD, UIHelper.VERY_LIGHT_GREY_COLOR);

        field.setBorder(null);

        Box fieldContainer = createFieldDetailWrapper(null, fieldName, padding);
        fieldContainer.add(field);

        return fieldContainer;
    }



    private Box createFieldDetailWrapper(ImageIcon image_icon, String text, int padding) {
        Box fieldContainer = Box.createHorizontalBox();
        fieldContainer.setBackground(UIHelper.VERY_LIGHT_GREY_COLOR);
        fieldContainer.setBorder(BorderFactory.createLineBorder(UIHelper.VERY_LIGHT_GREY_COLOR, 8));

        if (image_icon != null) {
            JLabel icon = new JLabel(image_icon);
            icon.setOpaque(true);
            icon.setBackground(UIHelper.VERY_LIGHT_GREY_COLOR);
            fieldContainer.add(icon);

        }

        if (text != null) {
            JLabel label = UIHelper.createLabel(text, UIHelper.VER_10_BOLD, new Color(127, 140, 141));
            label.setOpaque(true);
            label.setBackground(UIHelper.VERY_LIGHT_GREY_COLOR);
            fieldContainer.add(UIHelper.wrapComponentInPanel(label));
        }

        Component space = Box.createHorizontalStrut(padding);
        ((JComponent) space).setOpaque(true);
        space.setBackground(UIHelper.VERY_LIGHT_GREY_COLOR);

        fieldContainer.add(space);
        return fieldContainer;
    }


    private void submit() {

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

                    Box submitProgressContainer = createSubmitProgressContainer();

                    swapContainers(submitProgressContainer);
                }
            }
        });

        performer.start();

    }

    private Box createSubmitProgressContainer() {
        Box submitProgressContainer = Box.createVerticalBox();
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submitENAAnimation)));
        return submitProgressContainer;
    }

    private void addHeaderImageToContainer(Box submitProgressContainer) {
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submitIcon)));
        submitProgressContainer.add(Box.createVerticalStrut(20));
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

    public static void main(String[] args) {
        ISAcreatorGUIProperties.setProperties();
        ENASubmissionUI ui = createENASubmissionUI();
        ui.createGUI();
        ui.setVisible(true);
    }

}
