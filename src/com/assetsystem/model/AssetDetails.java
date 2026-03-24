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

    public AssetDetails(int id, String sn, Models m, Date d, double p, String s,String r) {
        this.assetId = id;
        this.serialNo = sn;
        this.model = m;
        this.purchaseDate = d;
        this.price = p;
        this.status = s;
        this.remarks = r;
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
    
}