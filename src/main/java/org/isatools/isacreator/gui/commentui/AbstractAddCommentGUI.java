package org.isatools.isacreator.gui.commentui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.OrderedMap;
import org.apache.log4j.Logger;
import org.isatools.isacreator.common.CommonMouseAdapter;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.components.RoundedJTextField;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;


public abstract class AbstractAddCommentGUI extends JFrame {

    public static final String CUSTOM = "custom";
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 500;
    private JPanel swappableContainer;

    protected static Map<String, TableReferenceObject> templateToFields;
    private List<JLabel> labels;
    private List<JCheckBox> selectedFieldComponents;
    private Timer timer;

    private static Logger log = Logger.getLogger(AbstractAddCommentGUI.class.getName());

    static {
        templateToFields = new HashMap<String, TableReferenceObject>();


        File fieldTemplateDirectory = new File("ProgramData/field_templates/");
        if (fieldTemplateDirectory.exists()) {
            ConfigXMLParser parser = new ConfigXMLParser(fieldTemplateDirectory.getAbsolutePath());
            parser.loadConfiguration();

            for (TableReferenceObject tro : parser.getTables()) {
                templateToFields.put(tro.getTableName(), tro);
            }
        } else {
            log.info("No field_templates directory in Program data directory, so ISAcreator hasn't loaded any field templates.");
        }
    }

    public AbstractAddCommentGUI() {
        labels = new ArrayList<JLabel>();
        createGUI();
    }

    private void createGUI() {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setBackground(new Color(248, 248, 249));
        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        ((JComponent) getContentPane()).setBorder(UIHelper.EMPTY_BORDER);
        addTitlePanel();
        addSidePanel();
        addCentralPanel();
        swapContainers(getPredefinedCommentsInterface(CUSTOM));
        pack();
        setLocationRelativeTo(ApplicationManager.getCurrentApplicationInstance());
        setVisible(true);
    }

    private void addTitlePanel() {
        HUDTitleBar titleBar = new HUDTitleBar(null, null);
        add(titleBar, BorderLayout.NORTH);
        titleBar.installListeners();
    }

