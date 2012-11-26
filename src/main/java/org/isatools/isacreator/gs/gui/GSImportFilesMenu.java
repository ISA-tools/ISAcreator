package org.isatools.isacreator.gs.gui;

import com.explodingpixels.macwidgets.IAppWidgetFactory;

import org.apache.log4j.Logger;

import org.genomespace.datamanager.core.GSFileMetadata;

import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.common.ClearFieldUtility;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.menu.*;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.utils.GeneralUtils;

import org.isatools.isacreator.gs.GSDataManager;
import org.isatools.isacreator.gs.GSIdentityManager;

import org.jdesktop.fuse.InjectedResource;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.File;

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
    private ImageIcon panelHeader, listImage, searchButton, searchButtonOver,
            backButton, backButtonOver, loadButton, loadButtonOver, filterLeft, filterRight, searchButtonGS, searchButtonGSOver;

    private JLabel back;
    private Container loadingImagePanel;
    private JLabel chooseFromGS;

    GSFileChooser gsFileChooser = null;
    GSDataManager gsDataManager = null;



    public GSImportFilesMenu(ISAcreatorMenu menu){
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
    public File[] getPreviousFiles() {
        return new File[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void getSelectedFileAndLoad() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setListRenderer() {
        previousFileList.setCellRenderer(new ImportFilesListCellRenderer(listImage));
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
    public ImageIcon getSearchButton() {
        return searchButton;
    }

    @Override
    public ImageIcon getSearchButtonOver() {
        return searchButtonOver;
    }

    public ImageIcon getSearchButtonGS() {
        return searchButtonGS;
    }

    public ImageIcon getSearchButtonGSOver() {
        return searchButtonGSOver;
    }

    @Override
    public ImageIcon getLoadButton() {
        return loadButton;
    }

    @Override
    public ImageIcon getLoadButtonOver() {
        return loadButtonOver;
    }

    @Override
    public ImageIcon getLeftFilterImage() {
        return filterLeft;
    }

    @Override
    public ImageIcon getRightFilterImage() {
        return filterRight;
    }


    private JPanel createButtonPanel() {

        //selection panel
        JPanel selectionPanel = new JPanel(new GridLayout(1, 2));
        selectionPanel.setOpaque(false);

        chooseFromElsewhere = new JLabel(getSearchButton(),
                JLabel.LEFT);
        chooseFromElsewhere.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                // precautionary measure to stop double execution of action...

                if (timeButtonLastClicked != System.currentTimeMillis()) {

                    chooseFromElsewhere.setIcon(getSearchButton());

                    if (jfc.showOpenDialog(menu.getMain()) == JFileChooser.APPROVE_OPTION) {
                        String directory = jfc.getSelectedFile().toString();

                        File dirFile = new File(directory);

                        menu.showProgressPanel(loadISAanimation);
                        /*
                        if (AbstractImportFilesMenu.this instanceof ImportFilesMenu) {
                            menu.showProgressPanel(loadISAanimation);
                        } else {
                            menu.showProgressPanel("attempting to load configuration files in directory " +
                                    dirFile.getName());
                        }
                        */
                        loadFile(directory + File.separator);
                    }

                    timeButtonLastClicked = System.currentTimeMillis();
                }
            }


            public void mouseEntered(MouseEvent event) {
                chooseFromElsewhere.setIcon(getSearchButtonOver());
            }

            public void mouseExited(MouseEvent event) {
                chooseFromElsewhere.setIcon(getSearchButton());
            }
        });


        chooseFromGS = new JLabel(getSearchButtonGS(),
                JLabel.CENTER);
        chooseFromGS.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {

                // precautionary meaaure to stop double execution of action...

                if (timeButtonLastClicked != System.currentTimeMillis()) {

                    chooseFromGS.setIcon(getSearchButtonGS());

                    gsFileChooser = new GSFileChooser(menu, GSFileChooser.GSFileChooserMode.OPEN);

                    gsFileChooser.addPropertyChangeListener("selectedFileMetadata",  new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            System.out.println("PropertyChangeEvent "+event);

                            GSFileMetadata fileMetadata = gsFileChooser.getSelectedFileMetadata();
                            if (fileMetadata == null)
                                return;
                            System.out.println("fileMetadata===>"+fileMetadata);

                            //menu.showProgressPanel(loadISAanimation);

                            String localTmpDirectory = GeneralUtils.createISATmpDirectory();
                            System.out.println("Downloading files to local tmp directory "+localTmpDirectory);
                            String pattern = "i_.*\\.txt|s_.*\\.txt|a_.*\\.txt";
                            gsDataManager.downloadAllFilesFromDirectory(fileMetadata.getPath(),localTmpDirectory, pattern);
                            System.out.println("Importing file...");

                            ApplicationManager.setCurrentISATABFolder(localTmpDirectory);

                            loadFile(localTmpDirectory);


                        }
                    });

                    gsFileChooser.showOpenDialog();

                    timeButtonLastClicked = System.currentTimeMillis();
                }
            }


            public void mouseEntered(MouseEvent event) {
                chooseFromGS.setIcon(getSearchButtonGSOver());
            }

            public void mouseExited(MouseEvent event) {
                chooseFromGS.setIcon(getSearchButtonGS());
            }
        });

        loadSelected = new JLabel(getLoadButton(),
                JLabel.CENTER);
        loadSelected.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                loadSelected.setIcon(getLoadButton());
                getSelectedFileAndLoad();
            }

            public void mouseEntered(MouseEvent event) {
                loadSelected.setIcon(getLoadButtonOver());
            }

            public void mouseExited(MouseEvent event) {
                loadSelected.setIcon(getLoadButton());
            }
        });

        selectionPanel.add(chooseFromElsewhere);
        selectionPanel.add(chooseFromGS);

        JPanel loadPanel = new JPanel(new GridLayout(1, 1));
        loadPanel.setOpaque(false);
        loadPanel.add(loadSelected);


        JPanel buttonPanel = new JPanel(new GridLayout(2, 1));
        buttonPanel.setOpaque(false);
        buttonPanel.add(selectionPanel);
        buttonPanel.add(loadPanel);

        return buttonPanel;
    }
}
