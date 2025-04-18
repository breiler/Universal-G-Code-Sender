package com.willwinder.universalgcodesender.fx.stage;

import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.universalgcodesender.connection.ConnectionDriver;
import com.willwinder.universalgcodesender.fx.component.PortComboBox;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.utils.FirmwareUtils;
import com.willwinder.universalgcodesender.utils.RefreshThread;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class ConnectStage extends Stage {
    private final ComboBox<String> protocolCombo;
    private final PortComboBox portCombo;
    private final Button connectButton;
    private final Button closeButton;
    private final BackendAPI backend;
    private final RefreshThread refreshThread;
    private final ComboBox<String> firmwareCombo;
    private final ComboBox<Object> baudCombo;

    private String selectedProtocol;
    private String selectedAddress;
    private String selectedPort;

    public ConnectStage(Window owner) {
        // Set modality and owner
        initModality(Modality.APPLICATION_MODAL);
        initOwner(owner);
        setTitle("Custom Modal");

        backend = CentralLookup.getDefault().lookup(BackendAPI.class);

        // Protocol dropdown
        protocolCombo = new ComboBox<>(FXCollections.observableArrayList(ConnectionDriver.getPrettyNames()));
        protocolCombo.valueProperty().addListener((observable, oldValue, newValue) -> backend.getSettings().setConnectionDriver(ConnectionDriver.prettyNameToEnum(newValue)));
        protocolCombo.getSelectionModel().select(backend.getSettings().getConnectionDriver().getPrettyName());


        firmwareCombo = new ComboBox<>(FXCollections.observableArrayList(FirmwareUtils.getFirmwareList()));
        firmwareCombo.valueProperty().addListener((observable, oldValue, newValue) -> backend.getSettings().setFirmwareVersion(newValue));
        firmwareCombo.getSelectionModel().select(backend.getSettings().getFirmwareVersion());

        // Port dropdown
        portCombo = new PortComboBox(backend);

        // Port dropdown
        baudCombo = new ComboBox<>();
        baudCombo.setItems(FXCollections.observableArrayList("80", "443", "502", "COM1", "COM3"));
        baudCombo.getSelectionModel().selectFirst();

        // Connect button
        connectButton = new Button("Connect");
        connectButton.setDefaultButton(true);
        connectButton.setOnAction(e -> {
            selectedProtocol = protocolCombo.getValue();

            close(); // Close modal
        });

        closeButton = new Button("Close");
        connectButton.setOnAction(e -> close());

        GridPane grid = new GridPane();

        // Create ColumnConstraints for each column
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setMaxWidth(200);  // Set max width for the first column
        column1.setHgrow(Priority.ALWAYS);  // Allow it to grow
        grid.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setMaxWidth(150);  // Set max width for the second column
        grid.getColumnConstraints().add(column2);

        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Protocol:"), 0, 0);
        grid.add(protocolCombo, 1, 0);

        grid.add(new Label("Firmware:"), 0, 1);
        grid.add(firmwareCombo, 1, 1);

        grid.add(new Label("Port:"), 0, 2);
        grid.add(portCombo, 1, 2);

        grid.add(new Label("Baud:"), 0, 3);
        grid.add(baudCombo, 1, 3);

        HBox buttonBox = new HBox(closeButton, connectButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, grid, buttonBox);
        root.setPadding(new Insets(10));

        setScene(new Scene(root));
        setWidth(350);
        setHeight(300);
        setResizable(false);
        refreshThread = new RefreshThread(this::refreshPorts, 3000);

        setOnShowing(event -> {
            double centerX = getOwner().getX() + getOwner().getWidth() / 2 - getWidth() / 2;
            double centerY = getOwner().getY() + getOwner().getHeight() / 2 - getHeight() / 2;
            setX(centerX);
            setY(centerY);

            refreshThread.start();
        });

        setOnCloseRequest(event -> {
            System.out.println("Closed!");
            refreshThread.interrupt();
        });
    }

    private void refreshPorts() {
        portCombo.refreshPorts();
    }
}
