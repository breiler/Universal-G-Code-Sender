/*
    Copyright 2013-2022 Will Winder

    This file is part of Universal Gcode Sender (UGS).

    UGS is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    UGS is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with UGS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.willwinder.ugs.nbm.visualizer.shared.jogl;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.willwinder.ugs.nbm.visualizer.shared.GL;
import com.willwinder.ugs.nbm.visualizer.shared.IRendererInputHandler;
import com.willwinder.ugs.nbm.visualizer.shared.DefualtRendererInputHandler;
import com.willwinder.ugs.nbm.visualizer.shared.AbstractRenderer;
import com.willwinder.ugs.nbm.visualizer.shared.GLDrawable;
import com.willwinder.ugs.nbm.visualizer.shared.Renderer;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.uielements.helpers.FPSCounter;
import com.willwinder.universalgcodesender.uielements.helpers.Overlay;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import javax.swing.JComponent;
import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.willwinder.ugs.nbp.lib.lookup.CentralLookup.getDefault;

/**
 * 3D Canvas for GCode Visualizer
 *
 * @author wwinder
 */
@SuppressWarnings("serial")
@ServiceProviders(value = {
        @ServiceProvider(service = JOGLGcodeRenderer.class)})
public class JOGLGcodeRenderer extends AbstractRenderer implements GLEventListener, Renderer {
    private static final Logger logger = Logger.getLogger(JOGLGcodeRenderer.class.getName());

    private final GLJPanel panel;
    private final FPSAnimator animator;
    private final DefualtRendererInputHandler rih;

    // GL Utility
    private GLU glu;

    private FPSCounter fpsCounter;
    private Overlay overlay;
    private final String dimensionsLabel = "";

    public JOGLGcodeRenderer() {
        this(getDefault().lookup(BackendAPI.class));
    }
    /**
     * Constructor.
     */
    public JOGLGcodeRenderer(BackendAPI backend) {
        super();

        setVerticalTranslationVector();
        setHorizontalTranslationVector();

        reloadPreferences();
        listenForSettingsEvents();

        GLCapabilities glCaps = new GLCapabilities(null);
        panel = new GLJPanel(glCaps);
        animator = new FPSAnimator(panel, 15);

        this.rih = new DefualtRendererInputHandler(this, new JOGLAnimator(animator), backend);

        // key listener...
        panel.addKeyListener(this.rih);

        // mouse wheel...
        panel.addMouseWheelListener(this.rih);

        // mouse motion...
        panel.addMouseMotionListener(this.rih);

        // mouse...
        panel.addMouseListener(this.rih);

        panel.addGLEventListener(this);
    }

    @Override
    public IRendererInputHandler getRendererInputHandler() {
        return rih;
    }

    @Override
    public JComponent getPanel() {
        return panel;
    }


    // ------ Implement methods declared in GLEventListener ------

    /**
     * Called back immediately after the OpenGL context is initialized. Can be used
     * to perform one-time initialization. Run only once.
     * GLEventListener method.
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        logger.log(Level.INFO, "Initializing OpenGL context.");
        // TODO: Figure out scale factor / dimensions label based on GcodeRenderer
        /*
            this.scaleFactorBase = VisualizerUtils.findScaleFactor(this.xSize, this.ySize, this.objectMin, this.objectMax);
            this.scaleFactor = this.scaleFactorBase * this.zoomMultiplier;

            double objectWidth = this.objectMax.x-this.objectMin.x;
            double objectHeight = this.objectMax.y-this.objectMin.y;
            this.dimensionsLabel = Localization.getString("VisualizerCanvas.dimensions") + ": " 
                    + Localization.getString("VisualizerCanvas.width") + "=" + format.format(objectWidth) + " " 
                    + Localization.getString("VisualizerCanvas.height") + "=" + format.format(objectHeight);

        */

        this.fpsCounter = new FPSCounter(drawable, new Font("SansSerif", Font.BOLD, 12));
        this.overlay = new Overlay(drawable, new Font("SansSerif", Font.BOLD, 12));
        this.overlay.setColor(127, 127, 127, 100);
        this.overlay.setTextLocation(Overlay.LOWER_LEFT);

        // Parse random gcode file and generate something to draw.
        GLDrawable glDrawable = new JOGLDrawable(drawable);      // get the OpenGL graphics context
        init(glDrawable);
    }

    /**
     * Call-back handler for window re-size event. Also called when the drawable is
     * first set to visible.
     * GLEventListener method.
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //logger.log(Level.INFO, "Reshaping OpenGL context.");
        this.xSize = width;
        this.ySize = height;

        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, xSize, ySize);

        resizeForCamera(objectMin, objectMax, 0.9);
    }

    /**
     * Called back by the animator to perform rendering.
     * GLEventListener method.
     */
    @Override
    public void display(GLAutoDrawable d) {
        GLDrawable drawable = new JOGLDrawable(d);

        draw(drawable);

        this.fpsCounter.draw();
        this.overlay.draw(this.dimensionsLabel);

        drawable.getGL().glLoadIdentity();
        update();
    }

    /**
     * Called after each render.
     */
    private void update() {
        if (debugCoordinates) {
            System.out.println("Machine coordinates: " + this.machineCoord.toString());
            System.out.println("Work coordinates: " + this.workCoord.toString());
            System.out.println("-----------------");
        }
    }

    /**
     * Called back before the OpenGL context is destroyed.
     * Release resource such as buffers.
     * GLEventListener method.
     */
    @Override
    synchronized public void dispose(GLAutoDrawable drawable) {
        logger.log(Level.INFO, "Disposing OpenGL context.");
    }

    @Override
    public void destroy() {
        panel.destroy();
    }
}
