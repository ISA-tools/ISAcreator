package org.isatools.isacreator.gui;

import org.isatools.isacreator.configuration.MappingObject;
import org.isatools.isacreator.gui.reference.DataEntryReferenceObject;
import org.isatools.isacreator.spreadsheet.model.TableReferenceObject;

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

    private static DataEntryReferenceObject investigationDataEntryReferenceObject;
    private static String currentlySelectedFieldName;

    public static void setCurrentApplicationInstance(ISAcreator isacreatorEnvironment) {
        ApplicationManager.currentApplicationInstance = isacreatorEnvironment;
    }

    public static ISAcreator getCurrentApplicationInstance() {
        return currentApplicationInstance;
    }

    public static void setCurrentDataReferenceObject() {
        TableReferenceObject tro = ApplicationManager.getCurrentApplicationInstance().selectTROForUserSelection(MappingObject.INVESTIGATION);
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
        System.out.println("Currently editing field: " + colName);
        ApplicationManager.currentlySelectedFieldName = colName;
    }

    public static String getCurrentlySelectedFieldName() {
        return currentlySelectedFieldName;
    }
}
