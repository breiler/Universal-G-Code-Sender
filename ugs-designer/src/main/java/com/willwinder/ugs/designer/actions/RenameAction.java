/*
    Copyright 2025 Damian Nikodem

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

import com.willwinder.ugs.designer.entities.selection.SelectionEvent;
import com.willwinder.ugs.designer.entities.selection.SelectionListener;
import com.willwinder.ugs.designer.entities.selection.SelectionManager;
import com.willwinder.ugs.designer.gui.tree.EntitiesTreeController;
import com.willwinder.ugs.designer.logic.ControllerFactory;
import com.willwinder.ugs.designer.utils.SvgLoader;
import com.willwinder.universalgcodesender.services.LookupService;

import java.awt.event.ActionEvent;

public class RenameAction extends AbstractDesignAction implements SelectionListener {
    public static final String SMALL_ICON_PATH = "img/text.svg";
    private static final String LARGE_ICON_PATH = "img/text.svg";

    public RenameAction() {
        putValue("menuText", "Rename");
        putValue(NAME, "Rename");
        putValue("iconBase", SMALL_ICON_PATH);
        putValue(SMALL_ICON, SvgLoader.loadImageIcon(SMALL_ICON_PATH, 16).orElse(null));
        putValue(LARGE_ICON_KEY, SvgLoader.loadImageIcon(SMALL_ICON_PATH, 24).orElse(null));

        registerSelectionListener();
    }

    private void registerSelectionListener() {
        SelectionManager selectionManager = ControllerFactory.getController().getSelectionManager();
        selectionManager.addSelectionListener(this);
        setEnabled(selectionManager.getChildren().size() == 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        EntitiesTreeController entitiesTreeController = LookupService.lookup(EntitiesTreeController.class).orElseThrow();
        entitiesTreeController.renameSelectedNode();
    }

    @Override
    public void onSelectionEvent(SelectionEvent selectionEvent) {
        SelectionManager selectionManager = ControllerFactory.getSelectionManager();
        setEnabled(!selectionManager.getSelection().isEmpty());
    }
}
