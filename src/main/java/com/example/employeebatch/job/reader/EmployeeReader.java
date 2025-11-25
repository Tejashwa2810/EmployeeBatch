package com.example.employeebatch.job.reader;

import com.example.employeebatch.bo.Employee;
import com.example.employeebatch.utils.enums.DepartmentEnum;
import com.example.employeebatch.config.PropertiesConfig;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;

@Component
public class EmployeeReader {

    private final ResourceLoader resourceLoader;
    private final PropertiesConfig propertiesConfig;

    @Autowired
    public EmployeeReader(ResourceLoader resourceLoader, PropertiesConfig propertiesConfig) {
        this.resourceLoader = resourceLoader;
        this.propertiesConfig = propertiesConfig;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Employee> reader() {
        FlatFileItemReader<Employee> reader = new FlatFileItemReader<>();
        reader.setName("employeeItemReader");
        reader.setResource(resourceLoader.getResource(propertiesConfig.getInputFilePath()));

        // tokenizer
        LineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA) {{
            setNames("id", "name", "department", "salary");
            setStrict(false);
        }};

        // mapper
        FieldSetMapper<Employee> fieldSetMapper = new FieldSetMapper<Employee>() {
            @Override
            public Employee mapFieldSet(FieldSet fieldSet) throws BindException {
                if (fieldSet == null) {
                    return null;
                }
                Employee e = new Employee();

                // id may be empty in CSV for new entities
                try {
                    String idStr = fieldSet.readString("id");
                    if (idStr != null && !idStr.trim().isEmpty()) {
                        e.setId(fieldSet.readLong("id"));
                    }
                } catch (Exception ex) {
                    // ignore - id optional for import
                }

                e.setName(fieldSet.readString("name"));

                String dept = fieldSet.readString("department");
                if (dept != null && !dept.trim().isEmpty()) {
                    try {
                        e.setDepartment(DepartmentEnum.valueOf(dept.trim().toUpperCase()));
                    } catch (IllegalArgumentException iae) {
                        // Unknown enum value -> leave null (processor can decide)
                        e.setDepartment(null);
                    }
                }

                try {
                    String salaryStr = fieldSet.readString("salary");
                    if (salaryStr != null && !salaryStr.trim().isEmpty()) {
                        e.setSalary(Double.valueOf(salaryStr.trim()));
                    }
                } catch (Exception ex) {
                    e.setSalary(null);
                }

                return e;
            }
        };

        DefaultLineMapper<Employee> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        reader.setLineMapper(lineMapper);

        reader.setLinesToSkip(1); // skip header
        return reader;
    }
}
