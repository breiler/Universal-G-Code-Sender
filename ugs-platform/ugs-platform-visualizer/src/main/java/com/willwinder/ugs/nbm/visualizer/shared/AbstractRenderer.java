package com.willwinder.ugs.nbm.visualizer.shared;

import com.jogamp.opengl.GL2;
import com.willwinder.ugs.nbm.visualizer.options.VisualizerOptions;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.Position;
import com.willwinder.universalgcodesender.model.UnitUtils;
import com.willwinder.universalgcodesender.model.events.SettingChangedEvent;
import com.willwinder.universalgcodesender.utils.Settings;
import com.willwinder.universalgcodesender.visualizer.VisualizerUtils;
import org.lwjgl.opengl.GL11;
import org.openide.util.Lookup;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.willwinder.ugs.nbm.visualizer.options.VisualizerOptions.VISUALIZER_OPTION_BG;
import static com.willwinder.ugs.nbp.lib.lookup.CentralLookup.getDefault;

public abstract class AbstractRenderer implements Renderer {
    private static final Logger LOGGER = Logger.getLogger(AbstractRenderer.class.getSimpleName());
    protected static boolean ortho = true;
    protected static boolean debugCoordinates = false; // turn on coordinate debug output
    // Machine data
    protected final Position machineCoord;
    protected final Position workCoord;
    protected final RenderableRegistrationService renderableRegistrationService;
    private final double maxZoomMultiplier = 50;
    // Movement
    private final int panMouseButton = InputEvent.BUTTON2_MASK; // TODO: Make configurable
    // const values until added to settings
    private final double minZoomMultiplier = .1;
    private final double zoomIncrement = 0.2;
    // Mouse rotation data
    protected Point mouseLastWindow;
    protected Position mouseWorldXY;
    protected Position rotation;
    // Preferences
    protected java.awt.Color clearColor;
    protected double panMultiplierY = 1;
    protected double panMultiplierX = 1;
    // Projection variables
    protected Position center;
    protected Position eye;
    protected Position objectMin;
    protected Position objectMax;
    protected int xSize;
    protected int ySize;
    // Scaling
    protected double scaleFactor = 1;
    protected double scaleFactorBase = 1;
    protected double zoomMultiplier = 1;
    protected boolean invertZoom = false;
    protected boolean idle = true;
    private Position translationVectorH;
    private Position translationVectorV;
    private Point mouseCurrentWindow;

    public AbstractRenderer() {
        rotation = new Position(0.0, -30.0, 0.0);
        machineCoord = new Position(0, 0, 0);
        workCoord = new Position(0, 0, 0);
        center = new Position(0, 0, 0);
        eye = new Position(0, 0, 1.5);
        objectMin = new Position(-10, -10, -10);
        objectMax = new Position(10, 10, 10);
        renderableRegistrationService = Lookup.getDefault().lookup(RenderableRegistrationService.class);
    }

    final public void reloadPreferences() {
        VisualizerOptions vo = new VisualizerOptions();

        clearColor = vo.getOptionForKey(VISUALIZER_OPTION_BG).value;

        for (Renderable r : renderableRegistrationService.getRenderables()) {
            r.reloadPreferences(vo);
        }
    }

    /**
     * Zoom the visualizer to the given region.
     */
    public void zoomToRegion(Position min, Position max, double bufferFactor) {
        if (min == null || max == null) return;

        if (this.ySize == 0) {
            this.ySize = 1;
        }  // prevent divide by zero

        // Figure out offset compared to the current center.
        Position regionCenter = VisualizerUtils.findCenter(min, max);
        this.eye.x = regionCenter.x - this.center.x;
        this.eye.y = regionCenter.y - this.center.y;

        // Figure out what the scale factors would be if we reset this object.
        double _scaleFactorBase = VisualizerUtils.findScaleFactor(this.xSize, this.ySize, min, max, bufferFactor);
        double _scaleFactor = _scaleFactorBase * this.zoomMultiplier;

        // Calculate the zoomMultiplier needed to get to that scale, and set it.
        this.zoomMultiplier = _scaleFactor / this.scaleFactorBase;
        this.scaleFactor = this.scaleFactorBase * this.zoomMultiplier;
    }

