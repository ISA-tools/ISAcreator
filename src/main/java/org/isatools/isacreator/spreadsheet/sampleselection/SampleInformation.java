package org.isatools.isacreator.spreadsheet.sampleselection;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/07/2011
 *         Time: 16:48
 */
public class SampleInformation {

    private String sampleName;
    private String additionalInformation;

    public SampleInformation(String sampleName, String additionalInformation) {
        this.sampleName = sampleName;
        this.additionalInformation = additionalInformation;
    }

    public String getSampleName() {
        return sampleName;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    @Override
    public String toString() {
        return sampleName;
    }
}
