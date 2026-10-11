package com.azani.model;

public class ServicePackage {
    private int subscriptionId;
    private int institutionId;
    private int bandwidthMbps;
    private double monthlyCost;
    private String connectionStatus;

    public ServicePackage(int subscriptionId, int institutionId, int bandwidthMbps, double monthlyCost, String connectionStatus) {
        this.subscriptionId = subscriptionId;
        this.institutionId = institutionId;
        this.bandwidthMbps = bandwidthMbps;
        this.monthlyCost = monthlyCost;
        this.connectionStatus = connectionStatus;
    }

    // Getters
    public int getSubscriptionId() { return subscriptionId; }
    public int getInstitutionId() { return institutionId; }
    public int getBandwidthMbps() { return bandwidthMbps; }
    public double getMonthlyCost() { return monthlyCost; }
    public String getConnectionStatus() { return connectionStatus; }
}