package com.willwinder.ugs.designer.actions;

import com.willwinder.ugs.designer.logic.Controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OpenStockSettingsAction implements ActionListener {
    private final Controller controller;

    public OpenStockSettingsAction(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO: Implement stock settings
        /*StockSettingsPanel stockSettingsPanel = new StockSettingsPanel(controller);
        DialogDescriptor dialogDescriptor = new DialogDescriptor(stockSettingsPanel, "Stock settings", true, null);
        if (DialogDisplayer.getDefault().notify(dialogDescriptor) == OK_OPTION) {
            double stockThickness = stockSettingsPanel.getStockThickness();
            ChangeStockSettingsAction changeStockSettingsAction = new ChangeStockSettingsAction(controller, stockThickness);
            changeStockSettingsAction.actionPerformed(null);
            controller.getUndoManager().addAction(changeStockSettingsAction);
        }*/
    }
}
