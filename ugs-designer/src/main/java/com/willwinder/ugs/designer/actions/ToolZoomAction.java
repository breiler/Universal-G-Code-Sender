/*
    Copyright 2021 Will Winder

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

import com.willwinder.ugs.designer.logic.ControllerFactory;
import com.willwinder.ugs.designer.logic.Tool;
import com.willwinder.ugs.designer.utils.SvgLoader;

import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

/**
 * @author Joacim Breiler
 */
public class ToolZoomAction extends AbstractAction {
    public static final String ICON_SMALL_PATH = "img/zoom.svg";
    private static final String ICON_LARGE_PATH = "img/zoom24.svg";

    public ToolZoomAction() {
        putValue("iconBase", ICON_SMALL_PATH);
        putValue(SMALL_ICON, SvgLoader.loadImageIcon(ICON_SMALL_PATH, 16).orElse(null));
        putValue(LARGE_ICON_KEY, SvgLoader.loadImageIcon(ICON_LARGE_PATH, 24).orElse(null));
        putValue("menuText", "Zoom");
        putValue(NAME, "Zoom");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ControllerFactory.getController().setTool(Tool.ZOOM);
    }
}
