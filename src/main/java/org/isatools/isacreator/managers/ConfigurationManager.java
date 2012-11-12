package org.isatools.isacreator.managers;

import org.isatools.isacreator.autofiltercombo.AutoFilterComboCellEditor;
import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.configuration.io.ConfigXMLParser;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.util.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 10/07/2012
 *         Time: 14:07
 */
public class ConfigurationManager {

    private static List<TableReferenceObject> assayDefinitions;
    private static List<MappingObject> mappings;


    /**
     * Loads ISA configurations
     *
     * @param configDirectory directory with configuration files
     */
    public static void loadConfigurations(String configDirectory) {
        if (configDirectory != null) {
            ConfigXMLParser cp = new ConfigXMLParser(configDirectory);
            cp.loadConfiguration();
            mappings = cp.getMappings();
            assayDefinitions = cp.getTables();
        }
    }

    /**
     * @return a list of MappingObjects
     */
    public static List<MappingObject> getMappings() {
        return mappings;
    }

    /**
     * Retrieves the measurement endpoints
     *
     * @return array of strings with measurement endpoints
     */
    public static String[] getMeasurementEndpoints() {
        List<MappingObject> assayToTypeMapping = mappings;
        Set<String> measTypeSet = new HashSet<String>();

        for (MappingObject mo : assayToTypeMapping) {
            if (!mo.getTechnologyType().equals("n/a") &&
                    !mo.getMeasurementEndpointType().equalsIgnoreCase("[sample]") && !mo.getMeasurementEndpointType().equalsIgnoreCase("[investigation]")) {
                measTypeSet.add(mo.getMeasurementEndpointType());
            }
        }

        List<String> tempMeasTypes = new ArrayList<String>();
        tempMeasTypes.addAll(measTypeSet);

        Collections.sort(tempMeasTypes);

        return tempMeasTypes.toArray(new String[tempMeasTypes.size()]);

    }

    /**
     * Retrieves technology types
     *
     * @return array of strings
     */
    public static String[] getTechnologyTypes() {
        List<MappingObject> assayToTypeMapping = mappings;
        Set<String> techTypeSet = new HashSet<String>();

        for (MappingObject mo : assayToTypeMapping) {
            if (!mo.getTechnologyType().equals("n/a") && !mo.getTechnologyType().equals("")) {
                techTypeSet.add(mo.getTechnologyType());
            }
        }

        List<String> tempTechTypes = new ArrayList<String>();
        tempTechTypes.addAll(techTypeSet);

        Collections.sort(tempTechTypes);

        tempTechTypes.add(0, AutoFilterComboCellEditor.BLANK_VALUE);

        return tempTechTypes.toArray(new String[tempTechTypes.size()]);
    }

    /**
     * Retrives the allowed technologies per endpoint
     *
     * @return map of string and string list
     */
    public static Map<String, List<String>> getAllowedTechnologiesPerEndpoint() {
        Map<String, List<String>> measToAllowedTechs = new HashMap<String, List<String>>();

        for (MappingObject mo : mappings) {
            if (!measToAllowedTechs.containsKey(mo.getMeasurementEndpointType())) {
                measToAllowedTechs.put(mo.getMeasurementEndpointType(), new ArrayList<String>());
            }
            measToAllowedTechs.get(mo.getMeasurementEndpointType()).add(mo.getTechnologyType());
        }

        return measToAllowedTechs;
    }

    /**
     * Select the TableReferenceObject which is required for a given measurement endpoint
     * and technology type using the MappingObject.
     *
     * @param measurementEndpoint - e.g. Gene Expression
     * @param techType            e.g. DNA Microarray
     * @return TableReferenceObject if one exists, null otherwise.
     */
    public static TableReferenceObject selectTROForUserSelection(
            String measurementEndpoint, String techType) {

        measurementEndpoint = getTrimmedName(measurementEndpoint);
        techType = getTrimmedName(techType);

        for (MappingObject mo : mappings) {
            if (mo.getMeasurementEndpointType().equalsIgnoreCase(measurementEndpoint) &&
                    mo.getTechnologyType().equalsIgnoreCase(techType)) {
                for (TableReferenceObject tro : assayDefinitions) {
                    if (tro.getTableName().equalsIgnoreCase(mo.getAssayName())) {
                        return tro;
                    }
                }
            }
        }

        return null;
    }

    private static String getTrimmedName(String string) {
        string = string.contains(":") ? string.substring(string.indexOf(":") + 1) : string;
        return string;
    }

    /**
     * Select the TableReferenceObject which is required for a given tableType
     *
     * @param tableType - e.g. study sample or investigation
     * @return TableReferenceObject if one exists, null otherwise.
     */
    public static TableReferenceObject selectTROForUserSelection(
            String tableType) {
        for (MappingObject mo : mappings) {

            if (mo.getTableType().equals(tableType)) {
                for (TableReferenceObject tro : assayDefinitions) {
                    if (tro.getTableName().equalsIgnoreCase(mo.getAssayName())) {
                        return new TableReferenceObject(tro.getTableFields());
                    }
                }
            }
        }

        return null;
    }

    public static MappingObject getMappingObjectForMeasurementAndTechnology(String measurementEndpoint, String techType) {
        measurementEndpoint = getTrimmedName(measurementEndpoint);
        techType = getTrimmedName(techType);
        for (MappingObject mappingObject : mappings) {
            if (mappingObject.getMeasurementEndpointType().equals(measurementEndpoint) && mappingObject.getTechnologyType().equals(techType)) {
                return mappingObject;
            }
        }
        return null;
    }

    /**
     * Sets the mappings object.
     *
     * @param mappings list of MappingObjects
     */
    public static void setMappings(List<MappingObject> mappings) {
        ConfigurationManager.mappings = mappings;
    }

    /**
     * Sets the assayDefinitions object
     *
     * @param assayDefinitions list of TableReferenceObjects
     */
    public static void setAssayDefinitions(List<TableReferenceObject> assayDefinitions) {
        ConfigurationManager.assayDefinitions = assayDefinitions;
    }
}
