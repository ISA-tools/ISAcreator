package org.isatools.isacreator.spreadsheet;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.effects.components.RoundedJTextField;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RenameColumnGUI extends AddColumnGUI {

    private TableColumn column;

    public RenameColumnGUI(Spreadsheet st, int type, TableColumn column) {
        super(st, type);
        this.column = column;
    }

    public void createGUI() {
        createMainPanel();
        createSouthPanel();
    }

    private String getHeaderValue() {
        String currentHeader = column.getHeaderValue().toString();
        currentHeader = currentHeader.substring(currentHeader.indexOf("[") + 1, currentHeader.indexOf("]"));
        return currentHeader;
    }

    private void createMainPanel() {
        JPanel containingPanel = new JPanel();
        containingPanel.setBackground(UIHelper.BG_COLOR);

        JPanel headerCont = new JPanel(new GridLayout(1, 1));
        headerCont.setSize(new Dimension(300, 25));
        headerCont.setOpaque(false);

        Box container = Box.createVerticalBox();
        container.setBackground(UIHelper.BG_COLOR);


        if (type == ADD_CHARACTERISTIC_COLUMN) {
            headerCont.add(UIHelper.createLabel("Rename Characteristic", UIHelper.VER_14_BOLD, UIHelper.DARK_GREEN_COLOR, JLabel.LEFT));
            container.add(headerCont);
            container.add(createStdOntologyField("characteristic"));
            varSelectOntologyField.setText(getHeaderValue());
        }

        if (type == ADD_PARAMETER_COLUMN) {
            headerCont.add(UIHelper.createLabel("Rename Parameter Value", UIHelper.VER_14_BOLD, UIHelper.DARK_GREEN_COLOR, JLabel.LEFT));
            container.add(headerCont);
            container.add(createStdOntologyField("parameter"));
            varSelectOntologyField.setText(getHeaderValue());
        }

        if (type == ADD_COMMENT_COLUMN) {
            headerCont.add(UIHelper.createLabel("Rename Comment", UIHelper.VER_14_BOLD, UIHelper.DARK_GREEN_COLOR, JLabel.LEFT));
            container.add(headerCont);

            JLabel lab = new JLabel("Enter comment qualifier");
            UIHelper.createLabel("Enter comment qualifier", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

            stdTextField = new RoundedJTextField(10);
            stdTextField.setText(getHeaderValue());

            JPanel commentFieldcont = new JPanel(new GridLayout(1, 2));
            commentFieldcont.setOpaque(false);

            commentFieldcont.add(lab);
            commentFieldcont.add(stdTextField);

            container.add(commentFieldcont);
        }

        containingPanel.add(container, BorderLayout.NORTH);
        add(containingPanel, BorderLayout.CENTER);
    }

    private void createSouthPanel() {
        JButton close = new FlatButton(ButtonType.RED, "Cancel");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        st.getParentFrame().hideSheet();
                        ontologySelectionTool = null;
                    }
                });
            }
        });

        JButton renameButton = new FlatButton(ButtonType.GREEN, "Rename");
        renameButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent event) {
                if (type == ADD_COMMENT_COLUMN) {
                    if (!stdTextField.getText().isEmpty()) {
                        setColumnHeaderValue("Comment[" + stdTextField.getText() + "]");
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                UIHelper.renderComponent(stdTextField, UIHelper.VER_12_PLAIN, UIHelper.RED_COLOR, UIHelper.TRANSPARENT_RED_COLOR);
                            }
                        });
                    }
                } else if (!varSelectOntologyField.getText().isEmpty()) {
                    if (type == ADD_CHARACTERISTIC_COLUMN) {
                        setColumnHeaderValue("Characteristics[" + varSelectOntologyField.getText() + "]");
                    }
                    if (type == ADD_PARAMETER_COLUMN) {
                        setColumnHeaderValue("Parameter Value[" + varSelectOntologyField.getText() + "]");
                    }
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            UIHelper.renderComponent(varSelectOntologyField, UIHelper.VER_12_PLAIN, UIHelper.RED_COLOR, UIHelper.TRANSPARENT_RED_COLOR);
                        }
                    });
                }
            }
        });

        JPanel buttonCont = new JPanel(new BorderLayout());
        buttonCont.setBorder(UIHelper.EMPTY_BORDER);
        buttonCont.setBackground(UIHelper.BG_COLOR);
        buttonCont.add(close, BorderLayout.WEST);
        buttonCont.add(renameButton, BorderLayout.EAST);

        add(buttonCont, BorderLayout.SOUTH);
    }

    private void setColumnHeaderValue(final String header) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                column.setHeaderValue(header);
                st.getParentFrame().hideSheet();
                st.getTable().addNotify();
                ontologySelectionTool = null;
            }
        });
    }

}
