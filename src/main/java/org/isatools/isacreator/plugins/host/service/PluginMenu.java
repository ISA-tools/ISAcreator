package org.isatools.isacreator.plugins.host.service;

import javax.swing.*;

/**
 * This plugin will allow any implementing classes to be presented as a new menu item in the menu bar shown in the ISAcreator
 * editing environment.
 */
public interface PluginMenu {

    JMenu removeMenu(JMenu menu);

    JMenu addMenu(JMenu menu);

}
