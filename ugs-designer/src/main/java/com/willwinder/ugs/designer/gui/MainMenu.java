/*
    Copyright 2021-2023 Will Winder

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
package com.willwinder.ugs.designer.gui;

import com.willwinder.ugs.designer.actions.ClearSelectionAction;
import com.willwinder.ugs.designer.actions.DeleteAction;
import com.willwinder.ugs.designer.actions.ExportGcodeAction;
import com.willwinder.ugs.designer.actions.ExportPngAction;
import com.willwinder.ugs.designer.actions.FlipHorizontallyAction;
import com.willwinder.ugs.designer.actions.FlipVerticallyAction;
import com.willwinder.ugs.designer.actions.NewAction;
import com.willwinder.ugs.designer.actions.OpenAction;
import com.willwinder.ugs.designer.actions.PrintDesignAction;
import com.willwinder.ugs.designer.actions.QuitAction;
import com.willwinder.ugs.designer.actions.RedoAction;
import com.willwinder.ugs.designer.actions.SaveAction;
import com.willwinder.ugs.designer.actions.SelectAllAction;
import com.willwinder.ugs.designer.actions.SelectNextAction;
import com.willwinder.ugs.designer.actions.SelectPreviousAction;
import com.willwinder.ugs.designer.actions.ToggleHidden;
import com.willwinder.ugs.designer.actions.UndoAction;
import com.willwinder.ugs.designer.logic.Controller;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

/**
 * Represents a main menu for a DrawGUI
 *
 * @author Alex Lagerstedt
 * @author Joacim Breiler
 */
public class MainMenu extends JMenuBar {

    private static final long serialVersionUID = 0;

    public MainMenu(Controller controller) {
        JMenuItem newdrawing = new JMenuItem(new NewAction());
        JMenuItem open = new JMenuItem(new OpenAction());
        JMenuItem save = new JMenuItem(new SaveAction(controller));
        JMenuItem export = new JMenuItem(new ExportPngAction(controller));
        JMenuItem exportGcode = new JMenuItem(new ExportGcodeAction());
        JMenuItem printDesign = new JMenuItem(new PrintDesignAction());
        JMenuItem quit = new JMenuItem(new QuitAction());

        JMenuItem undo = new JMenuItem(new UndoAction());
        JMenuItem redo = new JMenuItem(new RedoAction());

        //JMenuItem copy = new JMenuItem(new CopyAction());
        //JMenuItem paste = new JMenuItem(new PasteAction());

        JMenuItem flipHorizontal = new JMenuItem(new FlipHorizontallyAction());
        JMenuItem flipVertical = new JMenuItem(new FlipVerticallyAction());

        JMenuItem all = new JMenuItem(new SelectAllAction());
        JMenuItem previous = new JMenuItem(new SelectPreviousAction());
        JMenuItem next = new JMenuItem(new SelectNextAction());
        JMenuItem clear = new JMenuItem(new ClearSelectionAction());
        JMenuItem delete = new JMenuItem(new DeleteAction());
        JMenuItem toggleHidden = new JMenuItem(new ToggleHidden());

        JMenu fileMenu = new JMenu("File");
        fileMenu.add(newdrawing);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.addSeparator();
        fileMenu.add(export);
        fileMenu.add(exportGcode);
        fileMenu.addSeparator();
        fileMenu.add(printDesign);
        fileMenu.addSeparator();
        fileMenu.add(quit);

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(undo);
        editMenu.add(redo);
        editMenu.add(new JSeparator());
        editMenu.add(flipHorizontal);
        editMenu.add(flipVertical);
        editMenu.add(new JSeparator());
        editMenu.add(all);
        editMenu.add(previous);
        editMenu.add(next);
        editMenu.add(clear);
        editMenu.add(delete);
        editMenu.add(new JSeparator());
        editMenu.add(toggleHidden);

        add(fileMenu);
        add(editMenu);
    }
}
