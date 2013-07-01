package org.isatools.isacreator.launch;

import org.isatools.isacreator.archiveoutput.ArchiveOutputWindow;
import org.isatools.isacreator.autofilteringlist.FilterableListCellRenderer;
import org.isatools.isacreator.calendar.CalendarGUI;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.factorlevelentry.FactorLevelEntryGUI;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.formelements.assay.AssayInformationPanel;
import org.isatools.isacreator.gui.modeselection.ModeSelector;
import org.isatools.isacreator.ontologiser.ui.OntologyHelpPane;
import org.isatools.isacreator.ontologyselectiontool.OntologySelectionTool;
import org.isatools.isacreator.protocolselector.ProtocolSelectorListCellRenderer;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISATeam.
 * User: agbeltran
 * Date: 22/05/2013
 * Time: 17:52
 *
 * @author <a href="mailto:alejandra.gonzalez.beltran@gmail.com">Alejandra Gonzalez-Beltran</a>
 */
public class ISAcreatorGUIProperties {


    public static void setProperties(){

        UIManager.put("Panel.background", UIHelper.BG_COLOR);
        UIManager.put("ToolTip.foreground", Color.white);
        UIManager.put("ToolTip.background", UIHelper.DARK_GREEN_COLOR);
        UIManager.put("Tree.background", UIHelper.BG_COLOR);
        UIManager.put("Menu.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);
        UIManager.put("MenuItem.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);


        UIManager.put("Container.background", UIHelper.BG_COLOR);
        UIManager.put("PopupMenuUI", "org.isatools.isacreator.common.CustomPopupMenuUI");
        UIManager.put("MenuItemUI", "org.isatools.isacreator.common.CustomMenuItemUI");
        UIManager.put("MenuUI", "org.isatools.isacreator.common.CustomMenuUI");
        UIManager.put("SeparatorUI", "org.isatools.isacreator.common.CustomSeparatorUI");
        UIManager.put("MenuBarUI", "org.isatools.isacreator.common.CustomMenuBarUI");


        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("archiveoutput-package.style").load(
                ArchiveOutputWindow.class.getResource("/dependency-injections/archiveoutput-package.properties"));
        ResourceInjector.get("gui-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/gui-package.properties"));
        ResourceInjector.get("common-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("filechooser-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/filechooser-package.properties"));
        ResourceInjector.get("longtexteditor-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/longtexteditor-package.properties"));
        ResourceInjector.get("mergeutil-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/mergeutil-package.properties"));
        ResourceInjector.get("publicationlocator-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/publicationlocator-package.properties"));
        ResourceInjector.get("wizard-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/wizard-package.properties"));
        ResourceInjector.get("formatmappingutility-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/formatmappingutility-package.properties"));
        ResourceInjector.get("arraydesignbrowser-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/arraydesignbrowser-package.properties"));
        ResourceInjector.get("effects-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/effects-package.properties"));
        ResourceInjector.get("assayselection-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/assayselection-package.properties"));
        ResourceInjector.get("calendar-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/calendar-package.properties"));
        ResourceInjector.get("validateconvert-package.style").load(
                ISAcreator.class.getResource("/dependency-injections/validator-package.properties"));

        ResourceInjector.get("autofilteringlist-package.style").load(
                FilterableListCellRenderer.class.getResource("/dependency-injections/autofilteringlist-package.properties"));

        ResourceInjector.get("calendar-package.style").load(
                CalendarGUI.class.getResource("/dependency-injections/calendar-package.properties"));

        ResourceInjector.get("common-package.style").load(
                ArchiveOutputWindow.class.getResource("/dependency-injections/common-package.properties"));

        ResourceInjector.get("factorlevelentry-package.style").load(
                FactorLevelEntryGUI.class.getResource("/dependency-injections/factorlevelentry-package.properties"));

        ResourceInjector.get("gui-package.style").load(
                AssayInformationPanel.class.getResource("/dependency-injections/gui-package.properties"));
        ResourceInjector.get("gui-package.style").load(
                ModeSelector.class.getResource("/dependency-injections/gui-package.properties"));

        ResourceInjector.get("ontologiser-generator-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/ontologiser-generator-package.properties"));
        ResourceInjector.get("formatmappingutility-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/formatmappingutility-package.properties"));
        ResourceInjector.get("common-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("ontologyselectiontool-package.style").load(
                OntologyHelpPane.class.getResource("/dependency-injections/ontologyselectiontool-package.properties"));

        ResourceInjector.get("ontologyselectiontool-package.style").load(
                OntologySelectionTool.class.getResource("/dependency-injections/ontologyselectiontool-package.properties"));
        ResourceInjector.get("common-package.style").load(
                OntologySelectionTool.class.getResource("/dependency-injections/common-package.properties"));
        ResourceInjector.get("effects-package.style").load(OntologySelectionTool.class.getResource
                ("/dependency-injections/effects-package.properties"));

        ResourceInjector.get("sample-selection-package.style").load(
                ProtocolSelectorListCellRenderer.class.getResource("/dependency-injections/autofilterfield-package.properties"));

    }

}
