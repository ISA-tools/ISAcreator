package org.isatools.isacreator.visualization.workflowvisualization;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.visualization.TreeView;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import prefuse.controls.ControlAdapter;
import prefuse.data.Tree;
import prefuse.data.io.TreeMLReader;
import prefuse.visual.VisualItem;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
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


    public WorkflowVisualization() {
        ResourceInjector.get("workflow-package.style").inject(this);
    }

    private NodeDetail nodeDetailView = null;

    public void createGUI() {
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(600, 840));
        setBackground(UIHelper.BG_COLOR);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        TreeView treeView = new WorkflowVisualisationTreeView(t, new Dimension(800, 800), TreeView.NAME_STRING, 2);

        treeView.addControlListener(new ControlAdapter() {
            @Override
            public void itemEntered(VisualItem visualItem, MouseEvent mouseEvent) {
                // we should show a hover display about the item
                if (nodeDetailView != null && nodeDetailView.isShowing()) {
                    setContentForNodeDetail(visualItem);
                }
            }

            @Override
            public void itemClicked(VisualItem visualItem, MouseEvent mouseEvent) {
                // we should set the fixed panel to be visible (if it is not already) and display information
                // about the node.
                if (nodeDetailView == null) {
                    nodeDetailView = new NodeDetail();
                    nodeDetailView.createGUI();
                }

                setContentForNodeDetail(visualItem);
                if (!nodeDetailView.isShowing()) {
                    nodeDetailView.setVisible(true);
                }
            }
        });

        centralPanel.add(treeView);
        add(centralPanel, BorderLayout.CENTER);
    }

    private void setContentForNodeDetail(VisualItem visualItem) {
        try {
            nodeDetailView.setContent(new WorkflowVisualisationNode(visualItem.get("image").toString(),
                    visualItem.get("type").toString(), visualItem.get("name").toString(),
                    visualItem.get("workflow").toString()));
        } catch (Exception e) {
            // ignore the error.
        }
    }

    private void addSouthPanel() {
        FooterPanel footerPanel = new FooterPanel(this);
        add(footerPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new WorkflowVisualization().createGUI();

    }
}
