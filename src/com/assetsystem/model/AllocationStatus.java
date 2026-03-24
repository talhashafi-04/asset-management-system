package com.assetsystem.model;

public class AllocationStatus {
    private final int statusId;
    private final String name;

    public AllocationStatus(int statusId, String name) {
        this.statusId = statusId;
        this.name = name;
    }

    public int getStatusId() {
        return statusId;
    }

    public String getName() {
        return name;
    }
}
