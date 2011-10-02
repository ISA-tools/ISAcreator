package org.isatools.isacreator.plugins.registries;

import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by the ISA team
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

    /*
       Returns the column names this editor will be associated with
    */
    public static PluginSpreadsheetWidget findPluginForColumn(String columnName) {

        for (PluginSpreadsheetWidget widget : spreadsheetPlugins) {

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
