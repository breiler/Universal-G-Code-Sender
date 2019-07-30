/*
    Copyright 2016 Will Winder

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
package com.willwinder.ugs.nbp.core.control;

import com.google.common.base.Strings;
import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.ActionRegistrationService;
import com.willwinder.universalgcodesender.MacroHelper;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.types.Macro;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import com.willwinder.universalgcodesender.utils.Settings;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author wwinder
 */
@ServiceProvider(service=MacroService.class) 
public final class MacroService {
    public MacroService() {
        reInitActions();
    }

    public void reInitActions() {
        String menuPath = "Menu/Machine/Macros";
        String actionCategory = "Macro";
        String localCategory = Localization.getString("platform.menu.macros");
        String localized = String.format("Menu/%s/%s",
                Localization.getString("platform.menu.machine"),
                Localization.getString("platform.menu.macros"));

        try {
            FileObject root= FileUtil.getConfigRoot(); 

            // Clear out the menu items.
            FileUtil.createFolder(root, menuPath).delete(); 
            FileUtil.createFolder(root, menuPath); 

            String actionPath = "/Actions/" + actionCategory;
            FileUtil.createFolder(root, actionPath).delete();
            //FileObject actionsObject = FileUtil.createFolder(root, actionPath);

            ActionRegistrationService ars =  Lookup.getDefault().lookup(ActionRegistrationService.class);
            BackendAPI backend = CentralLookup.getDefault().lookup(BackendAPI.class);
            Settings settings = backend.getSettings();

            int numMacros = settings.getNumMacros();
            for (int i = 0; i < numMacros; i++) {
                Macro m = settings.getMacro(i);

                String text;
                if (Strings.isNullOrEmpty(m.getNameAndDescription())){
                    text = Integer.toString(i);
                } else {
                    text = m.getNameAndDescription();
                }

                ars.registerAction(MacroAction.class.getCanonicalName() + "." + m.getName(), text, actionCategory, null, menuPath, localized, new MacroAction(settings, backend, i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected class MacroAction extends AbstractAction {
        private BackendAPI backend;
        private Settings settings;
        private int macroIdx;

        public MacroAction(Settings s, BackendAPI b, int macro) {
            backend = b;
            settings = s;
            macroIdx = macro;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Macro macro = settings.getMacro(macroIdx);
            if (macro != null && macro.getGcode() != null) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MacroHelper.executeCustomGcode(macro.getGcode(), backend);
                        } catch (Exception ex) {
                            GUIHelpers.displayErrorDialog(ex.getMessage());
                            Exceptions.printStackTrace(ex);
                        }
                    }
                });
            }
        }

        @Override
        public boolean isEnabled() {
            return backend.isConnected() && backend.isIdle();
        }
    }
}
