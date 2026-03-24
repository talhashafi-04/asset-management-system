package com.assetsystem.controller;

import com.assetsystem.model.Categories;
import com.assetsystem.model.Manufacturer;
import com.assetsystem.model.Models;
import com.assetsystem.repository.CatalogRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class ModelCatalogController {

    @FXML
    private SidebarController sidebarController;
    @FXML
    private TextField fieldModelName;
    @FXML
    private ComboBox<Manufacturer> comboManufacturer;
    @FXML
    private ComboBox<Categories> comboCategory;
    @FXML
    private TableView<Models> modelTable;
    @FXML
    private TableColumn<Models, String> colModelId;
    @FXML
    private TableColumn<Models, String> colModelName;
    @FXML
    private TableColumn<Models, String> colMan;
    @FXML
    private TableColumn<Models, String> colCat;

    private final CatalogRepository catalog = new CatalogRepository();

    @FXML
    private void initialize() {
        if (sidebarController != null) {
            sidebarController.setActiveSection(SidebarSection.MODEL);
        }

        comboManufacturer.setConverter(new StringConverter<Manufacturer>() {
            @Override
            public String toString(Manufacturer m) {
                return m == null ? "" : m.getName();
            }

            @Override
            public Manufacturer fromString(String s) {
                return null;
            }
        });
        comboCategory.setConverter(new StringConverter<Categories>() {
            @Override
            public String toString(Categories c) {
                return c == null ? "" : c.getName();
            }

            @Override
            public Categories fromString(String s) {
                return null;
            }
        });

        colModelId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colModelName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colMan.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getManufacturer().getName()));
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory().getName()));

        reloadCombos();
        reloadTable();

        modelTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) {
                fieldModelName.setText(n.getName());
                selectManufacturer(n.getManufacturer().getId());
                selectCategory(n.getCategory().getId());
            }
        });
    }

    private void selectManufacturer(int id) {
        for (Manufacturer m : comboManufacturer.getItems()) {
            if (m.getId() == id) {
                comboManufacturer.getSelectionModel().select(m);
                return;
            }
        }
    }

    private void selectCategory(int id) {
        for (Categories c : comboCategory.getItems()) {
            if (c.getId() == id) {
                comboCategory.getSelectionModel().select(c);
                return;
            }
        }
    }

    private void reloadCombos() {
        comboManufacturer.setItems(FXCollections.observableArrayList(catalog.getAllManufacturers()));
        comboCategory.setItems(FXCollections.observableArrayList(catalog.getAllCategories()));
    }

    private void reloadTable() {
        modelTable.setItems(FXCollections.observableArrayList(catalog.getAllModels()));
    }

    @FXML
    private void onAdd() {
        String name = fieldModelName.getText();
        Manufacturer m = comboManufacturer.getSelectionModel().getSelectedItem();
        Categories c = comboCategory.getSelectionModel().getSelectedItem();
        if (name == null || name.trim().isEmpty()) {
            err("Enter a model name.");
            return;
        }
        if (m == null || c == null) {
            err("Select manufacturer and category.");
            return;
        }
        int id = catalog.insertModel(name.trim(), m.getId(), c.getId());
        if (id < 0) {
            err("Could not add model (duplicate name or database error).");
            return;
        }
        info("Model added.");
        reloadTable();
        onClearForm();
    }

    @FXML
    private void onUpdate() {
        Models sel = modelTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            err("Select a row to update.");
            return;
        }
        String name = fieldModelName.getText();
        Manufacturer m = comboManufacturer.getSelectionModel().getSelectedItem();
        Categories c = comboCategory.getSelectionModel().getSelectedItem();
        if (name == null || name.trim().isEmpty()) {
            err("Enter a model name.");
            return;
        }
        if (m == null || c == null) {
            err("Select manufacturer and category.");
            return;
        }
        if (!catalog.updateModel(sel.getId(), name.trim(), m.getId(), c.getId())) {
            err("Update failed.");
            return;
        }
        info("Model updated.");
        reloadTable();
    }

    @FXML
    private void onDelete() {
        Models sel = modelTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            err("Select a row to delete.");
            return;
        }
        if (!catalog.deleteModel(sel.getId())) {
            err("Delete failed (model may still be referenced by assets).");
            return;
        }
        info("Model deleted.");
        reloadTable();
        onClearForm();
    }

    @FXML
    private void onClearForm() {
        fieldModelName.clear();
        comboManufacturer.getSelectionModel().clearSelection();
        comboCategory.getSelectionModel().clearSelection();
        modelTable.getSelectionModel().clearSelection();
    }

    private void err(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Models");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private void info(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Models");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
