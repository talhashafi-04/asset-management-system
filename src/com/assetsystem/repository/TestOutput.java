package com.assetsystem.repository;

import com.assetsystem.model.AssetDetails;
import java.util.List;

public class TestOutput {
    public static void main(String[] args) {
        AssetRepository repo = new AssetRepository();
        List<AssetDetails> assets = repo.getAllAssets();
        
        System.out.println("Checking Database Records...");
        for (AssetDetails a : assets) {
            System.out.println("ID: " + a.getAssetId());
            System.out.println("Serial: " + a.getSerialNo());
            System.out.println("Manufacturer: " + a.getModel().getManufacturer().getName());
            System.out.println("-----------------------------");
        }
    }
}