package org.isatools.isacreator.gui.commentui;

import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.InvestigationDataEntry;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.gui.formelements.*;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;

import javax.swing.*;
import java.util.List;

public class SubFormAddCommentGUI<T extends DataEntryForm> extends AbstractAddCommentGUI {

    private T parent;
    private FieldTypes fieldType;

    public SubFormAddCommentGUI(T parent, FieldTypes fieldType) {
        super();
        this.parent = parent;
        this.fieldType = fieldType;
    }

    @Override
    public void addFieldsToDisplay(final FieldObject fieldObject) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                List<SubFormField> updatedFields = parent.getSubFormForFieldType(fieldType).getSubFormFields();
                updatedFields.add(new SubFormField(fieldObject.getFieldName(), fieldObject.getDatatype() == DataTypes.STRING ? SubFormField.STRING : SubFormField.SINGLE_ONTOLOGY_SELECT));
                parent.getContainerForFieldType(fieldType).removeAll();
                parent.getContainerForFieldType(fieldType).revalidate();

                int existingRecordSize;
                String title = parent.getSubFormForFieldType(fieldType).getTitle();

                SubForm subform;

                switch (fieldType) {
                    case DESIGN:
                        existingRecordSize = parent.getStudy().getStudyDesigns().size();
                        subform = new StudyDesignSubForm(title, fieldType,
                                updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize,
                                DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                        break;
                    case FACTOR:
                        existingRecordSize = parent.getStudy().getFactors().size();
                        subform = new FactorSubForm(title, fieldType,
                                updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize,
                                DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                        break;
                    case PROTOCOL:
                        existingRecordSize = parent.getStudy().getProtocols().size();
                        subform = new ProtocolSubForm(title, fieldType,
                                updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize,
                                DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                        break;
                    case CONTACT:

                        existingRecordSize = parent instanceof StudyDataEntry ? parent.getStudy().getContacts().size() : parent.getInvestigation().getContacts().size();
                        subform = new ContactSubForm(title, fieldType,
                                updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize,
                                DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);

                        break;
                    case PUBLICATION:

                        existingRecordSize = parent instanceof StudyDataEntry ? parent.getStudy().getPublications().size() : parent.getInvestigation().getPublications().size();
                        subform = new PublicationSubForm(title, fieldType,
                                updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize,
                                DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                        break;
                    default:
                        subform = null;
                        break;
                }


                if (subform != null) subform.createGUI();

                parent.setSubFormForFieldType(fieldType, subform);

                parent.getContainerForFieldType(fieldType).add(subform);
                parent.getContainerForFieldType(fieldType).repaint();
                parent.getContainerForFieldType(fieldType).revalidate();

            }
        });
    }

    @Override
    public boolean okToAddField(String fieldName) {
        for (SubFormField field : parent.getSubFormForFieldType(fieldType).getSubFormFields()) {
            if (field.getFieldName().equals(fieldName)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isFieldAllowedInSection(String template, String fieldName) {
        return templateToFields.get(template).getFieldByName(fieldName).getSection().equals(getInvestigationFileSectionFromFieldType(fieldType).toString());
    }

    private InvestigationFileSection getInvestigationFileSectionFromFieldType(FieldTypes type) {
        switch (type) {
            case DESIGN:
                return InvestigationFileSection.STUDY_DESIGN_SECTION;
            case FACTOR:
                return InvestigationFileSection.STUDY_FACTORS;
            case PROTOCOL:
                return InvestigationFileSection.STUDY_PROTOCOLS;
            case CONTACT:
                return parent instanceof StudyDataEntry ? InvestigationFileSection.STUDY_CONTACTS : InvestigationFileSection.INVESTIGATION_CONTACTS_SECTION;
            case PUBLICATION:
                return parent instanceof StudyDataEntry ? InvestigationFileSection.STUDY_PUBLICATIONS : InvestigationFileSection.INVESTIGATION_PUBLICATIONS_SECTION;
            default:
                return InvestigationFileSection.INVESTIGATION_SECTION;

        }
    }


}
