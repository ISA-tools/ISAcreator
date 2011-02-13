/**
 ISAcreator is a component of the ISA software suite (http://www.isa-tools.org)

 License:
 ISAcreator is licensed under the Common Public Attribution License version 1.0 (CPAL)

 EXHIBIT A. CPAL version 1.0
 ÒThe contents of this file are subject to the CPAL version 1.0 (the ÒLicenseÓ);
 you may not use this file except in compliance with the License. You may obtain a
 copy of the License at http://isa-tools.org/licenses/ISAcreator-license.html.
 The License is based on the Mozilla Public License version 1.1 but Sections
 14 and 15 have been added to cover use of software over a computer network and
 provide for limited attribution for the Original Developer. In addition, Exhibit
 A has been modified to be consistent with Exhibit B.

 Software distributed under the License is distributed on an ÒAS ISÓ basis,
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

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StreamTokenizer;

public class DirectoryFileList extends JList {

    static Color listForeground, listBackground,
            listSelectionForeground, listSelectionBackground;

    static {
        listForeground = UIHelper.DARK_GREEN_COLOR;
        listBackground = UIHelper.BG_COLOR;
        listSelectionForeground = UIHelper.BG_COLOR;
        listSelectionBackground = UIHelper.DARK_GREEN_COLOR;
    }

    @InjectedResource
    private ImageIcon fileIcon, textFileIcon, directoryIcon,
            imageFileIcon;

    private JComponent fileCellLayout, textCellPrototype,
            imageCellPrototype, directoryCellPrototype;
    private JLabel fileNameLabel, textNameLabel,
            directoryNameLabel, imageNameLabel,
            fileSizeLabel,
            textSizeLabel, textWordCountLabel,
            directoryCountLabel,
            imageSizeLabel, imageIconLabel;

    public DirectoryFileList() {
        super();

        ResourceInjector.get("filechooser-package.style").inject(this);

        buildCellStructures();
        setCellRenderer(new CellRenderer());
        setModel(new DefaultListModel());
    }


    protected void addFileItem(FileChooserFile cf) {
        DefaultListModel model = (DefaultListModel) getModel();

        if (isTextFile(cf)) {
            model.addElement(new TextFileItem(cf));
        } else if (isImageFile(cf)) {
            model.addElement(new ImageFileItem(cf));
        } else {
            model.addElement(new CustomFile(cf.getFilePath()));
        }

    }

    protected void addDirectoryItem(FileChooserFile cf) {
        DefaultListModel model = (DefaultListModel) getModel();
        if (cf instanceof CustomFTPFile) {
            model.addElement(new FTPDirectoryItem(cf));
        } else if (cf instanceof CustomFile) {
            model.addElement(new DirectoryItem(cf));
        }


    }

    protected boolean isImageFile(FileChooserFile f) {
        if (f.isDirectory()) {
            return false;
        }
        String name = f.getName().toUpperCase();
        return name.endsWith(".GIF") || name.endsWith(".JPG") ||
                name.endsWith(".JPEG") || name.endsWith(".BMP") ||
                name.endsWith(".PNG");
    }

    protected boolean isTextFile(FileChooserFile f) {
        if (f.isDirectory()) {
            return false;
        }
        String name = f.getName().toUpperCase();
        return name.endsWith(".TXT") || name.endsWith(".HTML") ||
                name.endsWith(".XML") || name.endsWith(".XHTML");
    }


    protected void buildCellStructures() {

        fileCellLayout = new JPanel();
        fileCellLayout.setLayout(new GridBagLayout());
        addGridBag(new JLabel(fileIcon), fileCellLayout,
                0, 0, 1, 2,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, 0, 0);
        fileNameLabel = new JLabel();

        Font nameFont = UIHelper.VER_10_BOLD;
        fileNameLabel.setFont(nameFont);
        addGridBag(fileNameLabel, fileCellLayout,
                1, 0, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 1, 0);
        fileSizeLabel = new JLabel();
        addGridBag(fileSizeLabel, fileCellLayout,
                1, 1, 1, 1,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, 1, 0);
        makeOpaque(fileCellLayout);
        // text file
        textCellPrototype = new JPanel();
        textCellPrototype.setLayout(new GridBagLayout());
        addGridBag(new JLabel(textFileIcon), textCellPrototype,
                0, 0, 1, 2,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, 0, 0);
        textNameLabel = new JLabel();
        textNameLabel.setFont(nameFont);
        addGridBag(textNameLabel, textCellPrototype,
                1, 0, 2, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 1, 0);
        textSizeLabel = new JLabel();
        textWordCountLabel = new JLabel();
        addGridBag(textSizeLabel, textCellPrototype,
                1, 1, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 0, 0);
        addGridBag(textWordCountLabel, textCellPrototype,
                2, 1, 1, 1,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, 1, 0);

        makeOpaque(textCellPrototype);
        // directory
        directoryCellPrototype = new JPanel();
        directoryCellPrototype.setLayout(new GridBagLayout());
        addGridBag(new JLabel(directoryIcon), directoryCellPrototype,
                0, 0, 1, 2,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, 0, 0);
        directoryNameLabel = new JLabel();
        directoryNameLabel.setFont(nameFont);
        addGridBag(directoryNameLabel, directoryCellPrototype,
                1, 0, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 1, 0);
        directoryCountLabel = new JLabel();
        addGridBag(directoryCountLabel, directoryCellPrototype,
                1, 1, 1, 1,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, 1, 0);
        makeOpaque(directoryCellPrototype);
        // image
        imageCellPrototype = new JPanel();
        imageCellPrototype.setLayout(new GridBagLayout());
        addGridBag(new JLabel(imageFileIcon), imageCellPrototype,
                0, 0, 1, 2,
                GridBagConstraints.WEST, GridBagConstraints.BOTH, 0, 0);
        imageNameLabel = new JLabel();
        imageNameLabel.setFont(nameFont);
        addGridBag(imageNameLabel, imageCellPrototype,
                1, 0, 1, 1,
                GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, 1, 0);
        imageSizeLabel = new JLabel();
        addGridBag(imageSizeLabel, imageCellPrototype,
                1, 1, 1, 1,
                GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, 1, 0);
        imageIconLabel = new JLabel();
        addGridBag(imageIconLabel, imageCellPrototype,
                2, 0, 1, 2,
                GridBagConstraints.EAST, GridBagConstraints.VERTICAL, 0, 0);
        makeOpaque(imageCellPrototype);
    }

    private void addGridBag(Component comp, Container cont,
                            int x, int y,
                            int width, int height,
                            int anchor, int fill,
                            int weightx, int weighty) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.anchor = anchor;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        cont.add(comp, gbc);
    }

    private void makeOpaque(Container container) {
        Component[] comps = container.getComponents();
        for (Component comp : comps) {
            if (comp instanceof JComponent) {
                ((JComponent) comp).setOpaque(true);
            }
        }
    }

    class ImageFileItem extends CustomFile {
        ImageIcon icon;
        private FileChooserFile file;

        public ImageFileItem(FileChooserFile file) {
            super(file.getFilePath());
            this.file = file;
            initIcon();
        }

        public FileChooserFile getFile() {
            return file;
        }

        void initIcon() {
            try {
                icon = new ImageIcon(getPath());
                Image img = icon.getImage();
                float factor;
                // we scale by the largest side to 32px.
                if (img.getWidth(null) > img.getHeight(null)) {
                    factor = Math.min(32f / img.getWidth(null), 1.0f);
                } else {
                    factor = Math.min(32f / img.getHeight(null), 1.0f);
                }
                Image scaledImage =
                        img.getScaledInstance((int) (img.getWidth(null) * factor),
                                (int) (img.getHeight(null) * factor),
                                Image.SCALE_FAST);
                icon.setImage(scaledImage);
            } catch (Exception e) {
                icon = null;
            }
        }
    }


    class DirectoryItem extends CustomFile {
        int childCount;
        private FileChooserFile f;

        public DirectoryItem(FileChooserFile f) {
            super(f.getFilePath());
            this.f = f;
            countChildren();
        }

        public FileChooserFile getFile() {
            return f;
        }

        public int getChildCount() {
            return childCount;
        }

        void countChildren() {
            if (!isDirectory()) {
                childCount = -1;
            } else {
                childCount = listFiles().length;
            }
        }
    }

    class FTPDirectoryItem extends CustomFTPFile {
        int childCount;
        private FileChooserFile f;

        public FTPDirectoryItem(FileChooserFile f) {
            super((CustomFTPFile) f, f.getAbsoluteLink());
            this.f = f;
        }

        public FileChooserFile getFile() {
            return f;
        }

        public String getChildCount() {
            return "ftp directory";
        }


    }


    class TextFileItem extends CustomFile {
        int wordCount = -1;
        private FileChooserFile f;

        public TextFileItem(FileChooserFile f) {
            super(f.getFilePath());
            this.f = f;
            initWordCount();
        }

        public int getWordCount() {
            return wordCount;
        }

        public FileChooserFile getFile() {
            return f;
        }

        protected void initWordCount() {
            try {
                StreamTokenizer izer =
                        new StreamTokenizer(new BufferedReader(new FileReader(this)));
                while (izer.nextToken() != StreamTokenizer.TT_EOF) {
                    wordCount++;
                }
            } catch (Exception e) {
                wordCount = -1;
            }
        }
    }

    class CellRenderer
            implements ListCellRenderer {

        public Component getListCellRendererComponent(JList list,
                                                      Object value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (value instanceof DirectoryItem) {
                DirectoryItem item = (DirectoryItem) value;
                directoryNameLabel.setText(item.getName());
                directoryCountLabel.setText(item.getChildCount() + " items");
                setSelectionStateColour(directoryCellPrototype, isSelected);
                return directoryCellPrototype;
            } else if (value instanceof FTPDirectoryItem) {
                FTPDirectoryItem item = (FTPDirectoryItem) value;
                directoryNameLabel.setText(item.getName());
                directoryCountLabel.setText("remote directory");
                setSelectionStateColour(directoryCellPrototype, isSelected);
                return directoryCellPrototype;
            } else if (value instanceof TextFileItem) {
                TextFileItem item = (TextFileItem) value;
                // populate values
                textNameLabel.setText(item.getName());
                String sizeText = item.length() > 0 ? item.length() / 1024 + " kb " : "remote text file";
                textSizeLabel.setText(sizeText);
                String wordCount = item.getWordCount() > 1 ? item.getWordCount() + " words" : "";
                textWordCountLabel.setText(wordCount);
                setSelectionStateColour(textCellPrototype, isSelected);
                return textCellPrototype;
            } else if (value instanceof ImageFileItem) {
                ImageFileItem item = (ImageFileItem) value;
                // pouplate values
                imageNameLabel.setText(item.getName());
                String sizeText;
                if (item.getFile() instanceof CustomFTPFile) {
                    sizeText = item.getFile().getLength() / 1024 + " kb";
                } else {
                    sizeText = item.length() / 1024 + " kb";
                }
                imageSizeLabel.setText(sizeText);
                imageIconLabel.setIcon(item.icon);
                setSelectionStateColour(imageCellPrototype, isSelected);
                return imageCellPrototype;
            } else {
                FileChooserFile item = (FileChooserFile) value;
                // pouplate values
                fileNameLabel.setText(item.getName());
                String sizeText;
                if (item instanceof CustomFTPFile) {
                    sizeText = "remote file";
                } else {
                    sizeText = item.getLength() / 1024 + " kb";
                }
                fileSizeLabel.setText(sizeText);
                setSelectionStateColour(fileCellLayout, isSelected);
                return fileCellLayout;
            }
        }

        private void setSelectionStateColour(Container prototype,
                                             boolean isSelected) {
            Component[] comps = prototype.getComponents();
            for (Component comp : comps) {
                if (isSelected) {
                    comp.setForeground(listSelectionForeground);
                    comp.setBackground(listSelectionBackground);
                } else {
                    comp.setForeground(listForeground);
                    comp.setBackground(listBackground);
                }
            }
        }
    }
}
