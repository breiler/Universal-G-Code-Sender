package com.willwinder.ugs.nbm.visualizer.shared;

public interface IAnimator {
    void stop();

    void setFPS(int fps);

    void start();

    void pause();

    void resume();
}
