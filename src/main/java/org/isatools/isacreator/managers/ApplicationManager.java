/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */
package org.isatools.isacreator.managers;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.AssaySpreadsheet;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.ISAcreator;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.ISASection;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 15/09/2011
 *         Time: 23:08
 */
public class ApplicationManager {


    private static ISAcreator currentApplicationInstance;
    private static Map<ISASection, DataEntryForm> isaSectionToDataEntryForm = new HashMap<ISASection, DataEntryForm>();
    private static Object screenInView;

    private static DataEntryReferenceObject investigationDataEntryReferenceObject;

    private static String currentlySelectedFieldName;
    private static String currentLocalISAtabFolder;
    private static String currentRemoteISAtabFolder;

    private static boolean isModified = false;

    public static String getCurrentLocalISAtabFolder() {
        return currentLocalISAtabFolder;
    }

    public static void setCurrentLocalISATABFolder(String folder) {
        currentLocalISAtabFolder = folder;
    }

    public static String getCurrentRemoteISAtabFolder() {
        return currentRemoteISAtabFolder;
    }

    public static void setCurrentRemoteISATABFolder(String folder) {
        currentRemoteISAtabFolder = folder;
    }


    public static void setCurrentApplicationInstance(ISAcreator isacreatorEnvironment) {
        ApplicationManager.currentApplicationInstance = isacreatorEnvironment;
    }

    public static ISAcreator getCurrentApplicationInstance() {
        return currentApplicationInstance;
    }

    public static void setCurrentDataReferenceObject() {
        TableReferenceObject tro = ConfigurationManager.selectTROForUserSelection(MappingObject.INVESTIGATION);
        investigationDataEntryReferenceObject = new DataEntryReferenceObject();
        investigationDataEntryReferenceObject.setFieldDefinition(tro.getTableFields().getFields());
    }

    public static DataEntryReferenceObject getInvestigationDataEntryReferenceObject() {
        return investigationDataEntryReferenceObject == null ? new DataEntryReferenceObject() : investigationDataEntryReferenceObject;
    }

    public static void setCurrentlySelectedField(String colName) {
        if (colName.contains("html")) {
            colName = colName.replaceAll("(Assay measuring)|<html>|<p align=\"right\">|<b>|</b>|</p>|</html>", "").trim();
        }
        ApplicationManager.currentlySelectedFieldName = colName;
    }

    public static String getCurrentlySelectedFieldName() {
        return currentlySelectedFieldName;
    }

    public static void assignDataEntryToISASection(ISASection isaSection, DataEntryForm dataEntryForm) {
        if (isaSectionToDataEntryForm == null) {
            isaSectionToDataEntryForm = new HashMap<ISASection, DataEntryForm>();
        }

        isaSectionToDataEntryForm.put(isaSection, dataEntryForm);
    }

    public static void removeISASectionAndDataEntryForm(ISASection isaSection) {
        isaSectionToDataEntryForm.remove(isaSection);
    }

    public static DataEntryForm getUserInterfaceForISASection(ISASection isaSection) {
        return isaSectionToDataEntryForm.get(isaSection);
    }

    public static void clearUserInterfaceAssignments() {
        isaSectionToDataEntryForm.clear();
    }

    public static Map<ISASection, DataEntryForm> getIsaSectionToDataEntryForm() {

        return isaSectionToDataEntryForm;
    }


    public static AssaySpreadsheet getUserInterfaceForAssay(Assay assay, StudyDataEntry sde) {

        if (assay.getTableReferenceObject() == null) {
            assay.setTableReferenceObject(
                    ConfigurationManager.selectTROForUserSelection(assay.getMeasurementEndpoint(), assay.getTechnologyType()));
        }

        if (assay.getMeasurementEndpoint().equals("") && assay.getTechnologyType().equals("")) {
            return new AssaySpreadsheet(sde, assay.getTableReferenceObject());
        } else {
            return new AssaySpreadsheet(sde, assay.getTableReferenceObject(),
                    assay.getMeasurementEndpoint(), assay.getTechnologyType());
        }

    }

    public static void setModified(boolean modified) {
        isModified = modified;
    }

    public static boolean isModified() {
        return isModified;
    }

    public static void resetForNextSession() {
        isaSectionToDataEntryForm.clear();
        screenInView = null;
    }

    public static void setScreenInView(Object objectInView) {
        screenInView = objectInView;
    }

    public static Object getScreenInView() {
        return screenInView;
    }
}
