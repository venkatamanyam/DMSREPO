package com.example.Doc_Prac.repo;

import com.example.Doc_Prac.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepo extends JpaRepository<Employee, String> {
    @Query(value = "select max(userid) from employee", nativeQuery = true)
    public String findMaxUserID();

    @Override
    Optional<Employee> findById(String userid);
}
