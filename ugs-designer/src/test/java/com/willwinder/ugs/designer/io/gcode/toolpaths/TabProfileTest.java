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
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TabProfileTest {

    @Test
    public void along_shouldNotPlaceTabsWhenNothingIsCutIntoTheStock() {
        TabProfile profile = TabProfile.along(new HoldingTabs(2, 6, 1), 100, 0);

        boolean empty = profile.isEmpty();

        assertTrue(empty);
    }

    @Test
    public void along_shouldPlaceTabsWhenTheyFitAlongThePath() {
        TabProfile profile = TabProfile.along(new HoldingTabs(2, 6, 1), 100, 3);

        boolean empty = profile.isEmpty();

        assertFalse(empty);
    }

    @Test
    public void depthLimitAt_shouldNotLimitTheDepthAwayFromTheTabs() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 1), 100, 3);

        double limit = profile.depthLimitAt(0);

        assertEquals(TabProfile.NO_LIMIT, limit, 0.01);
    }

    @Test
    public void depthLimitAt_shouldLimitTheDepthToTheTopOfTheTab() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 1), 100, 3);

        double limit = profile.depthLimitAt(50);

        assertEquals(2, limit, 0.01);
    }

    @Test
    public void depthLimitAt_shouldLimitTheDepthAcrossTheWholeWidthOfTheTab() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 1), 100, 3);

        double limit = profile.depthLimitAt(53);

        assertEquals(2, limit, 0.01);
    }

    @Test
    public void depthLimitAt_shouldRampFromTheFullDepthUpToTheTopOfTheTab() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 2), 100, 6);

        double limit = profile.depthLimitAt(54);

        assertEquals(5, limit, 0.01);
    }

    @Test
    public void depthLimitAt_shouldNotLimitTheDepthWhereTheRampStarts() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 2), 100, 6);

        double limit = profile.depthLimitAt(55);

        assertEquals(TabProfile.NO_LIMIT, limit, 0.01);
    }

    @Test
    public void depthLimitAt_shouldLeaveTheTabTopAtTheSurfaceWhenTheTabIsTallerThanTheCut() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 10), 100, 3);

        double limit = profile.depthLimitAt(50);

        assertEquals(0, limit, 0.01);
    }

    @Test
    public void depthLimitAt_shouldLimitTheDepthAtEveryTabAlongThePath() {
        TabProfile profile = TabProfile.along(new HoldingTabs(2, 6, 1), 100, 3);

        List<Double> limits = List.of(profile.depthLimitAt(25), profile.depthLimitAt(75));

        assertEquals(List.of(2d, 2d), limits);
    }

    @Test
    public void breakPoints_shouldGiveTheStartAndEndOfEveryRampAndTabTop() {
        TabProfile profile = TabProfile.along(new HoldingTabs(1, 6, 1), 100, 3);

        List<Double> breakPoints = profile.breakPoints();

        assertEquals(List.of(46d, 47d, 53d, 54d), breakPoints);
    }

    @Test
    public void breakPoints_shouldBeEmptyWithoutTabs() {
        TabProfile profile = TabProfile.none();

        List<Double> breakPoints = profile.breakPoints();

        assertTrue(breakPoints.isEmpty());
    }
}
