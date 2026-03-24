package com.assetsystem.controller;

import com.assetsystem.model.AssetDetails;
import com.assetsystem.repository.AssetRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AssetDisplayController {

    @FXML private TableView<AssetDetails> assetTable;
    @FXML private TableColumn<AssetDetails, String> colCategory;
    @FXML private TableColumn<AssetDetails, String> colModel;
    @FXML private TableColumn<AssetDetails, String> colManufacturer;
    @FXML private TableColumn<AssetDetails, String> colSerial;
    @FXML private TableColumn<AssetDetails, String> colStatus;

    private AssetRepository repo = new AssetRepository();

    @FXML
    public void initialize() {
        // Map simple fields
        colSerial.setCellValueFactory(new PropertyValueFactory<>("serialNo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Map nested fields from Models, Category, and Manufacturer
        colCategory.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getModel().getCategory().getName()));
            
        colModel.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getModel().getName()));
            
        colManufacturer.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getModel().getManufacturer().getName()));

        loadData();
    }

    private void loadData() {
        try {
            ObservableList<AssetDetails> data = FXCollections.observableArrayList(repo.getAllAssets());
            assetTable.setItems(data);
        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
        }
    }
}