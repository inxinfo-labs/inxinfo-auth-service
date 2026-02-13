package com.satishlabs.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Positive;

@ConfigurationProperties(prefix = "auth.rate-limit")
@Validated
public class RateLimitProperties {

    private boolean enabled = true;

    @Positive
    private int limitForPeriod = 50;

    private String limitRefreshPeriod = "1s";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getLimitForPeriod() { return limitForPeriod; }
    public void setLimitForPeriod(int limitForPeriod) { this.limitForPeriod = limitForPeriod; }
    public String getLimitRefreshPeriod() { return limitRefreshPeriod; }
    public void setLimitRefreshPeriod(String limitRefreshPeriod) { this.limitRefreshPeriod = limitRefreshPeriod; }
}
