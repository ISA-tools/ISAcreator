package org.isatools.isacreator.gui.commentui;

import org.isatools.isacreator.configuration.FieldObject;

public class TableAddCommentGUI extends AbstractAddCommentGUI {

    public TableAddCommentGUI() {
        super();
    }

    @Override
    public void addFieldsToDisplay(FieldObject fieldObject) {
        // implement.
    }

    @Override
    public boolean okToAddField(String fieldName) {
        return true;
    }
}
