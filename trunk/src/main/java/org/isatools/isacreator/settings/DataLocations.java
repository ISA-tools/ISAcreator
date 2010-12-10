package org.isatools.isacreator.settings;

import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.RoundedBorder;
import org.isatools.isacreator.gui.ISAcreator;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.Properties;

/**
 * Class provides the interface for entry of search locations for ISA files, configurations and user profile objects.
 *
 * @author Eamonn Maguire
 *         Date: Nov 18, 2010
 */
public class DataLocations extends SettingsScreen {
    private Properties settings;

    private FileSelectionPanel userProfileLocation;
    private FileSelectionPanel configurationLocation;
    private FileSelectionPanel isatabLocation;


    public DataLocations(Properties settings) {

        this.settings = settings;

        setLayout(new BorderLayout());
        setOpaque(false);
        add(createGUI(), BorderLayout.NORTH);
        setBorder(new TitledBorder(
                new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 9),
                "configure locations", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, UIHelper.VER_12_BOLD,
                UIHelper.GREY_COLOR));

    }

    private Container createGUI() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
        container.setOpaque(false);

        JFileChooser directoryChooser = new JFileChooser();
        directoryChooser.setDialogTitle("Select encompassing directory");
        directoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        directoryChooser.setApproveButtonText("Select directory");

        userProfileLocation = new FileSelectionPanel("User Profile Location", directoryChooser, FileSelectionPanel.OPEN);
        userProfileLocation.setToolTipText("<html>Specify where <strong>ISAcreator</strong> should look when<br/> loading the secure user profile file.</html>");

        configurationLocation = new FileSelectionPanel("Configuration Location", directoryChooser, FileSelectionPanel.OPEN);
        configurationLocation.setToolTipText("<html>Specify where <strong>ISAcreator</strong> should look when<br/> looking for configurations to load.</html>");

        isatabLocation = new FileSelectionPanel("ISAtab Location", directoryChooser, FileSelectionPanel.OPEN);
        isatabLocation.setToolTipText("<html>Specify where <strong>ISAcreator</strong> should look when<br/> looking for ISAtab files to load.</html>");

        container.add(userProfileLocation);
        container.add(configurationLocation);
        container.add(isatabLocation);

        return container;
    }

    public void updateLocations() {
        configurationLocation.setText(settings.getProperty("isacreator.configurationLocation"));
        isatabLocation.setText(settings.getProperty("isacreator.isatabLocation"));
        userProfileLocation.setText(settings.getProperty("isacreator.userProfileLocation"));
    }

    @Override
    protected boolean updateSettings() {
        try {
            settings.setProperty("isacreator.isatabLocation", isatabLocation.getSelectedFilePath());
            settings.setProperty("isacreator.configurationLocation", configurationLocation.getSelectedFilePath());
            settings.setProperty("isacreator.userProfileLocation", userProfileLocation.getSelectedFilePath());

            if (configurationLocation.getSelectedFilePath() != null && !configurationLocation.getSelectedFilePath().equals("")) {
                if (new File(configurationLocation.getSelectedFilePath()).exists()) {
                    ISAcreator.DEFAULT_CONFIGURATIONS_DIRECTORY = configurationLocation.getSelectedFilePath();
                }
            }

            if (isatabLocation.getSelectedFilePath() != null && !isatabLocation.getSelectedFilePath().equals("")) {
                if (new File(isatabLocation.getSelectedFilePath()).exists()) {
                    ISAcreator.DEFAULT_ISATAB_SAVE_DIRECTORY = isatabLocation.getSelectedFilePath();
                }
            }

            if (userProfileLocation.getSelectedFilePath() != null && !userProfileLocation.getSelectedFilePath().equals("")) {

                if (new File(userProfileLocation.getSelectedFilePath()).exists()) {
                    ISAcreator.DEFAULT_USER_PROFILE_DIRECTORY = userProfileLocation.getSelectedFilePath();
                }
            }

            return true;
        } catch (Exception e) {
            log.error("Problem occurred when trying to update Data locations.");
            return false;
        }
    }

    @Override
    protected void performImportLogic() {

    }

    @Override
    protected void performExportLogic() {

    }

    @Override
    protected void performDeletionLogic() {

    }
}
