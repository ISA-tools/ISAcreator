package org.isatools.isacreator.spreadsheet.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 29/01/2012
 *         Time: 16:51
 */
public class ReferenceData {

    private List<List<String>> data = null;

    public ReferenceData() {
        this.data = new ArrayList<List<String>>();
    }

    public void addData(List<String> rowData) {
        data.add(rowData);
    }

    public List<List<String>> getData() {
        return data;
    }

    public Set<String> getDataInColumn(int columnIndex) {
        Set<String> columnData = new HashSet<String>();
        for (List<String> rowData : data) {
            if (columnIndex < rowData.size()) {
                columnData.add(rowData.get(columnIndex));
            }
        }

        return columnData;
    }

}
