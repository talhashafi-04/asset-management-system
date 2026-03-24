package com.assetsystem.model;

public class Employees {
    private int id;
    private String fullName;
    private String department; // From SQL JOIN

    public Employees(int id, String name, String dept) {
        this.id = id;
        this.fullName = name;
        this.department = dept;
    }
    public String getFullName() { return fullName; }
    public String getDepartment() {return department;}
}