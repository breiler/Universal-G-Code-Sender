package com.willwinder.universalgcodesender.uielements.toolbar;

import javax.swing.Icon;
import javax.swing.UIManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

class ArrowIcon extends VectorIcon {
    public static final Icon INSTANCE_DEFAULT = new ArrowIcon(false);
    public static final Icon INSTANCE_DISABLED = new ArrowIcon(true);
    private final boolean disabled;

    private ArrowIcon(boolean disabled) {
        super(5, 4);
        this.disabled = disabled;
    }

    @Override
    protected void paintIcon(Component c, Graphics2D g, int width, int height, double scaling) {
        final Color color;
        if (UIManager.getBoolean("nb.dark.theme")) {
            // Foreground brightness level taken from the combobox dropdown on Darcula.
            color = disabled ? new Color(90, 90, 90, 255) : new Color(187, 187, 187, 255);
        } else {
            color = disabled ? new Color(201, 201, 201, 255) : new Color(86, 86, 86, 255);
        }
        g.setColor(color);
        final double overshoot = 2.0 / scaling;
        final double arrowWidth = width + overshoot * scaling;
        final double arrowHeight = height - 0.2 * scaling;
        final double arrowMidX = arrowWidth / 2.0 - (overshoot / 2.0) * scaling;
        g.clipRect(0, 0, width, height);
        Path2D.Double arrowPath = new Path2D.Double();
        arrowPath.moveTo(arrowMidX - arrowWidth / 2.0, 0);
        arrowPath.lineTo(arrowMidX, arrowHeight);
        arrowPath.lineTo(arrowMidX + arrowWidth / 2.0, 0);
        arrowPath.closePath();
        g.fill(arrowPath);
    }
}
