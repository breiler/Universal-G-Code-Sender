package com.willwinder.universalgcodesender.fx.stage;

import com.willwinder.ugs.nbp.lib.lookup.CentralLookup;
import com.willwinder.universalgcodesender.connection.ConnectionFactory;
import com.willwinder.universalgcodesender.connection.IConnectionDevice;
import com.willwinder.universalgcodesender.model.BackendAPI;
import com.willwinder.universalgcodesender.utils.RefreshThread;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;

public class ConnectStage extends Stage {
    private final ComboBox<String> protocolCombo;
    private final ComboBox<String> addressCombo;
    private final ComboBox<String> portCombo;
    private final Button connectButton;
    private final Button closeButton;
    private final BackendAPI backend;
    private final RefreshThread refreshThread;

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
        protocolCombo = new ComboBox<>();
        protocolCombo.setItems(FXCollections.observableArrayList("TCP/IP", "Serial"));
        protocolCombo.getSelectionModel().selectFirst();

        // Address dropdown (editable)
        addressCombo = new ComboBox<>();
        addressCombo.setEditable(true);
        addressCombo.setItems(FXCollections.observableArrayList());
        addressCombo.getSelectionModel().selectFirst();

        // Port dropdown
        portCombo = new ComboBox<>();
        portCombo.setItems(FXCollections.observableArrayList("80", "443", "502", "COM1", "COM3"));
        portCombo.getSelectionModel().selectFirst();

        // Connect button
        connectButton = new Button("Connect");
        connectButton.setDefaultButton(true);
        connectButton.setOnAction(e -> {
            selectedProtocol = protocolCombo.getValue();
            selectedAddress = addressCombo.getEditor().getText();
            selectedPort = portCombo.getValue();
            close(); // Close modal
        });

        closeButton = new Button("Close");
        connectButton.setOnAction(e -> close());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Protocol:"), 0, 0);
        grid.add(protocolCombo, 1, 0);

        grid.add(new Label("Address:"), 0, 1);
        grid.add(addressCombo, 1, 1);

        grid.add(new Label("Port:"), 0, 2);
        grid.add(portCombo, 1, 2);

        HBox buttonBox = new HBox(closeButton, connectButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10, grid, buttonBox);
        root.setPadding(new Insets(10));

        setScene(new Scene(root));
        setWidth(350);
        setHeight(220);
        setResizable(false);
        refreshThread = new RefreshThread(this::refreshPorts, 3000);

        setOnShowing(event -> {
            double centerX = getOwner().getX() + getOwner().getWidth() / 2 - getWidth() / 2;
            double centerY = getOwner().getY() + getOwner().getHeight() / 2 - getHeight() / 2;
            setX(centerX);
            setY(centerY);

            refreshThread.start();
        });
    }

    @Override
    public void close() {
        refreshThread.interrupt();
        super.close();
    }

    private void refreshPorts() {
        List<? extends IConnectionDevice> availablePorts = ConnectionFactory.getDevices(backend.getSettings().getConnectionDriver());
        addressCombo.setItems(FXCollections.observableArrayList(availablePorts.stream().map(IConnectionDevice::getAddress).toList()));
    }

    public void showModal() {
        showAndWait(); // Ensures modal behavior
    }
}
