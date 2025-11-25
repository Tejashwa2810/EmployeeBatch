package com.example.employeebatch.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig extends DefaultBatchConfigurer {

    private final DataSource dataSource;

    public BatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        // Override to use default in-memory JobRepository if needed
        // Leave empty for default behavior or call super.setDataSource(dataSource)
        super.setDataSource(dataSource);
    }
}