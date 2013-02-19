package org.isatools.isacreator.gs.gui;

import org.genomespace.datamanager.core.GSFileMetadata;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import org.apache.log4j.Logger;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.gs.GSDataManager;
import org.isatools.isacreator.gs.GSIdentityManager;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.ImportFilesMenu;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.errorreporter.model.ErrorMessage;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import uk.ac.ebi.utils.collections.Pair;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import java.awt.*;
import java.awt.event.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
            saveSubmission, saveSubmissionOver, newFolderButton, newFolderButtonOver, okButtonIcon, okButtonIconOver,
            cancelButtonIcon, cancelButtonIconOver;

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
    private NewFolderWindow newFolderWindow;

    public static final int SELECTED = 0;
    public static final int NOT_SELECTED = 1;


    public GSFileChooser(ISAcreatorMenu me, GSFileChooserMode mo) {
        menu = me;
        mode = mo;
        ResourceInjector.get("gui-package.style").inject(this);
        GSIdentityManager gsIdentityManager = GSIdentityManager.getInstance();
        gsDataManager = gsIdentityManager.getGsDataManager();
        newFolderWindow = new NewFolderWindow();
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

        containerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent windowEvent) {
                if(newFolderWindow.isShowing()) {
                    newFolderWindow.dispose();
                }
            }
        });
        containerFrame.setAlwaysOnTop(true);
        containerFrame.setUndecorated(true);
        containerFrame.setBackground(UIHelper.BG_COLOR);
        instantiatePanel(containerFrame);
        return containerFrame;
    }

    public void instantiatePanel(final JFrame fileSelectionFrame) {

        log.info("Instantiating panel... mode=" + mode);

        status = new JLabel();
        status.setSize(new Dimension(450,40));
        UIHelper.renderComponent(status,UIHelper.VER_10_PLAIN,UIHelper.RED_COLOR, false);

        Image headerIcon = mode == GSFileChooserMode.OPEN ? loadHeader : saveAsHeader;

        HUDTitleBar hud = new HUDTitleBar(headerIcon, headerIcon);
        fileSelectionFrame.add(hud, BorderLayout.NORTH);
        hud.installListeners();

        JPanel centerPanel = new JPanel(new BorderLayout());

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

                    newFolderWindow = new NewFolderWindow();
                    if (!newFolderWindow.isShowing()) {
                        Point displayLocation = newFolder.getLocationOnScreen();
                        displayLocation.y -= 35;
                        newFolderWindow.createGUI(displayLocation);
                    }

                    newFolderWindow.addPropertyChangeListener("fileNameSelected", new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                            if (selectedFileMetadata == null) {
                                status.setText("Please, select the parent directory");
                            } else {
                                String newFolderName = propertyChangeEvent.getNewValue().toString();

                                if (newFolderName.equals("New folder name")) {
                                    status.setText("Please, enter a valid directory name");
                                    return;
                                } else if (newFolderName.indexOf('/') != -1) {
                                    status.setText("Folder names must not contain slashes.  No folder was created.");
                                    return;
                                }

                                newFolderWindow.setVisible(false);
                                newFolderWindow.dispose();

                                Pair<GSFileMetadata, ErrorMessage> newDirResult = gsDataManager.mkDir(newFolderName, selectedFileMetadata);

                                if (newDirResult.snd == null  && newDirResult.fst!=null){

                                    GSFileMetadata newDirMetadata =  newDirResult.fst;

                                    status.setText("Folder " + newFolderName + " created.");
                                    status.setForeground(UIHelper.GREY_COLOR);

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
                                            status.setForeground(UIHelper.RED_COLOR);
                                            log.error("GenomeSpace error: duplicate folder name");
                                            return;
                                        }
                                        treeModel.insertNodeInto(newDirNode, parent, insertionPoint);

                                        // Make sure the user can see the new directory node:
                                        tree.scrollPathToVisible(new TreePath(newDirNode.getPath()));
                                    }
                                    if (tree.isCollapsed(path)) tree.expandPath(path);
                                }else{
                                    status.setText("<html>Folder " + newFolderName + " not created. "+newDirResult.snd.getMessage()+"</html>");
                                }
                            }
                        }
                    });
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
                    fileSelectionFrame.setVisible(false);
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

    private Container createLoadingImagePanel() {
        if (loadingImagePanel == null) {
            loadingImagePanel = UIHelper.wrapComponentInPanel(new JLabel(loadISAanimation));
        }
        return loadingImagePanel;
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

        status.setText("");

        currentNode = (GSFileMetadataTreeNode) tree.getLastSelectedPathComponent();
        if (currentNode == null) {
            selectedFileMetadata = null;
            selectDirLabel.setEnabled(false);
        } else {
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

    class NewFolderWindow extends JFrame {

        public void createGUI(Point position) {
            setUndecorated(true);
            setAlwaysOnTop(true);
            setPreferredSize(new Dimension(250, 30));
            setLayout(new BorderLayout());
            setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));

            Box container = Box.createHorizontalBox();
            container.setBorder(new EmptyBorder(4, 4, 4, 4));
            setLocation(position);

            final JTextField fileNameTxt = new JTextField("New folder name...", 30);
            UIHelper.renderComponent(fileNameTxt, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR, false);
            fileNameTxt.setBorder(new MatteBorder(0, 0, 2, 0, UIHelper.LIGHT_GREEN_COLOR));

            fileNameTxt.setBackground(UIHelper.BG_COLOR);
            UIHelper.renderComponent(fileNameTxt, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            container.add(fileNameTxt);

            createButtonPanel(container, fileNameTxt);

            add(container, BorderLayout.NORTH);

            pack();
            setVisible(true);
        }

        private void createButtonPanel(Box container, final JTextField fileNameTxt) {
            final JLabel okButton = new JLabel(okButtonIcon);
            okButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    okButton.setIcon(okButtonIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    okButton.setIcon(okButtonIcon);
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    okButton.setIcon(okButtonIcon);
                    firePropertyChange("fileNameSelected", "", fileNameTxt.getText());
                }
            });

            final JLabel cancelButton = new JLabel(cancelButtonIcon);
            cancelButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent mouseEvent) {
                    cancelButton.setIcon(cancelButtonIconOver);
                }

                @Override
                public void mouseExited(MouseEvent mouseEvent) {
                    cancelButton.setIcon(cancelButtonIcon);
                }

                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    cancelButton.setIcon(cancelButtonIcon);
                    setVisible(false);
                    NewFolderWindow.this.dispose();
                }
            });

            container.add(okButton);
            container.add(cancelButton);
        }
    }

}
