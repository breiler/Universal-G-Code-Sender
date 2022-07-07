package com.willwinder.ugs.nbm.visualizer.shared;

public interface GLDrawable {
    GL getGL();

    int getSurfaceHeight();

    int getSurfaceWidth();

    int[] convertToPixelUnits(int[] raw);
}
