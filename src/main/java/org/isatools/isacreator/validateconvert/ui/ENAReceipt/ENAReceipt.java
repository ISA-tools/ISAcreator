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

    private Set<String> experimentAccessions, sampleAccession, runAccessions, errors;

    public ENAReceipt(Set<String> experimentAccessions, Set<String> sampleAccession, Set<String> runAccessions, Set<String> errors) {
        this.experimentAccessions = experimentAccessions;
        this.sampleAccession = sampleAccession;
        this.runAccessions = runAccessions;
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

    public Set<String> getErrors() {
        return errors;
    }

    public List<ErrorMessage> getErrorsForDisplay(String studyId) {
        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();

        for(String error : getErrors()) {
           errors.add(new ErrorMessage(ErrorLevel.ERROR, error));
        }

        return errors;

    }
}