    protected void setHorizontalTranslationVector() {
        double x = Math.cos(Math.toRadians(this.rotation.x));
        double xz = Math.sin(Math.toRadians(this.rotation.x));

        double y = xz * Math.sin(Math.toRadians(this.rotation.y));
        double yz = xz * Math.cos(Math.toRadians(this.rotation.y));

        translationVectorH = new Position(x, y, yz);
        translationVectorH.normalizeXYZ();
    }

    protected void setVerticalTranslationVector() {
        double y = Math.cos(Math.toRadians(this.rotation.y));
        double yz = Math.sin(Math.toRadians(this.rotation.y));

        translationVectorV = new Position(0, y, yz);
        translationVectorV.normalizeXYZ();
    }

    public void mouseMoved(Point lastPoint) {
        mouseLastWindow = lastPoint;
    }

    public void mouseRotate(Point point) {
        this.mouseCurrentWindow = point;
        if (this.mouseLastWindow != null) {
            int dx = this.mouseCurrentWindow.x - this.mouseLastWindow.x;
            int dy = this.mouseCurrentWindow.y - this.mouseLastWindow.y;

            rotation.x += dx / 2.0;
            rotation.y = Math.min(0, Math.max(-180, this.rotation.y += dy / 2.0));

            if (ortho) {
                setHorizontalTranslationVector();
                setVerticalTranslationVector();
            }
        }

        // Now that the motion has been accumulated, reset last.
        this.mouseLastWindow = this.mouseCurrentWindow;
    }

    public void mousePan(Point point) {
        this.mouseCurrentWindow = point;
        int dx = this.mouseCurrentWindow.x - this.mouseLastWindow.x;
        int dy = this.mouseCurrentWindow.y - this.mouseLastWindow.y;
        pan(dx, dy);
    }

    public void pan(int dx, int dy) {
        if (ortho) {
            // Treat dx and dy as vectors relative to the rotation angle.
            this.eye.x -= ((dx * this.translationVectorH.x * this.panMultiplierX) + (dy * this.translationVectorV.x * panMultiplierY));
            this.eye.y += ((dy * this.translationVectorV.y * panMultiplierY) - (dx * this.translationVectorH.y * this.panMultiplierX));
            this.eye.z -= ((dx * this.translationVectorH.z * this.panMultiplierX) + (dy * this.translationVectorV.z * panMultiplierY));
        } else {
            this.eye.x += dx;
            this.eye.y += dy;
        }

        // Now that the motion has been accumulated, reset last.
        this.mouseLastWindow = this.mouseCurrentWindow;
    }

    public void zoom(int delta) {
        if (delta == 0)
            return;

        if (delta > 0) {
            if (this.invertZoom)
                zoomOut(delta);
            else
                zoomIn(delta);
        } else if (delta < 0) {
            if (this.invertZoom)
                zoomIn(delta * -1);
            else
                zoomOut(delta * -1);
        }
    }

    private void zoomOut(int increments) {
        if (ortho) {
            if (this.zoomMultiplier <= this.minZoomMultiplier)
                return;

            this.zoomMultiplier -= increments * zoomIncrement;
            if (this.zoomMultiplier < this.minZoomMultiplier)
                this.zoomMultiplier = this.minZoomMultiplier;

            this.scaleFactor = this.scaleFactorBase * this.zoomMultiplier;
        } else {
            this.eye.z += increments;
        }
    }

    private void zoomIn(int increments) {
        if (ortho) {
            if (this.zoomMultiplier >= this.maxZoomMultiplier)
                return;

            this.zoomMultiplier += increments * zoomIncrement;
            if (this.zoomMultiplier > this.maxZoomMultiplier)
                this.zoomMultiplier = this.maxZoomMultiplier;

            this.scaleFactor = this.scaleFactorBase * this.zoomMultiplier;
        } else {
            this.eye.z -= increments;
        }
    }

