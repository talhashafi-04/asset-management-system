package com.assetsystem.model;

public class Manufacturer {
	private int id;
	private String name;
	
	public Manufacturer(int id, String name) { 
		this.id = id; this.name = name; 
		}
    public int getId() { 
    	return id; 
    	}
    public String getName() {
    	return name; 
    	}
}
