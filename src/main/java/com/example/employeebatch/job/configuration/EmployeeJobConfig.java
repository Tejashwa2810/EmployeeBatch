package com.example.employeebatch.job.configuration;

import com.example.employeebatch.bo.Employee;
import com.example.employeebatch.job.listener.JobCompletionNotificationListener;
import com.example.employeebatch.job.process.EmployeeProcessor;
import com.example.employeebatch.job.reader.EmployeeReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class EmployeeJobConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EmployeeReader employeeReader;
    private final EmployeeProcessor employeeProcessor;
    private final JobCompletionNotificationListener listener;
    private final DataSource dataSource;

    @Autowired
    public EmployeeJobConfig(JobBuilderFactory jobBuilderFactory,
                             StepBuilderFactory stepBuilderFactory,
                             EmployeeReader employeeReader,
                             EmployeeProcessor employeeProcessor,
                             JobCompletionNotificationListener listener,
                             DataSource dataSource) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.employeeReader = employeeReader;
        this.employeeProcessor = employeeProcessor;
        this.listener = listener;
        this.dataSource = dataSource;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Employee> employeeWriter() {
        return new JdbcBatchItemWriterBuilder<Employee>()
                .dataSource(dataSource)
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO employee (name, department, salary) VALUES (:name, :department, :salary)")
                .build();
    }

    @Bean
    public Step employeeImportStep() {
        return stepBuilderFactory.get("employeeImportStep")
                .<Employee, Employee>chunk(10)
                .reader(employeeReader.reader())
                .processor(employeeProcessor)
                .writer(employeeWriter())
                .build();
    }

    @Bean(name = "employeeJob")
    public Job employeeJob() {
        return jobBuilderFactory.get("employeeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(employeeImportStep())
                .build();
    }
}