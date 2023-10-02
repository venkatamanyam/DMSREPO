package com.example.Doc_Prac.repo;

import com.example.Doc_Prac.model.DocMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocMetaRepo extends JpaRepository<DocMeta, Integer> {
    Optional<DocMeta> findById(Integer integer);
    Optional<DocMeta> findByTicketID(String ticketID);
    @Query(value = "select max(docid) from doc_meta", nativeQuery = true)
    public String findLastDocID();
    @Query(value = "select doc_title from doc_meta where userid=?", nativeQuery = true)
    public List<String> findByUserID(String userID);
    Optional<DocMeta> findByDocTitle(String docTitle);
    @Query(value = "select DISTINCT doc_tags from doc_meta where doc_tags like %?%", nativeQuery = true)
    public List<String> findByDocTags(String docTags);
    Optional<List<DocMeta>> findByUserGroup(String userGroup);
    @Query(value = "select * from doc_meta where doc_tags like %:docTags%", nativeQuery = true)
    public List<DocMeta> findAllDocumentsByDocTags(String docTags);
}
