package org.isatools.isacreator.ontologybrowsingutils;

import org.isatools.isacreator.configuration.Ontology;
import org.isatools.isacreator.configuration.RecommendedOntology;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.FileNotFoundException;
import java.util.Map;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 16/05/2011
 *         Time: 15:45
 */
public interface OntologyTreeCreator {
     public DefaultMutableTreeNode createTree(Map<String, RecommendedOntology> ontologies) throws FileNotFoundException;
}
