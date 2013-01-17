package org.isatools.isacreator.gs;

import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.isacreator.gs.gui.GSFileChooser;
import org.isatools.isacreator.gs.gui.GSSavingWindow;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.io.exportisa.OutputISAFilesFromGUI;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.managers.ApplicationManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 20/11/2012
 * Time: 00:09
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class GSSaveAction extends AbstractAction {

    public static final int SAVE_ONLY = 0;
    public static final int SAVE_AS = 1;

    private int type;
    private Timer closeWindowTimer;

    private ISAcreator frame = null;
    private ISAcreatorMenu menu = null;
    private GSDataManager gsDataManager = null;
    private OutputISAFilesFromGUI isatabExporter = null;

    /**
     * SaveAction constructor.
     *
     * @param type     - type of screen resize to be performed, either full screen or default screen...
     * @param text     - Text to be displayed in component
     * @param icon     - Icon to be displayed in component
     * @param desc     - description of the components purpose
     * @param mnemonic - shortcut key to be used!
     */
    public GSSaveAction(int type, String text, ImageIcon icon, String desc,
                        Integer mnemonic, ISAcreator f, ISAcreatorMenu m) {

        super(text, icon);
        this.type = type;
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);

        closeWindowTimer = new Timer(500, new CloseEvent());
        menu = m;
        frame = f;

        GSIdentityManager gsIdentityManager = GSIdentityManager.getInstance();
        gsDataManager = gsIdentityManager.getGsDataManager();

        isatabExporter = new OutputISAFilesFromGUI(ApplicationManager.getCurrentApplicationInstance());

    }


    public void actionPerformed(ActionEvent actionEvent) {

        final GSSavingWindow savingProgressIndicator = new GSSavingWindow();

        try {
        isatabExporter.saveISAFiles(false, ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation());

        if (type == SAVE_ONLY && ApplicationManager.getCurrentRemoteISAtabFolder() != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    savingProgressIndicator.createGUI();
                }
            });
            Thread saveThread = new Thread(new Runnable() {
                public void run() {
                    //save all the local files into GS
                    String localISATABFolder = ApplicationManager.getCurrentLocalISAtabFolder();

                    File folder = new File(localISATABFolder);
                    File[] files = folder.listFiles();

                    String folderPath = ApplicationManager.getCurrentRemoteISAtabFolder();

                    GSFileMetadata folderMetadata = gsDataManager.getFileMetadata(folderPath);

                    for (File file : files) {
                        updateProgressIndicator(savingProgressIndicator, file);
                        gsDataManager.saveFile(file, folderMetadata);
                    }



                    hideSaveProgressWindow(savingProgressIndicator);
                }
            });
            saveThread.start();

        } else {

            final GSFileChooser gsFileChooser = new GSFileChooser(menu, GSFileChooser.GSFileChooserMode.SAVE);
            gsFileChooser.addPropertyChangeListener("selectedFileMetadata", new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent event) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            savingProgressIndicator.createGUI();
                        }
                    });

                    Thread saveThread = new Thread(new Runnable() {
                        public void run() {

                            GSFileMetadata fileMetadata = gsFileChooser.getSelectedFileMetadata();
                            if (fileMetadata == null)
                                return;
                            System.out.println("fileMetadata===>" + fileMetadata);

                            //save all the local files into GS
                            String localISATABFolder = ApplicationManager.getCurrentLocalISAtabFolder();
                            System.out.println("localISATABFolder=" + localISATABFolder);

                            File folder = new File(localISATABFolder);
                            File[] files = folder.listFiles();
                            for (File file : files) {
                                updateProgressIndicator(savingProgressIndicator, file);
                                gsDataManager.saveFile(file, fileMetadata);
                            }

                            ApplicationManager.setCurrentRemoteISATABFolder(fileMetadata.getPath());

                            hideSaveProgressWindow(savingProgressIndicator);
                        }
                    });
                    saveThread.start();
                }
            });

            gsFileChooser.showOpenDialog();
        }
        } finally {
            hideSaveProgressWindow(savingProgressIndicator);
        }
    }

    private void hideSaveProgressWindow(GSSavingWindow savingProgressIndicator) {
        savingProgressIndicator.setVisible(false);
    }

    private void updateProgressIndicator(final GSSavingWindow savingProgressIndicator, final File file) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                savingProgressIndicator.fireUpdateToStatus("Uploading " + file.getName());
            }
        });
    }


    class CloseEvent implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            doEvent();
            if (closeWindowTimer != null) {
                closeWindowTimer.stop();
            }
        }

        private void doEvent() {
            /*
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {

                    if (outputISATABFromGUI.isShouldShowIncorrectOrderGUI()) {


                        Map<String, List<String>> report = new HashMap<String, List<String>>();
                        // highlight columns in each affected spreadsheet
                        for (Spreadsheet s : outputISATABFromGUI.getErrorSheets()) {
                            // highlight the columns in the spreadsheet
                            report.putAll(s.getTableConsistencyChecker().getErrorReport());
                            Map<Integer, Color> colorcoding = new HashMap<Integer, Color>();
                            for (IncorrectColumnPositioning icor : s.getTableConsistencyChecker().getRecords()) {
                                colorcoding.putAll(icor.getColumnColors());
                            }
                            s.highlightSpecificColumns(colorcoding);
                        }

                        // show IncorrectColumnOrderGUI
                        incorrectGUI = new IncorrectColumnOrderGUI(report);
                        incorrectGUI.setLocation((getX() + getWidth()) / 2 - (incorrectGUI.getWidth() / 2), (getY() + getHeight()) / 2 - (incorrectGUI.getHeight() / 2));
                        incorrectGUI.createGUI();

                    } else {
                        // IF EVERYTHING IS OK, RESET ROW COLORS!
                        for (Spreadsheet s : outputISATABFromGUI.getAllSheets()) {
                            s.setRowsToDefaultColor();
                        }
                        // proceed to subsequent tasks...
                        switch (type) {
                            case SAVE_MAIN:

                                saveProfilesAndGoToMain();
                                break;

                            case SAVE_EXIT:
                                saveProfilesAndExit();
                                break;

                            default:
                                //
                        }
                    }
                }
            });

            */
        } //doEvent
    } //CloseEvent

}
