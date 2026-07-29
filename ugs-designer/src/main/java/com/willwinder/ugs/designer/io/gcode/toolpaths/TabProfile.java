/*
    Copyright 2026 Joacim Breiler

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.designer.io.gcode.toolpaths;

import com.willwinder.ugs.designer.entities.cuttable.HoldingTabs;

import java.util.List;
import java.util.stream.Stream;

/**
 * How deep the tool may go along a closed cut with holding tabs, as a function of the distance
 * travelled along it.
 * <p>
 * A tab is shaped like a trapezoid, the tool is held at the top of the tab across its width and
 * ramps down to the full depth of the cut on either side of it.
 *
 * @author Joacim Breiler
 */
public class TabProfile {
    /**
     * The depth limit where the tool is free to cut as deep as the pass goes.
     */
    public static final double NO_LIMIT = Double.MAX_VALUE;

    private final double pathLength;
    private final List<Double> centers;
    private final double halfWidth;
    private final double rampLength;
    private final double topDepth;
    private final double targetDepth;

    private TabProfile(double pathLength, List<Double> centers, double halfWidth, double rampLength, double topDepth, double targetDepth) {
        this.pathLength = pathLength;
        this.centers = centers;
        this.halfWidth = halfWidth;
        this.rampLength = rampLength;
        this.topDepth = topDepth;
        this.targetDepth = targetDepth;
    }

    public static TabProfile none() {
        return new TabProfile(0, List.of(), 0, 0, 0, 0);
    }

    /**
     * Places the given tabs along a closed path.
     *
     * @param tabs        the tabs to place
     * @param pathLength  the length of the closed path in millimeters
     * @param targetDepth the depth the path is cut to, the tabs are measured up from it
     */
    public static TabProfile along(HoldingTabs tabs, double pathLength, double targetDepth) {
        // Nothing is cut away above the surface of the stock, so there is nothing to hold together
        if (targetDepth <= 0) {
            return none();
        }

        List<Double> centers = tabs.centersAlong(pathLength);
        if (centers.isEmpty()) {
            return none();
        }

        double topDepth = Math.max(0, targetDepth - tabs.height());
        return new TabProfile(pathLength, centers, tabs.width() / 2d, tabs.rampLength(), topDepth, targetDepth);
    }

    public boolean isEmpty() {
        return centers.isEmpty();
    }

    /**
     * The distances along the path where it needs a coordinate for the tabs to take shape, being
     * where the ramps start and where they reach the top of a tab. Distances landing on the start of
     * the path are left out, since the path already begins and ends there.
     */
    public List<Double> breakPoints() {
        return centers.stream()
                .flatMap(center -> Stream.of(
                        center - halfWidth - rampLength,
                        center - halfWidth,
                        center + halfWidth,
                        center + halfWidth + rampLength))
                .filter(distance -> distance > 0 && distance < pathLength)
                .sorted()
                .toList();
    }

    /**
     * The deepest the tool may go at the given distance along the path, or {@link #NO_LIMIT} where no
     * tab is in the way.
     */
    public double depthLimitAt(double distance) {
        double limit = NO_LIMIT;
        for (double center : centers) {
            double offset = offsetFrom(center, distance);
            if (offset >= halfWidth + rampLength) {
                continue;
            }

            limit = Math.min(limit, depthAtOffset(offset));
        }

        return limit;
    }

    private double depthAtOffset(double offset) {
        if (offset <= halfWidth) {
            return topDepth;
        }

        double rampedPart = (offset - halfWidth) / rampLength;
        return topDepth + (rampedPart * (targetDepth - topDepth));
    }

    /**
     * The path is closed, so the distance between two points along it is the shorter of the two ways
     * around.
     */
    private double offsetFrom(double center, double distance) {
        double offset = Math.abs(distance - center);
        return Math.min(offset, pathLength - offset);
    }
}
