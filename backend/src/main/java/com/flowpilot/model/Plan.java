package com.flowpilot.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "price_monthly", nullable = false)
    private BigDecimal priceMonthly;

    @Column(name = "price_yearly", nullable = false)
    private BigDecimal priceYearly;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(name = "max_workflows", nullable = false)
    private Integer maxWorkflows;

    @Column(name = "max_executions_per_month", nullable = false)
    private Integer maxExecutionsPerMonth;

    @Column(name = "features_json", columnDefinition = "TEXT")
    private String featuresJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Plan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getPriceMonthly() { return priceMonthly; }
    public void setPriceMonthly(BigDecimal priceMonthly) { this.priceMonthly = priceMonthly; }

    public BigDecimal getPriceYearly() { return priceYearly; }
    public void setPriceYearly(BigDecimal priceYearly) { this.priceYearly = priceYearly; }

    public Integer getMaxUsers() { return maxUsers; }
    public void setMaxUsers(Integer maxUsers) { this.maxUsers = maxUsers; }

    public Integer getMaxWorkflows() { return maxWorkflows; }
    public void setMaxWorkflows(Integer maxWorkflows) { this.maxWorkflows = maxWorkflows; }

    public Integer getMaxExecutionsPerMonth() { return maxExecutionsPerMonth; }
    public void setMaxExecutionsPerMonth(Integer maxExecutionsPerMonth) { this.maxExecutionsPerMonth = maxExecutionsPerMonth; }

    public String getFeaturesJson() { return featuresJson; }
    public void setFeaturesJson(String featuresJson) { this.featuresJson = featuresJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
