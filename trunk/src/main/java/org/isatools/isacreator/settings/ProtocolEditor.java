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

package org.isatools.isacreator.settings;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Protocol;

import javax.swing.*;
import java.awt.*;

/**
 * ProtocolEditor
 *
 * @author Eamonn Maguire
 * @date Sep 28, 2009
 */


public class ProtocolEditor extends ElementEditor {

    private Protocol currentProtocol;

    private JTextField name;
    private JTextField type;
    private JTextArea description;
    private JTextField uri;
    private JTextField version;
    private JTextField parameters;
    private JTextField components;
    private JTextField componentType;

    public ProtocolEditor() {
        super(ElementEditor.PROTOCOLS);
    }

    protected void createGUI() {
        name = new JTextField();
        name.setEnabled(false);
        add(UIHelper.createFieldComponent("name", name));
        type = new JTextField();
        type.setEnabled(false);
        add(UIHelper.createFieldComponent("Type", type));

        description = new JTextArea();
        description.setEnabled(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        UIHelper.renderComponent(description, UIHelper.VER_11_PLAIN, UIHelper.GREY_COLOR, false);

        JScrollPane descriptionScroller = new JScrollPane(description,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        descriptionScroller.setPreferredSize(new Dimension(100, 50));

        IAppWidgetFactory.makeIAppScrollPane(descriptionScroller);

        add(UIHelper.createFieldComponent("Description", descriptionScroller));
        uri = new JTextField();
        uri.setEnabled(false);
        add(UIHelper.createFieldComponent("URI", uri));
        version = new JTextField();
        version.setEnabled(false);
        add(UIHelper.createFieldComponent("Version", version));
        parameters = new JTextField();
        parameters.setEnabled(false);
        add(UIHelper.createFieldComponent("Parameters", parameters));
        components = new JTextField();
        components.setEnabled(false);
        add(UIHelper.createFieldComponent("Component", components));
        componentType = new JTextField();
        componentType.setEnabled(false);
        add(UIHelper.createFieldComponent("Component Type", componentType));
    }

    public void setCurrentProtocol(Protocol p) {
        this.currentProtocol = p;

        name.setText(p.getProtocolName());
        type.setText(p.getProtocolType());
        description.setText(p.getProtocolDescription());
        uri.setText(p.getProtocolURL());
        version.setText(p.getProtocolVersion());
        parameters.setText(p.getProtocolParameterName());
        components.setText(p.getProtocolComponentName());
        componentType.setText(p.getProtocolComponentType());
    }

    public Protocol getGeneratedProtocol() {
        return currentProtocol;
    }
}
