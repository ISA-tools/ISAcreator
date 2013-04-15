package org.isatools.isacreator.visualization.workflowvisualization.macroinformation;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
import org.isatools.isacreator.visualization.workflowvisualization.WorkflowInformation;
import org.isatools.macros.gui.common.AutoMacronUIHelper;
import org.isatools.macros.gui.macro.MacroDetail;
import org.isatools.macros.gui.macro.renderer.DetailedMacroRenderer;
import org.isatools.macros.gui.macro.selection_util.HighlightBorder;
import org.isatools.macros.motiffinder.Motif;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

public class MacroInformationPanel extends JPanel {

    private String motif;
    private WorkflowInformation workflowInformation;

    public MacroInformationPanel(String motif, WorkflowInformation workflowInformation) {
        this.motif = motif;
        this.workflowInformation = workflowInformation;

        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        setSize(new Dimension(70, 80));

        setBorder(new RoundedBorder(UIHelper.LIGHT_GREY_COLOR, 1));

        // add image to the central panel
        // add control box to the south panel.
        createCentralPanel();
        createControlPanel();
    }

    private void createCentralPanel() {
        JLabel macroImagePanel = new JLabel();

        JPanel centralPanel = new JPanel(new BorderLayout());

        File imageFile = new File(workflowInformation.getMotifToMacros().get(motif).get("small"));
        ImageIcon icon = AutoMacronUIHelper.scaleImageIcon(imageFile.getAbsolutePath(), 80);
        macroImagePanel.setIcon(icon);

        centralPanel.add(macroImagePanel, BorderLayout.CENTER);

        centralPanel.setBorder(new LineBorder(AutoMacronUIHelper.LIGHT_GREY_COLOR));

        add(centralPanel, BorderLayout.CENTER);
    }

    private void createControlPanel() {

        JPanel macroInformationPanel = new JPanel(new BorderLayout());
        macroInformationPanel.setBackground(AutoMacronUIHelper.GREY_COLOR);

        // add motif score

        Motif motifOfInterest = workflowInformation.getMotifs().get(motif);


        JLabel score = UIHelper.createLabel("<html># " + motifOfInterest.getCumulativeUsage() + " times</html>", UIHelper.VER_8_BOLD, UIHelper.BG_COLOR);

        Box scoreContainer = Box.createHorizontalBox();
        scoreContainer.add(score);

        scoreContainer.setBorder(new HighlightBorder(UIHelper.GREY_COLOR, UIHelper.GREY_COLOR));
        macroInformationPanel.add(scoreContainer, BorderLayout.WEST);

        add(macroInformationPanel, BorderLayout.SOUTH);

    }

}
