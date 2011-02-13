package org.isatools.isacreator.mergeutil;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 26/01/2011
 *         Time: 11:19
 */
public class Utils {

    protected static boolean checkForConflictingFiles(String isa1Dir, String isa2Dir) {

        File dir1 = new File(isa1Dir);
        File dir2 = new File(isa2Dir);
        File[] dir1Files = dir1.listFiles();
        File[] dir2Files = dir2.listFiles();

        Set<String> fileNames = new HashSet<String>();

        for (File f : dir1Files) {
            if (!f.getName().startsWith("i_")) {
                fileNames.add(f.getName());
            }
        }

        for (File f : dir2Files) {
            if (fileNames.contains(f.getName())) {
                return true;
            } else {
                if (!f.getName().startsWith("i_")) {
                    fileNames.add(f.getName());
                }
            }
        }
        return false;
    }

    /**
     * Check directory to determine if an investigation file exists (given naming convention of i_<<name>>.txt) <- simply a preliminary check!
     *
     * @param dir - Directory to be searched
     * @return boolean determining if the current directory contains an investigation file
     */
    protected static boolean checkDirectoryForISATAB(String dir) {
        File candidateFile = new File(dir);

        if (candidateFile.isDirectory()) {
            File[] directoryContents = candidateFile.listFiles();
            for (File f : directoryContents) {
                if (f.getName().toLowerCase().startsWith("i_")) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
}
