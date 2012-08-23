package org.isatools.isacreator.settings;
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

import org.isatools.isacreator.common.FileSelectionPanel;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.borders.RoundedBorder;
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
public class DataLocationSettings extends SettingsScreen {

    public static final String ISACREATOR_CONFIGURATION_LOCATION = "isacreator.configurationLocation";
    public static final String ISACREATOR_ISATAB_LOCATION = "isacreator.isatabLocation";
    public static final String ISACREATOR_USER_PROFILE_LOCATION = "isacreator.userProfileLocation";
    public static final String ISACREATOR_MAPPING_FILE_LOCATIONS = "isacreator.mappingFileLocations";
    private FileSelectionPanel userProfileLocation;
    private FileSelectionPanel configurationLocation;
    private FileSelectionPanel isatabLocation;
    private FileSelectionPanel mappingFileLocation;

    public DataLocationSettings(Properties settings, Properties propertiesOverride) {
        this.propertiesOverride = propertiesOverride;

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

        mappingFileLocation = new FileSelectionPanel("Mapping File Location", directoryChooser, FileSelectionPanel.OPEN);
        mappingFileLocation.setToolTipText("<html>Specify where <strong>ISAcreator</strong> should look when<br/> looking for files to be mapped.</html>");

        container.add(userProfileLocation);
        container.add(configurationLocation);
        container.add(isatabLocation);
        container.add(mappingFileLocation);

        return container;
    }

    public void updateLocations() {
        configurationLocation.setText(settings.getProperty(ISACREATOR_CONFIGURATION_LOCATION));
        isatabLocation.setText(settings.getProperty(ISACREATOR_ISATAB_LOCATION));
        userProfileLocation.setText(settings.getProperty(ISACREATOR_USER_PROFILE_LOCATION));
        mappingFileLocation.setText(settings.getProperty(ISACREATOR_MAPPING_FILE_LOCATIONS));

        configurationLocation.disableFileSelection(propertiesOverride.containsKey(ISACREATOR_CONFIGURATION_LOCATION));
        isatabLocation.disableFileSelection(propertiesOverride.containsKey(ISACREATOR_ISATAB_LOCATION));
        userProfileLocation.disableFileSelection(propertiesOverride.containsKey(ISACREATOR_USER_PROFILE_LOCATION));
        mappingFileLocation.disableFileSelection(propertiesOverride.containsKey(ISACREATOR_MAPPING_FILE_LOCATIONS));
    }

    @Override
    protected boolean updateSettings() {
        try {
            settings.setProperty(ISACREATOR_ISATAB_LOCATION, isatabLocation.getSelectedFilePath());
            settings.setProperty(ISACREATOR_CONFIGURATION_LOCATION, configurationLocation.getSelectedFilePath());
            settings.setProperty(ISACREATOR_USER_PROFILE_LOCATION, userProfileLocation.getSelectedFilePath());
            settings.setProperty(ISACREATOR_MAPPING_FILE_LOCATIONS, mappingFileLocation.getSelectedFilePath());

            // should replace the static var setting with the properties call instead. It's cleaner.
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

            if (mappingFileLocation.getSelectedFilePath() != null && !mappingFileLocation.getSelectedFilePath().equals("")) {
                ISAcreatorProperties.setProperty(ISACREATOR_MAPPING_FILE_LOCATIONS, mappingFileLocation.getSelectedFilePath());
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
