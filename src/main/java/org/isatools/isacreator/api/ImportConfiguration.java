package org.isatools.isacreator.api;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.io.File;
import java.util.List;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/08/2012
 * Time: 14:27
 *
 * Functionality for importing configuration files
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ImportConfiguration {

    //private static ISAcreator main = ApplicationManager.getCurrentApplicationInstance();
    private ConfigXMLParser configParser = null;

    public ImportConfiguration(){}

    public boolean loadConfiguration(String configDir){
        // provide location to the configuration parser!
        configParser = new ConfigXMLParser(configDir);
        configParser.loadConfiguration();

        if (!configParser.isProblemsEncountered()){

            ConfigurationManager.setAssayDefinitions(configParser.getTables());
            ConfigurationManager.setMappings(configParser.getMappings());

            ApplicationManager.setCurrentDataReferenceObject();

            ISAcreatorProperties.setProperty(ISAcreatorProperties.CURRENT_CONFIGURATION, new File(configDir).getAbsolutePath());
        }

        return configParser.isProblemsEncountered();
    }

    public String getProblemLog(){
        return configParser.getProblemLog();
    }

}
