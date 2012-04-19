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
        return investigationDataEntryReferenceObject;
    }
}
