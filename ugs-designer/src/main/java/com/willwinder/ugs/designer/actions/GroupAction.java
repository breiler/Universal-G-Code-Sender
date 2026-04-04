package com.willwinder.ugs.designer.actions;

import com.willwinder.ugs.designer.entities.Entity;
import com.willwinder.ugs.designer.entities.EntityGroup;
import com.willwinder.ugs.designer.entities.cuttable.Group;
import com.willwinder.ugs.designer.entities.selection.SelectionEvent;
import com.willwinder.ugs.designer.entities.selection.SelectionListener;
import com.willwinder.ugs.designer.entities.selection.SelectionManager;
import com.willwinder.ugs.designer.logic.Controller;
import com.willwinder.ugs.designer.logic.ControllerFactory;
import com.willwinder.ugs.designer.utils.SvgLoader;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GroupAction extends AbstractDesignAction implements SelectionListener {
    public static final String SMALL_ICON_PATH = "img/folder.svg";
    private static final String LARGE_ICON_PATH = "img/folder24.svg";

    public GroupAction() {
        putValue("menuText", "Group entities");
        putValue(NAME, "Group entities");
        putValue("iconBase", SMALL_ICON_PATH);
        putValue(SMALL_ICON, SvgLoader.loadImageIcon(SMALL_ICON_PATH, 16).orElse(null));
        putValue(LARGE_ICON_KEY, SvgLoader.loadImageIcon(SMALL_ICON_PATH, 24).orElse(null));

        registerSelectionListener();
    }

    private void registerSelectionListener() {
        SelectionManager selectionManager = ControllerFactory.getController().getSelectionManager();
        selectionManager.addSelectionListener(this);
        setEnabled(!selectionManager.getChildren().isEmpty());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Controller controller = ControllerFactory.getController();
        UndoableGroupAction action = new UndoableGroupAction(controller.getSelectionManager().getSelection());
        controller.getUndoManager().addAction(action);
        action.redo();
    }

    @Override
    public void onSelectionEvent(SelectionEvent selectionEvent) {
        SelectionManager selectionManager = ControllerFactory.getSelectionManager();
        setEnabled(!selectionManager.getSelection().isEmpty());
    }

    private static class UndoableGroupAction implements UndoableAction {
        private final List<Entity> entities;
        private Group group;

        public UndoableGroupAction(List<Entity> entities) {
            this.entities = entities;
        }

        @Override
        public void redo() {
            Controller controller = ControllerFactory.getController();
            Optional<EntityGroup> parent = controller.getDrawing().getRootEntity().findParentFor(entities.get(0));

            controller.getSelectionManager().clearSelection();
            controller.getDrawing().removeEntities(entities);

            group = new Group();
            group.addAll(entities);
            if (parent.isPresent()) {
                parent.get().addChild(group);
            } else {
                controller.getDrawing().insertEntity(group);
            }
            controller.getSelectionManager().setSelection(Collections.singletonList(group));
        }

        @Override
        public void undo() {
            Controller controller = ControllerFactory.getController();
            Optional<EntityGroup> parent = controller.getDrawing().getRootEntity().findParentFor(group);
            controller.getSelectionManager().clearSelection();
            controller.getDrawing().removeEntity(group);

            if (parent.isPresent()) {
                parent.get().addAll(entities);
            } else {
                controller.getDrawing().insertEntities(entities);
            }
            controller.getSelectionManager().setSelection(entities);
        }

        @Override
        public String toString() {
            return "group entities";
        }
    }
}
