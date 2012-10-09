package org.isatools.isacreator.protocolselector;

import org.isatools.isacreator.api.utils.StudyUtils;
import org.isatools.isacreator.autofilterfield.AutoCompleteUI;
import org.isatools.isacreator.autofilterfield.DefaultAutoFilterCellEditor;
import org.isatools.isacreator.model.Protocol;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/08/2011
 *         Time: 12:24
 */
public class ProtocolSelectorCellEditor extends DefaultAutoFilterCellEditor<Protocol> {

    public ProtocolSelectorCellEditor(Spreadsheet spreadsheet) {
        super(spreadsheet);
    }

    @Override
    protected void updateContent() {
        if (StudyUtils.isModified(getStudyFromSpreadsheet().getStudyId())) {
            selector.updateContent(getStudyFromSpreadsheet().getProtocols());
        }
    }

    @Override
    public void performAdditionalTasks() {
        // nothing else to do...
    }

    public void instantiateSelectorIfRequired() {
        if (selector == null) {
            selector = new AutoCompleteUI<Protocol>(this, getStudyFromSpreadsheet().getProtocols(), new ProtocolSelectorListCellRenderer());
            selector.createGUI();
            selector.setLocation(calculateDisplayLocation(currentTable, currentRow, currentColumn));
        }

        updateContent();
    }
}
