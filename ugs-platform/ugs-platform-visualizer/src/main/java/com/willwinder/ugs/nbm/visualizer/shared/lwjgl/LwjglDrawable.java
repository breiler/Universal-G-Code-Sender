package com.willwinder.ugs.nbm.visualizer.shared.lwjgl;

import com.willwinder.ugs.nbm.visualizer.shared.GL;
import com.willwinder.ugs.nbm.visualizer.shared.GLDrawable;

public class LwjglDrawable implements GLDrawable {
    private final LwjglGL gl;

    public LwjglDrawable() {
        gl = new LwjglGL();
    }
    @Override
    public GL getGL() {
        return gl;
    }

    @Override
    public int getSurfaceHeight() {
        return 100;
    }

    @Override
    public int getSurfaceWidth() {
        return 100;
    }

    @Override
    public int[] convertToPixelUnits(int[] raw) {
        return new int[0];
    }
}
