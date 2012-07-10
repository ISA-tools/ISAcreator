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

    private static DataEntryReferenceObject investigationDataEntryReferenceObject;

    private static String currentlySelectedFieldName;

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
}
