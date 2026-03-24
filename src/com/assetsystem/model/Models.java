package com.assetsystem.model;

public class Models {
    private int id;
    private String name;
    private Manufacturer manufacturer;
    private Categories category;

    public Models(int id, String name, Manufacturer man, Categories cat) {
        this.id = id;
        this.name = name;
        this.manufacturer = man;
        this.category = cat;
    }
    public int getId() { return id; }
    public String getName() { return name; }
    public Manufacturer getManufacturer() { return manufacturer; }
    public Categories getCategory() { return category; }
}