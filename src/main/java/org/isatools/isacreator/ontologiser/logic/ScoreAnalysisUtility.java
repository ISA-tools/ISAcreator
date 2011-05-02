package org.isatools.isacreator.ontologiser.logic;

import org.isatools.isacreator.ontologiser.model.SuggestedAnnotation;
import org.isatools.isacreator.ontologymanager.bioportal.model.ScoringConfidence;
import org.isatools.isacreator.utils.datastructures.ISAPair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Calculates the median score and assigns scoring confidence depending on how much other scores deviate from the median
 * Significantly higher than the median will result in a high score, the same for the lower bounds.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class ScoreAnalysisUtility {

    /**
     * Returns the max score along with the corresponding SuggestedAnnotation object
     *
     * @param annotations List<SuggestedAnnotation> annotations
     * @return ISAPair<Integer, SuggestedAnnotation> where the Integer is the highest score and SuggestedAnnotation is the corresponding SuggestedAnnotation instance
     */
    public static ISAPair<Integer, SuggestedAnnotation> getMaxScore(List<SuggestedAnnotation> annotations) {

        int maxScore = Integer.MIN_VALUE;
        SuggestedAnnotation value = null;
        for (SuggestedAnnotation annotation : annotations) {

            if (annotation.getAnnotatorResult().getScore() > maxScore) {
                maxScore = annotation.getAnnotatorResult().getScore();
                value = annotation;
            }
        }

        return new ISAPair<Integer, SuggestedAnnotation>(maxScore, value);
    }

    public static double getMedian(List<SuggestedAnnotation> annotations) {

        List<Integer> scores = extractScoresFromAnnotations(annotations);

        Collections.sort(scores);

        // if an even number of items
        if (scores.size() % 2 == 0) {

            // need
            int index1 = (scores.size() / 2);
            int index2 = index1 - 1;
            return (scores.get(index1) + scores.get(index2)) / 2;

        } else {
            // if there are 5 items in a list, then the item we want is in the middle, in position 2 in the list. To get this
            // we subtract 1 from the list size then divide the size of the list by 2. 5 { 0 1 *2* 3 4 } :: 5-1 = 4/2 = 2.
            int useIndex = ((scores.size() - 1) / 2);
            return scores.get(useIndex);
        }
    }

    private static List<Integer> extractScoresFromAnnotations(List<SuggestedAnnotation> annotations) {
        List<Integer> scores = new ArrayList<Integer>();

        for (SuggestedAnnotation annotation : annotations) {
            scores.add(annotation.getAnnotatorResult().getScore());
        }

        return scores;
    }

    public static void assignConfidenceLevels(List<SuggestedAnnotation> annotations) {

        double medianScore = getMedian(annotations);
        ISAPair<Integer, SuggestedAnnotation> maxValue = getMaxScore(annotations);

        for (SuggestedAnnotation annotation : annotations) {
            if (annotation.getAnnotatorResult().getScore() == maxValue.fst) {
                annotation.getAnnotatorResult().setScoringConfidence(ScoringConfidence.HIGH);
            } else if (annotation.getAnnotatorResult().getScore() >= medianScore) {
                annotation.getAnnotatorResult().setScoringConfidence(ScoringConfidence.MEDIUM);
            } else {
                annotation.getAnnotatorResult().setScoringConfidence(ScoringConfidence.LOW);
            }
        }
    }


}
