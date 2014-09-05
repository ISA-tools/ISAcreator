package org.isatools.isacreator.validateconvert.ui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.sun.awt.AWTUtilities;
import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.isacreator.autofilteringlist.FilterableListCellRenderer;
import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.SimpleListCellRenderer;
import org.isatools.isacreator.effects.SingleSelectionListCellRenderer;
import org.isatools.isacreator.launch.ISAcreatorGUIProperties;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.validateconvert.ui.ENAReceipt.ENAReceipt;
import org.isatools.isacreator.validateconvert.ui.ENAReceipt.ENAReceiptParser;
import org.isatools.isatab.export.sra.submission.ENAResponse;
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
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

/**
 * User intereface for ENA submission.
 */
public class ENASubmissionUI extends CommonValidationConversionUI {

    @InjectedResource
    private ImageIcon submitIcon, created_by, new_sub, new_sub_over, update_sub, update_sub_over,
            box_icon, metadata_icon, submission_complete, submission_failed, dev_server, dev_server_over, prod_server,
            prod_server_over, test_server, test_server_over;

    public static final float DESIRED_OPACITY = .98f;

    private static Logger log = Logger.getLogger(ENASubmissionUI.class.getName());
    private Container metadataPanel, menuPanel, serverPanel;


    private JTextField username, centerName, labName, brokerName, studyIdentifier;
    private JPasswordField password;
    private ENARestServer server = ENARestServer.TEST;

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

