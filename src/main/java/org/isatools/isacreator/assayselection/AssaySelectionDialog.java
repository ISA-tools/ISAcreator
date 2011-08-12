package org.isatools.isacreator.assayselection;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.dialog.ConfirmationDialog;
import org.isatools.isacreator.gui.ISAcreator;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.List;

/**
 * Dialog container for the add assay interface.
 * This means it can appear from the top of the screen, sliding in.
 */
public class AssaySelectionDialog extends JDialog {

    // todo add buttons to select the assays or cancel.

    @InjectedResource
    private ImageIcon closeWindowIcon, closeWindowIconOver, doneIcon, doneIconOver, buttonPanelFiller;

    private static AssaySelectionUI assaySelectionUI;
    private Map<String, List<String>> measurementsToTechnologies;
    private ISAcreator isacreatorEnvironment;

    public AssaySelectionDialog(ISAcreator isacreatorEnvironment, Map<String, List<String>> measurementsToTechnologies) {

        ResourceInjector.get("assayselection-package.style").inject(this);

        this.isacreatorEnvironment = isacreatorEnvironment;
        this.measurementsToTechnologies = measurementsToTechnologies;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 510));
    }

    public void createGUI() {

        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));

        assaySelectionUI = new AssaySelectionUI(measurementsToTechnologies);

        assaySelectionUI.createGUI();

        add(Box.createVerticalStrut(10), BorderLayout.NORTH);

        add(assaySelectionUI);

        add(createSouthPanel(), BorderLayout.SOUTH);

        pack();
    }

    private Container createSouthPanel() {
        Box southPanel = Box.createHorizontalBox();

        final JLabel closeButton = new JLabel(closeWindowIcon);
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeButton.setIcon(closeWindowIcon);
                closeWindow();
            }
        });

        final JLabel addAssay = new JLabel(doneIcon);
        addAssay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                addAssay.setIcon(doneIconOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                addAssay.setIcon(doneIcon);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeWindow();
            }
        });

        southPanel.add(closeButton);
        southPanel.add(new JLabel(buttonPanelFiller));
        southPanel.add(addAssay);

        return southPanel;
    }

    private void closeWindow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isacreatorEnvironment.hideSheet();
            }
        });
    }

}
