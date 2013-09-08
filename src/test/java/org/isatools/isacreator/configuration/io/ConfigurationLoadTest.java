package org.isatools.isacreator.configuration.io;

import org.isatools.isatab.configurator.schema.UnitFieldType;
import org.isatools.isatab.configurator.schema.impl.UnitFieldTypeImpl;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class ConfigurationLoadTest {

    @Test
    public void configurationTestLoad() {
        System.out.println("_____TESTING configurationTestLoad()");

        File configurationDirectory = new File("Configurations");
        if(configurationDirectory.exists() && configurationDirectory.isDirectory()) {

            File[] configurationFiles = configurationDirectory.listFiles();

            assert configurationFiles != null;
            for(File file : configurationFiles) {

                if(!file.isHidden() && !file.getName().startsWith(".")) {

                    ConfigXMLParser parser = new ConfigXMLParser(ConfigurationLoadingSource.ISACREATOR, file.getAbsolutePath());

                    System.out.println("___loading configuration " + file.getName().toLowerCase());
                    parser.loadConfiguration();

                    assertTrue("Oh, the configuration size is 0!", parser.getTables().size() > 0);

                    System.out.println("Configuration " + file.getName().toLowerCase() +  " loaded successfully");
                }
            }
        }
    }

}