    /**
     * Reset the view angle and zoom.
     */
    public void resetView() {
        moveCamera(new Position(0, 0, 1.5), new Position(0, -30, 0), 1);
    }

    /**
     * Moves the camera to a position and rotation
     *
     * @param position to the given position
     * @param rotation directs the camera given this rotation
     * @param zoom     the zoom level
     */
    public void moveCamera(Position position, Position rotation, double zoom) {
        this.zoomMultiplier = Math.min(Math.max(zoom, minZoomMultiplier), maxZoomMultiplier);
        this.scaleFactor = this.scaleFactorBase;
        this.eye = new Position(position);
        this.rotation = new Position(rotation);
    }

    /**
     * Get the location on the XY plane of the mouse.
     */
    public Position getMouseWorldLocation() {
        return this.mouseWorldXY;
    }

    public void setWorkCoordinate(Position p) {
        if (p != null) {
            this.workCoord.set(p.getPositionIn(UnitUtils.Units.MM));
        }
    }

    public void setMachineCoordinate(Position p) {
        if (p != null) {
            this.machineCoord.set(p.getPositionIn(UnitUtils.Units.MM));
        }
    }

    /**
     * Zoom to display the given region leaving the suggested buffer.
     */
    protected void resizeForCamera(Position min, Position max, double bufferFactor) {
        if (min == null || max == null) return;

        if (this.ySize == 0) {
            this.ySize = 1;
        }  // prevent divide by zero

        this.center = VisualizerUtils.findCenter(min, max);
        this.scaleFactorBase = VisualizerUtils.findScaleFactor(this.xSize, this.ySize, min, max, bufferFactor);
        this.scaleFactor = this.scaleFactorBase * this.zoomMultiplier;
        this.panMultiplierX = VisualizerUtils.getRelativeMovementMultiplier(min.x, max.x, this.xSize);
        this.panMultiplierY = VisualizerUtils.getRelativeMovementMultiplier(min.y, max.y, this.ySize);
    }

    public void setObjectSize(Position min, Position max) {
        if (min == null || max == null) {
            this.objectMin = new Position(-10, -10, -10);
            this.objectMax = new Position(10, 10, 10);
            idle = true;
        } else {
            this.objectMin = min;
            this.objectMax = max;
            idle = false;
        }
        resizeForCamera(objectMin, objectMax, 0.9);
    }

    protected void init(GLDrawable glDrawable) {
        GL gl = glDrawable.getGL();
        gl.glShadeModel(GL2.GL_SMOOTH); // blends colors nicely, and smoothes out lighting
        gl.glClearColor(clearColor.getRed() / 255f, clearColor.getGreen() / 255f, clearColor.getBlue() / 255f, clearColor.getAlpha() / 255f);
        gl.glClearDepth(1.0f);      // set clear depth value to farthest
        gl.glEnable(GL2.GL_BLEND);
        gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_DEPTH_TEST);
        gl.glDepthFunc(GL2.GL_LEQUAL);  // the type of depth test to do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // best perspective correction

        /*
        gl.glLoadIdentity();
        float[] lmodel_ambient = { 0.5f, 0.5f, 0.5f, 1.0f };
        gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, lmodel_ambient, 0);
        */

