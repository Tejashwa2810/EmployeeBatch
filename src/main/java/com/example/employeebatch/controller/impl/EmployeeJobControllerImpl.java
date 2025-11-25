package com.example.employeebatch.controller.impl;

import com.example.employeebatch.controller.EmployeeJobController;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/job")
public class EmployeeJobControllerImpl implements EmployeeJobController {

    private final JobLauncher jobLauncher;
    private final Job employeeJob;

    public EmployeeJobControllerImpl(JobLauncher jobLauncher,
                                     @Qualifier("employeeJob") Job employeeJob) {
        this.jobLauncher = jobLauncher;
        this.employeeJob = employeeJob;
    }

    @Override
    @PostMapping("/employee-import")
    public ResponseEntity<String> triggerEmployeeJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(employeeJob, jobParameters);

            return ResponseEntity.ok("Job triggered successfully!");
        } catch (JobExecutionAlreadyRunningException |
                 JobRestartException |
                 JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {

            return ResponseEntity.badRequest()
                    .body("Error triggering job: " + e.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500)
                    .body("Unexpected error: " + ex.getMessage());
        }
    }
}