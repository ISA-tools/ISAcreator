package org.isatools.isacreator.gs.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.log4j.Logger;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.common.button.ButtonType;
import org.isatools.isacreator.common.button.FlatButton;
import org.isatools.isacreator.gs.GSDataManager;
import org.isatools.isacreator.gs.GSIdentityManager;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.gui.menu.ImportFilesListCellRenderer;
import org.isatools.isacreator.gui.menu.ImportFilesMenu;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.utils.GeneralUtils;
import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 10/10/2012
 * Time: 15:13
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSImportFilesMenu extends ImportFilesMenu {

    private static Logger log = Logger.getLogger(GSImportFilesMenu.class);

    @InjectedResource
    private ImageIcon panelHeader, listImage,
            backButton, backButtonOver, filterLeft, filterRight, gslistImage;

    private JLabel back;
    private Container loadingImagePanel;
    private JButton chooseFromGS;

    GSFileChooser gsFileChooser = null;
    GSDataManager gsDataManager = null;
    private java.util.List<GSFileMetadata> previousGSFiles = null;


    public GSImportFilesMenu(ISAcreatorMenu menu) {
        super(menu);
        setPreferredSize(new Dimension(400, 400));
        GSIdentityManager gsIdentityManager = GSIdentityManager.getInstance();
        gsDataManager = gsIdentityManager.getGsDataManager();


    }

    public void createGUI() {
        Box container = Box.createVerticalBox();
        container.setOpaque(false);

        previousFileList = new ExtendedJList();
        previousFileList.setBorder(null);
        previousFileList.setOpaque(false);

        previousFiles = getPreviousFiles();

        previousFileList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent event) {
                if (event.getClickCount() >= 2) {
                    getSelectedFileAndLoad();
                }
            }
        });

        JPanel listPane = new JPanel(new BorderLayout());
        listPane.setOpaque(false);

        JScrollPane listScroller = new JScrollPane(previousFileList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listScroller.setBorder(new EmptyBorder(1, 1, 1, 1));
        listScroller.setOpaque(false);
        listScroller.setPreferredSize(new Dimension(250, 150));
        listScroller.getViewport().setOpaque(false);

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        listPane.add(listScroller);

        JPanel filterFieldPane = new JPanel();
        filterFieldPane.setLayout(new BoxLayout(filterFieldPane, BoxLayout.LINE_AXIS));
        filterFieldPane.setOpaque(false);

        UIHelper.renderComponent(previousFileList.getFilterField(), UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);
        previousFileList.getFilterField().setOpaque(false);
        previousFileList.getFilterField().setBorder(new EmptyBorder(1, 1, 1, 1));
        filterFieldPane.add(UIHelper.wrapComponentInPanel(new JLabel(getLeftFilterImage())));
        filterFieldPane.add(previousFileList.getFilterField());
        filterFieldPane.add(UIHelper.wrapComponentInPanel(new JLabel(getRightFilterImage())));
        filterFieldPane.add(new ClearFieldUtility(previousFileList.getFilterField()));

        listPane.add(filterFieldPane, BorderLayout.SOUTH);

        listPane.setBorder(new TitledBorder(
                UIHelper.GREEN_ROUNDED_BORDER, getBorderTitle(),
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER,
                UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR));

        setListRenderer();

        //top container contains images for refreshing the list, and the menu title image :o)
        JPanel topContainer = new JPanel(new GridLayout(1, 1));
        topContainer.setOpaque(false);

        JLabel loadISAImage = new JLabel(getPanelHeaderImage(),
                JLabel.RIGHT);
        loadISAImage.setForeground(UIHelper.DARK_GREEN_COLOR);

        topContainer.add(loadISAImage);

        container.add(topContainer);
        container.add(Box.createVerticalStrut(5));
        container.add(listPane);
        container.add(Box.createVerticalStrut(5));

        container.add(createButtonPanel());

        JLabel progress = new JLabel();

        container.add(Box.createVerticalStrut(10));
        container.add(progress);

        if (showProblemArea)
            container.add(createProblemDisplay());

        container.add(createAlternativeExitDisplay());

        add(container, BorderLayout.CENTER);

    }

    @Override
    public void getSelectedFileAndLoad() {

        if (previousFileList.getSelectedIndex() != -1) {
            // select file from list
            for (File candidate : previousFiles) {
                if (candidate.getName().equals(previousFileList.getSelectedValue().toString())) {
                    getSelectedFileAndLoad(candidate,true);
                }
            }

            for (GSFileMetadata candidate: previousGSFiles){
                if (candidate.getUrl().equals(previousFileList.getSelectedValue().toString()))
                    System.out.println("file candidate = "+candidate);

                    loadGenomeSpaceFiles(candidate);
            }

        }
    }

    @Override
    public File[] getPreviousFiles() {
        previousFileList.clearItems();


        File f = new File(ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY);

        if (!f.exists() || !f.isDirectory()) {
            f.mkdir();
        }

        previousFiles = f.listFiles();

        for (File prevSubmission : previousFiles) {
            if (prevSubmission.isDirectory()) {
                previousFileList.addItem(prevSubmission.getName());
            }
        }

        if (previousGSFiles!=null){
            for(GSFileMetadata fileMetadata : previousGSFiles){
                if (fileMetadata.isDirectory()){
                    previousFileList.addItem(fileMetadata.getUrl());
                }
            }
        }

        return previousFiles;
    }

    @Override
    public void setListRenderer() {
        previousFileList.setCellRenderer(new ImportFilesListCellRenderer(listImage, gslistImage));
    }

    @Override
    public String getBorderTitle() {
        return "select ISA-TAB to load";
    }

    @Override
    public ImageIcon getPanelHeaderImage() {
        return panelHeader;
    }

    @Override
    public JPanel createAlternativeExitDisplay() {

        JPanel previousButtonPanel = new JPanel(new GridLayout(1, 1));
        previousButtonPanel.setOpaque(false);

        back = new JLabel(backButton, JLabel.LEFT);
        back.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                if (problemScroll != null)
                    problemScroll.setVisible(false);

                ApplicationManager.getCurrentApplicationInstance().setGlassPanelContents(menu.getMainMenuGUI());
            }

            public void mouseEntered(MouseEvent event) {
                back.setIcon(backButtonOver);
            }

            public void mouseExited(MouseEvent event) {
                back.setIcon(backButton);
            }
        });

        back.setOpaque(false);

        previousButtonPanel.add(back);

        return previousButtonPanel;
    }

    @Override
    public ImageIcon getLeftFilterImage() {
        return filterLeft;
    }

    @Override
    public ImageIcon getRightFilterImage() {
        return filterRight;
    }


    private Container createButtonPanel() {

        //selection panel
        Box selectionPanel = Box.createHorizontalBox();
        selectionPanel.setOpaque(false);

        chooseFromElsewhere = new FlatButton(ButtonType.GREEN, "Open Another...");
        chooseFromElsewhere.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (timeButtonLastClicked != System.currentTimeMillis()) {

                    if (jfc.showOpenDialog(ApplicationManager.getCurrentApplicationInstance()) == JFileChooser.APPROVE_OPTION) {
                        String directory = jfc.getSelectedFile().toString();
                        File dirFile = new File(directory + File.separator);

                        ApplicationManager.setCurrentLocalISATABFolder(dirFile.getAbsolutePath());

                        menu.showProgressPanel(loadISAanimation);

                        loadFile(dirFile.getAbsolutePath());
                    }

                    timeButtonLastClicked = System.currentTimeMillis();
                }
            }
        });

        chooseFromGS = new FlatButton(ButtonType.BLUE, "Load from GenomeSpace");
        chooseFromGS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (timeButtonLastClicked != System.currentTimeMillis()) {

                    gsFileChooser = new GSFileChooser(menu, GSFileChooser.GSFileChooserMode.OPEN);

                    gsFileChooser.addPropertyChangeListener("selectedFileMetadata", new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            loadGenomeSpaceFiles();
                        }
                    });

                    gsFileChooser.showOpenDialog();

                    timeButtonLastClicked = System.currentTimeMillis();
                }
            }
        });

        loadSelected = new FlatButton(ButtonType.GREEN, "Load File");
        loadSelected.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                getSelectedFileAndLoad();
            }
        });

        selectionPanel.add(chooseFromElsewhere);
        selectionPanel.add(chooseFromGS);
        selectionPanel.add(loadSelected);
        selectionPanel.add(Box.createHorizontalGlue());

        return selectionPanel;
    }


    private void loadGenomeSpaceFiles(final GSFileMetadata fileMetadata) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showLoadingImagePane();
            }
        });

        Thread downloadFilesThread = new Thread(new Runnable() {
            public void run() {

                if (fileMetadata == null)
                    return;

                ApplicationManager.setCurrentRemoteISATABFolder(fileMetadata.getPath());

                String localTmpDirectory = GeneralUtils.createTmpDirectory("isatab-");
                System.out.println("Downloading files to local tmp directory " + localTmpDirectory);
                String pattern = "i_.*\\.txt|s_.*\\.txt|a_.*\\.txt";
                gsDataManager.downloadAllFilesFromDirectory(fileMetadata.getPath(), localTmpDirectory, pattern);
                System.out.println("Importing file...");

                ApplicationManager.setCurrentLocalISATABFolder(localTmpDirectory);

                if (previousGSFiles==null)
                    previousGSFiles = new ArrayList<GSFileMetadata>();
                if (!previousGSFiles.contains(fileMetadata))
                    previousGSFiles.add(fileMetadata);

                loadFile(localTmpDirectory);
            }
        });

        downloadFilesThread.start();

    }

    private void loadGenomeSpaceFiles() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                showLoadingImagePane();
            }
        });

        Thread downloadFilesThread = new Thread(new Runnable() {
            public void run() {
                GSFileMetadata fileMetadata = gsFileChooser.getSelectedFileMetadata();
                if (fileMetadata == null)
                    return;

                ApplicationManager.setCurrentRemoteISATABFolder(fileMetadata.getPath());

                String localTmpDirectory = GeneralUtils.createTmpDirectory("isatab-");
                System.out.println("Downloading files to local tmp directory " + localTmpDirectory);
                String pattern = "i_.*\\.txt|s_.*\\.txt|a_.*\\.txt";
                gsDataManager.downloadAllFilesFromDirectory(fileMetadata.getPath(), localTmpDirectory, pattern);
                System.out.println("Importing file...");

                ApplicationManager.setCurrentLocalISATABFolder(localTmpDirectory);

                if (previousGSFiles==null)
                    previousGSFiles = new ArrayList<GSFileMetadata>();
                previousGSFiles.add(fileMetadata);

                loadFile(localTmpDirectory);
            }
        });

        downloadFilesThread.start();

    }
}
