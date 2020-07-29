#set($symbol_pound='#')
#set($symbol_dollar='$')
#set($symbol_escape='\')
/*
    Copyright 2017-2020 Will Winder

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
package ${package};

import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.ugs.nbp.lib.services.LocalizingService;
import com.willwinder.universalgcodesender.model.BackendAPI;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "${artifactId}",
        iconBase = "${packageInPathFormat}/plugin.svg",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(
        mode = "output",
        openAtStartup = false)
@ActionID(
        category = MyTopComponent.CATEGORY,
        id = MyTopComponent.ACTION_ID)
@ActionReference(path = LocalizingService.MENU_WINDOW_PLUGIN)
@TopComponent.OpenActionRegistration(
        displayName = "MyTopComponent",
        preferredID = "MyTopComponent"
)
public final class MyTopComponent extends TopComponent {
    public final static String TITLE = "MyTopComponent";
    public final static String TOOLTIP = "MyTopComponent tooltip";
    public final static String ACTION_ID = "${package}.${artifactId}.MyTopComponent";
    public final static String CATEGORY = LocalizingService.CATEGORY_WINDOW;

    private final BackendAPI backend;

    public MyTopComponent() {
        setName(MyTopComponent.TITLE);
        setToolTipText(MyTopComponent.TOOLTIP);

        backend = CentralLookup.getDefault().lookup(BackendAPI.class);

        setLayout(new BorderLayout());
        add(new JLabel("Hello ${artifactId}!"), BorderLayout.CENTER);
    }

    public void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
    }

    public void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
