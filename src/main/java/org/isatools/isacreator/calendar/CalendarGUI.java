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

package org.isatools.isacreator.calendar;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * CalendarGUI provides the interface and Logic for the Calendar "widget"
 *
 * @author Eamonn Maguire
 */
public class CalendarGUI extends JFrame implements ActionListener {
    static final int WIDTH = 200;
    static final int HEIGHT = 230;

    @InjectedResource
    private ImageIcon closeIcon, closeIconOver, okIcon, okIconOver;

    private static Calendar calendar;
    private JComboBox months;
    private JComboBox years;

    private JTextField selectedDay;
    private JTextField today;
    private JTextField[][] dayArray;
    private int curMonth;
    private int curYear;
    private int selectedMonth;
    private int selectedYear;


    public CalendarGUI() {
        ResourceInjector.get("calendar-package.style").inject(this);
    }

    /**
     * CreateGUI method is called by any class wishing to properly instantiate the calendar. Until
     * this method is called, no painting is done.
     */
    public void createGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                instantiateFrame();
            }
        });
    }

    /**
     * Method called by instantiateFrame to create the overall Calendar
     */
    private void instantiateCalendar() {
        // create selection tools to select the month and year...
        String[] yearsList = new String[100];

        for (int i = -92; i < 8; i++) {
            // add 130 to years list so we start at 0!
            yearsList[i + 92] = (String.valueOf(curYear + i));
        }

        years = new JComboBox(yearsList);
        setupCombo(years);
        years.setSelectedItem(String.valueOf(curYear));
        years.addActionListener(this);

        String[] monthsList = {
                "January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"
        };

        months = new JComboBox(monthsList);
        setupCombo(months);
        months.setSelectedIndex(curMonth);
        months.addActionListener(this);

        JPanel monYrSelCont = new JPanel();
        monYrSelCont.setBackground(UIHelper.BG_COLOR);
        monYrSelCont.setLayout(new BoxLayout(monYrSelCont, BoxLayout.LINE_AXIS));

        // add month and year selection combos and labels to monYrSelCont container.
        monYrSelCont.add(months);
        monYrSelCont.add(years);


        add(monYrSelCont, BorderLayout.NORTH);

        JPanel buttonsCont = new JPanel(new BorderLayout());
        buttonsCont.setBackground(UIHelper.BG_COLOR);

        final JLabel confirm = new JLabel(okIcon, JLabel.RIGHT);
        confirm.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("selectedDate", "OLD_VALUE", getSelectedDay());
                setVisible(false);
            }

            public void mouseEntered(MouseEvent event) {
                confirm.setIcon(okIconOver);
            }

            public void mouseExited(MouseEvent event) {
                confirm.setIcon(okIcon);
            }
        });

        final JLabel discard = new JLabel(closeIcon, JLabel.LEFT);
        discard.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                firePropertyChange("noneSelected", "", getSelectedDay());
                setVisible(false);
            }

            public void mouseEntered(MouseEvent event) {
                discard.setIcon(closeIconOver);
            }

            public void mouseExited(MouseEvent event) {
                discard.setIcon(closeIcon);
            }
        });


        buttonsCont.add(discard, BorderLayout.WEST);
        buttonsCont.add(confirm, BorderLayout.EAST);

        add(buttonsCont, BorderLayout.SOUTH);
    }

    /**
     * Creates the entire calendar
     *
     * @param xPos       - the x position to start drawing at.
     * @param yPos       - the y position to start drawing at.
     * @param compWidth  - width which each component (i.e. textfield) should be.
     * @param compHeight - height which each component should be.
     */
    private void createDayGrid(int xPos, int yPos, int compWidth, int compHeight) {
        JPanel gridContainer = new JPanel(new BorderLayout());
        gridContainer.setBackground(UIHelper.BG_COLOR);
        gridContainer.add(createDayHeaders(xPos, yPos, compWidth),
                BorderLayout.NORTH);

        JPanel dayGrid = new JPanel(new GridLayout(6, 7));
        dayGrid.setPreferredSize(new Dimension(200, 200));
        dayGrid.setBackground(UIHelper.BG_COLOR);

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                JTextField day = new JTextField();
                day.setEditable(false);
                day.setEnabled(false);
                day.setDisabledTextColor(UIHelper.BG_COLOR);
                UIHelper.renderComponent(day, UIHelper.VER_10_BOLD, UIHelper.BG_COLOR, UIHelper.DARK_GREEN_COLOR);
                day.setBorder(null);
                dayArray[j][i] = day;

                day.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        JTextField active = (JTextField) e.getSource();
                        setSelectedDay(active);
                    }
                });
                dayGrid.add(day);
                day.setBounds(xPos, yPos, compWidth, compHeight);

                xPos += compWidth;
            }

            yPos += compHeight;
        }

        setMonth();

        gridContainer.add(dayGrid, BorderLayout.CENTER);

        add(gridContainer, BorderLayout.CENTER);
    }

    /**
     * Creates a JPanel with the day strings painted and separated by the compWidth.
     *
     * @param xPos      - X position to start drawing the Strings at in the panel
     * @param yPos      - Y position to start drawing the String at in the panel
     * @param compWidth - The separation space to be provided between each day string.
     * @return JPanel containing the headers painted onto it.
     */
    private JPanel createDayHeaders(final int xPos, final int yPos,
                                    final int compWidth) {
        JPanel header = new JPanel() {
            public void paintComponent(Graphics g) {
                int xPosTemp = xPos;
                g.setFont(UIHelper.VER_10_BOLD);
                g.setColor(UIHelper.DARK_GREEN_COLOR);

                String[] days = {
                        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
                };

                for (String day : days) {
                    g.drawString(day, xPosTemp, yPos);
                    xPosTemp += compWidth;
                }
            }
        };

        header.setPreferredSize(new Dimension(200, 30));

        return header;
    }


    /**
     * Return the date selected by the user
     *
     * @return a String representation of the date in the format dd-mm-yyyy
     */
    public String getSelectedDay() {

        int dayAsInt = calendar.get(Calendar.DAY_OF_MONTH);

        String month = String.valueOf(months.getSelectedIndex() + 1);

        if (selectedDay != null) {
            dayAsInt = Integer.valueOf(selectedDay.getText());
        }

        String day = String.valueOf(dayAsInt);
        if (dayAsInt < 10) {
            day = "0" + day;
        }

        if (month.length() < 2) {
            month = "0" + month;
        }

        return years.getSelectedItem() + "-" + month + "-" + day;
    }


    /**
     * Method called by createGUI to create the overall Interface.
     */
    private void instantiateFrame() {
        setAlwaysOnTop(true);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 2));

        calendar = new GregorianCalendar();
        curYear = calendar.get(Calendar.YEAR);
        selectedYear = curYear;
        curMonth = calendar.get(Calendar.MONTH);
        selectedMonth = curMonth;

        dayArray = new JTextField[7][6];

        int xPos = 5;
        int yPos = 20;
        int compWidth = 200 / 7;
        int compHeight = 150 / 6;

        instantiateCalendar();
        createDayGrid(xPos, yPos, compWidth, compHeight);

        pack();
    }

    /**
     * Reforms the Calendar view when the year or month is changed by the user in the GUI.
     * <p/>
     * Method essentially involves reforming the array of JTextFields to display a new numeric field which
     * corresponds to the correct day in a month.
     */
    private void setMonth() {
        Calendar cal = new GregorianCalendar(selectedYear, selectedMonth, 1);
        int firstDay = cal.get(Calendar.DAY_OF_WEEK);
        int lastDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

        int day = 1;
        int check = 1;

        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 7; x++) {
                // ensures that the days occur at the right time points on the calendar. i.e. day 1 of the month is
                // on a monday instead of sunday, and that the complete host
                if ((check >= firstDay) && (day <= lastDate)) {
                    dayArray[x][y].setText(Integer.toString(day));

                    // set colours of squares, and invert selected value
                    if ((day == calendar.get(Calendar.DAY_OF_MONTH)) &&
                            (selectedMonth == curMonth) &&
                            (selectedYear == curYear)) {
                        today = dayArray[x][y];
                        dayArray[x][y].setBackground(Color.GRAY);
                        dayArray[x][y].setForeground(UIHelper.BG_COLOR);
                    } else {
                        if (!dayArray[x][y].getText().equals("")) {
                            dayArray[x][y].setBackground(UIHelper.DARK_GREEN_COLOR);
                            dayArray[x][y].setForeground(UIHelper.BG_COLOR);
                        } else {
                            dayArray[x][y].setBackground(UIHelper.BG_COLOR);
                        }
                    }

                    day++;
                } else {
                    dayArray[x][y].setText("");
                    dayArray[x][y].setBackground(UIHelper.BG_COLOR);
                }

                check++;
            }
        }
    }

    /**
     * Gets the currently selected day and repaints its background to
     * reflect the fact that the user has made a selection
     *
     * @param active - The day object selected.
     */
    private void setSelectedDay(JTextField active) {
        if (selectedDay != null) {
            if ((selectedDay == today) && (selectedMonth == curMonth) && (curYear == selectedYear)) {
                selectedDay.setBackground(Color.GRAY);

            } else {
                selectedDay.setBackground(UIHelper.DARK_GREEN_COLOR);
                selectedDay.setForeground(UIHelper.LIGHT_GREEN_COLOR);

            }
            selectedDay.repaint();
        }

        selectedDay = active;

        String s = active.getText();

        if (s.equalsIgnoreCase("")) {
            selectedDay = null;
        } else {
            selectedDay.setBackground(UIHelper.LIGHT_GREEN_COLOR);
            selectedDay.setForeground(UIHelper.DARK_GREEN_COLOR);
        }


    }

    /**
     * Sets a JComboBox to have a standard font, foreground and background colour.
     *
     * @param jc - JComboBox to set up
     */
    private void setupCombo(JComboBox jc) {
        jc.setFont(UIHelper.VER_10_BOLD);
        jc.setForeground(UIHelper.DARK_GREEN_COLOR);
        jc.setBackground(UIHelper.BG_COLOR);
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == years) {
            selectedYear = Integer.valueOf(years.getSelectedItem().toString());
            setMonth();
        }

        if (event.getSource() == months) {
            selectedMonth = months.getSelectedIndex();
            setMonth();
        }
    }

}
