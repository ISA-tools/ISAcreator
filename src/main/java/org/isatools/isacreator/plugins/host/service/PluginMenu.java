package org.isatools.isacreator.plugins.host.service;

import org.isatools.isacreator.gui.ISAcreator;

import javax.swing.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/09/2011
 *         Time: 16:55
 */
public interface PluginMenu {

    JMenu removeMenu(JMenu menu);

    JMenu addMenu(JMenu menu);

}
