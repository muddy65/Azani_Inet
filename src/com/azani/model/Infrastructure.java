package com.azani.model;

public class Infrastructure {
    private int institutionId;
    private boolean isReady;
    private int pcsRequired;
    private int lanNodesRequired;

    public Infrastructure(int institutionId, boolean isReady, int pcsRequired, int lanNodesRequired) {
        this.institutionId = institutionId;
        this.isReady = isReady;
        this.pcsRequired = pcsRequired;
        this.lanNodesRequired = lanNodesRequired;
    }

    public int getInstitutionId() { return institutionId; }
    public boolean isReady() { return isReady; }
    public int getPcsRequired() { return pcsRequired; }
    public int getLanNodesRequired() { return lanNodesRequired; }
}