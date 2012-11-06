package org.isatools.isacreator.gs.gui;

import org.apache.log4j.Logger;
import org.genomespace.client.ui.GSFileBrowserDialog;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gs.GSDataManager;
import org.isatools.isacreator.gs.GSIdentityManager;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.ImportFilesMenu;
import org.isatools.isacreator.managers.ApplicationManager;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 04/11/2012
 * Time: 21:13
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSFileChooser extends JComponent implements TreeSelectionListener {

    protected static ImageIcon loadISAanimation = new ImageIcon(ImportFilesMenu.class.getResource("/images/gui/isa_load.gif"));

    private static Logger log = Logger.getLogger(GSFileChooser.class);

    @InjectedResource
    private ImageIcon dialogHeader, listImage, closeButton, closeButtonOver, selectDir, selectDirOver;

    private JLabel status;
    private Container loadingImagePanel;
    private GSFileMetadata selectedFileMetadata;
    private GSFileMetadataTreeNode currentNode;
    private JLabel selectDirLabel = null;
    private GSTree tree = null;
    private ISAcreatorMenu menu = null;
   // private JDialog dialog = null;

    public static final int SELECTED = 0;
    public static final int NOT_SELECTED = 1;

    private int retval = NOT_SELECTED;


    public GSFileChooser(ISAcreatorMenu m){
        menu = m;
        ResourceInjector.get("gui-package.style").inject(this);
    }

    public JDialog createDialog(){
        JDialog dialog = new JDialog();
        dialog.setBackground(UIHelper.BG_COLOR);
        instantiatePanel(dialog);
        return dialog;
    }

    public void instantiatePanel(final JDialog dialog){

        JPanel topPanel = new JPanel(new GridLayout(1, 1));
        topPanel.setBackground(UIHelper.BG_COLOR);

        JLabel chooseFileLabel = new JLabel(dialogHeader,
                JLabel.RIGHT);
        chooseFileLabel.setBackground(UIHelper.BG_COLOR);
        topPanel.add(chooseFileLabel);

        dialog.add(topPanel, BorderLayout.NORTH);

        //set up central panel with files - treePane

        GSIdentityManager identityManager = GSIdentityManager.getInstance();
        System.out.println("identityManager.isLoggedIn()="+identityManager.isLoggedIn());
        GSDataManager gsDataManager = identityManager.getGsDataManager();


        tree = new GSTree(gsDataManager.getDataManagerClient(),  new ArrayList<String>());
        this.currentNode = (GSFileMetadataTreeNode)tree.getModel().getRoot();
        tree.setEditable(true);
        tree.addTreeSelectionListener(this);
        final JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(450, 300));
        final JPanel treePane = new JPanel();
        treePane.add(treeScrollPane);
        dialog.add(treePane, BorderLayout.NORTH);


        // setup south panel with buttons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel cancelLabel = new JLabel(closeButton,
                JLabel.LEFT);
        cancelLabel.setOpaque(false);
        cancelLabel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                dialog.dispose();
            }

            public void mouseEntered(MouseEvent event) {
                cancelLabel.setIcon(closeButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                cancelLabel.setIcon(closeButton);
            }
        });

        selectDirLabel = new JLabel(selectDir,
                JLabel.RIGHT);
        selectDirLabel.setOpaque(false);

        selectDirLabel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                System.out.println("Mouse pressed in selectDirLabel... ");
                if (selectedFileMetadata!=null){
                    System.out.println("selectedFileMetadata="+selectedFileMetadata+" firing property change...");
                    firePropertyChange("selectedFileMetadata", "", selectedFileMetadata);
                    dialog.dispose();
                } else{
                    System.out.println("selectedFileMetadata="+selectedFileMetadata+" is null...");
                }
            }

            public void mouseEntered(MouseEvent event) {
                selectDirLabel.setIcon(selectDirOver);
            }

            public void mouseExited(MouseEvent event) {
                selectDirLabel.setIcon(selectDir);
            }
        });


        status = new JLabel();
        southPanel.add(cancelLabel, BorderLayout.WEST);
        southPanel.add(status, BorderLayout.CENTER);
        southPanel.add(selectDirLabel, BorderLayout.EAST);
        dialog.add(southPanel, BorderLayout.SOUTH);


    }



    public int showOpenDialog(){
        JDialog dialog = createDialog();

        dialog.pack();
        dialog.setLocationRelativeTo(ApplicationManager.getCurrentApplicationInstance());
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(dialog.DISPOSE_ON_CLOSE);

        return retval;

    }

    public GSFileMetadata getSelectedFileMetadata(){
        return selectedFileMetadata;
    }


    private void settingISAcreatorPane(){
        // capture the current glass pane. This is required when an error occurs on loading and we need to show the error screen etc..
        menu.captureCurrentGlassPaneContents();
        // we hide the glass pane which is currently holding the menu items, loading interface etc.
        menu.hideGlassPane();
        // add the loading image panel to the view. No need to use the glass pane here.
        menu.add(createLoadingImagePanel(), BorderLayout.CENTER);

    }

    private Container createLoadingImagePanel() {
        if(loadingImagePanel == null) {
            loadingImagePanel = UIHelper.wrapComponentInPanel(new JLabel(loadISAanimation));
        }
        return loadingImagePanel;
    }

//    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

        System.out.println("VALUE CHANGED!!! "+treeSelectionEvent);
        status.setText("");

        currentNode = (GSFileMetadataTreeNode)tree.getLastSelectedPathComponent();
        if (currentNode == null) {
            selectedFileMetadata = null;
            selectDirLabel.setEnabled(false);
            return;
        } else { // currentNode != null
            final boolean isFolder = currentNode.getFileMetadata().isDirectory();
            if (!isFolder){
                status.setText("Please, select a folder with an ISA-tab dataset");
                selectDirLabel.setEnabled(true);
                return;
            }else{
                selectedFileMetadata = currentNode.getFileMetadata();
                selectDirLabel.setEnabled(false);
                return;
            // setVisible(false);
            }

        }
    }




}
