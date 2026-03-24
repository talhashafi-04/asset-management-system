package com.assetsystem.controller;

import com.assetsystem.model.AssetDetails;
import com.assetsystem.repository.AssetRepository;
import com.assetsystem.repository.EmployeeRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class AssetDisplayController {

    @FXML
    private SidebarController sidebarController;

    @FXML
    private Label pageTitle;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<AssetDetails> assetTable;
    @FXML
    private TableColumn<AssetDetails, String> colCategory;
    @FXML
    private TableColumn<AssetDetails, String> colModel;
    @FXML
    private TableColumn<AssetDetails, String> colManufacturer;
    @FXML
    private TableColumn<AssetDetails, String> colSerial;
    @FXML
    private TableColumn<AssetDetails, String> colEmployee;
    @FXML
    private TableColumn<AssetDetails, String> colEmpDept;
    @FXML
    private TableColumn<AssetDetails, String> colAllocDate;
    @FXML
    private TableColumn<AssetDetails, String> colStatus;
    @FXML
    private TableColumn<AssetDetails, Void> colAction;

    private final AssetRepository repo = new AssetRepository();
    private final EmployeeRepository employeeRepo = new EmployeeRepository();
    private ObservableList<AssetDetails> masterData = FXCollections.observableArrayList();
    private SidebarSection viewSection;

    @FXML
    public void initialize() {
        viewSection = NavigationContext.takePendingAssetDisplaySection();
        if (sidebarController != null) {
            sidebarController.setActiveSection(viewSection);
        }
        switch (viewSection) {
            case MANAGE_STORE:
                pageTitle.setText("Manage Store");
                break;
            case ALLOCATED_ASSETS:
                pageTitle.setText("Allocated Assets");
                break;
            default:
                pageTitle.setText("Assets");
                break;
        }

        colSerial.setCellValueFactory(new PropertyValueFactory<>("serialNo"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colCategory.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getModel().getCategory().getName()));

        colModel.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getModel().getName()));

        colManufacturer.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getModel().getManufacturer().getName()));

        colEmployee.setCellValueFactory(cellData -> {
            String n = cellData.getValue().getAllocatedEmployeeName();
            return new SimpleStringProperty(n != null ? n : "");
        });
        colEmpDept.setCellValueFactory(cellData -> {
            String n = cellData.getValue().getAllocatedDepartmentName();
            return new SimpleStringProperty(n != null ? n : "");
        });
        colAllocDate.setCellValueFactory(cellData -> {
            java.sql.Date d = cellData.getValue().getAllocationDate();
            return new SimpleStringProperty(d != null ? d.toString() : "");
        });

        boolean showAssigneeCols = viewSection == SidebarSection.ALLOCATED_ASSETS;
        colEmployee.setVisible(showAssigneeCols);
        colEmpDept.setVisible(showAssigneeCols);
        colAllocDate.setVisible(showAssigneeCols);

        loadMasterData(viewSection);

        FilteredList<AssetDetails> filtered = new FilteredList<>(masterData, p -> true);
        searchField.textProperty().addListener((obs, old, val) ->
                filtered.setPredicate(buildPredicate(val, viewSection)));

        SortedList<AssetDetails> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(assetTable.comparatorProperty());
        assetTable.setItems(sorted);

        final SidebarSection section = viewSection;
        colAction.setCellFactory(col -> new TableCell<AssetDetails, Void>() {
            private final Button btn = new Button(section == SidebarSection.MANAGE_STORE ? "Allocate" : "Details");

            {
                btn.setOnAction(ev -> {
                    TableRow<AssetDetails> row = getTableRow();
                    AssetDetails a = row != null ? row.getItem() : null;
                    if (a == null) {
                        return;
                    }
                    if (section == SidebarSection.MANAGE_STORE) {
                        if (employeeRepo.getAllEmployees().isEmpty()) {
                            Alert al = new Alert(Alert.AlertType.WARNING);
                            al.setTitle("Allocate");
                            al.setHeaderText(null);
                            al.setContentText("Add at least one employee in the database before allocating assets.");
                            al.showAndWait();
                            return;
                        }
                        Window w = getTableView().getScene().getWindow();
                        if (AllocateAssetDialog.show(w, a)) {
                            loadMasterData(section);
                        }
                    } else {
                        showDetails(a);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private static Predicate<AssetDetails> buildPredicate(String raw, SidebarSection section) {
        if (raw == null || raw.trim().isEmpty()) {
            return p -> true;
        }
        String q = raw.trim().toLowerCase(Locale.ROOT);
        return a -> {
            if (a.getModel() == null) {
                return false;
            }
            String cat = a.getModel().getCategory() != null ? a.getModel().getCategory().getName() : "";
            String man = a.getModel().getManufacturer() != null ? a.getModel().getManufacturer().getName() : "";
            String mod = a.getModel().getName() != null ? a.getModel().getName() : "";
            String ser = a.getSerialNo() != null ? a.getSerialNo() : "";
            String st = a.getStatus() != null ? a.getStatus() : "";
            String emp = a.getAllocatedEmployeeName() != null ? a.getAllocatedEmployeeName() : "";
            String ad = a.getAllocatedDepartmentName() != null ? a.getAllocatedDepartmentName() : "";
            String adt = a.getAllocationDate() != null ? a.getAllocationDate().toString() : "";
            String blob = (cat + " " + man + " " + mod + " " + ser + " " + st).toLowerCase(Locale.ROOT);
            if (section == SidebarSection.ALLOCATED_ASSETS) {
                blob = blob + " " + emp.toLowerCase(Locale.ROOT) + " " + ad.toLowerCase(Locale.ROOT) + " " + adt.toLowerCase(Locale.ROOT);
            }
            return blob.contains(q);
        };
    }

    @FXML
    private void onClearSearch() {
        searchField.clear();
    }

    private void showDetails(AssetDetails a) {
        Alert al = new Alert(Alert.AlertType.INFORMATION);
        al.setTitle("Asset");
        al.setHeaderText(a.getSerialNo());
        StringBuilder sb = new StringBuilder();
        sb.append("Model: ").append(a.getModel().getName()).append("\n");
        sb.append("Category: ").append(a.getModel().getCategory().getName()).append("\n");
        sb.append("Manufacturer: ").append(a.getModel().getManufacturer().getName()).append("\n");
        sb.append("Status: ").append(a.getStatus()).append("\n");
        if (a.getPurchaseDate() != null) {
            sb.append("Purchase: ").append(a.getPurchaseDate()).append("\n");
        }
        sb.append("Price: ").append(a.getPrice()).append("\n");
        if (a.getRemarks() != null && !a.getRemarks().trim().isEmpty()) {
            sb.append("Remarks: ").append(a.getRemarks()).append("\n");
        }
        if (a.hasAllocationDetails()) {
            sb.append("\n--- Allocation ---\n");
            sb.append("Employee: ").append(a.getAllocatedEmployeeName()).append("\n");
            if (a.getAllocatedDepartmentName() != null && !a.getAllocatedDepartmentName().trim().isEmpty()) {
                sb.append("Department: ").append(a.getAllocatedDepartmentName()).append("\n");
            }
            if (a.getAllocationDate() != null) {
                sb.append("Allocated on: ").append(a.getAllocationDate());
            }
        }
        al.setContentText(sb.toString());
        al.showAndWait();
    }

    private void loadMasterData(SidebarSection section) {
        try {
            List<AssetDetails> list;
            switch (section) {
                case MANAGE_STORE:
                    list = repo.getAssetsInStore();
                    break;
                case ALLOCATED_ASSETS:
                    list = repo.getAllocatedAssetsWithAssignee();
                    break;
                default:
                    list = repo.getAllAssets();
                    break;
            }
            masterData.setAll(list);
        } catch (Exception e) {
            System.err.println("Error loading table data: " + e.getMessage());
        }
    }
}
