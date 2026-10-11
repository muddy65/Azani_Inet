package com.azani.model;

public class Institution {
    private int id;
    private String name;
    private String category;
    private String contactName;
    private String contactPhone;

    public Institution(int id, String name, String category, String contactName, String contactPhone) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getContactName() { return contactName; }
    public String getContactPhone() { return contactPhone; }
}