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


package org.isatools.isacreator.common;

import org.isatools.isacreator.ontologyselectiontool.OntologySelector;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.*;

/**
 * DropDownComponent is used to provide an intuitive way for components such as the Calendar component
 * and OntologySelectionTool to be displayed.
 *
 * @author Eamonn Maguire
 * @date May 21, 2008
 */
public class DropDownComponent extends JComponent implements ActionListener,
        AncestorListener {

    public static final int CALENDAR = 0;
    public static final int ONTOLOGY = 1;
    public static final int TABLE_BROWSER = 2;

    @InjectedResource
    private ImageIcon calendarIcon, calendarIconOver, ontologyIcon, ontologyIconOver, tableBrowserIcon, tableBrowserIconOver;

    private JComponent visibleComponent;
    private JLabel icon;
    private Window hideableWindow;


    /**
     * Creates a DropDownComponent for a JWindow type component.
     *
     * @param visibleComponent  - visible component (e.g. a JTextField)
     * @param dropDownComponent - e.g. FileChooser, etc.
     * @param type              - type of dropdown component to be added, e.g. ONTOLOGY, CALENDAR or TABLE_BROWSER
     */
    public DropDownComponent(JComponent visibleComponent,
                             Window dropDownComponent, final int type) {
        this.visibleComponent = visibleComponent;
        this.visibleComponent.setPreferredSize(new Dimension(visibleComponent.getWidth(), visibleComponent.getHeight()));
        this.hideableWindow = dropDownComponent;

        ResourceInjector.get("common-package.style").inject(this);

        createIcon(dropDownComponent, type);

        addAncestorListener(this);

        setupLayout();
    }


    private void createIcon(final Window container, final int type) {
        icon = new JLabel(type == CALENDAR ? calendarIcon : type == ONTOLOGY ? ontologyIcon : tableBrowserIcon);

        icon.setToolTipText(type == CALENDAR ? "<html>select <b>date</b> from a calendar utility</html>" :
                type == ONTOLOGY ? "<html>select <b>ontology</b> from the ontology lookup utility</html>" :
                        "<html>select <b>column</b> from the incoming file browsing utility</html>");

        icon.addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent event) {
                icon.setIcon(type == CALENDAR ? calendarIcon : type == ONTOLOGY ? ontologyIcon : tableBrowserIcon);

                if (container instanceof OntologySelector) {
                    OntologySelector ost = (OntologySelector) container;
                    ost.updatehistory();
                }

                addListener(container);
                showPopup(container);
            }

            public void mouseEntered(MouseEvent event) {
                icon.setIcon(type == CALENDAR ? calendarIconOver : type == ONTOLOGY ? ontologyIconOver : tableBrowserIconOver);
            }

            public void mouseExited(MouseEvent event) {
                icon.setIcon(type == CALENDAR ? calendarIcon : type == ONTOLOGY ? ontologyIcon : tableBrowserIcon);
            }
        });
    }

    public void actionPerformed(ActionEvent evt) {
        // build pop-up window

        if (hideableWindow instanceof OntologySelector) {
            OntologySelector ost = (OntologySelector) hideableWindow;
            ost.updatehistory();
        }

        addListener(hideableWindow);
        showPopup(hideableWindow);
    }

    private void addListener(final Window container) {
        container.addWindowFocusListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent evt) {
                container.setVisible(false);
            }
        });
        container.pack();
    }

    public void ancestorAdded(AncestorEvent event) {

        hidePopup(hideableWindow);
    }

    public void ancestorMoved(AncestorEvent event) {
        if (event.getSource() != hideableWindow) {
            hidePopup(hideableWindow);
        }
    }

    public void ancestorRemoved(AncestorEvent event) {
        hidePopup(hideableWindow);
    }

    public void disableElements() {
        visibleComponent.setEnabled(false);
        icon.setEnabled(false);
    }

    public void enableElements() {
        visibleComponent.setEnabled(true);
        icon.setEnabled(true);
    }

    protected Frame getFrame(Component comp) {
        if (comp == null) {
            comp = this;
        }

        if (comp.getParent() instanceof Frame) {
            return (Frame) comp.getParent();
        }

        return getFrame(comp.getParent());
    }

    /**
     * Hide the Window from view
     *
     * @param container - Window to hide.
     */
    public void hidePopup(Window container) {
        if ((container != null) && container.isVisible()) {
            container.setVisible(false);
        }
    }

    protected void setupLayout() {
        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(visibleComponent, c);
        add(visibleComponent);

        c.weightx = 0;
        c.gridx++;
        gbl.setConstraints(icon, c);
        add(icon);
    }

    /**
     * Show the window.
     *
     * @param container - Window of JFrame to show
     */
    private void showPopup(Window container) {
        if (visibleComponent.isEnabled()) {
            Point pt = visibleComponent.getLocationOnScreen();
            pt.translate(0, visibleComponent.getHeight());
            container.setLocation(pt);
            container.toFront();

            if (container instanceof OntologySelector) {
                ((OntologySelector) container).makeVisible();
                ((OntologySelector) container).loadRecommendedOntologiesIfAllowed();
            } else {
                container.setVisible(true);
                container.requestFocusInWindow();
            }
        }
    }
}
