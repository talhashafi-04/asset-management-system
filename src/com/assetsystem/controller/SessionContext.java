package com.assetsystem.controller;

import com.assetsystem.model.Users;

/**
 * Holds the signed-in user for the current JVM session (after successful {@link com.assetsystem.repository.UserRepository#authenticate}).
 */
public final class SessionContext {

    private static Users currentUser;

    private SessionContext() {
    }

    public static void setCurrentUser(Users user) {
        currentUser = user;
    }

    public static Users getCurrentUser() {
        return currentUser;
    }

    public static void clear() {
        currentUser = null;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /** Keeps session in sync after a successful password change in the database. */
    public static void replaceCurrentUserPassword(String newPassword) {
        if (currentUser == null || newPassword == null) {
            return;
        }
        Users u = currentUser;
        currentUser = new Users(u.getUserId(), u.getUsername(), u.getRole(), newPassword);
    }
}
