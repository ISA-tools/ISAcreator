package org.isatools.isacreator.gs.gui;

import org.genomespace.client.DataManagerClient;
import org.genomespace.datamanager.core.GSFileMetadata;
import org.isatools.isacreator.filechooser.FileBrowserTreeNode;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.Collections;
import java.util.List;

/**
 * From GS cdk
 *
 * User: agbeltran
 * Date: 05/11/2012
 * Time: 12:28
 *
 */
class GSFileMetadataTreeNode extends FileBrowserTreeNode {//DefaultMutableTreeNode {
    private static int UNINITIALISED = -1;
    private final GSFileMetadata fileMetadata;
    private final DataManagerClient dataManagerClient;
    private int childCount;
    private final List<String> acceptableExtensions;

    GSFileMetadataTreeNode(final GSFileMetadata fileMetadata,
                           final DataManagerClient dataManagerClient,
                           final List<String> acceptableExtensions)
    {
        super(fileMetadata, fileMetadata.isDirectory(), fileMetadata.isDirectory() ? FileBrowserTreeNode.DIRECTORY : FileBrowserTreeNode.FILE_TYPE);
        this.fileMetadata = fileMetadata;
        this.dataManagerClient = dataManagerClient;
        this.childCount = fileMetadata.isDirectory() ? UNINITIALISED : 0;
        this.acceptableExtensions = acceptableExtensions;
    }

    @Override
    public boolean isLeaf() {
        return !fileMetadata.isDirectory();
    }

    @Override
    public String toString() {
        return fileMetadata.getName();
    }

    @Override
    public int getChildCount() {
        if (childCount != UNINITIALISED)
            return childCount;

        initChildCount();
        return childCount;
    }

    private void initChildCount() {
        final List<GSFileMetadata> filesMetadata =
                dataManagerClient.list(fileMetadata).getContents();
        childCount = 0; // Warning: The way "childCount" is being updated piecemeal
        // in this method is essential!  Do not change it!!
        Collections.sort(filesMetadata, new GSFileMetadataComparator());
        for (final GSFileMetadata metadata : filesMetadata) {
            add(new GSFileMetadataTreeNode(metadata, dataManagerClient, acceptableExtensions));
            ++childCount;
        }
    }

    public GSFileMetadata getFileMetadata() {
        return fileMetadata;
    }

    public boolean isEnabled() {
        if (fileMetadata.isDirectory() || acceptableExtensions.isEmpty())
            return true;

        final String extension = getFileExtension(fileMetadata.getName());
        for (final String acceptableExtension : acceptableExtensions) {
            if (extension.equalsIgnoreCase(acceptableExtension))
                return true;
        }

        return false;
    }

    private static String getFileExtension(final String fileName) {
        final int lastDotPos = fileName.lastIndexOf('.');
        return (lastDotPos == -1) ? "" : fileName.substring(lastDotPos + 1);
    }

    public boolean childrenHaveBeenInitialised() {
        return childCount != UNINITIALISED;
    }
}