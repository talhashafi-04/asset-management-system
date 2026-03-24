package com.assetsystem.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class SidebarController {

    private static final String STYLE_INACTIVE =
            "-fx-background-color: transparent; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";
    private static final String STYLE_ACTIVE =
            "-fx-background-color: #e9ecef; -fx-alignment: CENTER_LEFT; -fx-padding: 10 20;";

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnAddAsset;
    @FXML
    private Button btnAllocatedAssets;
    @FXML
    private Button btnAssetLifeCycle;
    @FXML
    private Button btnModel;
    @FXML
    private Button btnManageStore;

    public void setActiveSection(SidebarSection section) {
        if (btnDashboard == null) {
            return;
        }
        btnDashboard.setStyle(STYLE_INACTIVE);
        btnAddAsset.setStyle(STYLE_INACTIVE);
        btnAllocatedAssets.setStyle(STYLE_INACTIVE);
        btnAssetLifeCycle.setStyle(STYLE_INACTIVE);
        btnModel.setStyle(STYLE_INACTIVE);
        btnManageStore.setStyle(STYLE_INACTIVE);
        switch (section) {
            case DASHBOARD:
                btnDashboard.setStyle(STYLE_ACTIVE);
                break;
            case ADD_ASSET:
                btnAddAsset.setStyle(STYLE_ACTIVE);
                break;
            case ALLOCATED_ASSETS:
                btnAllocatedAssets.setStyle(STYLE_ACTIVE);
                break;
            case ASSET_LIFE_CYCLE:
                btnAssetLifeCycle.setStyle(STYLE_ACTIVE);
                break;
            case MODEL:
                btnModel.setStyle(STYLE_ACTIVE);
                break;
            case MANAGE_STORE:
                btnManageStore.setStyle(STYLE_ACTIVE);
                break;
        }
    }

    private static void loadRoot(ActionEvent e, String resource) throws IOException {
        Parent root = FXMLLoader.load(SidebarController.class.getResource(resource));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 700));
    }

    @FXML
    private void onDashboard(ActionEvent e) throws IOException {
        loadRoot(e, "/com/assetsystem/controller/Dashboard.fxml");
    }

    @FXML
    private void onAddAsset(ActionEvent e) throws IOException {
        loadRoot(e, "/com/assetsystem/controller/AddAsset.fxml");
    }

    @FXML
    private void onAllocatedAssets(ActionEvent e) throws IOException {
        NavigationContext.setPendingAssetDisplaySection(SidebarSection.ALLOCATED_ASSETS);
        loadRoot(e, "/com/assetsystem/controller/AssetDisplay.fxml");
    }

    @FXML
    private void onAssetLifeCycle(ActionEvent e) throws IOException {
        loadRoot(e, "/com/assetsystem/controller/AssetLifeCycle.fxml");
    }

    @FXML
    private void onModel(ActionEvent e) throws IOException {
        loadRoot(e, "/com/assetsystem/controller/ModelCatalog.fxml");
    }

    @FXML
    private void onManageStore(ActionEvent e) throws IOException {
        NavigationContext.setPendingAssetDisplaySection(SidebarSection.MANAGE_STORE);
        loadRoot(e, "/com/assetsystem/controller/AssetDisplay.fxml");
    }
}
