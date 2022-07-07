package com.willwinder.ugs.nbm.visualizer.shared;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public interface GL {
    void glPushMatrix();

    void glTranslated(double i, double v, double i1);

    void glColor4fv(float[] color, int i);

    void glLineWidth(float v);

    void glBegin(int glLines);

    void glVertex3d(double x, double y, double i);

    void glEnd();

    void glPopMatrix();

    void glRotated(double i, double i1, double i2, double i3);

    void glColor4f(float v, float v1, float v2, float v3);

    boolean isFunctionAvailable(String glGenBuffers);

    void glEnableClientState(int glVertexArray);

    void glDrawArrays(int glLines, int i, int numberOfVertices);

    void glDisableClientState(int glColorArray);

    void glColor3ub(byte lineColorDatum, byte lineColorDatum1, byte lineColorDatum2);

    void glColorPointer(int i, int glUnsignedByte, int i1, ByteBuffer lineColorBuffer);

    void glVertexPointer(int i, int glFloat, int i1, FloatBuffer lineVertexBuffer);

    void glScaled(double scale, double scale1, double scale2);

    void glViewport(int i, int i1, int squareSize, int squareSize1);

    void glMatrixMode(int glProjection);

    void glLoadIdentity();

    void glOrtho(double v, double v1, double v2, double v3, double v4, double v5);

    void glRotatef(float i, float i1, float i2, float i3);

    void glColor3f(float v, float v1, float v2);

    void glVertex3f(float v, float v1, float halfFaceSize);

    void glDisable(int glDepthTest);

    void glEnable(int glCullFace);

    void glShadeModel(int glSmooth);

    void glClearColor(float v, float v1, float v2, float v3);

    void glClearDepth(float v);

    void glBlendFunc(int glSrcAlpha, int glOneMinusSrcAlpha);

    void glDepthFunc(int glLequal);

    void glHint(int glPerspectiveCorrectionHint, int glNicest);

    void glLightfv(int glLight0, int glAmbient, float[] ambient, int i);

    void glColorMaterial(int glFront, int glDiffuse);

    void glMaterialfv(int glFront, int glDiffuse, float[] diffuseMaterial, int i);

    void glColor4d(double v, double v1, double v2, double i);

    void glClear(int i);

    void glGetIntegerv(int glViewport, int[] viewPort, int i);

    void glGetDoublev(int glModelviewMatrix, double[] modelViewMatrix, int i);
}
