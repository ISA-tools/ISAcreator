package org.isatools.isacreator.gui.commentui;

import org.isatools.isacreator.configuration.DataTypes;
import org.isatools.isacreator.configuration.FieldObject;
import org.isatools.isacreator.gui.DataEntryForm;
import org.isatools.isacreator.gui.StudyDataEntry;
import org.isatools.isacreator.gui.formelements.FieldTypes;
import org.isatools.isacreator.gui.formelements.StudyDesignSubForm;
import org.isatools.isacreator.gui.formelements.SubForm;
import org.isatools.isacreator.gui.formelements.SubFormField;
import org.isatools.isacreator.io.importisa.investigationproperties.InvestigationFileSection;

import javax.swing.*;
import java.util.List;

public class TableAddCommentGUI<T extends DataEntryForm> extends AbstractAddCommentGUI {

    private T parent;

    public TableAddCommentGUI(T parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void addFieldsToDisplay(final FieldObject fieldObject) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                if (parent instanceof StudyDataEntry) {


                    List<SubFormField> updatedFields = ((StudyDataEntry) parent).getStudyDesignSubform().getSubFormFields();
                    updatedFields.add(new SubFormField(fieldObject.getFieldName(), fieldObject.getDatatype() == DataTypes.STRING ? SubFormField.STRING : SubFormField.SINGLE_ONTOLOGY_SELECT));
                    ((StudyDataEntry) parent).getStudyDesignContainer().removeAll();
                    ((StudyDataEntry) parent).getStudyDesignContainer().revalidate();

                    int existingRecordSize;
                    String title = "";
                    FieldTypes type = null;

                    existingRecordSize = parent.getStudy().getStudyDesigns().size();
                    title = InvestigationFileSection.STUDY_DESIGN_SECTION.toString();
                    type = FieldTypes.DESIGN;

                    // let's guestimate the size of the subform to avoid scrolling which is annoying.

                    SubForm subform = new StudyDesignSubForm(title, type,
                            updatedFields, (existingRecordSize == 0) ? 2 : existingRecordSize, DataEntryForm.SUBFORM_WIDTH, parent.estimateSubformHeight(updatedFields.size()), parent);
                    subform.createGUI();

                    ((StudyDataEntry) parent).setStudyDesignSubform(subform);

                    ((StudyDataEntry) parent).getStudyDesignContainer().add(subform);
                    ((StudyDataEntry) parent).getStudyDesignContainer().repaint();
                    ((StudyDataEntry) parent).getStudyDesignContainer().revalidate();
                }
            }
        });
    }

    /**
     * List<SubFormField> updatedFields = studyDesignSubform.getSubFormFields();
     * updatedFields.add(new SubFormField("Comment[Hi]", SubFormField.STRING));
     * studyDesignContainer.removeAll();
     * studyDesignContainer.revalidate();
     * <p/>
     * studyDesignSubform = new StudyDesignSubForm(InvestigationFileSection.STUDY_DESIGN_SECTION.toString(), FieldTypes.DESIGN,
     * updatedFields, (study.getStudyDesigns().size() == 0) ? 2
     * : study.getStudyDesigns().size(), 300, 60, StudyDataEntry.this);
     * studyDesignSubform.createGUI();
     * <p/>
     * studyDesignContainer.add(studyDesignSubform);
     * studyDesignContainer.repaint();
     * studyDesignContainer.revalidate();
     */

    @Override
    public boolean okToAddField(String fieldName) {
        return true;
    }
}
