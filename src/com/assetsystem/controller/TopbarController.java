package com.assetsystem.controller;

import com.assetsystem.model.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class TopbarController {

    @FXML
    private Label lblDisplayName;

    @FXML
    private void initialize() {
        Users u = SessionContext.getCurrentUser();
        if (lblDisplayName != null && u != null) {
            String role = u.getRole();
            if (role != null && !role.trim().isEmpty()) {
                lblDisplayName.setText(u.getUsername() + " (" + role + ")");
            } else {
                lblDisplayName.setText(u.getUsername());
            }
        } else if (lblDisplayName != null) {
            lblDisplayName.setText("");
        }
    }

    @FXML
    private void onChangePassword(ActionEvent e) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Change password");
        a.setHeaderText(null);
        a.setContentText("Change password is not implemented yet.");
        a.showAndWait();
    }

    @FXML
    private void onLogout(ActionEvent e) throws IOException {
        SessionContext.clear();
        Parent root = FXMLLoader.load(getClass().getResource("/com/assetsystem/controller/Login.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 700));
        stage.setTitle("PEC - Sign in");
    }
}
