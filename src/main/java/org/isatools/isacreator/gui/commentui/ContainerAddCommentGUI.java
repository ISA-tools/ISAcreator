package org.isatools.isacreator.gui.commentui;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.SingletonMap;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;

import javax.swing.*;

public class ContainerAddCommentGUI extends AbstractAddCommentGUI {

    private InvestigationDataEntry investigationDataEntryEnvironment;

    public ContainerAddCommentGUI(InvestigationDataEntry investigationDataEntryEnvironment) {
        super();
        this.investigationDataEntryEnvironment = investigationDataEntryEnvironment;
    }

    @Override
    public void addFieldsToDisplay(final FieldObject field) {

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                OrderedMap<String, String> fieldValues = new SingletonMap<String, String>(field.getFieldName(), "");
                investigationDataEntryEnvironment.getInvestigation().getReferenceObject().addFieldObject(field);

                investigationDataEntryEnvironment.addFieldsToPanel(
                        investigationDataEntryEnvironment.getInvestigationDetailsPanel(),
                        InvestigationFileSection.INVESTIGATION_SECTION,
                        fieldValues,
                        investigationDataEntryEnvironment.getInvestigation().getReferenceObject()
                );

                investigationDataEntryEnvironment.repaint();
                investigationDataEntryEnvironment.validate();
            }
        });

    }

    @Override
    public boolean okToAddField(String fieldName) {
        return !investigationDataEntryEnvironment.getInvestigation().getFieldValues().containsKey(fieldName);
    }

    public static void main(String[] args) {
        new ContainerAddCommentGUI(null);
    }
}
