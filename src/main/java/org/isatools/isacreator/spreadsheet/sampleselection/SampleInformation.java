package org.isatools.isacreator.spreadsheet.sampleselection;

import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/07/2011
 *         Time: 16:48
 */
public class SampleInformation {

    private int rowNumber;
    private String sampleName;
    private Map<String, String> additionalInformation;
    private Map<String, Integer> columnNamesToIndex;

    public SampleInformation(int rowNumber, String sampleName, Map<String, String> additionalInformation, Map<String, Integer> columnNamesToIndex) {
        this.rowNumber = rowNumber;
        this.sampleName = sampleName;
        this.additionalInformation = additionalInformation;
        this.columnNamesToIndex = columnNamesToIndex;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public Map<String, Integer> getColumnNamesToIndex() {
        return columnNamesToIndex;
    }

    public String getSampleName() {
        return sampleName;
    }


    public String getAdditionalInformation() {
        return extractSampleCharacteristics();
    }

    private String extractSampleCharacteristics() {
        StringBuilder value = new StringBuilder();

        for (String column : additionalInformation.keySet()) {
            if (column.contains("organism")) {
                value.append(additionalInformation.get(column));
                break;
            }
        }

        return value.toString().equals("") ? "No organism defined" : value.toString();
    }

    @Override
    public String toString() {
        return sampleName;
    }
}
