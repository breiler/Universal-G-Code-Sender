package com.willwinder.ugs.nbm.visualizer.shared;

import com.willwinder.ugs.nbm.visualizer.renderables.Grid;
import com.willwinder.ugs.nbm.visualizer.renderables.MachineBoundries;
import com.willwinder.ugs.nbm.visualizer.renderables.MouseOver;
import com.willwinder.ugs.nbm.visualizer.renderables.OrientationCube;
import com.willwinder.ugs.nbm.visualizer.renderables.Tool;
import com.willwinder.universalgcodesender.i18n.Localization;
import org.openide.util.lookup.ServiceProvider;
import org.openide.util.lookup.ServiceProviders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@ServiceProviders(value = {
        @ServiceProvider(service = RenderableRegistrationService.class)})
public class DefaultRenderableRegistationService implements RenderableRegistrationService {

    private final ArrayList<Renderable> objects;

    public DefaultRenderableRegistationService() {
        objects = new ArrayList<>();
        objects.add(new MachineBoundries(Localization.getString("platform.visualizer.renderable.machine-boundries")));
        objects.add(new Tool(Localization.getString("platform.visualizer.renderable.tool-location")));
        objects.add(new MouseOver(Localization.getString("platform.visualizer.renderable.mouse-indicator")));
        objects.add(new OrientationCube(0.5f, Localization.getString("platform.visualizer.renderable.orientation-cube")));
        objects.add(new Grid(Localization.getString("platform.visualizer.renderable.grid")));
        Collections.sort(objects);
    }

    @Override
    public final Collection<Renderable> getRenderables() {
        return objects;
    }

    @Override
    public void registerRenderable(Renderable r) {
        if (r == null) return;
        if (!objects.contains(r)) {
            objects.add(r);
            Collections.sort(objects);
        }
    }

    @Override
    public void removeRenderable(Renderable r) {
        if (r == null) return;
        if (objects.contains(r)) {
            objects.remove(r);
            Collections.sort(objects);
        }
    }
}
