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


package org.isatools.isacreator.common;

import org.isatools.isacreator.effects.borders.RoundedBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseListener;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Vector;


/**
 * @author Eamonn Maguire
 */
public class UIHelper {

    public static final Color BG_COLOR = Color.WHITE;
    public static final Color DARK_GREEN_COLOR = new Color(0, 104, 56);
    public static final Color GREY_COLOR = new Color(51, 51, 51);
    public static final Color RED_COLOR = new Color(191, 30, 45);
    public static final Color TRANSPARENT_RED_COLOR = new Color(191, 30, 45, 50);
    public static final Color LIGHT_GREY_COLOR = new Color(153, 153, 153);
    public static final Color LIGHT_GREEN_COLOR = new Color(140, 198, 63);
    public static final Color TRANSPARENT_LIGHT_GREEN_COLOR = new Color(140, 198, 63, 60);
    public static final Font VER_8_PLAIN = new Font("Verdana", Font.PLAIN, 8);
    public static final Font VER_8_BOLD = new Font("Verdana", Font.BOLD, 8);
    public static final Font VER_9_PLAIN = new Font("Verdana", Font.PLAIN, 9);
    public static final Font VER_9_BOLD = new Font("Verdana", Font.BOLD, 9);
    public static final Font VER_10_PLAIN = new Font("Verdana", Font.PLAIN, 10);
    public static final Font VER_10_BOLD = new Font("Verdana", Font.BOLD, 10);
    public static final Font VER_11_PLAIN = new Font("Verdana", Font.PLAIN, 11);
    public static final Font VER_11_BOLD = new Font("Verdana", Font.BOLD, 11);
    public static final Font VER_12_PLAIN = new Font("Verdana", Font.PLAIN, 12);
    public static final Font VER_12_BOLD = new Font("Verdana", Font.BOLD, 12);
    public static final Font VER_14_PLAIN = new Font("Verdana", Font.PLAIN, 14);
    public static final Font VER_14_BOLD = new Font("Verdana", Font.BOLD, 14);
    public static final Border STD_ETCHED_BORDER = new LineBorder(UIHelper.DARK_GREEN_COLOR, 1, true);

    public static final Border EMPTY_BORDER = new EmptyBorder(0, 0, 0, 0);
    public static final RoundedBorder GREEN_ROUNDED_BORDER = new RoundedBorder(UIHelper.LIGHT_GREEN_COLOR, 6);
    public static final RoundedBorder GREY_ROUNDED_BORDER = new RoundedBorder(UIHelper.GREY_COLOR, 6);
    public static final RoundedBorder DARK_GREEN_ROUNDED_BORDER = new RoundedBorder(UIHelper.DARK_GREEN_COLOR, 6);

    public static final ImageIcon OK_BUTTON = new ImageIcon(UIHelper.class.getResource("/images/common/ok.png"));
    public static final ImageIcon OK_BUTTON_OVER = new ImageIcon(UIHelper.class.getResource("/images/common/ok_over.png"));
    public static final ImageIcon CLOSE_BUTTON = new ImageIcon(UIHelper.class.getResource("/images/common/close.png"));
    public static final ImageIcon CLOSE_BUTTON_OVER = new ImageIcon(UIHelper.class.getResource("/images/common/close_over.png"));

    //

    public static JLabel createLabel(String text) {
        JLabel newLab = new JLabel(text);
        newLab.setBackground(BG_COLOR);
        newLab.setFont(VER_11_BOLD);
        newLab.setForeground(DARK_GREEN_COLOR);

        return newLab;
    }

    public static JLabel createLabel(String text, Font f) {
        JLabel newLab = new JLabel(text);
        newLab.setBackground(BG_COLOR);
        newLab.setFont(f);
        newLab.setForeground(DARK_GREEN_COLOR);
        return newLab;
    }

    public static JLabel createLabel(String text, Font f, Color c) {
        return createLabel(text, f, c, JLabel.LEFT);
    }

    public static JLabel createLabel(String text, Font f, Color c, int position) {
        JLabel newLab = new JLabel(text, position);

        newLab.setBackground(BG_COLOR);
        newLab.setFont(f);
        newLab.setForeground(c);
        return newLab;
    }

    public static void renderComponent(JComponent comp, Font f, Color foregroundColor, boolean opaque) {
        comp.setForeground(foregroundColor);
        comp.setFont(f);
        comp.setBackground(BG_COLOR);
        comp.setOpaque(opaque);
    }

    public static void renderComponent(JComponent comp, Font f, Color foregroundColor, Color backgroundColor) {
        comp.setForeground(foregroundColor);
        comp.setFont(f);
        comp.setBackground(backgroundColor);
    }

    public static void renderContainerComponents(JComponent comp, Font f, Color foregroundColor, Color backgroundColor) {
        for (Component c : getComponents(comp)) {
            c.setForeground(foregroundColor);
            c.setFont(f);
            c.setBackground(backgroundColor);
            ((JComponent) c).setOpaque(true);
        }
    }

    public static JOptionPane createOptionPane(String label, int messageType, Icon icon) {
        JOptionPane optionPane = new JOptionPane();

        JLabel lab = UIHelper.createLabel(label, UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);
        optionPane.setMessageType(messageType);
        optionPane.setMessage(lab);

        if (icon != null) {

            optionPane.setIcon(icon);
        }

        applyOptionPaneBackground(optionPane, UIHelper.BG_COLOR);

        return optionPane;
    }

