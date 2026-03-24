package com.assetsystem.controller;

import com.assetsystem.model.Users;
import com.assetsystem.repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public final class ChangePasswordDialog {

    private ChangePasswordDialog() {
    }

    /**
     * Shows modal dialog; updates DB and session on success.
     *
     * @return {@code true} if password was changed
     */
    public static boolean show(Window owner) {
        Users sessionUser = SessionContext.getCurrentUser();
        if (sessionUser == null) {
            err("You are not signed in.");
            return false;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Change password");
        dialog.setHeaderText("User: " + sessionUser.getUsername());
        dialog.initOwner(owner);

        PasswordField fieldCurrent = new PasswordField();
        fieldCurrent.setPromptText("Current password");
        PasswordField fieldNew = new PasswordField();
        fieldNew.setPromptText("New password");
        PasswordField fieldConfirm = new PasswordField();
        fieldConfirm.setPromptText("Confirm new password");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.add(new Label("Current password"), 0, 0);
        grid.add(fieldCurrent, 1, 0);
        grid.add(new Label("New password"), 0, 1);
        grid.add(fieldNew, 1, 1);
        grid.add(new Label("Confirm"), 0, 2);
        grid.add(fieldConfirm, 1, 2);

        DialogPane pane = dialog.getDialogPane();
        pane.setContent(grid);
        ButtonType changeType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(changeType, ButtonType.CANCEL);

        UserRepository repo = new UserRepository();
        final boolean[] success = {false};

        Button changeBtn = (Button) pane.lookupButton(changeType);
        changeBtn.addEventFilter(ActionEvent.ACTION, event -> {
            String current = fieldCurrent.getText();
            String newPwd = fieldNew.getText();
            String confirm = fieldConfirm.getText();

            if (current == null || current.isEmpty()) {
                event.consume();
                warn("Enter your current password.");
                return;
            }
            if (!current.equals(sessionUser.getPassword())) {
                event.consume();
                err("Current password is incorrect.");
                return;
            }
            if (newPwd == null || newPwd.isEmpty()) {
                event.consume();
                warn("Enter a new password.");
                return;
            }
            if (newPwd.length() > 255) {
                event.consume();
                warn("New password is too long (max 255 characters).");
                return;
            }
            if (!newPwd.equals(confirm)) {
                event.consume();
                warn("New password and confirmation do not match.");
                return;
            }
            if (newPwd.equals(current)) {
                event.consume();
                warn("New password must be different from the current password.");
                return;
            }

            if (!repo.updatePassword(sessionUser.getUserId(), newPwd)) {
                event.consume();
                err("Could not update password. Check the database connection.");
                return;
            }
            SessionContext.replaceCurrentUserPassword(newPwd);
            success[0] = true;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        return success[0] && result.isPresent() && result.get() == changeType;
    }

    private static void warn(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Change password");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private static void err(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Change password");
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
