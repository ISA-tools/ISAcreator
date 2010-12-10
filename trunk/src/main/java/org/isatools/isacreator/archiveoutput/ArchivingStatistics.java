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

import java.io.File;

/**
 * ArchivingStatistics
 *
 * @author eamonnmaguire
 * @date Jun 2, 2010
 */


public class ArchivingStatistics {

    private int numberOfFiles = 0;
    private double uncompressedSize = 0.0;
    private long startTime = 0;
    private long endTime = 0;
    private File archive = null;

    public ArchivingStatistics() {
    }

    public ArchivingStatistics(int numberOfFiles, double uncompressedSize, File archive) {
        this.numberOfFiles = numberOfFiles;
        this.uncompressedSize = uncompressedSize;
        this.archive = archive;
    }

    public double getCompressionAsPercent() {
        return getCompressedSize() / uncompressedSize * 100;
    }

    public double getCompressedSize() {
        return archive.length();
    }

    public int getNumberOfFiles() {
        return numberOfFiles;
    }

    public double getUncompressedSize() {
        return uncompressedSize;
    }

    public void setNumberOfFiles(int numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    public void setUncompressedSize(double uncompressedSize) {
        this.uncompressedSize = uncompressedSize;
    }

    public void addToUncompressedSize(double size) {
        uncompressedSize += size;
    }

    public void addToNumberOfFiles(int number) {
        numberOfFiles += number;
    }

    public void setArchive(File archive) {
        this.archive = archive;
    }

    public File getArchive() {
        return archive;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public double getTimeTaken() {
        return (endTime - startTime) / 1000;
    }

    public String getStatisticsAsHTML() {
        StringBuffer html = new StringBuffer("<html><body align=\"left\">");
        html.append("<p><strong>archive name</strong>: %s");
        html.append("</p>");
        html.append("<p><strong>number of files</strong>: %d");
        html.append("</p>");
        html.append("<p><strong>uncompressed size</strong>: %.2f <strong>kb</strong>");
        html.append("</p>");
        html.append("<p><strong>compressed size</strong>: %.2f <strong>kb</strong>");
        html.append("</p>");
        html.append("<p><strong>compression</strong>: %.2f %%");
        html.append("</p>");
        html.append("<p><strong>completion time</strong>: %.2f <strong>seconds</strong>");
        html.append("</p>");
        html.append("</html>");


        return String.format(html.toString(),
                getArchive().getName(),
                getNumberOfFiles(),
                getUncompressedSize() / 1024,
                getCompressedSize() / 1024,
                getCompressionAsPercent(),
                getTimeTaken());
    }
}
