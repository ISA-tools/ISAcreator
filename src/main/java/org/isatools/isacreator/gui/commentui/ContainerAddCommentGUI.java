package org.isatools.isacreator.gui.commentui;

import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.SingletonMap;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;

import javax.swing.*;

public class ContainerAddCommentGUI<T extends DataEntryForm> extends AbstractAddCommentGUI {

    private T entryEnvironment;

    public ContainerAddCommentGUI(T entryEnvironment) {
        super();
        this.entryEnvironment = entryEnvironment;
    }

    @Override
    public void addFieldsToDisplay(final FieldObject field) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                OrderedMap<String, String> fieldValues = new SingletonMap<String, String>(field.getFieldName(), "");
                entryEnvironment.generateAliases(fieldValues.keySet());

                if (entryEnvironment instanceof InvestigationDataEntry) {
                    entryEnvironment.getInvestigation().getReferenceObject().addFieldObject(field);
                    entryEnvironment.getInvestigation().getFieldValues().putAll(fieldValues);

                    entryEnvironment.addFieldsToPanel(
                            ((InvestigationDataEntry) entryEnvironment).getInvestigationDetailsPanel(),
                            InvestigationFileSection.INVESTIGATION_SECTION,
                            fieldValues,
                            entryEnvironment.getInvestigation().getReferenceObject()
                    );
                } else {
                    entryEnvironment.getStudy().getReferenceObject().addFieldObject(field);
                    entryEnvironment.getStudy().getFieldValues().putAll(fieldValues);

                    entryEnvironment.addFieldsToPanel(
                            ((StudyDataEntry) entryEnvironment).getStudyDetailsFieldContainer(),
                            InvestigationFileSection.STUDY_SECTION,
                            fieldValues,
                            entryEnvironment.getStudy().getReferenceObject()
                    );
                }


                entryEnvironment.repaint();
                entryEnvironment.validate();
            }
        });

    }

    @Override
    public boolean okToAddField(String fieldName) {
        if (entryEnvironment instanceof InvestigationDataEntry) {
            return !entryEnvironment.getInvestigation().getFieldValues().containsKey(fieldName);
        } else {
            return !entryEnvironment.getStudy().getFieldValues().containsKey(fieldName);
        }
    }

}
