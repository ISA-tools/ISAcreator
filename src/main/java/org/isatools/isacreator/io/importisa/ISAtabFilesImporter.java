package org.isatools.isacreator.io.importisa;

import org.apache.commons.collections15.OrderedMap;
import org.apache.log4j.Logger;
import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;
import org.isatools.errorreporter.model.FileType;
import org.isatools.errorreporter.model.ISAFileErrorReport;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.io.importisa.errorhandling.exceptions.MalformedInvestigationException;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;

import org.isatools.isacreator.managers.ConfigurationManager;

import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.settings.ISAcreatorProperties;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;
import uk.ac.ebi.utils.collections.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 09/03/2011
 *         Time: 14:27
 *
 * @author <a href="mailto:eamonnmag@gmail.com">Eamonn Maguire</a>
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAtabFilesImporter extends ISAtabImporter {

    private static final Logger log = Logger.getLogger(ISAtabFilesImporter.class.getName());


    /**
     * ImportISAFiles provides a facility for you to import ISATAB files
     * and convert these files into Java Objects for you to use.
     *
     * This constructor can be used from the API (without accessing GUI elements).
     *
     * @param configDir - the directory containing the configuration files you wish to use.
     */
    public ISAtabFilesImporter(String configDir) {
        super();
        ConfigurationManager.loadConfigurations(configDir);
    }

    /**
     * Import an ISATAB file set!
     *
     * @param parentDir - Directory containing the ISATAB files. Should include a file of type
     * @return boolean if successful or not!
     */
    public boolean importFile(String parentDir){
        return commonImportFile(parentDir);
    }




}
