/*
    Copyright 2025 Joacim Breiler

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
package com.willwinder.universalgcodesender.fx.component.settings;

import com.willwinder.universalgcodesender.fx.actions.Action;
import com.willwinder.universalgcodesender.fx.service.ActionRegistry;
import com.willwinder.universalgcodesender.fx.component.ActionIconTableCell;
import com.willwinder.universalgcodesender.fx.component.ActionShortcutTableCell;
import com.willwinder.universalgcodesender.fx.service.ShortcutService;
import com.willwinder.universalgcodesender.fx.settings.ShortcutSettings;
import com.willwinder.universalgcodesender.i18n.Localization;
import com.willwinder.universalgcodesender.utils.GUIHelpers;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class KeyboardSettingPane extends VBox {

    public KeyboardSettingPane() {
        setSpacing(20);
        addTitleSection();

        TableView<Action> table = new TableView<>();
        TableColumn<Action, Void> iconColumn = new TableColumn<>();
        iconColumn.setMaxWidth(30);
        iconColumn.setCellFactory(col -> new ActionIconTableCell());

        TableColumn<Action, String> titleColumn = new TableColumn<>(Localization.getString("settings.keyboard.action"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Action, String> categoryColumn = new TableColumn<>(Localization.getString("settings.keyboard.category"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Action, Void> shortcutColumn = new TableColumn<>(Localization.getString("settings.keyboard.shortcut"));
        shortcutColumn.setMaxWidth(Double.MAX_VALUE);
        shortcutColumn.setCellFactory(col -> new ActionShortcutTableCell());

        table.getColumns().addAll(iconColumn, titleColumn, shortcutColumn);
        table.setItems(FXCollections.observableArrayList(ActionRegistry.getInstance().getActions()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        getChildren().add(table);

        VBox.setVgrow(table, Priority.ALWAYS);

        addButtonSection();

        ShortcutService.getShortcuts().addListener((MapChangeListener<String, String>) change -> table.refresh());
    }

    private void addButtonSection() {
        Button importButton = new Button(Localization.getString("settings.keyboard.import"));
        importButton.setOnAction(this::importShortcuts);

        Button exportButton = new Button(Localization.getString("settings.keyboard.export"));
        exportButton.setOnAction(this::exportShortcuts);

        Button restoreDefaultsButton = new Button(Localization.getString("settings.keyboard.restoreDefaults"));
        restoreDefaultsButton.setOnAction(event -> ShortcutSettings.restoreDefaults());

        HBox buttonRow = new HBox(10, importButton, exportButton, restoreDefaultsButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);
        getChildren().add(buttonRow);
    }

    private void importShortcuts(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Localization.getString("settings.keyboard.import"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(resolveWindow(event));
        if (selectedFile == null) {
            return;
        }

        try {
            ShortcutSettings.importShortcuts(selectedFile);
        } catch (Exception e) {
            GUIHelpers.displayErrorDialog(e.getLocalizedMessage());
        }
    }

    private void exportShortcuts(ActionEvent event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(Localization.getString("settings.keyboard.export"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName("shortcuts-" + timestamp + ".json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        File selectedFile = fileChooser.showSaveDialog(resolveWindow(event));
        if (selectedFile == null) {
            return;
        }

        if (!selectedFile.getName().toLowerCase().endsWith(".json")) {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".json");
        }

        try {
            ShortcutSettings.exportShortcuts(selectedFile);
        } catch (Exception e) {
            GUIHelpers.displayErrorDialog(e.getLocalizedMessage());
        }
    }

    private static Window resolveWindow(ActionEvent event) {
        if (event.getSource() instanceof Node node && node.getScene() != null) {
            return node.getScene().getWindow();
        }
        return null;
    }

    private void addTitleSection() {
        Label title = new Label(Localization.getString("settings.keyboard"));
        title.setFont(Font.font(20));
        getChildren().add(title);
    }
}
