package com.assetsystem.model;

import java.sql.Date;

public class Asset_Allocation {
    private int allocationId;
    private AssetDetails asset;
    private Employees employee;
    private String status;
    private String locationName;   
    private String departmentName; 
    private Date allocateDate;
    private Date returnDate;
    private String notes;

    public Asset_Allocation(int id, AssetDetails asset, Employees emp, String status,
                            String loc, String dept, Date alloc, Date ret, String notes) {
        this.allocationId = id;
        this.asset = asset;
        this.employee = emp;
        this.status = status;
        this.locationName = loc;
        this.departmentName = dept;
        this.allocateDate = alloc;
        this.returnDate = ret;
        this.notes = notes;
    }

    // Getters
    public int getAllocationId() {
        return allocationId;
    }

    public AssetDetails getAsset() {
        return asset;
    }

    public Employees getEmployee() {
        return employee;
    }

    public String getStatus() {
        return status;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Date getAllocateDate() {
        return allocateDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public String getNotes() {
        return notes;
    }
}