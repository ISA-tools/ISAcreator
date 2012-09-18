package org.isatools.isacreator.visualization.workflowvisualization.taxonomy;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.HUDTitleBar;
import org.isatools.isacreator.visualization.workflowvisualization.taxonomy.io.TaxonomyLevelLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Created by the ISA team
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 *         <p/>
 *         Date: 25/06/2012
 *         Time: 22:26
 */
public class TaxonomyLegendRenderer extends JFrame {

    private List<TaxonomyLevel> taxonomyLevels;

    private JPanel items;

    public TaxonomyLegendRenderer(List<TaxonomyLevel> taxonomyLevels) {
        this.taxonomyLevels = taxonomyLevels;
        setLayout(new BorderLayout());
        setBackground(UIHelper.BG_COLOR);

        if (!isDisplayable()) {
            setUndecorated(true);
        }
    }

    public void createGUI() {
        ((JComponent) getContentPane()).setBorder(new LineBorder(UIHelper.LIGHT_GREEN_COLOR, 1));

        items = new JPanel(new FlowLayout());

        JScrollPane itemScroller = new JScrollPane(items, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        itemScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        itemScroller.setPreferredSize(new Dimension(350, 95));
        itemScroller.setBorder(new EmptyBorder(1, 1, 1, 1));

        IAppWidgetFactory.makeIAppScrollPane(itemScroller);

        addTitlePanel();
        add(itemScroller, BorderLayout.CENTER);
        pack();
    }

    private void addTitlePanel() {
        HUDTitleBar titleBar = new HUDTitleBar(null, null);
        add(titleBar, BorderLayout.NORTH);
        titleBar.installListeners();
    }

    public void renderLevel(final TaxonomyLevel level, final Point location) {

        if (level != null) {
            items.removeAll();

            for (TaxonomyItem item : level.getTaxonomyItems().values()) {
                items.add(item);
            }
            items.validate();

            setLocation(location);
            setVisible(true);

        }
    }

    public TaxonomyLevel findLevelForImage(String image) {
        for (TaxonomyLevel level : taxonomyLevels) {
            if (level.getTaxonomyItems().containsKey(image)) {
                return level;
            }
        }

        return null;
    }

}
