package org.isatools.isacreator.visualization.workflowvisualization;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.visualization.workflowvisualization.taxonomy.TaxonomyLevel;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 17/06/2012
 *         Time: 07:33
 */
public class NodeDetail extends JFrame implements WindowListener {

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("workflow-package.style").load(
                NodeDetail.class.getResource("/dependency-injections/workflow-package.properties"));
    }

    @InjectedResource
    private Image nodeDetailIcon, nodeDetailIconInactive;

    private JLabel glyphImage, typeLabel, valueLabel;
    private FragmentedGlyphRenderer taxonomyRenderer;
    private List<TaxonomyLevel> taxonomyLevels;

    public NodeDetail(List<TaxonomyLevel> taxonomyLevels) {
        this.taxonomyLevels = taxonomyLevels;
        ResourceInjector.get("workflow-package.style").inject(this);
    }

    public void createGUI() {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 350));
        setBackground(UIHelper.BG_COLOR);
        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));
        addTitlePane();
        setAlwaysOnTop(true);
        addCentralPanel();
        addWindowListener(this);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addTitlePane() {
        HUDTitleBar titleBar = new HUDTitleBar(nodeDetailIcon, nodeDetailIconInactive);
        add(titleBar, BorderLayout.NORTH);
        titleBar.installListeners();
    }

    private void addCentralPanel() {
        Box container = Box.createVerticalBox();

        // starting glyph information container
        Box glyphAndInfoContainer = Box.createHorizontalBox();
        glyphAndInfoContainer.setOpaque(false);

        glyphImage = new JLabel();
        glyphAndInfoContainer.add(glyphImage);

        Box informationContainer = Box.createVerticalBox();
        informationContainer.setAlignmentX(0.0f);

        informationContainer.add(UIHelper.createLabel("Type", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR));
        typeLabel = UIHelper.createLabel("", UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR);
        informationContainer.add(typeLabel);

        informationContainer.add(Box.createVerticalStrut(5));

        informationContainer.add(UIHelper.createLabel("Value", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR));
        valueLabel = UIHelper.createLabel("", UIHelper.VER_10_PLAIN, UIHelper.GREY_COLOR);
        informationContainer.add(valueLabel);

        glyphAndInfoContainer.add(informationContainer);
        glyphAndInfoContainer.setBorder(new EmptyBorder(2, 2, 2, 2));

        container.add(glyphAndInfoContainer);

        // ending glyph information container

        taxonomyRenderer = new FragmentedGlyphRenderer(taxonomyLevels);
        container.add(Box.createVerticalStrut(5));
        container.add(UIHelper.wrapComponentInPanel(UIHelper.createLabel("Glyph Composition", UIHelper.VER_10_BOLD, UIHelper.GREY_COLOR)));
        container.add(taxonomyRenderer);

        add(container, BorderLayout.CENTER);
    }

    public void setContent(WorkflowVisualisationNode visualisationNode) {
        setGlyphImage(visualisationNode.getImageFile());
        setTextInformation(visualisationNode.getType(), visualisationNode.getValue());
        setTaxonomyView(visualisationNode.getTaxonomyHierarchy());
        repaint();
    }

    private void setGlyphImage(String fileName) {
        glyphImage.setIcon(new ImageIcon(fileName));
    }

    private void setTextInformation(String type, String value) {
        typeLabel.setText("<html><p>" + type + "</p></html>");
        valueLabel.setText("<html><p>" + value + "</p></html>");
    }

    private void setTaxonomyView(String taxonomyAsString) {
        taxonomyRenderer.setTaxonomyToRender(taxonomyAsString);
        taxonomyRenderer.repaint();
    }

    public void windowOpened(WindowEvent windowEvent) {
    }

    public void windowClosing(WindowEvent windowEvent) {
    }

    public void windowClosed(WindowEvent windowEvent) {
        taxonomyRenderer.closeAll();
    }

    public void windowIconified(WindowEvent windowEvent) {
        taxonomyRenderer.closeAll();
    }

    public void windowDeiconified(WindowEvent windowEvent) {
    }

    public void windowActivated(WindowEvent windowEvent) {
    }

    public void windowDeactivated(WindowEvent windowEvent) {
    }
}
