/*
    Copyright 2015-2018 Will Winder


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
import com.willwinder.universalgcodesender.firmware.FirmwareSettingsException;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.listeners.ControllerState;
import com.willwinder.universalgcodesender.listeners.UGSEventListener;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.model.UGSEvent;
import com.willwinder.universalgcodesender.model.events.ControllerStateEvent;
import com.willwinder.universalgcodesender.model.events.FirmwareSettingEvent;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;

@com.willwinder.universalgcodesender.actions.Action(
        icon = HomingAction.ICON_BASE
)
@ActionID(
        category = LocalizingService.HomeCategory,
        id = LocalizingService.HomeActionId)
@ActionRegistration(
        iconBase = HomingAction.ICON_BASE,
        displayName = "resources/MessagesBundle#" + LocalizingService.HomeTitleKey,
        lazy = false)
@ActionReferences({
        @ActionReference(
                path = "Toolbars/Machine Actions",
                position = 980),
        @ActionReference(
                path = LocalizingService.HomeWindowPath,
                position = 1000)
})
public final class HomingAction extends AbstractAction implements UGSEventListener {

    public static final String ICON_BASE = "resources/icons/home.svg";

    private final BackendAPI backend;

    public HomingAction() {
        this.backend = CentralLookup.getDefault().lookup(BackendAPI.class);
        this.backend.addUGSEventListener(this);

        putValue("iconBase", ICON_BASE);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon(ICON_BASE, false));
        putValue("menuText", LocalizingService.HomeTitle);
        putValue(NAME, LocalizingService.HomeTitle);
        setEnabled(isEnabled());
        updateToolTip();
    }

    @Override
    public void UGSEvent(UGSEvent cse) {
        if (cse instanceof ControllerStateEvent || cse instanceof FirmwareSettingEvent) {
            EventQueue.invokeLater(() -> {
                updateToolTip();
                setEnabled(isEnabled());
            });
        }
    }

    @Override
    public boolean isEnabled() {
        return (backend.getControllerState() == ControllerState.IDLE || backend.getControllerState() == ControllerState.ALARM) &&
        isHomingEnabled();
    }

    private boolean isHomingEnabled() {
        boolean isHomingEnabled = false;
        try {
            isHomingEnabled = backend.getController() != null &&
                    backend.getController().getFirmwareSettings() != null &&
                    backend.getController().getFirmwareSettings().isHomingEnabled();
        } catch (FirmwareSettingsException ignored) {
            // Never mind
        }
        return isHomingEnabled;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            backend.performHomingCycle();
        } catch (Exception ex) {
            GUIHelpers.displayErrorDialog(ex.getLocalizedMessage());
        }
    }

    private void updateToolTip() {
            if (backend.getController() != null &&
                    backend.getController().getFirmwareSettings() != null &&
                    !isHomingEnabled()) {
                putValue(Action.SHORT_DESCRIPTION, Localization.getString("platform.actions.homing.disabled.tooltip"));
            } else {
                putValue(Action.SHORT_DESCRIPTION, Localization.getString("platform.actions.homing.enabled.tooltip"));
            }
    }
}
