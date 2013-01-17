package org.isatools.isacreator.gs.gui;

import org.genomespace.datamanager.core.GSFileMetadata;

import java.util.Comparator;

/**
 * Created by the ISATeam.
 * Date: 05/11/2012
 * Time: 12:29
 *
 * From GS code
 *
 */
final class GSFileMetadataComparator implements Comparator<GSFileMetadata> {
    @Override
    public int compare(final GSFileMetadata metadata1, final GSFileMetadata metadata2) {
        if (metadata1.isDirectory() || !metadata2.isDirectory())
            return -1;
        if (metadata2.isDirectory() || !metadata1.isDirectory())
            return +1;

        return metadata1.getPath().compareToIgnoreCase(metadata2.getPath());
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof GSFileMetadataComparator;
    }
}
