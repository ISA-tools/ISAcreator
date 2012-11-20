package org.isatools.isacreator.gs;

import org.isatools.isacreator.gs.gui.GSSaveAsDialog;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.menu.ISAcreatorMenu;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.IncorrectColumnOrderGUI;
import org.isatools.isacreator.spreadsheet.Spreadsheet;
import org.isatools.isacreator.utils.IncorrectColumnPositioning;

import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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
                        Integer mnemonic, ISAcreator f, ISAcreatorMenu m){

        super(text, icon);
        this.type = type;
        putValue(SHORT_DESCRIPTION, desc);
        putValue(MNEMONIC_KEY, mnemonic);

        closeWindowTimer = new Timer(500, new CloseEvent());
        menu = m;
        frame = f;

    }


    public void actionPerformed(ActionEvent actionEvent) {
        /*
        Calendar c = Calendar.getInstance();


        if ((type != SAVE_AS) && curDataEntryEnvironment.getInvestigation().getReference() != null && !curDataEntryEnvironment.getInvestigation().getReference().trim()
                .equals("")) {

            if (curDataEntryEnvironment.getInvestigation().getReference().trim()
                    .equals("")) {
                curDataEntryEnvironment.getInvestigation()
                        .setFileReference(DEFAULT_ISATAB_SAVE_DIRECTORY +
                                File.separator + "SubmissionOn" +
                                c.get(Calendar.DAY_OF_MONTH) + "-" +
                                c.get(Calendar.MONTH) + "-" +
                                c.get(Calendar.HOUR_OF_DAY) + ":" +
                                c.get(Calendar.MINUTE) + File.separator +
                                "Investigation");
                createSubmissionDirectory(DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator +
                        "SubmissionOn" + c.get(Calendar.DAY_OF_MONTH) + "-" +
                        c.get(Calendar.MONTH) + "-" +
                        c.get(Calendar.HOUR_OF_DAY) + ":" +
                        c.get(Calendar.MINUTE));
            }

            saveISATab();

            closeWindowTimer.start();

            if (type != SAVE_ONLY) {
                OntologyManager.clearReferencedOntologySources();
            }
        } else {
        */
            // need to get a new reference from the user!
            GSSaveAsDialog sad = new GSSaveAsDialog(menu);
            sad.addPropertyChangeListener("windowClosed",
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
                            //hideSheet();
                        }
                    });

            sad.addPropertyChangeListener("save",
                    new PropertyChangeListener() {
                        public void propertyChange(PropertyChangeEvent event) {
//                            String baseDirectory = DEFAULT_ISATAB_SAVE_DIRECTORY + File.separator +
//                                    event.getNewValue().toString();
//
//                            String fileName = baseDirectory + File.separator + "Investigation";
//                            createSubmissionDirectory(DEFAULT_ISATAB_SAVE_DIRECTORY +
//                                    File.separator +
//                                    event.getNewValue().toString());
//                            curDataEntryEnvironment.getInvestigation()
//                                    .setFileReference(fileName);
//
//                            ISAcreatorProperties.setProperty(ISAcreatorProperties.CURRENT_ISATAB, baseDirectory);
//
//                            saveISATab();
//                            userProfileIO.saveUserProfiles();
//
//                            hideSheet();
//                            closeWindowTimer.start();
                        }
                    });
            sad.createGUI();

            frame.showJDialogAsSheet(sad);
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
