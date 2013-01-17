package org.isatools.isacreator.gs.gui;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSDirectoryListing;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.isacreator.filechooser.FileSystemTreeCellRenderer;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by the ISATeam.
 * Date: 05/11/2012
 * Time: 12:38
 *
 * From GS code
 */
public class GSTree extends JTree {
    GSTree(final DataManagerClient dataManagerClient, final List<String> acceptableExtensions) {
        super(createTopAndFirstTier(dataManagerClient, acceptableExtensions));
        setShowsRootHandles(true);
        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        setCellRenderer(new FileSystemTreeCellRenderer());
    }

    GSTree(final DataManagerClient dataManagerClient) {
        this(dataManagerClient, new ArrayList<String>());
    }

    private static TreeModel createTopAndFirstTier(
            final DataManagerClient dataManagerClient,
            final List<String> acceptableExtensions)
    {
        final GSDirectoryListing dirListing = dataManagerClient.listDefaultDirectory();
        final Vector<GSFileMetadata> filesMetadata = new Vector(dirListing.getContents());
        Collections.sort(filesMetadata, new GSFileMetadataComparator());

        final RootTreeNode top =
                new RootTreeNode(dirListing.getDirectory(), dataManagerClient);

        final Iterator<GSFileMetadata> iter = filesMetadata.iterator();
        while (iter.hasNext()) {
            final GSFileMetadata metadata = iter.next();
            top.add(new GSFileMetadataTreeNode(metadata, dataManagerClient,
                    acceptableExtensions));
        }

        return new DefaultTreeModel(top);
    }

    static final class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(final JTree tree, final Object value,
                                                      final boolean sel,
                                                      final boolean expanded,
                                                      final boolean leaf, final int row,
                                                      final boolean hasFocus)
        {
            if (value instanceof GSFileMetadataTreeNode) {
                final GSFileMetadataTreeNode fileMetaTreeNode =
                        (GSFileMetadataTreeNode)value;
                if (fileMetaTreeNode.isEnabled()) {
                    setTextSelectionColor(Color.BLACK);
                    setTextNonSelectionColor(Color.BLACK);
                } else {
                    setTextSelectionColor(Color.GRAY);
                    setTextNonSelectionColor(Color.GRAY);
                }
            }
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            return this;
        }
    }
}