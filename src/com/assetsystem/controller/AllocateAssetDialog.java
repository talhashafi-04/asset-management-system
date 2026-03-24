package com.assetsystem.controller;

import com.assetsystem.model.AssetDetails;
import com.assetsystem.model.Department;
import com.assetsystem.model.Employees;
import com.assetsystem.model.Location;
import com.assetsystem.repository.AllocationRepository;
import com.assetsystem.repository.EmployeeRepository;
import com.assetsystem.repository.LookupRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

/**
 * Form to allocate an in-store asset: {@code Asset_Allocation} + {@code AssetDetails.CurrentStatusID} → Allocated.
 */
public final class AllocateAssetDialog {

    private AllocateAssetDialog() {
    }

    /**
     * @return {@code true} if allocation succeeded
     */
    public static boolean show(Window owner, AssetDetails asset) {
        EmployeeRepository empRepo = new EmployeeRepository();
        LookupRepository lookups = new LookupRepository();
        AllocationRepository allocRepo = new AllocationRepository();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Allocate asset");
        dialog.setHeaderText(asset.getSerialNo() + " · " + asset.getModel().getName());
        dialog.initOwner(owner);

        ComboBox<Employees> cbEmp = new ComboBox<>();
        cbEmp.setPrefWidth(360);
        cbEmp.setItems(FXCollections.observableArrayList(empRepo.getAllEmployees()));
        cbEmp.setConverter(new StringConverter<Employees>() {
            @Override
            public String toString(Employees e) {
                if (e == null) {
                    return "";
                }
                String d = e.getDepartment();
                if (d != null && !d.trim().isEmpty()) {
                    return e.getFullName() + " (" + d + ")";
                }
                return e.getFullName();
            }

            @Override
            public Employees fromString(String s) {
                return null;
            }
        });

        ComboBox<Department> cbDept = new ComboBox<>();
        cbDept.setPrefWidth(360);
        cbDept.setItems(FXCollections.observableArrayList(lookups.listDepartments()));
        cbDept.setConverter(new StringConverter<Department>() {
            @Override
            public String toString(Department d) {
                return d == null ? "" : d.getName();
            }

            @Override
            public Department fromString(String s) {
                return null;
            }
        });

        ComboBox<Location> cbLoc = new ComboBox<>();
        cbLoc.setPrefWidth(360);
        cbLoc.setItems(FXCollections.observableArrayList(lookups.listLocations()));
        cbLoc.setConverter(new StringConverter<Location>() {
            @Override
            public String toString(Location l) {
                return l == null ? "" : l.getName();
            }

            @Override
            public Location fromString(String s) {
                return null;
            }
        });

        DatePicker dp = new DatePicker(LocalDate.now());
        TextArea notes = new TextArea();
        notes.setPromptText("Optional notes");
        notes.setPrefRowCount(3);
        notes.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        int r = 0;
        grid.add(new Label("Employee *"), 0, r);
        grid.add(cbEmp, 1, r++);
        grid.add(new Label("Department"), 0, r);
        grid.add(cbDept, 1, r++);
        grid.add(new Label("Location"), 0, r);
        grid.add(cbLoc, 1, r++);
        grid.add(new Label("Allocation date *"), 0, r);
        grid.add(dp, 1, r++);
        grid.add(new Label("Notes"), 0, r);
        grid.add(notes, 1, r);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(grid);
        ButtonType allocateType = new ButtonType("Allocate", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(allocateType, ButtonType.CANCEL);

        cbEmp.valueProperty().addListener((obs, o, n) -> {
            if (n != null) {
                Integer did = empRepo.findDeptIdByEmpId(n.getId());
                if (did != null) {
                    for (Department d : cbDept.getItems()) {
                        if (d.getId() == did) {
                            cbDept.getSelectionModel().select(d);
                            return;
                        }
                    }
                }
            }
        });

        final boolean[] success = {false};
        Button allocateBtn = (Button) pane.lookupButton(allocateType);
        allocateBtn.addEventFilter(ActionEvent.ACTION, event -> {
            Employees emp = cbEmp.getSelectionModel().getSelectedItem();
            if (emp == null) {
                event.consume();
                warn("Select an employee.");
                return;
            }
            LocalDate day = dp.getValue();
            if (day == null) {
                event.consume();
                warn("Select allocation date.");
                return;
            }
            LocalDateTime ldt = LocalDateTime.of(day, LocalTime.NOON);
            Timestamp ts = Timestamp.valueOf(ldt);

            Department dep = cbDept.getSelectionModel().getSelectedItem();
            Location loc = cbLoc.getSelectionModel().getSelectedItem();
            Integer deptId = dep != null ? dep.getId() : null;
            Integer locId = loc != null ? loc.getId() : null;

            String n = notes.getText();
            boolean ok = allocRepo.allocateFromStore(
                    asset.getAssetId(),
                    emp.getId(),
                    locId,
                    deptId,
                    n,
                    ts
            );
            if (!ok) {
                event.consume();
                err("Allocation failed. Check DB connection, employees, and foreign keys.");
                return;
            }
            success[0] = true;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        return success[0] && result.isPresent() && result.get() == allocateType;
    }

    private static void warn(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Allocate");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private static void err(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Allocate");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
