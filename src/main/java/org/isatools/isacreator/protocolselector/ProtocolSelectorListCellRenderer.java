package org.isatools.isacreator.protocolselector;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.model.Protocol;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 08/08/2011
 *         Time: 12:24
 */
public class ProtocolSelectorListCellRenderer extends JComponent implements ListCellRenderer {

    @InjectedResource
    private ImageIcon selectedIcon;

    private Color selectedBackgroundColor = new Color(141, 198, 63, 10);

    JLabel selectedIconContainer;

    /**
     * CustomListCellRenderer Constructor
     */
    public ProtocolSelectorListCellRenderer() {

        ResourceInjector.get("sample-selection-package.style").inject(this);

        setLayout(new BorderLayout());

        selectedIconContainer = new JLabel(selectedIcon);
        add(selectedIconContainer, BorderLayout.EAST);

        setPreferredSize(new Dimension(205, 30));

        add(new ProtocolInformationPane(), BorderLayout.CENTER);
        setBorder(null);
    }

    /**
     * Sets all list values to have a white background and green foreground.
     *
     * @param jList        - List to render
     * @param val          - value of list item being rendered.
     * @param index        - list index for value to be renderered.
     * @param selected     - is the value selected?
     * @param cellGotFocus - has the cell got focus?
     * @return - The CustomListCellRendered Component.
     */
    public Component getListCellRendererComponent(JList jList, Object val,
                                                  int index, boolean selected, boolean cellGotFocus) {

        //image.checkIsIdEntered(selected);
        Component[] components = getComponents();

        for (Component c : components) {

            c.setBackground(selected ? selectedBackgroundColor : Color.WHITE);

            if (c instanceof ProtocolInformationPane) {
                ProtocolInformationPane protocolInformationPane = (ProtocolInformationPane) c;

                if (val instanceof Protocol) {

                    Protocol protocol = (Protocol) val;
                    protocolInformationPane.setProtocolName(protocol.getProtocolName());
                    protocolInformationPane.setProtocolType(protocol.getProtocolType());

                } else {
                    protocolInformationPane.setProtocolName(val.toString());
                }

                protocolInformationPane.setSelected(selected);

            }

            if (selected) {
                c.setFont(UIHelper.VER_11_BOLD);
            } else {
                c.setFont(UIHelper.VER_11_PLAIN);
            }
        }

        return this;
    }

    class ProtocolInformationPane extends JPanel {

        private Color lessTransparentSampleName = new Color(141, 198, 63, 70);
        private Color lessTransparentAdditionInfo = new Color(0, 104, 56, 70);

        private JLabel protocolName;
        private JLabel protocolType;

        ProtocolInformationPane() {
            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            setOpaque(false);

            protocolName = UIHelper.createLabel("", UIHelper.VER_12_BOLD, UIHelper.LIGHT_GREEN_COLOR);
            protocolType = UIHelper.createLabel("", UIHelper.VER_8_PLAIN, UIHelper.DARK_GREEN_COLOR);
            protocolType.setSize(new Dimension(160, 10));

            add(protocolName);
            add(protocolType);
        }

        public void setProtocolName(String sampleNameText) {
            protocolName.setText(sampleNameText);
        }

        public void setProtocolType(String additionalInfoText) {
            protocolType.setText(additionalInfoText);
        }

        public void setSelected(boolean selected) {
            protocolName.setForeground(selected ? UIHelper.LIGHT_GREEN_COLOR : lessTransparentSampleName);
            protocolType.setForeground(selected ? UIHelper.DARK_GREEN_COLOR : lessTransparentAdditionInfo);

            selectedIconContainer.setIcon(selected ? selectedIcon : null);
        }
    }


}
