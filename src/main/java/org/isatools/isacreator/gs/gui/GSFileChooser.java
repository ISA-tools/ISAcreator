package org.isatools.isacreator.gs.gui;

import org.apache.log4j.Logger;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    //the file chooser works in two modes: open and save
    public enum GSFileChooserMode {OPEN, SAVE};

    @InjectedResource
    private ImageIcon loadHeader, saveAsHeader, listImage, closeButton, closeButtonOver, selectDir, selectDirOver,
            saveSubmission, saveSubmissionOver, newFolderButton, newFolderButtonOver;;

    private GSFileChooserMode mode;
    private JLabel status;
    private Container loadingImagePanel;
    private GSFileMetadata selectedFileMetadata;
    private GSFileMetadataTreeNode currentNode;
    private JLabel selectDirLabel = null;
    private GSTree tree = null;
    protected ISAcreatorMenu menu = null;
    private GSDataManager gsDataManager = null;

    public static final int SELECTED = 0;
    public static final int NOT_SELECTED = 1;

    private int retval = NOT_SELECTED;


    public GSFileChooser(ISAcreatorMenu me, GSFileChooserMode mo){
        menu = me;
        mode = mo;
        ResourceInjector.get("gui-package.style").inject(this);
        GSIdentityManager gsIdentityManager = GSIdentityManager.getInstance();
        gsDataManager = gsIdentityManager.getGsDataManager();
    }

    public JDialog createDialog(){
        JDialog dialog = new JDialog();
        dialog.setBackground(UIHelper.BG_COLOR);
        instantiatePanel(dialog);
        return dialog;
    }

    public int showOpenDialog(){
        JDialog dialog = createDialog();

        dialog.pack();
        dialog.setLocationRelativeTo(ApplicationManager.getCurrentApplicationInstance());
        dialog.setVisible(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        return retval;

    }

    public void instantiatePanel(final JDialog dialog){

        System.out.println("Instantiating panel... mode="+mode);

        JPanel topPanel = null;
        status = new JLabel();

        //if (mode == GSFileChooserMode.OPEN) {
            topPanel = new JPanel();
            topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        //}else{
        //    topPanel = new JPanel(new GridLayout(3, 1));
        //}
        topPanel.setBackground(UIHelper.BG_COLOR);


        JLabel chooseFileLabel;
        if (mode == GSFileChooserMode.OPEN){
            chooseFileLabel = new JLabel(loadHeader,
                JLabel.RIGHT);
        }else{
            chooseFileLabel = new JLabel(saveAsHeader,
                    JLabel.RIGHT);
        }
        chooseFileLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        chooseFileLabel.setBackground(UIHelper.BG_COLOR);
        topPanel.add(chooseFileLabel);

        JPanel fileNamePanel = new JPanel(new GridLayout(1, 2));
        final JTextField fileNameTxt = new JTextField(
                "Please enter a directory name...");
        if (mode == GSFileChooserMode.SAVE){

            fileNamePanel.setOpaque(false);

            JLabel fileNameLabel = new JLabel("directory name");
            UIHelper.renderComponent(fileNameLabel, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);


            fileNameTxt.setBackground(UIHelper.BG_COLOR);
            UIHelper.renderComponent(fileNameTxt, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            fileNamePanel.add(fileNameLabel);
            fileNamePanel.add(fileNameTxt);

            topPanel.add(fileNamePanel);
        }


        final JPanel treePanel = getTreePanel();
        topPanel.add(treePanel);
        dialog.add(topPanel, BorderLayout.PAGE_START);


        // setup center panel with buttons
        JPanel centrePanel = new JPanel(new BorderLayout());
        centrePanel.setBackground(UIHelper.BG_COLOR);

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

        final JLabel newFolder = new JLabel(newFolderButton,
                JLabel.CENTER);

        if (mode == GSFileChooserMode.SAVE){

            newFolder.setOpaque(false);
            newFolder.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {
                    if (selectedFileMetadata == null){
                        status.setText("Please, select the parent directory");
                    }else{
                        String newFolderName = fileNameTxt.getText();
                        if (newFolderName.equals("Please enter a directory name...")){
                            status.setText("Please, enter a valid directory name");
                        }
                        gsDataManager.mkDir(newFolderName, selectedFileMetadata);
                    }
                }

                public void mouseEntered(MouseEvent event) {
                    newFolder.setIcon(newFolderButtonOver);
                }

                public void mouseExited(MouseEvent event) {
                    newFolder.setIcon(newFolderButton);
                }
            });
        }

        if (mode == GSFileChooserMode.OPEN){
            selectDirLabel = new JLabel(selectDir,
                JLabel.RIGHT);
        }else{
            selectDirLabel = new JLabel(saveSubmission,
                    JLabel.RIGHT);
        }
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
                if (mode == GSFileChooserMode.SAVE){
                    selectDirLabel.setIcon(saveSubmissionOver);
                }   else {
                    selectDirLabel.setIcon(selectDirOver);
                }
            }

            public void mouseExited(MouseEvent event) {
                if (mode == GSFileChooserMode.SAVE){
                    selectDirLabel.setIcon(saveSubmission);
                }else{
                    selectDirLabel.setIcon(selectDir);
                }
            }
        });


        centrePanel.add(cancelLabel, BorderLayout.WEST);

        if (mode == GSFileChooserMode.SAVE){
            centrePanel.add(newFolder, BorderLayout.CENTER);
        }

        centrePanel.add(selectDirLabel, BorderLayout.EAST);
        dialog.add(centrePanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        status.setOpaque(false);
        southPanel.add(status, BorderLayout.SOUTH);
        dialog.add(southPanel, BorderLayout.SOUTH);

    }

    private JPanel getTreePanel() {
        //set up central panel with files - treePanel
        GSIdentityManager identityManager = GSIdentityManager.getInstance();
        System.out.println("identityManager.isLoggedIn()="+identityManager.isLoggedIn());
        GSDataManager gsDataManager = identityManager.getGsDataManager();


        tree = new GSTree(gsDataManager.getDataManagerClient(),  new ArrayList<String>());
        this.currentNode = (GSFileMetadataTreeNode)tree.getModel().getRoot();
        tree.setEditable(true);
        tree.addTreeSelectionListener(this);
        final JScrollPane treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setPreferredSize(new Dimension(450, 300));
        final JPanel treePanel = new JPanel();
        treePanel.add(treeScrollPane);
        return treePanel;
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
                selectDirLabel.setEnabled(false);
                return;
            }else{
                selectedFileMetadata = currentNode.getFileMetadata();
                selectDirLabel.setEnabled(true);
                return;
            }

        }
    }




}
