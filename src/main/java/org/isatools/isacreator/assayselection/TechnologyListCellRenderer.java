package org.isatools.isacreator.assayselection;

import org.isatools.isacreator.common.UIHelper;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;


public class TechnologyListCellRenderer implements ListCellRenderer {

    @InjectedResource
    private ImageIcon microarray, microarrayUnselected, ms, msUnselected, nmr, nmrUnselected, uhts, uhtsUnselected,
            generic, genericUnselected, gelElec, gelElecUnselected, flowCyt, flowCytUnselected;

    public static final Color SELECTED_COLOR = UIHelper.LIGHT_GREEN_COLOR;
    public static final Color UNSELECTED_COLOR = UIHelper.BG_COLOR;

    private JPanel contents;
    private JLabel icon;
    private JLabel text;

    public TechnologyListCellRenderer() {
        ResourceInjector.get("assayselection-package.style").inject(this);

        contents = new JPanel(new BorderLayout());
        contents.setOpaque(true);

        icon = new JLabel();
        text = UIHelper.createLabel("", UIHelper.VER_12_PLAIN, UIHelper.DARK_GREEN_COLOR);

        contents.add(icon, BorderLayout.WEST);
        contents.add(text, BorderLayout.CENTER);
        contents.setBorder(new EmptyBorder(2, 2, 2, 2));
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {

        String technology = value.toString().toLowerCase();

        if (technology.contains(AssayType.MICROARRAY.getType())) {
            icon.setIcon(selected ? microarray : microarrayUnselected);
        } else if (technology.contains(AssayType.MASS_SPECTROMETRY.getType())) {
            icon.setIcon(selected ? ms : msUnselected);
        } else if (technology.contains(AssayType.NMR.getType())) {
            icon.setIcon(selected ? nmr : nmrUnselected);
        } else if (technology.contains(AssayType.FLOW_CYTOMETRY.getType())) {
            icon.setIcon(selected ? flowCyt : flowCytUnselected);
        } else if (technology.contains(AssayType.GEL_ELECTROPHORESIS.getType())) {
            icon.setIcon(selected ? gelElec : gelElecUnselected);
        } else if (technology.contains(AssayType.SEQUENCING.getType())) {
            icon.setIcon(selected ? uhts : uhtsUnselected);
        } else {
            icon.setIcon(selected ? generic : genericUnselected);
        }

        text.setText(value.toString());

        // change text colour depending on selection
        if (selected) {
            text.setForeground(UIHelper.BG_COLOR);
            contents.setBackground(UIHelper.LIGHT_GREEN_COLOR);
        } else {
            text.setForeground(UIHelper.DARK_GREEN_COLOR);
            contents.setBackground(UIHelper.BG_COLOR);
        }

        return contents;
    }
}
