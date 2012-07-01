package org.isatools.isacreator.visualization.workflowvisualization;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import org.apache.commons.collections15.OrderedMap;
import org.apache.commons.collections15.map.ListOrderedMap;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.visualization.workflowvisualization.taxonomy.TaxonomyLegendRenderer;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This will render the fragmented view of the glyph to show its composition
 * And to make it easier for users to learn how glyphs are composed.
 *
 * @author Eamonn Maguire (eamonnmag@gmail.com)
 */
public class FragmentedGlyphRenderer extends JPanel {

    public static final String FRAGMENT_FILE_DIR = "Data" + File.separator + "images" + File.separator + "fragments" + File.separator;
    private static final int EXPECTED_HEIGHT = 50;
    private static final int TARGET_HEIGHT = 200;
    private static final int WIDTH = 250;

    private static TaxonomyLegendRenderer taxonomyLegendRenderer = new TaxonomyLegendRenderer();
    private static EmptyBorder border = new EmptyBorder(0, 15, 0, 0);

    private OrderedMap<String, Integer> taxonomyToRender;

    private List<Fragment> fragments;

    @InjectedResource
    private ImageIcon arrow;

    public FragmentedGlyphRenderer() {
        ResourceInjector.get("workflow-package.style").inject(this);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, TARGET_HEIGHT));
        taxonomyLegendRenderer.createGUI();

    }

    public void render() {

        Box container = Box.createVerticalBox();
        container.setBackground(UIHelper.BG_COLOR);

        fragments = new ArrayList<Fragment>();

        for (String fragment : taxonomyToRender.keySet()) {
            Fragment fragmentView = createFragment(fragment);
            fragments.add(fragmentView);
            container.add(fragmentView);
        }
        // we add padding to the panel depending on the number of added elements
        if (taxonomyToRender.size() * EXPECTED_HEIGHT < TARGET_HEIGHT) {
            container.add(Box.createVerticalStrut(TARGET_HEIGHT - (taxonomyToRender.size() * EXPECTED_HEIGHT)));
        }

        JScrollPane containerScroller = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        containerScroller.getViewport().setBackground(UIHelper.BG_COLOR);
        containerScroller.setPreferredSize(new Dimension(WIDTH, 200));

        IAppWidgetFactory.makeIAppScrollPane(containerScroller);

        add(containerScroller, BorderLayout.CENTER);
    }

    private Fragment createFragment(String fragment) {
        Fragment fragmentView = new Fragment();
        fragmentView.createViewForFragment(fragment);
        return fragmentView;
    }

    public void setTaxonomyToRender(String taxonomyToRender) {
        this.taxonomyToRender = processTaxonomyString(taxonomyToRender);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                removeAll();
                render();
                updateUI();
            }
        });
    }

    public void closeAll() {
        taxonomyLegendRenderer.setVisible(false);
    }

    private OrderedMap<String, Integer> processTaxonomyString(String taxonomyAsString) {
        //"protocol(2):in vivo(3):material amplification(5):organism(7)"
        String[] fragments = taxonomyAsString.split(":");
        taxonomyToRender = new ListOrderedMap<String, Integer>();
        for (String fragment : fragments) {
            String classification = fragment.substring(0, fragment.indexOf("("));
            taxonomyToRender.put(classification, Integer.valueOf(fragment.replaceAll(classification, "").replaceAll("\\(|\\)", "")));
        }

        return taxonomyToRender;
    }

    private void resetArrow() {
        for (Fragment fragment : fragments) {
            fragment.arrowContainer.setIcon(null);
        }
    }

    class Fragment extends JPanel implements MouseListener {

        String image;
        JLabel arrowContainer;

        Fragment() {
            setLayout(new BorderLayout());
            setBackground(UIHelper.BG_COLOR);
            addMouseListener(this);
            setToolTipText("<html>Click to view related visual items</html>");
        }

        private void createViewForFragment(String fragment) {

            ImageIcon fragmentImage = getImageForFragment(fragment);
            if (fragmentImage != null) {
                JLabel image = new JLabel(fragmentImage);
                image.setBorder(border);
                add(image, BorderLayout.WEST);
            }

            arrowContainer = new JLabel();

            add(UIHelper.createLabel("<html><p>" + getCleanedFragmentName(fragment) + "</p></html>", UIHelper.VER_11_BOLD, UIHelper.GREY_COLOR), BorderLayout.CENTER);
            add(UIHelper.wrapComponentInPanel(arrowContainer), BorderLayout.EAST);
        }

        private String getCleanedFragmentName(String fragment) {
            return fragment.contains("_") ? fragment.substring(fragment.indexOf("_") + 1) : fragment;
        }

        private ImageIcon getImageForFragment(String fragment) {
            String actualFragmentImageName = fragment.replaceAll("\\s+", "-");

            image = FRAGMENT_FILE_DIR + actualFragmentImageName + ".png";
            File fragmentFile = new File(image);

            System.out.println(fragmentFile.getAbsolutePath());
            if (fragmentFile.exists()) {
                return new ImageIcon(fragmentFile.getAbsolutePath());
            }
            return null;
        }

        public void mouseClicked(MouseEvent mouseEvent) {
            Point point = new Point(getLocationOnScreen().x + FragmentedGlyphRenderer.WIDTH, getLocationOnScreen().y);
            taxonomyLegendRenderer.renderLevel(taxonomyLegendRenderer.findLevelForImage(image), point);
            resetArrow();
            arrowContainer.setIcon(arrow);
        }

        public void mousePressed(MouseEvent mouseEvent) {
        }

        public void mouseReleased(MouseEvent mouseEvent) {
        }

        public void mouseEntered(MouseEvent mouseEvent) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void mouseExited(MouseEvent mouseEvent) {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
}
