/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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

package org.isatools.isacreator.filechooser;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.net.ftp.FTPFile;
import org.isatools.isacreator.common.Globals;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.AnimatableJFrame;
import org.isatools.isacreator.effects.FooterPanel;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.effects.InfiniteProgressPanel;
import org.isatools.isacreator.launch.ISAcreatorGUIProperties;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;


/**
 * Custom FileChooser which provides more functionality to the user for selection of files of similar types,
 * creating multiple selections of files for subsequent entry into a table or list, sorting of files by name,
 * reordering them for entry into the list, and so forth.
 */
public class FileChooserUI extends AnimatableJFrame implements WindowListener {


    public static final int WIDTH = 530;
    public static final int HEIGHT = 300;

    @InjectedResource
    private ImageIcon connectIcon, viewHistoryIcon, previousLocationsHeader, localFileSystemIcon, localFileSystemIconOver,
            remoteFileSystemIcon, remoteFileSystemIconOver, downIcon, downIconOver, upIcon, upIconOver, deleteIcon, deleteIconOver,
            sortAscIcon, sortAscIconOver, sortDescIcon, sortDescIconOver, homeIcon, homeIconOver;

    private DefaultListModel listModel;
    private DefaultTreeModel treeModel;
    private DirectoryFileList selectedFiles;
    private JTree directoryTree;
    private FileBrowser fileBrowser;

    private static FTPManager ftpManager;
    private static InfiniteProgressPanel progressIndicator;

    private JPanel glass;
    private JPanel ftpConnectionContainer;
    private JLabel status;
    private JLabel localFsChoice;

    private JLabel remoteFsChoice;

    static {
        // we only ever want one FTPManager to be in existence, regardless of how many FileChooser instances
        // there are!
        ftpManager = new FTPManager();
        progressIndicator = new InfiniteProgressPanel();
        progressIndicator.setSize(new Dimension(
                WIDTH,
                HEIGHT));
    }

    public FileChooserUI() {
        fileBrowser = new LocalBrowser();
        ResourceInjector.get("filechooser-package.style").inject(this);
        createGUI();
    }


    /**
     * Create the entire GUI
     */
    private void createGUI() {

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BorderLayout());
        setAlwaysOnTop(true);
        setBackground(UIHelper.BG_COLOR);
        addWindowListener(this);
        setUndecorated(true);
        ((JComponent) getContentPane()).setBorder(new EtchedBorder(UIHelper.LIGHT_GREEN_COLOR, UIHelper.LIGHT_GREEN_COLOR));
        add(createTopPanel(), BorderLayout.NORTH);
        JPanel centralContainer = new JPanel(new GridLayout(1, 2));
        centralContainer.setBackground(UIHelper.BG_COLOR);
        centralContainer.add(createNavTree());
        centralContainer.add(createFilesSelectedPanel());
        add(centralContainer, BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        pack();
    }


