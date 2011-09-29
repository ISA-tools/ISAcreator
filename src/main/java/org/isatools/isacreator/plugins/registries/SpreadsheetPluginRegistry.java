package org.isatools.isacreator.plugins.registries;

import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/09/2011
 *         Time: 14:29
 */
public class SpreadsheetPluginRegistry {

    private static List<PluginSpreadsheetWidget> spreadsheetPlugins = new ArrayList<PluginSpreadsheetWidget>();

    public static void registerPlugin(PluginSpreadsheetWidget plugin) {
        System.out.println("Registering plugin");
        spreadsheetPlugins.add(plugin);
    }

    public static void deregisterPlugin(PluginSpreadsheetWidget plugin) {
        System.out.println("Deregistering plugin");
        if (spreadsheetPlugins.contains(plugin)) {
            spreadsheetPlugins.remove(plugin);
        }
    }

    public static PluginSpreadsheetWidget findPluginForColumn(String columnName) {
        System.out.println("Trying to find alternative widget for " + columnName);
        for (PluginSpreadsheetWidget widget : spreadsheetPlugins) {
            System.out.println("Looking at " + widget.getClass().toString());

            System.out.println("Target columns");

            for (String column : widget.targetColumns()) {
                System.out.println(column);
            }

            if (widget.targetColumns().contains(columnName)) {
                return widget;
            }
        }

        return null;
    }

}
