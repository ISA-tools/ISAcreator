package org.isatools.isacreator.io.exportisa;

import org.apache.axis.utils.StringUtils;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.ontologymanager.OntologyManager;
import org.isatools.isacreator.ontologymanager.OntologySourceRefObject;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 01/07/2012
 *         Time: 14:06
 */
public abstract class ISAFileOutput {

    public abstract void saveISAFiles(boolean removeEmptyColumns, Investigation investigation);

    public String getOntologiesUsedOutput() {
        String[] headerTerms = new String[]{
                "Term Source Name", "Term Source File", "Term Source Version",
                "Term Source Description"
        };
        String toReturn = "ONTOLOGY SOURCE REFERENCE\n";

        for (int i = 0; i < headerTerms.length; i++) {
            StringBuffer line = new StringBuffer(headerTerms[i] + "\t");
            String val = "";
            for (OntologySourceRefObject anOntologiesUsed : OntologyManager.getOntologiesUsed()) {

                if (headerTerms[i].equals("Term Source Name")) {
                    val = anOntologiesUsed.getSourceName();
                } else if (headerTerms[i].equals("Term Source File")) {
                    val = anOntologiesUsed.getSourceFile();
                } else if (headerTerms[i].equals("Term Source Version")) {
                    val = anOntologiesUsed.getSourceVersion();
                } else if (headerTerms[i].equals("Term Source Description")) {
                    val = anOntologiesUsed.getSourceDescription();
                }

                addToLine(line, StringUtils.isEmpty(val) ? " " : val);
            }

            // add new line to everything line but the last line
            if (i != (headerTerms.length - 1)) {
                line.append("\n");
            }

            toReturn += line;
        }

        return toReturn;
    }

    private void addToLine(StringBuffer line, String toAdd) {
        if (toAdd == null) {
            toAdd = "";
        }

        if (!toAdd.equals("")) {
            toAdd = toAdd.trim();

            line.append("\"").append(toAdd).append("\"\t");
        }
    }
}
