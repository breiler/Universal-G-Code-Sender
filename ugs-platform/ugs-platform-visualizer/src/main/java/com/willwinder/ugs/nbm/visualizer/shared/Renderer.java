package com.willwinder.ugs.nbm.visualizer.shared;

import com.willwinder.universalgcodesender.model.Position;

import javax.swing.JComponent;
import java.awt.Point;

public interface Renderer {
    void reloadPreferences();

    void setObjectSize(Position min, Position max);

    void setMachineCoordinate(Position machineCoord);

    void setWorkCoordinate(Position workCoord);

    void mouseMoved(Point point);

    Position getMouseWorldLocation();

    void mousePan(Point point);

    void mouseRotate(Point point);

    void zoom(int wheelRotation);

    void zoomToRegion(Position min, Position max, double bufferFactor);

    void pan(int dx, int dy);

    void resetView();

    IRendererInputHandler getRendererInputHandler();

    JComponent getPanel();

    void destroy();

    void moveCamera(Position position, Position rotation, double zoom);
}
