package com.azani.model;

public class PaymentRecord {
    private int institutionId;
    private String paymentType;
    private double amount;
    private String date;

    public PaymentRecord(int institutionId, String paymentType, double amount, String date) {
        this.institutionId = institutionId;
        this.paymentType = paymentType;
        this.amount = amount;
        this.date = date;
    }

    public int getInstitutionId() { return institutionId; }
    public String getPaymentType() { return paymentType; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
}