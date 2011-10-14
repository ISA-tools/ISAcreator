package org.isatools.isacreator.autofilterfield;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.sun.awt.AWTUtilities;
import org.isatools.isacreator.autofilteringlist.ExtendedJList;
import org.isatools.isacreator.autofilteringlist.FilterField;
import org.isatools.isacreator.autofilteringlist.FilterableListCellRenderer;
import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.effects.GraphicsUtils;
import org.isatools.isacreator.effects.borders.RoundedBorder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * The AutoCompleteUI class provides the user interface to allow users to select study samples from an autocompleting list
 * of Study sample ids. It also allows the user to propagate metadata from the study sample file directly into the Assay file.
 */
public class AutoCompleteUI<T> extends JWindow implements ActionListener {

    public static final int INCOMING = 1;
    public static final int OUTGOING = -1;

    public static final float ANIMATION_DURATION = 250f;
    public static final int ANIMATION_SLEEP = 10;
    public static final float DESIRED_OPACITY = .93f;

    private Timer animationTimer;

    private boolean animating;

    private int animationDirection;
    private long animationStart;

    public static final int HEIGHT = 200;
    public static final int WIDTH = 230;

    private FilterField filterField;
    private Collection<T> filterableContent;
    private ListCellRenderer cellRenderer;

    private ExtendedJList filterList;


    public AutoCompleteUI(FilterField filterField, Collection<T> filterableContent) {
        this(filterField, filterableContent, new FilterableListCellRenderer());
    }

    public AutoCompleteUI(FilterField filterField, Collection<T> filterableContent, ListCellRenderer cellRenderer) {
        this.filterField = filterField;
        this.filterableContent = filterableContent;
        this.cellRenderer = cellRenderer;
    }

    public void createGUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        setAlwaysOnTop(true);


        setBackground(UIHelper.BG_COLOR);

        addList();
        addCloseOption();
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(2, 2, 2, 2));
        pack();
    }

    private void addList() {
        filterList = new ExtendedJList(cellRenderer, filterField, true);
        filterList.setAutoscrolls(true);
        filterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                if (mouseEvent.getClickCount() > 1) {
                    if (!filterList.isSelectionEmpty()) {
                        filterField.setText(filterList.getSelectedTerm());
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
            }
        });
        updateContent(filterableContent);

        JScrollPane listScroller = new JScrollPane(filterList,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        IAppWidgetFactory.makeIAppScrollPane(listScroller);

        add(listScroller, BorderLayout.CENTER);
    }

    private void addCloseOption() {
        final JLabel closeLabel = UIHelper.createLabel("close this window (esc)", UIHelper.VER_10_PLAIN, UIHelper.DARK_GREEN_COLOR);
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        closeLabel.setToolTipText("<html><strong>close</strong></html>");

        closeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                closeLabel.setFont(UIHelper.VER_9_PLAIN);
                fadeOutWindow();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                closeLabel.setFont(UIHelper.VER_9_BOLD);

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                closeLabel.setFont(UIHelper.VER_9_PLAIN);
            }
        });

        JPanel bottomPanel = UIHelper.wrapComponentInPanel(closeLabel);
        bottomPanel.setBackground(UIHelper.LIGHT_GREEN_COLOR);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void selectNextItem() {

        if (filterList.isSelectionEmpty()) {
            filterList.setSelectedIndex(0);
        } else if (filterList.getSelectedIndex() < filterList.getItems().size()) {
            filterList.setSelectedIndex(filterList.getSelectedIndex() + 1);
            scrollToItem();
        }
    }

    public void selectPreviousItem() {

        if (filterList.isSelectionEmpty()) {
            filterList.setSelectedIndex(0);
        } else if (filterList.getSelectedIndex() > 0) {
            filterList.setSelectedIndex(filterList.getSelectedIndex() - 1);
            scrollToItem();
        }
    }

    public Object getSelectedItem() {
        if (!filterList.isSelectionEmpty()) {
            return filterList.getSelectedValue();
        }

        return null;
    }

    public String getSelectedValue() {
        if (!filterList.isSelectionEmpty()) {
            return filterList.getSelectedTerm();
        }

        return "";
    }

    private void scrollToItem() {
        filterList.scrollRectToVisible(
                filterList.getCellBounds(filterList.getSelectedIndex(), filterList.getSelectedIndex()));
    }


    public void fadeInWindow() {
        if (GraphicsUtils.isWindowTransparencySupported()) {
            animationDirection = INCOMING;
            startAnimation();
        } else {
            setVisible(true);
        }
    }

    public void fadeOutWindow() {
        if (GraphicsUtils.isWindowTransparencySupported()) {
            animationDirection = OUTGOING;
            startAnimation();
        } else {
            setVisible(false);
        }

    }

    private void startAnimation() {

        // start animation timer
        animationStart = System.currentTimeMillis();

        if (animationTimer == null) {
            animationTimer = new Timer(ANIMATION_SLEEP, this);
        }

        animating = true;

        if (!isShowing()) {

            AWTUtilities.setWindowOpacity(this, 0f);
            repaint();
            setVisible(true);
        }

        animationTimer.start();
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (animating) {
            // calculate height to show
            float animationPercent = (System.currentTimeMillis() -
                    animationStart) / ANIMATION_DURATION;
            animationPercent = Math.min(DESIRED_OPACITY, animationPercent);

            float opacity;

            if (animationDirection == INCOMING) {
                opacity = animationPercent;
            } else {
                opacity = DESIRED_OPACITY - animationPercent;
            }

            AWTUtilities.setWindowOpacity(this, opacity);
            repaint();

            if (animationPercent >= DESIRED_OPACITY) {
                stopAnimation();

                if (animationDirection == OUTGOING) {
                    setVisible(false);
                }
            }
        }
    }

    private void stopAnimation() {
        animationTimer.stop();
        animating = false;

        repaint();
    }

    public void updateContent(Collection<T> studySampleInformation) {
        filterableContent = studySampleInformation;

        filterList.clearItems();

        for (T item : filterableContent) {
            filterList.addItem(item);
        }
    }
}
