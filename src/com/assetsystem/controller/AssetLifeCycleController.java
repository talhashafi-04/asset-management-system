package com.assetsystem.controller;

import com.assetsystem.model.Asset_Allocation;
import com.assetsystem.repository.AllocationRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.Date;

public class AssetLifeCycleController {

    @FXML
    private SidebarController sidebarController;
    @FXML
    private TableView<Asset_Allocation> allocTable;
    @FXML
    private TableColumn<Asset_Allocation, String> colAllocId;
    @FXML
    private TableColumn<Asset_Allocation, String> colSerial;
    @FXML
    private TableColumn<Asset_Allocation, String> colModel;
    @FXML
    private TableColumn<Asset_Allocation, String> colEmployee;
    @FXML
    private TableColumn<Asset_Allocation, String> colDept;
    @FXML
    private TableColumn<Asset_Allocation, String> colLocation;
    @FXML
    private TableColumn<Asset_Allocation, String> colAllocStatus;
    @FXML
    private TableColumn<Asset_Allocation, String> colAllocDate;
    @FXML
    private TableColumn<Asset_Allocation, String> colReturnDate;
    @FXML
    private TableColumn<Asset_Allocation, String> colNotes;

    private final AllocationRepository repo = new AllocationRepository();

    @FXML
    private void initialize() {
        if (sidebarController != null) {
            sidebarController.setActiveSection(SidebarSection.ASSET_LIFE_CYCLE);
        }

        colAllocId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getAllocationId())));
        colSerial.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsset().getSerialNo()));
        colModel.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAsset().getModel().getName()));
        colEmployee.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployee().getFullName()));
        colDept.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDepartmentName()));
        colLocation.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLocationName()));
        colAllocStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        colAllocDate.setCellValueFactory(c -> new SimpleStringProperty(fmt(c.getValue().getAllocateDate())));
        colReturnDate.setCellValueFactory(c -> new SimpleStringProperty(fmt(c.getValue().getReturnDate())));
        colNotes.setCellValueFactory(c -> {
            String n = c.getValue().getNotes();
            return new SimpleStringProperty(n == null ? "" : n);
        });

        allocTable.setItems(FXCollections.observableArrayList(repo.getAllAllocations()));
    }

    private static String fmt(Date d) {
        return d == null ? "" : d.toString();
    }
}
