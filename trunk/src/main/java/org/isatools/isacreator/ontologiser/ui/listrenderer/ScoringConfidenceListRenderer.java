package org.isatools.isacreator.ontologiser.ui.listrenderer;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.ontologiser.model.SuggestedAnnotation;
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
    private ImageIcon high, medium, low;

    private DefaultListCellRenderer listCellRenderer;

    public ScoringConfidenceListRenderer() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        ResourceInjector.get("ontologiser-generator-package.style").inject(this);

        Box indicators = Box.createHorizontalBox();
        indicators.setBackground(UIHelper.BG_COLOR);

        indicators.add(new CheckedCellImage());
        indicators.add(new ConfidenceLevelCellImage());

        add(indicators, BorderLayout.WEST);

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

            if (c instanceof Box) {
                for (Component panelComponent : ((Box) c).getComponents()) {

                    if (panelComponent instanceof ConfidenceLevelCellImage) {
                        if (value instanceof SuggestedAnnotation) {
                            ((ConfidenceLevelCellImage) panelComponent).setConfidenceLevel((SuggestedAnnotation) value);
                        }
                    }

                    if (panelComponent instanceof CheckedCellImage) {
                        if (value instanceof SuggestedAnnotation) {
                            ((CheckedCellImage) panelComponent).checkIsIdEntered((SuggestedAnnotation) value);
                        }
                    }
                }
            } else {

                if (selected) {
                    c.setForeground(UIHelper.DARK_GREEN_COLOR);
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
            itemSelectedIndicator = new JLabel(medium);

            add(itemSelectedIndicator);
            add(Box.createHorizontalStrut(2));
        }

        public void setConfidenceLevel(SuggestedAnnotation fieldMapping) {

            ScoringConfidence level = fieldMapping.getAnnotatorResult().getScoringConfidenceLevel();

            if (level == ScoringConfidence.HIGH) {
                itemSelectedIndicator.setIcon(high);
            } else if (level == ScoringConfidence.MEDIUM) {
                itemSelectedIndicator.setIcon(medium);
            } else if (level == ScoringConfidence.LOW) {
                itemSelectedIndicator.setIcon(low);
            }
        }
    }


}
