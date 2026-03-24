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
}
