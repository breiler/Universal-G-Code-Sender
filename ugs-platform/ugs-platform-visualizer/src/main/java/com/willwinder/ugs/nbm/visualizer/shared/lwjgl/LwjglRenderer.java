package com.willwinder.ugs.nbm.visualizer.shared.lwjgl;

import com.jogamp.opengl.GL2;
import com.willwinder.ugs.nbm.visualizer.shared.AbstractRenderer;
import com.willwinder.ugs.nbm.visualizer.shared.DefualtRendererInputHandler;
import com.willwinder.ugs.nbm.visualizer.shared.GL;
import com.willwinder.ugs.nbm.visualizer.shared.IRendererInputHandler;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.universalgcodesender.model.BackendAPI;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glVertex2f;
import static org.lwjgl.opengl.GL11.glViewport;

@ServiceProviders(value = {
        @ServiceProvider(service = LwjglRenderer.class)})
public class LwjglRenderer extends AbstractRenderer {
    private final JPanel panel;
    private final AWTGLCanvas canvas;
    private LwjglDrawable drawable;

    DefualtRendererInputHandler rih;

    public LwjglRenderer() {
        setVerticalTranslationVector();
        setHorizontalTranslationVector();
        reloadPreferences();
        listenForSettingsEvents();

        GLData glData = new GLData();
        glData.samples = 10;
        glData.sampleBuffers = 4;
        glData.accumAlphaSize = 4;
        canvas = new AWTGLCanvas(glData) {
            public void initGL() {
                drawable = new LwjglDrawable();
                System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
                createCapabilities();
                glClearColor(0.3f, 0.4f, 0.5f, 1);

                init(drawable);
                //setupPerpective(getWidth(), getHeight(), drawable, ortho);
            }

            public void paintGL() {
                xSize = getWidth();
                ySize = getWidth();
                resizeForCamera(objectMin, objectMax, 0.9);
                setupPerpective(getWidth(), getHeight(), drawable, ortho);
                drawable.getGL().glViewport(0, 0, getWidth(), getHeight());
                /*int w = getWidth();
                int h = getHeight();
                float aspect = (float) w / h;
                double now = System.currentTimeMillis() * 0.001;
                float width = (float) Math.abs(Math.sin(now * 0.3));
                glClear(GL_COLOR_BUFFER_BIT);
                glViewport(0, 0, w, h);
                glBegin(GL_QUADS);
                glColor3f(0.4f, 0.6f, 0.8f);
                glVertex2f(-0.75f * width / aspect, 0.0f);
                glVertex2f(0, -0.75f);
                glVertex2f(+0.75f * width/ aspect, 0);
                glVertex2f(0, +0.75f);
                glEnd();*/

                draw(drawable);

                swapBuffers();
            }
        };
        panel = new JPanel(new BorderLayout()) {
            @Override
            public void reshape(int x, int y, int w, int h) {
                super.reshape(x,y, w, h);
              /*  if(drawable == null ) {
                    return;
                }

                GL gl = drawable.getGL();  // get the OpenGL 2 graphics context
                //logger.log(Level.INFO, "Reshaping OpenGL context.");
                xSize = w;
                ySize = h;

                // Set the view port (display area) to cover the entire window
                gl.glViewport(0, 0, xSize, ySize);
                resizeForCamera(objectMin, objectMax, 0.9);*/
            }
        };

        panel.add(canvas, BorderLayout.CENTER);
        rih = new DefualtRendererInputHandler(this, new LwjglAnimator(), CentralLookup.getDefault().lookup(BackendAPI.class));

        // key listener...
        canvas.addKeyListener(rih);

        // mouse wheel...
        canvas.addMouseWheelListener(rih);

        // mouse motion...
        canvas.addMouseMotionListener(rih);

        // mouse...
        canvas.addMouseListener(rih);

        Runnable renderLoop = new Runnable() {
            public void run() {
                if (!canvas.isValid()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    canvas.render();
                }
                SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);

    }

    @Override
    public IRendererInputHandler getRendererInputHandler() {
        return rih;
    }

    @Override
    public JComponent getPanel() {
        return panel;
    }

    @Override
    public void destroy() {
        canvas.disposeCanvas();
        drawable = null;
    }
}
