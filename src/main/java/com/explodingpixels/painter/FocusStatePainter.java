package com.explodingpixels.painter;

import com.explodingpixels.widgets.WindowUtils;

import java.awt.*;

/**
 * An implementation of {@link Painter} that delegates to given {@code Painter} based on the focused
 * state of the {@link Component} supplied in the
 * {@link #paint(java.awt.Graphics2D, java.awt.Component, int, int)} method.
 */
public class FocusStatePainter implements Painter<Component> {

    private Painter<Component> fComponentFocusedPainter;
    private Painter<Component> fWindowFocusedPainter;
    private Painter<Component> fWindowUnfocusedPainter;

    /**
     * Creates a {@link Painter} that delegates to the given {@code Painter}s based on the focus
     * state of the supplied {@link Component} or the focus state of it's parent
     * {@link java.awt.Window}.
     *
     * @param componentFocusedPainter the {@code Painter} to use when the given {@code Component} is
     *                                focused or it's parent {@code java.awt.Window} is focused.
     * @param windowUnfocusedPainter  the {@code Painter} to use when the given {@code Component}'s
     *                                parent {@code java.awt.Window} is unfocused.
     */
    public FocusStatePainter(Painter<Component> componentFocusedPainter,
                             Painter<Component> windowUnfocusedPainter) {
        this(componentFocusedPainter, windowUnfocusedPainter, windowUnfocusedPainter);
    }

    /**
     * Creates a {@link Painter} that delegates to the given {@code Painter}s based on the focus
     * state of the supplied {@link Component} or the focus state of it's parent
     * {@link java.awt.Window}.
     *
     * @param componentFocusedPainter the {@code Painter} to use when the given {@code Component} is
     *                                focused.
     * @param windowFocusedPainter    the {@code Painter} to use when the given {@code Component} is
     *                                unfocused but the {@code Component}'s parent window is focused.
     * @param windowUnfocusedPainter  the {@code Painter} to use when the given {@code Component}'s
     *                                parent {@code java.awt.Window} is unfocused.
     */
    public FocusStatePainter(Painter<Component> componentFocusedPainter,
                             Painter<Component> windowFocusedPainter,
                             Painter<Component> windowUnfocusedPainter) {

        if (componentFocusedPainter == null) {
            throw new IllegalArgumentException("Component focused Painter cannot be null.");
        }

        if (windowFocusedPainter == null) {
            throw new IllegalArgumentException("Window focused Painter cannot be null.");
        }

        if (windowUnfocusedPainter == null) {
            throw new IllegalArgumentException("Window unfocused Painter cannot be null.");
        }

        fComponentFocusedPainter = componentFocusedPainter;
        fWindowFocusedPainter = windowFocusedPainter;
        fWindowUnfocusedPainter = windowUnfocusedPainter;

    }

    public void paint(Graphics2D g, Component component, int width, int height) {
        Painter<Component> painterToUse;

        if (component.hasFocus()) {
            painterToUse = fComponentFocusedPainter;
        } else if (WindowUtils.isParentWindowFocused(component)) {
            painterToUse = fWindowFocusedPainter;
        } else {
            painterToUse = fWindowUnfocusedPainter;
        }

        painterToUse.paint(g, component, width, height);
    }
}
