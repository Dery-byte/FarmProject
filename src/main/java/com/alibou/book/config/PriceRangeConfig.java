package com.alibou.book.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "price.ranges")
public class PriceRangeConfig {
    private double stepSize = 50.0;
    private double maxBeforePlus = 200.0;

    // Getters and setters
    public double getStepSize() {
        return stepSize;
    }

    public void setStepSize(double stepSize) {
        this.stepSize = stepSize;
    }

    public double getMaxBeforePlus() {
        return maxBeforePlus;
    }

    public void setMaxBeforePlus(double maxBeforePlus) {
        this.maxBeforePlus = maxBeforePlus;
    }
}