/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 The contents of this file are subject to the CPAL version 1.0 (the License);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an AS IS basis,
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

import org.apache.commons.net.ftp.FTPFile;

import javax.swing.tree.TreeNode;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class FileBrowser {
    protected static final int LOCAL_FILE_SYSTEM = 0;
    public static final int REMOTE_FILE_SYSTEM = 1;


    protected Map<String, List<Object>> fileMap;
    protected Map<String, Object> dirFiles;
    protected String currentDirectory;
    private int currentFileSystem;

    public FileBrowser(int currentFileSystem) {
        this.currentFileSystem = currentFileSystem;
        fileMap = new HashMap<String, List<Object>>();
        dirFiles = new HashMap<String, Object>();
    }

    public void addFileGroupsToTree(FileBrowserTreeNode top) {
        for (String filetype : fileMap.keySet()) {
            // specify for types an appropriate icon
            int parentNodeType = FileBrowserTreeNode.ITEM_LIST;
            int childNodeType = FileBrowserTreeNode.FILE_TYPE;

            FileBrowserTreeNode categoryNode = new FileBrowserTreeNode(filetype, true,
                    parentNodeType);

            for (Object f : fileMap.get(filetype)) {
                String fileName;
                if (currentFileSystem == LOCAL_FILE_SYSTEM) {
                    fileName = ((File) f).getName();
                } else {
                    fileName = ((FTPFile) f).getName();
                }
                categoryNode.add(new FileBrowserTreeNode(fileName, false,
                        childNodeType));
            }

            top.add(categoryNode);
        }
    }

    public FileBrowserTreeNode refreshTree(String dir) {
        FileBrowserTreeNode top = new FileBrowserTreeNode(dir, true, FileBrowserTreeNode.DIRECTORY);
        fileMap.clear();
        dirFiles.clear();
        addRoots((FileBrowserTreeNode) top.getRoot());
        getFilesInDirectory(top);
        addFileGroupsToTree(top);

        return top;
    }

    public int getFileSystemType() {
        return currentFileSystem;
    }

    public abstract void addRoots(FileBrowserTreeNode top);

    public String getExtension(String name) {
        return name.substring(name.lastIndexOf(".") + 1).trim().toUpperCase();
    }

    public Map<String, List<Object>> getFileMap() {
        return fileMap;
    }

    public void setFileMap(Map<String, List<Object>> fileMap) {
        this.fileMap = fileMap;
    }

    public Map<String, Object> getDirFiles() {
        return dirFiles;
    }

    public void setDirFiles(Map<String, Object> dirFiles) {
        this.dirFiles = dirFiles;
    }

    protected abstract void getFilesInDirectory(FileBrowserTreeNode top);

    public abstract String getAbsoluteWorkingDirectory();

    public abstract TreeNode getHomeDirectory() throws IOException;

    public abstract TreeNode getParentDirectory() throws IOException;

    public abstract TreeNode changeDirectory(String newDir);

    public abstract boolean isConnected();


}
