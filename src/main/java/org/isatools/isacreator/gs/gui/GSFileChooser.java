package org.isatools.isacreator.gs.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.log4j.Logger;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gs.GSDataManager;
import org.isatools.isacreator.gs.GSIdentityManager;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.ImportFilesMenu;
import org.isatools.isacreator.managers.ApplicationManager;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

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
    public enum GSFileChooserMode {
        OPEN, SAVE
    }

    @InjectedResource
    private ImageIcon listImage, closeButton, closeButtonOver, selectDir, selectDirOver,
            saveSubmission, saveSubmissionOver, newFolderButton, newFolderButtonOver;

    @InjectedResource
    private Image loadHeader, saveAsHeader;

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


    public GSFileChooser(ISAcreatorMenu me, GSFileChooserMode mo) {
        menu = me;
        mode = mo;
        ResourceInjector.get("gui-package.style").inject(this);
        GSIdentityManager gsIdentityManager = GSIdentityManager.getInstance();
        gsDataManager = gsIdentityManager.getGsDataManager();
    }

    public int showOpenDialog() {
        JFrame frame = createDialog();
        frame.pack();
        frame.setLocationRelativeTo(ApplicationManager.getCurrentApplicationInstance());
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        return NOT_SELECTED;
    }

    public JFrame createDialog() {
        JFrame containerFrame = new JFrame();
        containerFrame.setUndecorated(true);
        containerFrame.setBackground(UIHelper.BG_COLOR);
        instantiatePanel(containerFrame);
        return containerFrame;
    }

    public void instantiatePanel(final JFrame fileSelectionFrame) {

        log.info("Instantiating panel... mode=" + mode);

        status = new JLabel();

        Image headerIcon = mode == GSFileChooserMode.OPEN ? loadHeader : saveAsHeader;

        HUDTitleBar hud = new HUDTitleBar(headerIcon, headerIcon);
        fileSelectionFrame.add(hud, BorderLayout.NORTH);
        hud.installListeners();

        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel fileNamePanel = new JPanel(new GridLayout(1, 2));
        fileNamePanel.setBackground(UIHelper.VERY_LIGHT_GREY_COLOR);
        final JTextField fileNameTxt = new JTextField("Please enter a directory name...");
        fileNameTxt.setBorder(new LineBorder(UIHelper.LIGHT_GREY_COLOR, 1));
        if (mode == GSFileChooserMode.SAVE) {

            fileNamePanel.setOpaque(false);

            JLabel fileNameLabel = new JLabel("directory name");
            UIHelper.renderComponent(fileNameLabel, UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, false);

            fileNameTxt.setBackground(UIHelper.BG_COLOR);
            UIHelper.renderComponent(fileNameTxt, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            fileNamePanel.add(fileNameLabel);
            fileNamePanel.add(fileNameTxt);

            centerPanel.add(fileNamePanel, BorderLayout.NORTH);
        }


        JComponent treePanel = getTreePanel();
        treePanel.setBorder(new EmptyBorder(10, 2, 7, 2));
        centerPanel.add(treePanel, BorderLayout.CENTER);

        fileSelectionFrame.add(centerPanel, BorderLayout.CENTER);


        // setup center panel with buttons
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel cancelLabel = new JLabel(closeButton,
                JLabel.LEFT);
        cancelLabel.setOpaque(false);
        cancelLabel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                fileSelectionFrame.dispose();
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

        if (mode == GSFileChooserMode.SAVE) {

            newFolder.setOpaque(false);
            newFolder.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {
                    if (selectedFileMetadata == null) {
                        status.setText("Please, select the parent directory");
                    } else {
                        String newFolderName = fileNameTxt.getText();

                        if (newFolderName.equals("Please enter a directory name...")) {
                            status.setText("Please, enter a valid directory name");
                            return;
                        } else if (newFolderName.indexOf('/') != -1) {
                            status.setText("Folder names must not contain slashes.  No folder was created.");
                            return;
                        }


                        GSFileMetadata newDirMetadata = gsDataManager.mkDir(newFolderName, selectedFileMetadata);

                        status.setText("Folder " + newFolderName + " created.");

                        final TreePath path = tree.getSelectionPath();
                        List<String> acceptableExtensions = new ArrayList<String>();
                        acceptableExtensions.add("txt");
                        if (tree.isExpanded(path) || currentNode.childrenHaveBeenInitialised()) {
                            final GSFileMetadataTreeNode newDirNode =
                                    new GSFileMetadataTreeNode(newDirMetadata, gsDataManager.getDataManagerClient(),
                                            acceptableExtensions);
                            final DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();

                            final GSFileMetadataTreeNode parent =
                                    path == null ? (GSFileMetadataTreeNode) treeModel.getRoot()
                                            : (GSFileMetadataTreeNode) path.getLastPathComponent();

                            final int insertionPoint = getTreeIndex(parent, treeModel, newDirNode, false);
                            if (insertionPoint == -1) {
                                status.setText("Duplicate folder name");
                                log.error("GenomeSpace error: duplicate folder name");
                                return;
                            }
                            treeModel.insertNodeInto(newDirNode, parent, insertionPoint);

                            // Make sure the user can see the new directory node:
                            tree.scrollPathToVisible(new TreePath(newDirNode.getPath()));
                        }

                        if (tree.isCollapsed(path))
                            tree.expandPath(path);

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

        if (mode == GSFileChooserMode.OPEN) {
            selectDirLabel = new JLabel(selectDir,
                    JLabel.RIGHT);
        } else {
            selectDirLabel = new JLabel(saveSubmission,
                    JLabel.RIGHT);
        }
        selectDirLabel.setOpaque(false);

        selectDirLabel.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                if (selectedFileMetadata != null) {
                    firePropertyChange("selectedFileMetadata", "", selectedFileMetadata);
                    fileSelectionFrame.dispose();
                }
            }

            public void mouseEntered(MouseEvent event) {
                if (mode == GSFileChooserMode.SAVE) {
                    selectDirLabel.setIcon(saveSubmissionOver);
                } else {
                    selectDirLabel.setIcon(selectDirOver);
                }
            }

            public void mouseExited(MouseEvent event) {
                if (mode == GSFileChooserMode.SAVE) {
                    selectDirLabel.setIcon(saveSubmission);
                } else {
                    selectDirLabel.setIcon(selectDir);
                }
            }
        });


        southPanel.add(cancelLabel, BorderLayout.WEST);

        if (mode == GSFileChooserMode.SAVE) {
            southPanel.add(newFolder, BorderLayout.CENTER);
        }

        southPanel.add(selectDirLabel, BorderLayout.EAST);
        southPanel.add(status, BorderLayout.NORTH);

        centerPanel.add(southPanel, BorderLayout.SOUTH);

        FooterPanel footer = new FooterPanel(fileSelectionFrame);
        fileSelectionFrame.add(footer, BorderLayout.SOUTH);

    }

    private JComponent getTreePanel() {
        //set up central panel with files - treePanel
        GSIdentityManager identityManager = GSIdentityManager.getInstance();
        //System.out.println("identityManager.isLoggedIn()="+identityManager.isLoggedIn());
        GSDataManager gsDataManager = identityManager.getGsDataManager();

        tree = new GSTree(gsDataManager.getDataManagerClient(), new ArrayList<String>());
        this.currentNode = (GSFileMetadataTreeNode) tree.getModel().getRoot();
        tree.setEditable(true);
        tree.addTreeSelectionListener(this);

        JScrollPane treeScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScrollPane.setBorder(new EmptyBorder(2, 2, 2, 2));
        treeScrollPane.setPreferredSize(new Dimension(450, 300));

        IAppWidgetFactory.makeIAppScrollPane(treeScrollPane);

        return treeScrollPane;
    }

    public GSFileMetadata getSelectedFileMetadata() {
        return selectedFileMetadata;
    }


    private void settingISAcreatorPane() {
        // capture the current glass pane. This is required when an error occurs on loading and we need to show the error screen etc..
        menu.captureCurrentGlassPaneContents();
        // we hide the glass pane which is currently holding the menu items, loading interface etc.
        menu.hideGlassPane();
        // add the loading image panel to the view. No need to use the glass pane here.
        menu.add(createLoadingImagePanel(), BorderLayout.CENTER);

    }

    private Container createLoadingImagePanel() {
        if (loadingImagePanel == null) {
            loadingImagePanel = UIHelper.wrapComponentInPanel(new JLabel(loadISAanimation));
        }
        return loadingImagePanel;
    }

    //    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

        status.setText("");

        currentNode = (GSFileMetadataTreeNode) tree.getLastSelectedPathComponent();
        if (currentNode == null) {
            selectedFileMetadata = null;
            selectDirLabel.setEnabled(false);
        } else { // currentNode != null
            final boolean isFolder = currentNode.getFileMetadata().isDirectory();
            if (!isFolder) {
                status.setText("Please, select a folder with an ISA-tab dataset");
                selectDirLabel.setEnabled(false);
            } else {
                selectedFileMetadata = currentNode.getFileMetadata();
                selectDirLabel.setEnabled(true);
            }

        }
    }

    private int getTreeIndex(final GSFileMetadataTreeNode parent,
                             final DefaultTreeModel treeModel,
                             final GSFileMetadataTreeNode newDirNode,
                             final boolean allowDups) {
        final String newFolderName = newDirNode.toString();
        int insertIndex = 0;

        final Enumeration iter = parent.children();
        final Set<String> alreadySeen = new HashSet<String>();
        while (iter.hasMoreElements()) {
            final GSFileMetadataTreeNode childNode = (GSFileMetadataTreeNode) iter.nextElement();
            final GSFileMetadata childMetadata = childNode.getFileMetadata();
            if (!childMetadata.isDirectory())
                continue;
            final String folderName = childMetadata.getName();

            if (alreadySeen.contains(folderName))
                continue;
            alreadySeen.add(folderName);

            final int comp = folderName.compareToIgnoreCase(newFolderName);
            if (comp == 0 && !allowDups)
                return -1; // We don't allow duplicate names!
            if (comp < 0)
                ++insertIndex;
        }

        return insertIndex;
    }


}
