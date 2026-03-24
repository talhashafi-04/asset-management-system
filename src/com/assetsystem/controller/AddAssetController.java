package com.assetsystem.controller;

import com.assetsystem.model.AllocationStatus;
import com.assetsystem.model.Models;
import com.assetsystem.repository.AssetRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AddAssetController {

    @FXML
    private SidebarController sidebarController;
    @FXML
    private ComboBox<Models> comboModel;
    @FXML
    private ComboBox<AllocationStatus> comboStatus;
    @FXML
    private TextField fieldSerial;
    @FXML
    private DatePicker pickerPurchaseDate;
    @FXML
    private TextField fieldPrice;
    @FXML
    private TextArea areaRemarks;

    private final AssetRepository repo = new AssetRepository();

    @FXML
    private void initialize() {
        if (sidebarController != null) {
            sidebarController.setActiveSection(SidebarSection.ADD_ASSET);
        }

        comboModel.setConverter(new StringConverter<Models>() {
            @Override
            public String toString(Models m) {
                if (m == null) {
                    return "";
                }
                return m.getName() + " (" + m.getManufacturer().getName() + " / " + m.getCategory().getName() + ")";
            }

            @Override
            public Models fromString(String string) {
                return null;
            }
        });
        comboStatus.setConverter(new StringConverter<AllocationStatus>() {
            @Override
            public String toString(AllocationStatus s) {
                return s == null ? "" : s.getName();
            }

            @Override
            public AllocationStatus fromString(String string) {
                return null;
            }
        });

        reloadDropdowns();
        pickerPurchaseDate.setValue(LocalDate.now());
    }

    private void reloadDropdowns() {
        List<Models> models = repo.getAllModels();
        comboModel.setItems(FXCollections.observableArrayList(models));
        List<AllocationStatus> statuses = repo.getAllAllocationStatuses();
        comboStatus.setItems(FXCollections.observableArrayList(statuses));
        selectInStoreStatus();
    }

    /** New inventory defaults to In Store (not first alphabetical, which was Allocated). */
    private void selectInStoreStatus() {
        for (AllocationStatus s : comboStatus.getItems()) {
            if (s != null && "In Store".equals(s.getName())) {
                comboStatus.getSelectionModel().select(s);
                return;
            }
        }
        if (!comboStatus.getItems().isEmpty()) {
            comboStatus.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void onSave(ActionEvent e) {
        String serial = fieldSerial.getText();
        if (serial == null || serial.trim().isEmpty()) {
            showError("Serial number is required.");
            return;
        }
        Models model = comboModel.getSelectionModel().getSelectedItem();
        if (model == null) {
            showError("Please select a model.");
            return;
        }
        AllocationStatus status = comboStatus.getSelectionModel().getSelectedItem();
        if (status == null) {
            showError("Please select an allocation status.");
            return;
        }
        LocalDate localDate = pickerPurchaseDate.getValue();
        if (localDate == null) {
            showError("Please select a purchase date.");
            return;
        }
        double price;
        try {
            String p = fieldPrice.getText();
            if (p == null || p.trim().isEmpty()) {
                showError("Price is required.");
                return;
            }
            price = Double.parseDouble(p.trim());
            if (price < 0) {
                showError("Price cannot be negative.");
                return;
            }
        } catch (NumberFormatException ex) {
            showError("Enter a valid price.");
            return;
        }

        String trimmedSerial = serial.trim();
        if (repo.serialNumberExists(trimmedSerial)) {
            showError("An asset with this serial number already exists.");
            return;
        }

        String remarks = areaRemarks.getText();
        Date sqlDate = Date.valueOf(localDate);
        boolean ok = repo.insertAsset(trimmedSerial, sqlDate, price, remarks, model.getId(), status.getStatusId());
        if (!ok) {
            showError("Could not save the asset. Check the database connection and try again.");
            return;
        }

        Alert okAlert = new Alert(Alert.AlertType.INFORMATION);
        okAlert.setTitle("Saved");
        okAlert.setHeaderText(null);
        okAlert.setContentText("Asset added successfully.");
        okAlert.showAndWait();

        clearFormAfterSave();
        reloadDropdowns();
    }

    private void clearFormAfterSave() {
        fieldSerial.clear();
        fieldPrice.clear();
        areaRemarks.clear();
        pickerPurchaseDate.setValue(LocalDate.now());
        comboModel.getSelectionModel().clearSelection();
    }

    @FXML
    private void onCancel(ActionEvent e) throws IOException {
        NavigationContext.setPendingAssetDisplaySection(SidebarSection.MANAGE_STORE);
        Parent root = FXMLLoader.load(getClass().getResource("/com/assetsystem/controller/AssetDisplay.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 700));
    }

    private void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Validation");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
