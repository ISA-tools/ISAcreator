package org.isatools.isacreator.externalutils.convertvalidate;

import org.isatools.errorreporter.ui.borders.RoundedBorder;
import org.isatools.errorreporter.ui.utils.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ConversionTargetInformationPanel extends JPanel {

    private Color hoverColor = new Color(249, 249, 249);

    @InjectedResource
    private ImageIcon unselected, selected;

    private JLabel selectionIndicator;
    private ConversionTarget conversionTarget;


    public ConversionTargetInformationPanel(ConversionTarget conversionTarget) {

        ResourceInjector.get("validator-package.style").inject(this);

        this.conversionTarget = conversionTarget;

        createGUI();
    }

    public ConversionTarget getConversionTarget() {
        return conversionTarget;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 5));

        add(createTopSection(), BorderLayout.NORTH);
        add(createAssayInfoSection(), BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                setBackground(conversionTarget.isSelected() ? hoverColor : UIHelper.BG_COLOR);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                setBackground(hoverColor);
            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

                firePropertyChange("conversionTargetSelected", false, true);
                conversionTarget.setSelected(!conversionTarget.isSelected());
                updateSelection();
            }
        });

    }

    private Container createTopSection() {
        Box topSection = Box.createHorizontalBox();

        selectionIndicator = new JLabel();
        updateSelection();

        topSection.add(selectionIndicator);
        topSection.add(Box.createHorizontalStrut(5));
        topSection.add(UIHelper.createLabel(conversionTarget.getTarget().getType(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));

        return topSection;
    }

    public void clearSelection() {
        conversionTarget.setSelected(false);
        updateSelection();
    }

    private Container createAssayInfoSection() {
        Box infoPane = Box.createVerticalBox();
        infoPane.setPreferredSize(new Dimension(130, 35));
        infoPane.add(Box.createVerticalStrut(5));
        infoPane.add(UIHelper.createLabel("<html>conversion possible on "
                + conversionTarget.getNumValidAssays() + " assay type" + (conversionTarget.getNumValidAssays() > 1 ? "s" : "") + "</html>",
                UIHelper.VER_8_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));

        infoPane.setBorder(new EmptyBorder(2, 1, 2, 1));
        return infoPane;
    }

    public void updateSelection() {
        selectionIndicator.setIcon(conversionTarget.isSelected() ? selected : unselected);
    }
}
