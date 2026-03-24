package com.assetsystem.controller;

final class NavigationContext {
    private static SidebarSection pendingAssetDisplaySection;

    private NavigationContext() {
    }

    static void setPendingAssetDisplaySection(SidebarSection section) {
        pendingAssetDisplaySection = section;
    }

    static SidebarSection takePendingAssetDisplaySection() {
        SidebarSection s = pendingAssetDisplaySection;
        pendingAssetDisplaySection = null;
        return s != null ? s : SidebarSection.ALLOCATED_ASSETS;
    }
}
