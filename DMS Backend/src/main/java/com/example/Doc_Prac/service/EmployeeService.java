package com.example.Doc_Prac.service;

import com.example.Doc_Prac.model.Employee;

public interface EmployeeService {
    public String addEmployee(Employee employee);
    public boolean loginCheck(Employee employee);
    public Employee getEmployeeByID(String userid);
    public Long getOTP(String userid);
    public String changePassword(Employee employee);
}
