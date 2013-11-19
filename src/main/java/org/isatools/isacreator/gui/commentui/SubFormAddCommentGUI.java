package org.isatools.isacreator.gui.commentui;

import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.gui.formelements.*;

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

                if (parent instanceof StudyDataEntry) {


                    List<SubFormField> updatedFields = ((StudyDataEntry) parent).getSubFormForFieldType(fieldType).getSubFormFields();
                    updatedFields.add(new SubFormField(fieldObject.getFieldName(), fieldObject.getDatatype() == DataTypes.STRING ? SubFormField.STRING : SubFormField.SINGLE_ONTOLOGY_SELECT));
                    ((StudyDataEntry) parent).getContainerForFieldType(fieldType).removeAll();
                    ((StudyDataEntry) parent).getContainerForFieldType(fieldType).revalidate();

                    int existingRecordSize;
                    String title = ((StudyDataEntry) parent).getSubFormForFieldType(fieldType).getTitle();

                    SubForm subform;

                    switch (fieldType) {
                        case DESIGN:
                            existingRecordSize = parent.getStudy().getStudyDesigns().size();
                            subform = new StudyDesignSubForm(title, fieldType,
                                    updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize, DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                            break;
                        case FACTOR:
                            existingRecordSize = parent.getStudy().getFactors().size();
                            subform = new FactorSubForm(title, fieldType,
                                    updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize, DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                            break;
                        case PROTOCOL:
                            existingRecordSize = parent.getStudy().getProtocols().size();
                            subform = new ProtocolSubForm(title, fieldType,
                                    updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize, DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                            break;
                        case CONTACT:
                            existingRecordSize = parent.getStudy().getContacts().size();
                            subform = new ContactSubForm(title, fieldType,
                                    updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize, DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);

                            break;
                        case PUBLICATION:
                            existingRecordSize = parent.getStudy().getPublications().size();
                            subform = new PublicationSubForm(title, fieldType,
                                    updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize, DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                            break;
                        default:
                            subform = null;
                            break;
                    }


                    if (subform != null) subform.createGUI();

                    ((StudyDataEntry) parent).setSubFormForFieldType(fieldType, subform);

                    ((StudyDataEntry) parent).getContainerForFieldType(fieldType).add(subform);
                    ((StudyDataEntry) parent).getContainerForFieldType(fieldType).repaint();
                    ((StudyDataEntry) parent).getContainerForFieldType(fieldType).revalidate();
                }
            }
        });
    }

    @Override
    public boolean okToAddField(String fieldName) {
        return true;
    }
}
