package com.willwinder.ugs.designer.platform;

import com.willwinder.ugs.designer.Utils;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.services.LookupService;
import org.openide.loaders.DataObject;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

import javax.swing.SwingUtilities;

public class UgsFileChangeListener implements LookupListener, UGSEventListener {
    private final BackendAPI backendAPI;

    public UgsFileChangeListener() {
        backendAPI = LookupService.lookup(BackendAPI.class).orElseThrow();
        backendAPI.addUGSEventListener(this);

        Lookup.Result<UgsDataObject> lookupResult = Utilities.actionsGlobalContext().lookupResult(UgsDataObject.class);
        lookupResult.addLookupListener(this);

        DataObject.getRegistry().addChangeListener((e) -> resultChanged(null));
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(() -> {
            boolean isIdleOrDisconnected = (backendAPI.isConnected() && backendAPI.isIdle()) || !backendAPI.isConnected();
            boolean isFileLoaded = Utils.isStandalone() || TopComponent.getRegistry().getActivatedNodes().length > 0;

            if (isIdleOrDisconnected && isFileLoaded) {
                // TODO load file context
            } else {
                // TODO unload file context
            }
        });
    }

    @Override
    public void UGSEvent(UGSEvent event) {
        if (event instanceof ControllerStateEvent) {
            resultChanged(null);
        }
    }
}
