package org.isatools.isacreator.visualization.workflowvisualization;


import org.isatools.macros.motiffinder.Motif;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class WorkflowInformation {

    private File file;
    private Map<String, Motif> motifs;
    private Map<String, Map<String, String>> motifToMacros;

    public WorkflowInformation(File file, Map<String, Motif> motifs) {
        this.file = file;
        this.motifs = motifs == null ? new HashMap<String, Motif>() : motifs;
        motifToMacros = new HashMap<String, Map<String, String>>();
    }

    public File getFile() {
        return file;
    }

    public Map<String, Motif> getMotifs() {
        return motifs;
    }

    public Map<String, Map<String, String>> getMotifToMacros() {
        return motifToMacros;
    }

    public void addMacroForMotif(String motif, String imageSize, String filename) {
        if (!motifToMacros.containsKey(motif)) {
            motifToMacros.put(motif, new HashMap<String, String>());
        }

        motifToMacros.get(motif).put(imageSize, filename);
    }
}
