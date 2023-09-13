package com.example.Doc_Prac.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Id
    private String userID;
    private String userName;
    private String password;
    private String authority;
    private String userGroup;
    private String userOrg;
    private String location;
    private String email;
}
