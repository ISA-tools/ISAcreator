/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 “The contents of this file are subject to the CPAL version 1.0 (the “License”);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an “AS IS” basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 the specific language governing rights and limitations under the License.

 The Original Code is ISAcreator.
 The Original Developer is the Initial Developer. The Initial Developer of the
 Original Code is the ISA Team (Eamonn Maguire, eamonnmag@gmail.com;
 Philippe Rocca-Serra, proccaserra@gmail.com; Susanna-Assunta Sansone, sa.sanson@gmail.com;
 http://www.isa-tools.org). All portions of the code written by the ISA Team are
 Copyright (c) 2007-2011 ISA Team. All Rights Reserved.

 EXHIBIT B. Attribution Information
 Attribution Copyright Notice: Copyright (c) 2008-2011 ISA Team
 Attribution Phrase: Developed by the ISA Team
 Attribution URL: http://www.isa-tools.org
 Graphic Image provided in the Covered Code as file: http://isa-tools.org/licenses/icons/poweredByISAtools.png
 Display of Attribution Information is required in Larger Works which are defined in the CPAL as a work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.

 Sponsors:
 The ISA Team and the ISA software suite have been funded by the EU Carcinogenomics project (http://www.carcinogenomics.eu), the UK BBSRC (http://www.bbsrc.ac.uk), the UK NERC-NEBC (http://nebc.nerc.ac.uk) and in part by the EU NuGO consortium (http://www.nugo.org/everyone).
 */

package org.isatools.isacreator.filechooser;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class LocalBrowser extends FileBrowser {

    private String currentDirectory;
    private static final String HOME = System.getProperty("user.home");

    public LocalBrowser() {
        super(FileBrowser.LOCAL_FILE_SYSTEM);
        currentDirectory = HOME;
    }

    /**
     * Return all the files in the directory. Also groups files into categories here for output in the JTree
     *
     * @param top - The TreeNode to add to.
     */
    public void getFilesInDirectory(FileBrowserTreeNode top) {
        File directory = new File(currentDirectory);
        File[] contents = directory.listFiles();

        if (contents != null) {
            for (File f : contents) {

                if (f.isDirectory()) {
                    // add all directories to the tree first!
                    dirFiles.put(f.getName(), f);
                    top.add(new FileBrowserTreeNode(f.getName(), false,
                            FileBrowserTreeNode.DIRECTORY));
                } else {
                    String extension = getExtension(f.getName());

                    if (!f.isHidden()) {
                        if (fileMap.get(extension) == null) {
                            fileMap.put(extension, new ArrayList<Object>());
                        }

                        // add to file map all the extensions, for addition to the tree node in a later step.
                        fileMap.get(extension).add(f);
                    }
                }
            }
        }
    }

    public FileBrowserTreeNode getHomeDirectory() throws IOException {
        currentDirectory = HOME;

        return refreshTree(currentDirectory);
    }

    public TreeNode getParentDirectory() throws IOException {
        File currentFile = new File(currentDirectory);


        if (currentFile.getParent() != null) {
            currentDirectory = currentFile.getParent();
            return refreshTree(currentDirectory);

        }

        return refreshTree(currentFile.getPath());
    }

    public TreeNode changeDirectory(String newDir) {
        currentDirectory = newDir;
        return refreshTree(currentDirectory);
    }

    public boolean isConnected() {
        return true;
    }

    public String getAbsoluteWorkingDirectory() {
        return new File(currentDirectory).getPath();
    }
}
