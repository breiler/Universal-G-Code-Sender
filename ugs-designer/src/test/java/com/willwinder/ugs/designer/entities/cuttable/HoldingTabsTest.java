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

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HoldingTabsTest {

    @Test
    public void isEnabled_shouldBeDisabledWithoutAnyTabs() {
        HoldingTabs tabs = new HoldingTabs(0, 6, 1);

        boolean enabled = tabs.isEnabled();

        assertFalse(enabled);
    }

    @Test
    public void isEnabled_shouldBeDisabledWithoutAnyHeight() {
        HoldingTabs tabs = new HoldingTabs(4, 6, 0);

        boolean enabled = tabs.isEnabled();

        assertFalse(enabled);
    }

    @Test
    public void isEnabled_shouldBeEnabledWithTabsThatHaveSize() {
        HoldingTabs tabs = new HoldingTabs(4, 6, 1);

        boolean enabled = tabs.isEnabled();

        assertTrue(enabled);
    }

    @Test
    public void centersAlong_shouldSpreadTabsEvenlyAlongThePath() {
        HoldingTabs tabs = new HoldingTabs(4, 6, 1);

        List<Double> centers = tabs.centersAlong(400);

        assertEquals(List.of(50d, 150d, 250d, 350d), centers);
    }

    @Test
    public void centersAlong_shouldKeepTabsAwayFromTheStartAndEndOfThePath() {
        HoldingTabs tabs = new HoldingTabs(2, 6, 1);

        List<Double> centers = tabs.centersAlong(16);

        assertEquals(2, centers.size());
        assertTrue("Expected the first tab to fit after the start of the path", centers.get(0) - (tabs.span() / 2) >= 0);
        assertTrue("Expected the last tab to fit before the end of the path", centers.get(1) + (tabs.span() / 2) <= 16);
    }

    @Test
    public void centersAlong_shouldPlaceFewerTabsWhenThePathIsTooShortForThemAll() {
        HoldingTabs tabs = new HoldingTabs(4, 6, 1);

        List<Double> centers = tabs.centersAlong(20);

        assertEquals(List.of(5d, 15d), centers);
    }

    @Test
    public void centersAlong_shouldPlaceNoTabsWhenThePathCanNotFitASingleOne() {
        HoldingTabs tabs = new HoldingTabs(4, 6, 1);

        List<Double> centers = tabs.centersAlong(7);

        assertTrue(centers.isEmpty());
    }

    @Test
    public void centersAlong_shouldPlaceNoTabsWhenDisabled() {
        HoldingTabs tabs = HoldingTabs.NONE;

        List<Double> centers = tabs.centersAlong(400);

        assertTrue(centers.isEmpty());
    }
}
