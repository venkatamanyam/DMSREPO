package com.example.Doc_Prac.service;

import com.example.Doc_Prac.model.Employee;
import com.example.Doc_Prac.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Service
public class EmployeeServiceImpl implements EmployeeService{
    private static final String FIXED_USER_ID="UID-";
    @Autowired
    EmployeeRepo employeeRepo;

    @Autowired
    JavaMailSender mailSender;
    @Override
    public String addEmployee(Employee employee) {
        employee.setUserID(getAutoUserID());
        employee.setPassword("abc");
        employee.setEmail("akshay_kumar@cms.co.in");
        employeeRepo.save(employee);
        return "Employee Added Successfully";
    }

    public String getAutoUserID(){
        String uid = employeeRepo.findMaxUserID();
        int varNum = (uid!=null)?Integer.parseInt(uid.substring(FIXED_USER_ID.length()))+1:1011001;
        return FIXED_USER_ID+varNum;
    }

    public boolean loginCheck(Employee employee){
        Optional<Employee> emp = employeeRepo.findById(employee.getUserID());

        if(emp.isPresent()){
            if(emp.get().getPassword().equals(employee.getPassword())){
                return true;
            }
        }
        return false;
    }

    @Override
    public Employee getEmployeeByID(String userid) {
        return employeeRepo.findById(userid).get();
    }

    @Override
    public Long getOTP(String userid) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        Optional<Employee> employee = employeeRepo.findById(userid);

        if(employee.isPresent()){
            Random random = new Random();
            Long OTP = random.nextLong(999999);

            mailMessage.setFrom("akshaychoudhary424@gmail.com");
            mailMessage.setSubject("OTP for Password Change For DMS");
            mailMessage.setText("OTP for reset DMS Password : "+OTP);
            mailMessage.setTo(employee.get().getEmail());
            mailSender.send(mailMessage);

            return OTP;
        }
        else{
            return null;
        }
    }

    @Override
    public String changePassword(Employee employee) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        Employee emp = employeeRepo.findById(employee.getUserID()).get();

        if(emp.getPassword().equals(employee.getPassword())){
            return "Please try with different password";
        }
        else{
            employee.setUserID(employee.getUserID());
            employee.setUserName(emp.getUserName());
            employee.setPassword(employee.getPassword());
            employee.setAuthority(emp.getAuthority());
            employee.setUserGroup(emp.getUserGroup());
            employee.setUserOrg(emp.getUserOrg());
            employee.setLocation(emp.getLocation());
            employee.setEmail(emp.getEmail());
            employeeRepo.save(employee);

            mailMessage.setFrom("akshaychoudhary424@gmail.com");
            mailMessage.setSubject("Password Changed Successfully");
            mailMessage.setText("Your DMS Password is set Successfully!!!!");
            mailMessage.setTo(emp.getEmail());
            mailSender.send(mailMessage);

            return "Password changed successfully";
        }
    }
}
