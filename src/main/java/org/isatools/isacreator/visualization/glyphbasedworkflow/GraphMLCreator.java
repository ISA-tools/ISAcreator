package org.isatools.isacreator.visualization.glyphbasedworkflow;

import org.isatools.isacreator.managers.ApplicationManager;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.macros.fileprocessing.isatab.ISAFileFlattener;
import org.isatools.macros.graph.graphio.GraphCreator;
import org.isatools.macros.graph.graphio.GraphFunctions;
import org.isatools.macros.gui.Experiment;
import org.isatools.macros.loaders.isa.ISAWorkflowLoader;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 03/09/2012
 *         Time: 14:52
 */
public class GraphMLCreator {

    // pass in filter on study samples to look at and create the tree based on that.
    private File graphMLFile;

    public File createGraphMLFileForExperiment(boolean targetedAssay) {
        return createGraphMLFileForExperiment(targetedAssay, null);
    }

    public File createGraphMLFileForExperiment(boolean isTargetedAssay, Set<String> selectedSamples) {
        try {

            graphMLFile = null;
            Investigation investigation = ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation();

            System.out.println("Investigation file is in:" + investigation.getReference());

            Collection<File> flattenedFiles = ISAFileFlattener.flattenISATabFiles(new File(investigation.getReference()).getParentFile(),
                    investigation, selectedSamples);

            // todo get the proper file back. Also, store experiments generated in some cache, or their file locations.
            // todo so that we don't regenerate every time. Need to detect change? :-o
            GraphCreator graphCreator = new GraphCreator();
            String assayInView = getNameOfAssayInView();

            for (File file : flattenedFiles) {

                String fileName = file.getName();
                System.out.println("Targeting: " + assayInView);
                if (isTargetedAssay && fileName.contains(assayInView)) {
                    System.out.println("Loading: " + file.getAbsolutePath());
                    graphCreator.loadGraph(new ISAWorkflowLoader(file, graphCreator.getNeo4JConnector().getGraphDB()));
                } else if (!isTargetedAssay) {
                    System.out.println("Loading: " + file.getAbsolutePath());
                    graphCreator.loadGraph(new ISAWorkflowLoader(file, graphCreator.getNeo4JConnector().getGraphDB()));
                }
            }

            System.out.println("Loading experiments");
            List<Experiment> experiments = GraphFunctions.loadExperiments(graphCreator.getNeo4JConnector().getGraphDB());
            System.out.println("Loaded " + experiments.size() + " experiments");

            for (Experiment experiment : experiments) {
                System.out.println("Analysing " + experiment.toString());
                if (isTargetedAssay && experiment.toString().contains(assayInView)) {
                    graphMLFile = graphCreator.createGraphMLForExperiment(experiment);
                    System.out.println("GraphML for experiment is in: " + graphMLFile.getAbsolutePath());
                }
            }

            // we want to shut it down afterwards to avoid having anything running on multiple experiments in view.
            graphCreator.getNeo4JConnector().getGraphDB().shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return graphMLFile;
    }

    private String getNameOfAssayInView() {
        if (ApplicationManager.getScreenInView() instanceof Assay) {
            Assay assay = (Assay) ApplicationManager.getScreenInView();
            return assay.getAssayReference();
        }
        return "";
    }
}
