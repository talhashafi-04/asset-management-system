package com.assetsystem.model;
import java.sql.Date;

public class AssetDetails {
    private int assetId;
    private Models model;
    private String serialNo;
    private Date purchaseDate;
    private double price;
    private String status;    
    private String remarks;
    /** Set when listing allocated assets: current assignee from open {@code Asset_Allocation}. */
    private final String allocatedEmployeeName;
    private final String allocatedDepartmentName;
    private final Date allocationDate;

    public AssetDetails(int id, String sn, Models m, Date d, double p, String s, String r) {
        this(id, sn, m, d, p, s, r, null, null, null);
    }

    public AssetDetails(int id, String sn, Models m, Date d, double p, String s, String r,
                        String allocatedEmployeeName, String allocatedDepartmentName, Date allocationDate) {
        this.assetId = id;
        this.serialNo = sn;
        this.model = m;
        this.purchaseDate = d;
        this.price = p;
        this.status = s;
        this.remarks = r;
        this.allocatedEmployeeName = allocatedEmployeeName;
        this.allocatedDepartmentName = allocatedDepartmentName;
        this.allocationDate = allocationDate;
    }
    // Getters for all fields...
    public int getAssetId() {
        return assetId;
    }

    public Models getModel() {
        return model;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getRemarks() {
        return remarks;
    }

    /** May be null when not loaded or not allocated. */
    public String getAllocatedEmployeeName() {
        return allocatedEmployeeName;
    }

    /** Department on the allocation row (or blank). */
    public String getAllocatedDepartmentName() {
        return allocatedDepartmentName;
    }

    /** Date of the active open allocation row. */
    public Date getAllocationDate() {
        return allocationDate;
    }

    public boolean hasAllocationDetails() {
        return allocatedEmployeeName != null && !allocatedEmployeeName.trim().isEmpty();
    }
}