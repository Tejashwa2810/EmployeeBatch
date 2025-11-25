package com.example.employeebatch.controller;

import org.springframework.http.ResponseEntity;

public interface EmployeeJobController {

    ResponseEntity<String> triggerEmployeeJob();
}