    /**
     * Work around to set the optionpane color. Otherwise components only have their default values!
     *
     * @param optionPane - Optionpane to change the backgroudn color of
     * @param color      - Color to change the background to
     */
    public static void applyOptionPaneBackground(JOptionPane
                                                         optionPane, Color color) {
        optionPane.setBackground(color);
        for (Component component : getComponents(optionPane)) {
            if (component instanceof JPanel) {
                component.setBackground(color);
            }
        }
    }

    public static void applyBackgroundToSubComponents(Container
                                                              container, Color color) {
        container.setBackground(color);
        for (Component component : getComponents(container)) {
            if (component instanceof JPanel) {
                component.setBackground(color);
            }
        }
    }


    /**
     * Gets the components contained in a given container
     *
     * @param container - container to retrieve the components contained within
     * @return - Collection of Components.
     */
    public static Collection<Component> getComponents(Container
                                                              container) {
        Collection<Component> components = new Vector<Component>();
        Component[] comp = container.getComponents();
        for (int i = 0, n = comp.length; i < n; i++) {
            components.add(comp[i]);
            if (comp[i] instanceof Container) {
                components.addAll(getComponents((Container) comp
                        [i]));
            }
        }
        return components;
    }

    public static int hashString(String s) {
        int hashcode = s.hashCode();

        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                int digit = (int) c;
                hashcode += digit * (hashcode % 50);
            }
        }

        return hashcode;
    }

    public static Color createColorFromString(String s, boolean includeAlpha) {

        int hashcode = hashString(s);
        hashcode = (hashcode < 0) ? hashcode * -1 : hashcode;

        int r = (hashcode % 223) + 10;
        int g = (hashcode % 231) + 20;
        int b = (hashcode % 217) + 30;

        return includeAlpha ? new Color(r, g, b, 100) : new Color(r, g, b);
    }

    public static JPanel wrapComponentInPanel(Component c) {
        JPanel wrapperPanel = new JPanel(new GridLayout(1, 1));
        wrapperPanel.setOpaque(false);
        wrapperPanel.add(c);

        return wrapperPanel;
    }

    public static JPanel createFieldComponent(String fieldName, JComponent editingComp) {
        JPanel fieldPanel = new JPanel(new GridLayout(1, 2));
        fieldPanel.setOpaque(false);

        JLabel lab = createLabel(fieldName, UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR);
        lab.setVerticalAlignment(SwingConstants.TOP);

        fieldPanel.add(lab);

        renderComponent(editingComp, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);

        fieldPanel.add(editingComp);

        return fieldPanel;
    }

    public static void setJComboBoxAsHeavyweight(JComboBox combo) {
        try {
            Class cls = Class.forName("javax.swing.PopupFactory");
            Field field = cls.getDeclaredField("forceHeavyWeightPopupKey");
            if (field != null) {
                field.setAccessible(true);
                combo.putClientProperty(field.get(null), Boolean.TRUE);
            }
        } catch (Exception e1) {
            combo.setLightWeightPopupEnabled(false);
        }
    }

    public static JPanel createTextEditEnableJTextArea(JScrollPane areaScroller, JTextComponent component) {
        JPanel container = new JPanel();
        container.setLayout(new BorderLayout());

        container.add(areaScroller, BorderLayout.CENTER);

        TextEditUtility textEdit = new TextEditUtility(component);
        textEdit.setVerticalAlignment(SwingConstants.TOP);

        JPanel textEditPanel = new JPanel();
        setLayoutForEditingIcons(textEditPanel, textEdit);

        textEditPanel.setSize(new Dimension(23, 23));

        container.add(textEditPanel, BorderLayout.EAST);

        return container;
    }


    public static void setLayoutForEditingIcons(JPanel parentPanel, JLabel icon) {

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        parentPanel.setLayout(gbl);

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(icon, c);
        parentPanel.add(icon);
    }


    public static Container createStyledFilterField(JTextField textField, ImageIcon leftImage, ImageIcon rightImage) {
        Box horBox = Box.createHorizontalBox();

        horBox.add(new JLabel(leftImage));

        textField.setBorder(null);
        UIHelper.renderComponent(textField, UIHelper.VER_11_BOLD, UIHelper.DARK_GREEN_COLOR, false);

        horBox.add(textField);
        horBox.add(new ClearFieldUtility(textField));
        horBox.add(new JLabel(rightImage));

        return horBox;
    }

    public static Container padComponentInHorizontalBox(int leftPadding, Component componentToAdd) {
        Box container = Box.createHorizontalBox();
        container.add(Box.createHorizontalStrut(leftPadding));

        container.add(componentToAdd);

        return container;
    }

    public static Container padComponentVerticalBox(int topPadding, Component componentToAdd) {
        Box container = Box.createVerticalBox();
        container.add(Box.createVerticalStrut(topPadding));

        container.add(componentToAdd);

        return container;
    }

    public static void removeMouseListenersFromComponent(Component... components) {
        for (Component component : components) {
            for (MouseListener mouseListener : component.getMouseListeners()) {
                component.removeMouseListener(mouseListener);
            }
        }
    }


    public static void removeAllComponents(Container container) {
        for (Component component : container.getComponents()) {

            if (component instanceof Container) {
                for (Component innerComponent : ((Container) component).getComponents()) {
                    ((Container) innerComponent).removeAll();
                }
            }
        }
        container.removeAll();
    }
}
