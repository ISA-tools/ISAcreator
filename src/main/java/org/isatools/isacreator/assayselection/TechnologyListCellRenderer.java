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

    private JPanel contents;
    private JLabel icon;
    private JLabel text;

    public TechnologyListCellRenderer() {
        ResourceInjector.get("assayselection-package.style").inject(this);

        contents = new JPanel(new BorderLayout());
        contents.setOpaque(true);

        icon = new JLabel();
        text = UIHelper.createLabel("", UIHelper.VER_11_PLAIN, UIHelper.DARK_GREEN_COLOR);

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

        text.setFont(selected ? UIHelper.VER_11_BOLD : UIHelper.VER_11_PLAIN);


        return contents;
    }
}
