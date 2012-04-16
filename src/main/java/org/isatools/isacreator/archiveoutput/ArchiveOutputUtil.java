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


package org.isatools.isacreator.archiveoutput;

import org.apache.log4j.Logger;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.gui.ApplicationManager;
import org.isatools.isacreator.io.OutputISAFiles;
import org.isatools.isacreator.model.Assay;
import org.isatools.isacreator.model.Investigation;
import org.isatools.isacreator.model.Study;
import org.isatools.isacreator.spreadsheet.Spreadsheet;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.io.*;
import java.util.*;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * OutputISArchive class provides GUI to output the ISATAB files/Archive to a location.
 *
 * @author Eamonn Maguire
 */
public class ArchiveOutputUtil extends JPanel implements Runnable {
    private static final Logger log = Logger.getLogger(ArchiveOutputUtil.class.getName());

    public static final int LOW_COMPRESSION = Deflater.BEST_SPEED;
    public static final int MED_COMPRESSION = Deflater.DEFAULT_COMPRESSION;
    public static final int HIGH_COMPRESSION = Deflater.BEST_COMPRESSION;

    private JLabel archiveOutputStatus;

    private String reference;
    private String outputLocation;
    private int compressionLevel = LOW_COMPRESSION;


    private ViewErrorPane browseViewErrorPane;
    private OutputISAFiles outputISATAB;
    private File archiveLocation;

    private ArchivingStatistics statistics;

    private Map<String, List<String>> localFiles;
    private Map<String, List<ArchiveOutputError>> missingFiles;
    private Map<String, List<ArchiveOutputError>> missingData;

    private Set<String> addedFilePaths;

    /**
     * OutputISArchive Constructor
     */
    public ArchiveOutputUtil() {
        missingFiles = null;
        archiveLocation = null;
        localFiles = null;
        browseViewErrorPane = new ViewErrorPane(ApplicationManager.getCurrentApplicationInstance());
        outputISATAB = new OutputISAFiles(ApplicationManager.getCurrentApplicationInstance());
        createProgressLabel();
        setOpaque(false);
    }

    public String getReference() {
        return reference;
    }

