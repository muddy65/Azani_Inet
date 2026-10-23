package com.azani.service;

public class BillingManager{
    // ----FIXED PROJECT CONSTANTS----
    public static final double REGISTRATION_FEE = 8500.00;
    public static final double INSTALLATION_FEE_READY = 10000.00;
    public static final double PC_COST = 40000.00;
    public static final double RECONNECTION_FEE = 1000.00;
    public static final double OVERDUE_FINE_RATE = 0.15;
    public static final double UPGRADE_DISCOUNT_RATE = 0.10;

    /**
     * Determines the  corredt monthly cost 
     */
    public static double getMonthlyBandwidthCost(int bandwidthMbps){
        switch(bandwidthMbps){
            case 4: return 1200.00;
            case 10: return 2000.00;
            case 20: return 3500.00;
            case 25: return 4000.00;
            case 50: return 7000.00;
            default: return 0.00;
        }
    }

    /**
     * Calculates hardware costs if an institution is NOT READY
     * 
     */
    public static double calculateHardwareCost(int pcCount, int lanNodes){
        double totalPcCost = pcCount * PC_COST;
        double totalLanCost = 0.00;

        //Tiered LAN pricing 
        if(lanNodes >= 2 && lanNodes <= 10){
            totalLanCost = 10000.00;
        }else if(lanNodes >= 11 && lanNodes <= 20){
            totalLanCost = 20000.00;
        }else if(lanNodes >= 21 && lanNodes <= 40){
            totalLanCost = 30000.00;
        }else if(lanNodes >= 41 && lanNodes <=100){
            totalLanCost = 40000.00;
        }
        return totalPcCost + totalLanCost;
    }

    /**
     * Applies the 10% discount to the new bandwidth cost for upgrading institutions
     */
    public static double calculateUpgradeCost(int newBandwidthMbps){
        double standardCost = getMonthlyBandwidthCost(newBandwidthMbps);
        double discount = standardCost * UPGRADE_DISCOUNT_RATE;
        return standardCost- discount;
    }
    /**
     * Calculates the 15% fine on ovverdue monthly bill
     */
    public static double calculateOverdueFine(double amountDue){
        return amountDue * OVERDUE_FINE_RATE;
    }

    /**
     * Calculates the total amount a disconnected client must pay to get back online
     * 
     */
    public static double calculateReconnectionTotal(double originalMonthlyCost){
        double fine = calculateOverdueFine(originalMonthlyCost);
        return originalMonthlyCost + fine + RECONNECTION_FEE;
    }
}