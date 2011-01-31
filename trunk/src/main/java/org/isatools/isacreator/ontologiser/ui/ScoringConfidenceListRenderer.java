package org.isatools.isacreator.ontologiser.ui;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.mgrast.model.FieldMapping;
import org.isatools.isacreator.ontologymanager.bioportal.model.AnnotatorResult;
import org.isatools.isacreator.ontologymanager.bioportal.model.ScoringConfidence;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 31/01/2011
 *         Time: 18:30
 */
public class ScoringConfidenceListRenderer extends JComponent
        implements ListCellRenderer {

    @InjectedResource
    private ImageIcon oneHundredPercent, seventyFivePercent,
            fiftyPercent, twentyFivePercent, zeroPercent;

    private DefaultListCellRenderer listCellRenderer;

    public ScoringConfidenceListRenderer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        ResourceInjector.get("exporters-package.style").inject(this);

        add(new ConfidenceLevelCellImage(), BorderLayout.WEST);

        listCellRenderer = new DefaultListCellRenderer();

        add(listCellRenderer, BorderLayout.CENTER);

        setBorder(new EmptyBorder(2, 2, 2, 2));
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean selected, boolean cellGotFocus) {
        listCellRenderer.getListCellRendererComponent(list, value, index,
                selected, cellGotFocus);
        listCellRenderer.setBorder(null);

        //image.checkIsIdEntered(selected);
        Component[] components = getComponents();

        for (Component c : components) {
            ((JComponent) c).setToolTipText(value.toString());
            if (c instanceof ConfidenceLevelCellImage) {
                if (value instanceof FieldMapping) {
                    ((ConfidenceLevelCellImage) c).setConfidenceLevel((AnnotatorResult) value);
                }
            } else {
                if (selected) {
                    c.setForeground(UIHelper.GREY_COLOR);
                    c.setBackground(UIHelper.BG_COLOR);
                    c.setFont(UIHelper.VER_11_BOLD);

                } else {
                    c.setForeground(UIHelper.LIGHT_GREEN_COLOR);
                    c.setBackground(UIHelper.BG_COLOR);
                    c.setFont(UIHelper.VER_11_BOLD);
                }
            }
        }


        return this;
    }

    class ConfidenceLevelCellImage extends JPanel {
        // this will contain the general panel layout for the list item and modifier elements to allow for changing of images
        // when rendering an item as being selected and so forth.
        private JLabel itemSelectedIndicator;

        ConfidenceLevelCellImage() {
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            itemSelectedIndicator = new JLabel(zeroPercent);

            add(itemSelectedIndicator);
            add(Box.createHorizontalStrut(2));
        }

        public void setConfidenceLevel(AnnotatorResult fieldMapping) {

            ScoringConfidence level = fieldMapping.getSocringConfidenceLevel();

            if (level == ScoringConfidence.HIGH) {
                itemSelectedIndicator.setIcon(oneHundredPercent);
            } else if (level == ScoringConfidence.MEDIUM) {
                itemSelectedIndicator.setIcon(seventyFivePercent);
            } else if (level == ScoringConfidence.LOW) {
                itemSelectedIndicator.setIcon(fiftyPercent);
            } else if (level == ScoringConfidence.NONE) {
                itemSelectedIndicator.setIcon(zeroPercent);
            }
        }
    }
}
