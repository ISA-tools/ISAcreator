package org.isatools.isacreator.validateconvert.ui;

import com.sun.awt.AWTUtilities;
import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.validateconvert.ui.ENAReceipt.ENAReceipt;
import org.isatools.isacreator.validateconvert.ui.ENAReceipt.ENAReceiptParser;
import org.isatools.isatab.export.sra.submission.ENARestServer;
import org.isatools.isatab.export.sra.submission.SRASubmitter;
import org.isatools.isatab.gui_invokers.AllowedConversions;
import org.isatools.isatab.gui_invokers.GUIISATABValidator;
import org.isatools.isatab.gui_invokers.GUIInvokerResult;
import org.isatools.isatab.isaconfigurator.ISAConfigurationSet;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * User intereface for ENA submission.
 */
public class ENASubmissionUI extends CommonValidationConversionUI {

    @InjectedResource
    private ImageIcon submitIcon, created_by, new_sub, new_sub_over, update_sub, update_sub_over,
            box_icon, metadata_icon, submission_complete, submission_failed;

    public static final float DESIRED_OPACITY = .98f;

    private static Logger log = Logger.getLogger(ENASubmissionUI.class.getName());
    private Container metadataPanel, menuPanel;

    private JLabel newSubmission, updateSubmission;

    private JTextField username, centerName, labName, brokerName;
    private JPasswordField password;

    private String sraAction;

    protected static ImageIcon submitENAAnimation = new ImageIcon(ENASubmissionUI.class.getResource("/images/submission/submitting.gif"));
    protected static ImageIcon convertISAAnimation = new ImageIcon(ENASubmissionUI.class.getResource("/images/validator/converting.gif"));

    public static ENASubmissionUI createENASubmissionUI() {
        return new ENASubmissionUI();
    }

