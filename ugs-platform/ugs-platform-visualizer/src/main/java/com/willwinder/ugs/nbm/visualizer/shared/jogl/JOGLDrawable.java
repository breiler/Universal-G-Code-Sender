package com.willwinder.ugs.nbm.visualizer.shared.jogl;

import com.jogamp.opengl.GLAutoDrawable;
import com.willwinder.ugs.nbm.visualizer.shared.GL;
import com.willwinder.ugs.nbm.visualizer.shared.GLDrawable;

public class JOGLDrawable  implements GLDrawable {
    private final GLAutoDrawable drawable;

    public JOGLDrawable(GLAutoDrawable drawable) {
        this.drawable = drawable;
    }

    public GL getGL() {
        return new JOGLGL(drawable.getGL());
    }

    @Override
    public int getSurfaceHeight() {
        return drawable.getDelegatedDrawable().getSurfaceHeight();
    }

    @Override
    public int getSurfaceWidth() {
        return drawable.getDelegatedDrawable().getSurfaceWidth();
    }

    @Override
    public int[] convertToPixelUnits(int[] raw) {
        return drawable.getNativeSurface().convertToPixelUnits(raw);
    }
}
