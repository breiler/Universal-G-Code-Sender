/*
    Copyright 2023 Will Winder

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
package com.willwinder.ugs.designer.actions;

import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.services.LookupService;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

/**
 * An abstract action class for design actions. This will listen if a connected machine is idle or
 * that a file has been loaded
 *
 * @author Joacim Breiler
 */
public abstract class AbstractDesignAction extends AbstractAction implements UGSEventListener {
    private final transient BackendAPI backendAPI;

    protected AbstractDesignAction() {
        super();
        backendAPI = LookupService.lookup(BackendAPI.class).orElseThrow();
        backendAPI.addUGSEventListener(this);

    }

    public void resultChanged(String fileContext) {
        SwingUtilities.invokeLater(() -> {
            boolean isIdleOrDisconnected = (backendAPI.isConnected() && backendAPI.isIdle()) || !backendAPI.isConnected();
            setEnabled(isIdleOrDisconnected);
            // TODO dispatch event
        });
    }

    @Override
    public void UGSEvent(UGSEvent event) {
        if (event instanceof ControllerStateEvent) {
            resultChanged(null);
        }
    }
}
