package com.assetsystem.controller;

import com.assetsystem.model.DashboardStats;
import com.assetsystem.repository.DashboardRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DashboardController {

    @FXML
    private SidebarController sidebarController;

    @FXML
    private Label lblTotalAssets;
    @FXML
    private Label lblTotalEmployees;
    @FXML
    private Label lblActiveAllocations;
    @FXML
    private TableView<Map.Entry<String, Integer>> statusTable;
    @FXML
    private TableColumn<Map.Entry<String, Integer>, String> colStatusName;
    @FXML
    private TableColumn<Map.Entry<String, Integer>, Integer> colStatusCount;

    private final DashboardRepository dashboardRepo = new DashboardRepository();

    @FXML
    private void initialize() {
        if (sidebarController != null) {
            sidebarController.setActiveSection(SidebarSection.DASHBOARD);
        }

        colStatusName.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getKey()));
        colStatusCount.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getValue()));

        loadStats();
    }

    private void loadStats() {
        try {
            DashboardStats stats = dashboardRepo.loadDashboardStats();
            lblTotalAssets.setText(String.valueOf(stats.getTotalAssets()));
            lblTotalEmployees.setText(String.valueOf(stats.getTotalEmployees()));
            lblActiveAllocations.setText(String.valueOf(stats.getActiveAllocations()));

            List<Map.Entry<String, Integer>> rows = new ArrayList<>();
            for (Map.Entry<String, Integer> e : stats.getAssetCountByStatus().entrySet()) {
                rows.add(new AbstractMap.SimpleEntry<>(e.getKey(), e.getValue()));
            }
            ObservableList<Map.Entry<String, Integer>> items = FXCollections.observableArrayList(rows);
            statusTable.setItems(items);
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
        }
    }
}
