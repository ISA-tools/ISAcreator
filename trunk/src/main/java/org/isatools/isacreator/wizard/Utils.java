package org.isatools.isacreator.wizard;

import org.apache.commons.collections15.map.ListOrderedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 20/01/2011
 *         Time: 12:05
 */
public class Utils {

    protected static Map<Integer, String> getGroupFactors(List<TempFactors> factors) {

        List<String> tempList1 = new ArrayList<String>();
        List<String> tempList2 = new ArrayList<String>();
        List<String> finalList = new ArrayList<String>();

        for (TempFactors factor : factors) {
            boolean isUnit = false;

            for (TimeUnitPair tup : factor.getFactorLevels()) {
                if (!tup.getUnit().equals("")) {
                    isUnit = true;

                    break;
                }
            }

            for (TimeUnitPair tup : factor.getFactorLevels()) {
                if (isUnit) {
                    tempList1.add(tup.getTime() + "\t" + tup.getUnit());
                } else {
                    tempList1.add(tup.getTime());
                }
            }

            if (finalList.size() == 0) {
                for (String s : tempList1) {
                    finalList.add(s);
                }

                tempList1.clear();
            } else {
                for (String f : finalList) {
                    for (String t : tempList1) {
                        tempList2.add(f + "\t" + t);
                    }
                }

                finalList.clear();

                for (String s : tempList2) {
                    finalList.add(s);
                }

                tempList1.clear();
                tempList2.clear();
            }
        }

        Map<Integer, String> groups = new ListOrderedMap<Integer, String>();

        for (int i = 0; i < finalList.size(); i++) {
            groups.put(i, finalList.get(i));
        }

        return groups;
    }

}