    private void addSidePanel() {
        JPanel sidePanel = new JPanel(new BorderLayout());

        sidePanel.setPreferredSize(new Dimension(175, 200));
        sidePanel.setBackground(UIHelper.BG_COLOR);
        sidePanel.setBorder(UIHelper.EMPTY_BORDER);

        Container sidePanelOptions = new JPanel();
        sidePanelOptions.setLayout(new BoxLayout(sidePanelOptions, BoxLayout.PAGE_AXIS));
        sidePanelOptions.setBackground(UIHelper.BG_COLOR);

        sidePanelOptions.add(UIHelper.createLabel("Add Field(s)", UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));
        sidePanelOptions.add(Box.createVerticalStrut(5));
        sidePanelOptions.add(new JSeparator());
        sidePanelOptions.add(Box.createVerticalStrut(15));

        sidePanelOptions.add(UIHelper.createLabel("Custom Field", UIHelper.VER_11_BOLD, UIHelper.LIGHT_GREY_COLOR));
        sidePanelOptions.add(Box.createVerticalStrut(5));

        JLabel addCustomCommentOption = UIHelper.createLabel("\t\t Add Custom Field", UIHelper.VER_10_BOLD, UIHelper.LIGHT_GREEN_COLOR);
        labels.add(addCustomCommentOption);
        addCustomCommentOption.addMouseListener(new CommonMouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                resetLabelStyle();
                ((JComponent) mouseEvent.getSource()).setForeground(UIHelper.LIGHT_GREEN_COLOR);
                swapContainers(getPredefinedCommentsInterface(CUSTOM));
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                super.mouseEntered(mouseEvent);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                super.mouseExited(mouseEvent);
            }
        });

        sidePanelOptions.add(addCustomCommentOption);

        sidePanelOptions.add(Box.createVerticalStrut(15));

        sidePanelOptions.add(UIHelper.createLabel("Predefined Fields", UIHelper.VER_11_BOLD, UIHelper.LIGHT_GREY_COLOR));
        sidePanelOptions.add(Box.createVerticalStrut(5));

        for (final String commentListTemplateName : templateToFields.keySet()) {
            JLabel addTemplateComments = UIHelper.createLabel(String.format("\t\t %s Fields", commentListTemplateName), UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);
            labels.add(addTemplateComments);
            addTemplateComments.addMouseListener(new CommonMouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    super.mousePressed(mouseEvent);
                    resetLabelStyle();
                    ((JComponent) mouseEvent.getSource()).setForeground(UIHelper.LIGHT_GREEN_COLOR);
                    swapContainers(getPredefinedCommentsInterface(commentListTemplateName));
                }

                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    super.mouseEntered(mouseEvent);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    super.mouseExited(mouseEvent);
                }
            });
            sidePanelOptions.add(addTemplateComments);
        }

        sidePanel.add(sidePanelOptions, BorderLayout.NORTH);

        add(sidePanel, BorderLayout.WEST);

    }

    private void resetLabelStyle() {
        for (JLabel label : labels) {
            label.setForeground(UIHelper.DARK_GREEN_COLOR);
        }
    }

    private void addCentralPanel() {
        swappableContainer = new JPanel();
        swappableContainer.setBorder(UIHelper.EMPTY_BORDER);
        swappableContainer.setBackground(UIHelper.BG_COLOR);

        add(swappableContainer, BorderLayout.CENTER);
    }

    private void swapContainers(Container newContainer) {
        if (newContainer != null) {
            swappableContainer.removeAll();
            swappableContainer.add(newContainer);
            swappableContainer.repaint();
            swappableContainer.validate();
        }
    }


    private JPanel getPredefinedCommentsInterface(final String templateName) {

        if (templateName.equals(CUSTOM)) {
            return getAddCustomPanel();
        }

        selectedFieldComponents = new ArrayList<JCheckBox>();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.BG_COLOR);
        panel.add(UIHelper.createLabel(String.format("Add %s fields to Interface", templateName)), BorderLayout.NORTH);

        Box fields = Box.createVerticalBox();
        int addedFieldsCount = 0;
        for (String fieldName : templateToFields.get(templateName).getFieldLookup().keySet()) {
            if (isFieldAllowedInSection(templateName, fieldName)) {
                final JPanel field = new JPanel(new GridLayout(1, 1));
                field.setBackground(UIHelper.BG_COLOR);

                final JCheckBox checkBox = new JCheckBox(fieldName, false);
                UIHelper.renderComponent(checkBox, UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR, false);

                checkBox.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        JCheckBox source = (JCheckBox) actionEvent.getSource();

                        if (source.isSelected()) {
                            selectedFieldComponents.add(source);
                        } else {
                            if (selectedFieldComponents.contains(source)) {
                                selectedFieldComponents.remove(source);
                            }
                        }
                    }
                });
                if (!okToAddField(fieldName)) checkBox.setEnabled(false);
                field.add(checkBox);

                fields.add(field);

                addedFieldsCount++;
            }
        }

        if (addedFieldsCount == 0) {
            fields.add(UIHelper.createLabel("No available predefined fields for the selected section.", UIHelper.VER_10_BOLD, UIHelper.LIGHT_GREY_COLOR));
        }

        JPanel fieldContainer = new JPanel(new BorderLayout());
        fieldContainer.setBackground(UIHelper.BG_COLOR);
        fieldContainer.add(fields, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(fieldContainer, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        IAppWidgetFactory.makeIAppScrollPane(scrollPane);
        panel.setPreferredSize(new Dimension(300, 400));
        scrollPane.setBorder(new EmptyBorder(30, 0, 0, 0));

        panel.add(scrollPane, BorderLayout.CENTER);

        // only add a button if we have fields to add to it.
        if (addedFieldsCount > 0) {
            final JPanel buttonContainer = new JPanel(new BorderLayout());
            buttonContainer.setOpaque(false);

            final JLabel fieldAddStatus = UIHelper.createLabel("", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR);
            buttonContainer.add(fieldAddStatus, BorderLayout.WEST);

            FlatButton button = new FlatButton(ButtonType.GREEN, "Add Field(s)");
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent actionEvent) {

                    boolean errors = false;
                    int error_count = 0;

                    if (selectedFieldComponents.size() > 0) {

                        for (JCheckBox fieldToAdd : selectedFieldComponents) {
                            if (fieldToAdd.isEnabled() && okToAddField(fieldToAdd.getText())) {
                                addFieldsToDisplay(templateToFields.get(templateName).getFieldByName(fieldToAdd.getText()));
                                fieldToAdd.setSelected(false);
                                fieldToAdd.setEnabled(false);

                            } else {
                                errors = true;
                                error_count++;
                            }
                        }

                        selectedFieldComponents.clear();

                        fieldAddStatus.setText(errors ? String.format("%d fields already exist...", error_count) : "Field added successfully!");
                        fieldAddStatus.setForeground(errors ? UIHelper.RED_COLOR : UIHelper.GREY_COLOR);
                    } else {
                        fieldAddStatus.setText("No fields have been selected!");
                    }

                    timer = new Timer(3000, new ActionListener() {
                        public void actionPerformed(ActionEvent actionEvent) {
                            fieldAddStatus.setText("");
                            fieldAddStatus.setForeground(UIHelper.GREY_COLOR);
                            timer.stop();

                        }
                    });
                    timer.start();
                }
            });
            buttonContainer.add(button, BorderLayout.EAST);
            panel.add(buttonContainer, BorderLayout.SOUTH);
        }

        return panel;
    }

    private JPanel getAddCustomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(300, 400));
        panel.setBackground(UIHelper.BG_COLOR);
        panel.add(UIHelper.createLabel("Add Custom Field to Interface"), BorderLayout.NORTH);

        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBorder(new EmptyBorder(30, 0, 0, 0));
        fieldPanel.setBackground(UIHelper.BG_COLOR);

        final JPanel field = new JPanel(new GridLayout(1, 3));
        field.setBackground(UIHelper.BG_COLOR);

        field.add(UIHelper.createLabel("Field name"));

        final RoundedJTextField fieldName = new RoundedJTextField(20);
        field.add(fieldName);

        final JComboBox fieldType = new JComboBox(new String[]{"String", "Ontology term"});
        field.add(fieldType);

        fieldPanel.add(field, BorderLayout.NORTH);

        panel.add(fieldPanel, BorderLayout.CENTER);

        final JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setOpaque(false);

        final JLabel fieldAddStatus = UIHelper.createLabel("", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR);
        buttonContainer.add(fieldAddStatus, BorderLayout.WEST);

        FlatButton button = new FlatButton(ButtonType.GREEN, "Add Field");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!fieldName.getText().isEmpty()) {
                    String fieldNameAsComment = transformFieldNameToComment(fieldName.getText());
                    if (okToAddField(fieldNameAsComment)) {
                        addFieldsToDisplay(new FieldObject(fieldNameAsComment, "",
                                DataTypes.resolveDataType(fieldType.getSelectedItem().toString()), "", false, false, false));
                        fieldAddStatus.setText("Field added successfully!");
                    } else {
                        fieldAddStatus.setForeground(UIHelper.RED_COLOR);
                        fieldAddStatus.setText("Field not added. Already exists!");
                    }
                } else {
                    fieldAddStatus.setForeground(UIHelper.RED_COLOR);
                    fieldAddStatus.setText("Please enter a value!");
                }
                timer = new Timer(3000, new ActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        fieldAddStatus.setText("");
                        fieldAddStatus.setForeground(UIHelper.GREY_COLOR);
                        timer.stop();

                    }
                });
                timer.start();
            }
        });


        buttonContainer.add(button, BorderLayout.EAST);
        panel.add(buttonContainer, BorderLayout.SOUTH);

        return panel;
    }

    private String transformFieldNameToComment(String fieldName) {
        return "Comment [" + fieldName + "]";
    }

    public abstract void addFieldsToDisplay(FieldObject fieldObject);

    public abstract boolean okToAddField(String fieldName);

    public abstract boolean isFieldAllowedInSection(String template, String fieldName);

}
