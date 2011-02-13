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
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * QRCodeListItem
 *
 * @author eamonnmaguire
 * @date Oct 18, 2010
 */


public class QRCodeListItem extends JPanel {

    @InjectedResource
    private ImageIcon leftSide, copyNumber, addCopy, addCopyOver, removeCopy, removeCopyOver;

    private QRCode qrCode;

    public QRCodeListItem(QRCode qrCode) {

        this.qrCode = qrCode;

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(570, 67));

        ResourceInjector.get("qrcode-generator-package.style").inject(this);
        createGUI();
    }

    public void createGUI() {
        add(new QRCodeRenderer(), BorderLayout.WEST);
        add(new QRCodeInfoRenderer(), BorderLayout.CENTER);
        add(new CopyNumberModificationRenderer(), BorderLayout.EAST);
    }

    class QRCodeInfoRenderer extends JPanel {

        private String css =
                "<style type=\"text/css\">" + "<!--" +
                        ".titleFont {" +
                        "   font-family: Verdana;" + "   font-size: 8px; font-weight: bold;" +
                        "   color: #8DC63F;" + "}" +
                        ".valueFont {" +
                        "   font-family: Verdana;" + "   font-size: 8px;" +
                        "   color: #414042;" + "}" +
                        "</style>";

        QRCodeInfoRenderer() {
            setLayout(new BorderLayout());
            setBackground(UIHelper.BG_COLOR);
            createPane();
        }

        private void createPane() {
            StringBuilder infoBuilder = new StringBuilder();
            infoBuilder.append("<html>").append(css).append("\n");
            infoBuilder.append("<div align=\"left\">").append("\n");
            infoBuilder.append("<span class=\"titleFont\">").append(qrCode.getSampleName()).append("</span><br/>").append("\n");
            infoBuilder.append("<span class=\"titleFont\">QR Contents: </span><span class=\"valueFont\">").append(qrCode.getContents()).append("</span><br/>").append("\n");
            infoBuilder.append("<span class=\"titleFont\">Unique Id: </span><span class=\"valueFont\">").append(qrCode.getUniqueId()).append("</span>").append("\n");
            infoBuilder.append("</div></html>");

            JEditorPane content = new JEditorPane();
            content.setContentType("text/html");
            content.setEditable(false);
            content.setBackground(UIHelper.BG_COLOR);
            content.setEditorKit(new HTMLEditorKit());
            content.setPreferredSize(new Dimension(420, 50));
            content.setText(infoBuilder.toString());

            QRCodeInfoRenderer.this.add(content);
        }
    }

    class QRCodeRenderer extends JPanel {
        // this will contain the general panel layout for the list item and modifier elements to allow for changing of images
        // when rendering an item as being selected and so forth.
        private JLabel itemSelectedIndicator;

        QRCodeRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setBackground(UIHelper.BG_COLOR);
            createPane();
        }

        private void createPane() {
            itemSelectedIndicator = new JLabel(leftSide);

            ImageIcon qrCodeImage = new ImageIcon(qrCode.getQrCode());

            JLabel qrCodeLabel = new JLabel(qrCodeImage);
            qrCodeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            qrCodeLabel.setPreferredSize(new Dimension(60, 60));

            QRCodeRenderer.this.add(itemSelectedIndicator);
            QRCodeRenderer.this.add(qrCodeLabel);

            QRCodeRenderer.this.add(Box.createHorizontalStrut(2));
        }

    }

    class CopyNumberModificationRenderer extends JPanel {

        JLabel copyNumberValue;

        CopyNumberModificationRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            setBackground(UIHelper.BG_COLOR);

            createPane();
        }

        private void createPane() {
            JLabel copiesField = new JLabel(copyNumber);

            CopyNumberModificationRenderer.this.add(copiesField);

            Box copyNumberModificationContainer = Box.createVerticalBox();

            copyNumberValue = UIHelper.createLabel(String.valueOf(qrCode.getCopies()),
                    UIHelper.VER_14_BOLD, UIHelper.LIGHT_GREEN_COLOR, SwingConstants.LEFT);
            copyNumberValue.setPreferredSize(new Dimension(30, 20));

            copyNumberModificationContainer.add(copyNumberValue);

            Box buttonPanel = Box.createHorizontalBox();

            final JLabel removeButton = new JLabel(removeCopy);
            removeButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent mouseEvent) {
                    removeButton.setIcon(removeCopyOver);
                }

                public void mouseExited(MouseEvent mouseEvent) {
                    removeButton.setIcon(removeCopy);
                }

                public void mousePressed(MouseEvent mouseEvent) {
                    removeButton.setIcon(removeCopy);
                    if (qrCode.getCopies() > 0) {
                        qrCode.setCopies(qrCode.getCopies() - 1);
                        setCopyNumber(qrCode.getCopies());
                    }
                }
            });

            final JLabel addButton = new JLabel(addCopy);
            addButton.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent mouseEvent) {
                    addButton.setIcon(addCopyOver);
                }

                public void mouseExited(MouseEvent mouseEvent) {
                    addButton.setIcon(addCopy);
                }

                public void mousePressed(MouseEvent mouseEvent) {
                    addButton.setIcon(addCopy);

                    qrCode.setCopies(qrCode.getCopies() + 1);
                    setCopyNumber(qrCode.getCopies());

                }
            });

            buttonPanel.add(removeButton);
            buttonPanel.add(addButton);

            copyNumberModificationContainer.add(buttonPanel);

            CopyNumberModificationRenderer.this.add(copyNumberModificationContainer);
        }

        public void setCopyNumber(int copies) {
            copyNumberValue.setText(String.valueOf(copies));
        }
    }
}
