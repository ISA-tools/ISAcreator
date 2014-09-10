package org.isatools.isacreator.validateconvert.ui.ENAReceipt;

import org.isatools.errorreporter.model.ErrorLevel;
import org.isatools.errorreporter.model.ErrorMessage;

import java.util.*;

/**
 * User: eamonnmaguire
 * Date: 01/09/2014
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */

public class ENAReceipt {

    private Set<String> experimentAccessions, sampleAccession, runAccessions, studyAccessions, infoMessages, errors;

    public ENAReceipt(Set<String> experimentAccessions,
                      Set<String> sampleAccession,
                      Set<String> runAccessions,
                      Set<String> studyAccessions,
                      Set<String> infoMessages,
                      Set<String> errors) {
        this.experimentAccessions = experimentAccessions;
        this.sampleAccession = sampleAccession;
        this.runAccessions = runAccessions;
        this.studyAccessions = studyAccessions;
        this.infoMessages = infoMessages;
        this.errors = errors;
    }

    public Set<String> getExperimentAccessions() {
        return experimentAccessions;
    }

    public Set<String> getSampleAccessions() {
        return sampleAccession;
    }

    public Set<String> getRunAccessions() {
        return runAccessions;
    }

    public Set<String> getStudyAccessions() {
        return studyAccessions;
    }

    public Set<String> getInfoMessages() {
        return infoMessages;
    }

    public Set<String> getErrors() {
        return errors;
    }

    public List<ErrorMessage> getMessagesForDisplay(String studyId) {
        List<ErrorMessage> messages = new ArrayList<ErrorMessage>();

        for(String info: getInfoMessages()){
            messages.add(new ErrorMessage(ErrorLevel.INFO, info));
        }

        for(String error : getErrors()) {
           messages.add(new ErrorMessage(ErrorLevel.ERROR, error));
        }

        return messages;

    }
}
