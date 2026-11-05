package com.flowpilot.dto;

import jakarta.validation.constraints.NotBlank;

public class UpgradeSubscriptionRequest {

    @NotBlank(message = "Plan code is required")
    private String planCode; // STARTER, PROFESSIONAL, ENTERPRISE

    private String billingCycle = "MONTHLY";
    private String paymentMethodId = "pm_card_visa";

    public UpgradeSubscriptionRequest() {}

    public String getPlanCode() { return planCode; }
    public void setPlanCode(String planCode) { this.planCode = planCode; }

    public String getBillingCycle() { return billingCycle; }
    public void setBillingCycle(String billingCycle) { this.billingCycle = billingCycle; }

    public String getPaymentMethodId() { return paymentMethodId; }
    public void setPaymentMethodId(String paymentMethodId) { this.paymentMethodId = paymentMethodId; }
}