        final JLabel newSubmission = new JLabel(new_sub);

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
                swapContainers(chooseServerUI());
            }
        });

        final JLabel updateSubmission = new JLabel(update_sub);
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
                swapContainers(chooseServerUI());
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

    private Container chooseServerUI() {
        serverPanel = Box.createVerticalBox();
        serverPanel.setPreferredSize(new Dimension(530, 390));

        addHeaderImageToContainer(serverPanel);

        serverPanel.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Please choose a server to upload to: ", UIHelper.VER_14_PLAIN, UIHelper.EMERALD)));
        serverPanel.add(Box.createVerticalStrut(10));

        final JLabel testServer = new JLabel(test_server);
        testServer.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                testServer.setIcon(test_server_over);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
                testServer.setIcon(test_server);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                testServer.setIcon(test_server);
                server = ENARestServer.TEST;
                swapContainers(createMetadataEntryUI());
            }
        });


        final JLabel devServer = new JLabel(dev_server);
        devServer.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                devServer.setIcon(dev_server_over);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
                devServer.setIcon(dev_server);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                devServer.setIcon(dev_server);
                server = ENARestServer.DEV;
                swapContainers(createMetadataEntryUI());
            }
        });

        final JLabel prodServer = new JLabel(prod_server);
        prodServer.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
                prodServer.setIcon(prod_server_over);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
                prodServer.setIcon(prod_server);
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                prodServer.setIcon(prod_server);
                server = ENARestServer.PROD;
                swapContainers(createMetadataEntryUI());
            }
        });

        Box menuContainer = Box.createHorizontalBox();
        menuContainer.add(testServer);
        menuContainer.add(Box.createHorizontalStrut(5));
        menuContainer.add(devServer);
        menuContainer.add(Box.createHorizontalStrut(5));
        menuContainer.add(prodServer);


        serverPanel.add(menuContainer);

        serverPanel.add(Box.createVerticalStrut(70));

        return serverPanel;
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

        metadataPanel.add(Box.createVerticalStrut(sraAction.equals("MODIFY") ? 20 : 30));
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

        userLoginSection.add(sraAction.equals("MODIFY")
                ? Box.createVerticalStrut(75) :
                Box.createVerticalStrut(45));

        JEditorPane registerInfo = new JEditorPane();
        registerInfo.setPreferredSize(new Dimension(230, 50));
        //UIHelper.renderComponent(registerInfo, UIHelper.VER_9_PLAIN, UIHelper.GREY_COLOR, false);
        registerInfo.setContentType("text/html");
        registerInfo.setEditable(false);
        //registerInfo.setEditorKit(new HTMLEditorKit());
        String label = "<html><p style=\"color: #888888; font-family: 'Verdana'; font-size: 9px\">Don’t have an account? <span style=\"color:#4FBA6F\">Create one in <a href=\"https://www.ebi.ac.uk/metagenomics/register\">EBI metagenomics</a> or <a href=\"https://www.ebi.ac.uk/ena/submit/sra/#registration\">EBI ENA</a></span></p></html>";
        registerInfo.setText(label);
        registerInfo.setVisible(true);

        registerInfo.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });


        userLoginSection.add(Box.createVerticalStrut(30));
        userLoginSection.add(registerInfo);

        return userLoginSection;
    }

    private Box createMetadataSection() {
        Box metadataSection = Box.createVerticalBox();

        JLabel metadataDetails = new JLabel("Additional Metadata", metadata_icon, JLabel.LEFT);
        UIHelper.renderComponent(metadataDetails, UIHelper.VER_12_BOLD, UIHelper.EMERALD, false);
        metadataSection.add(UIHelper.wrapComponentInPanel(metadataDetails));

        if (sraAction.equals("MODIFY")) {
            studyIdentifier = new JTextField("e.g. ERAxxxxxx");
            metadataSection.add(createMetadataFieldContainer(studyIdentifier, "Study Accession"));
            metadataSection.add(Box.createVerticalStrut(5));
        }

        centerName = new JTextField("OXFORD");
        metadataSection.add(createMetadataFieldContainer(centerName, "SRA Centre Name"));
        metadataSection.add(Box.createVerticalStrut(5));

        brokerName = new JTextField("OXFORD");
        metadataSection.add(createMetadataFieldContainer(brokerName, "Broker Name", 0, 35));
        metadataSection.add(Box.createVerticalStrut(5));

        labName = new JTextField("Oxford e-Research Centre");
        metadataSection.add(createMetadataFieldContainer(labName, "SRA Lab Name", 0, 30));
        metadataSection.add(Box.createVerticalStrut(50));
//        metadataSection.add(Box.createVerticalGlue());

        JEditorPane submissionInfo = new JEditorPane();
        submissionInfo.setPreferredSize(new Dimension(350, 40));
        //UIHelper.renderComponent(submissionInfo, UIHelper.VER_9_PLAIN, new Color(127, 140, 141), false);
        submissionInfo.setContentType("text/html");
        submissionInfo.setEditable(false);
        submissionInfo.setEditorKit(new HTMLEditorKit());
        String label = "<html><p style=\"color: #888888; font-family: 'Verdana'; font-size: 9px\"><span style=\"color:#4FBA6F; \"><a href=\"http://www.ebi.ac.uk/ena/about/sra_rest_submissions\">Read more</a></span> about ENA Submission Requirements...</p></html>";
        submissionInfo.setText(label);
        submissionInfo.setVisible(true);

        submissionInfo.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (URISyntaxException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        metadataSection.add(submissionInfo);

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
        fieldContainer.setOpaque(true);
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

                ENAResponse response = submitter.submit(server, username.getText(), new String(password.getPassword()), sraFolder);

                if (response!=null){

                    int status = response.getStatus_code();
                    String message = response.getReceipt();

                    System.out.println("STATUS is " + status);
                    System.out.println("RECEIPT/MESSAGE is " + message);
                    ENAReceipt receipt = null;

                    if (status == 406) {
                        swapContainers(createSubmitFailed(message));

                    } else if (status != 200) {

                        receipt = ENAReceiptParser.parseReceipt(message);
                        swapContainers(createSubmitFailed(receipt));

                    } else {

                        receipt = ENAReceiptParser.parseReceipt(message);
                        if (receipt.getErrors().size() > 0) {
                            swapContainers(createSubmitFailed(receipt));
                        } else {
                            swapContainers(createSubmitComplete(receipt));
                        }
                    }
                }
            }
        });
        performer.start();
    }


    private void validateConvertAndSubmitFiles() {
        log.info("Current ISA-Tab is: " + ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));
        ISAConfigurationSet.setConfigPath(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_CONFIGURATION));

        Investigation investigation = ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation();

        ApplicationManager.getCurrentApplicationInstance().saveISATab();

        ISAcreatorProperties.setProperty("sra_username", username.getText());
        final Set<String> studies = new HashSet<String>();
        for (Study study : investigation.getStudies().values()) {
            studies.add(study.getStudyId());

            study.addComment("Comment[SRA Submission Action]", sraAction);
            study.addComment("Comment[SRA Broker Name]", brokerName.getText());
            study.addComment("Comment[SRA Lab Name]", labName.getText());
            study.addComment("Comment[SRA Center Name]", centerName.getText());
            study.addComment("Comment[Study Accession]", sraAction.equals("MODIFY") ? studyIdentifier.getText() : "");

        }

        // We're changing the study comments programmatically, so to avoid saving with values currently in the interface,
        // we save by forcing the UI to not update. This preserves the comment values we've specified above.
        ISAcreatorProperties.setProperty("DO_NOT_UPDATE_FROM_GUI", "true");
        ApplicationManager.getCurrentApplicationInstance().saveISATab();
        ISAcreatorProperties.setProperty("DO_NOT_UPDATE_FROM_GUI", "false");

        swapContainers(UIHelper.padComponentVerticalBox(100, new JLabel(convertISAAnimation)));
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {


                final GUIISATABValidator isatabValidator = new GUIISATABValidator();
                GUIInvokerResult result = isatabValidator.validate(ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB));
                final Map<String, List<ErrorMessage>> errorMessages = getErrorMessages(isatabValidator.getLog());

                boolean strictValidationEnabled = Boolean.valueOf(ISAcreatorProperties.getProperty(ISAcreatorProperties.STRICT_VALIDATION));
                log.info("Strict validation on? " + strictValidationEnabled);

                boolean shouldShowErrors = strictValidationEnabled && errorMessages.size() > 0;

                if (result == GUIInvokerResult.SUCCESS && !shouldShowErrors) {

                    String outputLocation = System.getProperty("java.io.tmpdir") + "sra/" + System.currentTimeMillis() + "/";
                    new File(outputLocation).mkdirs();
                    result = convertISAtab(isatabValidator.getStore(), AllowedConversions.SRA,
                            ISAcreatorProperties.getProperty(ISAcreatorProperties.CURRENT_ISATAB),
                            outputLocation);


                    if (result == GUIInvokerResult.SUCCESS || result == GUIInvokerResult.WARNING) {
                        for (String study : studies) {
                            submit(outputLocation + "sra/" + study + "/");
                        }
                    }

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
        });
    }

    private Box createSubmitProgressContainer() {
        Box submitProgressContainer = Box.createVerticalBox();
        submitProgressContainer.add(Box.createVerticalStrut(40));
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submitENAAnimation)));
        return submitProgressContainer;
    }

    private Container createSubmitComplete(ENAReceipt receipt) {
        JPanel submitProgressContainer = new JPanel(new BorderLayout());
        submitProgressContainer.setPreferredSize(new Dimension(600, 420));
        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submission_complete)), BorderLayout.NORTH);

        // create 3 lists with the Sample, Experiment and Runs accessions

        JPanel listPanel = new JPanel(new GridLayout(1, 3));
        listPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 20));
        listPanel.setSize(new Dimension(600, 420));

        JList experimentList = new JList(receipt.getExperimentAccessions().toArray());
        JList runList = new JList(receipt.getRunAccessions().toArray());
        JList sampleList = new JList(receipt.getSampleAccessions().toArray());


        Box experimentListContainer = Box.createVerticalBox();
        JScrollPane experimentScroller = createScrollerForList(experimentList);

        experimentListContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Experiments", UIHelper.VER_11_BOLD, UIHelper.NEPHRITIS)));
        experimentListContainer.add(experimentScroller);
        listPanel.add(experimentListContainer);

        Box runListContainer = Box.createVerticalBox();

        runListContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Runs", UIHelper.VER_11_BOLD, UIHelper.NEPHRITIS)));
        JScrollPane runScroller = createScrollerForList(runList);
        runListContainer.add(runScroller);

        listPanel.add(runListContainer);

        Box sampleListContainer = Box.createVerticalBox();

        sampleListContainer.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Samples", UIHelper.VER_11_BOLD, UIHelper.NEPHRITIS)));
        JScrollPane sampleScroller = createScrollerForList(sampleList);
        sampleListContainer.add(sampleScroller);

        listPanel.add(sampleListContainer);


        submitProgressContainer.add(listPanel);

        FlatButton nextButton = new FlatButton(ButtonType.RED, "Close");
        nextButton.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                setVisible(false);
                ENASubmissionUI.this.dispose();
            }
        });


        submitProgressContainer.add(UIHelper.wrapComponentInPanel(nextButton), BorderLayout.SOUTH);

        return submitProgressContainer;
    }

    private JScrollPane createScrollerForList(JList experimentList) {
        experimentList.setCellRenderer(new SimpleListCellRenderer());
        JScrollPane experimentScroller = new JScrollPane(experimentList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        experimentScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(experimentScroller);
        return experimentScroller;
    }

    private Box createSubmitFailed(String message) {
        Box submitProgressContainer = Box.createVerticalBox();

        submitProgressContainer.add(UIHelper.wrapComponentInPanel(new JLabel(submission_failed)));

        ConversionErrorUI errorContainer = new ConversionErrorUI();

        List<ErrorMessage> errorMessages = new ArrayList();
        errorMessages.add(new ErrorMessage(ErrorLevel.ERROR, message));
        errorContainer.constructErrorPane(errorMessages);
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

    public static void main(String[] args) {
        ISAcreatorGUIProperties.setProperties();
        ENASubmissionUI ui = new ENASubmissionUI();
        ui.createGUI();
        ui.setVisible(true);

        Set<String> experiments = new HashSet<String>();
        experiments.add("ERX546955");
        experiments.add("ERX546956");
        experiments.add("ERX546957");
        experiments.add("ERX546958");
        experiments.add("ERX546959");
        experiments.add("ERX546960");
        experiments.add("ERX546961");
        experiments.add("ERX546962");

        Set<String> runs = new HashSet<String>();
        runs.add("ERR546955");
        runs.add("ERR546956");
        runs.add("ERR546957");
        runs.add("ERR546958");
        runs.add("ERR546959");
        runs.add("ERR546960");
        runs.add("ERR546961");
        runs.add("ERR546962");

        Set<String> samples = new HashSet<String>();
        samples.add("ERS546955");
        samples.add("ERS546956");
        samples.add("ERS546957");
        samples.add("ERS546958");
        samples.add("ERS546959");
        samples.add("ERS546960");
        samples.add("ERS546961");
        samples.add("ERS546962");

        ENAReceipt receipt = new ENAReceipt(experiments, samples, runs, new HashSet<String>());
//        ui.swapContainers(ui.createSubmitComplete(receipt));
    }

}