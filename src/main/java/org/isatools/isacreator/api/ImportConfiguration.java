package org.isatools.isacreator.api;

import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.managers.ConfigurationManager;
import org.isatools.isacreator.settings.ISAcreatorProperties;

import java.io.File;

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

    private ConfigXMLParser configParser = null;
    private String configDir = null;

    public ImportConfiguration(String cDir){
        configDir = cDir;
        configParser = new ConfigXMLParser(configDir);
    }

    /***
     *
     * @return true if the load of the configuration was successful and false otherwise
     */
    public boolean loadConfiguration(){
        // provide location to the configuration parser!

        configParser.loadConfiguration();

        if (!configParser.isProblemsEncountered()){

            ConfigurationManager.setAssayDefinitions(configParser.getTables());
            ConfigurationManager.setMappings(configParser.getMappings());
            ConfigurationManager.loadConfigurations(configDir);

            ApplicationManager.setCurrentDataReferenceObject();

            ISAcreatorProperties.setProperty(ISAcreatorProperties.CURRENT_CONFIGURATION, new File(configDir).getAbsolutePath());
        }else{
            System.out.println(configParser.getProblemLog());
        }


        return !configParser.isProblemsEncountered();
    }

    public String getProblemLog(){
        return configParser.getProblemLog();
    }

}