    private JPanel createTopPanel() {

        final JTextField uri = new JTextField(20);
        final JTextField username = new JTextField(20);
        final JPasswordField password = new JPasswordField(20);

        final JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.PAGE_AXIS));
        topContainer.setBackground(UIHelper.BG_COLOR);

        HUDTitleBar titlePanel = new HUDTitleBar(null, null, true);
        add(titlePanel, BorderLayout.NORTH);
        titlePanel.installListeners();

        topContainer.add(titlePanel);


        JPanel buttonPanel = new JPanel(new GridLayout(1, 1));
        buttonPanel.setOpaque(false);

        JPanel fileSystemPanel = new JPanel();
        fileSystemPanel.setLayout(new BoxLayout(fileSystemPanel, BoxLayout.LINE_AXIS));
        fileSystemPanel.setBackground(UIHelper.BG_COLOR);

        localFsChoice = new JLabel(localFileSystemIcon,
                JLabel.LEFT);
        localFsChoice.setBackground(UIHelper.BG_COLOR);
        localFsChoice.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        localFsChoice.setIcon(localFileSystemIconOver);
                        remoteFsChoice.setIcon(remoteFileSystemIcon);
                        ftpConnectionContainer.setVisible(false);
                        topContainer.revalidate();
                        status.setText("");
                        fileBrowser = new LocalBrowser();
                        try {
                            updateTree(fileBrowser.getHomeDirectory());
                        } catch (IOException e) {
                            FileBrowserTreeNode defaultFTPNode = new FileBrowserTreeNode("problem occurred!", false, FileBrowserTreeNode.DIRECTORY);
                            updateTree(defaultFTPNode);
                        }


                    }
                });

            }
        });

        fileSystemPanel.add(localFsChoice);
        fileSystemPanel.add(Box.createHorizontalStrut(5));

        remoteFsChoice = new JLabel(remoteFileSystemIcon,
                JLabel.LEFT);
        remoteFsChoice.setBackground(UIHelper.BG_COLOR);
        remoteFsChoice.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        localFsChoice.setIcon(localFileSystemIcon);
                        remoteFsChoice.setIcon(remoteFileSystemIconOver);
                        ftpConnectionContainer.setVisible(true);
                        topContainer.revalidate();
                    }
                });
                // immediately try to call FTP manager to get last sessions details
                final FTPAuthentication lastSession;
                if ((lastSession = ftpManager.getLastSession()) != null) {
                    Thread remoteConnector = new Thread(new Runnable() {
                        public void run() {
                            connectToFTP(lastSession.getUri(), lastSession.getUsername(), lastSession.getPassword());
                        }
                    });
                    remoteConnector.start();
                } else {
                    errorAction("no ftp location");
                }

            }
        });


        fileSystemPanel.add(remoteFsChoice);
        fileSystemPanel.add(Box.createHorizontalStrut(5));

        status = UIHelper.createLabel("", UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);
        status.setHorizontalAlignment(JLabel.RIGHT);

        fileSystemPanel.add(status);

        buttonPanel.add(fileSystemPanel);

        topContainer.add(buttonPanel);

        // now create panel to configure the FTP site
        ftpConnectionContainer = new JPanel(new GridLayout(1, 1));
        ftpConnectionContainer.setBackground(UIHelper.BG_COLOR);

        JPanel userAuthFTP = new JPanel();
        userAuthFTP.setLayout(new BoxLayout(userAuthFTP, BoxLayout.LINE_AXIS));
        userAuthFTP.setOpaque(false);

        // add field to add URI
        JPanel uriPanel = new JPanel(new GridLayout(1, 2));
        uriPanel.setBackground(UIHelper.BG_COLOR);

        JLabel uriLab = UIHelper.createLabel("FTP URI: ", UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);
        UIHelper.renderComponent(uri, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        uriPanel.add(uriLab);
        uriPanel.add(uri);

        userAuthFTP.add(uriPanel);

        // add field to add username
        JPanel usernamePanel = new JPanel(new GridLayout(1, 2));
        usernamePanel.setBackground(UIHelper.BG_COLOR);

        JLabel usernameLab = UIHelper.createLabel("Username: ", UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);

        UIHelper.renderComponent(username, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        uriPanel.add(usernameLab);
        uriPanel.add(username);

        userAuthFTP.add(usernamePanel);

        // add field to add password
        JPanel passwordPanel = new JPanel(new GridLayout(1, 2));
        passwordPanel.setBackground(UIHelper.BG_COLOR);

        JLabel passwordLab = UIHelper.createLabel("Password: ", UIHelper.VER_10_BOLD, UIHelper.DARK_GREEN_COLOR);

        UIHelper.renderComponent(password, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

        passwordPanel.add(passwordLab);
        passwordPanel.add(password);

        userAuthFTP.add(passwordPanel);

        JLabel connectLab = new JLabel(connectIcon);
        connectLab.setOpaque(false);
        connectLab.setToolTipText("<html><b>Connect</b><p>Connect to the FTP source defined!</p></html>");

        connectLab.addMouseListener(new MouseAdapter() {


            public void mousePressed(MouseEvent event) {
                if (uri.getText() != null && !uri.getText().trim().equals("")) {
                    String user = (username.getText() != null) ? username.getText() : "";
                    String pass = (password.getPassword() != null) ? new String(password.getPassword()) : "";

                    final FTPAuthentication newFTPLocation = new FTPAuthentication(uri.getText(), user, pass);

                    Thread remoteConnector = new Thread(new Runnable() {
                        public void run() {
                            connectToFTP(newFTPLocation.getUri(), newFTPLocation.getUsername(), newFTPLocation.getPassword());
                        }
                    });
                    remoteConnector.start();
                }
            }

        });

        userAuthFTP.add(connectLab);

        JLabel historyLab = new JLabel(viewHistoryIcon);
        historyLab.setOpaque(false);
        historyLab.setToolTipText("<html><b>Search previously connected to FTP locations</b><p>Connect to a previously defined FTP location</p></html>");
        historyLab.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                SelectFromFTPHistory selectFTP = new SelectFromFTPHistory();
                selectFTP.addPropertyChangeListener("locationSelected", new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent event) {

                        if (event.getNewValue() != null) {
                            final FTPAuthentication ftpRecord = ftpManager.retrieveFTPAuthenticationObject(event.getNewValue().toString());
                            Thread remoteConnector = new Thread(new Runnable() {
                                public void run() {
                                    connectToFTP(ftpRecord.getUri(), ftpRecord.getUsername(), ftpRecord.getPassword());
                                }
                            });
                            remoteConnector.start();
                        }

                    }
                });
                selectFTP.createGUI();
                showJDialogAsSheet(selectFTP);
            }

        });


        userAuthFTP.add(historyLab);

        ftpConnectionContainer.add(userAuthFTP);

        ftpConnectionContainer.setVisible(false);

        topContainer.add(ftpConnectionContainer);

        return topContainer;
    }

    /**
     * Creates the FileSelection panel containing the list of selected files, and the toolbar to modify the list
     *
     * @return JPanel containing a selection pane
     */
    private JPanel createFilesSelectedPanel() {
        JPanel selectionContainer = new JPanel(new BorderLayout());
        selectionContainer.setBackground(UIHelper.BG_COLOR);
        selectionContainer.setBorder(new TitledBorder(
                UIHelper.GREEN_ROUNDED_BORDER,
                "selection(s)", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));


        selectedFiles = new DirectoryFileList();
        listModel = (DefaultListModel) selectedFiles.getModel();

        selectedFiles.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        selectedFiles.setDragEnabled(false);

        JScrollPane selectedFileScroller = new JScrollPane(selectedFiles,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        selectedFileScroller.setPreferredSize(new Dimension(200, 175));
        selectedFileScroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        IAppWidgetFactory.makeIAppScrollPane(selectedFileScroller);

        selectionContainer.add(selectedFileScroller, BorderLayout.CENTER);

        // add list modification toolbar to sort, move, and delete files from the list.
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.LINE_AXIS));
        optionsPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel moveDown = new JLabel(downIcon);
        moveDown.setOpaque(false);
        moveDown.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                moveDown.setIcon(downIcon);

                if (!selectedFiles.isSelectionEmpty()) {
                    int toMoveDown = selectedFiles.getSelectedIndex();

                    if (toMoveDown != (listModel.getSize() - 1)) {
                        swapElements(toMoveDown, toMoveDown + 1);
                    }
                }
            }

            public void mouseEntered(MouseEvent event) {
                moveDown.setIcon(downIconOver);
            }

            public void mouseExited(MouseEvent event) {
                moveDown.setIcon(downIcon);
            }
        });

        optionsPanel.add(moveDown);
        optionsPanel.add(Box.createHorizontalStrut(5));

        final JLabel moveUp = new JLabel(upIcon);
        moveUp.setOpaque(false);
        moveUp.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                moveUp.setIcon(upIcon);

                if (!selectedFiles.isSelectionEmpty()) {
                    int toMoveUp = selectedFiles.getSelectedIndex();

                    if (toMoveUp != 0) {
                        swapElements(toMoveUp, toMoveUp - 1);
                    }
                }
            }

            public void mouseEntered(MouseEvent event) {
                moveUp.setIcon(upIconOver);
            }

            public void mouseExited(MouseEvent event) {
                moveUp.setIcon(upIcon);
            }
        });

        optionsPanel.add(moveUp);
        optionsPanel.add(Box.createHorizontalStrut(5));

        final JLabel deleteItem = new JLabel(deleteIcon);
        deleteItem.setOpaque(false);
        deleteItem.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                deleteItem.setIcon(deleteIcon);

                if (selectedFiles.getSelectedValues() != null) {
                    for (Object o : selectedFiles.getSelectedValues()) {
                        listModel.removeElement(o);
                    }
                }
            }

            public void mouseEntered(MouseEvent event) {
                deleteItem.setIcon(deleteIconOver);
            }

            public void mouseExited(MouseEvent event) {
                deleteItem.setIcon(deleteIcon);
            }
        });

        optionsPanel.add(deleteItem);
        optionsPanel.add(Box.createHorizontalStrut(5));

        final JLabel sortAsc = new JLabel(sortAscIcon);
        sortAsc.setOpaque(false);
        sortAsc.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                sortAsc.setIcon(sortAscIcon);
                if (listModel.getSize() > 0) {
                    sort(true);
                }
            }

            public void mouseEntered(MouseEvent event) {
                sortAsc.setIcon(sortAscIconOver);
            }

            public void mouseExited(MouseEvent event) {
                sortAsc.setIcon(sortAscIcon);
            }
        });

        optionsPanel.add(sortAsc);
        optionsPanel.add(Box.createHorizontalStrut(5));

        final JLabel sortDesc = new JLabel(sortDescIcon);
        sortDesc.setOpaque(false);
        sortDesc.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                sortDesc.setIcon(sortDescIcon);
                if (listModel.getSize() > 0) {
                    sort(false);
                }
            }

            public void mouseEntered(MouseEvent event) {
                sortDesc.setIcon(sortDescIconOver);
            }

            public void mouseExited(MouseEvent event) {
                sortDesc.setIcon(sortDescIcon);
            }
        });

        optionsPanel.add(sortDesc);
        optionsPanel.add(Box.createHorizontalStrut(5));

        selectionContainer.add(optionsPanel, BorderLayout.SOUTH);

        return selectionContainer;
    }

    /**
     * Create south panel to contain buttons for hiding, closing, etc.
     *
     * @return JPanel containing bottom panel elements
     */
    private JPanel createBottomPanel() {
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBackground(UIHelper.BG_COLOR);

        final JLabel ok = new JLabel(Globals.OK_ICON);
        ok.setHorizontalAlignment(JLabel.RIGHT);
        ok.setOpaque(false);
        ok.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("selectedFiles", "", selectedFiles.getSelectedValues());
                setVisible(false);
                listModel.removeAllElements();
            }

            public void mouseEntered(MouseEvent event) {
                ok.setIcon(Globals.OK_OVER_ICON);
            }

            public void mouseExited(MouseEvent event) {
                ok.setIcon(Globals.OK_ICON);
            }
        });

        southPanel.add(ok, BorderLayout.EAST);

        FooterPanel footer = new FooterPanel(this);
        southPanel.add(footer, BorderLayout.SOUTH);

        return southPanel;
    }

    public void setGlassPanelContents(Container panel) {
        if (glass != null) {
            glass.removeAll();
        }

        glass = (JPanel) getGlassPane();
        glass.setLayout(new BorderLayout());
        glass.add(panel);
        glass.setBackground(new Color(255, 255, 255, 10));

        glass.setVisible(true);
        glass.revalidate();
        glass.repaint();
    }


    private void connectToFTP(final String location, final String username, final String password) {
        try {
            // need to update size in case of screen resizing event on the file chooser ui component
            progressIndicator.setSize(this.getSize());
            setGlassPanelContents(progressIndicator);
            validate();
            progressIndicator.start();
            fileBrowser = new FTPBrowser(location, username, password);

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        updateTree(fileBrowser.getHomeDirectory());
                        status.setText("<html>status: <b>connected to " + location + "!</b></html>");
                        ftpManager.addLocation(new FTPAuthentication(location, username, password));
                    } catch (IOException e) {
                        errorAction("problem occurred. check ftp details!");
                    }
                }
            });

        } catch (NoSuchAlgorithmException e) {
            errorAction("problem occurred. invalid ftps algorithm!");
        } catch (IOException e) {
            errorAction("problem occurred. check ftp details!");
        } finally {
            if (progressIndicator.isStarted()) {
                progressIndicator.stop();
            }
            glass.setVisible(false);
        }
    }

    private void errorAction(String error) {
        status.setText("<html>status: not connected!</html>");
        FileBrowserTreeNode defaultFTPNode = new FileBrowserTreeNode(error, false, FileBrowserTreeNode.DIRECTORY);
        updateTree(defaultFTPNode);
    }

    private void updateTree(TreeNode newTree) {
        TreeNode tree;

        tree = newTree;
        if (tree != null) {
            treeModel.setRoot(tree);
        }

    }

    /**
     * Create the Navigation Tree panel
     *
     * @return @see JPanel containing the navigation tree to browse a file system.
     */
    private JPanel createNavTree() {
        JPanel treeContainer = new JPanel(new BorderLayout());
        treeContainer.setBackground(UIHelper.BG_COLOR);
        treeContainer.setBorder(new TitledBorder(
                UIHelper.GREEN_ROUNDED_BORDER,
                "navigation", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        JPanel navigationControls = new JPanel();
        navigationControls.setLayout(new BoxLayout(navigationControls,
                BoxLayout.LINE_AXIS));
        navigationControls.setOpaque(false);

        final JLabel navToParentDir = new JLabel(upIcon);
        navToParentDir.setOpaque(false);
        navToParentDir.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                navToParentDir.setIcon(upIcon);
                try {
                    updateTree(fileBrowser.getParentDirectory());
                } catch (IOException e) {
                    errorAction("problem occurred!");
                }
            }

            public void mouseEntered(MouseEvent event) {
                navToParentDir.setIcon(upIconOver);
            }

            public void mouseExited(MouseEvent event) {
                navToParentDir.setIcon(upIcon);
            }
        });

        navigationControls.add(navToParentDir);
        navigationControls.add(Box.createHorizontalStrut(5));

        final JLabel navToHomeDir = new JLabel(homeIcon);
        navToHomeDir.setOpaque(false);
        navToHomeDir.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                navToHomeDir.setIcon(homeIcon);
                try {
                    updateTree(fileBrowser.getHomeDirectory());

                } catch (IOException e) {
                    if (e instanceof ConnectionException) {
                        status.setText("<html>status: not connected!</html>");
                    }
                    FileBrowserTreeNode defaultFTPNode = new FileBrowserTreeNode("problem occurred!", false, FileBrowserTreeNode.DIRECTORY);
                    updateTree(defaultFTPNode);
                }
            }

            public void mouseEntered(MouseEvent event) {
                navToHomeDir.setIcon(homeIconOver);
            }

            public void mouseExited(MouseEvent event) {
                navToHomeDir.setIcon(homeIcon);
            }
        });

        navigationControls.add(navToHomeDir);
        navigationControls.add(Box.createGlue());

        treeContainer.add(navigationControls, BorderLayout.NORTH);

        try {
            treeModel = new DefaultTreeModel(fileBrowser.getHomeDirectory());
            directoryTree = new JTree(treeModel);
            directoryTree.setFont(UIHelper.VER_11_PLAIN);
            directoryTree.setCellRenderer(new FileSystemTreeCellRenderer());
        } catch (IOException e) {
            FileBrowserTreeNode defaultFTPNode = new FileBrowserTreeNode("problem occurred!", false, FileBrowserTreeNode.DIRECTORY);
            updateTree(defaultFTPNode);
        }


        directoryTree.addMouseListener(new MouseAdapter() {


            public void mousePressed(MouseEvent event) {
                int selRow = directoryTree.getRowForLocation(event.getX(),
                        event.getY());

                TreePath selPath = directoryTree.getPathForLocation(event.getX(),
                        event.getY());

                if (selRow != -1) {
                    final FileBrowserTreeNode node = (FileBrowserTreeNode) selPath.getLastPathComponent();

                    if (SwingUtilities.isLeftMouseButton(event)) {

                        if (event.getClickCount() == 2) {
                            if ((node.getType() == FileBrowserTreeNode.DIRECTORY) &&
                                    (node.getLevel() != 0)) {

                                String newPath;
                                if (fileBrowser instanceof LocalBrowser) {
                                    newPath = ((File) fileBrowser.getDirFiles().get(node.toString())).getPath();
                                } else {
                                    newPath = node.toString();
                                }
                                updateTree(fileBrowser.changeDirectory(newPath));
                            }

                            // else, if a leaf node, then add file to to list
                            if (node.isLeaf() &&
                                    (node.getType() != FileBrowserTreeNode.DIRECTORY)) {
                                String extension = node.toString()
                                        .substring(node.toString()
                                                .lastIndexOf(".") +
                                                1).trim().toUpperCase();

                                FileChooserFile toAdd = null;

                                for (Object o : fileBrowser.getFileMap().get(extension)) {
                                    String fileName;
                                    String filePath;
                                    if (fileBrowser instanceof LocalBrowser) {
                                        File file = (File) o;
                                        fileName = file.getName();
                                        filePath = file.getPath();

                                        if (fileName.equals(node.toString())) {
                                            toAdd = new CustomFile(filePath);
                                            break;
                                        }
                                    } else {
                                        FTPFile ftpFile = (FTPFile) o;
                                        fileName = ftpFile.getName();
                                        filePath = fileBrowser.getAbsoluteWorkingDirectory() + File.separator + ftpFile.getName();

                                        if (fileName.equals(node.toString())) {
                                            toAdd = new CustomFTPFile(ftpFile, filePath);
                                            break;
                                        }
                                    }

                                }

                                if (toAdd != null && !checkIfInList(toAdd)) {
                                    selectedFiles.addFileItem(toAdd);
                                }
                            }
                        }
                    } else {
                        if ((node.getType() == FileBrowserTreeNode.DIRECTORY) &&
                                (node.getLevel() != 0)) {

                            // show popup to add the directory to the selected files
                            JPopupMenu popup = new JPopupMenu();

                            JMenuItem addDirectory = new JMenuItem("add directory");
                            addDirectory.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent ae) {

                                    Object fileToAdd = fileBrowser.getDirFiles().get(node.toString());
                                    FileChooserFile toAdd;

                                    if (fileToAdd instanceof File) {
                                        toAdd = new CustomFile(((File) fileToAdd).getPath());
                                    } else {
                                        FTPFile ftpFile = (FTPFile) fileToAdd;
                                        String filePath = fileBrowser.getAbsoluteWorkingDirectory() + File.separator + ftpFile.getName();


                                        toAdd = new CustomFTPFile(ftpFile, filePath);
                                    }

                                    if (!checkIfInList(toAdd)) {
                                        selectedFiles.addDirectoryItem(toAdd);
                                    }
                                }
                            });

                            popup.add(addDirectory);
                            popup.show(directoryTree, event.getX(), event.getY());
                        }
                    }
                }
            }

        });

        BasicTreeUI ui = new BasicTreeUI() {
            public Icon getCollapsedIcon() {
                return null;
            }

            public Icon getExpandedIcon() {
                return null;
            }
        };

        directoryTree.setUI(ui);
        directoryTree.setFont(UIHelper.VER_12_PLAIN);

        JScrollPane treeScroll = new JScrollPane(directoryTree,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        treeScroll.setPreferredSize(new Dimension(300, 200));
        treeScroll.setBorder(new EmptyBorder(0, 0, 0, 0));
        treeContainer.add(treeScroll, BorderLayout.CENTER);

        IAppWidgetFactory.makeIAppScrollPane(treeScroll);

        return treeContainer;
    }

    public String[] getSelectedFiles() {
        String[] fileList = new String[listModel.getSize()];

        for (int i = 0; i < listModel.getSize(); i++) {
            fileList[i] = ((FileChooserFile) listModel.getElementAt(i)).getFilePath();
        }

        return fileList;
    }

    private boolean checkIfInList(FileChooserFile file) {
        Enumeration<FileChooserFile> fEnum = (Enumeration<FileChooserFile>) listModel.elements();
        while (fEnum.hasMoreElements()) {
            FileChooserFile fcf = fEnum.nextElement();
            if (fcf.getFilePath().equals(file.getFilePath())) {
                return true;
            }
        }
        return false;
    }

    public FTPManager getFtpManager() {
        return ftpManager;
    }

    public void setFtpManager(FTPManager ftpManager) {
        FileChooserUI.ftpManager = ftpManager;
    }


    public static void main(String[] args) {
        ISAcreatorGUIProperties.setProperties();
        FileChooserUI fc = new FileChooserUI();
        fc.setVisible(true);
    }

    /**
     * Sorts the elements in ascending or descending order depending on the argument passed to the ascending parameter.
     *
     * @param ascending - true if ascending, false if descending.
     */
    private void sort(boolean ascending) {
        for (int i = 0; i < listModel.getSize(); i++) {
            for (int j = 0; j < listModel.getSize(); j++) {
                FileChooserFile c1 = (FileChooserFile) listModel.getElementAt(i);
                FileChooserFile c2 = (FileChooserFile) listModel.getElementAt(j);

                if (ascending) {
                    if (c1.toString().toLowerCase().compareTo(c2.toString().toLowerCase()) > 1) {
                        listModel.set(i, c2);
                        listModel.set(j, c1);
                    }
                } else {
                    if (c1.toString().toLowerCase().compareTo(c2.toString().toLowerCase()) < 0) {
                        listModel.set(i, c2);
                        listModel.set(j, c1);
                    }
                }
            }
        }
        selectedFiles.setSelectedIndex(listModel.getSize() - 1);
        selectedFiles.ensureIndexIsVisible(listModel.getSize() - 1);
    }

    private void swapElements(int a, int b) {
        Object o1 = listModel.getElementAt(a);
        Object o2 = listModel.getElementAt(b);
        listModel.set(a, o2);
        listModel.set(b, o1);

        selectedFiles.setSelectedIndex(b);
        selectedFiles.ensureIndexIsVisible(b);
    }

    public void windowOpened(WindowEvent event) {

    }

    public void windowClosing(WindowEvent event) {

    }

    public void windowClosed(WindowEvent event) {

    }

    public void windowIconified(WindowEvent event) {

    }

    public void windowDeiconified(WindowEvent event) {

    }

    public void windowActivated(WindowEvent event) {
        selectedFiles.requestFocusInWindow();
    }

    public void windowDeactivated(WindowEvent event) {
        System.out.println("Window deactivated");
        firePropertyChange("noSelectedFiles", "", selectedFiles);
        listModel.removeAllElements();
        setVisible(false);
    }

    class SelectFromFTPHistory extends JDialog {


        public SelectFromFTPHistory() {
            setLayout(new BorderLayout());
            setBackground(UIHelper.BG_COLOR);
        }

        public void createGUI() {
            JPanel container = new JPanel();
            container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
            container.setBackground(UIHelper.BG_COLOR);


            JPanel headerPanel = new JPanel(new GridLayout(1, 1));
            headerPanel.setBackground(UIHelper.BG_COLOR);

            JLabel selectHistoryHeader = new JLabel(previousLocationsHeader, JLabel.RIGHT);
            selectHistoryHeader.setBackground(UIHelper.BG_COLOR);

            headerPanel.add(selectHistoryHeader);

            container.add(headerPanel);
            container.add(Box.createVerticalStrut(10));

            JPanel infoPanel = new JPanel(new GridLayout(1, 1));
            infoPanel.setBackground(UIHelper.BG_COLOR);

            JLabel info = new JLabel("<html><p>select from a previous <b>ftp location</b> to reconnect to it.</p></html>");
            UIHelper.renderComponent(info, UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR, false);

            infoPanel.add(info);

            container.add(infoPanel);

            // create list
            final DefaultListModel dlm = new DefaultListModel();

            for (String location : ftpManager.getFTPLocations()) {
                dlm.addElement(location);
            }

            final JList locationList = new JList(dlm);
            locationList.setCellRenderer(new FTPLocationListCellRenderer());

            locationList.setBackground(UIHelper.BG_COLOR);
            locationList.setFont(UIHelper.VER_12_PLAIN);

            JScrollPane listScroller = new JScrollPane(locationList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            listScroller.getViewport().setBackground(UIHelper.BG_COLOR);
            listScroller.setPreferredSize(new Dimension(150, 100));
            listScroller.setBorder(null);
            IAppWidgetFactory.makeIAppScrollPane(listScroller);

            container.add(listScroller);

            // create button panel

            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.setBackground(UIHelper.BG_COLOR);

            final JLabel close = new JLabel(Globals.CLOSE_ICON);
            close.setOpaque(false);
            close.setHorizontalAlignment(JLabel.LEFT);

            close.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {
                    closeAndDisposeWindow();
                }

                public void mouseEntered(MouseEvent event) {
                    close.setIcon(Globals.CLOSE_OVER_ICON);
                }

                public void mouseExited(MouseEvent event) {
                    close.setIcon(Globals.CLOSE_ICON);
                }
            });

            buttonPanel.add(close, BorderLayout.WEST);

            final JLabel ok = new JLabel(Globals.OK_ICON);
            ok.setHorizontalAlignment(JLabel.RIGHT);
            ok.setOpaque(false);
            ok.addMouseListener(new MouseAdapter() {

                public void mousePressed(MouseEvent event) {
                    firePropertyChange("locationSelected", "", locationList.getSelectedValue());
                    closeAndDisposeWindow();
                }

                public void mouseEntered(MouseEvent event) {
                    ok.setIcon(Globals.OK_OVER_ICON);
                }

                public void mouseExited(MouseEvent event) {
                    ok.setIcon(Globals.OK_ICON);
                }
            });

            buttonPanel.add(ok, BorderLayout.EAST);

            container.add(buttonPanel);

            add(container);

            pack();
        }

        private void closeAndDisposeWindow() {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    hideSheet();
                    dispose();
                }
            });
        }

    }

    public void makeVisible() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setVisible(true);
                selectedFiles.requestFocusInWindow();
            }
        });
    }

}
