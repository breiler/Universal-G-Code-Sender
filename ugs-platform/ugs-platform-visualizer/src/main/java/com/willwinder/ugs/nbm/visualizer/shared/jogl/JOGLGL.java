package com.willwinder.ugs.nbm.visualizer.shared.jogl;

import com.willwinder.ugs.nbm.visualizer.shared.GL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class JOGLGL implements GL {
    private final com.jogamp.opengl.GL gl;

    public JOGLGL(com.jogamp.opengl.GL gl) {
        this.gl = gl;
    }

    @Override
    public void glPushMatrix() {
        gl.getGL2().glPushMatrix();
    }

    @Override
    public void glTranslated(double v1, double v2, double v3) {
        gl.getGL2().glTranslated(v1, v2, v3);
    }

    @Override
    public void glColor4fv(float[] color, int i) {
        gl.getGL2().glColor4fv(color, i);
    }

    @Override
    public void glLineWidth(float v) {
        gl.getGL2().glLineWidth(v);
    }

    @Override
    public void glBegin(int i) {
        gl.getGL2().glBegin(i);
    }

    @Override
    public void glVertex3d(double x, double y, double i) {
        gl.getGL2().glVertex3d(x, y, i);
    }

    @Override
    public void glEnd() {
        gl.getGL2().glEnd();
    }

    @Override
    public void glPopMatrix() {
        gl.getGL2().glPopMatrix();
    }

    @Override
    public void glRotated(double i, double i1, double i2, double i3) {
        gl.getGL2().glRotated(i, i1, i2, i3);
    }

    @Override
    public void glColor4f(float v, float v1, float v2, float v3) {
        gl.getGL2().glColor4f(v, v1, v2, v3);
    }

    @Override
    public boolean isFunctionAvailable(String glGenBuffers) {
        return gl.getGL2().isFunctionAvailable(glGenBuffers);
    }

    @Override
    public void glEnableClientState(int glVertexArray) {
        gl.getGL2().glEnableClientState(glVertexArray);
    }

    @Override
    public void glDrawArrays(int glLines, int i, int numberOfVertices) {
        gl.getGL2().glDrawArrays(glLines, i, numberOfVertices);
    }

    @Override
    public void glDisableClientState(int glColorArray) {
        gl.getGL2().glDisableClientState(glColorArray);
    }

    @Override
    public void glColor3ub(byte lineColorDatum, byte lineColorDatum1, byte lineColorDatum2) {
        gl.getGL2().glColor3ub(lineColorDatum, lineColorDatum1, lineColorDatum2);
    }

    @Override
    public void glColorPointer(int i, int glUnsignedByte, int i1, ByteBuffer lineColorBuffer) {
        gl.getGL2().glColorPointer(i, glUnsignedByte, i1, lineColorBuffer);
    }

    @Override
    public void glVertexPointer(int i, int glFloat, int i1, FloatBuffer lineVertexBuffer) {
        gl.getGL2().glVertexPointer(i, glFloat, i1, lineVertexBuffer);
    }

    @Override
    public void glScaled(double scale, double scale1, double scale2) {
        gl.getGL2().glScaled(scale, scale1, scale2);
    }

    @Override
    public void glViewport(int i, int i1, int squareSize, int squareSize1) {
        gl.getGL2().glViewport(i, i1, squareSize, squareSize1);
    }

    @Override
    public void glMatrixMode(int glProjection) {
        gl.getGL2().glMatrixMode(glProjection);
    }

    @Override
    public void glLoadIdentity() {
        gl.getGL2().glLoadIdentity();
    }

    @Override
    public void glOrtho(double v, double v1, double v2, double v3, double v4, double v5) {
        gl.getGL2().glOrtho(v, v1, v2, v3, v4, v5);
    }

    @Override
    public void glRotatef(float i, float i1, float i2, float i3) {
        gl.getGL2().glRotatef(i, i1, i2, i3);
    }

    @Override
    public void glColor3f(float v, float v1, float v2) {
        gl.getGL2().glColor3f(v, v1, v2);
    }

    @Override
    public void glVertex3f(float v, float v1, float halfFaceSize) {
        gl.getGL2().glColor3f(v, v1, halfFaceSize);
    }

    @Override
    public void glDisable(int glDepthTest) {
        gl.getGL2().glDisable(glDepthTest);
    }

    @Override
    public void glEnable(int glCullFace) {
        gl.getGL2().glEnable(glCullFace);
    }

    @Override
    public void glShadeModel(int glSmooth) {
        gl.getGL2().glShadeModel(glSmooth);
    }

    @Override
    public void glClearColor(float v, float v1, float v2, float v3) {
        gl.getGL2().glClearColor(v, v1, v2, v3);
    }

    @Override
    public void glClearDepth(float v) {
        gl.getGL2().glClearDepth(v);
    }

    @Override
    public void glBlendFunc(int glSrcAlpha, int glOneMinusSrcAlpha) {
        gl.getGL2().glBlendFunc(glSrcAlpha, glOneMinusSrcAlpha);
    }

    @Override
    public void glDepthFunc(int glLequal) {
        gl.getGL2().glDepthFunc(glLequal);
    }

    @Override
    public void glHint(int glPerspectiveCorrectionHint, int glNicest) {
        gl.getGL2().glHint(glPerspectiveCorrectionHint, glNicest);
    }

    @Override
    public void glLightfv(int glLight0, int glAmbient, float[] ambient, int i) {
        gl.getGL2().glLightfv(glLight0, glAmbient, ambient, i);
    }

    @Override
    public void glColorMaterial(int glFront, int glDiffuse) {
        gl.getGL2().glColorMaterial(glFront, glDiffuse);
    }

    @Override
    public void glMaterialfv(int glFront, int glDiffuse, float[] diffuseMaterial, int i) {
        gl.getGL2().glMaterialfv(glFront, glDiffuse, diffuseMaterial, i);
    }

    @Override
    public void glColor4d(double v, double v1, double v2, double v3) {
        gl.getGL2().glColor4d(v, v1, v2, v3);
    }

    @Override
    public void glClear(int i) {
        gl.getGL2().glClear(i);
    }

    @Override
    public void glGetIntegerv(int glViewport, int[] viewPort, int i) {
        gl.getGL2().glGetIntegerv(glViewport, viewPort, i);
    }

    @Override
    public void glGetDoublev(int glModelviewMatrix, double[] modelViewMatrix, int i) {
        gl.getGL2().glGetDoublev(glModelviewMatrix, modelViewMatrix, i);
    }
}
