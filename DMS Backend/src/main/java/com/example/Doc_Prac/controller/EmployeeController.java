package com.example.Doc_Prac.controller;

import com.example.Doc_Prac.model.Employee;
import com.example.Doc_Prac.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emp/data")
@CrossOrigin("http://localhost:3000")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    // http://localhost:8082/emp/data/add
    @PostMapping("/add")
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee){
        return new ResponseEntity<>(employeeService.addEmployee(employee), HttpStatus.OK);
    }

    // http://localhost:8082/emp/data/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Employee employee){
        return new ResponseEntity<>(employeeService.loginCheck(employee),HttpStatus.OK);
    }

    // http://localhost:8082/emp/data/get/emp/
    @GetMapping("/get/emp/{userID}")
    public ResponseEntity<?> getEmployeeDetails(@PathVariable String userID){
        return new ResponseEntity<>(employeeService.getEmployeeByID(userID),HttpStatus.OK);
    }

    // http://localhost:8082/emp/data/change/pwd/otp/
    @PostMapping("/change/pwd/otp/{userID}")
    public ResponseEntity<?> changePasswordOTP(@PathVariable String userID){
        return new ResponseEntity<>(employeeService.getOTP(userID), HttpStatus.OK);
    }

    // http://localhost:8082/emp/data/pwd/req/chng
    @PutMapping("/pwd/req/chng")
    public ResponseEntity<?> changePassword(@RequestBody Employee employee){
        return new ResponseEntity<>(employeeService.changePassword(employee),HttpStatus.OK);
    }

}
