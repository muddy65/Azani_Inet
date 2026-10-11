package com.azani.service;

import com.azani.model.Infrastructure;

public class BillingManager {
    
    // Constants for pricing based on your project requirements
    private static final double PC_COST = 40000.00;
    private static final double LAN_NODE_COST = 2500.00;

    /**
     * Calculates the total setup cost for an institution if their infrastructure is not ready.
     */
    public static double calculateInstallationCost(Infrastructure infra) {
        if (infra.isReady()) {
            return 0.00; // No setup cost if they already have the network
        }
        
        double totalPcCost = infra.getPcsRequired() * PC_COST;
        double totalLanCost = infra.getLanNodesRequired() * LAN_NODE_COST;
        
        return totalPcCost + totalLanCost;
    }
    
    /**
     * Determines the monthly cost based on the selected bandwidth tier.
     */
    public static double getMonthlyBandwidthCost(int bandwidthMbps) {
        switch (bandwidthMbps) {
            case 4: return 5000.00;
            case 10: return 10000.00;
            case 20: return 18000.00;
            case 50: return 40000.00;
            default: return 0.00;
        }
    }
}