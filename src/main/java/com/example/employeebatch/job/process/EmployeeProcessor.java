package com.example.employeebatch.job.process;

import com.example.employeebatch.bo.Employee;
import com.example.employeebatch.utils.enums.DepartmentEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProcessor implements ItemProcessor<Employee, Employee> {

    private static final Logger log = LoggerFactory.getLogger(EmployeeProcessor.class);

    @Override
    public Employee process(Employee employee) throws Exception {
        if (employee == null) {
            return null;
        }

        // Validate name
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            log.warn("Skipping employee with missing name: " + employee);
            return null;
        }

        // Validate salary
        Double salary = employee.getSalary();
        if (salary == null || salary <= 0) {
            log.warn("Skipping employee with invalid salary for: " + employee.getName());
            return null; // Skip invalid records
        }

        // Validate department
        if (employee.getDepartment() == null) {
            log.warn("Skipping employee with unknown department: " + employee.getName());
            return null;
        }

        // Transform: uppercase name
        employee.setName(employee.getName().toUpperCase());

        log.info("Processed employee: " + employee.getName());
        return employee;
    }
}
