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

package org.isatools.isacreator.qrcode.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.qrcode.logic.QRCode;
import org.isatools.isacreator.utils.Imaging;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * QRCodeDetailedView
 *
 * @author eamonnmaguire
 * @date Oct 21, 2010
 */


public class QRCodeDetailedView extends JPanel {

    @InjectedResource
    private ImageIcon qrInfoSectionHeader, qrCodeSectionHeader;

    private JLabel qrCodeImage;
    private QRCode qrCode;
    private QRCodeInfoRenderer qrCodeDetailsUI;

    public QRCodeDetailedView() {
        ResourceInjector.get("qrcode-generator-package.style").inject(this);
        createGUI();
    }

    private void createGUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(1, 1, 1, 1));

        add(UIHelper.wrapComponentInPanel(createQRDetailPanel()), BorderLayout.CENTER);
        add(UIHelper.wrapComponentInPanel(createTopPanel()), BorderLayout.NORTH);
    }

    private Container createTopPanel() {
        // contains text about the QR code being shown plus the QR code.

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(UIHelper.BG_COLOR);

        container.add(Box.createVerticalStrut(16), BorderLayout.NORTH);

        JLabel sectionHeader = new JLabel(qrCodeSectionHeader);
        sectionHeader.setHorizontalAlignment(SwingConstants.LEFT);
        sectionHeader.setVerticalAlignment(SwingConstants.TOP);

        container.add(sectionHeader, BorderLayout.WEST);

        qrCodeImage = new JLabel();
        qrCodeImage.setPreferredSize(new Dimension(100, 100));
        qrCodeImage.setVerticalAlignment(SwingConstants.TOP);
        qrCodeImage.setHorizontalAlignment(SwingConstants.RIGHT);

        container.add(qrCodeImage, BorderLayout.EAST);

        return container;
    }

    private Container createQRDetailPanel() {
        qrCodeDetailsUI = new QRCodeInfoRenderer();
        return qrCodeDetailsUI;
    }


    public void setQRCode(QRCode code) {
        this.qrCode = code;

        qrCodeImage.setIcon(new ImageIcon(
                Imaging.createImageFromBufferedImage(
                        Imaging.createResizedCopy(
                                code.getQrCode(), 100, 100, true))));
        qrCodeDetailsUI.updateContent();

        revalidate();
        repaint();
    }

    class QRCodeInfoRenderer extends JPanel {

        private String css =
                "<style type=\"text/css\">" + "<!--" +
                        ".titleFont {" +
                        "   font-family: Verdana;" + "   font-size: 9px; font-weight: bold;" +
                        "   color: #8DC63F;" + "}" +
                        ".valueFont {" +
                        "   font-family: Verdana;" + "   font-size: 9px;" +
                        "   color: #414042;" + "}" +
                        "</style>";

        private JEditorPane content;

        QRCodeInfoRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setBackground(UIHelper.BG_COLOR);
            createPane();
        }

        private void createPane() {
            content = new JEditorPane();
            content.setContentType("text/html");
            content.setEditable(false);
            content.setBackground(UIHelper.BG_COLOR);
            content.setEditorKit(new HTMLEditorKit());
            content.setPreferredSize(new Dimension(250, 50));

            updateContent();


            JLabel infoSectionLabel = new JLabel(qrInfoSectionHeader);
            infoSectionLabel.setHorizontalAlignment(SwingConstants.LEFT);
            QRCodeInfoRenderer.this.add(Box.createVerticalStrut(15));
            QRCodeInfoRenderer.this.add(UIHelper.wrapComponentInPanel(infoSectionLabel));
            QRCodeInfoRenderer.this.add(Box.createVerticalStrut(15));
            QRCodeInfoRenderer.this.add(content);
        }

        public void updateContent() {
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("<html>").append(css).append("\n");

            String sampleName = "";
            String encodedContents = "";
            String uniqueId = "";
            if (qrCode != null) {
                sampleName = qrCode.getSampleName();
                encodedContents = qrCode.getContents();
                uniqueId = qrCode.getUniqueId();
            }


            infoBuilder.append("<div align=\"left\">").append("\n");
            infoBuilder.append("<span class=\"titleFont\">Sample Name: </span><span class=\"valueFont\">").append(sampleName).append("</span><br/>").append("\n");
            infoBuilder.append("<span class=\"titleFont\">QR Contents: </span><span class=\"valueFont\">").append(encodedContents).append("</span><br/>").append("\n");
            infoBuilder.append("<span class=\"titleFont\">Unique Id: </span><span class=\"valueFont\">").append(uniqueId).append("</span>").append("\n");
            infoBuilder.append("</div></html>");

            content.setText(infoBuilder.toString());
        }
    }

}
