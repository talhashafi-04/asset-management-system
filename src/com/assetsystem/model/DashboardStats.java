package com.assetsystem.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DashboardStats {

    private final int totalAssets;
    private final int totalEmployees;
    private final int activeAllocations;
    private final Map<String, Integer> assetCountByStatus;

    public DashboardStats(int totalAssets, int totalEmployees, int activeAllocations,
                          Map<String, Integer> assetCountByStatus) {
        this.totalAssets = totalAssets;
        this.totalEmployees = totalEmployees;
        this.activeAllocations = activeAllocations;
        this.assetCountByStatus = assetCountByStatus != null
                ? Collections.unmodifiableMap(new LinkedHashMap<>(assetCountByStatus))
                : Collections.emptyMap();
    }

    public int getTotalAssets() {
        return totalAssets;
    }

    public int getTotalEmployees() {
        return totalEmployees;
    }

    public int getActiveAllocations() {
        return activeAllocations;
    }

    public Map<String, Integer> getAssetCountByStatus() {
        return assetCountByStatus;
    }
}
