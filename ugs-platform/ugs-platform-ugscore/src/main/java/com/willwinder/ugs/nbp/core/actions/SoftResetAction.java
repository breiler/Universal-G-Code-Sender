/*
    Copyright 2015-2024 Will Winder


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
package com.willwinder.ugs.nbp.core.actions;

import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

@com.willwinder.universalgcodesender.actions.Action(
        icon = SoftResetAction.ICON_BASE
)
@ActionID(
        category = LocalizingService.SoftResetCategory,
        id = LocalizingService.SoftResetActionId)
@ActionRegistration(
        iconBase = SoftResetAction.ICON_BASE,
        displayName = "resources/MessagesBundle#" + LocalizingService.SoftResetTitleKey,
        lazy = false)
@ActionReferences({
        @ActionReference(
                path = "Toolbars/Machine Actions",
                position = 982),
        @ActionReference(
                path = LocalizingService.SoftResetWindowPath,
                position = 1028)
})
public final class SoftResetAction extends AbstractAction implements UGSEventListener {

    public static final String ICON_BASE = "resources/icons/reset.svg";

    private final BackendAPI backend;

    public SoftResetAction() {
        this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        this.backend.addUGSEventListener(this);

        putValue("iconBase", ICON_BASE);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
        putValue("menuText", LocalizingService.SoftResetTitle);
        putValue(NAME, LocalizingService.SoftResetTitle);
        putValue(Action.SHORT_DESCRIPTION, Localization.getString("platform.actions.softreset.tooltip"));
        setEnabled(isEnabled());
    }

    @Override
    public void UGSEvent(UGSEvent cse) {
        if (cse instanceof ControllerStateEvent) {
            EventQueue.invokeLater(() -> setEnabled(isEnabled()));
        }
    }

    @Override
    public boolean isEnabled() {
        return backend.getControllerState() == ControllerState.IDLE ||
                backend.getControllerState() == ControllerState.HOLD ||
                backend.getControllerState() == ControllerState.CHECK ||
                backend.getControllerState() == ControllerState.ALARM ||
                backend.getControllerState() == ControllerState.SLEEP;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            backend.issueSoftReset();
        } catch (Exception ex) {
            GUIHelpers.displayErrorDialog(ex.getLocalizedMessage());
        }
    }
}
