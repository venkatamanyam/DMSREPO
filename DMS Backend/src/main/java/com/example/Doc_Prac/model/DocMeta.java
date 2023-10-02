package com.example.Doc_Prac.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDate;

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
    private String ticketID;
    private String docTitle;
    private String docType;
    private String docPath;
    private boolean compressed;
    private boolean encryption;
    private String userID;
    private String userGroup;
    private String userOrg;
    private String docTags;
    private String location;
    @CreationTimestamp
    private LocalDate createdDate;
    @UpdateTimestamp
    private LocalDate lastUpdatedDate;
}
