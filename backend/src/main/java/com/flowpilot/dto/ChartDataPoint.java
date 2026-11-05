package com.flowpilot.dto;

public class ChartDataPoint {

    private String label;
    private double value;
    private double secondaryValue;

    public ChartDataPoint() {}

    public ChartDataPoint(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public ChartDataPoint(String label, double value, double secondaryValue) {
        this.label = label;
        this.value = value;
        this.secondaryValue = secondaryValue;
    }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public double getSecondaryValue() { return secondaryValue; }
    public void setSecondaryValue(double secondaryValue) { this.secondaryValue = secondaryValue; }
}
