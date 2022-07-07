package com.willwinder.ugs.nbm.visualizer.shared.jogl;

import com.jogamp.opengl.util.FPSAnimator;
import com.willwinder.ugs.nbm.visualizer.shared.IAnimator;

public class JOGLAnimator implements IAnimator {

    private final FPSAnimator animator;

    public JOGLAnimator(FPSAnimator animator) {
        this.animator = animator;
    }

    @Override
    public void stop() {
        animator.stop();
    }

    @Override
    public void setFPS(int fps) {
        animator.setFPS(fps);
    }

    @Override
    public void start() {
        animator.start();
    }

    @Override
    public void pause() {
        animator.stop();
    }

    @Override
    public void resume() {
        animator.resume();
    }
}
