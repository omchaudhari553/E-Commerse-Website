package com.springboot.dto;

public class PaymentResponse {
    private String razorpayOrderId;
    private String applicationFee;
    private String secretKey;
    private String paymentId;
    private String notes;

    public PaymentResponse() {
    }

    public PaymentResponse(String razorpayOrderId, String applicationFee, String secretKey, String paymentId, String notes) {
        this.razorpayOrderId = razorpayOrderId;
        this.applicationFee = applicationFee;
        this.secretKey = secretKey;
        this.paymentId = paymentId;
        this.notes = notes;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getApplicationFee() {
        return applicationFee;
    }

    public void setApplicationFee(String applicationFee) {
        this.applicationFee = applicationFee;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
