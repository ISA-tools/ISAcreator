package org.isatools.isacreator.gs.gui;

import org.genomespace.client.DataManagerClient;
import org.genomespace.client.ui.*;
import org.genomespace.datamanager.core.GSFileMetadata;

import java.util.ArrayList;

/**
 * Created by the ISATeam.
 * Date: 05/11/2012
 * Time: 12:39
 *
 * From GS code
 */
public class RootTreeNode extends GSFileMetadataTreeNode {
    RootTreeNode(final GSFileMetadata fileMetadata, final DataManagerClient dataManagerClient) {
        super(fileMetadata, dataManagerClient, new ArrayList<String>());
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    public String toString() {
        return "GenomeSpace Files";
    }

    @Override
    public boolean childrenHaveBeenInitialised() {
        return true;
    }
}