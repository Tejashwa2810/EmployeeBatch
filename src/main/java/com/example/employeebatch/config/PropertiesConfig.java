package com.example.employeebatch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "batch")
public class PropertiesConfig {

    /**
     * Path for input CSV. Example: classpath:employees.csv or file:/data/employees.csv
     */
    private String inputFilePath = "classpath:employees.csv";

    public String getInputFilePath() {
        return inputFilePath;
    }

    public void setInputFilePath(String inputFilePath) {
        this.inputFilePath = inputFilePath;
    }
}