    private ENASubmissionUI() {
        ResourceInjector.get("submission-package.style").inject(true, this);
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
        menuPanel = Box.createVerticalBox();

        addHeaderImageToContainer(menuPanel);

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
                sraAction = "ADD";
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
                sraAction = "MODIFY";
                swapContainers(createMetadataEntryUI());
            }
        });

        Box menuContainer = Box.createHorizontalBox();
        menuContainer.add(newSubmission);
        menuContainer.add(updateSubmission);

        menuPanel.add(menuContainer);

        JPanel created_by_panel = new JPanel();
        created_by_panel.setBackground(new Color(236, 240, 241));
        menuPanel.add(UIHelper.wrapComponentInPanel(new JLabel(created_by)));

        menuPanel.add(Box.createVerticalStrut(20));

        return menuPanel;
    }


    private Container createMetadataEntryUI() {
        metadataPanel = Box.createVerticalBox();

        addHeaderImageToContainer(metadataPanel);

        Box leftAndRightSections = Box.createHorizontalBox();

        Box userLoginSection = createUserLoginSection();
        leftAndRightSections.add(userLoginSection);

        leftAndRightSections.add(Box.createHorizontalStrut(10));
        Box metadataSection = createMetadataSection();
        leftAndRightSections.add(metadataSection);

        metadataPanel.add(leftAndRightSections);

        Box buttonContainer = Box.createHorizontalBox();
        FlatButton backButton = new FlatButton(ButtonType.RED, "Back");
        backButton.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                swapContainers(menuPanel);

            }
        });
        FlatButton nextButton = new FlatButton(ButtonType.EMERALD, "Next");
        nextButton.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                validateConvertAndSubmitFiles();
            }
        });

        buttonContainer.add(backButton);
        buttonContainer.add(Box.createHorizontalGlue());
        buttonContainer.add(nextButton);


        metadataPanel.add(Box.createVerticalStrut(70));
        metadataPanel.add(buttonContainer);

        return metadataPanel;
    }

    private Box createUserLoginSection() {
        Box userLoginSection = Box.createVerticalBox();

        JLabel enaBoxDetails = new JLabel("ENA Dropbox Credentials", box_icon, JLabel.LEFT);
        enaBoxDetails.setHorizontalAlignment(SwingConstants.LEFT);
        UIHelper.renderComponent(enaBoxDetails, UIHelper.VER_12_BOLD, UIHelper.EMERALD, false);
        userLoginSection.add(UIHelper.wrapComponentInPanel(enaBoxDetails));

        String sra_username = ISAcreatorProperties.getProperty("sra_username");
        username = new JTextField(sra_username.isEmpty() ? "Username" : sra_username);
        password = new JPasswordField("");

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

        centerName = new JTextField("OXFORD");
        metadataSection.add(createMetadataFieldContainer(centerName, "SRA Centre Name"));
        metadataSection.add(Box.createVerticalStrut(5));

        brokerName = new JTextField("OXFORD");
        metadataSection.add(createMetadataFieldContainer(brokerName, "Broker Name", 0, 35));
        metadataSection.add(Box.createVerticalStrut(5));

        labName = new JTextField("Oxford e-Research Centre");
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


    private void submit(final String sraFolder) {

        Thread performer = new Thread(new Runnable() {

            public void run() {
                log.info("Current ISA-Tab is: " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));

                Box submitProgressContainer = createSubmitProgressContainer();
                swapContainers(submitProgressContainer);

                SRASubmitter submitter = new SRASubmitter();

                String status = submitter.submit(ENARestServer.TEST, username.getText(), new String(password.getPassword()), sraFolder);

                ENAReceipt receipt = ENAReceiptParser.parseReceipt(status);
                if (status == null) {
                    swapContainers(createSubmitFailed(receipt));
                } else {

                    if (receipt.getErrors().size() > 0) {
                        swapContainers(createSubmitFailed(receipt));
                    } else {
                        swapContainers(createSubmitComplete(receipt));
                    }
                }
                System.out.println(status);
                System.out.println("Setting config path before validation to " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));


            }
        });
        performer.start();
    }


    private void validateConvertAndSubmitFiles() {
        log.info("Current ISA-Tab is: " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));
        ISAConfigurationSet.setConfigPath(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

        Investigation investigation = ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation();

        ISAcreatorProperties.setProperty("sra_username", username.getText());
        final Set<String> studies = new HashSet<String>();
        for (Study study : investigation.getStudies().values()) {
            studies.add(study.getStudyId());
            System.out.println("SRA Action is " + sraAction);
            study.addComment("Comment[SRA Submission Action]", sraAction);
            study.addComment("Comment[SRA Broker Name]", brokerName.getText());
            study.addComment("Comment[SRA Lab Name]", labName.getText());
            study.addComment("Comment[SRA Center Name]", centerName.getText());
        }

        log.info("Saving current ISAtab file");
        ApplicationManager.getCurrentApplicationInstance().saveISATab();
        log.info("ISAtab file saved");

        final GUIISATABValidator isatabValidator = new GUIISATABValidator();
        GUIInvokerResult result = isatabValidator.validate(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));
        final Map<String, List<ErrorMessage>> errorMessages = getErrorMessages(isatabValidator.getLog());

        boolean strictValidationEnabled = Boolean.valueOf(ISAcreatorProperties.getProperty(ISAcreatorProperties.STRICT_VALIDATION));
        log.info("Strict validation on? " + strictValidationEnabled);

        boolean shouldShowErrors = strictValidationEnabled && errorMessages.size() > 0;

        if (result == GUIInvokerResult.SUCCESS && !shouldShowErrors) {

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {


                    swapContainers(UIHelper.padComponentVerticalBox(100, new JLabel(convertISAAnimation)));

                    String outputLocation = System.getProperty("java.io.tmpdir") + "sra/" + System.currentTimeMillis() + "/";
                    new File(outputLocation).mkdirs();
                    GUIInvokerResult result = convertISAtab(isatabValidator.getStore(), AllowedConversions.SRA,
                            ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB),
                            outputLocation);


                    if (result == GUIInvokerResult.SUCCESS || result == GUIInvokerResult.WARNING) {
                        for (String study : studies) {
                            submit(outputLocation + "sra/" + study + "/");
                        }
                    }

                }
            });
        } else

        {
            log.info("Showing errors and warnings...");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    displayValidationErrorsAndWarnings(errorMessages);
                }
            });
        }

    }

    private Box createSubmitProgressContainer() {
        Box submitProgressContainer = Box.createVerticalBox();
        submitProgressContainer.add(Box.createVerticalStrut(40));
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submitENAAnimation)));
        return submitProgressContainer;
    }

    private Box createSubmitComplete(ENAReceipt receipt) {
        Box submitProgressContainer = Box.createVerticalBox();
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submission_complete)));

        // create 3 lists with the Sample, Experiment and Runs accessions

        Box listPanel = Box.createHorizontalBox();

        JList experimentList = new JList(receipt.getExperimentAccessions().toArray());
        JList runList = new JList(receipt.getRunAccessions().toArray());
        JList sampleList = new JList(receipt.getSampleAccessions().toArray());

        Dimension listDimension = new Dimension(200, 300);
        experimentList.setPreferredSize(listDimension);
        runList.setPreferredSize(listDimension);
        sampleList.setPreferredSize(listDimension);

        Box experimentListContainer = Box.createVerticalBox();
        experimentListContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Experiments", UIHelper.VER_11_BOLD, UIHelper.EMERALD)));
        experimentList.add(experimentList);
        listPanel.add(experimentListContainer);

        Box runListContainer = Box.createVerticalBox();
        runListContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Runs", UIHelper.VER_11_BOLD, UIHelper.EMERALD)));
        runListContainer.add(runList);
        listPanel.add(runListContainer);

        Box sampleListContainer = Box.createVerticalBox();
        sampleListContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Samples", UIHelper.VER_11_BOLD, UIHelper.EMERALD)));
        sampleListContainer.add(runList);

        listPanel.add(sampleListContainer);

        FlatButton nextButton = new FlatButton(ButtonType.RED, "Close");
        nextButton.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                setVisible(false);
                ENASubmissionUI.this.dispose();
            }
        });

        submitProgressContainer.add(Box.createVerticalStrut(80));
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(nextButton));

        return submitProgressContainer;
    }

    private Box createSubmitFailed(ENAReceipt receipt) {
        Box submitProgressContainer = Box.createVerticalBox();

        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submission_failed)));

        ConversionErrorUI errorContainer = new ConversionErrorUI();
        errorContainer.constructErrorPane(receipt.getErrorsForDisplay("Submission Errors"));
        errorContainer.setPreferredSize(new Dimension(650, 300));

        submitProgressContainer.add(errorContainer);

//        SUBMIT ANOTHER, OR BACK
        FlatButton nextButton = new FlatButton(ButtonType.RED, "Back to Submission Screen");
        nextButton.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                swapContainers(metadataPanel);
            }
        });

        submitProgressContainer.add(Box.createVerticalStrut(20));
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(nextButton));

        return submitProgressContainer;
    }

    private void addHeaderImageToContainer(Container submitProgressContainer) {
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submitIcon)));
        submitProgressContainer.add(Box.createVerticalStrut(20));
    }


}