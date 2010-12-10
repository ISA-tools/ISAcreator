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

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;


public class FTPBrowser extends FileBrowser {
    private static final Logger log = Logger.getLogger(FTPBrowser.class.getName());
    private FTPClient ftpClient;
    private String ftpDir;

    public FTPBrowser(String directory, String username, String password) throws NoSuchAlgorithmException, IOException {
        super(FileBrowser.REMOTE_FILE_SYSTEM);
        // if protocol if ftps
        if (directory.contains("ftps")) {

            ftpClient = new FTPSClient();

        } else {
            ftpClient = new FTPClient();
        }
        connect(directory, username, password);
    }

    public boolean connect(String directory, String username, String password) throws IOException {
        ftpDir = directory;
        ftpClient.connect(directory);
        ftpClient.login(username, password);

        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            ftpClient.disconnect();
            throw new ConnectionException(ftpClient.getReplyString());
        }
        return true;
    }

    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.disconnect();
        }
    }

    /**
     * Return all the files in the directory. Also groups files into categories here for output in the JTree
     *
     * @param top - The TreeNode to add to.
     */
    public void getFilesInDirectory(FileBrowserTreeNode top) {
        try {
            FTPFile[] contents = ftpClient.listFiles();
            if (contents != null) {
                for (FTPFile f : contents) {

                    if (f.isDirectory()) {
                        dirFiles.put(f.getName(), f);
                        top.add(new FileBrowserTreeNode(f.getName(), false,
                                FileBrowserTreeNode.DIRECTORY));

                    } else {
                        String extension = f.getName()
                                .substring(f.getName().lastIndexOf(".") +
                                        1).trim().toUpperCase();


                        if (fileMap.get(extension) == null) {
                            fileMap.put(extension, new ArrayList<Object>());
                        }
                        // add to file map all the extensions, for addition to the tree node in a later step.
                        fileMap.get(extension).add(f);
                    }
                }
            }


        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public TreeNode changeDirectory(String newDir) {
        try {
            if (ftpClient == null) {
                log.error("no connection available");
                throw new ConnectionException("no connection available");
            }
            ftpClient.changeWorkingDirectory(newDir);
            currentDirectory = ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            log.error("problems in client: " + e.getMessage());
        }
        return refreshTree(ftpDir + currentDirectory);
    }

    public TreeNode getHomeDirectory() throws IOException {
        if (ftpClient == null) {
            log.error("no connection available");
            throw new ConnectionException("no connection available");
        }
        ftpClient.changeWorkingDirectory("/");
        currentDirectory = ftpClient.printWorkingDirectory();
        return refreshTree(ftpDir + currentDirectory);
    }

    public TreeNode getParentDirectory() throws IOException {
        if (ftpClient == null) {
            log.error("no connection available");
            throw new ConnectionException("no connection available");
        }
        ftpClient.changeToParentDirectory();
        currentDirectory = ftpClient.printWorkingDirectory();
        return refreshTree(ftpDir + currentDirectory);
    }

    public String getAbsoluteWorkingDirectory() {
        try {
            return ftpDir + ftpClient.printWorkingDirectory();
        } catch (IOException e) {
            log.info("Problem occurred when getting working directory from FTP client: " + e.getMessage());
        }
        return ftpDir;
    }

    public boolean isConnected() {
        return ftpClient.isConnected();
    }
}
