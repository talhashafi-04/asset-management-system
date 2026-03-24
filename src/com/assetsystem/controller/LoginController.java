package com.assetsystem.controller;

import com.assetsystem.model.Users;
import com.assetsystem.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField fieldUsername;
    @FXML
    private PasswordField fieldPassword;
    @FXML
    private Button btnLogin;

    private final UserRepository userRepo = new UserRepository();

    @FXML
    private void initialize() {
        SessionContext.clear();
    }

    /** Temporary dev helper: admin / admin123 */
    @FXML
    private void onFillAdmin() {
        fieldUsername.setText("admin");
        fieldPassword.setText("admin123");
    }

    /** Temporary dev helper: staff user alice / pass123 */
    @FXML
    private void onFillStaff() {
        fieldUsername.setText("alice");
        fieldPassword.setText("pass123");
    }

    @FXML
    private void onLogin(ActionEvent e) throws IOException {
        String username = fieldUsername.getText();
        String password = fieldPassword.getText();
        if (username == null || username.trim().isEmpty()) {
            showError("Enter your username.");
            return;
        }
        if (password == null || password.isEmpty()) {
            showError("Enter your password.");
            return;
        }

        Users user = userRepo.authenticate(username.trim(), password);
        if (user == null) {
            showError("Invalid username or password.");
            fieldPassword.clear();
            return;
        }

        SessionContext.setCurrentUser(user);
        Parent root = FXMLLoader.load(getClass().getResource("/com/assetsystem/controller/Dashboard.fxml"));
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root, 1200, 700));
        stage.setTitle("PEC - Asset Management System");
    }

    private static void showError(String message) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Login");
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
