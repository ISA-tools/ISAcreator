package org.isatools.isacreator.visualization.workflowvisualization;

import com.sun.org.apache.bcel.internal.generic.NEW;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.formatmappingutility.ui.GenerateMappingView;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.visualization.TreeView;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 06/03/2012
 *         Time: 18:09
 */
public class WorkflowVisualization extends AnimatableJFrame {

    // will visualize a biological workflow. Tree will be formed using prefuse. Images will come from the
    // glyphs created for the visweek paper.

    // we need the unique branches of processing (so doesn't include names, just processes enacted)

    // grouped by pertubation, e.g. factor groups.

    static {
        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("workflow-package.style").load(
                WorkflowVisualization.class.getResource("/dependency-injections/workflow-package.properties"));
    }

    @InjectedResource
    private Image workflowVisIcon, workflowVisIconInactive;

    @InjectedResource
    private ImageIcon workflow;

    public WorkflowVisualization() {
        ResourceInjector.get("workflow-package.style").inject(this);
    }

    public void createGUI() {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(440, 600));
        setBackground(UIHelper.BG_COLOR);
        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));
        addTitlePane();
        createCentralPanel();
        addSouthPanel();

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addTitlePane() {
        HUDTitleBar titleBar = new HUDTitleBar(workflowVisIcon, workflowVisIconInactive);
        add(titleBar, BorderLayout.NORTH);
        titleBar.installListeners();
    }

    private void createCentralPanel() {
        // should get the graph and render this, for now just display a default image.
        JPanel centralPanel = new JPanel();

        Tree t = null;

        File treeFile = new File("Data/myfile.xml");

        try {
            t = (Tree) new TreeMLReader().readGraph(treeFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TreeView treeView = new WorkflowVisualisationTreeView(t, new Dimension(400, 600), TreeView.NAME_STRING, 2);
        centralPanel.add(treeView);
        add(centralPanel, BorderLayout.CENTER);
    }

    private void addSouthPanel() {
        FooterPanel footerPanel = new FooterPanel(this);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new WorkflowVisualization().createGUI();

    }
}
