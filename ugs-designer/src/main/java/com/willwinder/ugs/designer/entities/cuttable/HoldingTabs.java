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
package com.willwinder.ugs.designer.entities.cuttable;

import java.util.List;
import java.util.stream.IntStream;

/**
 * The holding tabs left along a cut, keeping the cut out part attached to the surrounding stock
 * until it is broken loose by hand.
 *
 * @param count  the number of tabs to distribute along the cut
 * @param width  the width of a tab in millimeters, measured across its top
 * @param height the thickness of the material left underneath the tool in millimeters
 *
 * @author Joacim Breiler
 */
public record HoldingTabs(int count, double width, double height) {
    public static final HoldingTabs NONE = new HoldingTabs(0, 0, 0);

    /**
     * The tool ramps onto a tab and back off again rather than dropping straight down beside it,
     * ramping at 45 degrees keeps the specified width as the flat top of the tab.
     */
    private static final double RAMP_TO_HEIGHT_RATIO = 1d;

    public boolean isEnabled() {
        return count > 0 && width > 0 && height > 0;
    }

    /**
     * The distance along the cut that the tool needs to ramp up onto a tab.
     */
    public double rampLength() {
        return height * RAMP_TO_HEIGHT_RATIO;
    }

    /**
     * How much of the cut a single tab takes up, its ramps included.
     */
    public double span() {
        return width + (2 * rampLength());
    }

    /**
     * Distributes the tabs along a closed path, giving the distance along the path to the center of
     * each tab. The tabs are spaced out to sit between the start and the end of the path, so that
     * the path can be started and finished at full depth.
     * <p>
     * Fewer tabs than asked for are placed when the path is too short to fit them all, and none at
     * all when it can not even fit a single one.
     *
     * @param pathLength the length of the closed path in millimeters
     */
    public List<Double> centersAlong(double pathLength) {
        if (!isEnabled() || pathLength <= 0) {
            return List.of();
        }

        int fittingCount = Math.min(count, (int) Math.floor(pathLength / span()));
        if (fittingCount < 1) {
            return List.of();
        }

        double spacing = pathLength / fittingCount;
        return IntStream.range(0, fittingCount)
                .mapToObj(index -> (index + 0.5) * spacing)
                .toList();
    }
}
