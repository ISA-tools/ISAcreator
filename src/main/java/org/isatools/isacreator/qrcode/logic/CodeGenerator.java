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

package org.isatools.isacreator.qrcode.logic;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.utils.Imaging;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/**
 * CodeGenerator
 *
 * @author eamonnmaguire
 * @date Oct 13, 2010
 */


public class CodeGenerator {

    public static Map<String, QRCode> createQRCodeImage(Map<String, String> sampleNameToContents, Dimension imageSize) {
        Map<String, QRCode> sampleNameToQRCode = new ListOrderedMap<String, QRCode>();

        for (String sampleName : sampleNameToContents.keySet()) {
            Image qrCode = CodeGenerator.createQRCodeImage(sampleNameToContents.get(sampleName), imageSize, UIHelper.GREY_COLOR, UIHelper.BG_COLOR);
            sampleNameToQRCode.put(sampleName, new QRCode(qrCode, sampleName, sampleNameToContents.get(sampleName), 1));
        }

        return sampleNameToQRCode;
    }

    public static boolean generateFilesFromQRCodes(Map<String, QRCode> sampleNameToQRCodes, String directoryToWriteTo) {
        try {
            for (String sampleName : sampleNameToQRCodes.keySet()) {
                QRCode code = sampleNameToQRCodes.get(sampleName);
                createQRCode(code.getContents(), new Dimension(100, 100), new File(directoryToWriteTo + File.separator + code.getUniqueId() + ".png"), FileFormat.PNG);
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void createQRCode(String toEncode, Dimension imageSize, File fileName, FileFormat format) {

        QRCodeWriter qrWrite = new QRCodeWriter();

        try {
            BitMatrix matrix = qrWrite.encode(toEncode, BarcodeFormat.QR_CODE, imageSize.width, imageSize.height);
            MatrixToImageWriter.writeToFile(matrix, format.getFormat(), fileName);
        } catch (WriterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static Image createQRCodeImage(String toEncode, Dimension imageSize) {

        QRCodeWriter qrWrite = new QRCodeWriter();

        try {
            BitMatrix matrix = qrWrite.encode(toEncode, BarcodeFormat.QR_CODE, imageSize.width, imageSize.height);
            return Imaging.createImageFromBufferedImage(MatrixToImageWriter.toBufferedImage(matrix));
        } catch (WriterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static Image createQRCodeImage(String toEncode, Dimension imageSize, Color foregroundColor, Color backgroundColor) {

        QRCodeWriter qrWrite = new QRCodeWriter();

        try {
            BitMatrix matrix = qrWrite.encode(toEncode, BarcodeFormat.QR_CODE, imageSize.width, imageSize.height);

            MatrixToImageWriter.setForeground(foregroundColor.getRGB());
            MatrixToImageWriter.setBackground(backgroundColor.getRGB());

            return Imaging.createImageFromBufferedImage(MatrixToImageWriter.toBufferedImage(matrix));
        } catch (WriterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }

    public static void main(String[] args) {
        CodeGenerator.createQRCode("http://esad.classics.ox.ac.uk", new Dimension(400, 400), new File("Data/esad.png"), FileFormat.PNG);
    }
}
