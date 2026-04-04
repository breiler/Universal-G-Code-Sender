package com.willwinder.ugs.designer.platform.actions;

import com.willwinder.ugs.designer.actions.AbstractDesignAction;
import com.willwinder.ugs.designer.entities.selection.SelectionEvent;
import com.willwinder.ugs.designer.entities.selection.SelectionListener;
import com.willwinder.ugs.designer.entities.selection.SelectionManager;
import com.willwinder.ugs.designer.io.ugsd.UgsDesignWriter;
import com.willwinder.ugs.designer.logic.Controller;
import com.willwinder.ugs.designer.logic.ControllerFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.StatusDisplayer;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

@ActionID(
        id = "com.willwinder.ugs.nbp.designer.actions.CopyAction",
        category = "Edit")
@ActionReferences({
        @ActionReference(
                path = "Shortcuts",
                name = "D-C")
})
public class CopyAction extends AbstractDesignAction implements SelectionListener {

    private final transient Controller controller;

    public CopyAction() {
        putValue("menuText", "Copy");
        putValue(NAME, "Copy");

        this.controller = ControllerFactory.getController();

        SelectionManager selectionManager = controller.getSelectionManager();
        selectionManager.addSelectionListener(this);
        setEnabled(!selectionManager.getSelection().isEmpty());
    }

    @Override
    public void onSelectionEvent(SelectionEvent selectionEvent) {
        SelectionManager selectionManager = controller.getSelectionManager();
        setEnabled(!selectionManager.getSelection().isEmpty());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (controller.getSelectionManager().getSelection().isEmpty()) {
            StatusDisplayer.getDefault().setStatusText("");
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(null, null);
        } else {
            StatusDisplayer.getDefault().setStatusText("Clipboard: " + controller.getSelectionManager().getSelection().size());
            UgsDesignWriter writer = new UgsDesignWriter();
            String data = writer.serialize(controller.getSelectionManager().getChildren());

            Transferable content = new StringSelection(data);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, null);
        }
    }
}