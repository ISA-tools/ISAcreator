package org.isatools.isacreator.gui.formelements.assay;

/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.assayselection.AssayType;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.configuration.MappingObject;
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
    private ImageIcon deleteIcon, deleteIconOver, viewAssayIcon, viewAssayIconOver, microarray, massNMR, sequencing,
            flowCytometry, gelElectrophoresis, histology, hematology, clinicalChemistry, generic;

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
        closeButton.setToolTipText("<html>Delete this assay</html>");
        closeButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent mouseEvent) {

                closeButton.setIcon(deleteIcon);

                firePropertyChange("removeAssay", null, AssayInformationPanel.this);

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

        final JLabel viewButton = new JLabel(viewAssayIcon);
        viewButton.setToolTipText("<html>View this assay</html>");
        viewButton.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent mouseEvent) {
                viewButton.setIcon(viewAssayIcon);

                firePropertyChange("viewAssay", null, AssayInformationPanel.this);

                setBackground(hoverColor);
            }

            public void mouseEntered(MouseEvent mouseEvent) {
                viewButton.setIcon(viewAssayIconOver);
            }

            public void mouseExited(MouseEvent mouseEvent) {
                viewButton.setIcon(viewAssayIcon);
            }
        });

        topSection.add(Box.createHorizontalStrut(58));
        topSection.add(viewButton);
        topSection.add(closeButton);

        return topSection;
    }

    private Container createAssayInfoSection() {
        Box infoPane = Box.createVerticalBox();
        infoPane.setPreferredSize(new Dimension(140, 60));
        infoPane.add(UIHelper.createLabel(assay.getMeasurementEndpoint(), UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));

        if (assay.getTechnologyType().equals("")) {
            infoPane.add(infoPane.add(Box.createVerticalStrut(15)));
        } else {
            infoPane.add(UIHelper.createLabel(assay.getTechnologyType(), UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT));
        }

        if (assay.getAssayPlatform().equals("")) {
            infoPane.add(Box.createVerticalStrut(15));
        } else {
            infoPane.add(
                    UIHelper.createLabel(assay.getAssayPlatform(), UIHelper.VER_10_PLAIN, UIHelper.LIGHT_GREEN_COLOR, SwingConstants.LEFT));
        }


        JLabel assayReference = UIHelper.createLabel(assay.getAssayReference(), UIHelper.VER_8_BOLD, UIHelper.DARK_GREEN_COLOR, SwingConstants.LEFT);
        assayReference.setVerticalTextPosition(SwingConstants.BOTTOM);

        infoPane.add(assayReference);

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
