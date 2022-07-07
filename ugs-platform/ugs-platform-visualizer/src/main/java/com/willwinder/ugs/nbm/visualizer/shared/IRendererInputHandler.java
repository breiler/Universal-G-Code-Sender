package com.willwinder.ugs.nbm.visualizer.shared;

import com.willwinder.universalgcodesender.listeners.UGSEventListener;

import java.util.prefs.PreferenceChangeListener;

public interface IRendererInputHandler extends UGSEventListener, PreferenceChangeListener {
    void setGcodeFile(String absolutePath);
}
