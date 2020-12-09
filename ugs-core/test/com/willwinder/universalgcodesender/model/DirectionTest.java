package com.willwinder.universalgcodesender.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DirectionTest {

    @Test
    public void shouldOnlyReturnTheMaxValueOfOne() {
        Direction direction = new Direction(11d, 22.2d, 33.3d);
        assertEquals(Double.valueOf(1), direction.getX());
        assertEquals(Double.valueOf(1), direction.getY());
        assertEquals(Double.valueOf(1), direction.getZ());
    }

    @Test
    public void shouldOnlyReturnTheMinValueOfMinusOne() {
        Direction direction = new Direction(-11d, -22d, -33d);
        assertEquals(Double.valueOf(-1), direction.getX());
        assertEquals(Double.valueOf(-1), direction.getY());
        assertEquals(Double.valueOf(-1), direction.getZ());
    }

    @Test
    public void shouldReturnNullsAsZero() {
        Direction direction = new Direction(null, null, null);
        assertEquals(Double.valueOf(0), direction.getX());
        assertEquals(Double.valueOf(0), direction.getY());
        assertEquals(Double.valueOf(0), direction.getZ());
    }

    @Test
    public void shouldOReturnWithDecimals() {
        Direction direction = new Direction(0.1, -0.1, 1.1);
        assertEquals(Double.valueOf(0.1), direction.getX());
        assertEquals(Double.valueOf(-0.1), direction.getY());
        assertEquals(Double.valueOf(1), direction.getZ());
    }
}
