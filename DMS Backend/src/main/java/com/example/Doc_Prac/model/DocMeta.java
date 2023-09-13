package com.example.Doc_Prac.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Builder
public class DocMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String docID;
    private String docTitle;
    private String docType;
    private String docPath;
    private boolean compressed;
    private String userID;
    private String userGroup;
    private String userOrg;
    private String docTags;
    private String location;
    private String createdDate;
    private String lastUpdatedDate;
}