        // init lighting
        float[] ambient = {.6f, .6f, .6f, 1.f};
        float[] diffuse = {.6f, .6f, .6f, 1.0f};
        float[] position = {0f, 0f, 20f, 1.0f};

        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambient, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuse, 0);
        gl.glEnable(GL2.GL_LIGHT0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, position, 0);

        // Allow glColor to set colors
        gl.glEnable(GL2.GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_DIFFUSE);
        gl.glColorMaterial(GL2.GL_FRONT, GL2.GL_AMBIENT);
        //gl.glColorMaterial(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);
        //gl.glColorMaterial(GL.GL_FRONT, GL2.GL_SPECULAR);


        float diffuseMaterial[] =
                {0.5f, 0.5f, 0.5f, 1.0f};

        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, diffuseMaterial, 0);
        //gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
        //gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 25.0f);

        //gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL2.GL_AMBIENT_AND_DIFFUSE);


        gl.glEnable(GL2.GL_LIGHTING);
        for (Renderable r : renderableRegistrationService.getRenderables()) {
            r.init(glDrawable);
        }
    }

    /**
     * Setup the perspective matrix.
     */
    protected void setupPerpective(int x, int y, GLDrawable drawable, boolean ortho) {
        final GL gl = drawable.getGL();
        float aspectRatio = (float) x / y;

        gl.glMatrixMode(GL11.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(-0.60 * aspectRatio / scaleFactor, 0.60 * aspectRatio / scaleFactor, -0.60 / scaleFactor, 0.60 / scaleFactor,
                -10 / scaleFactor, 10 / scaleFactor);
        gl.glMatrixMode(GL11.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    protected void draw(GLDrawable drawable) {

        final GL gl = drawable.getGL();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        // Update normals when an object is scaled
        gl.glEnable(GL2.GL_NORMALIZE);

        // Setup the current matrix so that the projection can be done.
        if (mouseLastWindow != null) {
            gl.glPushMatrix();
            gl.glRotated(this.rotation.x, 0.0, 1.0, 0.0);
            gl.glRotated(this.rotation.y, 1.0, 0.0, 0.0);
            gl.glTranslated(-this.eye.x - this.center.x, -this.eye.y - this.center.y, -this.eye.z - this.center.z);
            this.mouseWorldXY = MouseProjectionUtils.intersectPointWithXYPlane(
                    drawable, mouseLastWindow.x, mouseLastWindow.y);
            gl.glPopMatrix();
        } else {
            this.mouseWorldXY = new Position(0, 0, 0);
        }

        // Render the different parts of the scene.
        for (Renderable r : renderableRegistrationService.getRenderables()) {
            try {
                // Don't draw disabled renderables.
                if (!r.isEnabled()) continue;

                gl.glPushMatrix();
                // in case a renderable sets the color, set it back to gray and opaque.
                gl.glColor4f(0.5f, 0.5f, 0.5f, 1f);

                if (r.rotate()) {
                    gl.glRotated(this.rotation.x, 0.0, 1.0, 0.0);
                    gl.glRotated(this.rotation.y, 1.0, 0.0, 0.0);
                }
                if (r.center()) {
                    gl.glTranslated(-this.eye.x - this.center.x, -this.eye.y - this.center.y, -this.eye.z - this.center.z);
                }

                if (!r.enableLighting()) {
                    gl.glDisable(GL2.GL_LIGHTING);
                }
                try {
                    r.draw(drawable, idle, machineCoord, workCoord, objectMin, objectMax, scaleFactor, getMouseWorldLocation(), rotation);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "An exception occurred while drawing " + r.getClass().getSimpleName(), e);
                }
                if (!r.enableLighting()) {
                    gl.glEnable(GL2.GL_LIGHTING);
                    gl.glEnable(GL2.GL_LIGHT0);
                }
                gl.glPopMatrix();
            } catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Could not render " + r.getTitle() + ", disabling", e);
                r.setEnabled(false);
            }
        }
    }

    protected void listenForSettingsEvents() {
        BackendAPI backendAPI = getDefault().lookup(BackendAPI.class);
        Settings settings = backendAPI.getSettings();
        invertZoom = settings.isInvertMouseZoom();

        backendAPI.addUGSEventListener(event -> {
            if (event instanceof SettingChangedEvent) {
                invertZoom = backendAPI.getSettings().isInvertMouseZoom();
            }
        });
    }
}
