package org.isatools.isacreator.gui.formelements.assay;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.assayselection.AssayType;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.model.Assay;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class AssayInformationPanel extends JPanel {

    private Color hoverColor = new Color(249, 249, 249);

    @InjectedResource
    private ImageIcon deleteIcon, deleteIconOver, microarray, massNMR, sequencing, flowCytometry,
            gelElectrophoresis, histology, hematology, clinicalChemistry, generic;

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("gui-package.style").load(
                AssayInformationPanel.class.getResource("/dependency-injections/gui-package.properties"));
    }

    private Assay assay;

    public AssayInformationPanel(Assay assay) {

        ResourceInjector.get("gui-package.style").inject(this);

        this.assay = assay;
        setLayout(new BorderLayout());


        setBorder(new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 5));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                setBackground(UIHelper.BG_COLOR);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                setBackground(hoverColor);
            }
        });

        createGUI();
    }

    public Assay getAssay() {
        return assay;
    }

    public void createGUI() {
        add(createTopSection(), BorderLayout.NORTH);

        add(createAssayInfoSection(), BorderLayout.CENTER);
    }

    private Container createTopSection() {
        Box topSection = Box.createHorizontalBox();

        topSection.add(new JLabel(determineIcon()), BorderLayout.WEST);

        final JLabel closeButton = new JLabel(deleteIcon);
        closeButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {
                // todo delete assay... prompt first. Probably fire event back here
                closeButton.setIcon(deleteIcon);
                setBackground(hoverColor);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                closeButton.setIcon(deleteIconOver);
                setBackground(hoverColor);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                closeButton.setIcon(deleteIcon);
                setBackground(hoverColor);
            }
        });

        topSection.add(Box.createHorizontalStrut(87));
        topSection.add(closeButton);

        return topSection;
    }

    private Container createAssayInfoSection() {
        Box infoPane = Box.createVerticalBox();
        infoPane.setPreferredSize(new Dimension(140, 50));
        infoPane.add(UIHelper.createLabel(assay.getMeasurementEndpoint(), UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));

        if (!assay.getTechnologyType().equals("")) {
            infoPane.add(UIHelper.createLabel(assay.getTechnologyType(), UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));
        }

        if (!assay.getAssayPlatform().equals("")) {
            infoPane.add(UIHelper.createLabel(assay.getAssayPlatform(), UIHelper.VER_10_BOLD, UIHelper.LIGHT_GREEN_COLOR, SwingConstants.LEFT));
        }

        infoPane.setBorder(new EmptyBorder(2, 1, 2, 1));

        return infoPane;
    }

    private ImageIcon determineIcon() {
        String measurementAndTechnology = assay.getMeasurementEndpoint() + " " + assay.getTechnologyType();

        if (measurementAndTechnology.contains(AssayType.MICROARRAY.getType())) {
            return microarray;
        } else if (measurementAndTechnology.contains(AssayType.MASS_SPECTROMETRY.getType())) {
            return massNMR;
        } else if (measurementAndTechnology.contains(AssayType.NMR.getType())) {
            return massNMR;
        } else if (measurementAndTechnology.contains(AssayType.FLOW_CYTOMETRY.getType())) {
            return flowCytometry;
        } else if (measurementAndTechnology.contains(AssayType.GEL_ELECTROPHORESIS.getType())) {
            return gelElectrophoresis;
        } else if (measurementAndTechnology.contains(AssayType.SEQUENCING.getType())) {
            return sequencing;
        } else if (measurementAndTechnology.contains(AssayType.HISTOLOGY.getType())) {
            return histology;
        } else if (measurementAndTechnology.contains(AssayType.CLINICAL_CHEMISTRY.getType())) {
            return clinicalChemistry;
        } else if (measurementAndTechnology.contains(AssayType.HEMATOLOGY.getType())) {
            return hematology;
        }

        return generic;
    }


    public static void main(String[] args) {
        JFrame testFrame = new JFrame("Test Assay");
        testFrame.setPreferredSize(new Dimension(400, 150));
        testFrame.setBackground(UIHelper.BG_COLOR);

        testFrame.setLayout(new BorderLayout());

        JPanel assayContainer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assayContainer.setBackground(UIHelper.BG_COLOR);

        assayContainer.add(new AssayInformationPanel(
                new Assay("blah.txt", "transcription profiling", "dna microarray", "affymetrix")));

        assayContainer.add(new AssayInformationPanel(
                new Assay("blah.txt", "protein expression profiling", "mass spectrometry", "")));

        JScrollPane assayScroller = new JScrollPane(assayContainer, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(assayScroller);

        testFrame.add(assayScroller, BorderLayout.NORTH);

        testFrame.pack();
        testFrame.setVisible(true);

    }


}