    /**
     * Create the Panels containing all of the components
     */
    public void createProgressLabel() {
        //progressAndStatusPanel.add(progress);
        archiveOutputStatus = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.DARK_GREEN_COLOR, JLabel.CENTER);
    }

    /**
     * Creates the Files in a Separate Thread so as to be able to update the progress bar
     */
    public void run() {
        try {
            addedFilePaths = new HashSet<String>();
            statistics = new ArchivingStatistics();
            statistics.setStartTime(System.currentTimeMillis());
            Investigation inv = ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation();

            if (inv.getReference().equals("")) {
                archiveOutputStatus.setText("<html>Please <b>save</b> the submission first!</html>");
                return;
            }

            log.info("Starting creating of archive");
            archiveOutputStatus.setText("<html><i>creating archive...please wait</i></html>");
            localFiles = new HashMap<String, List<String>>();
            missingData = new HashMap<String, List<ArchiveOutputError>>();

            log.info("Gathering datafiles to be zipped");
            for (Study s : inv.getStudies().values()) {
                List<ArchiveOutputError> missingDataResult;

                if ((missingDataResult = s.getStudySample().getSpreadsheetUI().getSpreadsheet().checkForCompleteness()).size() > 0) {
                    log.info("Missing " + missingDataResult.size() + " files in the study sample file...");

                    missingData.put(s.getStudySample().getAssayReference(), missingDataResult);
                }

                for (Assay a : s.getAssays().values()) {
                    localFiles.put(a.getAssayReference(), new ArrayList<String>());
                    // getFilesDefinedInTable will get all files and directory paths listed in each file column in the table!
                    for (String file : a.getSpreadsheetUI().getSpreadsheet().getSpreadsheetFunctions().getFilesDefinedInTable()) {
                        if (!file.startsWith("ftp") && !file.startsWith("http")) {

                            localFiles.get(a.getAssayReference()).add(file);
                        }
                    }

                    if ((missingDataResult = a.getSpreadsheetUI().getSpreadsheet().checkForCompleteness()).size() > 0) {
                        log.info("Missing " + missingDataResult.size() + " files in file " + a.getAssayReference());
                        missingData.put(a.getAssayReference(), missingDataResult);
                    }

                    a.getSpreadsheetUI().getSpreadsheet().changeFilesToRelativeOrAbsolute(Spreadsheet.SWITCH_RELATIVE);
                }
            }


            outputISATAB.saveISAFiles(true, inv);

            File parentFile = new File(new File(inv.getReference()).getParent());

            File[] isaFiles = parentFile.listFiles();

            log.info("Starting to zip files and directories");
            // zip up contents. if process is unsuccessful, clean up any files output which may be incomplete.
            if (!zipDirectoryContents(parentFile.getName() + "_archive", outputLocation, localFiles, isaFiles)
                    && archiveLocation != null && archiveLocation.exists()) {
                updateArchiveOutputStatusLabel(
                        "<html><b>ISArchive has not been created! Check the log file!</b></html>");
                archiveLocation.delete();
            }

            // reset file names back to absolute locations.
            for (Study study : inv.getStudies().values()) {
                for (Assay assay : study.getAssays().values()) {
                    assay.getSpreadsheetUI().getSpreadsheet()
                            .changeFilesToRelativeOrAbsolute(Spreadsheet.SWITCH_ABSOLUTE);
                }
            }

            outputISATAB.saveISAFiles(false, inv);
        } catch (OutOfMemoryError ome) {
            // this will happen only with very large assays! if it does, inform the user to increase the memory allowance to ISAcreator
            log.info("The system ran out of memory whilst trying to zip the files. Ensure that overall archive size is below 4GB.");
            updateArchiveOutputStatusLabel("<html>increase memory!</html>");
            // force an immediate garbage collect to remove redundant objects immediately!
            System.gc();
        }
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    private void updateArchiveOutputStatusLabel(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                archiveOutputStatus.setText(message);
            }
        });
    }

    public void setupProgressBar() {
        updateArchiveOutputStatusLabel("Ready to create files...");
        archiveOutputStatus.setForeground(UIHelper.DARK_GREEN_COLOR);
        // hide missing information if it is not already hidden.
    }

    private JLayeredPane getAssayByRef(String assayRef) {
        Investigation investigation = ApplicationManager.getCurrentApplicationInstance().getDataEntryEnvironment().getInvestigation();
        return investigation.getStudies().get(investigation.getAssays().get(assayRef)).getAssays().get(assayRef).getSpreadsheetUI();
    }

    public void zipDir(String sourceDir, File dirToZip, ZipOutputStream zos, byte[] buffer) {
        try {
            //create a new File object based on the directory we
            //get a listing of the directory content
            String[] dirList = dirToZip.list();
            int bytesIn;
            //loop through dirList, and zip the files
            for (String aDirList : dirList) {
                File f = new File(dirToZip, aDirList);

                if (!f.getName().startsWith(".")) {
                    if (f.isDirectory()) {
                        //if the File object is a directory, call this
                        //function again to add its content recursively

                        zipDir(sourceDir, f, zos, buffer);
                        //loop again
                        continue;
                    }

                    //create a FileInputStream on top of f
                    FileInputStream fis = new FileInputStream(f);

                    //place the zip entry in the ZipOutputStream object
                    zos.putNextEntry(new ZipEntry(getPathForZip(sourceDir, f.getPath())));

                    statistics.addToNumberOfFiles(1);
                    statistics.addToUncompressedSize(f.length());
                    //now write the content of the file to the ZipOutputStream
                    while ((bytesIn = fis.read(buffer)) != -1) {
                        zos.write(buffer, 0, bytesIn);
                    }
                    //close the Stream
                    fis.close();
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private String getPathForZip(String sourceDir, String path) {
        return path.substring(path.lastIndexOf(sourceDir));
    }

    private void zipFile(File file, ZipOutputStream out, byte[] buffer) throws IOException {

        if (!addedFilePaths.contains(file.getAbsolutePath())) {

            FileInputStream fis = new FileInputStream(file);
            out.putNextEntry(new ZipEntry(file.getName()));

            int length;
            while ((length = fis.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            out.closeEntry();
            fis.close();

            addedFilePaths.add(file.getAbsolutePath());
            log.info("Successfully added " + file.getAbsolutePath());
        }

    }


    @SuppressWarnings({"ConstantConditions"})
    public boolean zipDirectoryContents(String archiveName, String directory,
                                        Map<String, List<String>> filesToZip, File[] isaFiles) {

        missingFiles = new HashMap<String, List<ArchiveOutputError>>();
        byte[] buffer = new byte[1024];
        ZipOutputStream out = null;

        try {
            archiveLocation = new File(directory +
                    File.separator + archiveName + ".zip");

            log.info("Attempting output to " + archiveLocation.getAbsolutePath());
            out = new ZipOutputStream(new FileOutputStream(archiveLocation));


            out.setLevel(getCompressionLevel());

            // zip up the rest of the ISAfiles
            updateArchiveOutputStatusLabel("zipping files");
            log.info("Zipping files");

            for (File isafile : isaFiles) {
                log.info("Attempting to zip " + isafile.getName());
                if (isafile.isFile()) {
                    statistics.addToNumberOfFiles(1);
                    statistics.addToUncompressedSize(isafile.length());
                    zipFile(isafile, out, buffer);
                }
            }

            // zip up the data files contained in each assay!
            for (String parentISAFile : filesToZip.keySet()) {
                for (String containingFile : filesToZip.get(parentISAFile)) {
                    File f = new File(containingFile);

                    // this will test if the file being mentioned is in a relative location to the directory, just in case!
                    f = testIfRelativeOrAbsolutePath(f, isaFiles[0].getParentFile());

                    if (f.exists()) {

                        if (f.isDirectory()) {
                            log.info("Zipping directory: " + f.getAbsolutePath());
                            zipDir(f.getName(), f, out, buffer);
                        } else {
                            log.info("Zipping file: " + f.getAbsolutePath());


                            updateArchiveOutputStatusLabel(
                                    "<html><b>Compressing:</b> " + f.getName() + "</html>");
                            statistics.addToNumberOfFiles(1);
                            statistics.addToUncompressedSize(f.length());

                            zipFile(f, out, buffer);
                        }

                    } else {
                        if (missingFiles.get(parentISAFile) == null) {
                            missingFiles.put(parentISAFile, new ArrayList<ArchiveOutputError>());
                        }
                        missingFiles.get(parentISAFile).add(new ArchiveOutputError(containingFile, getAssayByRef(parentISAFile), "", -1, -1));
                    }
                }
            }

            statistics.setArchive(archiveLocation);
            statistics.setEndTime(System.currentTimeMillis());

            if (missingFiles.size() > 0 || missingData.size() > 0) {
                log.info("Archive creation failed, there are " + missingFiles.size() + " missing files referenced in the submission.");

                updateArchiveOutputStatusLabel("<html><strong>Archive output failed<strong>, there are <i>files</i> or <i>data</i> missing.</html>");

                browseViewErrorPane.refreshErrorView(getErrors());
                browseViewErrorPane.setVisible(true);
                browseViewErrorPane.setPoppedOut(true);
                firePropertyChange("archiveOutputFailed", false, true);
                return false;
            } else {
                log.error("Archive output successful!");
                firePropertyChange("archiveOutputCompleted", false, true);
                return true;
            }

        } catch (FileNotFoundException e) {
            log.error("File not found..." + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private File testIfRelativeOrAbsolutePath(File file, File isaDirectory) {
        if (!file.exists()) {
            return new File(isaDirectory.getAbsolutePath() + File.separator + file.getName());
        }

        return file;
    }


    private Map<String, Map<String, List<ArchiveOutputError>>> processErrorsForTree(Map<String, List<ArchiveOutputError>> errors) {

        Map<String, Map<String, List<ArchiveOutputError>>> errorList =
                new HashMap<String, Map<String, List<ArchiveOutputError>>>();

        for (String key : errors.keySet()) {
            errorList.put(key, new HashMap<String, List<ArchiveOutputError>>());

            for (ArchiveOutputError error : errors.get(key)) {
                String fieldName = error.getFieldName();

                if (errorList.get(key).get(fieldName) == null) {
                    errorList.get(key).put(fieldName, new ArrayList<ArchiveOutputError>());
                }
                errorList.get(key).get(fieldName).add(error);
            }
        }

        return errorList;
    }

    /**
     * Creates the tree representing the errors found
     *
     * @param tree                         - Root tree to add to
     * @param processedErrors              - the errors to be added to the tree
     * @param isMissingFilesNotMissingData - are you outputting information about missing files or missing fields in this tree part.
     * @return @See DefaultMutableTreeNode
     */
    private DefaultMutableTreeNode createTree(DefaultMutableTreeNode tree, Map<String, Map<String, List<ArchiveOutputError>>> processedErrors, boolean isMissingFilesNotMissingData) {

        for (String fileOfInterest : processedErrors.keySet()) {

            if (processedErrors.get(fileOfInterest) != null) {

                String topLevelMessage = isMissingFilesNotMissingData ?
                        fileOfInterest + " is missing files"
                        :
                        fileOfInterest + " has " + processedErrors.get(fileOfInterest).size() +
                                " " + (processedErrors.get(fileOfInterest).size() > 1 ? "fields" : "field") + " missing data";

                DefaultMutableTreeNode subTree = new DefaultMutableTreeNode(topLevelMessage);
                // then use the second Map to split on each field!
                for (String field : processedErrors.get(fileOfInterest).keySet()) {
                    if (!field.equals("")) {
                        DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(
                                field + " is missing " + processedErrors.get(fileOfInterest).get(field).size() + " descriptions");
                        for (ArchiveOutputError el : processedErrors.get(fileOfInterest).get(field)) {
                            fieldNode.add(new DefaultMutableTreeNode(el));
                        }

                        subTree.add(fieldNode);
                    } else {
                        for (ArchiveOutputError outputError : processedErrors.get(fileOfInterest).get(field)) {
                            subTree.add(new DefaultMutableTreeNode(outputError));
                        }
                    }

                }

                tree.add(subTree);
            }
        }

        return tree;
    }

    private DefaultMutableTreeNode getErrors() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Errors found in isatab...");

        // create missing files branch
        if (missingFiles.size() > 0) {
            DefaultMutableTreeNode missingFilesNode = new DefaultMutableTreeNode(missingFiles.size() + " assays with missing files");
            Map<String, Map<String, List<ArchiveOutputError>>> processedFiles = processErrorsForTree(missingFiles);

            createTree(missingFilesNode, processedFiles, true);

            root.add(missingFilesNode);
        }

        // create data missing branch
        if (missingData.size() > 0) {

            Map<String, Map<String, List<ArchiveOutputError>>> processedErrors = processErrorsForTree(missingData);
            DefaultMutableTreeNode missingDataNode = new DefaultMutableTreeNode(processedErrors.size() + " files with fields missing required data");
            createTree(missingDataNode, processedErrors, false);

            root.add(missingDataNode);
        }
        // return final tree
        return root;
    }

    public String getOutputLocation() {
        return outputLocation;
    }

    public void setOutputLocation(String outputLocation) {
        this.outputLocation = outputLocation;
    }

    public JLabel getArchiveOutputStatus() {
        return archiveOutputStatus;
    }

    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public int getCompressionLevel() {
        return compressionLevel;
    }

    public ArchivingStatistics getStatistics() {
        return statistics;
    }

    public ViewErrorPane getBrowseViewErrorPane() {
        return browseViewErrorPane;
    }
}

