package com.willwinder.ugs.nbm.visualizer.shared.lwjgl;

import com.willwinder.ugs.nbm.visualizer.shared.GL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class LwjglGL implements GL {
    @Override
    public void glPushMatrix() {
        org.lwjgl.opengl.GL11.glPushMatrix();
    }

    @Override
    public void glTranslated(double i, double v, double i1) {
        org.lwjgl.opengl.GL11.glTranslated(i, v, i1);
    }

    @Override
    public void glColor4fv(float[] color, int i) {
        org.lwjgl.opengl.GL11.glColor4fv(color);
    }

    @Override
    public void glLineWidth(float v) {
        org.lwjgl.opengl.GL11.glLineWidth(v);
    }

    @Override
    public void glBegin(int glLines) {
        org.lwjgl.opengl.GL11.glBegin(glLines);
    }

    @Override
    public void glVertex3d(double x, double y, double i) {
        org.lwjgl.opengl.GL11.glVertex3d(x, y, i);
    }

    @Override
    public void glEnd() {
        org.lwjgl.opengl.GL11.glEnd();
    }

    @Override
    public void glPopMatrix() {
        org.lwjgl.opengl.GL11.glPopMatrix();
    }

    @Override
    public void glRotated(double i, double i1, double i2, double i3) {
        org.lwjgl.opengl.GL11.glRotated(i, i1, i2, i3);
    }

    @Override
    public void glColor4f(float v, float v1, float v2, float v3) {
        org.lwjgl.opengl.GL11.glColor4f(v, v1, v2, v3);
    }

    @Override
    public boolean isFunctionAvailable(String glGenBuffers) {
        return true; // FIXME
    }

    @Override
    public void glEnableClientState(int glVertexArray) {
        org.lwjgl.opengl.GL11.glEnableClientState(glVertexArray);
    }

    @Override
    public void glDrawArrays(int glLines, int i, int numberOfVertices) {
        org.lwjgl.opengl.GL11.glDrawArrays(glLines, i, numberOfVertices);
    }

    @Override
    public void glDisableClientState(int glColorArray) {
        org.lwjgl.opengl.GL11.glDisableClientState(glColorArray);
    }

    @Override
    public void glColor3ub(byte lineColorDatum, byte lineColorDatum1, byte lineColorDatum2) {
        org.lwjgl.opengl.GL11.glColor3ub(lineColorDatum, lineColorDatum1, lineColorDatum2);
    }

    @Override
    public void glColorPointer(int i, int glUnsignedByte, int i1, ByteBuffer lineColorBuffer) {
        org.lwjgl.opengl.GL11.glColorPointer(i, glUnsignedByte, i1, lineColorBuffer);
    }

    @Override
    public void glVertexPointer(int i, int glFloat, int i1, FloatBuffer lineVertexBuffer) {
        org.lwjgl.opengl.GL11.glVertexPointer(i, glFloat, i1, lineVertexBuffer);
    }

    @Override
    public void glScaled(double scale, double scale1, double scale2) {
        org.lwjgl.opengl.GL11.glScaled(scale, scale1, scale2);
    }

    @Override
    public void glViewport(int i, int i1, int squareSize, int squareSize1) {
        org.lwjgl.opengl.GL11.glViewport(i, i1, squareSize, squareSize1);
    }

    @Override
    public void glMatrixMode(int glProjection) {
        org.lwjgl.opengl.GL11.glMatrixMode(glProjection);
    }

    @Override
    public void glLoadIdentity() {
        org.lwjgl.opengl.GL11.glLoadIdentity();
    }

    @Override
    public void glOrtho(double v, double v1, double v2, double v3, double v4, double v5) {
        org.lwjgl.opengl.GL11.glOrtho(v, v1, v2, v3, v4, v5);
    }

    @Override
    public void glRotatef(float i, float i1, float i2, float i3) {
        org.lwjgl.opengl.GL11.glRotatef(i, i1, i2, i3);
    }

    @Override
    public void glColor3f(float v, float v1, float v2) {
        org.lwjgl.opengl.GL11.glColor3f(v, v1, v2);
    }

    @Override
    public void glVertex3f(float v, float v1, float halfFaceSize) {
        org.lwjgl.opengl.GL11.glVertex3f(v, v1,halfFaceSize);
    }

    @Override
    public void glDisable(int glDepthTest) {
        org.lwjgl.opengl.GL11.glDisable(glDepthTest);
    }

    @Override
    public void glEnable(int glCullFace) {
        org.lwjgl.opengl.GL11.glEnable(glCullFace);
    }

    @Override
    public void glShadeModel(int glSmooth) {
        org.lwjgl.opengl.GL11.glShadeModel(glSmooth);
    }

    @Override
    public void glClearColor(float v, float v1, float v2, float v3) {
        org.lwjgl.opengl.GL11.glClearColor(v, v1, v2, v3);
    }

    @Override
    public void glClearDepth(float v) {
        org.lwjgl.opengl.GL11.glClearDepth(v);
    }

    @Override
    public void glBlendFunc(int glSrcAlpha, int glOneMinusSrcAlpha) {
        org.lwjgl.opengl.GL11.glBlendFunc(glSrcAlpha, glOneMinusSrcAlpha);
    }

    @Override
    public void glDepthFunc(int glLequal) {
        org.lwjgl.opengl.GL11.glDepthFunc(glLequal);
    }

    @Override
    public void glHint(int glPerspectiveCorrectionHint, int glNicest) {
        org.lwjgl.opengl.GL11.glHint(glPerspectiveCorrectionHint, glNicest);
    }

    @Override
    public void glLightfv(int glLight0, int glAmbient, float[] ambient, int i) {
        org.lwjgl.opengl.GL11.glLightfv(glLight0, glAmbient, ambient);
    }

    @Override
    public void glColorMaterial(int glFront, int glDiffuse) {
        org.lwjgl.opengl.GL11.glColorMaterial(glFront, glDiffuse);
    }

    @Override
    public void glMaterialfv(int glFront, int glDiffuse, float[] diffuseMaterial, int i) {
        org.lwjgl.opengl.GL11.glMaterialfv(glFront, glDiffuse, diffuseMaterial);
    }

    @Override
    public void glColor4d(double v, double v1, double v2, double i) {
        org.lwjgl.opengl.GL11.glColor4d(v, v1, v2, i);
    }

    @Override
    public void glClear(int i) {
        org.lwjgl.opengl.GL11.glClear(i);
    }

    @Override
    public void glGetIntegerv(int glViewport, int[] viewPort, int i) {
        org.lwjgl.opengl.GL11.glGetIntegerv(glViewport, viewPort);
    }

    @Override
    public void glGetDoublev(int glModelviewMatrix, double[] modelViewMatrix, int i) {
        org.lwjgl.opengl.GL11.glGetDoublev(glModelviewMatrix, modelViewMatrix);
    }
}
