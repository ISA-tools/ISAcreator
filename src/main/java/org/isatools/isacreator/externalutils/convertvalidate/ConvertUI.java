package org.isatools.isacreator.externalutils.convertvalidate;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.errorreporter.ui.utils.UIHelper;
import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isatab.gui_invokers.AllowedConversions;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by the ISA team
 */
public class ConvertUI extends JPanel {


    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("validator-package.style").load(
                ConvertUI.class.getResource("/dependency-injections/validator-package.properties"));

        ResourceInjector.get("common-package.style").load(
                ConvertUI.class.getResource("/dependency-injections/common-package.properties"));

    }

    private Collection<ConversionTarget> conversionTargets;


    private List<ConversionTargetInformationPanel> conversionTargetInformationPanels;
    private JPanel conversionTargetContainer;
    private FileSelectionPanel fileSelectionPanel;

    @InjectedResource
    private ImageIcon startConversion, startConversionOver;

    public ConvertUI(Collection<ConversionTarget> conversionTargets) {

        ResourceInjector.get("validator-package.style").inject(this);

        this.conversionTargets = conversionTargets;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setOpaque(false);

        createSelectionPane();
    }

    private void createSelectionPane() {
        conversionTargetContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conversionTargetContainer.setOpaque(false);

        conversionTargetInformationPanels = new ArrayList<ConversionTargetInformationPanel>();


        for (ConversionTarget conversionTarget : conversionTargets) {
            ConversionTargetInformationPanel conversionTargetInformationPanel = new ConversionTargetInformationPanel(conversionTarget);

            conversionTargetInformationPanel.addPropertyChangeListener("conversionTargetSelected", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                    // clear selection on all panels...
                    for (ConversionTargetInformationPanel panel : conversionTargetInformationPanels) {
                        panel.clearSelection();
                    }
                }
            });

            conversionTargetInformationPanels.add(conversionTargetInformationPanel);
            conversionTargetContainer.add(conversionTargetInformationPanel);
        }

        JScrollPane scroller = new JScrollPane(conversionTargetContainer,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scroller.getViewport().setOpaque(false);
        scroller.setOpaque(false);
        scroller.setBorder(new EmptyBorder(1, 1, 1, 1));
        scroller.setPreferredSize(new Dimension(400, 80));

        IAppWidgetFactory.makeIAppScrollPane(scroller);

        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        container.add(UIHelper.wrapComponentInPanel(
                UIHelper.createLabel("<html><b>1. Choose your conversion target(s):</b></html>",
                        UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT)));

        container.add(Box.createVerticalStrut(5));

        container.add(UIHelper.wrapComponentInPanel(
                UIHelper.createLabel("<html><i>we’ve filtered out conversion targets not applicable to your data. just click on a box to select the format.</i></html>",
                        UIHelper.VER_9_PLAIN, UIHelper.GREY_COLOR, SwingConstants.LEFT)));

        container.add(Box.createVerticalStrut(15));
        container.add(scroller);
        container.add(Box.createVerticalStrut(15));

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setDialogTitle("Choose conversion output directory");
        fileChooser.setControlButtonsAreShown(true);
        fileChooser.setApproveButtonText("Put it here");

        fileSelectionPanel = new FileSelectionPanel("2. Choose the output directory",
                fileChooser, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR);

        container.add(fileSelectionPanel);

        container.add(Box.createVerticalStrut(10));

        container.add(createButtonPanel());

        add(container, BorderLayout.NORTH);
    }

    private JPanel createButtonPanel() {
        JPanel buttonContainer = new JPanel(new BorderLayout());

        final JLabel convertButton = new JLabel(startConversion);
        convertButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                convertButton.setIcon(startConversionOver);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                convertButton.setIcon(startConversion);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                convertButton.setIcon(startConversion);

                if (fileSelectionPanel.getSelectedFilePath().equals("") &&
                        !fileSelectionPanel.getSelectedFilePath().equals("Please select a directory!") &&
                        !fileSelectionPanel.getSelectedFilePath().equals("Please select a valid location!")) {

                    if (!(new File(fileSelectionPanel.getSelectedFilePath()).exists())) {
                        fileSelectionPanel.setText("Please select a valid location!");
                    } else {
                        fileSelectionPanel.setText("Please select a directory!");
                    }

                    fileSelectionPanel.setWarning(true);
                } else {
                    fileSelectionPanel.setWarning(false);
                    firePropertyChange("startConversion", false, true);
                }
            }
        });

        buttonContainer.add(convertButton, BorderLayout.CENTER);

        return buttonContainer;
    }

    public AllowedConversions getConversionToPerform() {
        for (ConversionTargetInformationPanel panel : conversionTargetInformationPanels) {
            if (panel.getConversionTarget().isSelected()) {
                return panel.getConversionTarget().getTarget();
            }
        }

        return AllowedConversions.ALL;
    }

    public String getOutputLocation() {
        return fileSelectionPanel.getSelectedFilePath();
    }

}
