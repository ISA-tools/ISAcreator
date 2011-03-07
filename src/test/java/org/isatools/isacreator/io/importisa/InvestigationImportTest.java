package org.isatools.isacreator.io.importisa;

import org.apache.commons.collections15.OrderedMap;
import org.isatools.isacreator.io.importisa.InvestigationFileProperties.InvestigationFileSections;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 07/03/2011
 *         Time: 11:53
 */
public class InvestigationImportTest {

    @Test
    public void loadInvestigationFile() {
        File testInvestigationFile = new File("isatab files/BII-I-1/i_Investigation.txt");

        InvestigationImport importer = new InvestigationImport();
        try {
            Map<String, OrderedMap<InvestigationFileSections, OrderedMap<String, List<String>>>> investigationFile = importer.importInvestigationFile(testInvestigationFile);

            for(String mainSection : investigationFile.keySet()) {
                System.out.println(mainSection);

                for(InvestigationFileSections section : investigationFile.get(mainSection).keySet()) {
                    System.out.println("\t" + section);

                    for(String sectionLabelsAndValues :  investigationFile.get(mainSection).get(section).keySet()) {
                        System.out.print("\t\t" + sectionLabelsAndValues);

                        for(String sectionValue : investigationFile.get(mainSection).get(section).get(sectionLabelsAndValues)) {
                            System.out.print("\t" + sectionValue);

                        }
                        System.out.println();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
